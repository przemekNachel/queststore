public class QuestModel {
  private String questName;
  private String questDescription;
  private Float questReward;

  public QuestModel(String name, String desc, Float reward) {
    this.questName = name;
    this.questDescription = desc;
    this.questReward = reward;
  }

  public void setName(String name) {
    this.questName = name;
  }

  public String getName() {
    return questName;
  }

  public void setReward(Float reward) {
    this.questReward = reward;
  }

  public Float getReward() {
    return questReward;
  }

  public void setDescription(String description) {
    this.questDescription = description;
  }

  public String getDescription() {
    return questDescription;
  }
}
