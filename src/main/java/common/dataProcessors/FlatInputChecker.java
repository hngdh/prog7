package common.dataProcessors;

import common.enums.Transport;

/**
 * The {@code HouseInputChecker} class provides static methods for validating input strings related
 * to {@link common.objects.Flat} properties. It extends the {@link InputChecker} class and provides
 * specific checks for name, coordination, area, ...
 */
public class FlatInputChecker extends InputChecker {
  public FlatInputChecker() {}

  public static boolean checkName(String name) {
    return checkString(name);
  }

  public static boolean checkCoorX(String coor) {
    return checkFloat(coor) && Float.parseFloat(coor) > -500;
  }

  public static boolean checkCoorY(String coor) {
    return checkFloat(coor);
  }

  public static boolean checkArea(String area) {
    return checkInteger(area) && 0 < Long.parseLong(area) && Long.parseLong(area) <= 700;
  }

  public static boolean checkNumberOfRooms(String rooms) {
    return checkInteger(rooms) && Integer.parseInt(rooms) > 0;
  }

  public static boolean checkLivingSpace(String area) {
    return checkInteger(area) && Long.parseLong(area) > 0;
  }

  public static boolean checkCentralHeating(String str) {
    return checkBoolean(str);
  }

  public static boolean checkTransport(String str) {
    if (str == null) {
      return false;
    }
    try {
      Transport.valueOf(str.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
