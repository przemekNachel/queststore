public class QuestModel {
  private String questName;
  private String questDescription;
  private Float questReward;

  public QuestModel(String name, String desc, Float reward) {
    this.questName = name;
    this.questDescription = desc;
    this.questReward = reward;
  }
}
