package common.exceptions.database;

public class UpdateFailedException extends Exception {
  private final String msg;

  public UpdateFailedException(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "Update " + msg + " failed";
  }
}
