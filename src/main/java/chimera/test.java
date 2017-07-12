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

import static chimera.FighterLibrary.*;

import core.Fighter;

/**
 * A class to test how various components integrate.
 * 
 * @author Andrew M. Teller (https://github.com/AndrewMiTe)
 */
public class test {

  /**
   * This method begins the test.
   * 
   * @param args
   *          command-line arguments.
   */
  public static void main(String[] args) {
    Squad squadOne = new Squad(WASHINGTON.get(), JEFFERSON.get());
    Squad squadTwo = new Squad(ADAMS.get(), HAMILTON.get());
    Battle theBattle = new Battle(squadOne, squadTwo);
    theBattle.getSquads().forEach(s -> s.getFighters().forEach(test::printRelations));
    System.out.println("Done.");
  }

  /**
   * Used to output who in the same battle as the fighter is identified as an
   * enemy and who is identified as an ally.
   * 
   * @param fighter
   *          fighter who's relations are outputed.
   */
  private static void printRelations(Fighter fighter) {
    System.out.print(fighter + ":");
    if (fighter.getTeam() instanceof Squad) {
      Squad fightersSquad = (Squad) fighter.getTeam();
      if (fightersSquad.isInBattle()) {
        Battle fightersBattle = fightersSquad.getBattle().get();
        fightersBattle.getSquads().forEach(s -> s.getFighters().forEach(f -> {
          System.out
              .print(" " + f + "(" + (fighter.isAlly(f) ? "Ally" : "") + (fighter.isEnemy(f) ? "Enemy" : "") + ")");
        }));
        System.out.println();
      } else {
        System.out.println("Squad: ! in Battle");
        return;
      }
    } else {
      System.out.println("Team: ! Squad");
      return;
    }
  }

}
