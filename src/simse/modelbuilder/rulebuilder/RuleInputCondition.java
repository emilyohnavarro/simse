/* This class defines a condition on an input for an EffectRule */

package simse.modelbuilder.rulebuilder;

public class RuleInputCondition implements Cloneable {
  private String guard; // e.g., >, <, =, etc.; defined by RuleInputGuard class
  private Object value; // value for this condition

  public RuleInputCondition() {
    guard = RuleInputGuard.LESS_THAN;
    value = null;
  }

  public RuleInputCondition(String grd, Object val) {
    guard = grd;
    value = val;
  }

  public Object clone() {
    try {
      RuleInputCondition cl = (RuleInputCondition) (super.clone());
      cl.guard = guard;
      if (value instanceof Integer) {
        cl.value = new Integer(((Integer) value).intValue());
      } else if (value instanceof Double) {
        cl.value = new Double(((Double) value).doubleValue());
      }
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public String getGuard() {
    return guard;
  }

  public Object getValue() {
    return value;
  }

  // returns true if there is a value for this condition, false otherwise
  public boolean isConstrained() { 
    if (value == null) {
      return false;
    } else {
      return true;
    }
  }

  public void setGuard(String newGuard) {
    guard = newGuard;
  }

  public void setValue(Object newVal) {
    value = newVal;
  }
}