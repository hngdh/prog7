package server.commands;

import common.enums.CommandFormats;
import common.enums.CommandTypes;
import common.exceptions.LogException;
import common.packets.Request;
import common.packets.Response;
import server.ioStream.Receiver;

public class SignIn extends Command {
  private Receiver receiver;

  public SignIn() {
    super(
        "sign_in",
        "",
        "access program with username and password",
        CommandTypes.NO_INPUT_NEEDED,
        CommandFormats.WITHOUT_ARG);
  }

  public void setReceiver(Receiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public Response execute(Request request) throws LogException {
    return receiver.signIn(request);
  }
}
