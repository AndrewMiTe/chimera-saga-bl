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
   * skill is only executed in the pre-battle phase. When retrieved with
   * {@link #get(int...)}, 
   */
  PRE_BATTLE {

    /**
     * {@inheritDoc} In the case {@link SkillLibrary#PRE_BATTLE PRE_BATTLE}, the
     * stack size of the {@link StatusLibrary#ENDURANCE ENDURANCE},
     * {@link StatusLibrary#EVASION EVASION}, and
     * {@link StatusLibrary#OPPOSITION OPPOSITION} statuses applied by this
     * skill equal to the first three respective given {@code int} values. The
     * default stack size of each status is {@code 2}.
     */
    @Override // from SkillLibrary
    public Skill get(int...vars) {return super.get(vars);}
    
    @Override // from SkillLibrary
    protected SkillBuilder builder() {return builder(null);}

    @Override // from SkillLibrary
    protected SkillBuilder builder(int...vars) {
      if (vars == null) vars = new int[0];
      return Skill.builder("PreBattle Defenses")
          .setDescription("Establishes your defenses before the battle begins.")
          .setAsPreBattleSkill()
          .addEffect(StatusLibrary.ENDURANCE.modify()
              .setStackSize(vars.length > 0 ? vars[0] : 2).build())
          .addEffect(StatusLibrary.EVASION.modify()
              .setStackSize(vars.length > 0 ? vars[1] : 2).build())
          .addEffect(StatusLibrary.OPPOSITION.modify()
              .setStackSize(vars.length > 0 ? vars[2] : 2).build())
          .addEffect(StatusLibrary.FATIGUE.get())
          .addEffect(StatusLibrary.STAGGER.get());
    }
  },

  /**
   * An attacking skill that inflicts a wound on the closest enemy if it doesn't
   * have enough stacks of the {@link StatusLibrary#EVASION EVASION} status.
   */
  STRIKE_EVADABLE {

    /**
     * {@inheritDoc} In the case {@link SkillLibrary#STRIKE_EVADABLE
     * STRIKE_EVADABLE}, the first given {@code int} value sets the stack size
     * of the {@link StatusLibrary#WOUND_EVADABLE WOUND_EVADABLE} status that
     * the skill inflicts. The default stack size is {@code 1}.
     */
    @Override // from SkillLibrary
    public Skill get(int...vars) {return super.get(vars);}
    
    @Override // from SkillLibrary
    protected SkillBuilder builder() {return builder(null);}

    @Override // from SkillLibrary
    protected SkillBuilder builder(int...vars) {
      if (vars == null) vars = new int[0];
      return Skill.builder("Evadable Strike")
          .setDescription("An attack that can be evaded but not opposed.")
          .setTarget(Target.CLOSE_ENEMY)
          .setCooldown(Duration.ofSeconds(4))
          .addEffect(StatusLibrary.WOUND_EVADABLE.modify()
              .setStackSize(vars.length > 0 ? vars[0] : 1).build());
    }
  },
  
  /**
   * An attacking skill that inflicts a wound on the closest enemy if it doesn't
   * have enough stacks of the {@link StatusLibrary#OPPOSITION OPPOSITION}
   * status.
   */
  STRIKE_OPPOSABLE {

    /**
     * {@inheritDoc} In the case {@link SkillLibrary#STRIKE_OPPOSABLE
     * STRIKE_OPPOSABLE}, the first given {@code int} value sets the stack size
     * of the {@link StatusLibrary#WOUND_OPPOSABLE WOUND_OPPOSABLE} status that
     * the skill inflicts. The default stack size is {@code 1}.
     */
    @Override // from SkillLibrary
    public Skill get(int...vars) {return super.get(vars);}
    
    @Override // from SkillLibrary
    protected SkillBuilder builder() {return builder(null);}

    @Override // from SkillLibrary
    protected SkillBuilder builder(int...vars) {
      if (vars == null) vars = new int[0];
      return Skill.builder("Opposable Strike")
          .setDescription("An attack that can be opposed but not evaded.")
          .setTarget(Target.CLOSE_ENEMY)
          .setCooldown(Duration.ofSeconds(4))
          .addEffect(StatusLibrary.WOUND_OPPOSABLE.modify()
              .setStackSize(vars.length > 0 ? vars[0] : 1).build());
      }
  };
  
  /**
   * Returns the {@link Skill} this enumerated value represents. The supplied
   * builder uses the default parameters for the skill.
   * 
   * @return skill this enumerated value represents.
   */
  public Skill get() {
    return modify().build();
  }
  
  /**
   * Returns the {@link Skill} this enumerated value represents using given
   * {@code int} values to potentially modify its parameters. Which parameters
   * are modified are specific to the skill. When given a {@code null} value of
   * an empty array of {@code int} values, the returned builder is for a skill
   * with defaults identical to {@link #get()}.
   * 
   * @param vars
   *          values to be modified.
   * @return skill this enumerated value represents.
   */
  public Skill get(int...vars) {
    return modify(vars).build();
  }
  
  /**
   * Returns a {@link SkillBuilder} object for making the skill this enumerated
   * value represents. The supplied builder uses the default parameters for the
   * skill.
   * 
   * @return fully prepared builder object for the skill this enumerated value
   *         represents.
   */
  public SkillBuilder modify() {
    return adjust(builder());
  }

  /**
   * Returns a {@link SkillBuilder} object for making the skill this enumerated
   * value represents using given {@code int} values to potentially modify its
   * parameters. Which parameters are modified are specific to the skill. When
   * given a {@code null} value of an empty array of {@code int} values, the
   * returned builder is for a skill with defaults identical to
   * {@link #modify()}.
   * 
   * @param vars
   *          values to be modified.
   * @return fully prepared builder object for the skill this enumerated value
   *         represents.
   */
  public SkillBuilder modify(int...vars) {
    return adjust(builder(vars));
  }
  
  /**
   * Returns a {@link SkillBuilder} object for making the skill this enumerated
   * value represents. The supplied builder uses the default parameters for the
   * skill. The supplied builder should lack any parameters or listener objects
   * common to all skills.
   * 
   * @return basic builder object for the skill this enumerated value
   *         represents.
   * @see #modify()
   */
  protected abstract SkillBuilder builder();
  
  /**
   * Returns a {@link SkillBuilder} object for making the skill this enumerated
   * value represents using given {@code int} values to potentially modify its
   * parameters. Which parameters are modified are specific to the skill. When
   * given a {@code null} value of an empty array of {@code int} values, the
   * returned builder is for a skill with defaults identical to
   * {@link #builder()}. The supplied builder should lack any parameters or
   * listener objects common to all skills.
   * 
   * @param vars
   *          values to be modified.
   * @return basic builder object for the skill this enumerated value
   *         represents.
   * @see #modify(int...)
   */
  protected SkillBuilder builder(int...vars) {
    return builder();
  }
  
  /**
   * Modifies all outgoing skills and skill builders with configurations common
   * to all.
   * 
   * @param unadjusted
   *          builder for a skill that is not yet configured.
   * @return builder configured with changes common to all skills.
   */
  private SkillBuilder adjust(SkillBuilder unadjusted) {
    return unadjusted.addListener(PrintLogger.get().getSkillLogger());
  }

}
