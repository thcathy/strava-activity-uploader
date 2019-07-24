package stravauploader;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stravauploader.api.StravaApi;
import stravauploader.handler.StravaHandler;
import stravauploader.io.TokenStore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;
import static spark.Spark.path;

public class Application {
    private final static Logger log = LoggerFactory.getLogger(Application.class);

    int jobInitialDelaySecond = 10;
    ApplicationConfig config;
    OkHttpClient httpClient;
    Gson gson;
    TokenStore straveTokenStore;
    StravaApi stravaApi;
    StravaHandler stravaHandler;
    MailClient mailClient;
    StravaUploader stravaUploader;

    public static void main(String[] args) {
        Application app = new Application();
        app.createInstances();
        app.init();
        app.startUploadSchedule();
    }

    protected void createInstances() {
        config = new ApplicationConfig();
        httpClient = new OkHttpClient();
        gson = new Gson();
        straveTokenStore = new TokenStore("strava-token");
        stravaApi = new StravaApi(config.getStravaClientId(), config.getStravaClientSecret())
                .httpClient(httpClient)
                .gson(gson)
                .tokenStore(straveTokenStore);
        stravaHandler = new StravaHandler(config, stravaApi);

        mailClient = new MailClient()
                .setHost(config.getMailHost())
                .setUsername(config.getMailUsername())
                .setPassword(config.getMailPassword());

        stravaUploader = new StravaUploader().stravaApi(stravaApi).mailClient(mailClient);
    }

    public void init() {
        setupHttpRequestHandler();
        stravaApi.loadToken();
        try {
            mailClient.connect();
        } catch (Exception e) {
            log.error("Cannot connect to email server", e);
            System.exit(-1);
        }
    }

    private void setupHttpRequestHandler() {
        path("/strava", () -> {
            get("/callback", (i, o) -> stravaHandler.callback(i, o));
            get("/login", (i, o) -> stravaHandler.openLogin(i, o));
            get("/athlete", (i, o) -> stravaHandler.getAthlete(i, o));
            get("/athlete", (i, o) -> stravaHandler.getAthlete(i, o));
            get("/check-mail-and-upload", (i, o) -> stravaUploader.checkEmailAndUploadActivity());
        });
    }

    public void startUploadSchedule() {
        var jobPeriod = config.getJobPeriod();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> stravaUploader.checkEmailAndUploadActivity(), jobInitialDelaySecond, Long.valueOf(jobPeriod), TimeUnit.SECONDS);
    }
}