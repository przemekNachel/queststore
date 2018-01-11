public class CodecoolerModel extends User {
  public String role = "codecooler";
  public WalletService wallet;
  // public Level level;
  public Group<ArtifactModel> artifacts;

  public CodecoolerModel(String nickname, String email, String password, Group<User> studentGroup) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;


    associatedGroups = new Group<Group<User>>();
    associatedGroups.add(studentGroup);
  }

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(String role) {
    this.role = role;
  }

  public void addArtifact(ArtifactModel artifact) {

  }

  public void getArtifact(String name) { //ArtifactModel

  }

  public void getGroupNames() { //Group<String>

  }

  public void getStatisticsDisplay() { //String

  }

  public void getCodecoolerGroupDisplay() { //String

  }

  public void getWallet() { //WalletService

  }

  // public String toString() { 
  //
  // }
}
