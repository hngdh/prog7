package common.dataProcessors;

/**
 * The {@code HouseInputChecker} class provides static methods for validating input strings related
 * to {@link common.objects.House} properties. It extends the {@link InputChecker} class and
 * provides specific checks for name, construction year and number of lifts.
 */
public class HouseInputChecker extends InputChecker {
  public HouseInputChecker() {}

  public static boolean checkName(String name) {
    return checkString(name);
  }

  public static boolean checkYear(String year) {
    return checkInteger(year) && Integer.parseInt(year) > 0;
  }

  public static boolean checkNumberOfLifts(String lifts) {
    return checkInteger(lifts) && Integer.parseInt(lifts) > 0;
  }
}
