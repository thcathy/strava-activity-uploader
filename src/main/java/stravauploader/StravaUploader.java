package stravauploader;

import java.util.List;

public class StravaUploader {
    static List<String> supportedFileTypes = List.of("fit", "fit.gz", "tcx", "tcx.gz", "gpx", "gpx.gz");

    MailClient mailClient;

    public StravaUploader mailClient(MailClient mailClient) {
        this.mailClient = mailClient;
        return this;
    }
}
