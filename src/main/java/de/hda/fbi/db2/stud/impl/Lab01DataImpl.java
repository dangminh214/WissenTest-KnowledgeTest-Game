package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab01Data;
import de.hda.fbi.db2.stud.entity.Answer;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Lab01DataImpl extends Lab01Data {
  List<Question> questionList;
  List<Category> categoryList;
  HashMap<String, Category> categoryCheck;

  int categoryCounter;

  public Lab01DataImpl() {
  }

  @Override
  public void init() {
    questionList = new ArrayList<>();
    categoryList = new ArrayList<>();
    categoryCheck = new HashMap<>();
    categoryCounter = 0;
  }

  @Override
  public List<?> getQuestions() {
    return questionList;
  }

  @Override
  public List<?> getCategories() {
    return categoryList;
  }


  /**
   * Add a new category to the category list.
   *
   * @param categoryName name of category
   */
  private void addCategory(String categoryName) {
    if (categoryList.isEmpty()) {
      Category newCategory = new Category(categoryName, categoryCounter);
      categoryCounter++;
      categoryList.add(newCategory);
      categoryCheck.put(categoryName,newCategory);
      System.out.println("added a new Category " + categoryName);
    } else {
      if (findCategory(categoryName) == null) {
        Category newCategory = new Category(categoryName, categoryCounter);
        categoryCounter++;
        categoryList.add(newCategory);
        categoryCheck.put(categoryName,newCategory);
        System.out.println("added a new Category " + categoryName);
      }
    }
  }

  /**
   * Find if a category is already exists in the category list.
   *
   * @param categoryName category name
   * @return category if found, null if not found
   */
  private Category findCategory(String categoryName) {
    return categoryCheck.getOrDefault(categoryName, null);
  }

  @Override
  public void loadCsvFile(List<String[]> csvLines) {
    for (String[] line : csvLines) {
      if (line[1].equals("_frage")) {
        continue;
      }
      try {
        int id = Integer.parseInt(line[0]);
        //find and add category if not exists in the list
        addCategory(line[7]);
        Question f = new Question(id,
            line[1],
            line[2],
            line[3],
            line[4],
            line[5],
            line[6],
            findCategory(line[7]));
        questionList.add(f);
      } catch (NumberFormatException e) {
        System.out.println("error");
        // Skip the line if line[0] is not parseable to an integer
      }
    }
    printData();
  }

  @Override
  public void printData() {
    System.out.println("\n\nPrinting Question\n\n");
    for (Question q : questionList) {
      int loesung = -1;
      System.out.println("Question ID: " + q.getQId());
      System.out.println("Question: " + q.getQuestionText());
      for (Answer a : q.getAnswerList()) {
        System.out.println("Answer ID: " + a.getAId());
        System.out.println("Answer: " + a.getAnswerText());
        if (a.isCorrect()) {
          loesung = a.getAId();
        }
      }
      System.out.println("Loesung: " + loesung);
      System.out.println("Category: " + q.getCategory().getName());
    }
    System.out.println("Menge der Fragen: " + questionList.size());
    System.out.println("Menge der Kategorien: " + categoryList.size());
  }
}