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

/**
 *
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public enum LogEntryType {
  
  /**
   * LogEntry contains the Skill object executed and the Unit object executing
   * the skill, in that order.
   */
  SKILL_EXECUTED,
  
  /**
   * LogEntry contains the Status object applied and the Unit object it was
   * applied to, in that order.
   */
  STATUS_APPLIED,
  
  /**
   * LogEntry contains the Status object removed, the Unit object it was removed
   * from, and a Boolean value of true if Status expired before it was removed,
   * in that order.
   */
  STATUS_REMOVED,
  
  /**
   * LogEntry contains the enumerated Team value that has been defeated.
   */
  TEAM_DEFEAT,
  
  /**
   * LogEntry contains the enumerated Team value that has claimed victory.
   */
  TEAM_VICTORY
  
}
