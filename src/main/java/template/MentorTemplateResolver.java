package template;

import artifact.ArtifactDaoImpl;
import artifact.ArtifactModel;
import level.LevelService;
import quest.QuestDao;
import quest.QuestDaoImpl;
import user.mentor.MentorModel;
import user.user.User;
import user.user.UserDaoImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MentorTemplateResolver {
    private ViewData template;
    private ArtifactDaoImpl artifactDao;
    private UserDaoImpl userDao;
    private QuestDao questDao;
    private MentorModel mentor;

    public MentorTemplateResolver(ViewData template, User mentor) {
        this.template = template;
        this.mentor = (MentorModel) mentor;
        artifactDao = new ArtifactDaoImpl();
        questDao = new QuestDaoImpl();
        userDao = new UserDaoImpl();
    }

    public ViewData getTemplate() {
        try {
            initializeVariables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return template;
    }

    private void initializeVariables() throws SQLException {
        new LevelService().initializeLevels();
        template.setVariable("user", mentor);
        ArrayList<User> students = new ArrayList<>();
        for (String groupName : mentor.getAssociatedGroupNames()) {
            for (User user : userDao.getUserGroup(groupName)) {
                students.add(user);
            }
        }
        List<ArtifactModel> artifacts = new ArrayList<>();
        artifacts.addAll(artifactDao.getArtifactGroup("artifact_basic").getGroup());
        artifacts.addAll(artifactDao.getArtifactGroup("artifact_magic").getGroup());

        template.setVariable("artifacts", artifacts);
        template.setVariable("students", students);
        template.setVariable("classes", mentor.getAssociatedGroupNames());
        template.setVariable("quests_basic", questDao.getQuestGroup("quest_basic"));
        template.setVariable("quests_extra", questDao.getQuestGroup("quest_extra"));
    }
}

