package template;

import artifact.ArtifactDaoImpl;
import level.LevelService;
import quest.QuestDao;
import quest.QuestDaoImpl;
import user.mentor.MentorModel;
import user.user.User;
import user.user.UserDaoImpl;

import java.sql.SQLException;

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
        template.setVariable("students", userDao.getUserGroup("codecoolers"));
        template.setVariable("classes", mentor.getAssociatedGroupNames());
        template.setVariable("artifacts_normal", artifactDao.getArtifactGroup("artifact_basic"));
        template.setVariable("artifacts_magic", artifactDao.getArtifactGroup("artifact_magic"));
        template.setVariable("quests_basic", questDao.getQuestGroup("quest_basic"));
        template.setVariable("quests_extra", questDao.getQuestGroup("quest_extra"));
    }
}

