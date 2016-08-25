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
import java.util.Comparator;

/**
 * Wrapper class for assigning Teams and giving Unit objects a Location on the
 * BattleField.
 * @author Andrew M. Teller (andrew.m.teller@gmail.com)
 */
public class Location implements Comparator {

  /**
   * The Unit that occupies the Location. Value can be null to signify an
   * empty but available Location.
   */
  private Unit unit;

  /**
   * The Team that the Location is on. Team combined with Row grossly
   * determines where the Unit resides on the y-axis.
   */
  private final Team team;

  /**
   * The Row that the Location is on within their Team. Team combined with Row
   * grossly determines where the Unit resides on the y-axis.
   */
  private final Row row;

  /**
   * The relative x-axis coordinates of the Unit in relation to other Unit
   * objects in the same Row. Values of the same Row are assumed to be
   * sequential. They are allowed to be negative.
   */
  private final int position;

  /**
   * The coordinates upon the Battlefield that the Location can be found.
   */
  private final Point coordinates;

  /**
   * Basic constructor.
   * @param  team the Team the Position is on.
   * @param  row  the Row within a Team that the Position is on.
   * @param  position the relative coordinates of the Unit in relation to other
       Unit object in the same Row.
   */
  protected Location(Team team, Row row, int position) {
    this.unit = null;
    this.team = team;
    this.row = row;
    this.position = position;
    this.coordinates = BattleField.getCoordinates(team, row, position);
  }

  /**
   * Allows other Location objects to be compared based on their distance away
   * from this Location.
   * @param  localOne first Location to have its distance from this Location
   *         compared to the second Location's distance from this Location.
   * @param  localTwo second Location to have its distance from this Location
   *         compared to the first Location's distance from this Location.
   * @return 1 if the distance calculated for the first Location is greater then
   *         the second Location, -1 if the first is less then the second, and 0
   *         if the first and second are equal in distance to this Location.
   */
  @Override public int compare(Object localOne, Object localTwo) {
    double distanceOne = getDistance(this, (Location) localOne);
    double distanceTwo = getDistance(this, (Location) localTwo);
    if (distanceOne > distanceTwo) {
      return 1;
    }
    else if (distanceOne < distanceTwo) {
      return -1;
    }
    else {
      return 0;
    }
  }

  /**
   * Getter for the coordinates of the Location on the BattleField.
   * @return a Point object that wraps the x and y coordinate of the Location.
   */
  protected Point getCoordinates() {
    return coordinates;
  }

  /**
   * Calculates the distance between two Locations and returns the distance
   * value.
   * @param  localOne Location object to begin measuring the distance from.
   * @param  localTwo Location object to complete the distance measurement.
   * @return calculated distance between the two Location objects.
   */
  protected static double getDistance(Location localOne, Location localTwo) {
    double xDist = localOne.getCoordinates().x - localTwo.getCoordinates().x;
    double yDist = localOne.getCoordinates().y - localTwo.getCoordinates().y;
    return Math.sqrt((xDist * xDist) + (yDist * yDist));
  }

  /**
   * Getter for the next the Location is in within its Row.
   * @return the next value in relation to other Location objects within
   *         the same Row as this Location.
   */
  protected int getPosition() {
    return position;
  }

  /**
   * Getter for the Row of the Team the Location is in.
   * @return Row value of the Location.
   */
  protected Row getRow() {
    return row;
  }

  /**
   * Getter for the Team the Location is on.
   * @return Team value of the Location.
   */
  protected Team getTeam() {
    return team;
  }

  /**
   * Getter for the Unit that is in the Location.
   * @return Unit object that the Location contains. Is null if the Location
   *         is vacant.
   */
  protected Unit getUnit() {
    return unit;
  }

  /**
   * Setter for the Unit object that this class wraps.
   * @param  unit object that the Location wraps.
   */
  protected void setUnit(Unit unit) {
    this.unit = unit;
  }

}