import java.sql.*;

public class MentorController {
    private MentorView view;

    public MentorController() {
        Menu mentorMenu = new Menu(
                new MenuOption("0", "Exit"),
                new MenuOption("1", "Create a Codecooler"),
                new MenuOption("2", "Assign a Codecooler to a group"),
                new MenuOption("3", "Mark Codecooler's quest completion"),
                new MenuOption("4", "Mark Codecooler's artifact usage")
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
            // codecooler creation
            case "1":
                createCodecooler();
                break;
            // assigning codecooler to a group
            case "2":
                assignCodecoolerToGroup();
                break;
            // mark quest completion
            case "3":
                // TODO markCodecoolerArtifactUsage();
                break;
            // mark artifact usage
            case "4":
                // TODO
                break;
        }
    }


    public void createCodecooler() {
        UserDaoImpl userDao = new UserDaoImpl();
        MentorView view = new MentorView();

        String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
        String email = view.getStringFromUserInput(view.userEmailQuestion);
        String password = view.getStringFromUserInput(view.userPasswordQuestion);

        // TODO Default level 0 -- next sprint
        // TODO Level object -- next sprint

        Group<User> studentsGroup = null;
        try{
            studentsGroup = userDao.getUserGroup("students");
        } catch (SQLException e) {

            view.printSQLException(e);
        }

        if (studentsGroup == null) {

            Group<User> newStudentsGroup = new Group<>("students");
            userDao.addUserGroup(newStudentsGroup);
            studentsGroup = newStudentsGroup;
        }

        WalletService wallet = new WalletService(0);
        CodecoolerModel codecooler = new CodecoolerModel(nickname, email, password, wallet, studentsGroup); // TODO add level to the object -- next sprint

        User user;
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
        User user;
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
      QuestDaoImpl questDao = new QuestDaoImpl();

      // get quest group names ...
      Group<String> allowedQuestNames = new Group<>("allowed quest name user input");
      Group<String> questGroupNames;
      try {

        questGroupNames = questDao.getQuestGroupNames();
      } catch (SQLException sqle) {

        view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
        return;
      }

      // ... to retrieve particular quest names
      String groupsFormatted = "";
      for (String groupName : questGroupNames) {

          Group<QuestModel> questGroup;
          try {

              questGroup = questDao.getQuestGroup(groupName);
          } catch (SQLException sqle) {

              view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
              return;
          }

          groupsFormatted += groupName + " :\n";
          for (QuestModel currentQuest : questGroup) {

              groupsFormatted += "*" + currentQuest + "\n";
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
      QuestModel quest;
      try {

        quest = questDao.getQuest(questName);
      } catch (SQLException sqle) {
          view.printLine(sqle.getMessage());
          return;
      }
      // get a Codecooler to be marked
      CodecoolerModel codecooler = getCodecooler();

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
            view.printLine("  Quest marked");
            UserDaoImpl userDao = new UserDaoImpl();
            try {

              userDao.updateUser(codecooler);
            } catch (SQLException sqle) {
                view.printLine(sqle.getMessage());
                return;
            }
        } else {
            view.printLine("  Someone has changed their mind...");
        }
    }

    public void markCodecoolerArtifactUsage() {

    }

}
