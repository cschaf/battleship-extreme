package de.hsbremen.battleshipextreme.network;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by cschaf on 05.06.2015.
 * Queue, welche für das Verwalten der nächsten Spielzügen zuständig ist
 */
public class ClientGameIndexQueue<T> implements Iterator<T>, Serializable {
    private LinkedList<T> elements;

    public ClientGameIndexQueue() {
        elements = new LinkedList<T>();
    }

    /**
     * Fügt das angegebene Element in die Warteschlange
     */
    public void add(T element) {
        elements.add(element);
    }

    /**
     * Gibt des nächste Element der Warteschlange zurück aber entfernt es nicht
     */
    public T peek() {
        return elements.getFirst();
    }

    /**
     * Entfernt alle Elemente aus der Warteschlange
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Gibt die Anzahl der Elemente in der Warteschlange zurück
     */
    public int size() {
        return elements.size();
    }

    /**
     * Gibt true zurück wenn die Warteschlange leer ist
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Gibt einen Iterator für die Warteschlange zurück
     */
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    /**
     * Gibt true zurück wenn es ein  nächstes Element gibt
     */
    public boolean hasNext() {
        return elements.iterator().hasNext();
    }

    /**
     * Entfernt das nächste Element aus der Warteschlange
     */

    public T next() {
        return elements.removeFirst();
    }

    /**
     * Entfernt das erste Element der Warteschlange
     */
    public void remove() {
        elements.remove();
    }
}

