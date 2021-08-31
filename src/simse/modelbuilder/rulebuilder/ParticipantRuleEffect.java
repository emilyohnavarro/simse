/* This class defines an effect of an EffectRule on a participant */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.objectbuilder.SimSEObjectType;

import java.util.Vector;

public class ParticipantRuleEffect implements Cloneable {
  private ActionTypeParticipant participant; // pointer to the participant for
                                             // this effect
  // Vector of ParticipantTypeRuleEffects for each SimSEObject type that this 
  // participant can be:
  private Vector<ParticipantTypeRuleEffect> partTypeEffects; 

  public ParticipantRuleEffect(ActionTypeParticipant part) {
    participant = part;
    // initialize a ParticipantTypeRuleEffect for each SimSEObject type:
    partTypeEffects = new Vector<ParticipantTypeRuleEffect>();
    Vector<SimSEObjectType> types = participant.getAllSimSEObjectTypes();
    for (int i = 0; i < types.size(); i++) {
      partTypeEffects.add(new ParticipantTypeRuleEffect(types.elementAt(i)));
    }
  }

  public Object clone() {
    try {
      ParticipantRuleEffect cl = (ParticipantRuleEffect) (super.clone());
      // participant:
      cl.participant = participant; // NOTE: since this is a pointer to the
                                    // participant, it must remain a pointer to
                                    // the participant, even in the clone. So BE
      														 	// CAREFUL!!

      // clone participant type rule effects:
      Vector<ParticipantTypeRuleEffect> clonedEffects = 
      	new Vector<ParticipantTypeRuleEffect>();
      for (int i = 0; i < partTypeEffects.size(); i++) {
        clonedEffects
            .add((ParticipantTypeRuleEffect) 
            		(partTypeEffects.elementAt(i).clone()));
      }
      cl.partTypeEffects = clonedEffects;
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

  public Vector<ParticipantTypeRuleEffect> getAllParticipantTypeEffects() {
    return partTypeEffects;
  }

  // returns the effect for the specified SimSEObjectType
  public ParticipantTypeRuleEffect getParticipantTypeEffect(
  		SimSEObjectType type) { 
    for (int i = 0; i < partTypeEffects.size(); i++) {
      ParticipantTypeRuleEffect tempEffect = partTypeEffects.elementAt(i);
      if (tempEffect.getSimSEObjectType() == type) {
        return tempEffect;
      }
    }
    return null;
  }

  /*
   * note to self: it's okay to just give the type name and not the 
   * SimSEObjectTypeType because all SimSEObjectTypes in one participant must be
   * of the same SimSEObjectTypeType.
   */
  public ParticipantTypeRuleEffect getParticipantTypeRuleEffect(
  		String typeName) { 
    for (int i = 0; i < partTypeEffects.size(); i++) {
      ParticipantTypeRuleEffect tempEffect = partTypeEffects.elementAt(i);
      if (tempEffect.getSimSEObjectType().getName().equals(typeName)) {
        return tempEffect;
      }
    }
    return null;
  }

  /*
   * adds the specified effect to the participant effect; if an effect for its
   * SimSEObjectType is already there, the new effect replaces it.
   */
  public void addParticipantTypeRuleEffect(
  		ParticipantTypeRuleEffect newEffect) { 
    boolean notFound = true;
    for (int i = 0; i < partTypeEffects.size(); i++) {
      ParticipantTypeRuleEffect tempEffect = partTypeEffects.elementAt(i);
      if (tempEffect.getSimSEObjectType() == newEffect.getSimSEObjectType()) {
        partTypeEffects.setElementAt(newEffect, i);
        notFound = false;
      }
    }
    if (notFound) { // new effect, not a replacement for a previous one
      partTypeEffects.add(newEffect);
    }
  }

  // adds the new effect in the specified position
  public void addParticipantTypeRuleEffect(ParticipantTypeRuleEffect newEffect,
      int position) { 
    partTypeEffects.add(position, newEffect);
  }

  // removes the ParticipantTypeRuleEffect for this SimSEObjectType
  public void removeParticipantTypeRuleEffect(SimSEObjectType type) { 
    for (int i = 0; i < partTypeEffects.size(); i++) {
      ParticipantTypeRuleEffect tempEffect = partTypeEffects.elementAt(i);
      if (tempEffect.getSimSEObjectType() == type) {
        partTypeEffects.removeElementAt(i);
      }
    }
  }

  /*
   * removes the ParticipantTypeRuleEffect for the SimSEObjectType with the
   * specified name and type; note to self: it's okay to just give the type name
   * and not the SimSEObjectTypeType because all SimSEObjectTypes in one
   * participant MUST be of the same SimSEObjectTypeType.
   */
  public void removeParticipantTypeRuleEffect(String typeName) { 
    for (int i = 0; i < partTypeEffects.size(); i++) {
      ParticipantTypeRuleEffect tempEffect = partTypeEffects.elementAt(i);
      if (tempEffect.getSimSEObjectType().getName().equals(typeName)) {
        partTypeEffects.removeElementAt(i);
      }
    }
  }
}

