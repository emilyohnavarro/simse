/*
 * This class is for generating the DefinedActions into a file and reading that
 * file into memory
 */

package simse.modelbuilder.actionbuilder;

import simse.modelbuilder.ModelFileManipulator;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.NumericalAttribute;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Vector;

import javax.swing.JOptionPane;

public class ActionFileManipulator {
  private DefinedObjectTypes objectTypes;
  private DefinedActionTypes actionTypes;

  public ActionFileManipulator(DefinedObjectTypes objectTypes,
      DefinedActionTypes actionTypes) {
    this.objectTypes = objectTypes;
    this.actionTypes = actionTypes;
  }

  /*
   * loads the model file into memory, filling the "actionTypes" data structure
   * with the data from the file, and returns a Vector of warning messages
   */
  public Vector<String> loadFile(File inputFile) { 
    actionTypes.clearAll();
    Vector<String> warnings = new Vector<String>(); // vector of warning msgs
    try {
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      boolean foundBeginningOfActions = false;
      while (!foundBeginningOfActions) {
        String currentLine = reader.readLine(); // read in a line of text from
                                                // the file
        if (currentLine.equals(
        		ModelFileManipulator.BEGIN_DEFINED_ACTIONS_TAG)) { // beginning of
                                                           		 // action types
          foundBeginningOfActions = true;
          boolean endOfActions = false;
          while (!endOfActions) {
            currentLine = reader.readLine();
            if (currentLine.equals(
            		ModelFileManipulator.END_DEFINED_ACTIONS_TAG)) { // end of 
            																										 // defined
                                                             		 // actions
              endOfActions = true;
            } else { // not end of defined actions yet
              if (currentLine.equals(
              		ModelFileManipulator.BEGIN_ACTION_TYPE_TAG)) {
                ActionType newAct = new ActionType(reader.readLine());
                newAct.setVisibilityInSimulation(Boolean.valueOf(
                    reader.readLine()).booleanValue()); // set visibility
                if (newAct.isVisibleInSimulation()) {
                  newAct.setDescription(reader.readLine()); // set description
                } else {
                  newAct.setDescription(null);
                }

                // visibility in explanatory tool / annotation
                reader.mark(newAct.getAnnotation().length() + 10);
                currentLine = reader.readLine(); // get the next line

                // new format 9/28/05 that includes annotation and visibility in
                // explanatory tool
                if (currentLine.equals("true") || currentLine.equals("false")) {
                  newAct.setVisibilityInExplanatoryTool(Boolean.valueOf(
                      currentLine).booleanValue());
                  StringBuffer annotation = new StringBuffer();
                  String tempInLine = reader.readLine(); // get the begin
                                                         // annotation tag
                  tempInLine = reader.readLine();
                  while (tempInLine.equals(
                  		ModelFileManipulator.END_ACTION_TYPE_ANNOTATION_TAG) == 
                  			false) { // not done yet
                    annotation.append(tempInLine);
                    tempInLine = reader.readLine();
                    if (tempInLine.equals(
                    		ModelFileManipulator.END_ACTION_TYPE_ANNOTATION_TAG) == 
                    			false) { // not done yet
                      annotation.append('\n');
                    }
                  }
                  newAct.setAnnotation(annotation.toString());
                } else { // has no visibility in exp tool / annotation (older
                       	 // version)
                  reader.reset();
                }
                
                // new format 1/24/07 that includes joining
                reader.mark(10);
                currentLine = reader.readLine();
                if (currentLine.equals("true") || currentLine.equals("false")) {
                  newAct.setJoiningAllowed(Boolean.valueOf(
                      currentLine).booleanValue());
                } else {
                  reader.reset();
                }

                int ssObjTypeType = 0; // SimSEObjectTypeType of this ActionType

                boolean endOfAct = false;
                while (!endOfAct) {
                  currentLine = reader.readLine(); // get the next line
                  if (currentLine.equals(
                  		ModelFileManipulator.END_ACTION_TYPE_TAG)) { // end of 
                  																								 // action
                                                               		 // type
                    endOfAct = true;
                    actionTypes.addActionType(newAct); // add action type to
                                                       // defined action types
                  } else if (currentLine.equals(
                  		ModelFileManipulator.BEGIN_PARTICIPANT_TAG)) { 
                  	// beginning of ActionTypeParticipant
                    String partName = reader.readLine(); // get the participant
                                                         // name
                    String metaTypeName = reader.readLine();
                    ssObjTypeType = SimSEObjectTypeTypes
                        .getIntRepresentation(metaTypeName); // get the
                                                             // meta type
                    ActionTypeParticipant newPart = new ActionTypeParticipant(
                        ssObjTypeType); // create a new ActionTypeParticipant
                    // with this info
                    newPart.setName(partName); // set the participant name
                    reader.readLine(); // get the restricted status
                    // NOTE: This restricted stuff has been taken out but is
                    // still in the file for backwards compatability:
                    //newPart.setRestricted((new
                    // Boolean(restricted)).booleanValue()); // set the
                    // restricted status
                    boolean endOfPart = false;
                    while (!endOfPart) {
                      String currentLine2 = reader.readLine(); // get the next
                                                               // line
                      if (currentLine2.equals(
                      		ModelFileManipulator.END_PARTICIPANT_TAG)) { 
                      	// end of ActionTypeParticipant
                        endOfPart = true;
                        if (newPart.getAllSimSEObjectTypes().size() > 0) { 
                        	// participant has at least one valid SimSEObjectType 
                        	// left
                          newAct.addParticipant(newPart); // add participant to
                                                          // action type
                        } else { // all of the participant's SimSEObjectTypes 
                        				 // were invalid
                          warnings
                              .add("All of "
                                  + newAct.getName()
                                  + " action's "
                                  + newPart.getName()
                                  + " participant's allowable object types " +
                                  		"are invalid -- ignoring this " +
                                  		"participant");
                        }
                      } else if (currentLine2.equals(
                      		ModelFileManipulator.BEGIN_QUANTITY_TAG)) { 
                      	// beginning of ActionTypeParticipantQuantity
                      	// set the guard:
                        newPart.getQuantity().setGuard(
                            Guard.getIntRepresentation(reader.readLine())); 
                        String quantity = reader.readLine(); // get quantity
                        String maxVal = reader.readLine(); // get max val
                        Integer[] quants = new Integer[2];
                        if ((quantity.equals(
                        		ModelFileManipulator.EMPTY_VALUE)) == false) {
                        	// quantity has a value
                          quants[0] = new Integer(Integer.parseInt(quantity));
                        }
                        if ((maxVal.equals(
                        		ModelFileManipulator.EMPTY_VALUE)) == false) {
                        	// max val has a value
                          quants[1] = new Integer(Integer.parseInt(maxVal));
                        }
                        newPart.getQuantity().setQuantity(quants); // set
                                                                   // quantity
                        reader.readLine(); // get next line (END_QUANTITY_TAG)
                      } else if (currentLine2.equals(
                      		ModelFileManipulator.BEGIN_SSOBJ_TYPE_NAMES_TAG)) { 
                      	// SimSEObjectType names
                        boolean endOfSSObjTypes = false;
                        while (!endOfSSObjTypes) {
                          String currentLine3 = reader.readLine(); // get the
                                                                   // next line
                          if (currentLine3.equals(
                          		ModelFileManipulator.END_SSOBJ_TYPE_NAMES_TAG)) { 
                          	// end of SimSEObjectType names
                            endOfSSObjTypes = true;
                          } else {
                          	// get the SimSEObjectType from the defined object
                          	// types:
                            SimSEObjectType tempType = objectTypes
                                .getObjectType(ssObjTypeType, currentLine3); 
                            if (tempType != null) { // object type was found
                            	// add the object type to the participant:
                            	newPart.addSimSEObjectType(tempType);
                            } else { // object type not found
                              warnings.add("Object type "
                                  + SimSEObjectTypeTypes.getText(ssObjTypeType)
                                  + " " + currentLine3
                                  + " removed -- ignoring " + newAct.getName()
                                  + " action participant of this type");
                            }
                          }
                        }
                      }
                    }
                  } else if (currentLine.equals(
                  		ModelFileManipulator.BEGIN_TRIGGER_TAG)) { // ActionType
                  																							 // Trigger
                    ActionTypeTrigger newTrig; // ActionTypeTrigger to be filled
                                               // in w/ the info. from the file
                    String triggerName = reader.readLine(); // get the trigger
                                                            // name
                    String triggerType = reader.readLine(); // get the trigger
                                                            // type
                    if (triggerType.equals(ActionTypeTrigger.RANDOM)) { 
                    	// random trigger type
                    	// get the frequency:
                      double freq = Double.parseDouble(reader.readLine()); 
                      // create a new random trigger w/ the specified frequency:
                      newTrig = new RandomActionTypeTrigger(triggerName,
                          newAct, freq); 
                    } else if (triggerType.equals(ActionTypeTrigger.USER)) { 
                    	// user trigger type
                      String menuText = reader.readLine();
                      reader.mark(100);
                      String confirm = reader.readLine();
                      if ((confirm.equals("true")) || 
                          (confirm.equals("false"))) { // new format 1/31/07
                        															 // that includes confirm
                        															 // option
                        newTrig = new UserActionTypeTrigger(triggerName, newAct,
                            menuText, Boolean.parseBoolean(confirm));
                      } else { // old format
                        newTrig = new UserActionTypeTrigger(triggerName, newAct,
                          menuText, false); 
                        reader.reset();
                      }
                    } else { // autonomous trigger type
                      newTrig = new AutonomousActionTypeTrigger(triggerName,
                          newAct); // create a new autonomous trigger
                    }
                    String triggerText = reader.readLine(); // get the trigger
                                                            // text
                    // set the trigger text:
                    if (triggerText.equals(
                    		ModelFileManipulator.EMPTY_VALUE) == false) { // trigger
                                                                  		// text 
                    																									// not
                                                                  		// empty
                      newTrig.setTriggerText(triggerText);
                    }
                    String currentLineTrig = reader.readLine();
                    boolean getNextLine = true;
                    if (currentLineTrig.startsWith("<") == false) { 
                    	// new format 4/28/04 that includes trigger/destroyer
                    	// prioritization
                    	// set priority:
                      newTrig.setPriority(Integer.parseInt(currentLineTrig)); 
                    } else {
                      getNextLine = false;
                    }

                    boolean endOfTrig = false;
                    while (!endOfTrig) {
                      if (getNextLine) {
                        currentLineTrig = reader.readLine(); // get the next
                                                             // line
                      } else {
                        getNextLine = true;
                      }
                      if (currentLineTrig.startsWith("<") == false) { 
                      	// new format 5/13/04 that includes game-ending
                      	// triggers/destroyers
                        newTrig.setGameEndingTrigger((new Boolean(
                            currentLineTrig)).booleanValue());
                      } else if (currentLineTrig.equals(
                      		ModelFileManipulator.END_TRIGGER_TAG)) { // end of 
                      																						 // trigger
                        endOfTrig = true;
                        newAct.addTrigger(newTrig);
                      } else if (currentLineTrig.equals(
                      		ModelFileManipulator.BEGIN_PARTICIPANT_TRIGGER_TAG)) { 
                      	// beginning of participant trigger
                        ActionTypeParticipant tempPart = newAct
                            .getParticipant(reader.readLine());
                        if (tempPart != null) { // participant found
                        	// create a new participant trigger w/ the specified
                        	// participant:
                          ActionTypeParticipantTrigger newPartTrig = 
                          	new ActionTypeParticipantTrigger(tempPart); 
                          boolean endOfPartTrig = false;
                          while (!endOfPartTrig) {
                          	// get the next line:
                            String currentLinePartTrig = reader.readLine(); 
                            if (currentLinePartTrig.equals(
                            		ModelFileManipulator.
                            		END_PARTICIPANT_TRIGGER_TAG)) { // end of
                            																		// participant
                                                                // trigger
                              endOfPartTrig = true;
                              newTrig.addParticipantTrigger(newPartTrig);
                            } else if (currentLinePartTrig.equals(
                            		ModelFileManipulator.
                            		BEGIN_PARTICIPANT_CONSTRAINT_TAG)) { 
                            	// beginning of particpiant constraint
                              String ssObjTypeName = reader.readLine();
                              int type = newPartTrig.getParticipant()
                                  .getSimSEObjectTypeType();
                              // get the SimSEObjectType from the defined
                              // objects:
                              SimSEObjectType tempObjType = objectTypes
                                  .getObjectType(type, ssObjTypeName); 
                              if (tempObjType != null) { // such a type exists
                              	/*
                              	 * get the SimSEObjectTypeType from the defined 
                              	 * objects and use that and the SimSEObjectType 
                              	 * name (read from the file) to create a new
                                 * ActionTypeParticipantConstraint:
                              	 */
                                ActionTypeParticipantConstraint newPartConst = 
                                	new ActionTypeParticipantConstraint(
                                			tempObjType); 
                                boolean endOfPartConst = false;
                                while (!endOfPartConst) {
                                  String currentLinePartConst = reader
                                      .readLine(); // get the next line
                                  if (currentLinePartConst.equals(
                                      		ModelFileManipulator.
                                      		END_PARTICIPANT_CONSTRAINT_TAG)) { 
                                  	// end of participant constraint
                                    endOfPartConst = true;
                                    newPartTrig.addConstraint(newPartConst);
                                  } else if (currentLinePartConst.equals(
                                  		ModelFileManipulator.
                                  		BEGIN_ATTRIBUTE_CONSTRAINT_TAG)) { 
                                  	// beginning of attribute constraint
                                  	// ge the attribute name:
                                    String attName = reader.readLine(); 
                                    // get the actual Attribute object:
                                    Attribute att = newPartConst
                                        .getSimSEObjectType().getAttribute(
                                            attName); 
                                    if (att == null) { // attribute not found
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
                                              + " attribute removed -- " +
                                              		"ignoring this attribute in "
                                              + newAct.getName()
                                              + " action "
                                              + tempPart.getName()
                                              + " participant");
                                    } else { // attribute found
                                    	// get the attribute guard:
                                      String guard = reader.readLine(); 
                                      // create a new attribute constraint with
                                      // the specified attribute:
                                      ActionTypeParticipantAttributeConstraint 
                                      newAttConst = 
                                      	new 
                                      	ActionTypeParticipantAttributeConstraint(
                                      			att); 
                                      newAttConst.setGuard(guard); // set the
                                                                   // guard
                                      String value = reader.readLine(); // get
                                                                        // the
                                                                        // value
                                      if ((value.equals(
                                      		ModelFileManipulator.EMPTY_VALUE)) == 
                                      			false) { // attribute has a
                                                     // constraining value
                                        if (att.getType() == 
                                        	AttributeTypes.BOOLEAN) { // boolean
                                                                    // attribute
                                          if (value.equals((new Boolean(true))
                                              .toString())) {
                                            newAttConst.setValue(new Boolean(
                                                true));
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
                                                    + " attribute changed " +
                                                    		"type -- " + 
                                                    		" trigger value no " +
                                                    		"longer valid -- " +
                                                    		"ignoring trigger " +
                                                    		"value for this " +
                                                    		"attribute in "
                                                    + newAct.getName()
                                                    + " action "
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
                                            if (numAtt.isMinBoundless() == 
                                            	false) { // has a minimum 
                                            					 // constraining value
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
                                                        + " attribute " +
                                                        		"changed min " +
                                                        		"value -- "
                                                        + " trigger value no " +
                                                        		"longer within " +
                                                        		"acceptable " +
                                                        		"range -- ignoring "
                                                        + " trigger value " +
                                                        		"for this " +
                                                        		"attribute in "
                                                        + newAct.getName()
                                                        + " action "
                                                        + tempPart.getName()
                                                        + " participant");
                                                valid = false;
                                              }
                                            }
                                            if (numAtt.isMaxBoundless() == 
                                            	false) { // has a maximum 
                                            					 // constraining value
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
                                                        + " trigger value no " +
                                                        		"longer within " +
                                                        		"acceptable " +
                                                        		"range -- ignoring "
                                                        + " trigger value " +
                                                        		"for this " +
                                                        		"attribute in "
                                                        + newAct.getName()
                                                        + " action "
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
                                                    		"type -- trigger " +
                                                    		"value no longer " +
                                                    		"valid -- ignoring "
                                                    + " trigger value for " +
                                                    		"this attribute in "
                                                    + newAct.getName()
                                                    + " action "
                                                    + tempPart.getName()
                                                    + " participant");
                                          }
                                        } else if (att.getType() == 
                                        	AttributeTypes.DOUBLE) { // double
                                                                   // attribute
                                          try {
                                            boolean valid = true;
                                            Double doubleVal = 
                                            	new Double(value);
                                            NumericalAttribute numAtt =
                                            	(NumericalAttribute)att;
                                            if (numAtt.isMinBoundless() == 
                                            	false) { // has a minimum 
                                            				   // constraining value
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
                                                        + " attribute " +
                                                        		"changed min " +
                                                        		"value -- " +
                                                        		"trigger value " +
                                                        		"no longer " +
                                                        		"within " +
                                                        		"acceptable " +
                                                        		"range -- ignoring "
                                                        + " trigger value " +
                                                        		"for this " +
                                                        		"attribute in "
                                                        + newAct.getName()
                                                        + " action "
                                                        + tempPart.getName()
                                                        + " participant");
                                                valid = false;
                                              }
                                            }
                                            if (numAtt.isMaxBoundless() == 
                                            	false) { // has a maximum 
                                            					 // constraining value
                                              if (doubleVal.doubleValue() > 
                                              numAtt.getMaxValue().
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
                                                        + " attribute " +
                                                        		"changed max " +
                                                        		"value -- " +
                                                        		"trigger value " +
                                                        		"no longer " +
                                                        		"within " +
                                                        		"acceptable " +
                                                        		"range -- " +
                                                        		"ignoring "
                                                        + " trigger value " +
                                                        		"for this " +
                                                        		"attribute in "
                                                        + newAct.getName()
                                                        + " action "
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
                                                    		"type -- trigger " +
                                                    		"value no longer " +
                                                    		"valid -- ignoring "
                                                    + " trigger value for " +
                                                    		"this attribute in "
                                                    + newAct.getName()
                                                    + " action "
                                                    + tempPart.getName()
                                                    + " participant");
                                          }
                                        } else if (att.getType() == 
                                        	AttributeTypes.STRING) { // string
                                                                   // attribute
                                          newAttConst.setValue(value);
                                        }
                                      }
                                      String newLn = reader.readLine();
                                      if (newLn.startsWith("<") == false) {
                                      	// new format 5/13/04 that includes
                                      	// scoring attributes
                                        newAttConst
                                            .setScoringAttribute((new Boolean(
                                                newLn)).booleanValue());
                                      }
                                      // else read END_ATTRIBUTE_CONSTRAINT_TAG
                                      // in
                                      
                                      /*
                                       * add compelted attribute constraint to
                                       * the participant constraint:
                                       */
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

                  else if (currentLine.equals(
                  		ModelFileManipulator.BEGIN_DESTROYER_TAG)) { 
                  	// ActionTypeDestroyer
                    ActionTypeDestroyer newDest; // ActionTypeDestroyer to be
                                                 // filled in w/ info. from the
                                                 // file
                    String destroyerName = reader.readLine(); // get the
                                                              // destroyer name
                    String destroyerType = reader.readLine(); // get the
                                                              // destroyer type
                    if (destroyerType.equals(ActionTypeDestroyer.RANDOM)) { 
                    	// random destroyer type
                    	// get the frequency:
                      double freq = Double.parseDouble(reader.readLine()); 
                      // create a new random destroyer w/ the specified
                      // frequency:
                      newDest = new RandomActionTypeDestroyer(destroyerName,
                          newAct, freq); 
                    } else if (destroyerType.equals(
                    		ActionTypeDestroyer.TIMED)) { // timed destroyer type
                      int time = Integer.parseInt(reader.readLine()); // get the
                                                                      // time
                      newDest = new TimedActionTypeDestroyer(destroyerName,
                          newAct, time); // create a new timed destroyer with
                                         // the specified time
                    } else if (destroyerType.equals(ActionTypeDestroyer.USER)) {
                    	// user destroyer type
                    	// create a new user destroyer with the menu text from
                    	// the file:
                      newDest = new UserActionTypeDestroyer(destroyerName,
                          newAct, reader.readLine()); 
                    } else { // autonomous destoryer type
                    	// create a new autonomous destroyer:
                      newDest = new AutonomousActionTypeDestroyer(
                          destroyerName, newAct); 
                    }
                    String destroyerText = reader.readLine(); // get the
                                                              // destroyer text
                    // set destroyer text:
                    if (destroyerText.equals(
                    		ModelFileManipulator.EMPTY_VALUE) == false) { 
                    	// destroyer text not empty
                      newDest.setDestroyerText(destroyerText);
                    }
                    String currentLineDest = reader.readLine();
                    boolean getNextLine = true;
                    if (currentLineDest.startsWith("<") == false) { 
                    	// new format 4/28/04 that includes trigger/destroyer
                      // prioritization
                    	
                    	// set priority:
                      newDest.setPriority(Integer.parseInt(currentLineDest)); 
                    } else {
                      getNextLine = false;
                    }

                    boolean endOfDest = false;
                    while (!endOfDest) {
                      if (getNextLine) {
                        currentLineDest = reader.readLine(); // get the next
                                                             // line
                      } else {
                        getNextLine = true;
                      }
                      if (currentLineDest.startsWith("<") == false) { 
                      	// new format 5/13/04 that includes game-ending
                        // triggers/destroyers
                        newDest.setGameEndingDestroyer((new Boolean(
                            currentLineDest)).booleanValue());
                      } else if (currentLineDest.equals(
                      		ModelFileManipulator.END_DESTROYER_TAG)) { 
                      	// end of destroyer
                        endOfDest = true;
                        newAct.addDestroyer(newDest);
                      } else if (currentLineDest.equals(
                      		ModelFileManipulator.
                      		BEGIN_PARTICIPANT_DESTROYER_TAG)) { // beginning
                                                              // of participant
                                                              // destroyer
                        ActionTypeParticipant tempPart = newAct
                            .getParticipant(reader.readLine());
                        if (tempPart != null) { // participant found
                        	/*
                        	 * create a new participant destroyer w/ the
                        	 * specified participant:
                        	 */
                          ActionTypeParticipantDestroyer newPartDest = 
                          	new ActionTypeParticipantDestroyer(tempPart); 
                          boolean endOfPartDest = false;
                          while (!endOfPartDest) {
                          	// get the next line:
                            String currentLinePartDest = reader.readLine(); 
                            if (currentLinePartDest.equals(
                            		ModelFileManipulator.
                            		END_PARTICIPANT_DESTROYER_TAG)) { // end of
                                                                  // participant
                                                                  // destroyer
                              endOfPartDest = true;
                              newDest.addParticipantDestroyer(newPartDest);
                            } else if (currentLinePartDest.equals(
                            		ModelFileManipulator.
                            		BEGIN_PARTICIPANT_CONSTRAINT_TAG)) { 
                            	// beginning of particpiant constraint
                              String ssObjTypeName = reader.readLine();
                              int type = newPartDest.getParticipant()
                                  .getSimSEObjectTypeType();
                              // get the SimSEObjectType from the defined
                              // objects:
                              SimSEObjectType tempObjType = objectTypes
                                  .getObjectType(type, ssObjTypeName); 
                              if (tempObjType != null) { // such a type exists
                              	/*
                              	 * get the SimSEObjectTypeType from the
                              	 * defined objects and use that and the
                              	 * SimSEObjectType name (read from the file) to
                              	 * create a new ActionTypeParticipantConstraint:
                              	 */
                                ActionTypeParticipantConstraint newPartConst = 
                                	new ActionTypeParticipantConstraint(
                                			tempObjType); 
                                boolean endOfPartConst = false;
                                while (!endOfPartConst) {
                                  String currentLinePartConst = reader
                                      .readLine(); // get the next line
                                  if (currentLinePartConst.equals(
                                  		ModelFileManipulator.
                                  		END_PARTICIPANT_CONSTRAINT_TAG)) { 
                                  	// end of participant constraint
                                    endOfPartConst = true;
                                    newPartDest.addConstraint(newPartConst);
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
                                      // don't add a warning because one was
                                      // already added for the trigger
                                    } else { // attribute found
                                    	// get the attribute guard:
                                      String guard = reader.readLine(); 
                                      /* 
                                       * create a new attribute constraint with
                                       * the specified attribute:
                                       */
                                      ActionTypeParticipantAttributeConstraint 
                                      newAttConst = 
                                      	new 
                                      	ActionTypeParticipantAttributeConstraint(
                                          att); 
                                      newAttConst.setGuard(guard); // set the
                                                                   // guard
                                      String value = reader.readLine(); // get
                                                                        // the
                                                                        // value
                                      if ((value.equals(
                                      		ModelFileManipulator.EMPTY_VALUE)) == 
                                      			false) { // attribute has a 
                                      							 // constraining value
                                        if (att.getType() == 
                                        	AttributeTypes.BOOLEAN) { // boolean
                                                                    // attribute
                                          if (value.equals((new Boolean(true))
                                              .toString())) {
                                            newAttConst.setValue(new Boolean(
                                                true));
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
                                                    + " attribute changed " +
                                                    		"type -- destroyer " +
                                                    		"value no longer " +
                                                    		"valid -- ignoring "
                                                    + " destroyer value for " +
                                                    		"this attribute in "
                                                    + newAct.getName()
                                                    + " action "
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
                                            if (numAtt.isMinBoundless() == 
                                            	false) { // has a minimum 
                                            				 	 // constraining value
                                              if (intVal.intValue() < numAtt
                                                  .getMinValue().intValue()) {
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
                                                        		"changed min " +
                                                        		"value -- " +
                                                        		"trigger value " +
                                                        		"no longer " +
                                                        		"within " +
                                                        		"acceptable " +
                                                        		"range -- ignoring "
                                                        + " trigger value " +
                                                        		"for this " +
                                                        		"attribute in "
                                                        + newAct.getName()
                                                        + " action "
                                                        + tempPart.getName()
                                                        + " participant");
                                                valid = false;
                                              }
                                            }
                                            if (numAtt.isMaxBoundless() == 
                                            	false) {
                                            // has a maximum constraining value
                                              if (intVal.intValue() > numAtt
                                                  .getMaxValue().intValue()) {
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
                                                        		"value -- " +
                                                        		"trigger value " +
                                                        		"no longer " +
                                                        		"within " +
                                                        		"acceptable " +
                                                        		"range -- ignoring "
                                                        + " trigger value " +
                                                        		"for this " +
                                                        		"attribute in "
                                                        + newAct.getName()
                                                        + " action "
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
                                                    		"type -- destroyer " +
                                                    		"value no longer " +
                                                    		"valid -- ignoring "
                                                    + " destroyer value " +
                                                    		"for this attribute in "
                                                    + newAct.getName()
                                                    + " action "
                                                    + tempPart.getName()
                                                    + " participant");
                                          }
                                        } else if (att.getType() == 
                                        	AttributeTypes.DOUBLE) { // double
                                                                   // attribute
                                          try {
                                            boolean valid = true;
                                            Double doubleVal = 
                                            	new Double(value);
                                            NumericalAttribute numAtt = 
                                            	(NumericalAttribute)att;
                                            if (numAtt.isMinBoundless() == 
                                            	false) {
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
                                                        + " attribute " +
                                                        		"changed min " +
                                                        		"value -- " +
                                                        		"trigger value " +
                                                        		"no longer " +
                                                        		"within " +
                                                        		"acceptable " +
                                                        		"range -- " +
                                                        		"ignoring " +
                                                        		"trigger value " +
                                                        		"for this " +
                                                        		"attribute in "
                                                        + newAct.getName()
                                                        + " action "
                                                        + tempPart.getName()
                                                        + " participant");
                                                valid = false;
                                              }
                                            }
                                            if (numAtt.isMaxBoundless() == 
                                            	false) { // has a maximum 
                                            			   	 // constraining value
                                              if (doubleVal.doubleValue() > 
                                              numAtt.getMaxValue().
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
                                                        + " attribute " +
                                                        		"changed max " +
                                                        		"value -- " +
                                                        		"trigger value " +
                                                        		"no longer " +
                                                        		"within " +
                                                        		"acceptable " +
                                                        		"range -- ignoring "
                                                        + " trigger value " +
                                                        		"for this " +
                                                        		"attribute in "
                                                        + newAct.getName()
                                                        + " action "
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
                                                    		"type -- destroyer " +
                                                    		"value no longer " +
                                                    		"valid -- ignoring "
                                                    + " destroyer value for " +
                                                    		"this attribute in "
                                                    + newAct.getName()
                                                    + " action "
                                                    + tempPart.getName()
                                                    + " participant");
                                          }
                                        } else if (att.getType() == 
                                        	AttributeTypes.STRING) { // string
                                        													 // attribute
                                          newAttConst.setValue(value);
                                        }
                                      }
                                      String newLn = reader.readLine();
                                      if (newLn.startsWith("<") == false) { 
                                      	// new format 5/13/04 that includes
                                      	// scoring attributes
                                        newAttConst
                                            .setScoringAttribute((new Boolean(
                                                newLn)).booleanValue());
                                      }
                                      // else read END_ATTRIBUTE_CONSTRAINT_TAG
                                      // in
                                      
                                      /*
                                       * add completed attribute constraint to
                                       * the participant constraint:
                                       */
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
      }
      reader.close();
    } catch (FileNotFoundException e) {
      JOptionPane.showMessageDialog(null,
          ("Cannot find action file " + inputFile.getPath()), "File Not Found",
          JOptionPane.WARNING_MESSAGE);
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