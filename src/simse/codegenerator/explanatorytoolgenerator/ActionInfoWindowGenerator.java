/*
 * This class is responsible for generating all of the code for the 
 * ActionInfoWindow class in the explanatory tool
 */

package simse.codegenerator.explanatorytoolgenerator;

import simse.codegenerator.CodeGeneratorConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ActionInfoWindowGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into

  public ActionInfoWindowGenerator(File directory) {
    this.directory = directory;
  }

  public void generate() {
    File actWindowFile = new File(directory,
        ("simse\\explanatorytool\\ActionInfoWindow.java"));
    if (actWindowFile.exists()) {
      actWindowFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(actWindowFile);
      writer
          .write("/* File generated by: simse.codegenerator.explanatorytoolgenerator.ActionInfoWindowGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.explanatorytool;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import javax.swing.*;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import java.awt.Dimension;");
      writer.write(NEWLINE);
      writer.write("import java.awt.event.WindowAdapter;");
      writer.write(NEWLINE);
      writer.write("import java.awt.event.WindowEvent;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public class ActionInfoWindow extends JFrame {");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      
      // constructor:
      writer.write("public ActionInfoWindow(JFrame owner, String actionName, simse.adts.actions.Action action, int clockTick) {");
      writer.write(NEWLINE);
      writer.write("super(actionName + \" Info for Clock Tick \" + clockTick);");
      writer.write(NEWLINE);
      writer.write("// Create main panel:");
      writer.write(NEWLINE);
      writer.write("JTabbedPane mainPane = new JTabbedPane();");
      writer.write(NEWLINE);
      writer.write("mainPane.setPreferredSize(new Dimension(900, 550));");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// add action info panel:");
      writer.write(NEWLINE);
      writer.write("ActionInfoPanel actionPanel = new ActionInfoPanel(action);");
      writer.write(NEWLINE);
      writer.write("mainPane.addTab(\"Action Info\", actionPanel);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// add rule info panel:");
      writer.write(NEWLINE);
      writer.write("RuleInfoPanel rulePanel = new RuleInfoPanel(this, action);");
      writer.write(NEWLINE);
      writer.write("mainPane.addTab(\"Rule Info\", rulePanel);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Set main window frame properties:");
      writer.write(NEWLINE);
      writer.write("mainPane.setOpaque(true);");
      writer.write(NEWLINE);
      writer.write("addWindowListener(new ExitListener());");
      writer.write(NEWLINE);
      writer.write("setContentPane(mainPane);");
      writer.write(NEWLINE);
      writer.write("setVisible(true);");
      writer.write(NEWLINE);
      writer.write("setSize(getLayout().preferredLayoutSize(this));");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Make it show up in the center of the screen:");
      writer.write(NEWLINE);
      writer.write("setLocationRelativeTo(null);");
      writer.write(NEWLINE);
      writer.write("validate();");
      writer.write(NEWLINE);
      writer.write("pack();");
      writer.write(NEWLINE);
      writer.write("repaint();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      
      // ExitListener class:
      writer.write("public class ExitListener extends WindowAdapter {");
      writer.write(NEWLINE);
      writer.write("public void windowClosing(WindowEvent event) {");
      writer.write(NEWLINE);
      writer.write("setVisible(false);");
      writer.write(NEWLINE);
      writer.write("dispose();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);

      writer.write(CLOSED_BRACK);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + actWindowFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}