package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Board;
import de.hsbremen.battleshipextreme.model.FieldState;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.network.ClientGameIndexQueue;
import de.hsbremen.battleshipextreme.network.TransferableType;
import de.hsbremen.battleshipextreme.server.ClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private boolean ready;

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
        this.ready = false;

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
        int index = getIndexByClient(player);
        if (index > -1) {
            players.put(index, null);
            clientIds.add(index);
        }
    }

    public int getIndexByClient(ClientHandler handler) {
        int index = -1;
        for (Map.Entry<Integer, ClientHandler> entry : players.entrySet()) {
            ClientHandler value = entry.getValue();
            if (value != null) {
                if (value == handler) {
                    index = entry.getKey();
                }
            }
        }
        return index;
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

    public ClientGameIndexQueue<Integer> getClientTurnOrder() {
        return clientTurnOrder;
    }

    public HashMap<Integer, ClientHandler> getPlayers() {

        return players;
    }

    public void addBoard(ClientHandler clientHandler, ClientBoard board) {
        int index = getIndexByClient(clientHandler);
        if (index > -1) {
            ClientHandler clientHandler1 =  players.get(index);
            clientHandler.setOwnBoard(board.getBoard());
            players.put(index, clientHandler1);
        }
    }

    public ArrayList<Board> getAllBoards(){
        ArrayList<Board> result = new ArrayList<Board>();
        for (Map.Entry<Integer, ClientHandler> entry : players.entrySet()) {
            ClientHandler value = entry.getValue();
            if (value != null) {
                result.add(value.getOwnBoard());
            }
        }
        return result;
    }

    public boolean haveAllPlayersSetTheirShips() {
        for (Map.Entry<Integer, ClientHandler> entry : players.entrySet()) {
            ClientHandler value = entry.getValue();
            if (value != null) {
                if (value.getOwnBoard() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setGameToReady() {
        this.ready = true;
    }

    @Override
    public String toString() {

        return getName() + " (" + this.getJoinedPlayers().size() + "/ " + getMaxPlayers() + ")";
    }

    public boolean getReady() {
        return ready;
    }
}
