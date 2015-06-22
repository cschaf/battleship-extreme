package de.hsbremen.battleshipextreme.network.transfarableObject;

import de.hsbremen.battleshipextreme.model.Game;
import de.hsbremen.battleshipextreme.model.Settings;
import de.hsbremen.battleshipextreme.model.player.Player;
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
public class NetGame extends Game {
    private String id;
    private String name;
    private HashMap<Integer, ClientHandler> playersMap;
    private ArrayList<Integer> clientIds;
    private ClientGameIndexQueue<Integer> clientTurnOrder;
    private int maxPlayers;
    private String password;
    private boolean isPrivate;
    private ArrayList<Turn> turns;
    private boolean ready;

    public NetGame(String name, String password, Settings settings) {
        super.initialize(settings);
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.playersMap = new HashMap<Integer, ClientHandler>();
        this.maxPlayers = settings.getPlayers();
        this.password = password;
        this.isPrivate = !password.equals("");
        this.turns = new ArrayList<Turn>();
        this.clientIds = new ArrayList<Integer>();
        this.clientTurnOrder = new ClientGameIndexQueue<Integer>();
        this.ready = false;

        for (int i = 0; i < maxPlayers; i++) {
            clientTurnOrder.add(i);
            clientIds.add(i);
            playersMap.put(i, null);
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
        for (int clientIndex : playersMap.keySet()) {
            if (playersMap.get(clientIndex) != null) {
                result.add(playersMap.get(clientIndex));
            }
        }
        return result;
    }

    public void addPlayer(ClientHandler player) {
        if (!this.isGameFull()) {
            this.playersMap.put(clientIds.get(0), player);
            this.clientIds.remove(clientIds.get(0));
        }
    }

    private boolean isGameFull() {
        int number = 0;
        for (int clientIndex : playersMap.keySet()) {
            if (playersMap.get(clientIndex) != null) {
                number++;
            }
        }
        return number >= maxPlayers;
    }

    public void removePlayer(ClientHandler player) {
        int index = getIndexByClient(player);
        if (index > -1) {
            playersMap.put(index, null);
            clientIds.add(index);
            getPlayers()[index].resetBoard();
            getPlayers()[index].setName("Player " + (index + 1));

        }

    }

    public int getIndexByClient(ClientHandler handler) {
        int index = -1;
        for (Map.Entry<Integer, ClientHandler> entry : playersMap.entrySet()) {
            ClientHandler value = entry.getValue();
            if (value != null) {
                if (value == handler) {
                    index = entry.getKey();
                }
            }
        }
        return index;
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

    public HashMap<Integer, ClientHandler> getPlayersMap() {
        return playersMap;
    }


    public boolean haveAllPlayersSetTheirShips() {
        for (Player player : this.getPlayers()) {
            if (!player.hasPlacedAllShips()) {
                return false;
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

    public void updatePlayerNames() {
        for (Map.Entry<Integer, ClientHandler> entry : getPlayersMap().entrySet()) {
            ClientHandler value = entry.getValue();
            Integer index = entry.getKey();
            getPlayers()[index].setName(value.getUsername());

        }
    }
}
