package artifact;

import generic_group.Group;

import java.sql.*;
import java.util.Iterator;
import java.util.Objects;


public class ArtifactDaoImpl implements ArtifactDao {

    @Override
    public Connection connectToDatabase() throws SQLException {

        String dbPath = "jdbc:log4jdbc:mysql://54.37.232.83:3306/queststore?user=queststore&password=kuuurla&serverTimezone=UTC&useSSL=false";
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
        stmt.setFetchSize(250);

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
        String sql = "SELECT * FROM artifact_store WHERE name LIKE ?";
        ArtifactModel artifactModel = null;

        try (Connection connection = connectToDatabase()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setFetchSize(250);
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String artifactName = resultSet.getString("name");
                    String artifactDescr = resultSet.getString("descr");
                    Integer artifactReward = resultSet.getInt("price");
                    artifactModel = new ArtifactModel(artifactName, artifactDescr, artifactReward);
                }

            }
        }

        return artifactModel;
    }


    @Override
    public ArtifactModel getArtifactById(int artifactId) throws SQLException {
        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();
        stmt.setFetchSize(250);

        String sql = ("SELECT * FROM artifact_store WHERE artifact_id=" + artifactId + ";");
        ResultSet rs = stmt.executeQuery(sql);


        String artName = null, artDesc = null;
        int artPrice = 0;
        if (rs.next()) {
            System.out.printf("test");
            artName = rs.getString("name");
            artDesc = rs.getString("descr");
            artPrice = rs.getInt("price");
        }

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
        stmt.setFetchSize(250);

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

        checkStmt.setFetchSize(250);

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
            idStmt.setFetchSize(250);
            artifactRecord = idStmt.executeQuery(getIdQuery);
            int artifactID = artifactRecord.getInt("artifact_id");

            String add = "INSERT INTO user_artifacts (user_id, artifact_id, used)" +
                    "VALUES('" + userId + "', '" + artifactID + "', '" + artStatus + "');";

            idStmt.executeUpdate(add);
            idStmt.close();
        } else {

            /* update */
            Statement updateStmt = con.createStatement();
            updateStmt.setFetchSize(250);
            String sql = "UPDATE user_artifacts SET used='" + artStatus + "' WHERE user_id='" + userId + "'" +
                    " AND artifact_id=(SELECT artifact_id FROM artifact_store WHERE name='" + artName + "');";
            updateStmt.executeUpdate(sql);
            updateStmt.close();
        }
        con.commit();

        con.close();
    }

    @Override
    public void updateArtifact(String oldName, ArtifactModel newArtifact) throws SQLException {
        String name = newArtifact.getName();
        String description = newArtifact.getDescription();
        int price = newArtifact.getPrice();

        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);

        String sql = "UPDATE artifact_store SET name = ?, descr = ?, price = ?  WHERE name = ? ";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setFetchSize(250);
        statement.setString(1, name);
        statement.setString(2, description);
        statement.setInt(3, price);
        statement.setString(4, oldName);

        statement.executeUpdate();

        con.commit();
        con.close();


    }

    @Override
    public void deleteArtifact(ArtifactModel artifact) throws SQLException {
        String artName = artifact.getName();

        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = con.createStatement();
        stmt.setFetchSize(250);

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
        stmt.setFetchSize(250);

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

        String sql = "SELECT name, descr, price, group_name FROM artifact_store JOIN artifact_associations ON artifact_store.artifact_id = artifact_associations.artifact_id JOIN group_names ON artifact_associations.group_id = group_names.group_id WHERE group_name != ? AND group_name = ?";

        Connection con = connectToDatabase();
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setFetchSize(250);

        preparedStatement.setString(1, "artifacts");
        preparedStatement.setString(2, groupName);


        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            String descr = rs.getString("descr");
            Integer price = rs.getInt("price");
            group.add(new ArtifactModel(name, descr, price));
        }

        preparedStatement.close();
        rs.close();
        con.close();

        return group;
    }

    @Override
    public void addArtifactGroup(Group<ArtifactModel> group) throws SQLException {
        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = con.createStatement();
        stmt.setFetchSize(250);

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
        con.setAutoCommit(false);

        String artifactIdSql = "SELECT artifact_id FROM artifact_store WHERE name LIKE ?;";
        String groupIdSql = "SELECT group_id FROM group_names WHERE group_name LIKE ?;";
        String insertSql = "INSERT INTO artifact_associations(artifact_id, group_id) VALUES(?, ?);";

        PreparedStatement artifactIdQuery = con.prepareStatement(artifactIdSql);
        artifactIdQuery.setFetchSize(250);
        artifactIdQuery.setString(1, artifactName);
        ResultSet artifactIdRs = artifactIdQuery.executeQuery();
        artifactIdRs.next();
        artifactId = artifactIdRs.getInt("artifact_id");

        PreparedStatement groupIdQuery = con.prepareStatement(groupIdSql);
        groupIdQuery.setFetchSize(250);
        groupIdQuery.setString(1, groupName);
        ResultSet groupIdRs = groupIdQuery.executeQuery();
        groupIdRs.next();
        groupId = groupIdRs.getInt("group_id");

        PreparedStatement insertQuery = con.prepareStatement(insertSql);
        insertQuery.setFetchSize(250);
        insertQuery.setInt(1, artifactId);
        insertQuery.setInt(2, groupId);
        insertQuery.executeUpdate();

        con.commit();
        con.close();
    }

    private int getGroupId(Connection connection, String groupName) throws SQLException {

        Statement statement = connection.createStatement();
        statement.setFetchSize(250);
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
