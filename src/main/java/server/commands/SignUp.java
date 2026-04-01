package server.commands;

import common.enums.CommandFormats;
import common.enums.CommandTypes;
import common.exceptions.LogException;
import common.packets.Request;
import common.packets.Response;
import server.ioStream.Receiver;

public class SignUp extends Command {
  private Receiver receiver;

  public SignUp() {
    super(
        "sign_up",
        "",
        "create account to access program",
        CommandTypes.NO_INPUT_NEEDED,
        CommandFormats.WITHOUT_ARG);
  }

  public void setReceiver(Receiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public Response execute(Request request) throws LogException {
    return receiver.signUp(request);
  }
}
