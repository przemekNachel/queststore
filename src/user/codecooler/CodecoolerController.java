package user.codecooler;

import artifact.ArtifactStoreController;
import artifact.ArtifactModel;
import console.menu.Menu;
import console.menu.MenuOption;
import user.user.UserDaoImpl;

import java.sql.SQLException;

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
                view.printLine(currentUser.getStatisticsDisplay() + "\n\nGroup adherence:\n" + currentUser.getCodecoolerGroupDisplay());
                break;
        }
    }

    public void buyArtifact() {
        UserDaoImpl userDao = new UserDaoImpl();
        ArtifactStoreController store = new ArtifactStoreController();
        store.buyProductProcess(currentUser);
    }

    private String codecoolerArtifactsToString(CodecoolerModel codecooler) {

        String result = "";
        for (ArtifactModel artifact : codecooler.getCodecoolerArtifacts()) {

            result += "  " + artifact.getName() + "\n";
        }
        return result;
    }

    public void useArtifact() {

        view.printLine("\n Artifacts to choose from:\n\n" + codecoolerArtifactsToString(currentUser));

        String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);

        ArtifactModel artifact = currentUser.getArtifact(artifactName);

        if (artifact != null) {

            artifact.setUsageStatus(true);

            boolean updated = true;
            try {

                new UserDaoImpl().updateUser(currentUser);
            } catch (SQLException e) {

                updated = false;
                view.printSQLException(e);
            }

            if(updated) {

                view.printLine("\n  Used artifact " + artifact.getName() + " by " + currentUser.getName());
            } else {

                view.printLine(view.artifactUsageUpdateFailure);
            }

        } else {

            view.printLine(view.artifactNoSuch);
        }
    }

}
