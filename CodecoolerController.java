public class CodecoolerController {
  public CodecoolerView view = new CodecoolerModel();
  public CodecoolerModel currentUser = new CodecoolerModel();

  public CodecoolerController(CodecoolerModel codecooler) {
    this.currentUser = codecooler;
  }

  public void buyArtifact(String name) {
    UserDaoImpl userDao = new UserDaoImpl();
    ArtifactStoreController store = new ArtifactStoreController();

  }

  public void useArtifact(Artifact artifact) {

  }
}
