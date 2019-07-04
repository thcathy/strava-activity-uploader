package stravauploader.api;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stravauploader.io.TokenStore;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StravaApiTest {
    static StravaApi stravaApi = new StravaApi("id", "secret");
    static MockWebServer server = new MockWebServer();

    @Mock
    TokenStore tokenStore;

    @BeforeClass
    public static void beforeTest() throws IOException {
        server.start();
        stravaApi.host = server.url("").toString();
        stravaApi.httpClient = new OkHttpClient();
        stravaApi.gson = new Gson();
    }

    @AfterClass
    public static void afterTest() throws IOException {
        server.shutdown();
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_loginUrl() {
        assertThat(stravaApi.loginUrl("http://test"))
                .isEqualTo("https://www.strava.com/oauth/authorize?client_id=id&redirect_uri=http://test&response_type=code&scope=activity:write");
    }

    @Test
    public void getAthlete_withToken_willReturnContent() throws Exception {
        server.enqueue(new MockResponse().setBody("testing"));
        stravaApi.token = new StravaApi.Token();
        stravaApi.token.expires_at = "0";
        assertThat(stravaApi.getAthlete()).isEqualTo("testing");
    }

    @Test(expected = NullPointerException.class)
    public void getAthlete_withoutToken_willThrowException() throws Exception {
        assertThat(stravaApi.getAthlete());
    }

    @Test
    public void getAthlete_withExpiredToken_willRefreshAndStoreNewOne() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"token_type\":\"Bearer\",\"expires_at\":1662093734,\"refresh_token\":\"478ab59ede93fe70bcd39e0dbc8242dfdbdc990d\",\"access_token\":\"471816b123b8ad4dd304c1cdf130aa50a12e559f\"}"));
        server.enqueue(new MockResponse().setBody("testing"));
        stravaApi.token = new StravaApi.Token();
        stravaApi.token.expires_at = "1530708000";
        stravaApi.tokenStore = tokenStore;

        assertThat(stravaApi.getAthlete()).isEqualTo("testing");
        assertThat(stravaApi.token.expires_at).isEqualTo("1662093734");
        verify(tokenStore, times(1)).save(anyString());
    }
}
