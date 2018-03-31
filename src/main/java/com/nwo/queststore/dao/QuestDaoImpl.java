package main.java.com.nwo.queststore.dao;

import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.model.QuestModel;

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
                "VALUES ('"+questName+"', '"+questDescr+"', '"+questReward+"');");

        stmt.executeUpdate(sql);
        con.commit();

        stmt.close();
        con.close();
    }

    @Override
    public void updateQuest(QuestModel quest) throws SQLException {
        Connection con = connectToDatabase();
        Statement stmt = con.createStatement();
        Objects.requireNonNull(con).setAutoCommit(false);

        String questName = quest.getName();
        String questDescr = quest.getDescription();
        Integer questReward = quest.getReward();

        String sql = ("UPDATE quest_store SET "+
                "descr='"+questDescr+"', "+
                "reward='"+questReward+"' "+
                "WHERE name='" + questName+ "';");

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
    public GroupModel<GroupModel<QuestModel>> getAllQuests() throws SQLException {
        GroupModel<GroupModel<QuestModel>> allQuests = new GroupModel<>("All quests");
        GroupModel<String> groupModelNames = getQuestGroupNames();
        Iterator<String> groupNamesIter = groupModelNames.getIterator();

        while(groupNamesIter.hasNext()) {
            allQuests.add(getQuestGroup(groupNamesIter.next()));
        }

        return allQuests;
    }

    @Override
    public GroupModel<String> getQuestGroupNames() throws SQLException {
        GroupModel<String> groupsNames = new GroupModel<>("generic_group.GroupModel name");

        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT group_name FROM group_names WHERE group_name LIKE 'quest%';";
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()) {
            String group_name = rs.getString("group_name");
            groupsNames.add(group_name);
        }
        return groupsNames;
    }

    @Override
    public GroupModel<QuestModel> getQuestGroup(String groupName) throws SQLException {
        GroupModel<QuestModel> groupModel = new GroupModel<>(groupName);

        Connection con = connectToDatabase();
        Statement stmt = Objects.requireNonNull(con).createStatement();

        String sql = "SELECT\n" +
                "  quest_store.quest_id, name, descr, reward, group_names.group_name\n" +
                "FROM\n" +
                "  quest_store\n" +
                "  INNER JOIN quest_associations ON quest_associations.association_id = quest_store.quest_id\n" +
                "  INNER JOIN group_names ON group_names.group_id = quest_associations.group_id\n" +
                " WHERE group_name='" + groupName + "';";

        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()) {
            String questName = rs.getString("name");
            String questDescr = rs.getString("descr");
            Integer questReward = rs.getInt("reward");
            groupModel.add(new QuestModel(questName, questDescr, questReward));
        }

        stmt.close();
        rs.close();
        con.close();

        return groupModel;
    }
    @Override
    public void addQuestAdherence(String name, String groupName) throws SQLException {
        Connection con = connectToDatabase();
        Objects.requireNonNull(con).setAutoCommit(false);
        Statement stmt = con.createStatement();

        int groupId = getGroupId(con, groupName);

        Statement checkStatement = con.createStatement();
        String query = "SELECT quest_id FROM quest_associations WHERE group_id='" + groupId + "';";
        ResultSet checkRecord = checkStatement.executeQuery(query);
        boolean addNew = !checkRecord.next();
        checkStatement.close();

        if(addNew) {

            String sql = "INSERT INTO quest_associations(quest_id, group_id) " +
                    "VALUES ((SELECT quest_id FROM quest_store WHERE name='"+name+"'), " +
                    "(SELECT group_id FROM group_names WHERE group_name='"+groupName+"'));";
            stmt.executeUpdate(sql);
        }

        con.commit();

        stmt.close();
        con.close();

    }

    private int getGroupId(Connection connection, String groupName) throws SQLException{

        Statement statement = connection.createStatement();
        ResultSet results = null;
        int id = -1;
        try {

            String query = "SELECT group_id FROM group_names WHERE group_name='"
                    + groupName + "';";
            results = statement.executeQuery(query);

            if(results.next()){
                id = results.getInt("group_id");
            }
        } finally {

            results.close();
            statement.close();
        }
        return id;
    }
}
