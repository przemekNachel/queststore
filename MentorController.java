public class MentorController {
    public MentorView view = new MentorView();

    public void createCodecooler() {

      String nickname = view.getStringFromUserInput(view.userNicknameQUestion);
      String email = view.getStringFromUserInput(view.userEmailQuestion);
      String password = view.getStringFromUserInput(view.userPasswordQuestion);

      CodecoolerModel codecooler = new CodecoolerModel("codecooler", nickname, email, password);
      UserDaoImpl userDao = new UserDaoImpl();

      // If user getter doesn't find given user, it returns null
      if (userDao.getUser(codecooler) == null) {
        userDao.addUser(codecooler);
      }

      else {
        view.printLine("User already in database.");
      }
    }
    public void assignCodecoolerToGroup() {
      
    }


}
