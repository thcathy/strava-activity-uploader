package api;

import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class StravaApi {
    private final static Logger log = LoggerFactory.getLogger(StravaApi.class);

    static private final String LOGIN_URL_TEMPLATE =
            "https://www.strava.com/oauth/authorize?" +
            "client_id=%s&redirect_uri=%s&response_type=code&scope=activity:write";

    static private final String TOKEN_URL = "https://www.strava.com/oauth/token";

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

    public Token exchangeToken(boolean init) throws Exception {
        RequestBody body = RequestBody.create(JSON, createTokenRequestBody(init));
        var request = new Request.Builder().url(TOKEN_URL)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                token = gson.fromJson(response.body().string(), Token.class);
                return token;
            } else {
                throw new RuntimeException("Cannot exchange token from strava");
            }
        }
    }

    private String createTokenRequestBody(boolean init) {
        var body = new ExchangeTokenRequest();
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

        ExchangeTokenRequest() {
            this.client_id = clientId;
            this.client_secret = clientSecret;
            this.code = code;
        }

        ExchangeTokenRequest grantType(String grantType) {
            this.grant_type = grantType;
            return this;
        }
    }

}
