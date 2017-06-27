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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import core.Fighter;
import core.Status;
import core.StatusBuilder;
import core.StatusHandler;

/**
 * Enumerates the various statuses specific to the Chimera Saga battle system
 * and provides a method for retrieving a new copy of the status.
 * 
 * @author Andrew M. Teller(https://github.com/AndrewMiTe)
 */
public enum StatusLibrary {

  /**
   * A status that defeats and potentially prevents the fighter from performing
   * more skills. If all the fighters on a team are defeated, the team loses the
   * battle, which grants victory to an opposing team. The application of the
   * Endurance status removes defeated. Removes the fighters evasion and
   * opposition statuses upon application.
   */
  DEFEATED {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Defeated")
          .setDescription("You are unable to continue the fight.")
          .setAsInfinite()
          .setStackable(false)
          .setDefeating(true)
          .addListener(new DefeatedHandler())
          .build();
    }
    class DefeatedHandler implements StatusHandler {
      @Override // from StatusHandler
      public void onStatusApplication(Status defeated) {
        defeated.getOwner().removeStatus(ENDURANCE.get());
        defeated.getOwner().removeStatus(EVASION.get());
        defeated.getOwner().removeStatus(OPPOSITION.get());
      }
    }
  },

  /**
   * A primary defense status that often must be removed before the fighter can
   * be defeated. Endurance represents the fighters ability to shrug off an
   * attack or deflect it with its armor after a failure to either evade or
   * oppose it. The application of this status removes the defeated status.
   */
  ENDURANCE {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Endurance")
          .setDescription("A primary defense allowing you to endure attacks.")
          .setAsInfinite()
          .setStackable(true)
          .addListener(new EnduranceHandler())
          .build();
    }
    class EnduranceHandler implements StatusHandler {
      @Override // from StatusHandler
      public void onStatusApplication(Status endurance) {
        endurance.getOwner().removeStatus(DEFEATED.get());
      }
    }
  },

  /**
   * A primary defense status that often must be removed before the fighter can
   * be defeated. Some statuses that defeat fighters may ignore this status
   * entirely. Evasion represents the fighters ability to avoid an incoming
   * attack.
   */
  EVASION {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Evasion")
          .setDescription("A primary defense allowing you to evade attacks.")
          .setAsInfinite()
          .setStackable(true)
          .build();
    }
  },

  /**
   * A status that decrements the owners evasion and opposition and refreshes 
   * its duration whenever it is up for removal. This represents the fatigue of
   * fighting and ensures that fighters will be defeated, and thus the battle
   * will end. 
   */
  FATIGUE {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Fatigue")
          .setDescription("Removes your evasion and opposition over time.")
          .setDuration(Duration.ofSeconds(6))
          .setStackable(false)
          .setRemoveCondition(new RemoveCondition())
          .build();
    }
    class RemoveCondition implements Predicate<Fighter> {
      @Override // from Predicate
      public boolean test(Fighter owner) {
        if (owner.hasStatus(EVASION.get())) {
          owner.getStatus(EVASION.get()).removeStacks(1);
        }
        if (owner.hasStatus(OPPOSITION.get())) {
          owner.getStatus(OPPOSITION.get()).removeStacks(1);
        }
        Status fatigue = owner.getStatus(FATIGUE.get());
        fatigue.combineWith(new Status(fatigue));
        return false;
      }
    }
  },

  /**
   * A primary defense status that often must be removed before the fighter can
   * be defeated. Some statuses that defeat fighters may ignore this status
   * entirely. Opposition represents the fighters ability to block, parry, or
   * otherwise counter an attack.
   */
  OPPOSITION {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Opposition")
          .setDescription("A primary defense allowing you to oppose attacks.")
          .setAsInfinite()
          .setStackable(true)
          .build();
    }
  },
  
  /**
   * This status is used to randomize (or stagger) the timing of the fighter's
   * actions. Typically this status is applied to all fighters in the pre-battle
   * phase of combat and uses the stunned parameter to keep fighters from
   * acting or decrementing their skill cooldowns. The stagger duration is a
   * random number of milliseconds from 0 to 3 second. 
   */
  STAGGER {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Stagger")
          .setDescription("The time it takes for you to act for the first time in battle.")
          .setDuration(Duration.ofMillis(ThreadLocalRandom.current().nextInt(3000)))
          .setStackable(true)
          .setStunning(true)
          .build();
    }
  },
  
  /**
   * An infliction status that removes stacks of endurance equal to the stack
   * size of this infliction. If the target owner has fewer stacks of endurance
   * then this infliction, this applies the defeated status to the target.
   */
  WOUND {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Wound")
          .setDescription("A lethal wound that either removes endurance or defeats.")
          .setAsInstant()
          .setStackable(true)
          .addListener(new WoundHandler())
          .build();
    }
    class WoundHandler implements StatusHandler {
      public void onStatusApplication(Status wound) {
        Fighter target = wound.getOwner();
        if (target.hasStatus(ENDURANCE.get()) 
            && target.getStatus(ENDURANCE.get()).getStackSize() >= wound.getStackSize()) {
          target.getStatus(ENDURANCE.get()).removeStacks(wound.getStackSize());
        }
        else {
          target.applyStatus(DEFEATED.get());
        }
      }
    }
  },
  
  /**
   * An infliction status that wounds its target owner if the target has fewer
   * stacks of opposition then the stack size of this infliction. The stack size
   * of the wound is equal to the unopposed stacks of this infliction.
   */
  WOUND_EVADABLE {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Evadable Wound")
          .setDescription("A lethal wound that might defeat you if it is not evaded.")
          .setAsInstant()
          .setStackable(true)
          .addListener(new WoundHandler())
          .build();
    }
    class WoundHandler implements StatusHandler {
      public void onStatusApplication(Status wound) {
        Fighter target = wound.getOwner();
        if (target.hasStatus(EVASION.get())) {
          int diff = wound.getStackSize() - target.getStatus(EVASION.get()).getStackSize();
          if (diff > 0) {
            target.applyStatus(WOUND.modify().setStackSize(diff).build());
          }
        }
      }
    }
  },
   
  /**
   * An infliction status that wounds its target owner if the target has fewer
   * stacks of opposition then the stack size of this infliction. The stack size
   * of the wound is equal to the unopposed stacks of this infliction.
   */
  WOUND_OPPOSABLE {
    @Override // from StatusLibrary
    public Status get() {
      return Status.builder("Opposable Wound")
          .setDescription("A lethal wound that might defeat you if it is not opposed.")
          .setAsInstant()
          .setStackable(true)
          .addListener(new WoundHandler())
          .build();
    }
    class WoundHandler implements StatusHandler {
      public void onStatusApplication(Status wound) {
        Fighter target = wound.getOwner();
        if (target.hasStatus(OPPOSITION.get())) {
          int diff = wound.getStackSize() - target.getStatus(OPPOSITION.get()).getStackSize();
          if (diff > 0) {
            target.applyStatus(WOUND.modify().setStackSize(diff).build());
          }
        }
      }
    }
  };

  /**
   * Returns a new copy of the status the enumerated object represents.
   * 
   * @return the new status.
   */
  abstract public Status get();
  
  /**
   * Returns a StatusBuilder object for making a status whose defaults match the
   * status returned from get().
   *  
   * @return a builder for modifying this status.
   */
  public StatusBuilder modify() {
    return new StatusBuilder(this.get());
  }
  
}
