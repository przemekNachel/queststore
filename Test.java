import java.util.Iterator;

public class Test{
    Group<Group<User>> school;
    Iterator<Group<User>> schoolIter;

    private void fillTheGroup(){
        school = new Group<Group<User>>("school");
        school.add(new Group<User>("mentors"));
        MentorModel mentor1 = new MentorModel("mentor1", "mentor1@cc.com", "aaaa", null);
        MentorModel mentor2 = new MentorModel("mentor2", "mentor2@cc.com", "aaaa", null);
        MentorModel mentor3 = new MentorModel("mentor3", "mentor3@cc.com", "aaaa", null);

        schoolIter = school.getIterator();

        while(schoolIter.hasNext()){
            Group<User> group = schoolIter.next();
            if(group.getName().equals("mentors")){
                group.add(mentor1);
                group.add(mentor2);
                group.add(mentor3);
            }
        }

    }

    public static void main(String []args){
        Test test = new Test();
        UserDaoImpl userDao;
        test.fillTheGroup();
        userDao = new UserDaoImpl();
        userDao.tmpSetUsers(test.school);

        //userDao tests
        System.out.println("UserDaoTests:\n");
        test.testGetUser();
        test.testAddUser();
        test.testUpdateUser();
        test.testRemoveUser();
        test.testGetUserGroupNames();
        test.testGetUserGroup();
        test.testAddUserAdh();
        userDao = null;
        //koniec testów userDao


    }























    // UserDaoTests
    private void testGetUser(){
        UserDaoImpl userDao = new UserDaoImpl();
        User testMentor = userDao.getUser("mentor1");
        System.out.println("TestGetUser: " +
            assertNotNull(testMentor));
        testMentor = null;
    }

    private void testAddUser(){
        UserDaoImpl userDao = new UserDaoImpl();

        String testGroupName = "krk-2012-B";
        String testName = "test Mentor";

        MentorModel testMentor = new MentorModel(testName, "test@test.com", "test", new Group<User>(testGroupName));

        userDao.addUser(testMentor);
        System.out.println("TestAddUser: " +
            assertEquals(testMentor, userDao.getUser(testName)));
        testMentor = null;

    }

    private void testUpdateUser(){
        UserDaoImpl userDao = new UserDaoImpl();

        String testName = "test Mentor";
        String testNameUpdated = testName + " Updated";

        User testMentor = userDao.getUser(testName);
        testMentor.setName(testNameUpdated);
        userDao.updateUser(testMentor);
        System.out.println("TestUpdateUser: " +
        assertNotNull(userDao.getUser(testNameUpdated)));

    }

    private void testRemoveUser(){
        UserDaoImpl userDao = new UserDaoImpl();
        String testName = "test Mentor Updated";
        User testMentor = userDao.getUser(testName);

        System.out.println("TestRemoveUser: " + assertTrue(userDao.deleteUser(testMentor)));


    }

    private void testGetUserGroupNames(){
        int elementsNumber = 2;
        int emptyGroupSize = 0;
        UserDaoImpl userDao = new UserDaoImpl();

        Group<String> names = userDao.getUserGroupNames();
        System.out.println("TestGetUserGroupNames: " +
            assertTrue(names.size() == elementsNumber));
        Group<Group<User>> clear = new Group<Group<User>>("school");
        userDao.tmpSetUsers(clear);
        Group<String> namesEmpty = userDao.getUserGroupNames();
        System.out.println("TestGetEmptyNames: " +
            assertTrue(namesEmpty.size() == emptyGroupSize));
        userDao.tmpSetUsers(this.school);
    }

    private void testGetUserGroup(){
        UserDaoImpl userDao = new UserDaoImpl();
        String testGroupName = "krk-2012-B";
        String testName = "test Mentor";
        Group<User> userGroup = userDao.getUserGroup(testGroupName);
        System.out.println("TestGetUserGroup: " +
            assertEquals(testGroupName, userGroup.getName()));
        Iterator<User> iter = userGroup.getIterator();
        User user = new MentorModel(testName, "test@test.com", "test", userGroup);
        while(iter.hasNext()){
            user = iter.next();
        }
        System.out.println("TestGetGroup2: " +
            assertEquals(testName, user.getName()));
    }

    private void testAddUserAdh(){
        UserDaoImpl userDao = new UserDaoImpl();
        String testGroupName = "krk-2012-B";
        String testName = "test Mentor";
        User testUser = userDao.getUser(testName);
        System.out.println("TestAddUserAdh: " +
            assertTrue(userDao.addUserAdherence(testUser, testGroupName)));
        System.out.println("TestAddUserAdh2: " +
            assertFalse(userDao.addUserAdherence(testUser, testGroupName)));

    }


    // test cases
    public void showNames(){
        UserDaoImpl userDao = new UserDaoImpl();
        Group<String> names = userDao.getUserGroupNames();
        Iterator<String> iter = names.getIterator();
        System.out.println("------------");
        while(iter.hasNext()){
            System.out.println(iter.next());
        }
        System.out.println("------------");
    }

    private String assertEquals(Object obj1, Object obj2){
        return obj1.equals(obj2) ? "PASS" : "***FAIL***";
    }

    private String assertNotEquals(Object obj1, Object obj2){
        return !obj1.equals(obj2) ? "PASS" : "***FAIL***";
    }

    private String assertNotNull(Object obj1){
        return obj1!=null ? "PASS" : "***FAIL***";
    }

    private String assertTrue(boolean bool){
        return bool ? "PASS" : "***FAIL***" ;
    }

    private String assertFalse(boolean bool){
        return !bool ? "PASS" : "***FAIL***";
    }
}
