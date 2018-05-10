package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generic_group.Group;
import user.mentor.MentorModel;
import user.service.UserService;
import user.user.RawUser;
import user.user.Role;
import user.user.User;
import user.user.UserDaoImpl;
import utils.ParametersUtil;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class UserRequestHandler implements HttpHandler {

    private static final String ADD_USER_PATH = "/user/add";
    private static final String REMOVE_USER_PATH = "/user/remove";
    private static final String EDIT_USER_PATH = "/user/edit";

    private static final String USER_NICKNAME = "nickname";
    private static final String USER_EMAIL = "email";
    private static final String USER_TYPE = "type";
    private static final String USER_PREVIOUS_NICKNAME = "previousnickname";
    private static final String USER_NEW_NICKNAME = "newnickname";
    private static final String STUDENT_TYPE = "student";
    private static final String MENTOR_TYPE = "mentor";
    private static final String MENTOR_GROUPS = "mentor groups";
    private static final String MENTORS = "mentors";

    private final SessionManager sessionManager;
    private final RequestRedirector redirector;

    private final UserService userSvc = new UserService();
    private final UserDaoImpl userDao = new UserDaoImpl();

    public UserRequestHandler(SessionManager sessionManager, RequestRedirector redirector) {
        this.sessionManager = sessionManager;
        this.redirector = redirector;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        User user = sessionManager.getUserFromSession(httpExchange);
        String path = httpExchange.getRequestURI().getPath();
        Map<String, String> parameters = ParametersUtil.parseParameters(httpExchange.getRequestURI().getQuery());

        if (user == null || user.getRole() == Role.CODECOOLER) {
            redirector.redirect(httpExchange, ",");
        } else if (path.equalsIgnoreCase(ADD_USER_PATH)) {
            processAddUserRequest(parameters);
        } else if (path.equalsIgnoreCase(REMOVE_USER_PATH)) {
            processRemoveUserRequest(parameters);
        } else if (path.equalsIgnoreCase(EDIT_USER_PATH)) {
            processEditUserRequest(parameters);
        }

        redirector.redirect(httpExchange, user);
    }

    private void processEditUserRequest(Map<String, String> parameters) {

        try {
            parameters.forEach((key, value) -> {
                System.out.println(key + " " + value);
            });
            String previousNickname = parameters.get(USER_PREVIOUS_NICKNAME);
            String newNickname = parameters.get(USER_NEW_NICKNAME);
            String email = parameters.get(USER_EMAIL);

            User user = userSvc.getUser(previousNickname);

            int userId = userDao.getUserId(previousNickname);
            userDao.upgradeCredentials(newNickname, user.getPassword(), email, userId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processRemoveUserRequest(Map<String, String> parameters) {
        String nickname = parameters.get(USER_NICKNAME);

        userDao.deleteUser(nickname);
    }

    private void processAddUserRequest(Map<String, String> parameters) {
        String email = parameters.get(USER_EMAIL);
        String nickname = parameters.get(USER_NICKNAME);
        String type = parameters.get(USER_TYPE);

        if (type.equalsIgnoreCase(STUDENT_TYPE)) createCodecooler(email, nickname);
        else if (type.equalsIgnoreCase(MENTOR_TYPE)) createMentor(email, nickname);

    }

    private void createMentor(String email, String nickname) {
        String password = UUID.randomUUID().toString();
        Group<String> mentorGroups = new Group<>(MENTOR_GROUPS);
        mentorGroups.add(MENTORS);
        MentorModel mentor = new MentorModel(new RawUser(Role.MENTOR, nickname, email, password, mentorGroups));

        userSvc.addUser(mentor);
    }

    private void createCodecooler(String email, String nickname) {
        String password = UUID.randomUUID().toString();
        userSvc.createCodecooler(nickname, email, password);
    }

}
