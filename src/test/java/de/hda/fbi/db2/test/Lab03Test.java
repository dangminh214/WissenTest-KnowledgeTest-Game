package de.hda.fbi.db2.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import de.hda.fbi.db2.api.Lab01Data;
import de.hda.fbi.db2.api.Lab03Game;
import de.hda.fbi.db2.controller.Controller;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Created by l.koehler on 05.08.2019.
 */
// Disable on CI server because it requires database access
@DisabledIfSystemProperty(
    named = "ci-server",
    matches = "true",
    disabledReason = "Running this test on the CI server is not supported"
)
@TestMethodOrder(MethodOrderer.MethodName.class)
class Lab03Test {

  private static EntityManager entityManager;

  private static Metamodel metaData;

  private static Controller controller;

  private static EntityType<?> gameEntity;

  /**
   * Lab03Test init.
   */
  @BeforeAll
  static void init() throws URISyntaxException, IOException {
    controller = Controller.getInstance();
    Lab03Game impl = controller.getLab03Game();
    // Skip test if students have not implemented class yet
    assumeTrue(impl != null, "Lab03Game implementation does not exist");

    String expectedPackage = "de.hda.fbi.db2.stud.impl";
    // Check startsWith to also allow subpackages
    if (!impl.getClass().getName().startsWith(expectedPackage + '.')) {
      fail("Implementation class should be in package " + expectedPackage);
    }

    if (!controller.isCsvRead()) {
      controller.readCsv();
    }

    if (!controller.isPersisted()) {
      controller.persistData();
    }

    try {
      entityManager = controller.getLab02EntityManager().getEntityManager();
      assertNotNull(entityManager, "Lab02EntityManager.getEntityManager() returns null");
      metaData = entityManager.getMetamodel();
    } catch (Exception e) {
      fail("Exception during entityManager creation", e);
    }
  }

  @AfterAll
  static void cleanUp() {
    if (entityManager != null) {
      entityManager.close();
    }
  }

  @Test
  void test1Functionality() throws URISyntaxException, IOException {
    if (metaData == null) {
      fail("No MetaModel");
    }

    Lab03Game gameController = controller.getLab03Game();
    Lab01Data lab01Data = controller.getLab01Data();
    List<Object> categories = new ArrayList<>();
    categories.add(lab01Data.getCategories().get(0));
    categories.add(lab01Data.getCategories().get(1));

    List<?> questions = gameController.getQuestions(categories, 2);
    assertNotNull(questions);
    assertFalse(questions.isEmpty(), "Questions for categories should not be empty");
    assertTrue(questions.size() <= 4, "Should at most return 4 questions, 2 per category");

    Object player = gameController.getOrCreatePlayer("PlayerName");
    assertNotNull(player);
    Object game = gameController.createGame(player, questions);
    assertNotNull(game);
    gameController.playGame(game);
    gameController.persistGame(game);
  }

  @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
  @Test
  void test2FindGameEntity() throws URISyntaxException, IOException {
    if (metaData == null) {
      fail("No MetaModel");
    }

    Lab03Game gameController = controller.getLab03Game();
    Lab01Data lab01Data = controller.getLab01Data();
    List<Object> categories = new ArrayList<>();
    categories.add(lab01Data.getCategories().get(0));
    categories.add(lab01Data.getCategories().get(1));

    List<?> questions = gameController.getQuestions(categories, 2);
    assertNotNull(questions);
    assertFalse(questions.isEmpty(), "Questions for categories should not be empty");
    assertTrue(questions.size() <= 4, "Should at most return 4 questions, 2 per category");

    Object player = gameController.getOrCreatePlayer("PlayerName");
    assertNotNull(player);
    Object game = gameController.createGame(player, questions);
    assertNotNull(game);

    boolean gameFound = false;
    for (EntityType<?> classes : metaData.getEntities()) {
      if (classes.getJavaType() == game.getClass()) {
        gameFound = true;
        gameEntity = classes;
      }
    }

    if (!gameFound) {
      fail("Could not find Game class as entity");
    }
  }

  @Test
  void test3FindPlayerEntity() {
    if (metaData == null) {
      fail("No MetaModel");
    }

    Lab03Game gameController = controller.getLab03Game();
    Object player = gameController.getOrCreatePlayer("PlayerName");
    assertNotNull(player);

    boolean playerFound = false;
    for (EntityType<?> classes : metaData.getEntities()) {
      if (classes.getJavaType() == player.getClass()) {
        playerFound = true;
      }
    }

    if (!playerFound) {
      fail("Could not find Player class as entity");
    }
  }
}