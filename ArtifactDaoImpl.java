import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Objects;

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
        String art_name = artifact.getName();
        String art_desc = artifact.getDescription();
        float art_price = artifact.getPrice();

        String db_path = "jdbc:sqlite:/home/adonalsium/Documents/Codecool/Java SE/Major Project/queststore-thenewworldorder/src/artifactdb";
        try {
            Connection con = DriverManager.getConnection(db_path);
            Objects.requireNonNull(con).setAutoCommit(false);
            Statement stmt = con.createStatement();

            String sql = ("INSERT INTO Artifacts (art_name, art_desc, art_price)" +
                    "VALUES('"+ art_name + "', '" + art_desc + "', '" + art_price + "');");
            stmt.executeUpdate(sql);

            stmt.close();
            con.commit();


        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }


        /*
        1. Insert to artifact_store:  name (string), desc (string), price (integer)

         */


        boolean artifactAdded = false;
        Iterator<Group<ArtifactModel>> allGroupsIterator = ArtifactDaoImpl.artifacts.getIterator();
        while(allGroupsIterator.hasNext()){
            Group<ArtifactModel> artifactGroup = allGroupsIterator.next();
            String artifactGroupName = artifactGroup.getName();
            if(artifactGroupName.equals(groupName)){
                artifactGroup.add(artifact); //if group exists dont add group to group_names
                artifactAdded = true;


                }
            }if(!artifactAdded){
                Group<ArtifactModel> tmp = new Group<>(groupName);
                tmp.add(artifact);
                artifacts.add(tmp); // if group doesnt exists, create new group in group name
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

    public void createArtifactGroup(Group<ArtifactModel> group){
        artifacts.add(group);
    }

    public void tmpSetArtifacts(Group<Group<ArtifactModel>> artifacts){
        this.artifacts = artifacts;
        /*
        1. file read in another method

         */
    }
}
