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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A abstract class for describing an event. Instantiation of concrete
 * subclasses requires the super class to be instantiated with the time of the
 * event. The {@link #toString() toString} method has been overridden as an 
 * abstraction. This is to force concrete subclasses to create the output as a
 * {@link String} that is readable to the user.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class LogEntry {
  
  /**
   * The time when the event described in the LogEntry occurred.
   */
  private final LocalDateTime timeStamp;
  
  private final Object[] entries;
  
  /**
   * Initiates the object with the time at which the event occurred. A null time
   * value will throw an {@link IllegalArgumentException}.
   * @param timeStamp the time of the event.
   * @param entries list of objects that sum up the log entry when printed in
   *        order.
   */
  public LogEntry(LocalDateTime timeStamp, Object... entries) {
    if (timeStamp == null) throw new IllegalArgumentException();
    this.timeStamp = timeStamp;
    this.entries = entries;
  }

  /**
   * @return the time when the event described in the LogEntry occurred.
   */
  public LocalDateTime getTimeStamp() {
    return timeStamp;
  }

  /**
   * The {@link LogEntry} class overrides this method with an abstraction. This
   * is so that concrete implementation are required to output a {@link String}
   * that correctly describes the log entry that is readable to the user.
   * @return String that describes the event in a manner that is readable to the
   *         user.
   */
  @Override
  public String toString() {
    DateTimeFormatter formatTime = DateTimeFormatter.ISO_LOCAL_TIME;
    String returnValue = "@" + formatTime.format(timeStamp) + ": ";
    for (Object s : entries) returnValue += s.toString();
    return returnValue;
  }

  public String toStringNoTimeStamp() {
    String returnValue = "";
    for (Object s : entries) returnValue += s.toString();
    return returnValue;
  }
}
