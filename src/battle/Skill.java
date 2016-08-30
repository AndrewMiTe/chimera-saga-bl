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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains the instructions for a interaction between its owner, a Unit object,
 * and its Target(s), other Unit objects contained within the same Battlefield
 * object. When executed, Unit objects matching both the Target description and
 * containing the required Status objects are search for. If a match is found,
 * then all Skill objects contained within this Skill object are executed
 * recursively. If some but not all sub-Skill objects report a successful
 * execution, then the skill returns as successful and does nothing else. If all
 * sub-Skill objects execute successfully, then all matching Unit objects, those
 * that match the Target description and containing the Status objects required
 * by this Skill, will have all of this Skill object's actions applied to them,
 * if there are any. Actions are a separate list of Status objects contained
 * within this Skill.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class Skill {
  
  /**
   * Name of the Skill.
   */
  private String name;
  /**
   * Simple text-based description of the Skill.
   */
  private String description;
  /**
   * Defines which Row(s) the Skill can be executed in.
   */
  private Row rowUse;
  /**
   * The default amount of time until the Skill can be used after execution.
   * Measured in milliseconds.
   */
  private int maxCooldown;
  /**
   * The current amount of time until the skill can be used. Measured in
   * milliseconds.
   */
  private int cooldown;
  /**
   * Enumerated Target value for determining valid Unit objects this Skill
   * applies its action to.
   */
  private Target target;
  /**
   * List of Status objects to apply to the Target of the Skill.
   */
  private final ArrayList<Status> actions;
  /**
   * List of Status objects that the Target of the Skill must have in order to
   * continue Skill execution.
   */
  private final ArrayList<String> requires;
  /**
   * List of Skill objects that must be successfully executed in order for this
   * Skill to apply its actions to its Target(s).
   */
  private final ArrayList<Skill> subSkills;

  /**
   * Basic constructor.
   * @param  name name of the Skill.
   * @param  description simple text-based description of the Skill.
   * @param  rowUse Defines which Row(s) the Skill can be executed in.
   * @param  target Enumerated Target value for determining valid Unit objects
   *         this Skill applies its action to.
   * @param  maxCooldown The default amount of time until the Skill can be used
   *         after execution. Measured in milliseconds.
   */
  public Skill(String name, String description, Row rowUse, Target target, int maxCooldown) {
    this.name = name;
    this.description = description;
    this.rowUse = rowUse;
    this.target = target;
    this.maxCooldown = maxCooldown;
    this.cooldown = maxCooldown;
    this.requires = new ArrayList<>();
    this.actions = new ArrayList<>();
    this.subSkills = new ArrayList<>();
  }

  /**
   * Adds a new action to perform on the Target.
   * @param  newStatus Status object to apply when the whole Skill is executed
   *         successfully.
   */
  public void addAction(Status newStatus) {
    actions.add(newStatus);
  }
  
  /**
   * Adds a new requirement for the Target to match.
   * @param  statusName Status object to check the Target against for a match.
   */
  public void addRequirement(String statusName) {
    requires.add(statusName);
  }

  /**
   * Adds a new sub-Skill that must execute successfully before this Skill can
   * apply its actions.
   * @param  newSubSkill Skill object required to be successfully performed
   *         before this Skill can apply its actions.
   */
  public void addSubSkill(Skill newSubSkill) {
    subSkills.add(newSubSkill);
  }

  /**
   * Getter for the actions performed on the Target.
   * @return Iterator of all the Status objects to apply to Target Unit objects
   *         when the Skill is successfully executed.
   */
  public Iterator<Status> getActions() {
    return actions.iterator();
  }

  /**
   * Getter for a simple text-based description of the Skill.
   * @return simple text-based description of the Skill.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Getter for the current time value needed to pass until the Skill can be
   * executed. Measured in milliseconds.
   * @return current time value needed to pass until the Skill can be executed.
   */
  public int getCooldown() {
    return cooldown;
  }

  /**
   * Getter for the default time value needed to pass until the Skill can be
   * executed. Measured in milliseconds.
   * @return default time value needed to pass until the Skill can be executed.
   */
  public int getMaxCooldown() {
    return maxCooldown;
  }

  /**
   * Getter for the name of the Skill.
   * @return name of the Skill.
   */
  public String getName() {
    return name;
  }
  
  /**
   * Getter for an Status objects that the Target Unit objects are required to
   * match for the skill to be successfully executed.
   * @return Iterator of Status objects that the Target Unit objects are
   *         required to match.
   */
  public Iterator<String> getRequires() {
    return requires.iterator();
  }

  /**
   * Getter for the Row the Skill is usable in.
   * @return enumerated Row value that the Skill is usable in.
   */
  public Row getRowUse() {
    return rowUse;
  }

  /**
   * Getter for the Skill objects required to execute successfully for this
   * Skill to apply its actions.
   * @return Iterator of the Skill objects required to execute successfully.
   */
  public Iterator<Skill> getSubSkills() {
    return subSkills.iterator();
  }

  /**
   * Getter for the Target of the Skill object's requirements and actions.
   * @return enumerated Target value of the Skill object's requirements and
   *         actions.
   */
  public Target getTarget() {
    return target;
  }

  /**
   * Setter for a simple text-based description of the Skill.
   * @param  description simple text-based description of the Skill.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Setter for the time value that must pass until the Skill is usable.
   * Measured in milliseconds.
   * @param  cooldown time value that must pass until the Skill is usable.
   */
  public void setCooldown(int cooldown) {
    this.cooldown = cooldown;
  }

  /**
   * Setter for the default time value that must pass until the Skill is usable.
   * The time value is measured in milliseconds.
   * @param  cooldown default time value that must pass until the Skill is
   *         usable.
   */
  public void setMaxCooldown(int cooldown) {
    this.maxCooldown = cooldown;
  }

  /**
   * Setter for the name of the Skill.
   * @param  name name of the Skill.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Setter for what Row the Skill is usable in.
   * @param  rowUse enumerated Row value the Skill is usable in.
   */
  public void setRowUse(Row rowUse) {
    this.rowUse = rowUse;
  }

  /**
   * Setter for the Target of the Skill object's requirements and actions.
   * @param  target enumerated Target value of the Skill object's requirements
   *         and actions.
   */
  public void setTarget(Target target) {
    this.target = target;
  }

  @Override public String toString() {
    return name;
  }
  
}
