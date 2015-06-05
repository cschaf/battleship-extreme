package de.hsbremen.battleshipextreme.network;

import java.io.Serializable;
import java.util.*;

/**
 * Created by cschaf on 05.06.2015.
 */
public class ClientGameIndexQueue<T> implements Iterator<T>, Serializable {
    private LinkedList<T> elements;

    public ClientGameIndexQueue() {
        elements = new LinkedList<T>();
    }

    /**
     * Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions, returning true upon success and throwing an IllegalStateException if no space is currently available.
     */
    public void add(T element) {
        elements.add(element);
    }

    /**
     * Retrieves the next element of the queue
     */

    public T next() {
        return elements.removeFirst();
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     */
    public T peek() {
        return elements.getFirst();
    }

    /**
     * Removes all elemets from queue.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Retrieves the size of the queue
     */
    public int size() {
        return elements.size();
    }

    /**
     * Retrieves true if queue is empty.
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Retrieves an iterator for the queue
     */
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    /**
     * Retrieves true if queue has a next element
     */
    public boolean hasNext() {
        return elements.iterator().hasNext();
    }

    /**
     * Retrieves and removes the head of this queue. This method differs from poll only in that it throws an exception if this queue is empty.
     */
    public void remove() {
        elements.remove();
    }
}

