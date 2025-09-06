package server.database;

import common.enums.Transport;
import common.exceptions.LogException;
import common.io.LogUtil;
import common.objects.Coordinate;
import common.objects.Flat;
import common.objects.House;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Loader {
  private final String loadFlats =
      "SELECT flats.*, houses.*, coordinates.* FROM (flats LEFT JOIN houses on flats.id = houses.flat_id) LEFT JOIN coordinates on flats.id = coordinates.flat_id ORDER BY id";
  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock readLock = readWriteLock.readLock();

  public Loader() {}

  public LinkedList<Flat> loadFlats(Connection connection) throws LogException {
    readLock.lock();
    LinkedList<Flat> collection = new LinkedList<>();
    try (PreparedStatement preparedStatement = connection.prepareStatement(loadFlats)) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          Flat flat = new Flat();
          flat.setId(resultSet.getInt("id"));
          flat.setName(resultSet.getString("name"));
          flat.setCreationDate(resultSet.getDate("date").toLocalDate());
          flat.setArea(resultSet.getLong("area"));
          flat.setNumberOfRooms(resultSet.getInt("rooms"));
          flat.setLivingSpace(resultSet.getLong("space"));
          flat.setCentralHeating(resultSet.getBoolean("heating"));
          flat.setTransport(Transport.valueOf(resultSet.getString("transport")));
          flat.setCreator(resultSet.getString("creator"));

          House house = new House();
          Integer houseYear = resultSet.getInt("year");
          Long houseLifts = resultSet.getLong("lifts");
          if (houseLifts == 0 && houseYear == 0) {
            house = null;
          } else {
            String houseName = resultSet.getString("house_name");
            house.setName(houseName);
            house.setYear(houseYear);
            house.setNumberOfLifts(houseLifts);
          }
          flat.setHouse(house);

          float coordinateX = resultSet.getFloat("coordinate_x");
          float coordinateY = resultSet.getFloat("coordinate_y");
          Coordinate coordinate = new Coordinate(coordinateX, coordinateY);
          flat.setCoordinate(coordinate);

          collection.add(flat);
        }
      }
    } catch (SQLException e) {
      LogUtil.logServerError(e);
      throw new LogException("Unable to load flats");
    }
    readLock.unlock();
    return collection;
  }
}
