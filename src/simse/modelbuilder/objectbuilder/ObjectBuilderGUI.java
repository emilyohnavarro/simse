/* This class defines the GUI for building objects with */

package simse.modelbuilder.objectbuilder;

import simse.modelbuilder.ModelBuilderGUI;
import simse.modelbuilder.WarningListPane;

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

public class ObjectBuilderGUI extends JPanel implements ActionListener,
    ListSelectionListener, MouseListener {
  private ModelBuilderGUI mainGUI;
  private DefinedObjectTypes objects; // data structure for holding all of the
                                      // created SimSE object types
  private ObjectFileManipulator fileManip; // for generating/loading files
  private JComboBox defineObjectList; // drop-down list of objects to define
  private JButton okDefineObjectButton; // button to "okay" choosing a new
                                        // object to define
  private JLabel attributeTableTitle; // label for title of attribute table
  private JScrollPane attributeTablePane; // pane for attribute table
  private JTable attributeTable; // table for displaying attributes of an object
  private ObjectBuilderAttributeTableModel attTblMod; // model for above table
  private JButton addNewAttributeButton; // button for adding a new attribute
  private JButton editAttributeButton; // button for editing an attribute
  private JButton removeAttributeButton; // button for deleting an attribute
  private JButton moveAttUpButton; // for moving an attribute up
  private JButton moveAttDownButton; // for moving an attribute down
  private JList definedObjectsList; // JList of already defined objects
  private JButton moveObjUpButton; // for moving an object type up
  private JButton moveObjDownButton; // for moving an object type down

  private JButton removeObjectButton; // button for removing an already defined
                                      // object
  private JButton renameObjectButton; // button for renaming an existing object
  private AttributeInfoForm aInfo; // form for entering attribute info

  //private JCheckBox allowHireAndFireCheckBox;
  private WarningListPane warningPane;

  public ObjectBuilderGUI(ModelBuilderGUI owner) {
    mainGUI = owner;
    objects = new DefinedObjectTypes();
    fileManip = new ObjectFileManipulator(objects);

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();
    mainPane.setPreferredSize(new Dimension(1024, 650));

    // Create "define object" pane:
    JPanel defineObjectPane = new JPanel();

//    allowHireAndFireCheckBox = new JCheckBox(
//        "Allow Hiring and Firing of Employees");
//    allowHireAndFireCheckBox.setEnabled(false);
//    allowHireAndFireCheckBox.addActionListener(this);
//    defineObjectPane.add(allowHireAndFireCheckBox);

    defineObjectPane.add(new JLabel("Define New Object Type:"));

    // Create and add "define object list":
    defineObjectList = new JComboBox();
    defineObjectList.addItem("Employee");
    defineObjectList.addItem("Artifact");
    defineObjectList.addItem("Tool");
    defineObjectList.addItem("Project");
    defineObjectList.addItem("Customer");
    defineObjectPane.add(defineObjectList);

    // Create and add "ok" button for choosing object to define:
    okDefineObjectButton = new JButton("OK");
    okDefineObjectButton.addActionListener(this);
    defineObjectPane.add(okDefineObjectButton);

    // Create attribute table title label and pane:
    JPanel attributeTableTitlePane = new JPanel();
    attributeTableTitle = new JLabel("No object type selected");
    attributeTableTitlePane.add(attributeTableTitle);

    // Create "attribute table" pane:
    attTblMod = new ObjectBuilderAttributeTableModel();
    attributeTable = new JTable(attTblMod);
    DefaultTableCellRenderer rightAlignRenderer = 
    	new DefaultTableCellRenderer();
		rightAlignRenderer.setHorizontalAlignment(JLabel.RIGHT);
		attributeTable.getColumnModel().getColumn(3).setCellRenderer(
				rightAlignRenderer);
		attributeTable.getColumnModel().getColumn(4).setCellRenderer(
				rightAlignRenderer);
		attributeTable.getColumnModel().getColumn(7).setCellRenderer(
				rightAlignRenderer);
		attributeTable.getColumnModel().getColumn(8).setCellRenderer(
				rightAlignRenderer);
    attributeTable.addMouseListener(this);
    attributeTablePane = new JScrollPane(attributeTable);
    attributeTablePane
        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    attributeTablePane.setPreferredSize(new Dimension(900, 350));
    setupAttributeTableRenderers();
    setupAttributeTableSelectionListenerStuff();

    // Create attribute button pane and buttons:
    JPanel attributeButtonPane = new JPanel();
    addNewAttributeButton = new JButton("Add New Attribute");
    addNewAttributeButton.addActionListener(this);
    editAttributeButton = new JButton("Edit Attribute");
    editAttributeButton.addActionListener(this);
    removeAttributeButton = new JButton("Remove Attribute");
    removeAttributeButton.addActionListener(this);
    moveAttUpButton = new JButton("Move Up");
    moveAttUpButton.addActionListener(this);
    moveAttDownButton = new JButton("Move Down");
    moveAttDownButton.addActionListener(this);
    attributeButtonPane.add(addNewAttributeButton);
    attributeButtonPane.add(editAttributeButton);
    attributeButtonPane.add(removeAttributeButton);
    attributeButtonPane.add(moveAttUpButton);
    attributeButtonPane.add(moveAttDownButton);
    addNewAttributeButton.setEnabled(false);
    editAttributeButton.setEnabled(false);
    removeAttributeButton.setEnabled(false);
    moveAttUpButton.setEnabled(false);
    moveAttDownButton.setEnabled(false);

    // Create "defined objects" pane:
    JPanel definedObjectsPane = new JPanel();
    definedObjectsPane.add(new JLabel("Object Types Already Defined:"));
    definedObjectsPane.setMinimumSize(new Dimension(1024, 210));

    // Create and add "already defined" objects list to a scroll pane:
    definedObjectsList = new JList();
    definedObjectsList.setVisibleRowCount(10); // make 10 items visible at a
                                               // time
    definedObjectsList.setFixedCellWidth(500);
    definedObjectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    definedObjectsList.addListSelectionListener(this);
    JScrollPane definedObjectsListPane = new JScrollPane(definedObjectsList);
    definedObjectsPane.add(definedObjectsListPane);
    updateDefinedObjectsList();
    setupDefinedObjectsListSelectionListenerStuff();

    // Create buttons for this pane:
    Box definedObjectsButtonPane = Box.createVerticalBox();
    moveObjUpButton = new JButton("Move up     ");
    definedObjectsButtonPane.add(moveObjUpButton);
    moveObjUpButton.addActionListener(this);
    moveObjUpButton.setEnabled(false);
    
    moveObjDownButton = new JButton("Move down");
    definedObjectsButtonPane.add(moveObjDownButton);
    moveObjDownButton.addActionListener(this);
    moveObjDownButton.setEnabled(false);
    
    renameObjectButton = new JButton("Rename      ");
    definedObjectsButtonPane.add(renameObjectButton);
    renameObjectButton.addActionListener(this);
    renameObjectButton.setEnabled(false);

    removeObjectButton = new JButton("Remove      ");
    definedObjectsButtonPane.add(removeObjectButton);
    removeObjectButton.addActionListener(this);
    removeObjectButton.setEnabled(false);
    definedObjectsPane.add(definedObjectsButtonPane);

    // Add panes and separators to main pane:
    mainPane.add(defineObjectPane);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator1);
    mainPane.add(attributeTableTitlePane);
    mainPane.add(attributeTablePane);
    mainPane.add(attributeButtonPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator2);
    mainPane.add(definedObjectsPane);
    JSeparator separator3 = new JSeparator();
    separator3.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator3);
    warningPane = new WarningListPane();
    mainPane.add(warningPane);
    add(mainPane);

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
      if (attributeTable.getSelectedRow() >= 0) { // a row is selected
        Attribute tempAttr = attTblMod.getObjectInFocus()
            .getAttribute(
                (String) (attTblMod.getValueAt(attributeTable.getSelectedRow(),
                    0)));
        editAttribute(tempAttr);
        editAttributeButton.setEnabled(false);
        removeAttributeButton.setEnabled(false);
        moveAttUpButton.setEnabled(false);
        moveAttDownButton.setEnabled(false);
      }
    }
  }

  public void valueChanged(ListSelectionEvent e) {
    if (definedObjectsList.getSelectedIndex() >= 0) { // an item (object) is
                                                    	// selected
      SimSEObjectType tempObj = 
      	objects.getAllObjectTypes().elementAt(
      			definedObjectsList.getSelectedIndex());
      // get the selected object type
      setObjectInFocus(tempObj);
    }
  }

  public DefinedObjectTypes getDefinedObjectTypes() {
    return objects;
  }

  public boolean allowHireFire() {
//    return allowHireAndFireCheckBox != null
//        && allowHireAndFireCheckBox.isSelected();
  	return false;
  }

  // handles user actions
  public void actionPerformed(ActionEvent evt) { 
    Object source = evt.getSource(); // get which component the action came from
    if (source == okDefineObjectButton) { // User has ok'ed the creation of a 
    																			// new object
      createObject((String) defineObjectList.getSelectedItem());
    } else if (source == addNewAttributeButton) { // user has requested to add a
    																							// new attribute
      addNewAttribute();
      editAttributeButton.setEnabled(false);
      removeAttributeButton.setEnabled(false);
      moveAttUpButton.setEnabled(false);
      moveAttDownButton.setEnabled(false);
    } else if (source == editAttributeButton) {
      if (attributeTable.getSelectedRow() >= 0) { // a row is selected
        Attribute tempAttr = attTblMod.getObjectInFocus()
            .getAttribute(
                (String) (attTblMod.getValueAt(attributeTable.getSelectedRow(),
                    0)));
        editAttribute(tempAttr);
        editAttributeButton.setEnabled(false);
        removeAttributeButton.setEnabled(false);
        moveAttUpButton.setEnabled(false);
        moveAttDownButton.setEnabled(false);
      }
    } else if (source == removeAttributeButton) {
      if (attributeTable.getSelectedRow() >= 0) { // a row is selected
        Attribute tempAttr = attTblMod.getObjectInFocus()
            .getAttribute(
                (String) (attTblMod.getValueAt(attributeTable.getSelectedRow(),
                    0)));
        removeAttribute(tempAttr);
      }
    } else if (source == moveAttDownButton) {
      if (attributeTable.getSelectedRow() >= 0) { // a row is selected
        Attribute tempAttr = attTblMod.getObjectInFocus()
        .getAttribute(
            (String) (attTblMod.getValueAt(attributeTable.getSelectedRow(),
                0)));
        moveAttributeDown(tempAttr);
      }
    } else if (source == moveAttUpButton) {
      if (attributeTable.getSelectedRow() >= 0) { // a row is selected
        Attribute tempAttr = attTblMod.getObjectInFocus()
        .getAttribute(
            (String) (attTblMod.getValueAt(attributeTable.getSelectedRow(),
                0)));
        moveAttributeUp(tempAttr);
      }
    } else if (source == moveObjDownButton) {
      if (!definedObjectsList.getSelectionModel().isSelectionEmpty()) {
        SimSEObjectType tempObj = 
        	objects.getAllObjectTypes().elementAt(
        			definedObjectsList.getSelectedIndex());
        moveObjectDown(tempObj);
      }
    } else if (source == moveObjUpButton) {
      if (!definedObjectsList.getSelectionModel().isSelectionEmpty()) {
        SimSEObjectType tempObj = 
        	objects.getAllObjectTypes().elementAt(
        			definedObjectsList.getSelectedIndex());
        moveObjectUp(tempObj);
      }
    } else if (source == removeObjectButton) {
      if (definedObjectsList.getSelectedIndex() >= 0) { // an item (object) is
                                                      	// selected
        SimSEObjectType tempObj = 
        	objects.getAllObjectTypes().elementAt(
        			definedObjectsList.getSelectedIndex());
        // get the selected object
        removeObject(tempObj);
      }
    } else if (source == renameObjectButton) {
      if (definedObjectsList.getSelectedIndex() >= 0) {
        SimSEObjectType tempObj = objects.getAllObjectTypes().elementAt(
        		definedObjectsList.getSelectedIndex());
        renameObject(tempObj);
      }
    }

//    else if (source == allowHireAndFireCheckBox) {
//      boolean warningAdded = false;
//      Vector warnings = new Vector();
//
//      if (allowHireAndFireCheckBox.isSelected()) {
//        Vector objs = objects.getAllObjectTypes();
//        for (int i = 0; i < objs.size(); i++) {
//          SimSEObjectType tmpObj = (SimSEObjectType) objs.get(i);
//          // find all the employee types and remove the hired attribute
//          if (tmpObj.getType() == SimSEObjectTypeTypes.EMPLOYEE) {
//            boolean found = false;
//            Vector attribs = tmpObj.getAllAttributes();
//            for (int j = 0; j < attribs.size(); j++) {
//              Attribute at = (Attribute) attribs.get(j);
//
//              if (at.getName().equalsIgnoreCase("Hired"))
//                found = true;
//            }
//            if (!found) {
//              if (!warningAdded) {
//                warnings
//                    .add("Employees will not appear in game unless their Hired attribute is assigned a value");
//                warningAdded = true;
//              }
//
//              NonNumericalAttribute a = new NonNumericalAttribute("Hired",
//                  AttributeTypes.BOOLEAN, true, attribs.size() == 0, true);
//              tmpObj.addAttribute(a);
//            }
//          }
//        }
//
//        generateWarnings(warnings);
//
//      } else // unchecking of hire and fire, confirm it
//      {
//        int choice = JOptionPane
//            .showConfirmDialog(
//                null,
//                ("By unchecking this option, All Employee Objects will lose their \"hired\" attribute.  Do you wish to Continue? "),
//                "Confirm Attribute Removal", JOptionPane.YES_NO_OPTION);
//        if (choice == JOptionPane.YES_OPTION) {
//          Vector objs = objects.getAllObjectTypes();
//          for (int i = 0; i < objs.size(); i++) {
//            SimSEObjectType tmpObj = (SimSEObjectType) objs.get(i);
//
//            // find all the employee types and remove the hired attribute
//            if (tmpObj.getType() == SimSEObjectTypeTypes.EMPLOYEE) {
//              Vector attribs = tmpObj.getAllAttributes();
//              for (int j = 0; j < attribs.size(); j++) {
//                Attribute at = (Attribute) attribs.get(j);
//
//                if (at.getName().equalsIgnoreCase("Hired")) {
//                  tmpObj.removeAttribute(at.getName());
//
//                  if (at.isKey()) {
//                    if (attribs.size() >= 1) {
//                      //set key
//                      at = (Attribute) attribs.get(0);
//                      at.setKey(true);
//
//                      warnings.add("Key removed for "
//                          + SimSEObjectTypeTypes.getText(tmpObj.getType())
//                          + " " + tmpObj.getName()
//                          + ": added to next available Attribute "
//                          + at.getName());
//                    } else {
//                      warnings.add(SimSEObjectTypeTypes.getText(tmpObj
//                          .getType())
//                          + " " + tmpObj.getName() + " has no key attribute");
//                    }
//                  }
//                }
//              }
//            }
//          }
//
//          generateWarnings(warnings);
//        }
//      }
//
//      // refresh the data
//      if (attTblMod.getObjectInFocus() != null)
//        attTblMod.refreshData();
//    }
  }

  private void renameObject(SimSEObjectType obj) {
  	// Show input dialog:
    String response = JOptionPane.showInputDialog(null, "Enter new name for "
        + obj.getName(), "Rename Object Type", JOptionPane.QUESTION_MESSAGE); 
    if (response != null) {
      if (objectNameInputValid(response) == false) { // input is invalid
        JOptionPane
            .showMessageDialog(
                null,
                "Please enter a unique name, between 2 and 40 alphabetic " +
                "characters, and no spaces",
                "Invalid Input", JOptionPane.WARNING_MESSAGE); // warn user to
                                                               // enter a valid
                                                               // number
        renameObject(obj); // try again
      } else { // user has entered valid input
        obj.setName(response);
        updateDefinedObjectsList();
        setObjectInFocus(obj); // set newly created object to be the focus of
        // the GUI
        mainGUI.setFileModSinceLastSave();
      }
    }
  }

  /*
   * creates a new SimSEObjectType of the specified type and adds it to the
   * data structure
   */
  private void createObject(String selectedItem) { 
    if ((attTblMod.getObjectInFocus() != null)
        && (attTblMod.getObjectInFocus().getNumAttributes() == 0)) { 
    	// current object in focus doesn't have any attributes
      JOptionPane.showMessageDialog(null,
          "You must add at least one attribute to this "
              + attTblMod.getObjectInFocus().getName()
              + " "
              + SimSEObjectTypeTypes.getText(attTblMod.getObjectInFocus()
                  .getType()) + " before continuing.", "Warning",
          JOptionPane.ERROR_MESSAGE);
    } else {
      String response = JOptionPane.showInputDialog(null,
          "Enter the name for this " + selectedItem + " type:",
          "Enter Object Type Name", JOptionPane.QUESTION_MESSAGE); // Show input
                                                                   // dialog
      if (response != null) {
        if (objectNameInputValid(response) == false) { // input is invalid
          JOptionPane
              .showMessageDialog(
                  null,
                  "Please enter a unique name, between 2 and 40 alphabetic " +
                  "characters, and no spaces",
                  "Invalid Input", JOptionPane.WARNING_MESSAGE); // warn user to
                                                                 // enter a
                                                                 // valid number
          createObject(selectedItem); // try again
        } else { // user has entered valid input
          SimSEObjectType newObj = new SimSEObjectType((SimSEObjectTypeTypes
              .getIntRepresentation(selectedItem)), response); // create new
                                                               // object

          // adds the hired attribute to employees,
          if ((SimSEObjectTypeTypes.getIntRepresentation(selectedItem) == 
          	SimSEObjectTypeTypes.EMPLOYEE)
              && allowHireFire()) {
            Vector<Attribute> attribs = newObj.getAllAttributes();
            NonNumericalAttribute a = new NonNumericalAttribute("Hired",
                AttributeTypes.BOOLEAN, true, attribs.size() == 0, true);
            newObj.addAttribute(a);
          }

          objects.addObjectType(newObj); // add new object type to the data
                                         // structure
          mainGUI.setFileModSinceLastSave();

          setObjectInFocus(newObj); // set newly created object to be the focus
                                    // of the GUI
          updateDefinedObjectsList();
        }
      }
    }
  }

  // returns true if input is a valid object name, false otherwise
  private boolean objectNameInputValid(String input) { 
    if ((input.equalsIgnoreCase(SimSEObjectTypeTypes
        .getText(SimSEObjectTypeTypes.EMPLOYEE)))
        || (input.equalsIgnoreCase(SimSEObjectTypeTypes
            .getText(SimSEObjectTypeTypes.ARTIFACT)))
        || (input.equalsIgnoreCase(SimSEObjectTypeTypes
            .getText(SimSEObjectTypeTypes.TOOL)))
        || (input.equalsIgnoreCase(SimSEObjectTypeTypes
            .getText(SimSEObjectTypeTypes.CUSTOMER)))
        || (input.equalsIgnoreCase(SimSEObjectTypeTypes
            .getText(SimSEObjectTypeTypes.PROJECT)))
        || (input.equalsIgnoreCase("action"))
        || (input.equalsIgnoreCase("compiler"))) { // System-wide keywords
      return false;
    }

    char[] cArray = input.toCharArray();

    // Check for length constraints:
    if ((cArray.length < 2) || (cArray.length > 40)) { // user has entered a
                                                     	 // string shorter than 2
                                                     	 // chars or longer than 
    																									 // 40 chars
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
    Vector<SimSEObjectType> existingObjects = objects.getAllObjectTypes();
    for (int i = 0; i < existingObjects.size(); i++) {
      SimSEObjectType tempObj = existingObjects.elementAt(i);
      if (tempObj.getName().equalsIgnoreCase(input)) { // name entered is not
                                                     	 // unique (there is 
      																								 // already another object
      																								 // defined with the same 
      																								 // name (hence, invalid))
        return false;
      }
    }
    return true; // none of the invalid conditions exist
  }

  private void addNewAttribute() {
    aInfo = new AttributeInfoForm(mainGUI, attTblMod.getObjectInFocus(), null);
    // Makes it so attribute info form window holds focus exclusively:
    WindowFocusListener l = new WindowFocusListener() {
      public void windowLostFocus(WindowEvent ev) {
        aInfo.requestFocus();
        attTblMod.refreshData();
        editAttributeButton.setEnabled(false);
        removeAttributeButton.setEnabled(false);
        moveAttUpButton.setEnabled(false);
        moveAttDownButton.setEnabled(false);
      }

      public void windowGainedFocus(WindowEvent ev) {}
    };
    aInfo.addWindowFocusListener(l);
    mainGUI.setFileModSinceLastSave();
  }

  private void editAttribute(Attribute a) {
    aInfo = new AttributeInfoForm(mainGUI, attTblMod.getObjectInFocus(), a);
    // Makes it so attribute info form window holds focus exclusively:
    WindowFocusListener l = new WindowFocusListener() {
      public void windowLostFocus(WindowEvent ev) {
        aInfo.requestFocus();
        attTblMod.refreshData();
      }

      public void windowGainedFocus(WindowEvent ev) {}
    };
    aInfo.addWindowFocusListener(l);
    mainGUI.setFileModSinceLastSave();
  }

  private void removeAttribute(Attribute a) {
    int choice = JOptionPane.showConfirmDialog(null, ("Really remove "
        + a.getName() + " attribute?"), "Confirm Attribute Removal",
        JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
      if (a.isKey()) { // this attribute is the key
        JOptionPane
            .showMessageDialog(
                null,
                "You must first make another attribute the key before you " +
                "can remove this one.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
      } else { // this attribute not the key
        // Remove attribute:
        attTblMod.getObjectInFocus().removeAttribute(a.getName());
        attTblMod.refreshData();
        removeAttributeButton.setEnabled(false);
        editAttributeButton.setEnabled(false);
        moveAttUpButton.setEnabled(false);
        moveAttDownButton.setEnabled(false);
        mainGUI.setFileModSinceLastSave();
        //mainGUI.resetObjectTypes();
      }
    }
  }

  private void setupAttributeTableRenderers() {
    // Set up alignment in columns:
    DefaultTableCellRenderer renderer1 = new DefaultTableCellRenderer();
    renderer1.setHorizontalAlignment(JLabel.RIGHT);
    attributeTable.getColumnModel().getColumn(3).setCellRenderer(renderer1);

    DefaultTableCellRenderer renderer2 = new DefaultTableCellRenderer();
    renderer2.setHorizontalAlignment(JLabel.RIGHT);
    attributeTable.getColumnModel().getColumn(4).setCellRenderer(renderer2);

    // Set selction mode to only one row at a time:
    attributeTable.getSelectionModel().setSelectionMode(
        ListSelectionModel.SINGLE_SELECTION);
  }

  // enables edit and remove buttons whenever a row (attribute) is selected
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
          editAttributeButton.setEnabled(true);
          removeAttributeButton.setEnabled(true);
          moveAttUpButton.setEnabled(true);
          moveAttDownButton.setEnabled(true);
        }
      }
    });
  }

  // enables buttons whenever a list item (object) is selected
  private void setupDefinedObjectsListSelectionListenerStuff() { 
    // Copied from a Java tutorial:
    ListSelectionModel rowSM = definedObjectsList.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting())
          return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty() == false) {
          definedObjectsList.ensureIndexIsVisible(
              definedObjectsList.getSelectedIndex());
          removeObjectButton.setEnabled(true);
          renameObjectButton.setEnabled(true);
          moveObjUpButton.setEnabled(true);
          moveObjDownButton.setEnabled(true);
        }
      }
    });
  }

  private void updateDefinedObjectsList() {
    Vector<String> objectNamesAndTypes = new Vector<String>();
    Vector<SimSEObjectType> currentObjs = objects.getAllObjectTypes();
    for (int i = 0; i < currentObjs.size(); i++) {
      SimSEObjectType tempObj = currentObjs.elementAt(i);
      objectNamesAndTypes.add(new String((tempObj.getName()) + " ("
          + (SimSEObjectTypeTypes.getText(tempObj.getType())) + ")"));
    }
    definedObjectsList.setListData(objectNamesAndTypes);
  }

  // sets the given object as the focus of this GUI
  private void setObjectInFocus(SimSEObjectType newObj) { 
    if ((attTblMod.getObjectInFocus() != null)
        && (attTblMod.getObjectInFocus().getNumAttributes() == 0)) { 
    	// current object in focus doesn't have any attributes
      JOptionPane.showMessageDialog(null,
          "You must add at least one attribute to this "
              + attTblMod.getObjectInFocus().getName()
              + " "
              + SimSEObjectTypeTypes.getText(attTblMod.getObjectInFocus()
                  .getType()) + " before continuing.", "Warning",
          JOptionPane.ERROR_MESSAGE);
    } else {
      attTblMod.setObjectInFocus(newObj); // set focus of attribute table to new
                                          // object
      // change title of table to reflect new object:
      attributeTableTitle.setText((newObj.getName()) + " "
          + (SimSEObjectTypeTypes.getText(newObj.getType())) + " Attributes:"); 
      // enable buttons:
      addNewAttributeButton.setEnabled(true);
      editAttributeButton.setEnabled(false);
      removeAttributeButton.setEnabled(false);
      moveAttUpButton.setEnabled(false);
      moveAttDownButton.setEnabled(false);
      
      // set focus on defined objects list:
      definedObjectsList.setSelectedIndex(objects.getIndexOf(newObj));
    }
  }

  // removes this object from the data structure
  private void removeObject(SimSEObjectType obj) { 
    int choice = JOptionPane.showConfirmDialog(null,
        ("Really remove " + obj.getName() + " "
            + (SimSEObjectTypeTypes.getText(obj.getType())) + " object type?"),
        "Confirm Object Removal", JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
      if ((attTblMod.getObjectInFocus() != null)
          && (attTblMod.getObjectInFocus().getName() == obj.getName())
          && (attTblMod.getObjectInFocus().getType() == obj.getType())) { 
      	// removing object currently in focus
        clearObjectInFocus(); // set it so that there's no object in focus
      }
      objects.removeObjectType(obj.getType(), obj.getName());
      removeObjectButton.setEnabled(false);
      renameObjectButton.setEnabled(false);
      moveObjUpButton.setEnabled(false);
      moveObjDownButton.setEnabled(false);
      updateDefinedObjectsList();
      mainGUI.setFileModSinceLastSave();
    }
  }

  // clears the GUI so that it doesn't have an object in focus
  private void clearObjectInFocus() { 
    attTblMod.clearObjectInFocus(); // clear the attribute table
    attributeTableTitle.setText("No object type selected");
    // disable button:
    addNewAttributeButton.setEnabled(false);
    editAttributeButton.setEnabled(false);
    removeAttributeButton.setEnabled(false);
    moveAttUpButton.setEnabled(false);
    moveAttDownButton.setEnabled(false);
  }
  
  /*
   * Moves an attribute up in the list
   */
  private void moveAttributeUp(Attribute a) {
    int attIndex = attTblMod.getObjectInFocus().getAttributeIndex(a.getName());
    if (attIndex > 0) { // first list element wasn't chosen
      int position = attTblMod.getObjectInFocus().removeAttribute(a.getName());
      attTblMod.getObjectInFocus().addAttribute(a, (position - 1)); // move up
      mainGUI.setFileModSinceLastSave();
      attTblMod.refreshData();
      attributeTable.setRowSelectionInterval((position - 1), (position - 1));
    }
  }
  
  /*
   * Moves an attribute down in the list
   */
  private void moveAttributeDown(Attribute a) {
    int attIndex = attTblMod.getObjectInFocus().getAttributeIndex(a.getName());
    Vector<Attribute> attributes = 
    	attTblMod.getObjectInFocus().getAllAttributes();
    if (attIndex < (attributes.size() - 1)) { // last list element wasn't chosen
      int position = attTblMod.getObjectInFocus().removeAttribute(a.getName());
      attTblMod.getObjectInFocus().addAttribute(a, (position + 1)); // move up
      mainGUI.setFileModSinceLastSave();
      attTblMod.refreshData();
      attributeTable.setRowSelectionInterval((position + 1), (position + 1));
    }
  }
  
  /*
   * Moves an object up in the list
   */
  private void moveObjectUp(SimSEObjectType obj) {
    int objIndex = objects.getIndexOf(obj);
    if (objIndex > 0) { // first element wasn't chosen
      int position = objects.removeObjectType(obj.getType(), obj.getName());
      objects.addObjectType(obj, (position - 1)); // move up
      updateDefinedObjectsList();
      setObjectInFocus(obj);
      mainGUI.setFileModSinceLastSave();
    }
  }
  
  /*
   * Moves an object down in the list
   */
  private void moveObjectDown(SimSEObjectType obj) {
    int objIndex = objects.getIndexOf(obj);
    if (objIndex < (objects.getAllObjectTypes().size() - 1)) { // last element
      																												 // wasn't chosen
      int position = objects.removeObjectType(obj.getType(), obj.getName());
      objects.addObjectType(obj, (position + 1)); // move down
      updateDefinedObjectsList();
      setObjectInFocus(obj);
      mainGUI.setFileModSinceLastSave();
    }
  }

  public void setNoOpenFile() {
    clearObjectInFocus();
    objects.clearAll();
    updateDefinedObjectsList();
    defineObjectList.setEnabled(false);
    okDefineObjectButton.setEnabled(false);
//    allowHireAndFireCheckBox.setEnabled(false);
  }

  public void setNewOpenFile(File f) {
    clearObjectInFocus();
    objects.clearAll();
//    allowHireAndFireCheckBox.setEnabled(true);
    if (f.exists()) { // file has been saved before
      fileManip.loadFile(f);

//     allowHireAndFireCheckBox.setSelected(fileManip.isAllowHireFireChecked());
    }
    updateDefinedObjectsList();
    defineObjectList.setEnabled(true);
    okDefineObjectButton.setEnabled(true);
  }
}