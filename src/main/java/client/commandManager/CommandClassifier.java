package client.commandManager;

import common.enums.CommandTypes;
import java.util.HashMap;

public class CommandClassifier {
  private final HashMap<String, CommandTypes> commandClassifier = new HashMap<>();

  public void init() {
    registerClassifier("add", CommandTypes.INPUT_NEEDED);
    registerClassifier("clear", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("execute_script", CommandTypes.INPUT_NEEDED);
    registerClassifier("exit", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("filter_contains_name", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("help", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("info", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("min_by_coordinates", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("ping", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("print_field_ascending_house", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("remove_by_id", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("remove_first", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("remove_lower", CommandTypes.INPUT_NEEDED);
    registerClassifier("show", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("sign_in", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("sign_out", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("sign_up", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("sort", CommandTypes.NO_INPUT_NEEDED);
    registerClassifier("update", CommandTypes.INPUT_NEEDED);
  }

  public void registerClassifier(String command, CommandTypes type) {
    commandClassifier.put(command, type);
  }

  public CommandTypes getCommandClassifier(String command) {
    return commandClassifier.get(command);
  }

  public boolean isCommand(String command) {
    return commandClassifier.containsKey(command);
  }
}
