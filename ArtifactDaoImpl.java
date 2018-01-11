public class ArtifactDaoImpl implements ArtifactDao{
    private static Group<Group<ArtifactModel>> artifacts;

    public Group<Group<ArtifactModel>> getAllArtifcts(){
        return artifacts;
    }

    public Artifact getArtifact(String name){
        Iterator<Group<ArtifactModel>> artifactGroupIterator = artifacts.getIterator();
        while(artifactGroupIterator.hasNext()){
            Iterator<ArtifactModel> artifactsIterator = artifactGroupIterator.next().getIterator();
            while(artifactsIterator.hasNext()){
                User currentArtifact = artifactsIterator.next();
                if(currentArtifact.getName().equals(name)){
                    return currentArtifact;
                }
            }
        }
        return null;
    }

    public void addArtifact(ArtifactModel artifact){}
    public void updateArtifact(ArtifactModel artifact){}
    public void remove(ArtifactModel artifact){}
}
