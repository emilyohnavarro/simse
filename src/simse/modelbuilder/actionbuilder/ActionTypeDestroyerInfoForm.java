/* This class defines the intial GUI for viewing/editing action destroyers with */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.awt.Dimension;
import java.awt.Color;
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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ActionTypeDestroyerInfoForm extends JDialog implements
    ActionListener, MouseListener {
  private ActionTypeDestroyer destroyer; // temporary copy of destroyer being
                                         // edited
  private ActionTypeDestroyer originalDestroyer; // original destroyer
  private ActionType actionInFocus; // action in focus
  private DefinedActionTypes allActions; // all of the currently defined actions
                                         // (for checking uniqueness of menu
                                         // text for user triggers)
  private Vector<String> participantNames; // for the JList

  private JTextField nameField; // for entering destroyer name
  private JComboBox destroyerTypeList; // for choosing type of destroyer
                                       // (autonomous, user, or random)
  private JTextField textTextField; // for entering destroyer text
  private JTextField menuTextField; // only for user destroyers
  private JTextField frequencyTextField; // only for random destroyers
  private JTextField timeTextField; // only for timed destroyers
  private JList participantList; // for choosing a participant whose destroyer
                                 // conditions to edit
  private JButton viewEditButton; // for viewing/editing destroyer conditions
  private JCheckBox gameEndCBox; // for indicating that this is a game-ending
                                 // destroyer
  private JButton okButton;
  private JButton cancelButton;
  private JPanel menuTextPane; // only for user destroyers
  private JPanel freqPane; // only for random destroyers
  private JPanel timePane; // only for timed destroyers
  private JPanel optionalPane; // only for user/random/timed destroyers

  public ActionTypeDestroyerInfoForm(JDialog owner, ActionType actionInFocus,
      ActionTypeDestroyer dest, DefinedActionTypes allActions) {
    super(owner, true);
    originalDestroyer = dest; // store pointer to original
    if (originalDestroyer instanceof AutonomousActionTypeDestroyer) {
      destroyer = (AutonomousActionTypeDestroyer) (dest.clone()); // make a copy
                                                                  // of the
                                                                  // destroyer
                                                                  // for
                                                                  // temporary
                                                                  // editing
    } else if (originalDestroyer instanceof UserActionTypeDestroyer) {
      destroyer = (UserActionTypeDestroyer) (dest.clone()); // make a copy of
                                                            // the destroyer for
                                                            // temporary editing
    } else if (originalDestroyer instanceof RandomActionTypeDestroyer) {
      destroyer = (RandomActionTypeDestroyer) (dest.clone()); // make a copy of
                                                              // the destroyer
                                                              // for temporary
                                                              // editing
    } else if (originalDestroyer instanceof TimedActionTypeDestroyer) {
      destroyer = (TimedActionTypeDestroyer) (dest.clone()); // make a copy of
                                                             // the destroyer
                                                             // for temporary
                                                             // editing
    }
    this.actionInFocus = actionInFocus;
    this.allActions = allActions;

    // Set window title:
    setTitle("Destroyer Information");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();

    // Create top pane:
    JPanel topPane = new JPanel();
    topPane.add(new JLabel("Name:"));
    nameField = new JTextField(10);
    nameField.setToolTipText("Enter the name for this destroyer");
    topPane.add(nameField);

    // Create type pane and components:
    JPanel typePane = new JPanel();
    typePane.add(new JLabel("Choose the destroyer type:"));
    destroyerTypeList = new JComboBox();
    destroyerTypeList.addItem(ActionTypeDestroyer.AUTO);
    // only add the user item to the list if there is an employee in this
    // action:
    Vector<ActionTypeParticipant> parts = actionInFocus.getAllParticipants();
    for (int i = 0; i < parts.size(); i++) {
      if (parts.elementAt(i).getSimSEObjectTypeType() == 
      	SimSEObjectTypeTypes.EMPLOYEE) {
        destroyerTypeList.addItem(ActionTypeDestroyer.USER);
        break;
      }
    }
    destroyerTypeList.addItem(ActionTypeDestroyer.RANDOM);
    destroyerTypeList.addItem(ActionTypeDestroyer.TIMED);
    destroyerTypeList.addActionListener(this);
    typePane.add(destroyerTypeList);

    // Create optionalPane:
    optionalPane = new JPanel();

    // Create menuText pane and components:
    menuTextPane = new JPanel();
    menuTextPane.add(new JLabel("Menu text:"));
    menuTextField = new JTextField(10);
    menuTextField
        .setToolTipText("Enter the text to be shown on the user's menu for " +
        		"this destroyer");
    menuTextPane.add(menuTextField);

    // Create freqPane and components:
    freqPane = new JPanel();
    freqPane.add(new JLabel("Frequency:"));
    frequencyTextField = new JTextField(10);
    frequencyTextField
        .setToolTipText("Enter the % chance of this destroyer being fired at " +
        		"each clock tick in which all of the destroyer conditions are met");
    freqPane.add(frequencyTextField);

    // Create timePane and components:
    timePane = new JPanel();
    timePane.add(new JLabel("Time to live:"));
    timeTextField = new JTextField(10);
    timeTextField
        .setToolTipText("Enter the number of clock ticks that this action " +
        		"will exist for before it is to be automatically destroyed");
    timePane.add(timeTextField);

    // Create textPane and components:
    JPanel textPane = new JPanel();
    textPane.add(new JLabel("Overhead Text:"));
    textTextField = new JTextField(15);
    textTextField
        .setToolTipText("Enter the text to be shown in an employee/" +
        		"customer's overhead bubble when this destroyer is fired");
    textPane.add(textTextField);

    // Create choosePartPane and label:
    JPanel choosePartPane = new JPanel();
    choosePartPane.add(new JLabel("Choose a participant:"));

    // Create partList pane and list:
    participantList = new JList();
    participantList.setVisibleRowCount(10); // make 10 items visible at a time
    participantList.setFixedCellWidth(200);
    // only allow the user to select one item at a time:
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
    viewEditButton = new JButton("View/Edit Destroyer Conditions");
    viewEditButton.addActionListener(this);
    viewEditButton.setEnabled(false);
    viewEditPane.add(viewEditButton);

    // Create gameEnding pane and check box:
    JPanel gameEndingPane = new JPanel();
    gameEndCBox = new JCheckBox("Game-ending destroyer");
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
    if (destroyerTypeList.getSelectedItem().equals(ActionTypeDestroyer.USER)) {
      JSeparator separator1 = new JSeparator();
      separator1.setMaximumSize(new Dimension(450, 1));
      optionalPane.add(separator1);
      optionalPane.add(menuTextPane);
    } else if (destroyerTypeList.getSelectedItem().equals(
        ActionTypeDestroyer.RANDOM)) {
      JSeparator separator1 = new JSeparator();
      separator1.setMaximumSize(new Dimension(450, 1));
      optionalPane.add(separator1);
      optionalPane.add(freqPane);
    } else if (destroyerTypeList.getSelectedItem().equals(
        ActionTypeDestroyer.TIMED)) {
      JSeparator separator1 = new JSeparator();
      separator1.setMaximumSize(new Dimension(450, 1));
      optionalPane.add(separator1);
      optionalPane.add(timePane);
    }
    mainPane.add(optionalPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(450, 1));
    mainPane.add(separator2);
    for (int i = 0; i < parts.size(); i++) {
      ActionTypeParticipant tempPart = parts.elementAt(i);
      if ((tempPart.getSimSEObjectTypeType() == SimSEObjectTypeTypes.EMPLOYEE)
          || (tempPart.getSimSEObjectTypeType() == 
          	SimSEObjectTypeTypes.CUSTOMER)) {
        mainPane.add(textPane);
        break;
      }
    }
    mainPane.add(choosePartPane);
    mainPane.add(partListPane);
    mainPane.add(viewEditPane);
    JSeparator separator3 = new JSeparator();
    separator3.setMaximumSize(new Dimension(2450, 1));
    mainPane.add(separator3);
    mainPane.add(gameEndingPane);
    JSeparator separator4 = new JSeparator();
    separator4.setMaximumSize(new Dimension(2450, 1));
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

    if (source == destroyerTypeList) {
      if (destroyerTypeList.getSelectedItem().equals(
      		ActionTypeDestroyer.AUTO)) { // autonomous destroyer type selected
        optionalPane.removeAll();
        validate();
        pack();
        repaint();
        destroyer = destroyer.morph(ActionTypeDestroyer.AUTO); // morph
                                                               // destroyer to
                                                               // autonomous
                                                               // destroyer
      } else if (destroyerTypeList.getSelectedItem().equals(
          ActionTypeDestroyer.USER)) { // user destroyer type selected
        optionalPane.removeAll();
        optionalPane.add(menuTextPane);
        validate();
        pack();
        repaint();
        destroyer = destroyer.morph(ActionTypeDestroyer.USER); // morph
                                                               // destroyer to
                                                               // user destroyer
      } else if (destroyerTypeList.getSelectedItem().equals(
          ActionTypeDestroyer.RANDOM)) { // random destroyer type selected
        optionalPane.removeAll();
        optionalPane.add(freqPane);
        validate();
        pack();
        repaint();
        destroyer = destroyer.morph(ActionTypeDestroyer.RANDOM); // morph
                                                                 // destroyer to
                                                                 // random
                                                                 // destroyer
      } else if (destroyerTypeList.getSelectedItem().equals(
          ActionTypeDestroyer.TIMED)) { // timed destroyer type selected
        optionalPane.removeAll();
        optionalPane.add(timePane);
        validate();
        pack();
        repaint();
        destroyer = destroyer.morph(ActionTypeDestroyer.TIMED); // morph
                                                                // destroyer to
                                                                // timed
                                                                // destroyer
      }
    }

    else if (source == viewEditButton) {
      viewEditButtonClicked();
    }

    else if (source == gameEndCBox) {
      // set game-ending destroyer:
      destroyer.setGameEndingDestroyer(gameEndCBox.isSelected());
    }

    else if (source == okButton) {
      Vector<String> errors = inputValid(); // check validity of input
      if (errors.size() == 0) { // input valid
        // set name:
        if ((nameField.getText() != null) && 
        		(nameField.getText().length() > 0)) {
          destroyer.setName(nameField.getText());
        }
        // set destroyer text:
        if ((textTextField.getText() != null)
            && (textTextField.getText().length() > 0)) {
          destroyer.setDestroyerText(textTextField.getText());
        } else {
          destroyer.setDestroyerText(null);
        }
        if (destroyerTypeList.getSelectedItem()
            .equals(ActionTypeDestroyer.USER)) { // user destroyer
          ((UserActionTypeDestroyer) (destroyer)).setMenuText(menuTextField
              .getText()); // set the destroyer's menu text
        } else if (destroyerTypeList.getSelectedItem().equals(
            ActionTypeDestroyer.RANDOM)) { // random destroyer
          try {
            ((RandomActionTypeDestroyer) (destroyer)).setFrequency(Double
                .parseDouble(frequencyTextField.getText())); // set
            // the destroyer's frequency
          } catch (NumberFormatException e) {
            System.out.println(e.getMessage()); // Note: this exception should
                                                // never be thrown since
                                                // inputValid() has been called 
            																		// before this to ensure that 
            																	 	// the input is valid
          }
        } else if (destroyerTypeList.getSelectedItem().equals(
            ActionTypeDestroyer.TIMED)) { // timed destroyer
          try {
            ((TimedActionTypeDestroyer) (destroyer)).setTime(Integer
                .parseInt(timeTextField.getText())); // set the
            // destroyer's time
          } catch (NumberFormatException e) {
            System.out.println(e.getMessage()); // Note: this exception should
                                                // never be thrown since
                                                // inputValid() has been called 
            																		// before this to ensure that 
            																	 	// the input is valid
          }
        }
        // remove old destroyer:
        int index = actionInFocus.removeDestroyer(originalDestroyer.getName()); 
        if (index > -1) { // destroyer was previously there
          actionInFocus.addDestroyer(destroyer, index); // add the new destroyer
                                                        // at its old position
        } else { // destroyer was not previously there
          actionInFocus.addDestroyer(destroyer); // add it to the end
        }
        setVisible(false);
        dispose();
      } else { // input not valid
        for (int i = 0; i < errors.size(); i++) {
          JOptionPane.showMessageDialog(null, ((String) errors.elementAt(i)),
              "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
      }
    }

    else if (source == cancelButton) {
      setVisible(false);
      dispose();
    }
  }
  
  public void mousePressed(MouseEvent me) { }

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
    if ((destroyer.getName() != null) && (destroyer.getName().length() > 0)) {
      nameField.setText(destroyer.getName());
    }
    // initialize destroyer text:
    if ((destroyer.getDestroyerText() != null)
        && (destroyer.getDestroyerText().length() > 0)) {
      textTextField.setText(destroyer.getDestroyerText());
    }
    // initialize game-ending destroyer status:
    gameEndCBox.setSelected(destroyer.isGameEndingDestroyer());
    // initialize other info:
    if (destroyer instanceof AutonomousActionTypeDestroyer) { // destroyer is
                                                            	// autonomous type
      destroyerTypeList.setSelectedItem(ActionTypeDestroyer.AUTO); // set type
                                                                   // list to
                                                                   // reflect
                                                                   // this
    } else if (destroyer instanceof UserActionTypeDestroyer) { // destroyer is
                                                             	 // user type
      destroyerTypeList.setSelectedItem(ActionTypeDestroyer.USER); // set type
                                                                   // list to
                                                                   // reflect
                                                                   // this
      menuTextField.setText(((UserActionTypeDestroyer) (destroyer))
          .getMenuText()); // set menu text field to reflect actual value
    } else if (destroyer instanceof RandomActionTypeDestroyer) { // destroyer is
                                                               	 // random type
      destroyerTypeList.setSelectedItem(ActionTypeDestroyer.RANDOM); // set type
                                                                     // list to
                                                                     // reflect
                                                                     // this
      frequencyTextField
          .setText((new Double(((RandomActionTypeDestroyer) (destroyer))
              .getFrequency())).toString()); // set
      // frequency text field to reflect actual value
    } else if (destroyer instanceof TimedActionTypeDestroyer) { // destroyer is
                                                              	// timed type
      destroyerTypeList.setSelectedItem(ActionTypeDestroyer.TIMED); // set type
                                                                    // list to
                                                                    // reflect
                                                                    // this
      // set time text field to reflect actual value:
      timeTextField.setText((new Integer(
          ((TimedActionTypeDestroyer) (destroyer)).getTime())).toString()); 
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
    Vector<String> messages = new Vector<String>(); // vector of strings of 
    																								// error messages

    // Check for validity of name:
    char[] cArrayA = nameField.getText().toCharArray();

    // Check for length constraints:
    if ((cArrayA.length < 2) || (cArrayA.length > 40)) { // user has a string
                                                       	 // shorter than 2 chars
                                                       	 // or longer than 40
                                                       	 // chars
      messages
          .add("Please enter text for the destroyer's name -- between 2 and " +
          		"40 alphabetic characters"); // warn user to enter a valid name
      }

    // Check for invalid characters:
    for (int i = 0; i < cArrayA.length; i++) {
      if ((Character.isLetter(cArrayA[i])) == false) { // character is not a
                                                     	 // letter (hence, 
      																								 // invalid)
        messages
            .add("Please enter only alphabetic characters for the " +
            		"destroyer's name"); // warn user to enter a valid name
      }
    }
    // Check for uniqueness of name:
    Vector<ActionTypeDestroyer> existingDests = 
    	actionInFocus.getAllDestroyers();
    for (int i = 0; i < existingDests.size(); i++) {
      ActionTypeDestroyer tempDest = existingDests.elementAt(i);
      if ((tempDest.getName().equalsIgnoreCase(nameField.getText()))
          && (tempDest != originalDestroyer)) { // name entered is not unique
      																					// (there is already another 
      																					// destroyer defined with the 
      																					// same name (hence, invalid)
        messages.add("Destroyer name must be unique");
      }
    }

    // Check overhead text:
    String destroyerText = textTextField.getText();
    if ((destroyerText != null) && (destroyerText.length() > 0)) { // destroyer
                                                                 	 // text 
    																															 // entered
      if (destroyerText.equalsIgnoreCase("Everyone stop what you're doing")) { 
      	// reserved phrase
        messages
            .add("\"Everyone stop what you're doing\" is a reserved phrase");
      }
    }

    // If this is a game-ending destroyer, check that there is a scoring
    // attribute assigned:
    boolean atLeastOneScoringAtt = false;
    if (gameEndCBox.isSelected()) { // game-ending destroyer
      // go through all attributes and make sure one of them is set to the
      // scoring attribute:
      Vector<ActionTypeParticipantDestroyer> partDests = 
      	destroyer.getAllParticipantDestroyers();
      for (int i = 0; i < partDests.size(); i++) {
        ActionTypeParticipantDestroyer pDest = partDests.elementAt(i);
        Vector<ActionTypeParticipantConstraint> partConsts2 = 
        	pDest.getAllConstraints();
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
            .add("Since this is a game-ending destroyer, you must assign " +
            		"exactly one attribute to be the scoring attribute");
      }
    }

    if (((String) (destroyerTypeList.getSelectedItem()))
        .equals(ActionTypeDestroyer.USER)) { // user destroyer
      // Check menu text input:
      String menuTextInput = menuTextField.getText();
      char[] cArray = menuTextInput.toCharArray();

      // Check for length constraints:
      if ((cArray.length == 0) || (cArray.length > 40)) { // user has entered
                                                        	// nothing or a string
                                                        	// longer than 40 
      																										// chars
        messages
            .add(new String("Menu text must be between 1 and 40 characters"));
      }

      // Check for uniqueness of menu text:
      Vector<ActionType> existingActionTypes = allActions.getAllActionTypes();
      for (int i = 0; i < existingActionTypes.size(); i++) {
        ActionType tempAct = existingActionTypes.elementAt(i);
        if (tempAct.getName().equals(actionInFocus.getName()) == false) { 
        	// not this action
          // destroyers:
          Vector<ActionTypeDestroyer> tempDests = tempAct.getAllDestroyers();
          for (int j = 0; j < tempDests.size(); j++) {
            ActionTypeDestroyer tempDest = tempDests.elementAt(j);
            if ((tempDest instanceof UserActionTypeDestroyer)
                && (tempDest != originalDestroyer)) { // only check user
                                                    	// destroyers and not the
            																					// one in focus
              if (((UserActionTypeDestroyer) (tempDest)).getMenuText().equals(
                  menuTextField.getText())) { // there already exists a 
              																// destroyer with this menu text
                messages
                    .add(new String(
                        "A user-initiated destroyer with that menu text " +
                        "already exists"));
              }
            }
          }

          // triggers:
          Vector<ActionTypeTrigger> tempTrigs = tempAct.getAllTriggers();
          for (int j = 0; j < tempTrigs.size(); j++) {
            ActionTypeTrigger tempTrig = tempTrigs.elementAt(j);
            if (tempTrig instanceof UserActionTypeTrigger) { // only check user
                                                           	 // triggers
              if (((UserActionTypeTrigger) (tempTrig)).getMenuText().equals(
                  menuTextField.getText())) { // there already exists a
              																// trigger with this menu text
                messages
                    .add(new String(
                        "A user-initiated trigger with that menu text " +
                        "already exists"));
              }
            }
          }
        }
      }

      // Check the other destroyers and destroyers in this action now:
      // destroyers:
      Vector<ActionTypeDestroyer> allDests = actionInFocus.getAllDestroyers();
      for (int i = 0; i < allDests.size(); i++) {
        ActionTypeDestroyer tempDest = allDests.elementAt(i);
        if ((tempDest instanceof UserActionTypeDestroyer)
            && (tempDest != originalDestroyer)) {
          if (((UserActionTypeDestroyer) (tempDest)).getMenuText().equals(
              menuTextField.getText())) { // there already exists a destroyer 
          																// with this menu text
            messages
                .add("A user-initiated destroyer with that menu text " +
                		"already exists");
          }
        }
      }
      // trigger:
      Vector<ActionTypeTrigger> allTrigs = actionInFocus.getAllTriggers();
      for (int i = 0; i < allTrigs.size(); i++) {
        ActionTypeTrigger tempTrig = allTrigs.elementAt(i);
        if ((tempTrig instanceof UserActionTypeTrigger)) {
          if (((UserActionTypeTrigger) (tempTrig)).getMenuText().equals(
              menuTextField.getText())) { // there already exists a trigger with
          																// this menu text
            messages
                .add("A user-initiated trigger with that menu text " +
                		"already exists");
          }
        }
      }
    }

    else if (((String) (destroyerTypeList.getSelectedItem()))
        .equals(ActionTypeDestroyer.RANDOM)) { // random destroyer
      // Check frequency input:
      String freqInput = frequencyTextField.getText();
      if ((freqInput != null) && (freqInput.length() > 0)) { // field is not 
      																											 // blank
        try {
          double freq = Double.parseDouble(freqInput); // parse the string into
                                                       // a double
          if ((freq < 0) || (freq > 100)) {
            messages.add(new String(
                "Frequency must be a valid real number between 0 and 100"));
          }
        } catch (NumberFormatException e) {
          messages.add(new String(
              "Frequency must be a valid real number between 0 and 100"));
        }
      }
    }

    else if (((String) (destroyerTypeList.getSelectedItem()))
        .equals(ActionTypeDestroyer.TIMED)) { // timed destroyer
      // Check time input:
      String timeInput = timeTextField.getText();
      if ((timeInput != null) && (timeInput.length() > 0)) { // field is not 
      																											 // blank
        try {
          int time = Integer.parseInt(timeInput); // parse the string into an
                                                  // int
          if (time <= 0) {
            messages.add(new String(
                "Time must be a valid positive, non-zero integer"));
          }
        } catch (NumberFormatException e) {
          messages.add(new String(
              "Time must be a valid positive, non-zero integer"));
        }
      }

      // check if there is already one timed destroyer:
      Vector<ActionTypeDestroyer> allDests = actionInFocus.getAllDestroyers();
      for (int i = 0; i < allDests.size(); i++) {
        ActionTypeDestroyer tempDest = allDests.elementAt(i);
        if ((tempDest instanceof TimedActionTypeDestroyer)
            && (tempDest != originalDestroyer)) {
          messages
              .add("You may only have at most one timed destroyer per action");
          break;
        }
      }
    }
    return messages;
  }
  
  private void viewEditButtonClicked() {
    if (participantList.isSelectionEmpty() == false) { // a participant is
      																								 // selected
      // Bring up form for entering info for the new participant destroyer:
      ActionTypeParticipantDestroyer partDest = destroyer
          .getParticipantDestroyer((String) participantList.getSelectedValue());
      new ActionTypeParticipantDestroyerConstraintsInfoForm(this, partDest, 
      		destroyer);
      if (destroyer.isGameEndingDestroyer()) {
        // check if any of the attributes have just been set to scoring
        // attributes, and if so, remove scoring status from whatever
        // attribute had this status previously:
        boolean attSetToScoring = false;
        Vector<ActionTypeParticipantConstraint> partConsts = 
        	partDest.getAllConstraints();
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
          Vector<ActionTypeParticipantDestroyer> partDests = 
          	destroyer.getAllParticipantDestroyers();
          for (int i = 0; i < partDests.size(); i++) {
            ActionTypeParticipantDestroyer pDest = partDests.elementAt(i);
            if (pDest != partDest) { // not participant destroyer that you just
            												 // edited
              Vector<ActionTypeParticipantConstraint> partConsts2 = 
              	pDest.getAllConstraints();
              for (int j = 0; j < partConsts2.size(); j++) {
                ActionTypeParticipantConstraint partConst = 
                	partConsts2.elementAt(j);
                ActionTypeParticipantAttributeConstraint[] attConsts = 
                	partConst.getAllAttributeConstraints();
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