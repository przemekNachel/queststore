import java.sql.*;
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
                "price='"+questReward+"' "+
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
    public Group<Group<QuestModel>> getAllQuests() throws SQLException {

        return null;
    }
}
