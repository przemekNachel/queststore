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

  public void addQuest(QuestModel quest) {

  }
  public void updateQuest(QuestModel quest) {

  }

  public void deleteQuest(QuestModel quest) {

  }
}
