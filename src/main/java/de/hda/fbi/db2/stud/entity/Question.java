package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.ExistenceType;

/**
 * Represents a question with a list of possible answers.
 */
@Entity
@Table(name = "question")
@ExistenceChecking(ExistenceType.ASSUME_EXISTENCE)

public class Question {

  @Id
  private int id;

  @Column(nullable = false)
  private String title;

  @OneToMany(mappedBy = "question",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  @OrderColumn(name = "sequence")
  private List<Answer> choices = new ArrayList<>();

  @Column(nullable = false)
  private int indexCorrectAnswer;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Category category;

  /**
   * Default constructor initializes name to "NOT_YET_SET" and initializes choices list.
   * */
  public Question() {
    this.id = 0;
    this.title = "NOT_YET_SET";
    this.choices = new ArrayList<>();
    this.indexCorrectAnswer = -1;
    this.category = null;
  }

  /**
   * Constructs a new Question with the specified details.
   *
   * @param id the unique identifier for the question
   * @param title the title of the question
   * @param choices a list of possible answers to the question
   * @param indexCorrectAnswer the index of the correct answer in the list of choices
   * @param category the category to which this question belongs
   */
  public Question(int id,
      String title,
      List<Answer> choices,
      int indexCorrectAnswer,
      Category category) {
    this.id = id;
    this.title = title;
    this.choices = choices;
    this.indexCorrectAnswer = indexCorrectAnswer;
    this.category = category;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Answer> getChoices() {
    return choices;
  }

  public void setChoices(List<Answer> choices) {
    this.choices = choices;
  }

  public int getIndexCorrectAnswer() {
    return indexCorrectAnswer;
  }

  public void setIndexCorrectAnswer(int indexCorrectAnswer) {
    this.indexCorrectAnswer = indexCorrectAnswer;
  }

  public Category getCategory() {
    return category;
  }

  /**
   * Sets the category of this question.
   *
   * @param category the category to be assigned to this question
   */
  public void setCategory(Category category) {
    this.category = category;
  }

  /**
   * Retrieves the correct answer text for this question.
   *
   * @return the text of the correct answer
   * @throws RuntimeException if the index of the correct answer is out of range
   */
  public String getCorrectAnswer() {
    if (indexCorrectAnswer < 1 || indexCorrectAnswer > choices.size()) {
      throw new RuntimeException("Invalid answer id: " + indexCorrectAnswer);
    }
    return choices.get(indexCorrectAnswer - 1).getText();
  }

  @Override
  public String toString() {
    return title + "\n" + choices + "\nCorrect: " + indexCorrectAnswer + "\n";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Question other = (Question) obj;
    if (id != other.id) {
      return false;
    }
    {
      return true;
    }
  }
}
