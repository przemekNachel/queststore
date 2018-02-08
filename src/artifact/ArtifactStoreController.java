package artifact;

import console.menu.Menu;
import console.menu.MenuOption;
import generic_group.Group;
import user.codecooler.CodecoolerModel;
import user.service.UserService;
import user.user.User;
import user.user.UserDaoImpl;
import user.wallet.WalletService;

import java.util.Iterator;
import java.sql.*;

public class ArtifactStoreController{

    ArtifactStoreView view;

    public ArtifactStoreController() {

      this.view = new ArtifactStoreView();
    }

    private Group<String> getAllowedArtifactNames() {

        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();

        Group<String> allowedArtifactNames = new Group<>("allowed artifact name user input");
        // get available artifacts by category/ group
        Group<String> artifactGroupNames = null;
        try {

            artifactGroupNames = artifactDao.getArtifactGroupNames();
        } catch (SQLException e) {
            view.printSQLException(e);
            return null;
        }

        for (String groupName : artifactGroupNames) {

            Group<ArtifactModel> artifactGroup = null;
            try {

                artifactGroup = artifactDao.getArtifactGroup(groupName);
            } catch (SQLException e) {

                view.printSQLException(e);
                return null;
            }

            for (ArtifactModel currentArtifact : artifactGroup) {

                allowedArtifactNames.add(currentArtifact.getName());
            }
        }
        return allowedArtifactNames;
    }

    private String getArtifactStoreDisplay() {

        ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();

        // get available artifacts by category/ group
        Group<String> artifactGroupNames = null;
        try {

            artifactGroupNames = artifactDao.getArtifactGroupNames();
        } catch (SQLException e) {
            view.printSQLException(e);
            return null;
        }

        String display = "Available artifacts:\n";
        for (String groupName : artifactGroupNames) {

            Group<ArtifactModel> artifactGroup = null;
            try {

                artifactGroup = artifactDao.getArtifactGroup(groupName);
            } catch (SQLException e) {

                view.printSQLException(e);
                return null;
            }

            display += "\n  Group " + artifactGroup.getName();
            for (ArtifactModel currentArtifact : artifactGroup) {
                display += "\n    " + currentArtifact.getName() + " - " + currentArtifact.getDescription() + " - PRICE: " + currentArtifact.getPrice();
            }
        }

        return display;
    }

    private String getArtifactNameFromUserInput() {

        Group<String> allowedArtifactNames = getAllowedArtifactNames();
        String storeDisplay = getArtifactStoreDisplay();

        view.printLine(view.productsMessage);
        view.printLine(storeDisplay);

        String artifactName;
        boolean providedValidArtifactName = false;
        do {

            artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
            if (allowedArtifactNames.contains(artifactName) || artifactName.equals(view.magicExitString)) {

                providedValidArtifactName = true;
            } else {
                view.printLine(view.artifactNotFoundError);
            }

        } while(!providedValidArtifactName);

        return artifactName;
    }

    private Group<CodecoolerModel> getConsumerGroup(CodecoolerModel codecooler) {

        UserService userSvc = new UserService();

        // get all user groups to choose from and display them
        Group<String> allowedGroupNames = codecooler.getAssociatedGroupNames();

        // get group which will crowd-fund the artifact
        Group<CodecoolerModel> codecoolers = null;
        boolean providedExistentGroupName = false;
        boolean wantToBuyAlone = false;
        String groupsDisplay = "\n\n  Groups that can crowd-fund this purchase: \n\n   " + codecooler.getCodecoolerGroupDisplay() + "\n";
        do {
            view.printLine(groupsDisplay);
            String consumerGroupName = view.getStringFromUserInput(view.chooseGroup);
            if (allowedGroupNames.contains(consumerGroupName)) {

                Group<User> users = userSvc.getUserGroup(consumerGroupName);

                codecoolers = new Group<>("Codecooler(s) buying an artifact");
                for (User currentUser : users) {

                    codecoolers.add((CodecoolerModel)currentUser);
                }
                providedExistentGroupName = true;

            } else {
                if (consumerGroupName.equals("ALONE")) {

                    codecoolers = new Group<>("buying alone");
                    codecoolers.add(codecooler);
                    wantToBuyAlone = true;
                } else if (consumerGroupName.equals(view.magicExitString)) {
                    return null;
                } else {

                    view.printLine(view.invalidGroupName);
                }
            }
        } while(!providedExistentGroupName && !wantToBuyAlone);

        return codecoolers;
    }

    public void buyProductProcess(CodecoolerModel codecooler){

        view.printLine(view.abortHint);
        String artifactName = getArtifactNameFromUserInput();
        if (artifactName.equals(view.magicExitString)) {

            return;
        }

        Group<CodecoolerModel> consumers = getConsumerGroup(codecooler);
        if (consumers == null) {
            /* purchase process aborted as requested by user */
            return;
        }
        CodecoolerModel buyer = getSelf(consumers, codecooler);
        /* the above is necessary for proper object to update */

        ArtifactModel boughtArtifact = buyArtifact(artifactName, consumers);

        if (boughtArtifact == null) {

            return;
        }
        /* purchase process succeeded */
        buyer.addArtifact(boughtArtifact);
        UserService userSvc = new UserService();
        for (User user : consumers) {

            userSvc.updateUser(user);
        }
    }

    /* reason for the below method: a getter for consumer group gets the same codecooler and
     * we have extract them because it is their wallet and artifacts that have supposedly been changed */
    private CodecoolerModel getSelf(Group<CodecoolerModel> consumers, CodecoolerModel twin) {

        for (CodecoolerModel codecooler : consumers) {

            if (codecooler.getName().equals(twin.getName())) {

                return codecooler;
            }
        }
        return null;
    }

    public ArtifactModel buyArtifact(String name, Group<CodecoolerModel> consumers) {

        ArtifactDaoImpl dao = new ArtifactDaoImpl();

        ArtifactModel artifact = null;
        try {

            artifact = dao.getArtifactByName(name);
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        if(artifact == null){
            return null;
        }

        Integer divideAmong = consumers.size();
        Integer artifactPrice = artifact.getPrice();
        Integer alignedPrice = artifactPrice - (artifactPrice % divideAmong);
        Integer share = alignedPrice / divideAmong;

        boolean allCanAfford = true;
        for (CodecoolerModel codecooler : consumers) {

            allCanAfford &= codecooler.getWallet().canAfford(share);
        }


        if (!allCanAfford) {

            view.printLine(view.insufficientFunds);
            return null;
        }

        for (CodecoolerModel codecooler : consumers) {

            codecooler.getWallet().withdraw(share);
        }
        return artifact;
    }

    public void editArtifact(){

        Menu editMenu = new Menu(
                new MenuOption("0", "exit"),
                new MenuOption("1", "Name"),
                new MenuOption("2", "Description"),
                new MenuOption("3", "Price")
        );

        ArtifactStoreView view = new ArtifactStoreView(editMenu);
        ArtifactDaoImpl dao = new ArtifactDaoImpl();

        String artName = view.getStringFromUserInput(view.artifactNameQuestion);
        ArtifactModel artifact = null;
        try {

            artifact = dao.getArtifactByName(artName);
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        if(artifact == null){
            view.printLine(view.artifactNotFoundError);
            return;
        }

        boolean requestedExit = false;
        while (!requestedExit) {

            String choice = view.getStringFromUserInput(view.artifactEditQuestion); //make menu from this
            MenuOption userOption = view.getMenuOptionFromUserInput(" Please choose option: ");
            switch(userOption.getId()){
                case "0":
                    requestedExit = true;
                    break;

                case "1":
                    String name = view.getStringFromUserInput(view.artifactNameQuestion);
                    artifact.setName(name);
                    break;

                case "2":
                    String description = view.getStringFromUserInput(view
                            .artifactDescriptionQuestion);
                    artifact.setDescription(description);
                    break;

                case "3":
                    String priceStr = view.getStringFromUserInput(view
                            .artifactPriceQuestion);
                    Integer price = Integer.parseInt(priceStr);
                    artifact.setPrice(price);
                    break;

                default :
                    view.printLine(view.noSuchOption);
            }
        }
    }

    public void createArtifactCategory(){
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl artDao = new ArtifactDaoImpl();
        String categoryName = view.getStringFromUserInput(view.artifactCategoryQuestion);

        Group<ArtifactModel> newGroup = new Group<>(categoryName);
        try {

            artDao.addArtifactGroup(newGroup);
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

}
