package de.hda.fbi.db2.controller;

import de.hda.fbi.db2.api.Lab03Game;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * MenuController Created by l.koehler on 05.08.2019.
 */
public class MenuController {

  private final Controller controller;

  public MenuController(Controller controller) {
    this.controller = controller;
  }

  /**
   * shows the menu.
   */
  public void showMenu() {
    do {
      System.out.println("Choose your Destiny?");
      System.out.println("--------------------------------------");
      System.out.println("1: Re-read csv");
      System.out.println("2: Play test");
      System.out.println("3: Create mass data");
      System.out.println("4: Analyze data");
      System.out.println("0: Quit");
    } while (readInput());
  }

  private boolean readInput() {
    try {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
      String input = reader.readLine();
      if (input == null) {
        return true;
      }
      switch (input) {
        case "0":
          return false;
        case "1":
          readCsv();
          break;
        case "2":
          playTest();
          break;
        case "3":
          createMassData();
          break;
        case "4":
          analyzeData();
          break;
        default:
          System.out.println("Input Error");
          break;
      }

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private void analyzeData() {
    controller.analyzeData();
  }

  private void createMassData() {
    controller.createMassData();
  }

  private void playTest() {
    Lab03Game gameController = controller.getLab03Game();
    Object player = gameController.interactiveGetOrCreatePlayer();
    List<?> questions = gameController.interactiveGetQuestions();
    Object game = gameController.createGame(player, questions);
    gameController.interactivePlayGame(game);
    gameController.persistGame(game);
  }

  private void readCsv() {
    controller.readCsv();
  }
}
