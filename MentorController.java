public class MentorController {
    public MentorView view = new MentorView();

    public void createCodecooler() {

      String nickname = view.getStringFromUserInput("provide user nickname");
      String email = view.getStringFromUserInput("provide user mail");
      String password = view.getStringFromUserInput("provide user password");

      CodecoolerModel codecooler = new CodecoolerModel("codecooler", nickname, email, password);
      UserDaoImpl userDao = new UserDaoImpl();

      // If user getter doesn't find given user, it returns null
      if (userDao.getUser(codecooler) == null) {
        userDao.addUser(codecooler);
      }

      else {
        view.printLine("User already in database.")
      }


      // instantiate userDaoImpl object
      // implement adding codecooler
    }
    // Using userDaoImpl, call addUser() method with has the user passed
    // check through day if such user already exists
    // by dao method getUser()
    // if returns null -- create new user, else, throw error
}
