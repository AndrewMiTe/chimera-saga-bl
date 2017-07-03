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

package chimera;

import java.util.HashSet;
import java.util.Set;

/**
 * Location of a battle where teams of fighters compete until the battle's
 * conclusion. Battlefield objects dictate the relative distance between
 * fighters for determining the usability of skills.
 * 
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class Battle {

  /**
   * The list of fighters 
   */
  private Set<Squad> squads;
  
  /**
   * Initializes an empty terrain.
   */
  public Battle() {
    this.squads = new HashSet<>();
  }
  
  /**
   * Initializes a terrain occupied by squads.
   */
  public Battle(Squad... squads) {
    if (squads != null) for (Squad s : squads) addSquad(s);
  }
  
  /**
   * Adds a squad to the battleground. Given squads that are {@code null} are
   * ignored.
   * 
   * @param newSquad the squad to be added.
   * @return {@code true} if the squad was successfully added.
   */
  public boolean addSquad(Squad newSquad) {
    if (newSquad != null) {
      return squads.add(newSquad);
    }
    return false;
  }

}
