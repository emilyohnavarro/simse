/* This class is responsible for generating all of the code for the simulation */

package simse.codegenerator;

import simse.codegenerator.enginegenerator.EngineGenerator;
import simse.codegenerator.explanatorytoolgenerator.ExplanatoryToolGenerator;
import simse.codegenerator.guigenerator.GUIGenerator;
import simse.codegenerator.logicgenerator.LogicGenerator;
import simse.codegenerator.stategenerator.StateGenerator;
import simse.codegenerator.utilgenerator.IDGeneratorGenerator;
import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.SimSEObject;
import simse.modelbuilder.mapeditor.TileData;
import simse.modelbuilder.mapeditor.UserData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JOptionPane;

public class CodeGenerator {
	public static boolean allowHireFire = false;
	
  private final char NEWLINE = '\n';
  private final char OPEN_BRACK = '{';
  private final char CLOSED_BRACK = '}';

  private ModelOptions options;
  
  private StateGenerator stateGen; // generates the state component
  private LogicGenerator logicGen; // generates the logic component
  private EngineGenerator engineGen; // generates the engine component
  private GUIGenerator guiGen; // generates the GUI component
  private ExplanatoryToolGenerator expToolGen; // generates the explanatory 
  																						 // tool
  private IDGeneratorGenerator idGen; // generates the IDGenerator

  public CodeGenerator(ModelOptions options, DefinedObjectTypes objTypes, 
      CreatedObjects objs, DefinedActionTypes actTypes, 
      Hashtable<SimSEObject, String> stsObjsToImages, 
      Hashtable<SimSEObject, String> ruleObjsToImages, TileData[][] map,
      ArrayList<UserData> userDatas) {
    this.options = options;
    stateGen = new StateGenerator(options, objTypes, actTypes);
    logicGen = new LogicGenerator(options, objTypes, actTypes);
    engineGen = new EngineGenerator(options, objs);
    guiGen = new GUIGenerator(options, objTypes, objs, actTypes, 
        stsObjsToImages, ruleObjsToImages, map, userDatas);
    expToolGen = new ExplanatoryToolGenerator(options, objTypes, objs, 
        actTypes);
    idGen = new IDGeneratorGenerator(options);
  }

  public void setAllowHireFire(boolean b) {
    allowHireFire = false;
  }

  /*
   * causes all of this component's sub-components to generate code
   */
  public void generate() {
    File codeGenDir = options.getCodeGenerationDestinationDirectory();
    if ((codeGenDir != null) && 
        ((!codeGenDir.exists()) || (!codeGenDir.isDirectory()))) {
      JOptionPane.showMessageDialog(null, ("Cannot find code generation" +
      		" destination directory " + codeGenDir.getAbsolutePath()), 
      		"File Not Found Error", JOptionPane.ERROR_MESSAGE);
    } else {
	    // generate directory structure:
	    File simse = new File(options.getCodeGenerationDestinationDirectory(), 
	        "simse");
	    // if directory already exists, delete all files in it:
	    if (simse.exists() && simse.isDirectory()) {
	      File[] files = simse.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    simse.mkdir();
	    
	    File lib = new File(options.getCodeGenerationDestinationDirectory(), 
	        "lib");
	    // if directory already exists, delete all files in it:
	    if (lib.exists() && lib.isDirectory()) {
	      File[] files = lib.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    lib.mkdir();
	
	    File adts = new File(simse, "adts");
	    // if directory already exists, delete all files in it:
	    if (adts.exists() && adts.isDirectory()) {
	      File[] files = adts.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    adts.mkdir();
	
	    File objects = new File(adts, "objects");
	    // if directory already exists, delete all files in it:
	    if (objects.exists() && objects.isDirectory()) {
	      File[] files = objects.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    objects.mkdir();
	
	    File actions = new File(adts, "actions");
	    // if directory already exists, delete all files in it:
	    if (actions.exists() && actions.isDirectory()) {
	      File[] files = actions.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    actions.mkdir();
	
	    File state = new File(simse, "state");
	    // if directory already exists, delete all files in it:
	    if (state.exists() && state.isDirectory()) {
	      File[] files = state.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    state.mkdir();
	
	    File logger = new File(state, "logger");
	    // if directory already exists, delete all files in it:
	    if (logger.exists() && logger.isDirectory()) {
	      File[] files = logger.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    logger.mkdir();
	
	    File logic = new File(simse, "logic");
	    // if directory already exists, delete all files in it:
	    if (logic.exists() && logic.isDirectory()) {
	      File[] files = logic.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    logic.mkdir();
	
	    File dialogs = new File(logic, "dialogs");
	    // if directory already exists, delete all files in it:
	    if (dialogs.exists() && dialogs.isDirectory()) {
	      File[] files = dialogs.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    dialogs.mkdir();
	
	    File engine = new File(simse, "engine");
	    // if directory already exists, delete all files in it:
	    if (engine.exists() && engine.isDirectory()) {
	      File[] files = engine.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    engine.mkdir();
	
	    File gui = new File(simse, "gui");
	    // if directory already exists, delete all files in it:
	    if (gui.exists() && gui.isDirectory()) {
	      File[] files = gui.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    gui.mkdir();
	    
	    File expTool = new File(simse, "explanatorytool");
	    // if directory already exists, delete all files in it:
	    if (expTool.exists() && expTool.isDirectory()) {
	      File[] files = expTool.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    expTool.mkdir();
	
	    File util = new File(simse, "util");
	    // if directory already exists, delete all files in it:
	    if (util.exists() && util.isDirectory()) {
	      File[] files = util.listFiles();
	      for (File f : files) {
	        f.delete();
	      }
	    }
	    util.mkdir();
	
	    // generate main SimSE component:
	    File ssFile = new File(options.getCodeGenerationDestinationDirectory(), 
	        ("simse\\SimSE.java"));
	    if (ssFile.exists()) {
	      ssFile.delete(); // delete old version of file
	    }
	    try {
	      FileWriter writer = new FileWriter(ssFile);
	      writer
	          .write("/* File generated by: simse.codegenerator.CodeGenerator */");
	      writer.write(NEWLINE);
	      writer.write("package simse;");
	      writer.write(NEWLINE);
	      writer.write("import simse.gui.*;");
	      writer.write(NEWLINE);
	      writer.write("import simse.state.*;");
	      writer.write(NEWLINE);
	      writer.write("import simse.logic.*;");
	      writer.write(NEWLINE);
	      writer.write("import simse.engine.*;");
	      writer.write(NEWLINE);
	      writer.write("import simse.explanatorytool.Branch;");
	      writer.write(NEWLINE);
	      writer.write("import simse.explanatorytool.MultipleTimelinesBrowser;");
	      writer.write(NEWLINE);
	      writer.write(NEWLINE);
	      writer.write("import java.util.ArrayList;");
	      writer.write(NEWLINE);
	      writer.write("public class SimSE");
	      writer.write(NEWLINE);
	      writer.write(OPEN_BRACK);
	      writer.write(NEWLINE);
	      
	      // member variables:
	    	writer.write("private static ArrayList<Branch> branches = new ArrayList<Branch>();");
	    	writer.write(NEWLINE);
	    	writer.write("private static ArrayList<SimSEGUI> guis = new ArrayList<SimSEGUI>();");
	    	writer.write(NEWLINE);
	    	writer.write("private static MultipleTimelinesBrowser timelinesBrowser = new MultipleTimelinesBrowser();");
	    	writer.write(NEWLINE);
	      writer.write(NEWLINE);
	      
	      // startNewBranch method:
	    	writer.write("public static void startNewBranch(State state, Branch branch) {");
	    	writer.write(NEWLINE);
	    	writer.write("Logic logic = new Logic(state);");
	    	writer.write(NEWLINE);
	    	writer.write("Engine engine = new Engine(logic, state);");
	    	writer.write(NEWLINE);
	    	writer.write("SimSEGUI gui = new SimSEGUI(engine, state, logic, branch, timelinesBrowser);");
	    	writer.write(NEWLINE);
	    	writer.write("state.getClock().setGUI(gui);");
	    	writer.write(NEWLINE);
	    	writer.write("gui.setBounds(0, 0, 1024, 744);");
	    	writer.write(NEWLINE);
	    	writer.write("engine.giveGUI(gui);");
	    	writer.write(NEWLINE);
	    	writer.write("logic.getTriggerChecker().update(false, gui);");
	    	writer.write(NEWLINE);
	  		writer.write("branches.add(branch);");
	  		writer.write(NEWLINE);
	  		writer.write("guis.add(gui);");
	  		writer.write(NEWLINE);
	  		writer.write("timelinesBrowser.update();");
	    	writer.write(NEWLINE);
	    	writer.write(CLOSED_BRACK);
	    	writer.write(NEWLINE);
	    	writer.write(NEWLINE);
	    	
	    	// "getBranches" method:
	    	writer.write("public static ArrayList<Branch> getBranches() {");
	    	writer.write(NEWLINE);
	    	writer.write("return branches;");
	    	writer.write(NEWLINE);
	    	writer.write(CLOSED_BRACK);
	    	writer.write(NEWLINE);
	    	writer.write(NEWLINE);
	    	
	    	// "getNumOpenBranches" method:
	    	writer.write("// returns total number of open branches");
	    	writer.write(NEWLINE);
	    	writer.write("public static int getNumOpenBranches() {");
	    	writer.write(NEWLINE);
	    	writer.write("int numOpen = 0;");
	    	writer.write(NEWLINE);
	    	writer.write("for (Branch b : branches) {");
	    	writer.write(NEWLINE);
	    	writer.write("if (!b.isClosed()) {");
	    	writer.write(NEWLINE);
	    	writer.write("numOpen++;");
	    	writer.write(CLOSED_BRACK);
	    	writer.write(NEWLINE);
	    	writer.write(CLOSED_BRACK);
	    	writer.write(NEWLINE);
	    	writer.write("return numOpen;");
	    	writer.write(NEWLINE);
	    	writer.write(CLOSED_BRACK);
	    	writer.write(NEWLINE);
	    	writer.write(NEWLINE);
	    	
	    	// "getGUIs" method:
	    	writer.write("public static ArrayList<SimSEGUI> getGUIs() {");
	    	writer.write(NEWLINE);
	    	writer.write("return guis;");
	    	writer.write(NEWLINE);
	    	writer.write(CLOSED_BRACK);
	    	writer.write(NEWLINE);
	    	writer.write(NEWLINE);
	      
	      // main method:
	      writer.write("public static void main(String args[])");
	      writer.write(NEWLINE);
	      writer.write(OPEN_BRACK);
	      writer.write(NEWLINE);
	      writer.write("startNewBranch(new State(), new Branch(null, 0, 0, null, null));");
	      writer.write(CLOSED_BRACK);
	      writer.write(NEWLINE);
	      
	      writer.write(CLOSED_BRACK);
	      writer.close();
	    } catch (IOException e) {
	      JOptionPane.showMessageDialog(null, ("Error writing file "
	          + ssFile.getPath() + ": " + e.toString()), "File IO Error",
	          JOptionPane.WARNING_MESSAGE);
	    }
	
	    // generate other components:
	    stateGen.generate();
	    boolean logicGenSuccess = logicGen.generate();
	    engineGen.generate();
	    boolean guiGenSuccess = guiGen.generate();
	    expToolGen.generate();
	    idGen.generate();
	    if (logicGenSuccess && guiGenSuccess) {
	      JOptionPane.showMessageDialog(null, "Simulation generated!",
	          "Generation Successful", JOptionPane.INFORMATION_MESSAGE);
	    }
    }
  }
}