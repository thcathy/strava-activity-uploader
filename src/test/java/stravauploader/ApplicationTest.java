package stravauploader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ApplicationTest {
    @Mock MailClient mailClient;
    @Mock StravaUploader stravaUploader;

    Application app;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        app = new Application();
        app.createInstances();
        app.mailClient = mailClient;
        app.stravaUploader = stravaUploader;
    }

    @Test
    public void test_init_willConnectToMailServer() {
        app.init();
        verify(mailClient, times(1)).connect();
    }

    @Test
    public void startUploadSchedule_willStartCallingUploader() throws InterruptedException {
        System.setProperty("job.interval.second", "1");
        app.jobInitialDelaySecond = 0;
        app.startUploadSchedule();

        Thread.sleep(1100);
        verify(stravaUploader, atLeastOnce()).checkEmailAndUploadActivity();
    }

}