package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab01Data;
import de.hda.fbi.db2.stud.entity.Answer;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lab01DataImpl extends Lab01Data {

  List<Question> questions;
  List<Category> categories;

  public void init() {
    questions = new ArrayList<>();
    categories = new ArrayList<>();
  }

  @Override
  public List<?> getQuestions() {
    return questions;
  }

  @Override
  public List<?> getCategories() {
    return categories;
  }

  @Override
  public void loadCsvFile(List<String[]> csvLines) {
    csvLines.remove(0);
    Map<String, List<Question>> csvCategories = new HashMap<>(10);
    csvLines.forEach(line -> {
      ArrayList<Answer> answers = new ArrayList<Answer>();
      Question toAdd = getQuestion(line, answers);
      var cat = csvCategories.getOrDefault(line[7], new ArrayList<Question>());
      cat.add(toAdd);
      csvCategories.put(line[7], cat);
    });

    csvCategories.forEach((catTitle, catQuestions) -> {
      Category category = new Category(catTitle);
      catQuestions.forEach(q -> {
        category.addQuestion(q);
        questions.add(q);
      });
      categories.add(category);
    });
    var sizeCategories = csvCategories.size();
    var sizeQuestions = questions.size();
    System.out.println("Anzahl Kategorien: " + sizeCategories);
    System.out.println("Anzahl Fragen: " + sizeQuestions);
  }

  private static Question getQuestion(String[] line, ArrayList<Answer> answers) {
    //AnswerA line[2]: AnswerB line[3]: AnswerC line[3]: AnswerD line[4]
    Answer answerA = new Answer(line[2], null);
    Answer answerB = new Answer(line[3], null);
    Answer answerC = new Answer(line[4], null);
    Answer answerD = new Answer(line[5], null);
    answers.add(answerA);
    answers.add(answerB);
    answers.add(answerC);
    answers.add(answerD);
    var newID = Integer.parseInt(line[0]);
    String newTitle = line[1];
    Category newCategory = new Category(line[7]);
    var indexCorrectAnswer = Integer.parseInt(line[6]);
    Question toAddQuestion = new Question(
        newID,
        newTitle,
        answers,
        indexCorrectAnswer,
        newCategory);
    answerA.setQuestion(toAddQuestion);
    answerB.setQuestion(toAddQuestion);
    answerC.setQuestion(toAddQuestion);
    answerD.setQuestion(toAddQuestion);
    System.out.println(toAddQuestion);
    return toAddQuestion;
  }
}
