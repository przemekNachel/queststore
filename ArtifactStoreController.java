import ArtifactModel;
import ArtifactStoreView;
import ArtifactStoreModel;

class ArtifactStoreController{
    public addNewArtifact(String name, Float price, String description){
        name = ArtifactStoreView.artifactNameQuestion();
        price = ArtifactStoreView.artifactPriceQuestion();
        description = ArtifactView.artifactDescriptionQuestion();
        ArtifactModel artifact = new ArtifactModel(name, price, description);
        ArtifactStroreModel.addNewArtifact(artifact)
    }

    public buyArtifact(String name, Group<User> consumers){

    }

    public editArtifact(){

    }

    public createArtifactCategory(){

    }

    public assignArtifactToCategory(){

    }
}
