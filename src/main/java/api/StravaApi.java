package api;

public class StravaApi {
    static private final String LOGIN_URL_TEMPLATE =
            "https://www.strava.com/oauth/authorize?" +
            "client_id=%s&redirect_uri=%s&response_type=code&scope=activity:write";

    private final String clientId;
    private final String clientSecret;

    public StravaApi(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String loginUrl(String redirectUri) {
        return String.format(LOGIN_URL_TEMPLATE, clientId, redirectUri);
    }
}
