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
 * Constructs {@link Status} objects using a builder pattern. All setter methods
 * return the instance of the object it is called upon. The build method returns
 * a new Status object using the information obtained through defaults and set
 * methods.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class StatusBuilder {

  /**
   * Stores the name value for producing a Status object.
   * @see #setName
   */
  private String name;
  
  /**
   * Stores the description value for producing a Status object.
   * @see #setDescription
   */
  private String description;
  
  /**
   * Stores the duration value for producing a Status object.
   * @see #setDuration
   */
  private Duration duration;
  
  /**
   * Stores the stackSize value for producing a Status object.
   * @see #setStackSize
   */
  private int stackSize;
  
  /**
   * Stores the stacks value for producing a Status object.
   * @see #setStackable
   */
  private boolean stackable;
  
  /**
   * Stores the stunning value for producing a Status object.
   * @see #setStunning
   */
  private boolean stunning;
  
  /**
   * Stores the defeating value for producing a Status object.
   * @see #setDefeating
   */
  private boolean defeating;
  
  /**
   * Stores the hidden value for producing a Status object.
   * @see #setHidden
   */
  private boolean hidden;
  
  /**
   * Stores the applyCondition value for producing a Status object.
   * @see #setApplyCondition
   */
  private Predicate<Fighter> applyCondition;
  
  /**
   * Stores the removeCondition value for producing a Status object.
   * @see #setRemoveCondition
   */
  private Predicate<Fighter> removeCondition;
  
  /**
   * Stores the listeners value for producing a Status object.
   * @see #addListener
   */
  private final List<StatusHandler> listeners;

  /**
   * Instantiates the object with the name of the {@link Status} to be built.
   * Sets all other properties of the status to be built to their default
   * values.
   * @param name see {@see #setName}. Cannot be null.
   */
  public StatusBuilder(String name) {
    if (name == null) {
      throw new NullPointerException("name: null");
    }
    this.name = name;
    this.description = "";
    this.duration = Duration.ZERO;
    this.stackSize = 1;
    this.stackable = true;
    this.stunning = false;
    this.defeating = false;
    this.hidden = false;
    this.applyCondition = a -> true;
    this.removeCondition = a -> true;
    this.listeners = new ArrayList<>();
  }
  
  /**
   * Instantiates the object by setting all properties so that any status built
   * directly after initialization would be a copy of the Status object given.
   * @param status the status used to set all properties.
   */
  public StatusBuilder(Status status) {
    Status copyOf = new Status(status);
    this.name = copyOf.getName();
    this.description = copyOf.getDescription();
    this.duration = copyOf.getDuration();
    this.stackSize = copyOf.getStackSize();
    this.stackable = copyOf.isStackable();
    this.stunning = copyOf.isStunning();
    this.defeating = copyOf.isDefeating();
    this.hidden = copyOf.isHidden();
    this.applyCondition = copyOf.getApplyConidtion();
    this.removeCondition = copyOf.getRemoveConidtion();
    this.listeners = copyOf.getListeners();
  }

  /**
   * Creates a new {@link Status} object built with the values set by this
   * builder object. Default values for all parameters, if not explicitely set,
   * are used with exception to the name parameter, which is set when the
   * StatusBuilder is initiated.
   * @return new Status object built with the values set in this builder object.
   */
  public Status build() {
    return new Status(name, description, duration, stackSize, stackable,
        stunning, defeating, hidden, applyCondition, removeCondition,
        listeners);
  }

  /**
   * Sets the name that identifies the status to be built from unrelated {@link
   * Status} objects. The name property has no default value.
   * @param name name parameter for producing a Status object. Cannot be {@code
   *        null}.
   * @return this object.
   */
  public StatusBuilder setName(String name) {
    if (name == null) throw new NullPointerException("name: null");
    this.name = name;
    return this;
  }

  /**
   * Sets the description of how the status to be built is intended to interact
   * with {@link Fighter} objects it is applied to, as well as its interactions
   * with other {@link Status} objects on the same fighter. The default
   * description is an empty string.
   * @param description description parameter for producing a Status object.
   *        Cannot be {@code null}.
   * @return this object.
   */
  public StatusBuilder setDescription(String description) {
    if (description == null)
      throw new NullPointerException("description: null");
    this.description = description;
    return this;
  }

  /**
   * Sets the time before this status expires once it is applied. Zero means the
   * status has an instant duration and should expire as soon as it is applied.
   * Less then zero means duration is infinite. The default duration is instant.
   * @param duration duration parameter for producing a Status object. Cannot be
   *        {@code null}.
   * @return this object.
   */
  public StatusBuilder setDuration(Duration duration) {
    if (duration == null) throw new NullPointerException("duration: null");
    this.duration = duration;
    return this;
  }

  /**
   * Sets the status to be built so that it would be marked for removal
   * immediately after being applied. This is a convenience method for setting
   * the duration to {@link Duration#ZERO ZERO}.
   * @return this object.
   */
  public StatusBuilder setAsInstant() {
    this.duration = Duration.ZERO;
    return this;
  }
  
  /**
   * Sets this so that the status to be built can not expire due to the passing
   * of time. This is a convenience method for setting the duration to
   * {@code -1} second.
   * @return this object.
   */
  public StatusBuilder setAsInfinite() {
    this.duration = Duration.ofSeconds(-1);
    return this;
  }
  
  /**
   * Sets the current number of stacks. The default stack size is {@code 1}.
   * @param stackSize stack size parameter for producing a Status object. Cannot
   *        be {@code < 1}.
   * @return this object.
   */
  public StatusBuilder setStackSize(int stackSize) {
    if (stackSize < 1) throw new IllegalArgumentException("stacks size: < 1");
    this.stackSize = stackSize;
    return this;
  }

  /**
   * Sets the status to be built so that it can have a stack size greater then
   * {@code 1} when set to {@code true}. The default is {@code true}.
   * @param stackable stackable parameter for producing a Status object.
   * @return this object.
   */
  public StatusBuilder setStackable(boolean stackable) {
    this.stackable = stackable;
    return this;
  }

  /**
   * Sets the status to be built so that it can stun the fighter it is applied
   * to when set to {@code true}. Stunned fighters do not decrement their skill
   * cooldowns or execute skills for non-stunbreak skills. The default is {@code
   * false}.
   * @param stunning stunning parameter for producing a Status object.
   * @return this object.
   * @see SkillBuilder#setStunBreak(boolean)
   */
  public StatusBuilder setStunning(boolean stunning) {
    this.stunning = stunning;
    return this;
  }

  /**
   * Sets the status to be built so that it can defeat the fighter it is applied
   * to when set to {@code true}. Defeated {@link Fighter} objects allow their
   * team to lose a battle if all other ally fighters are also defeated.
   * Defeated fighters do not decrement their skill cooldowns or execute skills
   * for non-deathless skills. The default is {@code false}.
   * @param defeats defeats parameter for producing a Status object.
   * @return this object.
   * @see SkillBuilder#setDeathless(boolean)
   */
  public StatusBuilder setDefeating(boolean defeats) {
    this.defeating = defeats;
    return this;
  }

  /**
   * Sets the status to be built so that it should not be visible to the user of
   * the client when set to {@code true}. The default is {@code false}.
   * @param hidden hidden parameter for producing a Status object.
   * @return this object.
   */
  public StatusBuilder setHidden(boolean hidden) {
    this.hidden = hidden;
    return this;
  }
  
  /**
   * Sets the test condition that returns {@code true} if the status can be
   * successfully applied to the target owner of the status to be built. Accepts
   * the target owner of the status as the given parameter. The default
   * condition is a function that returns {@code true}.
   * @param applyCondition apply condition parameter for producing a Status
   *        object. Cannot be {@code null}.
   * @return this object.
   */
  public StatusBuilder setApplyCondition(Predicate<Fighter> applyCondition) {
    if (applyCondition == null)
      throw new NullPointerException("apply condition: null");
    this.applyCondition = applyCondition;
    return this;
  }

  /**
   * Sets the test condition that returns {@code true} if the status can be
   * successfully removed from the owner of the status to be built. Accepts
   * the owner of the status as the given parameter.  The default condition is a
   * function that returns {@code true}.
   * @param removeCondition remove condition parameter for producing a Status
   *        object. Cannot be {@code null}.
   * @return this object.
   */
  public StatusBuilder setRemoveCondition(Predicate<Fighter> removeCondition) {
    if (removeCondition == null)
      throw new NullPointerException("remove condition: null");
    this.removeCondition = removeCondition;
    return this;
  }
  
  /**
   * Adds a new {@link StatusHandler} object to receive method calls during
   * appropriate events in the skill, such status application and removal.
   * @param listener new listener for producing a Status object. Cannot be
   *        {@code null}.
   * @return this object.
   */
  public StatusBuilder addListener(StatusHandler listener) {
    if (listener == null) throw new NullPointerException("listener: null");
    listeners.add(listener);
    return this;
  }
  
  /**
   * Removes a listener from the list of listeners for producing a Status
   * object.
   * @param  listener the object to be removed.
   * @return true if the object was successfully removed.
   * @see #addListener(battle.StatusHandler)
   */
  public boolean removeListener(StatusHandler listener) {
    return this.listeners.remove(listener);
  }
  
}
