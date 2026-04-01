package common.packets;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Response implements Serializable {
  private List<String> notice = new ArrayList<>();
  private List<String> result = new ArrayList<>();
  private SocketAddress address;

  public Response(List<String> notice, List<String> result) {
    this.notice = notice;
    this.result = result;
  }

  public Response() {}

  public void addNotice(String notice) {
    this.notice.add(notice);
  }

  public void addResult(String notice) {
    this.result.add(notice);
  }

  public List<String> getNotice() {
    return notice;
  }

  public List<String> getResult() {
    return result;
  }

  public SocketAddress getAddress() {
    return address;
  }

  public void setAddress(SocketAddress address) {
    this.address = address;
  }
}
