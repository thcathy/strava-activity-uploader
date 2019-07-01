import jodd.mail.EmailFilter;
import jodd.mail.ImapServer;
import jodd.mail.MailServer;
import jodd.mail.ReceiveMailSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Flags;

public class MailClient {
    private final static Logger log = LoggerFactory.getLogger(MailClient.class);

    private String host;
    private String username;
    private String password;

    private ImapServer server;

    public void connect() {
        log.info("Connecting to {}", host);
        server = MailServer.create()
                .host(host)
                .ssl(true)
                .auth(username, password)
                .buildImapMailServer();
        log.info("Connected to {}", host);
    }

    public void readEmails() {
        ReceiveMailSession session = server.createSession();
        session.open();
        var emails = session
                .receive()
                .markSeen()
                .filter(EmailFilter.filter().flag(Flags.Flag.SEEN, false))
                .get();
        for (var email : emails) {
            log.info("{}: {}", email.subject(), email.originalMessage());
        }
        session.close();
    }

    public MailClient setHost(String host) {
        this.host = host;
        return this;
    }

    public MailClient setUsername(String username) {
        this.username = username;
        return this;
    }

    public MailClient setPassword(String password) {
        this.password = password;
        return this;
    }
}
