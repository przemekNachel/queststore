package template;

import artifact.ArtifactDaoImpl;
import artifact.ArtifactModel;
import generic_group.Group;
import level.LevelService;
import quest.QuestDao;
import quest.QuestDaoImpl;
import quest.QuestModel;
import user.codecooler.CodecoolerModel;
import user.mentor.MentorModel;
import user.service.UserService;
import user.user.User;
import user.user.UserDaoImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MentorTemplateResolver {
    private ViewData template;
    private ArtifactDaoImpl artifactDao;
    private UserDaoImpl userDao;
    private QuestDao questDao;
    private MentorModel mentor;
    private UserService userService;

    public MentorTemplateResolver(ViewData template, User mentor) {
        this.template = template;
        this.mentor = (MentorModel) mentor;
        artifactDao = new ArtifactDaoImpl();
        questDao = new QuestDaoImpl();
        userDao = new UserDaoImpl();
        userService = new UserService();
    }

    private void initializeVariables() throws SQLException {
        new LevelService().initializeLevels();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ArrayList<Owner> artifactsOwners = new ArrayList<>();
        ArrayList<Owner> questsOwners = new ArrayList<>();
        Group<User> codecoolers = userDao.getUserGroup("codecoolers");
        Group<CodecoolerModel> codecoolerModels = new Group<>("codecoolers");
        codecoolers.forEach(user -> CompletableFuture.runAsync(() -> {
            codecoolerModels.add((CodecoolerModel) user);
        }, executor));
        for (CodecoolerModel codecooler : codecoolerModels) {
            for (ArtifactModel artifact : codecooler.getArtifacts()) {
                artifactsOwners.add(new Owner(codecooler.getNickname(), artifact.getName()));
            }

        }

        for (CodecoolerModel codecooler : codecoolerModels) {
            for (QuestModel quest : questDao.getQuestGroup("quests")) {
                questsOwners.add(new Owner(codecooler.getNickname(), quest.getName()));
            }
        }
        template.setVariable("artifacts_owners", artifactsOwners);
        template.setVariable("quests_owners", questsOwners);
        template.setVariable("user", mentor);
        template.setVariable("students", codecoolers);
        template.setVariable("classes", mentor.getAssociatedGroupNames());
        template.setVariable("artifacts_normal", artifactDao.getArtifactGroup("artifact_basic"));
        template.setVariable("artifacts_magic", artifactDao.getArtifactGroup("artifact_magic"));
        template.setVariable("quests_basic", questDao.getQuestGroup("quest_basic"));
        template.setVariable("quests_extra", questDao.getQuestGroup("quest_extra"));
    }

    public ViewData getTemplate() {
        try {
            initializeVariables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return template;
    }

    class Owner {
        private String nickname;
        private String thing;

        public Owner(String nickname, String thing) {
            this.nickname = nickname;
            this.thing = thing;
        }

        public String getNickname() {
            return nickname;
        }

        public String getThing() {
            return thing;
        }
    }
}

