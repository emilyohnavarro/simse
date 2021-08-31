/* This class defines the GUI for specifying EffectRules with */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.NumericalAttribute;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class EffectRuleInfoForm extends JDialog implements ActionListener,
    KeyListener, MouseListener {
  private EffectRule ruleInFocus; // copy of rule being edited
  private EffectRule actualRule; // actual rule being edited
  private ActionType actionInFocus; // action to whom this rule belongs
  private ActionTypeParticipant participantInFocus; // participant currently the
                                                    // focus of this GUI
  private SimSEObjectType objectTypeInFocus; // SimSEObjectType of the
                                             // participant currently in focus
  private DefinedActionTypes actions; // existing defined actions
  private boolean newRule; // whether this is a newly created rule (true) or
                           // editing an existing one (false)
  private JTextField lastFocusedTextField; // text field most recently in focus
  private JTextArea echoedTextField; // echoed text field for the button pad gui

  private boolean justClosedButtonPad = false;

  // constants for text field validation:
  private final int OPEN_PAREN = 0;
  private final int CLOSED_PAREN = 1;
  private final int NUMBER = 2;
  private final int OPERATOR = 3;
  private final int DIGIT = 4;

  // button pad:
  private JButton inputButton; // inputs to the rule
  private JButton attributeThisPartButton; // attributes of the participant in
                                           // focus
  private JButton attributeOtherPartButton; // attributes of the other
                                            // participants in the action
  private JButton numObjectsButton; // numbers of objects in the action
  private JButton numActionsThisPartButton; // number of actions the participant
                                            // in focus is involved in
  private JButton numActionsOtherPartButton; // number of actions the other
                                             // participants in the action are
                                             // involved in
  private Vector<JButton> timeButtons; // numbers relating to time
  private JButton timeElapsedButton;
  private JButton actTimeElapsedButton;
  private Vector<JButton> digitButtons; // 0, 1, 2, 3, ...
  private JButton buttonDot;
  private Vector<JButton> operatorButtons; // +, -, *, /, (, )
  private JButton buttonMinus;
  private JButton buttonOpenParen;
  private JButton buttonCloseParen;
  private Vector<JButton> otherButtons; // e.g., random(min, max)
  private JButton randomButton;
  private JButton stringButton;
  private JButton booleanButton;
  private JButton backspaceButton;
  private ButtonPadGUI buttGUI;

  private JLabel partEffectsTitle; // title for the main frame
  private JScrollPane effectsMiddlePane;
  private Box effectsMiddleMiddlePane; // pane for holding all of the
                                       // participant info
  private Vector<JTextField> textFields; // Vector of text fields, one for each 
  																			 // attribute of the object type 
  																			 // currently in focus
  private JList participantList; // list of participants to choose from for
                                 // editing
  private Vector<String> partNames; // list of participants for JList
  private JButton viewEditEffectsButton; // for viewing a participant's effects
  private JButton buttonPadButton; // for bringing up button pad
  private Box otherActEffectPanel; // main panel for the specifying effect on
                                      // participant's other actions:
  private JPanel actDeactActsListsPane;
  private ButtonGroup buttonGroupOtherActEffect; // for following radio buttons
  private JRadioButton activateButton; // for activating all other actions of
                                       // participant in focus
  private JRadioButton deactivateButton; // for deactivating all other actions
                                         // of participant in focus
  private JRadioButton noneButton; // for specifying no effects on the other
                                   // actions of the participant in focus
  private JRadioButton specificButton; // for specifying specific actions to
  																		 // activate/deactivate
  private JLabel activateLabel;
  private JLabel deactivateLabel;
  private JList actionsToActivateList; // for specifying which actions to 
  																		 // activate
  private JList actionsToDeactivateList; // for specifying which actions to
  																			 // deactivate
  private JList ruleInputList; // list for holding rule inputs
  private JButton newRuleInputButton; // for adding a new rule input
  private JButton viewEditInputButton; // for viewing/editing existing rule
                                       // input
  private JButton removeInputButton; // for removing a rule input
  private ButtonGroup timingButtonGroup; // for radio buttons for choosing the
                                         // timing of the rule
  private JRadioButton continuousButton; // for denoting a continuous rule
  private JRadioButton triggerButton; // for denoting a trigger rule
  private JRadioButton destroyerButton; // for denoting a destroyer rule
  private JCheckBox joinCheckBox; // for denoting this rule's relation to
                                  // joining/un-joining
  private JCheckBox visibilityCheckBox; // for denoting this rule's visibility
                                        // in the explanatory tool
  private JButton annotationButton; // for viewing/editing this rule's
                                    // annotation
  private JButton okButton; // for ok'ing the changes made in this form
  private JButton cancelButton; // for cancelling the changes made in this form

  public EffectRuleInfoForm(JFrame owner, EffectRule rule, ActionType act,
      DefinedActionTypes acts, DefinedObjectTypes objs, boolean newOrEdit) {
    super(owner, true);
    newRule = newOrEdit;
    actualRule = rule;
    actionInFocus = act;
    if (!newRule) { // editing existing rule
    	// make a copy of the rule for editing:
      ruleInFocus = (EffectRule) (actualRule.clone()); 
    } else { // new rule
      ruleInFocus = rule;
    }
    actions = acts;
    textFields = new Vector<JTextField>();
    echoedTextField = new JTextArea(50, 100);//250);
    echoedTextField.setEditable(false);
    echoedTextField.setLineWrap(true);
    echoedTextField.setWrapStyleWord(true);

    // Set window title:
    setTitle(ruleInFocus.getName() + " Rule Information - SimSE");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();

    // Create effectsTopPane:
    JPanel effectsTopPane = new JPanel();
    partEffectsTitle = new JLabel("No participant selected");
    effectsTopPane.add(partEffectsTitle);

    // Create effectsMiddlePane:
    effectsMiddleMiddlePane = Box.createVerticalBox();
    effectsMiddlePane = new JScrollPane(effectsMiddleMiddlePane);
    effectsMiddlePane
        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    effectsMiddlePane.setPreferredSize(new Dimension(1000, 200));

    // Radio buttons / Other actions effect panel:
    otherActEffectPanel = Box.createVerticalBox();
    JPanel otherActEffectButtonsPanel = new JPanel(new BorderLayout());
    JPanel radioButtonsPane = new JPanel();
    activateButton = new JRadioButton(OtherActionsEffect.ACTIVATE_ALL);
    activateButton.addActionListener(this);
    deactivateButton = new JRadioButton(OtherActionsEffect.DEACTIVATE_ALL);
    deactivateButton.addActionListener(this);
    noneButton = new JRadioButton(OtherActionsEffect.NONE);
    noneButton.addActionListener(this);
    specificButton = new JRadioButton(
        OtherActionsEffect.ACTIVATE_DEACTIVATE_SPECIFIC_ACTIONS);
    specificButton.addActionListener(this);
    JLabel labelT = new JLabel("Effect on Participant's Other Actions: ");
    radioButtonsPane.add(labelT);
    radioButtonsPane.add(activateButton);
    radioButtonsPane.add(deactivateButton);
    radioButtonsPane.add(noneButton);
    radioButtonsPane.add(specificButton);
    radioButtonsPane.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    buttonGroupOtherActEffect = new ButtonGroup();
    buttonGroupOtherActEffect.add(activateButton);
    buttonGroupOtherActEffect.add(deactivateButton);
    buttonGroupOtherActEffect.add(noneButton);
    buttonGroupOtherActEffect.add(specificButton);
    otherActEffectButtonsPanel.add(radioButtonsPane, BorderLayout.WEST);
    otherActEffectPanel.add(otherActEffectButtonsPanel);
    
    JPanel actDeactActsListsPaneOuter = new JPanel(new BorderLayout());
    actDeactActsListsPane = new JPanel();
    actDeactActsListsPane.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    activateLabel = new JLabel("Activate:");
    actDeactActsListsPane.add(activateLabel);
    actionsToActivateList = new JList();
    actionsToActivateList.setVisibleRowCount(5); // make 5 items visible
    actionsToActivateList.setFixedCellWidth(250);
    JScrollPane actListPane = new JScrollPane(actionsToActivateList);
    actDeactActsListsPane.add(actListPane);
    deactivateLabel = new JLabel("Deactivate:");
    actDeactActsListsPane.add(deactivateLabel);
    actionsToDeactivateList = new JList();
    actionsToDeactivateList.setVisibleRowCount(5); // make 5 items visible
    actionsToDeactivateList.setFixedCellWidth(250);
    JScrollPane deactListPane = new JScrollPane(actionsToDeactivateList);
    actDeactActsListsPane.add(deactListPane);
    actDeactActsListsPaneOuter.add(actDeactActsListsPane, BorderLayout.WEST);
    otherActEffectPanel.add(actDeactActsListsPaneOuter);

    // button pad:
    inputButton = new JButton("Rule Input");
    inputButton.addActionListener(this);
    inputButton.addKeyListener(this);
    attributeThisPartButton = new JButton("Attributes - this participant");
    attributeThisPartButton.addActionListener(this);
    attributeThisPartButton.addKeyListener(this);
    attributeOtherPartButton = new JButton("Attributes - other participants");
    attributeOtherPartButton.addActionListener(this);
    attributeOtherPartButton.addKeyListener(this);
    numObjectsButton = new JButton("Num participants in action");
    numObjectsButton.addActionListener(this);
    numObjectsButton.addKeyListener(this);
    numActionsThisPartButton = new JButton("Num actions - this participant");
    numActionsThisPartButton.addActionListener(this);
    numActionsThisPartButton.addKeyListener(this);
    numActionsOtherPartButton = new JButton("Num actions - other participants");
    numActionsOtherPartButton.addActionListener(this);
    numActionsOtherPartButton.addKeyListener(this);
    // initialize time buttons:
    timeButtons = new Vector<JButton>();
    timeElapsedButton = new JButton("totalTimeElapsed");
    timeElapsedButton.addActionListener(this);
    timeElapsedButton.addKeyListener(this);
    timeButtons.add(timeElapsedButton);
    actTimeElapsedButton = new JButton("actionTimeElapsed");
    actTimeElapsedButton.addActionListener(this);
    actTimeElapsedButton.addKeyListener(this);
    timeButtons.add(actTimeElapsedButton);

    // initialize digit buttons:
    digitButtons = new Vector<JButton>();
    JButton button0 = new JButton("0");
    digitButtons.add(button0);
    JButton button1 = new JButton("1");
    digitButtons.add(button1);
    JButton button2 = new JButton("2");
    digitButtons.add(button2);
    JButton button3 = new JButton("3");
    digitButtons.add(button3);
    JButton button4 = new JButton("4");
    digitButtons.add(button4);
    JButton button5 = new JButton("5");
    digitButtons.add(button5);
    JButton button6 = new JButton("6");
    digitButtons.add(button6);
    JButton button7 = new JButton("7");
    digitButtons.add(button7);
    JButton button8 = new JButton("8");
    digitButtons.add(button8);
    JButton button9 = new JButton("9");
    digitButtons.add(button9);
    buttonDot = new JButton(".");
    digitButtons.add(buttonDot);

    for (int i = 0; i < digitButtons.size(); i++) {
      JButton b = digitButtons.get(i);
      b.addActionListener(this);
      b.addKeyListener(this);
    }

    // initialize operator buttons:
    operatorButtons = new Vector<JButton>();
    JButton buttonPlus = new JButton("+");
    operatorButtons.add(buttonPlus);
    buttonMinus = new JButton("-");
    operatorButtons.add(buttonMinus);
    JButton buttonMultiply = new JButton("*");
    operatorButtons.add(buttonMultiply);
    JButton buttonDivide = new JButton("/");
    operatorButtons.add(buttonDivide);
    buttonOpenParen = new JButton("(");
    operatorButtons.add(buttonOpenParen);
    buttonCloseParen = new JButton(")");
    operatorButtons.add(buttonCloseParen);

    for (int i = 0; i < operatorButtons.size(); i++) {
      JButton b = operatorButtons.get(i);
      b.addActionListener(this);
      b.addKeyListener(this);
    }

    // initialize other buttons:
    otherButtons = new Vector<JButton>();
    randomButton = new JButton("Random(min, max)");
    randomButton.setMnemonic('R');
    otherButtons.add(randomButton);
    stringButton = new JButton("String");
    stringButton.setMnemonic('S');
    otherButtons.add(stringButton);
    booleanButton = new JButton("Boolean");
    booleanButton.setMnemonic('B');
    otherButtons.add(booleanButton);
    backspaceButton = new JButton("Backspace");
    otherButtons.add(backspaceButton);

    for (int i = 0; i < otherButtons.size(); i++) {
      JButton b = otherButtons.get(i);
      b.addActionListener(this);
      b.addKeyListener(this);
    }

    // Create effectsBottomPane:
    JPanel effectsBottomPane = new JPanel();
    // label:
    effectsBottomPane.add(new JLabel("Participants:"));
    // list of participants:
    participantList = new JList();
    participantList.addMouseListener(this);
    participantList.setVisibleRowCount(5); // make 5 items visible at a time
    participantList.setFixedCellWidth(250);
    participantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    JScrollPane participantListPane = new JScrollPane(participantList);
    effectsBottomPane.add(participantListPane);
    refreshParticipantList();
    setupParticipantListSelectionListenerStuff();
    // button:
    viewEditEffectsButton = new JButton("View/Edit Effects");
    viewEditEffectsButton.addActionListener(this);
    viewEditEffectsButton.setEnabled(false);
    effectsBottomPane.add(viewEditEffectsButton);
    buttonPadButton = new JButton("Button Pad");
    buttonPadButton.addActionListener(this);
    buttonPadButton.setEnabled(false);
    effectsBottomPane.add(buttonPadButton);

    // Create inputPane:
    Box inputPane = Box.createVerticalBox();
    JPanel inputTitlePane = new JPanel();
    inputTitlePane.add(new JLabel("Rule Input:"));
    inputPane.add(inputTitlePane);
    JPanel newInputButtonPane = new JPanel();
    newRuleInputButton = new JButton("Add new rule input");
    newRuleInputButton.addActionListener(this);
    newInputButtonPane.add(newRuleInputButton);
    inputPane.add(newInputButtonPane);
    JPanel bottomInputPane = new JPanel();
    bottomInputPane.add(new JLabel("Existing Rule Input:"));
    ruleInputList = new JList();
    ruleInputList.setVisibleRowCount(5); // make 5 items visible at a time
    ruleInputList.setFixedCellWidth(250);
    ruleInputList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    ruleInputList.addMouseListener(this);
    JScrollPane inputListPane = new JScrollPane(ruleInputList);
    refreshRuleInputList();
    setupRuleInputListSelectionListenerStuff();
    bottomInputPane.add(inputListPane);
    // rule button pane:
    Box ruleButtonPane = Box.createVerticalBox();
    viewEditInputButton = new JButton("View/Edit");
    viewEditInputButton.setEnabled(false);
    viewEditInputButton.addActionListener(this);
    JPanel pane1 = new JPanel();
    pane1.add(viewEditInputButton);
    ruleButtonPane.add(pane1);
    removeInputButton = new JButton("Remove   ");
    removeInputButton.setEnabled(false);
    removeInputButton.addActionListener(this);
    JPanel pane2 = new JPanel();
    pane2.add(removeInputButton);
    ruleButtonPane.add(pane2);
    bottomInputPane.add(ruleButtonPane);
    inputPane.add(bottomInputPane);

    // Create timing pane:
    JPanel timingPane = new JPanel();
    timingPane.add(new JLabel("Timing of Rule:"));
    timingButtonGroup = new ButtonGroup();
    continuousButton = new JRadioButton("Continuous Rule");
    continuousButton.setSelected(true);
    continuousButton.addActionListener(this);
    timingPane.add(continuousButton);
    triggerButton = new JRadioButton("Trigger Rule");
    triggerButton.addActionListener(this);
    timingPane.add(triggerButton);
    destroyerButton = new JRadioButton("Destroyer Rule");
    destroyerButton.addActionListener(this);
    timingPane.add(destroyerButton);
    timingButtonGroup.add(continuousButton);
    timingButtonGroup.add(triggerButton);
    timingButtonGroup.add(destroyerButton);

    // Create join pane:
    JPanel joinPane = new JPanel();
    joinCheckBox = new JCheckBox(
        "Re-execute rule with each joining/un-joining participant");
    joinCheckBox.setToolTipText("Re-execute this rule for all participants " +
    		"whenever any participant joins (for a trigger rule) or un-joins (for" +
    		" a destroyer rule)");
    joinPane.add(joinCheckBox);

    refreshTimingButtons();
    refreshJoinCheckBox();

    // Create annotation pane:
    JPanel annotationPane = new JPanel();
    visibilityCheckBox = new JCheckBox("Rule visible in explanatory tool");
    visibilityCheckBox.setToolTipText("Make this rule visible to the "
        + "player in the user interface of the explanatory tool");
    refreshVisibilityCheckBox();
    annotationPane.add(visibilityCheckBox);
    annotationButton = new JButton("View/Edit Annotation");
    annotationButton.setToolTipText("View/edit an annotation for this "
        + "rule");
    annotationButton.addActionListener(this);
    annotationPane.add(annotationButton);

    // Create bottom pane:
    JPanel bottomPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    bottomPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    bottomPane.add(cancelButton);

    // Add panes to main pane:
    mainPane.add(effectsTopPane);
    mainPane.add(effectsMiddlePane);
    mainPane.add(effectsBottomPane);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(1000, 1));
    mainPane.add(separator1);
    mainPane.add(inputPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(1000, 1));
    mainPane.add(separator2);
    mainPane.add(timingPane);
    mainPane.add(joinPane);
    JSeparator separator3 = new JSeparator();
    separator3.setMaximumSize(new Dimension(1000, 1));
    mainPane.add(separator3);
    mainPane.add(annotationPane);
    JSeparator separator4 = new JSeparator();
    separator4.setMaximumSize(new Dimension(1000, 1));
    mainPane.add(separator4);
    mainPane.add(bottomPane);

    // Set main window frame properties:
    addKeyListener(this);
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

  public void mouseClicked(MouseEvent me) {
    int clicks = me.getClickCount();
    Object o = me.getSource();

    if (clicks >= 2) {
      boolean handled = false;
      if (o == participantList && viewEditEffectsButton.isEnabled()) {
        viewEditEffects();
        handled = true;
      } else if (o == ruleInputList) { //&& ruleInputList.getSelectedIndex() >= 
                                    	 // 0)
        editRuleInput(ruleInFocus.getRuleInput((String) ruleInputList
            .getSelectedValue()));
        handled = true;
      } else if (!handled)
        newButtonPad();
    }

  }

  public void mousePressed(MouseEvent me) {}

  public void mouseReleased(MouseEvent me) {}

  public void mouseEntered(MouseEvent me) {}

  public void mouseExited(MouseEvent me) {}

  public void keyPressed(KeyEvent ke) {
    int key = ke.getKeyCode();
    JButton btn = new JButton(ke.getKeyChar() + "");
    boolean enabled = false;

    switch (key) {
    case KeyEvent.VK_BACK_SPACE:
      backspaceButtonChosen();
      break;

    case KeyEvent.VK_R:
      if (randomButton.isEnabled())
        randomButtonChosen();
      break;

    case KeyEvent.VK_S:
      if (stringButton.isEnabled())
        stringButtonChosen();
      break;

    case KeyEvent.VK_B:
      if (booleanButton.isEnabled())
        booleanButtonChosen();
      break;

    case KeyEvent.VK_0:
    case KeyEvent.VK_1:
    case KeyEvent.VK_2:
    case KeyEvent.VK_3:
    case KeyEvent.VK_4:
    case KeyEvent.VK_5:
    case KeyEvent.VK_6:
    case KeyEvent.VK_7:
    case KeyEvent.VK_8:
    case KeyEvent.VK_9:
    case KeyEvent.VK_DECIMAL:
    case KeyEvent.VK_PERIOD:
    case KeyEvent.VK_NUMPAD0:
    case KeyEvent.VK_NUMPAD1:
    case KeyEvent.VK_NUMPAD2:
    case KeyEvent.VK_NUMPAD3:
    case KeyEvent.VK_NUMPAD4:
    case KeyEvent.VK_NUMPAD5:
    case KeyEvent.VK_NUMPAD6:
    case KeyEvent.VK_NUMPAD7:
    case KeyEvent.VK_NUMPAD8:
    case KeyEvent.VK_NUMPAD9:
      for (int i = 0; i < digitButtons.size(); i++) {
        JButton b = digitButtons.get(i);
        if (b.getText().trim().equalsIgnoreCase(ke.getKeyChar() + ""))
          enabled = b.isEnabled();
      }
      if (enabled)
        digitButtonChosen(btn);
      break;
    }

    boolean found = false;
    for (int i = 0; !found && i < digitButtons.size(); i++) {
      JButton abtn = digitButtons.get(i);
      if (abtn.isEnabled()) {
        abtn.requestFocus();
        found = true;
      }
    }
    for (int i = 0; !found && i < otherButtons.size(); i++) {
      JButton abtn = otherButtons.get(i);
      if (abtn.isEnabled()) {
        abtn.requestFocus();
        found = true;
      }
    }
  }

  public void keyReleased(KeyEvent ke) {}

  public void keyTyped(KeyEvent ke) {
    char keyChar = ke.getKeyChar();
    boolean enabled = false;
    switch(keyChar) {
	  case '+':
	  case '-':
	  case '*':
	  case '/':
	  case '(':
	  case ')':
	    for (int i = 0; i < operatorButtons.size(); i++) {
	      JButton b = operatorButtons.get(i);
	      if (b.getText().trim().equalsIgnoreCase(keyChar + ""))
	        enabled = b.isEnabled();
	    }
	    if (enabled)
	      operatorButtonChosen(new JButton(keyChar + ""));
	    break;
	  }
	
	  boolean found = false;
	  for (int i = 0; !found && i < operatorButtons.size(); i++) {
	    JButton abtn = operatorButtons.get(i);
	    if (abtn.isEnabled()) {
	      abtn.requestFocus();
	      found = true;
	    }
	  }
  }

  // handles user actions
  public void actionPerformed(ActionEvent evt) { 
    if (buttGUI != null)
      buttGUI.requestFocus();

    Object source = evt.getSource();
    if (source == viewEditEffectsButton) {
      viewEditEffects();
    } else if (source == buttonPadButton) {
      newButtonPad();
    } else if (source == inputButton) {
      inputButtonChosen();
    } else if (source == attributeThisPartButton) {
      attributeThisPartButtonChosen();
    } else if (source == attributeOtherPartButton) {
      attributeOtherPartButtonChosen();
    } else if (source == numObjectsButton) {
      numObjectsButtonChosen();
    } else if (source == numActionsThisPartButton) {
      numActionsThisPartButtonChosen();
    } else if (source == numActionsOtherPartButton) {
      numActionsOtherPartButtonChosen();
    } else if (source == timeElapsedButton) {
      // append the text to the text field:
      if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
      		(getLastToken(echoedTextField.getText()).equals("-"))) { // negative
      																														 // sign was 
      																														 // last token
        setEchoedTextFieldText(echoedTextField.getText().trim().concat(
            "totalTimeElapsed "));
      } else {
        setEchoedTextFieldText(echoedTextField.getText().concat(
            "totalTimeElapsed "));
      }
      refreshButtonPad(NUMBER);
    } else if (source == actTimeElapsedButton) {
      actTimeElapsedButtonChosen();
    } else if (source == randomButton) {
      randomButtonChosen();
    } else if (source == activateButton) {
      setActionListsEnabled(false);
    } else if (source == deactivateButton) {
      setActionListsEnabled(false);
    } else if (source == noneButton) {
      setActionListsEnabled(false);
    } else if (source == specificButton) {
      setActionListsEnabled(true);
      refreshActionLists();
    } else if (source == newRuleInputButton) {
      newRuleInput();
    } else if (source == viewEditInputButton) {
      if (ruleInputList.getSelectedIndex() >= 0) { // a rule input is selected
        editRuleInput(ruleInFocus.getRuleInput((String) ruleInputList
            .getSelectedValue()));
      }
    } else if (source == removeInputButton) {
      if (ruleInputList.getSelectedIndex() >= 0) { // a rule input is selected
        removeRuleInput((String) ruleInputList.getSelectedValue());
      }
    } else if (source == triggerButton) { // trigger button has been clicked
      joinCheckBox.setEnabled(true);
    } else if (source == destroyerButton) { // destroyer button has been clicked
      joinCheckBox.setEnabled(true);
    } else if (source == continuousButton) { // continuous button has been 
    																				 // clicked
      joinCheckBox.setEnabled(false);
    } else if (source == annotationButton) { // annotation button selected
      new RuleAnnotationForm(this, ruleInFocus);
    } else if (source == cancelButton) { // cancel button has been pressed
      // Close window:
      setVisible(false);
      dispose();
    } else if (source == okButton) { // okButton has been pressed
      setParticipantInFocusDataFromForm();
      // check validity of input of object type currently in focus:
      Vector<String> errors = validateInput(); 
      if (errors.size() == 0) { // input valid
        if (!newRule) { // existing rule being edited
          // set the values to the actual participant from the copy whose values
          // have been edited:
          actualRule.setParticipantRuleEffects(ruleInFocus
              .getAllParticipantRuleEffects());
          actualRule.setRuleInputs(ruleInFocus.getAllRuleInputs());
          actualRule.setTiming(ruleInFocus.getTiming());
          actualRule.setVisibilityInExplanatoryTool(ruleInFocus
              .isVisibleInExplanatoryTool());
          actualRule.setAnnotation(ruleInFocus.getAnnotation());
          actualRule.setExecuteOnJoins(ruleInFocus.getExecuteOnJoins());
        } else { // new rule
          actionInFocus.addRule(ruleInFocus);
        }

        // close window:
        setVisible(false);
        dispose();
      } else { // input not valid
        for (int i = 0; i < errors.size(); i++) {
          JOptionPane.showMessageDialog(null, errors.elementAt(i),
          		"Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
      }
    } else if (source == stringButton) {
      stringButtonChosen();
    } else if (source == booleanButton) {
      booleanButtonChosen();
    } else if (source == backspaceButton) {
      backspaceButtonChosen();
    } else if (source instanceof JButton) {
    	JButton buttonSource = (JButton)source;
      if ((buttonSource.getText().length() == 1) && 
      		((Character.isDigit(buttonSource.getText().toCharArray()[0])) || 
      				(buttonSource.getText().toCharArray()[0] == '.'))) { // digit 
      																														 // button (or
      																														 // '.' 
      																														 // button)
	      digitButtonChosen(buttonSource);
	    } else if ((buttonSource.getText().equals("+")) || 
	    		(buttonSource.getText().equals("-")) || 
	    		(buttonSource.getText().equals("*")) || 
	    		(buttonSource.getText().equals("/"))) { // operator button chosen
	      operatorButtonChosen((JButton) source);
	    } else if (buttonSource.getText().equals("(")) {
	      // append the text to the text field:
	      if ((getLastTokenType(echoedTextField.getText()) == DIGIT)
	          && (getLastToken(echoedTextField.getText()).equals("-"))) { 
	      	// negative sign was last token
	        setEchoedTextFieldText(echoedTextField.getText().trim().concat(
	            "("));
	      } else {
	        setEchoedTextFieldText(echoedTextField.getText().concat("("));
	      }
	      refreshButtonPad(OPEN_PAREN);
	    } else if (buttonSource.getText().equals(")")) {
	      // append the text to the text field:
	      setEchoedTextFieldText(echoedTextField.getText().trim()
	          .concat(") "));
	      refreshButtonPad(CLOSED_PAREN);
	    }
    }
  }

  private void refreshParticipantList() {
    partNames = new Vector<String>();
    Vector<ActionTypeParticipant> parts = actionInFocus.getAllParticipants();
    for (int i = 0; i < parts.size(); i++) {
      ActionTypeParticipant tempPart = parts.elementAt(i);
      partNames.add(tempPart.getName() + ":"); // add each participant's name to
                                               // the list
      Vector<SimSEObjectType> objTypes = tempPart.getAllSimSEObjectTypes();
      for (int j = 0; j < objTypes.size(); j++) {
        SimSEObjectType tempType = objTypes.elementAt(j);
        partNames.add("--" + tempType.getName() + " "
            + SimSEObjectTypeTypes.getText(tempType.getType()));
      }
    }
    participantList.setListData(partNames);
  }

  /*
   * enables view/edit effects button whenever a list item (participant's object
   * type) is selected
   */
  private void setupParticipantListSelectionListenerStuff() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = participantList.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if ((lsm.isSelectionEmpty() == false) && 
        		(((String) participantList.getSelectedValue()).startsWith("--"))) { 
        	// an object type is selected (not a participant name)
          viewEditEffectsButton.setEnabled(true);
        } else if ((lsm.isSelectionEmpty() == false) && 
        		(((String) participantList.getSelectedValue()).startsWith("--")) == 
        			false) { // a participant name is selected
          viewEditEffectsButton.setEnabled(false);
        }
      }
    });
  }

  private void refreshRuleInputList() {
    Vector<RuleInput> inputs = ruleInFocus.getAllRuleInputs();
    Vector<String> inputNames = new Vector<String>();
    for (int i = 0; i < inputs.size(); i++) {
      inputNames.add(inputs.elementAt(i).getName());
    }
    ruleInputList.setListData(inputNames);
  }

  private void refreshTimingButtons() {
    int timing = ruleInFocus.getTiming();
    if (timing == RuleTiming.CONTINUOUS) {
      continuousButton.setSelected(true);
      triggerButton.setSelected(false);
      destroyerButton.setSelected(false);
      joinCheckBox.setEnabled(false);
    } else if (timing == RuleTiming.TRIGGER) {
      triggerButton.setSelected(true);
      continuousButton.setSelected(false);
      destroyerButton.setSelected(false);
      joinCheckBox.setEnabled(true);
    } else if (timing == RuleTiming.DESTROYER) {
      destroyerButton.setSelected(true);
      continuousButton.setSelected(false);
      triggerButton.setSelected(false);
      joinCheckBox.setEnabled(true);
    }
  }

  // refreshes the actionsToActivate and actionsToDeactivate lists
  private void refreshActionLists() {
    /*
     * make a Vector of the names of all possible actions this object 
     * could participate in:
     */
    Vector<String> relevantActs = new Vector<String>();
    Vector<ActionType> allActs = actions.getAllActionTypes();
    for (int i = 0; i < allActs.size(); i++) {
      ActionType tempAct = allActs.elementAt(i);
      Vector<ActionTypeParticipant> parts = tempAct.getAllParticipants();
      for (int j = 0; j < parts.size(); j++) {
        ActionTypeParticipant tempPart = parts.elementAt(j);
        if (tempPart.hasSimSEObjectType(objectTypeInFocus.getName())) {
          relevantActs.add(tempAct.getName());
          break;
        }
      }
    }
    actionsToActivateList.setListData(relevantActs);
    actionsToDeactivateList.setListData(relevantActs);
    actionsToActivateList.clearSelection();
    actionsToDeactivateList.clearSelection();
    
    // set the proper actions to activate to selected/unselected:
    Vector<ActionType> actActions = ruleInFocus.getParticipantRuleEffect(
        participantInFocus.getName()).getParticipantTypeEffect(
            objectTypeInFocus).getOtherActionsEffect().getActionsToActivate();
    for (int i = 0; i < actActions.size(); i ++) {
      ActionType tempAct = actActions.elementAt(i);
      for (int j = 0; j < relevantActs.size(); j++) {
        String listActName = relevantActs.elementAt(j);
        if (tempAct.getName().equals(listActName)) {
          actionsToActivateList.addSelectionInterval(j, j);
          break;
        }
      }
    }
    
    // set the proper actions to deactivate selected/unselected:
    Vector<ActionType> deactActions = ruleInFocus.getParticipantRuleEffect(
        participantInFocus.getName()).getParticipantTypeEffect(
            objectTypeInFocus).getOtherActionsEffect().
            getActionsToDeactivate();
    for (int i = 0; i < deactActions.size(); i ++) {
      ActionType tempAct = deactActions.elementAt(i);
      for (int j = 0; j < relevantActs.size(); j++) {
        String listActName = relevantActs.elementAt(j);
        if (tempAct.getName().equals(listActName)) {
          actionsToDeactivateList.addSelectionInterval(j, j);
          break;
        }
      }
    }
  }

  private void refreshJoinCheckBox() {
    joinCheckBox.setSelected(ruleInFocus.getExecuteOnJoins());
  }

  private void refreshVisibilityCheckBox() {
    visibilityCheckBox.setSelected(ruleInFocus.isVisibleInExplanatoryTool());
  }

  /*
   * enables view/edit and remove buttons whenever a list item (rule input) is
   * selected
   */
  private void setupRuleInputListSelectionListenerStuff() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = ruleInputList.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          viewEditInputButton.setEnabled(true);
          removeInputButton.setEnabled(true);
        }
      }
    });
  }

  private void viewEditEffects() {
    int selectedIndex = participantList.getSelectedIndex();
    if (selectedIndex >= 0) { // a participant is selected
      String selectedString = (String) participantList.getSelectedValue();
      // move up in the list from the selected item 'til you find the
      // participant name:
      int i = (selectedIndex - 1);
      String item = partNames.elementAt(i);
      while (item.startsWith("--")) {
        i--;
        item = partNames.elementAt(i);
      }
      // trim off the colon to get the participant name:
      ActionTypeParticipant tempPart = actionInFocus.getParticipant(item
          .substring(0, (item.length() - 1))); 
      // trim off dashes at beginning to get the type name:
      SimSEObjectType tempType = tempPart.getSimSEObjectType(selectedString
          .substring(2, selectedString.indexOf(" ")));
      if ((objectTypeInFocus != null) && (((tempPart == participantInFocus) && 
      		(tempType != objectTypeInFocus)) || 
      		(tempPart != participantInFocus))) { // selection is not the 
      																				 // SimSEObjectType and 
      																				 // participant that was already 
      																				 // in focus
        setParticipantInFocusDataFromForm();
        setParticipantInFocus(tempPart, tempType); // set the new focus
      } else if (objectTypeInFocus == null) {
        setParticipantInFocus(tempPart, tempType); // set the new focus
      }
    }
  }

  // returns a vector of error messages (if any)
  private Vector<String> validateInput() { 
    Vector<String> messages = new Vector<String>();
    Vector<ParticipantRuleEffect> partEffects = 
    	ruleInFocus.getAllParticipantRuleEffects();
    // go through all ParticipantRuleEffects to validate them:
    for (int i = 0; i < partEffects.size(); i++) {
      ParticipantRuleEffect tempRuleEffect = partEffects.elementAt(i);
      Vector<ParticipantTypeRuleEffect> partTypeEffects = 
      	tempRuleEffect.getAllParticipantTypeEffects();

      // go through all ParticipantTypeRuleEffects to validate them:
      for (int j = 0; j < partTypeEffects.size(); j++) {
        ParticipantTypeRuleEffect tempTypeRuleEffect = 
        	partTypeEffects.elementAt(j);
        Vector<ParticipantAttributeRuleEffect> attRuleEffects = 
        	tempTypeRuleEffect.getAllAttributeEffects();
        // go through all ParticipantAttributeRuleEffects to validate them:
        for (int k = 0; k < attRuleEffects.size(); k++) {
          String message = new String();
          ParticipantAttributeRuleEffect tempAttEffect = 
          	attRuleEffects.elementAt(k);
          if ((tempAttEffect.getAttribute().getType() == 
          	AttributeTypes.INTEGER) || 
          	(tempAttEffect.getAttribute().getType() == AttributeTypes.DOUBLE)) {
          	// numerical attribute (needs checking)
            String effect = tempAttEffect.getEffect();
            if ((effect != null) && (effect.length() > 0)) { // non-empty
              // Check for num open paren > num close paren:
              int numOpen = 0;
              int numClose = 0;
              char[] text = effect.toCharArray();
              for (int m = 0; m < text.length; m++) {
                if (text[m] == '(') {
                  numOpen++;
                } else if (text[m] == ')') {
                  numClose++;
                }
              }
              if (numOpen > numClose) {
                message = ("Invalid expression for "
                    + tempRuleEffect.getParticipant().getName()
                    + " "
                    + tempTypeRuleEffect.getSimSEObjectType().getName()
                    + " "
                    + SimSEObjectTypeTypes.getText(tempTypeRuleEffect
                        .getSimSEObjectType().getType()) + " "
                    + tempAttEffect.getAttribute().getName() + " attribute: " +
                    		"number of opening parentheses > number of closing " +
                    		"parentheses");
              }

              // Check for ending with an invalid ending token:
              String lastToken = getLastToken(effect);
              int lastTokenType = getLastTokenType(effect);
              if ((lastToken.trim().equals("-"))
                  || (lastToken.trim().equals("."))
                  || (lastTokenType == OPEN_PAREN)
                  || (lastTokenType == OPERATOR)) { // invalid ending tokens
                if ((message != null) && (message.length() > 0)) { // message
                                                                 	 // already 
                																									 // has some 
                																									 // text
                  // just concatenate the rest of it:
                  message = message
                      .concat("; incomplete expression / invalid ending token");
                } else { // message empty at this point
                  message = ("Invalid expression for "
                      + tempRuleEffect.getParticipant().getName()
                      + " "
                      + tempTypeRuleEffect.getSimSEObjectType().getName()
                      + " "
                      + SimSEObjectTypeTypes.getText(tempTypeRuleEffect
                          .getSimSEObjectType().getType()) + " "
                      + tempAttEffect.getAttribute().getName() + 
                      ": incomplete expression / invalid ending token");
                }
              }
              if ((message != null) && (message.length() > 0)) { // message got
                                                               	 // instantiated
                messages.add(message);
              }
            }
          }
        }
      }
    }
    return messages;
  }

  /*
   * Takes what's currently in the form for the participant in focus and sets
   * the current temporary copy of that participant with those values for the
   * object type in focus
   */
  private void setParticipantInFocusDataFromForm() {
    // timing:
    if (continuousButton.isSelected()) {
      ruleInFocus.setTiming(RuleTiming.CONTINUOUS);
    } else if (triggerButton.isSelected()) {
      ruleInFocus.setTiming(RuleTiming.TRIGGER);
    } else if (destroyerButton.isSelected()) {
      ruleInFocus.setTiming(RuleTiming.DESTROYER);
    }

    // executeOnJoins status:
    ruleInFocus.setExecuteOnJoins(joinCheckBox.isSelected()
        && joinCheckBox.isEnabled());

    // explanatory tool visibility:
    ruleInFocus.setVisibilityInExplanatoryTool(visibilityCheckBox.isSelected());

    if (objectTypeInFocus != null) {
      Vector<Attribute> attributes = objectTypeInFocus.getAllAttributes();
      for (int i = 0; i < attributes.size(); i++) {
        // get the participant attribute rule effect for the attribute:
        Attribute tempAtt = attributes.elementAt(i);
        ParticipantAttributeRuleEffect attEffect = ruleInFocus
            .getParticipantRuleEffect(participantInFocus.getName())
            .getParticipantTypeEffect(objectTypeInFocus).getAttributeEffect(
                tempAtt.getName());

        // set the effect from the corresponding text field:
        attEffect.setEffect(((JTextField) textFields.elementAt(i)).getText());
      }

      // set the other actions effects:
      if (activateButton.isSelected()) { // activate all
        ruleInFocus.getParticipantRuleEffect(
        		participantInFocus.getName()).getParticipantTypeEffect(
        				objectTypeInFocus).getOtherActionsEffect().setEffect(
        						OtherActionsEffect.ACTIVATE_ALL);
      } 
      else if (deactivateButton.isSelected()) { // deactivate all
        ruleInFocus.getParticipantRuleEffect(
        		participantInFocus.getName()).getParticipantTypeEffect(
        				objectTypeInFocus).getOtherActionsEffect().setEffect(
        						OtherActionsEffect.DEACTIVATE_ALL);
      } 
      else if (noneButton.isSelected()) { // none
        ruleInFocus.getParticipantRuleEffect(
        		participantInFocus.getName()).getParticipantTypeEffect(
        				objectTypeInFocus).getOtherActionsEffect().setEffect(
        						OtherActionsEffect.NONE);
      }
      else if (specificButton.isSelected()) { // specific
        ruleInFocus.getParticipantRuleEffect(
        		participantInFocus.getName()).getParticipantTypeEffect(
        				objectTypeInFocus).getOtherActionsEffect().setEffect(
        						OtherActionsEffect.ACTIVATE_DEACTIVATE_SPECIFIC_ACTIONS);
        
        // clear current settings for specific actions to activate/deactivate:
        ruleInFocus.getParticipantRuleEffect(participantInFocus.getName())
        	.getParticipantTypeEffect(objectTypeInFocus).
        	getOtherActionsEffect().clearAllActionsToActivate();
        ruleInFocus.getParticipantRuleEffect(participantInFocus.getName())
        	.getParticipantTypeEffect(objectTypeInFocus).
        	getOtherActionsEffect().clearAllActionsToDeactivate();
        
        // reset actions to activate:
        int[] selectedActActs = actionsToActivateList.getSelectedIndices();
        for (int i = 0; i < actionsToActivateList.getModel().getSize(); i++) {
          if (Arrays.binarySearch(selectedActActs, i) >= 0) { // selected
            String listItem = (String)actionsToActivateList.getModel().
          		getElementAt(i);
            ruleInFocus.getParticipantRuleEffect(participantInFocus.getName()).
            	getParticipantTypeEffect(objectTypeInFocus).
            	getOtherActionsEffect().addActionToActivate(actions.
            	    getActionType(listItem));
          }
        }
        
        // reset actions to deactivate:
        int[] selectedDeactActs = actionsToDeactivateList.getSelectedIndices();
        for (int i = 0; i < actionsToDeactivateList.getModel().getSize(); i++) {
          if (Arrays.binarySearch(selectedDeactActs, i) >= 0) { // selected
            String listItem = (String)actionsToDeactivateList.getModel().
          		getElementAt(i);
            ruleInFocus.getParticipantRuleEffect(participantInFocus.getName()).
            	getParticipantTypeEffect(objectTypeInFocus).
            	getOtherActionsEffect().addActionToDeactivate(actions.
            	    getActionType(listItem));
          }
        }
      }
    }
  }

  // makes the specified participant and object type the focus of the gui
  private void setParticipantInFocus(ActionTypeParticipant part,
      SimSEObjectType type) { 
    participantInFocus = part;
    objectTypeInFocus = type;

    partEffectsTitle.setText(participantInFocus.getName() + " "
        + objectTypeInFocus.getName() + " "
        + SimSEObjectTypeTypes.getText(objectTypeInFocus.getType())
        + " Effects:"); // set the title

    // clear data structures:
    textFields.removeAllElements();
    effectsMiddleMiddlePane.removeAll();

    JPanel middlePane = new JPanel();
    JPanel namePanel = new JPanel(new GridLayout(0, 1, 0, 3));
    JPanel equalsPanel = new JPanel(new GridLayout(0, 1, 0, 3));
    JPanel expPanel = new JPanel(new GridLayout(0, 1, 0, 1));
    // get all attributes:
    Vector<Attribute> attributes = type.getAllAttributes();
    // go through each one and add its info:
    for (int index = 0; index < attributes.size(); index++) { 
      Attribute att = attributes.elementAt(index);
      // name and type info:
      StringBuffer attLabel = new StringBuffer(att.getName() + " ("
          + AttributeTypes.getText(att.getType())); // attribute name & type
      if ((att.getType() == AttributeTypes.INTEGER)
          || (att.getType() == AttributeTypes.DOUBLE)) { // double or integer
                                                       	 // attribute
      	NumericalAttribute numAtt = (NumericalAttribute)att;
        if (!numAtt.isMinBoundless()) {
          attLabel.append(", min value = " + numAtt.getMinValue().toString());
        }
        if (!numAtt.isMaxBoundless()) {
          attLabel.append(", max value = " + numAtt.getMaxValue().toString());
        }
      }
      attLabel.append(")");
      JLabel label = new JLabel(attLabel.toString());
      label.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
      namePanel.add(label);

      // equals sign:
      JLabel equalsLabel = new JLabel(" = ");
      equalsLabel.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
      equalsPanel.add(equalsLabel);

      // text field:
      JTextField expTextField = new JTextField(500);
      if (att.isKey()) {
        expTextField.setEditable(false);
      } else {
        expTextField.addMouseListener(this);
	      // set the text field to the correct value:
	      ParticipantRuleEffect a = ruleInFocus
	          .getParticipantRuleEffect(participantInFocus.getName());
	      ParticipantTypeRuleEffect b = a
	          .getParticipantTypeEffect(objectTypeInFocus);
	      ParticipantAttributeRuleEffect c = b.getAttributeEffect(att.getName());
	      String exp = c.getEffect();
	      if ((exp != null) && (exp.length() > 0)) { // non-empty
	        expTextField.setText(exp);
	      }
	
	      // make it so whenever the text field gains focus, the button pad will 
	      // be refreshed:
	      FocusListener l = new FocusListener() {
	        public void focusGained(FocusEvent ev) {
	          if (!justClosedButtonPad) { // valid focus gained, not just as a 
	          														// result of closing the button pad -- 
	          														// if you don't do this, it will bring 
	          														// the first text field into focus every
	          														// time you close the button pad.
	            if (lastFocusedTextField != null) { // there was a text field in 
	            																		// focus before
	              lastFocusedTextField.setBackground(null);
	            }
	            lastFocusedTextField = (JTextField) ev.getSource();
	            lastFocusedTextField.setBackground(Color.YELLOW);
	            refreshButtonPad();
	            buttonPadButton.setEnabled(true);
	          } else { // just closed button pad
	            lastFocusedTextField.requestFocus();
	            justClosedButtonPad = false;
	          }
	        }
	
	        public void focusLost(FocusEvent ev) {}
	      };
	      expTextField.addFocusListener(l);
      }

      expPanel.add(expTextField);
      textFields.add(expTextField);
    }

    // other actions effect:
    actionsToActivateList.clearSelection();
    actionsToDeactivateList.clearSelection();
    if (ruleInFocus.getParticipantRuleEffect(participantInFocus.getName())
        .getParticipantTypeEffect(objectTypeInFocus).getOtherActionsEffect()
        .getEffect().equals(OtherActionsEffect.ACTIVATE_ALL)) {
      activateButton.setSelected(true);
      deactivateButton.setSelected(false);
      noneButton.setSelected(false);
      specificButton.setSelected(false);
      setActionListsEnabled(false);
    } else if (ruleInFocus.getParticipantRuleEffect(participantInFocus
        .getName()).getParticipantTypeEffect(objectTypeInFocus)
        .getOtherActionsEffect().getEffect().equals(
            OtherActionsEffect.DEACTIVATE_ALL)) {
      deactivateButton.setSelected(true);
      activateButton.setSelected(false);
      noneButton.setSelected(false);
      specificButton.setSelected(false);
      actDeactActsListsPane.setEnabled(false);
      setActionListsEnabled(false);
    } else if (ruleInFocus.getParticipantRuleEffect(participantInFocus
        .getName()).getParticipantTypeEffect(objectTypeInFocus)
        .getOtherActionsEffect().getEffect().equals(
            OtherActionsEffect.ACTIVATE_DEACTIVATE_SPECIFIC_ACTIONS)) {
      deactivateButton.setSelected(false);
      activateButton.setSelected(false);
      noneButton.setSelected(false);
      specificButton.setSelected(true);
      actDeactActsListsPane.setEnabled(true);
      setActionListsEnabled(true);
      refreshActionLists();      
    } else { // no other actions effect
      noneButton.setSelected(true);
      activateButton.setSelected(false);
      deactivateButton.setSelected(false);
      specificButton.setSelected(false);
      actDeactActsListsPane.setEnabled(false);
      setActionListsEnabled(false);
    }

    buttonPadButton.setEnabled(false);

    middlePane.add(namePanel);
    middlePane.add(equalsPanel);
    middlePane.add(expPanel);
    effectsMiddleMiddlePane.add(middlePane);
    effectsMiddleMiddlePane.add(otherActEffectPanel);

    repaint();
  }
  
  private void setActionListsEnabled(boolean enable) {
    activateLabel.setEnabled(enable);
    deactivateLabel.setEnabled(enable);
    actionsToActivateList.setEnabled(enable);
    actionsToDeactivateList.setEnabled(enable);
  }

  // creates a new rule input and adds it to the action in focus
  private void newRuleInput() { 
    String response = JOptionPane.showInputDialog(null,
        "Enter a name for this Rule Input:", "Enter Rule Input Name",
        JOptionPane.QUESTION_MESSAGE); // Show input dialog
    if (response != null) {
      if (ruleInputNameInputValid(response) == false) { // input is invalid
      	// warn user to enter a valid name:
        JOptionPane
            .showMessageDialog(
                null,
                "Please enter a unique name, between 1 and 40 alphanumeric " +
                "characters, and no spaces",
                "Invalid Input", JOptionPane.WARNING_MESSAGE); 
        newRuleInput(); // try again
      } else { // user has entered valid input
        RuleInput newInput = new RuleInput(response); // create new rule input
        RuleInputInfoForm rInfoForm = new RuleInputInfoForm(this, ruleInFocus,
            newInput);

        // Makes it so this gui will refresh itself after this rule input info
        // form closes:
        WindowFocusListener l = new WindowFocusListener() {
          public void windowLostFocus(WindowEvent ev) {
            refreshRuleInputList();
            viewEditInputButton.setEnabled(false);
            removeInputButton.setEnabled(false);
          }

          public void windowGainedFocus(WindowEvent ev) {}
        };
        rInfoForm.addWindowFocusListener(l);
      }
    }
  }

  // returns true if input is a valid rule input name, false if not
  private boolean ruleInputNameInputValid(String input) { 
    char[] cArray = input.toCharArray();

    // Check for length constraints:
    if ((cArray.length == 0) || (cArray.length > 40)) { // user has entered
                                                      	// nothing or a string
                                                      	// longer than 40 chars
      return false;
    }

    // Check for invalid characters:
    for (int i = 0; i < cArray.length; i++) {
      if (!Character.isLetterOrDigit(cArray[i])) { // character is not a letter 
      																						 // or a digit (hence, 
      																						 // invalid)
        return false;
      }
    }

    // Check for uniqueness of name:
    Vector<RuleInput> existingInputs = ruleInFocus.getAllRuleInputs();
    for (int i = 0; i < existingInputs.size(); i++) {
      RuleInput tempInput = existingInputs.elementAt(i);
      if (tempInput.getName().equalsIgnoreCase(input)) { // name not unique
        return false;
      }
    }
    return true; // none of the invalid conditions exist
  }

  // brings up the window for editing an existing rule input
  private void editRuleInput(RuleInput input) { 
    RuleInputInfoForm rInfoForm = new RuleInputInfoForm(this, ruleInFocus,
        input);

    // Makes it so this gui will refresh itself after this rule input info form
    // closes:
    WindowFocusListener l = new WindowFocusListener() {
      public void windowLostFocus(WindowEvent ev) {
        refreshRuleInputList();
        viewEditInputButton.setEnabled(false);
        removeInputButton.setEnabled(false);
      }

      public void windowGainedFocus(WindowEvent ev) {}
    };
    rInfoForm.addWindowFocusListener(l);
  }

  // removes the rule input with the specified name from the rule in focus
  private void removeRuleInput(String inputName) { 
    int choice = JOptionPane.showConfirmDialog(null, ("Really remove "
        + inputName + "rule input?"), "Confirm Rule Input Removal",
        JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
    	// remove rule input from rule in focus:
      ruleInFocus.removeRuleInput((String) ruleInputList.getSelectedValue()); 
      viewEditInputButton.setEnabled(false);
      removeInputButton.setEnabled(false);
      refreshRuleInputList();
    }
  }
  
  // creates a new button pad for the participant in focus
  private void newButtonPad() { 
    // bring up new button pad:
    echoedTextField.setText(lastFocusedTextField.getText());
    refreshButtonPad();
    refreshButtonPad(getLastTokenType(lastFocusedTextField.getText()));
    buttGUI = new ButtonPadGUI(this, inputButton, attributeThisPartButton,
        attributeOtherPartButton, numObjectsButton, numActionsThisPartButton,
        numActionsOtherPartButton, timeButtons, digitButtons, operatorButtons,
        otherButtons, echoedTextField, lastFocusedTextField, 
        new String(objectTypeInFocus.getName() + "." + 
            getAttributeInFocus().getName()));

    // Makes it so this gui will refresh itself after this rule input info form
    // closes:
    WindowFocusListener l = new WindowFocusListener() {
      public void windowLostFocus(WindowEvent ev) {
        justClosedButtonPad = true;
      }

      public void windowGainedFocus(WindowEvent ev) {}
    };
    buttGUI.addWindowFocusListener(l);

    buttonPadButton.setEnabled(false);
  }

  /*
   * refreshes the button pad by enabling/disabling buttons according to the
   * attribute in focus and what's in the field in focus
   */
  private void refreshButtonPad() { 
    // get the attInFocus:
    Attribute attInFocus = getAttributeInFocus();

    if ((attInFocus != null) && 
    		((attInFocus.getType() == AttributeTypes.STRING) || 
    				(attInFocus.getType() == AttributeTypes.BOOLEAN))) { // string or 
    																														 // boolean
    																														 // attribute
      Vector<RuleInput> inputs = ruleInFocus.getAllRuleInputs();
      inputButton.setEnabled(false);
      if (attInFocus.getType() == AttributeTypes.STRING) { // string attribute
        // enable/disable input button, depending on what kind of inputs
        // currently exist:
        for (int i = 0; i < inputs.size(); i++) {
          if (inputs.elementAt(i).getType().equals(InputType.STRING)) { 
          	// string rule input type
            inputButton.setEnabled(true);
            break;
          }
        }
        // enable string button:
        stringButton.setEnabled(true);
        // disable boolean button:
        booleanButton.setEnabled(false);
      } else if (attInFocus.getType() == AttributeTypes.BOOLEAN) { // boolean
                                                                 	 // attribute
        // enable/disable input button, depending on what kind of inputs
        // currently exist:
        for (int i = 0; i < inputs.size(); i++) {
          if (inputs.elementAt(i).getType().equals(InputType.BOOLEAN)) { 
          	// boolean rule input type
            inputButton.setEnabled(true);
            break;
          }
        }
        // enable boolean button:
        booleanButton.setEnabled(true);
        // disable string button:
        stringButton.setEnabled(false);
      }
      // enable buttons:
      attributeThisPartButton.setEnabled(true);
      // disable other participant attribute button:
      attributeOtherPartButton.setEnabled(false);
      // disable all numerical buttons:
      numObjectsButton.setEnabled(false);
      numActionsThisPartButton.setEnabled(false);
      numActionsOtherPartButton.setEnabled(false);
      for (int i = 0; i < timeButtons.size(); i++) {
        timeButtons.elementAt(i).setEnabled(false);
      }
      for (int i = 0; i < digitButtons.size(); i++) {
        digitButtons.elementAt(i).setEnabled(false);
      }
      for (int i = 0; i < operatorButtons.size(); i++) {
        operatorButtons.elementAt(i).setEnabled(false);
      }
      randomButton.setEnabled(false);
    } else if ((attInFocus != null) && 
    		((attInFocus.getType() == AttributeTypes.INTEGER) || 
    				(attInFocus.getType() == AttributeTypes.DOUBLE))) { // integer or 
    																														// double
    																														// attribute
      // enable or disable input button, depending on the types of inputs that
      // currently exist:
      Vector<RuleInput> inputs = ruleInFocus.getAllRuleInputs();
      inputButton.setEnabled(false);
      for (int i = 0; i < inputs.size(); i++) {
        if ((inputs.elementAt(i).getType().equals(InputType.INTEGER)) || 
        		(inputs.elementAt(i).getType().equals(InputType.DOUBLE))) { 
        	// numerical rule input type
          inputButton.setEnabled(true);
          break;
        }
      }
      // disable string & boolean buttons:
      stringButton.setEnabled(false);
      booleanButton.setEnabled(false);
      // enable all other buttons:
      attributeThisPartButton.setEnabled(true);
      attributeOtherPartButton.setEnabled(true);
      numObjectsButton.setEnabled(true);
      numActionsThisPartButton.setEnabled(true);
      numActionsOtherPartButton.setEnabled(true);
      for (int i = 0; i < timeButtons.size(); i++) {
        timeButtons.elementAt(i).setEnabled(true);
      }
      for (int i = 0; i < digitButtons.size(); i++) {
        digitButtons.elementAt(i).setEnabled(true);
      }
      for (int i = 0; i < operatorButtons.size(); i++) {
        operatorButtons.elementAt(i).setEnabled(true);
      }
      randomButton.setEnabled(true);
      // refresh the button pad as if an operator has just been entered:
      refreshButtonPad(OPERATOR); 
    }
  }

  /*
   * refreshes the button pad by disabling buttons according to what the last
   * entry into the field was
   */
  private void refreshButtonPad(int lastTokenType) { 
    if ((getAttributeInFocus().getType() == AttributeTypes.STRING) || 
    		(getAttributeInFocus().getType() == AttributeTypes.BOOLEAN)) { 
    	// boolean or string attribute
      if ((echoedTextField.getText() != null) && 
      		(echoedTextField.getText().length() > 0)) { // non-empty text field
        disableAllButtons();
      } else { // empty text field
        // Rule input button:
        if (inputButton.isEnabled()) { // if button is enabled, check if you 
        															 // need to disable it or not:
          Attribute attInFocus = getAttributeInFocus();
          Vector<RuleInput> inputs = ruleInFocus.getAllRuleInputs();
          if (attInFocus.getType() == AttributeTypes.STRING) { // string 
          																										 // attribute
            boolean disableButt = true;
            for (int i = 0; i < inputs.size(); i++) {
              if (inputs.elementAt(i).getType().equals(InputType.STRING)) { 
              	// string rule input type
                disableButt = false;
                break;
              }
            }
            if (disableButt) {
              inputButton.setEnabled(false);
            }
          } else if (attInFocus.getType() == AttributeTypes.BOOLEAN) { 
          	// boolean attribute
            boolean disableButt = true;
            for (int i = 0; i < inputs.size(); i++) {
              if (inputs.elementAt(i).getType().equals(InputType.BOOLEAN)) { 
              	// boolean rule input type
                disableButt = false;
                break;
              }
            }
            if (disableButt) {
              inputButton.setEnabled(false);
            }
          }
        }
      }
    } else {
      if (lastTokenType == OPEN_PAREN) {
        // disable all operator buttons:
        for (int i = 0; i < operatorButtons.size(); i++) {
          operatorButtons.elementAt(i).setEnabled(false);
        }
        // enable neg. sign button:
        buttonMinus.setEnabled(true);

        // enable open paren button:
        buttonOpenParen.setEnabled(true);

        // disable close paren button:
        buttonCloseParen.setEnabled(false);

        // enable all other buttons:
        inputButton.setEnabled(true);
        attributeThisPartButton.setEnabled(true);
        attributeOtherPartButton.setEnabled(true);
        numObjectsButton.setEnabled(true);
        numActionsThisPartButton.setEnabled(true);
        numActionsOtherPartButton.setEnabled(true);
        for (int i = 0; i < timeButtons.size(); i++) {
          timeButtons.elementAt(i).setEnabled(true);
        }
        for (int i = 0; i < digitButtons.size(); i++) {
          digitButtons.elementAt(i).setEnabled(true);
        }
        randomButton.setEnabled(true);
      } else if ((lastTokenType == CLOSED_PAREN) || (lastTokenType == NUMBER)) {
        // disable all buttons except operators (enable those):
        inputButton.setEnabled(false);
        attributeThisPartButton.setEnabled(false);
        attributeOtherPartButton.setEnabled(false);
        numObjectsButton.setEnabled(false);
        numActionsThisPartButton.setEnabled(false);
        numActionsOtherPartButton.setEnabled(false);
        for (int i = 0; i < timeButtons.size(); i++) {
          timeButtons.elementAt(i).setEnabled(false);
        }
        for (int i = 0; i < digitButtons.size(); i++) {
          digitButtons.elementAt(i).setEnabled(false);
        }
        for (int i = 0; i < operatorButtons.size(); i++) {
          operatorButtons.elementAt(i).setEnabled(true);
        }
        randomButton.setEnabled(false);
        // disable open paren:
        buttonOpenParen.setEnabled(false);
        // disable closing paren if necessary:
        if (numOpenParen() <= numClosingParen()) {
          buttonCloseParen.setEnabled(false);
        }
      } else if (lastTokenType == OPERATOR) {
        // disable all operator buttons:
        for (int i = 0; i < operatorButtons.size(); i++) {
          operatorButtons.elementAt(i).setEnabled(false);
        }
        // enable neg. sign button:
        buttonMinus.setEnabled(true);
        // enable all other buttons:
        for (int i = 0; i < digitButtons.size(); i++) {
          digitButtons.elementAt(i).setEnabled(true);
        }
        randomButton.setEnabled(true);
        for (int i = 0; i < timeButtons.size(); i++) {
          timeButtons.elementAt(i).setEnabled(true);
        }
        inputButton.setEnabled(true);
        attributeThisPartButton.setEnabled(true);
        attributeOtherPartButton.setEnabled(true);
        numObjectsButton.setEnabled(true);
        numActionsThisPartButton.setEnabled(true);
        numActionsOtherPartButton.setEnabled(true);
        // enable open paren button:
        buttonOpenParen.setEnabled(true);
      } else if (lastTokenType == DIGIT) {
        // disable all other number buttons:
        randomButton.setEnabled(false);
        for (int i = 0; i < timeButtons.size(); i++) {
          timeButtons.elementAt(i).setEnabled(false);
        }
        inputButton.setEnabled(false);
        attributeThisPartButton.setEnabled(false);
        attributeOtherPartButton.setEnabled(false);
        numObjectsButton.setEnabled(false);
        numActionsThisPartButton.setEnabled(false);
        numActionsOtherPartButton.setEnabled(false);

        // enable other buttons:
        for (int i = 0; i < operatorButtons.size(); i++) {
          operatorButtons.elementAt(i).setEnabled(true);
        }
        for (int i = 0; i < digitButtons.size(); i++) {
          digitButtons.elementAt(i).setEnabled(true);
        }

        // disable "." button if necessary:
        String textFieldText = echoedTextField.getText();
        if (getLastToken(textFieldText).indexOf('.') != -1) { // dot has already
                                                            	// appeared in 
        																											// this number
          buttonDot.setEnabled(false);
        }
        // disable open paren:
        buttonOpenParen.setEnabled(false);

        // disable closing paren if necessary:
        if (numOpenParen() <= numClosingParen()) {
          buttonCloseParen.setEnabled(false);
        }

        if (getLastToken(echoedTextField.getText()).equals("-")) { // negative
                                                                   // sign
          // disable minus button:
          buttonMinus.setEnabled(false);
          // enable number buttons:
          randomButton.setEnabled(true);
          for (int i = 0; i < timeButtons.size(); i++) {
            timeButtons.elementAt(i).setEnabled(true);
          }
          inputButton.setEnabled(true);
          attributeThisPartButton.setEnabled(true);
          attributeOtherPartButton.setEnabled(true);
          numObjectsButton.setEnabled(true);
          numActionsThisPartButton.setEnabled(true);
          numActionsOtherPartButton.setEnabled(true);
          // disable operator buttons:
          for (int i = 0; i < operatorButtons.size(); i++) {
            operatorButtons.elementAt(i).setEnabled(false);
          }
          // enable open paren:
          buttonOpenParen.setEnabled(true);
        } else if (getLastToken(echoedTextField.getText()).endsWith(".")) { 
        	// dot
          // disable operator buttons:
          for (int i = 0; i < operatorButtons.size(); i++) {
            operatorButtons.elementAt(i).setEnabled(false);
          }
          // disable closing parens:
          buttonCloseParen.setEnabled(false);
        }
      }

      // Rule input button:
      if (inputButton.isEnabled()) { // if button is enabled, check if you need
      															 // to disable it or not:
        Attribute attInFocus = getAttributeInFocus();
        Vector<RuleInput> inputs = ruleInFocus.getAllRuleInputs();
        if ((attInFocus.getType() == AttributeTypes.INTEGER) || 
        		(attInFocus.getType() == AttributeTypes.DOUBLE)) { // integer or
                                                               // double
        																											 // attribute
          // enable or disable input button, depending on the types of inputs
          // that currently exist:
          boolean disableButt = true;
          for (int i = 0; i < inputs.size(); i++) {
            if ((inputs.elementAt(i).getType().equals(InputType.INTEGER)) || 
            		(inputs.elementAt(i).getType().equals(InputType.DOUBLE))) { 
            	// numerical rule input type
              disableButt = false;
              break;
            }
          }
          if (disableButt) {
            inputButton.setEnabled(false);
          }
        }
      }
    }
  }

  // returns the number of open parentheses that are in the echoed text field
  private int numOpenParen() { 
    int num = 0;
    char[] text = echoedTextField.getText().toCharArray();
    for (int i = 0; i < text.length; i++) {
      if (text[i] == '(') {
        num++;
      }
    }
    return num;
  }

  // returns the number of closing parentheses that are in the echoed text field
  private int numClosingParen() { 
    int num = 0;
    char[] text = echoedTextField.getText().toCharArray();
    for (int i = 0; i < text.length; i++) {
      if (text[i] == ')') {
        num++;
      }
    }
    return num;
  }

  // disables all buttons in the button pad GUI except backspace
  private void disableAllButtons() { 
    inputButton.setEnabled(false);
    attributeThisPartButton.setEnabled(false);
    attributeOtherPartButton.setEnabled(false);
    numObjectsButton.setEnabled(false);
    numActionsThisPartButton.setEnabled(false);
    numActionsOtherPartButton.setEnabled(false);
    for (int i = 0; i < timeButtons.size(); i++) {
      timeButtons.elementAt(i).setEnabled(false);
    }
    for (int i = 0; i < digitButtons.size(); i++) {
      digitButtons.elementAt(i).setEnabled(false);
    }
    for (int i = 0; i < operatorButtons.size(); i++) {
      operatorButtons.elementAt(i).setEnabled(false);
    }
    randomButton.setEnabled(false);
    stringButton.setEnabled(false);
    booleanButton.setEnabled(false);
  }

  /*
   * returns the Attribute that is currently in focus, according to the most
   * recently-focused text field
   */
  private Attribute getAttributeInFocus() { 
    // figure out the index of the text field most recently in focus:
    for (int i = 0; i < textFields.size(); i++) {
      if (textFields.elementAt(i) == lastFocusedTextField) {
        // get the attribute in focus:
        return (objectTypeInFocus.getAllAttributes().elementAt(i));
      }
    }
    return null;
  }

  /*
   * takes care of what happens when the user clicks the input button on the
   * button pad
   */
  private void inputButtonChosen() { 
    Vector<RuleInput> inputs = ruleInFocus.getAllRuleInputs();

    // get attInFocus:
    Attribute attInFocus = getAttributeInFocus();

    // make a vector of rule input names for choosing from:
    Vector<String> choices = new Vector<String>();
    if (attInFocus.getType() == AttributeTypes.STRING) { // string attribute
      for (int i = 0; i < inputs.size(); i++) {
        RuleInput tempInput = inputs.elementAt(i);
        if (tempInput.getType().equals(InputType.STRING)) {
          choices.add(inputs.elementAt(i).getName());
        }
      }
    } else if (attInFocus.getType() == AttributeTypes.BOOLEAN) { // boolean
                                                               	 // attribute
      for (int i = 0; i < inputs.size(); i++) {
        RuleInput tempInput = inputs.elementAt(i);
        if (tempInput.getType().equals(InputType.BOOLEAN)) {
          choices.add(inputs.elementAt(i).getName());
        }
      }
    } else if ((attInFocus.getType() == AttributeTypes.INTEGER) || 
    		(attInFocus.getType() == AttributeTypes.DOUBLE)) { // numerical 
    																											 // attribute
      for (int i = 0; i < inputs.size(); i++) {
        RuleInput tempInput = inputs.elementAt(i);
        if ((tempInput.getType().equals(InputType.INTEGER))
            || (tempInput.getType().equals(InputType.DOUBLE))) {
          choices.add(inputs.elementAt(i).getName());
        }
      }
    }
    Object choiceInFocus;
    if (choices.size() == 0) { // no inputs
      choiceInFocus = null;
    } else {
      choiceInFocus = choices.elementAt(0);
    }
    // bring up dialog for choosing a rule input:
    String response = (String) JOptionPane.showInputDialog(this,
        "Choose a rule input:", "Rule Input", JOptionPane.PLAIN_MESSAGE, null,
        choices.toArray(), choiceInFocus);
    if ((response != null) && (response.length() > 0)) { // a selection was made
      // append the text for the selected input to the text field:
      if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
      		(getLastToken(echoedTextField.getText()).equals("-"))) { // negative 
      																														 // sign was 
      																														 // last token
        setEchoedTextFieldText(echoedTextField.getText().trim().concat(
            "input-" + response + " "));
      } else {
        setEchoedTextFieldText(echoedTextField.getText().concat(
            "input-" + response + " "));
      }
      if ((attInFocus.getType() == AttributeTypes.INTEGER)
          || (attInFocus.getType() == AttributeTypes.DOUBLE)) {
        refreshButtonPad(NUMBER);
      } else { // string/boolean attribute
        disableAllButtons();
      }
    }
  }

  // takes care of what to do when this button is clicked
  private void attributeThisPartButtonChosen() { 
    // attributes of the object type in focus:
    Vector<Attribute> atts = objectTypeInFocus.getAllAttributes();
    Attribute attInFocus = getAttributeInFocus();

    // make a vector of attribute names for choosing from:
    Vector<String> choices = new Vector<String>();
    if (attInFocus.getType() == AttributeTypes.STRING) { // string attribute
      for (int i = 0; i < atts.size(); i++) {
        Attribute tempAtt = atts.elementAt(i);
        if (tempAtt.getType() == AttributeTypes.STRING) {
          choices.add(atts.elementAt(i).getName());
        }
      }
    }
    if (attInFocus.getType() == AttributeTypes.BOOLEAN) { // boolean attribute
      for (int i = 0; i < atts.size(); i++) {
        Attribute tempAtt = atts.elementAt(i);
        if (tempAtt.getType() == AttributeTypes.BOOLEAN) {
          choices.add(atts.elementAt(i).getName());
        }
      }
    } else if ((attInFocus.getType() == AttributeTypes.INTEGER) || 
    		(attInFocus.getType() == AttributeTypes.DOUBLE)) { // numerical 
    																											 // attribute
      for (int i = 0; i < atts.size(); i++) {
        Attribute tempAtt = atts.elementAt(i);
        if ((tempAtt.getType() == AttributeTypes.INTEGER)
            || (tempAtt.getType() == AttributeTypes.DOUBLE)) {
          choices.add(atts.elementAt(i).getName());
        }
      }
    }
    Object choiceInFocus;
    if (choices.size() == 0) { // no inputs
      choiceInFocus = null;
    } else {
      choiceInFocus = choices.elementAt(0);
    }
    // bring up dialog for choosing a rule input:
    String response = (String) JOptionPane.showInputDialog(this,
        "Choose an attribute:", "Attribute", JOptionPane.PLAIN_MESSAGE, null,
        choices.toArray(), choiceInFocus);
    if ((response != null) && (response.length() > 0)) { // a selection was made
      // append the text for the selected input to the text field:
      if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
      		(getLastToken(echoedTextField.getText()).equals("-"))) { // negative 
      																														 // sign was 
      																														 // last token
        setEchoedTextFieldText(echoedTextField.getText().trim().concat(
            "this" + objectTypeInFocus.getName() + "-"
                + SimSEObjectTypeTypes.getText(objectTypeInFocus.getType())
                + ":" + response + " "));
      } else {
        setEchoedTextFieldText(echoedTextField.getText().concat(
            "this" + objectTypeInFocus.getName() + "-"
                + SimSEObjectTypeTypes.getText(objectTypeInFocus.getType())
                + ":" + response + " "));
      }
      if ((attInFocus.getType() == AttributeTypes.INTEGER)
          || (attInFocus.getType() == AttributeTypes.DOUBLE)) {
        refreshButtonPad(NUMBER);
      } else { // string/boolean attribute
        disableAllButtons();
      }
    }
  }

  // takes care of what to do when this button is clicked
  private void attributeOtherPartButtonChosen() { 
    Attribute attInFocus = getAttributeInFocus();
    // temporarily hold the text from the text field:
    String oldText = echoedTextField.getText(); 
    boolean trimWhitespace = false;
    if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
    		(getLastToken(echoedTextField.getText()).equals("-"))) { // negative 
    																														 // sign was 
    																														 // last token
      trimWhitespace = true;
    }
    new OtherParticipantAttributeDialog(this, 
    		actionInFocus.getAllParticipants(), echoedTextField, 
    		attInFocus.getType(), trimWhitespace);
    if ((attInFocus.getType() == AttributeTypes.INTEGER)
        || (attInFocus.getType() == AttributeTypes.DOUBLE)) {
      if (echoedTextField.getText().equals(oldText) == false) { // text has
                                                                // changed
        refreshButtonPad(NUMBER);
      }
    } else { // string/boolean attribute
      if (echoedTextField.getText().equals(oldText) == false) { // text has
                                                                // changed
        disableAllButtons();
      }
    }
  }

  // takes care of what to do when this button is clicked
  private void numObjectsButtonChosen() { 
  	// temporarily hold the text from the text field:
    String oldText = echoedTextField.getText(); 
    boolean trimWhitespace = false;
    if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
    		(getLastToken(echoedTextField.getText()).equals("-"))) { // negative 
    																														 // sign was 
    																														 // last token
      trimWhitespace = true;
    }
    new NumParticipantsDialog(this, actionInFocus.getAllParticipants(), 
    		echoedTextField, trimWhitespace);
    if (echoedTextField.getText().equals(oldText) == false) { // text has
                                                              // changed
      refreshButtonPad(NUMBER);
    }
  }

  // takes care of what to do when this button is clicked
  private void numActionsThisPartButtonChosen() { 
  	// temporarily hold the text from the text field:
    String oldText = echoedTextField.getText(); 
    boolean trimWhitespace = false;
    if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
    		(getLastToken(echoedTextField.getText()).equals("-"))) { // negative 
    																														 // sign was 
    																														 // last token
      trimWhitespace = true;
    }
    new NumActionsThisPartDialog(this, participantInFocus, objectTypeInFocus, 
    		actions, echoedTextField, trimWhitespace);
    if (echoedTextField.getText().equals(oldText) == false) { // text has
                                                              // changed
      refreshButtonPad(NUMBER);
    }
  }

  // takes care of what to do when this button is clicked
  private void numActionsOtherPartButtonChosen() { 
  	// temporarily hold the text from the text field:
    String oldText = echoedTextField.getText(); 
    boolean trimWhitespace = false;
    if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
    		(getLastToken(echoedTextField.getText()).equals("-"))) {  // negative 
    																															// sign was 
    																															// last token
      trimWhitespace = true;
    }
    new NumActionsOtherPartDialog(this, actionInFocus.getAllParticipants(), 
    		actions, echoedTextField, trimWhitespace);
    if (echoedTextField.getText().equals(oldText) == false) { // text has
                                                              // changed
      refreshButtonPad(NUMBER);
    }
  }

  // takes care of what to do when this button is clicked
  private void actTimeElapsedButtonChosen() { 
    String text = "actionTimeElapsed";
    // append the text for the selected input to the text field:
    if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
    		(getLastToken(echoedTextField.getText()).equals("-"))) { // negative 
    																														 // sign was 
    																														 // last token
      setEchoedTextFieldText(echoedTextField.getText().trim().concat(
          text + " "));
    } else {
      setEchoedTextFieldText(echoedTextField.getText().concat(text + " "));
    }
    refreshButtonPad(NUMBER);
  }

  // takes care of what to do when this button is clicked
  private void randomButtonChosen() { 
  	// temporarily hold the text from the text field:
    String oldText = echoedTextField.getText(); // temporarily hold the
                                                     // text from the text field
    boolean trimWhitespace = false;
    if ((getLastTokenType(echoedTextField.getText()) == DIGIT) && 
    		(getLastToken(echoedTextField.getText()).equals("-"))) { // negative 
    																														 // sign was 
    																														 // last token
      trimWhitespace = true;
    }
    new RandomFunctionDialog(this, echoedTextField, trimWhitespace);
    if (echoedTextField.getText().equals(oldText) == false) { // text has
                                                              // changed
      refreshButtonPad(NUMBER);
    }
  }

  // takes care of what to do when this button is clicked
  private void digitButtonChosen(JButton digitButt) { 
    //System.out.println("*********** At the beginning of digitButtonChosen for
    // butt w/ this text: *" + digitButt.getText() + "*");
    if (getLastToken(echoedTextField.getText()) == null) { // empty text field
      //System.out.println("Last token = null");
      //System.out.println("About to append the text *" + digitButt.getText() +
      // "* plus a space");
      // append the text:
      setEchoedTextFieldText(echoedTextField.getText().concat(
          digitButt.getText() + " "));
    } else { // non-empty text field
      //System.out.println("Last token = *" +
      // getLastToken(lastFocusedTextField.getText()) + "*");
      int tokenType = getLastTokenType(echoedTextField.getText());
      //System.out.println("Last token type = " + tokenType);
      if ((tokenType == OPEN_PAREN) || (tokenType == OPERATOR)) {
        //System.out.println("Last token type is open paren or operator --
        // about to append *" + digitButt.getText() + "* plus a space");
        // append the text:
        setEchoedTextFieldText(echoedTextField.getText().concat(
            digitButt.getText() + " "));
      } else if (tokenType == DIGIT) {
        //System.out.println("Last token type is digit -- about to remove
        // trailing whitespace and append *" + digitButt.getText()
        //+ "* plus a space");
        // append the text, removing trailing white space from what was
        // previously in the text field:
        setEchoedTextFieldText(echoedTextField.getText().trim().concat(
            digitButt.getText() + " "));
      }
    }
    //System.out.println("About to refresh button pad for digit just chosen");
    refreshButtonPad(DIGIT);
    //System.out.println("************* End of digitButtonChosen for butt w/
    // this text: *" + digitButt.getText() + "*");
  }

  // takes care of what to do when this button is clicked
  private void operatorButtonChosen(JButton opButt) { 
    if (opButt.getText().equals("-")) {
      //System.out.println("************* At the beginning of
      // operatorButtonChosen function, and - button chosen");
      String lastToken = getLastToken(echoedTextField.getText());
      //System.out.println("Last token = " + lastToken);
      //System.out.println("Last token type = " +
      // getLastTokenType(lastFocusedTextField.getText()));
      if ((lastToken == null) || (lastToken.length() == 0) || 
      		(getLastTokenType(echoedTextField.getText()) == OPERATOR) || 
      		(getLastTokenType(echoedTextField.getText()) == OPEN_PAREN)) { 
      	// "-" counts for a negative sign in this case
        //System.out.println("About to call digitButtonChosen -- minus button
        // counts for a digit");
        digitButtonChosen(opButt);
      } else { // "-" counts for a minus operator
        //System.out.println("About to append minus sign plus a space -- minus
        // button counts for a operator");
        // append text:
        setEchoedTextFieldText(echoedTextField.getText().concat(
            opButt.getText() + " "));
        //System.out.println("About to refresh button pad for operator just
        // chosen");
        refreshButtonPad(OPERATOR);
      }
      //System.out.println("************* At the end of operatorButtonChosen
      // function, and - button chosen");
    } else { // another operator
      //System.out.println("A non minus sign operator chosen, about to append
      // text: *" + opButt.getText() + "* plus a space");
      // append text:
      setEchoedTextFieldText(echoedTextField.getText().concat(
          opButt.getText() + " "));
      //System.out.println("About to refresh button pad for operator just
      // chosen.");
      refreshButtonPad(OPERATOR);
      //System.out.println("************* At the end of operatorButtonChosen
      // function, for button *" + opButt.getText() + "*");
    }
  }

  // takes care of what to do when this button is clicked
  private void stringButtonChosen() { 
    String response = JOptionPane.showInputDialog(null, "Enter the string:",
        "String", JOptionPane.QUESTION_MESSAGE); // Show input dialog
    if (response != null) {
      char[] cArray = response.toCharArray();

      // Check for length constraints:
      if ((cArray.length == 0) || (cArray.length > 500)) { // user has entered
                                                         	 // nothing or a 
      																										 // string longer than
      																										 // 500 chars
        JOptionPane.showMessageDialog(null,
            "Please enter a string between 1 and 500 characters",
            "Invalid Input", JOptionPane.WARNING_MESSAGE); // warn user to enter
                                                           // valid input
        stringButtonChosen(); // try again
      } else { // valid input
        setEchoedTextFieldText("\"" + response + "\"");
        disableAllButtons();
      }
    }
  }

  // takes care of what to do when this button is clicked
  private void booleanButtonChosen() { 
    Object[] choices = { "true", "false" };
    // bring up dialog for choosing a boolean value:
    String response = (String) JOptionPane.showInputDialog(this,
        "Choose a Boolean value:", "Boolean", JOptionPane.PLAIN_MESSAGE, null,
        choices, choices[0]);
    if ((response != null) && (response.length() > 0)) { // a selection was made
      // set the text field:
      setEchoedTextFieldText(response);
      disableAllButtons();
    }
  }

  // takes care of what to do when this button is clicked
  private void backspaceButtonChosen() { 
    if ((getAttributeInFocus().getType() == AttributeTypes.STRING) || 
    		(getAttributeInFocus().getType() == AttributeTypes.BOOLEAN)) { 
    	// string or boolean attribute
      setEchoedTextFieldText(null); // clear text
      refreshButtonPad();
    } else { // numerical attribute
      String lastToken = getLastToken(echoedTextField.getText());
      if ((lastToken != null) && (lastToken.length() > 0)) { // there was text 
      																											 // in the field
        if (getLastTokenType(echoedTextField.getText()) == DIGIT) { // digit
                                                                    // token
          // remove the last character:
          setEchoedTextFieldText(echoedTextField.getText().trim()
              .substring(0,
                  (echoedTextField.getText().trim().length() - 1)));
        } else { // non-digit token
          // remove the last token:
          setEchoedTextFieldText(echoedTextField.getText().trim()
              .substring(
                  0,
                  (echoedTextField.getText().trim().length() - lastToken
                      .length())));
        }
        if ((getLastTokenType(echoedTextField.getText()) != -1) && 
        		(getLastTokenType(echoedTextField.getText()) != OPEN_PAREN)) { 
        	// last token was not null, not digit, and not open paren
          // add a space to the text field:
          setEchoedTextFieldText(echoedTextField.getText().trim() + " ");
        }
        if ((echoedTextField.getText() == null) || 
        		(echoedTextField.getText().length() == 0)) { // all of the text has 
        																								 // been removed
          refreshButtonPad(); // refresh button pad based on attribute type only
        } else {
          // refresh button pad based on last token type:
          refreshButtonPad(getLastTokenType(echoedTextField.getText()));
        }
      }
    }
  }

  // returns the type of the last token in the token string
  private int getLastTokenType(String tokenString) { 
    //System.out.println("************* At the beginning of getLastTokenType
    // with *" + tokenString + "* as thte argument");
    String token = getLastToken(tokenString);
    //System.out.println("Last token = *" + token + "*");
    if ((token == null) || (token.length() == 0)) { // no last token
      //System.out.println("No last token!");
      //System.out.println("*********At the end of getLastTokenType for
      // tokenstring = *" + tokenString + "*, and about to return -1");
      return -1;
    }
    if ((token.startsWith("input-")) || (token.startsWith("this"))
        || (token.startsWith("all")) || (token.startsWith("num"))
        || (token.startsWith("total")) || (token.startsWith("action"))
        || (token.startsWith("random")) || (token.startsWith("-input-"))
        || (token.startsWith("-this")) || (token.startsWith("-all"))
        || (token.startsWith("-num")) || (token.startsWith("-total"))
        || (token.startsWith("-action")) || (token.startsWith("-random"))) {
      //System.out.println("*********At the end of getLastTokenType for
      // tokenstring = *" + tokenString + "*, and about to return NUMBER");
      return NUMBER;
    } else if (token.equals("+") || token.equals("*") || token.equals("/")) {
      //System.out.println("*********At the end of getLastTokenType for
      // tokenstring = *" + tokenString + "*, and about to return OPERATOR");
      return OPERATOR;
    } else if (token.equals("-")) {
      //System.out.println("Token equals - sign");
      // get the token before this one:
      String lastLastToken = tokenString.substring(0,
          (tokenString.trim().length() - token.length())).trim();
      //System.out.println("Last last token = *" + lastLastToken + "*");
      int lastLastTokenType = getLastTokenType(lastLastToken);
      //System.out.println("Last last token type = " + lastLastTokenType);
      if ((lastLastTokenType == OPERATOR) || 
      		(lastLastTokenType == OPEN_PAREN)|| (lastLastTokenType == -1)) { 
      	// "-" counts for a negative sign in this case
        //System.out.println("*********At the end of getLastTokenType for
        // tokenstring = *" + tokenString + "*, and about to return DIGIT");
        return DIGIT;
      } else { //((lastLastTokenType == NUMBER) || (lastLastTokenType == DIGIT))
             	 // "-" counts for a minus sign in this case
        //System.out.println("*********At the end of getLastTokenType for
        // tokenstring = *" + tokenString + "*, and about to return OPERATOR");
        return OPERATOR;
      }
    } else if (token.equals("(")) {
      //System.out.println("*********At the end of getLastTokenType for
      // tokenstring = *" + tokenString + "*, and about to return OPEN_PAREN");
      return OPEN_PAREN;
    } else if (token.equals(")")) {
      //System.out.println("*********At the end of getLastTokenType for
      // tokenstring = *" + tokenString + "*, and about to return
      // CLOSED_PAREN");
      return CLOSED_PAREN;
    } else {
      //System.out.println("*********At the end of getLastTokenType for
      // tokenstring = *" + tokenString + "*, and about to return DIGIT");
      return DIGIT;
    }
  }

  // returns the last token in the token string
  private String getLastToken(String tokenString) { 
    //System.out.println("********** At the beginning of getLastToken for
    // tokenString = *" + tokenString + "*");
    //System.out.println("(tokenString.trim().indexOf(' ') = " +
    // tokenString.trim().indexOf(' '));
    //System.out.println("(tokenString.trim().indexOf('(') = " +
    // tokenString.trim().indexOf('('));
    //System.out.println("(tokenString.trim().indexOf(')') = " +
    // tokenString.trim().indexOf(')'));
    if ((tokenString.equals(null)) || (tokenString.length() == 0)) { // empty 
    																																 // text
                                                                   	 // field
      //System.out.println("Empty tokenString!");
      return null;
    } else if ((tokenString.trim().indexOf(' ') < 0) && 
    		(tokenString.indexOf('(') < 0) && (tokenString.indexOf(')') < 0)) { 
    	// no spaces & no parentheses -- must be only one token
      //System.out.println("Only one token!");
      //System.out.println("********** At the end of getLastToken for
      // tokenString = *" + tokenString +
      //"* and About to return tokenString.trim(), which = *" +
      // tokenString.trim() + "*");
      if ((tokenString.startsWith("-")) && 
      		(tokenString.trim().equals("-") == false)) { // begins w/ a neg. sign
                                                       // but is not just a neg.
      																								 // sign
        return tokenString.trim().substring(1); // return token w/out neg. sign
      } else {
        return tokenString.trim();
      }
    } else if (tokenString.equals("(")) {
      return "(";
    } else { // multiple tokens
      //System.out.println("Multiple tokens!");
      String lastBlock = tokenString.trim().substring(
          tokenString.trim().lastIndexOf(' ') + 1);
      //System.out.println("lastBlock = *" + lastBlock + "*");
      if (lastBlock.endsWith(")")) {
        //System.out.println("********** At the end of getLastToken for
        // tokenString = *" + tokenString +
        //"* and About to return )");
        return ")";
      } else if (lastBlock.endsWith("(")) {
        return "(";
      } else if (lastBlock.startsWith("(")) {
        //System.out.println("********** At the end of getLastToken for
        // tokenString = *" + tokenString +
        //"* and About to return lastBlock.substring(1), which = *" +
        // lastBlock.substring(1) + "*");
        return lastBlock.substring(1); // return what comes after the
                                       // parenthesis
      } else if (lastBlock.indexOf("(") > 0) { // open paren somewhere in the
                                             	 // middle
        return lastBlock.substring(lastBlock.lastIndexOf("(") + 1);
      } else if ((lastBlock.startsWith("-")) && 
      		(lastBlock.trim().equals("-") == false)) { // begins w/ a neg. sign
                                                     // but is not just a neg. 
      																							 // sign
        return lastBlock.substring(1); // return token w/out neg. sign
      } else { // no parentheses
        //System.out.println("************No parentheses, at the end of
        // getLastToken for tokenString = *" + tokenString +
        //" and about to return lastBlock, which = *" + lastBlock + "*");
        return lastBlock;
      }
    }
  }

  private void setEchoedTextFieldText(String text) {
    echoedTextField.setText(text);
  }
}