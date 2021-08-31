/* This class defines an autonomous action type destroyer */

package simse.modelbuilder.actionbuilder;

public class AutonomousActionTypeDestroyer extends ActionTypeDestroyer
    implements Cloneable {
  public AutonomousActionTypeDestroyer(String name, ActionType action) {
    super(name, action);
  }

  public Object clone() {
    AutonomousActionTypeDestroyer cl = (AutonomousActionTypeDestroyer) (super
        .clone());
    return cl;
  }
}