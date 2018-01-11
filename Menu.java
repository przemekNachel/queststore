import java.util.Iterator;

public class Menu {

  private static final String INDENT = "    ";
  private Group<MenuOption> menuOptions;

  public Menu(MenuOption... options) {

    menuOptions = new Group<MenuOption>("testmenu");
    for (MenuOption mo : options) {

      menuOptions.add(mo);
    }
  }

  public String toString() {

      String result = "";
      Iterator<MenuOption> iter = menuOptions.getIterator();
      while (iter.hasNext()) {

        MenuOption currentOption = iter.next();
        result += "\n" + Menu.INDENT + currentOption.toString();
      }
      return result;
  }

  public Iterator<MenuOption> getIterator() {

    return menuOptions.getIterator();
  }
}
