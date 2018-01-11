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
    public void addArtifact(ArtifactModel artifact){
        Group<Group<ArtifactModel>> artifactGroups = artifacts.getAssociatedGroups();
        Iterator<Group<ArtifactModel>> artifactGroupIterator = artifactGroups.getIterator();
        Iterator<Group<ArtifactModel>> allGroupsIterator = users.getIterator();
        while(userGroupIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = artifactGroupIterator.next();
            String artifactGroupName = artifactGroup.getName();
            while(allGroupsIterator.hasNext()){
                Group<ArtifactModel> allUsersGroups = allGroupsIterator.next();
                String allUsersGroupsName = allUsersGroups.getName();
                if(artifactGroupName.equals(allUsersGroupsName)){
                    allUsersGroups.add(artifact); //zakładamy że dodawany
                                                  //bedzie element tylko
                                                  //wtedy gdy nie znajduje
                                                  //się już w danym zbiorze
                }
            }
        }
    }

    public void updateArtifact(ArtifactModel artifact){
        Iterator<Group<ArtifactModel>> artifactGroupIterator = artifacts.getIterator();
        while(artifactGroupIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = artifactGroupIterator.next();
            Iterator<ArtifactModel> artifactIterator = artifactGroup.getIterator();
            while(artifactIterator.hasNext()){
                ArtifactModel currentArtifact = usersIterator.next();
                if(currentUser.getName().equals(artifact.getName())){
                    artifactGroup.remove(currentArtifact);
                    artifactGroup.add(artifact);
                }
            }
        }
    }

    public void remove(ArtifactModel artifact){
        Iterator<Group<ArtifactModel>> artifactGroupIterator = artifacts.getIterator();
        while(artifactGroupIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = artifactGroupIterator.next();
            Iterator<ArtifactModel> artifactIterator = artifactGroup.getIterator();
            while(artifactIterator.hasNext()){
                ArtifactModel currentArtifact = artifactIterator.next();
                if(currentArtifact.getName().equals(artifact.getName())){
                    artifactGroup.remove(currentArtifact);
                }
            }
        }
    }

    public void tmpSetArtifacts(Group<Group<ArtifactModel>> artifacts){
        this.artifacts = artifacts;
    }
}
