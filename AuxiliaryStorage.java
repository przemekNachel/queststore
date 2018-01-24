import java.util.Iterator;

public class AuxiliaryStorage {

    static Group<Group<ArtifactModel>> getSampleArtifactGroup() {

        Group<Group<ArtifactModel>> artifacts = new Group<Group<ArtifactModel>>("artifacts");
        artifacts.add(new Group<ArtifactModel>("basic"));
        ArtifactModel art1 = new ArtifactModel("Sword", "aa", 55);
        ArtifactModel art2 = new ArtifactModel("Knife", "bb", 71);
        ArtifactModel art3 = new ArtifactModel("Dishwasher", "cc", 37);
        artifacts.add(new Group<ArtifactModel>("magic"));
        ArtifactModel art4 = new ArtifactModel("Adamantium", "dd", 21);
        ArtifactModel art5 = new ArtifactModel("Czeczenian uranu", "ee", 88);
        ArtifactModel art6 = new ArtifactModel("Magiczne serce Owsiaka", "ff", 14);

        Iterator<Group<ArtifactModel>> artIter = artifacts.getIterator();

        while(artIter.hasNext()){
            Group<ArtifactModel> group = artIter.next();
            if(group.getName().equals("basic")){
                group.add(art1);
                group.add(art2);
                group.add(art3);
            }
            if(group.getName().equals("magic")){
                group.add(art4);
                group.add(art5);
                group.add(art6);
            }
        }

        return artifacts;
    }

    static Group<Group<User>> getSampleUserGroup() {

        Group<Group<User>> school = new Group<>("school");

        Group<User> tmp = new Group<User>("mentors");
        Group<User> adminG = new Group<User>("admin");
        Group<User> students = new Group<User>("students");
        school.add(tmp);
        school.add(adminG);
        school.add(students);

        WalletService wallet1 = new WalletService(777);
        WalletService wallet2 = new WalletService(250);

        MentorModel mentor1 = new MentorModel("mentor1", "mentor1@cc.com", "aaaa", tmp);
        MentorModel mentor2 = new MentorModel("mentor2", "mentor2@cc.com", "aaaa", tmp);
        MentorModel mentor3 = new MentorModel("mentor3", "mentor3@cc.com", "aaaa", tmp);
        CodecoolerModel kdkl1 = new CodecoolerModel("Ferdynand", "Kiepski", "cycu", wallet1, students);
        CodecoolerModel kdkl2 = new CodecoolerModel("Halina", "Kiepska", "cycu", wallet2, students);

        AdminModel admin = new AdminModel("admin", "aaaa", adminG);

        Iterator<Group<User>>schoolIter = school.getIterator();

        while(schoolIter.hasNext()){
            Group<User> group = schoolIter.next();
            if(group.getName().equals("mentors")){
                group.add(mentor1);
                group.add(mentor2);
                group.add(mentor3);
            }
            if(group.getName().equals("admin")){
                group.add(admin);
            }
            if(group.getName().equals("students")){
                group.add(kdkl1);
                group.add(kdkl2);
            }
        }
        return school;
    }
}
