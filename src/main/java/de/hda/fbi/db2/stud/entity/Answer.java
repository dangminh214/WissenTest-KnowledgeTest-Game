package de.hda.fbi.db2.stud.entity;

import javax.persistence.Embeddable;

@Embeddable
public class Answer {

  private String answer;

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public boolean isRight() {
    return isRight;
  }

  public void setRight(boolean right) {
    isRight = right;
  }

  boolean isRight;

  public Answer(String answer) {
    isRight = false;
    this.answer = answer;
  }

  public Answer(String answer, boolean checkAnswer) {
    this.answer = answer;
    this.isRight = checkAnswer;
  }

  public Answer() {

  }
}