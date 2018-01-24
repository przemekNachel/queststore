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

    }

    @Override
    public void deleteQuest(QuestModel quest) throws SQLException {

    }

    @Override
    public QuestModel getQuest(String name) throws SQLException {
        return null;
    }

    @Override
    public Group<Group<QuestModel>> getAllQuests() throws SQLException {

        return null;
    }
}
