package handler;

import api.StravaApi;
import spark.Request;
import spark.Response;

public class StravaHandler {
    private final StravaApi api;

    public StravaHandler(StravaApi api) {
        this.api = api;
    }

    public String callback(Request request, Response response) throws Exception {
        String code = request.queryParams("code");
        api.code(code).exchangeToken(true);
        response.status(200);
        response.type("application/json");
        return "success";

    }

    public Response openLogin(Request request, Response response) {
        String callbackUrl = request.scheme() + "://" + request.host() + "/strava/callback";
        response.redirect(api.loginUrl(callbackUrl));
        return response;
    }
}
