/*
 * This class defines the window through which information about an attribute
 * can be added/edited
 */

package simse.modelbuilder.objectbuilder;

import simse.modelbuilder.ModelBuilderGUI;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AttributeInfoForm extends JDialog implements ActionListener {
  private SimSEObjectType objectInFocus; // object type whose attributes are
                                         // being edited
  private Attribute attribute; // attribute whose values are being edited

  private JTextField nameTextField; // for entering the name of the attribute
  private JComboBox typeList; // for choosing a type for the attribute
  private JComboBox visibleList; // for choosing whether an attribute is visible
  private JComboBox visibleAtEndList; // for choosing whether an attribute is
                                      // visible at game end
  private JComboBox keyList; // for choosing whether or not an attribute is the
                             // key for the object type
  private JLabel minValLabel; // labels minimum value field
  private JLabel maxValLabel; // labels maximum value field
  private JTextField minValTextField; // for entering the minimum value of a
                                      // variable
  private JTextField maxValTextField; // for entering the maximum value of a
                                      // variable
  private JLabel minNumDigitsLabel; // label for minNumDigits field
  private JTextField minNumDigitsTextField; // for entering min number of digits
                                            // after decimal to show
  private JLabel maxNumDigitsLabel; // label for maxNumDigits field
  private JTextField maxNumDigitsTextField; // for entering max number of digits
                                            // after decimal to show
  private JButton okButton; // for ok'ing the creating/editing of a new
                            // attribute
  private JButton cancelButton; // for canceling the creating/editing of a new
                                // attribute

  private ObjectBuilderGUI objectBuilder;

  public AttributeInfoForm(JFrame owner, SimSEObjectType objectInFocus, 
  		Attribute attribute) {
    super(owner, true);
    this.objectInFocus = objectInFocus;
    this.attribute = attribute;

    ModelBuilderGUI mb = (ModelBuilderGUI) owner;
    objectBuilder = mb.getObjectBuilderGUI();

    // Set window title:
    setTitle("Attribute Information - SimSE");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();

    // Create main label pane:
    JPanel mainLabelPane = new JPanel();
    mainLabelPane.add(new JLabel("Enter Attribute Information:"));

    // Create "name" pane, label, and text field:
    JPanel namePane = new JPanel();
    namePane.add(new JLabel("Name:"));
    nameTextField = new JTextField(18);
    namePane.add(nameTextField);

    // Create type/visible/key pane, labels, and combo boxes:
    // Types:
    JLabel typeLabel = new JLabel("Type:");
    typeLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    namePane.add(typeLabel);
    typeList = new JComboBox();
    typeList.addItem("Integer");
    typeList.addItem("Double");
    typeList.addItem("Boolean");
    typeList.addItem("String");
    typeList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // t, l,
                                                                      // b, r
    typeList.addActionListener(this);
    namePane.add(typeList);
    // Visible:
    namePane.add(new JLabel("Visible:"));
    visibleList = new JComboBox();
    visibleList.addItem("Yes");
    visibleList.addItem("No");
    namePane.add(visibleList);
    // VisibleAtEnd:
    namePane.add(new JLabel("Visible at game end:"));
    visibleAtEndList = new JComboBox();
    visibleAtEndList.addItem("Yes");
    visibleAtEndList.addItem("No");
    namePane.add(visibleAtEndList);
    // Key:
    namePane.add(new JLabel("Key:"));
    keyList = new JComboBox();
    keyList.addItem("Yes");
    keyList.addItem("No");
    if (objectInFocus.getAllAttributes().size() == 0) { // this is the first
                                                      	// attribute being added
      keyList.setSelectedItem("Yes");
    } else { // not the first
      keyList.setSelectedItem("No");
    }
    namePane.add(keyList);

    // Create min/max digits pane, labels, and text fields:
    JPanel minMaxDigitsPane = new JPanel();

    // minNumDigits:
    minNumDigitsLabel = new JLabel("Min Num Fraction Digits to Display:");
    minMaxDigitsPane.add(minNumDigitsLabel);
    minNumDigitsTextField = new JTextField(3);
    minMaxDigitsPane.add(minNumDigitsTextField);
    minNumDigitsLabel.setEnabled(false);
    minNumDigitsTextField.setEnabled(false);

    // maxNumDigits:
    maxNumDigitsLabel = new JLabel("Max Num Fraction Digits to Display:");
    minMaxDigitsPane.add(maxNumDigitsLabel);
    maxNumDigitsTextField = new JTextField(3);
    minMaxDigitsPane.add(maxNumDigitsTextField);
    minMaxDigitsPane.add(new JLabel("(Leave blank if boundless)"));
    maxNumDigitsLabel.setEnabled(false);
    maxNumDigitsTextField.setEnabled(false);

    // Create min/max pane, labels, and text fields:
    JPanel minMaxPane = new JPanel();

    // min value:
    minValLabel = new JLabel("Minimum Value:");
    minMaxPane.add(minValLabel);
    minValTextField = new JTextField(6);
    minMaxPane.add(minValTextField);

    // max value:
    maxValLabel = new JLabel("Maximum Value:");
    minMaxPane.add(maxValLabel);
    maxValTextField = new JTextField(6);
    minMaxPane.add(maxValTextField);
    minMaxPane.add(new JLabel("(Leave blank if boundless)"));

    // Create okCancelButton pane and buttons:
    JPanel okCancelButtonPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    okCancelButtonPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    okCancelButtonPane.add(cancelButton);

    // Add panes to main pane:
    mainPane.add(mainLabelPane);
    mainPane.add(namePane);
    mainPane.add(minMaxDigitsPane);
    mainPane.add(minMaxPane);
    mainPane.add(okCancelButtonPane);

    if (attribute != null) { // editing an existing attribute
      initializeForm(); // initialize values to reflect attribute being edited
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

  public void actionPerformed(ActionEvent evt) { // handles user actions
    Object source = evt.getSource(); // get which component the action came from
    if (source == typeList) { // User has chosen a type
      int selectedType = AttributeTypes.getIntRepresentation((String) typeList
          .getSelectedItem());
      if ((selectedType == AttributeTypes.STRING)
          || (selectedType == AttributeTypes.BOOLEAN)) { // non-numerical types
        // clear and disable min/max fields and labels:
        minNumDigitsLabel.setEnabled(false);
        minNumDigitsTextField.setText(null);
        minNumDigitsTextField.setEnabled(false);
        maxNumDigitsLabel.setEnabled(false);
        maxNumDigitsTextField.setText(null);
        maxNumDigitsTextField.setEnabled(false);
        minValLabel.setEnabled(false);
        minValTextField.setText(null);
        minValTextField.setEnabled(false);
        maxValLabel.setEnabled(false);
        maxValTextField.setText(null);
        maxValTextField.setEnabled(false);
      } else { // numerical type chosen
        // enable min/max val fields and labels:
        minValLabel.setEnabled(true);
        minValTextField.setEnabled(true);
        maxValLabel.setEnabled(true);
        maxValTextField.setEnabled(true);
        if (selectedType == AttributeTypes.DOUBLE) {
          // enable min/max digits stuff:
          minNumDigitsLabel.setEnabled(true);
          minNumDigitsTextField.setEnabled(true);
          maxNumDigitsLabel.setEnabled(true);
          maxNumDigitsTextField.setEnabled(true);
        } else { // int attribute
          // disable min/max digits stuff:
          minNumDigitsTextField.setText(null);
          maxNumDigitsTextField.setText(null);
          minNumDigitsLabel.setEnabled(false);
          minNumDigitsTextField.setEnabled(false);
          maxNumDigitsLabel.setEnabled(false);
          maxNumDigitsTextField.setEnabled(false);
        }
      }
    } else if (source == cancelButton) { // cancel button has been pressed
      // Close window:
      setVisible(false);
      dispose();
    } else if (source == okButton) { // okButton has been pressed
      Vector<String> errors = inputValid(); // check validity of input
      // check that max is greater than or equal to min:
      String otherError = maxGreaterThanOrEqualToMin(); 
      String otherError2 = maxDigitsGreaterThanOrEqualToMinDigits();
      if ((otherError != null) && (otherError.length() > 0)) {
        errors.add(otherError);
      }
      if ((otherError2 != null) && (otherError2.length() > 0)) {
        errors.add(otherError2);
      }
      if (errors.size() == 0) { // input valid
        if (attributeNameIsUnique()) {
          if (attribute == null) { // a new attribute is being created
            addAttribute();
          } else { // an existing attribute is being edited
            editAttribute();
          }
        }
      } else { // input not valid
        for (int i = 0; i < errors.size(); i++) {
          JOptionPane.showMessageDialog(null, ((String) errors.elementAt(i)),
              "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  /*
   * initializes the text in the text fields (if any) and greys-out the min/max
   * value fields if necessary
   */
  private void initializeForm() { 
    // fill in values:
    nameTextField.setText(attribute.getName());
    typeList.setSelectedItem(AttributeTypes.getText(attribute.getType()));
    if (attribute.isVisible()) {
      visibleList.setSelectedItem("Yes");
    } else {
      visibleList.setSelectedItem("No");
    }
    if (attribute.isVisibleOnCompletion()) {
      visibleAtEndList.setSelectedItem("Yes");
    } else {
      visibleAtEndList.setSelectedItem("No");
    }
    if (attribute.isKey()) {
      keyList.setSelectedItem("Yes");
    } else {
      keyList.setSelectedItem("No");
    }

    if ((attribute == null) || (attribute instanceof NonNumericalAttribute)) { 
    	// min/max val fields do not apply
      // disable min/max val fields and labels:
      minNumDigitsLabel.setEnabled(false);
      minNumDigitsTextField.setEnabled(false);
      maxNumDigitsLabel.setEnabled(false);
      maxNumDigitsTextField.setEnabled(false);
      minValLabel.setEnabled(false);
      minValTextField.setEnabled(false);
      maxValLabel.setEnabled(false);
      maxValTextField.setEnabled(false);
    } else { // attribute is a numerical attribute
    	NumericalAttribute numAttribute = (NumericalAttribute)attribute;
      if (!numAttribute.isMinBoundless()) { // min value is not boundless
        minValTextField.setText(numAttribute.getMinValue().toString());
      }
      if (!numAttribute.isMaxBoundless()) { // max value is not boundless
        maxValTextField.setText(numAttribute.getMaxValue().toString());
      }
      if (attribute.getType() != AttributeTypes.DOUBLE) { // non-double 
      																										// attribute
        // disable min/max num digits fields and labels:
        minNumDigitsLabel.setEnabled(false);
        minNumDigitsTextField.setEnabled(false);
        maxNumDigitsLabel.setEnabled(false);
        maxNumDigitsTextField.setEnabled(false);
      } else { // double attribute
        if (numAttribute.getMinNumFractionDigits() != null) { // has min num
                                                              // fraction digits
          minNumDigitsTextField.setText(
          		numAttribute.getMinNumFractionDigits().toString());
        }
        if (numAttribute.getMaxNumFractionDigits() != null) { // has max num
                                                              // fraction digits
          maxNumDigitsTextField.setText(
          		numAttribute.getMaxNumFractionDigits().toString());
        }
      }
    }
  }

  /*
   * validates the input from the text fields; returns null if input is valid,
   * or a Vector of String messages explaining the error(s)
   */
  private Vector<String> inputValid() { 
    Vector<String> messages = new Vector<String>(); 

    // Check name input:
    String nameInput = nameTextField.getText();
    char[] cArray = nameInput.toCharArray();
    // Check for length constraints:
    if ((cArray.length < 2) || (cArray.length > 40)) { // user has entered a
                                                     	 // string shorter than 2
                                                     	 // chars or longer than 
    																									 // 40 chars
      messages.add(new String("Name must be between 2 and 40 characters"));
    }
    // Check for invalid characters:
    for (int i = 0; i < cArray.length; i++) {
      if ((Character.isLetter(cArray[i])) == false) { // character is not a 
      																								// letter (hence, invalid)
        messages.add(new String(
            "Name must consist of only alphabetic characters"));
        break;
      }
    }

    // Check key input:
    if ((attribute != null) && (attribute.isKey() == true)
        && (((String) keyList.getSelectedItem()) == "No")) { // this is an
                                                           	 // attribute
    																												 // being edited, 
    																												 // not a new one, 
    																												 // it was the key, 
    																												 // but the user has
    																												 // just changed it 
    																												 // to not be the 
    																												 // key, hence there
    																												 // is no key for 
    																												 // this object
      if (objectInFocus.getNumAttributes() > 1) { // this is not the only
                                                	// attribute
        messages
            .add(new String(
                "You must make a different attribute the key in order to " +
                "remove this one as the key."));
      } else { // this is the only attribute
        messages
            .add(new String(
                "You cannot remove this attribute as the key since it is the " +
                "only attribute."));
      }
    } else if ((objectInFocus.getNumAttributes() == 0)
        && (((String) keyList.getSelectedItem()) == "No")) { // this is a new
                                                           	 // attribute being
    																												 // created and it 
    																												 // is also the 
    																												 // first attribute 
    																												 // being created 
    																												 // for the object
    																												 // type in focus
      messages
          .add(new String(
              "You must have exactly one attribute per object type defined " +
              "as the key."));
    }

    // Check min/max fraction digits and value inputs:
    int selectedType = AttributeTypes.getIntRepresentation((String) typeList
        .getSelectedItem());
    String minValInput = minValTextField.getText();
    String maxValInput = maxValTextField.getText();
    String minDigitsInput = minNumDigitsTextField.getText();
    String maxDigitsInput = maxNumDigitsTextField.getText();

    // disables renaming of Hired attribute in model allows hire and fire
    if (objectBuilder.allowHireFire()
        && attribute.getName().equalsIgnoreCase("Hired")
        && !nameInput.equals(attribute.getName()))
      messages.add("Hired is a reserved word and cannot be modified");

    // prevents Hired attribute of anything other than type BOOLEAN
    if (nameInput.equalsIgnoreCase("Hired")
        && selectedType != AttributeTypes.BOOLEAN) {
      messages.add("Hired is a reserved word and can only be of type BOOLEAN");
    }

    if (selectedType == AttributeTypes.DOUBLE) { // type = double
      // Check min value input:
      if ((minValInput != null) && (minValInput.length() > 0)) { // field is not
                                                               	 // blank
        try {
          Double.parseDouble(minValInput); // parse the string into a double
        } catch (NumberFormatException e) {
          messages
              .add(new String("Minimum value must be a valid double number"));
        }
      }

      // Check max value input:
      if ((maxValInput != null) && (maxValInput.length() > 0)) { // field is not
                                                               	 // blank
        try {
          Double.parseDouble(maxValInput); // parse the string into a double
        } catch (NumberFormatException e) {
          messages
              .add(new String("Maximum value must be a valid double number"));
        }
      }

      // Check minDigits input:
      if ((minDigitsInput != null) && (minDigitsInput.length() > 0)) { // field 
      																																 // is
                                                                       // not
                                                                     	 // blank
        try {
          int minDigits = Integer.parseInt(minDigitsInput); // parse the string
                                                            // into a int
          if ((minDigits < 0) || (minDigits > 16)) {
            messages.add(new String(
                "Minimum number of fraction digits must be between 0 and 16"));
          }
        } catch (NumberFormatException e) {
          messages
              .add(new String(
                  "Minimum number of fraction digits must be a valid integer " +
                  "number between 0 and 16"));
        }
      }

      // Check maxDigits input:
      if ((maxDigitsInput != null) && (maxDigitsInput.length() > 0)) { // field 
      																																 // is
                                                                       // not
                                                                       // blank
        try {
          int maxDigits = Integer.parseInt(maxDigitsInput); // parse the string
                                                            // into a int
          if ((maxDigits < 0) || (maxDigits > 16)) {
            messages.add(new String(
                "Maximum number of fraction digits must be between 0 and 16"));
          }
        } catch (NumberFormatException e) {
          messages
              .add(new String(
                  "Maximum number of fraction digits must be a valid integer " +
                  "number between 0 and 16"));
        }
      }
    } else if (selectedType == AttributeTypes.INTEGER) { // type = integer
      // Check min value input:
      if ((minValInput != null) && (minValInput.length() > 0)) { // field is not
                                                               	 // blank
        try {
          Integer.parseInt(minValInput); // parse the string into an int
        } catch (NumberFormatException e) {
          messages.add(new String("Minimum value must be a valid integer"));
        }
      }
      // Check max value input:
      if ((maxValInput != null) && (maxValInput.length() > 0)) { // field is not
                                                               	 // blank
        try {
          Integer.parseInt(maxValInput); // parse the string into an int
        } catch (NumberFormatException e) {
          messages.add(new String("Maximum value must be a valid integer"));
        }
      }
    }
    return messages;
  }

  /*
   * returns a string with the error message if this condition is not met, null
   * otherwise
   */
  private String maxGreaterThanOrEqualToMin() { 
    String minValInput = minValTextField.getText();
    String maxValInput = maxValTextField.getText();
    if ((minValInput != null) && (minValInput.length() > 0)
        && (maxValInput != null) && (maxValInput.length() > 0)) { // neither max
                                                                	// nor min
                                                                	// fields are
                                                                	// blank
    	
      int selectedType = AttributeTypes.getIntRepresentation((String) typeList
          .getSelectedItem());
      if (selectedType == AttributeTypes.INTEGER) {
        try {
          int minVal = Integer.parseInt(minValInput); // parse the string into
                                                      // an integer
          int maxVal = Integer.parseInt(maxValInput); // parse the string into
                                                      // an integer
          if (maxVal < minVal) { // invalid
            return (new String(
                "Maximum value must be greater than or equal to minimum " +
                "value"));
          }
        } catch (NumberFormatException e) {
          System.out.println(e.getMessage());
          // since this has already been checked with call to inputValid(), this
          // exception should never be thrown here.
        }
      } else if (selectedType == AttributeTypes.DOUBLE) {
        try {
          double minVal = Double.parseDouble(minValInput); // parse the string
                                                           // into a double
          double maxVal = Double.parseDouble(maxValInput); // parse the string
                                                           // into a double
          if (maxVal < minVal) { // invalid
            return (new String(
                "Maximum value must be greater than or equal to minimum " +
                "value"));
          }
        } catch (NumberFormatException e) {
          System.out.println(e.getMessage());
          // since this has already been checked with call to inputValid(), this
          // exception should never be thrown here.
        }
      }
    }
    return null;
  }

  /*
   * returns a string with the error message if this condition is not met, null
   * otherwise
   */
  private String maxDigitsGreaterThanOrEqualToMinDigits() { 
    String minDigitsInput = minNumDigitsTextField.getText();
    String maxDigitsInput = maxNumDigitsTextField.getText();
    if ((minDigitsInput != null) && (minDigitsInput.length() > 0)
        && (maxDigitsInput != null) && (maxDigitsInput.length() > 0)) { 
    	// neither max nor min fields are blank
      int selectedType = AttributeTypes.getIntRepresentation((String) typeList
          .getSelectedItem());
      if (selectedType == AttributeTypes.DOUBLE) {
        try {
          int minVal = Integer.parseInt(minDigitsInput); // parse the string
                                                         // into a int
          int maxVal = Integer.parseInt(maxDigitsInput); // parse the string
                                                         // into a int
          if (maxVal < minVal) { // invalid
            return (new String(
                "Maximum number of fraction digits must be greater than or " +
                "equal to minimum value"));
          }
        } catch (NumberFormatException e) {
          System.out.println(e.getMessage());
          // since this has already been checked with call to inputValid(), this
          // exception should never be thrown here.
        }
      }
    }
    return null;
  }

  /*
   * returns true if it is unique, false otherwise, and also takes care of 
   * asking the user if they want to overwrite it (and overwriting it, if
   * desired)
   */
  private boolean attributeNameIsUnique() { 
    String nameInput = nameTextField.getText();
    if ((attribute == null)
        || ((attribute != null) && 
        		(attribute.getName().equals(nameInput) == false))) { // only perform
                                                                 // this check
                                                                 // if this is a
    																														 // newly 
    																														 // created 
    																														 // attribute, 
    																														 // or it is an 
    																														 // edited 
    																														 // attribute 
    																														 // and the name
    																														 // has been 
    																														 // changed
      Vector<Attribute> existingAttributes = objectInFocus.getAllAttributes();
      for (int i = 0; i < existingAttributes.size(); i++) {
        Attribute tempAttr = existingAttributes.elementAt(i);
        String keyAttName = new String();
        if (tempAttr.isKey()) {
          keyAttName = tempAttr.getName();
        }
        if (tempAttr.getName().equalsIgnoreCase(nameInput)) { // name entered is
                                                            	// not unique 
        																											// (there is 
        																											// already another
        																											// attribute of
        																											// this object 
        																											// defined with 
        																											// the same name)
          int choice = JOptionPane
              .showConfirmDialog(
                  null,
                  ("Previously defined " + tempAttr.getName() + 
                  		" attribute will be overwritten. Continue?"), "Warning", 
                  		JOptionPane.YES_NO_OPTION);
          if (choice == JOptionPane.YES_OPTION) {
            if (attribute != null) { // this is an attribute being edited, not a
                                   	 // new one
              if ((((String) keyList.getSelectedItem()) == "No") && 
              		(attribute.isKey() == true)) { // this attribute being edited
              																	 // was the key previously, but 
              																	 // the user has just changed it
              																	 // to not be the key, hence 
              																	 // there would be no key for 
              																	 // this object
                if (objectInFocus.getNumAttributes() > 1) { // this is not the
                                                          	// only attribute
                  JOptionPane
                      .showMessageDialog(
                          null,
                          "You must make a different attribute the key in " +
                          "order to remove this one as the key.",
                          "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } else { // this is the only attribute
                  JOptionPane
                      .showMessageDialog(
                          null,
                          "You cannot remove this attribute as the key since " +
                          "it is the only attribute.",
                          "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
              } else { // everything is okay!
                // Overwrite existing attribute:
                objectInFocus.removeAttribute(tempAttr.getName());
                objectInFocus.removeAttribute(attribute.getName());
                if (((String) keyList.getSelectedItem()) == "Yes") { 
                	// this newly added/edited attribute is key
                  Attribute key = objectInFocus.getKey(); // get the current key
                  if (keyAttName.equalsIgnoreCase(nameInput) == false) { 
                  	// newly added/edited attribute was not the key previously
                    key.setKey(false); // remove "key status" from previous key
                  }
                }
                addAttribute();

                if (objectInFocus.getAllAttributes().size() == 1) { // only one
                                                                  	// attribute
                	// make sure the one attribute is the key:
                  objectInFocus.getAllAttributes().elementAt(0).setKey(true); 
                }
              }
            } else { // this is a new attribute, not one being edited
              // Overwrite existing attribute:
              objectInFocus.removeAttribute(tempAttr.getName());
              if (((String) keyList.getSelectedItem()) == "Yes") { 
              	// this newly added/edited attribute is key
                if (objectInFocus.hasKey()) {
                  Attribute key = objectInFocus.getKey(); // get the current key
                  if (key.getName().equalsIgnoreCase(nameInput) == false) { 
                  	// newly added/edited attribute was not the key previously
                    key.setKey(false); // remove "key status" from previous key
                  }
                }
              } else if ((((String) keyList.getSelectedItem()) == "No") && 
              		(tempAttr.isKey())) { // this newly added attribute is not the
              													// key, but the one it is supposed to be
              													// replacing was the key
                keyList.setSelectedItem("Yes"); // force this one to be the key
              }
              addAttribute();
            }
          }
          return false;
        }
      }
    }
    return true;
  }

  /*
   * creates a new attribute from the existing info in the form and adds it to
   * the object in focus. Note: inputValid(), maxGreaterThanOrEqualToMin(), and
   * attributeNameIsUnique() should all be called before calling this function
   * to ensure that you're adding a valid attribute.
   */
  private void addAttribute() { 
    int selectedType = AttributeTypes.getIntRepresentation((String) typeList
        .getSelectedItem()); // get type

    // get "visible" selection:
    boolean visible;
    if (((String) visibleList.getSelectedItem()) == "Yes") {
      visible = true;
    } else { // "No" selected
      visible = false;
    }

    // get "visibleAtEnd" selection:
    boolean visibleAtEnd;
    if (((String) visibleAtEndList.getSelectedItem()) == "Yes") {
      visibleAtEnd = true;
    } else { // "No" selected
      visibleAtEnd = false;
    }

    // get "key" selection:
    boolean key;
    if (((String) keyList.getSelectedItem()) == "Yes") {
      key = true;
      if (objectInFocus.hasKey()) {
        Attribute keyAttr = objectInFocus.getKey(); // get the current key
        if (keyAttr.getName().equals(nameTextField.getText()) == false) { 
        	// newly added/edited attribute was not the key previously
          keyAttr.setKey(false); // remove "key status" from previous key
        }
      }
    } else { // "No" selected
      key = false;
    }

    if ((selectedType == AttributeTypes.DOUBLE)
        || (selectedType == AttributeTypes.INTEGER)) { // numerical attribute
      String minValInput = minValTextField.getText(); // get min val
      String maxValInput = maxValTextField.getText(); // get max val
      String minDigitsInput = minNumDigitsTextField.getText();
      String maxDigitsInput = maxNumDigitsTextField.getText();

      if (selectedType == AttributeTypes.DOUBLE) {
        try {
          Double min = null;
          Double max = null;

          // min/max values:
          if ((minValInput != null) && (minValInput.length() > 0)
              && (maxValInput != null) && (maxValInput.length() > 0)) { 
          	// neither min val nor max val field is blank
            double minVal = Double.parseDouble(minValInput);
            double maxVal = Double.parseDouble(maxValInput);
            min = new Double(minVal);
            max = new Double(maxVal);
          } else if ((minValInput != null) && (minValInput.length() > 0)) { 
          	// only max val field is blank
            double minVal = Double.parseDouble(minValInput);
            min = new Double(minVal);
          } else if ((maxValInput != null) && (maxValInput.length() > 0)) { 
          	// only min val field is blank
            double maxVal = Double.parseDouble(maxValInput);
            max = new Double(maxVal);
          }

          // min/max num digits:
          if ((minDigitsInput != null) && (minDigitsInput.length() > 0)
              && (maxDigitsInput != null) && (maxDigitsInput.length() > 0)) {
          // neither min nor max field is blank
            int minVal = Integer.parseInt(minDigitsInput);
            int maxVal = Integer.parseInt(maxDigitsInput);
            // Add new attribute to object in focus:
            objectInFocus.addAttribute(new NumericalAttribute(nameTextField
                .getText(), selectedType, visible, key, visibleAtEnd, min, max,
                (new Integer(minVal)), (new Integer(maxVal))));
          } else if ((minDigitsInput != null) && 
          		(minDigitsInput.length() > 0)) { // only max field is blank
            int minVal = Integer.parseInt(minDigitsInput);
            // Add new attribute to object in focus:
            objectInFocus.addAttribute(new NumericalAttribute(nameTextField
                .getText(), selectedType, visible, key, visibleAtEnd, min, max,
                (new Integer(minVal)), null));
          } else if ((maxDigitsInput != null) && 
          		(maxDigitsInput.length() > 0)) { // only min field is blank
            int maxVal = Integer.parseInt(maxDigitsInput);
            // Add new attribute to object in focus:
            objectInFocus.addAttribute(new NumericalAttribute(nameTextField
                .getText(), selectedType, visible, key, visibleAtEnd, min, max,
                null, (new Integer(maxVal))));
          } else { // both min and max val field are blank
            // Add new attribute to object in focus:
            objectInFocus.addAttribute(new NumericalAttribute(nameTextField
                .getText(), selectedType, visible, key, visibleAtEnd, min, max,
                null, null));
          }
        } catch (NumberFormatException e) {
          System.out.println(e.getMessage());
        }
      } else if (selectedType == AttributeTypes.INTEGER) {
        try {
          if ((minValInput != null) && (minValInput.length() > 0)
              && (maxValInput != null) && (maxValInput.length() > 0)) { 
          	// neither min val nor max val field is blank
            int minVal = Integer.parseInt(minValInput);
            int maxVal = Integer.parseInt(maxValInput);
            // Add new attribute to object in focus:
            objectInFocus.addAttribute(new NumericalAttribute(nameTextField
                .getText(), selectedType, visible, key, visibleAtEnd,
                (new Integer(minVal)), (new Integer(maxVal)), null, null));
          } else if ((minValInput != null) && (minValInput.length() > 0)) { 
          	// only max val field is blank
            int minVal = Integer.parseInt(minValInput);
            // Add new attribute to object in focus:
            objectInFocus.addAttribute(new NumericalAttribute(nameTextField
                .getText(), selectedType, visible, key, visibleAtEnd,
                (new Integer(minVal)), null, null, null));
          } else if ((maxValInput != null) && (maxValInput.length() > 0)) { 
          	// only min val field is blank
            int maxVal = Integer.parseInt(maxValInput);
            // Add new attribute to object in focus:
            objectInFocus.addAttribute(new NumericalAttribute(nameTextField
                .getText(), selectedType, visible, key, visibleAtEnd, null,
                (new Integer(maxVal)), null, null));
          } else { // both min and max val field are blank
            // Add new attribute to object in focus:
            objectInFocus.addAttribute(new NumericalAttribute(nameTextField
                .getText(), selectedType, visible, key, visibleAtEnd, null,
                null, null, null));
          }
        } catch (NumberFormatException e) {
          System.out.println(e.getMessage());
        }
      }
    } else { // non-numerical attribute
      // Add new attribute to object in focus:
      objectInFocus.addAttribute(new NonNumericalAttribute(nameTextField
          .getText(), selectedType, visible, key, visibleAtEnd));
    }
    setVisible(false);
    dispose();
  }

  /*
   * changes the attribute being edited to reflect the changes made in this 
   * form. Note: inputValid(), maxGreaterThanOrEqualToMin(), and 
   * attributeNameIsUnique() should all be called before calling this function
   * to ensure that you're editing the attribute with valid values.
   */
  private void editAttribute() { 
    attribute.setName(nameTextField.getText()); // set name to new val
    int selectedType = AttributeTypes.getIntRepresentation((String) typeList
        .getSelectedItem()); // get type

    // get "visible" selection:
    if (((String) visibleList.getSelectedItem()) == "Yes") {
      attribute.setVisible(true); // set to new visible status
    } else { // "No" selected
      attribute.setVisible(false); // set to new visible status
    }

    // get "visibleAtEnd" selection:
    if (((String) visibleAtEndList.getSelectedItem()) == "Yes") {
      attribute.setVisibleOnCompletion(true); // set to new visible on
                                              // completion status
    } else { // "No" selected
      attribute.setVisibleOnCompletion(false); // set to new visible on
                                               // completion status
    }

    // get "key" selection:
    if (((String) keyList.getSelectedItem()) == "Yes") {
      if (objectInFocus.hasKey()) {
        objectInFocus.getKey().setKey(false); // remove "key status" from
                                              // previous key
      }
      attribute.setKey(true); // set to new key status
    } else { // "No" selected
      attribute.setKey(false); // set to new key status
    }

    if ((selectedType == AttributeTypes.DOUBLE)
        || (selectedType == AttributeTypes.INTEGER)) { // numerical attribute
      if ((attribute.getType() == AttributeTypes.STRING) || 
      		(attribute.getType() == AttributeTypes.BOOLEAN)) { // attribute is
                                                             // changing from
      																											 // a non-numerical 
      																											 // attribute to a 
      																											 // numerical one, 
      																											 // so needs to be 
      																											 // created new
      	// remove old non-numerical attribute:
        int position = objectInFocus.removeAttribute(attribute.getName()); 
        // add a new numerical attribute:
        objectInFocus.addAttribute(new NumericalAttribute(
            (NonNumericalAttribute) attribute, selectedType), position); 
        // re-focus attribute to this newly created one:
        attribute = objectInFocus.getAttribute(nameTextField.getText()); 
      }
      String minValInput = minValTextField.getText(); // get min val
      String maxValInput = maxValTextField.getText(); // get max val
      String minDigitsInput = minNumDigitsTextField.getText();
      String maxDigitsInput = maxNumDigitsTextField.getText();

      if (selectedType == AttributeTypes.DOUBLE) {
        try {
        	NumericalAttribute numAttribute = (NumericalAttribute)attribute;
          // min/max vals:
          if ((minValInput != null) && (minValInput.length() > 0)
              && (maxValInput != null) && (maxValInput.length() > 0)) { 
          	// neither min val nor max val field is blank
            // Set min and max vals to new vals:
          	numAttribute.setMinValue(
          			new Double(Double.parseDouble(minValInput)));
          	numAttribute.setMaxValue(
          			new Double(Double.parseDouble(maxValInput)));
          } else if ((minValInput != null) && (minValInput.length() > 0)) { 
          	// only max val field is blank
            // Set min val to new val:
          	numAttribute.setMinValue(
          			new Double(Double.parseDouble(minValInput)));
            // Set max val to null:
          	numAttribute.setMaxBoundless();
          } else if ((maxValInput != null) && (maxValInput.length() > 0)) { 
          	// only min val field is blank
            // Set max val to new val:
          	numAttribute.setMaxValue(
          			new Double(Double.parseDouble(maxValInput)));
            // Set min val to null:
          	numAttribute.setMinBoundless();
          } else { // both min and max val fields are blank
            // Set both vals to null:
          	numAttribute.setMinBoundless();
          	numAttribute.setMaxBoundless();
          }

          // min/max num digits:
          if ((minDigitsInput != null) && (minDigitsInput.length() > 0) && 
          		(maxDigitsInput != null) && (maxDigitsInput.length() > 0)) { 
          	// neither min nor max field is blank
            // Set min and max to new vals:
          	numAttribute.setMinNumFractionDigits(
          			new Integer(Integer.parseInt(minDigitsInput)));
          	numAttribute.setMaxNumFractionDigits(
          			new Integer(Integer.parseInt(maxDigitsInput)));
          } else if ((minDigitsInput != null) && 
          		(minDigitsInput.length() > 0)) { // only max field is blank
            // Set min to new val:
          	numAttribute.setMinNumFractionDigits(
          			new Integer(Integer.parseInt(minDigitsInput)));
            // Set max to null:
          	numAttribute.setMaxNumFractionDigits(null);
          } else if ((maxDigitsInput != null) && 
          		(maxDigitsInput.length() > 0)) { // only min field is blank
            // Set max to new val:
          	numAttribute.setMaxNumFractionDigits(
          			new Integer(Integer.parseInt(maxDigitsInput)));
            // Set min to null:
          	numAttribute.setMinNumFractionDigits(null);
          } else { // both min and max fields are blank
            // Set both vals to null:
          	numAttribute.setMinNumFractionDigits(null);
          	numAttribute.setMaxNumFractionDigits(null);
          }
        } catch (NumberFormatException e) {
          System.out.println(e.getMessage());
        }
      } else if (selectedType == AttributeTypes.INTEGER) {
        try {
        	NumericalAttribute numAttribute = (NumericalAttribute)attribute;
          if ((minValInput != null) && (minValInput.length() > 0) && 
          		(maxValInput != null) && (maxValInput.length() > 0)) { // neither
                                                                     // min val
                                                                     // nor max
                                                                     // val
                                                                     // field is
                                                                     // blank
            // Set min and max vals to new vals:
          	numAttribute.setMinValue(
          			new Integer(Integer.parseInt(minValInput)));
          	numAttribute.setMaxValue(
          			new Integer(Integer.parseInt(maxValInput)));
          } else if ((minValInput != null) && (minValInput.length() > 0)) { 
          	// only max val field is blank
            // Set min val to new val:
          	numAttribute.setMinValue(
          			new Integer(Integer.parseInt(minValInput)));
            // Set max val to null:
          	numAttribute.setMaxBoundless();
          } else if ((maxValInput != null) && (maxValInput.length() > 0)) { 
          	// only min val field is blank
            // Set max val to new val:
          	numAttribute.setMaxValue(
          			new Integer(Integer.parseInt(maxValInput)));
            // Set min val to null:
          	numAttribute.setMinBoundless();
          } else { // both min and max val field are blank
            // Set both vals to null:
          	numAttribute.setMinBoundless();
          	numAttribute.setMaxBoundless();
          }
        } catch (NumberFormatException e) {
          System.out.println(e.getMessage());
        }
      }
    } else { // type is String or Boolean, hence a non-numerical attribute
      if ((attribute.getType() == AttributeTypes.INTEGER)
          || (attribute.getType() == AttributeTypes.DOUBLE)) { // attribute is
                                                             	 // changing from 
      																												 // a numerical
                                                             	 // attribute to a
      																												 // non-numerical 
      																												 // one, so needs 
      																												 // to be created 
      																												 // new
      	// remove old non-numerical attribute:
        int position = objectInFocus.removeAttribute(attribute.getName()); 
        // add a new non-numerical attribute:
        objectInFocus.addAttribute(new NonNumericalAttribute(
            (NumericalAttribute) attribute, selectedType), position); 
        // re-focus attribute to this newly-created one:
        attribute = objectInFocus.getAttribute(nameTextField.getText()); 
      }
    }
    attribute.setType(selectedType);
    setVisible(false);
    dispose();
  }
}