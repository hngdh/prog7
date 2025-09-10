package server.network;

import common.exceptions.LogException;
import common.io.LogUtil;
import common.io.Printer;
import common.packets.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class Sender implements Callable<Response> {
  private DatagramChannel datagramChannel;
  private BlockingQueue<Response> responseQueue;

  public Sender(DatagramChannel datagramChannel, BlockingQueue<Response> responseQueue) {
    this.datagramChannel = datagramChannel;
    this.responseQueue = responseQueue;
  }

  public void sendResponse(Response response) throws LogException {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(response);
      oos.flush();
      ByteBuffer buf = ByteBuffer.wrap(baos.toByteArray());
      datagramChannel.send(buf, response.getAddress());
      LogUtil.logServerInfo("Response sent to " + response.getAddress());
    } catch (IOException e) {
      LogUtil.logServerError(e);
      throw new LogException("Error sending response");
    }
  }

  @Override
  public Response call() {
    Response response = null;
    try {
      response = responseQueue.take();
      sendResponse(response);
    } catch (LogException e) {
      Printer.printError(e.getMessage());
    } catch (InterruptedException e) {
      LogUtil.logServerError(e);
      Printer.printError("Logged error occurred");
    }
    return response;
  }
}
