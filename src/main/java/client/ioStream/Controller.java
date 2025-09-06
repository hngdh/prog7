package client.ioStream;

import client.authorization.AccountController;
import client.commandManager.CommandClassifier;
import client.commandManager.FormatChecker;
import client.network.ClientNetwork;
import client.readMode.ModeManager;
import common.dataProcessors.InputChecker;
import common.dataProcessors.InputReader;
import common.dataProcessors.InputSplitter;
import common.enums.CommandTypes;
import common.exceptions.LogException;
import common.exceptions.WrongCommandException;
import common.exceptions.WrongInputFormatException;
import common.io.LogUtil;
import common.io.Printer;
import common.packets.Request;
import common.packets.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller {
  private InputReader inputReader;
  private ModeManager modeManager;
  private FormatChecker formatChecker;
  private CommandClassifier commandClassifier;
  private ClientNetwork clientNetwork;
  private AccountController accountController;
  private Renderer renderer;
  private boolean isAwake;

  public Controller() {
    Printer.printInfo("Client started");
    Printer.printInfo("Type 'sign_in' to sign in or 'sign_up' to sign up");
  }

  public void prepare(String port) {
    clientNetwork = new ClientNetwork(port);
    inputReader = new InputReader();
    inputReader.setReader();
    formatChecker = new FormatChecker();
    formatChecker.init();
    modeManager = new ModeManager();
    modeManager.init();
    commandClassifier = new CommandClassifier();
    commandClassifier.init();
    accountController = new AccountController();
    renderer = new Renderer();
    isAwake = true;
  }

  public void run() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while (isAwake) {
      try {
        String input = reader.readLine();
        preprocess(input);
      } catch (WrongInputFormatException | WrongCommandException e) {
        Printer.printError(e.toString());
        Printer.printCondition("Command couldn't be executed!");
      } catch (IOException e) {
        LogUtil.logClientError(e);
      }
    }
  }

  public void preprocess(String input)
      throws IOException, WrongInputFormatException, WrongCommandException {
    if (!InputChecker.checkInput(input)) throw new WrongCommandException();
    formatChecker.checkFormat(InputSplitter.getCommand(input), InputSplitter.getArg(input));
    String command = InputSplitter.getCommand(input);
    String argument = InputSplitter.getArg(input);
    CommandTypes type = commandClassifier.getCommandClassifier(command);
    try {
      switch (command) {
        case "exit" -> exit();
        case "sign_in" -> accountController.signIn(clientNetwork, renderer);
        case "sign_up" -> accountController.signUp(clientNetwork, renderer);
        case "sign_out" -> {
          if (accountController.isSignIn()) signOut();
        }
        default -> {
          if (accountController.isSignIn()) {
            process(command, argument, type);
          } else {
            Printer.printCondition("Please sign in or sign up to continue");
          }
        }
      }
    } catch (LogException e) {
      Printer.printError(e);
    }
  }

  public void process(String command, String argument, CommandTypes type)
      throws LogException, IOException {
    Renderer renderer = new Renderer();
    switch (type) {
      case INPUT_NEEDED ->
          modeManager.call(
              renderer, clientNetwork, command, argument, accountController.getAccount());
      case NO_INPUT_NEEDED -> {
        Response response =
            clientNetwork.respond(new Request(command, argument, accountController.getAccount()));
        renderer.printResponse(response);
      }
    }
  }

  public void exit() {
    signOut();
    Printer.printInfo("Exiting...");
    System.exit(0);
  }

  public void signOut() {
    Response response =
        clientNetwork.respond(new Request("sign_out", null, accountController.getAccount()));
    renderer.printResponse(response);
    accountController.signOut();
  }
}
