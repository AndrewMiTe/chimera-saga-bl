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

/**
 * Describes a BattleFeild event and provides references to the various objects
 * involved in the event.
 * @author Andrew M. Teller (andrew.m.teller@gmail.com)
 */
public class LogEntry {

  /**
   * An enumerated type to specify what information is contained in the LogEntry
   * and how the eventItems should be organized.
   */
  private LogEntryType logEntryType;
  
  /**
   * The time since the start of the battle when the LogEntry occurred. Measured
   * in milliseconds.
   */
  private int timeStamp;
  
  /**
   * References to the various objects involved in the event that generated the
   * LogEntry.
   */
  private Object[] eventItems;
    
  @Override public String toString() {
    switch (logEntryType) {
      case SKILL_EXECUTED:
        return eventItems[1].toString() + " executed the "
          + eventItems[0].toString() + " skill.";
      case STATUS_APPLIED:
        return "The " + eventItems[0].toString() + " status was applied to "
          + eventItems[1].toString() + " with a duration of "
          + ((Status)eventItems[0]).getDuration();
      case STATUS_REMOVED:
        if (eventItems[2].toString().equals("true")) {
          return "The " + eventItems[0].toString() + " status has expired from "
            + eventItems[1].toString();
        }
        else {
          return "The " + eventItems[0].toString() + " status was forcfully"
            + " removed from " + eventItems[1].toString();
        }
      case TEAM_DEFEAT:
        return eventItems[0].toString() + " team defeated!";
      case TEAM_VICTORY:
        return eventItems[0].toString() + " team is Victorious!";
      default:
        throw new AssertionError(logEntryType.name());
    }
  }
  
}