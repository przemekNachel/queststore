package login;

import console.menu.AbstractConsoleView;
import console.menu.Menu;
import java.io.Console;


public class LoginView extends AbstractConsoleView {

    public final String welcomeMessage = "";
    public final String goodbyeMessage = "";
    public final String loginMessage = "  Welcome to QuestStore system!\n  Provide your credentials to log in or type X in nickname prompt to exit the program.";
    public final String loginNicknameQuestion = "  Please provide user nickname: ";
    public final String loginPasswordQuestion = "  Please provide user password: ";
    public final String loginInvalidCredentialsOrNoUser = "  The provided credentials are invalid \n  or the specified nickname does not exist in the system.";

    LoginView(Menu menu) {
        this.menu = menu;
    }

    public String getPassword(){
        return getStringFromUserInput(loginPasswordQuestion);
    }
}
