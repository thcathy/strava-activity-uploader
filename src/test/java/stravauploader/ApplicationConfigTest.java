package stravauploader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationConfigTest {
    ApplicationConfig config = new ApplicationConfig();

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void test_getStravaClientId() {
        assertThat(config.getStravaClientId()).isNullOrEmpty();

        environmentVariables.set("STRAVA_CLIENT_ID", "env value");
        assertThat(config.getStravaClientId()).isEqualTo("env value");

        System.setProperty("strava.client_id", "test value");
        assertThat(config.getStravaClientId()).isEqualTo("test value");
    }

    @Test
    public void test_getStravaClientSecret() {
        assertThat(config.getStravaClientSecret()).isNullOrEmpty();

        environmentVariables.set("STRAVA_CLIENT_SECRET", "env value");
        assertThat(config.getStravaClientSecret()).isEqualTo("env value");

        System.setProperty("strava.client_secret", "test value");
        assertThat(config.getStravaClientSecret()).isEqualTo("test value");
    }

    @Test
    public void test_getMailHost() {
        assertThat(config.getMailHost()).isNullOrEmpty();

        environmentVariables.set("MAIL_HOST", "env value");
        assertThat(config.getMailHost()).isEqualTo("env value");

        System.setProperty("mail.host", "test value");
        assertThat(config.getMailHost()).isEqualTo("test value");
    }

    @Test
    public void test_getMailUsername() {
        assertThat(config.getMailUsername()).isNullOrEmpty();

        environmentVariables.set("MAIL_USERNAME", "env value");
        assertThat(config.getMailUsername()).isEqualTo("env value");

        System.setProperty("mail.username", "test value");
        assertThat(config.getMailUsername()).isEqualTo("test value");
    }

    @Test
    public void test_getMailPassword() {
        assertThat(config.getMailPassword()).isNullOrEmpty();

        environmentVariables.set("MAIL_PASSWORD", "env value");
        assertThat(config.getMailPassword()).isEqualTo("env value");

        System.setProperty("mail.password", "test value");
        assertThat(config.getMailPassword()).isEqualTo("test value");
    }

    @Test
    public void test_getCallbackHost() {
        assertThat(config.getCallbackHost()).isNullOrEmpty();

        environmentVariables.set("CALLBACK_HOST", "env value");
        assertThat(config.getCallbackHost()).isEqualTo("env value");

        System.setProperty("callback.host", "test value");
        assertThat(config.getCallbackHost()).isEqualTo("test value");
    }

    @Test
    public void test_getJobPeriod() {
        assertThat(config.getJobPeriod()).isEqualTo("300");

        environmentVariables.set("JOB_INTERVAL_SECOND", "env value");
        assertThat(config.getJobPeriod()).isEqualTo("env value");

        System.setProperty("job.interval.second", "test value");
        assertThat(config.getJobPeriod()).isEqualTo("test value");
    }


}