package user.user;

import generic_group.Group;

import java.util.Iterator;

public abstract class User {
    protected Role role;
    protected String nickname;
    protected String password;
    protected String email;
    protected Group<String> associatedGroupNames;

    public String getName() {
        return nickname;
    }

    public void setName(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Group<String> getAssociatedGroupNames() {
        return associatedGroupNames;
    }

    public abstract Role getRole();
    public abstract void setRole(Role role);

    public String toString() {

        String strGroups = "";
        for (String groupName : associatedGroupNames) {

            strGroups += groupName + ";";
        }
        strGroups = removeLastChar(strGroups);
        return role + "|" + nickname + "|" + email  + "|" + password + "|" + strGroups  + "|";
    }

    private String removeLastChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, str.length()-1);
    }


}
