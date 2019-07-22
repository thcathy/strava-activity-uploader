package stravauploader;

import jodd.mail.EmailAttachment;
import jodd.mail.ReceivedEmail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import stravauploader.api.StravaApi;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StravaUploaderTest {
    StravaUploader uploader = new StravaUploader();

    @Mock
    MailClient mailClient;

    @Mock
    StravaApi stravaApi;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        uploader.mailClient(mailClient);
        uploader.stravaApi(stravaApi);
    }

    @Test
    public void dontUploadWithoutAttachment() {
        var email = ReceivedEmail.create();
        Mockito.when(mailClient.readEmails()).thenReturn(new ReceivedEmail[]{email});

        uploader.checkEmailAndUploadActivity();

        verify(stravaApi, times(0)).uploadActivity(any());
    }

    @Test
    public void dontUploadIfAttachmentFileTypeIsNotSupported() {
        var email = ReceivedEmail.create()
                        .attachment(
                            EmailAttachment.with()
                                    .name("test.txt")
                                    .content(new byte[0])
                        );
        Mockito.when(mailClient.readEmails()).thenReturn(new ReceivedEmail[]{email});

        uploader.checkEmailAndUploadActivity();

        verify(stravaApi, times(0)).uploadActivity(any());
    }

    @Test
    public void uploadIfAttachmentFileTypeIsSupported() {
        var email = ReceivedEmail.create()
                .attachment(
                        EmailAttachment.with()
                                .name("test.fit")
                                .content(new byte[0])
                );
        Mockito.when(mailClient.readEmails()).thenReturn(new ReceivedEmail[]{email});

        var results = uploader.checkEmailAndUploadActivity();
        assertThat(results.size()).isEqualTo(1);

        verify(stravaApi, times(1)).uploadActivity(any());
    }

    @Test
    public void uploadAllValidAttachments() {
        var email1 = ReceivedEmail.create()
                        .attachment(EmailAttachment.with().name("test.fit").content(new byte[0]));
        var attachment1 = EmailAttachment.with().name("test.fit.gz").content(new byte[0]).buildByteArrayDataSource();
        var attachment2 = EmailAttachment.with().name("test.gpx").content(new byte[0]).buildByteArrayDataSource();
        var email2 = ReceivedEmail.create().attachments(List.of(attachment1, attachment2));
        Mockito.when(mailClient.readEmails()).thenReturn(new ReceivedEmail[] { email1, email2 } );

        var results = uploader.checkEmailAndUploadActivity();
        assertThat(results.size()).isEqualTo(3);

        verify(stravaApi, times(3)).uploadActivity(any());
    }

    @Test
    public void uploadValidFilesInsideZip() throws Exception {
        File zipFile = new File(getClass().getClassLoader().getResource("01-runkeeper-data-export-2019-07-04-131816.zip").toURI());
        var attachment = EmailAttachment.with().name(zipFile.getName()).content(zipFile);
        var email = ReceivedEmail.create().attachment(attachment);
        Mockito.when(mailClient.readEmails()).thenReturn(new ReceivedEmail[] { email } );

        var results = uploader.checkEmailAndUploadActivity();
        assertThat(results.size()).isEqualTo(1);

        ArgumentCaptor<stravauploader.model.File> argCaptor = ArgumentCaptor.forClass(stravauploader.model.File.class);
        verify(stravaApi, times(1)).uploadActivity(argCaptor.capture());
        assertThat(argCaptor.getValue().name).isEqualTo("01-runkeeper-data-export-2019-07-04-131816/2019-07-01-182012.gpx");
        assertThat(argCaptor.getValue().type).isEqualTo("gpx");
    }

}