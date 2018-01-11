import java.util.Iterator;

public class AuxiliaryStorage {

  static Group<Group<User>> getSampleUserGroup() {

      Group<Group<User>> school = new Group<>("school");

      school.add(new Group<User>("mentors"));
      MentorModel mentor1 = new MentorModel("mentor1", "mentor1@cc.com", "aaaa", null);
      MentorModel mentor2 = new MentorModel("mentor2", "mentor2@cc.com", "aaaa", null);
      MentorModel mentor3 = new MentorModel("mentor3", "mentor3@cc.com", "aaaa", null);

      Iterator<Group<User>>schoolIter = school.getIterator();

      while(schoolIter.hasNext()){
          Group<User> group = schoolIter.next();
          if(group.getName().equals("mentors")){
              group.add(mentor1);
              group.add(mentor2);
              group.add(mentor3);
          }
      }
      return school;
    }
}
