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
import java.util.Iterator;
import java.util.List;

/**
 * Participant in battles that take place on BattleFields.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
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
  private final List<Skill> skillList;
  /**
   * The list of all the Status objects the Unit has.
   */
  private final List<Status> statusList;
  /** 
   * The list of TurnItem objects the Unit has inserted into the TurnOrder.
   */
  private final List<TurnItem> turnItems;
  /**
   * The TurnOrder that the Unit subscribes to for updates and that the Unit
   * submits its TurnItems to.
   */
  private TurnOrder turnOrder;
  /**
   * The time value representing the last time the Unit received a request to
   * update.
   */
  private LocalDateTime lastUpdated;

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
    this.lastUpdated = LocalDateTime.now();
  }

  /**
   * Initializes a deep copy of the given Unit such that changes to the state of
   * the copy have no affect on the original, and vica versa.
   * @param copyOf object which the copy is made from.
*/
  public Unit(Unit copyOf) {
    this.name = copyOf.name;
    this.battleRow = copyOf.battleRow;
    this.skillList = new ArrayList<>(copyOf.skillList.size());
    for (Skill next : copyOf.skillList) {
      this.skillList.add(new Skill(next));
    }
    this.statusList = new ArrayList<>(copyOf.statusList.size());
    for (Status next : copyOf.statusList) {
      this.statusList.add(new Status(next));
    }
    this.turnItems = new ArrayList<>(copyOf.turnItems.size());
    for (TurnItem next : copyOf.turnItems) {
      this.turnItems.add(new TurnItem(next));
    }
    this.lastUpdated = copyOf.lastUpdated;
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
    if ((newStatus != null) && newStatus.onApply(this)) {
      Duration oldStunDuration = getStunDuration();
      if (!newStatus.getDuration().isZero()) {
        Status match = getStatus(newStatus.getName());
        if (match != null) {
          if (match.isStackable() && newStatus.isStackable()) {
            match.setStacks(match.getStacks() + newStatus.getStacks());
          }
          else if (!match.isStackable() && !newStatus.isStackable()) {
            //int newDuration = match.getDuration() + newStatus.getDuration();
            Duration newDuration = match.getDuration().plus(newStatus.getDuration());
            match.setDuration(newDuration);
            turnItems.add(turnOrder.addTurnItem(this, newDuration, false));
          }
        }
        else {
          statusList.add(newStatus);
          if (!newStatus.isStackable()) {
            turnItems.add(turnOrder.addTurnItem(this, newStatus.getDuration(), false));
          }
        }
      }
      Duration stunDurationChange = getStunDuration().minus(oldStunDuration);
      if (!stunDurationChange.isZero()) {
        for (TurnItem nextItem : turnItems) {
          if (nextItem.isStunnable()) {
            nextItem.setTime(nextItem.getTime().plus(stunDurationChange));
          }
        }
        for (Skill nextSkill : skillList) {
          if (!nextSkill.getMaxCooldown().isNegative() &&
              !nextSkill.getMaxCooldown().isZero()) {
            nextSkill.setCooldown(nextSkill.getCooldown().plus(stunDurationChange));
          }
        }
      }    
      return true;
    }
    return false;
  }


  /**
   * Clears all battle related values.
   */
  protected void clearBattleState() {
    clearStatus();
    if (turnOrder != null) {
      turnOrder.removeUnit(this);
      turnOrder.unsubscribe(this);
      turnOrder = null;
    }
    turnItems.removeAll(turnItems);
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
   * Returns a reference to a {@link Status} object with a matching name to the
   * given {@link String} object.
   * @param  statusName String value representing the name value of a {@link
   *         Status} object.
   * @return the {@link Status} object matching value given. Returns null if no
   *         match was found.
   */
  public Status getStatus(String statusName) {
    Iterator<Status> iterateStatus = getStatuses();
    while (iterateStatus.hasNext()) {
      Status nextStatus = iterateStatus.next();
      if (nextStatus.getName().equals(statusName)) {
        return nextStatus;
      }
    }
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
   * Returns {@code true} if the Unit has a {@link Status} object with a
   * matching name value as the given Status object.
   * @param  status Status object with the name value being searched for.
   * @return true if a match was found.
   */
  public boolean hasStatus(Status status) {
    return getStatus(status.getName()) != null;
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
    for (Status nextStatus : statusList) {
      if (nextStatus.isDefeating()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true when the Unit has a Status that stuns it.
   * @return true when the Unit is stunned.
   */
  public boolean isStunned() {
    for (Status nextStatus : statusList) {
      if (nextStatus.isStunning()) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Iterates through the list of Status object's and returns the duration of
   * the longest Status that stuns.
   * @return duration the Unit is stunned.
   */
  private Duration getStunDuration() {
    Duration stunTime = Duration.ZERO;
    for (Status nextStatus : statusList) {
      if (nextStatus.isStunning()) {
        if (stunTime.compareTo(nextStatus.getDuration()) < 0) {
          stunTime = nextStatus.getDuration();
        }
      }
    }
    return stunTime;
  }

  /**
   * Returns the next Skill in the priority that is off its cooldown, or null if
   * their is no Skill that is ready.
   * @return Skill object ready for execution. Returns null if there is no Skill
   *         available.
   */
  protected Skill nextSkill() {
    if (!isStunned()) {
      Iterator<Skill> iterateSkills = skillList.iterator();
      while (iterateSkills.hasNext()) {
        Skill nextSkill = iterateSkills.next();
        if ((!nextSkill.getMaxCooldown().isNegative() &&
            !nextSkill.getMaxCooldown().isZero()) && 
            (nextSkill.getCooldown().isNegative() ||
            nextSkill.getCooldown().isZero())) {
          return nextSkill;
        }
      }
    }
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
    if (statusList.contains(oldStatus) && oldStatus.onRemove(this)) {
      return statusList.remove(oldStatus);
    }
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
    if ((match != null) && match.onRemove(this)) {
      return statusList.remove(match);
    }
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
    Status match = getStatus(oldStatus);
    if ((stacks > 0) && (match != null) && match.onRemove(this)) {
      if (match.getStacks() <= stacks) {
        statusList.remove(match);
      }
      else {
        match.setStacks(match.getStacks() - stacks);
      }
      return true;
    }
    return false;
  }

  /**
   * Removes a Skill from the Unit and returns true if the removal is
   * successful.
   * @param  oldSkill reference to the skill being removed.
   * @return true if removal of the skill is successful.
   */
  public boolean removeSkill(Skill oldSkill) {
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
    if ((skill != null) && (skillList.contains(skill))) {
      skill.setCooldown(skill.getMaxCooldown());
      turnItems.add(turnOrder.addTurnItem(this, skill.getCooldown(), true));
      return true;
    }
    return false;
  }
  
  /**
   * Informs the Unit that it is in a new battle. All previous battle states are
   * lost and the Unit is given new information in order to submit its own
   * TurnItem objects.
   * @param  turnOrder the object that the Unit uses to submit its TurnItems to.
   */
  protected void setBattleState(TurnOrder turnOrder) {
    clearBattleState();
    this.turnOrder = turnOrder;
    this.turnOrder.subscribe(this);
    for (Skill nextSkill : skillList) {
      if (!nextSkill.getCooldown().isNegative() &&
          !nextSkill.getCooldown().isZero()) {
        turnItems.add(turnOrder.addTurnItem(this, nextSkill.getCooldown(), true));
      }
    }
    lastUpdated = turnOrder.getCurrentTime();
  }

  /**
   * Setter for what Row within a Team this Unit starts a battle in.
   * @param  battleRow Row value Unit will start a battle in.
   */
  public void setStartingRow(Row battleRow) {
    this.battleRow = battleRow;
  }
  
  @Override
  public String toString() {
    return this.name;
  }
  
  @Override
  public void update() {
    Duration timeChange = Duration.between(lastUpdated, turnOrder.getCurrentTime());
    lastUpdated = turnOrder.getCurrentTime();
    Iterator<Status> iterateStatuses = statusList.iterator();
    while (iterateStatuses.hasNext()) {
      Status nextStatus = iterateStatuses.next();
      if (!nextStatus.getDuration().isNegative()) {
        nextStatus.setDuration(nextStatus.getDuration().minus(timeChange));
        if (nextStatus.isStackable()) {
          if (nextStatus.onRemove(this)) {
            iterateStatuses.remove();
          }
          else {
            nextStatus.setDuration(Duration.ZERO);
          }
        }
      }
    }
    Iterator<Skill> iterateSkills = skillList.iterator();
    while (iterateSkills.hasNext()) {
      Skill s = iterateSkills.next();
      s.setCooldown(s.getCooldown().minus(timeChange));
    }
  }

}
