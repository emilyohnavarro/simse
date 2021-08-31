/* This class defines the types for rule input */

package simse.modelbuilder.rulebuilder;

public class InputType {
  public static final String STRING = "String";
  public static final String BOOLEAN = "Boolean";
  public static final String INTEGER = "Integer";
  public static final String DOUBLE = "Double";

  public static String[] getInputTypes() {
    String[] types = { STRING, BOOLEAN, INTEGER, DOUBLE };
    return types;
  }
}