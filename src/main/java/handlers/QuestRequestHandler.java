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

    private static final String ADD_QUEST_PATH = "/quest/add";
    private static final String REMOVE_QUEST_PATH = "/quest/remove";
    private static final String EDIT_QUEST_PATH = "/quest/edit";
    private static final String MARK_QUEST_PATH = "/quest/mark";

    private static final String QUEST_NAME = "name";
    private static final String QUEST_PREVIOUS_NAME = "previousname";
    private static final String QUEST_TYPE = "type";
    private static final String QUEST_REWARD = "reward";
    private static final String QUEST_DESCRIPTION = "description";
    private static final String STUDENT_NAME = "nickname";

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
        if (user.getRole() == Role.MENTOR) {
            if (URIPath.equalsIgnoreCase(ADD_QUEST_PATH)) {
                handleAddNewQuest(httpExchange);
            } else if (URIPath.equalsIgnoreCase(REMOVE_QUEST_PATH)) {
                handleRemoveQuest(httpExchange);
            } else if (URIPath.equalsIgnoreCase(EDIT_QUEST_PATH)) {
                handleEditQuest(httpExchange);
            } else if (URIPath.equalsIgnoreCase(MARK_QUEST_PATH)) {
                handleMarkQuest(httpExchange);
            }
        }
        redirector.redirect(httpExchange, user);
    }

    public void handleAddNewQuest(HttpExchange exchange) throws IOException {
        QuestDaoImpl questDao = new QuestDaoImpl();
        String postInputData = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine();
        Map<String, String> parameters = ParametersUtil.parseParameters(postInputData);

        String name = parameters.get(QUEST_NAME);
        String type = parameters.get(QUEST_TYPE);
        String stringReward = parameters.get(QUEST_REWARD);
        String description = parameters.get(QUEST_DESCRIPTION);
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

        String questName = parameters.get(QUEST_NAME);
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
            QuestModel quest = questDao.getQuest(parameters.get(QUEST_PREVIOUS_NAME));
            quest.setName(parameters.get(QUEST_NAME));
            quest.setDescription(parameters.get(QUEST_DESCRIPTION));
            int reward = Integer.parseInt(parameters.get(QUEST_REWARD));
            quest.setReward(reward);
            questDao.updateQuest(quest, parameters.get(QUEST_PREVIOUS_NAME));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleMarkQuest(HttpExchange exchange) throws IOException {
        QuestDaoImpl questDao = new QuestDaoImpl();

        String postInputData = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine();
        Map<String, String> parameters = ParametersUtil.parseParameters(postInputData);
        String questName = parameters.get(QUEST_NAME);
        String studentName = parameters.get(STUDENT_NAME);

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
