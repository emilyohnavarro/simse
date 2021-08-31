/*
 * This class is responsible for generating all of the code for the
 * ExplanatoryTool component of the simulation
 */

package simse.codegenerator.explanatorytoolgenerator;

import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;
import simse.codegenerator.explanatorytoolgenerator.ActionGraphGenerator;
import simse.codegenerator.explanatorytoolgenerator.ActionInfoPanelGenerator;
import simse.codegenerator.explanatorytoolgenerator.ObjectGraphGenerator;
import simse.codegenerator.explanatorytoolgenerator.TriggerDescriptionsGenerator;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.NumericalAttribute;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.rulebuilder.Rule;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.SimSEObject;

import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ExplanatoryToolGenerator implements CodeGeneratorConstants {
  private ModelOptions options;
  private DefinedObjectTypes objTypes;
  private CreatedObjects objs;
  private DefinedActionTypes acts;
  private ObjectGraphGenerator objGraphGen; // generates the ObjectGraph class
	private ActionGraphGenerator actGraphGen; // generates the ActionGraph class
	private CompositeGraphGenerator compGraphGen; // generates the CompositeGraph
																								// class
	private ActionInfoPanelGenerator actInfoPanelGen; // generates the
																										// ActionInfoPanel class
	private RuleInfoPanelGenerator ruleInfoPanelGen; // generates the
																										// RuleInfoPanel class
	private ActionInfoWindowGenerator actInfoWindowGen; // generates the
																											// ActionInfoWindow class
	private TriggerDescriptionsGenerator trigDescGen; // generates the
																										// TriggerDescriptions class
	private DestroyerDescriptionsGenerator destDescGen; // generates the
																											// DestroyerDescriptions
																											// class
	private RuleDescriptionsGenerator ruleDescGen; // generates the
																									// RuleDescriptions class
	private BranchGenerator branchGen; // generates the Branch class
	private MultipleTimelinesBrowserGenerator browserGen; // generates the
																												 // MulitpleTimelinesBrowser
																												 // class

  public ExplanatoryToolGenerator(ModelOptions options, 
      DefinedObjectTypes objTypes, CreatedObjects objs, 
      DefinedActionTypes acts) {
    this.options = options;
    this.objTypes = objTypes;
    this.objs = objs;
    this.acts = acts;
    objGraphGen = new ObjectGraphGenerator(objTypes, objs, 
        options.getCodeGenerationDestinationDirectory(), options);
    actGraphGen = new ActionGraphGenerator(acts, 
        options.getCodeGenerationDestinationDirectory(), options);
    compGraphGen = new CompositeGraphGenerator(
        options.getCodeGenerationDestinationDirectory(), options);
    actInfoPanelGen = new ActionInfoPanelGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    ruleInfoPanelGen = new RuleInfoPanelGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    actInfoWindowGen = new ActionInfoWindowGenerator(
        options.getCodeGenerationDestinationDirectory());
    trigDescGen = new TriggerDescriptionsGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    destDescGen = new DestroyerDescriptionsGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    ruleDescGen = new RuleDescriptionsGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    branchGen = new BranchGenerator(
    		options.getCodeGenerationDestinationDirectory());
    browserGen = new MultipleTimelinesBrowserGenerator(
    		options.getCodeGenerationDestinationDirectory());
  }

/*
 * causes all of this component's sub-components to generate code
 */
  public void generate() { 
    // copy the JFreeChart jars:
    copyJFreeChartJars();

    objGraphGen.generate();
    actGraphGen.generate();
    compGraphGen.generate();
    actInfoPanelGen.generate();
    ruleInfoPanelGen.generate();
    actInfoWindowGen.generate();
    trigDescGen.generate();
    destDescGen.generate();
    ruleDescGen.generate();
    branchGen.generate();
    browserGen.generate();
    generateExplanatoryTool();
  }

  // generates the ExplanatoryTool class
  private void generateExplanatoryTool() { 
    File expToolFile = new File(options.
        getCodeGenerationDestinationDirectory(),
        ("simse\\explanatorytool\\ExplanatoryTool.java"));
    if (expToolFile.exists()) {
      expToolFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(expToolFile);
      writer
          .write("/* File generated by: simse.codegenerator.explanatorytoolgenerator.ExplanatoryToolGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.explanatorytool;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import simse.state.State;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import org.jfree.ui.RefineryUtilities;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import java.awt.event.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Color;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Dimension;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Frame;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import java.util.ArrayList;");
      writer.write(NEWLINE);
      writer.write("import java.util.Vector;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import javax.swing.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.event.ListSelectionEvent;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.event.ListSelectionListener;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer
          .write("public class ExplanatoryTool extends JFrame implements ActionListener, ListSelectionListener {");
      writer.write(NEWLINE);

      // member variables:
      writer.write("private ArrayList<State> log; // log for current simulation");
      writer.write(NEWLINE);
    	writer.write("private ArrayList<JFrame> visibleGraphs; // holds all of the currently visible graphs");
    	writer.write(NEWLINE);
    	writer.write("private static MultipleTimelinesBrowser timelinesBrowser;");
      writer.write(NEWLINE);
      writer.write("private JButton multipleTimelinesButton;");
      writer.write(NEWLINE);
      writer
          .write("private JComboBox objectList; // for choosing an object whose attributes to graph");
      writer.write(NEWLINE);
      writer
          .write("private JList attributeList; // for choosing which attributes to show");
      writer.write(NEWLINE);
      writer
          .write("private JList actionList; // for choosing which actions to show");
      writer.write(NEWLINE);
      writer
          .write("private JButton generateObjGraphButton; // for generating an object graph");
      writer.write(NEWLINE);
      writer
          .write("private JButton generateActGraphButton; // for generating an action graph");
      writer.write(NEWLINE);
      writer
          .write("private JButton generateCompGraphButton; // for generating a composite graph");
      writer.write(NEWLINE);
      writer.write("private JComboBox actionComboBox;");
      writer.write(NEWLINE);
      writer.write("private JList triggerRuleList;");
      writer.write(NEWLINE);
      writer.write("private JList destroyerRuleList;");
      writer.write(NEWLINE);
      writer.write("private JList intermediateRuleList;");
      writer.write(NEWLINE);
      writer.write("private JTextArea descriptionArea;");
      writer.write(NEWLINE);
      writer.write("private JButton closeButton;");
      writer.write(NEWLINE);
      writer.write("private Box mainPane;");
      writer.write(NEWLINE);
      writer.write("private Branch branch;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // constructor:
      writer.write("public ExplanatoryTool(JFrame owner, ArrayList<State> log, Branch branch, MultipleTimelinesBrowser browser) {");
      writer.write(NEWLINE);
  		writer.write("super();");
  		writer.write(NEWLINE);
  		writer.write("this.branch = branch;");
  		writer.write(NEWLINE);
  		writer.write("timelinesBrowser = browser;");
  		writer.write(NEWLINE);
  		writer.write("String title = \"Explanatory Tool\";");
  		writer.write(NEWLINE);
  		writer.write("if (branch.getName() != null) {");
  		writer.write(NEWLINE);
  		writer.write("title = title.concat(\" - \" + branch.getName());");
  		writer.write(NEWLINE);
  		writer.write(CLOSED_BRACK);
  		writer.write(NEWLINE);
  		writer.write("setTitle(title);");
      writer.write(NEWLINE);
      writer.write("this.log = log;");
      writer.write(NEWLINE);
      writer.write("this.visibleGraphs = new ArrayList<JFrame>();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create main panel (box):");
      writer.write(NEWLINE);
      writer.write("mainPane = Box.createVerticalBox();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
  		writer.write("JPanel multipleTimelinesPanel = new JPanel();");
  		writer.write(NEWLINE);
  		writer.write("multipleTimelinesButton = new JButton(\"Multiple Timelines Browser\");");
  		writer.write(NEWLINE);
  		writer.write("multipleTimelinesButton.addActionListener(this);");
  		writer.write(NEWLINE);
  		writer.write("multipleTimelinesPanel.add(multipleTimelinesButton);");
  		writer.write(NEWLINE);
  		writer.write(NEWLINE);
      writer.write("// Create main sub-panel:");
      writer.write(NEWLINE);
      writer.write("JPanel generateGraphsPanel = new JPanel();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create generate graphs title pane and label:");
      writer.write(NEWLINE);
      writer.write("JPanel generateGraphsTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("generateGraphsTitlePane.add(new JLabel(\"Generate Graph(s):\"));");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create object pane and components:");
      writer.write(NEWLINE);
      writer.write("Box objectPane = Box.createVerticalBox();");
      writer.write(NEWLINE);
      writer.write("JPanel objectTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("objectTitlePane.add(new JLabel(\"Object Graph:\"));");
      writer.write(NEWLINE);
      writer.write("objectPane.add(objectTitlePane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// object list:");
      writer.write(NEWLINE);
      writer.write("String[] objects = {");
      Vector<SimSEObject> objects = objs.getAllObjects();
      for (int i = 0; i < objects.size(); i++) {
        SimSEObject obj = objects.get(i);
        writer.write("\"" + CodeGeneratorUtils.getUpperCaseLeading(
        		obj.getSimSEObjectType().getName()) + " " + 
        		CodeGeneratorUtils.getUpperCaseLeading(
        				SimSEObjectTypeTypes.getText(
        						obj.getSimSEObjectType().getType())) + " " + 
        						obj.getKey().getValue().toString() + "\",");
        writer.write(NEWLINE);
      }
      writer.write("};");
      writer.write(NEWLINE);
      writer.write("objectList = new JComboBox(objects);");
      writer.write(NEWLINE);
      writer.write("objectList.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("objectPane.add(objectList);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create attribute list pane:");
      writer.write(NEWLINE);
      writer.write("JPanel attributeListTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer
          .write("attributeListTitlePane.add(new JLabel(\"Show Attributes:\"));");
      writer.write(NEWLINE);
      writer.write("objectPane.add(attributeListTitlePane);");
      writer.write(NEWLINE);
      writer.write("attributeList = new JList();");
      writer.write(NEWLINE);
      writer
          .write("attributeList.setVisibleRowCount(5); // make 5 items visible at a time");
      writer.write(NEWLINE);
      writer.write("attributeList.setFixedCellWidth(250);");
      writer.write(NEWLINE);
      writer
          .write("attributeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);");
      writer.write(NEWLINE);
      writer.write("attributeList.addListSelectionListener(this);");
      writer.write(NEWLINE);
      writer
          .write("JScrollPane attributeListPane = new JScrollPane(attributeList);");
      writer.write(NEWLINE);
      writer.write("objectPane.add(attributeListPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create objectBottom pane & button:");
      writer.write(NEWLINE);
      writer.write("JPanel objBottomPane = new JPanel();");
      writer.write(NEWLINE);
      writer
          .write("generateObjGraphButton = new JButton(\"Generate Object Graph\");");
      writer.write(NEWLINE);
      writer.write("generateObjGraphButton.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("objBottomPane.add(generateObjGraphButton);");
      writer.write(NEWLINE);
      writer.write("objectPane.add(objBottomPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create action pane and components:");
      writer.write(NEWLINE);
      writer.write("Box actionPane = Box.createVerticalBox();");
      writer.write(NEWLINE);
      writer.write("JPanel actionTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("actionTitlePane.add(new JLabel(\"Action Graph:\"));");
      writer.write(NEWLINE);
      writer.write("actionPane.add(actionTitlePane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// action list:");
      writer.write(NEWLINE);
      writer.write("String[] actions = {");
      writer.write(NEWLINE);
      Vector<ActionType> actions = acts.getAllActionTypes();
      for (int i = 0; i < actions.size(); i++) {
        ActionType act = actions.get(i);
        if (act.isVisibleInExplanatoryTool()) {
          writer.write("\"" + 
          		CodeGeneratorUtils.getUpperCaseLeading(act.getName()) + "\",");
          writer.write(NEWLINE);
        }
      }
      writer.write("};");
      writer.write(NEWLINE);
      writer.write("actionList = new JList(actions);");
      writer.write(NEWLINE);
      writer
          .write("actionList.setVisibleRowCount(5); // make 5 items visible at a time");
      writer.write(NEWLINE);
      writer.write("actionList.setFixedCellWidth(250);");
      writer.write(NEWLINE);
      writer
          .write("actionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);");
      writer.write(NEWLINE);
      writer.write("actionList.addListSelectionListener(this);");
      writer.write(NEWLINE);
      writer.write("JScrollPane actionListPane = new JScrollPane(actionList);");
      writer.write(NEWLINE);
      writer.write("actionPane.add(actionListPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create actionBottom pane & buttons:");
      writer.write(NEWLINE);
      writer.write("JPanel actBottomPane = new JPanel();");
      writer.write(NEWLINE);
      writer
          .write("generateActGraphButton = new JButton(\"Generate Action Graph\");");
      writer.write(NEWLINE);
      writer.write("generateActGraphButton.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("actBottomPane.add(generateActGraphButton);");
      writer.write(NEWLINE);
      writer.write("actionPane.add(actBottomPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create comp graph pane & button:");
      writer.write(NEWLINE);
      writer.write("JPanel generateCompGraphPane = new JPanel();");
      writer.write(NEWLINE);
      writer
          .write("generateCompGraphButton = new JButton(\"Generate Composite Graph\");");
      writer.write(NEWLINE);
      writer.write("generateCompGraphButton.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("generateCompGraphPane.add(generateCompGraphButton);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("refreshAttributeList();");
      writer.write(NEWLINE);
      writer.write("if (actions.length > 0) {");
      writer.write(NEWLINE);
      writer.write("actionList.setSelectedIndex(0);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("refreshButtons();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create viewRuleTitlePane and label:");
      writer.write(NEWLINE);
      writer.write("JPanel viewRulesTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("viewRulesTitlePane.add(new JLabel(\"View Rules:\"));");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create actionsComboBoxPane:");
      writer.write(NEWLINE);
      writer.write("JPanel actionComboBoxPane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("actionComboBoxPane.add(new JLabel(\"Actions:\"));");
      writer.write(NEWLINE);
      writer.write("actionComboBox = new JComboBox(actions);");
      writer.write(NEWLINE);
      writer.write("actionComboBox.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("actionComboBoxPane.add(actionComboBox);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create rulesMainPane:");
      writer.write(NEWLINE);
      writer.write("JPanel rulesMainPane = new JPanel();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create ruleListsPane:");
      writer.write(NEWLINE);
      writer.write("Box ruleListsPane = Box.createVerticalBox();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// rule lists:");
      writer.write(NEWLINE);
      writer.write("JPanel trigRuleTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("trigRuleTitlePane.add(new JLabel(\"Trigger Rules:\"));");
      writer.write(NEWLINE);
      writer.write("ruleListsPane.add(trigRuleTitlePane);");
      writer.write(NEWLINE);
      writer.write("triggerRuleList = new JList();");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setVisibleRowCount(4);");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setFixedCellWidth(250);");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.addListSelectionListener(this);");
      writer.write(NEWLINE);
      writer.write("JScrollPane triggerRuleListPane = new JScrollPane(triggerRuleList);");
      writer.write(NEWLINE);
      writer.write("ruleListsPane.add(triggerRuleListPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("JPanel destRuleTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("destRuleTitlePane.add(new JLabel(\"Destroyer Rules:\"));");
      writer.write(NEWLINE);
      writer.write("ruleListsPane.add(destRuleTitlePane);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList = new JList();");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setVisibleRowCount(4);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setFixedCellWidth(250);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.addListSelectionListener(this);");
      writer.write(NEWLINE);
      writer.write("JScrollPane destroyerRuleListPane = new JScrollPane(destroyerRuleList);");
      writer.write(NEWLINE);
      writer.write("ruleListsPane.add(destroyerRuleListPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("JPanel intRuleTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("intRuleTitlePane.add(new JLabel(\"Intermediate Rules:\"));");
      writer.write(NEWLINE);
      writer.write("ruleListsPane.add(intRuleTitlePane);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList = new JList();");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setVisibleRowCount(4);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setFixedCellWidth(250);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.addListSelectionListener(this);");
      writer.write(NEWLINE);
      writer.write("JScrollPane intermediateRuleListPane = new JScrollPane(intermediateRuleList);");
      writer.write(NEWLINE);
      writer.write("ruleListsPane.add(intermediateRuleListPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("rulesMainPane.add(ruleListsPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// description pane:");
      writer.write(NEWLINE);
      writer.write("Box descriptionPane = Box.createVerticalBox();");
      writer.write(NEWLINE);
      writer.write("JPanel descriptionTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("descriptionTitlePane.add(new JLabel(\"Description:\"));");
      writer.write(NEWLINE);
      writer.write("descriptionPane.add(descriptionTitlePane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// description text area:");
      writer.write(NEWLINE);
      writer.write("descriptionArea = new JTextArea(16, 30);");
      writer.write(NEWLINE);
      writer.write("descriptionArea.setLineWrap(true);");
      writer.write(NEWLINE);
      writer.write("descriptionArea.setWrapStyleWord(true);");
      writer.write(NEWLINE);
      writer.write("descriptionArea.setEditable(false);");
      writer.write(NEWLINE);
      writer.write("JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);");
      writer.write(NEWLINE);
      writer.write("descriptionPane.add(descriptionScrollPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("rulesMainPane.add(descriptionPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create close button pane:");
      writer.write(NEWLINE);
      writer.write("JPanel closeButtonPane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("closeButton = new JButton(\"Close\");");
      writer.write(NEWLINE);
      writer.write("closeButton.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("closeButtonPane.add(closeButton);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("if (actions.length > 0) { // at least one action in list");
      writer.write(NEWLINE);
      writer.write("actionComboBox.setSelectedIndex(0);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// set up tool tips:");
      writer.write(NEWLINE);
      writer.write("setUpToolTips();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Add panes to main pane and main sub-pane:");
      writer.write(NEWLINE);
  		writer.write("mainPane.add(multipleTimelinesPanel);");
  		writer.write(NEWLINE);
  		writer.write("JSeparator separator0 = new JSeparator();");
  		writer.write(NEWLINE);
  		writer.write("separator0.setMaximumSize(new Dimension(2900, 1));");
  		writer.write(NEWLINE);
  		writer.write("mainPane.add(separator0);");
  		writer.write(NEWLINE);
      writer.write("generateGraphsPanel.add(objectPane);");
      writer.write(NEWLINE);
      writer.write("generateGraphsPanel.add(actionPane);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(generateGraphsTitlePane);");
      writer.write(NEWLINE);
      writer.write("JSeparator separator1 = new JSeparator();");
      writer.write(NEWLINE);
      writer.write("separator1.setMaximumSize(new Dimension(2900, 1));");
      writer.write(NEWLINE);
      writer.write("mainPane.add(separator1);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(generateGraphsPanel);");
      writer.write(NEWLINE);
      writer.write("JSeparator separator2 = new JSeparator();");
      writer.write(NEWLINE);
      writer.write("separator2.setMaximumSize(new Dimension(2900, 1));");
      writer.write(NEWLINE);
      writer.write("mainPane.add(separator2);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(generateCompGraphPane);");
      writer.write(NEWLINE);
      writer.write("JSeparator separator3 = new JSeparator();");
      writer.write(NEWLINE);
      writer.write("separator3.setMaximumSize(new Dimension(2900, 1));");
      writer.write(NEWLINE);
      writer.write("mainPane.add(separator3);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(viewRulesTitlePane);");
      writer.write(NEWLINE);
      writer.write("JSeparator separator4 = new JSeparator();");
      writer.write(NEWLINE);
      writer.write("separator4.setMaximumSize(new Dimension(2900, 1));");
      writer.write(NEWLINE);
      writer.write("mainPane.add(separator4);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(actionComboBoxPane);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(rulesMainPane);");
      writer.write(NEWLINE);
      writer.write("JSeparator separator5 = new JSeparator();");
      writer.write(NEWLINE);
      writer.write("separator5.setMaximumSize(new Dimension(2900, 1));");
      writer.write(NEWLINE);
      writer.write("mainPane.add(separator5);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(closeButtonPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Set main window frame properties:");
      writer.write(NEWLINE);
      writer.write("setBackground(Color.black);");
      writer.write(NEWLINE);
      writer.write("setContentPane(mainPane);");
      writer.write(NEWLINE);
      writer.write("validate();");
      writer.write(NEWLINE);
      writer.write("pack();");
      writer.write(NEWLINE);
      writer.write("repaint();");
      writer.write(NEWLINE);
      writer.write("toFront();");
      writer.write(NEWLINE);
      writer.write("// Make it show up in the center of the screen:");
      writer.write(NEWLINE);
  		writer.write("RefineryUtilities.centerFrameOnScreen(this);");
  		writer.write(NEWLINE);
  		writer.write("setVisible(false);");
  		writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // "actionPerformed" method:
      writer.write("public void actionPerformed(ActionEvent evt) {");
      writer.write(NEWLINE);
      writer
          .write("Object source = evt.getSource(); // get which component the action came from");
      writer.write(NEWLINE);
      writer.write("if (source == objectList) { // user has chosen an object");
      writer.write(NEWLINE);
      writer.write("refreshAttributeList();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (source == multipleTimelinesButton) {");
      writer.write(NEWLINE);
  		writer.write("if (timelinesBrowser.getState() == Frame.ICONIFIED) {");
  		writer.write(NEWLINE);
  		writer.write("timelinesBrowser.setState(Frame.NORMAL);");
  		writer.write(NEWLINE);
  		writer.write(CLOSED_BRACK);
  		writer.write(NEWLINE);
  		writer.write("timelinesBrowser.setVisible(true);");
  		writer.write(NEWLINE);
  		writer.write(CLOSED_BRACK);
  		writer.write(NEWLINE);
      writer
          .write("else if (source == generateObjGraphButton) { // generateObjGraphButton has been pressed");
      writer.write(NEWLINE);
      writer
          .write("String selectedObj = (String)objectList.getSelectedItem();");
      writer.write(NEWLINE);
      writer.write("String[] words = selectedObj.split(\"\\\\s\");");
      writer.write(NEWLINE);
      writer
          .write("String title = selectedObj + \" Attributes\";");
      writer.write(NEWLINE);
      writer.write("String objType = words[0];");
      writer.write(NEWLINE);
      writer.write("String objTypeType = words[1];");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// add 2 for the 2 spaces:");
      writer.write(NEWLINE);
      writer
          .write("String keyAttVal = selectedObj.substring(objType.length() + objTypeType.length() + 2);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer
          .write("Object[] selectedAtts = attributeList.getSelectedValues();");
      writer.write(NEWLINE);
      writer.write("String[] attributes = new String[selectedAtts.length];");
      writer.write(NEWLINE);
      writer.write("for (int i = 0; i < selectedAtts.length; i++) {");
      writer.write(NEWLINE);
      writer.write("attributes[i] = new String((String)selectedAtts[i]);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer
          .write("if (attributes.length > 0) { // at least one attribute is selected");
      writer.write(NEWLINE);
      writer
          .write("ObjectGraph graph = new ObjectGraph(title, log, objTypeType, objType, keyAttVal, attributes, true, branch);");
      writer.write(NEWLINE);
      writer.write("visibleGraphs.add(graph);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else {");
      writer.write(NEWLINE);
      writer
          .write("JOptionPane.showMessageDialog(null, (\"Please select at least one attribute\"), \"Warning\", JOptionPane.WARNING_MESSAGE);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer
          .write("else if (source == generateActGraphButton) { // generateActGraphButton has been pressed");
      writer.write(NEWLINE);
      writer
          .write("Object[] selectedActions = actionList.getSelectedValues();");
      writer.write(NEWLINE);
      writer.write("String[] actions = new String[selectedActions.length];");
      writer.write(NEWLINE);
      writer.write("for (int i = 0; i < selectedActions.length; i++) {");
      writer.write(NEWLINE);
      writer.write("actions[i] = new String((String) selectedActions[i]);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer
          .write("if (actions.length > 0) { // at least one attribute is selected");
      writer.write(NEWLINE);
      writer.write("ActionGraph graph = new ActionGraph(log, actions, true, branch);");
      writer.write(NEWLINE);
      writer.write("visibleGraphs.add(graph);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else {");
      writer.write(NEWLINE);
      writer
          .write("JOptionPane.showMessageDialog(null, (\"Please select at least one action\"), \"Warning\", JOptionPane.WARNING_MESSAGE);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer
          .write("else if (source == generateCompGraphButton) { // generateCompGraphButton has been pressed");
      writer.write(NEWLINE);
      writer
          .write("String selectedObj = (String) objectList.getSelectedItem();");
      writer.write(NEWLINE);
      writer.write("String[] words = selectedObj.split(\"\\\\s\");");
      writer.write(NEWLINE);
      writer
          .write("String title = selectedObj + \" Attributes\";");
      writer.write(NEWLINE);
      writer.write("String objType = words[0];");
      writer.write(NEWLINE);
      writer.write("String objTypeType = words[1];");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// add 2 for the 2 spaces:");
      writer.write(NEWLINE);
      writer
          .write("String keyAttVal = selectedObj.substring(objType.length() + objTypeType.length() + 2);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer
          .write("Object[] selectedAtts = attributeList.getSelectedValues();");
      writer.write(NEWLINE);
      writer.write("String[] attributes = new String[selectedAtts.length];");
      writer.write(NEWLINE);
      writer.write("for (int i = 0; i < selectedAtts.length; i++) {");
      writer.write(NEWLINE);
      writer.write("attributes[i] = new String((String) selectedAtts[i]);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer
          .write("if (attributes.length > 0) { // at least one attribute is selected");
      writer.write(NEWLINE);
      writer
          .write("ObjectGraph objGraph = new ObjectGraph(title, log, objTypeType, objType, keyAttVal, attributes, false, branch);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer
          .write("Object[] selectedActions = actionList.getSelectedValues();");
      writer.write(NEWLINE);
      writer.write("String[] actions = new String[selectedActions.length];");
      writer.write(NEWLINE);
      writer.write("for (int i = 0; i < selectedActions.length; i++) {");
      writer.write(NEWLINE);
      writer.write("actions[i] = new String((String) selectedActions[i]);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer
          .write("if (actions.length > 0) { // at least one attribute is selected");
      writer.write(NEWLINE);
      writer
          .write("ActionGraph actGraph = new ActionGraph(log, actions, false, branch);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// generate composite graph:");
      writer.write(NEWLINE);
      writer
          .write("CompositeGraph compGraph = new CompositeGraph(objGraph, actGraph, branch);");
      writer.write(NEWLINE);
      writer.write("visibleGraphs.add(compGraph);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else {");
      writer.write(NEWLINE);
      writer
          .write("JOptionPane.showMessageDialog(null, (\"Please select at least one action\"), \"Warning\", JOptionPane.WARNING_MESSAGE);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else {");
      writer.write(NEWLINE);
      writer
          .write("JOptionPane.showMessageDialog(null, (\"Please select at least one attribute\"), \"Warning\", JOptionPane.WARNING_MESSAGE);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (source == actionComboBox) {");
      writer.write(NEWLINE);
      writer.write("if (actionComboBox.getItemCount() > 0) {");
      writer.write(NEWLINE);
      writer.write("refreshRuleLists((String)actionComboBox.getSelectedItem());");
      writer.write(NEWLINE);
      writer.write("descriptionArea.setText(\"\");");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (source == closeButton) {");
      writer.write(NEWLINE);
      writer.write("setVisible(false);");
      writer.write(NEWLINE);
      writer.write("dispose();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      
      // "valueChanged" method:
      writer.write("public void valueChanged(ListSelectionEvent e) {");
      writer.write(NEWLINE);
      writer.write("if ((e.getSource() == attributeList) || (e.getSource() == actionList)) {");
      writer.write(NEWLINE);
      writer.write("refreshButtons();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if ((e.getSource() == triggerRuleList && !triggerRuleList.isSelectionEmpty())) {");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("refreshDescriptionArea((String)triggerRuleList.getSelectedValue());");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (e.getSource() == destroyerRuleList && !destroyerRuleList.isSelectionEmpty()) {");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("refreshDescriptionArea((String)destroyerRuleList.getSelectedValue());");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (e.getSource() == intermediateRuleList && !intermediateRuleList.isSelectionEmpty()) {");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("refreshDescriptionArea((String)intermediateRuleList.getSelectedValue());");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      
      // "update" method:
    	writer.write("// Updates all of the visible graphs");
    	writer.write(NEWLINE);
    	writer.write("public void update() {");
    	writer.write(NEWLINE);
    	writer.write("for (int i = 0; i < visibleGraphs.size(); i++) {");
    	writer.write(NEWLINE);
    	writer.write("JFrame graph = visibleGraphs.get(i);");
    	writer.write(NEWLINE);
    	writer.write("// remove graphs whose windows have been closed from visibleGraphs:");
    	writer.write(NEWLINE);
    	writer.write("if (!graph.isShowing()) {");
    	writer.write(NEWLINE);
    	writer.write("visibleGraphs.remove(graph);");
    	writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
    	writer.write(NEWLINE);
    	writer.write("else if (graph instanceof ObjectGraph) {");
    	writer.write(NEWLINE);
    	writer.write("((ObjectGraph)graph).update();");
    	writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
    	writer.write(NEWLINE);
    	writer.write("else if (graph instanceof ActionGraph) {");
    	writer.write(NEWLINE);
    	writer.write("((ActionGraph)graph).update();");
    	writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
    	writer.write(NEWLINE);
    	writer.write("else if (graph instanceof CompositeGraph) {");
    	writer.write(NEWLINE);
    	writer.write("((CompositeGraph)graph).update();");
    	writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
    	writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
    	writer.write(NEWLINE);
    	writer.write(NEWLINE);
  		writer.write("// update timelines browser:");
  		writer.write(NEWLINE);
  		writer.write("if (timelinesBrowser != null) {");
  		writer.write(NEWLINE);
  		writer.write("timelinesBrowser.update();");
  		writer.write(NEWLINE);
  		writer.write(CLOSED_BRACK);
  		writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
    	writer.write(NEWLINE);
    	writer.write(NEWLINE);

      // "refreshAttributeList" method:
      writer.write("private void refreshAttributeList() {");
      writer.write(NEWLINE);
      writer.write("attributeList.removeAll();");
      writer.write(NEWLINE);
      writer
          .write("String selectedObject = (String)objectList.getSelectedItem();");
      writer.write(NEWLINE);
      Vector<SimSEObjectType> objectTypes = objTypes.getAllObjectTypes();
      for (int i = 0; i < objectTypes.size(); i++) {
        SimSEObjectType objType = objectTypes.get(i);
        if (i > 0) {
          writer.write("else ");
        }
        writer.write("if (selectedObject.startsWith(\""
            + CodeGeneratorUtils.getUpperCaseLeading(objType.getName()) + " "
            + SimSEObjectTypeTypes.getText(objType.getType())
            + "\")) {");
        writer.write(NEWLINE);
        writer.write("String[] attributes = {");
        writer.write(NEWLINE);
        Vector<Attribute> attributes = objType.getAllAttributes();
        int numVisibleNumericalAtts = 0;
        for (int j = 0; j < attributes.size(); j++) {
          Attribute att = attributes.get(j);
          if ((att instanceof NumericalAttribute)
              && ((att.isVisible()) || (att.isVisibleOnCompletion()))) {
            writer.write("\"" + att.getName() + "\",");
            writer.write(NEWLINE);
            numVisibleNumericalAtts++;
          }
        }
        if (numVisibleNumericalAtts == 0) {
          writer.write("\"(No numerical attributes)\"");
          writer.write(NEWLINE);
        }
        writer.write("};");
        writer.write(NEWLINE);
        writer.write("attributeList.setListData(attributes);");
        writer.write(NEWLINE);
        if (numVisibleNumericalAtts == 0) {
          writer.write("attributeList.setEnabled(false);");
          writer.write(NEWLINE);
        } else {
          writer.write("attributeList.setEnabled(true);");
          writer.write(NEWLINE);
          writer.write("attributeList.setSelectedIndex(0);");
          writer.write(NEWLINE);
        }
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
      }
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // "setUpToolTips" method:
      writer.write("private void setUpToolTips() {");
      writer.write(NEWLINE);
      writer.write("objectList.setToolTipText(\"Choose an object to graph\");");
      writer.write(NEWLINE);
      writer
          .write("attributeList.setToolTipText(\"Choose which attributes to graph\");");
      writer.write(NEWLINE);
      writer
          .write("actionList.setToolTipText(\"Choose which actions to graph\");");
      writer.write(NEWLINE);
      writer.write("actionComboBox.setToolTipText(\"Choose which action to show rules for\");");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setToolTipText(\"Rules that execute at the beginning of the action\");");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setToolTipText(\"Rules that execute at the end of the action\");");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setToolTipText(\"Rules that execute every clock tick during the life of the action\");");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      
      // "refreshButtons" method:
      writer.write("private void refreshButtons() {");
      writer.write(NEWLINE);
      writer.write("if (attributeList.isSelectionEmpty()) { // no attributes selected");
      writer.write(NEWLINE);
      writer.write("generateObjGraphButton.setEnabled(false);");
      writer.write(NEWLINE);
      writer.write("generateCompGraphButton.setEnabled(false);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else { // an attribute is selected");
      writer.write(NEWLINE);
      writer.write("generateObjGraphButton.setEnabled(true);");
      writer.write(NEWLINE);
      writer.write("if (!actionList.isSelectionEmpty()) { // an action is also selected");
      writer.write(NEWLINE);
      writer.write("generateCompGraphButton.setEnabled(true);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("if (actionList.isSelectionEmpty()) { // no actions selected");
      writer.write(NEWLINE);
      writer.write("generateActGraphButton.setEnabled(false);");
      writer.write(NEWLINE);
      writer.write("generateCompGraphButton.setEnabled(false);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else { // an action is selected");
      writer.write(NEWLINE);
      writer.write("generateActGraphButton.setEnabled(true);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      
      // "refreshRuleLists" method
      writer.write("private void refreshRuleLists(String actionName) {");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setListData(new Vector());");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setListData(new Vector());");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setListData(new Vector());");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      boolean writeElse = false;
      for (ActionType action : actions) {
        if (action.isVisibleInExplanatoryTool()) {
          if (writeElse) {
            writer.write("else ");
          } else {
            writeElse = true;
          }
          writer.write("if (actionName.equals(\"" + 
          		CodeGeneratorUtils.getUpperCaseLeading(action.getName()) + 
          		"\")) {");
          writer.write(NEWLINE);
          Vector<Rule> trigRules = action.getAllTriggerRules();
          if (trigRules.size() > 0) {
            writer.write("String[] trigList = {");
            writer.write(NEWLINE);

            // go through all trigger rules:
            for (int j = 0; j < trigRules.size(); j++) {
              Rule trigRule = trigRules.get(j);
              if (trigRule.isVisibleInExplanatoryTool()) {
                writer.write("\"" + trigRule.getName() + "\",");
                writer.write(NEWLINE);
              }
            }
            writer.write("};");
            writer.write(NEWLINE);
            writer.write("triggerRuleList.setListData(trigList);");
            writer.write(NEWLINE);
          }
          Vector<Rule> destRules = action.getAllDestroyerRules();
          if (destRules.size() > 0) {
            writer.write("String[] destList = {");
            writer.write(NEWLINE);

            // go through all destroyer rules:
            for (int j = 0; j < destRules.size(); j++) {
              Rule destRule = destRules.get(j);
              if (destRule.isVisibleInExplanatoryTool()) {
                writer.write("\"" + destRule.getName() + "\",");
                writer.write(NEWLINE);
              }
            }
            writer.write("};");
            writer.write(NEWLINE);
            writer.write("destroyerRuleList.setListData(destList);");
            writer.write(NEWLINE);
          }
          Vector<Rule> contRules = action.getAllContinuousRules();
          if (contRules.size() > 0) {
            writer.write("String[] intList = {");
            writer.write(NEWLINE);

            // go through all continuous rules:
            for (int j = 0; j < contRules.size(); j++) {
              Rule contRule = contRules.get(j);
              if (contRule.isVisibleInExplanatoryTool()) {
                writer.write("\"" + contRule.getName() + "\",");
                writer.write(NEWLINE);
              }
            }
            writer.write("};");
            writer.write(NEWLINE);
            writer.write("intermediateRuleList.setListData(intList);");
            writer.write(NEWLINE);
          }
          writer.write(CLOSED_BRACK);
          writer.write(NEWLINE);
        }
      }
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      
      // "refreshDescriptionArea" method:
      writer.write("// refreshes the description area with the selected rule description");
      writer.write(NEWLINE);
      writer.write("private void refreshDescriptionArea(String ruleName) {");
      writer.write(NEWLINE);
      writer.write("if (ruleName != null) {");
      writer.write(NEWLINE);
      writer.write("String text = \"\";");
      writer.write(NEWLINE);

      // go through all actions:
      writeElse = false;
      for (ActionType action : actions) {
        if (action.isVisibleInExplanatoryTool()) {
          // go through all rules:
          Vector<Rule> rules = action.getAllRules();
          for (Rule rule : rules) {
            if (rule.isVisibleInExplanatoryTool()) {
              if (writeElse) {
                writer.write("else ");
              } else {
                writeElse = true;
              }
              writer.write("if (ruleName.equals(\"" + rule.getName() + 
              		"\")) {");
              writer.write(NEWLINE);
              writer.write("text = RuleDescriptions."
                  + action.getName().toUpperCase() + "_"
                  + rule.getName().toUpperCase() + ";");
              writer.write(NEWLINE);
              writer.write(CLOSED_BRACK);
            }
          }
        }
      }
      writer.write("descriptionArea.setText(text);");
      writer.write(NEWLINE);
      writer.write("descriptionArea.setCaretPosition(0);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);

      writer.write(CLOSED_BRACK);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + expToolFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }

  // copies the JFreeChart jars into the generated code directory
  private void copyJFreeChartJars() {
    try {
      ZipInputStream zis = new ZipInputStream(ExplanatoryToolGenerator.class
          .getResourceAsStream("res/jfreechart.zip"));
      while (true) {
        ZipEntry ze = zis.getNextEntry();
        if (ze == null) {
          break;
        }
        new File(options.getCodeGenerationDestinationDirectory() + "\\lib\\" +
            ze.getName()).createNewFile();
        byte[] buffer = new byte[1024];
        int len = 1024;
        BufferedOutputStream out = new BufferedOutputStream(
            new FileOutputStream(
            		options.getCodeGenerationDestinationDirectory() + "\\lib\\" + 
                ze.getName()));
        while ((len = zis.read(buffer, 0, len)) >= 0) {
          out.write(buffer, 0, len);
        }
        out.close();
        zis.closeEntry();
      }
      zis.close();
    } catch (IOException ioe) {
      System.out.println("IOE");
      ioe.printStackTrace();
    }
  }
}