package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab04MassData;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.GameAnswer;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.Question;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class Lab04MassDataImpl extends Lab04MassData {
  private EntityManagerFactory emf;
  private EntityManager em;
  private Random random;
  private List<Category> categoriesBase;
  private List<Question> allQuestions;
  private static final int THREADS = 50; // Number of threads to use

  @Override
  public void init() {
    emf = Persistence.createEntityManagerFactory("fbi-postgresPU");
    em = emf.createEntityManager();
    allQuestions = new ArrayList<>();
    random = new Random();
  }


  @Override
  public void createMassData() {
    final long startTime = System.currentTimeMillis();
    categoriesBase = em.createNamedQuery("Category.findAll", Category.class).getResultList();

    // Fetch all questions
    for (Category category : categoriesBase) {
      allQuestions.addAll(category.getQuestionList());
    }
    int totalPlayers = 10000;
    ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);

    for (int i = 1; i <= totalPlayers; i++) {
      final int playerId = i;
      threadPool.execute(() -> {
        EntityManager threadEm = emf.createEntityManager();

        try {
          List<Category> categories = categoriesBase;
          String playerName = "Player" + playerId;
          Player player = new Player(playerName);
          List<Game> games = new ArrayList<>();

          for (int j = 1; j <= 100; j++) {
            Game game = new Game();
            game.setPlayer(player);

            Category randomCategory = categories.get(random.nextInt(categories.size()));
            List<Question> questions = getRandomQuestions(randomCategory);

            for (Question question : questions) {
              int answerIndex = random.nextInt(question.getAnswerList().size());
              game.addAnswer(question, answerIndex);
            }
            games.add(game);
          }
          threadEm.getTransaction().begin();
          threadEm.persist(player);
          for (Game game : games) {
            threadEm.persist(game);
          }

          threadEm.getTransaction().commit();

          double progress = (double) playerId / totalPlayers * 100;
          System.out.printf("Progress: %.2f%% (%d/%d players processed)"
              + "%n", progress, playerId, totalPlayers);

        } catch (Exception e) {
          threadEm.getTransaction().rollback();
          e.printStackTrace();
        } finally {
          threadEm.close();
        }
      });
    }

    threadPool.shutdown();
    while (true) {
      if (threadPool.isTerminated()) {
        break;
      }
      // wait for all threads to finish
    }
    Query query = em.createQuery("SELECT count(p) FROM Player p");
    Long playerCount = (Long) query.getSingleResult();
    System.out.printf("Overall Players: %d\n", playerCount);

    // Stop the timer and calculate the duration
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    System.out.printf("Execution time to create mass data: %d milliseconds%n", duration);
  }

  private List<Question> getRandomQuestions(Category category) {
    List<Question> questions = new ArrayList<>();
    int numberOfQuestions = random.nextInt(11) + 10;

    List<Question> categoryQuestions = category.getQuestionList();

    for (int i = 0; i < numberOfQuestions; i++) {
      Question randomQuestion = categoryQuestions.get(random.nextInt(categoryQuestions.size()));
      questions.add(randomQuestion);
    }
    return questions;
  }

  /*
  * Praktikum 5.
  *  */
  @Override
  public void analyzeData() {
    do {
      System.out.println("Analyze the game");
      System.out.println("--------------------------------------");
      System.out.println("1: print detail for player");
      System.out.println("2: print player and game count");
      System.out.println("3: print popular category");
      System.out.println("0: Quit Game Analyze");
    } while (readInput());
  }


  /**
   * this function use to read the input of user.
   * @return true
   */
  public boolean readInput() {
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
          System.out.println("Enter Player Name:");
          String playerNameInput = reader.readLine();
          try {
            printGameDetailsForPlayer(playerNameInput);
          } catch (NumberFormatException e) {
            System.out.println("Invalid Player ID. Please enter a numeric value.");
          }
          break;
        case "2":
          printPlayersAndGameCounts();
          break;
        case "3":
          printCategoryPopularity();
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

  /**
   * Print detail, game count of a player using its name.
   */
  public void printGameDetailsForPlayer(String playerName) {
    EntityManager em = lab02EntityManager.getEntityManager();

    try {
      // JPQL query string
      String query = "SELECT g.gameId, g.startTime, a "
          + "FROM Game g JOIN g.answerList a "
          + "WHERE g.player.name = :playerName "
          + "ORDER BY g.gameId";

      // creating the JPQL query
      TypedQuery<Object[]> typedQuery = em.createQuery(query, Object[].class);

      // setting parameter
      typedQuery.setParameter("playerName", playerName);

      // executing the query
      List<Object[]> results = typedQuery.getResultList();

      // checking and printing the result
      if (results.isEmpty()) {
        System.out.println("No game data found for player with name: " + playerName);
      } else {
        System.out.println("Game details for player " + playerName + ":");
        int countIsCorrect = 0;
        for (Object[] result : results) {
          int gameId = (int) result[0];
          Date startTime = (Date) result[1];
          GameAnswer answer = (GameAnswer) result[2]; // Assuming GameAnswer is a defined class
          System.out.println("Game ID: " + gameId
              + ", Start Time: " + startTime
              + ", Question: " + answer.getQuestionId()
              + ", Answer : " + answer.getAnswerId());
          if (answer.isCorrect()) {
            countIsCorrect++;
          }
        }

        System.out.println("Total Correct Answer: " + countIsCorrect);

      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /**
   * Print all players and number of played games.
   */
  public void printPlayersAndGameCounts() {
    EntityManager em = lab02EntityManager.getEntityManager();

    try {
      String jpql = "SELECT p.name, COUNT(g) as amount "
          + "FROM Player p JOIN Game g "
          + "WHERE p.playerId = g.player.playerId "
          + "GROUP BY p.name "
          + "ORDER BY amount DESC";

      TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);

      List<Object[]> results = query.getResultList();

      if (results.isEmpty()) {
        System.out.println("No player data found.");
      } else {
        System.out.println("Player names and their game counts:");
        for (Object[] result : results) {
          String playerName = (String) result[0];
          long gameCount = (Long) result[1];

          System.out.println("Player: " + playerName + ", Game Count: " + gameCount);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /**
   * Print statistic of categories which played.
   */
  public void printCategoryPopularity() {
    EntityManager em = lab02EntityManager.getEntityManager();

    try {
      String jpql = "SELECT c.name, COUNT(q) as amount "
          + "FROM Question q JOIN q.category c, Game g JOIN g.answerList ga "
          + "WHERE q.questionId = ga.questionId "
          + "GROUP BY c.name "
          + "ORDER BY amount DESC";

      TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
      List<Object[]> results = query.getResultList();

      if (results.isEmpty()) {
        System.out.println("No data found.");
      } else {
        System.out.println("Category Popularity:");
        for (Object[] result : results) {
          String categoryName = (String) result[0];
          Long count = (Long) result[1];
          System.out.println("Category: " + categoryName + ", Count: " + count);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }
}


