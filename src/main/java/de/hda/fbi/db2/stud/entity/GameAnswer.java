package de.hda.fbi.db2.stud.entity;

import javax.persistence.Embeddable;

@Embeddable
public class GameAnswer {

  private int answerId;
  private long questionId;

  private boolean isCorrect;


  public GameAnswer() {
  }

  public boolean isCorrect() {
    return isCorrect;
  }

  public void setCorrect(boolean correct) {
    isCorrect = correct;
  }


  public long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(long questionId) {
    this.questionId = questionId;
  }

  public int getAnswerId() {
    return answerId;
  }

  public void setAnswerId(int answerId) {
    this.answerId = answerId;
  }
}
