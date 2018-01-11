public class MentorModel extends User {
  public String role = "mentor";


  public MentorModel(String nickname, String email, String password, Group<User> mentorGroup) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;


    associatedGroups = new Group<Group<User>>("Groups to which adheres");
    associatedGroups.add(mentorGroup);
  }
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
