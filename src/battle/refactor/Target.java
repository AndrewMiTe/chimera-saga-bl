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
 * Enumerates the finite ways in which a skill can Target Unit objects on a
 * BattleField. Returns a Iterator object of valid targets when given a list of
 * Unit objects mapped upon a BattleField.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public enum Target {
  
  /**
   * The Unit with the targeting skill.
   */
  SELF,

  /**
   * One Unit from the set of Unit objects on the same team that are within a
   * range, excluding the Unit with the targeting Skill. This range is adjusted
   * based on the distance from the closest ally to the Unit with the targeting
   * Skill.
   */
  CLOSE_ALLY,

  /**
   * One Unit from the set of all Unit objects on the same team as the Unit with
   * the targeting Skill, including the Unit with the targeting Skill.
   */
  ANY_ALLY,
  
  /**
   * One Unit from the set of all Unit objects on the same team as the Unit with
   * the targeting Skill, excluding the Unit with the targeting Skill.
   */
  ANY_OTHER_ALLY,

  /**
   * The set of all Unit objects on the same team as the Unit with the targeting
   * Skill.
   */
  ALL_ALLIES,
  
  /**
   * The set of all Unit objects on the same team as the Unit with the targeting
   * Skill, with exception to the Unit with the targeting Skill.
   */
  All_OTHER_ALLIES,

  /**
   * One Unit from the set of opposing Unit objects that are within a range.
   * This range is adjusted based on the distance to the closest enemy of the
   * Unit with the targeting Skill.
   */
  CLOSE_ENEMY,

  /**
   * One Unit from the set of Unit objects on the opposing side of the Unit with
   * the targeting Skill.
   */
  ANY_ENEMY,

  /**
   * The set of all Unit objects on the opposing team to the Unit with the
   * targeting Skill.
   */
  ALL_ENEMIES,

  /**
   * One Unit from the set of Unit objects involved in the Battle.
   */
  ANYONE,
  
  /**
   * One Unit from the set of Unit objects involved in the Battle, except the
   * Unit with the targeting Skill.
   */
  ANYONE_ELSE,

  /**
   * The set of Unit objects that includes all members of the Battle.
   */
  EVERYONE,
  
  /**
   * The set of Unit objects that includes all members of the Battle, except the
   * Unit with the targeting Skill.
   */
  EVERYONE_ELSE;
  
}
