public class CodecoolerController {
  public CodecoolerView view = new CodecoolerModel();
  public CodecoolerModel currentUser = new CodecoolerModel();

  public CodecoolerController(CodecoolerModel codecooler) {
    this.currentUser = codecooler;
  }

  public void buyArtifact() {
    UserDaoImpl userDao = new UserDaoImpl();
    ArtifactStoreController store = new ArtifactStoreController();

    String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
    Group<User> consumers = userDao.getUserGroup("students");

    currentUser.addArtifact(store.buyArtifact(artifactName, consumers));
    userDao.updateUser(currentUser);

  }

  public void useArtifact(ArtifactModel artifact) {

  }
}
