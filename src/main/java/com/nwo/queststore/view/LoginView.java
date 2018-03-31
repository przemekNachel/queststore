package main.java.com.nwo.queststore.view;

import main.java.com.nwo.queststore.view.AbstractConsoleView;
import main.java.com.nwo.queststore.model.MenuModel;


public class LoginView extends AbstractConsoleView {

    public final String welcomeMessage = "";
    public final String goodbyeMessage = "";
    public final String loginMessage = "  Welcome to QuestStore system!\n  Provide your credentials to log in or type X in nickname prompt to exit the program.";
    public final String loginNicknameQuestion = "  Please provide user nickname: ";
    public final String loginPasswordQuestion = "  Please provide user password: ";
    public final String loginInvalidCredentialsOrNoUser = "  The provided credentials are invalid \n  or the specified nickname does not exist in the system.";

    public LoginView(MenuModel menuModel) {
        this.menuModel = menuModel;
    }

    public String getPassword(){
        return getStringFromUserInput(loginPasswordQuestion);
    }
}
