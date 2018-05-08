package handlers;

import artifact.ArtifactDaoImpl;
import artifact.ArtifactModel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generic_group.Group;
import level.Level;
import level.LevelDaoImpl;
import quest.QuestDaoImpl;
import quest.QuestModel;
import user.codecooler.CodecoolerModel;
import user.user.RawUser;
import user.user.Role;
import user.user.User;
import user.user.UserDaoImpl;
import user.wallet.WalletDaoImpl;
import user.wallet.WalletService;
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
            } else if (method.equalsIgnoreCase("post") && URIPath.equalsIgnoreCase("/quest/remove")) {
                handleRemoveQuest(httpExchange);
            } else if (method.equalsIgnoreCase("get") && URIPath.equalsIgnoreCase("/quest/edit")) {
                handleEditQuest(httpExchange);
            } else if (URIPath.equalsIgnoreCase("/quest/mark")) {
                handleMarkQuest(httpExchange);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleRemoveQuest(HttpExchange exchange) throws IOException {
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

    public void handleEditQuest(HttpExchange exchange) throws IOException {
        QuestDaoImpl questDao = new QuestDaoImpl();
        Map<String, String> parameters = ParametersUtil.parseParameters(exchange.getRequestURI().getQuery());
        try {
            QuestModel quest = questDao.getQuest(parameters.get("previousname"));
            quest.setName(parameters.get("name"));
            quest.setDescription(parameters.get("description"));
            int reward = Integer.parseInt(parameters.get("reward"));
            quest.setReward(reward);
            questDao.updateQuest(quest, parameters.get("previousname"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleMarkQuest(HttpExchange exchange) throws IOException {
        QuestDaoImpl questDao = new QuestDaoImpl();

        String postInputData = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine();
        Map<String, String> parameters = ParametersUtil.parseParameters(postInputData);
        String questName = parameters.get("name");
        String studentName = parameters.get("nickname");

        try {
            QuestModel quest = questDao.getQuest(questName);
            CodecoolerModel codecooler = createCodecooler(studentName);
            System.out.println(codecooler.getWallet().getBalance());

            int reward = quest.getReward();
            codecooler.getWallet().payIn(reward);
            codecooler.getLevel().addExperience(reward);

            new WalletDaoImpl().updateWallet(new UserDaoImpl().getUserId(codecooler.getNickname()), codecooler.getWallet());

            System.out.println(codecooler.getWallet().getBalance());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CodecoolerModel createCodecooler(String userName) throws SQLException {
        UserDaoImpl userDao = new UserDaoImpl();
        WalletDaoImpl walletDao = new WalletDaoImpl();
        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();
        LevelDaoImpl levelDao = new LevelDaoImpl();

        RawUser user = userDao.getUser(userName);
        int userID = new UserDaoImpl().getUserId(user.getNickname());
        WalletService userWallet = walletDao.getWallet(userID);
        Group<ArtifactModel> userArtifacts = artifactDao.getUserArtifacts(userID);
        Level userLevel = levelDao.getLevel(userID);

        return new CodecoolerModel(user, userWallet, userArtifacts, userLevel);

    }


}
