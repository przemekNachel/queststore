package main.java.com.nwo.queststore.controller;

import main.java.com.nwo.queststore.model.*;
import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.view.AdminView;
import main.java.com.nwo.queststore.enums.Role;
import main.java.com.nwo.queststore.service.UserService;
import main.java.com.nwo.queststore.service.LevelService;

import java.util.HashMap;
import java.util.Map;

public class AdminController extends AbstractUserController {

    private AdminView view;

    public AdminController() {
        super(new AdminView(
                new MenuModel(
                    new MenuOptionModel("0", "Exit"),
                    new MenuOptionModel("1", "Create a mentor"),
                    new MenuOptionModel("2", "Assign a mentor to a group"),
                    new MenuOptionModel("3", "Create user group"),
                    new MenuOptionModel("4", "Edit mentor"),
                    new MenuOptionModel("5", "View mentor's details"),
                    new MenuOptionModel("6", "View groups"),
                    new MenuOptionModel("7", "Create level")
                )
            )
        );
        this.view = (AdminView)super.view;

        userSvc = new UserService();
    }

    private String getNonexistentMentorName() {

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

        String name = getNonexistentMentorName();
        String email = view.getStringFromUserInput(view.mentorEmailQuestion);
        String password = view.getStringFromUserInput(view.mentorPasswordQuestion);

        GroupModel<String> mentorGroups = new GroupModel<>("mentor groups");
        mentorGroups.add("mentors");
        MentorModel mentor = new MentorModel(new RawUserModel(Role.MENTOR, name, email, password, mentorGroups));

        userSvc.addUser(mentor);

    }

    public void start() {

        boolean requestedExit = false;
        do {

            MenuOptionModel userOption = view.getMenuOptionFromUserInput(" Please choose option: ");
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

    public void assignMentorToGroup() {

        MentorModel mentor = getMentorFromUserInput();

        GroupModel<String> allowedGroupModelNames = userSvc.getUserGroupNames();
        String groupName = getNameFromUserInput(view.groupNameQuestion, view.nameOutOfRange, allowedGroupModelNames);

        boolean userAddedToGroup = userSvc.addUserAdherence(mentor, groupName);

        if (!userAddedToGroup || mentor == null) {
            view.printLine(view.assignMentorToGroupError);
        }
    }

    public void createGroup() {

        GroupModel<String> disallowedGroupModelNames = userSvc.getUserGroupNames();
        String groupName = getExclusiveNameFromUserInput(view.groupNameQuestion, view.nameAlreadyTaken, disallowedGroupModelNames);

        GroupModel<UserModel> newGroupModel = new GroupModel<>(groupName);
        userSvc.addUserGroup(newGroupModel);
    }

    public void editMentor() {

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

    private MentorModel getMentorFromUserInput() {

        return (MentorModel)getUserFromUserInput(view.mentorNameQuestion, view.nameOutOfRange, "mentors");
    }

    public String getGroupsDisplay(){
        UserService userSvc = new UserService();
        GroupModel<String> groupModelNames = userSvc.getUserGroupNames();

        String groupsFormatted = "";
        for (String name : groupModelNames) {
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

        HashMap<Integer, String> levels = LevelModel.getLevels();

        GroupModel<String> disallowedLevelNames = new GroupModel<>("disallowed level names");

        view.printLine(view.currentLevelsText);
        for (Map.Entry<Integer, String> entry : levels.entrySet()) {

            view.printLine(Integer.toString(entry.getKey()) + "   " + entry.getValue());
            disallowedLevelNames.add(entry.getValue());
        }
        String lvlName = getExclusiveNameFromUserInput(view.levelNameQuestion, view.nameAlreadyTaken, disallowedLevelNames);
        Integer thr = view.getIntFromUserInput(view.levelThresholdQuestion); // might need to be in loop in case of ivalid int input
        LevelModel.addLevel(lvlName, thr);
    }

}
