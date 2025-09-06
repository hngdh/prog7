package common.exceptions;

/**
 * The {@code WrongCommandException} class represents an exception that is thrown when the user
 * provides invalid command. It extends the {@code Exception} class and provides a specific error
 * message to inform the user about the incorrect command.
 */
public class WrongCommandException extends Exception {
  public WrongCommandException() {}

  @Override
  public String toString() {
    return "Wrong command!";
  }
}
