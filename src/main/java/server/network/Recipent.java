package server.network;

import common.exceptions.LogException;
import common.io.LogUtil;
import common.io.Printer;
import common.packets.Request;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class Recipent implements Callable<Request> {
  private final BlockingQueue<Request> requestQueue;
  private final DatagramChannel channel;
  private final int MAX_PACKET_SIZE = 65536;
  private final ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_SIZE);

  public Recipent(DatagramChannel channel, BlockingQueue<Request> requestQueue) {
    this.requestQueue = requestQueue;
    this.channel = channel;
  }

  public Request readRequest() throws LogException {
    try {
      String address;
      SocketAddress remoteAddress = channel.receive(buffer);
      buffer.flip();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
      ObjectInputStream ois = new ObjectInputStream(bais);
      Request request = (Request) ois.readObject();
      if (request.getAccount() != null) {
        address = request.getAccount().getUser();
      } else {
        address = remoteAddress.toString();
      }
      LogUtil.logServerInfo("Received '" + request.getCommand() + "' from: " + address);
      buffer.clear();
      request.setAddress(remoteAddress);
      return request;
    } catch (IOException | ClassNotFoundException e) {
      LogUtil.logServerError(e);
      throw new LogException("Error reading request");
    }
  }

  @Override
  public Request call() {
    Request request = null;
    try {
      request = readRequest();
      requestQueue.put(request);
    } catch (LogException e) {
      Printer.printError(e);
    } catch (InterruptedException e) {
      LogUtil.logServerError(e);
      Printer.printError("Logged error occurred");
    }
    return request;
  }
}
