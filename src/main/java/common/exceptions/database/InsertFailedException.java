package common.exceptions.database;

public class InsertFailedException extends Exception {
  private final String msg;

  public InsertFailedException(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "Insert failed: " + msg;
  }
}
