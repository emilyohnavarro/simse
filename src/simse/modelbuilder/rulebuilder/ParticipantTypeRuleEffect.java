/*
 * This class defines an effect of an EffectRule on a particular SimSEObjectType
 * instantiation of a participant
 */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.SimSEObjectType;

import java.util.Vector;

public class ParticipantTypeRuleEffect implements Cloneable {
  private SimSEObjectType ssObjType; // pointer to the SimSEObjectType for this
                                     // participant type
  // Vector of ParticipantAttributeRuleEffects for the attributes of this 
  // participant:
  private Vector<ParticipantAttributeRuleEffect> attributeEffects; 
  private OtherActionsEffect otherActEffect; // the effect of this action on the
                                 						 // participant's other actions

  public ParticipantTypeRuleEffect(SimSEObjectType type) {
    ssObjType = type;
    // initialize an effect for each attribute:
    attributeEffects = new Vector<ParticipantAttributeRuleEffect>();
    Vector<Attribute> attributes = ssObjType.getAllAttributes();
    for (int i = 0; i < attributes.size(); i++) {
      attributeEffects.add(new ParticipantAttributeRuleEffect(
          attributes.elementAt(i)));
    }
    otherActEffect = new OtherActionsEffect();
  }

  public Object clone() {
    try {
      ParticipantTypeRuleEffect cl = (ParticipantTypeRuleEffect) (super.clone());
      // participant:
      cl.ssObjType = ssObjType; // NOTE: since this is a pointer to the object
                                // type, it must remain a pointer to the
      													// object type, even in the clone. So BE CAREFUL

      // clone attribute effects:
      Vector<ParticipantAttributeRuleEffect> clonedAttEffects = 
      	new Vector<ParticipantAttributeRuleEffect>();
      for (int i = 0; i < attributeEffects.size(); i++) {
        clonedAttEffects
            .add((ParticipantAttributeRuleEffect) 
            		(attributeEffects.elementAt(i).clone()));
      }
      cl.attributeEffects = clonedAttEffects;

      // other effect:
      cl.otherActEffect = (OtherActionsEffect) otherActEffect.clone();
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public SimSEObjectType getSimSEObjectType() {
    return ssObjType;
  }

  public Vector<ParticipantAttributeRuleEffect> getAllAttributeEffects() {
    return attributeEffects;
  }

  public OtherActionsEffect getOtherActionsEffect() {
    return otherActEffect;
  }

  // returns the effect for the attribute with the specified name
  public ParticipantAttributeRuleEffect getAttributeEffect(String attName) { 
    for (int i = 0; i < attributeEffects.size(); i++) {
      ParticipantAttributeRuleEffect tempEffect = attributeEffects.elementAt(i);
      if (tempEffect.getAttribute().getName().equals(attName)) {
        return tempEffect;
      }
    }
    return null;
  }

  // if there is already an effect for this attribute, the new one replaces it
  public void addAttributeEffect(ParticipantAttributeRuleEffect newAttEffect) {
    for (int i = 0; i < attributeEffects.size(); i++) {
      ParticipantAttributeRuleEffect tempEffect = attributeEffects.elementAt(i);
      if (tempEffect.getAttribute().getName().equals(
          newAttEffect.getAttribute().getName())) {
        attributeEffects.remove(tempEffect);
      }
    }
    attributeEffects.add(newAttEffect);
  }

  // adds the new effect in the specified position
  public void addAttributeEffect(ParticipantAttributeRuleEffect newAttEffect,
      int position) {
    attributeEffects.add(position, newAttEffect);
  }

  // removes the attribute effect for the attribute with the specified name
  public void removeAttributeEffect(String attName) { 
    for (int i = 0; i < attributeEffects.size(); i++) {
      ParticipantAttributeRuleEffect tempEffect = attributeEffects.elementAt(i);
      if (tempEffect.getAttribute().getName().equals(attName)) {
        attributeEffects.remove(tempEffect);
      }
    }
  }

  public void setOtherActionsEffect(OtherActionsEffect newEffect) {
    otherActEffect = newEffect;
  }
}

