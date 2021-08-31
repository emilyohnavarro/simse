/*
 * This class defines the intial GUI for viewing/editing destroy objects rules
 * with
 */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DestroyObjectsRuleInfoForm extends JDialog implements
    ActionListener {
  private DestroyObjectsRule ruleInFocus; // copy of rule being edited
  private DestroyObjectsRule actualRule; // actual rule being edited
  private ActionType actionInFocus; // action to whom this rule belongs
  private boolean newRule; // whether this is a newly created rule (true) or
                           // editing an existing one (false)

  private Vector<String> participantNames; // for the JList

  private JList participantList; // for choosing a participant whose trigger
                                 // conditions to edit
  private JButton viewEditButton; // for viewing/editing trigger conditions
  private ButtonGroup timingButtonGroup; // for radio buttons for choosing the
                                         // timing of the rule
  private JRadioButton continuousButton; // for denoting a continuous rule
  private JRadioButton triggerButton; // for denoting a trigger rule
  private JRadioButton destroyerButton; // for denoting a destroyer rule

  private JCheckBox visibilityCheckBox; // for denoting whether this rule is
                                        // visible in the explanatory tool
  private JButton annotationButton; // for viewing/editing the rule's annotation
  private JButton okButton; // for ok'ing the changes made in this form
  private JButton cancelButton; // for cancelling the changes made in this form

  public DestroyObjectsRuleInfoForm(JFrame owner, DestroyObjectsRule rule,
      ActionType act, DefinedActionTypes acts, boolean newOrEdit) {
    super(owner, true);
    newRule = newOrEdit;
    actualRule = rule;
    actionInFocus = act;
    if (!newRule) { // editing existing rule
    	// make a copy of the rule for editing:
      ruleInFocus = (DestroyObjectsRule) (actualRule.clone()); 
    } else { // new rule
      ruleInFocus = rule;
    }

    // Set window title:
    setTitle(ruleInFocus.getName() + " Rule Information - SimSE");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();

    // Create choosePartPane and label:
    JPanel choosePartPane = new JPanel();
    choosePartPane.add(new JLabel("Choose a participant:"));

    // Create partList pane and list:
    participantList = new JList();
    participantList.setVisibleRowCount(10); // make 10 items visible at a time
    participantList.setFixedCellWidth(200);
    participantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    JScrollPane partListPane = new JScrollPane(participantList);
    // initialize participant names:
    participantNames = new Vector<String>();
    Vector<ActionTypeParticipant> parts = actionInFocus.getAllParticipants();
    for (int i = 0; i < parts.size(); i++) {
      participantNames.add(parts.elementAt(i).getName());
    }
    participantList.setListData(participantNames);
    setUpParticipantListActionListenerStuff();

    // Create viewEdit pane and button:
    JPanel viewEditPane = new JPanel();
    viewEditButton = new JButton("View/Edit Participant Conditions");
    viewEditButton.addActionListener(this);
    viewEditButton.setEnabled(false);
    viewEditPane.add(viewEditButton);

    // Create timing pane:
    JPanel timingPane = new JPanel();
    timingPane.add(new JLabel("Timing of Rule:"));
    timingButtonGroup = new ButtonGroup();
    continuousButton = new JRadioButton("Continuous Rule");
    continuousButton.setSelected(true);
    timingPane.add(continuousButton);
    triggerButton = new JRadioButton("Trigger Rule");
    timingPane.add(triggerButton);
    destroyerButton = new JRadioButton("Destroyer Rule");
    timingPane.add(destroyerButton);
    timingButtonGroup.add(continuousButton);
    timingButtonGroup.add(triggerButton);
    timingButtonGroup.add(destroyerButton);
    refreshTimingButtons();

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

    // Create okCancel pane and buttons:
    JPanel okCancelPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    okCancelPane.add(okButton);
    okCancelPane.add(cancelButton);

    // Add panes and separators to main pane:
    mainPane.add(choosePartPane);
    mainPane.add(partListPane);
    mainPane.add(viewEditPane);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(450, 1));
    mainPane.add(separator1);
    mainPane.add(timingPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(450, 1));
    mainPane.add(separator2);
    mainPane.add(annotationPane);
    JSeparator separator3 = new JSeparator();
    separator3.setMaximumSize(new Dimension(450, 1));
    mainPane.add(separator3);
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
  public void actionPerformed(ActionEvent evt) { // handles user actions
    Object source = evt.getSource(); // get which component the action came from
    if (source == viewEditButton) {
      if (participantList.isSelectionEmpty() == false) { // a participant is
                                                       	 // selected
        // Bring up form for entering info for the new participant condition:
        new DestroyObjectsRuleParticipantConditionInfoForm(this, 
        		ruleInFocus.getParticipantCondition((String) (participantList
                .getSelectedValue())));
      }
    } else if (source == annotationButton) { // annotation button selected
      new RuleAnnotationForm(this, ruleInFocus);
    } else if (source == okButton) {
      // set timing:
      if (continuousButton.isSelected()) {
        ruleInFocus.setTiming(RuleTiming.CONTINUOUS);
      } else if (triggerButton.isSelected()) {
        ruleInFocus.setTiming(RuleTiming.TRIGGER);
      } else if (destroyerButton.isSelected()) {
        ruleInFocus.setTiming(RuleTiming.DESTROYER);
      }

      // set visibility:
      ruleInFocus.setVisibilityInExplanatoryTool(visibilityCheckBox
          .isSelected());

      if (!newRule) { // existing rule being edited
        int indexOfRule = actionInFocus.getAllRules().indexOf(actualRule);
        // remove old version of rule:
        actionInFocus.removeRule(actualRule.getName()); 
        if (indexOfRule != -1) { // rule already exists
        	// add edited rule at its old location:
          actionInFocus.addRule(ruleInFocus, indexOfRule); 
        }
      } else { // newly created rule
        actionInFocus.addRule(ruleInFocus); // add new rule
      }

      // close window:
      setVisible(false);
      dispose();
    } else if (source == cancelButton) {
      setVisible(false);
      dispose();
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

  private void refreshTimingButtons() {
    int timing = ruleInFocus.getTiming();
    if (timing == RuleTiming.CONTINUOUS) {
      continuousButton.setSelected(true);
      triggerButton.setSelected(false);
      destroyerButton.setSelected(false);
    } else if (timing == RuleTiming.TRIGGER) {
      triggerButton.setSelected(true);
      continuousButton.setSelected(false);
      destroyerButton.setSelected(false);
    } else if (timing == RuleTiming.DESTROYER) {
      destroyerButton.setSelected(true);
      continuousButton.setSelected(false);
      triggerButton.setSelected(false);
    }
  }

  private void refreshVisibilityCheckBox() {
    visibilityCheckBox.setSelected(ruleInFocus.isVisibleInExplanatoryTool());
  }

  public class ExitListener extends WindowAdapter {
    public void windowClosing(WindowEvent event) {
      setVisible(false);
      dispose();
    }
  }
}