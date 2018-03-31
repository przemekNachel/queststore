package main.java.com.nwo.queststore.model;

import user.user.Role;

public abstract class UserModel {
    protected Role role;
    protected String nickname;
    protected String password;
    protected String email;
    protected GroupModel<String> associatedGroupModelNames;

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

    public GroupModel<String> getAssociatedGroupModelNames() {
        return associatedGroupModelNames;
    }

    public abstract Role getRole();
    public abstract void setRole(Role role);

    public String toString() {

        String strGroups = "";
        for (String groupName : associatedGroupModelNames) {

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
