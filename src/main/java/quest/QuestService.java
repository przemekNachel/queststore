package quest;

import exceptionlog.ExceptionLog;
import generic_group.Group;

import java.sql.SQLException;

public class QuestService {

    public Group<String> getQuestNames() {

        Group<String> names = new Group<>("quest names");
        for (Group<QuestModel> subGroup : getAllQuests()) {

            for (QuestModel quest : subGroup) {

                names.add(quest.getName());
            }
        }
        return names;
    }

    public Group<Group<QuestModel>> getAllQuests() {

        Group<Group<QuestModel>> questCollection = new Group<>("all quests");
        try {

            questCollection = new QuestDaoImpl().getAllQuests();
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return questCollection;
    }

    public QuestModel getQuestByName(String name) {

        QuestModel quest = null;
        try {
            quest = new QuestDaoImpl().getQuest(name);
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return quest;
    }

    public boolean updateQuest(QuestModel quest) {

        boolean updated = true;
        try {
            new QuestDaoImpl().updateQuest(quest);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            updated = false;
        }
        return updated;
    }

    public boolean createQuest(String name, String desc, Integer reward, String specializedGroupName) {

        boolean created = true;
        QuestModel newQuest = new QuestModel(name, desc, reward);
        try {

            new QuestDaoImpl().addQuest(newQuest);
            addQuestAdherence(newQuest, "artifacts");
            addQuestAdherence(newQuest, specializedGroupName);

        } catch (SQLException e) {
            ExceptionLog.add(e);
            created = false;
        }
        return created;
    }

    public boolean addQuestAdherence(QuestModel quest, String groupName) {

        boolean adherenceAdded = true;
        try {
            new QuestDaoImpl().addQuestAdherence(quest.getName(), groupName);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            adherenceAdded = false;
        }
        return adherenceAdded;
    }

    public Group<String> getQuestGroupNames() {

        Group<String> questGroupNames = new Group<>("quest group names");
        try {

            questGroupNames = new QuestDaoImpl().getQuestGroupNames();
            questGroupNames.remove("quests");
        } catch (SQLException e) {

            ExceptionLog.add(e);
        }
        return questGroupNames;
    }

    public boolean deleteQuest(String name) {

        QuestDaoImpl questDao = new QuestDaoImpl();

        boolean deleted = true;
        try {

            QuestModel quest = questDao.getQuest(name);
            questDao.deleteQuest(quest);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            deleted = false;
        }
        return deleted;
    }

}
