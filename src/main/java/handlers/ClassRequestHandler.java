package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generic_group.Group;
import user.service.UserService;
import user.user.Role;
import user.user.User;
import utils.ParametersUtil;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ClassRequestHandler implements HttpHandler {


    private final SessionManager sessionManager;
    private final RequestRedirector redirector;
    UserService userSvc = new UserService();

    public ClassRequestHandler(SessionManager sessionManager, RequestRedirector redirector) {
        this.sessionManager = sessionManager;
        this.redirector = redirector;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        User user = sessionManager.getUserFromSession(httpExchange);
        if (user == null || user.getRole() != Role.ADMIN) {
            redirector.redirect(httpExchange, "/");
        } else if (httpExchange.getRequestURI().getPath().equalsIgnoreCase("/class/add")) {
            processAddClassRequest(httpExchange);
        }

        redirector.redirect(httpExchange, user);
    }

    private void processAddClassRequest(HttpExchange httpExchange) {

        try {
            Group<String> disallowedGroupNames = userSvc.getUserGroupNames();
            Map<String, String> parameters = ParametersUtil.parseParameters(httpExchange.getRequestURI().getQuery());
            String groupName = parameters.getOrDefault("name", "");
            Group<User> newGroup = new Group<>(groupName);

            if (disallowedGroupNames.contains(groupName) || groupName.isEmpty()) {
                userSvc.addUserGroup(newGroup);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
}
