import java.sql.Connection;
import java.sql.SQLException;

public interface QuestDao {
    Connection connectToDatabase() throws SQLException;
    Group<Group<QuestModel>> getAllQuests() throws SQLException;
    QuestModel getQuest(String name) throws SQLException;
    void addQuest(QuestModel quest) throws SQLException;
    void updateQuest(QuestModel quest) throws SQLException;
    void deleteQuest(QuestModel quest) throws SQLException;
}
