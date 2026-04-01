package client.ioStream;

import common.io.Printer;
import common.packets.Response;
import java.util.List;

public class Renderer {
  public Renderer() {}

  public void printResponse(Response response) {
    if (response != null) {
      List<String> result = response.getResult();
      if (result != null && !result.isEmpty()) {
        switch (result.get(0)) {
          case "//pinged||" -> Printer.printResult("pong");
          case "||signed_in//" -> Printer.printResult("You are signed in");
          case "||signed_up//" -> Printer.printResult("You are signed up");
          default -> result.forEach(Printer::printResult);
        }
      }
      List<String> notice = response.getNotice();
      if (notice != null) notice.forEach(Printer::printCondition);
    }
  }
}
