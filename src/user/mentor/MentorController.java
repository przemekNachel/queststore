package user.mentor;

import abstractusercontroller.AbstractUserController;
import artifact.ArtifactModel;
import artifact.ArtifactService;
import quest.QuestModel;
import console.menu.Menu;
import console.menu.MenuOption;
import quest.QuestService;
import user.codecooler.CodecoolerModel;
import generic_group.Group;
import user.user.*;
import user.wallet.*;
import user.service.UserService;

import java.sql.*;

public class MentorController extends AbstractUserController {

    private MentorView view;
    private ArtifactService artifactSvc;
    private QuestService questSvc;

    public MentorController() {
        super(new MentorView(
                new Menu(
                    new MenuOption("0", "Exit"),
                    new MenuOption("1", "Create a codecooler"),
                    new MenuOption("2", "Assign a codecooler to a group"),
                    new MenuOption("3", "Mark codecooler's quest completion"),
                    new MenuOption("4", "Mark codecooler's artifact usage"),
                    new MenuOption("5", "Create artifact"),
                    new MenuOption("6", "Create quest"),
                    new MenuOption("7", "Display all artifacts"),
                    new MenuOption("8", "Update artifact"),
                    new MenuOption("9", "Remove artifact"),
                    new MenuOption("10", "Display all quests"),
                    new MenuOption("11", "Update quest"),
                    new MenuOption("12", "Delete quest"),
                    new MenuOption("13", "View codecoolers wallets")
                )
            )
        );

        this.view = (MentorView)super.view;

        userSvc = new UserService();
        artifactSvc = new ArtifactService();
        questSvc = new QuestService();
    }

    public void start() {

        boolean requestedExit = false;
        do {
            MenuOption userOption = view.getMenuOptionFromUserInput(" Please choose option: ");
            if (userOption.getId().equals("0")) {
                requestedExit = true;
                view.clearScreen();
            } else {

                String chosenOption = userOption.getId();
                handleUserChoice(chosenOption);
            }
        } while (!requestedExit);
    }

    private void handleUserChoice(String userChoice) {

        switch (userChoice) {
            case "1":
                createCodecooler();
                break;
            case "2":
                assignCodecoolerToGroup();
                break;
            case "3":
                markCodecoolerQuestCompletion();
                break;
            case "4":
                markCodecoolerArtifactUsage();
                break;
            case "5":
                createArtifact();
                break;
            case "6":
                createQuest();
                break;
            case "7":
                displayAllArtifacts();
                break;
            case "8":
                updateArtifact();
                break;
            case "9":
                removeArtifact();
                break;
            case "10":
                displayAllQuests();
                break;
            case "11":
                updateQuest();
                break;
            case "12":
                removeQuest();
                break;
            case "13":
                showCodecoolerWalletsBalance();
                break;
        }
    }

    private void displayAllArtifacts() {

        view.printLine("\n--- Available artifacts ---");
        for(Group<ArtifactModel> artifactGroups : artifactSvc.getAllArtifacts()) {

            for (ArtifactModel artifact : artifactGroups) {

                view.printLine(artifact.getName());
            }
        }
    }

    private void displayAllQuests() {

        view.printLine("\n--- Available Quests ---");
        for(Group<QuestModel> questGroups : questSvc.getAllQuests()) {

            for (QuestModel quest : questGroups) {

                view.printLine(quest.getName());
            }
        }
    }

    private CodecoolerModel getCodecoolerFromUserInput() {

        return (CodecoolerModel)getUserFromUserInput(view.userNicknameQuestion, view.nameOutOfRange, "codecoolers");
    }

    private void assignCodecoolerToGroup() {

        CodecoolerModel codecooler = getCodecoolerFromUserInput();

        Group<String> allowedGroupNames = userSvc.getUserGroupNames();
        String groupName = getNameFromUserInput(view.userGroupQuestion, view.nameOutOfRange, allowedGroupNames);

        boolean addUserAdherenceSuccess = userSvc.addUserAdherence(codecooler, groupName);

        if(!addUserAdherenceSuccess) {

            view.printLine(view.codecoolerAlreadyInGroupOrGroupAbsent);
        }
    }

    // Right now forces user to update both description and price. Can be improved to choose one or another with switch case.
    private void updateArtifact() {

        displayAllArtifacts();

        Group<String> allowedArtifactNames = artifactSvc.getArtifactNames();
        String name = getNameFromUserInput(view.chooseArtifactNameQuestion, view.nameOutOfRange, allowedArtifactNames);

        ArtifactModel artifact = artifactSvc.getArtifactByName(name);

        String newDesc = view.getStringFromUserInput(view.artifactDescQuestion);
        Integer newPrice = view.getIntFromUserInput(view.artifactPriceQuestion);

        artifact.setDescription(newDesc);
        artifact.setPrice(newPrice);

        artifactSvc.updateArtifact(artifact);
    }

    private void createArtifact() {

        Group<String> disallowedArtifactNames = artifactSvc.getArtifactNames();
        String name = getExclusiveNameFromUserInput(view.chooseArtifactNameQuestion, view.nameAlreadyTaken, disallowedArtifactNames);

        String desc = view.getStringFromUserInput(view.artifactDescQuestion);
        Integer price = view.getIntFromUserInput(view.artifactPriceQuestion);


        Group<String> availableGroups = artifactSvc.getArtifactGroupNames();
        view.printLine("\n--- Available groups ---");
        for (String s : availableGroups) {
            view.printLine(s);
        }
        view.print("\n");

        Group<String> allowedGroupNames = availableGroups;
        String groupName = getNameFromUserInput(view.GroupAssignmentQuestion, view.nameOutOfRange, allowedGroupNames);

        artifactSvc.createArtifact(name, desc, price, groupName);
    }

    private void removeArtifact() {

        Group<String> allowedArtifactNames = artifactSvc.getArtifactNames();

        displayAllArtifacts();

        String name = getNameFromUserInput(view.artifactNameQuestion, view.nameOutOfRange, allowedArtifactNames);

        artifactSvc.deleteArtifact(name);
    }

    private QuestModel getQuestFromUserInput(String prompt) {

        displayAllQuests();

        Group<String> allowedQuestNames = questSvc.getQuestNames();
        String name = getNameFromUserInput(prompt, view.nameOutOfRange, allowedQuestNames);

        return questSvc.getQuestByName(name);
    }

    private void removeQuest() {

        QuestModel questDeleted = getQuestFromUserInput(view.questNameQuestion);

        questSvc.deleteQuest(questDeleted.getName());
    }

    private void updateQuest() {

        QuestModel quest = getQuestFromUserInput(view.chooseQuestNameQuestion);

        String newDesc = view.getStringFromUserInput(view.questDescQuestion);
        Integer newReward = view.getIntFromUserInput(view.questPriceQuestion);

        quest.setDescription(newDesc);
        quest.setReward(newReward);

        questSvc.updateQuest(quest);
    }

    private void createQuest() {

        Group<String> disallowedQuestNames = questSvc.getQuestNames();
        String name = getExclusiveNameFromUserInput(view.chooseQuestNameQuestion, view.nameAlreadyTaken, disallowedQuestNames);

        String desc = view.getStringFromUserInput(view.questDescQuestion);
        Integer reward = view.getIntFromUserInput(view.questPriceQuestion);

        Group<String> availableGroups = questSvc.getQuestGroupNames();
        view.printLine("\n--- Available groups ---");
        for (String s : availableGroups) {
            view.printLine(s);
        }
        view.print("\n");

        Group<String> allowedGroupNames = availableGroups;
        String groupName = getNameFromUserInput(view.GroupAssignmentQuestion, view.nameOutOfRange, allowedGroupNames);

        questSvc.createQuest(name, desc, reward, groupName);
    }

    private boolean userNameIsTaken(String name) {

        return userSvc.getUser(name) != null;
    }

    private void createCodecooler() {

        boolean validName = false;
        String nickname;
        do {

            nickname = view.getStringFromUserInput(view.userNicknameQuestion);

            if (userNameIsTaken(nickname)) {

                view.printLine(view.nameAlreadyTaken);
            } else {

                validName = true;
            }

        } while(!validName);

        String email = view.getStringFromUserInput(view.userEmailQuestion);
        String password = view.getStringFromUserInput(view.userPasswordQuestion);

        if (!userSvc.createCodecooler(nickname, email, password)) {

            view.printLine(view.userAlreadyInDatabase);
        }
    }

    private void markCodecoolerQuestCompletion() {

        QuestModel quest = getQuestFromUserInput(view.markQuestNameQuestion);

        CodecoolerModel codecooler = getCodecoolerFromUserInput();

        markQuest(codecooler, quest);
    }

    private void markQuest(CodecoolerModel codecooler, quest.QuestModel quest) {

        view.printLine("You are about to mark the quest \"" + quest.getName() + "\" for " + codecooler.getName() + ", completion of which is worth " + quest.getReward().toString());
        String input = "";
        while (!input.equals("Y") && !input.equals("N")) {
            input = view.getStringFromUserInput("\n\n  Do you want to honor this achievement? [Y/N] ");
        }

        if (input.equals("Y")) {

            Integer worth = quest.getReward();
            codecooler.getWallet().payIn(worth);
            codecooler.getLevel().addExperience(worth);
            view.printLine("  quest marked");

            userSvc.updateUser(codecooler);
        } else {
            view.printLine("  Someone has changed their mind...");
        }
    }

    private void markCodecoolerArtifactUsage() {

        // get user.codecooler artifact usage of whom is to be marked
        CodecoolerModel codecooler = getCodecoolerFromUserInput();

        // get artifact to be marked
        Group<String> allowedArtifactNames = new Group<>("allowed artifact name user input");
        Group<ArtifactModel> userArtifacts = codecooler.getCodecoolerArtifacts();
        String artifactsFormatted = "  Artifacts of codecooler " + codecooler.getName() + "\n\n:";
        for (ArtifactModel currentArtifact : userArtifacts) {

            artifactsFormatted += "    " + currentArtifact + "\n";
            allowedArtifactNames.add(currentArtifact.getName());
        }
        view.printLine(artifactsFormatted);

        // get the name of the artifact to be marked

        String artifactName = getNameFromUserInput(view.artifactNameQuestion, view.artifactNotFoundError, allowedArtifactNames);

        String input = "";
        while (!input.equals("Y") && !input.equals("N")) {

            input = view.getStringFromUserInput("  Provide Y to mark as used, N as unused: ");
        }

        boolean usageStatus = input.equals("Y");
        codecooler.getArtifact(artifactName).setUsageStatus(usageStatus);

        userSvc.updateUser(codecooler);

    }

    public void showCodecoolerWalletsBalance(){

        int totalBalance = 0;
        int numberOfCodecoolers = 0;
        int averageCodecoolerBalance;

        try {

            Group<Group<User>> userGroups = userSvc.getAllUsers();
            String codecoolerGroupName = "codecoolers";

            for(Group<User> group: userGroups ) {

                while (group.getName().equals(codecoolerGroupName)) {

                    for (User user: group){

                        WalletService wallet;
                        CodecoolerModel codecooler = (CodecoolerModel) user;

                        wallet = codecooler.getWallet();
                        view.print(codecooler.getName() + " " + wallet.toString() + "\n");

                        totalBalance += wallet.getBalance();
                        numberOfCodecoolers += 1;
                    }
                    break;
                }
            }
            averageCodecoolerBalance = totalBalance / numberOfCodecoolers;
            view.print(view.totalCoolcoins + String.valueOf(totalBalance));
            view.print(view.avarageBalance + String.valueOf(averageCodecoolerBalance));

        } catch(ArithmeticException e) {

            view.printLine("No codecoolers found");
        }
    }
}
