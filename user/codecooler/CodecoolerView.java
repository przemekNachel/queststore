package User.Codecooler;

import Console.AbstractConsoleView;

public class CodecoolerView extends AbstractConsoleView {

    public CodecoolerView(Menu menu) {
        this.menu = menu;
    }

    public String artifactNameQuestion = "Provide artifact name: ";
    public String artifactUseConfirmation = "Confirm artifact usage Y/N: ";
}
