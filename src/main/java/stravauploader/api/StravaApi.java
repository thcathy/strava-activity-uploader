package stravauploader.api;

import com.google.gson.Gson;
import stravauploader.io.TokenStore;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StravaApi {
    final static Logger log = LoggerFactory.getLogger(StravaApi.class);

    static private final String LOGIN_URL_TEMPLATE =
            "https://www.strava.com/oauth/authorize?" +
            "client_id=%s&redirect_uri=%s&response_type=code&scope=activity:write";

    static String host = "https://www.strava.com";
    static final String API_VERSION = "/stravauploader/api/v3";
    static final String TOKEN_URL = "/oauth/token";
    static final String GET_ATHLETE_URL = API_VERSION + "/athlete";

    static private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    final String clientId;
    final String clientSecret;
    String code;
    Token token;
    TokenStore tokenStore;

    OkHttpClient httpClient;
    Gson gson;

    public StravaApi(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String loginUrl(String redirectUri) {
        return String.format(LOGIN_URL_TEMPLATE, clientId, redirectUri);
    }

    public String getAthlete() throws Exception {
        Request request = new Request.Builder()
                .url(host + GET_ATHLETE_URL)
                .header("Authorization", "Bearer " + token.access_token)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public Token exchangeToken(boolean init) throws Exception {
        RequestBody body = RequestBody.create(JSON, createTokenRequestBody(init));
        var request = new Request.Builder().url(host + TOKEN_URL)
                .post(body)
                .build();
        log.info("Get new token: request: {}", body.toString());
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                var responseBody = response.body().string();
                log.info("Get new token: response: {}", responseBody);
                setToken(responseBody);
                return token;
            } else {
                throw new RuntimeException(response.code() + ": " + response.body().string());
            }
        }
    }

    private void setToken(String responseBody) {
        token = gson.fromJson(responseBody, Token.class);
        try {
            if (tokenStore != null) tokenStore.save(responseBody);
        } catch (Exception e) {
            log.warn("cannot save token", e);
        }
    }

    private String createTokenRequestBody(boolean init) {
        var body = new ExchangeTokenRequest(this);
        body.grantType(init ? "authorization_code" : "refresh_token");
        return gson.toJson(body);
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

    public StravaApi tokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
        return this;
    }

    public void loadToken() {
        if (tokenStore == null) return;

        try {
            var json = tokenStore.load();
            token = gson.fromJson(json, Token.class);
        } catch (Exception e) {
            log.warn("cannot load token", e);
        }
    }

    static class Token {
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
