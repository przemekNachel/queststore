package Quest;

public class QuestModel {
  private String questName;
  private String questDescription;
  private Integer questReward;

  public QuestModel(String name, String desc, Integer reward) {
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

    public void setReward(Integer reward) {
        this.questReward = reward;
  }

  public Integer getReward() {
    return questReward;
  }

  public void setDescription(String description) {
    this.questDescription = description;
  }

  public String getDescription() {
    return questDescription;
  }
}
