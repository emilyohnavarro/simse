/*
 * This class is for generating the DefinedObjectTypes into a file and reading
 * that file into memory
 */

package simse.modelbuilder.objectbuilder;

import simse.modelbuilder.ModelFileManipulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ObjectFileManipulator {
  private DefinedObjectTypes objects;
  private boolean allowHireFire;
  
  public ObjectFileManipulator(DefinedObjectTypes objects) {
    this.objects = objects;
  }

  public boolean isAllowHireFireChecked() {
    return false;
  }

  /*
   * loads the mdl file into memory, filling the "objects" data structure with
   * the data from the file
   */
  public void loadFile(File inputFile) { 
    objects.clearAll();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      boolean foundBeginningOfObjectTypes = false;
      while (!foundBeginningOfObjectTypes) {
      	// read in a line of text from the file:
        String currentLine = reader.readLine(); 
        if (currentLine.equals(
        		ModelFileManipulator.BEGIN_ALLOW_HIRE_FIRE_TAG)) {
          allowHireFire = reader.readLine().equals("true");
          reader.readLine(); // remove the end allowHireFire tag
          allowHireFire = false; // removed this functionality, always false now
        }

        if (currentLine.equals(ModelFileManipulator.BEGIN_OBJECT_TYPES_TAG)) { 
        	// beginning of object types
          foundBeginningOfObjectTypes = true;
          boolean endOfObjectTypes = false;
          while (!endOfObjectTypes) {
            currentLine = reader.readLine();
            if (currentLine.equals(ModelFileManipulator.END_OBJECT_TYPES_TAG)) {
            	// end of object types
              endOfObjectTypes = true;
            } else { // not end of object types yet
              if (currentLine.equals(
              		ModelFileManipulator.BEGIN_OBJECT_TYPE_TAG)) {
              	/*
              	 * create a new object in memory with the type and name
              	 * specified in the following 2 lines of the file:
              	 */
                SimSEObjectType newObj = new SimSEObjectType(Integer
                    .parseInt(reader.readLine()), reader.readLine()); 
                boolean endOfObj = false;
                while (!endOfObj) {
                  currentLine = reader.readLine(); // get the next line
                  if (currentLine.equals(
                  		ModelFileManipulator.END_OBJECT_TYPE_TAG)) {
                  	// end of object
                    endOfObj = true;
                    // add object type to defined object types:
                    objects.addObjectType(newObj); 
                  } else if (currentLine.equals(
                  		ModelFileManipulator.BEGIN_ATTRIBUTE_TAG)) { // beginning
                  																								 // of
                  																								 // attribute
                  	// get the attribute name:
                    String attName = reader.readLine(); 
                    // get the attribute type:
                    int type = Integer.parseInt(reader.readLine()); 
                    // get the attribute's visibility:
                    String visString = reader.readLine(); 
                    boolean visible = visString.equals("1") ? true : false;
                    // get the attribute's "key" variable:
                    String keyString = reader.readLine(); 
                    boolean key = keyString.equals("1") ? true : false;
                    // get the attribute's visibility on completion:
                    String visAtEndString = reader.readLine(); 
                    boolean visibleAtEnd = 
                    	visAtEndString.equals("1") ? true : false;
                    if (type == AttributeTypes.INTEGER) { // integer attribute
                    	// get the minimum value:
                      String minValStr = reader.readLine(); 
                      Integer minVal = 
                      	(!minValStr.equals(ModelFileManipulator.BOUNDLESS)) ? 
                      			new Integer(minValStr) : null;
                      // get the maximum value:
                      String maxValStr = reader.readLine(); 
                      Integer maxVal = 
                      	(!maxValStr.equals(ModelFileManipulator.BOUNDLESS)) ? 
                      			new Integer(maxValStr) : null;
                      // add attribute to object:
                      newObj.addAttribute(new NumericalAttribute(attName, type,
                          visible, key, visibleAtEnd, minVal, maxVal, null,
                          null));
                    } else if (type == AttributeTypes.DOUBLE) { // double
                    																						// attribute
                    	// get the minimum value:
                      String minValStr = reader.readLine(); 
                      Double minVal = 
                      	(!minValStr.equals(ModelFileManipulator.BOUNDLESS)) ? 
                      			new Double(minValStr) : null;
                      // get the maximum value:
                      String maxValStr = reader.readLine(); 
                      Double maxVal = 
                      	(!maxValStr.equals(ModelFileManipulator.BOUNDLESS)) ?
                      			new Double(maxValStr) : null;
                      // get the minimum num digits:
                      String minDigStr = reader.readLine(); 
                      Integer minDig = 
                      	(!minDigStr.equals(ModelFileManipulator.BOUNDLESS)) ? 
                      			new Integer(minDigStr) : null;
                      // get the maximum value:
                      String maxDigStr = reader.readLine(); 
                      Integer maxDig = 
                      	(!maxDigStr.equals(ModelFileManipulator.BOUNDLESS)) ?
                      			new Integer(maxDigStr) : null;
                      // add attribute to object:
                      newObj.addAttribute(new NumericalAttribute(attName, type,
                          visible, key, visibleAtEnd, minVal, maxVal, minDig,
                          maxDig));
                    } else { // non-numerical attribute
                    	// add attribute to object:
                      newObj.addAttribute(new NonNumericalAttribute(attName,
                          type, visible, key, visibleAtEnd)); 
                    }
                    reader.readLine(); // read in the END_ATTRIBUTE tag
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
          ("Cannot find object file " + inputFile.getPath()), "File Not Found",
          JOptionPane.WARNING_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error reading file: " + e
          .toString()), "File IO Error", JOptionPane.WARNING_MESSAGE);
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, ("Error reading file: " + e
          .toString()), "File IO Error", JOptionPane.WARNING_MESSAGE);
    }
  }
}