package stravauploader;

import spark.utils.StringUtils;

public class ApplicationConfig {
    private final String stravaClientIdPropertyKey = "strava.client_id";
    private final String stravaClientIdEnvKey = "STRAVA_CLIENT_ID";

    private final String stravaClientSecretPropertyKey = "strava.client_secret";
    private final String stravaClientSecretEnvKey = "STRAVA_CLIENT_SECRET";

    private final String mailHostPropertyKey = "mail.host";
    private final String mailHostEnvKey = "MAIL_HOST";

    private final String mailUsernamePropertyKey = "mail.username";
    private final String mailUsernameEnvKey = "MAIL_USERNAME";

    private final String mailPasswordPropertyKey = "mail.password";
    private final String mailPasswordEnvKey = "MAIL_PASSWORD";

    private final String callbackHostPropertyKey = "callback.host";
    private final String callbackHostEnvKey = "CALLBACK_HOST";

    private final String jobPeriodPropertyKey = "job.period.second";
    private final String jobPeriodEnvKey = "JOB_PERIOD_SECOND";
    private final String defaultJobPeriod = "300";

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

    public String getJobPeriod() {
        var value = getValue(jobPeriodPropertyKey, jobPeriodEnvKey);
        if (StringUtils.isEmpty(value))
            return defaultJobPeriod;
        else
            return getValue(jobPeriodPropertyKey, jobPeriodEnvKey);
    }

}
