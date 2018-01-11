import java.util.Iterator;

public class CodecoolerController {
  public CodecoolerView view = new CodecoolerView();
  public CodecoolerModel currentUser = new CodecoolerModel();

  public CodecoolerController(CodecoolerModel codecooler) {
    this.currentUser = codecooler;
  }

  public void start() {
    view.printLine("From Codecooler");
  }

  public void buyArtifact() {
    UserDaoImpl userDao = new UserDaoImpl();
    ArtifactStoreController store = new ArtifactStoreController();

    String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
    Group<User> consumers = userDao.getUserGroup("students");

    Group<CodecoolerModel> converted = new Group<>("Codecooler");
    Iterator<User> iter = consumers.getIterator();
    while (iter.hasNext()) {
      converted.add((CodecoolerModel)iter.next());
    }

    ArtifactModel boughtArtifact = store.buyArtifact(artifactName, converted);
    currentUser.addArtifact(boughtArtifact);
    userDao.updateUser(currentUser);

  }

  public void useArtifact() {
    String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
    currentUser.getArtifact(artifactName);
    view.printLine("Artifact has been used!");
  }
}
