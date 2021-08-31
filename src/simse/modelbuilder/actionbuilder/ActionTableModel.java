/*
 * This class defines the model for the action table used in the
 * ActionBuilderGUI
 */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class ActionTableModel extends AbstractTableModel {
  private Vector<Vector<Object>> data; // data in table
  private ActionType action; // ActionType in focus, whose participants are 
  													 // being displayed in the table
  private String[] columnNames = { "Name", "Quantity Guard", "Quantity",
      "Participant Meta-Type", "Possible Participant Types" };

  // column names

  public ActionTableModel(ActionType action) {
    this.action = action;
    data = new Vector<Vector<Object>>();
    refreshData();
  }

  // creates an empty table:
  public ActionTableModel() { 
    data = new Vector<Vector<Object>>();
  }

  // sets the table to display the participants of this action type
  public void setActionTypeInFocus(ActionType act) { 
    action = act;
    refreshData();
  }

  // returns the action type currently in focus
  public ActionType getActionTypeInFocus() { 
    return action;
  }

  // clears the action type currently in focus
  public void clearActionTypeInFocus() { 
    action = null;
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

  // Initializes/refreshes table data
  public void refreshData() {
    Vector<Object> temp = new Vector<Object>();
    Vector<ActionTypeParticipant> participants = action.getAllParticipants();

    if (participants != null) {
      // Initialize participant names:
      for (int i = 0; i < participants.size(); i++) {
        temp.add(participants.elementAt(i).getName());
      }
      if (data.isEmpty()) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 0);
      }

      // Initialize participant quantity guards:
      temp = new Vector<Object>();
      for (int i = 0; i < participants.size(); i++) {
        temp.add(Guard.getText(
        		participants.elementAt(i).getQuantity().getGuard()));
      }
      if (data.size() < 2) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 1);
      }

      // Initialize participant quantity:
      StringBuffer quantString = new StringBuffer();
      temp = new Vector<Object>();
      for (int i = 0; i < participants.size(); i++) {
        quantString = new StringBuffer(); // clear the string buffer
        Integer[] quant = participants.elementAt(i).getQuantity().getQuantity();
        if (quant[0] == null) {
          quantString.append("Boundless");
        } else {
          quantString.append(quant[0].intValue());
        }
        if (participants.elementAt(i).getQuantity().getGuard() == 
        	Guard.AT_LEAST_AND_AT_MOST) {
          quantString.append(", ");
          if (quant[1] == null) {
            quantString.append("Boundless");
          } else {
            quantString.append(quant[1].intValue());
          }
        }
        temp.add(quantString.toString());
      }
      if (data.size() < 3) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 2);
      }

      // Initialize participant metatype:
      temp = new Vector<Object>();
      for (int i = 0; i < participants.size(); i++) {
        temp.add(SimSEObjectTypeTypes.getText(
        		participants.elementAt(i).getSimSEObjectTypeType()));
      }
      if (data.size() < 4) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 3);
      }

      // Initialize participant types:
      temp = new Vector<Object>();
      for (int i = 0; i < participants.size(); i++) {
        Vector<SimSEObjectType> types = 
        	participants.elementAt(i).getAllSimSEObjectTypes();
        StringBuffer s = new StringBuffer();
        for (int j = 0; j < types.size(); j++) {
          s.append(types.elementAt(j).getName());
          if (j < (types.size() - 1)) { // not the last element yet
            s.append(", ");
          }
        }
        temp.add(s);
      }
      if (data.size() < 5) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 4);
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