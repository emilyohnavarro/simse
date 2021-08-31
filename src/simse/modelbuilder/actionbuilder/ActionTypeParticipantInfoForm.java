/* This class defines a form for entering info about an action participant */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class ActionTypeParticipantInfoForm extends JDialog implements
    ActionListener {
  private ActionType actionInFocus; // original action type whose participant is
                                    // being edited
  private Vector<ActionTypeTrigger> triggers; // copy of triggers for this 
  																						// action type
  private Vector<ActionTypeDestroyer> destroyers; // copy of destroyers for this
  																								// action type
  private ActionTypeParticipant participant; // copy of participant whose values
                                             // are being edited
  private ActionTypeParticipant originalParticipant; // participant (as it was
                                                     // upon entry to this
                                                     // constructor) whose
                                                     // values are being
  // edited
  private DefinedObjectTypes objects; // data structure for holding all of the
                                      // defined SimSE object types
  private boolean newParticipant = true; // whether or not this is a newly
                                         // created participant (true) or one
                                         // being edited (false)

  private JTextField nameField; // for entering a name for the participant
  private JComboBox guardList; // for choosing a guard
  private JTextField quantityTextField; // for entering the quantity of the
                                        // participant
  private JTextField maxValTextField; // for entering the max value of the
                                      // quantity of the participant
  private JComboBox metaTypeList; // for choosing the meta-type
  private Vector<JCheckBox> checkBoxes; // vector of JCheckBoxes, each one 
  																			// representing a type
  private JPanel checkBoxesPane; // pane for checkboxes
  private Box middlePane;
  private JPanel bottomPane;
  private JButton okButton; // for ok'ing the info entered in this form
  private JButton cancelButton; // for cancelling this form
  private Box mainPane;
  private GridLayout checkBoxesLayout;
  private JPanel topPane;

  public ActionTypeParticipantInfoForm(JFrame owner, ActionType actionInFocus,
      ActionTypeParticipant part, DefinedObjectTypes objects) {
    super(owner, true);
    this.actionInFocus = actionInFocus;
    this.objects = objects;

    // make a copy of triggers:
    triggers = new Vector<ActionTypeTrigger>();
    Vector<ActionTypeTrigger> tempTrigs = actionInFocus.getAllTriggers();
    for (int i = 0; i < tempTrigs.size(); i++) {
      ActionTypeTrigger tempT = tempTrigs.elementAt(i);
      triggers.add((ActionTypeTrigger) tempT.clone());
    }

    // make a copy of destroyers:
    destroyers = new Vector<ActionTypeDestroyer>();
    Vector<ActionTypeDestroyer> tempDests = actionInFocus.getAllDestroyers();
    for (int i = 0; i < tempDests.size(); i++) {
      ActionTypeDestroyer tempD = tempDests.elementAt(i);
      destroyers.add((ActionTypeDestroyer) tempD.clone());
    }

    // Set window title:
    setTitle("Action Participant Information");

    // Create main panel (box):
    mainPane = Box.createVerticalBox();

    // Create title pane and label:
    JPanel titlePane = new JPanel();
    titlePane.add(new JLabel("Enter participant information:"));

    // Create name pane and components:
    JPanel namePane = new JPanel();
    JLabel nameLabel = new JLabel("Name: ");
    namePane.add(nameLabel);
    nameField = new JTextField(10);
    nameField.addActionListener(this);
    namePane.add(nameField);

    // Create top pane and components:
    topPane = new JPanel();

    // guard list:
    guardList = new JComboBox();
    int[] guards = Guard.getGuards();
    for (int i = 0; i < guards.length; i++) {
      guardList.addItem(Guard.getText(guards[i]));
    }
    guardList.addActionListener(this);
    topPane.add(guardList);

    // quantity text field:
    quantityTextField = new JTextField(6);
    topPane.add(quantityTextField);

    // max val text field:
    maxValTextField = new JTextField(6);
    if (guardList.getSelectedItem() != Guard
        .getText(Guard.AT_LEAST_AND_AT_MOST)) {
      maxValTextField.setEnabled(false); // this field doesn't apply unless the
                                         // guard is "at least/at most"
    }
    topPane.add(maxValTextField);

    // meta-type list:
    metaTypeList = new JComboBox();
    String[] metaTypes = SimSEObjectTypeTypes.getAllTypesAsStrings();
    for (int i = 0; i < metaTypes.length; i++) {
      metaTypeList.addItem(metaTypes[i]);
    }
    metaTypeList.addActionListener(this);
    topPane.add(metaTypeList);

    // Create middle pane and components:
    // Types:
    middlePane = Box.createVerticalBox();
    JPanel titleTypePane = new JPanel();
    JLabel instructionsLabel = new JLabel(
        "That are of type (check all that apply):");
    titleTypePane.add(instructionsLabel);
    middlePane.add(titleTypePane);
    checkBoxesLayout = new GridLayout();
    checkBoxesLayout.setRows(6);
    checkBoxesPane = new JPanel(checkBoxesLayout);
    Vector<SimSEObjectType> objTypes = 
    	objects.getAllObjectTypesOfType(
    			SimSEObjectTypeTypes.getIntRepresentation((String) 
    					(metaTypeList.getSelectedItem())));
    checkBoxesLayout.setColumns((int) (Math.ceil(objTypes.size() / 6)));
    checkBoxes = new Vector<JCheckBox>();
    for (int i = 0; i < objTypes.size(); i++) {
      JPanel tempPane = new JPanel(new BorderLayout());
      JCheckBox tempCheckBox = new JCheckBox(objTypes.elementAt(i).getName());
      tempCheckBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      tempCheckBox.addActionListener(this);
      checkBoxes.add(tempCheckBox);
      tempPane.add(tempCheckBox, BorderLayout.WEST);
      checkBoxesPane.add(tempPane);
    }
    middlePane.add(checkBoxesPane);

    // Create bottom pane & buttons:
    bottomPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    bottomPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    bottomPane.add(cancelButton);

    // initialize participant:
    if (part != null) { // participant being edited
      newParticipant = false;
      // make a copy of participant:
      participant = (ActionTypeParticipant) (part.clone());
      originalParticipant = part;
      initializeForm(); // initialize values to reflect participant being edited
    } else { // new participant
      // create new participant, triggers, and destroyers:
      participant = new ActionTypeParticipant(SimSEObjectTypeTypes
          .getIntRepresentation((String) (metaTypeList.getSelectedItem())));

      for (int i = 0; i < triggers.size(); i++) {
        ActionTypeTrigger trig = triggers.elementAt(i);
        trig.addParticipantTrigger(
        		new ActionTypeParticipantTrigger(participant));
      }
      for (int i = 0; i < destroyers.size(); i++) {
        ActionTypeDestroyer dest = destroyers.elementAt(i);
        dest.addParticipantDestroyer(new ActionTypeParticipantDestroyer(
            participant));
      }
    }

    // set up tool tips:
    setUpToolTips();

    // Add panes to main pane:
    mainPane.add(titlePane);
    mainPane.add(namePane);
    mainPane.add(topPane);
    mainPane.add(middlePane);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator1);
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
    Object source = evt.getSource(); // get which component the action came from
    if (source == guardList) { // User has chosen a guard
      int selectedGuard = Guard.getIntRepresentation((String) guardList
          .getSelectedItem());
      if (selectedGuard == Guard.AT_LEAST_AND_AT_MOST) {
        // enable 2nd text field:
        maxValTextField.setEnabled(true);
      } else { // other guard
        // clear and disable 2nd text field:
        maxValTextField.setText(null);
        maxValTextField.setEnabled(false);
      }
      setUpToolTips();
    }

    else if (source == metaTypeList) { // user has chosen a meta-type
      if (SimSEObjectTypeTypes.getIntRepresentation((String) (metaTypeList
          .getSelectedItem())) != participant.getSimSEObjectTypeType()) { 
      	// a new type is selected (not the type that this participant already
        // was)
        participant.setSimSEObjectTypeType(SimSEObjectTypeTypes
            .getIntRepresentation((String) (metaTypeList.getSelectedItem())));
        refreshCheckBoxes();
      }
    }

    else if (source instanceof JCheckBox) { // one of the check boxes have been
                                          	// checked or unchecked
      // get the text of the check box:
    	JCheckBox sourceCheckBox = (JCheckBox)source;
      String typeName = sourceCheckBox.getText();
      if (sourceCheckBox.isSelected()) { // a check box has just been selected
        // add this object type to the participant:
        participant.addSimSEObjectType(objects.getObjectType(
            SimSEObjectTypeTypes.getIntRepresentation((String) (metaTypeList
                .getSelectedItem())), typeName));
        // add a new empty constraint to the participant's triggers:
        for (int i = 0; i < triggers.size(); i++) {
          ActionTypeTrigger trig = triggers.elementAt(i);
          trig.getParticipantTrigger(participant.getName()).addEmptyConstraint(
              objects.getObjectType(SimSEObjectTypeTypes
                  .getIntRepresentation((String) (metaTypeList
                      .getSelectedItem())), typeName));
        }
        // add a new empty constraint to the participant's destroyer:
        for (int i = 0; i < destroyers.size(); i++) {
          ActionTypeDestroyer dest = destroyers.elementAt(i);
          dest.getParticipantDestroyer(participant.getName())
              .addEmptyConstraint(
                  objects.getObjectType(SimSEObjectTypeTypes
                      .getIntRepresentation((String) (metaTypeList
                          .getSelectedItem())), typeName));
        }
      } else { // a check box has just been de-selected
        // remove the object type from the participant:
        participant.removeSimSEObjectType(sourceCheckBox.getText());
        // remove the constraint from the participant's triggers:
        for (int i = 0; i < triggers.size(); i++) {
          ActionTypeTrigger trig = triggers.elementAt(i);
          trig.getParticipantTrigger(participant.getName()).removeConstraint(
              typeName);
        }
        // remove the constraint from the participant's destroyer:
        for (int i = 0; i < destroyers.size(); i++) {
          ActionTypeDestroyer dest = destroyers.elementAt(i);
          dest.getParticipantDestroyer(participant.getName()).removeConstraint(
              typeName);
        }
      }
    }

    else if (source == cancelButton) { // cancel button has been pressed
      // Close window:
      setVisible(false);
      dispose();
    }

    else if (source == okButton) { // okButton has been pressed
      Vector<String> errors = inputValid(); // check validity of input
      if (errors.size() == 0) { // input valid
        if (participantNameIsUnique()) {
          addEditParticipant();
        }
      } else { // input not valid
        for (int i = 0; i < errors.size(); i++) {
          JOptionPane.showMessageDialog(null, errors.elementAt(i),
          		"Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  // initializes the GUI components to reflect the participant being edited
  private void initializeForm() { 
    // fill in values:

    // name:
    nameField.setText(participant.getName());

    // guard:
    guardList.setSelectedItem(Guard.getText(participant.getQuantity()
        .getGuard()));

    // quantity:
    if (participant.getQuantity().isQuantityBoundless() == false) {
      quantityTextField.setText(participant.getQuantity().getQuantity()[0]
          .toString());
    }
    if (participant.getQuantity().getGuard() == Guard.AT_LEAST_AND_AT_MOST) {
      maxValTextField.setEnabled(true);
      if (participant.getQuantity().getQuantity()[1] != null) {
        maxValTextField.setText(participant.getQuantity().getQuantity()[1]
            .toString());
      }
    }

    // meta-type:
    metaTypeList.setSelectedItem(SimSEObjectTypeTypes.getText(participant
        .getSimSEObjectTypeType()));

    // checkboxes:
    refreshCheckBoxes();
    Vector<SimSEObjectType> types = participant.getAllSimSEObjectTypes();
    for (int i = 0; i < types.size(); i++) {
      String name = types.elementAt(i).getName();
      for (int j = 0; j < checkBoxes.size(); j++) {
        JCheckBox cBox = checkBoxes.elementAt(j);
        if (cBox.getText().equals(name)) {
          cBox.setSelected(true);
        }
      }
    }

    pack();
    validate();
    repaint();
    toFront();
  }

  /*
   * validates the input from the fields; returns null if input is valid, or a
   * Vector of String messages explaining the error(s)
   */
  private Vector<String> inputValid() { 
    Vector<String> messages = new Vector<String>(); // holds any String messages
    																								// about invalid input to 
    																								// display to the user

    // Check name input:
    String nameInput = nameField.getText();
    char[] cArray = nameInput.toCharArray();

    if ((nameInput.equalsIgnoreCase(SimSEObjectTypeTypes
        .getText(SimSEObjectTypeTypes.EMPLOYEE)))
        || (nameInput.equalsIgnoreCase(SimSEObjectTypeTypes
            .getText(SimSEObjectTypeTypes.ARTIFACT)))
        || (nameInput.equalsIgnoreCase(SimSEObjectTypeTypes
            .getText(SimSEObjectTypeTypes.TOOL)))
        || (nameInput.equalsIgnoreCase(SimSEObjectTypeTypes
            .getText(SimSEObjectTypeTypes.CUSTOMER)))
        || (nameInput.equalsIgnoreCase(SimSEObjectTypeTypes
            .getText(SimSEObjectTypeTypes.PROJECT)))
        || (nameInput.equalsIgnoreCase("action"))
        || (nameInput.equalsIgnoreCase("compiler"))) { // System-wide keywords
      messages.add("Name must be unique");
    }

    // Check for length constraints:
    if ((cArray.length < 2) || (cArray.length > 40)) { // user has entered a
                                                     // string shorter than 2
                                                     // chars or longer than 40
                                                     // chars
      messages.add("Name must be between 2 and 40 characters long");
    }

    // Check for invalid characters:
    for (int i = 0; i < cArray.length; i++) {
      if ((Character.isLetter(cArray[i])) == false) { // character is not a 
      																								// letter (hence, invalid)
        messages.add("Name must consist of only alphabetic characters");
        break;
      }
    }

    // Check quantity inputs:
    String quantityInput = quantityTextField.getText();
    String maxValInput = maxValTextField.getText();
    // Check quantity input:
    if ((quantityInput != null) && (quantityInput.length() > 0)) { // field is 
    																															 // not blank
      try {
        int quantity = Integer.parseInt(quantityInput); // parse the string into
                                                        // an int
        if (quantity < 0) {
          messages.add("Quantity must be a valid non-negative integer");
        }
      } catch (NumberFormatException e) {
        messages.add("Quantity must be a valid non-negative integer");
      }
    }
    // Check max value input:
    if ((maxValInput != null) && (maxValInput.length() > 0)) { // field is not
                                                             	 // blank
      try {
        int maxVal = Integer.parseInt(maxValInput); // parse the string into an
                                                    // int
        if (maxVal < 0) {
          messages.add("Maximum value must be a valid non-negative integer");
        }
      } catch (NumberFormatException e) {
        messages.add("Maximum value must be a valid non-negative integer");
      }
    }
    // Check whether max value > min value:
    if ((quantityInput != null) && (quantityInput.length() > 0)
        && (maxValInput != null) && (maxValInput.length() > 0)) { // neither
    																															// field is 
    																															// blank
      try {
        int quantity = Integer.parseInt(quantityInput);
        int maxVal = Integer.parseInt(maxValInput);
        if ((maxVal >= 0) && (quantity >= 0) && (maxVal < quantity)) { 
        	// invalid
          messages
              .add("'At most' value must be greater than or equal to 'at " +
              		"least' value");
        }
      } catch (NumberFormatException e) {
        System.out.println(e.getMessage());
        // Don't actually add a message to messages here because if either of
        // these fields are not valid positive integers, it
        // will have already been checked above
      }
    }

    // Check whether at least one checkbox is checked:
    boolean anyChecked = false;
    for (int i = 0; i < checkBoxes.size(); i++) {
      if (checkBoxes.elementAt(i).isSelected()) {
        anyChecked = true;
        break;
      }
    }
    if (anyChecked == false) {
      messages.add("You must choose at least one type");
    }
    return messages;
  }

  private void addEditParticipant() { // adds/edits the participant in focus
    // set name:
    participant.setName(nameField.getText());

    // set guard:
    participant.getQuantity().setGuard(
        Guard.getIntRepresentation((String) (guardList.getSelectedItem())));

    // set quantity:
    Integer[] newQuantity = new Integer[2];
    if (quantityTextField.getText().equals(null)) { // quantity text field empty
      newQuantity[0] = null;
    } else { // quantity text field non-empty
      try {
        newQuantity[0] = new Integer(Integer.parseInt(quantityTextField
            .getText()));
      } catch (NumberFormatException e) {
        // Note: this exception should never be thrown because inputValid()
        // should have already been called on this
        // input
        System.out.println(e.getMessage());
      }
    }
    if (maxValTextField.getText().equals(null)) { // max val text field empty
      newQuantity[1] = null;
    } else { // max val text field non-empty
      try {
        newQuantity[1] = new Integer(Integer
            .parseInt(maxValTextField.getText()));
      } catch (NumberFormatException e) {
        // Note: this exception should never be thrown because inputValid()
        // should have already been called on this
        // input
        System.out.println(e.getMessage());
      }
    }
    participant.getQuantity().setQuantity(newQuantity);

    if (!newParticipant) { // this is an existing participant being edited
      int indexOfPart = actionInFocus.getAllParticipants().indexOf(
          originalParticipant);
      actionInFocus.removeParticipant(originalParticipant); // remove the old
                                                            // version
      //triggerInFocus.getParticipantTrigger(originalParticipant.getName()).setParticipant(participant);
      // // set the participant for
      // the participant trigger to the copy participant that's been edited
      String name = originalParticipant.getName();
      for (int i = 0; i < triggers.size(); i++) {
        ActionTypeTrigger trig = triggers.elementAt(i);
        /*
         * set the participant for the participant trigger to the copy 
         * participant that's been edited
         */
        trig.getParticipantTrigger(name).setParticipant(participant); 
      }
      for (int i = 0; i < destroyers.size(); i++) {
        ActionTypeDestroyer dest = destroyers.elementAt(i);
        /*
         * set the participant for the participant destroyer to the copy
         * participant that's been edited
         */
        dest.getParticipantDestroyer(name).setParticipant(participant); 
      }
      if (indexOfPart != -1) { // participant was in the action
      	// add the edited participant at its old position:
        actionInFocus.addParticipant(participant, indexOfPart); 
      }
    } else { // new participant
      // add the new participant to the action in focus:
      actionInFocus.addParticipant(participant);
    }

    // update the action with the copy of the triggers that have been edited:
    actionInFocus.setTriggers(triggers);

    // update the action with the copy of the destroyers that have been edited:
    actionInFocus.setDestroyers(destroyers);

    // close window:
    setVisible(false);
    dispose();
  }

  /*
   * returns true if it is unique, false otherwise, and also takes care of
   * asking the user if they want to overwrite it (and overwriting it, if
   * desired)
   */
  private boolean participantNameIsUnique() { 
    String nameInput = nameField.getText();
    if ((newParticipant)
        || ((!newParticipant) && 
        		(participant.getName().equals(nameInput) == false))) { 
    	// only perform this check if this is a newly created participant, or it 
    	// is an edited participant and the name has been changed
      Vector<ActionTypeParticipant> existingParticipants = 
      	actionInFocus.getAllParticipants();
      for (int i = 0; i < existingParticipants.size(); i++) {
        ActionTypeParticipant tempPart = existingParticipants.elementAt(i);
        if (tempPart.getName().equalsIgnoreCase(nameInput)) { // name entered is
                                                            	// not unique 
        																											// (there is 
        																											// already
                                                            	// another
                                                            	// participant of
        																											// this action 
        																											// defined with 
        																											// the same name
          int choice = JOptionPane
              .showConfirmDialog(
                  null,
                  ("Previously defined " + tempPart.getName() + 
                  		" participant will be overwritten. Continue?"),
                  "Warning", JOptionPane.YES_NO_OPTION);
          if (choice == JOptionPane.YES_OPTION) {
            if (!newParticipant) { // this is a participant being edited, not a
            											 // new one
              // Overwrite existing participants:
              actionInFocus.removeParticipant(tempPart.getName());
              actionInFocus.removeParticipant(participant.getName());
              addEditParticipant();
            } else { // this is a new participant, not one being edited
              // Overwrite existing participant:
              actionInFocus.removeParticipant(tempPart.getName());
              addEditParticipant();
            }
          }
          return false;
        }
      }
    }
    return true;
  }

  /*
   * refreshes the check boxes to reflect the meta-type chosen in the meta-type
   * list
   */
  private void refreshCheckBoxes() { 
    Vector<SimSEObjectType> objTypes = 
    	objects.getAllObjectTypesOfType(
    			SimSEObjectTypeTypes.getIntRepresentation((String) 
    					(metaTypeList.getSelectedItem())));
    checkBoxes.clear();
    checkBoxesPane.removeAll(); // clear all the current checkboxes
    for (int i = 0; i < objTypes.size(); i++) {
      JPanel tempPane = new JPanel(new BorderLayout());
      JCheckBox tempCheckBox = new JCheckBox(objTypes.elementAt(i).getName());
      tempCheckBox.addActionListener(this);
      checkBoxes.add(tempCheckBox);
      tempPane.add(tempCheckBox, BorderLayout.WEST);
      checkBoxesPane.add(tempPane);
    }
    validate();
    pack();
    repaint();
  }

  private void setUpToolTips() {
    if (Guard.getIntRepresentation((String) (guardList.getSelectedItem())) == 
    	Guard.AT_LEAST_AND_AT_MOST) {
      quantityTextField.setToolTipText("Enter 'at least' value");
      maxValTextField.setToolTipText("Enter 'at most' value");
    } else {
      quantityTextField
          .setToolTipText("Enter quantity (leave blank if boundless)");
      maxValTextField.setToolTipText(null);
    }
  }
}