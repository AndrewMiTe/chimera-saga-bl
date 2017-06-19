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

package core;

/**
 * Objects implementing this interface can become listeners to various Status
 * object events and state changes. All methods have a do-nothing default
 * implementation that can be overridden.
 * 
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public interface StatusHandler {

  /**
   * Event method that handles the successful application of the status it is
   * listening to.
   * 
   * @param status
   *          the status that was successfully applied.
   */
  public default void onStatusApplication(Status status) {
  }

  /**
   * Event method that handles the successful removal of the status it is
   * listening to.
   * 
   * @param status
   *          the status that was successfully removed.
   */
  public default void onStatusRemoval(Status status) {
  }

}
