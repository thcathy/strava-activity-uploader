package io;

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
}