package main.java.com.nwo.queststore.view;

import main.java.com.nwo.queststore.model.MenuModel;
import main.java.com.nwo.queststore.model.MenuOptionModel;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.Iterator;

public abstract class AbstractConsoleView {

    public String nameAlreadyTaken = "\n This name cannot be used.\n";
    public String nameOutOfRange = "\n You need to provide an already existing name.\n";

    protected MenuModel menuModel;

    private static final String ESCAPE_SEQ_CLEAR_SCREEN = "\033[H\033[2J";

    private static Scanner scanner;

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

    private void printMenu(MenuModel menuModel) {

        printLine(menuModel.toString());
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
        } while(!validInput);
        return userInput;
    }

    public String getStringFromUserInput(String prompt) {

        print(prompt);
        return scanner.nextLine();
    }

    public MenuOptionModel getMenuOptionFromUserInput(String prompt) {

        MenuOptionModel chosenOption = null;

        boolean providedValidAnswer = false;
        while (!providedValidAnswer) {

            printMenu(menuModel);
            String userInput = getStringFromUserInput(prompt);

            Iterator<MenuOptionModel> iter = menuModel.getIterator();
            while (iter.hasNext()) {

                MenuOptionModel currentOption = iter.next();
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
