package common.dataProcessors;

import common.exceptions.WrongInputException;
import common.io.LogUtil;
import common.io.Printer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The {@code InputChecker} class provides static utility methods for validating user input. It
 * includes methods for checking the general format of input, validating strings, checking if a
 * string represents an integer, determining if a string is empty, and prompting the user for a
 * yes/no confirmation.
 */
public class InputChecker {
  public static boolean checkInput(String input) {
    input = input.trim();
    return !input.isEmpty();
  }

  public static boolean checkDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    try {
      LocalDate.parse(date, formatter);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  public static boolean checkString(String input) {
    if (input == null || input.isEmpty()) {
      return false;
    }
    input = input.trim();
    return !input.isEmpty();
  }

  public static boolean checkInteger(String input) {
    return input.matches("-?[0-9]+");
  }

  public static boolean checkFloat(String input) {
    return input.matches("-?[0-9]+.?[0-9]*E?[0-9]*") && input.length() < 40;
  }

  public static boolean checkBoolean(String input) {
    return input.equalsIgnoreCase("false") || input.equalsIgnoreCase("true");
  }

  public static boolean checkOptional(String act, String description) {
    try {
      Printer.printInfo("Do you want to " + act + " " + description + "? (yes/no)");
      InputReader inputReader = new InputReader();
      inputReader.setReader();
      String input;
      while (true) {
        input = inputReader.readLine();
        if (input.equalsIgnoreCase("yes")) {
          return true;
        } else if (input.equalsIgnoreCase("no")) {
          return false;
        } else {
          try {
            throw new WrongInputException();
          } catch (WrongInputException e) {
            Printer.printError(e.toString());
          }
        }
      }
    } catch (IOException e) {
      LogUtil.logClientError(e);
    }
    return false;
  }
}
