package common.exceptions;

public class NetworkException extends Exception {
  public NetworkException() {}

  @Override
  public String toString() {
    return "Network problem";
  }
}
