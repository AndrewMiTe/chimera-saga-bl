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
import java.util.List;
import java.util.function.Predicate;


/**
 * Constructs {@link Skill} objects using a builder pattern. All setter methods
 * return the instance of the object it is called upon. The build method returns
 * a new Skill object using the information obtained through defaults and set
 * methods.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class SkillBuilder {

  /**
   * Stores the name value for producing a Skill object.
   * @see battle.Skill Skill.name
   */
  private String name;
  
  /**
   * Stores the description value for producing a Skill object.
   * @see battle.Skill Skill.description
   */
  private String description;
  
  /**
   * Stores the target value for producing a Skill object.
   * @see battle.Skill Skill.target
   */
  private Target target;
  
  /**
   * Stores the maxCooldown value for producing a Skill object.
   * @see battle.Skill Skill.maxCooldown
   */
  private Duration maxCooldown;
  
  /**
   * Stores the usability value for producing a Skill object.
   * @see battle.Skill Skill.usability
   */
  private Predicate<Skill> usability;

  /**
   * Stores the effects value for producing a Skill object.
   * @see battle.Skill Skill.effects
   */
  private final List<Status> effects;

  /**
   * Stores the requirements value for producing a Skill object.
   * @see battle.Skill Skill.requirements
   */
  private final List<String> requirements;
  
  /**
   * Stores the subSkills value for producing a Skill object.
   * @see battle.Skill Skill.subSkills
   */
  private final List<Skill> subSkills;
  
  /**
   * Stores the listeners value for producing a Skill object.
   * @see battle.Skill Skill.listeners
   */
  private final List<SkillHandler> listeners;

  /**
   * Instantiates the object with the name of the {@link Skill} to be built.
   * Passing a {@code null} value to the constructor will throw an {@link
   * IllegalArgumentException}.
   * @param name name value for producing a Skill object.
   */
  public SkillBuilder(String name) {
    this.name = name;
    this.description = "";
    this.target = Target.SELF;
    this.maxCooldown = Duration.ofSeconds(5);
    this.usability = a -> true;
    this.effects = new ArrayList<>();
    this.requirements = new ArrayList<>();
    this.subSkills = new ArrayList<>();
    this.listeners = new ArrayList<>();
  }

  /**
   * Creates a new {@link Skill} object built with the values set by this
   * builder object. Default values for all parameters, if not explicitely set,
   * are used with exception to the name parameter, which is set when the
   * SkillBuilder is initiated.
   * @return new Skill object built with the values set in this builder object.
   */
  public Skill build() {
    return new Skill(name, description, target, maxCooldown, usability, effects,
        requirements, subSkills, listeners);
  }
  
  /**
   * @param name name value for producing a Skill object. There is no default
   * value. Initialization of the StatusBuilder object requires a non-null name
   * value.
   * @return this.
   * @see battle.Skill Skill.name
   */
  public SkillBuilder setName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    this.name = name;
    return this;
  }

  /**
   * @param description description value for producing a Skill object. The
   * default value is an empty string.
   * @return this.
   * @see battle.Skill Skill.name
   */
  public SkillBuilder setDescription(String description) {
    if (description == null) {
      throw new IllegalArgumentException("description cannot be null");
    }
    this.description = description;
    return this;
  }

  /**
   * @param target target value for producing a Skill object. The default value
   * is SELF.
   * @return this.
   * @see battle.Skill Skill.name
   */
  public SkillBuilder setTarget(Target target) {
    if (target == null) {
      throw new IllegalArgumentException("target cannot be null");
    }
    this.target = target;
    return this;
  }

  /**
   * @param maxCooldown maxCooldown value for producing a Skill object. The
   * default value is 5 seconds.
   * @return this.
   * @see battle.Skill Skill.name
   */
  public SkillBuilder setMaxCooldown(Duration maxCooldown) {
    if (maxCooldown.isZero()) {
      throw new IllegalArgumentException("maxCooldown cannot be ZERO");
    }
    if (maxCooldown == null) {
      throw new IllegalArgumentException("maxCooldown cannot be null");
    }
    this.maxCooldown = maxCooldown;
    return this;
  }

  /**
   * @param usability usability value for producing a Skill object. The default
   * value is a Predicate that always returns true.
   * @return this.
   * @see battle.Skill Skill.name
   */
  public SkillBuilder setUsablity(Predicate<Skill> usability) {
    if (usability == null) {
      throw new IllegalArgumentException("usablity Predicate cannot be null");
    }
    this.usability = usability;
    return this;
  }
  
  /**
   * Sets this so that the skill built would qualify as a pre-battle skill.
   * @return this.
   */
  public SkillBuilder setAsPreBattleSkill() {
    this.maxCooldown = Duration.ofSeconds(-1);
    this.target = Target.SELF;
    return this;
  }

  /**
   * Adds to the list of handler objects that who's methods are called during
   * appropriate state changes or method calls in the Skill object.
   * @param  listener object to handle state changes.
   * @return this.
   * @see battle.Skill Skill.listeners
   */
  public SkillBuilder addListener(SkillHandler listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listeners cannot be null");
    }
    listeners.add(listener);
    return this;
  }
  
  /**
   * Removes a handler object from the list of listeners who's methods are
   * called during appropriate state changes or method calls in the Skill
   * object.
   * @param  listener the object to be removed.
   * @return true if the object was successfully removed.
   * @see battle.Skill Skill.listeners
   */
  public boolean removeListener(SkillHandler listener) {
    return this.listeners.remove(listener);
  }
  
}
