package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Question {

  @Id
  private long questionId;


  private String questionText;


  @ElementCollection
  private List<Answer> answerList = new ArrayList<>();


  @ManyToOne
  private Category category;

  public Question() {}

  /**
   * constructor for question.
   *
   * @param questionId id
   * @param questionText question
   * @param answer1 answer 1
   * @param answer2 answer 2
   * @param answer3 answer 3
   * @param answer4 answer 4
   * @param correctAnswer solution
   * @param category category
   */
  public Question(int questionId,
      String questionText,
      String answer1,
      String answer2,
      String answer3,
      String answer4,
      String correctAnswer,
      Category category) {
    this.questionId = questionId;
    this.questionText = questionText;


    //adding answer to answer list
    answerList.add(new Answer(0, answer1));
    answerList.add(new Answer(1, answer2));
    answerList.add(new Answer(2, answer3));
    answerList.add(new Answer(3, answer4));

    //setting correct answer
    for (Answer a : answerList) {
      if (a.getAId() == Integer.parseInt(correctAnswer)) {
        a.setCorrect(true);
        break;
      }
    }
    this.category = category;

    //add question to the category
    this.category.addQuestion(this);

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Question question = (Question) o;
    return questionId == question.questionId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(questionId);
  }

  //getter + setter
  public long getQId() {
    return questionId;
  }

  public void setQId(long qid) {
    this.questionId = qid;
  }


  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }


  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public List<Answer> getAnswerList() {
    return answerList;
  }

  public void setAnswerList(List<Answer> antwortList) {
    this.answerList = antwortList;
  }
}
