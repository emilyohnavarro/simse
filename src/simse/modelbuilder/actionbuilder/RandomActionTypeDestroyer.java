/* This class defines a random action type destroyer */

package simse.modelbuilder.actionbuilder;

public class RandomActionTypeDestroyer extends ActionTypeDestroyer implements
    Cloneable {
  private double frequency; // the frequency per clock tick of this action being
                            // destroyed when all of the destroyer conditions
                            // are met

  public RandomActionTypeDestroyer(String name, ActionType action, 
  		double frequency) {
    super(name, action);
    this.frequency = frequency;
  }

  public RandomActionTypeDestroyer(String name, ActionType action) {
    super(name, action);
  }

  public Object clone() {
    RandomActionTypeDestroyer cl = (RandomActionTypeDestroyer) (super.clone());
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