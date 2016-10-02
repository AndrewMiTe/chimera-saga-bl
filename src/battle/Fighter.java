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
public class Fighter implements Actor {

  /**
   * Attempts to remove the given Status object from the fighter. Returns {@code
   * true} if the object was both found and if the predicate for its removal
   * returned {@code true}. This method will only remove the Status object
   * given.
   * @param status the status to be removed.
   * @return {@code true} if the object was removed.
   * @see #removeStatus(String name)
   * @stub
   */
  public boolean removeStatus(Status status) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean removeStatus(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isStunned() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isDefeated() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean executeSkill(Skill skill) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
