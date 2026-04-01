package client.readMode;

import client.ioStream.Renderer;
import client.network.ClientNetwork;
import common.dataProcessors.InputChecker;
import common.dataProcessors.InputReader;
import common.dataProcessors.ObjBuilder;
import common.dataProcessors.ObjInputChecker;
import common.enums.FlatDataTypes;
import common.enums.HouseDataTypes;
import common.exceptions.LogException;
import common.exceptions.WrongInputException;
import common.io.Printer;
import common.objects.Account;
import common.objects.Flat;
import common.packets.Request;
import common.packets.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class ConsoleReaderMode implements ReaderMode {
  public ConsoleReaderMode() {}

  public Flat build(InputReader reader) throws LogException, IOException {
    List<String> flatInfo = new LinkedList<>();
    List<String> houseInfo = new LinkedList<>();

    Printer.printInfo("Please type flat info");
    flatInfo.add(
        getFlatUserInput(reader, FlatDataTypes.STRING, "Flat's name", "must not be empty", ""));
    flatInfo.add(
        getFlatUserInput(
            reader, FlatDataTypes.COORDINATE_X, "Flat's X coordinate", "must not be empty", ""));
    flatInfo.add(
        getFlatUserInput(
            reader, FlatDataTypes.COORDINATE_Y, "Flat's Y coordinate", "must not be empty", ""));
    flatInfo.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    flatInfo.add(
        getFlatUserInput(
            reader, FlatDataTypes.AREA, "Flat's area", "must not be empty and less than 700", ""));
    flatInfo.add(
        getFlatUserInput(
            reader, FlatDataTypes.ROOMS, "Flat's number of rooms", "must not be empty", ""));
    flatInfo.add(
        getFlatUserInput(
            reader, FlatDataTypes.SPACE, "Flat's living space", "must not be empty", ""));
    flatInfo.add(
        getFlatUserInput(
            reader,
            FlatDataTypes.HEATING,
            "Flat's existence of central heating",
            "must not be empty",
            "true, false"));
    flatInfo.add(
        getFlatUserInput(
            reader,
            FlatDataTypes.TRANSPORT,
            "Flat's local transport",
            "must not be empty",
            "few, little, normal"));
    if (InputChecker.checkOptional("insert", "house's details")) {
      Printer.printInfo("Please type house info");
      houseInfo.add(
          getHouseUserInput(reader, HouseDataTypes.STRING, "House's name", "must not be empty"));
      houseInfo.add(
          getHouseUserInput(
              reader,
              HouseDataTypes.YEAR,
              "House's construction year",
              "must not be empty and positive number"));
      houseInfo.add(
          getHouseUserInput(
              reader,
              HouseDataTypes.LIFTS,
              "House's number of lifts",
              "must not be empty and positive number"));
    } else {
      for (int i = 0; i < 3; i++) {
        houseInfo.add("null");
      }
    }
    return ObjBuilder.buildFlat(flatInfo, houseInfo);
  }

  public String getFlatUserInput(
      InputReader reader,
      FlatDataTypes dataType,
      String description,
      String condition,
      String choices)
      throws IOException {
    boolean check = false;
    String temp = "";
    while (!check) {
      String sentence = description + " (" + condition + ')';
      if (!choices.isEmpty()) sentence += ", choose from [" + choices + ']';
      sentence += ':';
      Printer.printCondition(sentence);
      try {
        String str = reader.readLine();
        check = ObjInputChecker.checkFlatInput(str, dataType);
        if (check) {
          if (str == null || str.isEmpty()) {
            temp = "null";
          } else {
            temp = str;
          }
          break;
        } else {
          throw new WrongInputException();
        }
      } catch (WrongInputException | NumberFormatException e) {
        Printer.printError(e);
      }
    }
    return temp;
  }

  public String getHouseUserInput(
      InputReader reader, HouseDataTypes dataType, String description, String condition)
      throws IOException {
    boolean check = false;
    String temp = "";
    while (!check) {
      Printer.printCondition(description + '(' + condition + ')');
      try {
        String str = reader.readLine();
        check = ObjInputChecker.checkHouseInput(str, dataType);
        if (check) {
          if (str == null || str.isEmpty()) {
            temp = "null";
          } else {
            temp = str;
          }
          break;
        } else {
          throw new WrongInputException();
        }
      } catch (WrongInputException | NumberFormatException e) {
        Printer.printError(e);
      }
    }
    return temp;
  }

  @Override
  public void execute(
      Renderer renderer,
      ClientNetwork clientNetwork,
      String command,
      String argument,
      Account account)
      throws LogException, IOException {
    InputReader reader = new InputReader();
    reader.setReader();
    Request request = new Request(command, argument, build(reader), account);
    Response response = clientNetwork.respond(request);
    renderer.printResponse(response);
  }
}
