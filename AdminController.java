import java.util.Iterator;
import java.sql.*;

class AdminController{
  AdminView view;

  public AdminController() {
    Menu adminMenu = new Menu(
        new MenuOption("0", "Exit"),
        new MenuOption("1", "Create a mentor"),
        new MenuOption("2", "Assign a mentor to a group"),
        new MenuOption("3", "Create user group"),
        new MenuOption("4", "Edit mentor"),
        new MenuOption("5", "View mentor's details"),
        new MenuOption("6", "View groups")
        );

    view = new AdminView(adminMenu);
  }

  public void createMentor(){
      UserDaoImpl dao = new UserDaoImpl();
      String name = this.view.getStringFromUserInput(view.mentorNameQuestion);
      String email = this.view.getStringFromUserInput(view.mentorEmailQuestion);
      String password = this.view.getStringFromUserInput(view.mentorPasswordQuestion);
      Group<User> mentorsGroup = null;
      try{
          mentorsGroup = dao.getUserGroup("mentors");
      } catch (SQLException sqle) {
        view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
        return;
      }
      MentorModel mentor = new MentorModel(name, email, password, mentorsGroup);
      mentorsGroup.add(mentor);
      try {

        dao.addUser(mentor);
      } catch (SQLException sqle) {
        view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
        return;
      }
  }

  public void start() {

    boolean requestedExit = false;
    do {
      UserDao userDao = new UserDaoImpl();
      MenuOption userOption = view.getMenuOptionFromUserInput(" Please choose option: ");
      if (userOption.getId().equals("0")) {
        requestedExit = true;
        view.clearScreen();
      } else {

        String chosenOption = userOption.getId();
        handleUserChoice(userOption.getId());
      }
    } while (!requestedExit);
  }

  private void handleUserChoice(String userChoice) {

    switch (userChoice) {
      // mentor creation
      case "1":
        createMentor();
        break;
      // assigning mentor to a group
      case "2":
        assignMentorToGroup();
        break;
      // user group creation
      case "3":
        createGroup();
        break;
      // mentor edition
      case "4":
        editMentor();
        break;
      // view mentor details
      case "5":
        view.printLine(getMentorDisplay());
        break;
      // view all existing groups
      case "6":
        view.printLine(getGroupsDisplay());
        break;
    }
  }

  public void assignMentorToGroup(){
      UserDaoImpl userDao = new UserDaoImpl();

      String name = view.getStringFromUserInput(view.mentorNameQuestion);
      String groupName = view.getStringFromUserInput(view.groupNameQuestion);
      User user = null;
      try {

        user = userDao.getUser(name);
      } catch (SQLException sqle) {
        view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
        return;
      }

      // note: we may still have null in User
      if(!userDao.addUserAdherence(user, groupName) || user == null){
          view.printLine(view.assignMentorToFroupError);
      }else{

          Group<Group<User>> associatedGroups = user
            .getAssociatedGroups();
            try{
                associatedGroups.add(userDao.getUserGroup(groupName));
            } catch (SQLException sqle) {
              view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
              return;
            }

          user.setAssociatedGroups(associatedGroups);
      }

  }

  public void createGroup(){
      UserDaoImpl userDao = new UserDaoImpl();
      String groupName = view.getStringFromUserInput(view.groupNameQuestion);
      Group<User> tmp = new Group<>(groupName);
      userDao.addUserGroup(tmp);
  }

  public void editMentor(){
      UserDaoImpl dao = new UserDaoImpl();
      String mentorName = view.getStringFromUserInput(view.mentorNameQuestion);

      User mentor;
      try {

        mentor = dao.getUser(mentorName);
      } catch (SQLException sqle) {
        view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
        return;
      }

      String choice = view.getStringFromUserInput(view.mentorChangeQuestion);
      if (choice.equals("1")){
          String name = view.getStringFromUserInput(view.mentorNameQuestion);
          mentor.setName(name);
      }
      else if(choice.equals("2")){
          String email = view.getStringFromUserInput(view.mentorEmailQuestion);
          mentor.setEmail(email);
      }
      else if(choice.equals("3")){
          String password = view.getStringFromUserInput(view.mentorPasswordQuestion);
          mentor.setPassword(password);
      }
      else{
          view.printLine(view.noSuchOption);
      }
  }

  public String getGroupsDisplay(){
      UserDaoImpl dao = new UserDaoImpl();
      Group<String> groupNames = null;
      try{
        groupNames = dao.getUserGroupNames();
      } catch (SQLException sqle) {
        view.printLine(sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode()));
        return "error";
      }
      Iterator<String> iter = groupNames.getIterator();
      System.out.println(groupNames.size());
      String groupsFormatted = "";
      while(iter.hasNext()){
          groupsFormatted += iter.next() + " | ";
      }
      return groupsFormatted;
  }

  public String getMentorDisplay(){
      UserDaoImpl dao = new UserDaoImpl();
      String mentorName = view.getStringFromUserInput(view.mentorNameQuestion);

      User mentor;
      try {

        mentor = dao.getUser(mentorName);
      } catch (SQLException sqle) {

        return sqle.getClass().getCanonicalName() + " " + Integer.toString(sqle.getErrorCode());
      }

      if (mentor != null && mentor.getRole() == Role.MENTOR) {

        return mentor.toString();
      }
      return view.noMentorOfSuchName;
  }

}
