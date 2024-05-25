package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "question")
public class Question {

  private String question;

  @ElementCollection
  private ArrayList<Answer> answers = new ArrayList<>();
  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "categoryID")
  private Category category;
  @Id
  private int questionID;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Question question1 = (Question) o;
    return questionID == question1.questionID && Objects.equals(question,
        question1.question) && Objects.equals(answers, question1.answers)
        && Objects.equals(category, question1.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(questionID);
  }

  public String getQuestion() {
    return question;
  }

  public Question() {

  }

  /**
   * Constructor of class Question.
   *
   * @param line     line of the question in csv File
   * @param category The Category of the question
   */

  public Question(String[] line, Category category) {
    this.question = line[1];
    this.questionID = Integer.parseInt(line[0]);
    this.category = category;
    for (int i = 2; i < 6; i++) {
      this.answers.add(new Answer(line[i], Integer.parseInt(line[6]) == i - 1));
    }

  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public int getQuestionID() {
    return questionID;
  }

  public void setQuestionID(int questionID) {
    this.questionID = questionID;
  }

  public ArrayList<Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(ArrayList<Answer> answers) {
    this.answers = answers;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }
}