package common.io;

/**
 * The {@code Printer} class provides utility methods for printing colored output to the console. It
 * uses ANSI escape codes to color the text, providing different colors for different types of
 * information. This can improve the readability and clarity of console output.
 */
public class Printer {
  private static final String RED = "\u001B[31m";
  private static final String GREEN = "\u001B[32m";
  private static final String YELLOW = "\u001B[33m";
  private static final String CYAN = "\u001B[36m";
  private static final String WHITE = "\u001B[37m";

  public static void printError(Object error) {
    System.out.println(RED + error + WHITE);
  }

  public static void printInfo(Object info) {
    System.out.println(GREEN + info + WHITE);
  }

  public static void printCondition(Object condition) {
    System.out.println(YELLOW + condition + WHITE);
  }

  public static void printResult(Object result) {
    System.out.println(CYAN + result + WHITE);
  }
}
