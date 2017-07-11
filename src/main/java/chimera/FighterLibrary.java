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

import core.Fighter;
import core.FighterBuilder;

/**
 * A library of pre-generated fighters used primarily for testing.
 * 
 * @author Andrew M. Teller (https://github.com/AndrewMiTe)
 */
public enum FighterLibrary {
  
  WASHINGTON {
    @Override // from FighterLibrary
    public Fighter get() {
      return new FighterBuilder("Washington")
          .addSkill(SkillLibrary.PRE_BATTLE.get(2, 3, 4))
          .addSkill(SkillLibrary.STRIKE_EVADABLE.get())
          .build();
    }
  },
  
  JEFFERSON {
    @Override // from FighterLibrary
    public Fighter get() {
      return new FighterBuilder("Jefferson")
          .addSkill(SkillLibrary.PRE_BATTLE.get(4, 3, 2))
          .addSkill(SkillLibrary.STRIKE_OPPOSABLE.get())
          .build();
    }
  },
  
  ADAMS {
    @Override // from FighterLibrary
    public Fighter get() {
      return new FighterBuilder("Adams")
          .addSkill(SkillLibrary.PRE_BATTLE.get(4, 2, 3))
          .addSkill(SkillLibrary.STRIKE_OPPOSABLE.get())
          .build();
    }
  },
  
  HAMILTON {
    @Override // from FighterLibrary
    public Fighter get() {
      return new FighterBuilder("Hamilton")
          .addSkill(SkillLibrary.PRE_BATTLE.get(2, 4, 3))
          .addSkill(SkillLibrary.STRIKE_EVADABLE.get())
          .build();
    }
  };
  
  /**
   * Returns the fighter the enumerated value represents.
   * 
   * @return the fighter object.
   */
  public abstract Fighter get();

}
