package de.hda.fbi.db2.stud.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents an answer to a question.
 */
@Entity
@Table(name = "answer")
public class Answer {
  @Id
  private String text;

  @ManyToOne
  @JoinColumn(name = "question")
  private Question question;

  /**
   * Default constructor initializes text to "NOT_YET_SET" and initializes question for this answer.
   * */
  public Answer() {
    this.text = "NOT_YET_SET";
    this.question = new Question();
  }

  /**
   * Constructs an Answer with the specified id, text, and question.
   *
   * @param text the text of the answer.
   * @param question the question to which this answer belongs.
   */
  public Answer(String text, Question question) {
    this.text = text;
    this.question = question;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  @Override
  public String toString() {
    return text;
  }
}
