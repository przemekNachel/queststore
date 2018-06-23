package quest;

import generic_group.Group;

import java.sql.Connection;
import java.sql.SQLException;

public interface QuestDao {
    Connection connectToDatabase() throws SQLException;

    Group<QuestModel> getQuestGroup(String groupName) throws SQLException;

    QuestModel getQuest(String name) throws SQLException;

    void addQuest(QuestModel quest) throws SQLException;

    void updateQuest(QuestModel quest, String previousName) throws SQLException;

    void deleteQuest(QuestModel quest) throws SQLException;

    void addQuestAdherence(String Name, String groupName) throws SQLException;
}
