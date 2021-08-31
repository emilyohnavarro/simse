/*
 * This class defines a data structure that holds rule info for displaying in a
 * list for example
 */

package simse.modelbuilder.rulebuilder;

public abstract class RuleInfo {
  private String ruleName;
  private String actionName;

  public RuleInfo(String rName, String aName) {
    ruleName = rName;
    actionName = aName;
  }

  public String getRuleName() {
    return ruleName;
  }

  public String getActionName() {
    return actionName;
  }
}