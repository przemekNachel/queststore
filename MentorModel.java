public class MentorModel extends User {
  public String role = "mentor";

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return role + "|" + nickname + "|" + email  + "|" + password + "|" + associatedGroups  + "|";
  }
}
