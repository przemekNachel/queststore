package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
        String URIPath = httpExchange.getRequestURI().getPath();
        String method = httpExchange.getRequestMethod();
        if (user.getRole() == Role.MENTOR) {
            if (method.equalsIgnoreCase("post") && URIPath.equalsIgnoreCase("/quest/add")) {
                handleAddNewQuest(httpExchange);
            }else if(method.equalsIgnoreCase("post") && URIPath.equalsIgnoreCase("/quest/remove")){
                handleRemoveQuest(httpExchange);
            }
        }
        redirector.redirect(httpExchange, user);
    }

    public void handleAddNewQuest(HttpExchange exchange) throws IOException {
        QuestDaoImpl questDao = new QuestDaoImpl();
        String postInputData = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine();
        Map<String, String> parameters = ParametersUtil.parseParameters(postInputData);

        String name = parameters.get("name");
        String type = parameters.get("type");
        String stringReward = parameters.get("reward");
        String description = parameters.get("description");
        int reward = Integer.parseInt(stringReward);
        QuestModel newQuest = new QuestModel(name, description, reward);
        try {
            questDao.addQuest(newQuest);
            questDao.addQuestAdherence(name, "quest_" + type);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void handleRemoveQuest(HttpExchange exchange) throws IOException{
        QuestDaoImpl questDao = new QuestDaoImpl();
        String postInputData = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine();
        Map<String, String> parameters = ParametersUtil.parseParameters(postInputData);

        String questName = parameters.get("name");
        try {
            QuestModel quest = questDao.getQuest(questName);
            questDao.deleteQuest(quest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
