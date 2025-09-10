package server.network;

import common.dataProcessors.InputSplitter;
import common.exceptions.EnvNotFoundException;
import common.exceptions.LogException;
import common.io.LogUtil;
import common.io.Printer;
import common.packets.Request;
import common.packets.Response;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import server.commandManager.CommandManager;
import server.database.CollectionManager;
import server.database.DatabaseConnectionManager;
import server.database.DatabaseManager;
import server.ioStream.Invoker;
import server.ioStream.Receiver;

/**
 * The {@code Controller} class is the main entry point for processing user commands. It initializes
 * and manages various components, including file reading, command classification, execution, and
 * collection management. It interacts with the user through the console, providing instructions and
 * feedback.
 */
public class Processor implements Callable<Response> {
  private final BlockingQueue<Request> requestQueue;
  private final BlockingQueue<Response> responseQueue;
  private Invoker invoker;
  private Receiver receiver;
  private CommandManager commandManager;
  private DatabaseConnectionManager connectionManager;
  private DatabaseManager databaseManager;

  public Processor(BlockingQueue<Request> requestQueue, BlockingQueue<Response> responseQueue) {
    this.requestQueue = requestQueue;
    this.responseQueue = responseQueue;
    Printer.printInfo("Server console");
  }

  public void prepare() throws EnvNotFoundException, LogException {
    commandManager = new CommandManager();
    commandManager.init();
    connectionManager = new DatabaseConnectionManager();
    connectionManager.loadEnv();
    databaseManager = new DatabaseManager(connectionManager);
    CollectionManager collectionManager = new CollectionManager(databaseManager);
    collectionManager.loadData();

    receiver = new Receiver(collectionManager, commandManager, connectionManager);
    invoker = new Invoker(commandManager, receiver);

    Printer.printInfo("Usable " + commandManager.getServerCommandString());
  }

  public void processServer(String input) throws LogException {
    String command = InputSplitter.getCommand(input);
    String argument = InputSplitter.getArg(input);
    if (commandManager.isServerCommand(input)) {
      invoker.call(new Request(command, argument, null));
    } else {
      Printer.printError("Command not supported:");
      Printer.printCondition(commandManager.getServerCommandString());
    }
  }

  public Response processClient(Request request) throws LogException {
    Response response;
    response = invoker.call(request);
    response.addNotice(request.getCommand() + " command executed");
    response.setAddress(request.getAddress());
    //          response.getNotice().forEach(Printer::printCondition);
    //          response.getResult().forEach(Printer::printResult);
    return response;
  }

  @Override
  public Response call() {
    Response result = null;
    try {
      Request request = requestQueue.take();
      result = processClient(request);
      responseQueue.put(result);
    } catch (InterruptedException | LogException e) {
      LogUtil.logServerError(e);
      Printer.printError("Logged error occurred");
    }
    return result;
  }
}
