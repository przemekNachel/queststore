package artifact;

import generic_group.Group;

import java.sql.*;
import java.util.Iterator;
import java.util.Objects;


public class ArtifactDaoImpl implements ArtifactDao {

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

        while (groupNamesIter.hasNext()) {
            allArtifacts.add(getArtifactGroup(groupNamesIter.next()));
        }

        return allArtifacts;
    }

    @Override
    public Group<ArtifactModel> getUserArtifacts(int userId) throws SQLException {
        Group<ArtifactModel> group = new Group<>("User's artifacts: ");
        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT name, descr, price, used FROM artifact_store " +
                "JOIN user_artifacts USING (artifact_id) " +
                "WHERE user_id=" + userId + "";

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String artName = rs.getString("name");
            String artDescr = rs.getString("descr");
            Integer artPrice = rs.getInt("price");
            ArtifactModel artifact = new ArtifactModel(artName, artDescr, artPrice);
            artifact.setUsageStatus(Boolean.valueOf(rs.getString("used")));
            group.add(artifact);
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
                "VALUES('" + artName + "', '" + artDesc + "', '" + artPrice + "');");
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
        Statement checkStmt = Objects.requireNonNull(con).createStatement();

        // check if there is a record in the first place
        String check = "SELECT artifact_id FROM user_artifacts WHERE user_id='" + userId + "'" +
                " AND artifact_id=(SELECT artifact_id FROM artifact_store WHERE name='" + artName + "');";

        ResultSet artifactRecord = checkStmt.executeQuery(check);

        boolean shouldAddNew = !artifactRecord.next();
        checkStmt.close(); // this close *has* to be after the above line

        if (shouldAddNew) {

            /* add new record */
            String getIdQuery = "SELECT artifact_id FROM artifact_store WHERE name='" + artName + "';";
            Statement idStmt = con.createStatement();
            artifactRecord = idStmt.executeQuery(getIdQuery);
            int artifactID = artifactRecord.getInt("artifact_id");

            String add = "INSERT INTO user_artifacts (user_id, artifact_id, used)" +
                    "VALUES('" + userId + "', '" + artifactID + "', '" + artStatus + "');";

            idStmt.executeUpdate(add);
            idStmt.close();
        } else {

            /* update */
            Statement updateStmt = con.createStatement();
            String sql = "UPDATE user_artifacts SET used='" + artStatus + "' WHERE user_id='" + userId + "'" +
                    " AND artifact_id=(SELECT artifact_id FROM artifact_store WHERE name='" + artName + "');";
            updateStmt.executeUpdate(sql);
            updateStmt.close();
        }
        con.commit();

        con.close();
    }

    @Override
    public void updateArtifact(ArtifactModel artifact) throws SQLException {
        String artName = artifact.getName();
        String artDesc = artifact.getDescription();
        float artPrice = artifact.getPrice();

        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = con.createStatement();

        String sql = ("UPDATE artifact_store SET " +
                "descr='" + artDesc + "', " +
                "price='" + artPrice + "' " +
                "WHERE name='" + artName + "';");

        stmt.executeUpdate(sql);
        con.commit();

        stmt.close();
        con.close();


    }

    @Override
    public void deleteArtifact(ArtifactModel artifact) throws SQLException {
        String artName = artifact.getName();

        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = con.createStatement();

        String sql = "DELETE from artifact_store WHERE name='" + artName + "';";

        stmt.executeUpdate(sql);
        con.commit();

        stmt.close();
        con.close();
    }

    @Override
    public Group<String> getArtifactGroupNames() throws SQLException {
        Group<String> groupsNames = new Group<>("Group name");

        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT group_name FROM group_names WHERE group_name LIKE 'artifact%'";
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String group_name = rs.getString("group_name");
            groupsNames.add(group_name);
        }
        return groupsNames;
    }

    @Override
    public Group<ArtifactModel> getArtifactGroup(String groupName) throws SQLException {
        Group<ArtifactModel> group = new Group<>(groupName);

        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT name, descr, price, group_name AS type FROM artifact_store " +
                "JOIN artifact_associations ON artifact_store.artifact_id = artifact_associations.artifact_id " +
                "JOIN group_names ON artifact_associations.group_id = group_names.group_id " +
                "WHERE type != 'artifacts' AND type = '" + groupName + "';";

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
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
    public void addArtifactGroup(Group<ArtifactModel> group) throws SQLException {
        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = con.createStatement();

        String sql = "INSERT INTO group_names (group_name) VALUES ('" + group.getName() + "');";
        stmt.executeUpdate(sql);

        con.commit();

        stmt.close();
    }

    @Override
    public void addArtifactAdherence(String artifactName, String groupName) throws SQLException {
        Connection con = connectToDatabase();

        int artifactId;
        int groupId;

        String artifactIdSql = "SELECT artifact_id FROM artifact_store WHERE name LIKE ?;";
        String groupIdSql = "SELECT group_id FROM group_names WHERE group_name LIKE ?;";
        String insertSql = "INSERT INTO artifact_associations(artifact_id, group_id) VALUES(?, ?);";

        PreparedStatement artifactIdQuery = con.prepareStatement(artifactIdSql);
        artifactIdQuery.setString(1, artifactName);
        ResultSet artifactIdRs = artifactIdQuery.executeQuery();
        artifactIdRs.next();
        artifactId = artifactIdRs.getInt("artifact_id");

        PreparedStatement groupIdQuery = con.prepareStatement(groupIdSql);
        groupIdQuery.setString(1, groupName);
        ResultSet groupIdRs = groupIdQuery.executeQuery();
        groupIdRs.next();
        groupId = groupIdRs.getInt("group_id");

        PreparedStatement insertQuery = con.prepareStatement(insertSql);
        insertQuery.setInt(1, artifactId);
        insertQuery.setInt(2, groupId);
        insertQuery.executeUpdate();

        con.commit();
        con.close();
    }

    private int getGroupId(Connection connection, String groupName) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet results = null;
        int id = -1;
        try {

            String query = "SELECT group_id FROM group_names WHERE group_name='"
                    + groupName + "';";
            results = statement.executeQuery(query);

            if (results.next()) {
                id = results.getInt("group_id");
            }
        } finally {

            results.close();
            statement.close();
        }
        return id;
    }

}
