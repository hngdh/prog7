package server.ioStream;

import common.exceptions.LogException;
import common.io.LogUtil;
import common.objects.Account;
import common.objects.Flat;
import common.packets.Request;
import common.packets.Response;
import java.util.*;
import server.commandManager.CommandManager;
import server.database.AccountWorker;
import server.database.CollectionManager;
import server.database.DatabaseConnectionManager;

/**
 * The {@code Recipent} class contains the actual implementation of the commands. It interacts with
 * the {@link CollectionManager} to perform operations on the collection and the {@link
 * CommandManager} to access command information. It handles user requests by delegating to the
 * appropriate methods of the CollectionManager.
 */
public class Receiver {
  private final CollectionManager collectionManager;
  private final CommandManager commandManager;
  private final AccountWorker accountWorker;
  private HashMap<Integer, Integer> idResolver;

  public Receiver(
      CollectionManager collectionManager,
      CommandManager commandManager,
      DatabaseConnectionManager connectionManager) {
    this.commandManager = commandManager;
    this.collectionManager = collectionManager;
    accountWorker = new AccountWorker(connectionManager);
    idResolver = getIDResolver();
  }

  private boolean notEmpty() {
    LinkedList<Flat> collection = collectionManager.getCollection();
    return !collection.isEmpty();
  }

  private HashMap<Integer, Integer> getIDResolver() {
    LinkedList<Flat> collection = collectionManager.getCollection();
    HashMap<Integer, Integer> idResolver = new HashMap<>();
    collection.forEach((flat) -> idResolver.put(flat.getId(), idResolver.size()));
    return idResolver;
  }

  public Response signIn(Request request) throws LogException {
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    Account account = request.getAccount();
    if (accountWorker.isAccount(account)) {
      result.add("||signed_in//");
      LogUtil.logServerInfo("User " + request.getAccount().getUser() + " signed in");
    } else {
      notice.add("Your username or password is incorrect");
    }
    return new Response(notice, result);
  }

  public Response signUp(Request request) {
    Response response = new Response();
    Account account = request.getAccount();
    try {
      if (accountWorker.insertAccount(account)) {
        response.addResult("||signed_up//");
        LogUtil.logServerInfo("User " + request.getAccount().getUser() + " signed up");
      } else {
        response.addNotice("Your username or password is incorrect");
      }
    } catch (Exception e) {
      response.addNotice(e.toString());
    }
    return response;
  }

  public Response exit() {
    System.exit(0);
    return new Response();
  }

  public Response signOut(Request request) {
    Response response = new Response();
    response.addResult("You are signed out");
    LogUtil.logServerInfo("User " + request.getAccount().getUser() + " signed out");
    return response;
  }

  public Response ping() {
    Response response = new Response();
    response.addResult("//pinged||");
    return response;
  }

  public Response help() {
    Response response = new Response();
    commandManager
        .getClientCommandCollection()
        .forEach((name, command) -> response.addResult(command.getCommandInfo()));
    return response;
  }

  public Response clear() {
    Response response = new Response();
    collectionManager.clear();
    response.addResult("Collection cleared");
    return response;
  }

  public Response clearByCreator(Request request) {
    Response response = new Response();
    collectionManager.clearByCreator(request);
    response.addResult("Collection cleared by " + request.getAccount().getUser());
    return response;
  }

  public Response info() {
    Response response = new Response();
    response.addResult("Type of DS: LinkedList");
    response.addResult("Number of elements: " + collectionManager.getCollection().size());
    return response;
  }

  public Response show() {
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    if (notEmpty()) {
      collectionManager
          .getCollection()
          .forEach(
              flat -> {
                result.addAll(flat.getEverything());
                result.add("");
              });
    } else {
      notice.add("This collection is empty");
    }
    return new Response(notice, result);
  }

  public Response sort() {
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    if (notEmpty()) {
      collectionManager.sort();
    } else {
      notice.add("This collection is empty");
    }
    return new Response(notice, result);
  }

  public Response min_by_coordinates() {
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    if (notEmpty()) {
      collectionManager.min_by_coordinates();
      collectionManager
          .getCollection()
          .forEach(
              flat -> {
                result.addAll(flat.getEverything());
                result.add("");
              });
    } else {
      notice.add("This collection is empty");
    }
    return new Response(notice, result);
  }

  public Response add(Request request) {
    Response response = new Response();
    collectionManager.add(request);
    return response;
  }

  public Response filter_contains_name(Request request) {
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    if (notEmpty()) {
      String name = request.getArgument();
      collectionManager
          .getCollection()
          .forEach(
              flat -> {
                if (flat.getName().contains(name)) {
                  result.addAll(flat.getEverything());
                  result.add("");
                }
              });
      if (result.isEmpty()) result.add("Nothing found");
    } else {
      notice.add("This collection is empty");
    }
    return new Response(notice, result);
  }

  public Response print_field_ascending_house() {
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    if (notEmpty()) {
      LinkedList<Flat> list = new LinkedList<>();
      collectionManager
          .getCollection()
          .forEach(
              flat -> {
                if (flat.getHouse() != null) list.add(flat);
              });
      list.sort(Comparator.comparing(a -> a.getHouse().getName()));
      list.forEach(flat -> result.add(flat.getPropsHouse().toString()));
    } else {
      notice.add("This collection is empty");
    }
    return new Response(notice, result);
  }

  public Response remove_by_id(Request request) {
    idResolver = getIDResolver();
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    int id = Integer.parseInt(request.getArgument());
    if (idResolver.containsKey(id)) {
      int collectionID = idResolver.get(id);
      String creator = request.getAccount().getUser();
      if (notEmpty()) {
        if (collectionManager.getCollection().get(collectionID).getCreator().equals(creator)) {
          collectionManager.remove_by_id(id, collectionID);
        } else {
          notice.add("Flat not owned by you");
        }
      } else {
        notice.add("This collection is empty");
      }
    } else {
      notice.add("ID not existed");
    }
    return new Response(notice, result);
  }

  public Response remove_first(Request request) {
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    if (notEmpty()) {
      collectionManager.remove_first(request);
    } else {
      notice.add("This collection is empty");
    }
    return new Response(notice, result);
  }

  public Response remove_lower(Request request) {
    List<String> notice = new ArrayList<>();
    List<String> result = new ArrayList<>();
    if (notEmpty()) {
      collectionManager.remove_lower(request);
    } else {
      notice.add("This collection is empty");
    }
    return new Response(notice, result);
  }

  public Response update(Request request) {
    Response response = new Response();
    idResolver = getIDResolver();
    if (notEmpty()) {
      int id = Integer.parseInt(request.getArgument());
      int collectionID = idResolver.get(id);
      Flat flat = request.getFlat();
      flat.setId(id);
      if (idResolver.containsKey(id)) {
        collectionManager.update(flat, collectionID);
      } else {
        response.addNotice("Key doesn't exist");
      }
    } else {
      response.addNotice("This collection is empty");
    }
    return response;
  }
}
