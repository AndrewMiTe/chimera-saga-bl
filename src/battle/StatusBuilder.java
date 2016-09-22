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
   * @see #setName(java.lang.String)
   */
  private String name;
  
  /**
   * Stores the description value for producing a Status object.
   * @see #setDescription(java.lang.String)
   */
  private String description;
  
  /**
   * Stores the duration value for producing a Status object.
   * @see #setDuration(java.time.Duration)
   */
  private Duration duration;
  
  /**
   * Stores the stackSize value for producing a Status object.
   * @see #setStackSize(int)
   */
  private int stackSize;
  
  /**
   * Stores the stacks value for producing a Status object.
   * @see #setStackable(boolean)
   */
  private boolean stackable;
  
  /**
   * Stores the stunning value for producing a Status object.
   * @see #setStunning(boolean)
   */
  private boolean stunning;
  
  /**
   * Stores the defeating value for producing a Status object.
   * @see #setDefeating(boolean)
   */
  private boolean defeating;
  
  /**
   * Stores the hidden value for producing a Status object.
   * @see #setHidden(boolean)
   */
  private boolean hidden;
  
  /**
   * Stores the applyCondition value for producing a Status object.
   * @see #setApplyCondition(java.util.function.Predicate)
   */
  private Predicate<Fighter> applyCondition;
  
  /**
   * Stores the removeCondition value for producing a Status object.
   * @see #setRemoveCondition(java.util.function.Predicate)
   */
  private Predicate<Fighter> removeCondition;
  
  /**
   * Stores the listeners value for producing a Status object.
   * @see #addListener(battle.StatusHandler)
   */
  private final List<StatusHandler> listeners;

  /**
   * Instantiates the object with the name of the {@link Status} to be built.
   * Passing a {@code null} value to the constructor will throw an {@link
   * IllegalArgumentException}.
   * @param name see {@see #setName(java.lang.String)}
   */
  public StatusBuilder(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
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
   * Status} objects. Attempting to set the value as {@code null} will throw an
   * {@link IllegalArgumentException}. There is no default value.
   * @param name name of a status.
   * @return this object.
   */
  public StatusBuilder setName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    this.name = name;
    return this;
  }

  /**
   * Sets the description of the status to be built and how it is intended to
   * interact with {@link Fighter} objects it is applied to, as well as its
   * interactions with other {@link Status} objects on the same fighter.
   * Attempting to initiate the value as {@code null} will throw an {@link
   * IllegalArgumentException}. The default value is an empty string.
   * @param description description of a status.
   * @return this object.
   */
  public StatusBuilder setDescription(String description) {
    if (description == null) {
      throw new IllegalArgumentException("description cannot be null");
    }
    this.description = description;
    return this;
  }

  /**
   * Sets the time before this status expires once it is applied. Zero means the
   * status has an instant duration and should expire as soon as it is applied.
   * Less then zero means duration is infinite. Attempting to initiate the value
   * as {@code null} will throw an {@link IllegalArgumentException}. The default
   * value is instant ({@link Duration#ZERO ZERO}).
   * @param duration the time until the status expires.
   * @return this object.
   */
  public StatusBuilder setDuration(Duration duration) {
    if (duration == null) {
      throw new IllegalArgumentException("duration cannot be null");
    }
    this.duration = duration;
    return this;
  }

  /**
   * Sets the status to be built so that it would be marked for removal
   * immediately after being applied. This is a convenience method for setting
   * the duration value to {@link Duration#ZERO ZERO}.
   * @return this object.
   */
  public StatusBuilder setAsInstant() {
    this.duration = Duration.ZERO;
    return this;
  }
  
  /**
   * Sets this so that the status to be built can not expire due to the passing
   * of time. This is a convenience method for setting the duration value to
   * {@code -1} second.
   * @return this object.
   */
  public StatusBuilder setAsInfinite() {
    this.duration = Duration.ofSeconds(-1);
    return this;
  }
  
  /**
   * Sets the current number of stacks. Attempting to initiate the value as less
   * then {@code 1} will throw an {@link IllegalArgumentException}. The default
   * value is {@code 1}.
   * @param stackSize the size of the stack.
   * @return this object.
   */
  public StatusBuilder setStackSize(int stackSize) {
    if (stackSize < 1) {
      throw new IllegalArgumentException("stacks cannot be less then 1");
    }
    this.stackSize = stackSize;
    return this;
  }

  /**
   * Sets the status to be built so that it can have a stack size greater then
   * {@code 1}. The default value is {@code true}.
   * @param stackable allows the status to stack when {@code true}
   * @return this object.
   */
  public StatusBuilder setStackable(boolean stackable) {
    this.stackable = stackable;
    return this;
  }

  /**
   * Sets the status to be built so that it can stun the fighter it is applied
   * to. Stunned fighters do not decrement their skill cooldowns or execute
   * skills for non-stunbreak skills. The default value is {@code false}.
   * @param stunning allows the status to stun when {@code true}.
   * @return this object.
   * @see SkillBuilder#setStunBreak(boolean)
   */
  public StatusBuilder setStunning(boolean stunning) {
    this.stunning = stunning;
    return this;
  }

  /**
   * Sets the status to be built to that it can defeat the fighter it is applied
   * to. Defeated {@link Fighter} objects allow their team to lose a battle if
   * all other ally fighters are also defeated. Defeated fighters do not
   * decrement their skill cooldowns or execute skills for non-deathless skills.
   * The default value is {@code false}.
   * @param defeats allows the status to defeat when {@code true}.
   * @return this object.
   * @see SkillBuilder#setDeathless(boolean)
   */
  public StatusBuilder setDefeating(boolean defeats) {
    this.defeating = defeats;
    return this;
  }

  /**
   * @param hidden hidden value for producing a Status object. The default value
   * is {@code false}.
   * @return this.
   * @see battle.Status Status.hidden
   */
  public StatusBuilder setHidden(boolean hidden) {
    this.hidden = hidden;
    return this;
  }
  
  /**
   * @param applyCondition applyCondtion value for producing a Status object.
   * The default value is a function that returns {@code true}.
   * @return this.
   * @see battle.Status Status.applyCondition
   */
  public StatusBuilder setApplyCondition(Predicate<Fighter> applyCondition) {
    if (applyCondition == null) {
      throw new IllegalArgumentException("Condition for application cannot be"
          + " null");
    }
    this.applyCondition = applyCondition;
    return this;
  }

  /**
   * @param removeCondition removeCondition value for producing a Status object.
   * The default value is a function that returns {@code true}.
   * @return this.
   * @see battle.Status Status.removeCondition
   */
  public StatusBuilder setRemoveCondition(Predicate<Fighter> removeCondition) {
    if (removeCondition == null) {
      throw new IllegalArgumentException("Condition for removal cannot be"
          + " null");
    }
    this.removeCondition = removeCondition;
    return this;
  }
  
  /**
   * Adds to the list of handler objects that who's methods are called during
   * appropriate state changes in the Status object.
   * @param  listener object to handle state changes.
   * @return this.
   * @see battle.Status Status.listeners
   */
  public StatusBuilder addListener(StatusHandler listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listeners cannot be null");
    }
    listeners.add(listener);
    return this;
  }
  
  /**
   * Removes a handler object from the list of listeners who's methods are
   * called during appropriate state changes in the Status object.
   * @param  listener the object to be removed.
   * @return true if the object was successfully removed.
   * @see battle.Status Status.listeners
   */
  public boolean removeListener(StatusHandler listener) {
    return this.listeners.remove(listener);
  }
  
}
