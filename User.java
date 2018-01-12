import java.util.Iterator;

public abstract class User {
  protected Role role;
  protected String nickname;
  protected String password;
  protected String email;
  protected Group<Group<User>> associatedGroups;

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

  public Group<Group<User>> getAssociatedGroups() {
    return associatedGroups;
  }

  public void setAssociatedGroups(Group<Group<User>> associatedGroups) {
    this.associatedGroups = associatedGroups;
  }

  public abstract Role getRole();
  public abstract void setRole(Role role);

  public String toString() {

    String strGroups = "";
    Iterator<Group<User>> iter = associatedGroups.getIterator();
    while (iter.hasNext()) {
      Group<User> tmp = iter.next();
      strGroups += tmp.getName() + ";";
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
