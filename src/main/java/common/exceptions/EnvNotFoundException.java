package common.exceptions;

public class EnvNotFoundException extends Exception {
  public EnvNotFoundException() {}

  @Override
  public String toString() {
    return "(Some of) Env not found";
  }
}
