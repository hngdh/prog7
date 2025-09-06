package server.commands;

import common.enums.CommandFormats;
import common.enums.CommandTypes;
import common.exceptions.LogException;
import common.packets.Request;
import common.packets.Response;
import server.ioStream.Receiver;

public class ClearAdmin extends Command {
  private Receiver receiver;

  public ClearAdmin() {
    super(
        "clear_admin",
        "",
        "clear all collection (available only on server",
        CommandTypes.NO_INPUT_NEEDED,
        CommandFormats.WITHOUT_ARG);
  }

  @Override
  public void setReceiver(Receiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public Response execute(Request request) throws LogException {
    return receiver.clear();
  }
}
