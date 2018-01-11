import java.util.Iterator;

public class ArtifactDaoImpl implements ArtifactDao{
    private static Group<Group<ArtifactModel>> artifacts;

    public Group<Group<ArtifactModel>> getAllArtifcts(){
        return artifacts;
    }

    public ArtifactModel getArtifact(String name){
        Iterator<Group<ArtifactModel>> artifactGroupIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(artifactGroupIterator.hasNext()){
            Iterator<ArtifactModel> artifactsIterator = artifactGroupIterator.next().getIterator();
            while(artifactsIterator.hasNext()){
                ArtifactModel currentArtifact = artifactsIterator.next();
                if(currentArtifact.getName().equals(name)){
                    return currentArtifact;
                }
            }
        }
        return null;
    }

    public void addArtifact(ArtifactModel artifact, String groupName){
        boolean artifactAdded = false;
        Iterator<Group<ArtifactModel>> allGroupsIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(allGroupsIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = allGroupsIterator.next();
            String artifactGroupName = artifactGroup.getName();
            if(artifactGroupName.equals(groupName)){
                artifactGroup.add(artifact); //zakładamy że dodawany
                artifactAdded = true;        //bedzie element tylko
                                             //wtedy gdy nie znajduje
                                             //się już w danym zbiorze
                }
            }if(!artifactAdded){
                Group<ArtifactModel> tmp = new Group<>(groupName);
                tmp.add(artifact);
                artifacts.add(tmp);
            }
            artifactAdded = false;
        }

    public void updateArtifact(ArtifactModel artifact){
        Iterator<Group<ArtifactModel>> artifactGroupIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(artifactGroupIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = artifactGroupIterator.next();
            Iterator<ArtifactModel> artifactIterator = artifactGroup.getIterator();
            while(artifactIterator.hasNext()){
                ArtifactModel currentArtifact = artifactIterator.next();
                if(currentArtifact.getName().equals(artifact.getName())){
                    artifactGroup.remove(currentArtifact);
                    artifactGroup.add(artifact);
                }
            }
        }
    }

    public boolean deleteArtifact(ArtifactModel artifact){
        boolean artifactRemoved = false;
        Iterator<Group<ArtifactModel>> artifactGroupIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(artifactGroupIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = artifactGroupIterator.next();
            Iterator<ArtifactModel> artifactIterator = artifactGroup.getIterator();
            while(artifactIterator.hasNext()){
                ArtifactModel currentArtifact = artifactIterator.next();
                if(currentArtifact.getName().equals(artifact.getName())){
                    artifactGroup.remove(currentArtifact);
                    artifactRemoved = true;
                }
            }
        }
        return artifactRemoved;
    }

    public Group<String> getArtifactGroupNames(){
        Group<String> groupsNames = new Group<>("Group names");
        Iterator<Group<ArtifactModel>> groupIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(groupIterator.hasNext()){
            groupsNames.add(groupIterator.next().getName());
        }
        return groupsNames;
    }

    public boolean addArtifactAdherence(ArtifactModel artifact, String groupName){
        Iterator<Group<ArtifactModel>> artifactGroupsIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(artifactGroupsIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = artifactGroupsIterator.next();
            if(artifactGroup.getName().equals(groupName)){
                return artifactGroup.add(artifact);
            }
        }
        return false;
    }

    public Group<ArtifactModel> getArtifactGroup(String groupName){
        Iterator<Group<ArtifactModel>> groupIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(groupIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = groupIterator.next();
            if(artifactGroup.getName().equals(groupName)){
                return artifactGroup;
            }
        }
        return null;
    }

    public void tmpSetArtifacts(Group<Group<ArtifactModel>> artifacts){
        this.artifacts = artifacts;
    }
}
