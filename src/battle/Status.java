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

/**
 * A state that can be applied to {@link Fighter} objects through the execution
 * of {@link Skill} objects. All Status objects have a name field that is used
 * to identify its equivalence. Status objects with the same name can be applied
 * to the same fighter so as to stack, either in magnitude or duration.
 * @see StatusBuilder
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class Status {
  
  /**
   * Name of the status. Identifies the status from unrelated Status objects.
   * Attempting to initiate the value as {@code null} will throw an {@link
   * IllegalArgumentException}.
   */
  private final String name;
  /**
   * Description of the status and how it is intended to interact with
   * Fighter objects it is applied to, as well as its interactions with other
   * Status objects on the same fighter. Attempting to initiate the value as
   * {@code null} will throw an {@link IllegalArgumentException}.
   */
  private final String description;
  /**
   * The time before this status expires. Zero means the Status has an instant
   * duration and should expire as soon as it is applied. Less then zero means
   * duration is infinite. Attempting to initiate the value as {@code null} will
   * throw an {@link IllegalArgumentException}.
   */
  private Duration duration;
  /**
   * The current number of stacks. Attempting to initiate the value as less then
   * 1 will throw an {@link IllegalArgumentException}.
   */
  private int stackSize;
  /**
   * States whether the status increases in stack size when equivalent Status
   * objects are added to it.
   */
  private final boolean stacks;
  /**
   * States whether or not this status stuns the fighter. Stunned fighters do
   * not decrement their skill cooldowns and cannot execute skills.
   */
  private final boolean stuns;
  /**
   * States whether or not the Status object defeats the fighter. Defeated
   * Fighter objects allow their team to lose a battle if all other ally
   * fighters are also defeated.
   */
  private final boolean defeats;
  /**
   * States whether or not the status should be visible to the user of the
   * client.
   */
  private final boolean hidden;
  /**
   * List of handler objects that who's methods are called during appropriate
   * state changes in the Status object.
   */
  private final List<StatusHandler> handlers;
  /**
   * The Fighter object that the status belongs to. Value should remain null
   * until the status has been applied using the {@link #onApply(battle.Fighter)
   * onApply} method.
   */
  private Fighter owner;
  
  /**
   * Initializes the object so that all internal field variables that can be
   * explicitly set are done so through the given parameters. See the {@link 
   * StatusBuilder} class which allows you to create Status object using the
   * builder pattern.
   * @param name {@see #name}
   * @param description {@see #description}
   * @param duration {@see #duration}
   * @param stackSize {@see #stackSize}
   * @param stacks {@see #stacks}
   * @param stuns {@see #stuns}
   * @param defeats {@see #defeats}
   * @param hidden {@see #hidden}
   */
  public Status(String name, String description, Duration duration,
      int stackSize, boolean stacks, boolean stuns, boolean defeats,
      boolean hidden) {
    if (name == null) {
      throw new IllegalArgumentException("description cannot be null");
    }
    this.name = name;
    if (description == null) {
      throw new IllegalArgumentException("description cannot be null");
    }
    this.description = description;
    if (duration == null) {
      throw new IllegalArgumentException("duration cannot be null");
    }
    this.duration = duration;
    if (stackSize < 0) {
      throw new IllegalArgumentException("stacks cannot be negative");
    }
    this.stackSize = stackSize;
    this.stacks = stacks;
    this.stuns = stuns;
    this.defeats = defeats;
    this.hidden = hidden;
    this.handlers = new ArrayList<>();
    this.owner = null;
  }
  
  /**
   * Initializes a deep copy of the given Status object such that changes to the
   * state of either the original or the copy have no affect on the other.
   * @param copyOf object which the copy is made from.
   */
  public Status(Status copyOf) {
    this.name = copyOf.name;
    this.description = copyOf.description;
    this.duration = copyOf.duration;
    this.stackSize = copyOf.stackSize;
    this.stacks = copyOf.stacks;
    this.stuns = copyOf.stuns;
    this.defeats = copyOf.defeats;
    this.hidden = copyOf.hidden;
    this.handlers = new ArrayList<>(copyOf.handlers);
    this.owner = null;
  }
  
  /**
   * List of handler objects that who's methods are called during appropriate
   * state changes in the Status object.
   * @param handler object to handle state changes.
   */
  public void addStatusHandler(StatusHandler handler) {
    if (handler == null) {
      throw new IllegalArgumentException("StatusHandler cannot be null");
    }
    handlers.add(handler);
  }
  
  /**
   * Returns a description of the status and how it is intended to interact with
   * Fighter objects it is applied to, as well as its interactions with other
   * Status objects on the same fighter.
   * @return description of the status
   */
  public String getDescription() {
    return description;
  }

  /**
   * The time before this status expires. Zero means the Status has an instant
   * duration and should expire as soon as it is applied. Less then zero means
   * duration is infinite.
   * @return time before the status expires.
   */
  public Duration getDuration() {
    return duration;
  }

  /**
   * Name of the status. Identifies the status from unrelated Status objects.
   * @return name of the Status.
   */
  public String getName() {
    return name;
  }

  /**
   * The Fighter object that the status belongs to.
   * @return the owner of the status.
   */
  public Fighter getOwner() {
    return owner;
  }
  
  /**
   * The current number of stacks.
   * @return stack size.
   */
  public int getStackSize() {
    return stackSize;
  }

  /**
   * Returns true if the Status object defeats the fighter. Defeated Fighter
   * objects allow their team to lose a battle if all other ally fighters are
   * also defeated.
   * @return true if this defeats the fighter it is applied to.
   */
  public boolean isDefeating() {
    return defeats;
  }
  
  /**
   * Returns true is the status should be hidden from client users.
   * @return true if this is hidden from user.
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Returns true if the status increases in stack size when equivalent Status
   * objects are added to it.
   * @return true if this is stacks.
   */
  public boolean isStackable() {
    return stacks;
  }
  
  /**
   * Returns true if this status stuns the fighter. Stunned fighters do not
   * decrement their skill cooldowns and cannot execute skills.
   * @return true if the Status stuns the Unit it is applied to.
   */
  public boolean isStunning() {
    return stuns;
  }

  /**
   * Event method for when this Status is applied.
   * @param owner the Unit the Status is being applied to.
   */
  public void onApply(Fighter owner) {
    this.owner = owner;
    for (StatusHandler handler : handlers) {
      handler.onStatusApplication(owner);
    }
  }

  /**
   * Event method for when this status is removed.
   */
  public void onRemove() {
    owner = null;
    for (StatusHandler handler : handlers) {
      handler.onStatusApplication(owner);
    }
  }

  /**
   * Sets the time before the status expires. If set to zero, the status is
   * considered to have an instant duration. If set to a negative value, the
   * duration is considered infinite.
   * @param duration time before this Status expires.
   */
  public void setDuration(Duration duration) {
    this.duration = duration;
  }
  
  /**
   * Sets the stack size of the Status. Attempting to set this value below zero
   * will throw an IllegalArgumentException. Attempting to set this value to
   * anything other then 1 while the duration is a positive non-zero value will
   * also throw a IllegalArgumentException.
   * @param stacks stack size.
   */
  public void setStacks(int stacks) {
    if (stacks < 0) {
      throw new IllegalArgumentException("stacks cannot be negative");
    }
    if (!duration.isNegative() && !duration.isZero() && (stacks > 1)) {
      throw new IllegalArgumentException("stacks cannot be above 1 when the "
          + "duration is a positive non-zero value");
    }
    this.stackSize = stacks;
  }
  
  @Override
  public String toString() {
    return this.name;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Status)) return false;
    return this.name.equals(((Status)obj).getName());
  }
  
  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
  
}
