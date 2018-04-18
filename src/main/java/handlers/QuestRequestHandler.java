package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import quest.QuestDao;
import quest.QuestDaoImpl;
import quest.QuestModel;
import user.user.Role;
import user.user.User;
import utils.ParametersUtil;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;

public class QuestRequestHandler implements HttpHandler {


    private final SessionManager sessionManager;
    private final RequestRedirector redirector;

    public QuestRequestHandler(SessionManager sessionManager, RequestRedirector redirector) {
        this.sessionManager = sessionManager;
        this.redirector = redirector;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        User user = sessionManager.getUserFromSession(httpExchange);
        if (user.getRole() == Role.MENTOR) {
            if (httpExchange.getRequestMethod().equalsIgnoreCase("post")) {
                String postInputData = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody())).readLine();

                Map<String, String> parameters = ParametersUtil.parseParameters(postInputData);
                addNewQuest(parameters);
            }
        }
        redirector.redirect(httpExchange, user);
    }

    public void addNewQuest(Map<String, String> parameters) {
        QuestDaoImpl questDao = new QuestDaoImpl();
        String name = parameters.get("name");
        String stringReward = parameters.get("reward");
        String description = parameters.get("description");
        int reward = Integer.parseInt(stringReward);
        QuestModel newQuest = new QuestModel(name, description, reward);
        try {
            questDao.addQuest(newQuest);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
