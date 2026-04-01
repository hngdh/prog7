package common.enums;

/**
 * The {@code CommandTypes} enum represents whether a command requires user input or not. It defines
 * the possible states for input requirements, useful for setting suitable {@link
 * client.readMode.ReaderMode}.
 */
public enum CommandTypes {
  INPUT_NEEDED,
  NO_INPUT_NEEDED
}
