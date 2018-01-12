import java.lang.UnsupportedOperationException;

public class LoginController {

  private LoginView view;

  public LoginController() {

    Menu loginMenu = null;
    this.view = new LoginView(loginMenu);


    // WARNING: the calls below are part of a demonstration
    new UserDaoImpl().tmpSetUsers(AuxiliaryStorage.getSampleUserGroup());
  }

  public void start() {

    String nickname = "";
    String password = "";
    view.clearScreen();
    view.printLine(view.welcomeMessage);
    boolean wantToQuit = false;
    do {

      view.printLine(view.loginMessage);

      nickname = view.getStringFromUserInput(view.loginNicknameQuestion);
      if (nickname.equals("X")) {
        wantToQuit = true;
        break;
      }
      password = view.getStringFromUserInput(view.loginPasswordQuestion);

      User user = login(nickname, password);
      if (user == null) {

        view.printLine(view.loginInvalidCredentialsOrNoUser);
      } else {

        engageAppropriateController(user);
      }

    } while(!wantToQuit);
    view.printLine(view.goodbyeMessage);
    AbstractConsoleView.closeScanner();
  }

  public void engageAppropriateController(User user) {

    Role userRole = user.getRole();

    switch(userRole) {

      case ADMIN:
        new AdminController().start();
        break;

      case MENTOR:
        new MentorController().start();
        break;

      case CODECOOLER:
        new CodecoolerController((CodecoolerModel)user).start();
        break;

      default:
        throw new UnsupportedOperationException("unknown role encountered on login");
    }

  }

  public User login(String nickname, String password) {

    UserDao userDao = new UserDaoImpl();
    User user = userDao.getUser(nickname);
    if (user != null && user.getPassword().equals(password)) {
      return user;
    } else {
      return null;
    }
  }

}
