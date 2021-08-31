/*
 * This class defines the dialog for choosing an attribute of another
 * participant for the effect rule info form
 */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.AttributeTypes;
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

public class OtherParticipantAttributeDialog extends JDialog implements
    ActionListener {
  private Vector<ActionTypeParticipant> participants; // participants to choose 
  																										// from
  private JTextArea echoedField; // echoed text field in button pad gui
  private int attributeType; // type of the attribute in focus
  private boolean trimWhitespace; // whether or not to trim the trailing 
  																// whitespace b4 appending to the text field

  private JComboBox activeList; // list of active statuses
  private JComboBox partList; // list of participants
  private JComboBox attList; // list of attributes
  private JButton okButton;
  private JButton cancelButton;

  public OtherParticipantAttributeDialog(JDialog owner, 
  		Vector<ActionTypeParticipant> parts, JTextArea echoedTField, int attType, 
  		boolean trim) {
    super(owner, true);
    setTitle("Other Participant(s) Attribute");

    participants = parts;
    echoedField = echoedTField;
    attributeType = attType;
    trimWhitespace = trim;

    // main pane:
    Box mainPane = Box.createVerticalBox();

    // bottom pane (had to put it first because refreshAttList() references the
    // okButton):
    JPanel bottomPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    bottomPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    bottomPane.add(cancelButton);

    // top pane:
    JPanel topPane = new JPanel();
    topPane.setLayout(new BoxLayout(topPane, BoxLayout.Y_AXIS));
    JPanel statusPane = new JPanel();
    statusPane.add(new JLabel("Choose status:"));
    topPane.add(statusPane);
    activeList = new JComboBox();
    activeList.addItem("All");
    activeList.addItem("All Active");
    activeList.addItem("All Inactive");
    JPanel activeListPane = new JPanel();
    activeListPane.add(activeList);
    topPane.add(activeListPane);
    JPanel partPane = new JPanel();
    partPane.add(new JLabel("Choose participant:"));
    topPane.add(partPane);
    attList = new JComboBox();
    partList = new JComboBox();
    partList.addActionListener(this);
    JPanel partListPane = new JPanel();
    partListPane.add(partList);
    topPane.add(partListPane);
    refreshPartList();
    JPanel attPane = new JPanel();
    attPane.add(new JLabel("Choose attribute:"));
    topPane.add(attPane);
    JPanel attListPane = new JPanel();
    attListPane.add(attList);
    refreshAttList();
    topPane.add(attListPane);

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
    if (source == partList) { // a participant has been selected
      refreshAttList();
    } else if (source == cancelButton) {
      setVisible(false);
      dispose();
    } else if (source == okButton) {
      String active = (String) activeList.getSelectedItem();
      String activeString = new String();
      if (active.equals("All")) {
        activeString = "all";
      } else if (active.equals("All Active")) {
        activeString = "allActive";
      } else if (active.equals("All Inactive")) {
        activeString = "allInactive";
      }
      String partString = ((String) partList.getSelectedItem()).replace(' ',
          '-');
      String attString = (String) attList.getSelectedItem();
      // set the text field:
      if (trimWhitespace) {
        setEchoedTextFieldText(echoedField.getText().trim().concat(
            activeString + "-" + partString + ":" + attString + " "));
      } else {
        setEchoedTextFieldText(echoedField.getText().concat(
            activeString + "-" + partString + ":" + attString + " "));
      }
      setVisible(false);
      dispose();
    }
  }

  private void refreshPartList() {
    for (int i = 0; i < participants.size(); i++) {
      ActionTypeParticipant tempPart = participants.elementAt(i);
      Vector<SimSEObjectType> types = tempPart.getAllSimSEObjectTypes();
      for (int j = 0; j < types.size(); j++) { 
      	// add all of this participant's
      	// SimSEObjectTypes to the list:
        SimSEObjectType tempType = types.elementAt(j);
        partList.addItem(tempPart.getName() + " " + tempType.getName() + " "
            + SimSEObjectTypeTypes.getText(tempType.getType()));
      }
    }
  }

  private void refreshAttList() {
    if (attList.getItemCount() > 0) {
      attList.removeAllItems();
    }
    String part = (String) partList.getSelectedItem();
    String partName = part.substring(0, part.indexOf(' '));
    String typeName = part.substring((part.indexOf(' ') + 1), part
        .lastIndexOf(' '));
    // find the participant:
    for (int i = 0; i < participants.size(); i++) {
      ActionTypeParticipant tempPart = participants.elementAt(i);
      if (tempPart.getName().equals(partName)) { // found it
        Vector<Attribute> atts = 
        	tempPart.getSimSEObjectType(typeName).getAllAttributes();
        // add each attribute to the list:
        for (int j = 0; j < atts.size(); j++) {
          Attribute tempAtt = atts.elementAt(j);
          if ((attributeType == AttributeTypes.INTEGER) || 
          		(attributeType == AttributeTypes.DOUBLE)) { // numerical attribute
            if ((tempAtt.getType() == AttributeTypes.INTEGER) || 
            		(tempAtt.getType() == AttributeTypes.DOUBLE)) { // numerical
            																										// attribute 
            																										// also
              attList.addItem(tempAtt.getName());
            }
          } else if (attributeType == AttributeTypes.STRING) { // string 
          																										 // attribute
            if (tempAtt.getType() == AttributeTypes.STRING) { // string 
            																									// attribute
                                                            	// also
              attList.addItem(tempAtt.getName());
            }
          } else if (attributeType == AttributeTypes.BOOLEAN) { // boolean
                                                              	// attribute
            if (tempAtt.getType() == AttributeTypes.BOOLEAN) { // boolean
                                                             	 // attribute also
              attList.addItem(tempAtt.getName());
            }
          }
        }
        break;
      }
    }
    if (attList.getItemCount() == 0) { // empty attribute list
    	// don't allow the user to press ok with an empty attribute selection:
      okButton.setEnabled(false); 
    } else {
      okButton.setEnabled(true);
    }
    pack();
    validate();
    repaint();
  }

  // sets the echoed field to the specified text
  private void setEchoedTextFieldText(String text) { 
    echoedField.setText(text);
  }
}