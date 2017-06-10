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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 *
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class FighterBuilder {
	
	  /**
	   * Stores the name value for building a Fighter object.
	   * @see #setName
	   */
	  private String name;
	  
	  /**
	   * Stores the squad value for building a Fighter object.
	   * @see #setSquad
	   */
	  private Squad squad;
	  
	  /**
	   * Stores the skillList value for building a Fighter object.
	   * @see #addSkill
	   */
	  private List<Skill> skillList;
	  
	  /**
	   * Stores the closeRange value for building a Fighter object.
	   * @see #setCloseRange
	   */
	  private int closeRange;

	  /**
	   * Stores the isAllyCase value for building a Fighter object.
	   * @see #setIsAllyCase
	   */
	  private BiPredicate<Fighter, Fighter> isAllyCase;
	  
	  /**
	   * Stores the isEnemyCase value for building a Fighter object.
	   * @see #setIsEnemyCase
	   */
	  private BiPredicate<Fighter, Fighter> isEnemyCase;
	  
	  /**
	   * Stores the listeners value for building a Fighter object.
	   * @see #addListener
	   */
	  private List<FighterHandler> listeners;
	  
	  /**
	   * Instantiates the object with the name of the {@link Fighter} to be
	   * built. Sets all other properties of the fighter to be built to their
	   * default values.
	   * @param name see {@see #setName}. Cannot be null.
	   */
	  public FighterBuilder(String name) {
	    if (name == null) {
	      throw new NullPointerException("name: null");
	    }
	    this.name = name;
	    this.squad = new Squad();
	    this.skillList = new ArrayList<>();
	    this.closeRange = 0;
	    this.isAllyCase = (a, b) -> a.getSquad() == b.getSquad();
	    this.isEnemyCase = (a, b) -> a.getSquad() != a.getSquad();
	    this.listeners = new ArrayList<>();
	  }

	  /**
	   * Instantiates the object by setting all properties so that any status built
	   * directly after initialization would be a copy of the Status object given.
	   * @param status the status used to set all properties.
	   */
	  public FighterBuilder(Fighter fighter) {
	    Fighter copyOf = new Fighter(fighter);
	    this.name = copyOf.getName();
	    this.squad = copyOf.getSquad();
	    this.skillList = copyOf.getSkills();
	    this.closeRange = copyOf.getCloseRange();
	    this.isAllyCase = copyOf.getIsAllyCase();
	    this.isEnemyCase = copyOf.getIsEnemyCase();
	    this.listeners = copyOf.getListeners();
	  }

}
