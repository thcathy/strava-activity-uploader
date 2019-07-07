package stravauploader;

import jodd.mail.EmailAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stravauploader.api.StravaApi;
import stravauploader.model.File;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StravaUploader {
    final static Logger log = LoggerFactory.getLogger(StravaUploader.class);
    static List<String> supportedFileTypes = List.of("fit", "fit.gz", "tcx", "tcx.gz", "gpx", "gpx.gz", "zip");

    MailClient mailClient;
    StravaApi stravaApi;

    public List<StravaApi.UploadActivityResponse> checkEmailAndUploadActivity() {
        if (stravaApi.withoutToken()) return List.of();

        log.info("Start upload activity job");
        var emails = mailClient.readEmails();
        var results = Arrays.stream(emails)
                .flatMap(m -> m.attachments().stream())
                .filter(a -> isSupportedFileTypes(a.getName()))
                .map(this::toUploadFile)
                .flatMap(this::extractZip)
                .filter(f -> isSupportedFileTypes(f.name))
                .map(this::uploadActivity)
                .collect(Collectors.toList());

        log.info("Uploaded {} file", results.size());
        log.info("Completed upload activity job");
        return results;
    }

    public File toUploadFile(EmailAttachment attachment) {
        return new File(attachment.getName(), attachment.toByteArray(), getFileTypes(attachment.getName()));
    }

    private String getFileTypes(String name) {
        for (String type : supportedFileTypes) {
            if (name.endsWith(type)) return type;
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public Stream<File> extractZip(File file) {
        if (!file.name.endsWith(".zip")) return Stream.of(file);

        Stream<File> stream = Stream.empty();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(file.content))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                zipEntry.getSize();
                var content = zis.readAllBytes();
                if (!inHiddenFolder(zipEntry.getName()) && content != null && content.length > 0) {
                    stream = Stream.concat(stream, Stream.of(new File(zipEntry.getName(), content, getFileTypes(zipEntry.getName()))));
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            log.error(e.toString(), e);
        }

        return stream;
    }

    private boolean inHiddenFolder(String name) {
        return name.startsWith("__MACOS");
    }

    public StravaApi.UploadActivityResponse uploadActivity(File file) {
        return stravaApi.uploadActivity(file);
    }

    public boolean isSupportedFileTypes(String fileName) {
        return supportedFileTypes.stream().anyMatch(type -> fileName.endsWith(type));
    }

    public StravaUploader mailClient(MailClient mailClient) {
        this.mailClient = mailClient;
        return this;
    }

    public StravaUploader stravaApi(StravaApi stravaApi) {
        this.stravaApi = stravaApi;
        return this;
    }
}
