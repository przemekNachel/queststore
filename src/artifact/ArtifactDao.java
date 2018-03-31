package artifact;

import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.model.ArtifactModel;

import java.sql.Connection;
import java.sql.SQLException;

public interface ArtifactDao{

    Connection connectToDatabase() throws SQLException;
    GroupModel<GroupModel<ArtifactModel>> getAllArtifacts() throws SQLException;
    ArtifactModel getArtifactByName(String name) throws SQLException;
    ArtifactModel getArtifactById(int artifactId) throws SQLException;
    GroupModel<String> getArtifactGroupNames() throws SQLException;
    GroupModel<ArtifactModel> getArtifactGroup(String groupName) throws SQLException;
    void addArtifact(ArtifactModel artifact) throws SQLException ;
    void updateArtifact(ArtifactModel artifact) throws SQLException;
    void addArtifactGroup(GroupModel<ArtifactModel> groupModel) throws SQLException;
    void deleteArtifact(ArtifactModel artifact) throws SQLException;
    void addArtifactAdherence(String name, String groupName) throws SQLException;
    void updateUserArtifactsUsage(int userId, ArtifactModel artifact) throws SQLException;
    GroupModel<ArtifactModel> getUserArtifacts(int userId) throws SQLException;
}
