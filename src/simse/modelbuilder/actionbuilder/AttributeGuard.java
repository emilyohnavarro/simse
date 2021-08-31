/*
 * This class defines the guards for attribute constraints like <, >, <=, >=,
 * and = etc.
 */

package simse.modelbuilder.actionbuilder;

public class AttributeGuard {
  public static final String LESS_THAN = "<";
  public static final String GREATER_THAN = ">";
  public static final String LESS_THAN_OR_EQUAL_TO = "<=";
  public static final String GREATER_THAN_OR_EQUAL_TO = ">=";
  public static final String EQUALS = "=";

  public static String getText(String guard) {
    if (guard.equals(LESS_THAN)) {
      return LESS_THAN;
    } else if (guard.equals(GREATER_THAN)) {
      return GREATER_THAN;
    } else if (guard.equals(LESS_THAN_OR_EQUAL_TO)) {
      return LESS_THAN_OR_EQUAL_TO;
    } else if (guard.equals(GREATER_THAN_OR_EQUAL_TO)) {
      return GREATER_THAN_OR_EQUAL_TO;
    } else if (guard.equals(EQUALS)) {
      return EQUALS;
    } else {
      return "Error: Invalid Attribute Guard!";
    }
  }

  public static String[] getGuards() {
    String[] guards = { LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL_TO,
        GREATER_THAN_OR_EQUAL_TO, EQUALS };
    return guards;
  }
}