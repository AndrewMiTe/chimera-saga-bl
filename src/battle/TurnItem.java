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

/**
 * A point in time within a TurnOrder that a Unit object can be called in order
 * to perform an action. This class implements the Comparable interface and
 * sorts from earliest to latest in time.
 * @author Andrew M. Teller (andrew.m.teller@gmail.com)
 */
public class TurnItem implements Comparable<TurnItem> {

  /**
   * Reference to the Unit at a particular time in the TurnOrder.
   */
  private final Unit unit;
  /**
   * The particular time in the TurnOrder that the Unit is in.
   */
  private Integer timeOfTurn;
  /**
   * True if the TurnItem is affected by stuns made to the assigned Unit.
   */
  private final boolean stunnable;

  /**
   * Basic constructor.
   * @param unit Unit that is called to perform an action when the TurnItem is
   *        due.
   * @param time time of the turn.
   * @param stunnable true if the TurnItem is affected when the owning Unit has
   *        a Status that stuns.
   */
  protected TurnItem(Unit unit, int time, boolean stunnable) {
    this.unit = unit;
    timeOfTurn = time;
    this.stunnable = stunnable;
  }

  @Override
  public int compareTo(TurnItem other) {
    return timeOfTurn.compareTo(other.getTime());
  }

  /**
   * Getter for when the time occurs.
   * @return time of the turn.
   */
  protected int getTime() {
    return timeOfTurn;
  }

  /**
   * Getter for the unit in a particular time in the turn order.
   * @return Unit that is called to perform an action when the TurnItem is due.
   */
  protected Unit getUnit() {
    return unit;
  }

  /**
   * Returns true if the TurnItem is affected by stuns made to the assigned
   * Unit.
   * @return true if affected by stuns made to the assigned Unit.
   */
  protected boolean isStunnable() {
    return stunnable;
  }

  /**
   * Setter for the time when the turn occurs.
   * @param time time of the turn.
   */
  protected void setTime(int time) {
    this.timeOfTurn = time;
  }
  
}
