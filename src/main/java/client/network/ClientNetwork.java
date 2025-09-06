package client.network;

import common.dataProcessors.InputReader;
import common.exceptions.NetworkException;
import common.io.LogUtil;
import common.io.Printer;
import common.packets.Request;
import common.packets.Response;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ClientNetwork {
  private final int MAX_PACKET_SIZE = 65536;
  private final ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
  private final InputReader reader = new InputReader();
  private int PORT;
  private DatagramChannel channel;
  private SocketAddress serverAddress;

  public ClientNetwork(String port) {
    reader.setReader();
    portResolve(port);
  }

  public void connect() {
    try {
      channel = DatagramChannel.open();
      serverAddress = new InetSocketAddress("127.0.0.1", PORT);
      channel.connect(serverAddress);
    } catch (IOException e) {
      LogUtil.logClientError(e);
      Printer.printError("Error opening channel");
    }
  }

  public void shutdown() {
    try {
      if (channel != null) channel.close();
    } catch (IOException e) {
      LogUtil.logClientError(e);
    }
  }

  public Response respond(Request request) {
    try {
      connect();
      sendPacket(channel, serverAddress, request);
      Response response = getResponse(channel);
      shutdown();
      return response;
    } catch (NetworkException e) {
      ping();
    }
    return null;
  }

  public void ping() {
    Request request = new Request("ping", null, null);
    try {
      connect();
      sendPacket(channel, serverAddress, request);
      getResponse(channel);
      Printer.printInfo("Connected on port " + PORT);
      shutdown();
    } catch (NetworkException e) {
      Printer.printCondition("Server not established on port " + PORT);
      exit();
    }
  }

  public void portResolve(String port) {
    try {
      int PORT = Integer.parseInt(port);
      if (PORT > 65535 || PORT < 1025) {
        Printer.printError("Port number can't exceed range [1025-65535]");
        exit();
      } else {
        this.PORT = PORT;
        ping();
      }
    } catch (NumberFormatException f) {
      Printer.printError("Wrong input format");
      exit();
    }
  }

  private void exit() {
    System.exit(0);
  }

  public void sendPacket(DatagramChannel channel, SocketAddress serverAddress, Request request) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(request);
      oos.flush();
      ByteBuffer buf = ByteBuffer.wrap(baos.toByteArray());
      channel.send(buf, serverAddress);
      LogUtil.logClientInfo(
          "Sending '" + request.getCommand() + "' from " + channel.getLocalAddress());
    } catch (IOException e) {
      LogUtil.logClientError(e);
    }
  }

  public Response getResponse(DatagramChannel channel) throws NetworkException {
    Response response = new Response();
    try {
      channel.receive(buffer);
      buffer.flip();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
      ObjectInputStream ois = new ObjectInputStream(bais);
      response = (Response) ois.readObject();
      buffer.clear();
      LogUtil.logClientInfo("Responded from " + channel.getLocalAddress());
    } catch (PortUnreachableException e) {
      throw new NetworkException();
    } catch (IOException | ClassNotFoundException e) {
      LogUtil.logClientError(e);
      Printer.printError("Logged error occurred");
    }
    return response;
  }
}
