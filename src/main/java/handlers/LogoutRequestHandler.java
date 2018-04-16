package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.IOException;

public class LogoutRequestHandler implements HttpHandler {

    private final SessionManager sessionManager;
    private final RequestRedirector redirector;

    public LogoutRequestHandler(SessionManager sessionManager, RequestRedirector redirector) {
        this.sessionManager = sessionManager;
        this.redirector = redirector;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        sessionManager.expireUserSession(httpExchange);
        redirector.redirect(httpExchange, "/");
    }
}
