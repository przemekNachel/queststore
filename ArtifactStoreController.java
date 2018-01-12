import java.util.Iterator;

class ArtifactStoreController{
    public void addNewArtifact(){
        ArtifactStoreView view = new ArtifactStoreView();
        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        String description = view.getStringFromUserInput(view.artifactDescriptionQuestion);
        String priceStr = view.getStringFromUserInput(view.artifactPriceQuestion);
        float price = Float.parseFloat(priceStr);
        ArtifactModel artifact = new ArtifactModel(name, description, price);
        assignArtifactToCategory(artifact);
    }

    public void buyProductProcess(CodecoolerModel user){
        boolean shopUpdated = true;
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();
        UserDaoImpl userDao = new UserDaoImpl();
        if (shopUpdated){
            view.printLine(view.shopMaintenanceMsg);
            return;
        }
        Group<String> names = dao.getArtifactGroupNames();
        Iterator<String> iter = names.getIterator();
        String groupsFormatted = "";

        while(iter.hasNext()){
            String group = iter.next();
            Group<ArtifactModel> arts  = dao.getArtifactGroup(group);
            groupsFormatted += iter.next() + " :\n";
            Iterator<ArtifactModel> iterArtifact = arts.getIterator();
            while(iterArtifact.hasNext()){
                groupsFormatted += "*" + iterArtifact.next() + "\n";
            }
        }
        view.printLine(view.productsMessage);
        view.printLine(groupsFormatted);

        String artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
        Group<User> consumers = userDao.getUserGroup("students");

        Group<CodecoolerModel> converted = new Group<>("Codecoolers");
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
        userDao.updateUser(user);

    }

    public ArtifactModel buyArtifact(String name, Group<CodecoolerModel> consumers) {

        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();
        ArtifactModel artifact = dao.getArtifact(name);
        if(artifact == null){
            return null;
        }
        float priceDivider = consumers.size();
        float price = artifact.getPrice() / priceDivider;
        Iterator<CodecoolerModel> iter = consumers.getIterator();
        int acceptedPaymentCount = 0;
        // check if all consumers can pay
        while (iter.hasNext()) {
            if (iter.next().getWallet().canAfford(price)) {
                acceptedPaymentCount++;
            }
        }

        if (acceptedPaymentCount == consumers.size()) {
            while (iter.hasNext()) {
                iter.next().getWallet().withdraw(price);
                return artifact;
            }
        } else {
            view.printLine(view.insufficientFunds);
            return null;
        }
        return null;
    }

    public void editArtifact(){
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();

        String artName = view.getStringFromUserInput(view.artifactNameQuestion);
        ArtifactModel artifact = dao.getArtifact(artName);

        if(artifact == null){
            view.printLine(view.artifactNotFoundError);
            return;
        }
        String choice = view.getStringFromUserInput(view.artifactEditChoice); //make menu from this


        switch(choice){
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
                float price = Float.parseFloat(priceStr);
                artifact.setPrice(price);
                break;
            default :
                view.printLine(view.noSuchOption);
        }
    }

    public void createArtifactCategory(){
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl artDao = new ArtifactDaoImpl();
        String categoryName = view.getStringFromUserInput(view.artifactCategoryQuestion);

        Group<ArtifactModel> tmp = new Group<>(categoryName);
        artDao.createArtifactGroup(tmp);
    }

    public void assignArtifactToCategory(ArtifactModel artifact){
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();
        Group<String> group = dao.getArtifactGroupNames();
        Iterator<String> iter = group.getIterator();
        view.printLine(view.chooseGroup);
        String groups = "";
        int index = 0;
        while(iter.hasNext()){
            groups += Integer.toString(index) + iter.next() + "\n";
            index++;
        }
        view.printLine(groups);
        String groupName = view.getStringFromUserInput(view.artifactNameQuestion);
        dao.addArtifact(artifact, groupName); // adds artifact to category
                                              // even if category doesnt exist

    }
}
