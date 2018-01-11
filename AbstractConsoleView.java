import java.util.Scanner;
import java.util.Iterator;

public abstract class AbstractConsoleView {

  protected Menu menu;
  private Scanner scanner;

  public AbstractConsoleView() {

    scanner = new Scanner(System.in);
  }

  public void printLine(String message) {

    System.out.println(message);
  }

  public void print(String message) {

    System.out.print(message);
    System.out.flush();
  }

  public void printMenu(Menu menu) {

    printLine(menu.toString());
  }

  public String getStringFromUserInput(String prompt) {

    return scanner.next();
  }

  public char getCharacterFromUserInput(String prompt) {

    return getStringFromUserInput(prompt).charAt(0);
  }

  public MenuOption getMenuOptionFromUserInput(String prompt) {

    MenuOption chosenOption = null;

    boolean providedValidAnswer = false;
    while (!providedValidAnswer) {

      printMenu(menu);
      String userInput = getStringFromUserInput(prompt);

      Iterator<MenuOption> iter = menu.getIterator();
      while (iter.hasNext()) {

        MenuOption currentOption = iter.next();
        if (currentOption.getId().equals(userInput)) {
          providedValidAnswer = true;
          chosenOption = currentOption;
          break;
        }
      }
    }

    return chosenOption;
  }
}
