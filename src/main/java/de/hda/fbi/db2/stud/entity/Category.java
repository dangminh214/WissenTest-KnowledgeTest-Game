package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


@Entity
@NamedQueries({
    @NamedQuery(name = "Category.findAll",
        query = "select c from Category c")
})
public class Category {

  @Id
  int categoryId;

  @Column(unique = true)
  private String name;

  @OneToMany(mappedBy = "category")
  private List<Question> questionList;


  public Category() {
  }

  /**
   * constructor for Category.
   *
   */
  public Category(String name, int categoryId) {
    this.name = name;
    this.categoryId = categoryId;
    questionList = new ArrayList<>();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Category category = (Category) o;
    return categoryId == category.categoryId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(categoryId);
  }

  //getter + setter
  public List<Question> getQuestionList() {
    return questionList;
  }

  public void setQuestionList(List<Question> questionList) {
    this.questionList = questionList;
  }

  public String getName() {
    return name;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setName(String categoryName) {
    this.name = categoryName;
  }

  public void addQuestion(Question question) {
    questionList.add(question);
  }

}
