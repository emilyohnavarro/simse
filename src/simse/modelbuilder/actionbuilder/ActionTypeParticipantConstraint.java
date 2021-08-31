/* This class defines a constraint on an action type participant */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.SimSEObjectType;

import java.util.Vector;

public class ActionTypeParticipantConstraint implements Cloneable {
  private SimSEObjectType objType; // pointer to the SimSEObjectType that this
  																 // constraint is on
  private ActionTypeParticipantAttributeConstraint[] constraints; 
  // constraints on this SimSEObjectType's attributes

  public ActionTypeParticipantConstraint(SimSEObjectType objType) {
    this.objType = objType;
    Vector<Attribute> atts = objType.getAllAttributes();
    constraints = new ActionTypeParticipantAttributeConstraint[atts.size()];
    for (int i = 0; i < constraints.length; i++) {
      constraints[i] = 
      	new ActionTypeParticipantAttributeConstraint(atts.elementAt(i));
    }
  }

  public Object clone() {
    try {
      ActionTypeParticipantConstraint cl = 
      	(ActionTypeParticipantConstraint) (super.clone());
      cl.objType = objType; // NOTE: since this is a pointer to the object type,
                            // it must remain a pointer to the
      											// object type, even in the clone. So BE CAREFUL!!
      ActionTypeParticipantAttributeConstraint[] clonedConsts = 
      	new ActionTypeParticipantAttributeConstraint[constraints.length];
      for (int i = 0; i < constraints.length; i++) {
        clonedConsts[i] = (ActionTypeParticipantAttributeConstraint)
        (constraints[i].clone());
      }
      cl.constraints = clonedConsts;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  // returns a COPY of the SimSEObjectType
  public SimSEObjectType getSimSEObjectType() { 
    return (SimSEObjectType) objType.clone();
  }

  public ActionTypeParticipantAttributeConstraint[] 
                                                  getAllAttributeConstraints() {
    return constraints;
  }

  public void setAttributeConstraints(
      ActionTypeParticipantAttributeConstraint[] newConstraints) {
    constraints = newConstraints;
  }

  // replaces existing constraint for the specified attribute with the new one
  public void addAttributeConstraint(
      ActionTypeParticipantAttributeConstraint newConstraint) { 
    for (int i = 0; i < constraints.length; i++) {
      if (constraints[i].getAttribute().getName().equals(
          newConstraint.getAttribute().getName())) {
        constraints[i] = newConstraint;
      }
    }
  }

  // returns the constraint on the attribute with the specified name
  public ActionTypeParticipantAttributeConstraint getAttributeConstraint(
      String name) { 
    for (int i = 0; i < constraints.length; i++) {
      if (constraints[i].getAttribute().getName().equals(name)) {
        return constraints[i];
      }
    }
    return null;
  }
}