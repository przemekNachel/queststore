package user.codecooler;

import console.menu.AbstractConsoleView;
import console.menu.Menu;

public class CodecoolerView extends AbstractConsoleView {

    public String artifactNameQuestion = "Provide artifact name: ";
    public String artifactNoSuch = "You do not have such artifact";

    public CodecoolerView(Menu menu) {
        this.menu = menu;
    }
}