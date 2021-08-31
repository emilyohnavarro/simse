/* This class defines a rule for an action type */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionType;

public abstract class Rule implements Cloneable {
  // rule type constants:
  public static final String CREATE_OBJECTS_RULE = "Create objects Rule";
  public static final String DESTROY_OBJECTS_RULE = "Destroy objects Rule";
  public static final String EFFECT_RULE = "Effect Rule";
	
	private String name; // name of the rule
  private int timing; // timing of the rule (defined by RuleTiming class)
  private int priority; // priority of this rule for execution
  private boolean expVisibility; // whether or not this rule is visible in the
                                 // explanatory tool
  private String annotation; // annotation that describes this rule
  private boolean executeOnJoins; // whether or not this rule is to be executed
                                  // for each joining participant; only for 
  																// rules with trigger or destroyer timings
  private ActionType actType; // POINTER TO action type that this rule is
                              // attached to

  public Rule(String n, ActionType act) {
    name = n;
    timing = RuleTiming.CONTINUOUS;
    priority = -1;
    expVisibility = false;
    annotation = "";
    executeOnJoins = false;
    actType = act;
  }

  public Object clone() {
    try {
      Rule cl = (Rule) (super.clone());
      cl.name = name;
      cl.timing = timing;
      cl.priority = priority;
      cl.expVisibility = expVisibility;
      cl.annotation = annotation;
      cl.executeOnJoins = executeOnJoins;
      cl.actType = actType; // NOTE: since this is a pointer to the action type,
                            // it must remain a pointer to the action type, even
      											// in the clone. So BE CAREFUL!!
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public int getTiming() {
    return timing;
  }

  public boolean isVisibleInExplanatoryTool() {
    return expVisibility;
  }

  public int getPriority() {
    return priority;
  }

  public String getAnnotation() {
    return annotation;
  }

  public boolean getExecuteOnJoins() {
    return executeOnJoins;
  }

  public void setTiming(int newTiming) {
    timing = newTiming;
  }

  public void setPriority(int newPri) {
    priority = newPri;
  }

  public void setVisibilityInExplanatoryTool(boolean newVis) {
    expVisibility = newVis;
  }

  public void setAnnotation(String newAnn) {
    annotation = newAnn;
  }

  public void setExecuteOnJoins(boolean newJoin) {
    executeOnJoins = newJoin;
  }

  // returns a COPY of the action type
  public ActionType getActionType() { 
    return (ActionType) actType.clone();
  }
}