package artifact;

public class ArtifactModel {
    private String name;
    private String description;
    private Integer price;
    private boolean hesBeenUsed;

    public ArtifactModel(String name, String description, Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String toString() {
        return this.name + "\n" + this.description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean getUsageStatus() {
        return hesBeenUsed;
    }

    public void setUsageStatus(boolean hesBeenUsed) {
        this.hesBeenUsed = hesBeenUsed;
    }
}
