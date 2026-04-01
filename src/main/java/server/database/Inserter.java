package server.database;

import common.exceptions.database.InsertFailedException;
import common.objects.Coordinate;
import common.objects.Flat;
import common.objects.House;
import java.sql.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Inserter {
  private final String insertFlat =
      "INSERT INTO flats (name, date, area, rooms, space, heating, transport, creator) values (?, ?, ?, ?, ?, ?, CAST (? AS transport), ?)";
  private final String insertHouse =
      "INSERT INTO houses(flat_id, house_name, year, lifts) values (?, ?, ?, ?)";
  private final String insertCoordinate =
      "INSERT INTO coordinates(flat_id, coordinate_x, coordinate_y) values (?, ?, ?)";
  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  public Inserter() {}

  public void insertFlat(Flat flat, Connection connection)
      throws SQLException, InsertFailedException {
    try (PreparedStatement preparedStatement =
        connection.prepareStatement(insertFlat, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, flat.getName());
      preparedStatement.setDate(2, Date.valueOf(flat.getCreationDate()));
      preparedStatement.setLong(3, flat.getArea());
      preparedStatement.setInt(4, flat.getNumberOfRooms());
      preparedStatement.setLong(5, flat.getLivingSpace());
      preparedStatement.setBoolean(6, flat.isCentralHeating());
      preparedStatement.setString(7, flat.getTransport().toString());
      preparedStatement.setString(8, flat.getCreator());
      if (preparedStatement.executeUpdate() <= 0) throw new InsertFailedException("flat");
      try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
        if (resultSet.next()) {
          Integer id = resultSet.getInt(1);
          flat.setId(id);
        } else {
          throw new InsertFailedException("flat_id");
        }
      }
      insertHouse(flat, connection);
      insertCoordinate(flat, connection);
    }
  }

  private void insertHouse(Flat flat, Connection connection)
      throws SQLException, InsertFailedException {
    House house = flat.getHouse();
    if (house == null) return;
    try (PreparedStatement preparedStatement = connection.prepareStatement(insertHouse)) {
      preparedStatement.setInt(1, flat.getId());
      preparedStatement.setString(2, house.getName());
      preparedStatement.setString(3, house.getYear());
      preparedStatement.setString(4, house.getNumberOfLifts());
      if (preparedStatement.executeUpdate() <= 0) throw new InsertFailedException("house");
    }
  }

  private void insertCoordinate(Flat flat, Connection connection)
      throws SQLException, InsertFailedException {
    Coordinate coordinate = flat.getCoordinate();
    try (PreparedStatement preparedStatement = connection.prepareStatement(insertCoordinate)) {
      preparedStatement.setInt(1, flat.getId());
      preparedStatement.setString(2, coordinate.getX());
      preparedStatement.setString(3, coordinate.getY());
      if (preparedStatement.executeUpdate() <= 0) throw new InsertFailedException("coordinate");
    }
  }
}
