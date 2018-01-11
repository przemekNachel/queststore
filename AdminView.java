class AdminView extends AbstractConsoleView {

  public AdminView(Menu menu) {

    this.menu = menu;
  }

  public String mentorNameQuestion = "Provide name:";
  public String mentorPasswordQuestion = "Type in password: ";
  public String mentorEmailQuestion = "Type in email: ";
  public String groupNameQuestion = "Provide group name:";
  public String mentorChangeQuestion = "What do you want to edit? \n 1.Mentor's name \n 2. Mentor's email \n 3. Mentor's password";
  public String levelNameQuestion = "Type in lvls name: ";
  public String levelTresholdQuestion = "Provide lvl's treshold";
  public String noSuchOption = "No such option!";
}
