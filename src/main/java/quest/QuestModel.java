package quest;

public class QuestModel {
    private String questName;
    private String questDescription;
    private Integer questReward;

    public QuestModel(String name, String desc, Integer reward) {
        this.questName = name;
        this.questDescription = desc;
        this.questReward = reward;
    }

    public String getName() {
        return questName;
    }

    public void setName(String name) {
        this.questName = name;
    }

    public Integer getReward() {
        return questReward;
    }

    public void setReward(Integer reward) {
        this.questReward = reward;
    }

    public String getDescription() {
        return questDescription;
    }

    public void setDescription(String description) {
        this.questDescription = description;
    }
}
