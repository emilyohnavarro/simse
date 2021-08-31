/*
 * This class defines the window through which the starting value of an
 * attribute can be entered/edited
 */

package simse.modelbuilder.startstatebuilder;

import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.NumericalAttribute;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AttributeStartingValueForm extends JDialog implements
    ActionListener {
  private SimSEObject objectInFocus; // object type whose attributes are being
                                     // edited
  private InstantiatedAttribute attribute; // attribute whose starting value
                                           // being edited
  private CreatedObjects createdObjs; // objects already created
  private StartStateAttributeTableModel attTblMod; // attribute table model that
                                                   // this class must call the
                                                   // refresh function on

  private JTextField startValTextField; // for entering the starting value of
                                        // the attribute (except boolean
                                        // attributes)
  private JComboBox booleanStartValList; // for choosing the starting value of a
                                         // boolean attribute
  private JButton okButton; // for ok'ing the creating/editing of a new
                            // attribute
  private JButton cancelButton; // for canceling the creating/editing of a new
                                // attribute

  public AttributeStartingValueForm(JFrame owner, SimSEObject object,
      InstantiatedAttribute attr, String prompt, CreatedObjects cObjects,
      StartStateAttributeTableModel tableModel) {
    super(owner, true);
    objectInFocus = object;
    attribute = attr;
    createdObjs = cObjects;
    attTblMod = tableModel;

    // Set window title:
    setTitle("Attribute Starting Value - SimSE");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();
    mainPane.setMinimumSize(new Dimension(265, 250));

    // Create prompt panel:
    JPanel promptPane = new JPanel();
    promptPane.add(new JLabel(prompt));
    mainPane.add(promptPane);

    // Create name label pane:
    JPanel nameLabelPane = new JPanel();
    nameLabelPane
        .add(new JLabel("Name: " + attribute.getAttribute().getName()));
    mainPane.add(nameLabelPane);

    // Create type label pane:
    JPanel typeLabelPane = new JPanel();
    typeLabelPane.add(new JLabel("Type: "
        + AttributeTypes.getText(attribute.getAttribute().getType())));
    mainPane.add(typeLabelPane);

    // Create visible label pane:
    JPanel visibleLabelPane = new JPanel();
    if (attribute.getAttribute().isVisible()) {
      visibleLabelPane.add(new JLabel("Visible: Yes"));
    } else {
      visibleLabelPane.add(new JLabel("Visible: No"));
    }
    mainPane.add(visibleLabelPane);

    // Create key label pane:
    JPanel keyLabelPane = new JPanel();
    if (attribute.getAttribute().isKey()) {
      keyLabelPane.add(new JLabel("Key: Yes"));
    } else {
      keyLabelPane.add(new JLabel("Key: No"));
    }
    mainPane.add(keyLabelPane);

    if ((attribute.getAttribute().getType() == AttributeTypes.INTEGER) || 
    		(attribute.getAttribute().getType() == AttributeTypes.DOUBLE)) {
    // numerical attribute
    	NumericalAttribute numAttribute = 
    		(NumericalAttribute)attribute.getAttribute();
      // Create minVal label pane:
      JPanel minValLabelPane = new JPanel();
      if (numAttribute.isMinBoundless()) {
        minValLabelPane.add(new JLabel("Min Value: Boundless"));
      } else { // min is not boundless
        minValLabelPane.add(new JLabel("Min Value: " + 
        		numAttribute.getMinValue()));
      }
      mainPane.add(minValLabelPane);

      // Create maxVal label pane:
      JPanel maxValLabelPane = new JPanel();
      if (numAttribute.isMaxBoundless()) {
        maxValLabelPane.add(new JLabel("Max Value: Boundless"));
      } else { // max is not boundless
        maxValLabelPane.add(new JLabel("Max Value: " + 
        		numAttribute.getMaxValue()));
      }
      mainPane.add(maxValLabelPane);
    }

    // Create startVal pane:
    JPanel startValPane = new JPanel();
    startValPane.add(new JLabel("Starting Value:"));
    startValTextField = new JTextField(10);
    booleanStartValList = new JComboBox();
    booleanStartValList.addItem("True");
    booleanStartValList.addItem("False");
    if (attribute.getAttribute().getType() == AttributeTypes.BOOLEAN) { 
    	// boolean attribute
      startValPane.add(booleanStartValList);
      booleanStartValList.requestFocusInWindow();
    } else { // non-boolean attribute
      startValPane.add(startValTextField);
      startValTextField.requestFocusInWindow();
    }
    mainPane.add(startValPane);

    // Create okCancelButton pane and buttons:
    JPanel okCancelButtonPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    okCancelButtonPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    okCancelButtonPane.add(cancelButton);
    mainPane.add(okCancelButtonPane);

    if ((attribute instanceof InstantiatedAttribute) && 
    		(((InstantiatedAttribute) (attribute)).getValue() != null)) { 
    	// editing an existing attribute
      initializeForm(); // initialize value to reflect attribute being edited
    }

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
    } else if (source == okButton) { // okButton has been pressed
      Vector<String> errors = inputValid(); // check validity of input
      if (errors.size() > 0) { // input not valid
        for (int i = 0; i < errors.size(); i++) {
          JOptionPane.showMessageDialog(null, errors.elementAt(i),
              "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
      } else { // input valid
        if (attribute.getAttribute().isKey() && attribute.getValue() == null) { 
        	// new object
          addKeyAttribute();
        } else { // not a new object
          editAttribute();
        }
      }
    }
  }

  /*
   * returns true if there is no other created object with the same object type
   * as the object in focus and key attribute value as the one that is being
   * passed into this function
   */
  private boolean keyValueIsUnique(Object value) { 
    if ((attribute.getValue() == null) || ((attribute.getValue() != null) && 
    		(attribute.getValue().equals(value) == false))) { // the new value that 
    																											// the user is trying 
    																											// to set this 
    																											// attribute to is
    																											// different than its 
    																											// previous value
    	// get all already created objects:
      Vector<SimSEObject> alreadyCreatedObjs = createdObjs.getAllObjects(); 
      for (int i = 0; i < alreadyCreatedObjs.size(); i++) {
        SimSEObject tempObj = alreadyCreatedObjs.elementAt(i);
        if (tempObj.getKey().isInstantiated()) { // has a key value
          if ((tempObj.getKey().getValue().equals(value))
              && (tempObj.getSimSEObjectType().getName().equals(objectInFocus
                  .getSimSEObjectType().getName()))
              && (tempObj.getSimSEObjectType().getType() == objectInFocus
                  .getSimSEObjectType().getType())) { // found a match
            return false;
          }
        }
      }
    }
    return true;
  }

  private void addKeyAttribute() {
    if (attribute.getAttribute().getType() == AttributeTypes.BOOLEAN) { 
    	// boolean attribute
    	// get value:
      String value = (String) (booleanStartValList.getSelectedItem()); 
      if (value.equals("True")) {
        Boolean newVal = new Boolean(true);
        if (keyValueIsUnique(newVal)) { // no other object of the same object 
        																// type has the same key attribute value
          // add instantiated attribute:
          attribute.setValue(newVal);
          objectInFocus.addAttribute(attribute);
          createdObjs.addObject(objectInFocus);
          attTblMod.setObjectInFocus(objectInFocus);
          attTblMod.refreshData();
          setVisible(false);
          dispose();
        } else {
          showNonUniqueKeyValueWarning();
        }
      } else { // false
        Boolean newVal = new Boolean(false);
        if (keyValueIsUnique(newVal)) { // no other object of the same object 
        																// type has the same key attribute value
          // add instantiated attribute:
          attribute.setValue(newVal);
          objectInFocus.addAttribute(attribute);
          createdObjs.addObject(objectInFocus);
          attTblMod.setObjectInFocus(objectInFocus);
          attTblMod.refreshData();
          setVisible(false);
          dispose();
        } else {
          showNonUniqueKeyValueWarning();
        }
      }
    } else if (attribute.getAttribute().getType() == AttributeTypes.STRING) { 
    	// string attribute
      String newVal = new String(startValTextField.getText());
      if (keyValueIsUnique(newVal)) { // no other object of the same object type
                                    	// has the same key attribute value
        // add instantiated attribute:
        attribute.setValue(newVal);
        objectInFocus.addAttribute(attribute);
        createdObjs.addObject(objectInFocus);
        attTblMod.setObjectInFocus(objectInFocus);
        attTblMod.refreshData();
        setVisible(false);
        dispose();
      } else {
        showNonUniqueKeyValueWarning();
      }
    } else if (attribute.getAttribute().getType() == AttributeTypes.DOUBLE) { 
    	// double attribute
      String value = startValTextField.getText();
      try {
        Double newVal = new Double(Double.parseDouble(value));
        if (keyValueIsUnique(newVal)) { // no other object of the same object 
        																// type has the same key attribute value
          // add instantiated attribute:
          attribute.setValue(newVal);
          objectInFocus.addAttribute(attribute);
          createdObjs.addObject(objectInFocus);
          attTblMod.setObjectInFocus(objectInFocus);
          attTblMod.refreshData();
          setVisible(false);
          dispose();
        } else {
          showNonUniqueKeyValueWarning();
        }
      } catch (NumberFormatException e) {
        System.out.println(e.getMessage());
      }
    } else if (attribute.getAttribute().getType() == AttributeTypes.INTEGER) { 
    	// integer attribute
      String value = startValTextField.getText();
      try {
        Integer newVal = new Integer(Integer.parseInt(value));
        if (keyValueIsUnique(newVal)) { // no other object of the same object 
        																// type has the same key attribute value
          // add instantiated attribute:
          attribute.setValue(newVal);
          objectInFocus.addAttribute(attribute);
          createdObjs.addObject(objectInFocus);
          attTblMod.setObjectInFocus(objectInFocus);
          attTblMod.refreshData();
          setVisible(false);
          dispose();
        } else {
          showNonUniqueKeyValueWarning();
        }
      } catch (NumberFormatException e) {
        System.out.println(e.getMessage());
      }
    }

    if (objectInFocus.getAllAttributes().size() < objectInFocus
        .getSimSEObjectType().getAllAttributes().size()) { // not all of the
    																											 // attributes from 
    																											 // the object's type 
    																											 // have been 
    																											 // initialized and 
    																											 // added to the 
    																											 // object as 
    																											 // instantiated 
    																											 // attributes yet
    	// get all attributes:
      Vector<Attribute> atts = 
      	objectInFocus.getSimSEObjectType().getAllAttributes(); 
      for (int i = 0; i < atts.size(); i++) {
        Attribute a = atts.elementAt(i);
        if (a.isKey() == false) { // this attribute is not the key, which has
                                	// already been added
          objectInFocus.addAttribute(new InstantiatedAttribute(a));
        }
      }
    }
  }

  // sets the starting value of the attribute to the one typed int
  private void editAttribute() { 
    if (attribute.getAttribute().getType() == AttributeTypes.BOOLEAN) { 
    	// boolean attribute
    	// get value:
      String value = (String) (booleanStartValList.getSelectedItem()); 
      Boolean newVal;
      if (value.equals("True")) {
        newVal = new Boolean(true);
      } else { // false
        newVal = new Boolean(false);
      }
      if ((attribute.getAttribute().isKey() && (keyValueIsUnique(newVal))) || 
      		(attribute.getAttribute().isKey() == false)) { // if the attribute is 
      																									 // key it has a unique 
      																									 // value
        // set value of instantiated attribute:
        ((InstantiatedAttribute) (attribute)).setValue(newVal);
        attTblMod.refreshData();
        setVisible(false);
        dispose();
      } else { // non-unique key value
        showNonUniqueKeyValueWarning();
      }
    } else if (attribute.getAttribute().getType() == AttributeTypes.STRING) { 
    	// string attribute
      String newVal = new String(startValTextField.getText());
      if ((attribute.getAttribute().isKey() && (keyValueIsUnique(newVal))) || 
      		!attribute.getAttribute().isKey()) { // if the attribute is key it has
      																				 // a unique value
        // set value of instantiated attribute:
        ((InstantiatedAttribute) (attribute)).setValue(newVal);
        attTblMod.refreshData();
        setVisible(false);
        dispose();
      } else {
        showNonUniqueKeyValueWarning();
      }
    } else if (attribute.getAttribute().getType() == AttributeTypes.DOUBLE) { 
    	// double attribute
      String value = startValTextField.getText();
      try {
        Double newVal = new Double(Double.parseDouble(value));
        if ((attribute.getAttribute().isKey() && (keyValueIsUnique(newVal))) || 
        		!attribute.getAttribute().isKey()) { // if the attribute is key it 
        																				 // has a unique value
          // set value of instantiated attribute:
          ((InstantiatedAttribute) (attribute)).setValue(newVal);
          attTblMod.refreshData();
          setVisible(false);
          dispose();
        } else {
          showNonUniqueKeyValueWarning();
        }
      } catch (NumberFormatException e) {
        System.out.println(e.getMessage());
      }
    } else if (attribute.getAttribute().getType() == AttributeTypes.INTEGER) { 
    	// integer attribute
      String value = startValTextField.getText();
      try {
        Integer newVal = new Integer(Integer.parseInt(value));
        if ((attribute.getAttribute().isKey() && (keyValueIsUnique(newVal))) || 
        		!attribute.getAttribute().isKey()) { // if the attribute is key it 
        																				 // has a unique value
          // set value of instantiated attribute:
          ((InstantiatedAttribute) (attribute)).setValue(newVal);
          attTblMod.refreshData();
          setVisible(false);
          dispose();
        } else {
          showNonUniqueKeyValueWarning();
        }
      } catch (NumberFormatException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /*
   * validates the input from the text field; returns null if input is valid, or
   * a Vector of String messages explaining the error(s)
   */
  private Vector<String> inputValid() { 
    Vector<String> messages = new Vector<String>(); 
    if (attribute.getAttribute().getType() != AttributeTypes.BOOLEAN) { 
    	// only need to check for non-boolean attributes since boolean attributes 
    	// use a combo box to get the value
      // Get input:
      String value = startValTextField.getText();

      if (attribute.getAttribute().getType() == AttributeTypes.STRING) { 
      	// string attribute
        char[] cArray = value.toCharArray();
        // Check for length constraints:
        if ((cArray.length == 0) || (cArray.length > 100)) { // user has entered
                                                           	 // nothing or a 
        																										 // string longer 
        																										 // than 100 chars
          messages.add(new String(
          		"Value must be between 1 and 100 characters"));
        }
      } else if (attribute.getAttribute().getType() == AttributeTypes.DOUBLE) {
      	// double attribute
        if ((value != null) && (value.length() > 0)) { // field is not blank
          try {
            double doubleVal = Double.parseDouble(value); // parse the string
                                                          // into a double
            NumericalAttribute numAttribute = 
            	(NumericalAttribute)attribute.getAttribute();
            if (!numAttribute.isMinBoundless() && 
            		!numAttribute.isMaxBoundless()) { // has both a min and a max
              if ((doubleVal < numAttribute.getMinValue().doubleValue()) || 
              		(doubleVal > numAttribute.getMaxValue().doubleValue())) {
              	// it's outside of the range
                messages.add(new String(
                    "Value must be within minimum/maximum value ranges"));
              }
            } else if (!numAttribute.isMinBoundless() && 
            		numAttribute.isMaxBoundless()) { // has only a min value
              if (doubleVal < numAttribute.getMinValue().doubleValue()) {
              	// below the minimum value
                messages.add(new String(
                    "Value must be within minimum/maximum value ranges"));
              }
            } else if (numAttribute.isMinBoundless() && 
            		!numAttribute.isMaxBoundless()) { // has only a max value
              if (doubleVal > numAttribute.getMaxValue().doubleValue()) { 
              	// above the maximum value
                messages.add(new String(
                    "Value must be within minimum/maximum value ranges"));
              }
            }
          } catch (NumberFormatException e) {
            messages.add(new String("Value must be a valid double number"));
          }
        } else { // field is blank
          messages.add(new String("Value must be a valid double number"));
        }
      } else if (attribute.getAttribute().getType() == AttributeTypes.INTEGER) {
      	// integer attribute
        if ((value != null) && (value.length() > 0)) { // field is not blank
          try {
            int intVal = Integer.parseInt(value); // parse the string into an
                                                  // int
            NumericalAttribute numAttribute = 
            	(NumericalAttribute)attribute.getAttribute();
            if (!numAttribute.isMinBoundless() && 
            		!numAttribute.isMaxBoundless()) { // has both a min and a max
              if ((intVal < numAttribute.getMinValue().intValue()) || 
              		(intVal > numAttribute.getMaxValue().intValue())) { // it's 
              																												// outside
              																												// of the 
              																												// range
                messages.add(new String(
                    "Value must be within minimum/maximum value ranges"));
              }
            } else if (!numAttribute.isMinBoundless() && 
            		numAttribute.isMaxBoundless()) { // has only a min value
              if (intVal < numAttribute.getMinValue().intValue()) { // below the
              																											// minimum 
              																											// value
                messages.add(new String(
                    "Value must be within minimum/maximum value ranges"));
              }
            } else if (numAttribute.isMinBoundless() && 
            		!numAttribute.isMaxBoundless()) { // has only a max value
              if (intVal > numAttribute.getMaxValue().intValue()) { // above the
              																											// maximum 
              																											// value
                messages.add(new String(
                    "Value must be within minimum/maximum value ranges"));
              }
            }
          } catch (NumberFormatException e) {
            messages.add(new String("Value must be a valid integer"));
          }
        } else { // field is blank
          messages.add(new String("Value must be a valid integer"));
        }
      }
    }
    return messages;
  }

  /*
   * brings up a warning dialog box that says that the value they're trying to
   * assign to a key attribute's value is not unique
   */
  private void showNonUniqueKeyValueWarning() { 
    JOptionPane.showMessageDialog(null, (new String("There is already a "
        + objectInFocus.getSimSEObjectType().getName()
        + " "
        + SimSEObjectTypeTypes.getText(objectInFocus.getSimSEObjectType()
            .getType()) + " with that key attribute value!")),
        "Non-Unique Key Value", JOptionPane.ERROR_MESSAGE);
  }

  // initializes the value
  private void initializeForm() { 
  	InstantiatedAttribute instantiatedAttribute = 
  		(InstantiatedAttribute)attribute;
    if (attribute.getAttribute().getType() == AttributeTypes.BOOLEAN) { 
    	// boolean attribute
      if (instantiatedAttribute.isInstantiated() && 
      		(instantiatedAttribute.getValue() instanceof Boolean) && 
      		((Boolean) instantiatedAttribute.getValue()).booleanValue() == true) {
        booleanStartValList.setSelectedItem("True");
      } else { // false
        booleanStartValList.setSelectedItem("False");
      }
    } else { // non-boolean attribute
      if (instantiatedAttribute.isInstantiated()) {
        startValTextField.setText(instantiatedAttribute.getValue().toString());
      }
    }
  }
}