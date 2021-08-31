/* This class defines a quantity on an action type */

package simse.modelbuilder.actionbuilder;

public class ActionTypeParticipantQuantity implements Cloneable {
  private int quantityGuard; // at least, at most, exactly, etc. (defined by
                             // Guard class)
  private Integer[] quantity; // actual numerical quantity (can have up to 2
                              // numbers in it, for at least/at most guard)

  public ActionTypeParticipantQuantity(int guard) {
    quantityGuard = guard;
    quantity = new Integer[2];
  }

  public ActionTypeParticipantQuantity(int quantityGuard, Integer[] quantity) {
    this.quantityGuard = quantityGuard;
    this.quantity = quantity;
  }

  public Object clone() {
    try {
      ActionTypeParticipantQuantity cl = (ActionTypeParticipantQuantity) (super
          .clone());
      cl.quantityGuard = quantityGuard;
      Integer[] clonedQuant = new Integer[2];
      if (quantity[0] != null) {
        clonedQuant[0] = new Integer(quantity[0].intValue());
      }
      if (quantity[1] != null) {
        clonedQuant[1] = new Integer(quantity[1].intValue());
      }
      cl.quantity = clonedQuant;
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  // returns the quantity guard for this action type participant quantity
  public int getGuard() { 
    return quantityGuard;
  }

  // returns the quantity for this participant quantity
  public Integer[] getQuantity() { 
    return quantity;
  }

  // don't use this function anymore -- it's stupid & doesn't work.
  public boolean isQuantityBoundless() {
    if (quantity[0] == null) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isMinValBoundless() {
    if ((quantity[0] == null) || (quantityGuard == Guard.AT_MOST)) {
      return true;
    }
    return false;
  }

  public boolean isMaxValBoundless() {
    if (((quantityGuard == Guard.EXACTLY) && (quantity[0] != null))
        || ((quantityGuard == Guard.AT_MOST) && (quantity[0] != null))
        || ((quantityGuard == Guard.AT_LEAST_AND_AT_MOST) && 
        		(quantity[1] != null))) {
      return false;
    } else {
      return true;
    }
  }

  public Integer getMaxVal() {
    if ((quantityGuard == Guard.EXACTLY) || (quantityGuard == Guard.AT_MOST)) {
      return quantity[0];
    } else {
      return quantity[1];
    }
  }

  public Integer getMinVal() {
    if (quantityGuard != Guard.AT_MOST) {
      return quantity[0];
    } else {
      return null;
    }
  }

  public void setGuard(int guard) {
    quantityGuard = guard;
  }

  public void setQuantity(Integer[] quant) {
    quantity = quant;
  }
}