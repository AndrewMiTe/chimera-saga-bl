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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import core.Fighter;
import core.Team;

/**
 * Groups fighters together and assigns fighters in the same squad as being on
 * the same team.
 * 
 * @author Andrew M. Teller (https://github.com/AndrewMiTe)
 */
public class Squad implements Team {

  /**
   * The set of fighters in the squad.
   */
  private Set<Fighter> fighters;

  /**
   * The battle the squad is engaged in. When the squad is not in a battle the
   * value should be {@code null}. While not {@code null}, fighters are not 
   * permitted to join another squad and leave this one.
   */
  Battle battle;

  /**
   * Initializes an empty squad.
   */
  public Squad() {
    this.fighters = new HashSet<>();
    this.battle = null;
  }
  
  /**
   * Initializes a squad with fighters. Throws a {@link NullPointerException} if
   * the given list is a {@code null}.
   * 
   * @param fighters list of fighter to join the new squad. Cannot be a null
   * list. 
   */
  public Squad(Fighter...fighters) {
    this();
    for (Fighter f : fighters) addFighter(f);
  }
  
  /**
   * Removes the given fighter from its current team and adds it to this one if
   * the squad is not in a battle.
   * 
   * @param newFighter
   *          the fighter to be added. Cannot be {@code null}.
   * @return {@code true} if the fighter was successfully assigned to the squad.
   */
  public boolean addFighter(Fighter newFighter) {
    if (newFighter == null)
      throw new NullPointerException("Fighter: null");
    if (battle != null)
      return false;
    Team oldTeam = newFighter.getTeam();
    if (oldTeam instanceof Squad)
      if (!((Squad) newFighter.getTeam()).removeFighter(newFighter))
        return false;
    if (!fighters.add(newFighter))
      return false;
    newFighter.setTeam(this);
    return true;
  }

  /**
   * Returns {@code true} if the fighter can be removed from the squad and added
   * to another. Removes the given fighter from this squad and sets its team to
   * {@code null} if successful. This operation will fail and return
   * {@code false} if the squad is assigned to a battle.
   * 
   * @param oldFighter
   *          the fighter to be removed. Cannot be {@code null}. Must be found
   *          in the squad.
   */
  public boolean removeFighter(Fighter oldFighter) {
    if (oldFighter == null)
      throw new NullPointerException("Fighter: null");
    if (!fighters.contains(oldFighter))
      throw new NoSuchElementException("Fighter: not found in squad");
    if (battle != null)
      return false;
    fighters.remove(oldFighter);
    oldFighter.setTeam(null);
    return true;
  }
  
  /**
   * @return list of fighters in the squad.
   */
  public List<Fighter> getFighters() {
    return new ArrayList<>(fighters);
  }

  /**
   * @return {@code true} if the squad has no fighters.
   */
  public boolean isEmpty() {
    return fighters.isEmpty();
  }
  
  /**
   * Engages this squad in a battle and returns {@code true} if the squad is not
   * already assigned to a battle. Attempting to assign the squad to 
   * {@code null} will throw a {@link NullPointerException}. 
   * 
   * @param battle
   *          the battle to engage the squad in.
   * @return {@code true} if engagement is successful. Cannot be {@code null}.
   */
  protected boolean joinBattle(Battle battle) {
    if (battle == null) 
      throw new NullPointerException("Battle: null");
    if (this.battle != null) 
      return false;
    this.battle = battle;
    return true;
  }
  
  /**
   * Removes itself from its current battle the returns {@code true} if the
   * squad can successfully leave the battle. Always returns {@code false} if
   * the squad is not already assigned to a battle.  
   * 
   * @return {@code true} if the squad successfully left its battle.
   */
  protected boolean leaveBattle() {
    if (battle == null) return false;
    battle = null;
    return true;
  }
  
  /**
   * @return {@code true} if the squad is engaged in a battle.
   */
  public boolean isInBattle() {
    return battle != null;
  }

  /**
   * Returns an {@link Optional} of the battle the squad is in. Returns an empty
   * Optional object if the squad is currently not in a battle.  
   * 
   * @return the battle the squad is in.
   */
  public Optional<Battle> getBattle() {
    return Optional.ofNullable(battle);
  }

}
