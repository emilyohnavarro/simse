/* This class defines a generic object for use in SimSE */

package simse.modelbuilder.startstatebuilder;

import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.SimSEObjectType;

import java.util.Vector;

public class SimSEObject implements Cloneable {
  private SimSEObjectType objType; // SimSEObjectType that this object is 
  																 // instantiating
  private Vector<InstantiatedAttribute> instantiatedAttributes; // instantiated 
  																															// attributes of
  																															// this object

  public SimSEObject(Vector<InstantiatedAttribute> attrs, SimSEObjectType t) {
    instantiatedAttributes = attrs;
    objType = t;
  }

  public SimSEObject(SimSEObjectType t) {
    instantiatedAttributes = new Vector<InstantiatedAttribute>();
    objType = t;
//    Vector<Attribute> atts = objType.getAllAttributes();
//    for (int i = 0; i < atts.size(); i++) {
//      Attribute a = atts.elementAt(i);
//      InstantiatedAttribute newInstAtt = new InstantiatedAttribute(a);
//    }
  }

  public Object clone() {
    try {
      SimSEObject cl = (SimSEObject) (super.clone());
      cl.objType = (SimSEObjectType) (objType.clone());
      Vector<InstantiatedAttribute> clonedAtts = 
      	new Vector<InstantiatedAttribute>();
      for (int i = 0; i < instantiatedAttributes.size(); i++) {
        clonedAtts
            .add((InstantiatedAttribute) 
            		((instantiatedAttributes.elementAt(i)).clone()));
      }
      cl.instantiatedAttributes = clonedAtts;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public String getName() {
    return objType.getName();
  }

  public Vector<InstantiatedAttribute> getAllAttributes() {
    return instantiatedAttributes;
  }

  public SimSEObjectType getSimSEObjectType() {
    return objType;
  }

  /*
   * returns the instantiated attribute that is the "key" of this object -- the
   * attribute that must be unique
   */
  public InstantiatedAttribute getKey() { 
    for (int i = 0; i < instantiatedAttributes.size(); i++) {
      InstantiatedAttribute tempAttr = instantiatedAttributes.elementAt(i);
      if (tempAttr.getAttribute().isKey()) {
        return tempAttr;
      }
    }
    return null;
  }

  public void setInstantiatedAttributes(Vector<InstantiatedAttribute> attrs) {
    instantiatedAttributes = attrs;
  }

  // returns the number of attributes this object has
  public int getNumAttributes() { 
    return instantiatedAttributes.size();
  }

  // returns the instantiated attribute with the specified name
  public InstantiatedAttribute getAttribute(String name) { 
    for (int i = 0; i < instantiatedAttributes.size(); i++) {
      InstantiatedAttribute a = instantiatedAttributes.elementAt(i);
      if (a.getAttribute().getName().equals(name)) {
        return a;
      }
    }
    return null;
  }

  // adds the instantiated attribute to this object
  public void addAttribute(InstantiatedAttribute att) { 
    if (!hasAttribute(att.getAttribute())) { // doesn't already have this
                                           	 // attribute
      instantiatedAttributes.add(att);
    }
  }

  /*
   * returns true if this object has the specified attribute as an instantiated
   * attribute, false otherwise
   */
  public boolean hasAttribute(Attribute att) { 
    for (int i = 0; i < instantiatedAttributes.size(); i++) {
      InstantiatedAttribute instAtt = instantiatedAttributes.elementAt(i);
      if ((instAtt.getAttribute().getName().equals(att.getName()))) {
        return true;
      }
    }
    return false;
  }

  /*
   * checks to make sure that each attribute in the object type has a
   * corresponding instantiated attribute in this SimSEObject, and if not,
   * creates one for it
   */
  public void updateInstantiatedAttributes() { 
    Vector<Attribute> atts = objType.getAllAttributes();
    for (int i = 0; i < atts.size(); i++) {
      Attribute a = atts.elementAt(i);
      if (!hasAttribute(a)) { // doesn't have this attribute
        instantiatedAttributes.add(new InstantiatedAttribute(a));
      }
    }
  }
}