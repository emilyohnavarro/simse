/* This class defines a destroyer for an action type */

package simse.modelbuilder.actionbuilder;

import java.util.Vector;

public abstract class ActionTypeDestroyer implements Cloneable {
  // destroyer type constants:
  public static final String AUTO = "Autonomous";
  public static final String USER = "User-Initiated";
  public static final String RANDOM = "Random";
  public static final String TIMED = "Timed";
	
	private String name;
	// vector of ActionTypeParticipantDestroyers for this action type destroyer:
  private Vector<ActionTypeParticipantDestroyer> participantDestroyers; 
  private String destroyerText; // text that shows up over the head of an
                                // employee participant when this is destroyed
  private int priority; // priority of execution for destroyer
  private boolean gameEnding; // whether or not this is a game-ending destroyer
  private ActionType action; // POINTER TO the action that this destroyer is
                             // attached to

  public ActionTypeDestroyer(String name, ActionType action) {
    this.name = name;
    participantDestroyers = new Vector<ActionTypeParticipantDestroyer>();
    destroyerText = new String();
    priority = -1;
    gameEnding = false;
    this.action = action;
  }

  public Object clone() {
    try {
      ActionTypeDestroyer cl = (ActionTypeDestroyer) (super.clone());
      cl.name = name;
      Vector<ActionTypeParticipantDestroyer> clonedDestroyers = 
      	new Vector<ActionTypeParticipantDestroyer>();
      for (int i = 0; i < participantDestroyers.size(); i++) {
        clonedDestroyers
            .add((ActionTypeParticipantDestroyer) 
            		((participantDestroyers.elementAt(i)).clone()));
      }
      cl.participantDestroyers = clonedDestroyers;
      cl.destroyerText = destroyerText;
      cl.priority = priority;
      cl.gameEnding = gameEnding;
      cl.action = action; // NOTE: since this is a pointer to the action, it
                          // must remain a pointer to the action, even in the
                          // clone, so BE CAREFUL!
      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public void setName(String n) {
    name = n;
  }

  public String getDestroyerText() {
    return destroyerText;
  }

  public void setDestroyerText(String newText) {
    destroyerText = newText;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int newPri) {
    priority = newPri;
  }

  public boolean isGameEndingDestroyer() {
    return gameEnding;
  }

  public void setGameEndingDestroyer(boolean val) {
    gameEnding = val;
  }

  public Vector<ActionTypeParticipantDestroyer> getAllParticipantDestroyers() {
    return participantDestroyers;
  }

  // returns the destroyer for the specified participant
  public ActionTypeParticipantDestroyer getParticipantDestroyer(
      String participantName) { 
    for (int i = 0; i < participantDestroyers.size(); i++) {
      ActionTypeParticipantDestroyer tempDest = participantDestroyers
          .elementAt(i);
      if (tempDest.getParticipant().getName().equals(participantName)) {
        return tempDest;
      }
    }
    return null;
  }

  /*
   * adds the specified participant destroyer to this ActionTypeDestroyer; if a
   * destroyer for this participant already exists, the new destroyer replaces
   * it
   */
  public void addParticipantDestroyer(ActionTypeParticipantDestroyer newDest) { 
    boolean notFound = true;
    for (int i = 0; i < participantDestroyers.size(); i++) {
      ActionTypeParticipantDestroyer tempDest = 
      	participantDestroyers.elementAt(i);
      if (tempDest.getParticipant().getName().equals(
          newDest.getParticipant().getName())) { // destroyer for this 
      																					 // participant already exists, 
      																					 // needs to be replaced
        participantDestroyers.setElementAt(newDest, i);
        notFound = false;
      }
    }
    if (notFound) { // new destroyer, not a replacement for a previous one
      participantDestroyers.add(newDest);
    }
  }

  /*
   * adds a new participant destroyer that is unconstrained to this
   * ActionTypeDestroyer; if a destroyer for this participant already exitsts,
   * this new destroyer replaces it
   */
  public void addEmptyDestroyer(ActionTypeParticipant part) { 
    for (int i = 0; i < participantDestroyers.size(); i++) {
      ActionTypeParticipantDestroyer tempDest = 
      	participantDestroyers.elementAt(i);
      if (tempDest.getParticipant().getName().equals(part.getName())) { 
      	// destroyer for this participant already exists, needs to be replaced
        participantDestroyers.remove(tempDest);
      }
    }
    participantDestroyers.add(new ActionTypeParticipantDestroyer(part));
  }

  // removes the destroyer for the participant with the specified name
  public void removeDestroyer(String partName) { 
    for (int i = 0; i < participantDestroyers.size(); i++) {
      ActionTypeParticipantDestroyer tempDest = 
      	participantDestroyers.elementAt(i);
      if (tempDest.getParticipant().getName().equals(partName)) {
        participantDestroyers.removeElementAt(i);
      }
    }
  }

  public void setDestroyers(
  		Vector<ActionTypeParticipantDestroyer> newDestroyers) {
    participantDestroyers = newDestroyers;
  }

  /*
   * morphs this ActionTypeDestroyer ot the new type of destroyer specified
   * and returns it
   */
  public ActionTypeDestroyer morph(String destroyerType) { 
    if (destroyerType.equals(AUTO)) { // autonomous destroyer type
      AutonomousActionTypeDestroyer autoDest = 
      	new AutonomousActionTypeDestroyer(name, action);
      autoDest.setDestroyers(participantDestroyers);
      autoDest.setDestroyerText(destroyerText);
      autoDest.setPriority(priority);
      autoDest.setGameEndingDestroyer(gameEnding);
      return autoDest;
    }
    if (destroyerType.equals(USER)) { // user destroyer type
      UserActionTypeDestroyer userDest = new UserActionTypeDestroyer(name,
          action);
      if (this instanceof UserActionTypeDestroyer) {
        userDest.setMenuText(((UserActionTypeDestroyer) (this)).getMenuText());
      }
      userDest.setDestroyers(participantDestroyers);
      userDest.setDestroyerText(destroyerText);
      userDest.setPriority(priority);
      userDest.setGameEndingDestroyer(gameEnding);
      return userDest;
    }
    if (destroyerType.equals(RANDOM)) { // random destroyer type
      RandomActionTypeDestroyer randomDest = new RandomActionTypeDestroyer(
          name, action);
      if (this instanceof RandomActionTypeDestroyer) {
        randomDest.setFrequency(((RandomActionTypeDestroyer) (this))
            .getFrequency());
      }
      randomDest.setDestroyers(participantDestroyers);
      randomDest.setDestroyerText(destroyerText);
      randomDest.setPriority(priority);
      randomDest.setGameEndingDestroyer(gameEnding);
      return randomDest;
    }
    if (destroyerType.equals(TIMED)) { // timed destroyer type
      TimedActionTypeDestroyer timedDest = new TimedActionTypeDestroyer(name,
          action);
      if (this instanceof TimedActionTypeDestroyer) {
        timedDest.setTime(((TimedActionTypeDestroyer) (this)).getTime());
      }
      timedDest.setDestroyers(participantDestroyers);
      timedDest.setDestroyerText(destroyerText);
      timedDest.setPriority(priority);
      timedDest.setGameEndingDestroyer(gameEnding);
      return timedDest;
    }
    return null;
  }

  // returns a COPY of the action type that this destroyer is attached to
  public ActionType getActionType() { 
    return (ActionType) action.clone();
  }
}