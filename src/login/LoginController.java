package login;

import console.menu.Menu;
import user.admin.AdminController;
import user.codecooler.CodecoolerController;
import user.codecooler.CodecoolerModel;
import console.menu.AbstractConsoleView;
import user.mentor.MentorController;
import user.service.UserService;
import user.user.Role;
import user.user.User;
import user.user.UserDao;
import user.user.UserDaoImpl;

import java.lang.UnsupportedOperationException;
import java.sql.*;

public class LoginController {

    private LoginView view;

    public LoginController() {

        Menu loginMenu = null;
        this.view = new LoginView(loginMenu);

    }

    public void start() {

        String nickname;
        String password;
        view.clearScreen();
        view.printLine(view.welcomeMessage);
        boolean wantToQuit = false;
        do {

            view.printLine(view.loginMessage);

            nickname = view.getStringFromUserInput(view.loginNicknameQuestion);
            if (nickname.equals("X")) {
                break;
            }

            password = view.getPassword();

            User user = login(nickname, password);
            if (user == null) {

                view.printLine(view.loginInvalidCredentialsOrNoUser);
            } else {

                engageAppropriateController(user);
            }

        } while(!wantToQuit);
        view.printLine(view.goodbyeMessage);
        AbstractConsoleView.closeScanner();
    }

    public void engageAppropriateController(User user) {

        Role userRole = user.getRole();
        switch(userRole) {

            case ADMIN:
                new AdminController().start();
                break;

            case MENTOR:
                new MentorController().start();
                break;

            case CODECOOLER:
                new CodecoolerController((CodecoolerModel)user).start();
                break;

            default:
                throw new UnsupportedOperationException("unknown role encountered on login");
        }

    }

    public User login(String nickname, String password) {

        UserService userSvc = new UserService();

        User user = userSvc.getUser(nickname);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

}
