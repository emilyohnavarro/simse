/*
 * This class defines the window through which triggers can be put in order for
 * priority of execution
 */

package simse.modelbuilder.actionbuilder;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;

public class TriggerPrioritizer extends JDialog implements ActionListener {
  private DefinedActionTypes actions; // existing defined action types
  private Vector<String> nonPrioritizedTriggers; // vector of triggers that 
  																							 // haven't been prioritized yet
  private Vector<String> prioritizedTriggers; // vector of triggers that have 
  																						// been prioritized
  private JList nonPrioritizedTriggerList; // JList for non-prioritized triggers
  private JList prioritizedTriggerList; // JList for prioritized triggers
  private JButton rightArrowButton; // for moving triggers to the right
  private JButton leftArrowButton; // for moving triggers to the left
  private JButton moveUpButton; // for moving triggers up
  private JButton moveDownButton; // for moving triggers down
  private JButton okButton; // for ok'ing the changes made in this form
  private JButton cancelButton; // for cancelling the changes made in this form

  public TriggerPrioritizer(JFrame owner, DefinedActionTypes actions) {
    super(owner, true);
    this.actions = actions;

    // Set window title:
    setTitle("Trigger Prioritizer -- SimSE");

    // initialize lists:
    nonPrioritizedTriggers = new Vector<String>();
    prioritizedTriggers = new Vector<String>();
    Vector<ActionType> allActions = actions.getAllActionTypes();
    // go through all action types and add their triggers to the correct list:
    for (int i = 0; i < allActions.size(); i++) {
      ActionType tempAct = allActions.elementAt(i);
      Vector<ActionTypeTrigger> allTrigs = tempAct.getAllTriggers();
      for (int j = 0; j < allTrigs.size(); j++) {
        ActionTypeTrigger trig = allTrigs.elementAt(j);
        int priority = trig.getPriority();
        String triggerInfo = new String(trig.getName() + " - "
            + tempAct.getName() + " (");
        if (trig instanceof AutonomousActionTypeTrigger) {
          triggerInfo = triggerInfo.concat(ActionTypeTrigger.AUTO + ")");
        } else if (trig instanceof UserActionTypeTrigger) {
          triggerInfo = triggerInfo.concat(ActionTypeTrigger.USER + ")");
        } else if (trig instanceof RandomActionTypeTrigger) {
          triggerInfo = triggerInfo.concat(ActionTypeTrigger.RANDOM + ")");
        }
        if (priority == -1) { // trigger is not prioritized yet
          nonPrioritizedTriggers.addElement(triggerInfo);
        } else { // priority >= 0
          if (prioritizedTriggers.size() == 0) { // no elements have been added
                                               	 // yet to the prioritized 
          																			 // trigger list
            prioritizedTriggers.add(triggerInfo);
          } else {
            // find the correct position to insert the trigger at:
            for (int k = 0; k < prioritizedTriggers.size(); k++) {
              String tempTrigInfo = prioritizedTriggers.elementAt(k);
              String tempTrigName = tempTrigInfo.substring(0, tempTrigInfo
                  .indexOf(' '));
              String tempActName = tempTrigInfo.substring((tempTrigInfo
                  .indexOf('-') + 2), (tempTrigInfo.indexOf('(') - 1));
              ActionType tempActType = actions.getActionType(tempActName);
              ActionTypeTrigger tempTrig = tempActType.getTrigger(tempTrigName);
              if (priority <= tempTrig.getPriority()) {
                prioritizedTriggers.insertElementAt(triggerInfo, k); // insert
                                                                     // the
                                                                     // trigger
                break;
              } else if (k == (prioritizedTriggers.size() - 1)) { // on the last
                                                                	// element
                prioritizedTriggers.add(triggerInfo); // add the trigger to the
                                                      // end of the list
                break;
              }
            }
          }
        }
      }
    }

    nonPrioritizedTriggerList = new JList(nonPrioritizedTriggers);
    nonPrioritizedTriggerList.setVisibleRowCount(10); // make 10 items visible
                                                      // at a time
    nonPrioritizedTriggerList
        .setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    prioritizedTriggerList = new JList(prioritizedTriggers);
    prioritizedTriggerList.setVisibleRowCount(10); // make 10 items visible at a
                                                   // time
    prioritizedTriggerList
        .setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    setupListSelectionListeners();

    // Create main panel:
    Box mainPane = Box.createVerticalBox();

    // Create top panel:
    JPanel topPane = new JPanel();

    // Create topPaneA and components:
    Box topPaneA = Box.createVerticalBox();
    topPaneA.add(new JLabel("Non-Prioritized Triggers:"));
    JScrollPane nonPriListPane = new JScrollPane(nonPrioritizedTriggerList);
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
    topPaneC.add(new JLabel("Prioritized Triggers:"));
    JScrollPane priListPane = new JScrollPane(prioritizedTriggerList);
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
      int selIndex = nonPrioritizedTriggerList.getSelectedIndex();
      String tempStr = nonPrioritizedTriggers.elementAt(selIndex);
      nonPrioritizedTriggers.remove(selIndex); // remove trigger from lefthand
                                               // list
      prioritizedTriggers.add(tempStr); // add to righthand list
      String tempSelectedVal = ((String) (prioritizedTriggerList
          .getSelectedValue()));
      // reset list data:
      prioritizedTriggerList.setListData(prioritizedTriggers);
      // reset selected value:
      if ((tempSelectedVal != null) && (tempSelectedVal.length() > 0)) { 
      	// selection before resetting list data was non-empty
        prioritizedTriggerList.setSelectedValue(tempSelectedVal, true);
      }
      nonPrioritizedTriggerList.repaint();
      if ((nonPrioritizedTriggers.size() == 0)
          || (nonPrioritizedTriggerList.getSelectedIndex() > 
          (nonPrioritizedTriggers.size() - 1))) { // no item is selected (had to
      																						// use nonPrioritizedTriggers.
      																						// size() - 1 because it 
      																						// doesn't work using selected
      																						// index = -1 or selected 
      																						// value == null!
        // disable button:
        rightArrowButton.setEnabled(false);
      }
    } else if (source == leftArrowButton) {
      int selIndex = prioritizedTriggerList.getSelectedIndex();
      String tempStr = prioritizedTriggers.elementAt(selIndex);
      // remove trigger from righthand list:
      prioritizedTriggers.remove(selIndex); 
      // add to lefthand list:
      nonPrioritizedTriggers.add(tempStr); 
      String tempSelectedVal = ((String) (nonPrioritizedTriggerList
          .getSelectedValue()));
      // reset list data:
      nonPrioritizedTriggerList.setListData(nonPrioritizedTriggers);
      // reset selected value:
      if ((tempSelectedVal != null) && (tempSelectedVal.length() > 0)) { 
      	// selection before resetting list data was non-empty
        nonPrioritizedTriggerList.setSelectedValue(tempSelectedVal, true);
      }
      prioritizedTriggerList.repaint();
      if ((prioritizedTriggers.size() == 0)
          || (prioritizedTriggerList.getSelectedIndex() > (prioritizedTriggers
              .size() - 1))) { // no item is selected (had to use 
      												 // prioritizedTriggers.size() - 1 because it
      												 // doesn't work using selected index = -1 or
      												 // selected value == null!
        // disable butons:
        leftArrowButton.setEnabled(false);
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
      }
    } else if (source == moveUpButton) {
      int selIndex = prioritizedTriggerList.getSelectedIndex();
      if (selIndex > 0) { // first list element wasn't chosen
      	// remove trigger:
        String tempAct = prioritizedTriggers.remove(selIndex); 
        prioritizedTriggers.insertElementAt(tempAct, (selIndex - 1)); // move up
        prioritizedTriggerList.setSelectedIndex(selIndex - 1);
        prioritizedTriggerList.repaint();
        if ((prioritizedTriggers.size() == 0)
            || (prioritizedTriggerList.isSelectionEmpty())) { // no item is
                                                            	// selected
          // disable butons:
          leftArrowButton.setEnabled(false);
          moveUpButton.setEnabled(false);
          moveDownButton.setEnabled(false);
        }
      }
    } else if (source == moveDownButton) {
      int selIndex = prioritizedTriggerList.getSelectedIndex();
      if (selIndex < (prioritizedTriggers.size() - 1)) { // last list element
                                                       	 // wasn't chosen
      	// remove trigger:
        String tempAct = prioritizedTriggers.remove(selIndex); 
        // move down:
        prioritizedTriggers.insertElementAt(tempAct, (selIndex + 1)); 
        prioritizedTriggerList.setSelectedIndex(selIndex + 1);
        prioritizedTriggerList.repaint();
        if ((prioritizedTriggers.size() == 0)
            || (prioritizedTriggerList.isSelectionEmpty())) { // no item is
                                                            	// selected
          // disable butons:
          leftArrowButton.setEnabled(false);
          moveUpButton.setEnabled(false);
          moveDownButton.setEnabled(false);
        }
      }
    } else if (source == okButton) { // okButton has been pressed
      // update non-prioritized triggers:
      for (int i = 0; i < nonPrioritizedTriggers.size(); i++) {
        String tempTrigInfo = nonPrioritizedTriggers.elementAt(i);
        String tempTrigName = tempTrigInfo.substring(0, tempTrigInfo
            .indexOf(' '));
        String tempActName = tempTrigInfo.substring(
            (tempTrigInfo.indexOf('-') + 2), (tempTrigInfo.indexOf('(') - 1));
        ActionType tempActType = actions.getActionType(tempActName);
        ActionTypeTrigger tempTrig = tempActType.getTrigger(tempTrigName);
        tempTrig.setPriority(-1);
      }

      // update prioritized triggers:
      for (int i = 0; i < prioritizedTriggers.size(); i++) {
        String tempTrigInfo = prioritizedTriggers.elementAt(i);
        String tempTrigName = tempTrigInfo.substring(0, tempTrigInfo
            .indexOf(' '));
        String tempActName = tempTrigInfo.substring(
            (tempTrigInfo.indexOf('-') + 2), (tempTrigInfo.indexOf('(') - 1));
        ActionType tempActType = actions.getActionType(tempActName);
        ActionTypeTrigger tempTrig = tempActType.getTrigger(tempTrigName);
        tempTrig.setPriority(i);
      }

      // Close window:
      setVisible(false);
      dispose();
    }
  }

  // enables/disables buttons according to list selections
  private void setupListSelectionListeners() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = nonPrioritizedTriggerList.getSelectionModel();
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

    ListSelectionModel rowSM2 = prioritizedTriggerList.getSelectionModel();
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