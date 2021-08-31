/* This class defines an autonomous action type trigger */

package simse.modelbuilder.actionbuilder;

public class AutonomousActionTypeTrigger extends ActionTypeTrigger implements
    Cloneable {
  public AutonomousActionTypeTrigger(String name, ActionType action) {
    super(name, action);
  }

  public Object clone() {
    AutonomousActionTypeTrigger cl = (AutonomousActionTypeTrigger) (super
        .clone());
    return cl;
  }
}