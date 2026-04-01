package server.database;

import common.exceptions.EnvNotFoundException;
import common.exceptions.LogException;
import common.io.LogUtil;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {

  private static Dotenv dotenv;
  private static String user;
  private static String password;
  private static String url;

  public DatabaseConnectionManager() {}

  public void loadEnv() throws EnvNotFoundException {
    dotenv = Dotenv.configure().filename("env.env").load();
    user = dotenv.get("USER");
    password = dotenv.get("PASSWORD");
    url = dotenv.get("URL");
    if (user == null || password == null || url == null) throw new EnvNotFoundException();
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  public void closeConnection(Connection connection) {
    try {
      if (connection != null) connection.close();
    } catch (SQLException e) {
      LogUtil.logServerError("Close connection failed", e);
    }
  }

  public void rollback(Connection connection) {
    if (connection != null) {
      try {
        connection.rollback();
      } catch (SQLException e) {
        LogUtil.logServerError("Rollback failed", e);
      }
    }
  }

  public void autoCommit(Connection connection) throws LogException {
    try {
      connection.setAutoCommit(true);
    } catch (SQLException e) {
      LogUtil.logServerError("Set auto commit failed", e);
      throw new LogException("Set auto commit failed");
    }
  }
}
