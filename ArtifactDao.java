public interface ArtifactDao{

    Group<Group<ArtifactModel>> getAllArtifacts();
    ArtifactModel getArtifact(String name);
    Group<String> getArtifactGroupNames();
    Group<ArtifactModel> getArtifactGroup(String groupName);
    void addArtifact(ArtifactModel artifact, String groupName);
    void updateArtifact(ArtifactModel artifact);
    boolean deleteArtifact(ArtifactModel artifact);
}
