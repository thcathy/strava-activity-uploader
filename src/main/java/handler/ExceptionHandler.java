package handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public class ExceptionHandler implements spark.ExceptionHandler {
    private Gson gson;

    public ExceptionHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handle(Exception exception, Request request, Response response) {
         var message = new ErrorMessage();
         message.message = exception.getMessage();
         message.httpCode = response.status();
         message.body = response.body();
         response.body(gson.toJson(message));
    }

    class ErrorMessage {
        private String message;
        private int httpCode;
        private String body;
    }
}
