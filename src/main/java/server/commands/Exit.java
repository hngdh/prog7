package server.commands;

import common.enums.CommandFormats;
import common.enums.CommandTypes;
import common.packets.Request;
import common.packets.Response;
import server.ioStream.Receiver;

public class Exit extends Command {
  private Receiver receiver;

  public Exit() {
    super("exit", "", "exit the program", CommandTypes.NO_INPUT_NEEDED, CommandFormats.WITHOUT_ARG);
  }

  public void setReceiver(Receiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public Response execute(Request request) {
    return receiver.exit();
  }
}
