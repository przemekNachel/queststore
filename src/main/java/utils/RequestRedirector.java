package utils;

import com.sun.net.httpserver.HttpExchange;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import template.CodecoolerTemplateResolver;
import template.ViewData;
import user.user.Role;
import user.user.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class RequestRedirector {

    private final URIPathMapper uriPathMapper = URIPathMapper.getInstance();
    TemplateEngine engine = new TemplateEngine();
    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

    public RequestRedirector(SessionManager sessionManager) {
        createTheamLeaf();
    }

    public void redirect(HttpExchange exchange, String pathName) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", MimeTypes.MIME_TEXT_HTML);
        if (pathName.equals("/")) redirectToIndex(exchange, uriPathMapper.getMapping(pathName));
    }

    public void redirect(HttpExchange exchange, User user) throws IOException {
        if (user.getRole() == Role.CODECOOLER) {
            redirectToCodecooler(exchange, uriPathMapper.getMapping("/student"), user);
        }
        if (user.getRole() == Role.ADMIN) {
            redirectToAdmin(exchange, uriPathMapper.getMapping("/admin"), user);
        }
        if (user.getRole() == Role.MENTOR) {
            redirectToMentor(exchange, uriPathMapper.getMapping("/mentor"), user);
        }
    }

    private void redirectToMentor(HttpExchange exchange, ViewData template, User user) throws IOException {
        template.setVariable("user", user);
        String content = engine.process(template.getName(), template.getContext());
        sendTemplate(exchange, content);
    }

    private void redirectToAdmin(HttpExchange exchange, ViewData template, User user) throws IOException {
        template.setVariable("user", user);
        String content = engine.process(template.getName(), template.getContext());
        sendTemplate(exchange, content);
    }

    private void redirectToIndex(HttpExchange exchange, ViewData template) throws IOException {
        String content = engine.process(template.getName(), template.getContext());
        sendTemplate(exchange, content);
    }

    private void redirectToCodecooler(HttpExchange exchange, ViewData template, User user) throws IOException {
        CodecoolerTemplateResolver codecooler = new CodecoolerTemplateResolver(template, user);
        ViewData codecoolerTemplate = codecooler.getTemplate();
        String content = engine.process(codecoolerTemplate.getName(), codecoolerTemplate.getContext());
        sendTemplate(exchange, content);
    }

    public void createTheamLeaf() {
        resolver.setPrefix("web/");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setSuffix(".html");
        engine.setTemplateResolver(resolver);


    }

    private void sendTemplate(HttpExchange httpExchange, String content) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", MimeTypes.MIME_TEXT_HTML);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, content.length());

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(content.getBytes());
            os.flush();
        }

        httpExchange.close();

    }
}
