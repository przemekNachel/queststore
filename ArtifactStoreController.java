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
        float price = Float.parseFloat(priceStr);
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

    }

    public  void editArtifact(){

    }

    public void createArtifactCategory(){

    }

    public void assignArtifactToCategory(ArtifactModel artifact){

    }
}
