/* This class represents a generic attribute of a SimSE object type */

package simse.modelbuilder.objectbuilder;

public abstract class Attribute implements Cloneable {
  private String name; // name of the attribute
  private int type; // type of the attribute (defined by AttributeTypes class)
  private boolean visible; // whether or not this attribute should be visible to
                           // the player of the simulation
  private boolean key; // whether or not this attribute is the key for this
                       // object type -- i.e., must be unique
  private boolean visibleOnCompletion; // whether or not this attribute should
                                       // be visible to the player at the end of
                                       // the game

  public Attribute(String name, int type, boolean visible, boolean key, 
  		boolean visibleOnCompletion) {
    this.name = name;
    this.type = type;
    this.visible = visible;
    this.key = key;
    this.visibleOnCompletion = visibleOnCompletion;
  }

  public Object clone() {
    try {
      Attribute cl = (Attribute) (super.clone());
      cl.name = name;
      cl.type = type;
      cl.visible = visible;
      cl.key = key;
      cl.visibleOnCompletion = visibleOnCompletion;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  public boolean isVisible() {
    return visible;
  }

  public boolean isKey() {
    return key;
  }

  public boolean isVisibleOnCompletion() {
    return visibleOnCompletion;
  }

  public void setName(String newName) {
    name = new String(newName);
  }

  public void setType(int newType) {
    type = newType;
  }

  public void setVisible(boolean vis) {
    visible = vis;
  }

  public void setKey(boolean k) {
    key = k;
  }

  public void setVisibleOnCompletion(boolean visEnd) {
    visibleOnCompletion = visEnd;
  }
}