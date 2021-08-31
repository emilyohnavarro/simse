/* This class defines the model for the rule table used in the RuleBuilderGUI */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionType;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class RuleTableModel extends AbstractTableModel {
  private Vector<Vector<Object>> data; // data in table
  private ActionType action; // ActionType in focus, whose participants are 
  													 // being displayed in the table
  private String[] columnNames = { "Name", "Type", "Timing" }; // column names

  public RuleTableModel(ActionType act) {
    action = act;
    data = new Vector<Vector<Object>>();
    refreshData();
  }

  // creates an empty table
  public RuleTableModel() { 
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
    return ((Vector) data.elementAt(col)).elementAt(row);
  }

  public void setValueAt(Object value, int row, int col) {
    data.elementAt(col).add(value);
    fireTableCellUpdated(row, col);
  }

  // Initialize/refresh table data:
  public void refreshData() {
    Vector<Object> temp = new Vector<Object>();
    Vector<Rule> rules = action.getAllRules();

    if (rules != null) {
      // Initialize rule names:
      for (int i = 0; i < rules.size(); i++) {
        temp.add(rules.elementAt(i).getName());
      }
      if (data.isEmpty()) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 0);
      }

      // Initialize rule types:
      temp = new Vector<Object>();
      for (int i = 0; i < rules.size(); i++) {
        if (rules.elementAt(i) instanceof CreateObjectsRule) {
          temp.add(Rule.CREATE_OBJECTS_RULE);
        } else if (rules.elementAt(i) instanceof DestroyObjectsRule) {
          temp.add(Rule.DESTROY_OBJECTS_RULE);
        } else if (rules.elementAt(i) instanceof EffectRule) {
          temp.add(Rule.EFFECT_RULE);
        }
      }
      if (data.size() < 2) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 1);
      }

      // Initialize rule timing:
      temp = new Vector<Object>();
      for (int i = 0; i < rules.size(); i++) {
        if (rules.elementAt(i).getTiming() == RuleTiming.CONTINUOUS) {
          temp.add("Continuous");
        } else if (rules.elementAt(i).getTiming() == RuleTiming.TRIGGER) {
          temp.add("Trigger");
        } else if (rules.elementAt(i).getTiming() == RuleTiming.DESTROYER) {
          temp.add("Destroyer");
        }
      }
      if (data.size() < 3) { // first-time initialization
        data.add(temp);
      } else { // refreshing value
        data.setElementAt(temp, 2);
      }
    }
    fireTableDataChanged(); // notify listeners that table data has changed
  }

  /*
   * JTable uses this method to determine the default renderer/ editor for each
   * cell. (Copied from a Java tutorial)
   */
  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }
}