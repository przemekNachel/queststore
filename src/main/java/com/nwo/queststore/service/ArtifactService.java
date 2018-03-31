package main.java.com.nwo.queststore.service;

import main.java.com.nwo.queststore.dao.ArtifactDaoImpl;
import exceptionlog.ExceptionLog;
import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.model.ArtifactModel;

import java.sql.SQLException;

public class ArtifactService {

    public GroupModel<String> getArtifactGroupNames() {

        GroupModel<String> groupModelNames = new GroupModel<>("artifact group names");
        try {
            groupModelNames = new ArtifactDaoImpl().getArtifactGroupNames();
            groupModelNames.remove("artifacts");
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return groupModelNames;
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
            new ArtifactDaoImpl().updateArtifact(artifact);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            updated = false;
        }
        return updated;
    }

    public GroupModel<GroupModel<ArtifactModel>> getAllArtifacts() {

        GroupModel<GroupModel<ArtifactModel>> artifactCollection = new GroupModel<>("all artifacts");
        try {

            artifactCollection = new ArtifactDaoImpl().getAllArtifacts();
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return artifactCollection;
    }

    public GroupModel<String> getArtifactNames() {

        GroupModel<String> names = new GroupModel<>("artifact names");
        for (GroupModel<ArtifactModel> subGroupModel : getAllArtifacts()) {

            for (ArtifactModel artifact : subGroupModel) {

                names.add(artifact.getName());
            }
        }
        return names;
    }

    public GroupModel<ArtifactModel> getArtifactGroup(String groupName) {

        GroupModel<ArtifactModel> specializedGroupModel = null;
        try {
            specializedGroupModel = new ArtifactDaoImpl().getArtifactGroup(groupName);

        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return specializedGroupModel;
    }
}
