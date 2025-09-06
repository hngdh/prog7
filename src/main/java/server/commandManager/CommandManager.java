package server.commandManager;

import java.util.TreeMap;
import server.commands.*;

/**
 * The {@code CommandManager} class manages the registration and retrieval of commands. It stores
 * commands in a HashMap for quick access, including properties' getters.
 */
public class CommandManager {
  private final TreeMap<String, Command> commandClient = new TreeMap<>();
  private final TreeMap<String, Command> commandServer = new TreeMap<>();
  private final TreeMap<String, Command> commandCollection = new TreeMap<>();

  public CommandManager() {}

  public void init() {
    registerCommandClient(new Add());
    registerCommandClient(new Clear());
    registerCommandClient(new Exit());
    registerCommandClient(new ExecuteScript());
    registerCommandClient(new FilterContainsName());
    registerCommandClient(new Help());
    registerCommandClient(new Info());
    registerCommandClient(new MinByCoordinate());
    registerCommandClient(new Ping());
    registerCommandClient(new PrintFieldAscendingHouse());
    registerCommandClient(new RemoveByID());
    registerCommandClient(new RemoveFirst());
    registerCommandClient(new RemoveLower());
    registerCommandClient(new SignIn());
    registerCommandClient(new SignOut());
    registerCommandClient(new SignUp());
    registerCommandClient(new Show());
    registerCommandClient(new Sort());
    registerCommandClient(new Update());

    registerCommandServer(new ClearAdmin());
    registerCommandServer(new Exit());
    registerCommandServer(new Reload());

    commandCollection.putAll(commandClient);
    commandCollection.putAll(commandServer);
  }

  public void registerCommandClient(Command command) {
    commandClient.put(command.getName(), command);
  }

  public void registerCommandServer(Command command) {
    commandServer.put(command.getName(), command);
  }

  public Command getCommand(String commandName) {
    return commandCollection.get(commandName);
  }

  public TreeMap<String, Command> getClientCommandCollection() {
    return commandClient;
  }

  public String getServerCommandString() {
    return commandServer.keySet().toString();
  }

  public boolean isClientCommand(String commandName) {
    return commandClient.containsKey(commandName);
  }

  public boolean isServerCommand(String commandName) {
    return commandServer.containsKey(commandName);
  }
}
