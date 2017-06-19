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

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Used to store the logic to generate a list of Fighter object that can be
 * legal targets for a skill. Various prescribed strategies for determining
 * valid targets are enumerated as constant Target object field values. The
 * class also has a method for generating a costume Target object for
 * determining valid targets.
 * 
 * @see #getCostumTarget getCostumeTarget
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public abstract class Target {

  /**
   * The fighter with the targeting skill.
   */
  public static final Target SELF = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      if (!battlefield.hasFighter(fighter))
        return null;
      return Collections.singletonList(fighter);
    }
  };

  /**
   * Allied fighters close to the fighter with the targeting skill that includes
   * the fighter with the targeting skill. The range of close is determined by a
   * property of the targeting unit.
   */
  public static final Target CLOSE_ALLY = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      List<Fighter> targets = battlefield.getFighters();
      targets.removeIf(f -> 
        !fighter.isAlly(f) || 
        (battlefield.getDistance(f, fighter).orElseGet(fighter::getCloseRange) >= fighter.getCloseRange())
      );
      return targets;
    }
  };

  /**
   * Allied fighters close to the fighter with the targeting skill that excludes
   * the fighter with the targeting skill. The range of close is determined by a
   * property of the targeting unit.
   */
  public static final Target OTHER_CLOSE_ALLY = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      List<Fighter> targets = battlefield.getFighters();
      targets.removeIf(f -> 
        (f == fighter) || 
        !fighter.isAlly(f) || 
        (battlefield.getDistance(f, fighter).orElseGet(fighter::getCloseRange) >= fighter.getCloseRange())
      );
      return targets;
    }
  };

  /**
   * Allied fighters on the battlefield, including the fighter with the
   * targeting skill.
   */
  public static final Target ANY_ALLY = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      List<Fighter> targets = battlefield.getFighters();
      targets.removeIf(f -> !fighter.isAlly(f));
      return targets;
    }
  };

  /**
   * Allied fighters on the battlefield, excluding the fighter with the
   * targeting skill.
   */
  public static final Target ANY_OTHER_ALLY = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      List<Fighter> targets = battlefield.getFighters();
      targets.removeIf(f -> (f == fighter) || !fighter.isAlly(f));
      return targets;
    }
  };

  /**
   * Enemy fighters close to the fighter with the targeting skill. The range of
   * close is determined by a property of the targeting unit.
   */
  public static final Target CLOSE_ENEMY = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      List<Fighter> targets = battlefield.getFighters();
      targets.removeIf(f ->
        !fighter.isEnemy(f) ||
        (battlefield.getDistance(f, fighter).orElseGet(fighter::getCloseRange) >= fighter.getCloseRange())
      );
      return targets;
    }
  };

  /**
   * Enemy fighters on the battlefield.
   */
  public static final Target ANY_ENEMY = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      List<Fighter> targets = battlefield.getFighters();
      targets.removeIf(f -> !fighter.isEnemy(f));
      return targets;
    }
  };

  /**
   * Any fighter on the battlefield.
   */
  public static final Target ANYONE = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      return battlefield.getFighters();
    }
  };

  /**
   * Any fighter on the battlefield, except for the fighter with the targeting
   * skill.
   */
  public static final Target ANYONE_ELSE = new Target() {
    @Override // from Target
    public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
      List<Fighter> targets = battlefield.getFighters();
      targets.removeIf(f -> f == fighter);
      return targets;
    }
  };

  /**
   * Instantiates the Target object.
   */
  private Target() {
  }

  /**
   * Instantiates and returns a Target object using the given function for
   * returning a list of targets from the fighters on a battlefield.
   * 
   * @param function
   *          function for solving the list of fighters that can be targeted on
   *          the battlefield from the targeting fighter's perspective.
   * @return Target object using the given function in instantiation.
   */
  public static final Target getCostumTarget(BiFunction<Battlefield, Fighter, List<Fighter>> function) {
    if (function == null)
      throw new NullPointerException("function: null");
    return new Target() {
      @Override // from Target
      public List<Fighter> getTargets(Battlefield battlefield, Fighter fighter) {
        return function.apply(battlefield, fighter);
      }
    };
  }

  /**
   * Returns a list of Fighter objects chosen from fighters contained in the
   * given battlefield. The fighter with the skill that is assumed to be
   * targeting is taken by the method to provide a perspective.
   * 
   * @param battlefield
   *          battlefield containing all possible targets
   * @param fighter
   *          fighter with the targeting skill.
   * @return list of Fighter objects that can be targeted.
   */
  public abstract List<Fighter> getTargets(Battlefield battlefield, Fighter fighter);

  /**
   * Returns a {@link BiFunction} equvilant to the {@link getTargets} method of
   * this object.
   * 
   * @return function for solving the list of fighters that can be targeted on
   *         the battlefield from the targeting fighter's perspective.
   */
  public BiFunction<Battlefield, Fighter, List<Fighter>> getFunction() {
    return this::getTargets;
  }

}
