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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package combat;

/**
 * A state applied to Unit objects on a Battlefield.
 * @author Andrew M. Teller (andrew.m.teller@gmail.com)
 */
public class Status {
  
  /**
   * Name of the Status. Identifies the Status from unrelated Status objects.
   * This is a required field. No default value is available.
   */
  private final String name;
  /**
   * Simple text-based explanation of the Status. This is a required field. No
   * default value is available.
   */
  private final String description;
  /**
   * The time before this status expires. Duration of the status in milliseconds.
   * Zero means no duration. Less then zero means duration is infinite. This is
   * a required field. No default value is available.
   */
  private int duration;
  /**
   * The number of stacks this status is currently up to. Statuses with a
   * duration greater then 0 do not stack in magnitude, rather they stack
   * duration. The default for this is 1.
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
   * Basic constructor.
   * @param  name name of the Status.
   * @param  description simple text-based description of the Status.
   * @param  duration time before this Status expires.
   */
  public Status(String name, String description, int duration) {
    this(name, description, duration, 1, false, false, false);
  }
  
  /**
   * Basic constructor.
   * @param  name name of the Status.
   * @param  description simple text-based description of the Status.
   * @param  duration time before this Status expires.
   * @param  stacks number of stacks this Status is currently up to.
   */
  public Status(String name, String description, int duration, int stacks) {
    this(name, description, duration, stacks, false, false, false);
  }
  
  /**
   * Basic constructor.
   * @param  name name of the Status.
   * @param  description simple text-based description of the Status.
   * @param  duration time before this Status expires.
   * @param  stuns true if the Status stuns the target.
   */
  public Status(String name, String description, int duration, boolean stuns) {
    this(name, description, duration, 1, stuns, false, false);
  }
  
  /**
   * Basic constructor.
   * @param  name name of the Status.
   * @param  description simple text-based description of the Status.
   * @param  duration time before this Status expires.
   * @param  stacks number of stacks this Status is currently up to.
   * @param  stuns true if the Status stuns the target.
   */
  public Status(String name, String description, int duration, int stacks, boolean stuns) {
    this(name, description, duration, stacks, stuns, false, false);
  }
  
  /**
   * Basic constructor.
   * @param  name name of the Status.
   * @param  description simple text-based description of the Status.
   * @param  duration time before this Status expires.
   * @param  stuns true if the Status stuns the target.
   * @param  defeats true if the Status defeats the Unit.
   */
  public Status(String name, String description, int duration, boolean stuns, boolean defeats) {
    this(name, description, duration, 1, stuns, defeats, false);
  }
  
  /**
   * Basic constructor.
   * @param  name name of the Status.
   * @param  description simple text-based description of the Status.
   * @param  duration time before this Status expires.
   * @param  stacks number of stacks this Status is currently up to.
   * @param  stuns true if the Status stuns the target.
   * @param  defeats true if the Status defeats the Unit.
   */
  public Status(String name, String description, int duration, int stacks, boolean stuns, boolean defeats) {
    this(name, description, duration, stacks, stuns, defeats, false);
  }
  
  /**
   * Basic constructor.
   * @param  name name of the Status.
   * @param  description simple text-based description of the Status.
   * @param  duration time before this Status expires. 
   * @param  stuns true if the Status stuns the target.
   * @param  defeats true if the Status defeats the Unit.
   * @param  hidden true if the Status should be hidden from the client user.
   */
  public Status(String name, String description, int duration, boolean stuns, boolean defeats, boolean hidden) {
    this(name, description, duration, 1, stuns, defeats, hidden);
  }
  
  /**
   * Basic constructor.
   * @param  name name of the Status.
   * @param  description simple text-based description of the Status.
   * @param  duration time before this Status expires. 
   * @param  stacks number of stacks this Status is currently up to.
   * @param  stuns true if the Status stuns the target.
   * @param  defeats true if the Status defeats the Unit.
   * @param  hidden true if the Status should be hidden from the client user.
   */
  public Status(String name, String description, int duration, int stacks, boolean stuns, boolean defeats, boolean hidden) {
    this.name = name;
    this.description = description;
    this.duration = duration;
    if (stacks > 0) {
      this.stacks = stacks;
    }
    else
      this.stacks = 1;
    this.stuns = stuns;
    this.defeats = defeats;
    this.hidden = hidden;
  }
  
  /**
   * Copy constructor.
   * @param copyOf is the object of Status which we make our copy from.
   */
  public Status(Status copyOf) {
    name = copyOf.getName();
    description = copyOf.getDescription();
    duration = copyOf.getDuration();
    stacks = copyOf.getStacks();
    stuns = copyOf.isStunning();
    defeats = copyOf.isDefeating();
    hidden = copyOf.isHidden();
  }
  
  /**
   * Getter for the description of the Status.
   * @return simple text-based description of the Status.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Getter for the duration of the Status.
   * @return time before this Status expires.
   */
  public int getDuration() {
    return duration;
  }

  /**
   * Getter for the name of the Status.
   * @return name of the Status.
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for the stacks of the Status.
   * @return number of stacks this Status has.
   */
  public int getStacks() {
    return stacks;
  }

  /**
   * Returns true if the Status defeats the Unit.
   * @return true if the Status defeats the Unit.
   */
  public boolean isDefeating() {
    return defeats;
  }
  
  /**
   * Returns true if the Status should be hidden from the client user.
   * @return true if the Status should be hidden from the client user.
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Returns true if this Status can have stacks (duration is not 0).
   * @return true if this Status can have stacks.
   */
  public boolean isStackable() {
    return (duration <= 0);
  }
  
  /**
   * Returns true if the Status stuns the Unit.
   * @return true if the Status stuns the Unit.
   */
  public boolean isStunning() {
    return stuns;
  }

  /**
   * Event method for when this Status is applied. This method is meant to be
   * overidden in order to have any effect.
   * @param  thisUnit the Unit the Status is being applied to.
   * @return true if the Status allows itself to be applied.
   */
  protected boolean onApply(Unit thisUnit) {
    return true;
  }

  /**
   * Event method for when this status is removed. This method is meant to be
   * overridden in order to have any effect.
   * @param  thisUnit the Unit who owns the Status.
   * @return true if the Status allows itself to be removed.
   */
  protected boolean onRemove(Unit thisUnit) {
    return true;
  }

  /**
   * Setter for the duration of the status.
   * @param duration time before this Status expires.
   */
  public void setDuration(int duration) {
    this.duration = duration;
  }
  
  
  /**
   * Setter for the stacks the unit has.
   * @param stacks number of stacks this Status is set to. Stack size cannot be
   *        0 or less.
   */
  public void setStacks(int stacks) {
    if (stacks > 0) {
      this.stacks = stacks;
    }
  }
  
  @Override public String toString() {
    return name;
  }

}
