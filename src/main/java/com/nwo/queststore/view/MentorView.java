package main.java.com.nwo.queststore.view;

import main.java.com.nwo.queststore.model.MenuModel;

public class MentorView extends AbstractConsoleView {

    public MentorView() {
        // a no-menuModel constructor - not all uses require a menuModel
    }

    public MentorView(MenuModel menuModel) {

        this.menuModel = menuModel;
    }

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

}
