package de.hda.fbi.db2.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import de.hda.fbi.db2.api.Lab01Data;
import de.hda.fbi.db2.controller.Controller;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Created by l.koehler on 05.08.2019.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class Lab01Test {

  private static Controller controller;

  /**
   * Lab01Test init.
   */
  @BeforeAll
  static void init() {
    controller = Controller.getInstance();
    Lab01Data impl = controller.getLab01Data();
    // Skip test if students have not implemented class yet
    assumeTrue(impl != null, "Lab01Data implementation does not exist");

    String expectedPackage = "de.hda.fbi.db2.stud.impl";
    // Check startsWith to also allow subpackages
    if (!impl.getClass().getName().startsWith(expectedPackage + '.')) {
      fail("Implementation class should be in package " + expectedPackage);
    }

    if (!controller.isCsvRead()) {
      controller.readCsv();
    }
  }

  @Test
  void test1CategorySize() throws URISyntaxException, IOException {
    List<?> categories = controller.getLab01Data().getCategories();
    assertNotNull(categories, "Categories must not be null");
    assertEquals(51, categories.size(), "There should be 51 categories");
  }

  @Test
  void test2QuestionSize() throws URISyntaxException, IOException {

    List<?> questions = controller.getLab01Data().getQuestions();
    assertNotNull(questions, "Questions must not be null");
    assertEquals(200, questions.size(), "There should be 200 questions");


  }

  @Test
  void test3CategoryObject() throws URISyntaxException, IOException {
    List<?> categories = controller.getLab01Data().getCategories();
    assertNotNull(categories, "Categories must not be null");
    assertFalse(categories.isEmpty(), "Categories must not be empty");

    Object testObject = categories.get(0);
    assertEquals("Category", testObject.getClass().getSimpleName(),
        "Category class should be named 'Category'");
    assertEquals("de.hda.fbi.db2.stud.entity", testObject.getClass().getPackageName(),
        "Category class should be in correct package");
  }

  @Test
  void test4QuestionObject() throws URISyntaxException, IOException {

    List<?> questions = controller.getLab01Data().getQuestions();
    assertNotNull(questions, "Questions must not be null");
    assertFalse(questions.isEmpty(), "Questions must not be empty");

    Object testObject = questions.get(0);
    assertEquals("Question", testObject.getClass().getSimpleName(),
        "Question class should be named 'Question'");
    assertEquals("de.hda.fbi.db2.stud.entity", testObject.getClass().getPackageName(),
        "Question class should be in correct package");

  }
}