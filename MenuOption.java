public class MenuOption {

    private String optionId;
    private String optionName;

    public MenuOption(String optionId, String optionName) {

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
