package handler;

import api.StravaApi;
import spark.Request;
import spark.Response;

public class StravaHandler {
    private final StravaApi api;

    public StravaHandler(StravaApi api) {
        this.api = api;
    }

    public Object callback(Request request, Response response) {
        return null;
    }

    public Object openLogin(Request request, Response response) {
        String callbackUrl = request.scheme() + "://" + request.host() + "/callback";
        response.redirect(api.loginUrl(callbackUrl));
        return response;
    }
}
