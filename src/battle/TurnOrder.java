/*
 * MIT License
 *
 * Copyright (c) 2016 Andrew Michael Teller(https://github.com/AndrewMiTe)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package battle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class for organizing units based on when they have something to do. Takes in
 * both the unit who has a turn and the time their turn is due. Units can be in
 * the turn order multiple times. Multiple units can have their turn due at the
 * same time, yet only one unit will be returned per request.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class TurnOrder {

    
  /**
   * The date and time at the start of the battle. 
   */
  private final LocalDateTime startTime;
  /**
   * Keeps track of the current date and time. The clock starts at the local
   * date and time and increments from there.
   */
  private LocalDateTime currentTime;
  /**
   * List of Subscriber objects who request to be updated when the TurnOrder
   * advances to the next Turn.
   */
  private final ArrayList<Subscriber> subscribers;
  /**
   * An array of unit-time parings contained within an internal private class.
   */
  private final ArrayList<TurnItem> turnList;
  
  /**
   * Basic constructor.
   */
  protected TurnOrder() {
    this.startTime = LocalDateTime.now();
    this.currentTime = this.startTime;
    this.subscribers = new ArrayList<>();
    this.turnList = new ArrayList<>();
  }

  /**
   * Adds a TurnItem at a time value equal to the next TurnItem in the
   * TurnOrder.
   * @param  unit Unit object that that is the subject of the TurnItem.
   * @param  stunnable true if the TurnItem is affected by stuns made to the
   *         Unit.
   * @return true if the addition was successful.
   */
  protected TurnItem addAfterNext(Unit unit, boolean stunnable) {
    turnList.sort(null);
    LocalDateTime time = turnList.get(0).getTime();
    return addTurnItem(unit, time, stunnable);
  }
  
  /**
   * Takes TurnItem object and sorts it into the TurnOrder.
   * @param newItem new TurnItem to be added. Value is allowed to be null.
   */
  protected void addTurnItem(TurnItem newItem) {
      turnList.add(newItem);
  }
  
  /**
   * Takes a Unit and time paring and sorts it into the TurnOrder.
   * @param  unit Unit object to be called when the turn is due.
   * @param  time time from the start of the TurnOrder when the turn is due.
   *         Measured in milliseconds.
   * @param  stunnable true if the turn is delayed when the Unit called by the
   *         turn is stunned.
   * @return true when the addition of the turn information succeeds.
   */
  protected TurnItem addTurnItem(Unit unit, LocalDateTime time, boolean stunnable) {
      TurnItem newItem = new TurnItem(unit, time, stunnable);
      turnList.add(newItem);
      return newItem;
  }

  /**
   * Takes a Unit and time paring and sorts it into the TurnOrder.
   * @param  unit Unit object to be called when the turn is due.
   * @param  time time from the start of the TurnOrder when the turn is due.
   *         Measured in milliseconds.
   * @param  stunnable true if the turn is delayed when the Unit called by the
   *         turn is stunned.
   * @return true when the addition of the turn information succeeds.
   */
  protected TurnItem addTurnItem(Unit unit, int time, boolean stunnable) {
      LocalDateTime dateTime = currentTime.plus(Duration.ofMillis(time));
      TurnItem newItem = new TurnItem(unit, dateTime, stunnable);
      turnList.add(newItem);
      return newItem;
  }

  /**
   * Getter for the time value of the clock. Measured in milliseconds.
   * @return time value of the clock.
   */
  protected Integer getClock() {
    return (int)Duration.between(startTime, currentTime).toMillis();
  }
  
  /**
   * Returns the next Unit to have a turn due and increments the clock to the
   * time of that turn.
   * @return the Unit who's turn is now due.
   */
  protected Unit next() {
    turnList.sort(null);
    while (turnList.size() > 0) {
      TurnItem nextTurn = turnList.remove(0);
      if (currentTime.isBefore(nextTurn.getTime())) {
        currentTime = nextTurn.getTime();
        for (Subscriber sub :  subscribers) {
          sub.update();
        }
      }
      if (nextTurn.getUnit() != null) {
        nextTurn.getUnit().removeTurnItem(nextTurn);
        return nextTurn.getUnit();
      }
    }
    return null;
  }

  /**
   * Removes all TurnItem objects from the TurnOrder matching the given Unit.
   * @param  oldUnit the Unit object used to identify all turns to be removed.
   * @return true if a match to the given Unit was found and removed. 
   */
  protected boolean removeUnit(Unit oldUnit) {
    boolean returnValue = false;
    Iterator<TurnItem> iterateTurns = turnList.iterator();
    while (iterateTurns.hasNext()) {
      TurnItem nextTurn = iterateTurns.next();
      if (nextTurn.getUnit() == oldUnit) {
        iterateTurns.remove();
        returnValue = true;
      }
    }
    return returnValue;
  }
  
  /**
   * Adds a Subscriber object that is updated whenever the TurnOrder progresses
   * to the next TurnItem.
   * @param newSubscriber Subscriber requesting to receive requests to update.
   * @return true if the Subscriber was successfully added.
   */
  protected boolean subscribe(Subscriber newSubscriber) {
    if ((newSubscriber != null) && (!subscribers.contains(newSubscriber))) {
      return subscribers.add(newSubscriber);
    }
    return false;
  }
  
  /**
   * Removes a Subscriber object that is updated whenever the TurnOrder
   * progresses to the next TurnItem.
   * @param  oldSubscriber Subscriber requesting to be no longer receive requests
   *         to update.
   * @return true if the Subscriber was successfully removed.
   */
  protected boolean unsubscribe(Subscriber oldSubscriber) {
    return subscribers.remove(oldSubscriber);
  }
  
}
