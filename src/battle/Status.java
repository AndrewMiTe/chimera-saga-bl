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
 * A state that can be applied to {@link Fighter} objects through the execution of
 * {@link Skill} objects. All Status objects have a  name field that is used to
 * identify it. Status objects with the same name can be applied to the same
 * Fighter object so as to stack in magnitude, or to increment the duration of
 * the first status to be applied.
 * @see StatusBuilder
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class Status {
  
  /**
   * Name of the status. Identifies the status from unrelated Status objects.
   * Attempting to initiate the value as null will throw an {@link
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
   * The current number of stacks. Statuses with a duration greater then 0 does
   * not stack in magnitude, rather they increment the duration of the Status
   * first applied. Attempting to initiate the value as less then 1, or with a
   * value greater then 1 while the duration is greater then 0 will throw an
   * {@link IllegalArgumentException}.
   */
  private int stacks;
  /**
   * States whether or not this status stuns the unit. Stunned units do not
   * decrement their skill cooldowns and perform skills while true. The default
   * for this is false.
   */
  private final boolean stuns;
  /**
   * States whether or not the Status defeats the Unit. Defeated Unit objects
   * allow their Team to lose a battle if all other ally Unit object's are also
   * defeated.
   */
  private final boolean defeats;
  /**
   * States whether or not the status is visible to the user of the client. The
   * default for this is false.
   */
  private final boolean hidden;
  /**
   * List of handler objects that instantiate methods that the Status object
   * calls on during appropriate state changes in itself.
   */
  private final List<StatusHandler> handlers;
  /**
   * The Fighter object that the status belongs to. Value should remain null
   * until the status has been applied using the {@code apply} method.
   */
  private Fighter owner;
  
  /**
   * Initializes the object so that all internal field variables that can be
   * explicitly set are done so through the given parameters. See the
   * StatusBuilder class, found in the same package, in order to set the
   * parameters with default values and/or set them incrementally.
   * @param  name name of the Status.
   * @param  description A non-null String value that describes the Status.
   * @param  duration time before this Status expires. 
   * @param  stacks the current stack size.
   * @param  stuns true if the Status stuns the Unit it is applied to.
   * @param  defeats true if the Status defeats the Unit it is applied to.
   * @param  hidden true if the Status should be hidden from client users.
   */
  public Status(String name, String description, Duration duration, int stacks,
      boolean stuns, boolean defeats, boolean hidden) {
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
    if (stacks < 0) {
      throw new IllegalArgumentException("stacks cannot be negative");
    }
    if (!duration.isNegative() && !duration.isZero() && (stacks > 1)) {
      throw new IllegalArgumentException("stacks cannot be above 1 when the "
          + "duration is a positive non-zero value");
    }
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
    this.stacks = copyOf.stacks;
    this.stuns = copyOf.stuns;
    this.defeats = copyOf.defeats;
    this.hidden = copyOf.hidden;
    this.handlers = copyOf.handlers;
    this.owner = null;
  }
  
  /**
   * Adds a new StatusHandler object to the status. StatusHandler objects can
   * execute code whenever important state changes occur the the status, such as
   * when it is applied or removed from a fighter.
   * @param action
   */
  public void addStatusHandler(StatusHandler action) {
    if (action == null) {
      throw new IllegalArgumentException("StatusHandler cannot be null");
    }
    handlers.add(action);
  }
  
  /**
   * Returns a description of the status and how it is intended to interact with
   * the fighter it is applied to, as well as its interactions with other status
   * objects on the same fighter.
   * @return A non-null String value that describes the Status.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the time before the status expires. A duration of zero means that
   * the status is instant and immediately due to expire. A duration less then
   * zero indicates that the status has an infinite duration and cannot expire.
   * @return time before the status expires.
   */
  public Duration getDuration() {
    return duration;
  }

  /**
   * Returns a String value that Identifies the Status from unrelated Status
   * objects. All Status object with a value of null are considered unique and
   * each will not stack or enhance the other's duration.
   * @return name of the Status.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the reference to the Unit object that this status has most recently
   * been applied to.
   * @return the owner of the status.
   */
  public Fighter getOwner() {
    return owner;
  }
  
  /**
   * Returns a numeric value that indicates the magnitude or height of the
   * Status. This value should always be a positive value greater then zero.
   * This value can also not be above one if the duration of the Status is a
   * positive non-zero value.
   * @return stack size.
   */
  public int getStacks() {
    return stacks;
  }

  /**
   * Returns a boolean value that, if true, defeats any Unit it is applied to.
   * Defeated Unit objects allows opposing teams to be victorious within a
   * battle.
   * @return true if the Status defeats the Unit it is applied to.
   */
  public boolean isDefeating() {
    return defeats;
  }
  
  /**
   * Returns a boolean value that, if true, indicates that the Status should be
   * hidden from users of the client.
   * @return true if the Status should be hidden from client users.
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Returns a boolean value that, if true, indicates that the Status can be
   * stacked and that its stack value can be greater then one.
   * @return true if this Status is stackable.
   */
  public boolean isStackable() {
    return (duration.isNegative() || duration.isZero());
  }
  
  /**
   * Returns a boolean value that, if true, stuns the Unit it is applied to.
   * Stunned Unit objects cannot perform actions and do not decrement the
   * cooldown on their skills.
   * @return true if the Status stuns the Unit it is applied to.
   */
  public boolean isStunning() {
    return stuns;
  }

  /**
   * Event method for when this Status is applied.
   * @param  owner the Unit the Status is being applied to.
   */
  public void applyStatus(Fighter owner) {
    this.owner = owner;
    for (StatusHandler handler : handlers) {
      handler.onStatusApplication(owner);
    }
  }

  /**
   * Event method for when this status is removed.
   */
  public void removeStatus() {
    for (StatusHandler handler : handlers) {
      handler.onStatusApplication(this.owner);
    }
  }

  /**
   * Sets the time before the Status expires. If set to zero, the Status is
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
    this.stacks = stacks;
  }
  
  @Override
  public String toString() {
    return this.name;
  }
  
}
