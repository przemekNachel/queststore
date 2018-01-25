package Artifact;

import Console.AbstractConsoleView;

class ArtifactStoreView extends AbstractConsoleView {

    public ArtifactStoreView() {
        // a no-menu view constructor - not all uses require a menu
    }

    public ArtifactStoreView(Menu menu) {

        this.menu = menu;
    }

    public String artifactNameQuestion = "Enter the artifact's name: ";
    public String artifactDescriptionQuestion = "Provide artifact description: ";
    public String artifactPriceQuestion = "What should be its price?";
    public String artifactPurcahseQuestion;
    public String artifactCategoryQuestion = "Which category should artifact belong to?";
    public String artifactUnavaliable = "This artifact is unavaliable";
    public String insufficientFunds = "Insufficient funds";
    public String artifactEditQuestion = "Which attribute do you want to edit?";
    public String noSuchOption = "No such option";
    public String chooseCategory = "Choose category by index: \n";
    public String productsMessage = "Artifacts by category:";
    public String artifactNotFoundError = "No such artifact found!";
    public String chooseGroup = "Choose group, or type ALONE to buy as one user: ";
    public String shopMaintenanceMsg = "Shop under maintenance. Come back later!";
    public String invalidPrice = "Invalid price.";
    public String invalidGroupName = "The provided group name does not exist or is invalid.";
}
