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
   * @see #setName
   */
  private String name;
  
  /**
   * Stores the description value for producing a Skill object.
   * @see #setDescription
   */
  private String description;
  
  /**
   * Stores the target value for producing a Skill object.
   * @see #setTarget
   */
  private Target target;
  
  /**
   * Stores the maxCooldown value for producing a Skill object.
   * @see #setCooldown
   */
  private Duration cooldown;
  
  /**
   * Stores the use case value for producing a Skill object.
   * @see #setUseCase
   */
  private Predicate<Skill> useCase;

  /**
   * Stores the effects value for producing a Skill object.
   * @see #addEffect
   */
  private final List<Status> effects;

  /**
   * Stores the requirements value for producing a Skill object.
   * @see #addRequirement
   */
  private final List<String> requirements;
  
  /**
   * Stores the subSkills value for producing a Skill object.
   * @see #addSubSkill
   */
  private final List<Skill> subSkills;
  
  /**
   * Stores the listeners value for producing a Skill object.
   * @see #addListener
   */
  private final List<SkillHandler> listeners;
  
  /**
   * When {@code false} this object will build the skill with a useCase that
   * also checks to make sure the owner of the skill is not stunned.
   */
  private boolean stunBreak;
  
  /**
   * When {@code false} this object will build the skill with a useCase that
   * also checks to make sure the owner of the skill is not defeated.
   */
  private boolean deathless;

  /**
   * Instantiates the object with the name of the {@link Skill} to be built.
   * @param name see {@see #setName}. Cannot be {@code null}.
   */
  public SkillBuilder(String name) {
    if (name == null) throw new NullPointerException("name: null");
    this.name = name;
    this.description = "";
    this.target = Target.SELF;
    this.cooldown = Duration.ofSeconds(1);
    this.useCase = s -> true;
    this.stunBreak = false;
    this.deathless = false;
    this.effects = new ArrayList<>();
    this.requirements = new ArrayList<>();
    this.subSkills = new ArrayList<>();
    this.listeners = new ArrayList<>();
  }

  public SkillBuilder(Skill skill) {
    this.name = skill.getName();
    this.description = skill.getDescription();
    this.target = skill.getTarget();
    this.cooldown = skill.getCooldown();
    this.useCase = skill.getUseCase();
    this.stunBreak = skill.isStunBreak();
    this.deathless = skill.isDeathless();
    this.effects = skill.getEffects();
    this.requirements = skill.getRequires();
    this.subSkills = skill.getSubSkills();
    this.listeners = skill.getListeners();
  }
  
  /**
   * Creates a new {@link Skill} object built with the values set by this
   * builder object. Default values for all parameters, if not explicitely set,
   * are used with exception to the name parameter, which is set when the
   * SkillBuilder is initiated.
   * @return new Skill object built with properties set to this builder object.
   */
  public Skill build() {
    return new Skill(name, description, target, cooldown, useCase, stunBreak,
        deathless, effects, requirements, subSkills, listeners);
  }
  
  /**
   * Sets the name of the skill to be built. The name property has no default
   * value.
   * @param name name parameter for producing a Skill object. Cannot be {@code
   *        null}.
   * @return this object.
   */
  public SkillBuilder setName(String name) {
    if (name == null) throw new NullPointerException("name: null");
    this.name = name;
    return this;
  }

  /**
   * Sets the description of the skill and how it is intended to interact with
   * its target(s). The default description is an empty string.
   * @param description description parameter for producing a Skill object.
   *        Cannot be {@code null}.
   * @return this object.
   */
  public SkillBuilder setDescription(String description) {
    if (description == null)
      throw new NullPointerException("description: null");
    this.description = description;
    return this;
  }

  /**
   * Sets the enumerated target value for determining valid Fighter objects this
   * skill applies its effects to. The default target is {@link Target#SELF
   * SELF}.
   * @param target target parameter for producing a Skill object. Cannot be
   *        {@code null}.
   * @return this object.
   */
  public SkillBuilder setTarget(Target target) {
    if (target == null) throw new NullPointerException("target: null");
    this.target = target;
    return this;
  }

  /**
   * Sets the amount of time remaining until the skill can be used directly
   * after it is executed. The default cooldown is {@code 1} second.
   * @param cooldown cooldown parameter for producing a Skill object. Cannot be
   *        {@code null} or {@link Duration#ZERO ZERO}.
   * @return this object.
   */
  public SkillBuilder setCooldown(Duration cooldown) {
    if (cooldown.isZero()) {
      throw new IllegalArgumentException("cooldown: ZERO");
    }
    if (cooldown == null) {
      throw new IllegalArgumentException("cooldown: null");
    }
    this.cooldown = cooldown;
    return this;
  }

  /**
   * Sets the case when the skill is usable. When its test method returns {@code
   * true}, it allows the time remaining to decrement and for the skill to
   * execute. The default use case returns {@code true} if the owner of the
   * skill is neither stunned nor defeated.
   * @param useCase usability parameter for producing a Skill object. Cannot be
   *        {@code null}.
   * @return this object.
   */
  public SkillBuilder setUseCase(Predicate<Skill> useCase) {
    if (useCase == null) {
      throw new IllegalArgumentException("use case: null");
    }
    this.useCase = useCase;
    return this;
  }
  
  /**
   * This method allows you to set the skill to be built so that it is usable
   * while the owner of the skill is stunned.
   * @param stunBreak true if the skill is unaffected by stuns.
   * @return this object.
   */
  public SkillBuilder setStunBreak(boolean stunBreak) {
    this.stunBreak = stunBreak;
    return this;
  }
  
  /**
   * This method allows you to set the skill to be built so that it is usable
   * while the owner of the skill is defeated.
   * @param deathless true is the skill is unaffected by defeat.
   * @return this object.
   */
  public SkillBuilder setDeathless(boolean deathless) {
    this.deathless = deathless;
    return this;
  }
  
  /**
   * Sets this so that the skill built would qualify as a pre-battle skill.
   * (Cooldown is negative and the target is {@link Target#SELF SELF}.)
   * @return this object.
   */
  public SkillBuilder setAsPreBattleSkill() {
    this.cooldown = Duration.ofSeconds(-1);
    this.target = Target.SELF;
    return this;
  }

  /**
   * Adds a new Status object to apply to targets of this skill during
   * successful execution.
   * @param effect new effect for producing a Skill object. Cannot be {@code
   *        null}.
   * @return this object.
   */
  public SkillBuilder addEffect(Status effect) {
    if (effect == null) {
      throw new IllegalArgumentException("effect: null");
    }
    effects.add(effect);
    return this;
  }
  
  /**
   * Removes an effect from the list of effects for producing a Skill object.
   * @param effect the object to be removed.
   * @return true if the object was successfully removed.
   * @see SkillBuilder#addEffect
   */
  public boolean removeEffect(Status effect) {
    return this.effects.remove(effect);
  }
  
  /**
   * Adds the name of a new Status object required to be owned by the targets of
   * this skill in order for a successful execution.
   * @param requirement new requirement for producing a Skill object. Cannot be
   *        {@code null}.
   * @return this object.
   */
  public SkillBuilder addRequirement(String requirement) {
    if (requirement == null) {
      throw new IllegalArgumentException("requirement: null");
    }
    requirements.add(requirement);
    return this;
  }
  
  /**
   * Removes a requirement from the list of requirements for producing a Skill
   * object.
   * @param requirement the object to be removed.
   * @return true if the object was successfully removed.
   * @see SkillBuilder#addRequirement
   */
  public boolean removeRequirement(String requirement) {
    return this.requirements.remove(requirement);
  }
  
  /**
   * Adds a new Skill object required to execute successfully before this skill
   * can apply its effects.
   * @param subSkill new subSkill for producing a Skill object. Cannot be {@code
   *        null}.
   * @return this object.
   */
  public SkillBuilder addSubSkill(Skill subSkill) {
    if (subSkill == null) {
      throw new IllegalArgumentException("sub-skill: null");
    }
    subSkills.add(subSkill);
    return this;
  }
  
  /**
   * Removes a sub-skill from the list of sub-skills for producing a Skill
   * object.
   * @param subSkill the object to be removed.
   * @return true if the object was successfully removed.
   * @see SkillBuilder#addSubSkill
   */
  public boolean removeSubSkill(Skill subSkill) {
    return this.subSkills.remove(subSkill);
  }
  
  /**
   * Adds a new {@link SkillHandler} object to receive method calls during
   * appropriate events in the skill, such as successful skill execution.
   * @param listener new listener for producing a Skill object. Cannot be {@code
   *        null}.
   * @return this object.
   */
  public SkillBuilder addListener(SkillHandler listener) {
    if (listener == null) {
      throw new IllegalArgumentException("listener: null");
    }
    listeners.add(listener);
    return this;
  }
  
  /**
   * Removes a listener from the list of listeners for producing a Skill object.
   * @param  listener the object to be removed.
   * @return true if the object was successfully removed.
   * @see SkillBuilder#addListener
   */
  public boolean removeListener(SkillHandler listener) {
    return this.listeners.remove(listener);
  }
  
}
