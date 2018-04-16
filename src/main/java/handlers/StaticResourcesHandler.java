package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.Error404ResponseSender;
import utils.MimeTypeResolver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class StaticResourcesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        URI uri = httpExchange.getRequestURI();
        String path = "./web" + uri.getPath();

        ClassLoader classLoader = getClass().getClassLoader();

        Optional<URL> urlOptional = Optional.ofNullable(classLoader.getResource(path));

        if (urlOptional.isPresent()) {
            sendFile(urlOptional.get(), httpExchange);
        } else {
            Error404ResponseSender.send404Response(httpExchange);
        }

    }


    private void sendFile(URL url, HttpExchange exchange) throws IOException {

        File file = new File(url.getFile());
        MimeTypeResolver resolver = new MimeTypeResolver(file);
        String mimeType = resolver.getMimeType();
        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, file.length());

        try (OutputStream outputStream = exchange.getResponseBody()) {
            Files.copy(Paths.get(url.toURI()), outputStream);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }
}
