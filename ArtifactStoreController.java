import ArtifactModel;
import ArtifactStoreView;
import ArtifactStoreModel;
import ArtifactDaoImpl;

class ArtifactStoreController{
    public void addNewArtifact(String name, Float price, String description){
        ArtifactStoreView view = new ArtifactStoreView();
        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        String description = view.getStringFromUserInput(view.artifactDescriptionQuestion);
        String priceStr = view.getStringFromUserInput(view.artifactPriceQuestion)
        float price = float.parsefloat(priceStr);
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
          System.out.println(view.noSuchOption)
        }
    }

    public void createArtifactCategory(){

    }

    public void assignArtifactToCategory(ArtifactModel artifact){

    }
}
