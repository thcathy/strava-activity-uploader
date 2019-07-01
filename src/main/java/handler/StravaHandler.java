package handler;

import api.StravaApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import static handler.HandlerUtils.processException;

public class StravaHandler {
    private final static Logger log = LoggerFactory.getLogger(StravaHandler.class);

    private final StravaApi api;

    public StravaHandler(StravaApi api) {
        this.api = api;
    }

    public String callback(Request request, Response response) {
        try {
            String code = request.queryParams("code");
            api.code(code).exchangeToken(true);
            response.status(200);
            response.type("application/json");
            return "success";
        } catch (Exception e) {
            return processException(response, e);
        }
    }

    public String getAthlete(Request request, Response response) {
        try {
            return api.getAthlete();
        } catch (Exception e) {
            return processException(response, e);
        }
    }

    public Response openLogin(Request request, Response response) {
        String callbackUrl = request.scheme() + "://" + request.host() + "/strava/callback";
        response.redirect(api.loginUrl(callbackUrl));
        return response;
    }
}
