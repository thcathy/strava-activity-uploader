package stravauploader.handler;

import org.junit.Test;
import org.mockito.Mockito;
import spark.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class HandlerUtilsTest {

    @Test
    public void test_processException() {
        var response = Mockito.mock(Response.class);
        var result = HandlerUtils.processException(response, new RuntimeException("testing"));

        verify(response).status(500);
        assertThat(result).isEqualTo("fail: testing");
    }
}