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

import core.Status;
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
      public void onStatusApplication(Status status) {
        status.getOwner().removeStatus(EVASION.get());
        status.getOwner().removeStatus(OPPOSITION.get());
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
      public void onStatusApplication(Status status) {
        status.getOwner().removeStatus(DEFEATED.get());
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
  };

  /**
   * Returns a new copy of the status the enumerated object represents.
   * @return the new status.
   */
  abstract public Status get();
  
}
