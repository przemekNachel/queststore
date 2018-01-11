public class MentorController {
    public MentorView view = new MentorView();

    public void createCodecooler() {
      UserDaoImpl userDao = new UserDaoImpl();


      String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
      String email = view.getStringFromUserInput(view.userEmailQuestion);
      String password = view.getStringFromUserInput(view.userPasswordQuestion);

      // TODO Default level 0 -- next sprint
      // TODO Level object -- next sprint

      Group<User> studentsGroup = userDao.getUserGroup("students");


      WalletService wallet = new WalletService();
      CodecoolerModel codecooler = new CodecoolerModel(nickname, email, password, wallet, studentsGroup); // TODO add level to the object -- next sprint

      // If user getter doesn't find given user, return null
      if (userDao.getUser(nickname) == null) {
        userDao.addUser(codecooler);
      }
      else {
        view.printLine("User already in database.");
      }
    }
    public void assignCodecoolerToGroup() {
      UserDaoImpl userDao = new UserDaoImpl();

      String nickname = view.getStringFromUserInput(view.userNicknameQuestion);
      String groupName = view.getStringFromUserInput(view.userGroupQuestion);

      User user = userDao.getUser(nickname);
      userDao.addUserAdherence(user, groupName);
    }

}
