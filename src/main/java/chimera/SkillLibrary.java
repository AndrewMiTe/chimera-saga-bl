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
   * An attacking skill that inflicts a wound on the closest enemy if it doesn't
   * have enough stacks of the evasion status.
   */
  STRIKE_EVADABLE {
    @Override // from SkillLibrary
    public Skill get() {return get(1);}
    @Override // from SkillLibrary
    public Skill get(int stacks) {
      return Skill.builder("Evadable Strike")
          .setDescription("An attack that can be evaded but not opposed.")
          .setTarget(Target.CLOSE_ENEMY)
          .setCooldown(Duration.ofSeconds(4))
          .addEffect(StatusLibrary.WOUND_EVADABLE.modify().setStackSize(stacks).build())
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
    @Override // from SkillLibrary
    public Skill get(int stacks) {
      return Skill.builder("Opposable Strike")
          .setDescription("An attack that can be opposed but not evaded.")
          .setTarget(Target.CLOSE_ENEMY)
          .setCooldown(Duration.ofSeconds(4))
          .addEffect(StatusLibrary.WOUND_OPPOSABLE.modify().setStackSize(stacks).build())
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
   * given value setting the magnitude of the effect it applies. For some
   * skills, this method is no different then calling {@link #get()}. This method
   * should throw an {@link IllegalArgumentException} if the given value is < 1.
   * 
   * @return the new status.
   */
  abstract public Skill get(int stacks);
  
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
   * @return a builder for modifying this status.
   */
  public SkillBuilder modify(int stacks) {
    return new SkillBuilder(this.get(stacks));
  }

}
