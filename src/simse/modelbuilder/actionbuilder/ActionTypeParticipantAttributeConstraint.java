/* This class defines a constraint on an attribute of action type participant */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.Attribute;

public class ActionTypeParticipantAttributeConstraint implements Cloneable {
  private Attribute attribute; // POINTER TO the attribute that is being
                               // constrained
  private String guard; // guard on the value (defined by AttributeGuard class)
  private Object value; // value
  private boolean score; // whether or not this attribute defines the score for
                         // a game

  public ActionTypeParticipantAttributeConstraint(Attribute attribute, 
  		String guard, Object value, boolean score) {
    this.attribute = attribute;
    this.guard = guard;
    this.value = value;
    this.score = score;
  }

  public ActionTypeParticipantAttributeConstraint(Attribute a) {
    attribute = a;
    guard = AttributeGuard.LESS_THAN;
    value = null;
    score = false;
  }

  public Object clone() {
    try {
      ActionTypeParticipantAttributeConstraint cl = 
      	(ActionTypeParticipantAttributeConstraint) (super.clone());
      cl.attribute = attribute; // NOTE: since this is a pointer to the
                                // attribute, it must remain a pointer to the
      // attribute, even in the clone. So BE CAREFUL!!
      cl.guard = guard;
      cl.score = score;
      if (value instanceof Integer) {
        cl.value = new Integer(((Integer) value).intValue());
      } else if (value instanceof Double) {
        cl.value = new Double(((Double) value).doubleValue());
      } else if (value instanceof String) {
        cl.value = value;
      } else if (value instanceof Boolean) {
        cl.value = new Boolean(((Boolean) value).booleanValue());
      }
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  // returns a COPY OF the attribute
  public Attribute getAttribute() { // returns a COPY OF the attribute
    return (Attribute) attribute.clone();
  }

  // returns true if there is a constraint for this attirubte, false otherwise
  public boolean isConstrained() { 
    if ((value == null) || (value.equals(""))) {
      return false;
    } else {
      return true;
    }
  }

  public String getGuard() {
    return guard;
  }

  public Object getValue() {
    return value;
  }

  public void setGuard(String newGuard) {
    guard = newGuard;
  }

  public void setValue(Object newVal) {
    value = newVal;
  }

  public boolean isScoringAttribute() {
    return score;
  }

  public void setScoringAttribute(boolean val) {
    score = val;
  }
}