/*
 * This class defines the window through which information about an action's
 * visibility can be edited
 */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ActionTypeOptionsInfoForm extends JDialog implements
    ActionListener {
  private ActionType actionInFocus; // action type whose attributes are being
                                    // edited

  private JLabel simVisLabel;
  private JComboBox simVisibleList; // for choosing whether the action is
                                    // visible in the simulation
  private JComboBox expVisibleList; // for choosing whether the action is
                                    // visible in the explanatory tool
  private JLabel descriptionLabel; // label for description field
  private JTextField descriptionTextField; // for entering the description of
                                           // the action
  private JTextArea annotationTextArea; // for entering an annotation for the
                                        // action
  private JCheckBox joinCheckBox; // for indicating whether joining this action
  																// is allowed
  private JButton okButton; 
  private JButton cancelButton;

  public ActionTypeOptionsInfoForm(JFrame owner, ActionType actionInFocus) {
    super(owner, true);
    this.actionInFocus = actionInFocus;

    // Set window title:
    setTitle(actionInFocus.getName() + " Action Options");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();

    // Create simVis pane:
    JPanel simVisPane = new JPanel();
    simVisLabel = new JLabel("Visible in simulation:");
    simVisPane.add(simVisLabel);
    simVisibleList = new JComboBox();
    simVisibleList.addItem("true");
    simVisibleList.addItem("false");
    simVisibleList
        .setToolTipText("Whether or not this action type is visible in the " +
        		"simulation's user interface");
    simVisibleList.addActionListener(this);
    simVisPane.add(simVisibleList);

    // Create description pane:
    JPanel descriptionPane = new JPanel();
    descriptionLabel = new JLabel("Description:");
    descriptionPane.add(descriptionLabel);
    descriptionTextField = new JTextField(15);
    descriptionTextField
        .setToolTipText("Description of this action type to appear in the " +
        		"simulation's user interface");
    descriptionPane.add(descriptionTextField);
    
    // Disable simulation visibility stuff if no employee participant in action:
    if (!actionInFocus.hasParticipantOfMetaType(
        SimSEObjectTypeTypes.EMPLOYEE)) {
      simVisLabel.setEnabled(false);
      simVisibleList.setSelectedItem("false");
      simVisibleList.setEnabled(false);
      descriptionLabel.setEnabled(false);
      descriptionTextField.setEnabled(false);
    }

    // Create expVis pane:
    JPanel expVisPane = new JPanel();
    expVisPane.add(new JLabel("Visibile in explanatory tool:"));
    expVisibleList = new JComboBox();
    expVisibleList.addItem("true");
    expVisibleList.addItem("false");
    expVisibleList.setToolTipText("Whether or not this action type is "
        + "visible in the explanatory tool's user interface");
    expVisibleList.addActionListener(this);
    expVisPane.add(expVisibleList);

    // Create annotation panes:
    JPanel annLabelPane = new JPanel();
    annLabelPane.add(new JLabel("Annotation:"));
    annotationTextArea = new JTextArea(15, 30);
    annotationTextArea.setLineWrap(true);
    annotationTextArea.setWrapStyleWord(true);
    JScrollPane annotationPane = new JScrollPane(annotationTextArea,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    // Create join pane:
    JPanel joinPane = new JPanel();
    joinCheckBox = new JCheckBox("Allow joining");
    joinCheckBox.setToolTipText("Allow employee participants to join this " +
    		"action while it is in progress");
    if (!actionInFocus.hasTriggerOfType(ActionTypeTrigger.USER)) {
      joinCheckBox.setEnabled(false);
    }
    joinPane.add(joinCheckBox);

    // Create bottom pane and buttons:
    JPanel bottomPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    bottomPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    bottomPane.add(cancelButton);

    // Add panes to main pane:
    mainPane.add(simVisPane);
    mainPane.add(descriptionPane);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(1500, 1));
    mainPane.add(separator1);
    mainPane.add(expVisPane);
    mainPane.add(annLabelPane);
    mainPane.add(annotationPane);
    mainPane.add(joinPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(1500, 1));
    mainPane.add(separator2);
    mainPane.add(bottomPane);
    
    initializeForm(); // initialize values to reflect action being edited

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

    if (source == simVisibleList) {
      if (((String) simVisibleList.getSelectedItem()).equals("true")) {
        descriptionLabel.setEnabled(true);
        descriptionTextField.setEnabled(true);
      } else {
        descriptionLabel.setEnabled(false);
        descriptionTextField.setEnabled(false);
      }
    } else if (source == cancelButton) { // cancel button has been pressed
      // Close window:
      setVisible(false);
      dispose();
    } else if (source == okButton) { // okButton has been pressed
      // simulation visibility
      if (((String) simVisibleList.getSelectedItem()).equals("true")) {
        // check if description is valid:
        String des = descriptionTextField.getText();
        if ((des.equals(null)) || (des.length() == 0)) { // nothing entered
          JOptionPane.showMessageDialog(null, "Please enter a description",
              "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } else { // valid input
          actionInFocus.setVisibilityInSimulation(true);
          actionInFocus.setDescription(des);
          setVisible(false);
          dispose();
        }
      } else { // false
        actionInFocus.setVisibilityInSimulation(false);
        actionInFocus.setDescription(null);
        setVisible(false);
        dispose();
      }

      // explanatory tool visibility
      if (((String) expVisibleList.getSelectedItem()).equals("true")) {
        actionInFocus.setVisibilityInExplanatoryTool(true);
      } else { // false
        actionInFocus.setVisibilityInExplanatoryTool(false);
      }
      String ann = annotationTextArea.getText();
      if ((ann != null) && (ann.length() > 0)) {
        actionInFocus.setAnnotation(ann);
        setVisible(false);
        dispose();
      }
      
      // joining:
      actionInFocus.setJoiningAllowed(joinCheckBox.isSelected());
    }
  }

  // initializes the form to reflect the action being edited
  private void initializeForm() { 
    // simulation visibility:
    if (actionInFocus.hasParticipantOfMetaType(
        SimSEObjectTypeTypes.EMPLOYEE)) {
	    if (actionInFocus.isVisibleInSimulation()) {
	      simVisibleList.setSelectedIndex(0);
	    } else { // not visible
	      simVisibleList.setSelectedIndex(1);
	    }
	    if ((actionInFocus.getDescription() != null)
	        && (actionInFocus.getDescription().length() > 0)) { // has a 
	    																												// description
	      descriptionTextField.setText(actionInFocus.getDescription());
	    }
    }

    // explanatory tool visibility
    if (actionInFocus.isVisibleInExplanatoryTool()) {
      expVisibleList.setSelectedIndex(0);
    } else { // not visible
      expVisibleList.setSelectedIndex(1);
    }
    if ((actionInFocus.getAnnotation() != null)
        && (actionInFocus.getAnnotation().length() > 0)) { // has annotation
      annotationTextArea.setText(actionInFocus.getAnnotation());
      annotationTextArea.setCaretPosition(0);
    }
    
    // joining:
    if (actionInFocus.hasTriggerOfType(ActionTypeTrigger.USER)) {
      joinCheckBox.setSelected(actionInFocus.isJoiningAllowed());
    }
  }
}