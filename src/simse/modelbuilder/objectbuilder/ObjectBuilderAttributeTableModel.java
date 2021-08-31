/*
 * This class defines the model for the attribute table used in the
 * ObjectBuilderGUI
 */

package simse.modelbuilder.objectbuilder;

import java.text.NumberFormat;

import java.util.Locale;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class ObjectBuilderAttributeTableModel extends AbstractTableModel {
  private Vector<Vector<Object>> data; // data in table
  private SimSEObjectType object; // SimSEObjectType in focus, whose attributes 
  																// are being displayed in the table
  private String[] columnNames = { "Name", "Type", "Visible?", "Min Value",
      "Max Value", "Key?", "Visible At End?", "Min Digits", "Max Digits" };
  private NumberFormat numFormat; // for displaying number values

  public ObjectBuilderAttributeTableModel(SimSEObjectType object) {
    this.object = object;
    data = new Vector<Vector<Object>>();
    numFormat = NumberFormat.getNumberInstance(Locale.US);
    refreshData();
  }

  // Creates an empty table
  public ObjectBuilderAttributeTableModel() { 
    data = new Vector<Vector<Object>>();
    numFormat = NumberFormat.getNumberInstance(Locale.US);
  }

  // sets the table to display the attributes of the specified object
  public void setObjectInFocus(SimSEObjectType obj) {
    object = obj;
    refreshData();
  }

  // returns the object currently in focus
  public SimSEObjectType getObjectInFocus() { 
    return object;
  }

  // clears the object currently in focus
  public void clearObjectInFocus() { 
    object = null;
    data.removeAllElements();
    fireTableDataChanged(); // notify listeners that data has changed
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public int getRowCount() {
    if (data.size() == 0) {
      return 0;
    }
    return data.elementAt(0).size();
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }

  public Object getValueAt(int row, int col) {
    return data.elementAt(col).elementAt(row);
  }

  public void setValueAt(Object value, int row, int col) {
    data.elementAt(col).add(value);
    fireTableCellUpdated(row, col);
  }

  // Initialize/refresh table data:
  public void refreshData() {
    Vector<Object> temp = new Vector<Object>();
    Vector<Attribute> attributes = object.getAllAttributes();

    if (attributes != null) {
      // Initialize attribute names:
      for (int i = 0; i < attributes.size(); i++) {
        temp.add(attributes.elementAt(i).getName());
      }
      if (data.isEmpty()) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 0);
      }

      // Initialize attribute types:
      temp = new Vector<Object>();

      for (int i = 0; i < attributes.size(); i++) {
        temp.add(AttributeTypes.getText(attributes.elementAt(i).getType()));
      }
      if (data.size() < 2) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 1);
      }

      // Initialize attribute visible variable:
      temp = new Vector<Object>();
      for (int i = 0; i < attributes.size(); i++) {
        temp
            .add(new Boolean(attributes.elementAt(i).isVisible()));
      }
      if (data.size() < 3) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 2);
      }

      // Initialize attribute min values:
      temp = new Vector<Object>();
      for (int i = 0; i < attributes.size(); i++) {
        Attribute tempAttr = attributes.elementAt(i);
        if (tempAttr instanceof NonNumericalAttribute) {
          temp.add("N/A"); // this field not applicable for non-numerical
                           // attributes
        } else { // numerical attribute
        	NumericalAttribute numTempAttr = (NumericalAttribute)tempAttr;
          if (numTempAttr.isMinBoundless()) {
            temp.add("Boundless");
          } else {
            temp.add(numFormat.format(numTempAttr.getMinValue()));
          }
        }
      }
      if (data.size() < 4) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 3);
      }

      // Initialize attribute max values:
      temp = new Vector<Object>();
      for (int i = 0; i < attributes.size(); i++) {
        Attribute tempAttr = attributes.elementAt(i);
        if (tempAttr instanceof NonNumericalAttribute) {
          temp.add("N/A"); // this field not applicable for non-numerical
                           // attributes
        } else { // numerical attribute
        	NumericalAttribute numTempAttr = (NumericalAttribute)tempAttr;
          if (numTempAttr.isMaxBoundless()) {
            temp.add("Boundless");
          } else {
            temp.add(numFormat.format(numTempAttr.getMaxValue()));
          }
        }
      }
      if (data.size() < 5) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 4);
      }

      // Initialize attribute key variable:
      temp = new Vector<Object>();
      for (int i = 0; i < attributes.size(); i++) {
        temp.add(new Boolean(attributes.elementAt(i).isKey()));
      }
      if (data.size() < 6) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 5);
      }

      // Initialize attribute visible at game end variable:
      temp = new Vector<Object>();
      for (int i = 0; i < attributes.size(); i++) {
        temp.add(new Boolean(attributes.elementAt(i).isVisibleOnCompletion()));
      }
      if (data.size() < 7) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 6);
      }

      // Initialize attribute min num digits values:
      temp = new Vector<Object>();
      for (int i = 0; i < attributes.size(); i++) {
        Attribute tempAttr = attributes.elementAt(i);
        if ((tempAttr instanceof NonNumericalAttribute) || 
        		(tempAttr.getType() == AttributeTypes.INTEGER)) { // non-numerical
                                                              // or integer
        																											// attribute
          temp.add("N/A"); // this field not applicable for non-numerical or
                           // integer attributes
        } else { // double attribute
        	NumericalAttribute numTempAttr = (NumericalAttribute)tempAttr;
          if (numTempAttr.getMinNumFractionDigits() == null) {
            temp.add("Boundless");
          } else {
            numFormat = NumberFormat.getIntegerInstance(Locale.US);
            temp.add(numFormat.format(numTempAttr.getMinNumFractionDigits()));
          }
        }
      }
      
      if (data.size() < 8) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 7);
      }

      // Initialize attribute max num digits values:
      temp = new Vector<Object>();
      for (int i = 0; i < attributes.size(); i++) {
        Attribute tempAttr = attributes.elementAt(i);
        if ((tempAttr instanceof NonNumericalAttribute) || 
        		(tempAttr.getType() == AttributeTypes.INTEGER)) { // non-numerical
                                                              // or integer
          temp.add("N/A"); // this field not applicable for non-numerical or
                           // integer attributes
        } else { // double attribute
        	NumericalAttribute numTempAttr = (NumericalAttribute)tempAttr;
          if (numTempAttr.getMaxNumFractionDigits() == null) {
            temp.add("Boundless");
          } else {
            temp.add(numFormat.format(numTempAttr.getMaxNumFractionDigits()));
          }
        }
      }
      if (data.size() < 9) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 8);
      }
    }
    fireTableDataChanged(); // notify listeners that table data has changed
  }

  /*
   * JTable uses this method to determine the default renderer/ editor for each
   * cell. (Copied from a Java tutorial)
   */
  public Class<?> getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }
}