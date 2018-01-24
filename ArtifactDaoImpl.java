import java.sql.*;
import java.util.Objects;
import java.util.Iterator;


public class ArtifactDaoImpl implements ArtifactDao{

    public Connection connectToDatabase() throws SQLException {

        String db_path = "jdbc:sqlite:database/database.db";
            return DriverManager.getConnection(db_path);


    }

    public Group<Group<ArtifactModel>> getAllArtifacts() {
        Group<Group<ArtifactModel>> allArtifacts = new Group<>("All artifacts");
        Group<String> groupNames = getArtifactGroupNames();
        Iterator<String> groupNamesIter = groupNames.getIterator();

        while(groupNamesIter.hasNext()) {
            allArtifacts.add(getArtifactGroup(groupNamesIter.next()));
        }

        return allArtifacts;
    }

    public ArtifactModel getArtifact(String name) throws SQLException {
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

    }

    public void addArtifact(ArtifactModel artifact, String groupName) throws SQLException {
        String artName = artifact.getName();
        String artDesc = artifact.getDescription();
        float artPrice = artifact.getPrice();

            Connection con = connectToDatabase();
            Objects.requireNonNull(con).setAutoCommit(false);
            Statement stmt = con.createStatement();

            String sql = ("INSERT INTO artifact_store (name, descr, price)" +
                    "VALUES('"+ artName + "', '" + artDesc + "', '" + artPrice + "');");
            stmt.executeUpdate(sql);

            con.commit();

            stmt.close();
            con.close();
    }

    public void updateArtifact(ArtifactModel artifact)throws SQLException {
        String artName = artifact.getName();
        String artDesc = artifact.getDescription();
        float artPrice = artifact.getPrice();

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


    }

    public boolean deleteArtifact(ArtifactModel artifact) throws SQLException {
        String artName = artifact.getName();

            Connection con = connectToDatabase();
            Objects.requireNonNull(con).setAutoCommit(false);
            Statement stmt = con.createStatement();

            String sql = "DELETE from artifact_store WHERE name='" + artName + "';";

            stmt.executeUpdate(sql);
            con.commit();

            stmt.close();
            con.close();
            return true;
    }

    public Group<String> getArtifactGroupNames() throws SQLException {
        Group<String> groupsNames = new Group<>("Group name");

            Connection con = connectToDatabase();
            Statement stmt = Objects.requireNonNull(con).createStatement();

            String sql = "SELECT group_name FROM group_names";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                String group_name = rs.getString("group_name");
                groupsNames.add(group_name);
            }
        return groupsNames;
    }

    public Group<ArtifactModel> getArtifactGroup(String groupName) throws SQLException{
        Group<ArtifactModel> group = new Group<>(groupName);

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
    }

    public void addArtifactGroup(Group<ArtifactModel> group) throws SQLException{
            Connection con = connectToDatabase();
            Objects.requireNonNull(con).setAutoCommit(false);
            Statement stmt = con.createStatement();

            String sql = ("INSERT INTO group_names (group_name) VALUES ('" + group.getName() + "');");
            stmt.executeUpdate(sql);

            con.commit();

            stmt.close();
            stmt.close();
    }

}
