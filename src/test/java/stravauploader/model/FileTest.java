package stravauploader.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileTest {

    @Test
    public void test_toString() {
        var text = new File("filename", new byte[0], "zip").toString();
        Assertions.assertThat(text).isEqualTo("File[name='filename', type='zip', content=[]]");
    }
}