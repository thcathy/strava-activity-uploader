package stravauploader.io;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenStoreTest {
    @Test
    public void test_saveLoadToken() throws IOException {
        var tokenStore = new TokenStore("test");
        tokenStore.save("aabbcc");

        assertThat(tokenStore.load()).isEqualTo( "aabbcc");

        Files.delete(Paths.get("temp/test.txt"));
    }

    @Test(expected = RuntimeException.class)
    public void cannotSave_withThrowRuntimeException() {
        new TokenStore("\0").save("token");
    }

    @Test(expected = RuntimeException.class)
    public void cannotLoad_withThrowRuntimeException() {
        new TokenStore("\0").load();
    }
}