/*
 * This class defines the guards for rule input conditions like <, >, <=, >=,
 * and = etc.
 */

package simse.modelbuilder.rulebuilder;

public class RuleInputGuard {
  public static final String LESS_THAN = "<";
  public static final String GREATER_THAN = ">";
  public static final String LESS_THAN_OR_EQUAL_TO = "<=";
  public static final String GREATER_THAN_OR_EQUAL_TO = ">=";
  public static final String EQUALS = "=";

  public static String[] getGuards() {
    String[] guards = { LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL_TO,
        GREATER_THAN_OR_EQUAL_TO, EQUALS };
    return guards;
  }
}