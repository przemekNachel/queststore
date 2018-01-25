package Quest;

import GenericGroup.Group;

import java.sql.Connection;
import java.sql.SQLException;

public interface QuestDao {
    Connection connectToDatabase() throws SQLException;
    Group<Group<QuestModel>> getAllQuests() throws SQLException;
    Group<String> getQuestGroupNames() throws SQLException;
    Group<QuestModel> getQuestGroup(String groupName) throws SQLException;
    QuestModel getQuest(String name) throws SQLException;
    void addQuest(QuestModel quest) throws SQLException;
    void updateQuest(QuestModel quest) throws SQLException;
    void deleteQuest(QuestModel quest) throws SQLException;
}
