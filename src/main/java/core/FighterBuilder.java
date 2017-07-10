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

package core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Constructs {@link Fighter} objects using a builder pattern. All setter
 * methods return the instance of the object it called upon. The build method
 * returns a new Fighter object using the information obtained through defaults
 * and set methods.
 *
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class FighterBuilder {

  /**
   * Stores the name value for building a Fighter object.
   * 
   * @see #setName
   */
  private String name;

  /**
   * Stores the team value for building a Fighter object.
   * 
   * @see #setSquad
   */
  private Team team;

  /**
   * Stores the skillList value for building a Fighter object.
   * 
   * @see #addSkill
   */
  private List<Skill> skillList;

  /**
   * Stores the closeRange value for building a Fighter object.
   * 
   * @see #setCloseRange
   */
  private int closeRange;

  /**
   * Stores the isAllyCase value for building a Fighter object.
   * 
   * @see #setIsAllyCase
   */
  private BiPredicate<Fighter, Fighter> isAllyCase;

  /**
   * Stores the isEnemyCase value for building a Fighter object.
   * 
   * @see #setIsEnemyCase
   */
  private BiPredicate<Fighter, Fighter> isEnemyCase;

  /**
   * Stores the listeners value for building a Fighter object.
   * 
   * @see #addListener
   */
  private List<FighterHandler> listeners;

  /**
   * Instantiates the object with the name of the {@link Fighter} to be built.
   * Sets all other properties of the fighter to be built to their default
   * values.
   * 
   * @param name
   *          see {@see #setName}. Cannot be null.
   */
  public FighterBuilder(String name) {
    if (name == null) {
      throw new NullPointerException("name: null");
    }
    this.name = name;
    this.team = new Team() {};
    this.skillList = new ArrayList<>();
    this.closeRange = 0;
    this.isAllyCase = (a, b) -> a.getTeam().equals(b.getTeam());
    this.isEnemyCase = (a, b) -> !a.getTeam().equals(b.getTeam());
    this.listeners = new ArrayList<>();
  }

  /**
   * Instantiates the object by setting all properties so that any status built
   * directly after initialization would be a copy of the Status object given.
   * 
   * @param status
   *          the status used to set all properties.
   */
  public FighterBuilder(Fighter fighter) {
    Fighter copyOf = new Fighter(fighter);
    this.name = copyOf.getName();
    this.team = copyOf.getTeam();
    this.skillList = copyOf.getSkills();
    this.closeRange = copyOf.getCloseRange();
    this.isAllyCase = copyOf.getIsAllyCase();
    this.isEnemyCase = copyOf.getIsEnemyCase();
    this.listeners = copyOf.getListeners();
  }

  /**
   * Creates a new {@link Fighter} object built with the values set by this
   * builder object. Default values for all parameters, if not explicitly set,
   * are used with exception to the name parameter, which is set when the
   * FighterBuilder is initiated.
   * 
   * @return new Fighter object built with the values set in this builder
   *         object.
   */
  public Fighter build() {
    return new Fighter(name, team, skillList, closeRange, isAllyCase, isEnemyCase, listeners);
  }

  /**
   * Sets the name that identifies the fighter to be built from unrelated
   * {@link Fighter} objects. The name property has no default value.
   * 
   * @param name
   *          name parameter for producing a Fighter object. Cannot be {@code
   *        null}.
   * @return this object.
   */
  public FighterBuilder setName(String name) {
    if (name == null)
      throw new NullPointerException("name: null");
    this.name = name;
    return this;
  }

  /**
   * Sets the team parameter for which the fighter belongs to. By default,
   * fighters with the same team object recognize each other as allies and
   * fighters with all other teams as enemies. Setting the parameter to
   * {@code null} results in a team value of a  unique Team object such that 
   * {@code fightersTeam.equals(otherTeam)} should always returns {@code false}
   * unless if {@code otherTeam} is received from the same fighter.   
   * 
   * @param team
   *          Team that the unit belongs to.
   * @return this object.
   */
  public FighterBuilder setTeam(Team team) {
    if (team == null) team = new Team() {};
    this.team = team;
    return this;
  }

  /**
   * Adds a skill to the fighter to be built. Skills can be performed when the
   * fighter engages in a battle. In battle the skills apply statuses to either
   * allies or enemies, or potentially both, in order to further the battle to
   * its conclusion. The default list of skills is an empty list.
   * 
   * @param skill
   *          skill to be added. Cannot be {@code null}.
   * @return this object.
   * @see FighterBuilder#addSkill
   */
  public FighterBuilder addSkill(Skill skill) {
    if (skill == null)
      throw new NullPointerException("skill: null");
    skillList.add(skill);
    return this;
  }

  /**
   * Removes a skill from the list of skills.
   * 
   * @param skill
   *          the skill to be removed.
   * @return this object.
   * @see FighterBuilber#addSkill
   */
  public FighterBuilder removeSkill(Skill skill) {
    skillList.remove(skill);
    return this;
  }

  /**
   * Sets the distance that this fighter must be within in order to apply its
   * skill to targets that are required by the skill to be close. The default
   * for range is 0.
   * 
   * @param closeRange
   *          The distance where the fighter is considered to be close to its
   *          target. Cannot be {@code < 0}.
   * @return this object.
   */
  public FighterBuilder setCloseRange(int closeRange) {
    if (closeRange < 0)
      throw new NullPointerException("close range: < 0");
    this.closeRange = closeRange;
    return this;
  }

  /**
   * Sets the case for determining the fighter's allies. The required case is a
   * BiPredicate object that compares two fighters, the Fighter object to be
   * built and another fighter. If the test method for the BiPredicate object
   * returns true, the two fighter's are considered to be allies from the
   * perspective of the fighter to be built.  The default is a case that returns
   * {@code true} if the teams of the two fighters are not equal such that
   * {@code !team1.equals(team2)}.
   * 
   * @param isAllyCase
   * @return this object.
   */
  public FighterBuilder setIsAllyCase(BiPredicate<Fighter, Fighter> isAllyCase) {
    if (isAllyCase == null)
      throw new NullPointerException("Ally Case: null");
    this.isAllyCase = isAllyCase;
    return this;
  }

  /**
   * Sets the case for determining the fighter's enemies. The required case is a
   * BiPredicate object that compares two fighters, the Fighter object to be
   * built and another fighter. If the test method for the BiPredicate object
   * returns true, the two fighter's are considered to be enemies from the
   * perspective of the fighter to be built. The default is a case that returns
   * {@code true} if the teams of the two fighters are equal such that
   * {@code team1.equals(team2)}.
   * 
   * @param isEnemyCase
   * @return this object
   */
  public FighterBuilder setIsEnemyCase(BiPredicate<Fighter, Fighter> isEnemyCase) {
    if (isEnemyCase == null)
      throw new NullPointerException("Enemy Case: null");
    this.isEnemyCase = isEnemyCase;
    return this;
  }

  /**
   * Adds a new {@link FighterHandler} object to receive method calls during
   * appropriate events in the fighter's lifetime.
   * 
   * @param listener
   *          new listener for producing a Status object. Cannot be
   *          {@code null}.
   * @return this object.
   */
  public FighterBuilder addListener(FighterHandler listener) {
    if (listener == null)
      throw new NullPointerException("listener: null");
    listeners.add(listener);
    return this;
  }

  /**
   * Removes a listener from the list of listeners for producing a Fighter
   * object.
   * 
   * @param listener
   *          the object to be removed.
   * @return true if the object was successfully removed.
   * @see #addListener
   */
  public boolean removeListener(FighterHandler listener) {
    return this.listeners.remove(listener);
  }

}
