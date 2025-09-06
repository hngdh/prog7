package common.exceptions;

public class AccountInputException extends Exception {
  public AccountInputException() {}

  @Override
  public String toString() {
    return "Account input condition not met";
  }
}
