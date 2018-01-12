import java.util.Iterator;

public class AuxiliaryStorage {

  static Group<Group<User>> getSampleUserGroup() {

      Group<Group<User>> school = new Group<>("school");

      Group<User> tmp = new Group<User>("mentors");
      Group<User> adminG = new Group<User>("admin");
      school.add(tmp);
      school.add(adminG);

      MentorModel mentor1 = new MentorModel("mentor1", "mentor1@cc.com", "aaaa", tmp);
      MentorModel mentor2 = new MentorModel("mentor2", "mentor2@cc.com", "aaaa", tmp);
      MentorModel mentor3 = new MentorModel("mentor3", "mentor3@cc.com", "aaaa", tmp);
      AdminModel admin = new AdminModel("admin", "aaaa");
      Group<Group<User>> adminGroup = new Group<>("associated");
      adminGroup.add(adminG);
      admin.setAssociatedGroups(adminGroup);

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
      }
      return school;
    }
}
