package de.hda.fbi.db2.stud.entity;

import de.hda.fbi.db2.controller.CsvDataReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity

public class Category {

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Category category1 = (Category) o;
    return id == category1.id && Objects.equals(name, category1.name)
        && Objects.equals(questions, category1.questions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public String getName() {
    return name;
  }

  public void setName(String question) {
    this.name = question;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  @Column(unique = true)
  private String name;

  public Category() {

  }

  public ArrayList<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(ArrayList<Question> questions) {
    this.questions = questions;
  }

  @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
  private ArrayList<Question> questions = new ArrayList<>();

  /**
   * Constructor of class Category.
   *
   * @param q type of the category
   * @throws URISyntaxException syntax exception
   * @throws IOException        read data exception
   */
  public Category(String q) throws URISyntaxException, IOException {
    this.name = q;
    List<String[]> line = CsvDataReader.read();
    for (int i = 1; i < line.size(); i++) {
      if (q.equals(line.get(i)[7])) {
        Question ques = new Question(line.get(i), this);
        questions.add(ques);
      }
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}