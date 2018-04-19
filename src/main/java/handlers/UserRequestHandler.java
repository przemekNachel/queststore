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
        } else if (path.equalsIgnoreCase("/user/add")) {
            processAddUserRequest(parameters);
        } else if (path.equalsIgnoreCase("/user/remove")) {
            processRemoveUserRequest(parameters);
        } else if (path.equalsIgnoreCase("/user/edit")) {
            processEditUserRequest(parameters);
        }

        redirector.redirect(httpExchange, user);
    }

    private void processEditUserRequest(Map<String, String> parameters) {

        try {
            parameters.forEach((key, value) -> {
                System.out.println(key + " " + value);
            });
            String type = parameters.get("type");
            String previousNickname = parameters.get("previousnickname");
            String newNickname = parameters.get("newnickname");
            String email = parameters.get("email");

            User user = userSvc.getUser(previousNickname);

            int userId = userDao.getUserId(previousNickname);
            userDao.upgradeCredentials(newNickname, user.getPassword(), email, userId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processRemoveUserRequest(Map<String, String> parameters) {
        String nickname = parameters.get("nickname");

        userDao.deleteUser(nickname);
    }

    private void processAddUserRequest(Map<String, String> parameters) {


        String email = parameters.get("email");
        String nickname = parameters.get("nickname");
        String type = parameters.get("type");

        if (type.equalsIgnoreCase("student")) createCodecooler(email, nickname);
        else if (type.equalsIgnoreCase("mentor")) createMentor(email, nickname);

    }

    private void createMentor(String email, String nickname) {
        String password = UUID.randomUUID().toString();
        Group<String> mentorGroups = new Group<>("mentor groups");
        mentorGroups.add("mentors");
        MentorModel mentor = new MentorModel(new RawUser(Role.MENTOR, nickname, email, password, mentorGroups));

        userSvc.addUser(mentor);
    }

    private void createCodecooler(String email, String nickname) {
        String password = UUID.randomUUID().toString();
        userSvc.createCodecooler(nickname, email, password);
    }

}
