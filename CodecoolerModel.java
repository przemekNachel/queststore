public class CodecoolerModel extends User {
  public String role = "codecooler";
  public WalletService wallet;
  public Group<ArtifactModel> artifacts;
  // public Level level;

  public CodecoolerModel() {

  }

  public CodecoolerModel(String nickname, String email, String password, WalletService wallet, Group<User> studentGroup) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
    this.wallet = wallet;


    associatedGroups = new Group<Group<User>>("Groups to which adheres");
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
    artifacts.add(artifact);
  }

  public void getArtifact(String name) { //ArtifactModel

  }

  public void getGroupNames() { //Group<String>

  }

  public void getStatisticsDisplay() { //String

  }

  public void getCodecoolerGroupDisplay() { //String

  }

  public WalletService getWallet() {
    return wallet;
  }

  // public String toString() {
  //
  // }
}
