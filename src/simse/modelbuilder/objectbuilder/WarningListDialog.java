/*
 * This class defines a warning dialog that simply displays a JList of warning
 * messages
 */

package simse.modelbuilder.objectbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class WarningListDialog extends JFrame implements ActionListener {

  private JList warningList; // list of warnings
  private JButton okButton; 

  public WarningListDialog(Vector<String> warnings, String title) {
    // Set window title:
    setTitle(title);

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();

    // Create top pane:
    warningList = new JList();
    warningList.setListData(warnings);
    warningList.setVisibleRowCount(10); // make 10 items visible at a time
    JScrollPane topPane = new JScrollPane(warningList);
    topPane.setPreferredSize(new Dimension(900, 250));

    // Create bottom pane:
    JPanel bottomPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    bottomPane.add(okButton);

    // Add panes to main pane:
    mainPane.add(topPane);
    mainPane.add(bottomPane);

    // Set main window frame properties:
    setBackground(Color.black);
    setContentPane(mainPane);
    validate();
    pack();
    repaint();
    // Make it show up in the center of the screen:
    setLocationRelativeTo(null);
    setVisible(true);
    toFront();
    requestFocus();
  }

  // handles user actions
  public void actionPerformed(ActionEvent evt) { 
    Object source = evt.getSource(); // get which component the action came from

    if (source == okButton) { // ok button has been pressed
      // Close window:
      setVisible(false);
      dispose();
    }
  }
}