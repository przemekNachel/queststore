package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import user.service.UserService;
import user.user.User;
import utils.ParametersUtil;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class LoginRequestHandler implements HttpHandler {

    private static final String USER_NAME = "username";
    private static final String USER_PASSWORD = "password";

    private final SessionManager sessionManager;
    private final RequestRedirector requestRedirector;

    public LoginRequestHandler(SessionManager sessionManager, RequestRedirector requestRedirector) {
        this.sessionManager = sessionManager;
        this.requestRedirector = requestRedirector;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        User user = getUserFromRequest(httpExchange);
        System.out.println(user);
        if (user == null) {
            requestRedirector.redirect(httpExchange, "/");
        } else {
            sessionManager.createSessionAndAssignToCookie(httpExchange, user);
            requestRedirector.redirect(httpExchange, user);
        }
    }


    public User getUserFromRequest(HttpExchange exchange) throws IOException {
        String parametres = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine();
        if (parametres == null) return null;
        System.out.println(parametres);
        Map<String, String> params = ParametersUtil.parseParameters(parametres);

        String username = params.get(USER_NAME);
        String password = params.get(USER_PASSWORD);
        System.out.println(username + " " + password);
        return login(username, password);

    }

    public User login(String username, String password) {

        UserService userSvc = new UserService();

        User user = userSvc.getUser(username);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }
}
