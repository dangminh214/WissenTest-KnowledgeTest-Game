package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab04MassData;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.GameAnswer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.*;


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
    //    emf = Persistence.createEntityManagerFactory("docker-local-postgresPU");
    em = emf.createEntityManager();
    allQuestions = new ArrayList<>();
    random = new Random();
  }


  @Override
  public void createMassData() {
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
            Date startDate = getRandomDate();
            Date endDate = new Date(startDate.getTime() + random.nextInt(1000000));

            Game game = new Game();
            game.setPlayer(player);
            game.setStartTime(startDate);
            game.setEndTime(endDate);

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
    while (!threadPool.isTerminated()) {
      // wait for all threads to finish
    }
    Query query = em.createQuery("SELECT count(p) FROM Player p");
    Long playerCount = (Long) query.getSingleResult();
    System.out.printf("Overall Players: %d\n", playerCount);
  }

  private Date getRandomDate() {
    long currentTime = System.currentTimeMillis();
    long twoWeeks = 14 * 24 * 60 * 60 * 1000;
    return new Date(currentTime - random.nextInt((int) twoWeeks));
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

  @Override
  public void analyzeData() {
    do {
      System.out.println("Choose your Destiny?");
      System.out.println("--------------------------------------");
      System.out.println("1: print player in one week");
      System.out.println("2: print detail for player");
      System.out.println("3: print player and game count");
      System.out.println("4: print popular category");
      System.out.println("0: Quit");
    } while (readInput());
  }

  public boolean readInput() {
    //test printPlayerByTimeRange
    long oneWeekMillis = 7 * 24 * 60 * 60 * 1000L; // One week in milliseconds
    Date currentDate = new Date(); // Current date and time
    Date oneWeekAgoDate = new Date(currentDate.getTime() - oneWeekMillis); // Date one week ago

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
          printPlayersByTimeRange(oneWeekAgoDate, currentDate);
          break;
        case "2":
          System.out.println("Enter Player ID:");
          String playerIdInput = reader.readLine();
          try {
            int playerId = Integer.parseInt(playerIdInput);
            printGameDetailsForPlayer(playerId);
          } catch (NumberFormatException e) {
            System.out.println("Invalid Player ID. Please enter a numeric value.");
          }
          break;
        case "3":
          printPlayersAndGameCounts();
          break;
        case "4":
          printCategoryPopularity();
          break;
        default:
          System.out.println("Input Error");
          break;
      }

      return true ;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public void printPlayersByTimeRange(Date startDate, Date endDate) {
    EntityManager em = lab02EntityManager.getEntityManager();

    try {
      // creating the JPQL query
      TypedQuery<String> query = em.createQuery(
          "SELECT DISTINCT p.name FROM Player p JOIN Game g ON p.playerId = g.player.playerId WHERE g.startTime BETWEEN :startDate AND :endDate",
          String.class);

      // setting parameters
      query.setParameter("startDate", startDate);
      query.setParameter("endDate", endDate);

      // executing the query
      List<String> playerNames = query.getResultList();

      // printing the result
      if (playerNames.isEmpty()) {
        System.out.println("No players found in the specified time range.");
      } else {
        System.out.println("Players who played in the specified time range:");
        for (String playerName : playerNames) {
          System.out.println(playerName);
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
  public void printGameDetailsForPlayer(int playerId) {
    EntityManager em = lab02EntityManager.getEntityManager();

    try {
      // JPQL query string
      String query = "SELECT g.gameId, g.startTime, a " +
          "FROM Game g JOIN g.answerList a " +
          "WHERE g.player.playerId = :playerId " +
          "ORDER BY g.gameId";

      // creating the JPQL query
      TypedQuery<Object[]> typedQuery = em.createQuery(query, Object[].class);

      // setting parameter
      typedQuery.setParameter("playerId", playerId);

      // executing the query
      List<Object[]> results = typedQuery.getResultList();

      // checking and printing the result
      if (results.isEmpty()) {
        System.out.println("No game data found for player with ID: " + playerId);
      } else {
        System.out.println("Game details for player ID " + playerId + ":");
        int countIsCorrect = 0;
        for (Object[] result : results) {
          int gameId = (int) result[0];
          Date startTime = (Date) result[1];
          GameAnswer answer = (GameAnswer) result[2]; // Assuming GameAnswer is a defined class
          System.out.println("Game ID: " + gameId + ", Start Time: " + startTime + ", Question: " + answer.getQuestionId() + ", Answer : " +answer.getAnswerId());
          if(answer.isCorrect()) {
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

  public void printPlayersAndGameCounts() {
    EntityManager em = lab02EntityManager.getEntityManager();

    try {
      String jpql = "SELECT p.name, COUNT(g) as amount " +
          "FROM Player p JOIN Game g " +
          "WHERE p.playerId = g.player.playerId " +
          "GROUP BY p.name " +
          "ORDER BY amount DESC";

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

  public void printCategoryPopularity() {
    EntityManager em = lab02EntityManager.getEntityManager();

    try {
      String jpql = "SELECT c.name, COUNT(q) as amount " +
          "FROM Question q JOIN q.category c, Game g JOIN g.answerList ga " +
          "WHERE q.questionId = ga.questionId " +
          "GROUP BY c.name " +
          "ORDER BY amount DESC";

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


