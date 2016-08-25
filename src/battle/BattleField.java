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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javafx.scene.layout.Pane;

/**
 * @todo Reorder the time component to use a date and time class rather then an integer.
 * Takes the units of a battle and orders them. The class allows the calling
 * object to obtain the battling units as a whole, as a team, or as a set of
 * units that match a target when given the targeting unit.
 * @author Andrew M. Teller (andrew.m.teller@gmail.com)
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
    locationList = new ArrayList();
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
    //Check to see if the new unit is a real Unit.
    if ((newUnit != null) && (team != null)) {
      //Store the Unit's Row for easier use.
      Row row = newUnit.getStartingRow();
      //Create a list to contain all available openings.
      ArrayList<Location> openings = new ArrayList();
      //Add all empty Location objects to our list.
      for (Location locale : locationList) {
        if ((locale.getTeam() == team) && 
            (locale.getRow() == row) && 
            (locale.getUnit() == null)) {
          openings.add(locale);
        }
      }
      //Create more positions if needed.
      while (openings.size() <= EXTRA_POSITIONS) {
        Location newLocation = new Location(team, row, nextPosition(team, row));
        openings.add(newLocation);
        locationList.add(newLocation);
      }
      //Randomly choose an available Location and place the Unit there.
      openings.get((int)(Math.random() * EXTRA_POSITIONS)).setUnit(newUnit);
      //Set the Unit to a new battle state and pass it the TurnOrder.
      newUnit.setBattleState(turnOrder);
      //Exectue all pre-battle skills.
      executePreBattleSkills(newUnit);
      //Successful addition, return true.
      return true;
    }
    //Failed addition, return false.
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
    //Check to make sure unit is not null.
    if (unit != null) {
      //Iterate to find and perform pre-battle Skill objects.
      Iterator<Skill> skills = unit.getSkills();
      while (skills.hasNext()) {
        Skill s = skills.next();
        //Execute skills with a max cooldown less then zero that Target SELF.
        if ((s.getMaxCooldown() < 0) && (s.getTarget() == Target.SELF)) {
          for (Iterator<Status> actions = s.getActions(); actions.hasNext();) {
            unit.addStatus(new Status(actions.next()));
          }
        }
      }
    }
  }

  /**
   * Attempts to execute a battle-time Skill cast by the given Unit. Returns
   * true if the Skill or any sub-Skill was successfully executed.
   * @param  unit the assumed Unit to be targeting the Skill
   * @param  skill the Skill attempting to be executed.
   * @return true if the Skill or any sub-Skill successfully executed.
   */
  private boolean executeSkill(Unit unit, Skill skill) {  
    //Make sure the Unit and Skill objects are not null and that the Skill is
    //not a pre-battle Skill.
    if ((unit != null) && (skill != null) && (skill.getMaxCooldown() >= 0)){
      //Obtain the Target Unit objects of the Skill.
      Iterator<Unit> targets = getTarget(unit, skill.getTarget());
      //If the Skill has no targets, Skill fails, return false.
      if (!targets.hasNext()) {
        return false;
      }
      //Iterate through the Unit objects of our Target while there is no match.
      boolean match = false;
      while (targets.hasNext() && !match) {
        Unit nextUnit = targets.next();
        //For each Target, iterate through the required Status values.
        Iterator<String> requirements = skill.getRequires();
        //Iterate until a requirement is not met, or until no more requirements.
        match = true;
        while(requirements.hasNext() && match) {
          String requirement = requirements.next();
          //If a required Status is not found, then this Target doesn't match.
          if (nextUnit.getStatus(requirement) == null) {
            match = false;
          }
        }
      }
      //If requirements aren't met, execution fails, return false.
      if (!match) {
        return false;
      }
      //Create variables to track sub-Skill progress.
      boolean partialSuccess = false;
      boolean completeSuccess = true;
      //Iterate and execute through all sub-Skill objects.
      Iterator<Skill> subSkillList = skill.getSubSkills();
      while (subSkillList.hasNext()) {
        Skill subSkill = subSkillList.next();
        boolean subSkillSuccess = executeSkill(unit, subSkill);
        //If any sub-Skill succeeds, store the success.
        partialSuccess = partialSuccess || subSkillSuccess;
        //If any sub-Skill fails, store the failure.
        completeSuccess = completeSuccess && subSkillSuccess;
      }
      //If all sub-Skill objects successful, apply actions to the Target(s).
      if (completeSuccess) {
        //Obtain the Target Unit objects of the Skill.
        targets = getTarget(unit, skill.getTarget());
        //Should be able to assume that there will still be a Target.
        //Iterate through the Target Unit objects.
        while (targets.hasNext()) {
          Unit nextUnit = targets.next();
          //For each Target, iterate through the required Status values.
          Iterator<String> requirements = skill.getRequires();
          //Iterate until a requirement is not met, or until no more requirements.
          match = true;
          while(requirements.hasNext() && match) {
            String requirement = requirements.next();
            //If a required Status is not found, then this Target doesn't match.
            if (nextUnit.getStatus(requirement) == null) {
              match = false;
            }
          }
          //If a match is found, Iterate through the actions and apply them.
          if (match) {
            //Add the Target to the TurnOrder.
            turnOrder.addTurnItem(nextUnit, 0, true);
            //Iterate through all the applicable Status object.
            Iterator<Status> actions = skill.getActions();
            while (actions.hasNext()) {
              //Add the next Status to the Target.
              nextUnit.addStatus(actions.next());
            }
            //If the Skill only applies to one Target, return successfully.
            if ((skill.getTarget() != Target.ALL_ALLIES) ||
                (skill.getTarget() != Target.ALL_ENEMIES) ||
                (skill.getTarget() != Target.All_OTHER_ALLIES)) {
              return true;
            }
          }
        }
      }
      //Possible success, return disjuction of sub-Skill results.
      return partialSuccess || completeSuccess;
    }
    //Execution failed, return false.
    return false;
  }
  
  /**
   * Returns the Location wrapper class that contains the given Unit object.
   * @param  unit Unit object that the caller is searching for.
   * @return Location object containing the Unit searched for or null if search
   *         fails.
   */
  private Location findUnit(Unit unit) {
    //Check to make sure Unit is not null.
    if (unit != null) {
      //Iterate through all of the Location objects.
      for (Location next : locationList) {
        //If you find a match, return it.
        if (next.getUnit() == unit) {
          return next;
        }
      }
    }
    //Search failed, return null.
    return null;
  }
  
  /**
   * Returns all Unit objects in the BattleField as an Iterator object. Removing
   * Unit objects using calls to the Iterator do not affect the internal
   * workings of the BattleField.
   * @return Iterator containing all Unit objects on the Battlefield.
   */
  public Iterator<Unit> getAllUnits() {
    //Construct a list to hold any Units found in Location objects.
    ArrayList<Unit> allUnits = new ArrayList();
    //Add any Unit objects attached to a Location as you iterate.
    for (Location location : locationList) {
      //Ignore a Location if its index contains a Unit.
      if (location.getUnit() != null) {
        allUnits.add(location.getUnit());
      }
    }
    //Return an Iterator of the new list.
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
    //Calculate the coordinates of the Unit using our fields and constants.
    int xPosition = position * COL_DISTANCE;
    int yPosition = (int)(Math.random() * ROW_WIDTH);
    yPosition += ROW_DISTANCE / 2;
    if (row == Row.BACK) yPosition += ROW_WIDTH + ROW_DISTANCE;
    if (team == Team.GOLD) yPosition *= -1;
    //Return the calculation.
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
    //Find the Location object that contains the Unit parameter.
    Location match = findUnit(unit);
    //If match is found, return the Location object's coordinates.
    if (match != null) {
      return match.getCoordinates();
    }
    //Search failed, return null.
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
    //Check to make sure the targting Unit and its Target type is not null.
    if ((match != null) && (target != null)) {
      //Sort the master list of Locations compared this the targeting Unit.
      locationList.sort(match);
      //Switch case for the various Target possibilities.
      switch (target) {
        case SELF: {
          //Return a newly created Iterator with just the targeting Unit.
          if (!unit.isDefeated()) {
            return (new ArrayList((Collection<? extends Unit>) unit)).iterator();
          }
          return null;
        }
        case CLOSE_ALLY: {
          //Create lists for building and returning the Target objects.
          ArrayList<Location> builderList = new ArrayList();
          ArrayList<Unit> returnList = new ArrayList();
          //Keep track of the shortest distance to the Targeting Unit.
          double shortestDistance = -1;
          //Add any Unit objects attached to a Location as you iterate.
          for (Location next : locationList) {
            //Ignore a Location unless it contains a Unit on the same Team.
            if ((next.getUnit() != null) &&
                (next.getUnit() != unit) &&
                (!next.getUnit().isDefeated()) &&
                (next.getTeam() == match.getTeam())) {
              //Add this Unit to the list of possible Targets.
              builderList.add(next);
              //Get the distance between this Unit and the Targeting Unit.
              double distance = Location.getDistance(next, match);
              //Check if the new distance the shortest or if this is the first
              //calculation.
              if (distance < shortestDistance || shortestDistance < 0) {
                shortestDistance = distance;
              }
            }
          }
          //Sort our temporary list.
          builderList.sort(match);
          //Iterate through the list of potential targets.
          for (Location next : builderList) {
            //Get the distance between them and the targeting Unit again.
            double distance = Location.getDistance(next, match);
            //If the distance too far, remove the Unit.
            if (distance <= (shortestDistance + CLOSE_DISTANCE)) {
              returnList.add(next.getUnit());
            }
          }
          //Return an Iterator of the new and culled list.
          return returnList.iterator();
        }
        case ANY_ALLY: case ALL_ALLIES: {
          //Return the Team of the tarheting Unit.
          return getTargetTeam(match.getTeam()).iterator();
        }
        case ANY_OTHER_ALLY: case All_OTHER_ALLIES: {
          //Get the list of the targeting Unit's allies.
          ArrayList<Unit> returnList = getTargetTeam(match.getTeam());
          //Remove the targting Unit.
          returnList.remove(unit);
          //Return an Iterator of the modified list.
          return returnList.iterator();
        }
        case CLOSE_ENEMY: {
          //Create lists for building and returning the Target objects.
          ArrayList<Location> builderList = new ArrayList();
          ArrayList<Unit> returnList = new ArrayList();
          //Keep track of the shortest distance to the Targeting Unit.
          double shortestDistance = -1;
          //Add any Unit objects attached to a Location as you iterate.
          for (Location next : locationList) {
            //Ignore a Location unless it contains a Unit on the same Team.
            if ((next.getUnit() != null) &&
                (!next.getUnit().isDefeated()) &&
                (((match.getTeam() == Team.SILVER) && 
                (next.getTeam() == Team.GOLD)) || 
                ((match.getTeam() == Team.GOLD) && 
                (next.getTeam() == Team.SILVER)))) {
              //Add this Unit to the list of possible Targets.
              builderList.add(next);
              //Get the distance between this Unit and the Targeting Unit.
              double distance = Location.getDistance(next, match);
              //Check if the new distance the shortest or if this is the first
              //calculation.
              if (distance < shortestDistance || shortestDistance < 0) {
                shortestDistance = distance;
              }
            }
          }
          //Sort our temporary list.
          builderList.sort(match);
          //Iterate through the list of potential targets.
          for (Location next : builderList) {
            //Get the distance between them and the targeting Unit again.
            double distance = Location.getDistance(next, match);
            //If the distance too far, remove the Unit.
            if (distance <= (shortestDistance + CLOSE_DISTANCE)) {
              returnList.add(next.getUnit());
            }
          }
          //Return an Iterator of the new and culled list.
          return returnList.iterator();
        }
        case ANY_ENEMY: case ALL_ENEMIES: {
          //Return the SILVER Team if the Targting Team is GOLD.
          if (match.getTeam() == Team.GOLD) {
            return getTargetTeam(Team.SILVER).iterator();
          }
          //Return the GOLD Team if the Targting Team is SILVER.
          else if (match.getTeam() == Team.SILVER) {
            return getTargetTeam(Team.GOLD).iterator();
          }
        }
        case ANYONE: case EVERYONE: {
          //Construct a list to hold any Units found in Location objects.
          ArrayList<Unit> allUnits = new ArrayList();
          //Add any Unit objects attached to a Location as you iterate.
          for (Location location : locationList) {
            //Ignore a Location if its index contains a Unit.
            if ((location.getUnit() != null) && 
                (!location.getUnit().isDefeated())) {
              allUnits.add(location.getUnit());
            }
          }
          //Return an Iterator of the new list.
          return allUnits.iterator();
        }
        case ANYONE_ELSE: case EVERYONE_ELSE: {
          //Construct a list to hold any Units found in Location objects.
          ArrayList<Unit> returnList = new ArrayList();
          //Add any Unit objects attached to a Location as you iterate.
          for (Location location : locationList) {
            //Ignore a Location if its index contains a Unit.
            if ((location.getUnit() != null) && 
                (location.getUnit() != unit) &&
                (!location.getUnit().isDefeated())) {
              returnList.add(location.getUnit());
            }
          }
          //Return an Iterator of the new list.
          return returnList.iterator();
        }
      }
    }
    //Targting failed, return null.
    return null;
  }
  
  /**
   * Returns the Team value of a given Unit on the BattleField.
   * @param  unit object being searched for on the BattleField.
   * @return Team value of a given Unit. Returns null if Unit is not found.
   */
  public Team getTeam(Unit unit) {
    //Find the Location object that contains the Unit parameter.
    Location match = findUnit(unit);
    //If match is found, return the Location object's Team value.
    if (match != null) {
      return match.getTeam();
    }
    //Search failed, return null.
    return null;
  }
  
  /**
   * Returns an Iterator of Unit objects on the BattleField who share the same
   * Team value as the given parameter.
   * @param  team Team value owned by Unit object being searched for.
   * @return Iterator of Unit objects who share the same Team value.
   */
  private ArrayList<Unit> getTargetTeam(Team team) {
    //Create a list to contain the Unit objects from the same Team.
    ArrayList<Unit> allUnits = new ArrayList();
    //Iterate through the Location objects.
    for (Location location : locationList) {
      //If a Unit is found and is on the right Team, add the Unit to the list.
      if ((location.getUnit() != null) && (location.getTeam() == team)) {
        //Make sure the Unit is not defeated.
        if (!location.getUnit().isDefeated()) {
          allUnits.add(location.getUnit());
        }
      }
    }
    //Return an Iterator of the constructed list.
    return allUnits;
  }
  
  /**
   * Returns true if all Unit objects on either Team are defeated.
   * @return true if all Unit objects on either Team are defeated.
   */
  public boolean isBattleComplete() {
    //Iterate through the GOLD team. Check for those not defeated.
    boolean goldIsDown = true;
    Iterator<Unit> goldTeam = getTargetTeam(Team.GOLD).iterator();
    while (goldTeam.hasNext()) {
      if (!goldTeam.next().isDefeated()) {
        goldIsDown = false;
      }
    }
    //Iterate through the SILVER team. Check for those not defeated.
    boolean silverIsDown = true;
    Iterator<Unit> silverTeam = getTargetTeam(Team.SILVER).iterator();
    while (silverTeam.hasNext()) {
      if (!silverTeam.next().isDefeated()) {
        silverIsDown = false;
      }
    }
    //End the fight if either team have units that are all defeated.
    return goldIsDown || silverIsDown;
  }

  /**
   * Returns the next next available for a given Team and Row.
   * @param team the Team with the Location we are looking for.
   * @param row the Row with the Location we are looking for.
   * @return the next next available.
   */
  private int nextPosition(Team team, Row row) {
    //Create local variables to store the matching indexes.
    int teamIndex = 0;
    int rowIndex = 0;
    //Locate the matching Team's index.
    for (int index = 0; index < Team.values().length; index++) {
      if (Team.values()[index] == team) {
        teamIndex = index;
      }
    }
    //Locate the matching Row's index.
    for (int index = 0; index < Row.values().length; index++) {
      if (Row.values()[index] == row) {
        rowIndex = index;
      }
    }
    //Save the return value.
    int returnValue = nextPosition[teamIndex][rowIndex];
    //Recalculate the next for the next call.
    nextPosition[teamIndex][rowIndex] *= -1;
    if (nextPosition[teamIndex][rowIndex] <= 0) {
      nextPosition[teamIndex][rowIndex]--;
    }
    //Return our result.
    return returnValue;
  }
  
  /**
   * Moves the time of the battle to the next valid Skill execution.
   */
  public void nextTurn() {
    //Declare variables for checking our loop condition.
    Unit nextUnit;
    Skill nextSkill = null;
    //Loop until a skill is executed or until no more Unit objects have turns.
    do {
      //Get the next Unit in the turnOrder.
      nextUnit = turnOrder.next();
      //Check to make sure the Unit is not null.
      if (nextUnit != null) {
        //Fetch the highest priority skill that the Unit has ready.
        nextSkill = nextUnit.nextSkill();
        if (nextSkill != null) {
          if (executeSkill(nextUnit, nextSkill)) {
            //Logs the time right before any new actions are taken.
            battleLog.logClock(turnOrder);
            //Start building the output of the skill for the user to see.
            String logItem = nextUnit.getName() + " performs " + nextSkill.getName();
            //Tell the Unit to reset the Skill.
            nextUnit.resetSkill(nextSkill);
            //Finish up the log entry for the skill.
            battleLog.logText(logItem);
            //Logs state of all units post action.
            battleLog.logState(this);
          }
          //If attempt fails.
          else {
            //Put the Unit after the next item on the TurnOrder
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
    //Make sure the Unit being searched for is not null.
    if (oldUnit != null) {
      //Search through our list of all Location objects.
      for (Location fighter : locationList) {
        //Check to see if a location contains the Unit.
        if (fighter.getUnit() == oldUnit) {
          //Remove the Unit from the location and clear its battle state.
          fighter.setUnit(null);
          oldUnit.clearBattleState();
          //Removal successful, return true.
          return true;
        }
      }
    }
    //Removal failed, return false.
    return false;
  }

}