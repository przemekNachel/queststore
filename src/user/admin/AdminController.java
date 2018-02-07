package user.admin;

import console.menu.Menu;
import console.menu.MenuOption;
import generic_group.Group;
import user.mentor.MentorModel;
import user.user.Role;
import user.user.User;
import user.user.UserDao;
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

    private void createMentor() {

        UserDaoImpl dao = new UserDaoImpl();
        String name = this.view.getStringFromUserInput(view.mentorNameQuestion);
        String email = this.view.getStringFromUserInput(view.mentorEmailQuestion);
        String password = this.view.getStringFromUserInput(view.mentorPasswordQuestion);

        try {
            Group<User> mentorsGroup = dao.getUserGroup("mentors");
            MentorModel mentor = new MentorModel(name, email, password, mentorsGroup);

            dao.addUser(mentor);
            mentorsGroup.add(mentor);
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

    private void assignMentorToGroup() {
        UserDaoImpl userDao = new UserDaoImpl();

        String name = view.getStringFromUserInput(view.mentorNameQuestion);
        String groupName = view.getStringFromUserInput(view.groupNameQuestion);
        User user = null;
        try {

            user = userDao.getUser(name);
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        boolean userAddedtoGroup = false;
        try {
            userAddedtoGroup = userDao.addUserAdherence(user, groupName);
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        if (!userAddedtoGroup || user == null) {
            view.printLine(view.assignMentorToFroupError);
        } else {

            try {
                Group<Group<User>> associatedGroups = user
                        .getAssociatedGroups();

                associatedGroups.add(userDao.getUserGroup(groupName));

                user.setAssociatedGroups(associatedGroups);
            } catch (SQLException e) {
                view.printSQLException(e);
            }
        }
    }

    private void createGroup() {
        UserDaoImpl userDao = new UserDaoImpl();
        String groupName = view.getStringFromUserInput(view.groupNameQuestion);
        Group<User> tmp = new Group<>(groupName);
        try {
            userDao.addUserGroup(tmp);
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    private void editMentor() {
        UserDaoImpl dao = new UserDaoImpl();
        String mentorName = view.getStringFromUserInput(view.mentorNameQuestion);

        User mentor = null;
        try {

            mentor = dao.getUser(mentorName);
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        String choice = view.getStringFromUserInput(view.mentorChangeQuestion);
        if (choice.equals("1")) {
            String name = view.getStringFromUserInput(view.mentorNameQuestion);
            mentor.setName(name);
        } else if (choice.equals("2")) {
            String email = view.getStringFromUserInput(view.mentorEmailQuestion);
            mentor.setEmail(email);
        } else if (choice.equals("3")) {
            String password = view.getStringFromUserInput(view.mentorPasswordQuestion);
            mentor.setPassword(password);
        } else {
            view.printLine(view.noSuchOption);
        }
        try {

            dao.updateUser(mentor);
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    private String getGroupsDisplay(){
        UserDaoImpl dao = new UserDaoImpl();
        Group<String> groupNames = null;
        try {
            groupNames = dao.getUserGroupNames();
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        String groupsFormatted = "";
        for (String name : groupNames) {
            groupsFormatted += name + " | ";
        }
        return groupsFormatted;
    }

    private String getMentorDisplay() {
        UserDaoImpl dao = new UserDaoImpl();
        String mentorName = view.getStringFromUserInput(view.mentorNameQuestion);

        User mentor = null;
        try {

            mentor = dao.getUser(mentorName);
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        if (mentor != null && mentor.getRole() == Role.MENTOR) {
            return mentor.toString();
        }
        return view.noMentorOfSuchName;
    }

    private void createLevel() {

        LevelService levelService = new LevelService();
        levelService.initializeLevels();
        HashMap<Integer, String> levels = Level.getLevels();

        view.printLine(view.currentLevelsText);
        for (Map.Entry<Integer, String> entry : levels.entrySet()) {
            view.printLine(Integer.toString(entry.getKey()) + "   " + entry.getValue());
        }
        String lvlName = view.getStringFromUserInput(view.levelNameQuestion);
        Integer thr = view.getIntFromUserInput(view.levelTresholdQuestion); // might need to be in loop in case of ivalid int input
    }

}
