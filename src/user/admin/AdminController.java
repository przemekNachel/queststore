package user.admin;

import console.menu.Menu;
import console.menu.MenuOption;
import generic_group.Group;
import user.mentor.MentorModel;
import user.user.Role;
import user.user.User;
import user.user.RawUser;
import user.user.UserDao;
import user.service.UserService;
import user.user.UserDaoImpl;
import level.Level;
import level.LevelService;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AdminController{
    AdminView view;

    public AdminController() {
        Menu adminMenu = new Menu(
                new MenuOption("0", "Exit"),
                new MenuOption("1", "Create a mentor"),
                new MenuOption("2", "Assign a mentor to a group"),
                new MenuOption("3", "Create user group"),
                new MenuOption("4", "Edit mentor"),
                new MenuOption("5", "View mentor's details"),
                new MenuOption("6", "View groups"),
                new MenuOption("7", "Create level"));

        view = new AdminView(adminMenu);
    }

    public void createMentor() {

        UserDaoImpl dao = new UserDaoImpl();
        String name = this.view.getStringFromUserInput(view.mentorNameQuestion);
        String email = this.view.getStringFromUserInput(view.mentorEmailQuestion);
        String password = this.view.getStringFromUserInput(view.mentorPasswordQuestion);

        try {
            Group<String> mentorGroups = new Group<>("mentor groups");
            mentorGroups.add("mentors");
            MentorModel mentor = new MentorModel(new RawUser(Role.MENTOR, name, email, password, mentorGroups));

            dao.addUser(mentor);
//            mentorsGroup.add(mentor);
        } catch (SQLException e) {
            view.printSQLException(e);
        }
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
                handleUserChoice(userOption.getId());
            }
        } while (!requestedExit);
    }

    private void handleUserChoice(String userChoice) {

        switch (userChoice) {
            case "1":
                createMentor();
                break;

            case "2":
                assignMentorToGroup();
                break;

            case "3":
                createGroup();
                break;

            case "4":
                editMentor();
                break;

            case "5":
                view.printLine(getMentorDisplay());
                break;

            case "6":
                view.printLine(getGroupsDisplay());
                break;
            case "7":

                createLevel();
                break;
        }
    }

    public void assignMentorToGroup() {
        UserService userSvc = new UserService();

        String name = view.getStringFromUserInput(view.mentorNameQuestion);
        String groupName = view.getStringFromUserInput(view.groupNameQuestion);
        User user = userSvc.getUser(name);

        boolean userAddedtoGroup = userSvc.addUserAdherence(user, groupName);

        if (!userAddedtoGroup || user == null) {
            view.printLine(view.assignMentorToFroupError);
        }
    }

    public void createGroup() {

        UserService userSvc = new UserService();
        String groupName = view.getStringFromUserInput(view.groupNameQuestion);
        Group<User> newGroup = new Group<>(groupName);
        userSvc.addUserGroup(newGroup);
    }

    public void editMentor() {
        UserService userSvc = new UserService();

        String mentorName = view.getStringFromUserInput(view.mentorNameQuestion);

        User mentor = userSvc.getUser(mentorName);

        String choice = view.getStringFromUserInput(view.mentorChangeQuestion);
        switch (choice) {
            case "1":
                String name = view.getStringFromUserInput(view.mentorNameQuestion);
                mentor.setName(name);
                break;
            case "2":
                String email = view.getStringFromUserInput(view.mentorEmailQuestion);
                mentor.setEmail(email);
                break;
            case "3":
                String password = view.getStringFromUserInput(view.mentorPasswordQuestion);
                mentor.setPassword(password);
                break;
            default:
                view.printLine(view.noSuchOption);
                break;
        }
        userSvc.updateUser(mentor);
    }


    public String getGroupsDisplay(){
        UserService userSvc = new UserService();
        Group<String> groupNames = userSvc.getUserGroupNames();

        String groupsFormatted = "";
        for (String name : groupNames) {
            groupsFormatted += name + " | ";
        }
        return groupsFormatted;
    }

    public String getMentorDisplay() {
        UserService userSvc = new UserService();
        String mentorName = view.getStringFromUserInput(view.mentorNameQuestion);

        User mentor = userSvc.getUser(mentorName);

        if (mentor != null && mentor.getRole() == Role.MENTOR) {
            return mentor.toString();
        }
        return view.noMentorOfSuchName;
    }

    public void createLevel() {

        LevelService levelService = new LevelService();
        levelService.initializeLevels();
        HashMap<Integer, String> levels = Level.getLevels();

        view.printLine(view.currentLevelsText);
        for (Map.Entry<Integer, String> entry : levels.entrySet()) {
            view.printLine(Integer.toString(entry.getKey()) + "   " + entry.getValue());
        }
        String lvlName = view.getStringFromUserInput(view.levelNameQuestion);
        Integer thr = getInt(view.levelTresholdQuestion);
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
}
