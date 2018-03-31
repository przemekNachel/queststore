package main.java.com.nwo.queststore.controller;

import main.java.com.nwo.queststore.model.MenuModel;
import main.java.com.nwo.queststore.view.LoginView;
import main.java.com.nwo.queststore.model.CodecoolerModel;
import main.java.com.nwo.queststore.view.AbstractConsoleView;
import main.java.com.nwo.queststore.service.UserService;
import user.user.Role;
import main.java.com.nwo.queststore.model.UserModel;

import java.lang.UnsupportedOperationException;

public class LoginController {

    private LoginView view;

    public LoginController() {

        MenuModel loginMenuModel = null;
        this.view = new LoginView(loginMenuModel);

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

            UserModel userModel = login(nickname, password);
            if (userModel == null) {

                view.printLine(view.loginInvalidCredentialsOrNoUser);
            } else {

                engageAppropriateController(userModel);
            }

        } while(!wantToQuit);
        view.printLine(view.goodbyeMessage);
        AbstractConsoleView.closeScanner();
    }

    public void engageAppropriateController(UserModel userModel) {

        Role userRole = userModel.getRole();
        switch(userRole) {

            case ADMIN:
                new AdminController().start();
                break;

            case MENTOR:
                new MentorController().start();
                break;

            case CODECOOLER:
                new CodecoolerController((CodecoolerModel) userModel).start();
                break;

            default:
                throw new UnsupportedOperationException("unknown role encountered on login");
        }

    }

    public UserModel login(String nickname, String password) {

        UserService userSvc = new UserService();

        UserModel userModel = userSvc.getUser(nickname);

        if (userModel != null && userModel.getPassword().equals(password)) {
            return userModel;
        } else {
            return null;
        }
    }

}
