package stravauploader.io;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TokenStoreTest {
    @Test
    public void test_saveLoadToken() throws IOException {
        var tokenStore = new TokenStore("test");
        tokenStore.save("aabbcc");

        assertThat(tokenStore.load()).isEqualTo( "aabbcc");

        Files.delete(Paths.get("temp/test.txt"));
    }

    @Test
    public void cannotSave_withThrowRuntimeException() {
        try {
            new TokenStore("\0").save("token");
            fail("did not throw exception");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    public void cannotLoad_withThrowRuntimeException() {
        try {
            new TokenStore("\0").load();
            fail("did not throw exception");
        } catch (Exception e){
            assertThat(e).isInstanceOf(RuntimeException.class);
        }

    }
}