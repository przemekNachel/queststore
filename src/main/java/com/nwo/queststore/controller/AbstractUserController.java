package main.java.com.nwo.queststore.controller;

import main.java.com.nwo.queststore.view.AbstractConsoleView;
import main.java.com.nwo.queststore.model.GroupModel;
import user.service.UserService;
import main.java.com.nwo.queststore.model.UserModel;

public abstract class AbstractUserController {

    protected AbstractConsoleView view;
    protected UserService userSvc;

    public AbstractUserController(AbstractConsoleView view) {

        this.view = view;
    }

    public GroupModel<String> userGroupToStringGroup(GroupModel<UserModel> userGroupModel) {

        GroupModel<String> stringGroupModel = new GroupModel<>("user names");
        for (UserModel userModel : userGroupModel) {

            stringGroupModel.add(userModel.getName());
        }
        return stringGroupModel;
    }

    public String getNameFromUserInput(String prompt, String disallowedMessage, GroupModel<String> allowedNames) {

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
    public String getExclusiveNameFromUserInput(String prompt, String disallowedMessage, GroupModel<String> disallowedNames) {

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

    public UserModel getUserFromUserInput(String nameQuestion, String outOfRangeError, String groupName) {

        GroupModel<UserModel> users = userSvc.getUserGroup(groupName);
        GroupModel<String> allowedUserNames = userGroupToStringGroup(users);

        String name = getNameFromUserInput(nameQuestion, outOfRangeError, allowedUserNames);

        return userSvc.getUser(name);
    }
}
