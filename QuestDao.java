public interface QuestDao {
  public Group<Group<QuestModel>> getAllQuests();
  public QuestModel getQuest(String name);
  public void addQuest(QuestModel quest, String groupName);
  public void updateQuest(QuestModel quest);
  public boolean deleteQuest(QuestModel quest);
}
