package stravauploader;

import stravauploader.api.StravaApi;
import com.google.gson.Gson;
import stravauploader.handler.StravaHandler;
import stravauploader.io.TokenStore;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        var mailService = new MailClient()
                                .setHost(System.getenv("MAIL_HOST"))
                                .setUsername(System.getenv("MAIL_USERNAME"))
                                .setPassword(System.getenv("MAIL_PASSWORD"));

        path("/strava", () -> {
            get("/callback", (i, o) -> stravaHandler.callback(i, o));
            get("/login", (i, o) -> stravaHandler.openLogin(i, o));
            get("/athlete", (i, o) -> stravaHandler.getAthlete(i, o));
        });

        stravaApi.loadToken();

        try {
            mailService.connect();
            mailService.readEmails();
        } catch (Exception e) {
            log.error("Error when reading emails", e);
        }

    }
}
