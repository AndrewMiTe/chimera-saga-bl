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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Instructions that, when executed by its owner, applies Status objects to
 * valid target(s). When executed, fighters matching both the enumerated Target
 * description and containing the required Status objects are search for. If a
 * match is found, then all sub-skills (other Skill objects) contained within
 * this Skill object are executed recursively. If some but not all sub-skills
 * report a successful execution, then the skill returns as successful and does
 * nothing else. If all sub-Skill objects execute successfully, then all target
 * Fighter objects, those that match the Target description and containing the
 * Status objects required by this skill, will have all of this skill's effects
 * applied to them. The effects of this skill are a separate list of Status
 * objects listed within.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class Skill {
  
  /**
   * Name of the Skill. Attempting to initiate the value as {@code null} will
   * throw an {@link IllegalArgumentException}.
   */
  private String name;
  /**
   * Description of the skill and how it is intended to interact with its
   * target(s). Attempting to initiate the value as {@code null} will throw an
   * {@link IllegalArgumentException}.
   */
  private String description;
  /**
   * The default amount of time until the Skill can be used after execution.
   * Attempting to initiate the value as {@code null} will throw an {@link
   * IllegalArgumentException}.
   */
  private Duration maxCooldown;
  /**
   * The current amount of time until the skill can be used.
   */
  private Duration cooldown;
  /**
   * Enumerated Target value for determining valid Fighter objects this skill
   * applies its effects to. Attempting to initiate the value as {@code null}
   * will throw an {@link IllegalArgumentException}.
   */
  private Target target;
  /**
   * Defines the case when the skill is usable, which allows its cooldown to
   * decrement and for the skill to execute. Attempting to initiate the value as
   * {@code null} will throw an {@link IllegalArgumentException}.
   */
  private final Predicate<Skill> usablity;
  /**
   * List of Status objects to apply to the Target of the Skill.
   */
  private final List<Status> effects;
  /**
   * List of Status objects that the target of the skill must have in order to
   * successfully execute.
   */
  private final List<String> requirements;
  /**
   * List of Skill objects that must be successfully executed in order for this
   * skill to apply its effects to its target(s).
   */
  private final List<Skill> subSkills;
    /**
   * The Fighter object that the skill belongs to. Value should remain null
   * until the skill has been applied using the {@link #onApply(battle.Fighter)
   * onApply} method.
   */
  private Fighter owner;

  /**
   * Initializes the object so that all internal field variables that can be
   * explicitly set are done so through the given parameters. See the {@link 
   * SkillBuilder} class which allows you to create Skill object using a builder
   * pattern.
   * @param name {@see #name}
   * @param description {@see #description}
   * @param usablity {@see #usablity}
   * @param target {@see #target}
   * @param maxCooldown {@see #maxCooldown}
   * @param requirements {@see #requirements}
   * @param effects {@see #effects}
   * @param subSkills {@see #subSkills}
   */
  public Skill(String name, String description, Target target,
      Duration maxCooldown, Predicate<Skill> usablity, List<Status> effects,
      List<String> requirements, List<Skill> subSkills) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    this.name = name;
    if (description == null) {
      throw new IllegalArgumentException("description cannot be null");
    }
    this.description = description;
    if (target == null) {
      throw new IllegalArgumentException("target cannot be null");
    }
    this.target = target;
    this.maxCooldown = maxCooldown;
    this.cooldown = maxCooldown;
    if (usablity == null) {
      throw new IllegalArgumentException("usablity Predicate cannot be null");
    }
    this.usablity = usablity;
    this.effects = new ArrayList<>(effects);
    this.requirements = new ArrayList<>(requirements);
    this.subSkills = new ArrayList<>(subSkills);
    this.owner = null;
  }

  /**
   * Initializes a copy of the given Skill object such that direct changes to
   * the state of either the original or the copy have no affect on the other.
   * Copies are always without an owner, even if the original has one, thus
   * making the value always {@code null}.
   * @param copyOf object which the copy is made from.
   */
  public Skill(Skill copyOf) {
    this.name = copyOf.name;
    this.description = copyOf.description;
    this.usablity = copyOf.usablity;
    this.target = copyOf.target;
    this.maxCooldown = copyOf.maxCooldown;
    this.cooldown = copyOf.cooldown;
    this.requirements = new ArrayList<>(copyOf.requirements);
    this.effects = new ArrayList<>(copyOf.effects);
    this.subSkills = new ArrayList<>(copyOf.subSkills);
    this.owner = null;
  }

  /**
   * Getter for the actions performed on the Target.
   * @return Iterator of all the Status objects to apply to Target Unit objects
   *         when the Skill is successfully executed.
   */
  public Iterator<Status> getActions() {
    return effects.iterator();
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
  public Duration getCooldown() {
    return cooldown;
  }

  /**
   * Getter for the default time value needed to pass until the Skill can be
   * executed. Measured in milliseconds.
   * @return default time value needed to pass until the Skill can be executed.
   */
  public Duration getMaxCooldown() {
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
    return requirements.iterator();
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
   * The Fighter object that the skill belongs to.
   * @return the owner of the skill.
   */
  public Fighter getOwner() {
    return owner;
  }
  
  /**
   * Returns {@code true} if the skill is in a usable state. By default, a skill
   * cannot be used if it lacks an owner. In addition, a {@link Predicate}
   * object passed to this skill during initiation can place additional
   * conditions.
   * @return enumerated Row value that the Skill is usable in.
   */
  public boolean isUsable() {
    // @todo uncomment if (owner == null) return false;
    // @todo uncomment return usablity.test(this);
    return true;
  }

  @Override
  public String toString() {
    return this.name;
  }
  
}
