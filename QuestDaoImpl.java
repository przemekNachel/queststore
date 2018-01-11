import java.util.Iterator;

public class QuestDaoImpl implements QuestDao {
  private static Group<Group<QuestModel>> quests;

  public Group<Group<Quest>> getAllQuests() {
    return quests;
  }

  public QuestModel getQuest(String name) {
    Iterator<Group<QuestModel>> questGroupIterator = QuestDaoImpl.quests.getIterator();
    while(questGroupIterator.hasNext()) {
      Iterator<QuestModel> questsIterator = questGroupIterator.next().getIterator();
      while(questsIterator.hasNext()) {
        QuestModel currentQuest = questsIterator.next();
        if(currentQuest.getName().equals(name)) {
          return currentQuest;
        }
      }
    }
    return null;
  }

  public void addQuest(QuestModel quest, String groupName) {
    boolean questAdded = false;
    Iterator<Group<QuestModel>> allGroupsIterator = QuestDaoImpl.quests.getIterator();
    while(allGroupsIterator.hasNext()) {
      Group<QuestModel> questGroup = allGroupsIterator.next();
      String questGroupname = questGroup.getName();
      if(questGroupName.equals(groupName)) {
        questGroup.add(quest);
        questAdded = true;
      }
    }if(!questAdded) {
      Group<QuestModel> tmp = new Group<>(groupName);
      tmp.add(quest);
      quests.add(tmp);
    }
    questAdded = false;
  }
  public void updateQuest(QuestModel quest) {
    Iterator<Group<QuestModel>> questGroupIterator = QuestDaoImpl.artifact.getIterator();
    while(questGroupIterator.hasNext()) {
      Group<QuestModel> questGroup = questGroupIterator.next();
      Iterator<QuestModel> questIterator = questGroup.getIterator();
      while(questIterator.hasNext()) {
        QuestModel currentQuest = questIterator.next();
        if(currentQuest.getName().equals(quest.getName())) {
          questGroup.remove(currentQuest);
          questGroup.add(quest);
        }
      }
    }

  }

  public void deleteQuest(QuestModel quest) {

  }
}
