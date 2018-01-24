import java.sql.*;

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

        if(!userDao.addUserAdherence(user, groupName)) {

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
    //    // mark that codecooler's artifact has been use and disable it's usage.
    // }

}
