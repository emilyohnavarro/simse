/*
 * This class defines the window through which continuous rules can be put in
 * order for priority of execution
 */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ContinuousRulePrioritizer extends JDialog implements
    ActionListener {
  private DefinedActionTypes actions; // existing defined action types
  private Vector<String> nonPrioritizedRules; // vector of rules that haven't 
  																						// been prioritized yet
  private Vector<String> prioritizedRules; // vector of rules that have been 
  																				 // prioritized
  private JList nonPrioritizedRuleList; // JList for non-prioritized rules
  private JList prioritizedRuleList; // JList for prioritized rules
  private JButton rightArrowButton; // for moving rules to the right
  private JButton leftArrowButton; // for moving rules to the left
  private JButton moveUpButton; // for moving rules up
  private JButton moveDownButton; // for moving rules down
  private JButton okButton; // for ok'ing the changes made in this form
  private JButton cancelButton; // for cancelling the changes made in this form

  public ContinuousRulePrioritizer(JFrame owner, DefinedActionTypes actions) {
    super(owner, true);
    this.actions = actions;

    // Set window title:
    setTitle("Continuous Rule Prioritizer -- SimSE");

    // initialize lists:
    nonPrioritizedRules = new Vector<String>();
    prioritizedRules = new Vector<String>();
    Vector<ActionType> allActions = actions.getAllActionTypes();
    // go through all action types and get their rules:
    for (int i = 0; i < allActions.size(); i++) {
      ActionType tempAct = allActions.elementAt(i);
      Vector<Rule> rules = tempAct.getAllContinuousRules();
      // go through each rule and add it to the correct list:
      for (int j = 0; j < rules.size(); j++) {
        Rule tempRule = rules.elementAt(j);
        String ruleInfo = (tempRule.getName() + " (" + tempAct.getName() + ")");
        int priority = tempRule.getPriority();
        if (priority == -1) { // rule is not prioritized yet
          nonPrioritizedRules.addElement(ruleInfo);
        } else { // priority >= 0
          if (prioritizedRules.size() == 0) { // no elements have been added yet
                                            	// to the prioritized rule list
            prioritizedRules.add(ruleInfo);
          } else {
            // find the correct position to insert the rule at:
            for (int k = 0; k < prioritizedRules.size(); k++) {
              String tempRuleInfo = prioritizedRules.elementAt(k);
              ActionType tempActType = actions.getActionType(tempRuleInfo
                  .substring((tempRuleInfo.indexOf('(') + 1), (tempRuleInfo
                      .length() - 1)));
              Rule tempR = tempActType.getRule(tempRuleInfo.substring(0,
                  (tempRuleInfo.indexOf('(') - 1)));
              if (priority <= tempR.getPriority()) {
                prioritizedRules.insertElementAt(ruleInfo, k); // insert the
                                                               // rule info
                break;
              } else if (k == (prioritizedRules.size() - 1)) { // on the last
                                                             	 // element
                prioritizedRules.add(ruleInfo); // add the rule info to the end
                                                // of the list
                break;
              }
            }
          }
        }
      }
    }

    nonPrioritizedRuleList = new JList(nonPrioritizedRules);
    nonPrioritizedRuleList.setVisibleRowCount(10); 
    nonPrioritizedRuleList
        .setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    prioritizedRuleList = new JList(prioritizedRules);
    prioritizedRuleList.setVisibleRowCount(10); 
    prioritizedRuleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    setupListSelectionListeners();

    // Create main panel:
    Box mainPane = Box.createVerticalBox();

    // Create top panel:
    JPanel topPane = new JPanel();

    // Create topPaneA and components:
    Box topPaneA = Box.createVerticalBox();
    topPaneA.add(new JLabel("Non-Prioritized Rules:"));
    JScrollPane nonPriListPane = new JScrollPane(nonPrioritizedRuleList);
    nonPriListPane
        .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    topPaneA.add(nonPriListPane);
    topPane.add(topPaneA);

    // Create topPaneB and components:
    Box topPaneB = Box.createVerticalBox();
    rightArrowButton = new JButton("->");
    rightArrowButton.addActionListener(this);
    rightArrowButton.setEnabled(false);
    topPaneB.add(rightArrowButton);
    leftArrowButton = new JButton("<-");
    leftArrowButton.addActionListener(this);
    leftArrowButton.setEnabled(false);
    topPaneB.add(leftArrowButton);
    topPane.add(topPaneB);

    // Create topPaneC and components:
    Box topPaneC = Box.createVerticalBox();
    topPaneC.add(new JLabel("Prioritized Rules:"));
    JScrollPane priListPane = new JScrollPane(prioritizedRuleList);
    priListPane
        .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    topPaneC.add(priListPane);
    topPane.add(topPaneC);

    // Create topPaneD and components:
    Box topPaneD = Box.createVerticalBox();
    moveUpButton = new JButton("Move Up      ");
    moveUpButton.addActionListener(this);
    moveUpButton.setEnabled(false);
    topPaneD.add(moveUpButton);
    moveDownButton = new JButton("Move Down");
    moveDownButton.addActionListener(this);
    moveDownButton.setEnabled(false);
    topPaneD.add(moveDownButton);
    topPane.add(topPaneD);

    // Create bottom pane and components:
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
    Object source = evt.getSource(); // get which component the action came from
    if (source == cancelButton) { // cancel button has been pressed
      // Close window:
      setVisible(false);
      dispose();
    } else if (source == rightArrowButton) {
      int selIndex = nonPrioritizedRuleList.getSelectedIndex();
      String tempStr = nonPrioritizedRules.elementAt(selIndex);
      nonPrioritizedRules.remove(selIndex); // remove rule from lefthand list
      prioritizedRules.add(tempStr); // add to righthand list
      String tempSelectedVal = ((String) (prioritizedRuleList
          .getSelectedValue()));
      // reset list data:
      prioritizedRuleList.setListData(prioritizedRules);
      // reset selected value:
      if ((tempSelectedVal != null) && (tempSelectedVal.length() > 0)) { 
      	// selection before resetting list data was non-empty
        prioritizedRuleList.setSelectedValue(tempSelectedVal, true);
      }
      nonPrioritizedRuleList.repaint();
      if ((nonPrioritizedRules.size() == 0)
          || (nonPrioritizedRuleList.getSelectedIndex() > (nonPrioritizedRules
              .size() - 1))) { // no item is selected (had to use 
      												 // nonPrioritizedRules.size() - 1 because it
      												 // doesn't work using selected index = -1 or
      												 // selected value == null!
        // disable button:
        rightArrowButton.setEnabled(false);
      }
    } else if (source == leftArrowButton) {
      int selIndex = prioritizedRuleList.getSelectedIndex();
      String tempStr = prioritizedRules.elementAt(selIndex); 
      // remove rule from righthand list:
      prioritizedRules.remove(selIndex); 
      // add to lefthand list:
      nonPrioritizedRules.add(tempStr); 
      String tempSelectedVal = ((String) (nonPrioritizedRuleList
          .getSelectedValue()));
      // reset list data:
      nonPrioritizedRuleList.setListData(nonPrioritizedRules);
      // reset selected value:
      if ((tempSelectedVal != null) && (tempSelectedVal.length() > 0)) { 
      	// selection before resetting list data was non-empty
        nonPrioritizedRuleList.setSelectedValue(tempSelectedVal, true);
      }
      prioritizedRuleList.repaint();
      if ((prioritizedRules.size() == 0)
          || (prioritizedRuleList.getSelectedIndex() > 
          (prioritizedRules.size() - 1))) { // no item is selected (had to use 
      																			// prioritizedRules.size() - 1 
      																			// because it doesn't work using 
      																			// selected index = -1 or selected 
      																			// value == null!
        // disable butons:
        leftArrowButton.setEnabled(false);
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
      }
    } else if (source == moveUpButton) {
      int selIndex = prioritizedRuleList.getSelectedIndex();
      if (selIndex > 0) { // first list element wasn't chosen
        String tempRule = prioritizedRules.remove(selIndex); // remove rule
        prioritizedRules.insertElementAt(tempRule, (selIndex - 1)); // move up
        prioritizedRuleList.setSelectedIndex(selIndex - 1);
        prioritizedRuleList.repaint();
        if ((prioritizedRules.size() == 0)
            || (prioritizedRuleList.isSelectionEmpty())) { // no item is 
        																									 // selected
          // disable butons:
          leftArrowButton.setEnabled(false);
          moveUpButton.setEnabled(false);
          moveDownButton.setEnabled(false);
        }
      }
    } else if (source == moveDownButton) {
      int selIndex = prioritizedRuleList.getSelectedIndex();
      if (selIndex < (prioritizedRules.size() - 1)) { // last list element 
      																								// wasn't chosen
        String tempRule = prioritizedRules.remove(selIndex); // remove rule
        prioritizedRules.insertElementAt(tempRule, (selIndex + 1)); // move down
        prioritizedRuleList.setSelectedIndex(selIndex + 1);
        prioritizedRuleList.repaint();
        if ((prioritizedRules.size() == 0)
            || (prioritizedRuleList.isSelectionEmpty())) { // no item is 
        																									 // selected
          // disable butons:
          leftArrowButton.setEnabled(false);
          moveUpButton.setEnabled(false);
          moveDownButton.setEnabled(false);
        }
      }
    } else if (source == okButton) { // okButton has been pressed
      // update non-prioritized rules:
      for (int i = 0; i < nonPrioritizedRules.size(); i++) {
        String tempRuleInfo = nonPrioritizedRules.elementAt(i);
        ActionType tempActType = actions.getActionType(tempRuleInfo.substring(
            (tempRuleInfo.indexOf('(') + 1), (tempRuleInfo.length() - 1)));
        Rule tempR = tempActType.getRule(tempRuleInfo.substring(0,
            (tempRuleInfo.indexOf('(') - 1)));
        tempR.setPriority(-1);
      }

      // update prioritized rules:
      for (int i = 0; i < prioritizedRules.size(); i++) {
        String tempRuleInfo = prioritizedRules.elementAt(i);
        ActionType tempActType = actions.getActionType(tempRuleInfo.substring(
            (tempRuleInfo.indexOf('(') + 1), (tempRuleInfo.length() - 1)));
        Rule tempR = tempActType.getRule(tempRuleInfo.substring(0,
            (tempRuleInfo.indexOf('(') - 1)));
        tempR.setPriority(i);
      }

      // Close window:
      setVisible(false);
      dispose();
    }
  }

  // enables/disables buttons according to list selections
  private void setupListSelectionListeners() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = nonPrioritizedRuleList.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          rightArrowButton.setEnabled(true);
        }
      }
    });

    ListSelectionModel rowSM2 = prioritizedRuleList.getSelectionModel();
    rowSM2.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          leftArrowButton.setEnabled(true);
          moveUpButton.setEnabled(true);
          moveDownButton.setEnabled(true);
        }
      }
    });
  }
}