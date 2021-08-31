/* This class defines a participant in an action type */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.objectbuilder.SimSEObjectType;

import java.util.Vector;

public class ActionTypeParticipant implements Cloneable {
  private String name; // name of the participant
  private int simseObjTypeType; // employee, artifact, tool, project, or
                                // customer
  private ActionTypeParticipantQuantity quantity; // conditions on the quantity
                                                  // of this participant
  private Vector<SimSEObjectType> simseObjTypes; // Vector of SimSEObjectTypes 
  																							 // that this participant can be

  public ActionTypeParticipant(int simseObjTypeType) {
    name = new String();
    this.simseObjTypeType = simseObjTypeType;
    quantity = new ActionTypeParticipantQuantity(Guard.AT_LEAST);
    simseObjTypes = new Vector<SimSEObjectType>();
  }

  public Object clone() {
    try {
      ActionTypeParticipant cl = (ActionTypeParticipant) (super.clone());
      cl.name = name;
      cl.simseObjTypeType = simseObjTypeType;
      cl.quantity = (ActionTypeParticipantQuantity) (quantity.clone());
      Vector<SimSEObjectType> clonedTypes = new Vector<SimSEObjectType>();
      for (int i = 0; i < simseObjTypes.size(); i++) {
        clonedTypes.add((SimSEObjectType) 
        		((simseObjTypes.elementAt(i)).clone()));
      }
      cl.simseObjTypes = clonedTypes;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public String getName() {
    return name;
  }

  // returns the SimSEObjectTypeType of this participant
  public int getSimSEObjectTypeType() { 
    return simseObjTypeType;
  }

  public void setName(String newName) {
    name = newName;
  }

  public void setSimSEObjectTypeType(int newType) {
    simseObjTypeType = newType;
    simseObjTypes.removeAllElements();
  }

  // returns the SimSEObjectTypes that this participant can be of
  public Vector<SimSEObjectType> getAllSimSEObjectTypes() { 
    return simseObjTypes;
  }

  // returns the conditions on the quantity of this participant
  public ActionTypeParticipantQuantity getQuantity() { 
    return quantity;
  }

  /*
   * adds the specified SimSEObjectType to the possible types this participant
   * can be; if it's already there, it doesn't add it
   */
  public void addSimSEObjectType(SimSEObjectType newType) { 
    boolean notFound = true;
    for (int i = 0; i < simseObjTypes.size(); i++) {
      SimSEObjectType tempType = simseObjTypes.elementAt(i);
      if (tempType.getName().equals(newType.getName())) {
        notFound = false;
      }
    }
    if (notFound) { // new SimSEObjectType
      simseObjTypes.add(newType);
    }
  }

  /*
   * removes this SimSEObjectType from the possible types this participant can
   * be
   */
  public void removeSimSEObjectType(String typeName) { 
    for (int i = 0; i < simseObjTypes.size(); i++) {
      SimSEObjectType tempType = simseObjTypes.elementAt(i);
      if (tempType.getName().equals(typeName)) {
        simseObjTypes.removeElementAt(i);
      }
    }
  }

  public SimSEObjectType getSimSEObjectType(String typeName) {
    for (int i = 0; i < simseObjTypes.size(); i++) {
      SimSEObjectType tempType = simseObjTypes.elementAt(i);
      if (tempType.getName().equals(typeName)) {
        return tempType;
      }
    }
    return null;
  }
  
  public boolean hasSimSEObjectType(String typeName) {
    for (int i = 0; i < simseObjTypes.size(); i++) {
      SimSEObjectType tempType = simseObjTypes.elementAt(i);
      if (tempType.getName().equals(typeName)) {
        return true;
      }
    }
    return false;
  }

  // sets the conditions on the quantity of this participant
  public void setQuantity(ActionTypeParticipantQuantity newQuantity) { 
    quantity = newQuantity;
  }
}