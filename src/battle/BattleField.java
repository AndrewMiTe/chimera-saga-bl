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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javafx.scene.layout.Pane;

//TODO Reorder the time component to use a date and time class rather then an integer.
//TODO Allow battlefield more flexibility in how Units are spaced.
 /**
 * Takes the units of a battle and orders them. The class allows the calling
 * object to obtain the battling units as a whole, as a team, or as a set of
 * units that match a target when given the targeting unit.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class BattleField {

  /**
   * The fixed y-axis distance between the various rows of the Battlefield.
   */
  protected static final int ROW_DISTANCE = 9;
  
  /**
   * The fixed x-axis distance between units in the same Row.
   */
  protected static final int COL_DISTANCE = 4;
  
  /**
   * The width of the Row where units can be randomly placed along its y-axis.
   */
  protected static final int ROW_WIDTH = 4;
  
  /** 
   * The number of extra Location objects a Row can have.
   */
  protected static final int EXTRA_POSITIONS = 3;
  
  /**
   * The distance that determines if a Unit is close to another Unit.
   */
  protected static final int CLOSE_DISTANCE = 8;
  
  /**
   * The list of all Location objects on the Battlefield currently.
   */
  private final ArrayList<Location> locationList;
  
  /**
   * A matrix that tracks the next available positions for a given Team and Row.
   */
  private final int[][] nextPosition;

  /**
   * The display pane for the results of the battle.
   */
  private final BattleLog battleLog;

  /**
   * Keeps track of which units have a turn and when.
   */
  private final TurnOrder turnOrder;
    
  /**
   * Basic constructor.
   */
  public BattleField() {
    locationList = new ArrayList<>();
    nextPosition = new int[Team.values().length][Row.values().length];
    battleLog = new BattleLog();
    turnOrder = new TurnOrder();
  }
  
  /**
   * Adds a Unit to the BattleField. Unit objects trigger any pre-battle Skill
   * they own and a STAGGERED Status is applied to it.
   * @param  team identifies which Team to assign the Unit to.
   * @param  newUnit Unit object to be added to the BattleField.
   * @return true if addition successful.
   */
  public boolean addUnit(Unit newUnit, Team team) {
    if ((newUnit != null) && (team != null)) {
      Row row = newUnit.getStartingRow();
      ArrayList<Location> openings = new ArrayList<>();
      for (Location locale : locationList) {
        if ((locale.getTeam() == team) && 
            (locale.getRow() == row) && 
            (locale.getUnit() == null)) {
          openings.add(locale);
        }
      }
      while (openings.size() <= EXTRA_POSITIONS) {
        Location newLocation = new Location(team, row, nextPosition(team, row));
        openings.add(newLocation);
        locationList.add(newLocation);
      }
      openings.get((int)(Math.random() * EXTRA_POSITIONS)).setUnit(newUnit);
      newUnit.setBattleState(turnOrder);
      executePreBattleSkills(newUnit);
      return true;
    }
    return false;
  }
  
  /**
   * Iterates through the Unit object's Skill objects and executes any that
   * qualify as a pre-battle Skill. Only Skill objects with a maximum cooldown
   * value less then zero and has a Target value of SELF qualify. All
   * requirement values for the Skill are ignored. Skill objects contained by
   * the primary Skill are not executed.
   * @param  unit the Unit who is executing the pre-battle Skill objects.
   */
  private void executePreBattleSkills(Unit unit) {
    if (unit != null) {
      Iterator<Skill> skills = unit.getSkills();
      while (skills.hasNext()) {
        Skill s = skills.next();
        if ((s.getMaxCooldown() < 0) && (s.getTarget() == Target.SELF)) {
          for (Iterator<Status> actions = s.getActions(); actions.hasNext();) {
            unit.addStatus(new Status(actions.next()));
          }
        }
      }
    }
  }

  /**
   * Attempts to execute a Skill cast by the given Unit. Returns true if the
   * Skill or any sub-Skill was successfully executed. The order in which the
   * given skill is executed is as such:<br><ol>
   * <li>The BattleField is checked for any valid targets for the Skill passed to
   *    the method. Valid targets match the enumerated Target value of the Skill
   *    object and have all of the matching Status objects found in the Skill
   *    object's requirements. If no targets are found, then the Skill fails and
   *    this method returns false.</li>
   * <li>All Skill objects found in the list of sub-skills is executed
   *    recursively using this method. If all calls return false, then the Skill
   *    fails and this method returns false. If there are no sub-skills, the
   *    passed Skill succeeds and applies its actions to all or one of the valid
   *    targets, depending on the enumerated Target value of the Skill. This
   *    method would then return true.</li>
   * <li>If any but not all sub-skill executions return true, this Skill fails to
   *    apply its actions but returns true. If all sub-skill executions return
   *    true, this Skill succeeds and applies its actions to all or one of the
   *    valid targets, depending on the enumerated Target value of the Skill. 
   *    This method would then return true. </li></ol>
   * Note that valid targets that meet the requirements are checked both before
   * and after sub-Skills are executed. It is possible to create a Skill that
   * can never apply its actions. This will be so if your sub-Skills apply
   * Status objects that remove any of the Status objects that the primary skill
   * requires, or if it applies a Status that defeats.
   * @param  unit the assumed Unit to be targeting the Skill
   * @param  skill the Skill attempting to be executed.
   * @return true if the Skill or any sub-Skill successfully executed.
   */
  private boolean executeSkill(Unit unit, Skill skill) {  
    if ((unit != null) && (skill != null) && (skill.getMaxCooldown() >= 0)){
      // 1. Checking for valid targets w/ required Status objects applied.
      Iterator<Unit> targets = getTarget(unit, skill.getTarget());
      if (!targets.hasNext()) {
        return false;
      }
      boolean match = false;
      while (targets.hasNext() && !match) {
        Unit nextUnit = targets.next();
        Iterator<String> requirements = skill.getRequires();
        match = true;
        while(requirements.hasNext() && match) {
          String requirement = requirements.next();
          if (nextUnit.getStatus(requirement) == null) {
            match = false;
          }
        }
      }
      if (!match) {
        return false;
      }
      // 2. Execute sub-Skills and track success.
      boolean partialSuccess = false;
      boolean completeSuccess = true;
      Iterator<Skill> subSkillList = skill.getSubSkills();
      while (subSkillList.hasNext()) {
        Skill subSkill = subSkillList.next();
        boolean subSkillSuccess = executeSkill(unit, subSkill);
        partialSuccess = partialSuccess || subSkillSuccess;
        completeSuccess = completeSuccess && subSkillSuccess;
      }
      // 3. Check again for valid targets and apply actions.
      if (completeSuccess) {
        targets = getTarget(unit, skill.getTarget());
        while (targets.hasNext()) {
          Unit nextUnit = targets.next();
          Iterator<String> requirements = skill.getRequires();
          match = true;
          while(requirements.hasNext() && match) {
            String requirement = requirements.next();
            if (nextUnit.getStatus(requirement) == null) {
              match = false;
            }
          }
          if (match) {
            turnOrder.addTurnItem(nextUnit, 0, true);
            Iterator<Status> actions = skill.getActions();
            while (actions.hasNext()) {
              nextUnit.addStatus(actions.next());
            }
            if ((skill.getTarget() != Target.ALL_ALLIES) ||
                (skill.getTarget() != Target.ALL_ENEMIES) ||
                (skill.getTarget() != Target.All_OTHER_ALLIES)) {
              return true;
            }
          }
        }
      }
      return partialSuccess || completeSuccess;
    }
    return false;
  }
  
  /**
   * Returns the Location wrapper class that contains the given Unit object.
   * @param  unit Unit object that the caller is searching for.
   * @return Location object containing the Unit searched for or null if search
   *         fails.
   */
  private Location findUnit(Unit unit) {
    if (unit != null) {
      for (Location next : locationList) {
        if (next.getUnit() == unit) {
          return next;
        }
      }
    }
    return null;
  }
  
  /**
   * Returns all Unit objects in the BattleField as an Iterator object. Removing
   * Unit objects using calls to the Iterator do not affect the internal
   * workings of the BattleField.
   * @return Iterator containing all Unit objects on the Battlefield.
   */
  public Iterator<Unit> getAllUnits() {
    ArrayList<Unit> allUnits = new ArrayList<>();
    for (Location location : locationList) {
      if (location.getUnit() != null) {
        allUnits.add(location.getUnit());
      }
    }
    return allUnits.iterator();
  }
  
  /**
   * Getter for the Pane object that contains a display for all events and
   * state changes that have taken place on the BattleField.
   * @return Pane object with the display for the user.
   */
  public Pane getBattleLog() {
    return battleLog;
  }
  
  /**
   * Determines the Location object's exact coordinates on the BattleField
   * based on their Team, Row, and next.
   * @param  team the side of battle a Unit is on.
   * @param  row the forward to back position the Unit it in on a Team.
   * @param  position the place within a row that the Unit is in.
   * @return a Point object that wraps the x and y coordinate of the Location.
   */
  public static Point getCoordinates(Team team, Row row, int position) {
    int xPosition = position * COL_DISTANCE;
    int yPosition = (int)(Math.random() * ROW_WIDTH);
    yPosition += ROW_DISTANCE / 2;
    if (row == Row.BACK) yPosition += ROW_WIDTH + ROW_DISTANCE;
    if (team == Team.GOLD) yPosition *= -1;
    return new Point(xPosition, yPosition);
  }
  
  /**
   * Returns an x and y coordinate of where exactly the Unit is upon the battle-
   * field.
   * @param  unit object that the method is looking for. Uses reference equality.
   * @return Point object with the x and y coordinate of the Unit. Is null if
   *         the Unit is not found.
   */
  public Point getCoordinates(Unit unit) {
    Location match = findUnit(unit);
    if (match != null) {
      return match.getCoordinates();
    }
    return null;
  }
  
  /**
   * Returns an Iterator of all Unit objects that are valid targets of the
   * Target type passed by the caller. Removing Unit objects using calls to the
   * Iterator do not affect the internal workings of the BattleField.
   * @param  unit object that is performing the targeting.
   * @param  target specifies the targets the Unit is looking for.
   * @return Iterator of all Unit objects that are valid targets.
   */
  private Iterator<Unit> getTarget(Unit unit, Target target) {
    Location match = findUnit(unit);
    if ((match != null) && (target != null)) {
      locationList.sort(match);
      switch (target) {
        case SELF: {
          if (unit.isDefeated()) return null;
            return (Iterator<Unit>)Arrays.asList(unit).iterator();
        }
        case CLOSE_ALLY: {
          ArrayList<Location> builderList = new ArrayList<>();
          ArrayList<Unit> returnList = new ArrayList<>();
          double shortestDistance = -1;
          for (Location next : locationList) {
            if ((next.getUnit() != null) &&
                (next.getUnit() != unit) &&
                (!next.getUnit().isDefeated()) &&
                (next.getTeam() == match.getTeam())) {
              builderList.add(next);
              double distance = Location.getDistance(next, match);
              if (distance < shortestDistance || shortestDistance < 0) {
                shortestDistance = distance;
              }
            }
          }
          builderList.sort(match);
          for (Location next : builderList) {
            double distance = Location.getDistance(next, match);
            if (distance <= (shortestDistance + CLOSE_DISTANCE)) {
              returnList.add(next.getUnit());
            }
          }
          return returnList.iterator();
        }
        case ANY_ALLY: case ALL_ALLIES: {
          return getTargetTeam(match.getTeam()).iterator();
        }
        case ANY_OTHER_ALLY: case All_OTHER_ALLIES: {
          ArrayList<Unit> returnList = getTargetTeam(match.getTeam());
          returnList.remove(unit);
          return returnList.iterator();
        }
        case CLOSE_ENEMY: {
          ArrayList<Location> builderList = new ArrayList<>();
          ArrayList<Unit> returnList = new ArrayList<>();
          double shortestDistance = -1;
          for (Location next : locationList) {
            if ((next.getUnit() != null) &&
                (!next.getUnit().isDefeated()) &&
                (((match.getTeam() == Team.SILVER) && 
                (next.getTeam() == Team.GOLD)) || 
                ((match.getTeam() == Team.GOLD) && 
                (next.getTeam() == Team.SILVER)))) {
              builderList.add(next);
              double distance = Location.getDistance(next, match);
              if (distance < shortestDistance || shortestDistance < 0) {
                shortestDistance = distance;
              }
            }
          }
          builderList.sort(match);
          for (Location next : builderList) {
            double distance = Location.getDistance(next, match);
            if (distance <= (shortestDistance + CLOSE_DISTANCE)) {
              returnList.add(next.getUnit());
            }
          }
          return returnList.iterator();
        }
        case ANY_ENEMY: case ALL_ENEMIES: {
          if (match.getTeam() == Team.GOLD) {
            return getTargetTeam(Team.SILVER).iterator();
          }
          else if (match.getTeam() == Team.SILVER) {
            return getTargetTeam(Team.GOLD).iterator();
          }
        }
        case ANYONE: case EVERYONE: {
          ArrayList<Unit> allUnits = new ArrayList<>();
          for (Location location : locationList) {
            if ((location.getUnit() != null) && 
                (!location.getUnit().isDefeated())) {
              allUnits.add(location.getUnit());
            }
          }
          return allUnits.iterator();
        }
        case ANYONE_ELSE: case EVERYONE_ELSE: {
          ArrayList<Unit> returnList = new ArrayList<>();
          for (Location location : locationList) {
            if ((location.getUnit() != null) && 
                (location.getUnit() != unit) &&
                (!location.getUnit().isDefeated())) {
              returnList.add(location.getUnit());
            }
          }
          return returnList.iterator();
        }
      }
    }
    return null;
  }
  
  /**
   * Returns the Team value of a given Unit on the BattleField.
   * @param  unit object being searched for on the BattleField.
   * @return Team value of a given Unit. Returns null if Unit is not found.
   */
  public Team getTeam(Unit unit) {
    Location match = findUnit(unit);
    if (match != null) {
      return match.getTeam();
    }
    return null;
  }
  
  /**
   * Returns an Iterator of Unit objects on the BattleField who share the same
   * Team value as the given parameter.
   * @param  team Team value owned by Unit object being searched for.
   * @return Iterator of Unit objects who share the same Team value.
   */
  private ArrayList<Unit> getTargetTeam(Team team) {
    ArrayList<Unit> allUnits = new ArrayList<>();
    for (Location location : locationList) {
      if ((location.getUnit() != null) && (location.getTeam() == team)) {
        if (!location.getUnit().isDefeated()) {
          allUnits.add(location.getUnit());
        }
      }
    }
    return allUnits;
  }
  
  /**
   * Returns true if all Unit objects on either Team are defeated.
   * @return true if all Unit objects on either Team are defeated.
   */
  public boolean isBattleComplete() {
    boolean goldIsDown = true;
    Iterator<Unit> goldTeam = getTargetTeam(Team.GOLD).iterator();
    while (goldTeam.hasNext()) {
      if (!goldTeam.next().isDefeated()) {
        goldIsDown = false;
      }
    }
    boolean silverIsDown = true;
    Iterator<Unit> silverTeam = getTargetTeam(Team.SILVER).iterator();
    while (silverTeam.hasNext()) {
      if (!silverTeam.next().isDefeated()) {
        silverIsDown = false;
      }
    }
    return goldIsDown || silverIsDown;
  }

  /**
   * Returns the next next available for a given Team and Row.
   * @param team the Team with the Location we are looking for.
   * @param row the Row with the Location we are looking for.
   * @return the next next available.
   */
  private int nextPosition(Team team, Row row) {
    int teamIndex = 0;
    int rowIndex = 0;
    for (int index = 0; index < Team.values().length; index++) {
      if (Team.values()[index] == team) {
        teamIndex = index;
      }
    }
    for (int index = 0; index < Row.values().length; index++) {
      if (Row.values()[index] == row) {
        rowIndex = index;
      }
    }
    int returnValue = nextPosition[teamIndex][rowIndex];
    nextPosition[teamIndex][rowIndex] *= -1;
    if (nextPosition[teamIndex][rowIndex] <= 0) {
      nextPosition[teamIndex][rowIndex]--;
    }
    return returnValue;
  }
  
  /**
   * Moves the time of the battle to the next valid Skill execution.
   */
  public void nextTurn() {
    Unit nextUnit;
    Skill nextSkill = null;
    do {
      nextUnit = turnOrder.next();
      if (nextUnit != null) {
        nextSkill = nextUnit.nextSkill();
        if (nextSkill != null) {
          if (executeSkill(nextUnit, nextSkill)) {
            battleLog.logClock(turnOrder);
            String logItem = nextUnit.getName() + " performs " + nextSkill.getName();
            nextUnit.resetSkill(nextSkill);
            battleLog.logText(logItem);
            battleLog.logState(this);
          }
          else {
            turnOrder.addAfterNext(nextUnit, true);
          }
        }          
      }
    } while ((nextUnit != null) && (nextSkill == null));
  }
  
  /**
   * Removes an existing Unit from the BattleField.
   * @param oldUnit the Unit object to be removed.
   * @return true if the removal was successful, otherwise false.
   */
  public boolean removeUnit(Unit oldUnit) {
    if (oldUnit != null) {
      for (Location fighter : locationList) {
        if (fighter.getUnit() == oldUnit) {
          fighter.setUnit(null);
          oldUnit.clearBattleState();
          return true;
        }
      }
    }
    return false;
  }

}
