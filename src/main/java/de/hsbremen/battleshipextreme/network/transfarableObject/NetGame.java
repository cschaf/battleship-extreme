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
 * Die Spiellogik innerhalb des Netzwerkspiels
 */
public class NetGame extends Game {
    private String id; // Spiel ID
    private String name; // Name des Spiels
    private HashMap<Integer, ClientHandler> playersMap; // Spieler und ihre Reihenfolge(Setzten, Spielzug) im Spiel
    private ArrayList<Integer> clientIds; // verf�gbare Reihenfolge-IDs
    private ClientGameIndexQueue<Integer> clientTurnOrder; // Queue, die verwendet wird um den nächsten Spieler zu finden, der am Zug ist
    private int maxPlayers; // maximale Anzahl der möglichen Spieler im Spiel
    private String password; // Password für das Spiel
    private boolean isPrivate;
    private ArrayList<Turn> turns; // alle Spielzüge, die gemacht wurden
    private boolean ready;

    public NetGame(String name, String password, Settings settings) {
        super.initialize(settings);
        this.id = UUID.randomUUID().toString(); //  erzeuge einzigartige ID
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

    /**
     * Fügt einen Spielzug hinzu
     */
    public void addTurn(Turn turn) {
        this.turns.add(turn);
    }

    /**
     * Gibt eine Liste aller gejointen Spieler zurück
     */
    public ArrayList<ClientHandler> getJoinedPlayers() {
        ArrayList<ClientHandler> result = new ArrayList<ClientHandler>();
        for (int clientIndex : playersMap.keySet()) {
            if (playersMap.get(clientIndex) != null) {
                result.add(playersMap.get(clientIndex));
            }
        }
        return result;
    }

    /**
     * Fügt einen Spieler dem Spiel hinzu
     */
    public void addPlayer(ClientHandler player) {
        if (!this.isGameFull()) {
            this.playersMap.put(clientIds.get(0), player);
            this.clientIds.remove(clientIds.get(0));
        }
    }

    /**
     * Gibt an on das Spiel voll ist
     */
    private boolean isGameFull() {
        int number = 0;
        for (int clientIndex : playersMap.keySet()) {
            if (playersMap.get(clientIndex) != null) {
                number++;
            }
        }
        return number >= maxPlayers;
    }

    /**
     * Entfernt einen Spieler aus dem Spiel
     */
    public void removePlayer(ClientHandler player) {
        int index = getIndexByClient(player);
        if (index > -1) {
            playersMap.put(index, null);
            clientIds.add(index);
            getPlayers()[index].resetBoard();
            getPlayers()[index].setName("Player " + (index + 1));
            getPlayers()[index].resetShips();
        }
    }

    /**
     * Gibt den Index des Clients zurück
     */
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

    /**
     * Gibt das Passowrd des Spiels zurück
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setzt das Password des Spiels
     */
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

    /**
     * Gibt die Queue der Client Spielzureihenfolge zurück
     */
    public ClientGameIndexQueue<Integer> getClientTurnOrder() {
        return clientTurnOrder;
    }

    /**
     * Gibt die Spieler und ihrem Index im Spiel zurück
     */
    public HashMap<Integer, ClientHandler> getPlayersMap() {
        return playersMap;
    }

    /**
     * Prüft ob alle Spieler ihre Schiffe gesetzt haben
     */
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

    /**
     * Synchronisiert die Soielernamen der gejointen und den spielenden Spielern
     */
    public void updatePlayerNames() {
        for (Map.Entry<Integer, ClientHandler> entry : getPlayersMap().entrySet()) {
            ClientHandler value = entry.getValue();
            Integer index = entry.getKey();
            getPlayers()[index].setName(value.getUsername());
        }
    }
}
