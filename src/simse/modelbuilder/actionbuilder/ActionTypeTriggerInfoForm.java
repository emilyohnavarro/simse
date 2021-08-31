/* This class defines the intial GUI for viewing/editing action triggers with */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;

public class ActionTypeTriggerInfoForm extends JDialog implements
    ActionListener, MouseListener {
  private ActionTypeTrigger trigger; // temporary copy of trigger being edited
  private ActionTypeTrigger originalTrigger; // original trigger
  private ActionType actionInFocus; // action in focus
  private DefinedActionTypes allActions; // all of the currently defined actions
                                         // (for checking uniqueness of menu
                                         // text for user triggers)
  private Vector<String> participantNames; // for the JList
  private JTextField nameField; // for entering trigger name
  private JComboBox triggerTypeList; // for choosing type of trigger
                                     // (autonomous, user, or random)
  private JTextField textTextField; // for entering trigger text
  private JTextField menuTextField; // only for user triggers
  private JCheckBox areYouSureCBox; // only for user triggers
  private JTextField frequencyTextField; // only for random triggers
  private JList participantList; // for choosing a participant whose trigger
                                 // conditions to edit
  private JButton viewEditButton; // for viewing/editing trigger conditions
  private JCheckBox gameEndCBox; // for indicating that this trigger is a
                                 // game-ending trigger
  private JButton okButton;
  private JButton cancelButton;
  private Box userTrigPane; // only for user triggers
  private JPanel freqPane; // only for random triggers
  private JPanel optionalPane; // only for user/random triggers

  public ActionTypeTriggerInfoForm(JDialog owner, ActionType actionInFocus,
      ActionTypeTrigger trig, DefinedActionTypes allActions) {
    super(owner, true);
    originalTrigger = trig; // store pointer to original
    if (originalTrigger instanceof AutonomousActionTypeTrigger) {
    	// make a copy of the trigger for temporary editing:
      trigger = (AutonomousActionTypeTrigger) (trig.clone()); 
    } else if (originalTrigger instanceof UserActionTypeTrigger) {
    	// make a copy of the trigger for temporary editing:
      trigger = (UserActionTypeTrigger) (trig.clone()); 
    } else if (originalTrigger instanceof RandomActionTypeTrigger) {
    	// make a copy of the trigger for temporary editing:
      trigger = (RandomActionTypeTrigger) (trig.clone()); 
    }
    this.actionInFocus = actionInFocus;
    this.allActions = allActions;

    // Set window title:
    setTitle("Trigger Information");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();

    // Create top pane:
    JPanel topPane = new JPanel();
    topPane.add(new JLabel("Name:"));
    nameField = new JTextField(10);
    nameField.setToolTipText("Enter the name for this trigger");
    topPane.add(nameField);

    // Create type pane and components:
    JPanel typePane = new JPanel();
    typePane.add(new JLabel("Choose the trigger type:"));
    triggerTypeList = new JComboBox();
    triggerTypeList.addItem(ActionTypeTrigger.AUTO);
    // only add the user item to the list if there is an employee in this
    // action:
    Vector<ActionTypeParticipant> parts = actionInFocus.getAllParticipants();
    for (int i = 0; i < parts.size(); i++) {
      if (parts.elementAt(i).getSimSEObjectTypeType() == 
      	SimSEObjectTypeTypes.EMPLOYEE) {
        triggerTypeList.addItem(ActionTypeTrigger.USER);
        break;
      }
    }
    triggerTypeList.addItem(ActionTypeTrigger.RANDOM);
    triggerTypeList.addActionListener(this);
    typePane.add(triggerTypeList);

    // Create optionalPane:
    optionalPane = new JPanel();

    // Create userTrig pane and components:
    userTrigPane = Box.createVerticalBox();
    JPanel menuTextPane = new JPanel();
    menuTextPane.add(new JLabel("Menu text:"));
    menuTextField = new JTextField(10);
    menuTextField.setToolTipText("Enter the text to be shown on the user's " +
    		"menu for this trigger");
    menuTextPane.add(menuTextField);
    userTrigPane.add(menuTextPane);
    JPanel areYouSurePane = new JPanel();
    areYouSureCBox = new JCheckBox("User must confirm to start action");
    areYouSureCBox.setToolTipText("Asks the user, \"Are you sure?\" " +
    		"before starting the action");
    areYouSurePane.add(areYouSureCBox);
    userTrigPane.add(areYouSurePane);

    // Create freqPane and components:
    freqPane = new JPanel();
    freqPane.add(new JLabel("Frequency:"));
    frequencyTextField = new JTextField(10);
    frequencyTextField
        .setToolTipText("Enter the % chance of this trigger being fired at " +
        		"each clock tick in which all of the trigger conditions are met");
    freqPane.add(frequencyTextField);

    // Create textPane and components:
    JPanel textPane = new JPanel();
    textPane.add(new JLabel("Overhead Text:"));
    textTextField = new JTextField(15);
    textTextField
        .setToolTipText("Enter the text to be shown in an employee's " +
        		"overhead bubble when this trigger is fired");
    textPane.add(textTextField);

    // Create choosePartPane and label:
    JPanel choosePartPane = new JPanel();
    choosePartPane.add(new JLabel("Choose a participant:"));

    // Create partList pane and list:
    participantList = new JList();
    participantList.setVisibleRowCount(10); // make 10 items visible at a time
    participantList.setFixedCellWidth(200);
    participantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    participantList.addMouseListener(this);
    JScrollPane partListPane = new JScrollPane(participantList);
    // initialize participant names:
    participantNames = new Vector<String>();
    for (int i = 0; i < parts.size(); i++) {
      participantNames.add(parts.elementAt(i).getName());
    }
    participantList.setListData(participantNames);
    setUpParticipantListActionListenerStuff();

    // Create viewEdit pane and button:
    JPanel viewEditPane = new JPanel();
    viewEditButton = new JButton("View/Edit Trigger Conditions");
    viewEditButton.addActionListener(this);
    viewEditButton.setEnabled(false);
    viewEditPane.add(viewEditButton);

    // Create gameEnding pane and check box:
    JPanel gameEndingPane = new JPanel();
    gameEndCBox = new JCheckBox("Game-ending trigger");
    gameEndCBox.addActionListener(this);
    gameEndingPane.add(gameEndCBox);

    // Create okCancel pane and buttons:
    JPanel okCancelPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    okCancelPane.add(okButton);
    okCancelPane.add(cancelButton);

    // Initialize form:
    initializeForm();

    // Add panes and separators to main pane:
    mainPane.add(topPane);
    mainPane.add(typePane);
    if (triggerTypeList.getSelectedItem().equals(ActionTypeTrigger.USER)) {
      JSeparator separator1 = new JSeparator();
      separator1.setMaximumSize(new Dimension(450, 1));
      optionalPane.add(separator1);
      optionalPane.add(userTrigPane);
    } else if (triggerTypeList.getSelectedItem().equals(
        ActionTypeTrigger.RANDOM)) {
      JSeparator separator1 = new JSeparator();
      separator1.setMaximumSize(new Dimension(450, 1));
      optionalPane.add(separator1);
      optionalPane.add(freqPane);
    }
    mainPane.add(optionalPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(450, 1));
    mainPane.add(separator2);
    for (int i = 0; i < parts.size(); i++) {
      ActionTypeParticipant tempPart = parts.elementAt(i);
      if (tempPart.getSimSEObjectTypeType() == SimSEObjectTypeTypes.EMPLOYEE) {
      //|| (tempPart.getSimSEObjectTypeType() ==
      // SimSEObjectTypeTypes.CUSTOMER)) // no overhead text for customers yet!
        mainPane.add(textPane);
        break;
      }
    }
    mainPane.add(choosePartPane);
    mainPane.add(partListPane);
    mainPane.add(viewEditPane);
    JSeparator separator3 = new JSeparator();
    separator3.setMaximumSize(new Dimension(450, 1));
    mainPane.add(separator3);
    mainPane.add(gameEndingPane);
    JSeparator separator4 = new JSeparator();
    separator4.setMaximumSize(new Dimension(450, 1));
    mainPane.add(separator4);
    mainPane.add(okCancelPane);

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

    if (source == triggerTypeList) {
      if (triggerTypeList.getSelectedItem().equals(ActionTypeTrigger.AUTO)) { 
      	// autonomous trigger type selected
        optionalPane.removeAll();
        pack();
        validate();
        pack();
        repaint();
        // morph trigger to autonomous trigger:
        trigger = trigger.morph(ActionTypeTrigger.AUTO); 
      } else if (triggerTypeList.getSelectedItem().equals(
          ActionTypeTrigger.USER)) { // user trigger type selected
        optionalPane.removeAll();
        optionalPane.add(userTrigPane);
        validate();
        pack();
        repaint();
        // morph trigger to user trigger:
        trigger = trigger.morph(ActionTypeTrigger.USER); 
      } else if (triggerTypeList.getSelectedItem().equals(
          ActionTypeTrigger.RANDOM)) { // random trigger type selected
        optionalPane.removeAll();
        optionalPane.add(freqPane);
        validate();
        pack();
        repaint();
        // morph trigger to random trigger:
        trigger = trigger.morph(ActionTypeTrigger.RANDOM); 
      }
    }

    else if (source == viewEditButton) {
      viewEditButtonClicked();
    }

    else if (source == gameEndCBox) {
      // set game-ending trigger:
      trigger.setGameEndingTrigger(gameEndCBox.isSelected());
    }

    else if (source == okButton) {
      Vector<String> errors = inputValid(); // check validity of input
      if (errors.size() == 0) { // input valid
        // set name:
        if ((nameField.getText() != null) && 
        		(nameField.getText().length() > 0)) {
          trigger.setName(nameField.getText());
        }
        // set trigger text:
        if ((textTextField.getText() != null)
            && (textTextField.getText().length() > 0)) {
          trigger.setTriggerText(textTextField.getText());
        } else {
          trigger.setTriggerText(null);
        }
        if (triggerTypeList.getSelectedItem().equals(ActionTypeTrigger.USER)) { 
        	// user trigger
        	UserActionTypeTrigger userTrigger = (UserActionTypeTrigger)trigger;
        	// set the trigger's menu text:
        	userTrigger.setMenuText(menuTextField.getText()); 
        	userTrigger.setRequiresConfirmation(areYouSureCBox.isSelected());
        } else if (triggerTypeList.getSelectedItem().equals(
            ActionTypeTrigger.RANDOM)) { // random trigger
          try {
          	// set the trigger's frequency:
            ((RandomActionTypeTrigger) (trigger)).setFrequency(Double
                .parseDouble(frequencyTextField.getText())); 
          } catch (NumberFormatException e) {
            System.out.println(e.getMessage()); // Note: this exception should
                                                // never be thrown since
                                                // inputValid() has been
            																		// called before this to ensure 
            																		// that the input is valid
          }
        }
        // remove old trigger:
        int index = actionInFocus.removeTrigger(originalTrigger.getName()); 
        if (index > -1) { // trigger was previously there
        	// add the new trigger in its old position:
          actionInFocus.addTrigger(trigger, index); 
        } else { // trigger was not previously there
          actionInFocus.addTrigger(trigger); // add it to the end
        }
        setVisible(false);
        dispose();
      } else { // input not valid
        for (int i = 0; i < errors.size(); i++) {
          JOptionPane.showMessageDialog(null, errors.elementAt(i), 
          		"Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
      }
    } else if (source == cancelButton) {
      setVisible(false);
      dispose();
    }
  }
  
  public void mousePressed(MouseEvent me) {}

  public void mouseReleased(MouseEvent me) {}

  public void mouseEntered(MouseEvent me) {}

  public void mouseExited(MouseEvent me) {}

  public void mouseClicked(MouseEvent me) {
    int clicks = me.getClickCount();
    if ((me.getSource() == participantList) && 
        (me.getButton() == MouseEvent.BUTTON1) && (clicks >= 2)) {
      viewEditButtonClicked();
    }
  }

  private void initializeForm() {
    // initialize name:
    if ((trigger.getName() != null) && (trigger.getName().length() > 0)) {
      nameField.setText(trigger.getName());
    }
    // initialize trigger text:
    if ((trigger.getTriggerText() != null)
        && (trigger.getTriggerText().length() > 0)) {
      textTextField.setText(trigger.getTriggerText());
    }
    // initialize game-ending trigger status:
    gameEndCBox.setSelected(trigger.isGameEndingTrigger());
    // initialize other info:
    if (trigger instanceof AutonomousActionTypeTrigger) { // trigger is 
    																											// autonomous type
    	// set type list to reflect this:
      triggerTypeList.setSelectedItem(ActionTypeTrigger.AUTO); 
    } else if (trigger instanceof UserActionTypeTrigger) { // trigger is user 
    																											 // type
    	// set type list to reflect this:
      triggerTypeList.setSelectedItem(ActionTypeTrigger.USER); 
      UserActionTypeTrigger userTrigger = (UserActionTypeTrigger)trigger;
      // set menu text field to reflect actual value:
      menuTextField.setText(userTrigger.getMenuText()); 
      areYouSureCBox.setSelected(userTrigger.requiresConfirmation());
    } else if (trigger instanceof RandomActionTypeTrigger) { // trigger is 
    																												 // random type
    	// set type list to reflect this:
      triggerTypeList.setSelectedItem(ActionTypeTrigger.RANDOM);
      // set frequency text field to reflect actual value:
      frequencyTextField.setText((new Double(
          ((RandomActionTypeTrigger) (trigger)).getFrequency())).toString()); 
    }
  }

  // enables view/edit button whenever a list item (action) is selected
  private void setUpParticipantListActionListenerStuff() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = participantList.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          viewEditButton.setEnabled(true);
        }
      }
    });
  }

  private Vector<String> inputValid() {
  	// vector of strings of error messages:
    Vector<String> messages = new Vector<String>(); 
    // Check for validity of name:
    char[] cArrayA = nameField.getText().toCharArray();
    // Check for length constraints:
    if ((cArrayA.length < 2) || (cArrayA.length > 40)) { // user has a string
                                                       	 // shorter than 2 chars
                                                       	 // or longer than 40
                                                       	 // chars
      messages
          .add("Please enter text for the trigger's name -- between 2 and 40 " +
          		"alphabetic characters"); // warn user to enter a valid name
    }

    // Check for invalid characters:
    for (int i = 0; i < cArrayA.length; i++) {
      if ((Character.isLetter(cArrayA[i])) == false) { // character is not a
                                                     	 // letter (hence, 
      																								 // invalid)
        messages
            .add("Please enter only alphabetic characters for the trigger's " +
            		"name"); // warn user to enter a valid name
      }
    }
    // Check for uniqueness of name:
    Vector<ActionTypeTrigger> existingTrigs = actionInFocus.getAllTriggers();
    for (int i = 0; i < existingTrigs.size(); i++) {
      ActionTypeTrigger tempTrig = existingTrigs.elementAt(i);
      if ((tempTrig.getName().equalsIgnoreCase(nameField.getText()))
          && (tempTrig != originalTrigger)) { // name entered is not unique
      																				// (there is already another 
      																				// trigger defined with the same 
      																				// name (hence, invalid)
        messages.add("Trigger name must be unique");
      }
    }

    // Check overhead text:
    String trigText = textTextField.getText();
    if ((trigText != null) && (trigText.length() > 0)) { // trigger text entered
      if (trigText.equalsIgnoreCase("Everyone stop what you're doing")) { 
      	// reserved phrase
        messages
            .add("\"Everyone stop what you're doing\" is a reserved phrase");
      }
    }

    // If this is a game-ending trigger, check that there is a scoring attribute
    // assigned:
    boolean atLeastOneScoringAtt = false;
    if (gameEndCBox.isSelected()) { // game-ending trigger
      // go through all attributes and make sure one of them is set to the
      // scoring attribute:
      Vector<ActionTypeParticipantTrigger> partTrigs = 
      	trigger.getAllParticipantTriggers();
      for (int i = 0; i < partTrigs.size(); i++) {
        ActionTypeParticipantTrigger pTrig = partTrigs.elementAt(i);
        Vector<ActionTypeParticipantConstraint> partConsts2 = 
        	pTrig.getAllConstraints();
        for (int j = 0; j < partConsts2.size(); j++) {
          ActionTypeParticipantConstraint partConst = partConsts2.elementAt(j);
          ActionTypeParticipantAttributeConstraint[] attConsts = partConst
              .getAllAttributeConstraints();
          for (int k = 0; k < attConsts.length; k++) {
            if (attConsts[k].isScoringAttribute()) { // scoring attribute
              atLeastOneScoringAtt = true;
              break;
            }
          }
        }
      }
      if (!atLeastOneScoringAtt) { // no scoring attribute
        messages
            .add("Since this is a game-ending trigger, you must assign " +
            		"exactly one attribute to be the scoring attribute");
      }
    }

    if (((String) (triggerTypeList.getSelectedItem()))
        .equals(ActionTypeTrigger.USER)) { // user trigger
      // Check menu text input:
      String menuTextInput = menuTextField.getText();
      char[] cArray = menuTextInput.toCharArray();

      // Check for length constraints:
      if ((cArray.length == 0) || (cArray.length > 40)) { // user has entered
                                                        	// nothing or a string
                                                        	// longer than 40 
      																										// chars
        messages.add("Menu text must be between 1 and 40 characters");
      }

      // Check for uniqueness of menu text:
      Vector<ActionType> existingActionTypes = allActions.getAllActionTypes();
      for (int i = 0; i < existingActionTypes.size(); i++) {
        ActionType tempAct = existingActionTypes.elementAt(i);
        if (tempAct.getName().equals(actionInFocus.getName()) == false) { 
        	// not this action
          // triggers:
          Vector<ActionTypeTrigger> tempTrigs = tempAct.getAllTriggers();
          for (int j = 0; j < tempTrigs.size(); j++) {
            ActionTypeTrigger tempTrig = tempTrigs.elementAt(j);
            if ((tempTrig instanceof UserActionTypeTrigger)
                && (tempTrig != originalTrigger)) { // only check user triggers 
            																				// and not the one in focus
              if (((UserActionTypeTrigger) (tempTrig)).getMenuText().equals(
                  menuTextField.getText())) { // there already exists a trigger 
              																// with this menu text
                messages
                    .add("A user-initiated trigger with that menu text " +
                    		"already exists");
              }
            }
          }

          // destroyers:
          Vector<ActionTypeDestroyer> tempDests = tempAct.getAllDestroyers();
          for (int j = 0; j < tempDests.size(); j++) {
            ActionTypeDestroyer tempDest = tempDests.elementAt(j);
            if (tempDest instanceof UserActionTypeDestroyer) { // only check 
            																									 // user 
            																									 // destroyers
              if (((UserActionTypeDestroyer) (tempDest)).getMenuText().equals(
                  menuTextField.getText())) { // there already exists a 
              															  // destroyer with this menu text
                messages
                    .add("A user-initiated destroyer with that menu text " +
                    		"already exists");
              }
            }
          }
        }
      }

      // Check the other triggers and destroyers in this action now:
      // triggers:
      Vector<ActionTypeTrigger> allTrigs = actionInFocus.getAllTriggers();
      for (int i = 0; i < allTrigs.size(); i++) {
        ActionTypeTrigger tempTrig = allTrigs.elementAt(i);
        if ((tempTrig instanceof UserActionTypeTrigger)
            && (tempTrig != originalTrigger)) {
          if (((UserActionTypeTrigger) (tempTrig)).getMenuText().equals(
              menuTextField.getText())) { // there already exists a trigger with
          																// this menu text
            messages
                .add("A user-initiated trigger with that menu text " +
                		"already exists");
          }
        }
      }
      // destroyers:
      Vector<ActionTypeDestroyer> allDests = actionInFocus.getAllDestroyers();
      for (int i = 0; i < allDests.size(); i++) {
        ActionTypeDestroyer tempDest = allDests.elementAt(i);
        if ((tempDest instanceof UserActionTypeDestroyer)) {
          if (((UserActionTypeDestroyer) (tempDest)).getMenuText().equals(
              menuTextField.getText())) { // there already exists a destroyer 
          																// with this menu text
            messages
                .add("A user-initiated destroyer with that menu text " +
                		"already exists");
          }
        }
      }
    }

    else if (((String) (triggerTypeList.getSelectedItem()))
        .equals(ActionTypeTrigger.RANDOM)) { // random trigger
      // Check frequency input:
      String freqInput = frequencyTextField.getText();
      if ((freqInput != null) && (freqInput.length() > 0)) { // field is not 
      																											 // blank
        try {
          double freq = Double.parseDouble(freqInput); // parse the string into
                                                       // a double
          if ((freq < 0) || (freq > 100)) {
            messages
                .add("Frequency must be a valid real number between 0 and 100");
          }
        } catch (NumberFormatException e) {
          messages
              .add("Frequency must be a valid real number between 0 and 100");
        }
      }
    }
    return messages;
  }
  
  private void viewEditButtonClicked() {
    if (participantList.isSelectionEmpty() == false) {
      // Bring up form for entering info for the new participant trigger:
      ActionTypeParticipantTrigger partTrig = trigger
          .getParticipantTrigger((String) (participantList.getSelectedValue()));
      new ActionTypeParticipantTriggerConstraintsInfoForm(this, partTrig, 
      		trigger);
      if (trigger.isGameEndingTrigger()) {
        // check if any of the attributes have just been set to scoring
        // attributes, and if so, remove scoring status from whatever
        // attribute had this status previously:
        boolean attSetToScoring = false;
        Vector<ActionTypeParticipantConstraint> partConsts = 
        	partTrig.getAllConstraints();
        for (int i = 0; i < partConsts.size(); i++) {
          ActionTypeParticipantConstraint partConst = partConsts.elementAt(i);
          ActionTypeParticipantAttributeConstraint[] attConsts = partConst
              .getAllAttributeConstraints();
          for (int j = 0; j < attConsts.length; j++) {
            if (attConsts[j].isScoringAttribute()) {
              attSetToScoring = true;
              break;
            }
          }
        }
        if (attSetToScoring) {
          Vector<ActionTypeParticipantTrigger> partTrigs = 
          	trigger.getAllParticipantTriggers();
          for (int i = 0; i < partTrigs.size(); i++) {
            ActionTypeParticipantTrigger pTrig = partTrigs.elementAt(i);
            if (pTrig != partTrig) { // not participant trigger that you just
            												 // edited
              Vector<ActionTypeParticipantConstraint> partConsts2 = 
              	pTrig.getAllConstraints();
              for (int j = 0; j < partConsts2.size(); j++) {
                ActionTypeParticipantConstraint partConst = 
                	partConsts2.elementAt(j);
                ActionTypeParticipantAttributeConstraint[] attConsts = partConst
                    .getAllAttributeConstraints();
                for (int k = 0; k < attConsts.length; k++) {
                  attConsts[k].setScoringAttribute(false);
                }
              }
            }
          }
        }
      }
    }
  }

  public class ExitListener extends WindowAdapter {
    public void windowClosing(WindowEvent event) {
      setVisible(false);
      dispose();
    }
  }
}