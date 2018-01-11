class ArtifactStoreController{
    public void addNewArtifact(){
        ArtifactStoreView view = new ArtifactStoreView();
        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        String description = view.getStringFromUserInput(view.artifactDescriptionQuestion);
        String priceStr = view.getStringFromUserInput(view.artifactPriceQuestion);
        float price = priceStr.parsefloat();
        ArtifactModel artifact = new ArtifactModel(name, price, description);
        ArtiffactDaoImpl dao = new ArtiffactDaoImpl();
        Group<String> group = dao.getArtifactGroupNames();
        Iterator<String> iter = group.getIterator();
        while(iter.hasNext()){
          System.out.println(iter.next());
        }
        assignArtifactToCategory();
    }

    public void buyArtifact(String name, Group<User> consumers){
        ArtiffactDaoImpl dao = new ArtiffactDaoImpl();
        ArtifactModel artifact = dao.getArtifact(name);
        float priceDivider = consumers.size();
        float price = artifact.getPrice() / priceDivider;
        Iterator<User> iter = consumers.getIterator();
        int acceptedPaymentCount = 0;
        while(iter.hasNext()){
          if(iter.next().getWallet().canAfford()){
            acceptedPaymentCount++;
          }
        }
        if(acceptedPaymentCount == consumers.size()){
          while(iter.hasNext()){
            iter.next.getWallet().withdraw(price);
          }
        }
        else{
          system.out.println(view.insufficientFunds);
        }
    }

    public void editArtifact(){
        ArtiffactDaoImpl dao = new ArtiffactDaoImpl();
        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        ArtifactModel artifact = dao.getArtifact();
        String choice = view.getStringFromUserInput(view.artifactEditChoice);
        if (choice == "1"){
          String name = view.getStringFromUserInput(view.artifactNameQuestion);
          artifact.setName(name);
        }
        else if(choice == "2"){
          String description = view.getStringFromUserInput(view.artifactDescriptionQuestion);
          artifact.setDescription(description);
        }
        else if(choice == "3"){
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

    }
}
