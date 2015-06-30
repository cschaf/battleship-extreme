package de.hsbremen.battleshipextreme.network.eventhandling;

import java.util.EventObject;

/**
 * Created by cschaf on 25.04.2015.
 * Dient als Container für Objekte, die über Events verteilt werden
 */
public class EventArgs<T> extends EventObject{

  private T item; // Generisches Objekt was weitergegeben werden soll

  public EventArgs(Object source, T item){
    super(source);

    this.item = item;
  }

  /**
   * Gib das generische Objekt zurück
   * @return
   */
  public T getItem(){
    return item;
  }
}
