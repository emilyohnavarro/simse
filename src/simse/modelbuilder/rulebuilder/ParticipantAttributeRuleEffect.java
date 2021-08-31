/*
 * This class defines an effect of an EffectRule on an attribute of a
 * participant
 */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.objectbuilder.Attribute;

public class ParticipantAttributeRuleEffect implements Cloneable {
  private Attribute attribute; // attribute of this effect
  private String effect; // expression describing the effect on this attribute

  public ParticipantAttributeRuleEffect(Attribute att) {
    attribute = att;
    effect = new String();
  }

  public Object clone() {
    try {
      ParticipantAttributeRuleEffect cl = 
      	(ParticipantAttributeRuleEffect) (super.clone());
      // attribute:
      cl.attribute = attribute; // NOTE: since this is a pointer to the
                                // attribute, it must remain a pointer to the
      													// attribute, even in the clone. So BE CAREFUL!!
      // effect:
      cl.effect = effect;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  // returns a COPY of the attribute
  public Attribute getAttribute() { 
    return (Attribute) attribute.clone();
  }

  public String getEffect() {
    return effect;
  }

  public void setEffect(String newEffect) {
    effect = newEffect;
  }
}

