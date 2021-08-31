/* This class defines the GUI for building start states with */

package simse.modelbuilder.startstatebuilder;

import simse.modelbuilder.ModelBuilderGUI;
import simse.modelbuilder.WarningListPane;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

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
import javax.swing.JComboBox;
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

public class StartStateBuilderGUI extends JPanel implements ActionListener,
    ListSelectionListener, MouseListener {
  private JFrame mainGUI;
  private DefinedObjectTypes objectTypes; // defined object types that can be
                                          // instantiated as objects
  private CreatedObjects objects; // data structure for holding all of the SimSE
                                  // objects that are being instantiated/created
  private StartStateFileManipulator ssFileManip; // for generating/loading start
                                                 // state files

  private JLabel createNewObjLabel;
  private JComboBox createObjectList; // drop-down list of objects to create
  private JButton okCreateObjectButton; // button to "okay" choosing a new
                                        // object to create
  private JLabel attributeTableTitle; // label for title of attribute table
  private JScrollPane attributeTablePane; // pane for attribute table
  private JTable attributeTable; // table for displaying attributes of an object
  private StartStateAttributeTableModel attTblMod; // model for above table
  private JButton editStartingValButton; // button for editing the starting
                                         // value of an attribute
  private JList createdObjectsList; // JList of already created objects
  private JButton moveObjectUpButton;
  private JButton moveObjectDownButton;
  private JButton removeObjectButton; // button for removing an already created
                                      // object
  private AttributeStartingValueForm aInfo; // form for entering the starting
                                            // value of an attribute

  private WarningListPane warningPane;

  public StartStateBuilderGUI(JFrame owner, DefinedObjectTypes objTypes) {
    mainGUI = owner;
    objectTypes = objTypes;
    objects = new CreatedObjects();
    ssFileManip = new StartStateFileManipulator(objectTypes, objects);

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();
    mainPane.setPreferredSize(new Dimension(1024, 650));

    // Create "create object" pane:
    JPanel createObjectPane = new JPanel();
    createNewObjLabel = new JLabel("Create New Object:");
    createObjectPane.add(createNewObjLabel);

    // Create and add "create object list":
    createObjectList = new JComboBox();
    resetCreateObjectList();
    createObjectPane.add(createObjectList);

    // Create and add "ok" button for choosing object to define:
    okCreateObjectButton = new JButton("OK");
    okCreateObjectButton.addActionListener(this);
    createObjectPane.add(okCreateObjectButton);

    // Create attribute table title label and pane:
    JPanel attributeTableTitlePane = new JPanel();
    attributeTableTitle = new JLabel("No object selected");
    attributeTableTitlePane.add(attributeTableTitle);

    // Create "attribute table" pane:
    attTblMod = new StartStateAttributeTableModel();
    attributeTable = new JTable(attTblMod);
    DefaultTableCellRenderer rightAlignRenderer = 
    	new DefaultTableCellRenderer();
		rightAlignRenderer.setHorizontalAlignment(JLabel.RIGHT);
		attributeTable.getColumnModel().getColumn(3).setCellRenderer(
				rightAlignRenderer);
		attributeTable.getColumnModel().getColumn(4).setCellRenderer(
				rightAlignRenderer);
		attributeTable.getColumnModel().getColumn(5).setCellRenderer(
				rightAlignRenderer);
		attributeTable.getColumnModel().getColumn(6).setCellRenderer(
				rightAlignRenderer);
    attributeTable.addMouseListener(this);
    attributeTablePane = new JScrollPane(attributeTable);
    attributeTablePane
        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    attributeTablePane.setPreferredSize(new Dimension(1024, 250));
    setupAttributeTableRenderers();
    setupAttributeTableSelectionListenerStuff();

    // Create attribute button pane and button:
    JPanel attributeButtonPane = new JPanel();
    editStartingValButton = new JButton("Edit Starting Value");
    editStartingValButton.addActionListener(this);
    attributeButtonPane.add(editStartingValButton);
    editStartingValButton.setEnabled(false);

    // Create "created objects" pane:
    JPanel createdObjectsPane = new JPanel();
    createdObjectsPane.add(new JLabel("Objects Already Created:"));

    // Create and add created objects list to a scroll pane:
    createdObjectsList = new JList();
    createdObjectsList.setVisibleRowCount(7); // make 7 items visible at a time
    createdObjectsList.setFixedCellWidth(500);
    createdObjectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    createdObjectsList.addListSelectionListener(this);
    JScrollPane createdObjectsListPane = new JScrollPane(createdObjectsList);
    createdObjectsPane.add(createdObjectsListPane);
    setupCreatedObjectsListSelectionListenerStuff();

    // Create and add "move up", "move down", and "remove" button, and pane:
    Box createdObjectsButtonPane = Box.createVerticalBox();
    moveObjectUpButton = new JButton("Move up     ");
    createdObjectsButtonPane.add(moveObjectUpButton);
    moveObjectUpButton.addActionListener(this);
    moveObjectUpButton.setEnabled(false);
    moveObjectDownButton = new JButton("Move down");
    createdObjectsButtonPane.add(moveObjectDownButton);
    moveObjectDownButton.addActionListener(this);
    moveObjectDownButton.setEnabled(false);
    removeObjectButton = new JButton("Remove      ");
    createdObjectsButtonPane.add(removeObjectButton);
    removeObjectButton.addActionListener(this);
    removeObjectButton.setEnabled(false);
    
    createdObjectsPane.add(createdObjectsButtonPane);

    // Warning list pane:
    warningPane = new WarningListPane();

    // Add panes and separators to main pane:
    mainPane.add(createObjectPane);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator1);
    mainPane.add(attributeTableTitlePane);
    mainPane.add(attributeTablePane);
    mainPane.add(attributeButtonPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator2);
    mainPane.add(createdObjectsPane);
    JSeparator separator3 = new JSeparator();
    separator3.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator3);
    mainPane.add(warningPane);
    add(mainPane);

    setNoOpenFile(); // make it blank to begin with

    validate();
    repaint();
  }

  public void valueChanged(ListSelectionEvent e) {
    if (createdObjectsList.getSelectedIndex() >= 0) { // an item (object) is
                                                    	// selected
      SimSEObject tempObj = objects.getAllObjects().elementAt(
      		createdObjectsList.getSelectedIndex());
      // get the selected object type
      setObjectInFocus(tempObj);
    }
  }

  public void mousePressed(MouseEvent me) {}

  public void mouseReleased(MouseEvent me) {}

  public void mouseEntered(MouseEvent me) {}

  public void mouseExited(MouseEvent me) {}

  public void mouseClicked(MouseEvent me) {
    int clicks = me.getClickCount();

    if (me.getButton() == MouseEvent.BUTTON1 && clicks >= 2) {
      if (attributeTable.getSelectedRow() >= 0) { // a row is selected
        InstantiatedAttribute tempAttr = attTblMod.getObjectInFocus()
            .getAttribute(
                (String) (attTblMod.getValueAt(attributeTable.getSelectedRow(),
                    0)));
        editStartingVal(tempAttr);
        editStartingValButton.setEnabled(false);
      }
    }
  }

  public CreatedObjects getCreatedObjects() {
    return objects;
  }

  /*
   * reloads the start state objects from a temporary file; if resetUI is
   * true, it clears the current selections in the UI
   */
  public void reload(File tempFile, boolean resetUI) {
    // reload:
    Vector<String> warnings = ssFileManip.loadFile(tempFile);
    generateWarnings(warnings);

    // reset UI stuff:
    if (resetUI) {
	    resetCreateObjectList();
	    updateCreatedObjectsList();
	    clearObjectInFocus();
	    attTblMod.refreshData();
	    createNewObjLabel.setEnabled(false);
	    createObjectList.setEnabled(false);
	    okCreateObjectButton.setEnabled(false);
	    Vector<SimSEObjectType> objTypes = objectTypes.getAllObjectTypes();
	    if (objTypes.size() > 0) { // there is at least one object type
	      // make sure there is at least one object type w/ at least one 
	    	// attribute:
	      for (int i = 0; i < objTypes.size(); i++) {
	        SimSEObjectType type = objTypes.elementAt(i);
	        // check if it has at least one attribute:
	        if (type.getAllAttributes().size() > 0) {
	          createNewObjLabel.setEnabled(true);
	          createObjectList.setEnabled(true);
	          okCreateObjectButton.setEnabled(true);
	          break;
	        }
	      }
	    } else {
	      createNewObjLabel.setEnabled(false);
	      createObjectList.setEnabled(false);
	      okCreateObjectButton.setEnabled(false);
	    }
    }
  }

  // displays warnings of errors found during checking for inconsistencies
  private void generateWarnings(Vector<String> warnings) { 
    if (warnings.size() > 0) { // there is at least 1 warning
      warningPane.setWarnings(warnings);
    }
  }

  // handles user actions
  public void actionPerformed(ActionEvent evt) { 
  	// get which component the action came from:
    Object source = evt.getSource(); 
    if (source == okCreateObjectButton) { // User has ok'ed the creation of a 
    																			// new object
      createObject((String) createObjectList.getSelectedItem());
    } else if (source == editStartingValButton) {
      if (attributeTable.getSelectedRow() >= 0) { // a row is selected
        InstantiatedAttribute tempAttr = attTblMod.getObjectInFocus()
            .getAttribute(
                (String) (attTblMod.getValueAt(attributeTable.getSelectedRow(),
                    0)));
        editStartingVal(tempAttr);
        editStartingValButton.setEnabled(false);
      }
    } else if (source == moveObjectUpButton) {
      if (createdObjectsList.getSelectedIndex() >= 0) { // an item (object) is
                                                      	// selected
        SimSEObject tempObj = objects.getAllObjects().elementAt(
        		createdObjectsList.getSelectedIndex());
        moveObjectUp(tempObj);
      }
    } else if (source == moveObjectDownButton) {
      if (createdObjectsList.getSelectedIndex() >= 0) { // an item (object) is
                                                      	// selected
        SimSEObject tempObj = (SimSEObject) (objects.getAllObjects()
            .elementAt(createdObjectsList.getSelectedIndex()));
        moveObjectDown(tempObj);
      }
    } else if (source == removeObjectButton) {
      if (createdObjectsList.getSelectedIndex() >= 0) { // an item (object) is
                                                      	// selected
        SimSEObject tempObj = objects.getAllObjects().elementAt(
        		createdObjectsList.getSelectedIndex());
        // get the selected object
        removeObject(tempObj);
      }
    }
  }

  /*
   * creates a new SimSE object of the type based on selectedItem and adds it to
   * the data structure
   */
  private void createObject(String selectedItem) { 
    int type = 0;
    String name = new String();
    if (selectedItem.endsWith(SimSEObjectTypeTypes
        .getText(SimSEObjectTypeTypes.EMPLOYEE))) { // employee object type
      type = SimSEObjectTypeTypes.EMPLOYEE;
      name = selectedItem.substring(0, (selectedItem.length()
          - SimSEObjectTypeTypes.getText(SimSEObjectTypeTypes.EMPLOYEE)
              .length() - 1));
      // parse object name from selected item
    } else if (selectedItem.endsWith(SimSEObjectTypeTypes
        .getText(SimSEObjectTypeTypes.ARTIFACT))) { // artifact object type
      type = SimSEObjectTypeTypes.ARTIFACT;
      name = selectedItem.substring(0, (selectedItem.length()
          - SimSEObjectTypeTypes.getText(SimSEObjectTypeTypes.ARTIFACT)
              .length() - 1));
      // parse object name from selected item
    } else if (selectedItem.endsWith(SimSEObjectTypeTypes
        .getText(SimSEObjectTypeTypes.TOOL))) { // tool object type
      type = SimSEObjectTypeTypes.TOOL;
      name = selectedItem.substring(0,
          (selectedItem.length()
              - SimSEObjectTypeTypes.getText(SimSEObjectTypeTypes.TOOL)
                  .length() - 1));
      // parse object name from selected item
    } else if (selectedItem.endsWith(SimSEObjectTypeTypes
        .getText(SimSEObjectTypeTypes.PROJECT))) { // project object type
      type = SimSEObjectTypeTypes.PROJECT;
      name = selectedItem.substring(0,
          (selectedItem.length()
              - SimSEObjectTypeTypes.getText(SimSEObjectTypeTypes.PROJECT)
                  .length() - 1));
      // parse object name from selected item
    } else if (selectedItem.endsWith(SimSEObjectTypeTypes
        .getText(SimSEObjectTypeTypes.CUSTOMER))) { // customer object type
      type = SimSEObjectTypeTypes.CUSTOMER;
      name = selectedItem.substring(0, (selectedItem.length()
          - SimSEObjectTypeTypes.getText(SimSEObjectTypeTypes.CUSTOMER)
              .length() - 1));
      // parse object name from selected item
    }

    SimSEObjectType objType = objectTypes.getObjectType(type, name);
    // create new object:
    SimSEObject newObj = new SimSEObject(objectTypes.getObjectType(type, name)); 
    InstantiatedAttribute instAtt = new InstantiatedAttribute(objType.getKey());
    aInfo = new AttributeStartingValueForm(mainGUI, newObj, instAtt,
    		("Enter a starting value for this " + selectedItem +
    				"'s key attribute:"), objects, attTblMod);

    // Makes it so attribute starting val form window holds focus exclusively:
    WindowFocusListener l = new WindowFocusListener() {
      public void windowLostFocus(WindowEvent ev) {
        aInfo.requestFocus();
        if (attTblMod.getObjectInFocus() != null) {
        	// set newly created object to be the focus of the GUI:
          setObjectInFocus(attTblMod.getObjectInFocus()); 
        }
        updateCreatedObjectsList();
        ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
        editStartingValButton.setEnabled(false);
      }

      public void windowGainedFocus(WindowEvent ev) {}
    };
    aInfo.addWindowFocusListener(l);
  }

  // allows user to edit starting val of this attribute
  private void editStartingVal(InstantiatedAttribute att) { 
    aInfo = new AttributeStartingValueForm(mainGUI, attTblMod
        .getObjectInFocus(), att, ("Edit starting value:"), objects, attTblMod);

    // Makes it so attribute starting val form window holds focus exclusively:
    WindowFocusListener l = new WindowFocusListener() {
      public void windowLostFocus(WindowEvent ev) {
        aInfo.requestFocus();
        if (attTblMod.getObjectInFocus() != null) {
        	// set newly created object to be the focus of the GUI:
          setObjectInFocus(attTblMod.getObjectInFocus()); 
        }
        updateCreatedObjectsList();
        ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
        editStartingValButton.setEnabled(false);
      }

      public void windowGainedFocus(WindowEvent ev) {
      }
    };
    aInfo.addWindowFocusListener(l);
  }

  // sets the given object as the focus of this GUI
  private void setObjectInFocus(SimSEObject newObj) {
  	// set the focus of attribute table to new object:
    attTblMod.setObjectInFocus(newObj); 
    String keyVal = new String();
    if (newObj.getKey().isInstantiated()) {
      keyVal = newObj.getKey().getValue().toString();
    }
    // change title of table to reflect new object:
    attributeTableTitle.setText((newObj.getName()) + " "
        + (SimSEObjectTypeTypes.getText(newObj.getSimSEObjectType().getType()))
        + " " + keyVal + " Attributes:"); 
    // disable buttons:
    editStartingValButton.setEnabled(false);
    
    // set focus on created objects list:
    createdObjectsList.setSelectedIndex(objects.getIndexOf(newObj));
  }

  private void setupAttributeTableRenderers() {
    // Set selction mode to only one row at a time:
    attributeTable.getSelectionModel().setSelectionMode(
        ListSelectionModel.SINGLE_SELECTION);
  }

  // enables edit starting val button whenever a row (attribute) is selected
  private void setupAttributeTableSelectionListenerStuff() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = attributeTable.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          editStartingValButton.setEnabled(true);
        }
      }
    });
  }

  // enables buttons whenever a list item (object) is selected
  private void setupCreatedObjectsListSelectionListenerStuff() {
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = createdObjectsList.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          createdObjectsList.ensureIndexIsVisible(
              createdObjectsList.getSelectedIndex());
          moveObjectUpButton.setEnabled(true);
          moveObjectDownButton.setEnabled(true);
          removeObjectButton.setEnabled(true);
        }
      }
    });
  }

  private void updateCreatedObjectsList() {
    Vector<String> objectNamesTypesAndKeys = new Vector<String>();
    Vector<SimSEObject> currentObjs = objects.getAllObjects();
    for (int i = 0; i < currentObjs.size(); i++) {
      SimSEObject tempObj = currentObjs.elementAt(i);
      String name = tempObj.getName();
      String type = SimSEObjectTypeTypes.getText(tempObj.getSimSEObjectType()
          .getType());
      String keyVal = new String();
      if (tempObj.getKey().getValue() != null) { // has a key value
        keyVal = tempObj.getKey().getValue().toString();
      }
      objectNamesTypesAndKeys.add(name + " " + type + " " + keyVal);
    }
    createdObjectsList.setListData(objectNamesTypesAndKeys);
  }

  // removes this object from the data structure
  private void removeObject(SimSEObject obj) { 
    String keyVal = new String();
    if (obj.getKey().isInstantiated()) {
      keyVal = obj.getKey().getValue().toString();
    }
    int choice = JOptionPane.showConfirmDialog(null, ("Really remove "
        + obj.getName() + " "
        + (SimSEObjectTypeTypes.getText(obj.getSimSEObjectType().getType()))
        + " " + keyVal + " object?"), "Confirm Object Removal",
        JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
      if ((attTblMod.getObjectInFocus() != null)
          && (attTblMod.getObjectInFocus().getName() == obj.getName())
          && (attTblMod.getObjectInFocus().getSimSEObjectType().getType() == obj
              .getSimSEObjectType().getType())) { // removing object currently 
      																						// in focus
        clearObjectInFocus(); // set it so that there's no object in focus
      }
      objects.removeObject(obj.getSimSEObjectType().getType(), obj.getName(),
          obj.getKey().getValue());
      moveObjectUpButton.setEnabled(false);
      moveObjectDownButton.setEnabled(false);
      removeObjectButton.setEnabled(false);
      updateCreatedObjectsList();
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    } 
  }

  // clears the GUI so that it doesn't have an object in focus
  private void clearObjectInFocus() { 
    attTblMod.clearObjectInFocus(); // clear the attribute table
    attributeTableTitle.setText("No object selected");
    // disable button:
    editStartingValButton.setEnabled(false);
  }

  public void setNewOpenFile(File f) {
    clearObjectInFocus();
    objects.clearAll();
    resetCreateObjectList();
    updateCreatedObjectsList();
    warningPane.clearWarnings();
    if (f.exists()) { // file has been saved before
      reload(f, true);
    }
  }

  // makes it so there's not open file in the GUI
  public void setNoOpenFile() { 
    clearObjectInFocus();
    objects.clearAll();
    resetCreateObjectList();
    updateCreatedObjectsList();
    // disable UI components:
    createNewObjLabel.setEnabled(false);
    createObjectList.setEnabled(false);
    okCreateObjectButton.setEnabled(false);
    warningPane.clearWarnings();
  }

  private void resetCreateObjectList() {
    createObjectList.removeAllItems(); // clear list
    Vector<SimSEObjectType> types = objectTypes.getAllObjectTypes();
    for (int i = 0; i < types.size(); i++) { 
    	// go through all of the defined object types, and add its name and type 
    	// to the list IF it has at least one attribute:
      SimSEObjectType tempType = types.elementAt(i);
      if (tempType.getAllAttributes().size() > 0) {
        String tempStr = new String(tempType.getName() + " "
            + SimSEObjectTypeTypes.getText(tempType.getType()));
        createObjectList.addItem(tempStr);
      }
    }
  }
  
  /*
   * Moves an object up in the list
   */
  private void moveObjectUp(SimSEObject obj) {
    int objIndex = objects.getIndexOf(obj);
    if (objIndex > 0) { // first element wasn't chosen
      int position = objects.removeObject(obj.getSimSEObjectType().getType(),
          obj.getSimSEObjectType().getName(), obj.getKey().getValue());
      objects.addObject(obj, (position - 1)); // move up
      updateCreatedObjectsList();
      setObjectInFocus(obj);
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    }
  }
  
  /*
   * Moves an object down in the list
   */
  private void moveObjectDown(SimSEObject obj) {
    int objIndex = objects.getIndexOf(obj);
    if (objIndex < (objects.getAllObjects().size() - 1)) { // last element
			 // wasn't chosen
      int position = objects.removeObject(obj.getSimSEObjectType().getType(),
          obj.getSimSEObjectType().getName(), obj.getKey().getValue());
      objects.addObject(obj, (position + 1)); // move down
      updateCreatedObjectsList();
      setObjectInFocus(obj);
      ((ModelBuilderGUI) mainGUI).setFileModSinceLastSave();
    }
  }
}