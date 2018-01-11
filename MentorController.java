public class MentorController {
    public MentorView view = new MentorView();

    public void createCodecooler() {
      UserDaoImpl userDao = new UserDaoImpl();


      String nickname = view.getStringFromUserInput(view.userNicknameQUestion);
      String email = view.getStringFromUserInput(view.userEmailQuestion);
      String password = view.getStringFromUserInput(view.userPasswordQuestion);
      Float walletBalance = 0.0f;
      // TODO Default level 0 -- next sprint

      // TODO Level object -- next sprint
      WalletService wallet = new WalletService(walletBalance);
      CodecoolerModel codecooler = new CodecoolerModel("codecooler", nickname, email, password, wallet); // TODO add level to the object -- next sprint

      // If user getter doesn't find given user, it returns null
      if (userDao.getUser(codecooler) == null) {
        userDao.addUser(codecooler);
      }

      else {
        view.printLine("User already in database.");
      }
    }
    public void assignCodecoolerToGroup() {

      UserDaoImpl userDao = new UserDaoImpl();
     //zmien associatedGroups w polu usera, wywolujesz update user z tym userem
      //
    }

}
