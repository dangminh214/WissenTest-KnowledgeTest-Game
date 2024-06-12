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
  private EntityManager em;
  private Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
  private Random random = new Random();

  @Override
  public void init() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("fbi-postgresPU");
    //EntityManagerFactory emf = Persistence.createEntityManagerFactory("docker-local-postgresPU");
    em = emf.createEntityManager();
  }

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

  @Override
  public Object interactiveGetOrCreatePlayer() {
    System.out.print("Enter player name: ");
    String playerName = scanner.nextLine();
    return (Object) getOrCreatePlayer(playerName);
  }

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


  @Override
  public List<Question> getQuestions(List<?> categories, int amountOfQuestionsForCategory) {
    List<Question> questions = new ArrayList<>();
    // Use proper generic type for categories list

    for (Object obj : categories) {
      if (obj instanceof Category) {
        Category category = (Category) obj;
        List<Question> questionList = em.createQuery(
            "SELECT q FROM Question q WHERE q.category.categoryId = :category", Question.class)
            .setParameter("category", category.getCategoryId())
            .setMaxResults(amountOfQuestionsForCategory)
            .getResultList();
        questions.addAll(questionList);
      } else {
        throw new IllegalArgumentException("Invalid type in categories list");
      }
    }

    return questions;
  }


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

  @Override
  public Game createGame(Object player, List<?> questions) {
    // Ensure player is of type Player
    Player castPlayer = (Player) player;

    // Assuming questions are actually a list of questions, cast safely
    List<Question> castQuestions = new ArrayList<>();
    for (Object obj : questions) {
      if (obj instanceof Question) {
        castQuestions.add((Question) obj);
      } else {
        // Handle the case where questions list contains unexpected types
        throw new IllegalArgumentException("Invalid type in questions list");
      }
    }

    // Now create the Game object using the properly typed variables
    return new Game(castPlayer, castQuestions);
  }


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
  }
}