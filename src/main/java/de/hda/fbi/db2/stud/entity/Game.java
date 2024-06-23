package de.hda.fbi.db2.stud.entity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;


@Entity
public class Game {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private int gameId;
  private Date startTime;
  private Date endTime;

  @ManyToOne
  @JoinColumn(name = "playerId")
  private Player player;

  @Transient // Don't save in database
  private List<Question> questions;

  @ElementCollection
  @CollectionTable(name = "game_answerlist", joinColumns = @JoinColumn(name = "gameId"))
  private List<GameAnswer> answerList = new ArrayList<>();

  public Game() {
  }

  public Game(Player player, List<Question> questions) {
    this.player = player;
    this.questions = questions;
  }

  // Getter und Setter Methoden
  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Game game = (Game) o;
    return gameId == game.gameId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameId);
  }

  /**
   * Adds an answer to the answer list based on the index of the selected answer
   * of the given question.
   *
   * @param question    The question to which the answer is being added.
   * @param answerIndex The index of the selected answer in the question's answer list.
   */
  public void addAnswer(Question question, int answerIndex) {
    GameAnswer answer = new GameAnswer();
    answer.setCorrect(question.getAnswerList().get(answerIndex).isCorrect());
    answer.setQuestionId(question.getQId());
    answer.setAnswerId(question.getAnswerList().get(answerIndex).getAId());
    this.answerList.add(answer);
  }
}

