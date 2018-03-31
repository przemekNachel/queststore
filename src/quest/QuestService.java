package quest;

import exceptionlog.ExceptionLog;
import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.model.QuestModel;

import java.sql.SQLException;

public class QuestService {

    public GroupModel<String> getQuestNames() {

        GroupModel<String> names = new GroupModel<>("quest names");
        for (GroupModel<QuestModel> subGroupModel : getAllQuests()) {

            for (QuestModel quest : subGroupModel) {

                names.add(quest.getName());
            }
        }
        return names;
    }

    public GroupModel<GroupModel<QuestModel>> getAllQuests() {

        GroupModel<GroupModel<QuestModel>> questCollection = new GroupModel<>("all quests");
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

    public GroupModel<String> getQuestGroupNames() {

        GroupModel<String> questGroupModelNames = new GroupModel<>("quest group names");
        try {

            questGroupModelNames = new QuestDaoImpl().getQuestGroupNames();
            questGroupModelNames.remove("quests");
        } catch (SQLException e) {

            ExceptionLog.add(e);
        }
        return questGroupModelNames;
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
