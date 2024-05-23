package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab02EntityManager;
import de.hda.fbi.db2.stud.entity.Category;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Lab02Impl extends Lab02EntityManager {

  private EntityManager em;
  private Lab01DataImpl lab01Impl = new Lab01DataImpl();

  @Override
  public void init() {
    em = getEntityManager();
  }

  @Override
  public void destroy() {
    em.close();
  }

  @Override
  public void persistData() {
    List<Category> categoryList = null;

    categoryList = lab01Impl.getCategories();

    em.getTransaction().begin();

    for (Category category : categoryList) {
      em.persist(category);
    }
    em.getTransaction().commit();
    em.close();
  }

  @Override
  public EntityManager getEntityManager() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("fbi-postgresPU");
    return emf.createEntityManager();
  }
}