package client.readMode;

import client.ioStream.Renderer;
import client.network.ClientNetwork;
import common.exceptions.LogException;
import common.objects.Account;
import java.io.IOException;

/**
 * The {@code ReadMode} interface defines the contract for different reading modes used by the
 * application. Implementing classes are responsible for reading input, processing it, and executing
 * commands.
 */
public interface ReaderMode {
  void execute(
      Renderer renderer,
      ClientNetwork clientNetwork,
      String commandName,
      String arg,
      Account account)
      throws LogException, IOException;
}
