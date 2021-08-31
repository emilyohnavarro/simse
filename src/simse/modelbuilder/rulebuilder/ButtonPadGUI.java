/*
 * This class is the GUI responsible for displaying the button pad for
 * specifying effects on attributes for EffectRules with
 */

package simse.modelbuilder.rulebuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ButtonPadGUI extends JDialog implements ActionListener {
  // button pad:
  private JTextArea echoedTextField; // text field from the effect rule info
                                     // form to be echoed under the button pad
  private JTextField parentTextField; // actual text field for the attribute
  																		// effect being edited
  private JButton okButton;
  private JButton cancelButton;

  public ButtonPadGUI(JDialog owner, JButton inputButton, 
  		JButton attributeThisPartButton, JButton attributeOtherPartButton, 
  		JButton numObjectsButton, JButton numActionsThisPartButton,
      JButton numActionsOtherPartButton, Vector<JButton> timeButtons, 
      Vector<JButton> digitButtons, Vector<JButton> operatorButtons, 
      Vector<JButton> otherButtons, JTextArea echoedTextField, 
      JTextField parentTextField, String attDescription) {
    super(owner, true);
    
    this.echoedTextField = echoedTextField;
    this.parentTextField = parentTextField;

    // Set window title:
    setTitle("Button Pad - " + attDescription);

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();
    mainPane.setPreferredSize(new Dimension(660, 477));

    // Top panel:
    JPanel topPane = new JPanel(new GridLayout(0, 3, 3, 3));
    // add buttons to pane:
    topPane.add(inputButton);
    topPane.add(attributeThisPartButton);
    topPane.add(attributeOtherPartButton);
    topPane.add(numObjectsButton);
    topPane.add(numActionsThisPartButton);
    topPane.add(numActionsOtherPartButton);
    // time buttons:
    for (int i = 0; i < timeButtons.size(); i++) {
      topPane.add(timeButtons.elementAt(i));
    }

    // Middle panel:
    JPanel middlePane = new JPanel();
    middlePane.add(new JLabel("Constants, operators, other:"));

    // Bottom panel:
    JPanel bottomPane = new JPanel(new GridLayout(0, 4, 3, 3));
    // digit buttons:
    for (int i = 0; i < digitButtons.size(); i++) {
      bottomPane.add(digitButtons.elementAt(i));
    }
    // operator buttons:
    for (int i = 0; i < operatorButtons.size(); i++) {
      bottomPane.add(operatorButtons.elementAt(i));
    }
    // other buttons:
    for (int i = 0; i < otherButtons.size(); i++) {
      bottomPane.add(otherButtons.elementAt(i));
    }

    // Echoed panel:
    JScrollPane echoedPane = new JScrollPane(echoedTextField);
    echoedPane.setHorizontalScrollBarPolicy(
    		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    echoedPane.setPreferredSize(new Dimension(660, 100));

    // Okay panel:
    JPanel okPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    okPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    okPane.add(cancelButton);

    // Add panes to main pane:
    mainPane.add(topPane);
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(660, 1));
    mainPane.add(separator1);
    mainPane.add(middlePane);
    mainPane.add(bottomPane);
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(660, 2));
    mainPane.add(separator2);
    mainPane.add(echoedPane);
    mainPane.add(okPane);

    // Set main window frame properties:
    setBackground(Color.black);
    setContentPane(mainPane);
    validate();
    pack();
    repaint();
    toFront();
    // Make it show up in the center, lower half of the screen, slightly above
    // center:
    Point ownerLoc = owner.getLocationOnScreen();
    Point thisLoc = new Point();
    thisLoc.setLocation((ownerLoc.getX() + (owner.getWidth() / 2) - (this
        .getWidth() / 2)), (ownerLoc.getY() + (owner.getHeight() / 2) - 200));
    setLocation(thisLoc);
    setVisible(true);
  }

  public void okButtonPressed() {
    parentTextField.setText(echoedTextField.getText());
    setVisible(false);
    dispose();
  }

  // handles user actions
  public void actionPerformed(ActionEvent evt) { 
    Object source = evt.getSource();
    if (source == okButton) {
      okButtonPressed();
    }
    else if (source == cancelButton) {
      setVisible(false);
      dispose();
    }
  }
}