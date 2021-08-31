/*
 * This class represents attributes that are not of numerical types, for
 * example, Strings and booleans.
 */

package simse.modelbuilder.objectbuilder;

public class NonNumericalAttribute extends Attribute {
  public NonNumericalAttribute(String name, int type, boolean visible,
      boolean key, boolean visAtEnd) {
    super(name, type, visible, key, visAtEnd);
  }

  /*
   * casts a NumericalAttribute into a new NonNumericalAttribute with the 
   * specified type
   */
  public NonNumericalAttribute(NumericalAttribute n, int newType) { 
    super(n.getName(), newType, n.isVisible(), n.isKey(), n
        .isVisibleOnCompletion());
  }
}