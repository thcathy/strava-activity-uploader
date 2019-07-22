package stravauploader;

import jodd.mail.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class MailClientTest {
    @Mock
    ImapServer server;

    MailClient mailClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mailClient = new MailClient();
        mailClient.server = server;
    }

    @Nested
    @DisplayName("Exception flows")
    class ExceptionFlows {
        @Test
        public void connect_withoutUsername_withThrowException() {
            try {
                new MailClient().connect();
                fail("should throw exception");
            } catch (Exception e) {
                assertThat(e).isInstanceOf(NullPointerException.class);
            }
        }

        @Test
        public void readEmails_withWrongHost_willThrowException() {
            var tempClient = new MailClient().setHost("unknown host").setUsername("dummy").setPassword("dummy");
            tempClient.connect();
            try {
                tempClient.readEmails();
            } catch (Exception e) {
                assertThat(e).isInstanceOf(MailException.class);
            }
        }

        @Test
        public void readEmails_withWrongAccount_willThrowException() {
            var tempClient = new MailClient().setHost("imap.gmail.com").setUsername("dummy").setPassword("dummy");
            tempClient.connect();
            try {
                tempClient.readEmails();
            } catch (Exception e) {
                assertThat(e).isInstanceOf(MailException.class);
            }
        }
    }

    @Test
    public void connect_willSetServerVariable() {
        var tempClient = new MailClient().setHost("wrong host").setUsername("dummy").setPassword("dummy");
        tempClient.connect();
        assertThat(tempClient.server).isNotNull();
    }

    @Test
    public void readEmails_willOpenSession_andReturnEmails() {
        var session = Mockito.mock(ReceiveMailSession.class);
        var receiverBuilder = Mockito.mock(ReceiverBuilder.class);
        var email = ReceivedEmail.create();

        when(server.createSession()).thenReturn(session);
        when(session.receive()).thenReturn(receiverBuilder);
        when(receiverBuilder.markSeen()).thenReturn(receiverBuilder);
        when(receiverBuilder.filter(any())).thenReturn(receiverBuilder);
        when(receiverBuilder.get()).thenReturn(new ReceivedEmail[]{email});

        var emails = mailClient.readEmails();
        assertThat(emails.length).isEqualTo(1);
        assertThat(emails[0]).isSameAs(email);

        verify(session, times(1)).open();
        verify(session, times(1)).close();
    }
}