/*
 * This class is responsible for generating all of the code for the different
 * At-A-Glance Frames in the GUI
 */

package simse.codegenerator.guigenerator;

import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.NumericalAttribute;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;

import java.util.Vector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class AtAGlanceFramesGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into
  private DefinedObjectTypes objTypes; // holds all of the defined object types
                                       // from an sso file

  public AtAGlanceFramesGenerator(DefinedObjectTypes objTypes, File directory) {
    this.objTypes = objTypes;
    this.directory = directory;
  }

  public void generate() {
    String[] types = SimSEObjectTypeTypes.getAllTypesAsStrings();
    for (String type : types) {
      generateFrameFile(type);
    }
  }

  private void generateFrameFile(String type) {
    // generate file:
    File frameFile = new File(directory,
        ("simse\\gui\\" + type + "sAtAGlanceFrame.java"));
    if (frameFile.exists()) {
      frameFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(frameFile);
      writer
          .write("/* File generated by: simse.codegenerator.guigenerator.AtAGlanceFramesGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.gui;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import simse.state.*;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import java.awt.event.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Dimension;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.text.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.event.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.table.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.border.*;");
      writer.write(NEWLINE);
      writer.write("import java.util.*;");
      writer.write(NEWLINE);
      writer.write("import java.text.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Color;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      writer
          .write("public class "
              + type
              + "sAtAGlanceFrame extends JFrame implements MouseListener, ActionListener");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("private State state;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("private JPopupMenu popup;");
      writer.write(NEWLINE);
      writer.write("private PopupListener popupListener;");
      writer.write(NEWLINE);

      // get all types:
      Vector<SimSEObjectType> types = 
      	objTypes.getAllObjectTypesOfType(
      			SimSEObjectTypeTypes.getIntRepresentation(type));
      for (SimSEObjectType tempType : types) {
        writer.write("private JTable " + tempType.getName().toLowerCase()
            + "Table;");
        writer.write(NEWLINE);
        writer.write("private " + 
        		CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) + 
        		"TableModel " + tempType.getName().toLowerCase() + "Model;");
        writer.write(NEWLINE);
        writer.write("private JPanel " + tempType.getName().toLowerCase()
            + "TitlePane;");
        writer.write(NEWLINE);
      }
      writer.write("private JPanel mainPane;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("private int realColumnIndex; // index of selected column");
      writer.write(NEWLINE);
      writer.write("private JTable selectedTable; // selected table");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // constructor:
      writer.write("public " + type + "sAtAGlanceFrame(State s,SimSEGUI gui)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("state = s;");
      writer.write(NEWLINE);
      writer.write("// Set window title:");
      writer.write(NEWLINE);
      writer.write("setTitle(\"" + type + "s At-A-Glance\");");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create tables:");
      writer.write(NEWLINE);
      writer.write("int numCols;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType tempType = types.elementAt(i);
        writer.write(tempType.getName().toLowerCase() + "Model = new "
            + CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) + 
            "TableModel(s);");
        writer.write(NEWLINE);
        writer.write(tempType.getName().toLowerCase() + "Table = new JTable("
            + tempType.getName().toLowerCase() + "Model);");
        writer.write(NEWLINE);
        writer.write(tempType.getName().toLowerCase()
            + "Table.setColumnSelectionAllowed(false);");
        writer.write(NEWLINE);
        writer.write(tempType.getName().toLowerCase()
            + "Table.setRowSelectionAllowed(false);");
        writer.write(NEWLINE);
        writer.write(tempType.getName().toLowerCase()
            + "Table.addMouseListener(this);");
        writer.write(NEWLINE);
        writer.write(tempType.getName().toLowerCase()
            + "Table.getTableHeader().setReorderingAllowed(false);");
        writer.write(NEWLINE);
        writer
            .write("// make it so that the user can make each column disappear if they want:");
        writer.write(NEWLINE);
        writer.write("numCols = " + tempType.getName().toLowerCase()
            + "Table.getColumnCount();");
        writer.write(NEWLINE);
        writer.write("for(int i=0; i<numCols; i++)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        writer.write(tempType.getName().toLowerCase()
            + "Table.getColumnModel().getColumn(i).setMinWidth(0);");
        writer.write(NEWLINE);
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
      }
      writer.write(NEWLINE);
      writer.write("// right click menu:");
      writer.write(NEWLINE);
      writer.write("popup = new JPopupMenu();");
      writer.write(NEWLINE);
      writer.write("popupListener = new PopupListener(popup,gui);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      writer.write("// Create panes:");
      writer.write(NEWLINE);

      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType tempType = types.elementAt(i);
        writer.write("JScrollPane " + tempType.getName().toLowerCase()
            + "Pane = new JScrollPane(" + tempType.getName().toLowerCase()
            + "Table);");
        writer.write(NEWLINE);
      }

      writer.write(NEWLINE);
      writer.write("// Table headers:");
      writer.write(NEWLINE);

      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType tempType = types.elementAt(i);
        writer.write(tempType.getName().toLowerCase()
            + "TitlePane = new JPanel();");
        writer.write(NEWLINE);
        writer.write(tempType.getName().toLowerCase()
            + "TitlePane.add(new JLabel(\""
            + CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) + 
            "s:\"));");
        writer.write(NEWLINE);
      }

      writer.write(NEWLINE);
      writer.write("// Create main pane:");
      writer.write(NEWLINE);
      writer.write("mainPane = new JPanel();");
      writer.write(NEWLINE);
      writer
          .write("mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Add panes to main pane:");
      writer.write(NEWLINE);

      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType tempType = types.elementAt(i);
        writer.write("mainPane.add(" + tempType.getName().toLowerCase()
            + "TitlePane);");
        writer.write(NEWLINE);
        writer.write("mainPane.add(" + tempType.getName().toLowerCase()
            + "Pane);");
        writer.write(NEWLINE);
      }

      writer.write(NEWLINE);
      writer.write("// Set main window frame properties:");
      writer.write(NEWLINE);
      writer.write("setBackground(Color.white);");
      writer.write(NEWLINE);
      writer.write("setContentPane(mainPane);");
      writer.write(NEWLINE);
      writer.write("setVisible(false);");
      writer.write(NEWLINE);
      writer.write("pack();");
      writer.write(NEWLINE);
      writer.write("validate();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("resetHeight();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // mouse listener functions:
      writer.write("public void mousePressed(MouseEvent me){}");
      writer.write(NEWLINE);
      writer.write("public void mouseClicked(MouseEvent me){}");
      writer.write(NEWLINE);
      writer.write("public void mouseEntered(MouseEvent me){}");
      writer.write(NEWLINE);
      writer.write("public void mouseExited(MouseEvent me){}");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // mouseReleased function:
      writer.write("public void mouseReleased(MouseEvent me)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("Point p = me.getPoint();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("if(me.isPopupTrigger())");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType tempType = types.elementAt(i);
        if (i > 0) {
          writer.write("else ");
        }
        writer.write("if(me.getComponent().equals("
            + tempType.getName().toLowerCase() + "Table)) // correct table");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        writer.write("createPopupMenu(" + tempType.getName().toLowerCase()
            + "Table, p);");
        writer.write(NEWLINE);
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
      }
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // actionPerformed function:
      writer
          .write("public void actionPerformed(ActionEvent e)	// dealing with actions generated by popup menus");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("Object source = e.getSource();");
      writer.write(NEWLINE);
      writer.write("if(source instanceof JMenuItem)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("String itemText = ((JMenuItem)source).getText();");
      writer.write(NEWLINE);
      writer.write("if(itemText.equals(\"Hide\"))");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("if(selectedTable != null)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer
          .write("selectedTable.getColumnModel().getColumn(realColumnIndex).setMaxWidth(0);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else // an item on the \"Unhide\" menu");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("if(selectedTable != null)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("TableModel model = selectedTable.getModel();");
      writer.write(NEWLINE);
      writer.write("TableColumn column = null;");
      writer.write(NEWLINE);

      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType tempType = types.elementAt(i);
        if (i > 0) { // not on first element
          writer.write("else ");
        }
        writer.write("if(model instanceof "
            + CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) + 
            "TableModel)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        writer
            .write("column = selectedTable.getColumnModel().getColumn((("
                + CodeGeneratorUtils.getUpperCaseLeading(tempType.getName())
                + "TableModel)selectedTable.getModel()).getColumnIndex(itemText));");
        writer.write(NEWLINE);
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
      }
      writer.write("if(column != null)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("column.setMinWidth(0);");
      writer.write(NEWLINE);
      writer.write("column.setMaxWidth(2147483647);");
      writer.write(NEWLINE);
      writer
          .write("column.setPreferredWidth(selectedTable.getWidth() / (selectedTable.getColumnCount() - getAllHiddenColumnIndices(selectedTable).size() + 1));");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // createPopupMenu function:
      writer.write("public void createPopupMenu(JTable table, Point p)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("popup.removeAll();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("int colIndex = table.columnAtPoint(p);");
      writer.write(NEWLINE);
      writer
          .write("realColumnIndex = table.convertColumnIndexToModel(colIndex);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("Vector<Integer> hiddenCols = getAllHiddenColumnIndices(table);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer
          .write("if((realColumnIndex >= 0) || (hiddenCols.size() > 0)) // user clicked on a column and/or there is at least one hidden column");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("if(realColumnIndex >= 0)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("JMenuItem hideItem = new JMenuItem(\"Hide\");");
      writer.write(NEWLINE);
      writer.write("hideItem.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("popup.add(hideItem);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer
          .write("if(hiddenCols.size() > 0) // there is at least one hidden column");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("JMenu unhideMenu = new JMenu(\"Unhide\");");
      writer.write(NEWLINE);
      writer.write("for(int i=0; i<hiddenCols.size(); i++)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer
          .write("int index = hiddenCols.elementAt(i).intValue();");
      writer.write(NEWLINE);
      writer
          .write("JMenuItem tempItem = new JMenuItem(table.getColumnName(index));");
      writer.write(NEWLINE);
      writer.write("tempItem.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("unhideMenu.add(tempItem);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer
          .write("if(popup.getComponents().length > 0) // already has the hide menu item");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("popup.addSeparator();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("popup.add(unhideMenu);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("addMouseListener(popupListener);");
      writer.write(NEWLINE);
      writer.write("popup.show(table, (int)p.getX(), (int)p.getY());");
      writer.write(NEWLINE);
      writer.write("selectedTable = table;");
      writer.write(NEWLINE);
      writer.write("repaint();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // update function:
      writer.write("public void update()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("DefaultTableCellRenderer rightAlignRenderer = new DefaultTableCellRenderer();");
      writer.write(NEWLINE);
  		writer.write("rightAlignRenderer.setHorizontalAlignment(JLabel.RIGHT);");
  		writer.write(NEWLINE);
      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType tempType = types.elementAt(i);
        writer.write(tempType.getName().toLowerCase() + "Model.update();");
        writer.write(NEWLINE);
        
        // right-aligning for all numerical columns:
    		writer.write("if (!state.getClock().isStopped()) { // game not over");
    		writer.write(NEWLINE);
    		
    		Vector<Attribute> visibleAtts = tempType.getAllVisibleAttributes();
    		for (int j = 0; j < visibleAtts.size(); j++) {
    		  Attribute att = visibleAtts.get(j);
    		  if (att instanceof NumericalAttribute) {
	    		  writer.write(tempType.getName().toLowerCase() + 
	    		      "Table.getColumnModel().getColumn(" + 
	    		      tempType.getName().toLowerCase() + "Model.getColumnIndex(\"" + 
	    		      att.getName() + "\")).setCellRenderer(rightAlignRenderer);");
	    		  writer.write(NEWLINE);
    		  }
    		}
    		writer.write(CLOSED_BRACK);
    		writer.write(NEWLINE);
    		writer.write("else { // game over");
    		writer.write(NEWLINE);
    		Vector<Attribute> visibleOnCompletionAtts = 
    		  tempType.getAllVisibleOnCompletionAttributes();
    		for (int j = 0; j < visibleOnCompletionAtts.size(); j++) {
    		  Attribute att = visibleOnCompletionAtts.get(j);
    		  if (att instanceof NumericalAttribute) {
	    		  writer.write(tempType.getName().toLowerCase() + 
	    		      "Table.getColumnModel().getColumn(" + 
	    		      tempType.getName().toLowerCase() + "Model.getColumnIndex(\"" + 
	    		      att.getName() + "\")).setCellRenderer(rightAlignRenderer);");
	    		  writer.write(NEWLINE);
    		  }
    		}
    		writer.write(CLOSED_BRACK);
    		writer.write(NEWLINE);
        
        writer.write(tempType.getName().toLowerCase() + "Table.update("
            + tempType.getName().toLowerCase() + "Table.getGraphics());");
        writer.write(NEWLINE);
      }
      writer.write("resetHeight();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);

      // resetHeight function:
      writer.write("private void resetHeight()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("// Set appropriate height:");
      writer.write(NEWLINE);
      writer.write("double height = 0;");
      writer.write(NEWLINE);
      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType tempType = types.elementAt(i);
        writer.write("height += ((" + tempType.getName().toLowerCase()
            + "Table.getRowHeight() + (" + tempType.getName().toLowerCase()
            + "Table.getRowMargin() * 2)) * ("
            + tempType.getName().toLowerCase() + "Table.getRowCount() + 1));");
        writer.write(NEWLINE);
        writer.write("height += " + tempType.getName().toLowerCase()
            + "TitlePane.getSize().getHeight();");
        writer.write(NEWLINE);
      }
      writer.write(NEWLINE);
      writer
          .write("mainPane.setPreferredSize(new Dimension((int)(mainPane.getSize().getWidth()), (int)height));");
      writer.write(NEWLINE);
      writer.write("pack();");
      writer.write(NEWLINE);
      writer.write("validate();");
      writer.write(NEWLINE);
      writer.write("repaint();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // getAllHiddenColumnIndices function:
      writer.write(NEWLINE);
      writer.write("private Vector<Integer> getAllHiddenColumnIndices(JTable table)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("Vector<Integer> hiddenCols = new Vector<Integer>();");
      writer.write(NEWLINE);
      writer.write("int numCols = table.getColumnModel().getColumnCount();");
      writer.write(NEWLINE);
      writer.write("for(int i=0; i<numCols; i++)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("TableColumn col = table.getColumnModel().getColumn(i);");
      writer.write(NEWLINE);
      writer.write("if(col.getWidth() == 0) // hidden");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("hiddenCols.add(new Integer(i));");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("return hiddenCols;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + frameFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}