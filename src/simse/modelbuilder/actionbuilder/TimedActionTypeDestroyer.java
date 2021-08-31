/* This class defines a timed action type destroyer */

package simse.modelbuilder.actionbuilder;

public class TimedActionTypeDestroyer extends ActionTypeDestroyer implements
    Cloneable {
  private int time; // the number of clock ticks that the action remains until
                    // it is automatically destroyed

  public TimedActionTypeDestroyer(String name, ActionType action, int time) {
    super(name, action);
    this.time = time;
  }

  public TimedActionTypeDestroyer(String name, ActionType action) {
    super(name, action);
  }

  public Object clone() {
    TimedActionTypeDestroyer cl = (TimedActionTypeDestroyer) (super.clone());
    cl.time = time;
    return cl;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int newTime) {
    time = newTime;
  }
}