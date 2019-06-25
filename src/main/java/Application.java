import api.StravaApi;
import handler.StravaHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.get;
import static spark.Spark.path;

public class Application {
    private final static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        var stravaHandler = new StravaHandler(new StravaApi(System.getenv("STRAVA_CLIENT_ID"), "STRAVA_CLIENT_SECRET"));

        path("/strava", () -> {
            get("/callback", (i, o) -> stravaHandler.callback(i, o));
            get("/login", (i, o) -> stravaHandler.openLogin(i, o));
        });

    }
}
