package server.database;

import common.exceptions.LogException;
import common.exceptions.database.InsertFailedException;
import common.exceptions.database.RemoveFailedException;
import common.io.LogUtil;
import common.objects.Flat;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class DatabaseManager {
  private final DatabaseConnectionManager connectionManager;
  private final Inserter inserter = new Inserter();
  private final Loader loader = new Loader();
  private final Remover remover = new Remover();
  private final Updater updater = new Updater();

  public DatabaseManager(DatabaseConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  public void insertFlat(Flat flat) throws LogException, InsertFailedException {
    Connection connection = null;
    try {
      connection = connectionManager.getConnection();
      connection.setAutoCommit(false);
      inserter.insertFlat(flat, connection);
      connection.commit();
    } catch (SQLException e) {
      connectionManager.rollback(connection);
      LogUtil.logServerError("Error at DatabaseManager.insertFlat", e);
      throw new LogException("Flat insert failed");
    } catch (InsertFailedException e) {
      connectionManager.rollback(connection);
      throw e;
    } finally {
      if (connection != null) connectionManager.autoCommit(connection);
      connectionManager.closeConnection(connection);
    }
  }

  public LinkedList<Flat> loadFlats() throws LogException {
    try (Connection connection = connectionManager.getConnection()) {
      return loader.loadFlats(connection);
    } catch (SQLException e) {
      LogUtil.logServerError("Flats load failed", e);
      throw new LogException("Flats load failed");
    }
  }

  public void clearByCreator(String creator) throws LogException, RemoveFailedException {
    Connection connection = null;
    try {
      connection = connectionManager.getConnection();
      connection.setAutoCommit(false);
      remover.clearByCreator(creator, connection);
      connection.commit();
    } catch (SQLException e) {
      connectionManager.rollback(connection);
      LogUtil.logServerError("Error at DatabaseManager.clearByCreator", e);
      throw new LogException("Collection clear failed");
    } catch (RemoveFailedException e) {
      connectionManager.rollback(connection);
      throw e;
    } finally {
      if (connection != null) connectionManager.autoCommit(connection);
      connectionManager.closeConnection(connection);
    }
  }

  public void removeById(int id) throws LogException, RemoveFailedException {
    Connection connection = null;
    try {
      connection = connectionManager.getConnection();
      connection.setAutoCommit(false);
      remover.removeById(id, connection);
      connection.commit();
    } catch (RemoveFailedException e) {
      connectionManager.rollback(connection);
      throw e;
    } catch (SQLException e) {
      connectionManager.rollback(connection);
      throw new LogException("Remove by ID failed");
    } finally {
      if (connection != null) connectionManager.autoCommit(connection);
      connectionManager.closeConnection(connection);
    }
  }

  public void update(Flat flat) throws InsertFailedException, LogException {
    Connection connection = null;
    try {
      connection = connectionManager.getConnection();
      connection.setAutoCommit(false);
      updater.updateFlat(flat, connection);
      connection.commit();
    } catch (InsertFailedException e) {
      connectionManager.rollback(connection);
      throw e;
    } catch (SQLException e) {
      connectionManager.rollback(connection);
      throw new LogException("Update flat failed");
    } finally {
      if (connection != null) connectionManager.autoCommit(connection);
      connectionManager.closeConnection(connection);
    }
  }
}
