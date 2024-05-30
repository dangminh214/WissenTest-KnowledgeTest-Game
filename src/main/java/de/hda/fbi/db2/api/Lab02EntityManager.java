package de.hda.fbi.db2.api;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * API Class for lab02 Created by l.koehler on 05.08.2019.
 */
public abstract class Lab02EntityManager {

  /**
   * Creates the {@link EntityManagerFactory} and stores it in a field. {@link #destroy()} should
   * then clean up the factory later again.
   */
  public abstract void init();

  /**
   * {@linkplain EntityManagerFactory#close() Closes} the {@code EntityManagerFactory} created by
   * {@link #init()}.
   */
  public abstract void destroy();

  /**
   * You can use the data from lab01, this variable will be automatically set.
   */
  protected Lab01Data lab01Data;

  /**
   * Setter for Lab01Data. Do not touch this method.
   *
   * @param lab01Data lab01Data
   */
  public final void setLab01Data(Lab01Data lab01Data) {
    this.lab01Data = lab01Data;
  }

  /**
   * Here you have to persist the data in the database.
   */
  public abstract void persistData() throws URISyntaxException, IOException;

  /**
   * Creates a new {@code EntityManager}.
   *
   * @return new EntityManager
   */
  public abstract EntityManager getEntityManager();
}
