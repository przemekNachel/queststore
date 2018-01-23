import java.sql.*;
import java.util.Objects;

/*
TODO: - getAllArtifacts: --- figure out whether it needs to return a Group<Group<ArtifactModeL>>, since
TODO:   it does not seem to be used anywhere (yet) - look at the UML and figure it out.
TODO:   SQL just needs to select ALL artifacts from artifact_store (SELECT name, descr, price FROM artifact_store)

TODO: - Figure out the question asked on slack regarding createArtifactGroup
TODO: - Figure out what tmpSetArtifacts is supposed to do.
*/


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

    public Group<Group<ArtifactModel>> getAllArtifacts(){
        return artifacts;
    }

    public ArtifactModel getArtifact(String name){
        try {
            Connection con = connectToDatabase();
            Statement stmt = Objects.requireNonNull(con).createStatement();

            String sql = ("SELECT * FROM artifact_store WHERE name='" + name + "';");
            ResultSet rs = stmt.executeQuery(sql);


            String artName = rs.getString("name");
            String artDesc = rs.getString("descr");
            float artPrice = rs.getFloat("price");

            stmt.close();
            rs.close();
            con.close();

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

            String sql = ("INSERT INTO artifact_store (name, descr, price)" +
                    "VALUES('"+ artName + "', '" + artDesc + "', '" + artPrice + "');");
            stmt.executeUpdate(sql);

            con.commit();

            stmt.close();
            con.close();


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
                    "descr='" + artDesc + "', " +
                    "price='" + artPrice + "' " +
                    "WHERE name='" + artName+ "';");

            stmt.executeUpdate(sql);
            con.commit();

            stmt.close();
            con.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to update artifact: " + e.getMessage());
        }

    }

    public boolean deleteArtifact(ArtifactModel artifact){
        String artName = artifact.getName();

        try {
            Connection con = connectToDatabase();
            Objects.requireNonNull(con).setAutoCommit(false);
            Statement stmt = con.createStatement();

            String sql = "DELETE from artifact_store WHERE name='" + artName + "';";

            stmt.executeUpdate(sql);
            con.commit();

            stmt.close();
            con.close();
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Unable to remove artifact: " + e.getMessage());
        }

    }

    public Group<String> getArtifactGroupNames(){
        Group<String> groupsNames = new Group<>("Group names");

        try {
            Connection con = connectToDatabase();
            Statement stmt = Objects.requireNonNull(con).createStatement();

            String sql = "SELECT group_name FROM group_names";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                String group_name = rs.getString("group_name");
                groupsNames.add(group_name);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch group names: " + e.getMessage());
        }

        return groupsNames;
    }

    public Group<ArtifactModel> getArtifactGroup(String groupName){
        Group<ArtifactModel> group = new Group<>("Artifact group");

        try {
            Connection con = connectToDatabase();
            Statement stmt = Objects.requireNonNull(con).createStatement();

            String sql = "SELECT\n" +
                    "  artifact_store.artifact_id, name, descr, price, group_names.group_name\n" +
                    "FROM\n" +
                    "  artifact_store\n" +
                    "  INNER JOIN artifact_associations ON artifact_associations.association_id = artifact_store.artifact_id\n" +
                    "  INNER JOIN group_names ON group_names.group_id = artifact_associations.group_id\n" +
                    " WHERE group_name='" + groupName + "';";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                String name = rs.getString("name");
                String descr = rs.getString("descr");
                float price = rs.getFloat("price");
                group.add(new ArtifactModel(name, descr, price));
            }

            stmt.close();
            rs.close();
            con.close();

            return group;

        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch artifact group " + e.getMessage());
        }

    }

    public void createArtifactGroup(Group<ArtifactModel> group){

        artifacts.add(group);
    }

    public void tmpSetArtifacts(Group<Group<ArtifactModel>> artifacts){
        this.artifacts = artifacts;

    }
}
