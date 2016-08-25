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

package battle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Participant in battles that take place on BattleFields.
 * @author Andrew M. Teller (andrew.m.teller@gmail.com)
 */
public class Unit implements Subscriber {
  
  /**
   * The name of the Unit.
   */
  private final String name;
  /**
   * The Row this Unit starts battles in.
   */
  private Row battleRow;
  /**
   * The list of all the Skill objects the Unit can execute in battle.
   */
  private final Collection<Skill> skillList;
  /**
   * The list of all the Status objects the Unit has.
   */
  private final Collection<Status> statusList;
  /** 
   * The list of TurnItem objects the Unit has inserted into the TurnOrder.
   */
  private final Collection<TurnItem> turnItems;
  /**
   * The TurnOrder that the Unit subscribes to for updates and that the Unit
   * submits its TurnItems to.
   */
  private TurnOrder turnOrder;
  /**
   * The time value representing the last time the Unit received a request to
   * update.
   */
  private int lastUpdated;

  /**
   * Basic constructor.
   * @param name name of the Unit.
   */
  public Unit(String name) {
    this.name = name;
    this.battleRow = Row.FRONT;
    this.skillList = new ArrayList<>();
    this.statusList = new ArrayList<>();
    this.turnItems = new ArrayList<>();
    this.lastUpdated = 0;
  }

  /**
   * Adds a new Skill to the Unit object's list of Skill objects.
   * @param  newSkill Skill to be added to the Unit.
   */
  public void addSkill(Skill newSkill) {
    skillList.add(newSkill);
  }

  /**
   * Adds a new status to the unit's list of Status objects.
   * @param  newStatus Status to be added to the Unit.
   * @return true if the addition was successful.
   */
  public boolean addStatus(Status newStatus) {
    //Check to see if the Status is not null and succeeds to apply itself.
    if ((newStatus != null) && newStatus.onApply(this)) {
      //Store the Unit object's stun duration.
      int oldStunDuration = getStunDuration();
      //Check to make sure the Status has a non-zero duration value.
      if (newStatus.getDuration() != 0) {
        //Search this Unit for a matching Status and store it.
        Status match = getStatus(newStatus.getName());
        //Check to see if a match was found.
        if (match != null) {
          if (match.isStackable() && newStatus.isStackable()) {
            //That new Status stacks with the old, combine the stacks.
            match.setStacks(match.getStacks() + newStatus.getStacks());
          }
          //Otherwise make sure both Status objects have a positive value.
          else if ((match.getDuration() > 0) && (newStatus.getDuration() > 0)) {
            //Calculate the new duration by combining the old ones.
            int newDuration = match.getDuration() + newStatus.getDuration();
            //Modify the old Status with the new duration.
            match.setDuration(newDuration);
            //Add a new TurnItem to the TurnOrder for when the Status ends.
            turnItems.add(turnOrder.addTurnItem(this, newDuration, false));
          }
        }
        else {
          //The Status is unique to the Unit, add it.
          statusList.add(newStatus);
          //Check to see if the Status has a finite duration.
          if (newStatus.getDuration() > 0) {
            //Add a new TurnItem to the TurnOrder for when the Status ends.
            turnItems.add(turnOrder.addTurnItem(this, newStatus.getDuration(), false));
          }
        }
      }
      //Get the current stun duration and store it.
      int stunDurationChange = getStunDuration() - oldStunDuration;
      //Check to see if the stun duration has changed.
      if (stunDurationChange != 0) {
        //Add the change to stun duration to all stunnable TurnItem objects.
        for (TurnItem nextItem : turnItems) {
          if (nextItem.isStunnable()) {
            nextItem.setTime(nextItem.getTime() + stunDurationChange);
          }
        }
        //Add the change to stun duration to all stunnable Skills objects.
        for (Skill nextSkill : skillList) {
          if (nextSkill.getMaxCooldown() > 0) {
            nextSkill.setCooldown(nextSkill.getCooldown() + stunDurationChange);
          }
        }
      }    
      //Applied successfully, return true.
      return true;
    }
    //Application failed, return false.
    return false;
  }


  /**
   * Clears all battle related values.
   */
  protected void clearBattleState() {
    //Remove any Status objects.
    clearStatus();
    //Check to make sure the TurnOrder is not null.
    if (turnOrder != null) {
      //Remove all TurnItem objects from the TrunOrder and unsubscribe.
      turnOrder.removeUnit(this);
      turnOrder.unsubscribe(this);
      //Set the TurnOrder to null.
      turnOrder = null;
    }
    //Remove all TurnItem objects from this Unit.
    turnItems.removeAll(turnItems);
    //Reset all cooldowns on skills.
    for (Skill nextSkill : skillList) {
      nextSkill.setCooldown(nextSkill.getMaxCooldown());
    }
  }

  /**
   * Removes all Status objects without calling the onRemove events.
   */
  public void clearStatus() {
    statusList.removeAll(statusList);
  }

  /**
   * Getter for what place within a team this unit prefers to reside.
   * @return Row value that the Unit starts battle in.
   */
  public Row getStartingRow() {
    return battleRow;
  }

  /**
   * Getter for the name of the unit.
   * @return name of the unit.
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for all the skills of the unit in the form of an iterator.
   * @return iterator of the Unit object's skills.
   */
  public Iterator<Skill> getSkills() {
    return skillList.iterator();
  }
  
  /**
   * Returns a Status object with the matching Status value as the given Status.
   * @param  status the object who's value is being match to a Status owned by
   *         the Unit.
   * @return Status object with the matching Status value as the given Status.
   *         Returns null if no match was found.
   */
  public Status getStatus(Status status) {
    return getStatus(status.getName());
  }
  
  /**
   * Checks to see if the unit has a status who's type matches the type passed
   * by the caller and returns the first match.
   * @param  statusName the enumerated value of a Status being searched for.
   * @return the Status object matching value given. Returns null if no match
   *         was found.
   */
  public Status getStatus(String statusName) {
    //Iterate through all of this unit'nextStatus statuses.
    Iterator<Status> iterateStatus = getStatuses();
    while (iterateStatus.hasNext()) {
      Status nextStatus = iterateStatus.next();
      //Stop iteration if you find a match and return the Status object.
      if (nextStatus.getName().equals(statusName)) {
        return nextStatus;
      }
    }
    //Return null if the search failed to find a match.
    return null;
  }
  
  /**
   * Getter for all Status objects possessed by the Unit returned as an
   * iterator.
   * @return iterator of the Unit object's Status objects.
   */
  public Iterator<Status> getStatuses() {
    return statusList.iterator();
  }

  /**
   * Returns true if the Unit has a Status object with the matching
   * StatusLibrary value as the status given.
   * @param  status the object who's value matches the Status searched for.
   * @return true if a match was found.
   */
  public boolean hasStatus(Status status) {
    if (status != null) {
      return getStatus(status.getName()) != null;
    }
    return false;
  }

  /**
   * Returns true if the Unit has a Status object with the matching given
   * StatusLibrary value.
   * @param  statusName the value that matches the Status being searched for.
   * @return true if a match was found.
   */
  public boolean hasStatus(String statusName) {
    return getStatus(statusName) != null;
  }

  /**
   * Returns true when the Unit has a Status that would remove it from combat.
   * @return true when the Unit is removed from combat.
   */
  public boolean isDefeated() {
    //Iterate through all the Status objects.
    for (Status nextStatus : statusList) {
      //If the Status defeats, return true.
      if (nextStatus.isDefeating()) {
        return true;
      }
    }
    //Unit is not defeated, return false.
    return false;
  }

  /**
   * Returns true when the Unit has a Status that stuns it.
   * @return true when the Unit is stunned.
   */
  public boolean isStunned() {
    //Iterate through all the Status objects.
    for (Status nextStatus : statusList) {
      //If the Status stuns, return true.
      if (nextStatus.isStunning()) {
        return true;
      }
    }
    //Unit is not stunned, return false.
    return false;
  }
  
  /**
   * Iterates through the list of Status object's and returns the duration of
   * the longest Status that stuns.
   * @return duration the Unit is stunned.
   */
  private int getStunDuration() {
    //Assume the stun duration to be zero unless proven otherwise.
    int stunTime = 0;
    //Iterate through all the statuses for this unit.
    for (Status nextStatus : statusList) {
      //If this status stuns, set the stun time to the statuses duration if it
      //is higher then the current stun time.
      if (nextStatus.isStunning()) {
        if (stunTime < nextStatus.getDuration()) {
          stunTime = nextStatus.getDuration();
        }
      }
    }
    //Return to caller the truth.
    return stunTime;
  }

  /**
   * Returns the next Skill in the priority that is off its cooldown, or null if
   * their is no Skill that is ready.
   * @return Skill object ready for execution. Returns null if there is no Skill
   *         available.
   */
  protected Skill nextSkill() {
    //If the unit it stunned, no skills are to be used.
    if (!isStunned()) {
      //Iterate through all the skills of this unit.
      Iterator<Skill> iterateSkills = skillList.iterator();
      while (iterateSkills.hasNext()) {
        Skill nextSkill = iterateSkills.next();
        //Check to see if Skill is not a pre-battle Skill and is off cooldown.
        if ((nextSkill.getMaxCooldown() > 0) && (nextSkill.getCooldown() <= 0)) {
          //Return this Skill.
          return nextSkill;
        }
      }
    }
    //No Skill ready, return null.
    return null;
  }

  /**
   * Removes a Status object from the list of Status objects applied to this
   * character. The Status given in the search must be a precise reference to
   * the object being removed.
   * @param  oldStatus object being searched for and removed from the Unit.
   * @return true if the Status was successfully removed.
   */
  protected boolean removeStatus(Status oldStatus) {
    //Check that the status belong to this unit and that removal is successful.
    if (statusList.contains(oldStatus) && oldStatus.onRemove(this)) {
      //Return final success of removal.
      return statusList.remove(oldStatus);
    }
    //Removal failed, return false.
    return false;
  }

  /**
   * Removes a Status object from the list of Status objects applied to this
   * character. The Status given in the search must be a precise reference to
   * the object being removed.
   * @param  oldStatus object being searched for and removed from the Unit.
   * @return true if the Status was successfully removed.
   */
  public boolean removeStatus(String oldStatus) {
    Status match = getStatus(oldStatus);
    //Check that the status belong to this unit and that removal is successful.
    if ((match != null) && match.onRemove(this)) {
      //Return final success of removal.
      return statusList.remove(match);
    }
    //Removal failed, return false.
    return false;
  }

  /**
   * Removes a specified number of stacks of a Status object from the list of
   * Status objects applied to this Unit. The Status object is identified by
   * the enumerated StatusLibrary value passed by the caller. The onRemove event
   * is triggered before stack decrement. A failed removal results in no changes
   * to the statuses state.
   * @param  oldStatus enumerated value of the Status being searched for.
   * @param  stacks number of stacks to remove from any Status matching the
   *         Status value being searched for.
   * @return true if the status was successfully removed.
   */
  public boolean removeStatus(String oldStatus, int stacks) {
    //Search for matching Status object.
    Status match = getStatus(oldStatus);
    //Continue if the parameters are valid and if the Status is removable.
    if ((stacks > 0) && (match != null) && match.onRemove(this)) {
      //Remove status if it no longer has positive stacks.
      if (match.getStacks() <= stacks) {
        statusList.remove(match);
      }
      else {
        //Decrement that statuses stack size.
        match.setStacks(match.getStacks() - stacks);
      }
      //Return successful operation.
      return true;
    }
    //Return failed operation.
    return false;
  }

  /**
   * Removes a Skill from the Unit and returns true if the removal is
   * successful.
   * @param  oldSkill reference to the skill being removed.
   * @return true if removal of the skill is successful.
   */
  public boolean removeSkill(Skill oldSkill) {
    //Remove and return the result of the removals success.
    return skillList.remove(oldSkill);
  }
  
  /**
   * Removes a point in time of a battle. This should be called whenever this
   * time point has been passed, or the battle is over.
   * @param  oldItem TurnItem to be searched for and removed.
   */
  protected void removeTurnItem(TurnItem oldItem) {
    turnItems.remove(oldItem);
  }

  /**
   * Resets the cooldown on the given Skill and places the Unit into the
   * TurnOrder at the time the Skill goes off cooldown again. Returns false if
   * the Unit does not possess the given skill.
   * @param skill Skill to be searched for and reset.
   * @return true if the Skill belongs to the Unit.
   */
  protected boolean resetSkill(Skill skill) {
    //Check that the skill is not null and that it belongs to this Unit.
    if ((skill != null) && (skillList.contains(skill))) {
      //Reset the cooldown and place the Unit into the TurnOrder.
      skill.setCooldown(skill.getMaxCooldown());
      turnItems.add(turnOrder.addTurnItem(this, turnOrder.getClock() + skill.getCooldown(), true));
      //Skill reset successful, return true.
      return true;
    }
    //Skill not null or not found, return false.
    return false;
  }
  
  /**
   * Informs the Unit that it is in a new battle. All previous battle states are
   * lost and the Unit is given new information in order to submit its own
   * TurnItem objects.
   * @param  turnOrder the object that the Unit uses to submit its TurnItems to.
   */
  protected void setBattleState(TurnOrder turnOrder) {
    //Be sure to remove the Unit from any privious battle.
    clearBattleState();
    //Store the TurnOrder for this Unit to submit TurnItems to and subscribe.
    this.turnOrder = turnOrder;
    this.turnOrder.subscribe(this);
    //Iterate through all of the Skill objects.
    for (Skill nextSkill : skillList) {
      if (nextSkill.getCooldown() > 0) {
        //Add the cooldown of all eligiable Skill objects to the TurnOrder.
        turnItems.add(turnOrder.addTurnItem(this, nextSkill.getCooldown(), true));
      }
    }
    lastUpdated = turnOrder.getClock();
  }

  /**
   * Setter for what Row within a Team this Unit starts a battle in.
   * @param  battleRow Row value Unit will start a battle in.
   */
  public void setStartingRow(Row battleRow) {
    this.battleRow = battleRow;
  }

  @Override public String toString() {
    return name;
  }
  
  @Override public void update() {
    int timeChange = turnOrder.getClock() - lastUpdated;
    lastUpdated = turnOrder.getClock();
    //Iterates through all of the Unit object's statuses.
    Iterator<Status> iterateStatuses = statusList.iterator();
    while (iterateStatuses.hasNext()) {
      Status nextStatus = iterateStatuses.next();
      //Check for statuses with a finite duration.
      if (nextStatus.getDuration() >= 0) {
        //Decrement the duration by the incremented time parameter.
        nextStatus.setDuration(nextStatus.getDuration() - timeChange);
        if (nextStatus.getDuration() <= 0) {
          //Triggers the onRemove event for this status.
          if (nextStatus.onRemove(this)) {
            //If removal is successful, remove the status from the Unit.
            iterateStatuses.remove();
          }
          else {
            //If removal failed, set the status duration to zero (might
            //currently be negative), marking it for removal in the future.
            nextStatus.setDuration(0);
          }
        }
      }
    }
    Iterator<Skill> iterateSkills = skillList.iterator();
    while (iterateSkills.hasNext()) {
      Skill s = iterateSkills.next();
      //Decrement the cooldown by the incremented time parameter.
      s.setCooldown(s.getCooldown() - timeChange);
    }
  }

}