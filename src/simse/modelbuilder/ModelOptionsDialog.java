/* This class defines the window through which the options for 
 * a model can be edited */

package simse.modelbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class ModelOptionsDialog extends JDialog implements ActionListener {
  private ModelOptions options; // options being edited/viewed

  private JCheckBox everyoneStopCheckBox; 
  //private JCheckBox expToolAccessCheckBox;
  //private JCheckBox branchingCheckBox;
  private JTextField iconDirField;
  private JButton iconBrowseButton;
  private JFileChooser iconDirFileChooser;
  private JTextField genDirField;
  private JButton genBrowseButton;
  private JFileChooser codeGenDirFileChooser;
  private JButton okButton; 
  private JButton cancelButton;

  public ModelOptionsDialog(JFrame owner, ModelOptions options) {
    super(owner, true);
    this.options = options;

    // Set window title:
    setTitle("Model Options");
    
    // Create file choosers:
    iconDirFileChooser = new JFileChooser();
    iconDirFileChooser.addChoosableFileFilter(new DirectoryFileFilter()); 
    iconDirFileChooser.setDialogTitle("Choose icon directory");
    iconDirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    codeGenDirFileChooser = new JFileChooser();
    codeGenDirFileChooser.addChoosableFileFilter(new DirectoryFileFilter()); 
    codeGenDirFileChooser.setDialogTitle("Choose destination directory");
    codeGenDirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    // Create main panel (box):
    Box mainPane = Box.createVerticalBox();
    mainPane.setMaximumSize(new Dimension(600, 300));

    // Create everyoneStop panel:
    JPanel everyoneStopPane = new JPanel();
    everyoneStopCheckBox = new JCheckBox("Include \"Everyone stop what " +
    		"you're doing\" menu option");
    everyoneStopCheckBox.setToolTipText("<html>Include this option on each " +
    		"employee's menu, which <br>removes them from all of their " +
    		"user-destroyable actions</html>");
    everyoneStopPane.add(everyoneStopCheckBox);
    mainPane.add(everyoneStopPane);
    
//    // Create explToolAccess panel:
//    JPanel explToolAccessPane = new JPanel();
//    expToolAccessCheckBox = new JCheckBox("Allow explanatory tool access " +
//    		"during the game");
//    expToolAccessCheckBox.setToolTipText("<html>Allow access to the " +
//    		"explanatory tool during the game,<br>not just at the end</html>");
//    explToolAccessPane.add(expToolAccessCheckBox);
//    mainPane.add(explToolAccessPane);
//    
//    // Create branching panel:
//    JPanel branchingPane = new JPanel();
//    branchingCheckBox = new JCheckBox("Allow multiple game branches");
//    branchingCheckBox.setToolTipText("<html>Allow player to start multiple" +
//    		" simultaneous branches of the game <br> from any previous point in " +
//    		"time</html>");
//    branchingPane.add(branchingCheckBox);
//    mainPane.add(branchingPane);
//    
    JSeparator separator1 = new JSeparator();
    separator1.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator1);
    
    // Create directories panel:
    Box directoriesPane = Box.createVerticalBox();
    JPanel iconDirPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    iconDirPane.add(new JLabel("Icon directory:"));
    iconDirField = new JTextField(15);
    iconDirField.setToolTipText("Directory where icons for objects are " +
    		"located");
    iconDirPane.add(iconDirField);
    iconBrowseButton = new JButton("Browse...");
    iconBrowseButton.addActionListener(this);
    iconDirPane.add(iconBrowseButton);
    directoriesPane.add(iconDirPane);
    JPanel codeGenDestDirPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    codeGenDestDirPane.add(new JLabel("Code generation destination " +
    		"directory:"));
    genDirField = new JTextField(15);
    genDirField.setToolTipText("Directory to generate code into");
    codeGenDestDirPane.add(genDirField);
    genBrowseButton = new JButton("Browse...");
    genBrowseButton.addActionListener(this);
    codeGenDestDirPane.add(genBrowseButton);
    directoriesPane.add(codeGenDestDirPane);
    mainPane.add(directoriesPane);
    
    JSeparator separator2 = new JSeparator();
    separator2.setMaximumSize(new Dimension(2900, 1));
    mainPane.add(separator2);

    // Create okCancelButton pane and buttons:
    JPanel okCancelButtonPane = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(this);
    okCancelButtonPane.add(okButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    okCancelButtonPane.add(cancelButton);
    mainPane.add(okCancelButtonPane);

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

  public void actionPerformed(ActionEvent evt) { // handles user actions
  	// get which component the action came from:
  	Object source = evt.getSource(); 

    if (source == cancelButton) { // cancel button has been pressed
      // Close window:
      setVisible(false);
      dispose();
    }

    else if (source == okButton) { // okButton has been pressed
      if (inputValid()) {
        options.setEveryoneStopOption(everyoneStopCheckBox.isSelected());
//        options.setExplanatoryToolAccessOption(
//        		expToolAccessCheckBox.isSelected());
//        options.setAllowBranchingOption(branchingCheckBox.isSelected());
        if (hasNonBlankEntry(iconDirField)) {
          options.setIconDirectory(new File(iconDirField.getText()));
        } else {
          options.setIconDirectory(null);
        }
        if (hasNonBlankEntry(genDirField)) {
          options.setCodeGenerationDestinationDirectory(new File(
              genDirField.getText()));
        } else {
          options.setCodeGenerationDestinationDirectory(null);
        }

        // Close window:
        setVisible(false);
        dispose();
      }
    }
    
    else if (source == iconBrowseButton) {
      // bring up file chooser:
      if (hasNonBlankEntry(iconDirField)) {
        File iconDir = new File(iconDirField.getText());
        if ((iconDir != null) && (iconDir.exists())) { // icon dir already set
          iconDirFileChooser.setSelectedFile(iconDir);
        } else { // no icon dir set yet
          iconDirFileChooser.setSelectedFile(new File(""));
        }
      } else { // icon dir entry blank
        iconDirFileChooser.setSelectedFile(new File(""));
      }
      int dirReturnVal = iconDirFileChooser.showOpenDialog(this);
      if (dirReturnVal == JFileChooser.APPROVE_OPTION) {
        File f = iconDirFileChooser.getSelectedFile();
        if (f.isDirectory()) { // valid
          iconDirField.setText(f.getAbsolutePath());
        }
      }
    }
    
    else if (source == genBrowseButton) {
      // bring up file chooser:
      if (hasNonBlankEntry(genDirField)) {
        File genDir = new File(genDirField.getText());
        if ((genDir != null) && (genDir.exists())) { // gen dir already set
          codeGenDirFileChooser.setSelectedFile(genDir);
        } else { // no gen dir set yet
          codeGenDirFileChooser.setSelectedFile(new File(""));
        }
      } else { // gen dir entry blank
        codeGenDirFileChooser.setSelectedFile(new File(""));
      }
      int dirReturnVal = codeGenDirFileChooser.showOpenDialog(this);
      if (dirReturnVal == JFileChooser.APPROVE_OPTION) {
        File f = codeGenDirFileChooser.getSelectedFile();
        if (f.isDirectory()) { // valid
          genDirField.setText(f.getAbsolutePath());
        }
      }
    }
  }
  
  // Initializes the form with existing data
  private void initializeForm() {
    everyoneStopCheckBox.setSelected(options.getEveryoneStopOption());
//    expToolAccessCheckBox.setSelected(options.getExplanatoryToolAccessOption());
//    branchingCheckBox.setSelected(options.getAllowBranchingOption());

    File iconDir = options.getIconDirectory();
    if (iconDir != null) {
      iconDirField.setText(iconDir.getAbsolutePath());
    }
    File codeGenDir = options.getCodeGenerationDestinationDirectory();
    if (codeGenDir != null) {
      genDirField.setText(codeGenDir.getAbsolutePath());
    }
  }
  
  private boolean inputValid() {
    boolean valid = true;
    
    // Check icon dir text:
    if (hasNonBlankEntry(iconDirField)) {
      String iconDirText = iconDirField.getText();
      File iconDir = new File(iconDirText);
      if (!iconDir.exists() || !iconDir.isDirectory()) {
        JOptionPane.showMessageDialog(null, "Cannot find icon directory: \"" +
            iconDirText + "\"", "File not Found", JOptionPane.ERROR_MESSAGE);
        valid = false;
      }
    }
    
    // Check gen dir text:
    if (hasNonBlankEntry(genDirField)) {
      String genDirText = genDirField.getText();
      File genDir = new File(genDirText);
      if (!genDir.exists() || !genDir.isDirectory()) {
        JOptionPane.showMessageDialog(null, "Cannot find code generation " +
        		"destination directory: \"" + genDirText + "\"", "File not Found",
        		JOptionPane.ERROR_MESSAGE);
        valid = false;
      }
    }
    return valid;
  }
  
  private boolean hasNonBlankEntry(JTextField field) {
    String text = field.getText();
    if ((text != null) && (text.length() != 0) && (!text.matches("\\s+"))) {
      return true;
    }
    return false;
  }
}