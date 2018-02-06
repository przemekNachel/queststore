package artifact;

import console.menu.Menu;
import console.menu.MenuOption;
import generic_group.Group;
import user.codecooler.CodecoolerModel;
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

        UserDaoImpl userDao = new UserDaoImpl();

        // get all user groups to choose from and display them
        Group<String> allowedGroupNames = null;

        try{
            allowedGroupNames = userDao.getUserGroupNames();
        } catch (SQLException e) {
            view.printSQLException(e);
            return null;
        }

        // get group which will crowd-fund the artifact
        Group<CodecoolerModel> codecoolers = null;
        boolean providedExistentGroupName = false;
        boolean wantToBuyAlone = false;
        do {
            String consumerGroupName = view.getStringFromUserInput(view.chooseGroup);
            if (allowedGroupNames.contains(consumerGroupName)) {
                try{
                    Group<User> users = userDao.getUserGroup(consumerGroupName);
                    // buy the artifact as a group
                    codecoolers = new Group<>("Codecooler(s) buying an artifact");
                    for (User currentUser : users) {

                        codecoolers.add((CodecoolerModel)currentUser);
                    }
                } catch (SQLException e) {
                    view.printSQLException(e);
                    return null;
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

        UserDaoImpl userDao = new UserDaoImpl();

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

        ArtifactModel boughtArtifact = buyArtifact(artifactName, consumers);
        if (boughtArtifact == null) {

            return;
        }
        /* purchase process succeeded */
        codecooler.addArtifact(boughtArtifact);
        try {

            for (User user : consumers) {

                userDao.updateUser(user);
            }
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    public ArtifactModel buyArtifact(String name, Group<CodecoolerModel> consumers) {

        ArtifactDaoImpl dao = new ArtifactDaoImpl();

        ArtifactModel artifact = null;
        try {

            artifact = dao.getArtifact(name);
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

        Iterator<CodecoolerModel> iter = consumers.getIterator();
        boolean allCanAfford = true;
        while (iter.hasNext()) {

            WalletService currentWallet = iter.next().getWallet();
            allCanAfford &= currentWallet.canAfford(share);
        }

        if (!allCanAfford) {

            view.printLine(view.insufficientFunds);
            return null;
        }

        iter = consumers.getIterator();
        while (iter.hasNext()) {

            WalletService currentWallet = iter.next().getWallet();
            currentWallet.withdraw(share);
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

            artifact = dao.getArtifact(artName);
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
