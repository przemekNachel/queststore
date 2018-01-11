import java.util.Iterator;

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

  public ArtifactModel getArtifact(String name) {
    ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();
    return artifactDao.getArtifact(name);
  }

  public Group<String> getGroupNames() {
    Group<String> names = new Group<String>("Group Names");

    Group<Group<User>> groups = getAssociatedGroups();
    Iterator<Group<User>> iter = groups.getIterator();
    while(iter.hasNext()) {
      names.add(iter.next().getName());
    }

    return names;
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
