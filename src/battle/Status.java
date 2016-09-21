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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A state that can be applied to {@link Fighter} objects through the execution
 * of {@link Skill} objects. All Status objects have a name field that is used
 * to identify its equivalence. Status objects with the same name can be applied
 * to the same fighter so as to stack, either in magnitude or duration.
 * @see StatusBuilder
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class Status implements TurnItem {
  
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
   * The time before this status expires once it is applied. Zero means the
   * Status has an instant duration and should expire as soon as it is applied.
   * Less then zero means duration is infinite. Attempting to initiate the value
   * as {@code null} will throw an {@link IllegalArgumentException}.
   */
  private final Duration duration;

  /**
   * The current number of stacks. Attempting to initiate the value as less then
   * {@code 1} will throw an {@link IllegalArgumentException}.
   */
  private final int stackSize;

  /**
   * States whether the status increases in stack size when equivalent Status
   * objects are combined with it.
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
   * Test condition that returns true if the status can be successfully applied
   * to the target owner of the status. Accepts the target owner of the status 
   * as the given parameter. Attempting to initiate the value as {@code null}
   * will throw an {@link IllegalArgumentException}.
   */
  private final Predicate<Fighter> applyCondition;

  /**
   * Test condition that returns true if the status can be successfully removed
   * from the owner of the status. Accepts the owner of the status as the given
   * parameter. Attempting to initiate the value as {@code null} will throw an
   * {@link IllegalArgumentException}.
   */

  private final Predicate<Fighter> removeCondition;
  /**
   * List of handler objects that who's methods are called during appropriate
   * state changes in the Status object.
   */
  private final Set<StatusHandler> listeners;

  /**
   * The Fighter object that the status belongs to. Value should remain null
   * until the status has been applied using the {@link #onApply(battle.Fighter)
   * onApply} method.
   */
  private Fighter owner;

  /**
   * Used within this class to track when to decrement the stack size of the
   * status when it combined with Status objects that have a finite in duration
   * and are stackable.
   */
  private class Stack {
    int stackSize;
    Duration duration;
    Stack(int stackSize, Duration duration) {
      this.stackSize = stackSize;
      this.duration = duration;
    }
  }
  
  /**
   * List of Stack objects representing the stack size and duration of other
   * statuses combined into this.
   */
  private final List<Stack> stackList;
  
  /**
   * Initializes the object so that all internal field variables that can be
   * explicitly set are done so through the given parameters. See the {@link 
   * StatusBuilder} class which allows you to create Status object using a
   * builder pattern.
   * @param name {@see #name}
   * @param description {@see #description}
   * @param duration {@see #duration}
   * @param stackSize {@see #stackSize}
   * @param stacks {@see #stacks}
   * @param stuns {@see #stuns}
   * @param defeats {@see #defeats}
   * @param hidden {@see #hidden}
   * @param applyCondition {@see #applyCondition}
   * @param removeCondition {@see #removeCondition}
   * @param listeners {@see #listeners}
   */
  protected Status(String name, String description, Duration duration,
      int stackSize, boolean stacks, boolean stuns, boolean defeats,
      boolean hidden, Predicate<Fighter> applyCondition, Predicate<Fighter>
      removeCondition, List<StatusHandler> listeners) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
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
    if (applyCondition == null) {
      throw new IllegalArgumentException("Condition for application cannot be"
          + " null");
    }
    this.applyCondition = applyCondition;
    if (removeCondition == null) {
      throw new IllegalArgumentException("Condition for removal cannot be"
          + " null");
    }
    this.removeCondition = removeCondition;
    if (listeners != null & listeners.contains(null)) {
      throw new IllegalArgumentException("listeners list cannot contain null");
    }
    this.listeners = new HashSet<>(listeners);
    this.owner = null;
    this.stackList = new ArrayList<>();
    this.stackList.add(new Stack(stackSize, duration));
  }
  
  /**
   * Initializes a copy of the given Status object such that direct changes to
   * the state of either the original or the copy have no affect on the other.
   * Some copied parameters are purposefully not deep. It is assumed that all
   * {@link StatusHandler} objects passed to handle events should be copied by
   * reference so as not to duplicate potentially large listeners. {@link
   * Predicate} objects passed to test various conditions are also copied by
   * reference and therefore must be immutable in regards to its {@code test}
   * method. A copy is based on the stack size and duration of when the the
   * status was first created. Copies are always without an owner, even if the
   * original has one, thus making the value always {@code null}.
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
    this.applyCondition = copyOf.applyCondition;
    this.removeCondition = copyOf.removeCondition;
    this.listeners = new HashSet<>(copyOf.listeners);
    this.owner = null;
    this.stackList = new ArrayList<>();
    this.stackList.add(new Stack(stackSize, duration));
  }
  
  /**
   * Event method for when this Status is applied.
   * @param  newOwner the Unit the Status is being applied to.
   * @return true if status can be applied to the target owner.
   */
  public final boolean onApply(Fighter newOwner) {
    if (!applyCondition.test(owner)) return false;
    this.owner = newOwner;
    for (StatusHandler handler : listeners) {
      handler.onStatusApplication(this);
    }
    return true;
  }

  /**
   * Event method for when this status is removed.
   * @return true if the status can be removed from its owner.
   */
  public final boolean onRemove() {
    if (!removeCondition.test(owner)) return false;
    owner = null;
    for (StatusHandler handler : listeners) {
      handler.onStatusRemoval(this);
    }
    return true;
  }

  /**
   * Returns {@code true} if the the given Status object can be legally combined
   * with this object. A legal status must first be equivalent by having the
   * same name value. The two objects are checked to see if their duration
   * values are either both instant, both infinite, or both finite. Mismatched
   * flags for stacks, defeats, stuns, and hidden values results in a {@code
   * false} return value. See {@link #combineWith(Status status) combineWith}
   * for a description of what happens when two statuses are successfully
   * combined.
   * @param status the status to test.
   * @return {@code true} if the given status can combine with this one.
   */
  public final boolean canCombine(Status status) {
    return (status != null) &&
        status.name.equals(name) &&
        (status.stacks == this.stacks) &&
        (status.defeats == this.defeats) &&
        (status.stuns == this.stuns) &&
        (status.hidden == this.hidden) &&
        ((status.isInstant() == status.isInstant()) ||
        (status.isFinite() == this.isFinite()) ||
        (status.isInfinite() == this.isInfinite()));
  }
  
  /**
   * Takes a Status object that can legally combine with this status and
   * incorporates many properties, including stack size, duration, and listeners
   * whenever applicable. When the {@link #isStackable() isStackable} method
   * returns {@code true} for this status, the stack size of the given status is
   * combined with this. In addition, if this status is finite in duration, the
   * given status is incorporated so that the stack size may decrement at
   * varying intervals, based on the durations of the incorporated status and
   * this. If this status is finite and not stackable, then the duration of the
   * given status is added to this. In all cases this will inherit any {@link
   * StatusHandler} objects listening to the given Status object that this
   * doesn't already have. Passing this method a value that would return {@code
   * false} if passed to this object's {@link #canCombine(battle.Status)
   * canCombine} method will throw an {@link IllegalArgumentException}.
   * @param status the status to combine with.
   */
  public final void combineWith(Status status) {
    if (!canCombine(status)) {
      throw new IllegalArgumentException("Status cannot be combined.");
    }
    if (isStackable()) {
      if (isFinite()) {
        stackList.addAll(status.stackList);
      }
      else {
        stackList.get(0).stackSize += status.stackList.get(0).stackSize;
      }
    }
    else {
      if (isFinite()) {
        stackList.get(0).duration.plus(status.stackList.get(0).duration);
      }
    }
  }
  
  /**
   * Decrements the time remaining by the amount of time given. If the result
   * decreases the time remaining to zero, the status informs the owner to
   * remove the status. If the status has multiple stacks of varying duration,
   * all stacks decrement equally and any stacks with no remaining time are
   * removed. Status objects that are infinite in duration are unchanged by
   * calls to this method. Passing a negative duration value throws an {@link
   * IllegalArgumentException}.
   * @param amount the amount of time to remove.
   */
  public final void removeDuration(Duration amount) {
    if (amount.isNegative()) throw new IllegalArgumentException(" Cannot "
        + "remove negative time from a status.");
    if (!isInfinite()) {
      if (getDuration().compareTo(amount) <= 0) {
        stackList.clear();
        if (owner != null) owner.removeStatus(this);
      }
      else {
        for (Stack s : stackList) {
          if (s.duration.compareTo(amount) <= 0) {
            stackList.remove(s);
          }
          else {
            s.duration = s.duration.minus(amount);
          }
        }
      }
    }
  }
  
  /**
   * Decrements the stack size by the amount given. If the result decreases the
   * stack size to zero, the status informs the owner to remove the status. If
   * the status has multiple stacks of varying duration, the stacks with the
   * longest duration are removed first. Passing a negative value throws an 
   * {@link IllegalArgumentException}.
   * @param amount the amount of stacks to remove.
   */
  public final void removeStacks(int amount) {
    if (amount < 0)  throw new IllegalArgumentException("Cannnot remove "
        + "negative stacks from a status.");
    if (amount >= getStackSize()) {
      stackList.clear();
      if (owner != null) owner.removeStatus(this);
    }
    else {
      stackList.sort((a, b) -> b.duration.compareTo(a.duration));
      for (int i = 0; i < amount; i++) {
        if (--stackList.get(0).stackSize <= 0) stackList.remove(0);
      }
    }    
  }
  
  /**
   * Adds to the list of handler objects that who's methods are called during
   * appropriate state changes in the Status object.
   * @param listener object to handle state changes.
   */
  public final void addListener(StatusHandler listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listeners cannot be null");
    }
    listeners.add(listener);
  }
  
  /**
   * Removes a handler object from the list of listeners who's methods are
   * called during appropriate state changes in the Status object.
   * @param listener the object to be removed.
   * @return true if the object was successfully removed.
   */
  public final boolean removeListener(StatusHandler listener) {
    return this.listeners.remove(listener);
  }
  
  /**
   * Name of the status. Identifies the status from unrelated Status objects.
   * @return name of the Status.
   */
  public final String getName() {
    return name;
  }

  /**
   * Returns a description of the status and how it is intended to interact with
   * Fighter objects it is applied to, as well as its interactions with other
   * Status objects on the same fighter.
   * @return description of the status
   */
  public final String getDescription() {
    return description;
  }

  /**
   * The time before this status expires. Zero means the Status has an instant
   * duration and should expire as soon as it is applied. Less then zero means
   * duration is infinite.
   * @return time before the status expires.
   */
  public final Duration getDuration() {
    if (stackList.isEmpty()) return Duration.ZERO;
    Duration currentDuration = stackList.get(0).duration;
    for (Stack s : stackList) {
      if (currentDuration.compareTo(s.duration) < 0) {
        currentDuration = s.duration;
      }
    }
    return currentDuration;
  }

  /**
   * The current number of stacks.
   * @return stack size.
   */
  public final int getStackSize() {
    int currentSize = 0;
    for (Stack s : stackList) {
        currentSize += s.stackSize;
    }
    return currentSize;
  }

  /**
   * The Fighter object that the status belongs to.
   * @return the owner of the status.
   */
  public final Fighter getOwner() {
    return owner;
  }
  
  /**
   * Returns true if the status increases in stack size when equivalent Status
   * objects are added to it.
   * @return true if this is stacks.
   */
  public final boolean isStackable() {
    return stacks;
  }
  
  /**
   * Returns true if this status stuns the fighter. Stunned fighters do not
   * decrement their skill cooldowns and cannot execute skills.
   * @return true if the Status stuns the Unit it is applied to.
   */
  public final boolean isStunning() {
    return stuns;
  }

  /**
   * Returns true if the Status object defeats the fighter. Defeated Fighter
   * objects allow their team to lose a battle if all other ally fighters are
   * also defeated.
   * @return true if this defeats the fighter it is applied to.
   */
  public final boolean isDefeating() {
    return defeats;
  }
  
  /**
   * Returns true is the status should be hidden from client users.
   * @return true if this is hidden from user.
   */
  public final boolean isHidden() {
    return hidden;
  }

  /**
   * Returns {@code true} if the status should be removed as soon as it is
   * applied. This is indicated my setting the duration value to {@code ZERO}.
   * @return true if this is an instant status.
   */
  public final boolean isFinite() {
    return !isInfinite() && !isInstant();
  }
  
  /**
   * Returns {@code true} if the status should be unable to expire due to
   * passing time. This is indicated my setting the duration value to a
   * negative value.
   * @return true if this is an instant status.
   */
  public final boolean isInfinite() {
    return this.duration.isNegative();
  }
  
  /**
   * Returns {@code true} if the status should be removed as soon as it is
   * applied. This is indicated my setting the duration value to {@code ZERO}.
   * @return true if this is an instant status.
   */
  public final boolean isInstant() {
    return this.duration.isZero();
  }
  
  @Override // from TurnItem
  public final LocalDateTime getTurnTime(LocalDateTime currentTime) {
    return currentTime.plus(getDuration());
  }
  
  @Override // from TurnItem
  public final void advanceTime(Duration timeChange) {
    removeDuration(timeChange);
  }
  
  @Override // from Object
  public final int hashCode() {
    return this.name.hashCode();
  }
  
  /**
   * Returns {@code true} if the object is an instance of Status and the name
   * value of this status and the assumed status are equal. This is to ensure
   * that {@link Fighter} objects fail to apply a status with a duplicate name
   * and attempt to combine them instead.
   * @param obj the status to test equality with.
   * @return {@code true} if the given status is equivalent.
   */
  @Override // from Object
  public final boolean equals(Object obj) {
    if (!(obj instanceof Status)) return false;
    return this.name.equals(((Status)obj).getName());
  }
  
}
