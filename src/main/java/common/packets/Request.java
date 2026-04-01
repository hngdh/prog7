package common.packets;

import common.objects.Account;
import common.objects.Flat;
import java.io.Serializable;
import java.net.SocketAddress;

/**
 * The {@code Request} class represents a request containing an argument and a {@link Flat} object.
 * It is used to encapsulate the data needed for various operations on the collection of flats.
 */
public class Request implements Serializable {
  private final String command;
  private final String argument;
  private final Account account;
  private Flat flat;
  private SocketAddress address;

  public Request(String command, String argument, Flat flat, Account account) {
    this.command = command;
    this.argument = argument;
    this.flat = flat;
    this.account = account;
  }

  public Request(String command, String argument, Account account) {
    this.command = command;
    this.argument = argument;
    this.account = account;
  }

  public String getCommand() {
    return command;
  }

  public SocketAddress getAddress() {
    return address;
  }

  public void setAddress(SocketAddress address) {
    this.address = address;
  }

  public String getArgument() {
    return argument;
  }

  public Flat getFlat() {
    return flat;
  }

  public Account getAccount() {
    return account;
  }
}
