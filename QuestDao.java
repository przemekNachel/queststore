public interface QuestDao {
  public Group<Group<QuestModel>> getAllQuests();
  public QuestModel getQuest(String name);
  public void addQuest(QuestModel quest);
  public void updateQuest(QuestModel quest);
  public void deleteQuest(QuestModel quest);
}
