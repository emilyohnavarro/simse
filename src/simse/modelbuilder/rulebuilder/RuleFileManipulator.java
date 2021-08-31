/*
 * This class is for generating the rules from a DefinedActions data structure
 * into a file and reading that file into memory
 */

package simse.modelbuilder.rulebuilder;

import simse.modelbuilder.ModelFileManipulator;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantAttributeConstraint;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantConstraint;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.NumericalAttribute;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.startstatebuilder.InstantiatedAttribute;
import simse.modelbuilder.startstatebuilder.SimSEObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Vector;

import javax.swing.JOptionPane;

public class RuleFileManipulator {
  private DefinedObjectTypes objectTypes;
  private DefinedActionTypes actionTypes;

  public RuleFileManipulator(DefinedObjectTypes defObjs,
      DefinedActionTypes defActs) {
    objectTypes = defObjs;
    actionTypes = defActs;
  }

  /*
   * loads the model file into memory, filling the "actionTypes" data structure
   * with the data from the file, and returns a Vector of warning messages
   */
  public Vector<String> loadFile(File inputFile) {
    actionTypes.removeAllRules();
    // vector of warning messages:
    Vector<String> warnings = new Vector<String>(); 
    try {
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      boolean foundBeginningOfRules = false;
      while (!foundBeginningOfRules) {
      	// read in a line of text from the file:
        String currentLine = reader.readLine(); 
        if (currentLine.equals(ModelFileManipulator.BEGIN_RULES_TAG)) { 
        	// beginning of rules
          foundBeginningOfRules = true;
          boolean endOfRules = false;
          while (!endOfRules) {
            currentLine = reader.readLine();
            if (currentLine.equals(ModelFileManipulator.END_RULES_TAG)) { 
            	// end of rules
              endOfRules = true;
            } else { // not end of rules yet
              if (currentLine.equals(
              		ModelFileManipulator.BEGIN_EFFECT_RULE_TAG)) {
                String ruleName = reader.readLine(); // get the rule name
                // get the rule priority:
                Integer priority = new Integer(reader.readLine()); 
                // get the name of the ActionType this rule is associated with:
                String actionName = reader.readLine(); 
                // attempt to find it in the actionTypes:
                ActionType tempAct = actionTypes.getActionType(actionName); 
                if (tempAct == null) { // ActionType not found
                  warnings.add("Action type " + actionName
                      + " removed -- ignoring " + ruleName
                      + " effect rule associated with this action type");
                } else { // ActionType found
                	// create a new effect rule with it:
                  EffectRule newRule = new EffectRule(ruleName, tempAct); 
                  newRule.setPriority(priority.intValue()); // set priority
                  currentLine = reader.readLine();
                  boolean getNextLine = true;
                  if (currentLine.startsWith("<") == false) { // new format
                                                            	// 4/26/04 that
                                                            	// includes rule
                                                            	// timing
                  	// set timing:
                    newRule.setTiming(Integer.parseInt(currentLine)); 
                  } else {
                    getNextLine = false;
                  }
                  if (getNextLine) {
                    currentLine = reader.readLine();
                    if (currentLine.equals("true")
                        || currentLine.equals("false")) { // new format 06/03/05
                                                        	// that includes
                                                        	// executeOnJoin
                    																			// status
                    	// set executeOnJoin status:
                      newRule.setExecuteOnJoins(Boolean
                          .parseBoolean(currentLine)); 

                      // visibility
                      currentLine = reader.readLine(); // get the next line
                      if (currentLine.equals("true")
                          || currentLine.equals("false")) { // new format 
                      																			// 9/28/05 that 
                      																			// includes 
                      																			// visibility
                        newRule.setVisibilityInExplanatoryTool(Boolean.valueOf(
                            currentLine).booleanValue());
                        StringBuffer annotation = new StringBuffer();

                        // get begin annotation tag
                        String tempInLine = reader.readLine();

                        tempInLine = reader.readLine();
                        while (!tempInLine.equals(
                        		ModelFileManipulator.END_RULE_ANNOTATION_TAG)) { 
                        	// not done yet
                          annotation.append(tempInLine);
                          tempInLine = reader.readLine();
                          if (!tempInLine.equals
                          		(ModelFileManipulator.END_RULE_ANNOTATION_TAG)) {
                          	// not done yet
                            annotation.append('\n');
                          }
                        }
                        newRule.setAnnotation(annotation.toString());
                        getNextLine = true;
                      } else { // has no annotation (older version)
                        getNextLine = false;
                      }
                    } else {
                      getNextLine = false;
                    }
                  }

                  boolean endOfRule = false;
                  while (!endOfRule) {
                    if (getNextLine) {
                      currentLine = reader.readLine(); // get the next line
                    } else {
                      getNextLine = true;
                    }
                    if (currentLine.equals(
                    		ModelFileManipulator.END_EFFECT_RULE_TAG)) { // end of 
                    																								 // rule
                      endOfRule = true;
                      // check if any participants have been added since this
                      // file was saved:
                      if (tempAct.getAllParticipants().size() > newRule
                          .getAllParticipantRuleEffects().size()) { // new one
                                                                  	// have been
                                                                  	// added
                        for (int i = 0; i < 
                        tempAct.getAllParticipants().size(); i++) {
                          ActionTypeParticipant tempPart = 
                          	tempAct.getAllParticipants().elementAt(i);
                          if (!(newRule.hasParticipantRuleEffect(tempPart
                              .getName()))) { // does not have an effect for 
                          										// this participant
                            // create and add it:
                            ParticipantRuleEffect newPartEff = 
                            	new ParticipantRuleEffect(tempPart);
                            newRule.addParticipantRuleEffect(newPartEff);
                          }
                        }
                      }
                      if (newRule.getAllParticipantRuleEffects().size() > 0) { 
                      	// Rule has at least one ParticipantRuleEffect
                        tempAct.addRule(newRule);
                      } else { // no effects
                        warnings
                            .add("All of " + tempAct.getName()
                                + " action's participant's are invalid -- " +
                                		"ignoring the " + ruleName 
                                		+ " effect rule associated with this " +
                                				"action type");
                      }
                    } else if (currentLine.equals(
                    		ModelFileManipulator.BEGIN_PARTICIPANT_EFFECT_TAG)) { 
                    	// beginning of ParticipantRuleEffect
                      String partName = reader.readLine(); // get the
                                                           // participant name
                      ActionTypeParticipant tempPart = tempAct
                          .getParticipant(partName);
                      if (tempPart == null) { // ActionTypeParticipant not found
                        warnings
                            .add(tempAct.getName()
                                + "action type participant " + partName + 
                                " removed -- ignoring this participant in " +
                                "this action type's " + newRule.getName() + 
                                " effect rule");
                      } else { // ActionTypeParticipant found
                        ParticipantRuleEffect tempPartEffect = 
                        	new ParticipantRuleEffect(tempPart);
                        boolean endOfPartEffect = false;
                        while (!endOfPartEffect) {
                        	// get the next line:
                          String currentLine2 = reader.readLine(); 
                          if (currentLine2.equals(
                          		ModelFileManipulator.
                          		END_PARTICIPANT_EFFECT_TAG)) { // end of
                          																	 // ParticipantRule
                          																	 // Effect
                            if (tempPartEffect.getAllParticipantTypeEffects()
                                .size() > 0) { // has at least one type effect
                              endOfPartEffect = true;
                              // add the ParticipantRuleEffect to the effect
                              // rule:
                              newRule.addParticipantRuleEffect(tempPartEffect); 
                            } else { // no type effects
                              warnings
                                  .add("All of "
                                      + tempAct.getName()
                                      + " action type's "
                                      + tempPart.getName()
                                      + " participant's allowable object " +
                                      		"types are invalid -- ignoring this "
                                      + " participant in this action type's "
                                      + ruleName + " effect rule");
                            }
                          } else if (currentLine2.equals(
                          		ModelFileManipulator.
                          		BEGIN_PARTICIPANT_TYPE_EFFECT_TAG)) { 
                          	// beginning of ParticipantTypeRuleEffect
                          	// get the SimSEObjectTypeType
                            String metaType = reader.readLine(); 
                            int intMetaType = Integer.parseInt(metaType);
                            // get the SimSEObjectType name:
                            String ssObjType = reader.readLine(); 
                            /*
                             * attempt to get the SimSEObjectType from the 
                             * defined object types:
                             */
                            SimSEObjectType tempObjType = objectTypes
                                .getObjectType(intMetaType, ssObjType); 
                            if (tempObjType == null) { // object type not found
                              warnings
                                  .add("Object type "
                                      + ssObjType
                                      + " "
                                      + SimSEObjectTypeTypes
                                          .getText(intMetaType)
                                      + " removed -- ignoring "
                                      + tempAct.getName()
                                      + " action type participant of this " +
                                      		"type for this action type's "
                                      + ruleName + " effect rule");
                            } else { // object type found
                            	// create a new ParticipantTypeRuleEffect:
                              ParticipantTypeRuleEffect tempPartTypeEffect = 
                              	new ParticipantTypeRuleEffect(tempObjType); 
                              // set the other actions effect:
                              tempPartTypeEffect.getOtherActionsEffect().
                              setEffect(reader.readLine()); 
                              
                              reader.mark(200);
                              String currentLineA = reader.readLine();
                              if (currentLineA.equals(
                              		ModelFileManipulator.
                              		BEGIN_ACTIONS_TO_ACTIVATE_TAG)) { // new
                                																		// format
                                																		// 2/13
                                
                                // actions to activate:
                                boolean endOfActsToAdd = false;
                                while (!endOfActsToAdd) {
	                                currentLineA = reader.readLine();
	                                if (!currentLineA.equals(
	                                		ModelFileManipulator.
	                                		END_ACTIONS_TO_ACTIVATE_TAG)) {
	                                  ActionType actToAdd = actionTypes.
	                                  	getActionType(currentLineA);
	                                  if (actToAdd != null) {
	                                    tempPartTypeEffect.
	                                    getOtherActionsEffect().
	                                    addActionToActivate(actToAdd);
	                                  }
	                                }
	                                else {
	                                  endOfActsToAdd = true;
	                                }
                                }
                                
                                // actions to deactivate:
                                // read in begin actions to deactivate tag:
                                reader.readLine(); 
                                endOfActsToAdd = false;
                                while (!endOfActsToAdd) {
	                                currentLineA = reader.readLine();
	                                if (!currentLineA.equals(
	                                		ModelFileManipulator.
	                                		END_ACTIONS_TO_DEACTIVATE_TAG)) {
	                                  ActionType actToAdd = actionTypes.
	                                  	getActionType(currentLineA);
	                                  if (actToAdd != null) {
	                                    tempPartTypeEffect.
	                                    getOtherActionsEffect().
	                                    addActionToDeactivate(actToAdd);
	                                  }
	                                }
	                                else {
	                                  endOfActsToAdd = true;
	                                }
                                }
                              } else { // old format
                                reader.reset();
                              }
                              
                              String currentLine3 = reader.readLine();
                              boolean getNextLine2 = true;
                              if (currentLine3.startsWith("<") == false) { 
                              	// old format that includes other actions effect
                              	// upon destruction.
                                // ignore this old format stuff, which was
                                // overwritten by another format change on
                                // 4/26/04 that added trigger
                                // and destroyer rules:
                                //tempPartTypeEffect.
                              	// setOtherActionsEffectUponDestruction(
                              	// currentLine3);
                                // // set the other actions
                                // effect upon destruction
                              } else {
                                getNextLine2 = false;
                              }
                              boolean endOfPartTypeEffect = false;
                              while (!endOfPartTypeEffect) {
                                if (getNextLine2) {
                                	// get the next line:
                                  currentLine3 = reader.readLine(); 
                                } else {
                                  getNextLine2 = true;
                                }
                                if (currentLine3.equals(
                                		ModelFileManipulator.
                                		END_PARTICIPANT_TYPE_EFFECT_TAG)) { 
                                	// end of ParticipantTypeRuleEffect
                                  if (tempPartTypeEffect.
                                  		getAllAttributeEffects().size() > 0) { 
                                  	// at least one attribute effect
                                    endOfPartTypeEffect = true;
                                    // add the ParticipantTypeRuleEffect to the
                                    // ParticipantRuleEffect:
                                    tempPartEffect
                                        .addParticipantTypeRuleEffect(
                                        		tempPartTypeEffect); 
                                  } else { // no attribute effects
                                    warnings.add("All of "
                                        + ssObjType
                                        + " "
                                        + SimSEObjectTypeTypes
                                            .getText(intMetaType)
                                        + " attributes invalid "
                                        + "-- ignoring " + tempPart.getName()
                                        + " participants of this type "
                                        + " in the " + ruleName
                                        + " effect rule associated with the "
                                        + tempAct.getName() + " action type");
                                  }
                                } else if (currentLine3.equals(
                                		ModelFileManipulator.
                                		BEGIN_ATTRIBUTE_EFFECT_TAG)) {
                                	// beginning of ParticipantAttributeRuleEffect
                                	// get the attribute name:
                                  String attName = reader.readLine(); 
                                  // attempt to get the attribute from the
                                  // SimSEObjectType:
                                  Attribute tempAtt = tempObjType
                                      .getAttribute(attName); 
                                  if (tempAtt == null) { // attribute not found
                                    warnings
                                        .add("Attribute "
                                            + attName
                                            + " removed from "
                                            + tempObjType.getName()
                                            + " "
                                            + SimSEObjectTypeTypes
                                                .getText(intMetaType)
                                            + " object type -- "
                                            + "ignoring this attribute in "
                                            + tempAct.getName()
                                            + " action type "
                                            + tempPart.getName()
                                            + " participant for this " +
                                            		"action type's "
                                            + ruleName + " effect rule");
                                  } else { // attribute found
                                  	// create a new 
                                  	// ParticipantAttributeRuleEffect with the
                                  	// specified attribute:
                                    ParticipantAttributeRuleEffect tempAttEffect
                                    = new ParticipantAttributeRuleEffect(
                                    		tempAtt); 
                                    // set the effect from the file:
                                    tempAttEffect.setEffect(reader.readLine()); 
                                    // add the attribute effect to the 
                                    // participant type effect:
                                    tempPartTypeEffect
                                        .addAttributeEffect(tempAttEffect); 
                                    // read in END_ATTRIBUTE_EFFECT_TAG:
                                    reader.readLine(); 
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    } else if (currentLine.equals(
                    		ModelFileManipulator.BEGIN_RULE_INPUT_TAG)) { 
                    	// beginning of RuleInput
                    	// create a new RuleInput with the name from the file:
                      RuleInput tempInput = new RuleInput(reader.readLine()); 
                      // set the type from the file:
                      tempInput.setType(reader.readLine()); 
                      // set the prompt from the file:
                      tempInput.setPrompt(reader.readLine()); 
                      // set the cancelable value from the file:
                      tempInput.setCancelable((new Boolean(reader.readLine()))
                          .booleanValue()); 
                      // read in BEING_RULE_INPUT_CONDITION_TAG:
                      reader.readLine(); 
                      // set guard from file:
                      tempInput.getCondition().setGuard(reader.readLine()); 
                      // get the conditon value:
                      String conditionVal = reader.readLine(); 
                      if (!conditionVal.equals(
                      		ModelFileManipulator.EMPTY_VALUE)) { // has a 
                      																				 // condition val
                        if (tempInput.getType().equals(InputType.INTEGER)) { 
                        	// integer input
                          tempInput.getCondition().setValue(
                              new Integer(conditionVal)); // set the value
                        } else if (tempInput.getType().equals(
                        		InputType.DOUBLE)) { // double input
                          tempInput.getCondition().setValue(
                              new Double(conditionVal)); // set the value
                        }
                        // won't be string or boolean -- those don't have
                        // conditions
                      }
                      reader.readLine(); // read in END_RULE_INPUT_CONDITION_TAG
                      reader.readLine(); // read in END_RULE_INPUT_TAG
                      // add the input to the rule:
                      newRule.addRuleInput(tempInput); 
                    }
                  }
                }
              } else if (currentLine.equals(
              		ModelFileManipulator.BEGIN_CREATE_OBJECTS_RULE_TAG)) { 
              	// beginning of CreateObjectsRule
                String ruleName = reader.readLine(); // get the name
                // get the priority:
                Integer priority = new Integer(reader.readLine()); 
                // get the name of the ActionType this rule is associated with:
                String actionName = reader.readLine(); 
                // attempt to find it in the actionTypes:
                ActionType tempAct = actionTypes.getActionType(actionName); 
                if (tempAct == null) { // ActionType not found
                  warnings
                      .add("Action type "
                          + actionName
                          + " removed -- ignoring "
                          + ruleName
                          + " create objects rule associated with this " +
                          		"action type");
                } else { // ActionType found
                  CreateObjectsRule newRule = new CreateObjectsRule(ruleName,
                      tempAct);
                  newRule.setPriority(priority.intValue()); // set priority
                  String currentLine2 = reader.readLine();
                  boolean getNextLine = true;
                  if (currentLine2.startsWith("<") == false) { // new format
                                                             	 // 4/26/04 that
                                                             	 // includes rule
                                                             	 // timing
                  	// set timing:
                    newRule.setTiming(Integer.parseInt(currentLine2)); 

                    // visibility
                    currentLine2 = reader.readLine(); // get the next line
                    if (currentLine2.equals("true") || 
                    		currentLine2.equals("false")) { // new format 9/28/05
                                                        // that includes
                                                        // visibiltiy
                      newRule.setVisibilityInExplanatoryTool(Boolean.valueOf(
                          currentLine2).booleanValue());
                      StringBuffer annotation = new StringBuffer();

                      // get begin annotation tag
                      String tempInLine = reader.readLine();

                      tempInLine = reader.readLine();
                      while (!tempInLine.equals(
                      		ModelFileManipulator.END_RULE_ANNOTATION_TAG)) { 
                      	// not done yet
                        annotation.append(tempInLine);
                        tempInLine = reader.readLine();
                        if (!tempInLine.equals(
                        		ModelFileManipulator.END_RULE_ANNOTATION_TAG)) { 
                        	// not done yet
                          annotation.append('\n');
                        }
                      }
                      newRule.setAnnotation(annotation.toString());
                      getNextLine = true;
                    } else { // has no annotation (older version)
                      getNextLine = false;
                    }
                  } else {
                    getNextLine = false;
                  }

                  boolean endOfRule = false;
                  while (!endOfRule) {
                    if (getNextLine) {
                      currentLine2 = reader.readLine(); // get the next line
                    } else {
                      getNextLine = true;
                    }
                    if (currentLine2.equals(
                    		ModelFileManipulator.END_CREATE_OBJECTS_RULE_TAG)) { 
                    	// end of CreateObjectsRule
                      endOfRule = true;
                      // add the rule to the action type:
                      tempAct.addRule(newRule); 
                    } else if (currentLine2.equals(
                    		ModelFileManipulator.BEGIN_OBJECT_TAG)) { // beginning
                                                                  // of
                                                                  // SimSEObject
                      // get the SimSEObjectTypeType:
                    	String metaType = reader.readLine(); 
                      int metaTypeInt = Integer.parseInt(metaType);
                      // get the SimSEObjectType name:
                      String ssObjType = reader.readLine(); 
                      // attempt to get the SimSEObjectType from the defined 
                      // object types:
                      SimSEObjectType tempObjType = objectTypes.getObjectType(
                          metaTypeInt, ssObjType); 
                      if (tempObjType == null) { // object type not found
                        warnings
                            .add("Object type "
                                + ssObjType
                                + " "
                                + SimSEObjectTypeTypes.getText(metaTypeInt)
                                + " removed -- ignoring "
                                + tempAct.getName()
                                + " object of this type created by this " +
                                		"action type's "
                                + ruleName + " create objects rule");
                      } else { // object type found
                        SimSEObject tempObj = new SimSEObject(tempObjType);
                        boolean endOfObj = false;
                        while (!endOfObj) {
                        	// get the next line:
                          String currentLine3 = reader.readLine(); 
                          if (currentLine3.equals(
                          		ModelFileManipulator.END_OBJECT_TAG)) { // end of
                                                                   	  // object
                            if (tempObj.hasAttribute(tempObj
                                .getSimSEObjectType().getKey()) == false) { 
                            	// no key attribute value
                              warnings
                                  .add("The key attribute for the "
                                      + ssObjType
                                      + " "
                                      + SimSEObjectTypeTypes
                                          .getText(metaTypeInt)
                                      + " has been changed -- ignoring object " +
                                      		"of this "
                                      + " type created by the "
                                      + ruleName
                                      + " create objects rule associated with " +
                                      		"the "
                                      + tempAct.getName() + " action type");
                              endOfObj = true;
                            } else {
                              // make sure all of the instantiated atts have
                              // been added to the object:
                              Vector<Attribute> objTypeAtts = 
                              	tempObj.getSimSEObjectType().getAllAttributes();
                              Vector<InstantiatedAttribute> objAtts = 
                              	tempObj.getAllAttributes();
                              if (objAtts.size() < objTypeAtts.size()) { 
                              	// not all instantiated attributes have been
                              	// added
                                for (int i = 0; i < objTypeAtts.size(); i++) {
                                  Attribute objAtt = objTypeAtts.elementAt(i);
                                  if (!tempObj.hasAttribute(objAtt)) { // object
                                  																		 // does
                                  																		 // not
                                                                       // have
                                                                       // this
                                                                       // att
                                    // add it:
                                    tempObj
                                        .addAttribute(new InstantiatedAttribute(
                                            objAtt));
                                  }
                                }
                              }
                              endOfObj = true;
                              // add the object to the rule:
                              newRule.addSimSEObject(tempObj); 
                            }
                          } else if (currentLine3.equals(
                          		ModelFileManipulator.
                          		BEGIN_INSTANTIATED_ATTRIBUTE_TAG)) { // begin
                                                                   // instant.
                                                                   // att
                            String attName = reader.readLine(); // get att name
                            // attempt to get the attribute from the 
                            // SimSEObjectType:
                            Attribute tempAtt = tempObjType
                                .getAttribute(attName);
                            if (tempAtt == null) { // attribute not found
                              warnings
                                  .add("Attribute "
                                      + attName
                                      + " removed from "
                                      + tempObjType.getName()
                                      + " "
                                      + SimSEObjectTypeTypes
                                          .getText(metaTypeInt)
                                      + " object type -- ignoring this " +
                                      		"attribute in object of this type " +
                                      		"created by "
                                      + " the " + tempAct.getName()
                                      + " action type's " + ruleName
                                      + " create objects rule");
                            } else { // attribute found
                              InstantiatedAttribute tempInstAtt = 
                              	new InstantiatedAttribute(tempAtt);
                              // get attribute value:
                              String value = reader.readLine(); 
                              if (!value.equals(
                              		ModelFileManipulator.EMPTY_VALUE)) { // non-
                              																				 // empty
                                if (tempAtt.getType() == 
                                	AttributeTypes.INTEGER) { // integer att
                                	// set value:
                                  tempInstAtt.setValue(new Integer(value)); 
                                } else if (tempAtt.getType() == 
                                	AttributeTypes.DOUBLE) { // double att
                                	// set value:
                                  tempInstAtt.setValue(new Double(value)); 
                                } else if (tempAtt.getType() == 
                                	AttributeTypes.BOOLEAN) { // boolean att
                                	// set value:
                                  tempInstAtt.setValue(new Boolean(value)); 
                                } else { // string att
                                  tempInstAtt.setValue(value); // set value
                                }
                                // add the instantiated att:
                                tempObj.addAttribute(tempInstAtt); 
                              } else if (value.equals(
                              		ModelFileManipulator.EMPTY_VALUE) && 
                              		tempAtt.isKey()) { // this is the key 
                              											 // attribute and there is
                                                     // no value for it
                                warnings
                                    .add("The key attribute for the "
                                        + ssObjType
                                        + " "
                                        + SimSEObjectTypeTypes
                                            .getText(metaTypeInt)
                                        + " has been changed -- ignoring " +
                                        		"object of this type created by " +
                                        		"the " + ruleName
                                        + " create objects rule associated " +
                                        		"with the "
                                        + tempAct.getName() + " action type");
                              } else { // empty value for non-key attribute -- 
                              				 // ok
                              	// add the instantiated att:
                                tempObj.addAttribute(tempInstAtt); 
                              }
                              // read in END_INSTANTIATED_ATTRIBUTE_TAG:
                              reader.readLine(); 
                            }
                          }
                        }
                      }
                    }
                  }
                }
              } else if (currentLine.equals(
              		ModelFileManipulator.BEGIN_DESTROY_OBJECTS_RULE_TAG)) { 
              	// beginning of DestroyObjectsRule
                String ruleName = reader.readLine(); // get the name
                // get the priority:
                Integer priority = new Integer(reader.readLine()); 
                // get the name of the ActionType this rule is associated with:
                String actionName = reader.readLine(); 
                // attempt to find it in the actionTypes:
                ActionType tempAct = actionTypes.getActionType(actionName); 
                if (tempAct == null) { // ActionType not found
                  warnings
                      .add("Action type "
                          + actionName
                          + " removed -- ignoring "
                          + ruleName
                          + " create objects rule associated with this " +
                          		"action type");
                } else { // ActionType found
                  DestroyObjectsRule newRule = new DestroyObjectsRule(ruleName,
                      tempAct);
                  newRule.setPriority(priority.intValue()); // set priority
                  String currentLine2 = reader.readLine();
                  boolean getNextLine = true;
                  if (currentLine2.startsWith("<") == false) { // new format
                                                             	 // 4/26/04 that
                                                             	 // includes rule
                                                             	 // timing
                  	// set timing:
                    newRule.setTiming(Integer.parseInt(currentLine2)); 

                    // visibility
                    currentLine2 = reader.readLine(); // get the next line
                    if (currentLine2.equals("true") || 
                    		currentLine2.equals("false")) { // new format 9/28/05
                                                        // that includes
                                                        // visibiltiy
                      newRule.setVisibilityInExplanatoryTool(Boolean.valueOf(
                          currentLine2).booleanValue());
                      StringBuffer annotation = new StringBuffer();

                      // get begin annotation tag
                      String tempInLine = reader.readLine();

                      tempInLine = reader.readLine();
                      while (!tempInLine.equals(
                      		ModelFileManipulator.END_RULE_ANNOTATION_TAG)) { 
                      	// not done yet
                        annotation.append(tempInLine);
                        tempInLine = reader.readLine();
                        if (!tempInLine.equals(
                        		ModelFileManipulator.END_RULE_ANNOTATION_TAG)) { 
                        	// not done yet
                          annotation.append('\n');
                        }
                      }
                      newRule.setAnnotation(annotation.toString());
                      getNextLine = true;
                    } else { // has no annotation (older version)
                      getNextLine = false;
                    }
                  } else {
                    getNextLine = false;
                  }

                  boolean endOfRule = false;
                  while (!endOfRule) {
                    if (getNextLine) {
                      currentLine2 = reader.readLine(); // get the next line
                    } else {
                      getNextLine = true;
                    }
                    if (currentLine2.equals(
                    		ModelFileManipulator.END_DESTROY_OBJECTS_RULE_TAG)) { 
                    	// end of DestroyObjectsRule
                      endOfRule = true;
                      // add the rule to the action type:
                      tempAct.addRule(newRule); 
                    } else if (currentLine2.equals(
                    		ModelFileManipulator.BEGIN_PARTICIPANT_CONDITION_TAG)) {
                    	// beginning of participant condition
                      ActionTypeParticipant tempPart = tempAct
                          .getParticipant(reader.readLine());
                      if (tempPart != null) { // participant found
                      	// create a new participant condition with the specified
                      	// participant:
                        DestroyObjectsRuleParticipantCondition newPartCond = 
                        	new DestroyObjectsRuleParticipantCondition(tempPart); 
                        boolean endOfPartCond = false;
                        while (!endOfPartCond) {
                        	// get the next line:
                          String currentLinePartCond = reader.readLine(); 
                          if (currentLinePartCond.equals(
                          		ModelFileManipulator.
                          		END_PARTICIPANT_CONDITION_TAG)) { // end of
                                                                // participant
                                                                // condition
                            endOfPartCond = true;
                            newRule.addParticipantCondition(newPartCond);
                          } else if (currentLinePartCond.equals(
                          		ModelFileManipulator.
                          		BEGIN_PARTICIPANT_CONSTRAINT_TAG)) { 
                          	// beginning of particpiant constraint
                            String ssObjTypeName = reader.readLine();
                            int type = newPartCond.getParticipant()
                                .getSimSEObjectTypeType();
                            // get the SimSEObjectType from the defined objects:
                            SimSEObjectType tempObjType = objectTypes
                                .getObjectType(type, ssObjTypeName); 
                            if (tempObjType != null) { // such a type exists
                            	// get the SimSEObjectTypeType from the defined
                            	// objects and use that and the SimSEObjectType
                            	// name (read from the file) to create a new
                            	// ActionTypeParticipantConstraint:
                              ActionTypeParticipantConstraint newPartConst = 
                              	new ActionTypeParticipantConstraint(
                                  tempObjType); 
                              boolean endOfPartConst = false;
                              while (!endOfPartConst) {
                              	// get the next line:
                                String currentLinePartConst = reader.readLine(); 
                                if (currentLinePartConst.equals(
                                		ModelFileManipulator.
                                		END_PARTICIPANT_CONSTRAINT_TAG)) { 
                                	// end of participant constraint
                                  endOfPartConst = true;
                                  newPartCond.addConstraint(newPartConst);
                                } else if (currentLinePartConst.equals(
                                		ModelFileManipulator.
                                		BEGIN_ATTRIBUTE_CONSTRAINT_TAG)) { 
                                	// beginning of attribute constraint
                                	// get the attribute name:
                                  String attName = reader.readLine(); 
                                  // get the actual Attribute object:
                                  Attribute att = newPartConst
                                      .getSimSEObjectType().getAttribute(
                                          attName); 
                                  if (att == null) { // attribute not found
                                    warnings
                                        .add(SimSEObjectTypeTypes
                                            .getText(newPartConst
                                                .getSimSEObjectType().getType())
                                            + " "
                                            + newPartConst.getSimSEObjectType()
                                                .getName()
                                            + " "
                                            + attName
                                            + " attribute removed -- " +
                                            		"ignoring this attribute in "
                                            + newRule.getName()
                                            + " destroy objects rule "
                                            + tempPart.getName()
                                            + " participant");
                                  } else { // attribute found
                                  	// get the attribute guard:
                                    String guard = reader.readLine(); 
                                    // create a new attribute constraint with 
                                    // the specified attribute:
                                    ActionTypeParticipantAttributeConstraint 
                                    newAttConst = new 
                                    ActionTypeParticipantAttributeConstraint(
                                        att); 
                                    // set the guard:
                                    newAttConst.setGuard(guard); 
                                    // get the value:
                                    String value = reader.readLine(); 
                                    if (!value.equals(
                                    		ModelFileManipulator.EMPTY_VALUE)) {
                                    	// attribute has a constraining value
                                      if (att.getType() == 
                                      	AttributeTypes.BOOLEAN) { // boolean
                                                                  // attribute
                                        if (value.equals((new Boolean(true))
                                            .toString())) {
                                          newAttConst
                                              .setValue(new Boolean(true));
                                        } else if (value.equals((new Boolean(
                                            false)).toString())) {
                                          newAttConst.setValue(new Boolean(
                                              false));
                                        } else { // a non-boolean value
                                          warnings
                                              .add(SimSEObjectTypeTypes
                                                  .getText(newPartConst
                                                      .getSimSEObjectType()
                                                      .getType())
                                                  + " "
                                                  + newPartConst
                                                      .getSimSEObjectType()
                                                      .getName()
                                                  + " "
                                                  + attName
                                                  + " attribute changed type -- "
                                                  + " destroy objects rule " +
                                                  		"condition no longer " +
                                                  		"valid -- ignoring "
                                                  + " destroy objects rule " +
                                                  		"condition for this " +
                                                  		"attribute in "
                                                  + newRule.getName()
                                                  + " rule "
                                                  + tempPart.getName()
                                                  + " participant");
                                        }
                                      } else if (att.getType() == 
                                      	AttributeTypes.INTEGER) { // integer
                                                                  // attribute
                                        try {
                                          boolean valid = true;
                                          Integer intVal = new Integer(value);
                                          NumericalAttribute numAtt =
                                          	(NumericalAttribute)att;
                                          if (!numAtt.isMinBoundless()) {
                                          // has a minimum constraining value
                                            if (intVal.intValue() < 
                                            		numAtt.getMinValue().
                                            		intValue()) { // outside of 
                                            									// range
                                              warnings
                                                  .add(SimSEObjectTypeTypes
                                                      .getText(newPartConst
                                                          .getSimSEObjectType()
                                                          .getType())
                                                      + " "
                                                      + newPartConst
                                                          .getSimSEObjectType()
                                                          .getName()
                                                      + " "
                                                      + attName
                                                      + " attribute changed " +
                                                      		"min value -- "
                                                      + " destroy objects " +
                                                      		"rule condition no " +
                                                      		"longer within " +
                                                      		"acceptable range " +
                                                      		"-- ignoring "
                                                      + " destroy objects " +
                                                      		"rule condition " +
                                                      		"for this " +
                                                      		"attribute in "
                                                      + newRule.getName()
                                                      + " rule "
                                                      + tempPart.getName()
                                                      + " participant");
                                              valid = false;
                                            }
                                          }
                                          if (!numAtt.isMaxBoundless()) {
                                          // has a maximum constraining value
                                            if (intVal.intValue() > 
                                            numAtt.getMaxValue().intValue()) {
                                            // outside of range
                                              warnings
                                                  .add(SimSEObjectTypeTypes
                                                      .getText(newPartConst
                                                          .getSimSEObjectType()
                                                          .getType())
                                                      + " "
                                                      + newPartConst
                                                          .getSimSEObjectType()
                                                          .getName()
                                                      + " "
                                                      + attName
                                                      + " attribute " +
                                                      		"changed max " +
                                                      		"value -- "
                                                      + " destroy objects " +
                                                      		"rule condition " +
                                                      		"no longer within " +
                                                      		"acceptable range " +
                                                      		"-- ignoring "
                                                      + " destroy objects " +
                                                      		"rule condition " +
                                                      		"for this " +
                                                      		"attribute in "
                                                      + newRule.getName()
                                                      + " rule "
                                                      + tempPart.getName()
                                                      + " participant");
                                              valid = false;
                                            }
                                          }
                                          if (valid) {
                                            newAttConst.setValue(new Integer(
                                                value));
                                          }
                                        } catch (NumberFormatException e) {
                                          warnings
                                              .add(SimSEObjectTypeTypes
                                                  .getText(newPartConst
                                                      .getSimSEObjectType()
                                                      .getType())
                                                  + " "
                                                  + newPartConst
                                                      .getSimSEObjectType()
                                                      .getName()
                                                  + " "
                                                  + attName
                                                  + " attribute changed " +
                                                  		"type -- "
                                                  + " destroy objects rule " +
                                                  		"condition no longer " +
                                                  		"valid -- ignoring "
                                                  + " destroy objects rule " +
                                                  		"condition for this " +
                                                  		"attribute in "
                                                  + newRule.getName()
                                                  + " rule "
                                                  + tempPart.getName()
                                                  + " participant");
                                        }
                                      } else if (att.getType() == 
                                      	AttributeTypes.DOUBLE) { // double
                                                                 // attribute
                                        try {
                                          boolean valid = true;
                                          Double doubleVal = new Double(value);
                                          NumericalAttribute numAtt =
                                          	(NumericalAttribute)att;
                                          if (!numAtt.isMinBoundless()) {
                                          // has a minimum constraining value
                                            if (doubleVal.doubleValue() < 
                                            		numAtt.getMinValue().
                                            		doubleValue()) { // outside of 
                                            										 // range
                                              warnings
                                                  .add(SimSEObjectTypeTypes
                                                      .getText(newPartConst
                                                          .getSimSEObjectType()
                                                          .getType())
                                                      + " "
                                                      + newPartConst
                                                          .getSimSEObjectType()
                                                          .getName()
                                                      + " "
                                                      + attName
                                                      + " attribute changed " +
                                                      		"min value -- "
                                                      + " destroy objects " +
                                                      		"rule condition no " +
                                                      		"longer within " +
                                                      		"acceptable range " +
                                                      		"-- ignoring "
                                                      + " destroy objects " +
                                                      		"rule condition " +
                                                      		"for this " +
                                                      		"attribute in "
                                                      + newRule.getName()
                                                      + " rule "
                                                      + tempPart.getName()
                                                      + " participant");
                                              valid = false;
                                            }
                                          }
                                          if (!numAtt.isMaxBoundless()) {
                                          // has a maximum constraining value
                                            if (doubleVal.doubleValue() > 
                                            numAtt.getMaxValue().
                                            doubleValue()) { // outside of range
                                              warnings
                                                  .add(SimSEObjectTypeTypes
                                                      .getText(newPartConst
                                                          .getSimSEObjectType()
                                                          .getType())
                                                      + " "
                                                      + newPartConst
                                                          .getSimSEObjectType()
                                                          .getName()
                                                      + " "
                                                      + attName
                                                      + " attribute changed" +
                                                      		" max value -- "
                                                      + " destroy objects " +
                                                      		"rule condition no " +
                                                      		"longer within " +
                                                      		"acceptable " +
                                                      		"range -- ignoring "
                                                      + " destroy objects " +
                                                      		"rule condition " +
                                                      		"for this " +
                                                      		"attribute in "
                                                      + newRule.getName()
                                                      + " rule "
                                                      + tempPart.getName()
                                                      + " participant");
                                              valid = false;
                                            }
                                          }
                                          if (valid) {
                                            newAttConst.setValue(new Double(
                                                value));
                                          }
                                        } catch (NumberFormatException e) {
                                          warnings
                                              .add(SimSEObjectTypeTypes
                                                  .getText(newPartConst
                                                      .getSimSEObjectType()
                                                      .getType())
                                                  + " "
                                                  + newPartConst
                                                      .getSimSEObjectType()
                                                      .getName()
                                                  + " "
                                                  + attName
                                                  + " attribute changed " +
                                                  		"type -- "
                                                  + " destroy objects rule " +
                                                  		"condition no longer " +
                                                  		"valid -- ignoring "
                                                  + " destroy objects rule " +
                                                  		"condition for this " +
                                                  		"attribute in "
                                                  + newRule.getName()
                                                  + " rule "
                                                  + tempPart.getName()
                                                  + " participant");
                                        }
                                      } else if (att.getType() == 
                                      	AttributeTypes.STRING) { // string
                                                                 // attribute
                                        newAttConst.setValue(value);
                                      }
                                    }
                                    // read END_ATTRIBUTE_CONSTRAINT_TAG in:
                                    reader.readLine(); 
                                    // add completed attribute constraint to the
                                    // participant constraint:
                                    newPartConst
                                        .addAttributeConstraint(newAttConst); 
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      reader.close();
    } catch (FileNotFoundException e) {
      JOptionPane.showMessageDialog(null, ("Cannot find rule file " + inputFile
          .getPath()), "File Not Found", JOptionPane.WARNING_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error reading file: " + e
          .toString()), "File IO Error", JOptionPane.WARNING_MESSAGE);
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, ("Error reading file: " + e
          .toString()), "File IO Error", JOptionPane.WARNING_MESSAGE);
    }
    return warnings;
  }
}