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

import java.time.LocalDateTime;
import java.util.ArrayList;

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
   * @param startTime starting date and time.
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
   * @param item the turn item to add.
   */
  public void addTurnItem(TurnItem item) {
    if (item == null) {
      throw new IllegalArgumentException("turn items cannot be null");
    }
    turnList.add(item);
  }

  /**
   * Removes an item from the turn order.
   * @param item the turn item to remove.
   * @return {@code true} if the item was removed.
   */
  public boolean removeTurnItem(TurnItem item) {
    return false;
  }
  
  /**
   * Removes all TurnItem objects from the turn order matching that are owned
   * by a given Fighter object.
   * @param fighter the owner of the items to be removed.
   * @return true if a match to the given fighter was found and removed. 
   */
  public boolean removeFighter(Fighter fighter) {
    return false;
  }
 
  /**
   * Advances the current time to the time of the next turn, removes that item,
   * and returns it to the caller.
   * @return the next turn item.
   */
  public TurnItem advanceToNext() {
    return null;
  }
  
  /**
   * Returns the next turn item in the turn order.
   * @return the next turn item.
   */
  public boolean hasNext() {
    return false;
  }
  
  public TurnItem getNext() {
    return null;
  }
  
  public Skill advanceToNextSkill() {
    return null;
  }
  
  public boolean hasNextSkill() {
    return false;
  }
  
  public Skill getNextSkill() {
    return null;
  }
  
  public Status advanceToNextStatus() {
    return null;
  }
  
  public boolean hasNextStatus() {
    return false;
  }
  
  public Status getNextStatus() {
    return null;
  }
  
  /**
   * Getter for the current time of the battle.
   * @return date and time value.
   */
  public LocalDateTime getCurrentTime() {
    return currentTime;
  }

  /**
   * Getter for the start time of the battle.
   * @return date and time value.
   */
  public LocalDateTime getStartTime() {
    return startTime;
  }
  
  private void decendingTurnItemSort() {
  }

}
