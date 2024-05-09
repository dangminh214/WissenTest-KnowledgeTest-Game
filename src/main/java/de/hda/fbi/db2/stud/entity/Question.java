package de.hda.fbi.db2.stud.entity;

import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.ExistenceType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // Defines the class as entity
@Table(name = "question") // Set the table
@ExistenceChecking(ExistenceType.ASSUME_EXISTENCE)
public class Question {
    @Id
    private int id;
    @Column(nullable = false) // Null is not allowed --> Must be set
    private String title;

    @ElementCollection
    @OrderColumn(name = "sequence")
    private List<String> choices;
    @Column(nullable = false)
    private int indexCorrectAnswer;

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    @ManyToOne // Own the relation to category --> mapping is done there
    @JoinColumn(nullable = false) // Null is not allowed --> Must be set
    private Category category;

    public Question() {
        this.id = 0;
        this.title = "NOT_YET_SET";
        this.choices = new ArrayList<>();
        this.indexCorrectAnswer = -1;
        this.category = null;
    }

    public Question(int id, String title, List<String> choices, int indexCorrectAnswer) {
        this.id = id;
        this.title = title;
        this.choices = choices;
        this.indexCorrectAnswer = indexCorrectAnswer;
    }

    public String getCorrectAnswer() {
        if (this.indexCorrectAnswer < 1 || this.indexCorrectAnswer > this.choices.size()) {
            throw new RuntimeException("Invalid answer id: " + this.indexCorrectAnswer);
        }
        return this.choices.get(indexCorrectAnswer - 1);
    }

    public List<String> getChoices() {
        return this.choices;
    }

    public int getCorrectAnswerIndex() {
        return this.indexCorrectAnswer;
    }

    public String getTitle() {
        return this.title;
    }

    public String toString() {
        return this.title + "\n" + this.choices + "\nKorrekt: " + this.indexCorrectAnswer + "\n";
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Question other = (Question) obj;
        if (id != other.id)
            return false;
        return true;
    }
}