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

/**
 * Tracks time within a battle and advances items that produce events as time
 * continues forward.
 * 
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class TurnOrder {

  /**
   * Limits the number of passes performed on a single instant of time where the
   * interactions of skills ready to execute and statuses applied might interact
   * indefinitely.
   */
  public final int PASS_LIMIT = 10;

  /**
   * The date and time at the start of the battle.
   */
  private final LocalDateTime startTime;

  /**
   * Keeps track of the date and time of the most current turn.
   */
  private LocalDateTime currentTime;

  /**
   * An list of objects that can be sorted as items in the turn order.
   */
  private final ArrayList<TurnItem> turnList;

  /**
   * Initializes the turn order using the current date and time as the start of
   * the battle.
   */
  public TurnOrder() {
    this(LocalDateTime.now());
  }

  /**
   * Initializes the turn order using a given parameter as the starting date and
   * time of the battle. Attempting to initiate the start time value as {@code
   * null} will throw an {@link IllegalArgumentException}.
   * 
   * @param startTime
   *          starting date and time.
   */
  public TurnOrder(LocalDateTime startTime) {
    if (startTime == null) {
      throw new IllegalArgumentException("start time cannot be null");
    }
    this.startTime = startTime;
    this.currentTime = this.startTime;
    this.turnList = new ArrayList<>();
  }

  /**
   * Adds an item to the turn order. Attempting to set the item as {@code null}
   * will throw an {@link IllegalArgumentException}.
   * 
   * @param item
   *          the turn item to add.
   */
  public void addTurnItem(TurnItem item) {
    if (item == null) {
      throw new IllegalArgumentException("turn items cannot be null");
    }
    turnList.add(item);
  }

  /**
   * Removes an item from the turn order.
   * 
   * @param item
   *          the turn item to remove.
   * @return {@code true} if the item was removed.
   */
  public boolean removeTurnItem(TurnItem item) {
    return turnList.remove(item);
  }

  /**
   * Removes all TurnItem objects from the turn order matching that are owned by
   * a given Fighter object.
   * 
   * @param actor
   *          the actor of the items to be removed.
   * @return true if a match to the given fighter was found and removed.
   */
  public boolean removeActor(Actor actor) {
    return turnList.removeIf(t -> t.getActor() == actor);
  }

  /**
   * Advances to the next point in time where at least one successful event
   * occurs. After time is incrementally advanced to the point where one or more
   * turn item report an event, such as the expiration of a status or the
   * execution of a skill, all turns are given an additional pass to report if
   * new events result from the changes to the state of the battle. These passes
   * will continue until either all interactions have played themselves out or
   * until the passes exceed {@link #PASS_LIMIT PASS_LIMIT}. If the pass limit
   * is exceeded or if there are no turn items beyond the current time, the
   * method returns {@code false}, which indicates that the battle should be
   * concluded, likely with no one being the victor.
   * 
   * @return {@code true} if time has advanced to a successful event. A {@code
   *         false} value indicates that the battle should be concluded.
   */
  public boolean advanceToNext() {
    boolean successfulEvent = false;
    while (successfulEvent == false) {
      sortTurnItems();
      LocalDateTime nextTime = currentTime;
      for (int i = turnList.size(); i > 0;) {
        nextTime = turnList.get(--i).getTurnTime(currentTime);
        if (nextTime.isAfter(currentTime))
          break;
      }
      if (!nextTime.isAfter(currentTime))
        return false;
      Duration timeChange = Duration.between(currentTime, nextTime);
      boolean successfulPass = false;
      int passCount = 0;
      do {
        for (TurnItem t : turnList) {
          successfulPass = successfulPass || t.advanceTime(timeChange);
        }
        successfulEvent = successfulEvent || successfulPass;
        timeChange = Duration.ZERO;
        if (++passCount > PASS_LIMIT)
          return false;
      } while (successfulPass = true);
    }
    return true;
  }

  /**
   * Advances the turn order until it has determined that successful events can
   * no longer occur.
   */
  public void advanceAll() {
    while (advanceToNext() != false)
      ;
  }

  /**
   * Getter for the current time of the battle.
   * 
   * @return date and time value.
   */
  public LocalDateTime getCurrentTime() {
    return currentTime;
  }

  /**
   * Getter for the start time of the battle.
   * 
   * @return date and time value.
   */
  public LocalDateTime getStartTime() {
    return startTime;
  }

  /**
   * Sorts the turn order in descending order based on the time that events are
   * due.
   */
  private void sortTurnItems() {
    turnList.sort((t1, t2) -> t2.getTurnTime(currentTime).compareTo(t1.getTurnTime(currentTime)));
  }

}
