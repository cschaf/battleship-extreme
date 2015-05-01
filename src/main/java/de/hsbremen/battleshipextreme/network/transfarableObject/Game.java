package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.network.TransferableType;

import java.util.ArrayList;

/**
 * Created by cschaf on 30.04.2015.
 */
public class Game extends TransferableObject {
    private String name;
    private Settings settings;
    private int joinedPlayers;
    private int maxPlayers;
    private String password;
    private boolean isPrivate;
    private ArrayList<Turn> turns;

    public Game(String name, Settings settings) {
        this.name = name;
        this.settings = settings;
        this.joinedPlayers = 0;
        this.maxPlayers = 6;
        this.password = null;
        this.isPrivate = false;
        this.turns = new ArrayList<Turn>();
    }

    @Override
    public TransferableType getType() {
        return TransferableType.Game;
    }

    public void addTurn(Turn turn){
        this.turns.add(turn);
    }

    public int getJoinedPlayers() {
        return joinedPlayers;
    }

    public void setJoinedPlayers(int joinedPlayers) {
        this.joinedPlayers = joinedPlayers;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!password.equals("")) {
            this.password = password;
            this.isPrivate = true;
        }
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getName() {
        return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
