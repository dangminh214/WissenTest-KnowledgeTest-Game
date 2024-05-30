package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab02EntityManager;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class Lab02EntityManagerImpl extends Lab02EntityManager {
  private EntityManagerFactory emf;

  @Override
  public void init() {
    emf = Persistence.createEntityManagerFactory("fbi-postgresPU");
    //    emf = Persistence.createEntityManagerFactory("docker-local-postgresPU");
  }

  @Override
  public void destroy() {
    emf.close();
  }

  /**
   * Persists the data related to questions and categories into the database.
   *
   * @throws RuntimeException if an error occurs while persisting the data.
   */
  public void persistData() {
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();

      //persist questions
      List<?> questions = lab01Data.getQuestions();
      for (Object q : questions) {
        em.persist(q);
      }

      //persist categories
      List<?> categories = lab01Data.getCategories();
      for (Object c : categories) {
        em.persist(c);
      }

      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      System.err.println("Error while trying to persist questions.");
    } finally {
      em.close();
    }
  }

  @Override
  public EntityManager getEntityManager() {
    return emf.createEntityManager();
  }
}