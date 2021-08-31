/*
 * This class defines the dialog for choosing a number of actions for the
 * participant in focus for the effect rule info form
 */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class NumActionsThisPartDialog extends JDialog implements 
ActionListener {
  private ActionTypeParticipant participant; // participant in focus
  private SimSEObjectType objType; // object type in focus
  private DefinedActionTypes actions; // defined action types
  private JTextArea echoedField; // text field echoed in the button pad GUI
  private boolean trimWhitespace; // whether or not to trim trailing whitespace
                                  // in the text field b4 appending to it

  private JComboBox activeList; // list of active statuses
  private JComboBox actionList; // list of actions
  private JButton okButton;
  private JButton cancelButton;

  public NumActionsThisPartDialog(JDialog owner,
      ActionTypeParticipant participantInFocus,
      SimSEObjectType objectTypeInFocus, DefinedActionTypes acts,
      JTextArea echoedTField, boolean trim) {
    super(owner, true);
    setTitle("Num Actions This Participant");

    participant = participantInFocus;
    objType = objectTypeInFocus;
    actions = acts;
    echoedField = echoedTField;
    trimWhitespace = trim;

    // main pane:
    Box mainPane = Box.createVerticalBox();

    // top pane:
    JPanel topPane = new JPanel();
    topPane.setLayout(new BoxLayout(topPane, BoxLayout.Y_AXIS));
    JPanel statusPane = new JPanel();
    statusPane.add(new JLabel("Choose status:"));
    topPane.add(statusPane);
    activeList = new JComboBox();
    activeList.addItem("All");
    activeList.addItem("Active");
    activeList.addItem("Inactive");
    JPanel activeListPane = new JPanel();
    activeListPane.add(activeList);
    topPane.add(activeListPane);
    JPanel actionPane = new JPanel();
    actionPane.add(new JLabel("Choose action:"));
    topPane.add(actionPane);
    actionList = new JComboBox();
    JPanel actionListPane = new JPanel();
    actionListPane.add(actionList);
    topPane.add(actionListPane);
    refreshActionList();

    // bottom pane
    JPanel bottomPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    bottomPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    bottomPane.add(cancelButton);

    // Add panes to main pane:
    mainPane.add(topPane);
    mainPane.add(bottomPane);

    // Set main window frame properties:
    setBackground(Color.black);
    setContentPane(mainPane);
    validate();
    pack();
    repaint();
    toFront();
    // Make it show up in the center of the screen:
    Point ownerLoc = owner.getLocationOnScreen();
    Point thisLoc = new Point();
    thisLoc.setLocation((ownerLoc.getX() + (owner.getWidth() / 2) - (this
        .getWidth() / 2)), (ownerLoc.getY() + (owner.getHeight() / 2) - (this
        .getHeight() / 2)));
    setLocation(thisLoc);
    setVisible(true);
  }

  // handles user actions
  public void actionPerformed(ActionEvent evt) { 
    Object source = evt.getSource();
    if (source == cancelButton) {
      setVisible(false);
      dispose();
    } else if (source == okButton) {
      String active = (String) activeList.getSelectedItem();
      String activeString = new String();
      if (active.equals("All")) {
        activeString = "numActionsThis";
      } else if (active.equals("Active")) {
        activeString = "numActiveActionsThis";
      } else if (active.equals("Inactive")) {
        activeString = "numInactiveActionsThis";
      }
      String partString = participant.getName();
      String typeString = (objType.getName() + "-" + SimSEObjectTypeTypes
          .getText(objType.getType()));
      String actionString = (String) actionList.getSelectedItem();
      if (actionString.equals("* (any)")) {
        actionString = "*";
      }
      // set the text field:
      if (trimWhitespace) {
        setFocusedTextFieldText(echoedField.getText().trim().concat(
            activeString + "-" + partString + "-" + typeString + ":"
                + actionString + " "));
      } else {
        setFocusedTextFieldText(echoedField.getText().concat(
            activeString + "-" + partString + "-" + typeString + ":"
                + actionString + " "));
      }
      setVisible(false);
      dispose();
    }
  }

  private void refreshActionList() {
    actionList.addItem("* (any)");
    Vector<ActionType> acts = actions.getAllActionTypes();
    for (int i = 0; i < acts.size(); i++) {
      actionList.addItem(acts.elementAt(i).getName());
    }
  }
  
  // sets echoed text field to the specified text
  private void setFocusedTextFieldText(String text) { 
    echoedField.setText(text);
  }
}