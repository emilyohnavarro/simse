/*
 * This class defines the intial GUI for managing the action destroyers of an
 * action type
 */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.SimSEObjectType;

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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ActionTypeDestroyerManagementForm extends JDialog implements
    ActionListener, MouseListener {
  private ActionType actionInFocus; // temporary copy of action being edited
  private ActionType originalAction; // original action
  private DefinedActionTypes allActions; // all of the currently defined actions
                                         // (for passing into the destroyer info
                                         // form)
  private Vector<String> destroyerNames; // for the JList

  private JList destroyerList; // for choosing a destroyer to edit
  private JButton viewEditButton; // for viewing/editing destroyers
  private JButton removeButton; // for removing destroyers
  private JButton newDestroyerButton; // for adding a new destroyer
  private JButton okButton;
  private JButton cancelButton;

  public ActionTypeDestroyerManagementForm(JFrame owner, ActionType action,
      DefinedActionTypes allActions) {
    super(owner, true);
    originalAction = action; // store pointer to original
    actionInFocus = (ActionType) action.clone(); // make a copy of the action
                                                 // for temporary editing
    this.allActions = allActions;

    // Set window title:
    setTitle("Destroyer Management");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();

    // Create topPane and label:
    JPanel topLabelPane = new JPanel();
    topLabelPane.add(new JLabel("Existing Destroyers:"));

    // Create top pane and destroyer list:
    destroyerList = new JList();
    destroyerList.setVisibleRowCount(10); // make 10 items visible at a time
    destroyerList.setFixedCellWidth(200);
    destroyerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    destroyerList.addMouseListener(this);
    JScrollPane topPane = new JScrollPane(destroyerList);
    // initialize destroyer names:
    destroyerNames = new Vector<String>();
    refreshDestroyerList();
    setUpDestroyerListActionListenerStuff();

    // Create middle pane and buttons:
    JPanel middlePane = new JPanel();
    viewEditButton = new JButton("View/Edit");
    viewEditButton.addActionListener(this);
    viewEditButton.setEnabled(false);
    middlePane.add(viewEditButton);
    removeButton = new JButton("Remove");
    removeButton.addActionListener(this);
    removeButton.setEnabled(false);
    middlePane.add(removeButton);
    newDestroyerButton = new JButton("Add New Destroyer");
    newDestroyerButton.addActionListener(this);
    middlePane.add(newDestroyerButton);

    // Create bottom pane and buttons:
    JPanel bottomPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    bottomPane.add(okButton);
    bottomPane.add(cancelButton);

    // Add panes and separators to main pane:
    mainPane.add(topLabelPane);
    mainPane.add(topPane);
    mainPane.add(middlePane);
    JSeparator separator3 = new JSeparator();
    separator3.setMaximumSize(new Dimension(450, 1));
    mainPane.add(separator3);
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

    if (source == viewEditButton) {
      viewEditButtonClicked();
    } else if (source == removeButton) {
      if (destroyerList.isSelectionEmpty() == false) { // a destroyer is 
      																								 // selected
        String selectedStr = (String) destroyerList.getSelectedValue();
        int choice = JOptionPane
            .showConfirmDialog(
                null,
                ("Really remove "
                    + selectedStr.substring(0, selectedStr.indexOf(' ')) + 
                    " destroyer?"), "Confirm Destroyer Removal", 
                    JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
          // Remove destroyer:
          actionInFocus.removeDestroyer(selectedStr.substring(0, selectedStr
              .indexOf(' ')));
          removeButton.setEnabled(false);
          viewEditButton.setEnabled(false);
          refreshDestroyerList();
        }
      }
    } else if (source == newDestroyerButton) {
      // Create a new destroyer:
      AutonomousActionTypeDestroyer newDest = new AutonomousActionTypeDestroyer(
          "", actionInFocus);
      // add a participant destroyer for each participant:
      Vector<ActionTypeParticipant> allParts = 
      	actionInFocus.getAllParticipants();
      for (int i = 0; i < allParts.size(); i++) {
        ActionTypeParticipant tempPart = allParts.elementAt(i);
        newDest.addEmptyDestroyer(tempPart);
        // add an empty constraint to the participant destroyer for each
        // allowable SimSEObjectType:
        Vector<SimSEObjectType> allTypes = tempPart.getAllSimSEObjectTypes();
        for (int j = 0; j < allTypes.size(); j++) {
          newDest.getParticipantDestroyer(tempPart.getName())
              .addEmptyConstraint(allTypes.elementAt(j));
        }
      }
      // Bring up form for adding a new destroyer:
      new ActionTypeDestroyerInfoForm(this, actionInFocus, newDest, allActions);
      refreshDestroyerList();
    } else if (source == okButton) {
    	// set permanent action's destroyers to the edited one:
      originalAction.setDestroyers(actionInFocus.getAllDestroyers()); 
      setVisible(false);
      dispose();
    }  else if (source == cancelButton) {
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
    if ((me.getSource() == destroyerList) && 
        (me.getButton() == MouseEvent.BUTTON1) && (clicks >= 2)) {
      viewEditButtonClicked();
    }
  }

  private void refreshDestroyerList() {
    destroyerNames.removeAllElements();
    Vector<ActionTypeDestroyer> dests = actionInFocus.getAllDestroyers();
    for (int i = 0; i < dests.size(); i++) {
      ActionTypeDestroyer tempDest = dests.elementAt(i);
      String destInfo = new String(tempDest.getName() + " (");
      if (tempDest instanceof AutonomousActionTypeDestroyer) {
        destInfo = destInfo.concat(ActionTypeDestroyer.AUTO);
      } else if (tempDest instanceof RandomActionTypeDestroyer) {
        destInfo = destInfo.concat(ActionTypeDestroyer.RANDOM);
      } else if (tempDest instanceof UserActionTypeDestroyer) {
        destInfo = destInfo.concat(ActionTypeDestroyer.USER);
      } else if (tempDest instanceof TimedActionTypeDestroyer) {
        destInfo = destInfo.concat(ActionTypeDestroyer.TIMED);
      }
      destInfo = destInfo.concat(")");
      destroyerNames.add(destInfo);
    }
    destroyerList.setListData(destroyerNames);
  }

  /*
   * enables view/edit and remove buttons whenever a list item (destroyer) is
   * selected
   */
  private void setUpDestroyerListActionListenerStuff() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = destroyerList.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          viewEditButton.setEnabled(true);
          removeButton.setEnabled(true);
        }
      }
    });
  }
  
  private void viewEditButtonClicked() {
    if (!destroyerList.isSelectionEmpty()) { // a destroyer is selected
      // Bring up form for viewing/editing destroyer:
      String selectedStr = (String) destroyerList.getSelectedValue();
      ActionTypeDestroyer dest = actionInFocus.getDestroyer(selectedStr
          .substring(0, selectedStr.indexOf(' ')));
      new ActionTypeDestroyerInfoForm(this, actionInFocus, dest, allActions);
      removeButton.setEnabled(false);
      viewEditButton.setEnabled(false);
      refreshDestroyerList();
    }
  }

  public class ExitListener extends WindowAdapter {
    public void windowClosing(WindowEvent event) {
      setVisible(false);
      dispose();
    }
  }
}