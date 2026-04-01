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

  public CollectionManager(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public LinkedList<Flat> getCollection() {
    return collection;
  }

  public void clearByCreator(Request request) {
    try {
      String creator = request.getAccount().getUser();
      readWriteLock.writeLock().lock();
      databaseManager.clearByCreator(creator);
      Iterator<Flat> iterator = collection.iterator();
      iterator.forEachRemaining(
          flat -> {
            if (flat.getCreator().equals(creator)) iterator.remove();
          });
    } catch (LogException | RemoveFailedException e) {
      Printer.printError(e);
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  public void sort() {
    readWriteLock.writeLock().lock();
    collection.sort(Comparator.comparing(Flat::getName));
    readWriteLock.writeLock().unlock();
  }

  public void min_by_coordinates() {
    readWriteLock.writeLock().lock();
    collection.sort(
        Comparator.comparing(
            a ->
                Float.parseFloat(a.getCoordinate().getX())
                    + Float.parseFloat(a.getCoordinate().getY())));
    readWriteLock.writeLock().unlock();
  }

  public void clear() {
    try {
      readWriteLock.writeLock().lock();
      databaseManager.clear();
      collection.clear();
    } catch (LogException | RemoveFailedException e) {
      Printer.printError(e);
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  public void add(Request request) {
    Flat flat = request.getFlat();
    try {
      flat.setId(collection.size() + 1);
      flat.setCreator(request.getAccount().getUser());
      readWriteLock.writeLock().lock();
      databaseManager.insertFlat(flat);
      collection.add(flat);

    } catch (LogException | InsertFailedException e) {
      Printer.printError(e);
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  public void remove_by_id(int id, int collectionID) {
    try {
      readWriteLock.writeLock().lock();
      databaseManager.removeById(id);
      collection.remove(collectionID);

    } catch (LogException | RemoveFailedException e) {
      Printer.printError(e);
    } finally {
      readWriteLock.writeLock().unlock();
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
      readWriteLock.writeLock().lock();
      databaseManager.removeById(id);
      collection.removeFirst();

    } catch (RemoveFailedException | LogException e) {
      Printer.printError(e);
    } finally {
      readWriteLock.writeLock().unlock();
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
              readWriteLock.writeLock().lock();
              databaseManager.removeById(id);
              collection.remove(flat);

            } catch (LogException | RemoveFailedException e) {
              Printer.printError(e);
            } finally {
              readWriteLock.writeLock().unlock();
            }
          }
          ;
        });
  }

  public void update(Flat flat, int collectionID) {
    try {
      readWriteLock.writeLock().lock();
      databaseManager.update(flat);
      collection.remove(collectionID);
      collection.add(collectionID, flat);

    } catch (InsertFailedException | LogException e) {
      Printer.printError(e);
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  public void loadData() throws LogException {
    readWriteLock.writeLock().lock();
    LinkedList<Flat> result = databaseManager.loadFlats();
    collection.addAll(result);
    readWriteLock.writeLock().unlock();
    Printer.printResult("Loaded " + collection.size() + " flat(s) from file");
  }
}
