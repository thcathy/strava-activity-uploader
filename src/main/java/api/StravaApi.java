package api;

import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StravaApi {
    private final static Logger log = LoggerFactory.getLogger(StravaApi.class);

    static private final String LOGIN_URL_TEMPLATE =
            "https://www.strava.com/oauth/authorize?" +
            "client_id=%s&redirect_uri=%s&response_type=code&scope=activity:write";

    static private final String STRAVA_HOST = "https://www.strava.com";
    static private final String API_VERSION = "/api/v3";
    static private final String TOKEN_URL = STRAVA_HOST + "/oauth/token";
    static private final String GET_ATHLETE_URL = STRAVA_HOST + API_VERSION + "/athlete";

    static private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String clientId;
    private final String clientSecret;
    private String code;
    private Token token;

    private OkHttpClient httpClient;
    private Gson gson;

    public StravaApi(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public StravaApi httpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public StravaApi gson(Gson gson) {
        this.gson = gson;
        return this;
    }

    public StravaApi code(String code) {
        this.code = code;
        return this;
    }

    public String loginUrl(String redirectUri) {
        return String.format(LOGIN_URL_TEMPLATE, clientId, redirectUri);
    }

    public String getAthlete() throws Exception {
        Request request = new Request.Builder()
                .url(GET_ATHLETE_URL)
                .header("Authorization", "Bearer " + token.access_token)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public Token exchangeToken(boolean init) throws Exception {
        RequestBody body = RequestBody.create(JSON, createTokenRequestBody(init));
        var request = new Request.Builder().url(TOKEN_URL)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                var responseBody = response.body().string();
                log.info("Get new token: {}", body);
                token = gson.fromJson(responseBody, Token.class);
                return token;
            } else {
                throw new RuntimeException(response.code() + ": " + response.body().string());
            }
        }
    }

    private String createTokenRequestBody(boolean init) {
        var body = new ExchangeTokenRequest(this);
        body.grantType(init ? "authorization_code" : "refresh_token");
        return gson.toJson(body);
    }

    class Token {
        private String token_type;
        private String access_token;
        private String refresh_token;
        private String expires_at;
        private String state;
    }

    class ExchangeTokenRequest {
        private String client_id;
        private String client_secret;
        private String code;
        private String grant_type;

        ExchangeTokenRequest(StravaApi api) {
            this.client_id = clientId;
            this.client_secret = clientSecret;
            this.code = api.code;
        }

        ExchangeTokenRequest grantType(String grantType) {
            this.grant_type = grantType;
            return this;
        }
    }

}
