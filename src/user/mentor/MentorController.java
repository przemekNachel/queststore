package user.mentor;

import artifact.ArtifactModel;
import artifact.ArtifactDao;
import artifact.ArtifactDaoImpl;
import quest.QuestModel;
import quest.QuestDao;
import quest.QuestDaoImpl;
import console.menu.Menu;
import console.menu.MenuOption;
import user.codecooler.CodecoolerModel;
import generic_group.Group;
import user.user.Role;
import user.user.User;
import user.user.UserDao;
import user.user.UserDaoImpl;
import user.wallet.*;

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
                new MenuOption("6", "Create quest")
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
        }
    }

    private Integer getInt(String prompt) {

      Integer result = null;
      boolean validInputProvided;
      do {

        validInputProvided = true;
        try {

          result = Integer.valueOf(view.getStringFromUserInput(prompt));

        } catch (NumberFormatException nfe) {

            validInputProvided = false;
            view.printLine("  Invalid input.");
        }
      } while(!validInputProvided);

      return result;
    }

    public void createArtifact() {

        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();
        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        String desc = view.getStringFromUserInput(view.artifactDescQuestion);
        Integer price = getInt(view.artifactPriceQuestion);
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

    public void createQuest() {

        QuestDaoImpl questDao = new QuestDaoImpl();
        String name = view.getStringFromUserInput(view.questNameQuestion);
        String desc = view.getStringFromUserInput(view.questDescQuestion);
        Integer reward = getInt(view.questPriceQuestion);
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

    public void createCodecooler() {
        UserDaoImpl userDao = new UserDaoImpl();

        String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
        String email = view.getStringFromUserInput(view.userEmailQuestion);
        String password = view.getStringFromUserInput(view.userPasswordQuestion);

        // TODO Default level 0 -- next sprint
        // TODO level object -- next sprint

        Group<User> studentsGroup = null;
        try{
            studentsGroup = userDao.getUserGroup("codecoolers");
        } catch (SQLException e) {

            view.printSQLException(e);
        }

        if (studentsGroup == null) {

            Group<User> newStudentsGroup = new Group<>("codecoolers");
            try{
                userDao.addUserGroup(newStudentsGroup);
            } catch (SQLException e) {
                view.printSQLException(e);
            }
            studentsGroup = newStudentsGroup;
        }

        WalletService wallet = new WalletService(0);
        CodecoolerModel codecooler = new CodecoolerModel(nickname, email, password, wallet, studentsGroup); // TODO add level to the object -- next sprint

        User user = null;
        try {

            user = userDao.getUser(nickname);
            // If user getter doesn't find given user, return null
            if (user == null) {
                userDao.addUser(codecooler);
            }
            else {
                view.printLine(view.userAlreadyInDatabase);
            }
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    public void assignCodecoolerToGroup() {
        UserDaoImpl userDao = new UserDaoImpl();

        String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
        String groupName = view.getStringFromUserInput(view.userGroupQuestion);

        User user = null;
        try {

            user = userDao.getUser(nickname);
        } catch (SQLException e) {

            view.printSQLException(e);
        }

        boolean addUserAdherenceSuccess = false;
        try{
            addUserAdherenceSuccess = userDao.addUserAdherence(user, groupName);
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        if(!addUserAdherenceSuccess) {

            view.printLine(view.codecoolerAlreadyInGroupOrGroupAbsent);
        } else {

            Group<Group<User>> associatedGroups = user.getAssociatedGroups();
            try{
                associatedGroups.add(userDao.getUserGroup(groupName));
            } catch (SQLException e) {
                view.printSQLException(e);
            }
            user.setAssociatedGroups(associatedGroups);
        }
    }

    private CodecoolerModel getCodecooler() {

        UserDaoImpl userDao = new UserDaoImpl();
        boolean validNameProvided = false;
        User user = null;
        do {

            String name = view.getStringFromUserInput(view.userNicknameQuestion);
            try {

              user = userDao.getUser(name);
            } catch (SQLException sqle) {
                view.printLine(sqle.getMessage());
                return null;
            }

            if (user != null && user.getRole() == Role.CODECOOLER) {
                validNameProvided = true;
            } else {
                view.printLine(view.invalidNickname);
            }
        } while (!validNameProvided);

        return (CodecoolerModel)user;
    }

    public void markCodecoolerQuestCompletion() {

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

    public void markCodecoolerArtifactUsage() {

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

}
