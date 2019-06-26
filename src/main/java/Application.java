import api.StravaApi;
import com.google.gson.Gson;
import handler.ExceptionHandler;
import handler.StravaHandler;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Application {
    private final static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        var httpClient = new OkHttpClient();
        var gson = new Gson();
        var stravaApi = new StravaApi(System.getenv("STRAVA_CLIENT_ID"), System.getenv("STRAVA_CLIENT_SECRET"))
                .httpClient(httpClient)
                .gson(gson);

        var stravaHandler = new StravaHandler(stravaApi);

        path("/strava", () -> {
            get("/callback", (i, o) -> stravaHandler.callback(i, o), gson::toJson);
            get("/login", (i, o) -> stravaHandler.openLogin(i, o));
        });

        exception(Exception.class, (exception, request, response) -> new ExceptionHandler(gson));
    }
}
