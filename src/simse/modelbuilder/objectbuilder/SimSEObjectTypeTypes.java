/* This is a reference class for defining SimSEObject types using integers */

package simse.modelbuilder.objectbuilder;

public class SimSEObjectTypeTypes {
  public static final int EMPLOYEE = 1;
  public static final int ARTIFACT = 2;
  public static final int TOOL = 3;
  public static final int PROJECT = 4;
  public static final int CUSTOMER = 5;

  public SimSEObjectTypeTypes() {}

  // retursn the text corresponding to the object type
  public static String getText(int objType) { 
    switch (objType) {
			case EMPLOYEE: {
				return "Employee";
			}
			case ARTIFACT: {
				return "Artifact";
			}
			case TOOL: {
				return "Tool";
			}
			case PROJECT: {
				return "Project";
			}
			case CUSTOMER: {
				return "Customer";
			}
		}
    return "Error -- Invalid type";
  }

  // returns the integer corresponding to the object type
  public static int getIntRepresentation(String objType) {
    if (objType.equals("Employee")) {
      return EMPLOYEE;
    } else if (objType.equals("Artifact")) {
      return ARTIFACT;
    } else if (objType.equals("Tool")) {
      return TOOL;
    } else if (objType.equals("Project")) {
      return PROJECT;
    } else if (objType.equals("Customer")) {
      return CUSTOMER;
    } else {
      return 0;
    }
  }

  public static String[] getAllTypesAsStrings() {
    String[] types = { "Employee", "Artifact", "Tool", "Project", "Customer" };
    return types;
  }
}