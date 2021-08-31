/* This class defines a random action type trigger */

package simse.modelbuilder.actionbuilder;

public class RandomActionTypeTrigger extends ActionTypeTrigger implements
    Cloneable {
  private double frequency; // the frequency per clock tick of this action
                            // happening when all of the trigger conditions are
                            // met

  public RandomActionTypeTrigger(String name, ActionType action, 
  		double frequency) {
    super(name, action);
    this.frequency = frequency;
  }

  public RandomActionTypeTrigger(String name, ActionType action) {
    super(name, action);
  }

  public Object clone() {
    RandomActionTypeTrigger cl = (RandomActionTypeTrigger) (super.clone());
    cl.frequency = frequency;
    return cl;
  }

  public double getFrequency() {
    return frequency;
  }

  public void setFrequency(double newFreq) {
    frequency = newFreq;
  }
}