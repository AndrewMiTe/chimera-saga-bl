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

package chimera;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.TurnOrder;

import java.util.ArrayList;

/**
 * Location of a battle where teams of fighters compete until the battle's
 * conclusion. Battle objects dictate the relative distance between
 * fighters for determining the usability of skills.
 * 
 * @author Andrew M. Teller (https://github.com/AndrewMiTe)
 */
public class Battle {
  
  /**
   * The list of fighters 
   */
  private Set<Squad> squads;
  
  /**
   * Tracks turn items, the current time, and indicates that the battle has been
   * started when not {@code null}.
   */
  private TurnOrder turnOrder;
  
  /**
   * Initializes an empty battle.
   */
  public Battle() {
    this.squads = new HashSet<>();
  }
  
  /**
   * Initializes a battle occupied by squads. Throws a 
   * {@link NullPointerException} if any of the given values are {@code null} or
   * if the given is an array with a {@code null} element. 
   * 
   * @param squads
   *          squad(s) to be added. Cannot be {@code null} or an array with a 
   *          {@code null} element.
   */
  public Battle(Squad... squads) {
    this();
    for (Squad s : squads) addSquad(s);
  }
  
  /**
   * Adds a squad to the battleground. Throws a {@link NullPointerException}
   * if squad is {@code null}.
   * 
   * @param newSquad 
   *          the squad to be added. Cannot be {@code null}.
   * @return {@code true} if the squad was successfully added.
   */
  public boolean addSquad(Squad newSquad) {
    if (newSquad.joinBattle(this)) {
      return squads.add(newSquad);
    }
    return false;
  }

  /**
   * Returns a list of squads in the battle.
   * 
   * @return list of squads in battle.
   */
  public List<Squad> getSquads() {
    return new ArrayList<>(squads);
  }
  
  /**
   * Starts the battle. Squads will fight until all but one squad is defeated,
   * until only one squad is defeated, or until the battle exceeds the duration
   * of its timeout property. Squads can continue to be added and removed from
   * the battle. Newly added squads will be prep'ed for battle and placed into
   * the turn order. Battles without fighters, battles where no fighter can
   * identify an enemy fighter, or battle that have already been started will
   * fail to start and return {@code false}.  
   * 
   * @return {@code true} if the battle successfully started.
   */
  public boolean start() {
    if (turnOrder != null || squads.isEmpty())
      return false;
    turnOrder = new TurnOrder();
    return true;
  }
  
  /**
   * return {@code true} when the battle has been started and is not finished.
   */
  public boolean isInProgress() {
    return turnOrder != null;
  }

}
