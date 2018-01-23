import java.sql.*;
import java.util.Iterator;
import java.util.Objects;

public class ArtifactDaoImpl implements ArtifactDao{
    private static Group<Group<ArtifactModel>> artifacts;

    private Connection connectToDatabase() {

        String db_path = "jdbc:sqlite:database/database.db";
        try {
            return DriverManager.getConnection(db_path);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to the database." + e.getMessage());
        }
    }

    public Group<Group<ArtifactModel>> getAllArtifcts(){
        return artifacts;
    }

    public ArtifactModel getArtifact(String name){
        try {
            Connection con = connectToDatabase();
            Statement stmt = Objects.requireNonNull(con).createStatement();

            String sql = ("SELECT * FROM artifact_store WHERE name='" + name + "';");
            ResultSet rs = stmt.executeQuery(sql);


            String artName = rs.getString("name");
            String artDesc = rs.getString("desc");
            float artPrice = rs.getFloat("price");

            return new ArtifactModel(artName, artDesc, artPrice);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch the artifact: " + e.getMessage());
        }
    }

    public void addArtifact(ArtifactModel artifact, String groupName){
        String artName = artifact.getName();
        String artDesc = artifact.getDescription();
        float artPrice = artifact.getPrice();


        try {
            Connection con = connectToDatabase();
            Objects.requireNonNull(con).setAutoCommit(false);
            Statement stmt = con.createStatement();

            String sql = ("INSERT INTO artifact_store (name, desc, price)" +
                    "VALUES('"+ artName + "', '" + artDesc + "', '" + artPrice + "');");
            stmt.executeUpdate(sql);

            stmt.close();
            con.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to add artifact to the database." + e.getMessage());
            }

    }

    public void updateArtifact(ArtifactModel artifact){
        String artName = artifact.getName();
        String artDesc = artifact.getDescription();
        float artPrice = artifact.getPrice();

        try {
            Connection con = connectToDatabase();
            Objects.requireNonNull(con).setAutoCommit(false);
            Statement stmt = con.createStatement();

            String sql = ("UPDATE artifact_store SET " +
                    "desc='" + artDesc + "', " +
                    "price='" + artPrice + "' " +
                    "WHERE name='" + artName+ "';");

            stmt.executeUpdate(sql);
            con.commit();

            stmt.close();
            con.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to update artifact: " + e.getMessage()) ;
            }

    }

    public boolean deleteArtifact(ArtifactModel artifact){
        boolean artifactRemoved = false;
        Iterator<Group<ArtifactModel>> artifactGroupIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(artifactGroupIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = artifactGroupIterator.next();
            Iterator<ArtifactModel> artifactIterator = artifactGroup.getIterator();
            while(artifactIterator.hasNext()){
                ArtifactModel currentArtifact = artifactIterator.next();
                if(currentArtifact.getName().equals(artifact.getName())){
                    artifactGroup.remove(currentArtifact);
                    artifactRemoved = true;
                }
            }
        }
        return artifactRemoved;
    }

    public Group<String> getArtifactGroupNames(){
        Group<String> groupsNames = new Group<>("Group names");
        Iterator<Group<ArtifactModel>> groupIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(groupIterator.hasNext()){
            groupsNames.add(groupIterator.next().getName());
        }
        return groupsNames;
    }

    public Group<ArtifactModel> getArtifactGroup(String groupName){
        Iterator<Group<ArtifactModel>> groupIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(groupIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = groupIterator.next();
            if(artifactGroup.getName().equals(groupName)){
                return artifactGroup;
            }
        }
        return null;
    }

    public void createArtifactGroup(Group<ArtifactModel> group){
        artifacts.add(group);
    }

    public void tmpSetArtifacts(Group<Group<ArtifactModel>> artifacts){
        this.artifacts = artifacts;
        /*
        1. file read in another method

         */
    }
}
