package main.java.com.nwo.queststore.model;

public class MenuOptionModel {

    private String optionId;
    private String optionName;

    public MenuOptionModel(String optionId, String optionName) {

        this.optionId = optionId;
        this.optionName = optionName;
    }

    public String getId() {

        return optionId;
    }

    public String getName() {

        return optionName;
    }

    public String toString() {

        return String.format("%s  %s", optionId, optionName);
    }
}
