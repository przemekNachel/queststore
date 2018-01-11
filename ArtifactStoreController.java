import java.util.Iterator;

class ArtifactStoreController{
    public void addNewArtifact(){
        ArtifactStoreView view = new ArtifactStoreView();
        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        String description = view.getStringFromUserInput(view.artifactDescriptionQuestion);
        String priceStr = view.getStringFromUserInput(view.artifactPriceQuestion);
        float price = 2f;
        ArtifactModel artifact = new ArtifactModel(name, description, price);
        assignArtifactToCategory(artifact);
    }

    public ArtifactModel buyArtifact(String name, Group<User> consumers){
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();
        ArtifactModel artifact = dao.getArtifact(name);
        float priceDivider = consumers.size();
        float price = artifact.getPrice() / priceDivider;
        Iterator<User> iter = consumers.getIterator();
        int acceptedPaymentCount = 0;
        //while(iter.hasNext()){
        // if(iter.next().getWallet().canAfford()){
          //  acceptedPaymentCount++;
          //}
        //}
        //if(acceptedPaymentCount == consumers.size()){
          //while(iter.hasNext()){
           // iter.next().getWallet().withdraw(price);
            //return artifact;
          //}
        //}
        //else{
          //System.out.println(view.insufficientFunds);
        return null;
        }

    public void editArtifact(){
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();
        String artName = view.getStringFromUserInput(view.artifactNameQuestion);
        ArtifactModel artifact = dao.getArtifact(artName);
        String choice = view.getStringFromUserInput(view.artifactEditChoice);
        if (choice.equals("1")){
          String name = view.getStringFromUserInput(view.artifactNameQuestion);
          artifact.setName(name);
        }
        else if(choice.equals("2")){
          String description = view.getStringFromUserInput(view.artifactDescriptionQuestion);
          artifact.setDescription(description);
        }
        else if(choice.equals("3")){
          String priceStr = view.getStringFromUserInput(view.artifactPriceQuestion);
          float price = Float.parseFloat(priceStr);
          artifact.setPrice(price);
        }
        else{
          System.out.println(view.noSuchOption);
        }
    }

    public void createArtifactCategory(){

    }

    public void assignArtifactToCategory(ArtifactModel artifact){
        ArtifactStoreView view = new ArtifactStoreView();
        ArtifactDaoImpl dao = new ArtifactDaoImpl();
        Group<String> group = dao.getArtifactGroupNames();
        Iterator<String> iter = group.getIterator();
        String groups = "Choose group: \n";
        int index = 0;
        while(iter.hasNext()){
            groups += Integer.toString(index) + iter.next() + "\n";
            index++;
        }
        String chosenIndex = view.getStringFromUserInput(view.artifactNameQuestion);
        String chosenGroupName = dao.getArtifactGroupNames().get(Integer.parseInt(chosenIndex));
        dao.addArtifact(artifact, chosenGroupName);


    }
}
