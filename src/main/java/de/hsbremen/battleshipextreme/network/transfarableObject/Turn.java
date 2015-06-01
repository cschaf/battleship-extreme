package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Orientation;
import de.hsbremen.battleshipextreme.model.player.Player;
import de.hsbremen.battleshipextreme.network.TransferableType;

/**
 * Created by cschaf on 30.04.2015.
 */
public class Turn extends TransferableObject{
  private String gameId;
  private Player from;
  private Player to;
  private int fieldX;
  private int fieldY;
  private Orientation orientation;

  public Turn(Player from, Player to, int fieldX, int fieldY, Orientation orientation){
    this.from = from;
    this.to = to;
    this.fieldX = fieldX;
    this.fieldY = fieldY;
    this.orientation = orientation;
  }

  public TransferableType getType(){
    return TransferableType.Turn;
  }

  public Player getFrom(){
    return from;
  }

  public Player getTo(){
    return to;
  }

  public String getGameId(){
    return gameId;
  }

  public int getFieldX(){
    return fieldX;
  }

  public int getFieldY(){
    return fieldY;
  }

  public Orientation getOrientation(){
    return orientation;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }
}