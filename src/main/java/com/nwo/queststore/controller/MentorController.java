package main.java.com.nwo.queststore.controller;

import main.java.com.nwo.queststore.model.MenuModel;
import main.java.com.nwo.queststore.model.*;
import main.java.com.nwo.queststore.service.ArtifactService;
import main.java.com.nwo.queststore.model.MenuOptionModel;
import main.java.com.nwo.queststore.service.QuestService;
import main.java.com.nwo.queststore.service.WalletService;
import main.java.com.nwo.queststore.view.MentorView;
import main.java.com.nwo.queststore.service.UserService;

public class MentorController extends AbstractUserController {

    private MentorView view;
    private ArtifactService artifactSvc;
    private QuestService questSvc;

    public MentorController() {
        super(new MentorView(
                new MenuModel(
                    new MenuOptionModel("0", "Exit"),
                    new MenuOptionModel("1", "Create a codecooler"),
                    new MenuOptionModel("2", "Assign a codecooler to a group"),
                    new MenuOptionModel("3", "Mark codecooler's quest completion"),
                    new MenuOptionModel("4", "Mark codecooler's artifact usage"),
                    new MenuOptionModel("5", "Create artifact"),
                    new MenuOptionModel("6", "Create quest"),
                    new MenuOptionModel("7", "Display all artifacts"),
                    new MenuOptionModel("8", "Update artifact"),
                    new MenuOptionModel("9", "Remove artifact"),
                    new MenuOptionModel("10", "Display all quests"),
                    new MenuOptionModel("11", "Update quest"),
                    new MenuOptionModel("12", "Delete quest"),
                    new MenuOptionModel("13", "View codecoolers wallets")
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
            MenuOptionModel userOption = view.getMenuOptionFromUserInput(" Please choose option: ");
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
        for(GroupModel<ArtifactModel> artifactGroups : artifactSvc.getAllArtifacts()) {

            for (ArtifactModel artifact : artifactGroups) {

                view.printLine(artifact.getName());
            }
        }
    }

    private void displayAllQuests() {

        view.printLine("\n--- Available Quests ---");
        for(GroupModel<QuestModel> questGroups : questSvc.getAllQuests()) {

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

        GroupModel<String> allowedGroupModelNames = userSvc.getUserGroupNames();
        String groupName = getNameFromUserInput(view.userGroupQuestion, view.nameOutOfRange, allowedGroupModelNames);

        boolean addUserAdherenceSuccess = userSvc.addUserAdherence(codecooler, groupName);

        if(!addUserAdherenceSuccess) {

            view.printLine(view.codecoolerAlreadyInGroupOrGroupAbsent);
        }
    }

    // Right now forces user to update both description and price. Can be improved to choose one or another with switch case.
    private void updateArtifact() {

        ArtifactModel artifact = getArtifactFromUserInput(view.artifactNameQuestion);

        String newDesc = view.getStringFromUserInput(view.artifactDescQuestion);
        Integer newPrice = view.getIntFromUserInput(view.artifactPriceQuestion);

        artifact.setDescription(newDesc);
        artifact.setPrice(newPrice);

        artifactSvc.updateArtifact(artifact);
    }

    private void createArtifact() {

        GroupModel<String> disallowedArtifactNames = artifactSvc.getArtifactNames();
        String name = getExclusiveNameFromUserInput(view.artifactNameQuestion, view.nameAlreadyTaken, disallowedArtifactNames);

        String desc = view.getStringFromUserInput(view.artifactDescQuestion);
        Integer price = view.getIntFromUserInput(view.artifactPriceQuestion);


        GroupModel<String> availableGroups = artifactSvc.getArtifactGroupNames();
        view.printLine("\n--- Available groups ---");
        for (String s : availableGroups) {
            view.printLine(s);
        }
        view.print("\n");

        GroupModel<String> allowedGroupModelNames = availableGroups;
        String groupName = getNameFromUserInput(view.GroupAssignmentQuestion, view.nameOutOfRange, allowedGroupModelNames);

        artifactSvc.createArtifact(name, desc, price, groupName);
    }

    private void removeArtifact() {

        ArtifactModel artifact = getArtifactFromUserInput(view.artifactNameQuestion);

        artifactSvc.deleteArtifact(artifact.getName());
    }

    private ArtifactModel getArtifactFromUserInput(String prompt) {

        displayAllArtifacts();

        GroupModel<String> allowedArtifactNames = artifactSvc.getArtifactNames();
        String name = getNameFromUserInput(prompt, view.nameOutOfRange, allowedArtifactNames);

        return artifactSvc.getArtifactByName(name);
    }

    private QuestModel getQuestFromUserInput(String prompt) {

        displayAllQuests();

        GroupModel<String> allowedQuestNames = questSvc.getQuestNames();
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

        GroupModel<String> disallowedQuestNames = questSvc.getQuestNames();
        String name = getExclusiveNameFromUserInput(view.chooseQuestNameQuestion, view.nameAlreadyTaken, disallowedQuestNames);

        String desc = view.getStringFromUserInput(view.questDescQuestion);
        Integer reward = view.getIntFromUserInput(view.questPriceQuestion);

        GroupModel<String> availableGroups = questSvc.getQuestGroupNames();
        view.printLine("\n--- Available groups ---");
        for (String s : availableGroups) {
            view.printLine(s);
        }
        view.print("\n");

        GroupModel<String> allowedGroupModelNames = availableGroups;
        String groupName = getNameFromUserInput(view.GroupAssignmentQuestion, view.nameOutOfRange, allowedGroupModelNames);

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

    private void markQuest(CodecoolerModel codecooler, QuestModel quest) {

        view.printLine("You are about to mark the quest \"" + quest.getName() + "\" for " + codecooler.getName() + ", completion of which is worth " + quest.getReward().toString());
        String input = "";
        while (!input.equals("Y") && !input.equals("N")) {
            input = view.getStringFromUserInput("\n\n  Do you want to honor this achievement? [Y/N] ");
        }

        if (input.equals("Y")) {

            Integer worth = quest.getReward();
            codecooler.getWallet().payIn(worth);
            codecooler.getLevelModel().addExperience(worth);
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
        GroupModel<String> allowedArtifactNames = new GroupModel<>("allowed artifact name user input");
        GroupModel<ArtifactModel> userArtifacts = codecooler.getCodecoolerArtifacts();
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

            GroupModel<GroupModel<UserModel>> userGroups = userSvc.getAllUsers();
            String codecoolerGroupName = "codecoolers";

            for(GroupModel<UserModel> groupModel : userGroups ) {

                while (groupModel.getName().equals(codecoolerGroupName)) {

                    for (UserModel userModel : groupModel){

                        WalletService wallet;
                        CodecoolerModel codecooler = (CodecoolerModel) userModel;

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
