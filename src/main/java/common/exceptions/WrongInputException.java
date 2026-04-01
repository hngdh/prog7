package common.exceptions;

/**
 * The {@code WrongInputException} class represents an exception that is thrown when the user
 * provides invalid input. It extends the {@code Exception} class and provides a specific error
 * message to inform the user about the invalid input.
 */
public class WrongInputException extends RuntimeException {
  public WrongInputException() {}

  @Override
  public String toString() {
    return "Wrong input!";
  }
}
