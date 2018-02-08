package helper;

import console.menu.AbstractConsoleView;
import generic_group.Group;
import user.service.UserService;
import user.user.User;

public class ControllerHelper {

    private AbstractConsoleView view;

    public ControllerHelper(AbstractConsoleView view) {

        this.view = view;
    }

    public Group<String> userGroupToStringGroup(Group<User> userGroup) {

        Group<String> stringGroup = new Group<>("user names");
        for (User user : userGroup) {

            stringGroup.add(user.getName());
        }
        return stringGroup;
    }

    public String getNameFromUserInput(String prompt, String disallowedMessage, Group<String> allowedNames) {

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
    public String getExclusiveNameFromUserInput(String prompt, String disallowedMessage, Group<String> disallowedNames) {

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

    public User getUserFromUserInput(String nameQuestion, String outOfRangeError, String groupName) {

        UserService userSvc = new UserService();
        ControllerHelper ch = new ControllerHelper(view);

        Group<User> users = userSvc.getUserGroup(groupName);
        Group<String> allowedUserNames = ch.userGroupToStringGroup(users);

        String name = ch.getNameFromUserInput(nameQuestion, outOfRangeError, allowedUserNames);

        return userSvc.getUser(name);
    }
}
