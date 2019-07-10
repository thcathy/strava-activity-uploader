package stravauploader.api;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stravauploader.io.TokenStore;
import stravauploader.model.File;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StravaApiTest {
    StravaApi stravaApi = new StravaApi("id", "secret");
    MockWebServer server = new MockWebServer();
    Gson gson = new Gson();

    @Mock
    TokenStore tokenStore;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @After
    public void afterTest() throws IOException {
        server.shutdown();
    }

    @Before
    public void setup() throws IOException {
        server.start();
        stravaApi.host = server.url("").toString();
        stravaApi.httpClient = new OkHttpClient();
        stravaApi.gson = gson;
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_loginUrl() {
        assertThat(stravaApi.loginUrl("http://test"))
                .isEqualTo("https://www.strava.com/oauth/authorize?client_id=id&redirect_uri=http://test&response_type=code&scope=activity:write");
    }

    @Test
    public void initToken_withCode_shouldGetANewToken() throws Exception {
        server.enqueue(new MockResponse().setBody(getValidTokenResponse()));
        stravaApi.code = "testing_code";
        stravaApi.exchangeToken(true);

        assertThat(stravaApi.token.access_token).isEqualTo("471816b123b8ad4dd304c1cdf130aa50a12e559f");
    }

    @Test
    public void getAthlete_withToken_willReturnContent() throws Exception {
        server.enqueue(new MockResponse().setBody("testing"));
        stravaApi.token = getValidToken();
        assertThat(stravaApi.getAthlete()).isEqualTo("testing");
    }

    @Test
    public void getAthlete_withoutToken_willThrowException() {
        try {
            stravaApi.token = null;
            stravaApi.getAthlete();
            fail("Should have thrown SomeException but did not!");
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("Access token is empty");
        }
    }

    @Test
    public void getAthlete_withExpiredToken_willRefreshAndStoreNewOne() throws Exception {
        server.enqueue(new MockResponse().setBody(getValidTokenResponse()));
        server.enqueue(new MockResponse().setBody("testing"));
        stravaApi.token = new StravaApi.Token();
        stravaApi.token.expires_at = "1530708000";
        stravaApi.token.refresh_token = "xyz";
        stravaApi.tokenStore = tokenStore;

        assertThat(stravaApi.getAthlete()).isEqualTo("testing");
        assertThat(stravaApi.token.expires_at).isEqualTo("1662093734");
        verify(tokenStore, times(1)).save(anyString());
    }

    @Test
    public void uploadActivity_withToken_willReturnSuccessResponse() {
        StravaApi.UploadActivityResponse responseBody = getUploadSuccessResponse();
        server.enqueue(new MockResponse().setBody(gson.toJson(responseBody)));
        stravaApi.token = getValidToken();

        var response = stravaApi.uploadActivity(new File("test.fit", new byte[1], "fit"));
        assertThat(response.error).isNullOrEmpty();
        assertThat(response.status).isEqualTo("Your activity is still being processed.");
    }

    @Test
    public void uploadActivity_withoutToken_willThrowException() {
        try {
            stravaApi.token = null;
            stravaApi.uploadActivity(new File("test.fit", new byte[1], "fit"));
            fail("Should have thrown SomeException but did not!");
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("Access token is empty");
        }
    }

    private String getValidTokenResponse() {
        return "{\"token_type\":\"Bearer\",\"expires_at\":1662093734,\"refresh_token\":\"478ab59ede93fe70bcd39e0dbc8242dfdbdc990d\",\"access_token\":\"471816b123b8ad4dd304c1cdf130aa50a12e559f\"}";
    }

    private StravaApi.UploadActivityResponse getUploadSuccessResponse() {
        StravaApi.UploadActivityResponse response = new StravaApi.UploadActivityResponse();
        response.status = "Your activity is still being processed.";
        response.external_id = "test.fit";
        response.id = 16486788;
        return response;
    }

    private StravaApi.Token getValidToken() {
        var token = new StravaApi.Token();
        token.expires_at = "9000000000";
        token.access_token = "abcde";
        token.refresh_token = "xyz";
        return token;
    }
}
