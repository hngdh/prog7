package common.exceptions.database;

public class LoadFailedException extends Exception {
  private final String msg;

  public LoadFailedException(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "Load " + msg + " failed";
  }
}
