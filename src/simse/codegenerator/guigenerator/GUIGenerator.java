/*
 * This class is responsible for generating all of the code for the SimSEGUI
 * component of the simulation
 */

package simse.codegenerator.guigenerator;

import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.WarningListDialog;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.SimSEObject;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.mapeditor.ImageLoader;
import simse.modelbuilder.mapeditor.TileData;
import simse.modelbuilder.mapeditor.UserData;
import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;;

public class GUIGenerator implements CodeGeneratorConstants {
  private ModelOptions options;
  private ImageLoaderGenerator imageLoaderGen; // generates the image loader
  private ClockPanelGenerator clockPanelGen; // generates the clock panel
  private TabPanelGenerator tabPanelGen; // generates the tab panel
  private LogoPanelGenerator logoPanelGen; // generates the logo panel
  private AttributePanelGenerator attPanelGen; // generates the attribute panel
  private ActionPanelGenerator actPanelGen; // generates the action panel
  private PopupListenerGenerator popupListGen; // generates the PopupListener
                                               // class
  private DisplayedEmployeeGenerator dispEmpGen; // generates the
                                                 // DisplayedEmployee class
  private MapDataGenerator mapDataGen; // generates the MapData class
  private SimSEMapGenerator ssmGen; // generates the SimSEMap class
  private WorldGenerator worldGen; // generates the world
  private AtAGlanceFramesGenerator glanceFramesGen; // generates the At-A-Glance
                                                    // frames for each object
                                                    // meta-type
  private AtAGlanceTableModelGenerator glanceTblModGen; // generates the
                                                        // At-A-Glance tables
                                                        // for each object type
  private SimSEAboutDialogGenerator aboutDialogGen; // generates the About
                                                    // Dialog when you click the
                                                    // SimSE logo

  public GUIGenerator(ModelOptions options, DefinedObjectTypes objTypes, 
      CreatedObjects objs, DefinedActionTypes acts, Hashtable<SimSEObject, 
      String> stsObjs, Hashtable<SimSEObject, String> ruleObjs, 
      TileData[][] map, ArrayList<UserData> userDatas) {
    this.options = options;
    Hashtable<SimSEObject, String> allObjsToImages = 
    	new Hashtable<SimSEObject, String>();
    allObjsToImages.putAll(stsObjs);
    allObjsToImages.putAll(ruleObjs);
    imageLoaderGen = new ImageLoaderGenerator(
        options.getCodeGenerationDestinationDirectory());
    tabPanelGen = new TabPanelGenerator(objTypes, allObjsToImages, 
        options.getCodeGenerationDestinationDirectory(), 
        options.getIconDirectory());
    clockPanelGen = new ClockPanelGenerator(
        options.getCodeGenerationDestinationDirectory());
    logoPanelGen = new LogoPanelGenerator(
        options.getCodeGenerationDestinationDirectory());
    attPanelGen = new AttributePanelGenerator(objTypes, 
        options.getCodeGenerationDestinationDirectory());
    actPanelGen = new ActionPanelGenerator(objTypes, acts, 
        options.getCodeGenerationDestinationDirectory());
    popupListGen = new PopupListenerGenerator(
        options.getCodeGenerationDestinationDirectory());
    dispEmpGen = new DisplayedEmployeeGenerator(
        options.getCodeGenerationDestinationDirectory());
    mapDataGen = new MapDataGenerator(
        options.getCodeGenerationDestinationDirectory());
    ssmGen = new SimSEMapGenerator(objTypes, allObjsToImages, map, userDatas,
        options.getCodeGenerationDestinationDirectory());
    worldGen = new WorldGenerator(
        options.getCodeGenerationDestinationDirectory());
    glanceFramesGen = new AtAGlanceFramesGenerator(objTypes, 
        options.getCodeGenerationDestinationDirectory());
    glanceTblModGen = new AtAGlanceTableModelGenerator(objTypes, 
        options.getCodeGenerationDestinationDirectory());
    aboutDialogGen = new SimSEAboutDialogGenerator(
        options.getCodeGenerationDestinationDirectory());
  }

  /*
   * causes all of this component's sub-components to generate code; returns
   * true if no errors, false otherwise
   */
  public boolean generate() {
	  if (!options.getIconDirectory().exists() || 
			  !options.getIconDirectory().isDirectory()) { // icon dir doesn't exist
      Vector<String> warnings = new Vector<String>();
	  	warnings.add(0, "ERROR! Incomplete simulation generated!!");
	  	warnings.add("Cannot find icon directory " + 
	  			options.getIconDirectory().getAbsolutePath());
      new WarningListDialog(warnings, "Code Generation Errors");
      return false;
	  } else {
	    CodeGeneratorUtils.copyDir(options.getIconDirectory().getPath(),
	        (options.getCodeGenerationDestinationDirectory().getPath() + 
	            "\\simse\\gui\\" + (new File(options.getIconDirectory().
	                getPath())).getName()));
	
	    ImageLoader.copyImagesToDir(
	    		options.getCodeGenerationDestinationDirectory().getPath() + 
	    		"\\simse\\gui\\");
	
	    imageLoaderGen.generate();
	    clockPanelGen.generate();
	    tabPanelGen.generate();
	    logoPanelGen.generate();
	    attPanelGen.generate();
	    actPanelGen.generate();
	    popupListGen.generate();
	    dispEmpGen.generate();
	    mapDataGen.generate();
	    ssmGen.generate();
	    worldGen.generate();
	    glanceFramesGen.generate();
	    glanceTblModGen.generate();
	    aboutDialogGen.generate();
	    generateMainGUI();
	    return true;
	  }
  }

  // generates the SimSEGUI class
  private void generateMainGUI() { 
    File mainGUIFile = new File(
        options.getCodeGenerationDestinationDirectory(), 
        ("simse\\gui\\SimSEGUI.java"));
    if (mainGUIFile.exists()) {
      mainGUIFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(mainGUIFile);
      writer
          .write("/* File generated by: simse.codegenerator.guigenerator.GUIGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.gui;");
      writer.write(NEWLINE);
      writer.write("import simse.SimSE;");
      writer.write(NEWLINE);
      writer.write("import simse.state.*;");
      writer.write(NEWLINE);
      writer.write("import simse.logic.*;");
      writer.write(NEWLINE);
      writer.write("import simse.engine.*;");
      writer.write(NEWLINE);
      writer.write("import simse.explanatorytool.Branch;");
      writer.write(NEWLINE);
      writer.write("import simse.explanatorytool.ExplanatoryTool;");
      writer.write(NEWLINE);
      writer.write("import simse.explanatorytool.MultipleTimelinesBrowser;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import java.awt.event.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Dimension;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Color;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import java.util.ArrayList;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import javax.swing.*;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public class SimSEGUI extends JFrame implements ActionListener");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      
      // member variables:
      writer.write("private TabPanel tabPanel;");
      writer.write(NEWLINE);
      writer.write("private AttributePanel attribPanel;");
      writer.write(NEWLINE);
      writer.write("private ActionPanel actionPanel;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Analyze menu:");
      writer.write(NEWLINE);
      writer.write("private JMenuBar menuBar; // menu bar at top of window");
      writer.write(NEWLINE);
      writer.write("private JMenu analyzeMenu; // analyze menu");
      writer.write(NEWLINE);
      writer.write("private JMenuItem analyzeSimItem; // menu item in \"Analyze\" menu");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("private State state;");
      writer.write(NEWLINE);
      writer.write("private Logic logic;");
      writer.write(NEWLINE);
      writer.write("private Engine engine;");
      writer.write(NEWLINE);
      writer.write("private World world;");
      writer.write(NEWLINE);
      writer.write("private ExplanatoryTool expTool;");
      writer.write(NEWLINE);
      writer.write("private static MultipleTimelinesBrowser timelinesBrowser;");
      writer.write(NEWLINE);
      writer.write("private Branch branch; // branch associated with this particular game");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // constructor:
      writer.write("public SimSEGUI(Engine e, State s, Logic l, Branch branch, MultipleTimelinesBrowser browser)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("this.branch = branch;");
      writer.write(NEWLINE);
      writer.write("timelinesBrowser = browser;");
      writer.write(NEWLINE);
      writer.write("reset(e,s,l);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // reset method
      writer.write("public void reset(Engine e, State s, Logic l)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("state = s;");
      writer.write(NEWLINE);
      writer.write("logic = l;");
      writer.write(NEWLINE);
      writer.write("engine = e;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
  		writer.write("expTool = new ExplanatoryTool(this, state.getLogger().getLog(), branch, timelinesBrowser);");
  		writer.write(NEWLINE);
  		writer.write(NEWLINE);
      writer.write("attribPanel = new AttributePanel(this, state, engine);");
      writer.write(NEWLINE);
      writer.write("tabPanel = new TabPanel(this, state, logic, attribPanel);");
      writer.write(NEWLINE);
      writer.write("actionPanel = new ActionPanel(this, state, logic);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Set window title:");
      writer.write(NEWLINE);
  		writer.write("String title = \"SimSE\";");
  		writer.write(NEWLINE);
  		writer.write("if (branch.getName() != null) {");
  		writer.write(NEWLINE);
  		writer.write("title = title.concat(\" - \" + branch.getName());");
  		writer.write(NEWLINE);
  		writer.write(CLOSED_BRACK);
  		writer.write(NEWLINE);
  		writer.write("setTitle(title);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("menuBar = new JMenuBar();");
      writer.write(NEWLINE);
      writer.write("// Analyze menu:");
      writer.write(NEWLINE);
      writer.write("analyzeMenu = new JMenu(\"Analyze\"); // \"Analyze\" menu");
      writer.write(NEWLINE);
      writer.write("analyzeSimItem = new JMenuItem(\"Analyze Simulation\");");
      writer.write(NEWLINE);
      writer.write("analyzeMenu.add(analyzeSimItem);");
      writer.write(NEWLINE);
      writer.write("analyzeSimItem.addActionListener(this);");
      writer.write(NEWLINE);
      if (!options.getExplanatoryToolAccessOption()) {
      	writer.write("analyzeMenu.setEnabled(false);");
      	writer.write(NEWLINE);
      }
      writer.write("menuBar.add(analyzeMenu);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Add menu bar to this frame:");
      writer.write(NEWLINE);
      writer.write("this.setJMenuBar(menuBar);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create main panel:");
      writer.write(NEWLINE);
      writer.write("JPanel mainPane = new JPanel(new BorderLayout());");
      writer.write(NEWLINE);
      writer.write("mainPane.setPreferredSize(new Dimension(1024, 710));");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("mainPane.add(tabPanel, BorderLayout.NORTH);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(attribPanel, BorderLayout.SOUTH);");
      writer.write(NEWLINE);
      writer.write("world = new World(state, logic, this);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(world, BorderLayout.CENTER);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(actionPanel, BorderLayout.EAST);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Set main window frame properties:");
      writer.write(NEWLINE);
      writer.write("mainPane.setBackground(Color.white);");
      writer.write(NEWLINE);
      writer.write("addWindowListener(new ExitListener());");
      writer.write(NEWLINE);
      writer.write("setContentPane(mainPane);");
      writer.write(NEWLINE);
      writer.write("setVisible(true);");
      writer.write(NEWLINE);
      writer.write("setSize(getLayout().preferredLayoutSize(this));");
      writer.write(NEWLINE);
      writer.write("// Make it show up in the center of the screen:");
      writer.write(NEWLINE);
      writer.write("setLocationRelativeTo(null);");
      writer.write(NEWLINE);
      writer.write("validate();");
      writer.write(NEWLINE);
      writer.write("repaint();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      
      // actionPerformed method:
      writer.write("public void actionPerformed(ActionEvent evt) {");
      writer.write(NEWLINE);
      writer.write("Object source = evt.getSource(); // get which component the action came from");
      writer.write(NEWLINE);
      writer.write("if (source == analyzeSimItem) {");
      writer.write(NEWLINE);
			writer.write("if (expTool.getState() == Frame.ICONIFIED) {");
			writer.write(NEWLINE);
			writer.write("expTool.setState(Frame.NORMAL);");
			writer.write(NEWLINE);
			writer.write(CLOSED_BRACK);
			writer.write(NEWLINE);
			writer.write("expTool.setVisible(true);");
			writer.write(NEWLINE);
			writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // getEngine function:
      writer.write("public Engine getEngine()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("return engine;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      
      // getSimSEState function:
      writer.write("public State getSimSEState()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("return state;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // getWorld function:
      writer.write("public World getWorld()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("return world;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // getAttributePanel function:
      writer.write("public AttributePanel getAttributePanel()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("return attribPanel;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // getTabPanel function:
      writer.write("public TabPanel getTabPanel()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("return tabPanel;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // force gui update function
      writer.write("//forces gui to update, used when the game ends");
      writer.write(NEWLINE);
      writer.write("public void forceGUIUpdate()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("tabPanel.setGUIChanged();");
      writer.write(NEWLINE);
      writer.write("attribPanel.setGUIChanged();");
      writer.write(NEWLINE);
      writer.write("update();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // update function:
      writer.write("// Update the GUI to reflect the current state:");
      writer.write(NEWLINE);
      writer.write("public void update()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("tabPanel.update();");
      writer.write(NEWLINE);
      writer.write("attribPanel.update();");
      writer.write(NEWLINE);
      writer.write("world.update();");
      writer.write(NEWLINE);
      writer.write("actionPanel.update();");
      writer.write(NEWLINE);
      if (!options.getExplanatoryToolAccessOption()) {
      	writer.write("if (!analyzeMenu.isEnabled() && state.getClock().isStopped()) {");
      	writer.write(NEWLINE);
      	writer.write("analyzeMenu.setEnabled(true);");
      	writer.write(NEWLINE);
      	writer.write(CLOSED_BRACK);
      	writer.write(NEWLINE);
      }
  		writer.write("expTool.update();");
  		writer.write(NEWLINE);
  		writer.write("branch.update(state);");
  		writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      
      // "close" method:
    	writer.write("public void close() {");
    	writer.write(NEWLINE);
    	writer.write("branch.setClosed();");
    	writer.write(NEWLINE);
    	writer.write("if (!timelinesBrowser.isVisible() && SimSE.getNumOpenBranches() == 0) {");
    	writer.write(NEWLINE);
    	writer.write("System.exit(0);");
    	writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
    	writer.write(NEWLINE);
    	writer.write("timelinesBrowser.update();");
    	writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
    	writer.write(NEWLINE);
    	writer.write(NEWLINE);

      // ExitListener class:
      writer.write("public class ExitListener extends WindowAdapter");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("public void windowClosing(WindowEvent event)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("close();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);

      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + mainGUIFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}