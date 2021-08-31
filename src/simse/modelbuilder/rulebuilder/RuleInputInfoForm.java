/*
 * This class defines the window through which info about a rule input can be
 * added/edited
 */

package simse.modelbuilder.rulebuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class RuleInputInfoForm extends JDialog implements ActionListener {
  private EffectRule ruleInFocus; // rule whose input is being edited
  private RuleInput inputInFocus; // rule input being edited
  private boolean newInput; // whether this is a new RuleInput being created
                            // (true) or an existing one being edited (false)

  private JComboBox typeList; // holds types that this input can be
  private JComboBox guardList; // guards for condition
  private JTextField conditionTextField; // for specifying a condition value
  private JTextField promptTextField; // for specifying a prompt for this input
  private JCheckBox cancelableBox; // for specifying whether or not it's
                                   // cancelable
  private JButton okButton; // for ok'ing the changes made in this form
  private JButton cancelButton; // for cancelling the changes made in this form

  public RuleInputInfoForm(JDialog owner, EffectRule rule, RuleInput input) {
    super(owner, true);
    ruleInFocus = rule;
    inputInFocus = input;

    // set newInput:
    newInput = true;
    Vector<RuleInput> inputs = ruleInFocus.getAllRuleInputs();
    for (int i = 0; i < inputs.size(); i++) {
      if (inputs.elementAt(i) == inputInFocus) // input in focus already exists
      																				 // in the rule in focus
        newInput = false;
    }

    // Set window title:
    setTitle("Rule Input Information");

    // Create main panel:
    Box mainPane = Box.createVerticalBox();

    // Create top pane:
    JPanel topPane = new JPanel();
    topPane.add(new JLabel("Type:"));
    typeList = new JComboBox();
    String[] types = InputType.getInputTypes();
    // add each type to the type list:
    for (int i = 0; i < types.length; i++) {
      typeList.addItem(types[i]);
    }
    typeList.addActionListener(this);
    topPane.add(typeList);

    // Create middle pane:
    JPanel middlePane = new JPanel();
    middlePane.add(new JLabel("Condition (leave blank if no condition):"));
    guardList = new JComboBox();
    String[] guards = RuleInputGuard.getGuards();
    // add each guard to the guard list:
    for (int i = 0; i < guards.length; i++) {
      guardList.addItem(guards[i]);
    }
    middlePane.add(guardList);
    conditionTextField = new JTextField(10);
    middlePane.add(conditionTextField);

    // Create bottom pane:
    JPanel bottomPane = new JPanel();
    bottomPane.add(new JLabel("Prompt:"));
    promptTextField = new JTextField(25);
    bottomPane.add(promptTextField);

    // Create bottom pane2:
    JPanel bottomPane2 = new JPanel();
    cancelableBox = new JCheckBox("Cancelable?");
    bottomPane2.add(cancelableBox);

    // Create button pane:
    JPanel buttonPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    buttonPane.add(okButton);
    buttonPane.add(cancelButton);

    // Add panes to main pane:
    mainPane.add(topPane);
    mainPane.add(middlePane);
    mainPane.add(bottomPane);
    mainPane.add(bottomPane2);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(900, 1));
    mainPane.add(separator1);
    mainPane.add(buttonPane);

    // initialize form:
    initializeForm();

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
    } else if (source == typeList) {
      String selectedString = (String) typeList.getSelectedItem();
      if ((selectedString.equals(InputType.STRING)) || 
      		(selectedString.equals(InputType.BOOLEAN))) { // string or boolean
                                                        // selected
        // disable condition stuff:
        guardList.setEnabled(false);
        conditionTextField.setEnabled(false);
      } else { // integer or double type selected
        // enable condition stuff:
        guardList.setEnabled(true);
        conditionTextField.setEnabled(true);
      }
    } else if (source == okButton) { // okButton has been pressed
      Vector<String> errors = validateInput(); // check validity of input

      if (errors.size() == 0) { // input valid
        addEditInput();
      } else { // input not valid
        for (int i = 0; i < errors.size(); i++) {
          JOptionPane.showMessageDialog(null, errors.elementAt(i),
              "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  // returns a vector of error messages (if any)
  private Vector<String> validateInput() { 
    Vector<String> messages = new Vector<String>();

    // validate prompt:
    String prompt = promptTextField.getText();
    if ((prompt == null) || (prompt.length() == 0) || (prompt.length() > 400)) {
    	// string empty or longer than 400 chars
      messages.add("Please enter a prompt between 1 and 400 characters");
    }

    // get input type:
    String type = (String) typeList.getSelectedItem();
    String condition = conditionTextField.getText();
    if ((condition != null) && (condition.length() > 0)) { // not blank
      if (type.equals(InputType.INTEGER)) { // integer type
        try {
          Integer.parseInt(condition);
        } catch (NumberFormatException e) {
          messages
              .add("Please enter a valid integer for the condition or else " +
              		"leave it blank");
        }
      } else if (type.equals(InputType.DOUBLE)) { // double type
        try {
          Double.parseDouble(condition);
        } catch (NumberFormatException e) {
          messages
              .add("Please enter a valid double value for the condition or " +
              		"else leave it blank");
        }
      }
    }
    return messages;
  }

  /*
   * sets the values of the RuleInput in focus from the existing info in the 
   * form and adds it to the rule in focus. Note: validateInput() should be
   * called before calling this function to ensure that you're adding a valid
   * rule input
   */
  private void addEditInput() { 
    String type = (String) typeList.getSelectedItem();
    inputInFocus.setType(type); // set type
    if ((type.equals(InputType.STRING)) || (type.equals(InputType.BOOLEAN))) {
    	// string or boolean
      // clear condition:
      inputInFocus.clearCondition();
    } else if (type.equals(InputType.INTEGER)) { // integer input type
      inputInFocus.getCondition()
          .setGuard((String) guardList.getSelectedItem()); // set guard
      String condition = conditionTextField.getText();
      try {
        int intVal = Integer.parseInt(condition);
        // set condition value:
        inputInFocus.getCondition().setValue(new Integer(intVal)); 
      } catch (NumberFormatException e) {
      	// NOTE: this exception should never be thrown since validateInput()
      	// should always be called before this function to ensure that the input
      	// is valid
        System.out.println(e.getMessage()); 
      }
    } else if (type.equals(InputType.DOUBLE)) { // double input type
      inputInFocus.getCondition()
          .setGuard((String) guardList.getSelectedItem()); // set guard
      String condition = conditionTextField.getText();
      try {
        double doubleVal = Double.parseDouble(condition);
        // set condition value:
        inputInFocus.getCondition().setValue(new Double(doubleVal)); 
      } catch (NumberFormatException e) {
      	
        System.out.println(e.getMessage()); 
      	// NOTE: this exception should never be thrown since validateInput()
      	// should always be called before this function to ensure that the input
      	// is valid
      }
    }

    // set prompt:
    inputInFocus.setPrompt(promptTextField.getText());

    // set cancelable:
    inputInFocus.setCancelable(cancelableBox.isSelected());

    if (newInput) { // newly created rule input
    	// add the input to the rule in focus:
      ruleInFocus.addRuleInput(inputInFocus); 
    }

    setVisible(false);
    dispose();
  }

  private void initializeForm() {
    if (!newInput) { // editing existing input; initialize form with input's
                   	 // values
      typeList.setSelectedItem(inputInFocus.getType()); // set type
      // set guard:
      guardList.setSelectedItem(inputInFocus.getCondition().getGuard()); 
      if (inputInFocus.getCondition().isConstrained()) {
        conditionTextField.setText(inputInFocus.getCondition().getValue()
            .toString()); // set condition value
      }
      promptTextField.setText(inputInFocus.getPrompt()); // set prompt
    }
    cancelableBox.setSelected(inputInFocus.isCancelable());
    String type = (String) typeList.getSelectedItem();
    if ((type.equals(InputType.STRING)) || (type.equals(InputType.BOOLEAN))) { 
    	// string or boolean
      // disable condition stuff:
      guardList.setEnabled(false);
      conditionTextField.setEnabled(false);
    }
  }
}