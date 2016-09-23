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
   * @see StatusBuilder#setName(java.lang.String)
   */
  private final String name;

  /**
   * @see StatusBuilder#setDescription(java.lang.String)
   */
  private final String description;

  /**
   * @see StatusBuilder#setDuration(java.time.Duration)
   */
  private final Duration duration;

  /**
   * @see StatusBuilder#setStackSize(int)
   */
  private final int stackSize;

  /**
   * @see StatusBuilder#setStackable(boolean)
   */
  private final boolean stackable;

  /**
   * @see StatusBuilder#setStunning(boolean)
   */
  private final boolean stunning;

  /**
   * @see StatusBuilder#setDefeating(boolean)
   */
  private final boolean defeating;

  /**
   * @see StatusBuilder#setHidden(boolean)
   */
  private final boolean hidden;

  /**
   * @see StatusBuilder#setApplyCondition(java.util.function.Predicate)
   */
  private final Predicate<Fighter> applyCondition;

  /**
   * @see StatusBuilder#setRemoveCondition(java.util.function.Predicate)
   */

  private final Predicate<Fighter> removeCondition;
  
  /**
   * @see StatusBuilder#addListener(battle.StatusHandler)
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
   * @param name {@see StatusBuilder#setName}
   * @param description {@see StatusBuilder#setDescription}
   * @param duration {@see StatusBuilder#setDuration}
   * @param stackSize {@see StatusBuilder#setStackSize}
   * @param stackable {@see StatusBuilder#setStackable}
   * @param stunning {@see StatusBuilder#setStunning}
   * @param defeating {@see StatusBuilder#setDefeating}
   * @param hidden {@see StatusBuilder#setHidden}
   * @param applyCondition {@see StatusBuilder#setApplyCondition}
   * @param removeCondition {@see StatusBuilder#setRemoveCondition}
   * @param listeners {@see StatusBuilder#addListener}
   */
  protected Status(String name, String description, Duration duration,
      int stackSize, boolean stackable, boolean stunning, boolean defeating,
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
    this.stackable = stackable;
    this.stunning = stunning;
    this.defeating = defeating;
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
   * the state of either the original or the copy has no affect on the other.
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
    this.stackable = copyOf.stackable;
    this.stunning = copyOf.stunning;
    this.defeating = copyOf.defeating;
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
        (status.stackable == this.stackable) &&
        (status.defeating == this.defeating) &&
        (status.stunning == this.stunning) &&
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
   * Adds to the list of {@link StatusHandler} objects that are called during
   * appropriate events in the Status object.
   * @param listener object to handle events.
   */
  public final void addListener(StatusHandler listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listeners cannot be null");
    }
    listeners.add(listener);
  }
  
  /**
   * Removes a listener from the list of {@link StatusHandler} objects that are
   * called during appropriate events in the Status object.
   * @param listener the object to be removed.
   * @return true if the object was successfully removed.
   * @see #addListener(battle.StatusHandler)
   */
  public final boolean removeListener(StatusHandler listener) {
    return this.listeners.remove(listener);
  }
  
  /**
   * @return name of the Status.
   * @see StatusBuilder#setName
   */
  public final String getName() {
    return name;
  }

  /**
   * @return description of the status.
   * @see StatusBuilder#setDescription
   */
  public final String getDescription() {
    return description;
  }

  /**
   * @return time before the status expires.
   * @see StatusBuilder#setDuration
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
   * @return the current stack size.
   * @see StatusBuilder#setStackSize
   */
  public final int getStackSize() {
    int currentSize = 0;
    for (Stack s : stackList) {
        currentSize += s.stackSize;
    }
    return currentSize;
  }

  /**
   * @return the function that determines if status can be applied.
   * @see StatusBuilder#setApplyCondition
   */
  protected final Predicate<Fighter> getApplyConidtion() {
    return applyCondition;
  }
  
  /**
   * @return the function that determines if status can be removed.
   * @see StatusBuilder#setRemoveCondition
   */
  protected final Predicate<Fighter> getRemoveConidtion() {
    return removeCondition;
  }
  
  /**
   * @return the list of listeners.
   * @see StatusBuilder#addListener
   */
  protected final List<StatusHandler> getListeners() {
    return new ArrayList<>(listeners);
  }
 
  /**
   * The Fighter object that the status belongs to.
   * @return the owner of the status.
   */
  @Override // from TurnItem
  public final Fighter getOwner() {
    return owner;
  }
  
  /**
   * @return {@code true} if the status is stackable.
   * @see StatusBuilder#setStackable
   */
  public final boolean isStackable() {
    return stackable;
  }
  
  /**
   * @return {@code true} if the status stuns the fighter it is applied to.
   * @see StatusBuilder#setStunning
   */
  public final boolean isStunning() {
    return stunning;
  }

  /**
   * @return {@code true} if the status defeats the fighter it is applied to.
   * @see StatusBuilder#setDefeating
   */
  public final boolean isDefeating() {
    return defeating;
  }
  
  /**
   * @return {@code true} if the status should be hidden from user.
   * @see StatusBuilder#setHidden
   */
  public final boolean isHidden() {
    return hidden;
  }

  /**
   * Returns {@code true} if the status has a finite duration and does not
   * expire instantly.
   * @return {@code true} if this is a finite status.
   */
  public final boolean isFinite() {
    return !isInfinite() && !isInstant();
  }
  
  /**
   * Returns {@code true} if the status should be unable to expire due to
   * passing time.
   * @return {@code true} if this is an infinite status.
   */
  public final boolean isInfinite() {
    return this.duration.isNegative();
  }
  
  /**
   * Returns {@code true} if the status should be removed as soon as it is
   * applied.
   * @return {@code true} if this is an instant status.
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
