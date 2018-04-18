package template;

import artifact.ArtifactDaoImpl;
import level.LevelService;
import user.codecooler.CodecoolerModel;
import user.user.User;

import java.sql.SQLException;

public class CodecoolerTemplateResolver {

    private ViewData template;
    private ArtifactDaoImpl artifactDao;
    private CodecoolerModel codecooler;

    public CodecoolerTemplateResolver(ViewData template, User codecooler) {
        this.template = template;
        this.codecooler = (CodecoolerModel) codecooler;
        artifactDao = new ArtifactDaoImpl();
    }

    public ViewData getTemplate() {
        try {
            initializeVariables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return template;
    }

    private void initializeVariables() throws SQLException {
        new LevelService().initializeLevels();
        template.setVariable("user", codecooler);
        template.setVariable("level", codecooler.getLevel().getCurrentLevel());
        template.setVariable("money", codecooler.getWallet().getBalance());
        template.setVariable("classes", codecooler.getCodecoolerGroupDisplay());
        template.setVariable("basic_artifacts", artifactDao.getArtifactGroup("artifact_basic"));
        template.setVariable("magic_artifacts", artifactDao.getArtifactGroup("artifact_magic"));
        template.setVariable("user_artifacts", codecooler.getArtifacts());
    }
}
