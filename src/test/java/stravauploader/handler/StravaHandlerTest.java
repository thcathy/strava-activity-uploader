package stravauploader.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spark.Request;
import spark.Response;
import stravauploader.ApplicationConfig;
import stravauploader.api.StravaApi;

import static org.mockito.Mockito.*;

public class StravaHandlerTest {
    @Mock
    StravaApi stravaApi;

    @Mock
    ApplicationConfig config;

    StravaHandler handler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        handler = new StravaHandler(config, stravaApi);
    }

    @Test
    public void openLoginWithoutHostSetting_willUseServerHost() {
        var request = mock(Request.class);
        var response = mock(Response.class);
        when(request.host()).thenReturn("192.168.0.1");
        when(request.scheme()).thenReturn("scp");

        handler.openLogin(request, response);
        verify(stravaApi, times(1)).loginUrl("scp://192.168.0.1/strava/callback");
        verify(response, times(1)).redirect(any());
    }

    @Test
    public void openLoginWithHost_willUseHostSetup() {
        var request = mock(Request.class);
        var response = mock(Response.class);
        when(config.getCallbackHost()).thenReturn("http://google.com");

        handler.openLogin(request, response);
        verify(stravaApi, times(1)).loginUrl("http://google.com/strava/callback");
        verify(response, times(1)).redirect(any());
    }
}