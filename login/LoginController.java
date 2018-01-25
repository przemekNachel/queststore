package Login;

import User.Admin.AdminController;
import User.Codecooler.CodecoolerController;
import User.Codecooler.CodecoolerModel;
import Console.AbstractConsoleView;

import java.lang.UnsupportedOperationException;
import java.sql.*;

public class LoginController {

    private LoginView view;

    public LoginController() {

        Menu loginMenu = null;
        this.view = new LoginView(loginMenu);

    }

    public void start() {

        String nickname = "";
        String password = "";
        view.clearScreen();
        view.printLine(view.welcomeMessage);
        boolean wantToQuit = false;
        do {

            view.printLine(view.loginMessage);

            nickname = view.getStringFromUserInput(view.loginNicknameQuestion);
            if (nickname.equals("X")) {
                wantToQuit = true;
                break;
            }
            password = view.getStringFromUserInput(view.loginPasswordQuestion);

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

            case Role.ADMIN:
                new AdminController().start();
                break;

            case Role.MENTOR:
                new MentorController().start();
                break;

            case Role.CODECOOLER:
                new CodecoolerController((CodecoolerModel)user).start();
                break;

            default:
                throw new UnsupportedOperationException("unknown role encountered on login");
        }

    }

    public User login(String nickname, String password) {

        UserDao userDao = new UserDaoImpl();

        User user;
        try {

            user = userDao.getUser(nickname);
        } catch (SQLException sqle) {

            System.err.println( sqle.getClass().getName() + ": " + sqle.getMessage() );
            return null;
        }

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

}
