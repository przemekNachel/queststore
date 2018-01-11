public class CodecoolerModel extends User {
  public String role = "codecooler";
  public WalletService wallet;
  public Level level;
  public Group<Artifact> artifacts;

  public CodecoolerModel(String nickname, String email, String password, Group<User> studentGroup) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;


    associatedGroups = new Group<Group<User>>();
    associatedGroups.add(student);
  }

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(String role) {
    this.role = role;
  }

  public void addArtifact(Artifact artifact) {

  }
}
