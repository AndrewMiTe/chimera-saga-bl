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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class Skill implements TurnItem {
  
  /**
   * @see SkillBuilder#setName
   */
  private String name;
  
  /**
   * @see SkillBuilder#setDescription
   */
  private String description;
  
  /**
   * @see SkillBuilder#setTarget
   */
  private Target target;
  
  /**
   * @see SkillBuilder#setMaxTargets
   */
  private int maxTargets;
  
  /**
   * @see SkillBuilder#setCooldown
   */
  private Duration cooldown;
  
  /**
   * The current amount of time remaining until the skill can be used.
   */
  private Duration timeRemaining;
  
  /**
   * @see SkillBuilder#setUseCase
   */
  private final Predicate<Skill> useCase;
  
  /**
   * @see SkillBuilder#setStunBreak
   */
  private final boolean stunBreak;

  /**
   * @see SkillBuilder#setDeathless
   */
  private final boolean deathless;
  
  /**
   * @see SkillBuilder#addEffect
   */
  private final List<Status> effects;
  
  /**
   * @see SkillBuilder#addRequirement
   */
  private final List<String> requirements;
  
  /**
   * @see SkillBuilder#addSubskill
   */
  private final List<Skill> subSkills;
  
  /**
   * @see SkillBuilder#addListener
   */
  private final List<SkillHandler> listeners;
  
  /**
   * The Fighter object that the skill belongs to. Value should remain null
   * until the skill has been applied using the {@link #onApply(battle.Fighter)
   * onApply} method.
   */
  private Fighter owner;

  /**
   * Initializes the object so that all internal field variables that can be
   * explicitly set are done so through the given parameters. See the {@link 
   * SkillBuilder} class which allows you to create Skill objects using a
   * builder pattern.
   * @param name {@see SkillBuilder#setName}
   * @param description {@see SkillBuilder#setDescription}
   * @param target {@see SkillBuilder#setTarget}
   * @param maxTargets {@see SkillBuilder#setMaxTargets}
   * @param cooldown {@see SkillBuilder#setCooldown}
   * @param useCase {@see SkillBuilder#setUseCase}
   * @param stunBreak {@see SkillBuilder#setStunBreak}
   * @param deathless {@see SkillBuilder#setDeathless}
   * @param requirements {@see SkillBuilder#addRequirement}
   * @param effects {@see SkillBuilder#addEffect}
   * @param subSkills {@see SkillBuilder#addSubSkill}
   * @param listeners {@see SkillBuilder#addListener}
   */
  protected Skill(String name, String description, Target target,
      int maxTargets, Duration cooldown, Predicate<Skill> useCase,
      boolean stunBreak, boolean deathless, List<Status> effects,
      List<String> requirements, List<Skill> subSkills,
      List<SkillHandler> listeners) {
    if (name == null) {
      throw new NullPointerException("name: null");
    }
    this.name = name;
    if (description == null) {
      throw new NullPointerException("description: null");
    }
    this.description = description;
    if (target == null) {
      throw new NullPointerException("target: null");
    }
    this.target = target;
    if (maxTargets < 1) {
      throw new IllegalArgumentException("maxTargets: < 1");
    }
    this.maxTargets = maxTargets;
    if (cooldown.isZero()) {
      throw new IllegalArgumentException("maxCooldown: ZERO");
    }
    if (cooldown == null) {
      throw new NullPointerException("maxCooldown: null");
    }
    this.cooldown = cooldown;
    this.timeRemaining = cooldown;
    if (useCase == null) {
      throw new NullPointerException("usablity: null");
    }
    this.useCase = useCase;
    if (effects != null & effects.contains(null)) {
      throw new NullPointerException("effects: null");
    }
    this.stunBreak = stunBreak;
    this.deathless = deathless;
    if (effects != null && effects.contains(null)) {
      throw new NullPointerException("effects: contains null");
    }
    this.effects = new ArrayList<>(effects);
    if (requirements != null && requirements.contains(null)) {
      throw new NullPointerException("requirements: contains null");
    }
    this.requirements = new ArrayList<>(requirements);
    if (subSkills != null && subSkills.contains(null)) {
      throw new NullPointerException("subSkills: contains null");
    }
    this.subSkills = new ArrayList<>(subSkills);
    if (listeners != null && listeners.contains(null)) {
      throw new NullPointerException("listeners: contains null");
    }
    this.listeners = new ArrayList<>(listeners);
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
    this.useCase = copyOf.useCase;
    this.stunBreak = copyOf.stunBreak;
    this.deathless = copyOf.deathless;
    this.target = copyOf.target;
    this.cooldown = copyOf.cooldown;
    this.timeRemaining = copyOf.timeRemaining;
    this.requirements = new ArrayList<>(copyOf.requirements);
    this.effects = new ArrayList<>(copyOf.effects);
    this.subSkills = new ArrayList<>(copyOf.subSkills);
    this.listeners = new ArrayList<>(copyOf.listeners);
    this.owner = null;
  }

  /**
   * @param listener object to handle events.
   * @see SkillBuilder#addListener
   */
  public void addListener(SkillHandler listener) {
    if (listener == null) throw new NullPointerException("listeners: null");
    listeners.add(listener);
  }
  
  /**
   * Removes a listener from the list of listeners.
   * @param listener the object to be removed.
   * @return {@code true} if the object was successfully removed.
   * @see SkillBuilder#addListener
   */
  public boolean removeListener(SkillHandler listener) {
    return this.listeners.remove(listener);
  }
  
  /**
   * Event method for when this skill is applied.
   * @param  newOwner the fighter the skill is being applied to.
   * @return {@code true} if skill can be applied to the target owner.
   */
  protected final boolean onApply(Fighter newOwner) {
    this.owner = newOwner;
    for (SkillHandler handler : listeners) {
      handler.onSkillApplication(this);
    }
    return true;
  }

  /**
   * Event method for when this skill is removed.
   * @return {@code true} if the skill can be removed from its owner.
   */
  protected final boolean onRemove() {
    owner = null;
    for (SkillHandler handler : listeners) {
      handler.onSkillRemoval(this);
    }
    return true;
  }

  /**
   * Event method for when this skill is executed.
   */
  protected final void onExecute() {
    for (SkillHandler handler : listeners) {
      handler.onSkillExecution(this);
    }
  }
  
  /**
   * @return name property of the skill.
   * @see SkillBuilder#setName
   */
  public String getName() {
    return name;
  }
  
  /**
   * @return description property of the skill.
   * @see SkillBuilder#setDescription
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return target property of the skill.
   * @see SkillBuilder#setTarget
   */
  public Target getTarget() {
    return target;
  }
  
  /**
   * @return maximum targets property of the skill.
   * @see SkillBuilder#setMaxTargets
   */
  public int getMaxTargets() {
    return maxTargets;
  }
  
  /**
   * @return cooldown property of the skill.
   * @see SkillBuilder#setCooldown
   */
  public Duration getCooldown() {
    return cooldown;
  }

  /**
   * Returns the current amount of time remaining until the skill can be used.
   * @return time remaining of the skill.
   */
  public Duration getTimeRemaining() {
    return timeRemaining;
  }

  /**
   * @return useCase property of the skill.
   * @see SkillBuilder@setUseCase
   */
  protected Predicate<Skill> getUseCase() {
    return useCase;
  }
  
  /**
   * @return list of effects.
   * @see SkillBuilder#addEffect
   */
  public List<Status> getEffects() {
    return new ArrayList<>(effects);
  }

  /**
   * @return list of requirements.
   * @see SkillBuilder#addRequirement
   */
  public List<String> getRequires() {
    return new ArrayList<>(requirements);
  }

  /**
   * @return list of sub-skills.
   * @see SkillBuilder#addSubSkill
   */
  public List<Skill> getSubSkills() {
    return new ArrayList<>(subSkills);
  }

  /**
   * @return list of listeners.
   * @see SkillBuilder#addListener
   */
  protected final List<SkillHandler> getListeners() {
    return new ArrayList<>(listeners);
  }
 
  /**
   * Returns the Fighter object that the skill belongs to.
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
   * @return {@code true} if the skill is usable by its owner.
   */
  public boolean isUsable() {
    return owner != null && useCase.test(this) &&
        (deathless ? true : !owner.isDefeated()) &&
        (stunBreak ? true : !owner.isStunned());
  }

  /**
   * @return {@code true} if the skill is usable while the owner is stunned.
   * @see SkillBuilder#setStunBreak(boolean)
   */
  public boolean isStunBreak() {
    return stunBreak;
  }
  
  /**
   * @return {@code true} if the skill is usable while the owner is defeated.
   * @see SkillBuilder#setDeathless(boolean)
   */
  public boolean isDeathless() {
    return deathless;
  }
  
  /**
   * Returns {@code true} if the skill is consistent with the requirements to be
   * a pre-battle skill, a skill executed before combat begins and is not
   * executed again that battle. Pre-battle skills are required to have a
   * negative cooldown value and a target value of SELF only.
   * @return {@code true} if this is a pre-battle skill.
   */
  public boolean isPreBattleSkill() {
    return cooldown.isNegative() && (target == Target.SELF);
  }
  
  @Override // from TurnItem
  public LocalDateTime getTurnTime(LocalDateTime currentTime) {
    return currentTime.plus(timeRemaining);
  }
  
  @Override // from TurnItem
  public boolean advanceTime(Duration timeChange) {
    if (!isPreBattleSkill() && isUsable()) {
      timeRemaining = timeRemaining.minus(timeChange);
      if (timeRemaining.isNegative()) timeRemaining.isZero();
      if (timeRemaining.isZero()) return owner.executeSkill(this);
    }
    return false;
  }
  
  @Override // from TurnItem
  public Actor getActor() {
    return owner;
  }

  @Override // from Object
  public String toString() {
    return this.name;
  }
  
}
