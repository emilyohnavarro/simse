/* This class defines an action type */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.rulebuilder.CreateObjectsRule;
import simse.modelbuilder.rulebuilder.DestroyObjectsRule;
import simse.modelbuilder.rulebuilder.EffectRule;
import simse.modelbuilder.rulebuilder.Rule;
import simse.modelbuilder.rulebuilder.RuleTiming;

import java.util.Vector;

public class ActionType implements Cloneable {
  private String name;
  private boolean visibleInSimulation; // whether or not the existence of this
                                       // action should be visible in the user
                                       // interface of the simulation
  private boolean visibleInExplanatoryTool; // whether or not this action should
                                            // be visible in the user interface
                                            // of the explanatory tool
  private String description; // description of this action, to be shown in the
                              // user interface of the simulation (if visible)
  private String annotation; // a textual description of this action
  private boolean joiningAllowed; // whether or not employee participants can
  																// join this action while it is in progress
  private Vector<ActionTypeParticipant> participants; // all of the
																											// ActionTypeParticipants
																											// involved in this action
  private Vector<ActionTypeTrigger> triggers; // a Vector of ActionTypeTriggers
																							// for this ActionType

	private Vector<ActionTypeDestroyer> destroyers; // a Vector
																									// ActionTypeDestroyers for
																									// this ActionType
  private Vector<Rule> rules; // all of the Rules associated with this 
  														// ActionType

  public ActionType(String name) {
    this.name = name;
    visibleInSimulation = false;
    visibleInExplanatoryTool = false;
    description = null;
    annotation = "";
    joiningAllowed = true;
    participants = new Vector<ActionTypeParticipant>();
    triggers = new Vector<ActionTypeTrigger>();
    destroyers = new Vector<ActionTypeDestroyer>();
    rules = new Vector<Rule>();
  }

  public Object clone() {
    try {
      ActionType cl = (ActionType) (super.clone());
      cl.name = name;
      cl.visibleInSimulation = visibleInSimulation;
      cl.visibleInExplanatoryTool = visibleInExplanatoryTool;
      cl.description = description;
      cl.annotation = annotation;
      cl.joiningAllowed = joiningAllowed;
      Vector<ActionTypeTrigger> clonedTrigs = new Vector<ActionTypeTrigger>();
      for (int i = 0; i < triggers.size(); i++) {
        clonedTrigs.add((ActionTypeTrigger) (triggers.elementAt(i)).clone());
      }
      cl.triggers = clonedTrigs;
      Vector<ActionTypeDestroyer> clonedDests = 
      	new Vector<ActionTypeDestroyer>();
      for (int i = 0; i < destroyers.size(); i++) {
        clonedDests.add((ActionTypeDestroyer) 
        		(destroyers.elementAt(i)).clone());
      }
      cl.destroyers = clonedDests;
      Vector<ActionTypeParticipant> clonedParts = 
      	new Vector<ActionTypeParticipant>();
      for (int i = 0; i < participants.size(); i++) {
        clonedParts
            .add((ActionTypeParticipant) (participants.elementAt(i)).clone());
      }
      cl.participants = clonedParts;
      Vector<Rule> clonedRules = new Vector<Rule>();
      for (int i = 0; i < rules.size(); i++) {
        clonedRules.add((Rule) (rules.elementAt(i)).clone());
      }
      cl.rules = clonedRules;

      return cl;
    } catch (CloneNotSupportedException c) {
      System.out.println(c.getMessage());
    }
    return null;
  }

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public boolean isVisibleInSimulation() {
    return visibleInSimulation;
  }

  public void setVisibilityInSimulation(boolean newVis) {
    visibleInSimulation = newVis;
  }

  public boolean isVisibleInExplanatoryTool() {
    return visibleInExplanatoryTool;
  }

  public void setVisibilityInExplanatoryTool(boolean newVis) {
    visibleInExplanatoryTool = newVis;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String s) {
    description = s;
  }

  public String getAnnotation() {
    return annotation;
  }

  public void setAnnotation(String s) {
    annotation = s;
  }
  
  public boolean isJoiningAllowed() {
    return joiningAllowed;
  }
  
  public void setJoiningAllowed(boolean b) {
    joiningAllowed = b;
  }

  /*
   * returns a vector of all ActionTypeParticipants involved in this action
   * type
   */
  public Vector<ActionTypeParticipant> getAllParticipants() { 
    return participants;
  }
  
  public boolean hasParticipantOfMetaType(int metaType) {
    for (int i = 0; i < participants.size(); i++) {
      ActionTypeParticipant part = participants.elementAt(i);
      if (part.getSimSEObjectTypeType() == metaType) {
        return true;
      }
    }
    return false;
  }

  // returns a vector of all ActionTypeTriggers for this action type
  public Vector<ActionTypeTrigger> getAllTriggers() { 
    return triggers;
  }

  public ActionTypeTrigger getTrigger(String name) {
    for (int i = 0; i < triggers.size(); i++) {
      ActionTypeTrigger trigger = triggers.elementAt(i);
      if (trigger.getName().equals(name)) {
        return trigger;
      }
    }
    return null;
  }

  // returns a vector of all ActionTypeDestroyers for this action type
  public Vector<ActionTypeDestroyer> getAllDestroyers() { 
    return destroyers;
  }

  public ActionTypeDestroyer getDestroyer(String name) {
    for (int i = 0; i < destroyers.size(); i++) {
      ActionTypeDestroyer destroyer = destroyers.elementAt(i);
      if (destroyer.getName().equals(name)) {
        return destroyer;
      }
    }
    return null;
  }

  public Vector<Rule> getAllRules() {
    return rules;
  }

  // returns all rules of type CreateObjectsRule for this action type
  public Vector<CreateObjectsRule> getAllCreateObjectsRules() { 
    Vector<CreateObjectsRule> coRules = new Vector<CreateObjectsRule>();
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule instanceof CreateObjectsRule) {
        coRules.add((CreateObjectsRule)tempRule);
      }
    }
    return coRules;
  }

  // returns all rules of type DestroyObjectsRule for this action type
  public Vector<DestroyObjectsRule> getAllDestroyObjectsRules() { 
    Vector<DestroyObjectsRule> doRules = new Vector<DestroyObjectsRule>();
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule instanceof DestroyObjectsRule) {
        doRules.add((DestroyObjectsRule)tempRule);
      }
    }
    return doRules;
  }

  // returns all rules of type EffectRule
  public Vector<EffectRule> getAllEffectRules() { 
    Vector<EffectRule> eRules = new Vector<EffectRule>();
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule instanceof EffectRule) {
        eRules.add((EffectRule)tempRule);
      }
    }
    return eRules;
  }

  // returns the rule with the specified name
  public Rule getRule(String name) { 
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule.getName().equals(name)) {
        return tempRule;
      }
    }
    return null;
  }

  /*
   * adds this rule to the action type; if there already exists one with its
   * name, it replaces it with the new one
   */
  public void addRule(Rule newRule) { 
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule.getName().equals(newRule.getName())) {
        rules.removeElement(tempRule);
      }
    }
    rules.addElement(newRule);
  }

  /*
   * adds this rule to the action type at the specified index; if there already
   * exists one with its name, it removes the old one first
   */
  public void addRule(Rule newRule, int index) { 
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule.getName().equals(newRule.getName())) {
        rules.removeElement(tempRule);
      }
    }
    rules.add(index, newRule);
  }

  // removes the rule w/ the specified name from the action type
  public void removeRule(String name) { 
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule.getName().equals(name)) {
        rules.removeElement(tempRule);
      }
    }
  }

  /*
   * adds this trigger to the action type; if there already exists one with its
   * name, it replaces it with the new one
   */
  public void addTrigger(ActionTypeTrigger newTrigger) { 
    for (int i = 0; i < triggers.size(); i++) {
      ActionTypeTrigger tempTrig = triggers.elementAt(i);
      if (tempTrig.getName().equals(newTrigger.getName())) {
        triggers.removeElement(tempTrig);
      }
    }
    triggers.addElement(newTrigger);
  }

  /*
   * adds thsi trigger to the action type at the specified index; if there 
   * already exists one with its name, it first removes the old one
   */
  public void addTrigger(ActionTypeTrigger newTrigger, int index) { 
    for (int i = 0; i < triggers.size(); i++) {
      ActionTypeTrigger tempTrig = triggers.elementAt(i);
      if (tempTrig.getName().equals(newTrigger.getName())) {
        triggers.removeElement(tempTrig);
      }
    }
    triggers.add(index, newTrigger);
  }

  /*
   * removes the trigger with the specified name from the action type, and
   * returns its index
   */
  public int removeTrigger(String name) { 
    for (int i = 0; i < triggers.size(); i++) {
      ActionTypeTrigger tempTrig = triggers.elementAt(i);
      if (tempTrig.getName().equals(name)) {
        triggers.removeElement(tempTrig);
        return i;
      }
    }
    return -1;
  }

  /*
   * replaces all existing triggers with the triggers being passed in in the
   * Vector
   */
  public void setTriggers(Vector<ActionTypeTrigger> newTrigs) { 
    triggers = newTrigs;
  }
  
  public boolean hasDestroyerOfType(String destroyerType) {
    for (int i = 0; i < destroyers.size(); i++) {
      ActionTypeDestroyer tempDest = destroyers.elementAt(i);
      if (destroyerType.equals(ActionTypeDestroyer.USER)) {
        if (tempDest instanceof UserActionTypeDestroyer) {
          return true;
        }
      }
      else if (destroyerType.equals(ActionTypeDestroyer.AUTO)) {
        if (tempDest instanceof AutonomousActionTypeDestroyer) {
          return true;
        }
      }
      else if (destroyerType.equals(ActionTypeDestroyer.RANDOM)) {
        if (tempDest instanceof RandomActionTypeDestroyer) {
          return true;
        }
      }
      else if (destroyerType.equals(ActionTypeDestroyer.TIMED)) {
        if (tempDest instanceof TimedActionTypeDestroyer) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean hasTriggerOfType(String triggerType) {
    for (int i = 0; i < triggers.size(); i++) {
      ActionTypeTrigger tempTrig = triggers.elementAt(i);
      if (triggerType.equals(ActionTypeTrigger.USER)) {
        if (tempTrig instanceof UserActionTypeTrigger) {
          return true;
        }
      }
      else if (triggerType.equals(ActionTypeTrigger.AUTO)) {
        if (tempTrig instanceof AutonomousActionTypeTrigger) {
          return true;
        }
      }
      else if (triggerType.equals(ActionTypeTrigger.RANDOM)) {
        if (tempTrig instanceof RandomActionTypeTrigger) {
          return true;
        }
      }
    }
    return false;
  }

  /*
   * adds this destroyer to the action type; if there already exists one with
   * its name, it replaces it
   */
  public void addDestroyer(ActionTypeDestroyer newDestroyer) { 
    for (int i = 0; i < destroyers.size(); i++) {
      ActionTypeDestroyer tempDest = destroyers
          .elementAt(i);
      if (tempDest.getName().equals(newDestroyer.getName())) {
        destroyers.removeElement(tempDest);
      }
    }
    destroyers.addElement(newDestroyer);
  }

  /*
   * adds this destroyer to the action type at the specified index; if there 
   * exists one with its name, it first removes the old one
   */
  public void addDestroyer(ActionTypeDestroyer newDestroyer, int index) { 
    for (int i = 0; i < destroyers.size(); i++) {
      ActionTypeDestroyer tempDest = destroyers
          .elementAt(i);
      if (tempDest.getName().equals(newDestroyer.getName())) {
        destroyers.removeElement(tempDest);
      }
    }
    destroyers.add(index, newDestroyer);
  }

  /*
   * removes the destroyer with the specified name from the action type and
   * returns its index
   */
  public int removeDestroyer(String name) { 
    for (int i = 0; i < destroyers.size(); i++) {
      ActionTypeDestroyer tempDest = destroyers.elementAt(i);
      if (tempDest.getName().equals(name)) {
        destroyers.removeElement(tempDest);
        return i;
      }
    }
    return -1;
  }

  /*
   * replaces all existing destroyers with the destroyers being passed in in the
   * Vector
   */
  public void setDestroyers(Vector<ActionTypeDestroyer> newDests) { 
    destroyers = newDests;
  }

  // returns the participant with the specified name
  public ActionTypeParticipant getParticipant(String name) { 
    for (int i = 0; i < participants.size(); i++) {
      ActionTypeParticipant tempPart = (participants.elementAt(i));
      if (tempPart.getName().equals(name)) {
        return tempPart;
      }
    }
    return null;
  }
  
  // Returns the index of the participant with the specified name
  public int getParticipantIndex(String name) {
    for (int i = 0; i < participants.size(); i++) {
      ActionTypeParticipant p = participants.elementAt(i);
      if (p.getName().equals(name)) {
        int index = participants.indexOf(p);
        return index;
      }
    }
    return -1;
  }

  public void addParticipant(ActionTypeParticipant part) {
    participants.add(part);
  }

  // adds the participant at the specified position
  public void addParticipant(ActionTypeParticipant part, int index) { 
    participants.add(index, part);
  }

  /*
   * removes the participant with the specified name and returns the position
   * it removed it from; returns -1 if participant was not found
   */
  public int removeParticipant(String name) {
    for (int i = 0; i < participants.size(); i++) {
      ActionTypeParticipant tempPart = (participants.elementAt(i));
      if (tempPart.getName().equals(name)) {
        participants.removeElementAt(i);

        // remove participant triggers for this participant:
        for (int j = 0; j < triggers.size(); j++) {
          ActionTypeTrigger tempTrig = triggers.elementAt(j);
          tempTrig.removeTrigger(tempPart.getName());
        }

        // remove participant destroyer for this participant:
        for (int j = 0; j < destroyers.size(); j++) {
          ActionTypeDestroyer tempDest = destroyers.elementAt(j);
          tempDest.removeDestroyer(tempPart.getName());
        }
        return i;
      }
    }
    return -1;
  }
  
  /*
   * removes the participant with the specified name and returns the position
   * it removed it from, but does not remove the participant triggers and
   * destroyers for the participant; returns -1 if participant was not found
   */
  public int temporarilyRemoveParticipant(String name) {
    for (int i = 0; i < participants.size(); i++) {
      ActionTypeParticipant tempPart = (participants.elementAt(i));
      if (tempPart.getName().equals(name)) {
        participants.removeElementAt(i);
        return i;
      }
    }
    return -1;
  }

  public void removeParticipant(ActionTypeParticipant part) {
    participants.remove(part);
    // remove participant triggers for this participant:
    for (int j = 0; j < triggers.size(); j++) {
      ActionTypeTrigger tempTrig = triggers.elementAt(j);
      tempTrig.removeTrigger(part.getName());
    }

    // remove participant destroyer for this participant:
    for (int j = 0; j < destroyers.size(); j++) {
      ActionTypeDestroyer tempDest = destroyers.elementAt(j);
      tempDest.removeDestroyer(part.getName());
    }
  }

  // returns true if this actio has at least one trigger rule, false otherwise
  public boolean hasTriggerRules() { 
    // go through all rules:
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule.getTiming() == RuleTiming.TRIGGER) {
        return true;
      }
    }
    return false;
  }

  // returns them in prioritized order, from first to execute to last to execute
  public Vector<Rule> getAllTriggerRules() { 
    // initialize lists:
    Vector<Rule> nonPrioritizedRules = new Vector<Rule>();
    Vector<Rule> prioritizedRules = new Vector<Rule>();
    // go through each rule and add the trigger ones to the list:
    for (int j = 0; j < rules.size(); j++) {
      Rule tempRule = rules.elementAt(j);
      if (tempRule.getTiming() == RuleTiming.TRIGGER) {
        int priority = tempRule.getPriority();
        if (priority == -1) { // rule is not prioritized yet
          nonPrioritizedRules.addElement(tempRule);
        } else { // priority >= 0
          if (prioritizedRules.size() == 0) { // no elements have been added yet
                                            	// to the prioritized rule list
            prioritizedRules.add(tempRule);
          } else {
            // find the correct position to insert the rule at:
            for (int k = 0; k < prioritizedRules.size(); k++) {
              Rule tempR = prioritizedRules.elementAt(k);
              if (priority <= tempR.getPriority()) {
                prioritizedRules.insertElementAt(tempRule, k); // insert the
                                                               // rule info
                break;
              } else if (k == (prioritizedRules.size() - 1)) { // on the last
                                                             	 // element
                prioritizedRules.add(tempRule); // add the rule info to the end
                                                // of the list
                break;
              }
            }
          }
        }
      }
    }
    /* 
     * add all of the non-prioritized ruels ot the end of the prioritized rules
     * vector (not using addAll() because I'm not sure it'll maintain the
     * order):
     */
    for (int i = 0; i < nonPrioritizedRules.size(); i++) {
      prioritizedRules.add(nonPrioritizedRules.elementAt(i));
    }
    return prioritizedRules;
  }

  /*
   * returns true if this action has at least one destroyer rule, false
   * otherwise
   */
  public boolean hasDestroyerRules() { 
    // go through all rules:
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule.getTiming() == RuleTiming.DESTROYER) {
        return true;
      }
    }
    return false;
  }

  /*
   * returns them in prioritized order, from first to execute to last to
   * execute
   */
  public Vector<Rule> getAllDestroyerRules() { 
    // initialize lists:
    Vector<Rule> nonPrioritizedRules = new Vector<Rule>();
    Vector<Rule> prioritizedRules = new Vector<Rule>();
    // go through each rule and add the trigger ones to the list:
    for (int j = 0; j < rules.size(); j++) {
      Rule tempRule = rules.elementAt(j);
      if (tempRule.getTiming() == RuleTiming.DESTROYER) {
        int priority = tempRule.getPriority();
        if (priority == -1) { // rule is not prioritized yet
          nonPrioritizedRules.addElement(tempRule);
        } else { // priority >= 0
          if (prioritizedRules.size() == 0) { // no elements have been added yet
          																		// to the prioritized rule list
            prioritizedRules.add(tempRule);
          } else {
            // find the correct position to insert the rule at:
            for (int k = 0; k < prioritizedRules.size(); k++) {
              Rule tempR = prioritizedRules.elementAt(k);
              if (priority <= tempR.getPriority()) {
                prioritizedRules.insertElementAt(tempRule, k); // insert the
                                                               // rule info
                break;
              } else if (k == (prioritizedRules.size() - 1)) { // on the last
                                                             	 // element
                prioritizedRules.add(tempRule); // add the rule info to the end
                                                // of the list
                break;
              }
            }
          }
        }
      }
    }
    /*
     * add all of the non-prioritized rules to the end of the prioritized rules
     * vector (not using addAll() because I'm not sure it'll maintain the
     * order):
     */ 
    for (int i = 0; i < nonPrioritizedRules.size(); i++) {
      prioritizedRules.add(nonPrioritizedRules.elementAt(i));
    }
    return prioritizedRules;
  }

  /*
   * returns true if this action has at least one continuous rule, false
   * otherwise
   */
  public boolean hasContinuousRules() { 
    // go through all rules:
    for (int i = 0; i < rules.size(); i++) {
      Rule tempRule = rules.elementAt(i);
      if (tempRule.getTiming() == RuleTiming.CONTINUOUS) {
        return true;
      }
    }
    return false;
  }

  /*
   * returns them in prioritized order, from first to execute to last to 
   * execute
   */
  public Vector<Rule> getAllContinuousRules() { 
    // initialize lists:
    Vector<Rule> nonPrioritizedRules = new Vector<Rule>();
    Vector<Rule> prioritizedRules = new Vector<Rule>();
    // go through each rule and add the trigger ones to the list:
    for (int j = 0; j < rules.size(); j++) {
      Rule tempRule = rules.elementAt(j);
      if (tempRule.getTiming() == RuleTiming.CONTINUOUS) {
        int priority = tempRule.getPriority();
        if (priority == -1) { // rule is not prioritized yet
          nonPrioritizedRules.addElement(tempRule);
        } else { // priority >= 0
          if (prioritizedRules.size() == 0) { // no elements have been added yet
          																		// to the prioritized rule list
            prioritizedRules.add(tempRule);
          } else {
            // find the correct position to insert the rule at:
            for (int k = 0; k < prioritizedRules.size(); k++) {
              Rule tempR = prioritizedRules.elementAt(k);
              if (priority <= tempR.getPriority()) {
                prioritizedRules.insertElementAt(tempRule, k); // insert the
                                                               // rule info
                break;
              } else if (k == (prioritizedRules.size() - 1)) { // on the last
                                                             	 // element
                prioritizedRules.add(tempRule); // add the rule info to the end
                                                // of the list
                break;
              }
            }
          }
        }
      }
    }
    /*
     * add all of the non-prioritized rules to the end of the prioritized rules
     * vector (not using addAll() because I'm not sure it'll maintain the
     * order):
     */ 
    for (int i = 0; i < nonPrioritizedRules.size(); i++) {
      prioritizedRules.add(nonPrioritizedRules.elementAt(i));
    }
    return prioritizedRules;
  }

  // returns true if this action has one or more game-ending triggers
  public boolean hasGameEndingTrigger() { 
    for (int i = 0; i < triggers.size(); i++) {
      if (triggers.elementAt(i).isGameEndingTrigger()) {
        return true;
      }
    }
    return false;
  }

  // returns true if this action has one or more game-ending destroyers
  public boolean hasGameEndingDestroyer() { 
    for (int i = 0; i < destroyers.size(); i++) {
      if (destroyers.elementAt(i).isGameEndingDestroyer()) {
        return true;
      }
    }
    return false;
  }

  public void removeAllRules() {
    rules.clear();
  }
}