package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab02EntityManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class Lab02EntityManagerImpl extends Lab02EntityManager {
  EntityManagerFactory entityManagerFactory;

  @Override
  public void init() {
    // this.entityManagerFactory =
    // Persistence.createEntityManagerFactory("fbi-postgresPU");
    this.entityManagerFactory = Persistence.createEntityManagerFactory("fbi-postgresPU");
  }

  @Override
  public void destroy() {
    this.entityManagerFactory.close();
  }

  @Override
  public void persistData() throws URISyntaxException, IOException {
    var categories = this.lab01Data.getCategories();
    var mgr = this.getEntityManager();

    // Fragen werden nicht explizit hinzugefügt, da sie bereits mit den Kategorien
    // erstellt wurden.
    mgr.getTransaction().begin();
    for (Iterator<?> i = categories.iterator(); i.hasNext();) {
      var c = i.next();
      mgr.persist(c);
    }
    mgr.flush();
    mgr.clear();
    mgr.getTransaction().commit();
  }

  @Override
  public EntityManager getEntityManager() {
    return this.entityManagerFactory.createEntityManager();
  }

}
