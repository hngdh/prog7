package server.database;

import common.exceptions.database.RemoveFailedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Remover {
  private final String clearFlatsByCreator = "DELETE FROM flats WHERE creator = ?";
  private final String clear = "TRUNCATE TABLE flats CASCADE";
  private final String removeFlat = "DELETE FROM flats WHERE id = ?";
  private final String removeHouse = "DELETE FROM houses WHERE flat_id = ?";
  private final String removeCoordinate = "DELETE FROM coordinates WHERE flat_id = ?";

  public Remover() {}

  public void clearByCreator(String creator, Connection connection)
      throws SQLException, RemoveFailedException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(clearFlatsByCreator)) {
      int rowAffected;
      preparedStatement.setString(1, creator);
      rowAffected = preparedStatement.executeUpdate();
      if (rowAffected < 0) {
        throw new RemoveFailedException("");
      }
    }
  }

  public void clear(Connection connection) throws SQLException, RemoveFailedException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(clear)) {
      int rowAffected;
      rowAffected = preparedStatement.executeUpdate();
      if (rowAffected < 0) {
        throw new RemoveFailedException("");
      }
    }
  }

  public void removeById(int id, Connection connection) throws SQLException, RemoveFailedException {
    removeHouse(id, connection);
    removeCoordinate(id, connection);
    removeFlat(id, connection);
  }

  public void removeFlat(int id, Connection connection) throws SQLException, RemoveFailedException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(removeFlat)) {
      preparedStatement.setInt(1, id);
      if (preparedStatement.executeUpdate() < 0) throw new RemoveFailedException("");
    }
  }

  public void removeHouse(int id, Connection connection)
      throws SQLException, RemoveFailedException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(removeHouse)) {
      preparedStatement.setInt(1, id);
      if (preparedStatement.executeUpdate() < 0) throw new RemoveFailedException("");
    }
  }

  public void removeCoordinate(int id, Connection connection)
      throws SQLException, RemoveFailedException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(removeCoordinate)) {
      preparedStatement.setInt(1, id);
      if (preparedStatement.executeUpdate() < 0) throw new RemoveFailedException("");
    }
  }
}
