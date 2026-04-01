package server.database;

import common.exceptions.LogException;
import common.exceptions.database.InsertFailedException;
import common.io.LogUtil;
import common.objects.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountWorker {
  private final DatabaseConnectionManager connectionManager;
  private final String checkAccount = "SELECT * FROM accounts WHERE username = ? AND password = ?";
  private final String checkUser = "SELECT username FROM accounts WHERE username = ?";
  private final String insertAccount = "INSERT INTO accounts (username, password) VALUES (?, ?)";

  public AccountWorker(DatabaseConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  public boolean isAccount(Account account) throws LogException {
    String user = account.getUser();
    String password = account.getPassword();
    Connection connection;
    try {
      connection = connectionManager.getConnection();
    } catch (SQLException e) {
      LogUtil.logServerError(e);
      throw new LogException("Connection failed at isAccount");
    }
    try (PreparedStatement preparedStatement = connection.prepareStatement(checkAccount); ) {
      preparedStatement.setString(1, user);
      preparedStatement.setString(2, password);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      LogUtil.logServerError(e);
      throw new LogException("checkAccount failed");
    } finally {
      connectionManager.closeConnection(connection);
    }
  }

  public boolean isUser(Account account) {
    String user = account.getUser();
    Connection connection;
    try {
      connection = connectionManager.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    try (PreparedStatement preparedStatement = connection.prepareStatement(checkUser)) {
      preparedStatement.setString(1, user);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      connectionManager.closeConnection(connection);
    }
  }

  public boolean insertAccount(Account account) throws LogException, InsertFailedException {
    String user = account.getUser();
    String password = account.getPassword();
    Connection connection;
    if (!isUser(account)) {
      try {
        connection = connectionManager.getConnection();
      } catch (SQLException e) {
        LogUtil.logServerError(e);
        throw new LogException("Connection failed at insertAccount");
      }
      int rowsAffected;
      try (PreparedStatement preparedStatement = connection.prepareStatement(insertAccount)) {
        connection.setAutoCommit(false);
        preparedStatement.setString(1, user);
        preparedStatement.setString(2, password);
        rowsAffected = preparedStatement.executeUpdate();
        connection.commit();
        return rowsAffected > 0;
      } catch (SQLException e) {
        connectionManager.rollback(connection);
        LogUtil.logServerError(e);
        throw new LogException("Insert account failed");
      } finally {
        if (connection != null) connectionManager.autoCommit(connection);
        connectionManager.closeConnection(connection);
      }
    } else throw new InsertFailedException("User already existed");
  }
}
