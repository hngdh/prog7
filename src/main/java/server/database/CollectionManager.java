package server.database;

import common.exceptions.LogException;
import common.exceptions.database.InsertFailedException;
import common.exceptions.database.RemoveFailedException;
import common.io.Printer;
import common.objects.Flat;
import common.packets.Request;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The {@code CollectionManager} class manages a collection of {@link Flat} objects stored in a
 * {@link LinkedList}. It provides methods for adding, removing, updating, and retrieving elements
 * from the collection. It also handles saving and loading the collection to/from a CSV file.
 */
public class CollectionManager {
  private final LinkedList<Flat> collection = new LinkedList<>();
  private final DatabaseManager databaseManager;
  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock writeLock = readWriteLock.writeLock();

  public CollectionManager(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public LinkedList<Flat> getCollection() {
    return collection;
  }

  public void clearByCreator(Request request) {
    try {
      String creator = request.getAccount().getUser();
      databaseManager.clearByCreator(creator);
      writeLock.lock();
      Iterator<Flat> iterator = collection.iterator();
      iterator.forEachRemaining(
          flat -> {
            if (flat.getCreator().equals(creator)) iterator.remove();
          });
      writeLock.unlock();
    } catch (LogException e) {
      Printer.printError(e);
    } catch (RemoveFailedException e) {
      Printer.printError(e);
    }
  }

  public void sort() {
    collection.sort(Comparator.comparing(Flat::getName));
  }

  public void min_by_coordinates() {
    collection.sort(
        Comparator.comparing(
            a ->
                Float.parseFloat(a.getCoordinate().getX())
                    + Float.parseFloat(a.getCoordinate().getY())));
  }

  public void clear() {
    collection.clear();
  }

  public void add(Request request) {
    Flat flat = request.getFlat();
    try {
      flat.setId(collection.size() + 1);
      flat.setCreator(request.getAccount().getUser());
      databaseManager.insertFlat(flat);
      writeLock.lock();
      collection.add(flat);
      writeLock.unlock();
    } catch (LogException e) {
      Printer.printError(e);
    } catch (InsertFailedException e) {
      Printer.printError(e);
    }
  }

  public void remove_by_id(int id, int collectionID) {
    try {
      databaseManager.removeById(id);
      writeLock.lock();
      collection.remove(collectionID);
      writeLock.unlock();
    } catch (LogException e) {
      throw new RuntimeException(e);
    } catch (RemoveFailedException e) {
      throw new RuntimeException(e);
    }
  }

  public void remove_first(Request request) {
    try {
      int id = -1;
      String creator = request.getAccount().getUser();
      Iterator<Flat> iterator = collection.iterator();
      while (iterator.hasNext()) {
        Flat flat = iterator.next();
        if (flat.getCreator().equals(creator)) {
          id = flat.getId();
          break;
        }
      }
      if (id < 0) throw new RemoveFailedException("No flat found for " + creator);
      databaseManager.removeById(id);
      writeLock.lock();
      collection.removeFirst();
      writeLock.unlock();
    } catch (RemoveFailedException e) {
      throw new RuntimeException(e);
    } catch (LogException e) {
      throw new RuntimeException(e);
    }
  }

  public void remove_lower(Request request) {
    String creator = request.getAccount().getUser();
    Flat compareFlat = request.getFlat();
    LinkedList<Flat> tempList = new LinkedList<>(collection);
    tempList.forEach(
        flat -> {
          if (flat.toString().compareTo(compareFlat.toString()) < 0
              && flat.getCreator().equals(creator)) {
            int id = flat.getId();
            try {
              databaseManager.removeById(id);
              writeLock.lock();
              collection.remove(flat);
              writeLock.unlock();
            } catch (LogException e) {
              throw new RuntimeException(e);
            } catch (RemoveFailedException e) {
              throw new RuntimeException(e);
            }
          }
          ;
        });
  }

  public void update(Flat flat, int collectionID) {
    try {
      databaseManager.update(flat);
      writeLock.lock();
      collection.remove(collectionID);
      collection.add(collectionID, flat);
      writeLock.unlock();
    } catch (InsertFailedException e) {
      throw new RuntimeException(e);
    } catch (LogException e) {
      throw new RuntimeException(e);
    }
  }

  public void loadData() throws LogException {
    LinkedList<Flat> result = databaseManager.loadFlats();
    collection.addAll(result);
    Printer.printResult("Loaded " + collection.size() + " flat(s) from file");
  }
}
