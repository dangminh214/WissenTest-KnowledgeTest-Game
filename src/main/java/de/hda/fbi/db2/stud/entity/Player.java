package de.hda.fbi.db2.stud.entity;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private int playerId;

  private String name;

  public Player() {
  }

  public Player(String name) {
    this.name = name;
  }

  public int getPlayerId() {
    return playerId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Player player = (Player) o;
    return playerId == player.playerId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerId);
  }
}