package stravauploader.api;

import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;
import stravauploader.io.TokenStore;
import stravauploader.model.File;

import java.io.IOException;

public class StravaApi {
    final static Logger log = LoggerFactory.getLogger(StravaApi.class);

    static private final String LOGIN_URL_TEMPLATE =
            "https://www.strava.com/oauth/authorize?" +
            "client_id=%s&redirect_uri=%s&response_type=code&scope=activity:write";

    static String host = "https://www.strava.com";
    static final String API_VERSION = "/api/v3";
    static final String TOKEN_URL = "/oauth/token";
    static final String GET_ATHLETE_URL = API_VERSION + "/athlete";
    static final String UPLOAD_URL = API_VERSION + "/uploads";

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
                .header("Authorization", "Bearer " + getAccessToken())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    private String getAccessToken() {
        if (isTokenExpired())
            refreshToken();

        if (token != null && StringUtils.isNotEmpty(token.access_token))
            return token.access_token;
        else
            throw new RuntimeException("Access token is empty");
    }

    private void refreshToken() {
        try {
            if (token != null && StringUtils.isNotEmpty(token.refresh_token))
                exchangeToken(false);
        } catch (Exception e) {
            throw new RuntimeException("cannot refresh token", e);
        }
    }

    private boolean isTokenExpired() {
        return token != null && Long.valueOf(token.expires_at) * 1000 < System.currentTimeMillis();
    }

    public Token exchangeToken(boolean init) throws Exception {
        RequestBody body = RequestBody.create(JSON, createTokenRequestBody(init));
        var request = new Request.Builder().url(host + TOKEN_URL)
                .post(body)
                .build();
        log.info("Get token: request: {}", body.toString());
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                var responseBody = response.body().string();
                log.info("Get token: response: {}", responseBody);
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
        if (init) {
            body.refresh_token = token.refresh_token;
        }
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

    public UploadActivityResponse uploadActivity(File file) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file.content))
                .addFormDataPart("data_type", file.type)
                .build();

        Request request = new Request.Builder()
                .url(host + UPLOAD_URL)
                .header("Authorization", "Bearer " + getAccessToken())
                .post(requestBody)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            var responseBody = response.body().string();
            log.info("uploadActivity: response: {}", responseBody);
            if (response.isSuccessful()) {
                return gson.fromJson(responseBody, UploadActivityResponse.class);
            } else {
                throw new RuntimeException("Cannot upload activity: " + file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean withoutToken() {
        return token == null || StringUtils.isEmpty(token.access_token);
    }

    static class Token {
        String token_type;
        String access_token;
        String refresh_token;
        String expires_at;
        String state;
    }

    public static class UploadActivityResponse {
        long id;
        String external_id;
        String error;
        String status;
        String activity_id;
    }

    class ExchangeTokenRequest {
        String client_id;
        String client_secret;
        String code;
        String grant_type;
        String refresh_token;

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
