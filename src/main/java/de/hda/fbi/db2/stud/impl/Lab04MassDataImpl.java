package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab04MassData;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.Question;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

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
    while (!threadPool.isTerminated()) {
      // wait for all threads to finish
    }
    Query query = em.createQuery("SELECT count(p) FROM Player p");
    Long playerCount = (Long) query.getSingleResult();
    System.out.printf("Overall Players: %d\n", playerCount);

    // Stop the timer and calculate the duration
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    System.out.printf("Execution time: %d milliseconds%n", duration);
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
}


