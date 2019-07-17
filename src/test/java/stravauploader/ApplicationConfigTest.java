package stravauploader;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ApplicationConfigTest {
    ApplicationConfig config = new ApplicationConfig();

    @Test
    public void test_getStravaClientId() {
        assertThat(config.getStravaClientId()).isNullOrEmpty();

        System.setProperty("strava.client_id", "test value");
        assertThat(config.getStravaClientId()).isEqualTo("test value");
    }

    @Test
    public void test_getStravaClientSecret() {
        assertThat(config.getStravaClientSecret()).isNullOrEmpty();

        System.setProperty("strava.client_secret", "test value");
        assertThat(config.getStravaClientSecret()).isEqualTo("test value");
    }

    @Test
    public void test_getMailHost() {
        assertThat(config.getMailHost()).isNullOrEmpty();

        System.setProperty("mail.host", "test value");
        assertThat(config.getMailHost()).isEqualTo("test value");
    }

    @Test
    public void test_getMailUsername() {
        assertThat(config.getMailUsername()).isNullOrEmpty();

        System.setProperty("mail.username", "test value");
        assertThat(config.getMailUsername()).isEqualTo("test value");
    }

    @Test
    public void test_getMailPassword() {
        assertThat(config.getMailPassword()).isNullOrEmpty();

        System.setProperty("mail.password", "test value");
        assertThat(config.getMailPassword()).isEqualTo("test value");
    }

    @Test
    public void test_getCallbackHost() {
        assertThat(config.getCallbackHost()).isNullOrEmpty();

        System.setProperty("callback.host", "test value");
        assertThat(config.getCallbackHost()).isEqualTo("test value");
    }

}