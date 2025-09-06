package server.network;

import common.exceptions.EnvNotFoundException;
import common.exceptions.LogException;
import common.io.LogUtil;
import common.io.Printer;
import common.packets.Request;
import common.packets.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.*;

public class ServerNetwork {
  private final ExecutorService readerRequest = Executors.newCachedThreadPool();
  private final ForkJoinPool handlerRequest = new ForkJoinPool(5);
  private final ExecutorService writerResponse = Executors.newFixedThreadPool(5);
  private int PORT;
  private Processor processor;
  private Sender sender;
  private Selector selector;
  private Recipent recipent;
  private boolean isAwaken;
  private DatagramChannel channel;
  private BlockingQueue<Request> requestQueue;
  private BlockingQueue<Response> responseQueue;
  private BlockingQueue<SelectionKey> reRegisterKeys;

  public ServerNetwork(String port) {
    isAwaken = true;
    portResolve(port);
  }

  public void init() throws LogException, EnvNotFoundException {
    LogUtil.logServerInfo("Server started");
    reRegisterKeys = new LinkedBlockingQueue<>();
    requestQueue = new LinkedBlockingQueue<>();
    responseQueue = new LinkedBlockingQueue<>();
    processor = new Processor(this.requestQueue, this.responseQueue);
    processor.prepare();
    establish();
    recipent = new Recipent(this.channel, this.requestQueue);
    sender = new Sender(this.channel, this.responseQueue);
    handle();
  }

  private void establish() {
    try {
      channel = DatagramChannel.open();
      channel.socket().bind(new InetSocketAddress("127.0.0.1", PORT));
      channel.configureBlocking(false);
      selector = Selector.open();
      channel.register(selector, SelectionKey.OP_READ);
    } catch (IOException e) {
      Printer.printError(
          "Error establish connection on port " + PORT + ", please choose another port");
      exit();
    }
  }

  private void portResolve(String port) {
    try {
      int PORT = Integer.parseInt(port);
      if (PORT > 65535 || PORT < 1025) {
        Printer.printError("Port number can't exceed range [1025-65535]");
        exit();
      } else {
        this.PORT = PORT;
      }
    } catch (NumberFormatException f) {
      Printer.printError("Wrong input format");
      exit();
    }
  }

  private void exit() {
    readerRequest.shutdownNow();
    handlerRequest.shutdownNow();
    writerResponse.shutdownNow();
    System.exit(0);
  }

  public void handle() throws LogException {
    LogUtil.logServerInfo("Listening on port " + PORT);
    while (isAwaken) {
      try {
        while (!reRegisterKeys.isEmpty()) {
          SelectionKey key = reRegisterKeys.poll();
          key.interestOps(SelectionKey.OP_READ);
        }
        if (selector.selectNow() == 0) {
          BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
          if (br.ready()) {
            String input = br.readLine();
            processor.processServer(input);
          }
        }
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove();
          if (key.isValid() && key.isReadable()) {
            key.interestOps(0);
            CompletableFuture.runAsync(() -> recipent.call(), readerRequest)
                .thenRunAsync(
                    () -> {
                      reRegisterKeys.add(key);
                      selector.wakeup();
                    })
                .thenRunAsync(() -> handlerRequest.submit(processor), handlerRequest)
                .thenRunAsync(() -> writerResponse.submit(sender), writerResponse);
          }
          selector.selectedKeys().clear();
        }
      } catch (IOException e) {
        throw new LogException("Select failed");
      }
    }
  }

  public void shutdown() {
    LogUtil.logServerInfo("Exit detected...");
    isAwaken = false;
  }
}
