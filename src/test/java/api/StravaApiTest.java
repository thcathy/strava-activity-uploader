package api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StravaApiTest {
    StravaApi stravaApi = new StravaApi("id", "secret");

    @Test
    public void test_loginUrl() {
        assertThat(stravaApi.loginUrl("http://test"))
                .isEqualTo("https://www.strava.com/oauth/authorize?client_id=id&redirect_uri=http://test&response_type=code&scope=activity:write");
    }
}
