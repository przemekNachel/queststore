import java.util.Scanner;

class ArtifactStoreView extends AbstractConsoleView{
    public String artifactNameQuestion = "Enter the artifact's name: ";
    public String artifactDescriptionQuestion = "Provide artifact description: ";
    public String artifactPriceQuestion = "What should be its price?";
    public String artifactPurcahseQuestion;
    public String artifactCategoryQuestion = "Which category should artifact belong to?";
    public String artifactUnavaliable = "This artifact is unavaliable";
    public String insufficientFunds = "Insufficient funds";
    public String artifactEditChoice = "Which attribute do you want to edit: \n 1. Name \n 2. Description \n 3. Price";
    public String noSuchOption = "No such option";
    public String chooseCategory = "Choose category by index: \n";
    public String productsMessage = "Categories:";
    public String artifactNotFoundError = "No such artifact found!";
    public String chooseGroup = "Choose group: ";
}
