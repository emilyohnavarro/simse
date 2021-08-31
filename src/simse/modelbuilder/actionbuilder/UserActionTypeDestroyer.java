/* This class defines a user action type destroyer */

package simse.modelbuilder.actionbuilder;

public class UserActionTypeDestroyer extends ActionTypeDestroyer implements
    Cloneable {
  private String menuText; // what should appear on a user's menu for destroying
                           // this action

  public UserActionTypeDestroyer(String name, ActionType action, 
  		String menuText) {
    super(name, action);
    this.menuText = menuText;
  }

  public UserActionTypeDestroyer(String name, ActionType action) {
    super(name, action);
    menuText = new String();
  }

  public Object clone() {
    UserActionTypeDestroyer cl = (UserActionTypeDestroyer) (super.clone());
    cl.menuText = menuText;
    return cl;
  }

  public String getMenuText() {
    return menuText;
  }

  public void setMenuText(String newText) {
    menuText = newText;
  }
}