package de.hda.fbi.db2.api;

import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * API Class for lab01 Created by l.koehler on 05.08.2019.
 */
public abstract class Lab01Data {

  /**
   * Can be overridden to perform additional initialization tasks.
   */
  public void init() {
  }
  /**
   * Return all questions.
   *
   * @return questions

   */
  public abstract List<Question> getQuestions() throws URISyntaxException, IOException;

  /**
   * Return all categories.
   *
   * @return categories
   */

  public abstract List<Category> getCategories() throws URISyntaxException, IOException;

  /**
   * Save the CSV data in appropriate objects.
   *
   * @param csvLines CSV lines, each line is a String array consisting of the columns of the line.
   *                 The first line consists of the headers of the CSV columns.
   */
  public abstract void loadCsvFile(List<String[]> csvLines) throws URISyntaxException, IOException;
}