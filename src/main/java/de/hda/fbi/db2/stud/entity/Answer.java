package de.hda.fbi.db2.stud.entity;

import javax.persistence.Embeddable;


@Embeddable
public class Answer {

  private int answerId;

  private boolean isCorrect;

  private String answerText;

  public Answer() {
  }

  /**
   * constructor for answer.
   *
   * @param answerId        id of the answer
   * @param answerText answer
   */
  public Answer(int answerId, String answerText) {
    this.answerId = answerId;
    this.answerText = answerText;
  }

  public int getAId() {
    return answerId;
  }

  public void setAId(int id) {
    this.answerId = id;
  }


  public boolean isCorrect() {
    return isCorrect;
  }

  public void setCorrect(boolean correct) {
    isCorrect = correct;
  }

  public String getAnswerText() {
    return answerText;
  }

  public void setAnswerText(String answer) {
    this.answerText = answer;
  }


}