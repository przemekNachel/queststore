package user.admin;

import console.menu.AbstractConsoleView;
import console.menu.Menu;

class AdminView extends AbstractConsoleView {

    public String mentorNameQuestion = "Provide mentor nickname:";
    public String mentorPasswordQuestion = "Type in password: ";
    public String mentorEmailQuestion = "Type in email: ";
    public String groupNameQuestion = "Provide group name:";
    public String mentorChangeQuestion = "What do you want to edit? \n 1. user.mentor's email \n 2. user.mentor's password";
    public String levelNameQuestion = "Type in lvls name: ";
    public String levelThresholdQuestion = "Provide lvl's treshold";
    public String noSuchOption = "No such option!";
    public String assignMentorToGroupError = "Given group does not exist or mentor is already in group!";
    public String noMentorOfSuchName = "There is no mentor of given nickname.";
    public String currentLevelsText = "Note: Creating a lvl with the same treshold as currently existing one will rename it \n" +
            "Currently existing levels:\n\nThres   Name";

    public AdminView(Menu menu) {

        this.menu = menu;
    }
}
