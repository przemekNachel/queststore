package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generic_group.Group;
import level.Level;
import level.LevelService;
import user.user.Role;
import user.user.User;
import utils.ParametersUtil;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LevelRequestHandler implements HttpHandler {

    private static final String ADD_LEVEL_PATH = "/level/add";
    private static final String LEVEL_NAME = "name";
    private static final String LEVEL_THRESHOLD = "threshold";


    private final SessionManager sessionManager;
    private final RequestRedirector redirector;

    public LevelRequestHandler(SessionManager sessionManager, RequestRedirector redirector) {
        this.sessionManager = sessionManager;
        this.redirector = redirector;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        User user = sessionManager.getUserFromSession(httpExchange);
        if (user == null || user.getRole() != Role.ADMIN) {
            redirector.redirect(httpExchange, "/");
        } else if (httpExchange.getRequestURI().getPath().equalsIgnoreCase(ADD_LEVEL_PATH)) {
            processAddLevelRequest(httpExchange);
        }

        redirector.redirect(httpExchange, user);
    }

    private void processAddLevelRequest(HttpExchange httpExchange) {

        try {
            Map<String, String> parameters = ParametersUtil.parseParameters(httpExchange.getRequestURI().getQuery());

            LevelService levelService = new LevelService();
            levelService.initializeLevels();
            
            String levelName = parameters.get(LEVEL_NAME);
            String threshold = parameters.getOrDefault(LEVEL_THRESHOLD, "0");

            Group<String> disallowedLevelNames = new Group<>("disallowed level names");

            if (levelName != null && !disallowedLevelNames.contains(levelName) && threshold.matches("[0-9]+")) {
                Level.addLevel(levelName, Integer.valueOf(threshold));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
}
