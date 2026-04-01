package common.exceptions.database;

public class RemoveFailedException extends Exception {
  private final String msg;

  public RemoveFailedException(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "Remove " + msg + " failed";
  }
}
