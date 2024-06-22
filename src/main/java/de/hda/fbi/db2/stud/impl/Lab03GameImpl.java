package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab03Game;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.Question;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Lab03GameImpl extends Lab03Game {
  Player player;

  private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
  private final Random random = new Random();

  static EntityManagerFactory emf = Persistence.createEntityManagerFactory("fbi-postgresPU");
  private static final EntityManager em =  emf.createEntityManager();
  /**
   * Creates a new Player or retrieves it from the database.
   * <p>
   * This function shall try to retrieve the player with the given name playerName from the
   * database. If no such player exists, it shall be created as a Java Object. It is not necessary
   * to persist the Player yet.
   * </p>
   *
   * <p>This function is primarily used for testing. There exists a version with user interaction
   * which shall be used from the menu.
   * </p>
   *
   * @param playerName The name for the new Player.
   * @return Player object which was created or retrieved.
   * @see Lab03Game#interactiveGetOrCreatePlayer()
   */
  @Override
  public Object getOrCreatePlayer(String playerName) {
    List<Player> playerList = new ArrayList<>();
    try {
      playerList = em.createQuery("SELECT p FROM Player p WHERE p.name = :name", Player.class)
          .setParameter("name", playerName)
          .getResultList();
    } catch (Exception e) {
      System.err.println("No players yet");
    }
    if (playerList.isEmpty()) {
      System.out.println("Player doesn't exist");
      player = new Player(playerName);
      return player;
    } else {
      System.out.println("Player existed: " + playerList.get(0).getName());
      player = playerList.get(0);
      return player;
    }
  }

  /**
   * Creates a new Player or retrieves it from the database (interactive version).
   *
   * <p>
   * This function shall ask the user for a player name, and then shall try to retrieve such a
   * player from the database. If no such player exists, it shall be created as a Java Object. It is
   * not necessary to persist the player yet.
   * </p>
   *
   * <p>This function is primarily used for user interaction. There exists a version used for
   * testing, {@link #getOrCreatePlayer(String)}.</p>
   *
   * @return Player object which was created or retrieved.
   * @see Lab03Game#getOrCreatePlayer(String)
   */
  @Override
  public Object interactiveGetOrCreatePlayer() {
    System.out.print("Enter player name: ");
    String playerName = scanner.nextLine();
    return getOrCreatePlayer(playerName);
  }

  /**
   * This function shall generate a random list of questions which are from the given categories.
   *
   * <p>Per category there shall be a certain amount of questions chosen. If a category hosts less
   * questions than that amount, then all of the questions shall be chosen. Questions shall be
   * randomly selected.
   * </p>
   *
   * <p>There is also an interactive version of this function which asks the user for categories
   * instead of randomly selecting them.</p>
   *
   * @param categories                   A list of categories to select questions from
   * @param amountOfQuestionsForCategory The amount of questions per category. If a category has
   *                                     less than this amount, then all questions of that category
   *                                     shall be selected.
   * @return A List of randomly chosen Questions from the given Categories.
   * @see Lab03Game#interactiveGetQuestions()
   */
  @Override
  public List<?> getQuestions(List<?> categories, int amountOfQuestionsForCategory) {
    if (em == null) {
      throw new IllegalStateException("EntityManager is not initialized.");
    }
    List<Question> questions = new ArrayList<>();
    List<Category> categoryList = new ArrayList<>();

    for (Object obj : categories) {
      if (obj instanceof Category) {
        categoryList.add((Category) obj);
      } else {
        throw new ClassCastException("The list contains an element that is not of type Category");
      }
    }

    for (Category category : categoryList) {
      List<Question> questionList = em.createQuery(
          "SELECT q FROM Question q WHERE"
              + " q.category.categoryId = :categoryId", Question.class)
          .setParameter("categoryId", category.getCategoryId())
          .setMaxResults(amountOfQuestionsForCategory)
          .getResultList();
      questions.addAll(questionList);
    }

    return questions;
  }

  /**
   * This function shall generate a random list of questions after asking the user for categories.
   *
   * <p>In this function, ask the user for categories and the number of questions per category.
   * Then, randomly select questions from those categories. Choose as many questions per category as
   * were entered, as long as the category has that many questions. If there are fewer questions,
   * then select all of them.</p>
   *
   * @return A List of randomly chosen Questions from categories which the user entered.
   * @see Lab03Game#getQuestions(List, int)
   */
  @Override
  public List<Question> interactiveGetQuestions() {
    List<Question> questions = new ArrayList<>();

    // Retrieve Categories
    List<Object[]> categories =
        em.createQuery("SELECT c.categoryId, c.name FROM Category c", Object[].class)
            .getResultList();

    // Print Categories
    System.out.println("Available categories: ");
    for (Object[] category : categories) {
      System.out.println("ID: " + category[0] + ", Name: " + category[1]);
    }

    List<Integer> categoryIds = new ArrayList<>();
    List<Integer> validCategoryIds =
        em.createQuery("SELECT c.categoryId FROM Category c", Integer.class).getResultList();

    while (categoryIds.size() < 2) {

      // Enter Categories
      System.out.print("Enter category IDs (separated by comma): ");
      String input = scanner.nextLine();
      String[] ids = input.split(",");

      for (String id : ids) {
        try {
          int categoryId = Integer.parseInt(id.trim());

          // is valid and not already chosen
          if ((validCategoryIds.contains(categoryId)) && (!categoryIds.contains(categoryId))) {
            categoryIds.add(categoryId);
          } else {
            System.out.println("Category ID " + categoryId + " isn't correct. Please try again!");
          }
        } catch (NumberFormatException e) {
          System.out.println("Invalid input! Please enter a number.");
        }
      }
      if (categoryIds.size() < 2) {
        System.out.println("You need to select at least two categories. Please try again.");
      }
    }

    // Enter how many questions from each category
    int amount = -1;
    while (amount < 1) {
      System.out.print("Enter amount of questions for each category: ");
      try {
        amount = Integer.parseInt(scanner.nextLine());
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number.");
      }
    }


    for (int categoryId : categoryIds) {
      // Retrieve questions
      List<Question> questionList =
          em.createQuery("SELECT q FROM Question q WHERE q.category.categoryId = :id",
                  Question.class)
              .setParameter("id", categoryId)
              .getResultList();

      Collections.shuffle(questionList);

      // If there are fewer questions select all
      int endIndex = Math.min(amount, questionList.size());
      List<Question> catQuestions = questionList.subList(0, endIndex);
      questions.addAll(catQuestions);
    }
    return questions;
  }

  /**
   * This function creates a Game Object with the given player and questions, but without playing
   * the game yet.
   *
   * <p>It is important that you neither play the game yet nor persist the game! This is just meant
   * to create the game object.</p>
   *
   * @param player    The Player which shall play the game.
   * @param questions The Questions which shall be asked during the game.
   * @return A Game object which contains an unplayed game for the given player with the given
   *         questions.
   */
  @Override
  public Object createGame(Object player, List<?> questions) {
    List<Question> questionList = new ArrayList<>();
    for (Object obj : questions) {
      if (obj instanceof Question) {
        questionList.add((Question) obj);
      } else {
        throw new ClassCastException("The list contains an element that is not of type Question");
      }
    }
    return new Game((Player) player, questionList);
  }

  /**
   * This function simulates a game play without user interaction by randomly choosing answers.
   *
   * <p>There is also an interactive version of this function which shall be called from the menu.
   * </p>
   *
   * @param game The Game object which shall be played.
   * @see Lab03Game#interactivePlayGame(Object)
   */
  @Override
  public void playGame(Object game) {
    Game g = (Game) game;
    g.setStartTime(new Date());
    List<Question> questions = g.getQuestions();

    // answering questions
    for (Question question : questions) {
      int answerIndex = random.nextInt(question.getAnswerList().size());
      g.addAnswer(question, answerIndex);
    }
    g.setEndTime(new Date());
  }

  /**
   * This function plays the given game with the user, that is by using user interaction.
   *
   * <p>This is the function that should be called from the menu. Here you can implement the
   * necessary user interaction for the playing of the game.</p>
   *
   * @param game The Game object which shall be played.
   * @see Lab03Game#playGame(Object)
   */
  @Override
  public void interactivePlayGame(Object game) {
    Game g = (Game) game;
    g.setStartTime(new Date());
    List<Question> questions = g.getQuestions();

    int correctAnswers = 0;
    int wrongAnswers = 0;

    for (Question question : questions) {
      System.out.println("Question: " + question.getQuestionText());
      for (int i = 0; i < question.getAnswerList().size(); i++) {
        System.out.println(i + ": " + question.getAnswerList().get(i).getAnswerText());
      }

      int answerIndex = -1;
      while (answerIndex < 0 || answerIndex >= question.getAnswerList().size()) {
        System.out.print("Enter your answer index: ");
        try {
          answerIndex = Integer.parseInt(scanner.nextLine());
          if (answerIndex < 0 || answerIndex >= question.getAnswerList().size()) {
            System.out.println("Invalid input! Try again.. Please enter a number between 0 and "
                + (question.getAnswerList().size() - 1) + ".");
          }
        } catch (NumberFormatException e) {
          System.out.println("Invalid input! Try again.. Please enter a number.");
        }
      }
      g.addAnswer(question, answerIndex);

      // checks if correct
      if (question.getAnswerList().get(answerIndex).isCorrect()) {
        System.out.println("Correct Answer!");
        correctAnswers++;
      } else {
        System.out.println("Wrong Answer!");
        wrongAnswers++;
      }
    }

    g.setEndTime(new Date());

    // print result
    System.out.println("Game Finished!");
    System.out.println("Correct Answers: " + correctAnswers);
    System.out.println("Wrong Answers: " + wrongAnswers);
    System.out.println("Start Time: " + g.getStartTime());
    System.out.println("End Time: " + g.getEndTime());
  }

  /**
   * Persists a played game, including the player who played it.
   *
   * @param game The Game object to be persisted.
   */
  @Override
  public void persistGame(Object game) {
    try {
      em.getTransaction().begin();
      em.persist(player);
      em.persist(game);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      System.err.println("Error while trying to persist questions.");
    }
  }
}