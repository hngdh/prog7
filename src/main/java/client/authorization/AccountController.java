package client.authorization;

import client.ioStream.Renderer;
import client.network.ClientNetwork;
import common.dataProcessors.AccountInputChecker;
import common.exceptions.AccountInputException;
import common.exceptions.LogException;
import common.io.LogUtil;
import common.io.Printer;
import common.objects.Account;
import common.packets.Request;
import common.packets.Response;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

public class AccountController {
  private Account account = null;

  public AccountController() {}

  public boolean isSignIn() {
    return account != null;
  }

  public void signOut() {
    account = null;
  }

  public Account getAccount() {
    return account;
  }

  public void signIn(ClientNetwork clientNetwork, Renderer renderer) throws LogException {
    if (!isSignIn()) {
      String user = getUser("Please enter your username: ");
      String password = getPasswordSignIn();
      //            String user = "hos";
      //            String password = "h@S314";
      password = MD2Hashing.MD2(password);
      Request request = new Request("sign_in", null, new Account(user, password));
      Response response = clientNetwork.respond(request);
      renderer.printResponse(response);
      if (response.getResult() != null && !response.getResult().isEmpty()) {
        if (response.getResult().get(0).equals("||signed_in//")) {
          account = new Account(user, password);
        }
      }
    } else Printer.printCondition("You are already signed in");
  }

  public void signUp(ClientNetwork clientNetwork, Renderer renderer) throws LogException {
    if (!isSignIn()) {
      String user = getUser("Please enter your account name (1-45 characters): ");
      String password = getPasswordSignUp();
      password = MD2Hashing.MD2(password);
      Request request = new Request("sign_up", null, new Account(user, password));
      Response response = clientNetwork.respond(request);
      renderer.printResponse(response);
      if (response.getResult() != null && !response.getResult().isEmpty()) {
        if (response.getResult().get(0).equals("||signed_up//")) {
          account = new Account(user, password);
        }
      }
    } else Printer.printCondition("You are already signed in");
  }

  private String getUser(String notice) throws LogException {
    String user = "";
    while (user.isEmpty()) {
      try {
        Printer.printCondition(notice);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        user = br.readLine();
        if (user.isEmpty() || !AccountInputChecker.checkUser(user)) {
          throw new AccountInputException();
        }
      } catch (IOException e) {
        LogUtil.logServerError(e);
        throw new LogException("Reading Username failed");
      } catch (AccountInputException e) {
        Printer.printError(e.toString());
      }
    }
    return user;
  }

  private String getPasswordSignIn() {
    String password = "";
    while (password.isEmpty()) {
      try {
        Printer.printCondition("Please enter your password: ");
        Console console = System.console();
        if (console != null) {
          char[] pwdArray = console.readPassword();
          password = new String(pwdArray);
        } else {
          BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
          password = reader.readLine();
        }

        if (password.isEmpty()) {
          throw new AccountInputException();
        }
      } catch (AccountInputException e) {
        Printer.printError(e.toString());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return password;
  }

  private String getPasswordSignUp() {
    String password = "";
    while (password.isEmpty()) {
      try {
        Printer.printCondition("Your password must have 5-45 characters and contains:");
        Printer.printInfo("* Special character: !@#$%^&*|?+=");
        Printer.printInfo("* Number");
        Printer.printInfo("* Uppercase and Lowercase letters");
        Console console = System.console();
        if (console != null) {
          char[] pwdArray = console.readPassword("Please enter your account password: ");
          password = new String(pwdArray);
        } else {
          BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
          password = reader.readLine();
        }
        if (!AccountInputChecker.checkPassword(password)) {
          password = "";
          throw new AccountInputException();
        }
      } catch (AccountInputException e) {
        Printer.printError(e.toString());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return password;
  }
}
