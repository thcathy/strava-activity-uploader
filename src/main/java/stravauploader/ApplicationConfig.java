package stravauploader;

public class ApplicationConfig {
    final String stravaClientIdPropertyKey = "strava.client_id";
    final String stravaClientIdEnvKey = "STRAVA_CLIENT_ID";

    final String stravaClientSecretPropertyKey = "strava.client_secret";
    final String stravaClientSecretEnvKey = "STRAVA_CLIENT_SECRET";

    final String mailHostPropertyKey = "mail.host";
    final String mailHostEnvKey = "MAIL_HOST";

    final String mailUsernamePropertyKey = "mail.username";
    final String mailUsernameEnvKey = "MAIL_USERNAME";

    final String mailPasswordPropertyKey = "mail.password";
    final String mailPasswordEnvKey = "MAIL_PASSWORD";

    final String callbackHostPropertyKey = "callback.host";
    final String callbackHostEnvKey = "CALLBACK_HOST";

    String getValue(String propertyKey, String envKey) {
        return System.getProperty(propertyKey, System.getenv(envKey));
    }

    public String getCallbackHost() {
        return getValue(callbackHostPropertyKey, callbackHostEnvKey);
    }

    public String getStravaClientId() {
        return getValue(stravaClientIdPropertyKey, stravaClientIdEnvKey);
    }

    public String getStravaClientSecret() {
        return getValue(stravaClientSecretPropertyKey, stravaClientSecretEnvKey);
    }

    public String getMailHost() {
        return getValue(mailHostPropertyKey, mailHostEnvKey);
    }

    public String getMailUsername() {
        return getValue(mailUsernamePropertyKey, mailUsernameEnvKey);
    }

    public String getMailPassword() {
        return getValue(mailPasswordPropertyKey, mailPasswordEnvKey);
    }
}
