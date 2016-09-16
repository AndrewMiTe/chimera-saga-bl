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
 * a new Status object using the information obtained through setters.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class StatusBuilder {

  /**
   * Stores the name value for producing a Status object.
   * @see battle.Status Status.name
   */
  private String name;
  /**
   * Stores the description value for producing a Status object.
   * @see battle.Status Status.description
   */
  private String description;
  /**
   * Stores the duration value for producing a Status object.
   * @see battle.Status Status.duration
   */
  private Duration duration;
  /**
   * Stores the stackSize value for producing a Status object.
   * @see battle.Status Status.stackSize
   */
  private int stackSize;
  /**
   * Stores the stacks value for producing a Status object.
   * @see battle.Status Status.stacks
   */
  private boolean stacks;
  /**
   * Stores the stuns value for producing a Status object.
   * @see battle.Status Status.stuns
   */
  private boolean stuns;
  /**
   * Stores the defeats value for producing a Status object.
   * @see battle.Status Status.defeats
   */
  private boolean defeats;
  /**
   * Stores the hidden value for producing a Status object.
   * @see battle.Status Status.hidden
   */
  private boolean hidden;
  /**
   * Stores the applyCondition value for producing a Status object.
   * @see battle.Status Status.applyCondition
   */
  private Predicate<Fighter> applyCondition;
  /**
   * Stores the removeCondition value for producing a Status object.
   * @see battle.Status Status.removeCondition
   */
  private Predicate<Fighter> removeCondition;
  /**
   * Stores the listeners value for producing a Status object.
   * @see battle.Status Status.listeners
   */
  private final List<StatusHandler> listeners;

  /**
   * Instantiates the object with the name of the {@link Status} to be built.
   * Passing a {@code null} value to the constructor will throw an {@link
   * IllegalArgumentException}.
   * @param name name value for producing a Status object.
   */
  public StatusBuilder(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    this.name = name;
    this.description = "";
    this.duration = Duration.ZERO;
    this.stackSize = 1;
    this.stacks = true;
    this.stuns = false;
    this.defeats = false;
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
   * @return new Status object built with the values set by this builder object.
   */
  public Status build() {
    return new Status(name, description, duration, stackSize, stacks, stuns,
        defeats, hidden, applyCondition, removeCondition, listeners);
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
  
/**
   * @param name name value for producing a Status object. There is no default
   * value. Initialization of the StatusBuilder object requires a non-null name
   * value.
   * @return this.
   * @see battle.Status Status.name
   */
  public StatusBuilder setName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    this.name = name;
    return this;
  }

  /**
   * @param description name value for producing a Status object. The default
   * value is an empty string.
   * @return this.
   * @see battle.Status Status.description
   */
  public StatusBuilder setDescription(String description) {
    if (description == null) {
      throw new IllegalArgumentException("description cannot be null");
    }
    this.description = description;
    return this;
  }

  /**
   * @param duration name value for producing a Status object. The default value
   * is a Duration object of ZERO.
   * @return this.
   * @see battle.Status Status.duration
   */
  public StatusBuilder setDuration(Duration duration) {
    if (duration == null) {
      throw new IllegalArgumentException("duration cannot be null");
    }
    this.duration = duration;
    return this;
  }

  /**
   * Sets this so that the status built would be marked for removal immediately
   * after being applied. This is a convenience method for setting the duration
   * value to ZERO.
   * @return this.
   */
  public StatusBuilder setAsInstant() {
    this.duration = Duration.ZERO;
    return this;
  }
  
  /**
   * Sets this so that the status built could not expire due to the passing of
   * time. This is a convenience method for setting the duration value to one
   * negative second.
   * @return this.
   */
  public StatusBuilder setAsInfinite() {
    this.duration = Duration.ofSeconds(-1);
    return this;
  }
  
  /**
   * @param stackSize name value for producing a Status object. The default
   * value is 1.
   * @return this.
   * @see battle.Status Status.stackSize
   */
  public StatusBuilder setStackSize(int stackSize) {
    if (stackSize < 1) {
      throw new IllegalArgumentException("stacks cannot be less then 1");
    }
    this.stackSize = stackSize;
    return this;
  }

  /**
   * @param stacks name value for producing a Status object. The default value
   * is {@code true}.
   * @return this.
   * @see battle.Status Status.stacks
   */
  public StatusBuilder setStackable(boolean stacks) {
    this.stacks = stacks;
    return this;
  }

  /**
   * @param stuns name value for producing a Status object. The default value
   * is {@code false}.
   * @return this.
   * @see battle.Status Status.stuns
   */
  public StatusBuilder setStuning(boolean stuns) {
    this.stuns = stuns;
    return this;
  }

  /**
   * @param defeats name value for producing a Status object. The default value
   * is {@code false}.
   * @return this.
   * @see battle.Status Status.defeats
   */
  public StatusBuilder setDefeating(boolean defeats) {
    this.defeats = defeats;
    return this;
  }

  /**
   * @param hidden name value for producing a Status object. The default value
   * is {@code false}.
   * @return this.
   * @see battle.Status Status.hidden
   */
  public StatusBuilder setHidden(boolean hidden) {
    this.hidden = hidden;
    return this;
  }
  
  /**
   * @param applyCondition name value for producing a Status object. The default
   * value is a function that returns {@code true}.
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
   * @param removeCondition name value for producing a Status object. The
   * default value is a function that returns {@code true}.
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
  
}
