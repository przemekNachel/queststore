package user.codecooler;

import console.menu.AbstractConsoleView;
import console.menu.Menu;

public class CodecoolerView extends AbstractConsoleView {

    public CodecoolerView(Menu menu) {
        this.menu = menu;
    }

    public String artifactNameQuestion = "Provide artifact name: ";
    public String artifactUseConfirmation = "Confirm artifact usage Y/N: ";
}
