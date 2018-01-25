package User.Admin;

import Console.AbstractConsoleView;

class AdminView extends AbstractConsoleView {

    public AdminView(Menu menu) {

        this.menu = menu;
    }

    public String mentorNameQuestion = "Provide name:";
    public String mentorPasswordQuestion = "Type in password: ";
    public String mentorEmailQuestion = "Type in email: ";
    public String groupNameQuestion = "Provide group name:";
    public String mentorChangeQuestion = "What do you want to edit? \n 1.User.Mentor's name \n 2. User.Mentor's email \n 3. User.Mentor's password";
    public String levelNameQuestion = "Type in lvls name: ";
    public String levelTresholdQuestion = "Provide lvl's treshold";
    public String noSuchOption = "No such option!";
    public String assignMentorToFroupError = "Given group does not exist or mentor is already in group!";
    public String noMentorOfSuchName = "There is no mentor of given name.";
}
