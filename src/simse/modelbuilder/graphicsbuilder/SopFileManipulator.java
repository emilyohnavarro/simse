/* This class is for all file manipulation involving sop files */

package simse.modelbuilder.graphicsbuilder;

import simse.modelbuilder.ModelFileManipulator;
import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.rulebuilder.CreateObjectsRule;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.SimSEObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

public class SopFileManipulator {
  private ModelOptions options;
  private DefinedObjectTypes objectTypes;
  private CreatedObjects objects;
  private DefinedActionTypes actTypes;
  
  /*
   * maps SimSEObjects from CreatedObjects to filename of associated image file
   * (String):
   */
  private Hashtable<SimSEObject, String> startStateObjsToImages; 
  
  /*
   * maps SimSEObjects from CreateObjectsRules in DefinedActionTypes to
   * filename of associated image file (String)
   */
  private Hashtable<SimSEObject, String> ruleObjsToImages; 

  public SopFileManipulator(ModelOptions options, 
  		DefinedObjectTypes objectTypes, CreatedObjects objects, 
  		DefinedActionTypes actTypes, 
  		Hashtable<SimSEObject, String> startStateObjsToImages, 
      Hashtable<SimSEObject, String> ruleObjsToImgs) {
    this.options = options;
    this.objectTypes = objectTypes;
    this.objects = objects;
    this.actTypes = actTypes;
    this.startStateObjsToImages = startStateObjsToImages;
    this.ruleObjsToImages = ruleObjsToImgs;
  }

  /*
   * loads the mdl file, filling the startStateObjsToImages, ruleObjsToImages,
   * createdObjs, objTypes, and actTypes with the data from the file, and
   * returns a Vector of warning messages (if any)
   */
  public Vector<String> loadFile(File inputFile) { 
    startStateObjsToImages.clear();
    ruleObjsToImages.clear();
    // vector of warning messages:
    Vector<String> warnings = new Vector<String>(); 
    try {
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      boolean foundBeginningOfGraphics = false;
      while (!foundBeginningOfGraphics) {
      	// read in a line of text from the file:
        String currentLine = reader.readLine(); 
        if (currentLine.equals(ModelFileManipulator.BEGIN_GRAPHICS_TAG)) { 
        	// beginning of graphics
          foundBeginningOfGraphics = true;
          reader.mark(200);
          currentLine = reader.readLine(); 
          if (currentLine.equals("<beginIconDirectoryPath>")) { // old format
            String iconDir = reader.readLine(); // get the icon directory path
            if (iconDir != null) {
              File iconDirFile = new File(iconDir);
              if (iconDirFile.exists() && iconDirFile.isDirectory()) {
                options.setIconDirectory(iconDirFile);
              } else {
                warnings.add("Cannot find icon directory " + 
                    iconDirFile.getAbsolutePath());
              }
            }
            reader.readLine(); // read in END_ICONS_DIR_TAG
          } else { // new format
            reader.reset();
          }
          boolean endOfGraphics = false;
          while (!endOfGraphics) {
            currentLine = reader.readLine(); // read in the next line of text
                                             // from the file
            if (currentLine.equals(ModelFileManipulator.END_GRAPHICS_TAG)) { 
            	// end of graphics
              endOfGraphics = true;
            } else { // not end of graphics yet
              String ssObjTypeTypeName = currentLine;
              String ssObjTypeName = reader.readLine();
              String keyAttVal = reader.readLine();
              String imageFile = reader.readLine();
              reader.readLine(); // blank line
              if ((imageFile == null) || (imageFile.length() == 0)) { // no 
              																												// image
              } else { // has image
                SimSEObjectType tempObjType = objectTypes
                    .getObjectType(SimSEObjectTypeTypes
                        .getIntRepresentation(ssObjTypeTypeName), 
                        ssObjTypeName);
                if (tempObjType == null) { // object type not found
                  warnings.add("Invalid object type: " + ssObjTypeTypeName
                      + " " + ssObjTypeName + "; ignoring object of this type");
                } else { // object type found
                  // find out what type the key attribute value is:
                  Attribute keyAtt = tempObjType.getKey();
                  if (keyAtt.getType() == AttributeTypes.INTEGER) { // integer
                                                                  	// attribute
                    try {
                      Integer keyInt = new Integer(keyAttVal);
                      // try to get the object from created objects:
                      SimSEObject tempObj = objects.getObject(
                          SimSEObjectTypeTypes
                              .getIntRepresentation(ssObjTypeTypeName),
                          ssObjTypeName, keyInt);
                      if (tempObj == null) { // object not found
                        // try to get the object from the CreateObjectsRules:
                        tempObj = getObjectFromRules(SimSEObjectTypeTypes
                            .getIntRepresentation(ssObjTypeTypeName),
                            ssObjTypeName, keyInt);
                        if (tempObj == null) { // object still not found
                          warnings.add("Invalid object: " + ssObjTypeTypeName
                              + " " + ssObjTypeName + " - " + keyAttVal
                              + "; ignoring this object");
                        } else { // object found
                          ruleObjsToImages.put(tempObj, imageFile);
                        }
                      } else { // object found
                        startStateObjsToImages.put(tempObj, imageFile);
                      }
                    } catch (NumberFormatException e) {
                      warnings.add("Invalid key attribute value for object: "
                          + ssObjTypeTypeName + " " + ssObjTypeName + " - "
                          + keyAttVal + "; ignoring this object");
                    }
                  } else if (keyAtt.getType() == AttributeTypes.DOUBLE) { 
                  	// double attribute
                    try {
                      Double keyDouble = new Double(keyAttVal);
                      // try to get the object from created objects:
                      SimSEObject tempObj = objects.getObject(
                          SimSEObjectTypeTypes
                              .getIntRepresentation(ssObjTypeTypeName),
                          ssObjTypeName, keyDouble);
                      if (tempObj == null) { // object not found
                        // try to get the object from the CreateObjectsRules:
                        tempObj = getObjectFromRules(SimSEObjectTypeTypes
                            .getIntRepresentation(ssObjTypeTypeName),
                            ssObjTypeName, keyDouble);
                        if (tempObj == null) { // object still not found
                          warnings.add("Invalid object: " + ssObjTypeTypeName
                              + " " + ssObjTypeName + " - " + keyAttVal
                              + "; ignoring this object");
                        } else { // object found
                          ruleObjsToImages.put(tempObj, imageFile);
                        }
                      } else { // object found
                        startStateObjsToImages.put(tempObj, imageFile);
                      }
                    } catch (NumberFormatException e) {
                      warnings.add("Invalid key attribute value for object: "
                          + ssObjTypeTypeName + " " + ssObjTypeName + " - "
                          + keyAttVal + "; ignoring this object");
                    }
                  } else if (keyAtt.getType() == AttributeTypes.BOOLEAN) { 
                  	// boolean attribute
                    Boolean keyBool = new Boolean(keyAttVal);
                    // try to get the object from created objects:
                    SimSEObject tempObj = objects.getObject(
                        SimSEObjectTypeTypes
                            .getIntRepresentation(ssObjTypeTypeName),
                        ssObjTypeName, keyBool);
                    if (tempObj == null) { // object not found
                      // try to get the object from the CreateObjectsRules:
                      tempObj = getObjectFromRules(SimSEObjectTypeTypes
                          .getIntRepresentation(ssObjTypeTypeName),
                          ssObjTypeName, keyBool);
                      if (tempObj == null) { // object still not found
                        warnings.add("Invalid object: " + ssObjTypeTypeName
                            + " " + ssObjTypeName + " - " + keyAttVal
                            + "; ignoring this object");
                      } else { // object found
                        ruleObjsToImages.put(tempObj, imageFile);
                      }
                    } else { // object found
                      startStateObjsToImages.put(tempObj, imageFile);
                    }
                  } else if (keyAtt.getType() == AttributeTypes.STRING) { 
                  	// string attribute
                    // try to get the object from created objects:
                    SimSEObject tempObj = objects.getObject(
                        SimSEObjectTypeTypes
                            .getIntRepresentation(ssObjTypeTypeName),
                        ssObjTypeName, keyAttVal);
                    if (tempObj == null) { // object not found
                      // try to get the object from the CreateObjectsRules:
                      tempObj = getObjectFromRules(SimSEObjectTypeTypes
                          .getIntRepresentation(ssObjTypeTypeName),
                          ssObjTypeName, keyAttVal);
                      if (tempObj == null) { // object still not found
                        warnings.add("Invalid object: " + ssObjTypeTypeName
                            + " " + ssObjTypeName + " - " + keyAttVal
                            + "; ignoring this object");
                      } else { // object found
                        ruleObjsToImages.put(tempObj, imageFile);
                      }
                    } else { // object found
                      startStateObjsToImages.put(tempObj, imageFile);
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
      JOptionPane.showMessageDialog(null, ("Cannot find file " + inputFile
          .getPath()), "File Not Found", JOptionPane.WARNING_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error reading file! " + e
          .toString()), "File IO Error", JOptionPane.WARNING_MESSAGE);
    }
    return warnings;
  }

  /*
   * returns the specified object if it is generated by one of the 
   * CreateObjectsRules, otherwise returns null
   */
  private SimSEObject getObjectFromRules(int type, String simSEObjectTypeName,
      Object keyAttValue) { 
    Vector<ActionType> actions = actTypes.getAllActionTypes();
    for (int i = 0; i < actions.size(); i++) {
      ActionType act = actions.elementAt(i);
      Vector<CreateObjectsRule> rules = act.getAllCreateObjectsRules();
      for (int j = 0; j < rules.size(); j++) {
        CreateObjectsRule rule = rules.elementAt(j);
        Vector<SimSEObject> objs = rule.getAllSimSEObjects();
        for (int k = 0; k < objs.size(); k++) {
          SimSEObject tempObj = objs.elementAt(k);
          if ((tempObj.getSimSEObjectType().getType() == type)
              && (tempObj.getName().equals(simSEObjectTypeName))
              && (tempObj.getKey().isInstantiated())
              && (tempObj.getKey().getValue().equals(keyAttValue))) {
            return tempObj;
          }
        }
      }
    }
    return null;
  }
}