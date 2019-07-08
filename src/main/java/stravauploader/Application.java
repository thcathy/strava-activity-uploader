package stravauploader;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stravauploader.api.StravaApi;
import stravauploader.handler.StravaHandler;
import stravauploader.io.TokenStore;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;
import static spark.Spark.path;

public class Application {
    private final static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        var httpClient = new OkHttpClient();
        var gson = new Gson();
        var straveTokenStore = new TokenStore("strava-token");
        var stravaApi = new StravaApi(System.getenv("STRAVA_CLIENT_ID"), System.getenv("STRAVA_CLIENT_SECRET"))
                .httpClient(httpClient)
                .gson(gson)
                .tokenStore(straveTokenStore);
        var stravaHandler = new StravaHandler(stravaApi);

        var mailClient = new MailClient()
                                .setHost(System.getenv("MAIL_HOST"))
                                .setUsername(System.getenv("MAIL_USERNAME"))
                                .setPassword(System.getenv("MAIL_PASSWORD"));

        var stravaUploader = new StravaUploader();
        stravaUploader.stravaApi = stravaApi;
        stravaUploader.mailClient = mailClient;

        // default to 5 minutes
        var jobPeriod = Optional.ofNullable(System.getenv("JOB_PERIOD_IN_SECOND")).orElse("300");

        path("/strava", () -> {
            get("/callback", (i, o) -> stravaHandler.callback(i, o));
            get("/login", (i, o) -> stravaHandler.openLogin(i, o));
            get("/athlete", (i, o) -> stravaHandler.getAthlete(i, o));
            get("/check-mail-and-upload", (i, o) -> stravaUploader.checkEmailAndUploadActivity());
        });

        // init application
        stravaApi.loadToken();
        try {
            mailClient.connect();
        } catch (Exception e) {
            log.error("Cannot connect to email server", e);
            System.exit(-1);
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> stravaUploader.checkEmailAndUploadActivity(), 10, Long.valueOf(jobPeriod), TimeUnit.SECONDS);
    }
}