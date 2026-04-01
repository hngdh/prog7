package common.objects;

import java.io.Serializable;

public class Account implements Serializable {
  private final String user;
  private final String password;

  public Account(String user, String password) {
    this.user = user;
    this.password = password;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
