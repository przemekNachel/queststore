package main.java.com.nwo.queststore.dao;

import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.model.QuestModel;

import java.sql.Connection;
import java.sql.SQLException;

public interface QuestDao {
    Connection connectToDatabase() throws SQLException;
    GroupModel<GroupModel<QuestModel>> getAllQuests() throws SQLException;
    GroupModel<String> getQuestGroupNames() throws SQLException;
    GroupModel<QuestModel> getQuestGroup(String groupName) throws SQLException;
    QuestModel getQuest(String name) throws SQLException;
    void addQuest(QuestModel quest) throws SQLException;
    void updateQuest(QuestModel quest) throws SQLException;
    void deleteQuest(QuestModel quest) throws SQLException;
    void addQuestAdherence(String Name, String groupName) throws SQLException;
}
