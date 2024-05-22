package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "category")
public class Category {
  @Id
    private String name;
  @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private List<Question> questions;

  /**
   * Default constructor initializes name to "NOT_YET_SET" and initializes questions list.
  * */
  public Category() {
    this.name = "NOT_YET_SET";
    this.questions = new ArrayList<>();
  }

  /**
   * Default constructor initializes name to "NOT_YET_SET" and initializes questions list.
  * */
  public Category(String name) {
    this.name = name;
    this.questions = new ArrayList<>();
  }

  /**
   * Gets the name of the category.
   *
   * @return the name of the category
   */
  public String getName() {
    return this.name;
  }

  /**
   * Adds a question to the category.
   *
   * @param question the question to add
   */
  public void addQuestion(Question question) {
    question.setCategory(this);
    this.questions.add(question);
  }

  /**
   * Gets a random question from the category.
   *
   * @return a random question
   */
  public Question getRandomQuestion() {
    final int min = 0;
    final int max = this.questions.size();
    int randomIndex = ThreadLocalRandom.current().nextInt(min, max);
    return this.questions.get(randomIndex);
  }

  public List<Question> getQuestions() {
    return this.questions;
  }

  private String printEveryQuestion() {
    String allQuestions = "";
    for (Question question : this.questions) {
      allQuestions += question.toString();
    }
    return allQuestions;
  }

  public String toString() {
    return this.name + " [" + this.questions.size() + "]" + "\n" + printEveryQuestion();
  }

  @Override
    public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Category other = (Category) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }
}
