package stravauploader.api;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class StravaApiTest {
    static StravaApi stravaApi = new StravaApi("id", "secret");
    static MockWebServer server = new MockWebServer();

    @BeforeClass
    public static void beforeTest() throws IOException {
        server.start();
        stravaApi.host = server.url("").toString();
        stravaApi.httpClient = new OkHttpClient();
    }

    @AfterClass
    public static void afterTest() throws IOException {
        server.shutdown();
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
}
