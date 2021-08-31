/* This class defines a condition for an action type participant */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantConstraint;
import simse.modelbuilder.objectbuilder.SimSEObjectType;

import java.util.Vector;

public class DestroyObjectsRuleParticipantCondition implements Cloneable {
  private ActionTypeParticipant participant; // participant
  private Vector<ActionTypeParticipantConstraint> constraints; 
  // vector of ActionTypeParticipantConstraints for this participant

  public DestroyObjectsRuleParticipantCondition(ActionTypeParticipant part) {
    participant = part;
    constraints = new Vector<ActionTypeParticipantConstraint>();
    // add an empty constraint for each participant type:
    Vector<SimSEObjectType> types = participant.getAllSimSEObjectTypes();
    for (int i = 0; i < types.size(); i++) {
      addEmptyConstraint(types.elementAt(i));
    }
  }

  public Object clone() {
    try {
      DestroyObjectsRuleParticipantCondition cl = (
      		DestroyObjectsRuleParticipantCondition) (super.clone());
      /*
       * NOTE: since this is a pointer to the participant, it must remain a
       * pointer to the participant, even in the clone. So BE CAREFUL!!
       */
      cl.participant = participant; 
      Vector<ActionTypeParticipantConstraint> clonedConsts = 
      	new Vector<ActionTypeParticipantConstraint>();
      for (int i = 0; i < constraints.size(); i++) {
        clonedConsts
            .add((ActionTypeParticipantConstraint) (constraints
                .elementAt(i).clone()));
      }
      cl.constraints = clonedConsts;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  // returns a COPY of the participant
  public ActionTypeParticipant getParticipant() { 
    return (ActionTypeParticipant) (participant.clone());
  }

  public void setParticipant(ActionTypeParticipant newPart) {
    participant = newPart;
  }

  public Vector<ActionTypeParticipantConstraint> getAllConstraints() {
    return constraints;
  }

  // returns the constraint for the specified SimSEObjectType
  public ActionTypeParticipantConstraint getConstraint(SimSEObjectType type) { 
    for (int i = 0; i < constraints.size(); i++) {
      ActionTypeParticipantConstraint tempConst = constraints.elementAt(i);
      if (tempConst.getSimSEObjectType() == type) {
        return tempConst;
      }
    }
    return null;
  }

  /*
   * note to self: it's okay to just give the type name and not the 
   * SimSEObjectTypeType because all SimSEObjectTypes in one participant MUST be
   * of the same SimSEObjectTypeType!
   */
  public ActionTypeParticipantConstraint getConstraint(String typeName) { 
    for (int i = 0; i < constraints.size(); i++) {
      ActionTypeParticipantConstraint tempConst = constraints.elementAt(i);
      if (tempConst.getSimSEObjectType().getName().equals(typeName)) {
        return tempConst;
      }
    }
    return null;
  }

  /*
   * adds the specified constraint to the participant; if a constraint for its
   * SimSEObjectType is already there, the new constraint replaces it
   */
  public void addConstraint(ActionTypeParticipantConstraint newConst) {
    boolean notFound = true;
    for (int i = 0; i < constraints.size(); i++) {
      ActionTypeParticipantConstraint tempConst = constraints.elementAt(i);
      if (tempConst.getSimSEObjectType().getName().equals(
          newConst.getSimSEObjectType().getName())) {
        constraints.setElementAt(newConst, i);
        notFound = false;
        break;
      }
    }
    if (notFound) { // new constraint, not a replacement for a previous one
      constraints.add(newConst);
    }
  }

  /*
   * adds a new constraint (of the specified SimSEObjectType) that is 
   * unconstrained to this participant; if a constraint with this type is
   * already there, the new constraint replaces it
   */
  public void addEmptyConstraint(SimSEObjectType type) { 
    for (int i = 0; i < constraints.size(); i++) {
      ActionTypeParticipantConstraint tempConst = constraints.elementAt(i);
      if (tempConst.getSimSEObjectType().getName().equals(type.getName())) {
        constraints.remove(tempConst);
      }
    }
    constraints.add(new ActionTypeParticipantConstraint(type));
  }

  // removes the constraint for this SimSEObjectType
  public void removeConstraint(SimSEObjectType type) {
    for (int i = 0; i < constraints.size(); i++) {
      ActionTypeParticipantConstraint tempConst = constraints.elementAt(i);
      if (tempConst.getSimSEObjectType() == type) {
        constraints.removeElementAt(i);
      }
    }
  }

  /*
   * removes the constraint for the SimSEObjectType with the specified name;
   * note to self: it's okay to just give the type name and not the 
   * SimSEObjectTypeType because all simSEObjectTypes in one participant MUST
   * be of the same SimSEObjectTypeType!
   */
  public void removeConstraint(String typeName) { 
    for (int i = 0; i < constraints.size(); i++) {
      ActionTypeParticipantConstraint tempConst = constraints.elementAt(i);
      if (tempConst.getSimSEObjectType().getName().equals(typeName)) {
        constraints.removeElementAt(i);
      }
    }
  }

  public void setConstraints(
  		Vector<ActionTypeParticipantConstraint> newConstraints) {
    constraints = newConstraints;
  }
}