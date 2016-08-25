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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import java.util.Iterator;
import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//@todo remove or replace.
/**
 * A log of an ongoing battle. Elements of the current battle time, the state of
 * the units involved, and other details are appended to the bottom of the log
 * as calls are made. This object can be inserted into a scene for the display
 * to the user.
 * @author Andrew M. Teller (andrew.m.teller@gmail.com)
 */
public class BattleLog extends VBox {
  
  /**
   * Defines a border for the various log of the state elements.
   */
  private static final Border LOG_BORDER = new Border(
    new BorderStroke(
      Paint.valueOf("#000000"),
      BorderStrokeStyle.SOLID,
      new CornerRadii(4),
      BorderWidths.DEFAULT
    )
  );

  /**
   * Getter for the Pane object that contains a display for all events and
   * state changes in the battle.
   * @return this BattleLog as a Pane object.
   */
  public Pane getBattleLog() {
    return this;
  }
    
  /**
   * Appends to the battle log Pane a header that displays the time of the
   * battle.
   * @param turnOrder TurnOrder object containing the current turns and time of
   *        of the battle.
   */
  public void logClock(TurnOrder turnOrder) {
    //Defines the outer pane that padds the clock title.
    HBox titleBox = new HBox();
    titleBox.setPadding(new Insets(5, 5, 0, 5));
    //Defines the inner pane for the clock title and gives iterateUnits a border.
    HBox clockBox = new HBox();
    clockBox.setPadding(new Insets(1, 5, 1, 5));
    clockBox.setStyle("-fx-background-color: #000000;");
    titleBox.getChildren().add(clockBox);
    //The actual clock time.
    Text clockTime = new Text("" + turnOrder.getClock() + "ms Into battle.");
    clockTime.setFont(Font.font(null, FontWeight.BOLD, 16));
    clockTime.setFill(Paint.valueOf("#ffffff"));
    clockBox.getChildren().add(clockTime);
    this.getChildren().add(titleBox);
  }

  /**
   * Takes in the text and appends iterateSkills to the battle log Pane object.
   * @param text the String object to insert into the battle log.
   */
  public void logText(String text) {
    //Defines the outer pane that pads the inner objects.
    HBox textBox = new HBox();
    textBox.setPadding(new Insets(5, 5, 0, 5));
    //Adds text to the padded pane.
    textBox.getChildren().add(new Text(text));
    //Adds the padded pane to the log.
    this.getChildren().add(textBox);
  }
  
  /**
   * Adds various nodes to the battle log Pane object for the display of the
   * current state of all units on the battlefield.
   * @param battleField object for retrieving the state of all Unit objects in
   *        the battle.
   */
  public void logState(BattleField battleField) {
    Iterator<Unit> unitList = battleField.getAllUnits();
    HBox teamBox = new HBox();
    VBox goldBox = new VBox();
    teamBox.getChildren().add(goldBox);
    VBox silverBox = new VBox();
    teamBox.getChildren().add(silverBox);
    //Loop that iterates through all the units and logs their state.
    while (unitList.hasNext()) {
      Unit unit = unitList.next();
      //Defines the outer pane that pads the inner objects.
      HBox unitBox = new HBox();
      unitBox.setPadding(new Insets(5, 5, 0, 5));
      //Defines the inner pane for the unit's name and gives iterateUnits a border.
      HBox nameBox = new HBox();
      nameBox.setPadding(new Insets(1, 5, 1, 5));
      nameBox.setBorder(LOG_BORDER);
      unitBox.getChildren().add(nameBox);
      //The actual name display for the units name. In bold. '*' added for units
      //that are stunned.
      Text name = new Text(unit.getName());
      if (unit.isStunned()) name.setText(name.getText() + "*");
      name.setFont(Font.font(null, FontWeight.BOLD, 10));
      nameBox.getChildren().add(name);
      //Creates boxes and text for every status the unit has.
      Iterator<Status> statusList = unit.getStatuses();
      while (statusList.hasNext()) {
        Status s = statusList.next();
        if (!s.isHidden()) {
          HBox statusBox = new HBox();
          statusBox.setPadding(new Insets(1, 5, 1, 5));
          statusBox.setBorder(LOG_BORDER);
          Text statusName = new Text(s.getName());
          if (s.getName().equals("Wounded")) {
            statusName.setFill(Paint.valueOf("FF0000"));
          }
          if (s.getStacks() > 1) {
            statusName.setText(String.valueOf(s.getStacks()) + "x " + statusName.getText());
          }
          statusName.setFont(Font.font(null, FontWeight.BOLD, 10));
          statusBox.getChildren().add(statusName);
          if (s.getDuration() > 0) {
            Text statusDuration = new Text(" " + s.getDuration() + "ms");
            statusDuration.setFont(Font.font(null, FontWeight.NORMAL, 9));
            statusBox.getChildren().add(statusDuration);
          }
          unitBox.getChildren().add(statusBox);
        }
      }
      //Finishing touches to the unit names box.
      nameBox.setPrefWidth(120);
      //Adds the unit to the proper team.
      if (battleField.getTeam(unit) == Team.GOLD) {
        nameBox.setStyle("-fx-background-color: #DAA520;");
        goldBox.getChildren().add(unitBox);
      }
      else if (battleField.getTeam(unit) == Team.SILVER) {
        nameBox.setStyle("-fx-background-color: #C0C0C0;");
        silverBox.getChildren().add(unitBox);
      }
    }
    //Adds the team boxes to this log.
    this.getChildren().add(teamBox);
  }
  
}