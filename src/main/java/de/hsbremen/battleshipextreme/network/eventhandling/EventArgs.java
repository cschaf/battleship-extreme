package de.hsbremen.battleshipextreme.network.eventhandling;
import java.util.EventObject;

/**
 * Created by cschaf on 25.04.2015.
 */
public class EventArgs<T> extends EventObject{
  /**
   * Constructs a prototypical Event.
   *
   * @param source The object on which the Event initially occurred.
   * @throws IllegalArgumentException if source is null.
   */
  private T item;

  public EventArgs(Object source, T item){
    super(source);
    this.item = item;
  }

  public T getItem() {
    return item;
  }
}