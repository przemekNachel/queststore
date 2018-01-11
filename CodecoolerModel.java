import java.util.Iterator;

public class CodecoolerModel extends User {

  public WalletService wallet;
  public Group<ArtifactModel> artifacts;
  // public Level level;

  public CodecoolerModel() {
    this.role = Role.CODECOOLER;
  }

  public CodecoolerModel(String nickname, String email, String password, WalletService wallet, Group<User> studentGroup) {
    this();
    this.nickname = nickname;
    this.email = email;
    this.password = password;
    this.wallet = wallet;

    associatedGroups = new Group<Group<User>>("Groups to which adheres");
    associatedGroups.add(studentGroup);
  }

  @Override
  public Role getRole() {
    return role;
  }

  @Override
  public void setRole(Role role) {
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

  public String getStatisticsDisplay() {
    String statistics = "";
    String walletBalance = "Wallet balance: ";
    String artifactsOwned = "Owned artifacts:";

    walletBalance = wallet.toString() + "\n";

    Iterator<ArtifactModel> iter = artifacts.getIterator();
    while(iter.hasNext()) {
      artifactsOwned += "\n" + iter.next().getName();
    }
    statistics = walletBalance + artifactsOwned;

    return statistics;
  }

  public String getCodecoolerGroupDisplay() {
    String groupNamesString = "";

    Group<Group<User>> groups = getAssociatedGroups();
    Iterator<Group<User>> iter = groups.getIterator();
    while(iter.hasNext()) {
      groupNamesString += "|" + iter.next().getName();
    }

    return groupNamesString;
  }

  public WalletService getWallet() {
    return wallet;
  }

  public String toString() {
    return "Blank lol";
  }
}
