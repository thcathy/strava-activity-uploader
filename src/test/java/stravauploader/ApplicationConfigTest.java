package stravauploader;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ApplicationConfigTest {

    @Test
    public void test_getStravaClientId() {
        assertThat(ApplicationConfig.getStravaClientId()).isNullOrEmpty();

        System.setProperty("strava.client_id", "test value");
        assertThat(ApplicationConfig.getStravaClientId()).isEqualTo("test value");
    }

    @Test
    public void test_getStravaClientSecret() {
        assertThat(ApplicationConfig.getStravaClientSecret()).isNullOrEmpty();

        System.setProperty("strava.client_secret", "test value");
        assertThat(ApplicationConfig.getStravaClientSecret()).isEqualTo("test value");
    }

    @Test
    public void test_getMailHost() {
        assertThat(ApplicationConfig.getMailHost()).isNullOrEmpty();

        System.setProperty("mail.host", "test value");
        assertThat(ApplicationConfig.getMailHost()).isEqualTo("test value");
    }

    @Test
    public void test_getMailUsername() {
        assertThat(ApplicationConfig.getMailUsername()).isNullOrEmpty();

        System.setProperty("mail.username", "test value");
        assertThat(ApplicationConfig.getMailUsername()).isEqualTo("test value");
    }

    @Test
    public void test_getMailPassword() {
        assertThat(ApplicationConfig.getMailPassword()).isNullOrEmpty();

        System.setProperty("mail.password", "test value");
        assertThat(ApplicationConfig.getMailPassword()).isEqualTo("test value");
    }
}