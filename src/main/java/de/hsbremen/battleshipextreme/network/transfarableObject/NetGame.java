package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.network.IDisposable;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.server.ClientHandler;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by cschaf on 30.04.2015.
 */
public class NetGame extends TransferableObject {
    private String id;
    private String name;
    private Settings settings;
    private ArrayList<ClientHandler> joinedPlayers;
    private int maxPlayers;
    private String password;
    private boolean isPrivate;
    private ArrayList<Turn> turns;

    public NetGame(String name, Settings settings) {
        //this.id = UUID.randomUUID().toString();
        this.id = "123456";
        this.name = name;
        this.settings = settings;
        this.joinedPlayers = new ArrayList<ClientHandler>();
        this.maxPlayers = 6;
        this.password = "";
        this.isPrivate = false;
        this.turns = new ArrayList<Turn>();
    }

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

    @Override
    public String toString() {
        if(joinedPlayers != null){
            return getName()+ " (" + this.getJoinedPlayers().size() + "/ " + getMaxPlayers() + ")";
        }
        return getName() + " ( ? / " + getMaxPlayers() + ")" ;

    }
}