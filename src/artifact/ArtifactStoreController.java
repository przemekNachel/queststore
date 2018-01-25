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

    public void addNewArtifact(){
        ArtifactStoreView view = new ArtifactStoreView();

        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        String description = view.getStringFromUserInput(view.artifactDescriptionQuestion);

        boolean providedValidPrice;
        Integer price = null;
        do {

            try {

                providedValidPrice = true;
                String priceStr = view.getStringFromUserInput(view.artifactPriceQuestion);
                price = Integer.parseInt(priceStr);

            } catch (NumberFormatException nfe) {
                providedValidPrice = false;
                view.printLine(view.invalidPrice);
            }

        } while(!providedValidPrice);

        ArtifactModel artifact = new ArtifactModel(name, description, price);
        assignArtifactToCategory(artifact);
    }

    public void buyProductProcess(CodecoolerModel user){

        boolean shopUpdated = true;
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();
        UserDaoImpl userDao = new UserDaoImpl();

        System.out.println(user.getStatisticsDisplay());

        Group<String> allowedArtifactNames = new Group<>("allowed artifact name user input");
        Group<String> artifactGroupNames = null;
        try {

            artifactGroupNames = dao.getArtifactGroupNames();
        } catch (SQLException e) {
            view.printSQLException(e);
        }

        Iterator<String> iter = artifactGroupNames.getIterator();
        String groupsFormatted = "";
        while (iter.hasNext()) {

            String groupName = iter.next();
            Group<ArtifactModel> artifactGroup = null;
            try {

                artifactGroup = dao.getArtifactGroup(groupName);
            } catch (SQLException e) {

                view.printSQLException(e);
            }

            groupsFormatted += groupName + " :\n";
            Iterator<ArtifactModel> iterArtifact = artifactGroup.getIterator();
            while (iterArtifact.hasNext()) {
                ArtifactModel currentArtifact = iterArtifact.next();
                groupsFormatted += "*" + currentArtifact + "\n";
                allowedArtifactNames.add(currentArtifact.getName());
            }
        }

        view.printLine(view.productsMessage);
        view.printLine(groupsFormatted);

        String artifactName;
        boolean providedValidArtifactName = false;
        do {

            artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
            if (allowedArtifactNames.contains(artifactName)) {

                providedValidArtifactName = true;
            } else {
                view.printLine(view.artifactNotFoundError);
            }

        } while(!providedValidArtifactName);

        Group<String> allowedGroupNames = null;

        try{
            allowedGroupNames = userDao.getUserGroupNames();
        } catch (SQLException e) {
            view.printSQLException(e);

        }
        String availableGroups = allowedGroupNames.toString();
        view.printLine(availableGroups);

        Group<User> consumers = null;
        boolean providedExistentGroupName = false;
        boolean wantToBuyAlone = false;
        do {
            String consumerGroupName = view.getStringFromUserInput(view.chooseGroup);
            if (allowedGroupNames.contains(consumerGroupName)) {
                try{
                    consumers = userDao.getUserGroup(consumerGroupName);
                } catch (SQLException e) {
                    view.printSQLException(e);
                    return;
                }
                providedExistentGroupName = true;
            } else {
                if (consumerGroupName.equals("ALONE")) {

                    consumers = new Group<>("buying alone");
                    consumers.add(user);
                    wantToBuyAlone = true;
                } else {
                    view.printLine(view.invalidGroupName);
                }
            }

        } while(!providedExistentGroupName && !wantToBuyAlone);

        Group<CodecoolerModel> converted = new Group<>("user.codecooler(s) buying an artifact");
        Iterator<User> iterUser = consumers.getIterator();
        while (iterUser.hasNext()) {
            converted.add((CodecoolerModel)iterUser.next());
        }

        ArtifactModel boughtArtifact = buyArtifact(artifactName, converted);
        if (boughtArtifact == null){
            view.printLine(view.artifactNotFoundError);
            return;
        }
        user.addArtifact(boughtArtifact);

        try {

            userDao.updateUser(user);
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }

    public ArtifactModel buyArtifact(String name, Group<CodecoolerModel> consumers) {

        ArtifactStoreView view = new ArtifactStoreView();
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

    public void assignArtifactToCategory(ArtifactModel artifact){
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();

        Group<String> possibleGroupNames = null;
        try {

            possibleGroupNames = dao.getArtifactGroupNames();
        } catch (SQLException e) {

            view.printSQLException(e);
        }

        Iterator<String> iter = possibleGroupNames.getIterator();
        view.printLine(view.chooseGroup);

        String groupsToChooseFrom = "";
        int index = 0;
        while(iter.hasNext()){
            groupsToChooseFrom += Integer.toString(index) + " " + iter.next() + "\n";
            index++;
        }

        view.printLine(groupsToChooseFrom);
        String desiredGroupName = view.getStringFromUserInput(view.artifactNameQuestion);
        try {

            dao.addArtifact(artifact, desiredGroupName);
        } catch (SQLException e) {
            view.printSQLException(e);
        }
    }
}