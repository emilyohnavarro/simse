/*
 * This class defines a warning pane that simply displays a JList of warning
 * messages
 */

package simse.modelbuilder;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class WarningListPane extends JPanel implements ActionListener {
  private Vector warnings; // vector of strings, each one a warning message

  private JList warningList; // list of warnings
  private JButton clearButton;

  public WarningListPane() {
    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();
    mainPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Create title pane:
    JPanel titlePane = new JPanel(new BorderLayout());
    JPanel innerPane = new JPanel();
    JLabel titleLabel = new JLabel("Warnings:    ");
    innerPane.add(titleLabel);
    clearButton = new JButton("Clear");
    clearButton.addActionListener(this);
    innerPane.add(clearButton);
    titlePane.add(innerPane, BorderLayout.WEST);

    // Create top pane:
    warningList = new JList();
    warnings = new Vector();
    warningList.setListData(warnings);
    warningList.setVisibleRowCount(10); // make 10 items visible at a time
    JScrollPane topPane = new JScrollPane(warningList);
    topPane.setPreferredSize(new Dimension(1014, 100));

    // Add panes to main pane:*/
    mainPane.add(titlePane);
    mainPane.add(topPane);
    add(mainPane);

    repaint();
  }

  public void setWarnings(Vector w) {
    warnings = w;
    warningList.setListData(w);
  }

  public void clearWarnings() {
    warningList.setListData(new Vector());
  }

  public void actionPerformed(ActionEvent evt) // handles user actions
  {
    Object source = evt.getSource(); // get which component the action came from

    if (source == clearButton) {
      clearWarnings();
    }
  }
}