/*
 * This class defines the dialog for choosing a number of actions that another
 * participant is in for the effect rule info form
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

public class NumActionsOtherPartDialog extends JDialog implements
    ActionListener {
  private Vector<ActionTypeParticipant> participants; // participants to choose 
  																										// from
  private DefinedActionTypes actions; // currently defined action types
  private JTextArea echoedField; // echoed field in ButtonPadGUI to edit
  private boolean trimWhitespace; // whether or not to trim the trailing
                                  // whitespace in the textfield b4 appending to
                                  // it

  private JComboBox activeList; // list of active statuses
  private JComboBox partList; // list of participants
  private JComboBox actionList; // list of action types
  private JButton okButton;
  private JButton cancelButton;

  public NumActionsOtherPartDialog(JDialog owner, 
  		Vector<ActionTypeParticipant> parts, DefinedActionTypes acts, 
  		JTextArea echoedTField, boolean trim) {
    super(owner, true);
    setTitle("Num Actions");

    participants = parts;
    actions = acts;
    echoedField = echoedTField;
    trimWhitespace = trim;

    // main pane:
    Box mainPane = Box.createVerticalBox();

    // top pane:
    JPanel topPane = new JPanel();
    topPane.setLayout(new BoxLayout(topPane, BoxLayout.Y_AXIS));
    JPanel panelA = new JPanel();
    panelA.add(new JLabel("Choose status:"));
    topPane.add(panelA);
    activeList = new JComboBox();
    activeList.addItem("All");
    activeList.addItem("Active");
    activeList.addItem("Inactive");
    JPanel panelB = new JPanel();
    panelB.add(activeList);
    topPane.add(panelB);
    JPanel panelC = new JPanel();
    panelC.add(new JLabel("Choose participant:"));
    topPane.add(panelC);
    partList = new JComboBox();
    JPanel panelD = new JPanel();
    panelD.add(partList);
    topPane.add(panelD);
    refreshPartList();
    JPanel panelF = new JPanel();
    panelF.add(new JLabel("Choose action:"));
    topPane.add(panelF);
    actionList = new JComboBox();
    JPanel panelG = new JPanel();
    panelG.add(actionList);
    topPane.add(panelG);
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
        activeString = "numActionsAll";
      } else if (active.equals("Active")) {
        activeString = "numActionsAllActive";
      } else if (active.equals("Inactive")) {
        activeString = "numActionsAllInactive";
      }
      String partString = ((String) partList.getSelectedItem()).replace(' ',
          '-');
      String actionString = (String) actionList.getSelectedItem();
      if (actionString.equals("* (any)")) {
        actionString = "*";
      }
      // set the text field:
      if (trimWhitespace) {
        setFocusedTextFieldText(echoedField.getText().trim().concat(
            activeString + "-" + partString + ":" + actionString + " "));
      } else {
        setFocusedTextFieldText(echoedField.getText().concat(
            activeString + "-" + partString + ":" + actionString + " "));
      }
      setVisible(false);
      dispose();
    }
  }

  private void refreshPartList() {
    for (int i = 0; i < participants.size(); i++) {
      ActionTypeParticipant tempPart = participants.elementAt(i);
      // add participant name:
      partList.addItem(tempPart.getName());
      Vector<SimSEObjectType> types = tempPart.getAllSimSEObjectTypes();
      for (int j = 0; j < types.size(); j++) { 
      	// add all of this participant's SimSEObjectTypes to the list:
        SimSEObjectType tempType = types.elementAt(j);
        partList.addItem(tempPart.getName() + " " + tempType.getName() + " "
            + SimSEObjectTypeTypes.getText(tempType.getType()));
      }
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