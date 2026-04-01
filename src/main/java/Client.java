import client.ioStream.Controller;

/**
 * The {@code Server} class serves as the entry point for the application. It initializes and starts
 * the application's core components.
 */
public class Client {
  public static void main(String[] args) {
    String port;
    if (args.length != 0) {
      port = args[0];
    } else port = "4004";
    Controller controller = new Controller();
    controller.prepare(port);
    controller.run();
  }
}
