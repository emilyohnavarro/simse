/* This class defines the window through which a narrative can be 
 * entered/edited */

package simse.modelbuilder.startstatebuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NarrativeDialog extends JDialog implements ActionListener {
  private CreatedObjects createdObjs; // objects already created

  private JTextArea textArea; // for entering the narrative
  private JButton okButton; // for ok'ing the creating/editing of a new
                            // attribute
  private JButton cancelButton; // for canceling the creating/editing of a new
                                // attribute

  public NarrativeDialog(JFrame owner, CreatedObjects cObjects, String title) {
    super(owner, true);
    createdObjs = cObjects;

    // Set window title:
    setTitle(title);

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

    if (!createdObjs.getStartingNarrative().equals(null) && 
    		(createdObjs.getStartingNarrative().length() > 0)) { // has a starting
    																												 // narrative
      textArea.setText(createdObjs.getStartingNarrative());
      textArea.setCaretPosition(0);
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
      createdObjs.setStartingNarrative(textArea.getText());
      // Close window:
      setVisible(false);
      dispose();
    }
  }
}