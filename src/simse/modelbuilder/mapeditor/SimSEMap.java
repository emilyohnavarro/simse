/*
 * This class contains methods that are used in both MapEditorGUI and World.
 */

package simse.modelbuilder.mapeditor;

import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.InstantiatedAttribute;
import simse.modelbuilder.startstatebuilder.SimSEObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SimSEMap extends JPanel implements MouseListener, ActionListener {
  protected ModelOptions options;
  protected DefinedObjectTypes objectTypes;
  protected CreatedObjects objects;
  protected DefinedActionTypes actions;
  protected Hashtable<SimSEObject, String> startStateObjsToImages;
  protected Hashtable<SimSEObject, String> ruleObjsToImages;

  protected TileData[][] mapRep;
  protected ArrayList<UserData> sopUsers;
  protected int ssObjCount;

  // map constants:
  private final String BEGIN_MAP_TAG = "<beginMap>";
  private final String END_MAP_TAG = "<endMap>";

  public SimSEMap(ModelOptions options, DefinedObjectTypes objectTypes, 
      CreatedObjects objects, DefinedActionTypes actions, 
      Hashtable<SimSEObject, String> startStateObjsToImages, 
      Hashtable<SimSEObject, String> ruleObjsToImages) {
    this.options = options;
    this.objectTypes = objectTypes;
    this.objects = objects;
    this.actions = actions;
    this.startStateObjsToImages = startStateObjsToImages;
    this.ruleObjsToImages = ruleObjsToImages;

    mapRep = new TileData[MapData.X_MAPSIZE][MapData.Y_MAPSIZE];
    for (int i = 0; i < MapData.Y_MAPSIZE; i++) {
      for (int j = 0; j < MapData.X_MAPSIZE; j++) {
        mapRep[j][i] = new TileData(MapData.TILE_GRID, MapData.TRANSPARENT);
      }
    }
    sopUsers = new ArrayList<UserData>();
  }

  /*
   * loads the map from the input file (.mdl) and returns a Vector of warning
   * message Strings
   */
  public Vector<String> loadFile(File inputFile) { 
  	// vector of warning messages:
  	Vector<String> warnings = new Vector<String>();
  	
    sopUsers.clear();
    
    // try loading the icon directory:
    File iconDir = options.getIconDirectory();
    if (iconDir != null) {
      if ((!iconDir.exists()) || (!iconDir.isDirectory())) {
        warnings.add("Cannot find icon directory " + 
            iconDir.getAbsolutePath());
      }
      //else {
		    // load the startstate objects
		    Enumeration<SimSEObject> ssObj = startStateObjsToImages.keys();
		    Enumeration<String> ssImg = startStateObjsToImages.elements();
		
		    while (ssObj.hasMoreElements()) {
		      SimSEObject simObj = ssObj.nextElement();
		      String tmpIconLoc = options.getIconDirectory().getPath() + "\\"
		          + ssImg.nextElement();
		
		      int type = simObj.getSimSEObjectType().getType();
		      String objType = SimSEObjectTypeTypes.getText(type);
		
		      if (objType.equals(SimSEObjectTypeTypes
		          .getText(SimSEObjectTypeTypes.EMPLOYEE))) {
		        Vector<InstantiatedAttribute> attrib = simObj.getAllAttributes();
		
		        boolean hired = true;
		        for (int i = 0; i < attrib.size(); i++) {
		          InstantiatedAttribute ia = attrib.get(i);
		
		          if (ia.getAttribute().getName().equalsIgnoreCase("hired")) {
		            Boolean b = (Boolean) ia.getValue();
		            // in case adding hire/fire to model, it will start as null and 
		            // none of the employees will be visible
		            if (b != null)
		              hired = b.booleanValue();
		          }
		        }
		        UserData tmpUser = new UserData(simObj, tmpIconLoc, this, false, 
		        		hired, -1, -1);
		        sopUsers.add(tmpUser);
		      }
		    }
		
		    ssObjCount = sopUsers.size();
		
		    Enumeration<SimSEObject> rObj = ruleObjsToImages.keys();
		    Enumeration<String> rImg = ruleObjsToImages.elements();
		
		    // load the rule objects
		    while (rObj.hasMoreElements()) {
		      SimSEObject simObj = rObj.nextElement();
		      String tmpIconLoc = options.getIconDirectory().getPath() + "\\"
		          + rImg.nextElement();
		
		      int type = simObj.getSimSEObjectType().getType();
		      String objType = SimSEObjectTypeTypes.getText(type);
		
		      if (objType.equals(SimSEObjectTypeTypes
		          .getText(SimSEObjectTypeTypes.EMPLOYEE))) {
		        UserData tmpUser = new UserData(simObj, tmpIconLoc, this, false, 
		        		false, -1, -1);
		        sopUsers.add(tmpUser);
		      }
		    }
      //}
      
      // read in the map:
	    try {
	      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	      boolean foundBeginningOfMap = false;
	      while (!foundBeginningOfMap) {
	      	// read in a line of text from the file:
	        String currentLine = reader.readLine(); 
	        if (currentLine.equals(BEGIN_MAP_TAG)) { // beginning of map
	          foundBeginningOfMap = true;
	          boolean endOfMap = false;
	          while (!endOfMap) {
	          	// read in the next line of text from the file:
	            currentLine = reader.readLine(); 
	            if (currentLine.equals(END_MAP_TAG)) { // end of graphics
	              endOfMap = true;
	            } else { // not end of map yet
	              if (currentLine.equals("<beginSOPUsers>")) {
	                currentLine = reader.readLine();
	                while (!currentLine.equals("<endSOPUsers>")) { // read in the 
	                																							 // sop users:
	                  String tmpName = currentLine;
	                  boolean tmpDisplayed = reader.readLine().equals("true");
	                  reader.readLine().equals("true"); // activated
	                  int tmpX = Integer.parseInt(reader.readLine());
	                  int tmpY = Integer.parseInt(reader.readLine());
	
	                  for (int i = 0; i < sopUsers.size(); i++) {
	                    UserData user = sopUsers.get(i);
	                    if (user.getName().equals(tmpName)) {
	                      user.setDisplayed(tmpDisplayed);
	                      // user.setActivated(activated);
	                      user.setXYLocations(tmpX, tmpY);
	                    }
	                  }
	                  currentLine = reader.readLine(); // ignore space
	                  currentLine = reader.readLine(); // next SOP object or
	                                                   // <endSOPUsers>
	                }
	              }
	
	              // draw map
	              for (int i = 0; i < MapData.Y_MAPSIZE; i++) {
	                for (int j = 0; j < MapData.X_MAPSIZE; j++) {
	                  mapRep[j][i].baseKey = Integer.parseInt(reader.readLine());
	                  mapRep[j][i].fringeKey = 
	                  	Integer.parseInt(reader.readLine());
	                }
	              }
	            }
	          }
	        }
	      }
	      reader.close();
	    } catch (IOException i) {
	      JOptionPane.showMessageDialog(null,
	          "An error has occured while reading file.");
	    } catch (NumberFormatException e) {}
    }

    return warnings;
  }

  public void mouseClicked(MouseEvent me) {}

  public void mousePressed(MouseEvent me) {}

  public void mouseReleased(MouseEvent me) {}

  public void mouseEntered(MouseEvent me) {}

  public void mouseExited(MouseEvent me) {}

  public void actionPerformed(ActionEvent e) {}
}