package handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;

public class HandlerUtils {
    private final static Logger log = LoggerFactory.getLogger(HandlerUtils.class);

    static String processException(Response response, Exception e) {
        log.error("cannot process", e);
        response.status(500);
        return "fail: " + e.getMessage();
    }
}
