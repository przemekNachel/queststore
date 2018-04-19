package quest;

import generic_group.Group;

import java.sql.*;
import java.util.Iterator;
import java.util.Objects;


public class QuestDaoImpl implements QuestDao {
    @Override
    public Connection connectToDatabase() throws SQLException {
        String db_path = "jdbc:sqlite:database/database.db";
        return DriverManager.getConnection(db_path);
    }

    @Override
    public void addQuest(QuestModel quest) throws SQLException {
        Connection con = connectToDatabase();
        Statement stmt = con.createStatement();
        Objects.requireNonNull(con).setAutoCommit(false);

        String questName = quest.getName();
        String questDescr = quest.getDescription();
        Integer questReward = quest.getReward();

        String sql = ("INSERT INTO quest_store (name, descr, reward)" +
                "VALUES ('" + questName + "', '" + questDescr + "', '" + questReward + "');");

        stmt.executeUpdate(sql);
        con.commit();

        stmt.close();
        con.close();
    }

    @Override
    public void updateQuest(QuestModel quest, String previousName) throws SQLException {
        Connection con = connectToDatabase();
        Statement stmt = con.createStatement();
        Objects.requireNonNull(con).setAutoCommit(false);

        String questName = quest.getName();
        String questDescr = quest.getDescription();
        Integer questReward = quest.getReward();

        String sql = ("UPDATE quest_store SET " +
                "name='" + questName + "' " +
                "descr='" + questDescr + "', " +
                "reward='" + questReward + "' " +
                "WHERE name='" + previousName + "';");

        stmt.executeUpdate(sql);
        con.commit();

        stmt.close();
        con.close();
    }

    @Override
    public void deleteQuest(QuestModel quest) throws SQLException {
        Connection con = connectToDatabase();
        Statement stmt = con.createStatement();
        Objects.requireNonNull(con).setAutoCommit(false);

        String sql = "DELETE from quest_store WHERE name='" + quest.getName() + "';";

        stmt.executeUpdate(sql);
        con.commit();

        stmt.close();
        con.close();
    }

    @Override
    public QuestModel getQuest(String name) throws SQLException {
        Connection con = connectToDatabase();
        Statement stmt = con.createStatement();

        String sql = "SELECT * FROM quest_store WHERE name='" + name + "';";

        ResultSet rs = stmt.executeQuery(sql);

        String questName = rs.getString("name");
        String questDescr = rs.getString("descr");
        Integer questReward = rs.getInt("reward");

        stmt.close();
        rs.close();
        con.close();

        return new QuestModel(questName, questDescr, questReward);
    }


    @Override
    public Group<Group<QuestModel>> getAllQuests() throws SQLException {
        Group<Group<QuestModel>> allQuests = new Group<>("All quests");
        Group<String> groupNames = getQuestGroupNames();
        Iterator<String> groupNamesIter = groupNames.getIterator();

        while (groupNamesIter.hasNext()) {
            allQuests.add(getQuestGroup(groupNamesIter.next()));
        }

        return allQuests;
    }

    @Override
    public Group<String> getQuestGroupNames() throws SQLException {
        Group<String> groupsNames = new Group<>("generic_group.Group name");

        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT group_name FROM group_names WHERE group_name LIKE 'quest%';";
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String group_name = rs.getString("group_name");
            groupsNames.add(group_name);
        }
        return groupsNames;
    }

    @Override
    public Group<QuestModel> getQuestGroup(String groupName) throws SQLException {
        Group<QuestModel> group = new Group<>(groupName);

        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT " +
                "quest_store.quest_id, name, descr, reward, group_names.group_name " +
                "FROM " +
                "quest_store " +
                "INNER JOIN quest_associations ON quest_associations.quest_id = quest_store.quest_id " +
                "INNER JOIN group_names ON group_names.group_id = quest_associations.group_id " +
                " WHERE group_name='" + groupName + "';";

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String questName = rs.getString("name");
            String questDescr = rs.getString("descr");
            Integer questReward = rs.getInt("reward");
            group.add(new QuestModel(questName, questDescr, questReward));
        }

        stmt.close();
        rs.close();
        con.close();

        return group;
    }

    @Override
    public void addQuestAdherence(String questName, String groupName) throws SQLException {
        Connection con = connectToDatabase();

        int questId;
        int groupId;
        con.setAutoCommit(false);

        String questIdSql = "SELECT quest_id FROM quest_store WHERE name LIKE ?;";
        String groupIdSql = "SELECT group_id FROM group_names WHERE group_name LIKE ?;";
        String insertSql = "INSERT INTO quest_associations(quest_id, group_id) VALUES(?, ?);";
        String insertToQuestGroupSql = "INSERT INTO quest_associations(quest_id, group_id) VALUES(?, 3);";

        PreparedStatement questIdQuery = con.prepareStatement(questIdSql);
        questIdQuery.setString(1, questName);
        ResultSet questIdRs = questIdQuery.executeQuery();
        questIdRs.next();
        questId = questIdRs.getInt("quest_id");

        PreparedStatement groupIdQuery = con.prepareStatement(groupIdSql);
        groupIdQuery.setString(1, groupName);
        ResultSet groupIdRs = groupIdQuery.executeQuery();
        groupIdRs.next();
        groupId = groupIdRs.getInt("group_id");

        PreparedStatement insertQuery = con.prepareStatement(insertSql);
        insertQuery.setInt(1, questId);
        insertQuery.setInt(2, groupId);
        insertQuery.executeUpdate();

        PreparedStatement insertIntouestGroupQuery = con.prepareStatement(insertToQuestGroupSql);
        insertIntouestGroupQuery.setInt(1, questId);
        insertIntouestGroupQuery.executeUpdate();

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
