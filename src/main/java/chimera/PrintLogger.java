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

import java.io.PrintStream;

import core.Fighter;
import core.FighterHandler;
import core.Skill;
import core.SkillHandler;
import core.Status;
import core.StatusHandler;

/**
 * Generates various handler objects that output filtered events to a
 * {@link PrintStream}. The current stream is set to {@link System.out}.
 * 
 * @author Andrew M. Teller (https://github.com/AndrewMiTe)
 */
public class PrintLogger {

  /**
   * Singelton instance of a print logger.
   */
  private static final PrintLogger instance = new PrintLogger();
  
  /**
   * The stream to output to.
   */
  private PrintStream output;

  /**
   * Implements the logger with {@link System.out} as the output.
   */
  private PrintLogger() {
    output = System.out;
  }

  public static PrintLogger get() {
    return instance;
  }
  
  /**
   * @return the output stream the print logger uses.
   */
  public PrintStream out() {
    return output;
  }

  /**
   * @return {@link StatusHandler} object set to print events to loggers output
   * stream
   */
  public StatusHandler getStatusLogger() {
    return new StatusLogger();
  }

  /**
   * Return value for getStatusLogger().
   */
  private class StatusLogger implements StatusHandler {

    @Override // from StatusHandler
    public void onStatusApplication(Status status) {
      out().println(status + " applied to " + status.getOwner());
    }

    @Override // from StatusHandler
    public void onStatusRemoval(Status status) {
      out().println(status + " removed from " + status.getOwner());
    }

  }

  /**
   * @return {@link SkillHandler} object set to print events to loggers output
   * stream
   */
  public SkillHandler getSkillLogger() {
    return new SkillLogger();
  }

  /**
   * Return value for getSkillLogger().
   */
  private class SkillLogger implements SkillHandler {

    @Override // from SkillHandler
    public void onSkillExecution(Skill skill) {
      out().println(skill + " executed by " + skill.getOwner());
    }

  }

  /**
   * @return {@link FighterHandler} object set to print events to loggers output
   * stream
   */
  public FighterHandler getFighterLogger() {
    return new FighterLogger();
  }

  /**
   * Return value for getFighterLogger().
   */
  private class FighterLogger implements FighterHandler {

    @Override // from FighterHandler
    public void onDefeated(Fighter fighter) {
      out().println(fighter + " has been defeated.");
    }

  }

}
