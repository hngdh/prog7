package client.commandManager;

import common.enums.CommandFormats;
import common.exceptions.WrongCommandException;
import common.exceptions.WrongInputFormatException;
import java.util.TreeMap;

/**
 * The {@code FormatChecker} class is responsible for validating the format of user commands. It
 * checks whether a given command has the correct type of argument (or no argument) as defined in
 * command's properties.
 */
public class FormatChecker {
  private final TreeMap<String, CommandFormats> commandFormats = new TreeMap<>();

  public FormatChecker() {}

  public void init() {
    registerFormat("add", CommandFormats.WITHOUT_ARG);
    registerFormat("clear", CommandFormats.WITHOUT_ARG);
    registerFormat("execute_script", CommandFormats.WITH_STRING_ARG);
    registerFormat("exit", CommandFormats.WITHOUT_ARG);
    registerFormat("filter_contains_name", CommandFormats.WITH_STRING_ARG);
    registerFormat("help", CommandFormats.WITHOUT_ARG);
    registerFormat("info", CommandFormats.WITHOUT_ARG);
    registerFormat("min_by_coordinates", CommandFormats.WITHOUT_ARG);
    registerFormat("ping", CommandFormats.WITHOUT_ARG);
    registerFormat("print_field_ascending_house", CommandFormats.WITHOUT_ARG);
    registerFormat("remove_by_id", CommandFormats.WITH_NUMERAL_ARG);
    registerFormat("remove_first", CommandFormats.WITHOUT_ARG);
    registerFormat("remove_lower", CommandFormats.WITHOUT_ARG);
    registerFormat("show", CommandFormats.WITHOUT_ARG);
    registerFormat("sign_in", CommandFormats.WITHOUT_ARG);
    registerFormat("sign_out", CommandFormats.WITHOUT_ARG);
    registerFormat("sign_up", CommandFormats.WITHOUT_ARG);
    registerFormat("sort", CommandFormats.WITHOUT_ARG);
    registerFormat("update", CommandFormats.WITH_NUMERAL_ARG);
  }

  public void registerFormat(String command, CommandFormats format) {
    commandFormats.put(command, format);
  }

  public void checkFormat(String command, String argument)
      throws WrongInputFormatException, WrongCommandException {
    if (!commandFormats.containsKey(command)) throw new WrongCommandException();
    CommandFormats commandFormat = commandFormats.get(command);
    switch (commandFormat) {
      case WITH_NUMERAL_ARG:
        try {
          int integer = Integer.parseInt(argument);
          if (integer < 0) {
            throw new WrongInputFormatException();
          }
        } catch (NumberFormatException e) {
          throw new WrongInputFormatException();
        }
        break;
      case WITH_STRING_ARG:
        if (argument == null || argument.isEmpty()) throw new WrongInputFormatException();
        break;
      case WITHOUT_ARG:
        if (argument != null && !argument.isEmpty()) throw new WrongInputFormatException();
        break;
    }
  }
}
