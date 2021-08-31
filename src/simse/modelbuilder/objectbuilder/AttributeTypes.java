/* This is a reference class for defining attribute types using integers */

package simse.modelbuilder.objectbuilder;

public class AttributeTypes {
  public static final int STRING = 1;
  public static final int BOOLEAN = 2;
  public static final int INTEGER = 3;
  public static final int DOUBLE = 4;

  public AttributeTypes() {}

  // returns the text corresponding to the attribute type
  public static String getText(int attrType) { 
    switch (attrType) {
    case STRING: {
      return "String";
    }
    case BOOLEAN: {
      return "Boolean";
    }
    case INTEGER: {
      return "Integer";
    }
    case DOUBLE: {
      return "Double";
    }
    }
    return "Error -- Invalid type";
  }

  // returns the integer corresponding to the attribute type
  public static int getIntRepresentation(String attrType) { 
    if (attrType.equals("String")) {
      return STRING;
    } else if (attrType.equals("Boolean")) {
      return BOOLEAN;
    } else if (attrType.equals("Integer")) {
      return INTEGER;
    } else if (attrType.equals("Double")) {
      return DOUBLE;
    } else {
      return 0;
    }
  }

  public static String[] getAllAttributeTypes() {
    String[] types = { "String", "Boolean", "Integer", "Double" };
    return types;
  }
}