public class ArtifactModel {
    private String name;
    private String description;
    private Float price;
    private boolean hesBeenUsed;

    public String toString(){
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public boolean getUsageStatus() {
        return hesBeenUsed;
    }

    public void setUsageStatus(boolean hesBeenUsed) {
        this.hesBeenUsed = hesBeenUsed;
    }
}
