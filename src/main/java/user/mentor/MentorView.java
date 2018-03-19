package user.mentor;

import console.menu.AbstractConsoleView;
import console.menu.Menu;

public class MentorView extends AbstractConsoleView {

    public String userNicknameQuestion = "Provide user Nickname : ";
    public String userEmailQuestion = "Provide user Email: ";
    public String userPasswordQuestion = "Provide user Password: ";
    public String userGroupQuestion = "Provide user group: ";
    public String userAlreadyInDatabase = "user already in database.";
    public String codecoolerAlreadyInGroupOrGroupAbsent = "The codecooler *had* been added to the given group or the group does not exist.";
    public String artifactNotFoundError = "No such artifact found!";
    public String artifactNameQuestion = "Provide artifact name: ";
    public String artifactDescQuestion = "Provide artifact description: ";
    public String artifactPriceQuestion = "Provide artifact price: ";
    public String GroupAssignmentQuestion = "Choose from availtble groups: ";
    public String questNameQuestion = "Provide quest name: ";
    public String chooseQuestNameQuestion = "Choose quest: ";
    public String questDescQuestion = "Provide quest description: ";
    public String questPriceQuestion = "Provide quest price: ";
    public String markQuestNameQuestion = "Provide quest name: ";
    public String totalCoolcoins = "\n\n Total coolcoins owned: ";
    public String avarageBalance = "\n Average coolcoins owned: ";

    public MentorView() {
        // a no-menu constructor - not all uses require a menu
    }

    public MentorView(Menu menu) {

        this.menu = menu;
    }

}