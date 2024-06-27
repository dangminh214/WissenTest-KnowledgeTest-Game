package de.hda.fbi.db2.controller;

import de.hda.fbi.db2.api.Lab01Data;
import de.hda.fbi.db2.api.Lab02EntityManager;
import de.hda.fbi.db2.api.Lab03Game;
import de.hda.fbi.db2.api.Lab04MassData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * Controller Created by l.koehler on 05.08.2019.
 */
public class Controller {

  private static Controller controller;

  /**
   * Singleton Pattern.
   *
   * @return a singleton Pattern instance
   */
  public static synchronized Controller getInstance() {
    if (controller == null) {
      controller = new Controller();
    }
    return controller;
  }

  private boolean isCsvRead = false;

  private boolean isPersisted = false;

  private static final String IMPL_PACKAGE_NAME = "de.hda.fbi.db2.stud";

  private Lab01Data lab01Data;

  private Lab02EntityManager lab02EntityManager;

  private Lab03Game lab03Game;

  private Lab04MassData lab04MassData;

  private MenuController menuController;

  private Controller() {
    findImplementations();
  }

  private static <T> T createInstance(Class<?> c) throws Exception {
    if (c.isInterface()) {
      throw new IllegalArgumentException(
          "Class " + c.getSimpleName() + " must not be an interface");
    }
    if (Modifier.isAbstract(c.getModifiers())) {
      throw new IllegalArgumentException("Class " + c.getSimpleName() + " must not be abstract");
    }

    Constructor<?> constructor;
    try {
      constructor = c.getConstructor();
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "Class " + c.getSimpleName() + " has no default constructor", e);
    }

    // Trust that the caller has verified class already
    @SuppressWarnings("unchecked")
    T result = (T) constructor.newInstance();
    return result;
  }

  private void findImplementations() {
    try {
      Enumeration<URL> elements = Thread.currentThread().getContextClassLoader()
          .getResources(IMPL_PACKAGE_NAME.replace('.', '/'));
      if (!elements.hasMoreElements()) {
        return;
      }
      // Convert URL -> URI -> File to properly handle special characters or spaces in file path
      File directory = new File(elements.nextElement().toURI());
      List<Class<?>> classes = new ArrayList<>(findClasses(directory, IMPL_PACKAGE_NAME));
      for (Class<?> clazz : classes) {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
          if (superclass == Lab01Data.class) {
            if (lab01Data != null) {
              throw new IllegalStateException("Multiple classes implement Lab01Data: "
                  + lab01Data.getClass().getName() + ", " + clazz.getName());
            }
            lab01Data = createInstance(clazz);
          } else if (superclass == Lab02EntityManager.class) {
            if (lab02EntityManager != null) {
              throw new IllegalStateException("Multiple classes implement Lab02EntityManager: "
                  + lab02EntityManager.getClass().getName() + ", " + clazz.getName());
            }
            lab02EntityManager = createInstance(clazz);
          } else if (superclass == Lab03Game.class) {
            if (lab03Game != null) {
              throw new IllegalStateException("Multiple classes implement Lab03Game: "
                  + lab03Game.getClass().getName() + ", " + clazz.getName());
            }
            lab03Game = createInstance(clazz);
          } else if (superclass == Lab04MassData.class) {
            if (lab04MassData != null) {
              throw new IllegalStateException("Multiple classes implement Lab04MassData: "
                  + lab04MassData.getClass().getName() + ", " + clazz.getName());
            }
            lab04MassData = createInstance(clazz);
          }
        }
      }

      if (lab01Data == null) {
        return;
      }
      lab01Data.init();

      if (lab02EntityManager == null) {
        return;
      }
      lab02EntityManager.setLab01Data(lab01Data);
      lab02EntityManager.init();

      if (lab03Game == null) {
        return;
      }
      lab03Game.setLab01Data(lab01Data);
      lab03Game.setLab02EntityManager(lab02EntityManager);
      lab03Game.init();

      if (lab04MassData == null) {
        return;
      }
      lab04MassData.setLab01Data(lab01Data);
      lab04MassData.setLab02EntityManager(lab02EntityManager);
      lab04MassData.setLab03Game(lab03Game);
      lab04MassData.init();
    } catch (Exception e) {
      throw new IllegalStateException("API classes are not implemented correctly", e);
    }
  }

  private List<Class<?>> findClasses(File directory, String packageName)
      throws ClassNotFoundException {
    List<Class<?>> classes = new ArrayList<>();
    if (!directory.exists()) {
      return classes;
    }
    File[] files = directory.listFiles();
    assert files != null;
    for (File file : files) {
      if (file.isDirectory()) {
        assert !file.getName().contains(".");
        classes.addAll(findClasses(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        classes.add(Class.forName(packageName + '.' + file.getName()
            .substring(0, file.getName().length() - 6)));
      }
    }
    return classes;
  }

  private boolean isMissingLabImplementation(int highestLabNumber) {
    if (highestLabNumber >= 1 && lab01Data == null) {
      System.err.println("Could not find Lab01Data implementation");
      return true;
    }
    if (highestLabNumber >= 2 && lab02EntityManager == null) {
      System.err.println("Could not find Lab02EntityManager implementation");
      return true;
    }
    if (highestLabNumber >= 3 && lab03Game == null) {
      System.err.println("Could not find Lab03Game implementation");
      return true;
    }
    if (highestLabNumber >= 4 && lab04MassData == null) {
      System.err.println("Could not find Lab04MassData implementation");
      return true;
    }
    return false;
  }

  /**
   * Reads the CSV data.
   */
  public void readCsv() {
    if (isMissingLabImplementation(1)) {
      return;
    }
    try {
      List<String> availableFiles = CsvDataReader.getAvailableFiles();
      for (String availableFile : availableFiles) {
        final List<String[]> additionalCsvLines = CsvDataReader.read(availableFile);
        lab01Data.loadCsvFile(additionalCsvLines);
      }
    } catch (URISyntaxException | IOException e) {
      throw new RuntimeException("Failed reading CSV data", e);
    }
    isCsvRead = true;
  }

  public Lab01Data getLab01Data() {
    return lab01Data;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Lab02EntityManager getLab02EntityManager() {
    return lab02EntityManager;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Lab03Game getLab03Game() {
    return lab03Game;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Lab04MassData getLab04MassData() {
    return lab04MassData;
  }

  /**
   * Starts the main menu.
   */
  public void startMenu() {
    if (isMissingLabImplementation(3)) {
      return;
    }

    if (menuController == null) {
      menuController = new MenuController(this);
    }
    menuController.showMenu();
  }

  /**
   * Persists data.
   */
  public void persistData() throws URISyntaxException, IOException {
    if (isMissingLabImplementation(2)) {
      return;
    }

    EntityManager entityManager = lab02EntityManager.getEntityManager();
    String schemaGeneration = (String) entityManager.getProperties()
        .get("javax.persistence.schema-generation.database.action");

    entityManager.close();

    if (schemaGeneration.equals("drop-and-create") || schemaGeneration.equals("create")) {
      lab02EntityManager.persistData();
    }
    isPersisted = true;
  }

  public boolean isCsvRead() {
    return isCsvRead;
  }

  public boolean isPersisted() {
    return isPersisted;
  }

  /**
   * Creates mass data.
   */
  public void createMassData() {
    if (isMissingLabImplementation(4)) {
      return;
    }

    lab04MassData.createMassData();
  }

  public void analyzeData() {
    lab04MassData.analyzeData();
  }
}
