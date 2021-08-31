/*
 * This class is responsible for generating all of the code for the trigger
 * checker component in SimSE
 */

package simse.codegenerator.logicgenerator;

import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;

import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantAttributeConstraint;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantConstraint;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantTrigger;
import simse.modelbuilder.actionbuilder.ActionTypeTrigger;
import simse.modelbuilder.actionbuilder.AttributeGuard;
import simse.modelbuilder.actionbuilder.AutonomousActionTypeTrigger;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.actionbuilder.RandomActionTypeTrigger;
import simse.modelbuilder.actionbuilder.UserActionTypeTrigger;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.rulebuilder.Rule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Vector;

import javax.swing.JOptionPane;

public class TriggerCheckerGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into
  private DefinedActionTypes actTypes; // holds all of the defined action types
  private FileWriter writer;
  private File trigFile;
  private Vector<String> vectors; // for keeping track of which vectors are 
  																// being used in generated code so that you 
  															  // don't generate the same ones more than once
  																// -- e.g., Vector programmers 
  																// state.getEmployeeStateRepository().
  																// getProgrammerStateRepository().getAll()
  																// will be generated more than once if you
  																// don't keep track of this.
  private Vector<ActionTypeTrigger> nonPrioritizedTriggers;
  private Vector<ActionTypeTrigger> prioritizedTriggers;

  public TriggerCheckerGenerator(DefinedActionTypes actTypes, File directory) {
    this.actTypes = actTypes;
    this.directory = directory;
    vectors = new Vector<String>();
    initializeTriggerLists();
  }

  public void generate() {
    try {
      trigFile = new File(directory, ("simse\\logic\\TriggerChecker.java"));
      if (trigFile.exists()) {
        trigFile.delete(); // delete old version of file
      }
      writer = new FileWriter(trigFile);
      writer
          .write("/* File generated by: simse.codegenerator.logicgenerator.TriggerCheckerGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.logic;");
      writer.write(NEWLINE);
      writer.write("import simse.state.*;");
      writer.write(NEWLINE);
      writer.write("import simse.gui.*;");
      writer.write(NEWLINE);
      writer.write("import simse.adts.objects.*;");
      writer.write(NEWLINE);
      writer.write("import simse.adts.actions.*;");
      writer.write(NEWLINE);
      writer.write("import java.util.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.*;");
      writer.write(NEWLINE);
      writer.write("public class TriggerChecker");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);

      // member variables:
      writer.write("private State state;");
      writer.write(NEWLINE);
      writer.write("private RuleExecutor ruleExec;");
      writer.write(NEWLINE);
      writer.write("private Random ranNumGen;");
      writer.write(NEWLINE);

      // constructor:
      writer.write("public TriggerChecker(State s, RuleExecutor r)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("state = s;");
      writer.write(NEWLINE);
      writer.write("ruleExec = r;");
      writer.write(NEWLINE);
      writer.write("ranNumGen = new Random();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);

      // update function:
      writer
          .write("public void update(boolean updateUserTrigsOnly, JFrame gui)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      int counter = 0;
      // generate prioritized triggers:
      for (int i = 0; i < prioritizedTriggers.size(); i++) {
        generateTriggerChecker(prioritizedTriggers.elementAt(i), counter);
        counter++;
      }
      // generate non-prioritized triggers:
      for (int i = 0; i < nonPrioritizedTriggers.size(); i++) {
        generateTriggerChecker(nonPrioritizedTriggers.elementAt(i), counter);
        counter++;
      }
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + trigFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }

  private void generateTriggerChecker(ActionTypeTrigger outerTrig, 
  		int counter) {
    try {
      ActionType action = outerTrig.getActionType();
      if (!(outerTrig instanceof UserActionTypeTrigger)) { // not a user trigger
        writer.write("if(!updateUserTrigsOnly)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
      }
      Vector<ActionTypeParticipantTrigger> triggers = 
      	outerTrig.getAllParticipantTriggers();
      for (int j = 0; j < triggers.size(); j++) {
        ActionTypeParticipantTrigger trig = triggers.elementAt(j);
        String metaTypeName = CodeGeneratorUtils.getUpperCaseLeading(
        		SimSEObjectTypeTypes.getText(
        				trig.getParticipant().getSimSEObjectTypeType()));
        writer.write("Vector <" + metaTypeName + "> " + 
        		trig.getParticipant().getName().toLowerCase()
            + "s" + counter + " = new Vector<" + metaTypeName + ">();");
        writer.write(NEWLINE);
        Vector<ActionTypeParticipantConstraint> constraints = 
        	trig.getAllConstraints();
        for (int k = 0; k < constraints.size(); k++) {
          ActionTypeParticipantConstraint constraint = constraints.elementAt(k);
          String objTypeName = constraint.getSimSEObjectType().getName();
          if (vectorContainsString(vectors, 
          		(objTypeName.toLowerCase() + "s")) == false) { // this vector has
          																									 // not been 
          																									 // generated 
          																									 // already
            String uCaseObjTypeName = 
            	CodeGeneratorUtils.getUpperCaseLeading(objTypeName);
          	writer.write("Vector <" + uCaseObjTypeName + "> "
                + objTypeName.toLowerCase()
                + "s = state.get"
                + CodeGeneratorUtils.getUpperCaseLeading(
                		SimSEObjectTypeTypes.getText(constraint.
                				getSimSEObjectType().getType())) + 
                				"StateRepository().get" + 
                				CodeGeneratorUtils.getUpperCaseLeading(objTypeName) + 
                				"StateRepository().getAll();");
            // generate it
            writer.write(NEWLINE);
            if (outerTrig instanceof UserActionTypeTrigger) { // user trigger --
                                                              // can be used by
                                                            	// others
            	// add it to the list:
              vectors.add(objTypeName.toLowerCase() + "s"); 
            }
          }
          writer.write("for(int i=0; i<" + objTypeName.toLowerCase()
              + "s.size(); i++)");
          writer.write(NEWLINE);
          writer.write(OPEN_BRACK);
          writer.write(NEWLINE);
          writer.write(CodeGeneratorUtils.getUpperCaseLeading(objTypeName) + 
          		" a = " + objTypeName.toLowerCase() + "s.elementAt(i);");
          writer.write(NEWLINE);
          writer
              .write("Vector<" + CodeGeneratorUtils.getUpperCaseLeading(
              		action.getName()) + 
              		"Action> allActions = state.getActionStateRepository().get"
                  + CodeGeneratorUtils.getUpperCaseLeading(action.getName())
                  + "ActionStateRepository().getAllActions(a);");
          writer.write(NEWLINE);
          writer.write("boolean alreadyInAction = false;");
          writer.write(NEWLINE);

          if ((trig.getParticipant().getSimSEObjectTypeType() == 
          	SimSEObjectTypeTypes.EMPLOYEE) || 
          	(trig.getParticipant().getSimSEObjectTypeType() == 
              	SimSEObjectTypeTypes.ARTIFACT)) { // employees and artifacts can 
          																				// only be in one of these 
          																				// actions in this role at a 
          																				// time
            writer.write("for(int j=0; j<allActions.size(); j++)");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write(CodeGeneratorUtils.getUpperCaseLeading(
            		action.getName()) + "Action b = allActions.elementAt(j);");
            writer.write(NEWLINE);
            writer.write("if(b.getAll" + trig.getParticipant().getName()
                + "s().contains(a))");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write("alreadyInAction = true;");
            writer.write(NEWLINE);
            writer.write("break;");
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
          }

          writer.write("if((alreadyInAction == false) ");

          ActionTypeParticipantAttributeConstraint[] attConstraints = constraint
              .getAllAttributeConstraints();
          for (int m = 0; m < attConstraints.length; m++) {
            ActionTypeParticipantAttributeConstraint attConst = 
            	attConstraints[m];
            if (attConst.isConstrained()) {
              writer.write(" && (a.get"
                  + CodeGeneratorUtils.getUpperCaseLeading(
                  		attConst.getAttribute().getName()) + "() ");
              if (attConst.getAttribute().getType() == AttributeTypes.STRING) {
                writer.write(".equals(" + "\"" + attConst.getValue().toString()
                    + "\")");
              } else {
                if (attConst.getGuard().equals(AttributeGuard.EQUALS)) {
                  writer.write("== ");
                } else {
                  writer.write(attConst.getGuard() + " ");
                }
                writer.write(attConst.getValue().toString());
              }
              writer.write(")");
            }
          }
          writer.write(")");
          writer.write(NEWLINE);
          writer.write(OPEN_BRACK);
          writer.write(NEWLINE);
          writer.write(trig.getParticipant().getName().toLowerCase() + "s"
              + counter + ".add(a);");
          writer.write(NEWLINE);
          writer.write(CLOSED_BRACK);
          writer.write(NEWLINE);
          writer.write(CLOSED_BRACK);
          writer.write(NEWLINE);
        }
      }
      if (outerTrig instanceof UserActionTypeTrigger) {
        writer.write("if(");
      } else if ((outerTrig instanceof AutonomousActionTypeTrigger)
          || (outerTrig instanceof RandomActionTypeTrigger)) {
        writer.write("while(");
      }
      Vector<ActionTypeParticipant> parts = action.getAllParticipants();
      for (int k = 0; k < parts.size(); k++) {
        ActionTypeParticipant part = parts.elementAt(k);
        if (k > 0) { // not on first element
          writer.write(" && ");
        }
        writer.write("(" + part.getName().toLowerCase() + "s" + counter
            + ".size() ");
        if (part.getQuantity().isMinValBoundless()) {
          if (part.getSimSEObjectTypeType() == SimSEObjectTypeTypes.EMPLOYEE) {
            writer.write("> 0)");
          } else { // non-employee
            writer.write(">= 0)");
          }
        } else { // min val bounded
          writer
              .write(" >= " + part.getQuantity().getMinVal().intValue() + ")");
        }
      }
      writer.write(")");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      if (outerTrig instanceof UserActionTypeTrigger) {
        // go through each participant, and if it's an employee, add the text to
        // their menu:
        for (int k = 0; k < parts.size(); k++) {
          ActionTypeParticipant part = parts.elementAt(k);
          if (part.getSimSEObjectTypeType() == SimSEObjectTypeTypes.EMPLOYEE) {
            writer.write("for(int j=0; j<" + part.getName().toLowerCase() + "s"
                + counter + ".size(); j++)");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write("Employee z = (Employee)"
                + part.getName().toLowerCase() + "s" + counter
                + ".elementAt(j);");
            writer.write(NEWLINE);
            writer.write("z.addMenuItem(\""
                + ((UserActionTypeTrigger) outerTrig).getMenuText() + "\");");
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
          }
        }
      } else if ((outerTrig instanceof AutonomousActionTypeTrigger)
          || (outerTrig instanceof RandomActionTypeTrigger)) {
        writer.write(CodeGeneratorUtils.getUpperCaseLeading(action.getName()) + 
        		"Action a = new " + CodeGeneratorUtils.getUpperCaseLeading(
        				action.getName()) + "Action();");
        writer.write(NEWLINE);
        for (int k = 0; k < parts.size(); k++) {
          ActionTypeParticipant part = parts.elementAt(k);
          if (part.getQuantity().isMaxValBoundless()) {
            writer.write("while(true)");
          } else { // max bounded
            writer.write("for(int i=0; i<"
                + part.getQuantity().getMaxVal().intValue() + "; i++)");
          }
          writer.write(NEWLINE);
          writer.write(OPEN_BRACK);
          writer.write(NEWLINE);
          writer.write("if(" + part.getName().toLowerCase() + "s" + counter
              + ".size() > 0)");
          writer.write(NEWLINE);
          writer.write(OPEN_BRACK);
          writer.write(NEWLINE);
          writer.write(CodeGeneratorUtils.getUpperCaseLeading(
          		SimSEObjectTypeTypes.getText(part.getSimSEObjectTypeType())) + 
          		" a" + k + " = " + 
          						part.getName().toLowerCase() + "s" + counter);
          if ((part.getSimSEObjectTypeType() == SimSEObjectTypeTypes.ARTIFACT)
          		|| (part.getSimSEObjectTypeType() == 
          			SimSEObjectTypeTypes.EMPLOYEE)) { // can't be in more than one 
          																				// action of the same type 
          																				// at a time
          	writer.write(".remove(0);");
          }
          else {
          	writer.write(".elementAt(0);");
          }
          writer.write(NEWLINE);
          writer.write("a.add" + part.getName() + "(a" + k + ");");
          writer.write(NEWLINE);
          writer.write(CLOSED_BRACK);
          writer.write(NEWLINE);
          writer.write("else");
          writer.write(NEWLINE);
          writer.write(OPEN_BRACK);
          writer.write(NEWLINE);
          writer.write("break;");
          writer.write(NEWLINE);
          writer.write(CLOSED_BRACK);
          writer.write(NEWLINE);
          writer.write(CLOSED_BRACK);
          writer.write(NEWLINE);
        }

        // RANDOM TRIGGER:
        if ((outerTrig instanceof RandomActionTypeTrigger)
            && (((RandomActionTypeTrigger) outerTrig).getFrequency() < 100.0)) {
        	// have to put the < 100 here because otherwise it might not always be
        	// triggered (if ran num is 100 and frequency is 100)
          writer.write("if((ranNumGen.nextDouble() * 100.0) < "
              + (((RandomActionTypeTrigger) outerTrig).getFrequency()) + ")");
          writer.write(NEWLINE);
          writer.write(OPEN_BRACK);
          writer.write(NEWLINE);
        }
        writer.write("// set all overhead texts:");
        writer.write(NEWLINE);
        writer.write("Vector<SSObject> allPart = a.getAllParticipants();");
        writer.write(NEWLINE);
        writer.write("for(int i=0; i<allPart.size(); i++)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        writer.write("SSObject tempObj = allPart.elementAt(i);");
        writer.write(NEWLINE);
        writer.write("if(tempObj instanceof Employee)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        if ((outerTrig.getTriggerText() != null)
            && (outerTrig.getTriggerText().length() > 0)) { // has trigger text
          writer.write("((Employee)tempObj).setOverheadText(\""
              + outerTrig.getTriggerText() + "\");");
          writer.write(NEWLINE);
        }
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
        writer.write("else if(tempObj instanceof Customer)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        writer.write("((Customer)tempObj).setOverheadText(\""
            + outerTrig.getTriggerText() + "\");");
        writer.write(NEWLINE);
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
        writer.write("state.getActionStateRepository().get"
            + CodeGeneratorUtils.getUpperCaseLeading(action.getName())
            + "ActionStateRepository().add(a);");
        writer.write(NEWLINE);
        // execute all trigger rules:
        Vector<Rule> trigRules = action.getAllTriggerRules();
        for (int i = 0; i < trigRules.size(); i++) {
          Rule tRule = trigRules.elementAt(i);
          writer.write("ruleExec.update(gui, RuleExecutor.UPDATE_ONE, \""
              + tRule.getName() + "\", a);");
          writer.write(NEWLINE);
        }

        // game-ending:
        if (outerTrig.isGameEndingTrigger()) {
          writer.write("// stop game and give score:");
          writer.write(NEWLINE);
          writer.write(CodeGeneratorUtils.getUpperCaseLeading(action.getName())
              + "Action t111 = (" + CodeGeneratorUtils.getUpperCaseLeading(
              		action.getName()) + "Action)a;");
          writer.write(NEWLINE);
          // find the scoring attribute:
          ActionTypeParticipantTrigger scoringPartTrig = null;
          ActionTypeParticipantConstraint scoringPartConst = null;
          ActionTypeParticipantAttributeConstraint scoringAttConst = null;
          Vector<ActionTypeParticipantTrigger> partTrigs = 
          	outerTrig.getAllParticipantTriggers();
          for (int k = 0; k < partTrigs.size(); k++) {
            ActionTypeParticipantTrigger partTrig = partTrigs.elementAt(k);
            Vector<ActionTypeParticipantConstraint> partConsts = 
            	partTrig.getAllConstraints();
            for (int m = 0; m < partConsts.size(); m++) {
              ActionTypeParticipantConstraint partConst = 
              	partConsts.elementAt(m);
              ActionTypeParticipantAttributeConstraint[] attConsts = partConst
                  .getAllAttributeConstraints();
              for (int n = 0; n < attConsts.length; n++) {
                if (attConsts[n].isScoringAttribute()) {
                  scoringAttConst = attConsts[n];
                  scoringPartConst = partConst;
                  scoringPartTrig = partTrig;
                  break;
                }
              }
            }
          }
          if ((scoringAttConst != null) && (scoringPartConst != null)
              && (scoringPartTrig != null)) {
            writer.write("if(t111.getAll"
                + scoringPartTrig.getParticipant().getName()
                + "s().size() > 0)");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write(CodeGeneratorUtils.getUpperCaseLeading(scoringPartConst
                .getSimSEObjectType().getName())
                + " t = ("
                + CodeGeneratorUtils.getUpperCaseLeading(
                		scoringPartConst.getSimSEObjectType().getName()) + 
                		")(t111.getAll" + 
                		scoringPartTrig.getParticipant().getName() + 
                		"s().elementAt(0));");
            writer.write(NEWLINE);
            writer.write("if(t != null)");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            if (scoringAttConst.getAttribute().getType() == 
            	AttributeTypes.INTEGER) {
              writer.write("int");
            } else if (scoringAttConst.getAttribute().getType() == 
            	AttributeTypes.DOUBLE) {
              writer.write("double");
            } else if (scoringAttConst.getAttribute().getType() == 
            	AttributeTypes.STRING) {
              writer.write("String");
            } else if (scoringAttConst.getAttribute().getType() == 
            	AttributeTypes.BOOLEAN) {
              writer.write("boolean");
            }
            writer.write(" v = t.get"
                + scoringAttConst.getAttribute().getName() + "();");
            writer.write(NEWLINE);
            writer.write("state.getClock().stop();");
            writer.write(NEWLINE);
            writer.write("state.setScore(v);");
            writer.write(NEWLINE);
            writer.write("((SimSEGUI)gui).update();");
            writer.write(NEWLINE);
            writer
                .write("JOptionPane.showMessageDialog(null, (\"Your score is \" + v), \"Game over!\", JOptionPane.INFORMATION_MESSAGE);");
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
          }
        }

        // RANDOM TRIGGER:
        if ((outerTrig instanceof RandomActionTypeTrigger)
            && (((RandomActionTypeTrigger) outerTrig).getFrequency() < 100.0)) {
        	// have to put the < 100 here because otherwise it might not always be
        	// triggered (if ran num is 100 and frequency is 100)
          writer.write(CLOSED_BRACK);
          writer.write(NEWLINE);
        }
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
      }
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);

      // JOINING existing actions:
      if ((outerTrig instanceof UserActionTypeTrigger) && 
          (action.isJoiningAllowed())) {
        int cnt = counter + 2;
        writer.write("Vector a" + cnt
            + "s = state.getActionStateRepository().get"
            + CodeGeneratorUtils.getUpperCaseLeading(action.getName())
            + "ActionStateRepository().getAllActions();");
        writer.write(NEWLINE);
        writer.write("if(a" + cnt + "s.size() == 0)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        writer.write("Vector f" + cnt
            + " = state.getEmployeeStateRepository().getAll();");
        writer.write(NEWLINE);
        writer.write("for(int i=0; i<f" + cnt + ".size(); i++)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        writer.write("((Employee)f" + cnt
            + ".elementAt(i)).removeMenuItem(\"JOIN "
            + ((UserActionTypeTrigger) outerTrig).getMenuText() + "\");");
        writer.write(NEWLINE);
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
        writer.write("for(int i=0; i<a" + cnt + "s.size(); i++)");
        writer.write(NEWLINE);
        writer.write(OPEN_BRACK);
        writer.write(NEWLINE);
        writer.write(CodeGeneratorUtils.getUpperCaseLeading(action.getName()) + 
        		"Action a" + cnt + " = (" + CodeGeneratorUtils.getUpperCaseLeading(
        				action.getName()) + "Action)a" + cnt + "s.elementAt(i);");
        writer.write(NEWLINE);
        // go through all participants:
        for (int j = 0; j < parts.size(); j++) {
          ActionTypeParticipant part = parts.elementAt(j);
          if (part.getSimSEObjectTypeType() == SimSEObjectTypeTypes.EMPLOYEE) 
          	// employee participant, hence, a role this employee could play
          {
            writer.write("if(a" + cnt + ".getAll" + part.getName()
                + "s().size() < ");
            if (part.getQuantity().isMaxValBoundless()) {
              writer.write("999999");
            } else { // max val has a value
              writer.write((part.getQuantity().getMaxVal()).toString());
            }
            writer.write(")");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write("Vector b" + cnt
                + "s = state.getEmployeeStateRepository().getAll();");
            writer.write(NEWLINE);
            writer.write("for(int j=0; j<b" + cnt + "s.size(); j++)");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write("Employee b" + cnt + " = (Employee)b" + cnt
                + "s.elementAt(j);");
            writer.write(NEWLINE);
            writer.write("if((");
            // go through each SimSEObjectType:
            Vector<SimSEObjectType> types = part.getAllSimSEObjectTypes();
            for (int k = 0; k < types.size(); k++) {
              SimSEObjectType tempType = types.elementAt(k);
              if (k > 0) { // not on first element
                writer.write(" || ");
              }
              writer.write("(b" + cnt + " instanceof "
                  + CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) +
                  ")");
            }
            writer.write(") && (a" + cnt + ".getAll" + part.getName()
                + "s().contains(b" + cnt + ") == false))");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write("boolean z" + cnt + " = true;");
            writer.write(NEWLINE);
            //if(part.isRestricted()) // participant is restricted
            //{
            writer.write("for(int k=0; k<a" + cnt + "s.size(); k++)");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write(
            		CodeGeneratorUtils.getUpperCaseLeading(action.getName()) + 
            		"Action a" + cnt + "b = (" + 
            		CodeGeneratorUtils.getUpperCaseLeading(action.getName()) + 
            		"Action)a" + cnt + "s.elementAt(k);");
            writer.write(NEWLINE);
            writer.write("if(a" + cnt + "b.getAll" + part.getName()
                + "s().contains(b" + cnt + "))");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            writer.write("z" + cnt + " = false;");
            writer.write(NEWLINE);
            writer.write("break;");
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
            //}
            writer.write("if(z" + cnt + " && (b" + cnt
                + ".getMenu().contains(\"JOIN "
                + ((UserActionTypeTrigger) outerTrig).getMenuText()
                + "\") == false))");
            writer.write(NEWLINE);
            writer.write(OPEN_BRACK);
            writer.write(NEWLINE);
            // go through all participant constraints:
            for (int k = 0; k < types.size(); k++) {
              SimSEObjectType tempType = types.elementAt(k);
              if (k > 0) { // not on first element
                writer.write("else ");
              }
              writer.write("if((b" + cnt + " instanceof "
                  + CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) +
                  ")");
              // go through all attribute constraints:
              ActionTypeParticipantAttributeConstraint[] attConstraints = 
              	outerTrig.getParticipantTrigger(part.getName()).getConstraint(
                      tempType.getName()).getAllAttributeConstraints();
              for (int m = 0; m < attConstraints.length; m++) {
                ActionTypeParticipantAttributeConstraint attConst = 
                	attConstraints[m];
                if (attConst.isConstrained()) {
                  writer.write(" && ((("
                      + CodeGeneratorUtils.getUpperCaseLeading(
                      		tempType.getName()) + ")b" + cnt + ").get" + 
                      		CodeGeneratorUtils.getUpperCaseLeading(
                      				attConst.getAttribute().getName()) + "() ");
                  if (attConst.getAttribute().getType() == 
                  	AttributeTypes.STRING) {
                    writer.write(".equals(" + "\""
                        + attConst.getValue().toString() + "\")");
                  } else {
                    if (attConst.getGuard().equals(AttributeGuard.EQUALS)) {
                      writer.write("== ");
                    } else {
                      writer.write(attConst.getGuard() + " ");
                    }
                    writer.write(attConst.getValue().toString());
                  }
                  writer.write(")");
                }
              }
              writer.write(")");
              writer.write(NEWLINE);
              writer.write(OPEN_BRACK);
              writer.write(NEWLINE);
              writer.write("b" + cnt + ".addMenuItem(\"JOIN "
                  + ((UserActionTypeTrigger) outerTrig).getMenuText() + "\");");
              writer.write(NEWLINE);
              writer.write(CLOSED_BRACK);
              writer.write(NEWLINE);
            }
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
            writer.write(CLOSED_BRACK);
            writer.write(NEWLINE);
          }
        }
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + trigFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }

  private boolean vectorContainsString(Vector<String> v, String s) {
    for (int i = 0; i < v.size(); i++) {
      String temp = v.elementAt(i);
      if (temp.equals(s)) {
        return true;
      }
    }
    return false;
  }

  /*
   * gets the triggers in prioritized order according to their priority
   */
  private void initializeTriggerLists() 
  {
    // initialize lists:
    nonPrioritizedTriggers = new Vector<ActionTypeTrigger>();
    prioritizedTriggers = new Vector<ActionTypeTrigger>();
    Vector<ActionType> allActions = actTypes.getAllActionTypes();
    // go through all action types and get their triggers:
    for (int i = 0; i < allActions.size(); i++) {
      ActionType tempAct = allActions.elementAt(i);
      Vector<ActionTypeTrigger> trigs = tempAct.getAllTriggers();
      for (int j = 0; j < trigs.size(); j++) {
        ActionTypeTrigger tempTrig = trigs.elementAt(j);
        int priority = tempTrig.getPriority();
        if (priority == -1) { // trigger is not prioritized
          nonPrioritizedTriggers.addElement(tempTrig);
        } else { // priority >= 0
          if (prioritizedTriggers.size() == 0) { // no elements have been added
                                               	 // yet to the prioritized 
          																			 // trigger list
            prioritizedTriggers.add(tempTrig);
          } else {
            // find the correct position to insert the trigger at:
            for (int k = 0; k < prioritizedTriggers.size(); k++) {
              ActionTypeTrigger tempA = prioritizedTriggers.elementAt(k);
              if (priority <= tempA.getPriority()) {
                prioritizedTriggers.insertElementAt(tempTrig, k); // insert the
                                                                  // trigger
                break;
              } else if (k == (prioritizedTriggers.size() - 1)) { // on the last
                                                                	// element
                prioritizedTriggers.add(tempTrig); // add the trigger to the end
                                                   // of the list
                break;
              }
            }
          }
        }
      }
    }
  }
}