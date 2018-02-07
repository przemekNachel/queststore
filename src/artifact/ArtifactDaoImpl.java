package artifact;

import generic_group.Group;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;


public class ArtifactDaoImpl implements ArtifactDao{

    @Override
    public Connection connectToDatabase() throws SQLException {

        String dbPath = "jdbc:sqlite:database/database.db";
        return DriverManager.getConnection(dbPath);
    }


    @Override
    public Group<Group<ArtifactModel>> getAllArtifacts() throws SQLException {
        Group<Group<ArtifactModel>> allArtifacts = new Group<>("All artifacts");
        Group<String> groupNames = getArtifactGroupNames();
        Iterator<String> groupNamesIter = groupNames.getIterator();

        while(groupNamesIter.hasNext()) {
            allArtifacts.add(getArtifactGroup(groupNamesIter.next()));
        }

        return allArtifacts;
    }

    @Override
    public Group<ArtifactModel> getUserArtifacts(int userId) throws SQLException {
        Group<ArtifactModel> group = new Group<>("User's artifacts: ");
        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT name, descr, price FROM artifact_store " +
                "JOIN user_artifacts USING (artifact_id) " +
                "WHERE user_id="+userId+"";

        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()) {
            String artName = rs.getString("name");
            String artDescr = rs.getString("descr");
            Integer artPrice = rs.getInt("price");
            group.add(new ArtifactModel(artName, artDescr, artPrice));
        }

        stmt.close();
        con.close();
        rs.close();

        return group;
    }

    @Override
    public ArtifactModel getArtifactByName(String name) throws SQLException {
        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = ("SELECT * FROM artifact_store WHERE name='" + name + "';");
        ResultSet rs = stmt.executeQuery(sql);


        String artName = rs.getString("name");
        String artDesc = rs.getString("descr");
        Integer artPrice = rs.getInt("price");

        stmt.close();
        rs.close();
        con.close();

        return new ArtifactModel(artName, artDesc, artPrice);
    }

    @Override
    public ArtifactModel getArtifactById(int artifactId) throws SQLException {
        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = ("SELECT * FROM artifact_store WHERE artifact_id=" + artifactId + ";");
        ResultSet rs = stmt.executeQuery(sql);


        String artName = rs.getString("name");
        String artDesc = rs.getString("descr");
        Integer artPrice = rs.getInt("price");

        stmt.close();
        rs.close();
        con.close();

        return new ArtifactModel(artName, artDesc, artPrice);
    }
    @Override
    public void addArtifact(ArtifactModel artifact) throws SQLException {
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

    @Override
    public void updateUserArtifactsUsage(int userId, ArtifactModel artifact) throws SQLException {
        String artName = artifact.getName();
        String artStatus = String.valueOf(artifact.getUsageStatus());

        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "UPDATE user_artifacts SET used='"+artStatus+"' WHERE user_id='"+userId+"'" +
                " AND artifact_id=(SELECT artifact_id FROM artifact_store WHERE name='"+artName+"');";
        stmt.executeUpdate(sql);
        con.commit();

        stmt.close();
        con.close();
    }

    @Override
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

    @Override
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

    @Override
    public Group<String> getArtifactGroupNames() throws SQLException {
        Group<String> groupsNames = new Group<>("Group name");

        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT group_name FROM group_names WHERE group_name LIKE 'artifact%'";
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()) {
            String group_name = rs.getString("group_name");
            groupsNames.add(group_name);
        }
        return groupsNames;
    }

    @Override
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
            Integer price = rs.getInt("price");
            group.add(new ArtifactModel(name, descr, price));
        }

        stmt.close();
        rs.close();
        con.close();

        return group;
    }
    @Override
    public void addArtifactGroup(Group<ArtifactModel> group) throws SQLException{
        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = con.createStatement();

        String sql = "INSERT INTO group_names (group_name) VALUES ('" + group.getName() + "');";
        stmt.executeUpdate(sql);

        con.commit();

        stmt.close();
    }

    @Override
    public void addArtifactAdherence(String name, String groupName) throws SQLException {
        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = con.createStatement();

        String sql = "INSERT INTO artifact_associations(artifact_id, group_id) " +
                "VALUES ((SELECT artifact_id FROM artifact_store WHERE name='"+name+"'), " +
                "(SELECT group_id FROM group_names WHERE group_name='"+groupName+"'));";
        stmt.executeUpdate(sql);

        con.commit();

        stmt.close();
        con.close();

    }


}
