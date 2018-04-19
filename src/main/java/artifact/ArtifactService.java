package artifact;

import exceptionlog.ExceptionLog;
import generic_group.Group;

import java.sql.SQLException;

public class ArtifactService {

    public Group<String> getArtifactGroupNames() {

        Group<String> groupNames = new Group<>("artifact group names");
        try {
            groupNames = new ArtifactDaoImpl().getArtifactGroupNames();
            groupNames.remove("artifacts");
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return groupNames;
    }

    public boolean deleteArtifact(String name) {

        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();

        boolean deleted = true;
        try {

            ArtifactModel artifact = artifactDao.getArtifactByName(name);
            artifactDao.deleteArtifact(artifact);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            deleted = false;
        }
        return deleted;
    }

    public boolean createArtifact(String name, String desc, Integer price, String specializedGroupName) {

        boolean created = true;
        ArtifactModel newArtifact = new ArtifactModel(name, desc, price);
        try {

            new ArtifactDaoImpl().addArtifact(newArtifact);
            addArtifactAdherence(newArtifact, "artifacts");
            addArtifactAdherence(newArtifact, specializedGroupName);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            created = false;
        }
        return created;
    }

    public boolean addArtifactAdherence(ArtifactModel artifact, String groupName) {

        boolean adherenceAdded = true;
        try {
            new ArtifactDaoImpl().addArtifactAdherence(artifact.getName(), groupName);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            adherenceAdded = false;
        }
        return adherenceAdded;
    }

    public ArtifactModel getArtifactByName(String name) {

        ArtifactModel artifact = null;
        try {
            artifact = new ArtifactDaoImpl().getArtifactByName(name);
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return artifact;
    }

    public boolean updateArtifact(ArtifactModel artifact) {

        boolean updated = true;
        try {
            new ArtifactDaoImpl().updateArtifact(artifact.getName(), artifact);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            updated = false;
        }
        return updated;
    }

    public Group<Group<ArtifactModel>> getAllArtifacts() {

        Group<Group<ArtifactModel>> artifactCollection = new Group<>("all artifacts");
        try {

            artifactCollection = new ArtifactDaoImpl().getAllArtifacts();
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return artifactCollection;
    }

    public Group<String> getArtifactNames() {

        Group<String> names = new Group<>("artifact names");
        for (Group<ArtifactModel> subGroup : getAllArtifacts()) {

            for (ArtifactModel artifact : subGroup) {

                names.add(artifact.getName());
            }
        }
        return names;
    }

    public Group<ArtifactModel> getArtifactGroup(String groupName) {

        Group<ArtifactModel> specializedGroup = null;
        try {
            specializedGroup = new ArtifactDaoImpl().getArtifactGroup(groupName);

        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return specializedGroup;
    }
}
