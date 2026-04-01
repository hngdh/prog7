package common.dataProcessors;

import common.enums.FlatDataTypes;
import common.enums.HouseDataTypes;

/**
 * The {@code ObjInputChecker} class provides static methods for validating input strings for
 * different object properties, specifically for {@link common.objects.Flat} and {@link
 * common.objects.House} objects.
 */
public class ObjInputChecker {
  public ObjInputChecker() {}

  public static boolean checkFlatInput(String str, FlatDataTypes type) {
    return switch (type) {
      case ID -> str.matches("[0-9]+");
      case STRING -> FlatInputChecker.checkName(str);
      case COORDINATE_X -> FlatInputChecker.checkCoorX(str);
      case COORDINATE_Y -> FlatInputChecker.checkCoorY(str);
      case DATE -> FlatInputChecker.checkDate(str);
      case AREA -> FlatInputChecker.checkArea(str);
      case ROOMS -> FlatInputChecker.checkNumberOfRooms(str);
      case SPACE -> FlatInputChecker.checkLivingSpace(str);
      case HEATING -> FlatInputChecker.checkCentralHeating(str);
      case TRANSPORT -> FlatInputChecker.checkTransport(str);
    };
  }

  public static boolean checkHouseInput(String str, HouseDataTypes type) {
    return switch (type) {
      case STRING -> HouseInputChecker.checkName(str);
      case YEAR -> HouseInputChecker.checkYear(str);
      case LIFTS -> HouseInputChecker.checkNumberOfLifts(str);
    };
  }
}
