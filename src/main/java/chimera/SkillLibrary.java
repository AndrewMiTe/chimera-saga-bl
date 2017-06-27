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

import java.time.Duration;
import core.Skill;
import core.SkillBuilder;
import core.Target;

public enum SkillLibrary {

  /**
   * A skill preparatory that establishes the fighters primary defenses, rate of
   * fatigue, and their staggering time before they can act in battle. This
   * skill is only executed in the pre-battle phase.
   */
  PRE_BATTLE {
    @Override // from SkillLibrary
    public Skill get() {return get(2, 2, 2);}

    /**
     * {@inheritDoc} In the case of PRE_BATTLE, the given input sets the stack
     * size of the ENDURANCE, EVASION, and OPPOSITION statuses applied by this
     * skill equal to the first three respective given int values. If any of the
     * first three given values are {@code < 1}, or if fewer then three int
     * values are given, this method throws a {@link IllegalArgumentException}.
     * If the given value is {@code null} this method throws a
     * {@link NullPointerException}.
     */
    @Override // from SkillLibrary
    public Skill get(int... vars) {
      return Skill.builder("PreBattle Defenses")
          .setDescription("Establishes your defenses before the battle begins.")
          .setAsPreBattleSkill()
          .addEffect(StatusLibrary.ENDURANCE.modify().setStackSize(vars[0]).build())
          .addEffect(StatusLibrary.EVASION.modify().setStackSize(vars[1]).build())
          .addEffect(StatusLibrary.OPPOSITION.modify().setStackSize(vars[2]).build())
          .addEffect(StatusLibrary.FATIGUE.get())
          .addEffect(StatusLibrary.STAGGER.get())
          .build();
    }
  },

  /**
   * An attacking skill that inflicts a wound on the closest enemy if it doesn't
   * have enough stacks of the evasion status.
   */
  STRIKE_EVADABLE {
    @Override // from SkillLibrary
    public Skill get() {return get(1);}

    /**
     * {@inheritDoc} In the case of STRIKE_EVADABLE, the given input sets the
     * stack size of the {@link StatusLibrary.WOUND_EVADABLE} status inflicted
     * by this skill equal to the first given int value. If the first given
     * value is {@code < 1}, this method throws a
     * {@link IllegalArgumentException}. If the given value is {@code null} this
     * method throws a {@link NullPointerException}.
     */
    @Override // from SkillLibrary
    public Skill get(int... vars) {
      return Skill.builder("Evadable Strike")
          .setDescription("An attack that can be evaded but not opposed.")
          .setTarget(Target.CLOSE_ENEMY)
          .setCooldown(Duration.ofSeconds(4))
          .addEffect(StatusLibrary.WOUND_EVADABLE.modify().setStackSize(vars[0]).build())
          .build();
    }
  },
  
  /**
   * An attacking skill that inflicts a wound on the closest enemy if it doesn't
   * have enough stacks of the opposition status.
   */
  STRIKE_OPPOSABLE {
    @Override // from SkillLibrary
    public Skill get() {return get(1);}

    /**
     * {@inheritDoc} In the case of STRIKE_OPPOSABLE, the given input sets the
     * stack size of the {@link StatusLibrary.WOUND_OPPOSABLE} status inflicted
     * by this skill equal to the first given int value. If the first given
     * value is {@code < 1}, this method throws a
     * {@link IllegalArgumentException}. If the given value is {@code null} this
     * method throws a {@link NullPointerException}.
     */
    @Override // from SkillLibrary
    public Skill get(int... vars) {
      return Skill.builder("Opposable Strike")
          .setDescription("An attack that can be opposed but not evaded.")
          .setTarget(Target.CLOSE_ENEMY)
          .setCooldown(Duration.ofSeconds(4))
          .addEffect(StatusLibrary.WOUND_OPPOSABLE.modify().setStackSize(vars[0]).build())
          .build();
    }
  };
  
  /**
   * Returns a new copy of the skill the enumerated object represents.
   * 
   * @return the new skill.
   */
  abstract public Skill get();
  
  /**
   * Returns a new copy of the skill the enumerated object represents with the
   * given values setting various parameters of the skill. What parameters are
   * modifiable with this method is specific to the skill. For some skills, this
   * method is no different then calling {@link #get()}.
   * 
   * @param vars
   *          variables for modifying the default status.
   * @return the new status.
   */
  public Skill get(int... vars) {
    return get();
  }
  
  /**
   * Returns a SkillBuilder object for making a skill whose defaults match the
   * skill returned from {@link #get()}.
   *  
   * @return a builder for modifying this status.
   */
  public SkillBuilder modify() {
    return new SkillBuilder(this.get());
  }

  /**
   * Returns a SkillBuilder object for making a skill whose defaults match the
   * skill returned from {@link #get(int)}.
   * 
   * @param vars variables for modifying the default status.
   * @return a builder for modifying this status.
   */
  public SkillBuilder modify(int... vars) {
    return new SkillBuilder(this.get(vars));
  }

}
