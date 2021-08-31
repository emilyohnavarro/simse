/* This class defines an input for an EffectRule */

package simse.modelbuilder.rulebuilder;

public class RuleInput implements Cloneable {
  private String name; // name of this rule
  private String type; // type of the input (defined by InputType class)
  private String prompt; // text that is shown when the user is prompted to
                         // enter this input
  private RuleInputCondition condition; // condition for acceptable input for
                                        // this input
  private boolean cancelable; // whether or not this input can be cancelled by
                              // the player

  public RuleInput() {
    name = new String();
    type = new String(InputType.STRING);
    prompt = new String();
    condition = new RuleInputCondition();
    cancelable = true;
  }

  public RuleInput(String n) {
    name = n;
    type = new String(InputType.STRING);
    prompt = new String();
    condition = new RuleInputCondition();
    cancelable = true;
  }

  public Object clone() {
    try {
      RuleInput cl = (RuleInput) (super.clone());
      cl.name = name;
      cl.type = type;
      cl.prompt = prompt;
      cl.condition = (RuleInputCondition) condition.clone();
      cl.cancelable = cancelable;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getPrompt() {
    return prompt;
  }

  public RuleInputCondition getCondition() {
    return condition;
  }

  public boolean isCancelable() {
    return cancelable;
  }

  public void setName(String n) {
    name = n;
  }

  public void setType(String t) {
    type = t;
  }

  public void setPrompt(String p) {
    prompt = p;
  }

  public void setCondition(RuleInputCondition c) {
    condition = c;
  }

  /*
   * clears the condition value and sets its guard to the default,
   * RuleInputGurad.LESS_THAN
   */
  public void clearCondition() { 
    condition.setGuard(RuleInputGuard.LESS_THAN);
    condition.setValue(null);
  }

  public void setCancelable(boolean b) {
    cancelable = b;
  }
}

