/*
 * This class defines the constants for an EffectRule's effect on other actions
 * that a participant is in
 */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionType;

import java.util.Vector;

public class OtherActionsEffect implements Cloneable {
  public static final String ACTIVATE_ALL = "Activate all other actions";
  public static final String DEACTIVATE_ALL = "Deactivate all other actions";
  public static final String NONE = "None";
  public static final String ACTIVATE_DEACTIVATE_SPECIFIC_ACTIONS = 
    "Activate/deactivate specific actions";
  
  private String effect;
  private Vector<ActionType> actionsToActivate;
  private Vector<ActionType> actionsToDeactivate;
  
  public OtherActionsEffect() {
    effect = NONE;
    actionsToActivate = new Vector<ActionType>();
    actionsToDeactivate = new Vector<ActionType>();
  }
  
  public Object clone() {
    try {
      OtherActionsEffect cl = (OtherActionsEffect) (super.clone());
      // effect:
      cl.effect = effect; 

      // actions to activate:
      if (actionsToActivate != null) {
	      Vector<ActionType> clonedActsToActivate = new Vector<ActionType>();
	      for (int i = 0; i < actionsToActivate.size(); i++) {
	        // no cloning, since these are pointers to actions
	        clonedActsToActivate
	            .add(actionsToActivate.elementAt(i));
	      }
	      cl.actionsToActivate = clonedActsToActivate;
      }
      
      // actions to deactivate:
      if (actionsToDeactivate != null) {
	      Vector<ActionType> clonedActsToDeactivate = new Vector<ActionType>();
	      for (int i = 0; i < actionsToDeactivate.size(); i++) {
	        // no cloning, since these are pointers to actions
	        clonedActsToDeactivate
	            .add(actionsToDeactivate.elementAt(i));
	      }
	      cl.actionsToDeactivate = clonedActsToDeactivate;
      }
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }
  
  public String getEffect() {
    return effect;
  }
  
  public void setEffect(String newEffect) {
    effect = newEffect;
  }
  
  // returns a Vector of ActionTypes to activate
  public Vector<ActionType> getActionsToActivate() {
    return actionsToActivate;
  }
  
  // returns a Vector of ActionTypes to deactivate
  public Vector<ActionType> getActionsToDeactivate() {
    return actionsToDeactivate;
  }
  
  public void setActionsToActivate(Vector<ActionType> acts) {
    actionsToActivate = acts;
  }
  
  public void setActionsToDeactivate(Vector<ActionType> acts) {
    actionsToDeactivate = acts;
  }
  
  public void clearAllActionsToActivate() {
    actionsToActivate.clear();
  }
  
  public void clearAllActionsToDeactivate() {
    actionsToDeactivate.clear();
  }
  
  /*
   * returns true if action is added, false if not (if already contains the 
   * action)
   */
  public boolean addActionToActivate(ActionType act) {
    if (actionsToActivate.contains(act)) {
      return false;
    }
    else {
      actionsToActivate.add(act);
      return true;
    }
  }
  
  /*
   * returns true if action is added, false if not (if already contains the 
   * action)
   */
  public boolean addActionToDeactivate(ActionType act) {
    if (actionsToDeactivate.contains(act)) {
      return false;
    }
    else {
      actionsToDeactivate.add(act);
      return true;
    }
  }
}