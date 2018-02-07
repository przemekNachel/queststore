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

    private String getNonexistentMentorName() {

        UserService userSvc = new UserService();

        String name;
        boolean providedValidName = false;
        do {
            name = this.view.getStringFromUserInput(view.mentorNameQuestion);
            if (userSvc.getUser(name) == null) {

                providedValidName = true;
            } else {

                view.printLine(view.nameAlreadyTaken);
            }

        } while(!providedValidName);
        return name;
    }

    private void createMentor() {

        UserService userSvc = new UserService();

        String name = getNonexistentMentorName();
        String email = this.view.getStringFromUserInput(view.mentorEmailQuestion);
        String password = this.view.getStringFromUserInput(view.mentorPasswordQuestion);

        Group<String> mentorGroups = new Group<>("mentor groups");
        mentorGroups.add("mentors");
        MentorModel mentor = new MentorModel(new RawUser(Role.MENTOR, name, email, password, mentorGroups));

        userSvc.addUser(mentor);

    }

    public void start() {

        boolean requestedExit = false;
        do {

            MenuOption userOption = view.getMenuOptionFromUserInput(" Please choose option: ");
            if (userOption.getId().equals("0")) {
                requestedExit = true;
                view.clearScreen();
            } else {

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

    private String getNameFromUserInput(String prompt, String disallowedMessage, Group<String> allowedNames) {

        String name;
        boolean providedValidName = false;
        do {

            name = view.getStringFromUserInput(prompt);
            if (allowedNames.contains(name)) {

                providedValidName = true;
            } else {

                view.printLine(disallowedMessage);
            }

        } while(!providedValidName);
        return name;
    }

    /* Returns the first input string that does not occur in disallowedNames */
    private String getExclusiveNameFromUserInput(String prompt, String disallowedMessage, Group<String> disallowedNames) {

        String name;
        boolean providedValidName = false;
        do {

            name = view.getStringFromUserInput(prompt);
            if (!disallowedNames.contains(name)) {

                providedValidName = true;
            } else {

                view.printLine(disallowedMessage);
            }

        } while(!providedValidName);
        return name;
    }

    private Group<String> userGroupToStringGroup(Group<User> userGroup) {

        Group<String> stringGroup = new Group<>("user names");
        for (User user : userGroup) {

            stringGroup.add(user.getName());
        }
        return stringGroup;
    }

    private MentorModel getMentorFromUserInput() {

        UserService userSvc = new UserService();
        Group<String> allowedMentorNames = userGroupToStringGroup(userSvc.getUserGroup("mentors"));

        String name = getNameFromUserInput(view.mentorNameQuestion, view.nameOutOfRange, allowedMentorNames);

        return (MentorModel)userSvc.getUser(name);
    }

    public void assignMentorToGroup() {

        UserService userSvc = new UserService();

        MentorModel mentor = getMentorFromUserInput();
        Group<String> allowedGroupNames = userSvc.getUserGroupNames();

        String groupName = getNameFromUserInput(view.groupNameQuestion, view.nameOutOfRange, allowedGroupNames);

        boolean userAddedToGroup = userSvc.addUserAdherence(mentor, groupName);

        if (!userAddedToGroup || mentor == null) {
            view.printLine(view.assignMentorToGroupError);
        }
    }

    public void createGroup() {

        UserService userSvc = new UserService();

        Group<String> disallowedGroupNames = userSvc.getUserGroupNames();

        String groupName = getExclusiveNameFromUserInput(view.groupNameQuestion, view.nameAlreadyTaken, disallowedGroupNames);

        Group<User> newGroup = new Group<>(groupName);
        userSvc.addUserGroup(newGroup);
    }

    public void editMentor() {
        UserService userSvc = new UserService();

        MentorModel mentor = getMentorFromUserInput();

        String choice = view.getStringFromUserInput(view.mentorChangeQuestion);
        switch (choice) {
            case "1":
                String email = view.getStringFromUserInput(view.mentorEmailQuestion);
                mentor.setEmail(email);
                break;
            case "2":
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
  
        MentorModel mentor = getMentorFromUserInput();

        if (mentor != null && mentor.getRole() == Role.MENTOR) {
            return mentor.toString();
        }
        return view.noMentorOfSuchName;
    }

    private void createLevel() {

        LevelService levelService = new LevelService();
        levelService.initializeLevels();

        HashMap<Integer, String> levels = Level.getLevels();

        Group<String> disallowedLevelNames = new Group<>("disallowed level names");

        view.printLine(view.currentLevelsText);
        for (Map.Entry<Integer, String> entry : levels.entrySet()) {

            view.printLine(Integer.toString(entry.getKey()) + "   " + entry.getValue());
            disallowedLevelNames.add(entry.getValue());
        }
        String lvlName = getExclusiveNameFromUserInput(view.levelNameQuestion, view.nameAlreadyTaken, disallowedLevelNames);
        Integer thr = view.getIntFromUserInput(view.levelThresholdQuestion); // might need to be in loop in case of ivalid int input
        Level.addLevel(lvlName, thr);
    }

}
