public class CodecoolerController {
  public CodecoolerView view = new CodecoolerView();
  public CodecoolerModel currentUser = new CodecoolerModel();

  public CodecoolerController(CodecoolerModel codecooler) {
    this.currentUser = codecooler;
  }

  public void buyArtifact() {
    UserDaoImpl userDao = new UserDaoImpl();
    ArtifactStoreController store = new ArtifactStoreController();

    String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
    Group<User> consumers = userDao.getUserGroup("students");

    ArtifactModel boughtArtifact = store.buyArtifact(artifactName, consumers);
    currentUser.addArtifact(boughtArtifact);
    userDao.updateUser(currentUser);

  }

  public void useArtifact() {
    String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
    currentUser.getArtifact(artifactName);
    view.printLine("Artifact has been used!");
  }
}
