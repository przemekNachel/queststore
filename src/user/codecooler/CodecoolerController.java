package user.codecooler;

import artifact.ArtifactStoreController;
import console.menu.Menu;
import console.menu.MenuOption;
import user.user.UserDaoImpl;

public class CodecoolerController {
    public CodecoolerView view;
    public CodecoolerModel currentUser;

    public CodecoolerController(CodecoolerModel codecooler) {
        this.currentUser = codecooler;
        Menu codecoolerMenu = new Menu(
                new MenuOption("0", "Exit"),
                new MenuOption("1", "Buy artifact"),
                new MenuOption("2", "Use artifact"),
                new MenuOption("3", "View My Info")
        );

        view = new CodecoolerView(codecoolerMenu);
    }

    public void start() {
        boolean requestedExit = false;
        do {
            MenuOption userOption = view.getMenuOptionFromUserInput(" Please choose option: ");
            if (userOption.getId().equals("0")) {
                requestedExit = true;
                view.clearScreen();
            } else {

                String chosenOption = userOption.getId();
                handleUserChoice(userOption.getId());
            }
        } while (!requestedExit);
    }

    private void handleUserChoice(String userChoice) {

        switch (userChoice) {
            case "1":
                buyArtifact();
                break;
            case "2":
                useArtifact();
                break;
            case "3":
                view.printLine(currentUser.toString());
                break;
        }
    }

    public void buyArtifact() {
        UserDaoImpl userDao = new UserDaoImpl();
        ArtifactStoreController store = new ArtifactStoreController();
        store.buyProductProcess(currentUser);
    }

    public void useArtifact() {
        String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
        currentUser.getArtifact(artifactName);
        view.printLine("artifact has been used!");
    }

}
