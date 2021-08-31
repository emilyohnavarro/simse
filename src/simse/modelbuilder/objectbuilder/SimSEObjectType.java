/* This class defines a generic object type for use in SimSE */

package simse.modelbuilder.objectbuilder;

import java.util.Vector;

public class SimSEObjectType implements Cloneable {
  private String name; // name of the object
  private Vector<Attribute> attributes; // attributes of this object
  private int type; // type of object (see SimSEObjectTypeTypes class)

  public SimSEObjectType(Vector<Attribute> attributes, int type, String name) {
    this.attributes = attributes;
    this.type = type;
    this.name = name;
  }

  public SimSEObjectType(int t, String n) {
    type = t;
    name = n;
    attributes = new Vector<Attribute>();
  }

  public SimSEObjectType() {}

  public String getName() {
    return name;
  }

  public Object clone() {
    try {
      SimSEObjectType cl = (SimSEObjectType) super.clone();
      cl.name = name;
      Vector<Attribute> clonedAtts = new Vector<Attribute>();
      for (int i = 0; i < attributes.size(); i++) {
        clonedAtts.addElement((Attribute) (attributes.elementAt(i).clone()));
      }
      cl.attributes = clonedAtts;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public Vector<Attribute> getAllAttributes() {
    return attributes;
  }
  
  public Vector<Attribute> getAllVisibleAttributes() {
    Vector<Attribute> visibleAtts = new Vector<Attribute>();
    for (int i = 0; i < attributes.size(); i++) {
      Attribute att = attributes.get(i);
      if (att.isVisible()) {
        visibleAtts.add(att);
      }
    }
    return visibleAtts;
  }
  
  public Vector<Attribute> getAllVisibleOnCompletionAttributes() {
    Vector<Attribute> visibleAtts = new Vector<Attribute>();
    for (int i = 0; i < attributes.size(); i++) {
      Attribute att = attributes.get(i);
      if (att.isVisibleOnCompletion()) {
        visibleAtts.add(att);
      }
    }
    return visibleAtts;
  }
  
  public int getNumVisibleAttributes() {
    return getAllVisibleAttributes().size();
  }
  
  public int getNumVisibleOnCompletionAttributes() {
    return getAllVisibleOnCompletionAttributes().size();
  }

  public int getType() {
    return type;
  }

  /*
   * returns the attribute that is the "key" of this object -- the attribute
   * that must be unique
   */
  public Attribute getKey() {
    for (int i = 0; i < attributes.size(); i++) {
      Attribute tempAttr = attributes.elementAt(i);
      if (tempAttr.isKey()) {
        return tempAttr;
      }
    }
    return null;
  }

  // returns true if this object has a key, false otherwise
  public boolean hasKey() { 
    for (int i = 0; i < attributes.size(); i++) {
      Attribute tempAttr = attributes.elementAt(i);
      if (tempAttr.isKey()) {
        return true;
      }
    }
    return false;
  }

  public void setName(String newName) {
    name = newName;
  }

  public void setAttributes(Vector<Attribute> attrs) {
    attributes = attrs;
  }

  public void setType(int newType) {
    type = newType;
  }

  // adds the attribute to the end of the Vector
  public void addAttribute(Attribute a) {
    attributes.add(a); 
  }

  // adds the attribute at the specified index
  public void addAttribute(Attribute a, int index) {
    attributes.add(index, a);
  }

  /*
   * removes the attribute with the specified name and returns the position it
   * removed it from
   */
  public int removeAttribute(String name) { 
    for (int i = 0; i < attributes.size(); i++) {
      Attribute a = attributes.elementAt(i);
      if (a.getName().equals(name)) {
        int index = attributes.indexOf(a);
        attributes.remove(a);
        return index;
      }
    }
    return -1;
  }
  
  // Returns the index of the attribute with the specified name
  public int getAttributeIndex(String name) { 
    for (int i = 0; i < attributes.size(); i++) {
      Attribute a = attributes.elementAt(i);
      if (a.getName().equals(name)) {
        int index = attributes.indexOf(a);
        return index;
      }
    }
    return -1;
  }

  // returns the number of attributes this oibject has
  public int getNumAttributes() { 
    return attributes.size();
  }

  // returns the attribute with the specified name
  public Attribute getAttribute(String name) { 
    for (int i = 0; i < attributes.size(); i++) {
      Attribute a = attributes.elementAt(i);
      if (a.getName().equals(name)) {
        return a;
      }
    }
    return null;
  }
}