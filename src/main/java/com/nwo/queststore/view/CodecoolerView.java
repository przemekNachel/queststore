package main.java.com.nwo.queststore.view;

import main.java.com.nwo.queststore.model.MenuModel;

public class CodecoolerView extends AbstractConsoleView {

    public CodecoolerView(MenuModel menuModel) {
        this.menuModel = menuModel;
    }

    public String artifactNameQuestion = "Provide artifact name: ";
    public String artifactNoSuch = "You do not have such artifact";
}
