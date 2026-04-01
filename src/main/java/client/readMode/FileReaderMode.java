package client.readMode;

import client.commandManager.CommandClassifier;
import client.ioStream.Renderer;
import client.network.ClientNetwork;
import common.dataProcessors.*;
import common.enums.CommandTypes;
import common.enums.FlatDataTypes;
import common.enums.HouseDataTypes;
import common.exceptions.LogException;
import common.exceptions.WrongCommandException;
import common.exceptions.WrongFileInputException;
import common.exceptions.WrongInputException;
import common.io.LogUtil;
import common.io.Printer;
import common.objects.Account;
import common.objects.Flat;
import common.packets.Request;
import common.packets.Response;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code FileReaderMode} class implements the {@link ReaderMode} interface and provides
 * functionality to read commands and data from a file and execute them.
 */
public class FileReaderMode implements ReaderMode {
  private final CommandClassifier commandClassifier;

  public FileReaderMode() {
    commandClassifier = new CommandClassifier();
    commandClassifier.init();
  }

  public Flat build(LinkedList<String> commandList) throws LogException, IOException {
    List<String> flatInfo = new LinkedList<>();
    List<String> houseInfo = new LinkedList<>();

    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.STRING));
    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.COORDINATE_X));
    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.COORDINATE_Y));
    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.DATE));
    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.AREA));
    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.ROOMS));
    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.SPACE));
    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.HEATING));
    flatInfo.add(getFlatFileInput(commandList, FlatDataTypes.TRANSPORT));
    if (commandList.getFirst().equals("yes")) {
      commandList.removeFirst();
      houseInfo.add(getHouseFileInput(commandList, HouseDataTypes.STRING));
      houseInfo.add(getHouseFileInput(commandList, HouseDataTypes.YEAR));
      houseInfo.add(getHouseFileInput(commandList, HouseDataTypes.LIFTS));
    } else {
      commandList.removeFirst();
      for (int i = 0; i < 3; i++) {
        houseInfo.add("null");
      }
    }
    return ObjBuilder.buildFlat(flatInfo, houseInfo);
  }

  public String getFlatFileInput(LinkedList<String> commandList, FlatDataTypes dataType) {
    String str = commandList.getFirst();
    commandList.removeFirst();
    boolean check = ObjInputChecker.checkFlatInput(str, dataType);
    if (check) {
      return str;
    } else {
      throw new WrongInputException();
    }
  }

  public String getHouseFileInput(LinkedList<String> commandList, HouseDataTypes dataType) {
    String str = commandList.getFirst();
    commandList.removeFirst();
    boolean check = ObjInputChecker.checkHouseInput(str, dataType);
    if (check) {
      return str;
    } else {
      throw new WrongInputException();
    }
  }

  public String getCommand(LinkedList<String> commandList) throws WrongFileInputException {
    String input = commandList.getFirst();
    commandList.removeFirst();
    if (input == null || input.isEmpty()) return null;
    if (!InputChecker.checkInput(input)) {
      throw new WrongFileInputException();
    }
    if (!commandClassifier.isCommand(InputSplitter.getCommand(input)))
      try {
        throw new WrongCommandException();
      } catch (WrongCommandException e) {
        Printer.printError(e.toString());
      }
    return input;
  }

  @Override
  public void execute(
      Renderer renderer,
      ClientNetwork clientNetwork,
      String commandName,
      String currentFile,
      Account account)
      throws LogException {
    LinkedList<String> commandList = seekForward(currentFile);
    try {
      while (!commandList.isEmpty()) {
        String input;
        if ((input = getCommand(commandList)) != null) {
          String command = InputSplitter.getCommand(input);
          Printer.printInfo(command);
          if (commandClassifier.isCommand(command)) {
            String argument = InputSplitter.getArg(input);
            CommandTypes type = commandClassifier.getCommandClassifier(command);
            switch (type) {
              case NO_INPUT_NEEDED -> {
                Response responce = clientNetwork.respond(new Request(command, argument, null));
                renderer.printResponse(responce);
              }
              case INPUT_NEEDED -> {
                Flat flat = build(commandList);
                Response response =
                    clientNetwork.respond(new Request(command, argument, flat, account));
                renderer.printResponse(response);
              }
            }
          }
        }
      }
    } catch (IOException e) {
      LogUtil.logServerError(e);
    }
  }

  private LinkedList<String> seekForward(String currentFile) throws LogException {
    int loopCount = 0;
    LinkedList<String> fileLoop = new LinkedList<>();
    LinkedList<String> commandList = new LinkedList<>();
    try {
      InputReader inputReader = new InputReader();
      inputReader.setReader(currentFile);
      LinkedList<String> commandLines = inputReader.readLines();
      for (String commandLine : commandLines) {
        String command = InputSplitter.getCommand(commandLine);
        if (!command.equals("execute_script")) {
          commandList.add(commandLine);
        } else {
          fileLoop.add(currentFile);
          String fileName = InputSplitter.getArg(commandLine);
          commandList = seekForward(commandList, fileName, loopCount, fileLoop);
        }
      }
    } catch (IOException e) {
      LogUtil.logClientError(e);
      Printer.printError("File not found or with no access");
    }
    return commandList;
  }

  private LinkedList<String> seekForward(
      LinkedList<String> commandList,
      String currentFile,
      int loopCount,
      LinkedList<String> fileLoop) {
    loopCount += 1;
    if (!fileLoop.contains(currentFile)) {
      fileLoop.add(currentFile);
      try {
        InputReader inputReader = new InputReader();
        inputReader.setReader(currentFile);
        LinkedList<String> commandLines = inputReader.readLines();
        for (String commandLine : commandLines) {
          String command = InputSplitter.getCommand(commandLine);
          if (!command.equals("execute_script")) {
            commandList.add(commandLine);
          } else {
            fileLoop.add(currentFile);
            String fileName = InputSplitter.getArg(commandLine);
            commandList = seekForward(commandList, fileName, loopCount, fileLoop);
          }
        }
      } catch (IOException e) {
        LogUtil.logClientError(e);
        Printer.printError(
            "File not found or with no access founded in script: " + e.getMessage().split(" ")[0]);
      }
    } else if (loopCount == 10) {
      Printer.printCondition(loopCount + " times jumping is enough I think");
    } else if (loopCount > 5) {
      Printer.printCondition(
          "You really want to jump more than " + (loopCount - 1) + " times? That's wild");
    } else {
      Printer.printCondition("File loop detected, executing commands in all files once");
      return commandList;
    }
    return commandList;
  }
}
