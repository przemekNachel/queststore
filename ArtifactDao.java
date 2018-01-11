public interface ArtifactDao{

    public Group<Group<ArtifactModel>> getAllArtifcts();
    public Artifact getArtifact(String name);
    public void addArtifact(ArtifactModel artifact);
    public void updateArtifact(ArtifactModel artifact);
    public void remove(ArtifactModel artifact);
}
