package main.java.com.nwo.queststore.controller;

import main.java.com.nwo.queststore.model.MenuModel;
import main.java.com.nwo.queststore.model.ArtifactModel;
import main.java.com.nwo.queststore.model.MenuOptionModel;
import main.java.com.nwo.queststore.model.CodecoolerModel;
import main.java.com.nwo.queststore.view.CodecoolerView;
import user.service.UserService;

public class CodecoolerController extends AbstractUserController {
    private CodecoolerView view;
    private CodecoolerModel currentUser;

    public CodecoolerController(CodecoolerModel codecooler) {
        super(new CodecoolerView(
                new MenuModel(
                        new MenuOptionModel("0", "Exit"),
                        new MenuOptionModel("1", "Buy artifact"),
                        new MenuOptionModel("2", "Use artifact"),
                        new MenuOptionModel("3", "View My Info")
                        )
                )
        );
        this.view = (CodecoolerView)super.view;

        userSvc = new UserService();
        this.currentUser = codecooler;
    }

    public void start() {
        boolean requestedExit = false;
        do {
            MenuOptionModel userOption = view.getMenuOptionFromUserInput(" Please choose option: ");
            if (userOption.getId().equals("0")) {
                requestedExit = true;
                view.clearScreen();
            } else {

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
                view.printLine(currentUser.getStatisticsDisplay()
                        + "\nOwned artifacts:\n" + codecoolerArtifactsToString(currentUser)
                        + "\nGroupModel adherence:\n" + currentUser.getCodecoolerGroupDisplay());
                break;
        }
    }

    public void buyArtifact() {
        ArtifactStoreController store = new ArtifactStoreController();
        store.buyProductProcess(currentUser);
        currentUser = (CodecoolerModel)userSvc.getUser(currentUser.getName());
    }

    private String codecoolerArtifactsToString(CodecoolerModel codecooler) {

        String result = "";
        for (ArtifactModel artifact : codecooler.getCodecoolerArtifacts()) {

            result += "  " + artifact.getName() + "  " + (artifact.getUsageStatus() ? "USED" : "NOT USED") +"\n";
        }
        return result;
    }

    public void useArtifact() {

        view.printLine("\n Artifacts to choose from:\n\n" + codecoolerArtifactsToString(currentUser));

        String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);

        ArtifactModel artifact = currentUser.getArtifact(artifactName);

        if (artifact != null) {

            view.printLine("\n  Used artifact " + artifact.getName() + " by " + currentUser.getName());
        } else {

            view.printLine(view.artifactNoSuch);
        }
    }

}
