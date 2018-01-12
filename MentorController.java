public class MentorController {
    public MentorView view;

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
          // TODO
          break;
        // mark artifact usage
        case "4":
          // TODO
          break;
      }
    }


    public void createCodecooler() {
      UserDaoImpl userDao = new UserDaoImpl();


      String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
      String email = view.getStringFromUserInput(view.userEmailQuestion);
      String password = view.getStringFromUserInput(view.userPasswordQuestion);

      // TODO Default level 0 -- next sprint
      // TODO Level object -- next sprint

      Group<User> studentsGroup = userDao.getUserGroup("students");
      if (studentsGroup == null) {

        Group<User> newStudentsGroup = new Group<>("students");
        userDao.addUserCategory(newStudentsGroup);
      }

      WalletService wallet = new WalletService();
      CodecoolerModel codecooler = new CodecoolerModel(nickname, email, password, wallet, studentsGroup); // TODO add level to the object -- next sprint

      // If user getter doesn't find given user, return null
      if (userDao.getUser(nickname) == null) {
        userDao.addUser(codecooler);
      }
      else {
        view.printLine("User already in database.");
      }
    }

    public void assignCodecoolerToGroup() {
      UserDaoImpl userDao = new UserDaoImpl();

      String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
      String groupName = view.getStringFromUserInput(view.userGroupQuestion);

      User user = userDao.getUser(nickname);
      if(!userDao.addUserAdherence(user, groupName)) {

        view.printLine(view.codecoolerAlreadyInGroupOrGroupAbsent);
      } else {

        Group<Group<User>> associatedGroups = user.getAssociatedGroups();
        associatedGroups.add(userDao.getUserGroup(groupName));
        user.setAssociatedGroups(associatedGroups);
      }
    }

    // public void markCodecoolerQuestCompletion() {
    //   UserDaoImpl userDao = new UserDaoImpl();
    //   QuestDaoImpl questDao = new QuestDaoImpl();
    //
    //   String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
    //   String questname = view.getStringFromUserInput(view.markQuestNameQuestion);
    //
    //   User user = userDao.getUser(nickname);
    //   Quest questname = questDao.getQuest(questname);
    //
    //
    //
    // }
    //
    // public void markCodecoolerArtifactUsage() {
    //   // mark that codecooler's artifact has been use and disable it's usage.
    // }

}
