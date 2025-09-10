package common.exceptions;

/**
 * The {@code WrongDataException} class represents an exception that is thrown when there was a
 * problem while loading data. It extends the {@code Exception} class and provides a specific error
 * message to inform the user about the problem.
 */
public class WrongDataException extends Exception {
  public WrongDataException() {}

  @Override
  public String toString() {
    return "There were problems loading data";
  }
}
