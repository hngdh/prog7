package server.commands;

import common.enums.CommandFormats;
import common.enums.CommandTypes;
import common.packets.Request;
import common.packets.Response;
import server.ioStream.Receiver;

public class FilterContainsName extends Command {
  private Receiver receiver;

  public FilterContainsName() {
    super(
        "filter_contains_name",
        "name",
        "display elements with given name",
        CommandTypes.NO_INPUT_NEEDED,
        CommandFormats.WITH_STRING_ARG);
  }

  public void setReceiver(Receiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public Response execute(Request request) {
    return receiver.filter_contains_name(request);
  }
}
