package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.stud.entity.Category;
import java.net.URISyntaxException;
import java.util.List;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import de.hda.fbi.db2.api.Lab02EntityManager;

public class Lab02EntityManagerImpl extends Lab02EntityManager {
  private EntityManager em;
  private Lab01DataImpl lab01 = new Lab01DataImpl();

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
    List<Category> cateList = lab01.getCategories();

    if (cateList != null && !cateList.isEmpty()) {
      em.getTransaction().begin();
      for (Category category : cateList) {
        em.persist(category);
      }
      em.getTransaction().commit();
    }

    em.close();
  }

  @Override
  public EntityManager getEntityManager() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("fbi-postgresPU");
    EntityManager em = emf.createEntityManager();
    return em;
  }

}
