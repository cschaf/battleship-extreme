package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.server.ClientHandler;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by cschaf on 30.04.2015.
 */
public class Game extends TransferableObject {
    private String id;
    private String name;
    private Settings settings;
    private ArrayList<ClientHandler> joinedPlayers;
    private int maxPlayers;
    private String password;
    private boolean isPrivate;
    private ArrayList<Turn> turns;

    public Game(String name, Settings settings) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.settings = settings;
        this.joinedPlayers = new ArrayList<ClientHandler>();
        this.maxPlayers = 6;
        this.password = null;
        this.isPrivate = false;
        this.turns = new ArrayList<Turn>();
    }

    @Override
    public TransferableType getType() {
        return TransferableType.Game;
    }

    public void addTurn(Turn turn) {
        this.turns.add(turn);
    }

    public ArrayList<ClientHandler> getJoinedPlayers() {
        return joinedPlayers;
    }

    public void addPlayer(ClientHandler player) {
        if (this.joinedPlayers.size() < this.maxPlayers) {
            this.joinedPlayers.add(player);
        }
    }

    public void removePlayer(ClientHandler player) {
        ClientHandler removeClient = null;
        for (ClientHandler each : this.joinedPlayers) {
            if (each == player) {
                removeClient = each;
                break;
            }
        }
        if (removeClient != null) {
            this.joinedPlayers.remove(removeClient);
        }
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

    public String getId() {
        return id;
    }
}
