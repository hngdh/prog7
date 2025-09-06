package common.dataProcessors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountInputChecker {
  public static boolean checkUser(String user) {
    return user != null && !user.isEmpty() && user.length() <= 45;
  }

  public static boolean checkPassword(String password) {
    if (password == null) return false;
    String pattern = "^(?=.*\\d)(?=.*[!@#$%^&*|?+=])(?=.*[A-Z])(?=.*[a-z]).{5,45}$";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(password);
    return m.matches();
  }
}
