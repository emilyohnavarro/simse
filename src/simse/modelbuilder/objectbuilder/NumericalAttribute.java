/*
 * This is a class for attributes that are numerical types, such as integers and
 * doubles.
 */

package simse.modelbuilder.objectbuilder;

public class NumericalAttribute extends Attribute {
  private Number min; // minimum required value of this attribute
  private Number max; // maximum possible value of this attribute
  private Integer minNumFractionDigits; // min number of digits to show after 
  																			// the decimal points for this 
  																			// attribute's value
  private Integer maxNumFractionDigits; // max number of digits to show after 
  																			// the decimal points for this 
  																			// attriubte's value

  public NumericalAttribute(String name, int type, boolean visible,
      boolean key, boolean visibleOnCompletion, Number min, Number max,
      Integer minNumFractionDigits, Integer maxNumFractionDigits) {
    super(name, type, visible, key, visibleOnCompletion);

    this.minNumFractionDigits = minNumFractionDigits;
    this.maxNumFractionDigits = maxNumFractionDigits;

    if (type == AttributeTypes.INTEGER) {
    	this.min = (min != null) ? new Integer(min.intValue()) : null;
    	this.max = (max != null) ? new Integer(max.intValue()) : null;
    } else if (type == AttributeTypes.DOUBLE) {
      this.min = (min != null) ? new Double(min.doubleValue()) : null;
      this.max = (max != null) ? new Double(max.doubleValue()) : null;
    }
  }

  /*
   * casts a NonNumericalAttribute into a new NumericalAttribute with null min
   * and max values, null min and max num fraction digits, and with the
   * specified type
   */
  public NumericalAttribute(NonNumericalAttribute n, int newType) { 
    super(n.getName(), newType, n.isVisible(), n.isKey(), n
        .isVisibleOnCompletion());
    minNumFractionDigits = null;
    maxNumFractionDigits = null;
    min = null;
    max = null;
  }

  public Integer getMinNumFractionDigits() {
    return minNumFractionDigits;
  }

  public Integer getMaxNumFractionDigits() {
    return maxNumFractionDigits;
  }

  public void setMinNumFractionDigits(Integer newNum) {
    minNumFractionDigits = newNum;
  }

  public void setMaxNumFractionDigits(Integer newNum) {
    maxNumFractionDigits = newNum;
  }

  public Number getMinValue() {
    return min;
  }

  public Number getMaxValue() {
    return max;
  }

  public void setMinValue(Integer minimum) {
    min = new Integer(minimum.intValue());
  }

  public void setMinValue(Double minimum) {
    min = new Double(minimum.doubleValue());
  }

  public void setMaxValue(Integer maximum) {
    max = new Integer(maximum.intValue());
  }

  public void setMaxValue(Double maximum) {
    max = new Double(maximum.doubleValue());
  }

  // sets the min value to boundless
  public void setMinBoundless() { 
    min = null;
  }

  // sets the max value to boundless
  public void setMaxBoundless() { 
    max = null;
  }

  // returns true if the min value is boundless, false otherwise
  public boolean isMinBoundless() {
    if (min == null) {
      return true;
    } else {
      return false;
    }
  }

  // returns true if the max value is boundless, false otherwise
  public boolean isMaxBoundless() {
    if (max == null) {
      return true;
    } else {
      return false;
    }
  }
}