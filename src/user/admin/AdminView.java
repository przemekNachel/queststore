package user.admin;

import console.menu.AbstractConsoleView;
import console.menu.Menu;

class AdminView extends AbstractConsoleView {

    public AdminView(Menu menu) {

        this.menu = menu;
    }

    public String mentorNameQuestion = "Provide name:";
    public String mentorPasswordQuestion = "Type in password: ";
    public String mentorEmailQuestion = "Type in email: ";
    public String groupNameQuestion = "Provide group name:";
    public String mentorChangeQuestion = "What do you want to edit? \n 1.user.mentor's name \n 2. user.mentor's email \n 3. user.mentor's password";
    public String levelNameQuestion = "Type in lvls name: ";
    public String levelTresholdQuestion = "Provide lvl's treshold";
    public String noSuchOption = "No such option!";
    public String assignMentorToFroupError = "Given group does not exist or mentor is already in group!";
    public String noMentorOfSuchName = "There is no mentor of given name.";
    public String currentLevelsText = "Note: Creating a lvl with the same treshold as currently existing one will rename it \n" +
            "Currently existing levels:\n\nThres   Name" ;
}
