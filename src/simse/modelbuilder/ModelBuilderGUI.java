/* This class defines the main GUI for the model builder */

package simse.modelbuilder;

import simse.codegenerator.CodeGenerator;

import simse.modelbuilder.actionbuilder.ActionBuilderGUI;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.actionbuilder.DestroyerPrioritizer;
import simse.modelbuilder.actionbuilder.TriggerPrioritizer;
import simse.modelbuilder.graphicsbuilder.GraphicsBuilderGUI;
import simse.modelbuilder.mapeditor.MapEditorGUI;
import simse.modelbuilder.mapeditor.TileData;
import simse.modelbuilder.mapeditor.UserData;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.ObjectBuilderGUI;
import simse.modelbuilder.rulebuilder.ContinuousRulePrioritizer;
import simse.modelbuilder.rulebuilder.DestroyerRulePrioritizer;
import simse.modelbuilder.rulebuilder.RuleBuilderGUI;
import simse.modelbuilder.rulebuilder.TriggerRulePrioritizer;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.NarrativeDialog;
import simse.modelbuilder.startstatebuilder.StartStateBuilderGUI;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class ModelBuilderGUI extends JFrame implements ActionListener,
    ChangeListener, MenuListener {
  private File openFile; // file currently open
  private boolean fileModSinceLastSave; // denotes whether the file has been
                                        // modified since the last save

  private DefinedObjectTypes objectTypes;
  private CreatedObjects objects;
  private DefinedActionTypes actionTypes;
  private ArrayList<UserData> userDatas; // list of UserData objects for each 
  																			 // employee that is displayed in the 
  																			 // map
  private TileData[][] map; // map
  private ModelOptions options;
  private ModelOptionsFileManipulator optionsFileManip;
  
  private ObjectBuilderGUI objectBuilder;
  private StartStateBuilderGUI startStateBuilder;
  private ActionBuilderGUI actionBuilder;
  private RuleBuilderGUI ruleBuilder;
  private GraphicsBuilderGUI graphicsBuilder;
  private MapEditorGUI mapEditor;

  private ModelFileManipulator fileManip;
  private JFileChooser fileChooser;

  private JTabbedPane mainPane;
  private JMenuBar menuBar; // menu bar at top of window

  // File menu:
  private JMenu fileMenu; // file menu
  private JMenuItem newItem; // menu item in "File" menu
  private JMenuItem openItem; // menu item in "File" menu
  private JMenuItem closeItem; // menu item in "File" menu
  private JMenuItem saveItem; // menu item in "File" menu
  private JMenuItem saveAsItem; // menu item in "File" menu
  private JMenuItem exitItem; // menu item in "File" menu

  // Edit menu:
  private JMenu editMenu; // edit menu
  private JMenuItem optionsItem;
  private JMenuItem narrativeItem;

  // Prioritize menu:
  private JMenu prioritizeMenu;
  private JMenuItem triggerItem; // menu item in "Prioritize" menu for
                                 // prioritizing triggers
  private JMenuItem destroyerItem; // menu item in "Prioritize" menu for
                                   // prioritizing destroyers
  // rules submenu:
  private JMenu rulesMenu;
  private JMenuItem continuousItem; // menu item in "rules" sub-menu for
                                    // prioritizing continuous rules
  private JMenu triggerRulesMenu; // sub-menu in "rules" sub-menu for
                                  // prioritizing trigger rules
  private JMenu destroyerRulesMenu; // sub-menu in "rules" sub-menu for
                                    // prioritizing destroyer rules

  // Generate menu:
  private JMenu generateMenu; // generate menu
  private JMenuItem generateSimItem; // menu item in "Generate" menu

  public ModelBuilderGUI() {
    fileChooser = new JFileChooser();
    fileChooser.addChoosableFileFilter(new MDLFileFilter()); // make it so it
                                                             // only displays
                                                             // .mdl files

    fileModSinceLastSave = false;

    // Set window title:
    setTitle("SimSE Model Builder");

    // Create main panel:
    mainPane = new JTabbedPane();
    mainPane.setPreferredSize(new Dimension(1024, 710));
    SingleSelectionModel model = mainPane.getModel();
    model.addChangeListener(this);
    
    options = new ModelOptions();
    optionsFileManip = new ModelOptionsFileManipulator(options);

    objectBuilder = new ObjectBuilderGUI(this);
    mainPane.addTab("Object Types", objectBuilder);

    objectTypes = objectBuilder.getDefinedObjectTypes();

    startStateBuilder = new StartStateBuilderGUI(this, objectTypes);
    mainPane.addTab("Start State", startStateBuilder);

    objects = startStateBuilder.getCreatedObjects();

    actionBuilder = new ActionBuilderGUI(this, objectTypes);
    mainPane.addTab("Actions", actionBuilder);

    actionTypes = actionBuilder.getDefinedActionTypes();

    ruleBuilder = new RuleBuilderGUI(this, objectTypes, actionTypes);
    mainPane.addTab("Rules", ruleBuilder);

    graphicsBuilder = new GraphicsBuilderGUI(this, options, objectTypes, 
        objects, actionTypes);
    mainPane.addTab("Graphics", graphicsBuilder);

    mapEditor = new MapEditorGUI(this, options, objectTypes, objects, 
        actionTypes, graphicsBuilder.getStartStateObjsToImages(), 
        graphicsBuilder.getRuleObjsToImages());
    mainPane.addTab("Map", mapEditor);

    mainPane.setOpaque(true);

    userDatas = mapEditor.getUserDatas();
    map = mapEditor.getMap();

    fileManip = new ModelFileManipulator(options, objectTypes, actionTypes, 
        objects, userDatas, map);

    // Create menu bar and menus:
    menuBar = new JMenuBar();
    fileMenu = new JMenu("File"); // "File" menu
    // "New" menu item:
    newItem = new JMenuItem("New");
    fileMenu.add(newItem);
    newItem.addActionListener(this);
    // "Open" menu item:
    openItem = new JMenuItem("Open");
    fileMenu.add(openItem);
    openItem.addActionListener(this);
    // "Close" menu item:
    closeItem = new JMenuItem("Close");
    fileMenu.add(closeItem);
    closeItem.addActionListener(this);
    // (Add separator):
    fileMenu.addSeparator();
    // "Save" menu item:
    saveItem = new JMenuItem("Save");
    fileMenu.add(saveItem);
    saveItem.addActionListener(this);
    // "Save As..." menu item:
    saveAsItem = new JMenuItem("Save As...");
    fileMenu.add(saveAsItem);
    saveAsItem.addActionListener(this);
    // (Add separtor):
    fileMenu.addSeparator();
    // "Exit" menu item:
    exitItem = new JMenuItem("Exit");
    fileMenu.add(exitItem);
    exitItem.addActionListener(this);

    menuBar.add(fileMenu);

    // Edit menu:
    editMenu = new JMenu("Edit"); // "Edit" menu
    editMenu.setEnabled(false); // disable menu
    optionsItem = new JMenuItem("Model options");
    editMenu.add(optionsItem);
    optionsItem.addActionListener(this);
    narrativeItem = new JMenuItem("Starting narrative");
    editMenu.add(narrativeItem);
    narrativeItem.addActionListener(this);

    menuBar.add(editMenu);

    // Prioritize menu:
    prioritizeMenu = new JMenu("Prioritize"); // "Prioritize" menu
    prioritizeMenu.setEnabled(false); // disable menu
    prioritizeMenu.addMenuListener(this);
    triggerItem = new JMenuItem("Triggers");
    prioritizeMenu.add(triggerItem);
    triggerItem.addActionListener(this);
    destroyerItem = new JMenuItem("Destroyers");
    prioritizeMenu.add(destroyerItem);
    destroyerItem.addActionListener(this);
    menuBar.add(prioritizeMenu);

    // Rules sub-menu:
    rulesMenu = new JMenu("Rules"); // "Rules" sub-menu
    continuousItem = new JMenuItem("Continuous Rules");
    rulesMenu.add(continuousItem);
    continuousItem.addActionListener(this);
    triggerRulesMenu = new JMenu("Trigger Rules"); // "Trigger Rules" menu
    rulesMenu.add(triggerRulesMenu);
    destroyerRulesMenu = new JMenu("Destroyer Rules"); // "Destroyer rules" menu
    rulesMenu.add(destroyerRulesMenu);
    prioritizeMenu.add(rulesMenu);

    // Generate menu:
    generateMenu = new JMenu("Generate"); // "Generate" menu
    generateMenu.setEnabled(false); // disable menu
    generateSimItem = new JMenuItem("Generate Simulation");
    generateMenu.add(generateSimItem);
    generateSimItem.addActionListener(this);
    menuBar.add(generateMenu);

    // Add menu bar to this frame:
    this.setJMenuBar(menuBar);

    // make it so no file is open to begin with:
    setNoOpenFile();

    // Set main window frame properties:
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new ExitListener());
    setContentPane(mainPane);
    setVisible(true);
    setSize(getLayout().preferredLayoutSize(this));
    // Make it show up in the center of the screen:
    setLocationRelativeTo(null);
    validate();
    pack();
    repaint();
  }

  // sets this variable to "true"
  public void setFileModSinceLastSave() { 
    fileModSinceLastSave = true;
  }

  // handles user actions
  public void actionPerformed(ActionEvent evt) { 
    Object source = evt.getSource(); // get which component the action came from
    if (source == newItem) {
      newFile();
    } else if (source == openItem) {
      openFile();
    } else if (source == closeItem) {
      if (openFile != null) { // a file is open
        closeFile();
      }
    }

    if (source == saveItem) {
      if (openFile != null) { // a file is open
        if (openFile.exists()) { // file has already been saved before
          checkForInconsistencies(false);
          fileManip.generateFile(openFile, graphicsBuilder.
              getStartStateObjsToImages(), graphicsBuilder.
              getRuleObjsToImages(), objectBuilder.allowHireFire());
          fileModSinceLastSave = false;
        } else { // file has not been saved before
          saveAs();
        }
      }
    } else if (source == saveAsItem) {
      if (openFile != null) { // a file is open
        saveAs();
      }
    } else if (source == exitItem) {
      if (closeFile()) { // if current file is successfully closed, exit
        System.exit(0);
      }
    } else if (source == optionsItem) {
      if (openFile != null) { // a file is open
        // bring up model options dialog:
        new ModelOptionsDialog(this, options);
        fileModSinceLastSave = true;
        
        Component selectedComp = mainPane.getSelectedComponent();
        /*
         * if the graphics builder or map editor are in focus and the model
         * options (icon dir) have changed, reload: 
         */
        if ((selectedComp instanceof JPanel) &&
            ((selectedComp == graphicsBuilder) ||
                (selectedComp == mapEditor))) {
          checkForInconsistencies(true);
        }
      }
    } else if (source == narrativeItem) {
      if (openFile != null) { // a file is open
        // bring up starting narrative dialog:
        new NarrativeDialog(this, objects, "Starting Narrative");
        fileModSinceLastSave = true;
      }
    } else if (source == triggerItem) {
      new TriggerPrioritizer(this, actionTypes);
      fileModSinceLastSave = true;
    } else if (source == destroyerItem) {
      new DestroyerPrioritizer(this, actionTypes);
      fileModSinceLastSave = true;
    } else if (source == continuousItem) {
      new ContinuousRulePrioritizer(this,
          actionTypes);
      fileModSinceLastSave = true;
    } else if (source == generateSimItem) {
      int choice = JOptionPane.OK_OPTION;
      if (fileModSinceLastSave) {
        // must save first
        choice = JOptionPane
            .showConfirmDialog(
                null,
                ("You must save this model before generating the simulation. " +
                		"Press OK to save now."), "Confirm Save", 
                		JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.OK_OPTION) {
          if (openFile.exists()) { // file has already been saved before
            fileManip.generateFile(openFile, graphicsBuilder
                .getStartStateObjsToImages(), graphicsBuilder
                .getRuleObjsToImages(), objectBuilder.allowHireFire());
            fileModSinceLastSave = false;
          } else { // file has not been saved before
            saveAs();
          }
        }
      }

      if (choice == JOptionPane.OK_OPTION) {
        if (options.getCodeGenerationDestinationDirectory() == null) { // no
          																														 // dest
          																														 // dir 
          																														 // set
		      // get directory to generate code in:
		      // Bring up a file chooser to choose a directory:
		      JFileChooser dirFileChooser = new JFileChooser();
		      // make it so it only displays directories:
		      dirFileChooser.addChoosableFileFilter(new DirectoryFileFilter()); 
		      // bring up open file chooser:
		      dirFileChooser.setSelectedFile(new File(""));
		      dirFileChooser
		          .setDialogTitle("Please select a destination directory (to " +
		          		"generate code into):");
		      dirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		      int returnVal = dirFileChooser.showOpenDialog(this);
		      if (returnVal == JFileChooser.APPROVE_OPTION) {
		        File f = dirFileChooser.getSelectedFile();
		        if (f.isDirectory()) { // valid
		          // set destination directory:
		          options.setCodeGenerationDestinationDirectory(f);
		        }
		      }
        }
        // generate code:
        CodeGenerator codeGen = new CodeGenerator(options, objectTypes, 
            objects, actionTypes, graphicsBuilder.
            getStartStateObjsToImages(), graphicsBuilder.
            getRuleObjsToImages(), map, userDatas);
        
        codeGen.setAllowHireFire(objectBuilder.allowHireFire());
        mainPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        codeGen.generate();
        mainPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
    } else if (source instanceof JMenuItem) {
      JMenuItem mItem = (JMenuItem) source;
      if (mItem.getText().endsWith("Trigger")) {
        ActionType tempAct = actionTypes.getActionType(mItem.getText()
            .substring(0, mItem.getText().indexOf(' ')));
        new TriggerRulePrioritizer(this, tempAct);
        fileModSinceLastSave = true;
      } else if (mItem.getText().endsWith("Destroyer")) {
        ActionType tempAct = actionTypes.getActionType(mItem.getText()
            .substring(0, mItem.getText().indexOf(' ')));
        new DestroyerRulePrioritizer(this, tempAct);
        fileModSinceLastSave = true;
      }
    }
  }

  public void stateChanged(ChangeEvent e) {
    checkForInconsistencies(true);
  }

  public void menuSelected(MenuEvent e) {
    if (e.getSource() == prioritizeMenu) {
      resetRulesMenu();
    }
  }

  public void menuCanceled(MenuEvent e) {}

  public void menuDeselected(MenuEvent e) {}

  private boolean saveAs() {
    // bring up save file chooser:
    fileChooser.setSelectedFile(openFile);
    int returnVal = fileChooser.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      if (isMDLFile(selectedFile)) {
        checkForInconsistencies(false);
        // generate file:
        fileManip.generateFile(selectedFile, graphicsBuilder.
            getStartStateObjsToImages(), graphicsBuilder.getRuleObjsToImages(),
            objectBuilder.allowHireFire());
        openFile = selectedFile;
        resetWindowTitle();
        fileModSinceLastSave = false;
        return true;
      } else {
        JOptionPane.showMessageDialog(null,
            "File not saved! File must have extension \".mdl\"",
            "Unexpected File Format", JOptionPane.WARNING_MESSAGE);
        saveAs(); // try again
        return false;
      }
    } else { // "cancel" button chosen
      return false;
    }
  }

  private void setNewOpenFile(File file) {
    openFile = file;
    prioritizeMenu.setEnabled(true);
    editMenu.setEnabled(true);
    generateMenu.setEnabled(true);
    resetWindowTitle();
    if (file.exists()) {
      optionsFileManip.loadFile(file);
    }
    objectBuilder.setNewOpenFile(openFile);
    startStateBuilder.setNewOpenFile(openFile);
    actionBuilder.setNewOpenFile(openFile);
    ruleBuilder.setNewOpenFile(openFile);
    graphicsBuilder.setNewOpenFile(openFile);
    mapEditor.setNewOpenFile(openFile);
  }

  // make sit so there's no open file in the GUI
  private void setNoOpenFile() {
    openFile = null;
    setTitle("SimSE Model Builder");
    fileModSinceLastSave = false;
    options.clearAll();
    objectBuilder.setNoOpenFile();
    startStateBuilder.setNoOpenFile();
    actionBuilder.setNoOpenFile();
    ruleBuilder.setNoOpenFile();
    graphicsBuilder.setNoOpenFile();
    mapEditor.setNoOpenFile();
    // disable UI components:
    prioritizeMenu.setEnabled(false);
    editMenu.setEnabled(false);
    generateMenu.setEnabled(false);
  }

  // resets the window title to reflect the current file open
  private void resetWindowTitle() {
    setTitle(("SimSE Model Builder - [") + openFile.getName() + ("]"));
  }

  // creates a new .mdl file
  private void newFile() { 
    if (closeFile()) { // if currently-open file is successfuly closed, continue
      setNewOpenFile(new File("NewFile.mdl"));
      fileModSinceLastSave = false;
    }
  }

  // allows the user to open a file
  private void openFile() { 
  	// attempt ot close currently opened file; if it works, continue:
    if (closeFile()) { 
      // bring up open file chooser:
      fileChooser.setSelectedFile(new File(""));
      int returnVal = fileChooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        // open file:
        File f = fileChooser.getSelectedFile();
        if (isMDLFile(f)) { // valid file format
          setNewOpenFile(f);
          fileModSinceLastSave = false;
        } else {
          JOptionPane.showMessageDialog(null,
              "File not opened! File must have extension \".mdl\"",
              "Unexpected File Format", JOptionPane.WARNING_MESSAGE);
          openFile(); // try again
        }
      }
    }
  }

  /*
   * closes the file currently open (returns true if file is closed, false
   * otherwise (i.e., user cancels operation))
   */
  private boolean closeFile() {
    if (fileModSinceLastSave) { // file has been modified since last save
      int choice = JOptionPane.showConfirmDialog(null, ("Save changes to "
          + openFile.getName() + "?"), "SimSE Model Builder",
          JOptionPane.YES_NO_CANCEL_OPTION);
      if (choice != JOptionPane.CANCEL_OPTION) {
        if (choice == JOptionPane.YES_OPTION) {
          if (openFile.exists()) { // file has already been saved before
            fileManip.generateFile(openFile, graphicsBuilder
                .getStartStateObjsToImages(), graphicsBuilder
                .getRuleObjsToImages(), objectBuilder.allowHireFire());
          } else { // file has not been saved before
            if (saveAs() == false) {
              return false; // file not saved, action cancelled
            }
          }
        }
        setNoOpenFile();
        return true;
      }
      return false;
    }
    setNoOpenFile();
    return true;
  }

  /*
   * reset the trigger/destroyer prioritize menus to reflect the rules currently
   * in the model
   */
  public void resetRulesMenu() { 
    // trigger rules menu:
    triggerRulesMenu.removeAll();
    Vector<ActionType> acts = actionTypes.getAllActionTypes();
    // go through all action types and add them to the menu:
    for (int i = 0; i < acts.size(); i++) {
      ActionType tempAct = acts.elementAt(i);
      if (tempAct.hasTriggerRules()) {
        JMenuItem tempItem = new JMenuItem(tempAct.getName() + " Trigger");
        triggerRulesMenu.add(tempItem);
        tempItem.addActionListener(this);
      }
    }

    // destroyer rules menu:
    destroyerRulesMenu.removeAll();
    // go through all action types and add them to the menu:
    for (int i = 0; i < acts.size(); i++) {
      ActionType tempAct = acts.elementAt(i);
      if (tempAct.hasDestroyerRules()) {
        JMenuItem tempItem = new JMenuItem(tempAct.getName() + " Destroyer");
        destroyerRulesMenu.add(tempItem);
        tempItem.addActionListener(this);
      }
    }
  }

  private boolean isMDLFile(File f) {
    String extension = f.getName().substring(f.getName().length() - 4);
    if (extension.equalsIgnoreCase(".mdl")) {
      return true;
    } else {
      return false;
    }
  }
  
  /*
   * Checks for inconsistencies between the model parts; if resetUI is true,
   * resets the UI by clearing all current selections.
   */
  private void checkForInconsistencies(boolean resetUI) {
    if (openFile != null) { // there is a file currently open
      startStateBuilder.getCreatedObjects().updateAllInstantiatedAttributes();
      try {
      	// create a temporary file:
        File tempFile = File.createTempFile(openFile.getName(), ".mdl"); 
        tempFile.deleteOnExit(); // make sure it's deleted on exit
        fileManip.generateFile(tempFile, graphicsBuilder.
            getStartStateObjsToImages(), graphicsBuilder.getRuleObjsToImages(),
            objectBuilder.allowHireFire());
        startStateBuilder.reload(tempFile, resetUI);
        actionBuilder.reload(tempFile, resetUI);
        ruleBuilder.reload(tempFile, resetUI);
        graphicsBuilder.reload(tempFile, resetUI);
        mapEditor.reload(tempFile);
        tempFile.delete();
      } catch (IOException i) {
        System.out.println("File I/O error creating temp file for "
            + openFile.getName() + ": " + i.toString());
      }
    }
  }

  public ObjectBuilderGUI getObjectBuilderGUI() {
    return objectBuilder;
  }

  public static void main(String[] args) {
    new ModelBuilderGUI();
  }

  public class ExitListener extends WindowAdapter {
    public void windowClosing(WindowEvent event) {
      if (closeFile()) {
        System.exit(0);
      }
    }
  }
}