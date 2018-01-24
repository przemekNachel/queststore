public class MentorView extends AbstractConsoleView {

<<<<<<< Updated upstream
    public MentorView() {
        // a no-menu constructor - not all uses require a menu
    }
=======
  public MentorView() {
    // a no-menu constructor - not all uses require a menu
  }

  public MentorView(Menu menu) {
>>>>>>> Stashed changes

    public MentorView(Menu menu) {

<<<<<<< Updated upstream
        this.menu = menu;
    }

    public String userNicknameQuestion = "Provide user Nickname: ";
    public String userEmailQuestion = "Provide user Email: ";
    public String userPasswordQuestion = "Provide user Password: ";
    public String userGroupQuestion = "Provide user Group: ";
    public String editNicknameQuestion = "Provide new user Nickname";
    public String editEmailQuestion = "Provide new user Email: ";
    public String editPasswordQuestion = "Provide new user Password: ";
    public String editGroupQuestion = "Provide new user Group: ";
    public String markQuestNameQuestion = "Provide Quest name: ";
    public String markArtifactUsedQuestion = "Provide name of used artifact: ";
    public String codecoolerAlreadyInGroupOrGroupAbsent = "The Codecooler *had* been added to the given group or the group does not exist.";
    public String userAlreadyInDatabase = "User already in database.";
=======
  public String userNicknameQuestion = "Provide user Nickname : ";
  public String userEmailQuestion = "Provide user Email: ";
  public String userPasswordQuestion = "Provide user Password: ";
  public String userGroupQuestion = "Provide user Group: ";
  public String editNicknameQuestion = "Provide new user Nickname";
  public String editEmailQuestion = "Provide new user Email: ";
  public String editPasswordQuestion = "Provide new user Password: ";
  public String editGroupQuestion = "Provide new user Group: ";
  public String markQuestNameQuestion = "Provide Quest name: ";
  public String markArtifactUsedQuestion = "Provide name of used artifact: ";
  public String codecoolerAlreadyInGroupOrGroupAbsent = "The Codecooler *had* been added to the given group or the group does not exist.";
  public String userAlreadyInDatabase = "User already in database.";
  public String availableQuests = "Available quests: ";
  public String invalidNickname = "User not in database or not a Codecooler.";
  public String questNotFoundError = "Quest name not found";
>>>>>>> Stashed changes
}
