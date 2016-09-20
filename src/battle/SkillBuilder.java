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
import java.util.function.Predicate;


/**
 * Constructs {@link Skill} objects using a builder pattern. All setter methods
 * return the instance of the object it is called upon. The build method returns
 * a new Skill object using the information obtained through defaults and set
 * methods.
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public class SkillBuilder {

  private String name;
  private String description = "";
  private Target target = Target.SELF;
  private Duration maxCooldown = Duration.ofSeconds(5);
  /**
   * This default Predicate returns {@code true} if the fighter is not stunned
   * or defeated.
   */
  private Predicate<Skill> usablity = a -> true;

  public SkillBuilder(String name) {
    this.name = name;
  }

  public SkillBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public SkillBuilder setDescription(String description) {
    this.description = description;
    return this;
  }

  public SkillBuilder setTarget(Target target) {
    this.target = target;
    return this;
  }

  public SkillBuilder setMaxCooldown(Duration maxCooldown) {
    this.maxCooldown = maxCooldown;
    return this;
  }

  public SkillBuilder setUsablity(Predicate<Skill> usablity) {
    this.usablity = usablity;
    return this;
  }
  
  /**
   * Sets this so that the skill built would qualify as a pre-battle skill.
   * @return this.
   */
  public SkillBuilder setAsPreBattleSkill() {
    this.maxCooldown = Duration.ofSeconds(-1);
    this.target = Target.SELF;
    return this;
  }

  public Skill build() {
    return new Skill(name, description, target, maxCooldown, usablity, null,
        null, null, null);
  }
  
}
