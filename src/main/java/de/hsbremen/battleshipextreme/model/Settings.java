package de.hsbremen.battleshipextreme.model;

import java.io.Serializable;

public class Settings implements Serializable{
    private int players;
    private int boardSize;
    private int destroyers;
    private int frigates;
    private int corvettes;
    private int submarines;

    public Settings(int players, int boardSize, int destroyers, int frigates, int corvettes, int submarines) {
        this.setPlayers(players); // evtl. Exception bei zu wenig oder zu vielen Players
        this.setBoardSize(boardSize); // evtl. Exception bei zu kleinem Board
        this.boardSize = boardSize;
        this.destroyers = destroyers;
        this.frigates = frigates;
        this.corvettes = corvettes;
        this.submarines = submarines;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int getDestroyers() {
        return destroyers;
    }

    public void setDestroyers(int destroyers) {
        this.destroyers = destroyers;
    }

    public int getFrigates() {
        return frigates;
    }

    public void setFrigates(int frigates) {
        this.frigates = frigates;
    }

    public int getCorvettes() {
        return corvettes;
    }

    public void setCorvettes(int corvettes) {
        this.corvettes = corvettes;
    }

    public int getSubmarines() {
        return submarines;
    }

    public void setSubmarines(int submarines) {
        this.submarines = submarines;
    }
}
