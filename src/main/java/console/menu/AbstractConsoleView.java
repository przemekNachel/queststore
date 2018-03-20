package console.menu;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Scanner;

public abstract class AbstractConsoleView {

    private static final String ESCAPE_SEQ_CLEAR_SCREEN = "\033[H\033[2J";
    private static Scanner scanner;
    public String nameAlreadyTaken = "\n This name cannot be used.\n";
    public String nameOutOfRange = "\n You need to provide an already existing name.\n";
    protected Menu menu;

    protected AbstractConsoleView() {

        scanner = new Scanner(System.in);
    }

    public static void closeScanner() {

        AbstractConsoleView.scanner.close();
    }

    public void clearScreen() {

        print(AbstractConsoleView.ESCAPE_SEQ_CLEAR_SCREEN);
    }

    public void printLine(String message) {

        System.out.println(message);
    }

    public void print(String message) {

        System.out.print(message);
        System.out.flush();
    }

    private void printMenu(Menu menu) {

        printLine(menu.toString());
    }

    public void printSQLException(SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }

    public Integer getIntFromUserInput(String prompt) {
        Integer userInput = null;

        boolean validInput;
        do {
            validInput = true;
            try {
                userInput = Integer.valueOf(getStringFromUserInput(prompt));
            } catch (Exception e) {
                validInput = false;
                printLine("\n Invalid input.\n");
            }
        } while (!validInput);
        return userInput;
    }

    public String getStringFromUserInput(String prompt) {

        print(prompt);
        return scanner.nextLine();
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
