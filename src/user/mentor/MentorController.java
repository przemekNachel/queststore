package user.mentor;

import artifact.ArtifactModel;
import artifact.ArtifactDao;
import artifact.ArtifactDaoImpl;
import level.Level;
import quest.QuestModel;
import quest.QuestDao;
import quest.QuestDaoImpl;
import console.menu.Menu;
import console.menu.MenuOption;
import user.codecooler.CodecoolerModel;
import generic_group.Group;
import user.user.*;
import user.wallet.*;
import user.service.UserService;

import java.sql.*;
import java.util.Iterator;

public class MentorController {
    private MentorView view;

    public MentorController() {
        Menu mentorMenu = new Menu(
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
        );

        view = new MentorView(mentorMenu);
    }

    public void start() {

        boolean requestedExit = false;
        do {
            UserDao userDao = new UserDaoImpl();
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
        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();

        try {
            Group<Group<ArtifactModel>> artifactCollection = artifactDao.getAllArtifacts();
            view.printLine("\n--- Available artifacts ---");
            for(Group<ArtifactModel> artifactGroups : artifactCollection) {
                for (ArtifactModel artifact : artifactGroups) {
                    view.printLine(artifact.getName());
                }
            }
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    private void displayAllQuests() {
        QuestDaoImpl questDao = new QuestDaoImpl();

        try {
            Group<Group<QuestModel>> questCollection = questDao.getAllQuests();
            view.printLine("\n--- Available Quests ---");
            for(Group<QuestModel> questGroups : questCollection) {
                for (QuestModel quest : questGroups) {
                    view.printLine(quest.getName());
                }
            }
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    private void assignCodecoolerToGroup() {
        UserService userSvc = new UserService();

        String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
        String groupName = view.getStringFromUserInput(view.userGroupQuestion);

        User user = userSvc.getUser(nickname);

        boolean addUserAdherenceSuccess = userSvc.addUserAdherence(user, groupName);

        if(!addUserAdherenceSuccess) {

            view.printLine(view.codecoolerAlreadyInGroupOrGroupAbsent);
        }
    }

    // Right now forces user to update both description and price. Can be improved to choose one or another with switch case.
    private void updateArtifact() {
        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();
        displayAllArtifacts();
        try {
            ArtifactModel artifact = artifactDao.getArtifactByName(view.getStringFromUserInput(view.chooseArtifactNameQuestion));

            String artifactDesc = view.getStringFromUserInput(view.artifactDescQuestion);
            Integer artifactPrice = view.getIntFromUserInput(view.artifactPriceQuestion);

            ArtifactModel updatedArtifact = new ArtifactModel(artifact.getName(), artifactDesc, artifactPrice);
            artifactDao.updateArtifact(updatedArtifact);

        } catch (SQLException e){
            view.printSQLException(e);
        }
    }

    private void createArtifact() {

        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();
        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        String desc = view.getStringFromUserInput(view.artifactDescQuestion);
        Integer price = view.getIntFromUserInput(view.artifactPriceQuestion);
        ArtifactModel newArtifact = new ArtifactModel(name, desc, price);

        try {
            Group<String> availableGroups = artifactDao.getArtifactGroupNames();
            view.printLine("\n--- Available groups ---");
            for (String s : availableGroups) {
                if (!s.equals("artifacts")) {
                    view.printLine(s);
                }
            }
            view.print("\n");

            String groupName = view.getStringFromUserInput(view.GroupAssignmentQuestion);

            artifactDao.addArtifact(newArtifact);
            artifactDao.addArtifactAdherence(name, "artifacts");
            artifactDao.addArtifactAdherence(name, groupName);
        } catch (SQLException e) {

            view.printSQLException(e);
        }
    }

    private void removeArtifact() {
        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();
        displayAllArtifacts();
        try {
            ArtifactModel artifact = artifactDao.getArtifactByName(view.getStringFromUserInput(view.artifactNameQuestion));
            artifactDao.deleteArtifact(artifact);
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    private void removeQuest() {
        QuestDaoImpl questDao = new QuestDaoImpl();
        displayAllQuests();
        try {
            QuestModel quest = questDao.getQuest(view.getStringFromUserInput(view.questNameQuestion));
            questDao.deleteQuest(quest);
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    private void updateQuest() {
        QuestDaoImpl questDao = new QuestDaoImpl();
        displayAllQuests();
        try {
            QuestModel quest = questDao.getQuest(view.getStringFromUserInput(view.chooseQuestNameQuestion));

            String questDesc = view.getStringFromUserInput(view.questDescQuestion);
            Integer questPrice = view.getIntFromUserInput(view.questPriceQuestion);

            QuestModel updatedQuest = new QuestModel(quest.getName(), questDesc, questPrice);
            questDao.updateQuest(updatedQuest);

        } catch (SQLException e){
            view.printSQLException(e);
        }

    }

    private void createQuest() {

        QuestDaoImpl questDao = new QuestDaoImpl();
        String name = view.getStringFromUserInput(view.questNameQuestion);
        String desc = view.getStringFromUserInput(view.questDescQuestion);
        Integer reward = view.getIntFromUserInput(view.questPriceQuestion);
        QuestModel newQuest = new QuestModel(name, desc, reward);

        try {
            Group<String> availableGroups = questDao.getQuestGroupNames();
            view.printLine("\n--- Available groups ---");
            for (String s : availableGroups) {
                if (!s.equals("quests")) {
                    view.printLine(s);
                }
            }
            view.print("\n");

            String groupName = view.getStringFromUserInput(view.GroupAssignmentQuestion);

            questDao.addQuest(newQuest);
            questDao.addQuestAdherence(name, "quests");
            questDao.addQuestAdherence(name, groupName);
        } catch (SQLException e) {

            view.printSQLException(e);
        }
    }

    private void createCodecooler() {
        UserService userSvc = new UserService();

        String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
        String email = view.getStringFromUserInput(view.userEmailQuestion);
        String password = view.getStringFromUserInput(view.userPasswordQuestion);

        // TODO Default level 0 -- next sprint
        // TODO level object -- next sprint


        WalletService wallet = new WalletService(0);
        Level level = new Level(0);

        Group<String> studentGroups = new Group<>("student groups");
        studentGroups.add("codecoolers");

        Group<ArtifactModel> artifacts = new Group<>("user artifacts");
        CodecoolerModel codecooler = new CodecoolerModel(new RawUser(Role.CODECOOLER, nickname, email, password, studentGroups), wallet, artifacts, level);

        User user = userSvc.getUser(nickname);
        // If user getter doesn't find given user, return null
        if (user == null) {
            userSvc.addUser(codecooler);
        } else {
            view.printLine(view.userAlreadyInDatabase);
        }
    }

    private void markCodecoolerQuestCompletion() {

        UserDaoImpl userDao = new UserDaoImpl();
        quest.QuestDaoImpl questDao = new quest.QuestDaoImpl();

        // get quest group names ...
        Group<String> allowedQuestNames = new Group<>("allowed quest name user input");
        Group<String> questGroupNames = null;
        try {

            questGroupNames = questDao.getQuestGroupNames();
        } catch (SQLException sqle) {

            view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
            return;
        }

        // ... to retrieve particular quest names
        String groupsFormatted = "";
        for (String groupName : questGroupNames) {

            Group<quest.QuestModel> questGroup;
            try {

                questGroup = questDao.getQuestGroup(groupName);
            } catch (SQLException sqle) {

                view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
                return;
            }

            groupsFormatted += "\n" + groupName + " :\n";
            for (quest.QuestModel currentQuest : questGroup) {

                groupsFormatted += "*" + currentQuest.getName() + "\n";
                allowedQuestNames.add(currentQuest.getName());
            }
        }

        view.printLine(view.availableQuests);
        view.printLine(groupsFormatted);

        // get the quest to be checked
        String questName = null;
        boolean providedValidQuestName = false;
        do {

            questName = view.getStringFromUserInput(view.markQuestNameQuestion);
            if (allowedQuestNames.contains(questName)) {

                providedValidQuestName = true;
            } else {
                view.printLine(view.questNotFoundError);
            }
        } while(!providedValidQuestName);

        // get quest to be marked
        quest.QuestModel quest;
        try {

            quest = questDao.getQuest(questName);
        } catch (SQLException sqle) {
            view.printLine(sqle.getMessage());
            return;
        }
        // get a user.codecooler to be marked
        CodecoolerModel codecooler = getCodecooler();

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
            codecooler.getExperience().addExperience(worth);
            view.printLine("  quest marked");
            UserDaoImpl userDao = new UserDaoImpl();
            try {

                userDao.updateUser(codecooler);
            } catch (SQLException e) {
                view.printLine(e.getMessage());
            }
        } else {
            view.printLine("  Someone has changed their mind...");
        }
    }

    private void markCodecoolerArtifactUsage() {
        MentorView view = new MentorView();

        // get user.codecooler artifact usage of whom is to be marked
        CodecoolerModel codecooler = getCodecooler();

        // get artifact to be marked
        Group<String> allowedArtifactNames = new Group<>("allowed artifact name user input");
        Group<ArtifactModel> userArtifacts = codecooler.getCodecoolerArtifacts();
        String artifactsFormatted = "Artifacts of codecooler " + codecooler.getName() + "\n\n:";
        for (ArtifactModel currentArtifact : userArtifacts) {

            artifactsFormatted += "*" + currentArtifact + "\n";
            allowedArtifactNames.add(currentArtifact.getName());
        }

        view.printLine(artifactsFormatted);

        // get the name of the artifact to be marked
        String artifactName = null;
        boolean providedValidArtifactName = false;
        do {

            artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
            if (allowedArtifactNames.contains(artifactName)) {

                providedValidArtifactName = true;
            } else {
                view.printLine(view.artifactNotFoundError);
            }

        } while(!providedValidArtifactName);

        String input = "";
        while (!input.equals("Y") && !input.equals("N")) {

            input = view.getStringFromUserInput("  Provide Y to mark as used, N as unused: ");
        }

        boolean usageStatus = input.equals("Y");
        codecooler.getArtifact(artifactName).setUsageStatus(usageStatus);
        UserDaoImpl userDao = new UserDaoImpl();
        try {

            userDao.updateUser(codecooler);
        } catch (SQLException e) {

            view.printSQLException(e);
        }

    }

    private CodecoolerModel getCodecooler() {

        UserService userSvc = new UserService();
        boolean validNameProvided = false;
        User user = null;
        do {

            String name = view.getStringFromUserInput(view.userNicknameQuestion);
            user = userSvc.getUser(name);

            if (user != null && user.getRole() == Role.CODECOOLER) {
                validNameProvided = true;
            } else {
                view.printLine(view.invalidNickname);
            }
        } while (!validNameProvided);

        return (CodecoolerModel)user;
    }
    public void showCodecoolerWalletsBalance(){
        MentorView view = new MentorView();
        UserService userService = new UserService();
        int totalBalance = 0;
        int numberOfCodecoolers = 0;
        int averageCodecoolerBalance;

        try {
            Group<Group<User>> userGroups = userService.getAllUsers();
            String codecoolerGroupName = "codecoolers";

            for(Group<User> group: userGroups ) {
                while (group.getName().equals(codecoolerGroupName)) {
                    for(User user: group){
                        WalletService wallet;
                        RawUser rawUser = (RawUser) user;
                        CodecoolerModel codecooler = (CodecoolerModel) rawUser;
                        wallet = codecooler.getWallet();
                        view.print(codecooler.getName() + " " + wallet.toString() + "\n");
                        totalBalance += wallet.getBalance();
                        numberOfCodecoolers += 1;
                    }
                    break;
                }
            }
            averageCodecoolerBalance  = totalBalance/numberOfCodecoolers;
            view.print(view.totalCoolcoins + String.valueOf(totalBalance));
            view.print(view.avarageBalance + String.valueOf(averageCodecoolerBalance));
        }
        catch(java.lang.ArithmeticException e){
            view.print("No codecoolers found");
        }
    }



}
