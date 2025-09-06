import common.exceptions.EnvNotFoundException;
import common.exceptions.LogException;
import common.io.Printer;
import server.network.ServerNetwork;

/**
 * The {@code Server} class serves as the entry point for the application. It initializes and starts
 * the application's core components.
 */
public class Server {
  public static void main(String[] args) {
    String port;
    if (args.length != 0) {
      port = args[0];
    } else {
      port = "4004";
    }
    ServerNetwork serverNetwork = new ServerNetwork(port);
    Runtime.getRuntime().addShutdownHook(new Thread(serverNetwork::shutdown));
    try {
      serverNetwork.init();
    } catch (LogException | EnvNotFoundException e) {
      Printer.printError("Server error: " + e);
    }
  }
}
