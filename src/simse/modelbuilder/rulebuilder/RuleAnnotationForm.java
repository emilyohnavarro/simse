/*
 * This class defines the window through which a rule's annotation can be
 * entered/edited
 */

package simse.modelbuilder.rulebuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RuleAnnotationForm extends JDialog implements ActionListener {
  private Rule rule; // the rule the annotation is attached to
  private JTextArea textArea; // for entering the narrative
  private JButton okButton; // for ok'ing the creating/editing of an annotation
  private JButton cancelButton; // for canceling the creating/editing of an 
  															// annotation

  public RuleAnnotationForm(JDialog owner, Rule rule) {
    super(owner, true);
    this.rule = rule;

    // Set window title:
    setTitle("View/Edit " + rule.getName() + " Annotation");

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();
    mainPane.setMaximumSize(new Dimension(300, 300));

    // Create text area panel:
    textArea = new JTextArea(15, 30);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(textArea,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    mainPane.add(scrollPane);

    // Create okCancelButton pane and buttons:
    JPanel okCancelButtonPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    okCancelButtonPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    okCancelButtonPane.add(cancelButton);
    mainPane.add(okCancelButtonPane);

    // initialize text if annotation exists
    textArea.setText(rule.getAnnotation());
    textArea.setCaretPosition(0);

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
    Object source = evt.getSource();
    if (source == cancelButton) { // cancel button has been pressed
      // Close window:
      setVisible(false);
      dispose();
    } else if (source == okButton) { // okButton has been pressed
      rule.setAnnotation(textArea.getText());

      // Close window:
      setVisible(false);
      dispose();
    }
  }
}