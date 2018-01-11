import ArtifactModel;
import ArtifactStoreView;
import ArtifactStoreModel;

class ArtifactStoreController{
    public addNewArtifact(String name, Float price, String description){
        ArtifactStoreView view = new ArtifactStoreView();
        String name = view.getStringFromUserInput(view.artifactNameQuestion);
        String description = view.getStringFromUserInput(view.artifactDescriptionQuestion);
        String priceStr = view.getStringFromUserInput(view.artifactPriceQuestion)
        float price = Float.parseFloat(priceStr);
        ArtifactModel artifact = new ArtifactModel(name, price, description);
        ArtifactStroreModel.addNewArtifact(artifact);
        ArtiffactDaoImpl dao = new ArtiffactDaoImpl();
        // getAllArtifacts() getIterator(),
        assignArtifactToCategory();
    }

    public buyArtifact(String name, Group<User> consumers){

    }

    public editArtifact(){

    }

    public createArtifactCategory(){

    }

    public assignArtifactToCategory(ArtifactModel artifact){

    }
}
