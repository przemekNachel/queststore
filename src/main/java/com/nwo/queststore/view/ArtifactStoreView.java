package main.java.com.nwo.queststore.view;

public class ArtifactStoreView extends AbstractConsoleView {

    public ArtifactStoreView() {
        // a no-menuModel view constructor - not all uses require a menuModel
    }

    public String artifactNameQuestion = "Enter the artifact's name: ";
    public String insufficientFunds = "Insufficient funds";
    public String productsMessage = "Artifacts by category:";
    public String artifactNotFoundError = "No such artifact found!";
    public String chooseGroup = "Choose group, or type ALONE to buy as one user: ";
    public String invalidGroupName = "The provided group name does not exist or is invalid.";
    public String magicExitString = "EXIT";
    public String abortHint = "Type " + magicExitString + " anywhere during the purchase process to abort.";
}
