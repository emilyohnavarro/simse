/* This class defines the GUI for building action types with */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.ModelBuilderGUI;
import simse.modelbuilder.WarningListPane;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import java.io.File;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

public class ActionBuilderGUI extends JPanel implements ActionListener,
    ListSelectionListener, MouseListener {
  private JFrame mainGUI;
  private DefinedActionTypes actions; // data structure for holding all of the
                                      // created action types
  private DefinedObjectTypes objects; // data structure for holding all of the
                                      // created SimSE object types
  private ActionFileManipulator actFileManip; // for saving/loading action files
  private JButton createNewActionButton; // button to create a new action
  private JLabel actionTableTitle; // label for title of action table
  private JScrollPane actionTablePane; // pane for action table
  private JTable actionTable; // table for displaying the entities in an action
  private ActionTableModel actTblMod; // model for above table
  private JButton addParticipantButton; // button for adding a participant to an
                                        // action
  private JButton editParticipantButton; // button for editing a participant in
                                         // an action
  private JButton removeParticipantButton; // button for deleting a participant
                                           // from an action
  private JButton movePartUpButton; // for moving a participant up
  private JButton movePartDownButton; // for moving a participant down
  private JButton triggerButton; // button for viewing/editing action triggers
  private JButton destroyerButton; // button for viewing/editing action
                                   // destroyers
  private JButton optionsButton; // button for viewing/editing action
                                    // visibility
  private JList definedActionsList; // JList of already defined actions
  private JButton moveActUpButton;
  private JButton moveActDownButton;
  private JButton renameActionButton; // button for renaming actions
  private JButton removeActionButton; // button for removing an already defined
                                      // action
  private ActionTypeParticipantInfoForm apInfoForm; // form for entering/editing
                                                    // info about a participant
  private WarningListPane warningPane;

  public ActionBuilderGUI(JFrame owner, DefinedObjectTypes objects) {
    mainGUI = owner;
    actions = new DefinedActionTypes();

    this.objects = objects;
    actFileManip = new ActionFileManipulator(objects, actions);

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();
    mainPane.setPreferredSize(new Dimension(1024, 650));

    // Create "create action" pane:
    JPanel createActionPane = new JPanel();
    createNewActionButton = new JButton("Create New Action Type");
    createNewActionButton.addActionListener(this);
    createActionPane.add(createNewActionButton);

    // Create action table title label and pane:
    JPanel actionTableTitlePane = new JPanel();
    actionTableTitle = new JLabel("No action selected");
    actionTableTitlePane.add(actionTableTitle);

    // Create "action table" pane:
    actTblMod = new ActionTableModel();
    actionTable = new JTable(actTblMod);
    DefaultTableCellRenderer rightAlignRenderer = 
    	new DefaultTableCellRenderer();
		rightAlignRenderer.setHorizontalAlignment(JLabel.RIGHT);
		actionTable.getColumnModel().getColumn(2).setCellRenderer(
				rightAlignRenderer);
    actionTable.addMouseListener(this);
    actionTablePane = new JScrollPane(actionTable);
    actionTablePane
        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    actionTablePane.setPreferredSize(new Dimension(1024, 250));
    setupActionTableSelectionListenerStuff();

    // Create action table button pane and buttons:
    JPanel actionTableButtonPane = new JPanel();
    addParticipantButton = new JButton("Add Participant");
    addParticipantButton.addActionListener(this);
    editParticipantButton = new JButton("Edit Participant");
    editParticipantButton.addActionListener(this);
    removeParticipantButton = new JButton("Remove Participant");
    removeParticipantButton.addActionListener(this);
    movePartUpButton = new JButton("Move Up");
    movePartUpButton.addActionListener(this);
    movePartDownButton = new JButton("Move Down");
    movePartDownButton.addActionListener(this);
    actionTableButtonPane.add(addParticipantButton);
    actionTableButtonPane.add(editParticipantButton);
    actionTableButtonPane.add(removeParticipantButton);
    actionTableButtonPane.add(movePartUpButton);
    actionTableButtonPane.add(movePartDownButton);
    addParticipantButton.setEnabled(false);
    editParticipantButton.setEnabled(false);
    removeParticipantButton.setEnabled(false);
    movePartUpButton.setEnabled(false);
    movePartDownButton.setEnabled(false);

    // Create trigger/destroyer button pane and buttons:
    JPanel trigDestroyButtonPane = new JPanel();
    triggerButton = new JButton("View/Edit Triggers");
    triggerButton.addActionListener(this);
    destroyerButton = new JButton("View/Edit Destroyers");
    destroyerButton.addActionListener(this);
    optionsButton = new JButton("View/Edit Action Options");
    optionsButton.addActionListener(this);
    trigDestroyButtonPane.add(triggerButton);
    trigDestroyButtonPane.add(destroyerButton);
    trigDestroyButtonPane.add(optionsButton);
    triggerButton.setEnabled(false);
    destroyerButton.setEnabled(false);
    optionsButton.setEnabled(false);

    // Create "defined actions" pane:
    JPanel definedActionsPane = new JPanel();
    definedActionsPane.add(new JLabel("Actions Already Defined:"));

    // Create and add defined actions list to a scroll pane:
    definedActionsList = new JList();
    definedActionsList.setVisibleRowCount(7); // make 7 items visible at a time
    definedActionsList.setFixedCellWidth(500);
    // only allow the user to select one item at a time:
    definedActionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    definedActionsList.addListSelectionListener(this);
    JScrollPane definedActionsListPane = new JScrollPane(definedActionsList);
    definedActionsPane.add(definedActionsListPane);
    updateDefinedActionsList();
    setupDefinedActionsListSelectionListenerStuff();

    // Create and defined actions button pane:
    Box definedActionsButtonPane = Box.createVerticalBox();
    moveActUpButton = new JButton("Move up     ");
    definedActionsButtonPane.add(moveActUpButton);
    moveActUpButton.addActionListener(this);
    moveActUpButton.setEnabled(false);
    moveActDownButton = new JButton("Move down");
    definedActionsButtonPane.add(moveActDownButton);
    moveActDownButton.addActionListener(this);
    moveActDownButton.setEnabled(false);    
    renameActionButton = new JButton("Rename     ");
    definedActionsButtonPane.add(renameActionButton);
    renameActionButton.addActionListener(this);
    renameActionButton.setEnabled(false);
    removeActionButton = new JButton("Remove     ");
    definedActionsButtonPane.add(removeActionButton);
    removeActionButton.addActionListener(this);
    removeActionButton.setEnabled(false);
    definedActionsPane.add(definedActionsButtonPane);

    // Warning list pane:
    warningPane = new WarningListPane();

    // Add panes and separators to main pane:
    mainPane.add(createActionPane);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator1);
    mainPane.add(actionTableTitlePane);
    mainPane.add(actionTablePane);
    mainPane.add(actionTableButtonPane);
    mainPane.add(trigDestroyButtonPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator2);
    mainPane.add(definedActionsPane);
    JSeparator separator3 = new JSeparator();
    separator3.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator3);
    mainPane.add(warningPane);
    add(mainPane);

    // make it so no file is open to begin with:
    setNoOpenFile();

    setOpaque(true);
    validate();
    repaint();
  }

  public void mousePressed(MouseEvent me) {}

  public void mouseReleased(MouseEvent me) {}

  public void mouseEntered(MouseEvent me) {}

  public void mouseExited(MouseEvent me) {}

  public void mouseClicked(MouseEvent me) {
    int clicks = me.getClickCount();

    if (me.getButton() == MouseEvent.BUTTON1 && clicks >= 2) {
      if (actionTable.getSelectedRow() >= 0) { // a row is selected
        ActionTypeParticipant tempPart = actTblMod
            .getActionTypeInFocus()
            .getParticipant((String) (actTblMod.getValueAt(
            		actionTable.getSelectedRow(), 0)));
        editParticipant(tempPart);
        editParticipantButton.setEnabled(false);
        removeParticipantButton.setEnabled(false);
        movePartUpButton.setEnabled(false);
        movePartDownButton.setEnabled(false);
      }
    }
  }

  public void valueChanged(ListSelectionEvent e) {
    if (definedActionsList.getSelectedIndex() >= 0) { // an item (action) is
                                                      // selected
      ActionType tempAct = actions.getAllActionTypes()
          .elementAt(definedActionsList.getSelectedIndex());
      // get the selected action type
      setActionInFocus(tempAct);
    }
  }

  public DefinedActionTypes getDefinedActionTypes() {
    return actions;
  }

  /*
   * reloads the action types from a temporary file; if restUI is true, clears
   * all current selections
   */
  public void reload(File tempFile, boolean resetUI) {
    // reload:
    Vector<String> warnings = actFileManip.loadFile(tempFile);
    generateWarnings(warnings);

    if (resetUI) {
	    // reset UI stuff:
	    updateDefinedActionsList();
	    clearActionInFocus();
	    createNewActionButton.setEnabled(false);
	    Vector<SimSEObjectType> objTypes = objects.getAllObjectTypes();
	    if (objTypes.size() > 0) { // there is at least one object type
	      // make sure there is at least one object type w/ at least one 
	    	// attribute:
	      for (int i = 0; i < objTypes.size(); i++) {
	        SimSEObjectType type = objTypes.elementAt(i);
	        // check if it has at least one attribute:
	        if (type.getAllAttributes().size() > 0) {
	          createNewActionButton.setEnabled(true);
	          break;
	        }
	      }
	    } else {
	      createNewActionButton.setEnabled(false);
	    }
    }
  }

  /*
   * displays warnings of errors found during checking for inconsistencies
   */
  private void generateWarnings(Vector<String> warnings) { 
    if (warnings.size() > 0) { // there is at least 1 warning
      warningPane.setWarnings(warnings);
    }
  }

  // handles user actions
  public void actionPerformed(ActionEvent evt) { 
    Object source = evt.getSource(); // get which component the action came from
    if (source == createNewActionButton) { // User has pressed the "Create New
                                         	 // Action" button
      createNewAction();
    }

    else if (source == addParticipantButton) { // user has requested to add a 
    																					 // new participant
      addParticipant();
      editParticipantButton.setEnabled(false);
      removeParticipantButton.setEnabled(false);
      movePartUpButton.setEnabled(false);
      movePartDownButton.setEnabled(false);
    }

    else if (source == editParticipantButton) {
      if (actionTable.getSelectedRow() >= 0) { // a row is selected
        ActionTypeParticipant tempPart = actTblMod
            .getActionTypeInFocus()
            .getParticipant((String) (actTblMod.getValueAt(
            		actionTable.getSelectedRow(), 0)));
        editParticipant(tempPart);
        editParticipantButton.setEnabled(false);
        removeParticipantButton.setEnabled(false);
        movePartUpButton.setEnabled(false);
        movePartDownButton.setEnabled(false);
      }
    }

    else if (source == removeParticipantButton) {
      if (actionTable.getSelectedRow() >= 0) { // a row is selected
        ActionTypeParticipant tempPart = actTblMod
            .getActionTypeInFocus()
            .getParticipant((String) (actTblMod.getValueAt(
            		actionTable.getSelectedRow(), 0)));
        removeParticipant(tempPart);
      }
    }
    
    else if (source == movePartUpButton) {
      if (actionTable.getSelectedRow() >= 0) { // a row is selected
        ActionTypeParticipant tempPart = actTblMod.getActionTypeInFocus().
        	getParticipant((String) (actTblMod.getValueAt(
        	    actionTable.getSelectedRow(), 0)));
        moveParticipantUp(tempPart);
      }
    }
    
    else if (source == movePartDownButton) {
      if (actionTable.getSelectedRow() >= 0) { // a row is selected
        ActionTypeParticipant tempPart = actTblMod.getActionTypeInFocus().
        	getParticipant((String) (actTblMod.getValueAt(
        	    actionTable.getSelectedRow(), 0)));
        moveParticipantDown(tempPart);
      }
    }
    
    else if (source == moveActUpButton) {
      if (!definedActionsList.getSelectionModel().isSelectionEmpty()) {
        ActionType tempAct = actions.getAllActionTypes()
            .elementAt(definedActionsList.getSelectedIndex());
        moveActionUp(tempAct);
      }
    }
    
    else if (source == moveActDownButton) {
      if (!definedActionsList.getSelectionModel().isSelectionEmpty()) {
        ActionType tempAct = actions.getAllActionTypes()
            .elementAt(definedActionsList.getSelectedIndex());
        moveActionDown(tempAct);
      }
    }

    else if (source == removeActionButton) {
      if (definedActionsList.getSelectedIndex() >= 0) { // an item (action) is
                                                      	// selected
        // remove the selected action type:
        removeAction((String) (definedActionsList.getSelectedValue()));
      }
    } else if (source == renameActionButton) {
      if (definedActionsList.getSelectedIndex() >= 0) { // an item (action) is
                                                      	// selected
        // remove the selected action type:
        renameAction((String) (definedActionsList.getSelectedValue()));
      }
    }

    else if (source == triggerButton) { // trigger button selected
      new ActionTypeTriggerManagementForm(mainGUI, 
      		actTblMod.getActionTypeInFocus(), actions);
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    }

    else if (source == destroyerButton) { // destroyer button selected
      new ActionTypeDestroyerManagementForm(mainGUI, 
      		actTblMod.getActionTypeInFocus(), actions);
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    }

    else if (source == optionsButton) { // options button selected
      new ActionTypeOptionsInfoForm(mainGUI, actTblMod.getActionTypeInFocus());
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    }
  }

  /*
   * creates a new action and adds it to the data structure
   */
  private void createNewAction() { 
    String response = JOptionPane.showInputDialog(null,
        "Enter a name for this action:", "Enter Action Name",
        JOptionPane.QUESTION_MESSAGE); // Show input dialog
    if (response != null) {
      if (actionNameInputValid(response) == false) { // input is invalid
        JOptionPane
            .showMessageDialog(
                null,
                "Please enter a unique name, between 2 and 40 alphabetic " +
                "characters, and no spaces", "Invalid Input", 
                JOptionPane.WARNING_MESSAGE); // warn user to enter a valid
                                              // name
        createNewAction(); // try again
      } else { // user has entered valid input
        ActionType newAction = new ActionType(response); // create new action
                                                         // type
        actions.addActionType(newAction); // add to the data structure
        ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
        updateDefinedActionsList();
        setActionInFocus(newAction); // set newly created action to be the focus
        														 // of the GUI
        // disable buttons:
        moveActUpButton.setEnabled(false);
        moveActDownButton.setEnabled(false);
        removeActionButton.setEnabled(false);
        renameActionButton.setEnabled(false);
      }
    }
  }

  // returns true if input is a valid action name, false if not
  private boolean actionNameInputValid(String input) { 
    char[] cArray = input.toCharArray();

    // Check for length constraints:
    if ((cArray.length < 2) || (cArray.length > 40)) { // user has a string
                                                     	 // shorter than 2 chars 
    																									 // or longer than 40 
    																									 // chars
      return false;
    }

    // Check for invalid characters:
    for (int i = 0; i < cArray.length; i++) {
      if ((Character.isLetter(cArray[i])) == false) { // character is not a 
      																								// letter (hence, invalid)
        return false;
      }
    }

    // Check for uniqueness of name:
    Vector<ActionType> existingActionTypes = actions.getAllActionTypes();
    for (int i = 0; i < existingActionTypes.size(); i++) {
      ActionType tempAct = existingActionTypes.elementAt(i);
      if (tempAct.getName().equalsIgnoreCase(input)) { // name entered is not
                                                     	 // unique (there is 
      																							   // already another action
      																								 // defined with the same 
      																								 // name (hence, invalid)
        return false;
      }
    }

    return true; // none of the invalid conditions exist
  }

  private void addParticipant() {
    actTblMod.refreshData();
    apInfoForm = new ActionTypeParticipantInfoForm(mainGUI, actTblMod
        .getActionTypeInFocus(), null, objects);
    // Makes it so this gui will refresh itself when the ap info form closes:
    WindowFocusListener l = new WindowFocusListener() {
      public void windowLostFocus(WindowEvent ev) {
        actTblMod.refreshData();
        editParticipantButton.setEnabled(false);
        removeParticipantButton.setEnabled(false);
        movePartUpButton.setEnabled(false);
        movePartDownButton.setEnabled(false);
      }

      public void windowGainedFocus(WindowEvent ev) {}
    };
    apInfoForm.addWindowFocusListener(l);
    ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
  }

  private void editParticipant(ActionTypeParticipant p) {
    actTblMod.refreshData();
    apInfoForm = new ActionTypeParticipantInfoForm(mainGUI, actTblMod
        .getActionTypeInFocus(), p, objects);
    // Makes it so action info form window holds focus exclusively:
    WindowFocusListener l = new WindowFocusListener() {
      public void windowLostFocus(WindowEvent ev) {
        actTblMod.refreshData();
      }

      public void windowGainedFocus(WindowEvent ev) {}
    };
    apInfoForm.addWindowFocusListener(l);
    ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
  }

  private void removeParticipant(ActionTypeParticipant p) {
    int choice = JOptionPane.showConfirmDialog(null, ("Really remove "
        + p.getName() + " participant?"), "Confirm Participant Removal",
        JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
      // Remove participant:
      actTblMod.getActionTypeInFocus().removeParticipant(p.getName());
      actTblMod.refreshData();
      removeParticipantButton.setEnabled(false);
      editParticipantButton.setEnabled(false);
      movePartUpButton.setEnabled(false);
      movePartDownButton.setEnabled(false);
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    } 
  }

  /*
   * enables edit and remove participant buttons whenever a row (participant)
   * is selected
   */
  private void setupActionTableSelectionListenerStuff() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = actionTable.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          editParticipantButton.setEnabled(true);
          removeParticipantButton.setEnabled(true);
          movePartUpButton.setEnabled(true);
          movePartDownButton.setEnabled(true);
        }
      }
    });
  }

  // enables buttons whenever a list item (action is selected)
  private void setupDefinedActionsListSelectionListenerStuff() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = definedActionsList.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          definedActionsList.ensureIndexIsVisible(
              definedActionsList.getSelectedIndex());
          moveActUpButton.setEnabled(true);
          moveActDownButton.setEnabled(true);
          removeActionButton.setEnabled(true);
          renameActionButton.setEnabled(true);
        }
      }
    });
  }

  private void updateDefinedActionsList() {
    Vector<String> actionNames = new Vector<String>();
    Vector<ActionType> currentActs = actions.getAllActionTypes();
    for (int i = 0; i < currentActs.size(); i++) {
      actionNames.add(currentActs.elementAt(i).getName());
    }
    definedActionsList.setListData(actionNames);
  }

  // sets the given action type as the focus of this GUI
  private void setActionInFocus(ActionType newAct) { 
    actTblMod.setActionTypeInFocus(newAct); // set focus of action table to new
                                            // action type
    // change title of table to reflect new action:
    actionTableTitle.setText((newAct.getName()) + " Action Participants:"); 
    // enable buttons:
    addParticipantButton.setEnabled(true);
    editParticipantButton.setEnabled(false);
    removeParticipantButton.setEnabled(false);
    movePartUpButton.setEnabled(false);
    movePartDownButton.setEnabled(false);
    triggerButton.setEnabled(true);
    destroyerButton.setEnabled(true);
    optionsButton.setEnabled(true);
    
    // set focus on defined actions list:
    definedActionsList.setSelectedValue(newAct.getName(), true);
  }

  // remove sthe action with the specified name from the data structure
  private void renameAction(String actionName) { 
    ActionType act = actions.getActionType(actionName);
    // show input dialog:
    String response = JOptionPane.showInputDialog(null, "Enter new name for "
        + act.getName(), "Rename Action Type", JOptionPane.QUESTION_MESSAGE); 
    if (response != null) {
      if (actionNameInputValid(response) == false) { // input is invalid
        JOptionPane
            .showMessageDialog(
                null,
                "Please enter a unique name, between 2 and 40 alphabetic " +
                "characters, and no spaces", "Invalid Input", 
                JOptionPane.WARNING_MESSAGE); // warn user to enter a valid
                                              // number
        renameAction(actionName); // try again
      } else { // user has entered valid input
        act.setName(response);
        updateDefinedActionsList();
        setActionInFocus(act); // set newly created object to be the focus of
                               // the GUI
        ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
      }
    }
  }

  // removes the action with the specified name from the data structure
  private void removeAction(String actionName) { 
    int choice = JOptionPane.showConfirmDialog(null, ("Really remove "
        + actionName + " action type?"), "Confirm Action Type Removal",
        JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
      actions.removeActionType(actionName);
      if ((actTblMod.getActionTypeInFocus() != null)
          && (actTblMod.getActionTypeInFocus().getName() == actionName)) { 
      	// removing action type currently in focus
        clearActionInFocus(); // set it so that there's no action type in focus
      }
      moveActUpButton.setEnabled(false);
      moveActDownButton.setEnabled(false);
      removeActionButton.setEnabled(false);
      renameActionButton.setEnabled(false);
      updateDefinedActionsList();
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    } 
  }

  // clears the GUI so that it doesn't have an action type in focus
  private void clearActionInFocus() { 
    actTblMod.clearActionTypeInFocus(); // clear the table
    actionTableTitle.setText("No action type selected");
    // disable buttons:
    addParticipantButton.setEnabled(false);
    editParticipantButton.setEnabled(false);
    removeParticipantButton.setEnabled(false);
    movePartUpButton.setEnabled(false);
    movePartDownButton.setEnabled(false);
    triggerButton.setEnabled(false);
    destroyerButton.setEnabled(false);
    optionsButton.setEnabled(false);
  }

  public void setNewOpenFile(File f) {
    clearActionInFocus();
    actions.clearAll();
    updateDefinedActionsList();
    createNewActionButton.setEnabled(false);
    warningPane.clearWarnings();
    if (f.exists()) { // file has been saved before
      reload(f, true);
    }
  }

  // makes it so there's no open file in the GUI
  public void setNoOpenFile() { 
    clearActionInFocus();
    actions.clearAll();
    updateDefinedActionsList();
    // disable UI components:
    createNewActionButton.setEnabled(false);
    addParticipantButton.setEnabled(false);
    editParticipantButton.setEnabled(false);
    removeParticipantButton.setEnabled(false);
    movePartUpButton.setEnabled(false);
    movePartDownButton.setEnabled(false);
    triggerButton.setEnabled(false);
    destroyerButton.setEnabled(false);
    optionsButton.setEnabled(false);
    moveActUpButton.setEnabled(false);
    moveActDownButton.setEnabled(false);
    removeActionButton.setEnabled(false);
    renameActionButton.setEnabled(false);
    warningPane.clearWarnings();
  }
  
  // Moves a participant up in the list
  private void moveParticipantUp(ActionTypeParticipant part) {
    int partIndex = 
      actTblMod.getActionTypeInFocus().getParticipantIndex(part.getName());
    if (partIndex > 0) { // first list element wasn't chosen
      int position = actTblMod.getActionTypeInFocus().
      	temporarilyRemoveParticipant(part.getName());
      
      // move up:
      actTblMod.getActionTypeInFocus().addParticipant(part, (position - 1)); 
      ((ModelBuilderGUI)mainGUI).setFileModSinceLastSave();
      actTblMod.refreshData();
      actionTable.setRowSelectionInterval((position - 1), (position - 1));
    }
  }
  
  // Moves a participant down in the list
  private void moveParticipantDown(ActionTypeParticipant part) {
    int partIndex = 
      actTblMod.getActionTypeInFocus().getParticipantIndex(part.getName());
    Vector<ActionTypeParticipant> parts = 
    	actTblMod.getActionTypeInFocus().getAllParticipants();
    if (partIndex < (parts.size() - 1)) { // last list element wasn't chosen
      int position = actTblMod.getActionTypeInFocus().
      	temporarilyRemoveParticipant(part.getName());
      
      // move down:
      actTblMod.getActionTypeInFocus().addParticipant(part, (position + 1)); 
      ((ModelBuilderGUI)mainGUI).setFileModSinceLastSave();
      actTblMod.refreshData();
      actionTable.setRowSelectionInterval((position + 1), (position + 1));
    }
  }
  
  // Moves an action up in the list
  private void moveActionUp(ActionType act) {
    int actIndex = actions.getIndexOf(act);
    if (actIndex > 0) { // first element wasn't chosen
      int position = actions.removeActionType(act.getName());
      actions.addActionType(act, (position - 1)); // move up
      updateDefinedActionsList();
      setActionInFocus(act);
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    }
  }
  
  // Moves an action down in the list
  private void moveActionDown(ActionType act) {
    int actIndex = actions.getIndexOf(act);
    if (actIndex < (actions.getAllActionTypes().size() - 1)) { // last element
			 																												 // wasn't chosen
      int position = actions.removeActionType(act.getName());
      actions.addActionType(act, (position + 1)); // move down
      updateDefinedActionsList();
      setActionInFocus(act);
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    }
  }
}