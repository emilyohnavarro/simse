/*
 * This class defines a data structure for holding all of the action types that
 * have been defined
 */

package simse.modelbuilder.actionbuilder;

import java.util.Vector;

public class DefinedActionTypes {
  Vector<ActionType> actions; 

  public DefinedActionTypes() {
    actions = new Vector<ActionType>();
  }

  public Vector<ActionType> getAllActionTypes() {
    return actions;
  }

  // returns the action type with the specified name
  public ActionType getActionType(String actionName) { 
    for (int i = 0; i < actions.size(); i++) {
      ActionType tempAct = actions.elementAt(i);
      if (actions.elementAt(i).getName().equals(actionName)) {
        return tempAct;
      }
    }
    return null;
  }

  public void addActionType(ActionType act) {
//	  // insert at correct alpha order:
//	  for (int i = 0; i < actions.size(); i++) {
//	    ActionType tempAct = (ActionType) actions.elementAt(i);
//	    if (act.getName().compareToIgnoreCase(tempAct.getName()) < 0) { 
//	      // should be inserted before tempAct
//	      actions.insertElementAt(act, i);
//	      return;
//	    }
//	  }
  
	  // only reaches here if actions is empty or "act" should be placed at 
	  // the end
	  actions.add(act);
  }
  
  /*
   * adds and action type at the specified position
   */
  public void addActionType(ActionType act, int position) {
    actions.insertElementAt(act, position);
  }

  // removes the specified action type from the data structure
  public void removeActionType(ActionType act) { 
    actions.remove(act);
  }

  /*
   * removes the action with the specified name and returns the position from
   * which it was removed
   */
  public int removeActionType(String name) {
    for (int i = 0; i < actions.size(); i++) {
      if (actions.elementAt(i).getName().equals(name)) {
        actions.removeElementAt(i);
        return i;
      }
    }
    return -1;
  }
  
  
  public int getIndexOf(ActionType type) {
    for (int i = 0; i < actions.size(); i++) {
      if (actions.elementAt(i).getName().equals(type.getName())) {
        return i;
      }
    }
    return -1;
  }

  // removes all existing action types from the data structure
  public void clearAll() {
    actions.removeAllElements();
  }
  
  /*
   * sorts the action types in ascending alpha order by name
   */
//  public void sort() { 
//    Vector temp = (Vector) actions.clone();
//    clearAll();
//    for (int i = 0; i < temp.size(); i++) {
//      addActionType((ActionType)temp.elementAt(i));
//    }
//  }

  // removes all rules from all action types
  public void removeAllRules() {
    for (int i = 0; i < actions.size(); i++) {
      actions.elementAt(i).removeAllRules();
    }
  }
}