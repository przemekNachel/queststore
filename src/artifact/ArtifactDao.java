package artifact;

import generic_group.Group;

import java.sql.Connection;
import java.sql.SQLException;

public interface ArtifactDao{

    Connection connectToDatabase() throws SQLException;
    Group<Group<ArtifactModel>> getAllArtifacts() throws SQLException;
    ArtifactModel getArtifact(String name) throws SQLException;
    Group<String> getArtifactGroupNames() throws SQLException;
    Group<ArtifactModel> getArtifactGroup(String groupName) throws SQLException;
    void addArtifact(ArtifactModel artifact) throws SQLException ;
    void updateArtifact(ArtifactModel artifact) throws SQLException;
    void addArtifactGroup(Group<ArtifactModel> group) throws SQLException;
    boolean deleteArtifact(ArtifactModel artifact) throws SQLException;
}
