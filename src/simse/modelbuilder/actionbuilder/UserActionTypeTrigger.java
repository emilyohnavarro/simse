/* This class defines a user action type trigger */

package simse.modelbuilder.actionbuilder;

public class UserActionTypeTrigger extends ActionTypeTrigger implements
    Cloneable {
  private String menuText; // what should appear on a user's menu for performing
                           // this action
  private boolean requiresConfirmation; // whether or not this action requires
  																		  // confirmation from the user before
  																			// starting

  public UserActionTypeTrigger(String name, ActionType action, String menuText,
      boolean requiresConfirmation) {
    super(name, action);
    this.menuText = menuText;
    this.requiresConfirmation = requiresConfirmation;
  }

  public UserActionTypeTrigger(String name, ActionType action) {
    super(name, action);
    menuText = new String();
    requiresConfirmation = false;
  }

  public Object clone() {
    UserActionTypeTrigger cl = (UserActionTypeTrigger) (super.clone());
    cl.menuText = menuText;
    cl.requiresConfirmation = requiresConfirmation;
    return cl;
  }

  public String getMenuText() {
    return menuText;
  }

  public void setMenuText(String newText) {
    menuText = newText;
  }
  
  public boolean requiresConfirmation() {
    return requiresConfirmation;
  }
  
  public void setRequiresConfirmation(boolean b) {
    requiresConfirmation = b;
  }
}