package common.dataProcessors;

/**
 * The {@code InputSplitter} class provides static utility methods for splitting input into two
 * parts, representing command and its argument.
 */
public class InputSplitter {
  public InputSplitter() {}

  public static String getCommand(String input) {
    return input.split(" ")[0];
  }

  public static String getArg(String input) {
    if (input.split(" ").length >= 2) {
      return input.split(" ")[1];
    } else {
      return null;
    }
  }
}
