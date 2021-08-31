/*
 * This class is for loading the ModelOptions from a file into memory
 */

package simse.modelbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ModelOptionsFileManipulator {
  private ModelOptions options;

  public ModelOptionsFileManipulator(ModelOptions options) {
    this.options = options;
  }

  /*
   * loads the mdl file into memory, filling the "options" data structure with
   * data from the file
   */
  public void loadFile(File inputFile) { 
    options.clearAll();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      boolean foundBeginningOfModelOptions = false;
      while (!foundBeginningOfModelOptions) {
        String currentLine = reader.readLine();
        if (currentLine == null) {
          break;
        } else if (currentLine.equals(
        		ModelFileManipulator.BEGIN_MODEL_OPTIONS_TAG)) {
          foundBeginningOfModelOptions = true;
          currentLine = reader.readLine();
          
          // everyone stop what you're doing option:
          options.setEveryoneStopOption(Boolean.parseBoolean(currentLine));
          
          currentLine = reader.readLine();
          if (currentLine.equals(ModelFileManipulator.END_MODEL_OPTIONS_TAG)) { 
          	// old format
            break;
          } else { // new format 2/2/07 that includes icon and code gen dirs
            // icon dir:
            if (!currentLine.equals(ModelFileManipulator.EMPTY_VALUE)) {
              options.setIconDirectory(new File(currentLine));
            }
            
            // code gen dir:
            currentLine = reader.readLine();
            if (!currentLine.equals(ModelFileManipulator.EMPTY_VALUE)) {
              options.setCodeGenerationDestinationDirectory(
                  new File(currentLine));
            }
            
            currentLine = reader.readLine();
            if (currentLine.equals(
            		ModelFileManipulator.END_MODEL_OPTIONS_TAG)) { // old format
              break;
            } else { // new format 9/12/07 that includes expl tool access option
//            	options.setExplanatoryToolAccessOption((
//            			Boolean.parseBoolean(currentLine)));
            	// commented out 1/28/08 when implementing multiple timelines 
            	// browser
            }
            
            currentLine = reader.readLine();
            if (currentLine.equals(
            		ModelFileManipulator.END_MODEL_OPTIONS_TAG)) { // old format
              break;
            } else { // new format 10/2/07 that includes branching option
//            	options.setAllowBranchingOption((
//            			Boolean.parseBoolean(currentLine)));
            	// commented out 1/28/08 when implementing multiple timelines 
            	// browser
            }
          }
        }
      }
      reader.close();
    } catch (FileNotFoundException e) {
      JOptionPane.showMessageDialog(null,
          ("Cannot find .mdl file " + inputFile.getPath()), "File Not Found",
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