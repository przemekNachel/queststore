public interface ArtifactDao{

    public Group<Group<ArtifactModel>> getAllArtifcts();
    public ArtifactModel getArtifact(String name);
    public Group<String> getArtifactGroupNames();
    public void addArtifact(ArtifactModel artifact, String groupName);
    public void updateArtifact(ArtifactModel artifact);
    public void remove(ArtifactModel artifact);
}
