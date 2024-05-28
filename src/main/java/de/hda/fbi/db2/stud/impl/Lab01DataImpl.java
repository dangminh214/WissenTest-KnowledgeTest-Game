package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab01Data;
import de.hda.fbi.db2.controller.CsvDataReader;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lab01DataImpl extends Lab01Data {

  List<Question> allQuestion = new ArrayList<>();

  public Lab01DataImpl() {

  }

  public void init() {
    allQuestion = new ArrayList<>();
  }

  @Override
  public List<Question> getQuestions() throws URISyntaxException, IOException {
    List<String[]> line = CsvDataReader.read();
    List<Question> question = new ArrayList<>();
    for (int i = 1; i < line.size(); i++) {
      String[] values = line.get(i);
      Question newQuestion = new Question(values, new Category(values[7]));
      question.add(newQuestion);
    }
    System.out.println("Number of Question: " + question.size());
    return question;
  }

  @Override
  public List<Category> getCategories() throws URISyntaxException, IOException {
    List<String[]> line = CsvDataReader.read();
    List<Category> categories = new ArrayList<>();
    List<String> listString = new ArrayList<>();
    for (int i = 1; i < line.size(); i++) {
      listString.add(line.get(i)[7]);
    }
    //remove Duplicate
    List<String> newList = listString.stream()
        .distinct()
        .collect(Collectors.toList());
    for (String s : newList) {
      Category cgr = new Category(s);
      categories.add(cgr);
    }
    System.out.println("Number of Categories " + categories.size());
    return categories;
  }

  @Override
  public void loadCsvFile(List<String[]> csvLines) throws URISyntaxException, IOException {
    for (int i = 1; i < csvLines.size(); i++) {
      String[] values = csvLines.get(i);
      Question u = new Question(values, new Category(values[7]));
      allQuestion.add(u);
    }
  }
}