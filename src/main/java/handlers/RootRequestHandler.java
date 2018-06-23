package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import user.user.User;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.IOException;

public class RootRequestHandler implements HttpHandler {

    private final SessionManager sessionManager;
    private final RequestRedirector redirector;

    public RootRequestHandler(SessionManager sessionManager, RequestRedirector redirector) {
        this.sessionManager = sessionManager;
        this.redirector = redirector;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        User user = sessionManager.getUserFromSession(httpExchange);
        if (user == null) {
            redirector.redirect(httpExchange, "/");
        } else {
            redirector.redirect(httpExchange, user);
        }

    }
}
