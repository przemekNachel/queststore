public interface ArtifactDao{

    public Group<Group<ArtifactModel>> getAllArtifcts();
    public ArtifactModel getArtifact(String name);
    public Group<String> getArtifactGroupNames();
    public Group<ArtifactModel> getArtifactGroup(String groupName);
    public void addArtifact(ArtifactModel artifact, String groupName);
    public void updateArtifact(ArtifactModel artifact);
    public boolean deleteArtifact(ArtifactModel artifact);
}
