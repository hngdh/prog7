package server.database;

import common.exceptions.database.InsertFailedException;
import common.exceptions.database.RemoveFailedException;
import common.objects.Coordinate;
import common.objects.Flat;
import common.objects.House;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Updater {
  private final String updateFlat =
      "UPDATE flats SET name = ?, date = ?, area = ?, rooms = ?, space = ?, heating = ?, transport = CAST(? AS transport) WHERE id = ?";
  private final String updateHouse =
      "UPDATE houses SET name = ?, year = ?, lifts = ? WHERE flat_id = ?";
  private final String updateCoordinate =
      "UPDATE coordinates SET coordinate_x = ?, coordinate_y = ? WHERE  flat_id = ?";
  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock writeLock = readWriteLock.writeLock();
  private final Remover remover = new Remover();
  private final Inserter inserter = new Inserter();

  public Updater() {}

  public void updateFlat(Flat flat, Connection connection)
      throws SQLException, InsertFailedException {
    writeLock.lock();
    try (PreparedStatement preparedStatement = connection.prepareStatement(updateFlat)) {
      preparedStatement.setString(1, flat.getName());
      preparedStatement.setDate(2, Date.valueOf(flat.getCreationDate()));
      preparedStatement.setLong(3, flat.getArea());
      preparedStatement.setInt(4, flat.getNumberOfRooms());
      preparedStatement.setLong(5, flat.getLivingSpace());
      preparedStatement.setBoolean(6, flat.isCentralHeating());
      preparedStatement.setString(7, flat.getTransport().toString());
      preparedStatement.setInt(8, flat.getId());
      if (preparedStatement.executeUpdate() < 0) throw new InsertFailedException("flat");
      updateHouse(flat, connection);
      updateCoordinate(flat, connection);
    }
    writeLock.unlock();
  }

  private void updateHouse(Flat flat, Connection connection)
      throws SQLException, InsertFailedException {
    House house = flat.getHouse();
    if (house == null) {
      try {
        remover.removeHouse(flat.getId(), connection);
      } catch (RemoveFailedException e) {
        throw new InsertFailedException("house: null");
      }
      return;
    }

    try (PreparedStatement preparedStatement = connection.prepareStatement(updateHouse)) {
      preparedStatement.setString(1, house.getName());
      preparedStatement.setString(2, house.getYear());
      preparedStatement.setString(3, house.getNumberOfLifts());
      preparedStatement.setInt(4, flat.getId());
      if (preparedStatement.executeUpdate() < 0) throw new InsertFailedException("house");
    }
  }

  private void updateCoordinate(Flat flat, Connection connection)
      throws SQLException, InsertFailedException {
    Coordinate coordinate = flat.getCoordinate();
    try (PreparedStatement preparedStatement = connection.prepareStatement(updateCoordinate)) {
      preparedStatement.setString(1, coordinate.getX());
      preparedStatement.setString(2, coordinate.getY());
      preparedStatement.setInt(3, flat.getId());
      if (preparedStatement.executeUpdate() < 0) throw new InsertFailedException("coordinate");
    }
  }
}
