package template;

import artifact.ArtifactDaoImpl;
import level.LevelDaoImpl;
import level.LevelService;
import user.admin.AdminModel;
import user.codecooler.CodecoolerModel;
import user.service.UserService;
import user.user.User;
import user.user.UserDaoImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class AdminTemplateResolver {

    private ViewData template;
    private ArtifactDaoImpl artifactDao;
    private CodecoolerModel codecooler;
    private AdminModel admin;
    private UserService userService;
    private LevelDaoImpl levelDao;
    private UserDaoImpl userDao;

    public AdminTemplateResolver(ViewData template, User admin) {
        this.template = template;
        this.admin = (AdminModel) admin;
        artifactDao = new ArtifactDaoImpl();
        userService = new UserService();
        userDao = new UserDaoImpl();
        levelDao = new LevelDaoImpl();
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
        String[] groups = {"codecoolers", "mentors", "admins"};
        ArrayList<User> users = new ArrayList<>();
        for (String group : groups) {
            for (User user : userDao.getUserGroup(group)) {
                users.add(user);
            }
        }
        Map<Integer, String> levels = levelDao.getLevelCollection();
        template.setVariable("user", admin);
        template.setVariable("users", users);
        template.setVariable("levels", levels);
    }
}
