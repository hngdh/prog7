package common.exceptions;

/**
 * The {@code LogException} class represents an exception that is thrown when there was an error
 * during work of program. It provides a general error message to inform the user about error being
 * logged in log file.
 */
public class LogException extends Exception {
  private final String msg;

  public LogException(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return msg;
  }

  @Override
  public String getMessage() {
    return "Error during processing, please check log";
  }
}
