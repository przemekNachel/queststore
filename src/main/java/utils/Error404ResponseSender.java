package utils;

import java.io.IOException;
import java.io.OutputStream;

public class Error404ResponseSender {

    public static void send404Response(com.sun.net.httpserver.HttpExchange exchange) throws IOException {

        String content = "<h1>404 error: File not found<h1>";
        exchange.getResponseHeaders().set("Content-Type", MimeTypes.MIME_TEXT_HTML);
        exchange.sendResponseHeaders(404, content.length());

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(content.getBytes());
        }
    }

}
