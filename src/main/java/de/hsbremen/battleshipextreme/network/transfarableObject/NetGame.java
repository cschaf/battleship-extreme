package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.network.ClientGameIndexQueue;
import de.hsbremen.battleshipextreme.server.ClientHandler;

import java.util.*;

/**
 * Created by cschaf on 30.04.2015.
 */
public class NetGame extends TransferableObject {
    private String id;
    private String name;
    private Settings settings;
    private HashMap<Integer, ClientHandler> players;
    private ArrayList<Integer> clientIds;
    private ClientGameIndexQueue<Integer> clientTurnOrder;
    private int maxPlayers;
    private String password;
    private boolean isPrivate;
    private ArrayList<Turn> turns;

    public NetGame(String name, Settings settings) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.settings = settings;
        this.players = new HashMap<Integer, ClientHandler>();
        this.maxPlayers = settings.getPlayers();
        this.password = "";
        this.isPrivate = false;
        this.turns = new ArrayList<Turn>();
        this.clientIds = new ArrayList<Integer>();
        this.clientTurnOrder = new ClientGameIndexQueue<Integer>();
        for (int i = 0; i < maxPlayers; i++) {
            clientTurnOrder.add(i);
            clientIds.add(i);
            players.put(i, null);
        }

    }

    public TransferableType getType() {
        return TransferableType.Game;
    }

    public void addTurn(Turn turn) {
        this.turns.add(turn);
    }

    public ArrayList<ClientHandler> getJoinedPlayers() {
        ArrayList<ClientHandler> result = new ArrayList<ClientHandler>();
        for (int clientIndex : players.keySet()) {
            if (players.get(clientIndex) != null) {
                result.add(players.get(clientIndex));
            }
        }
        return result;
    }

    public void addPlayer(ClientHandler player) {
        if (!this.isGameFull()) {
            this.players.put(clientIds.get(0), player);
            this.clientIds.remove(clientIds.get(0));
        }
    }

    private boolean isGameFull() {
        int number = 0;
        for (int clientIndex : players.keySet()) {
            if (players.get(clientIndex) != null) {
                number++;
            }
        }
        return number >= maxPlayers;
    }

    public void removePlayer(ClientHandler player) {
        if (players.containsValue(player)) {
            int index = -1;
            for (Map.Entry<Integer, ClientHandler> entry : players.entrySet()) {
                if (entry.getValue().equals(player)) {
                    index = entry.getKey();
                }
            }
            players.put(index, null);
            clientIds.add(index);
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

        return getName() + " (" + this.getJoinedPlayers().size() + "/ " + getMaxPlayers() + ")";
    }
}
