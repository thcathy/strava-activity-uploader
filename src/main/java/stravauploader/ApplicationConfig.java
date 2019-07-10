package stravauploader;

public class ApplicationConfig {
    static final String stravaClientIdPropertyKey = "strava.client_id";
    static final String stravaClientIdEnvKey = "STRAVA_CLIENT_ID";

    static final String stravaClientSecretPropertyKey = "strava.client_secret";
    static final String stravaClientSecretEnvKey = "STRAVA_CLIENT_SECRET";

    static final String mailHostPropertyKey = "mail.host";
    static final String mailHostEnvKey = "MAIL_HOST";

    static final String mailUsernamePropertyKey = "mail.username";
    static final String mailUsernameEnvKey = "MAIL_USERNAME";

    static final String mailPasswordPropertyKey = "mail.password";
    static final String mailPasswordEnvKey = "MAIL_PASSWORD";

    static String getValue(String propertyKey, String envKey) {
        return System.getProperty(propertyKey, System.getenv(envKey));
    }

    public static String getStravaClientId() {
        return getValue(stravaClientIdPropertyKey, stravaClientIdEnvKey);
    }

    public static String getStravaClientSecret() {
        return getValue(stravaClientSecretPropertyKey, stravaClientSecretEnvKey);
    }

    public static String getMailHost() {
        return getValue(mailHostPropertyKey, mailHostEnvKey);
    }

    public static String getMailUsername() {
        return getValue(mailUsernamePropertyKey, mailUsernameEnvKey);
    }

    public static String getMailPassword() {
        return getValue(mailPasswordPropertyKey, mailPasswordEnvKey);
    }
}
