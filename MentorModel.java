public class MentorModel extends User {

  public MentorModel(String nickname, String email, String password, Group<User> mentorGroup) {
    role = Role.MENTOR;
    this.nickname = nickname;
    this.email = email;
    this.password = password;

    associatedGroups = new Group<Group<User>>("Groups to which adheres");
    associatedGroups.add(mentorGroup);
  }
  @Override
  public Role getRole() {
    return role;
  }

  @Override
  public void setRole(Role role) {
    this.role = role;
  }
}
