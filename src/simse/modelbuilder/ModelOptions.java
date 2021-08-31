/*
 * This class is the data structure for holding the options definted for a
 * model.
 */

package simse.modelbuilder;

import java.io.File;

public class ModelOptions {
  private boolean includeEveryoneStopOption; // whether or not to include an
  																	 				 // "everyone stop what you're 
  																					 // doing" option on the employees'
  																					 // menus
  private boolean expToolAccessibleDuringGame = true; // whether or not to make 
  																										// the explanatory tool 
  																										// accessible during the 
  																										// game; set to true
  																										// 1/29/08 when 
  																										// implementing multiple
  																										// timelines browser
  private boolean allowBranching = true; // whether or not to allow branching 
  																			 // multiple games through the 
  																			 // explanatory tool; set to true 
  																			 // 1/29/08 when implementing multiple
  																			 // timelines browser
  private File iconDirectory; // directory containing icons for this model
  private File codeGenerationDestinationDirectory; // directory to generate code 
  																								 // into

  public ModelOptions(boolean includeEveryoneStopOption, 
  		File iconDirectory, File codeGenerationDestinationDirectory) {
    this.includeEveryoneStopOption = includeEveryoneStopOption;
    this.iconDirectory = iconDirectory;
    this.codeGenerationDestinationDirectory = 
    	codeGenerationDestinationDirectory;
  }

  public ModelOptions() {
    includeEveryoneStopOption = false;
  }

  public boolean getEveryoneStopOption() {
    return includeEveryoneStopOption;
  }

  public void setEveryoneStopOption(boolean everyoneStop) {
    includeEveryoneStopOption = everyoneStop;
  }
  
  public boolean getExplanatoryToolAccessOption() {
    return expToolAccessibleDuringGame;
  }

//  public void setExplanatoryToolAccessOption(boolean option) {
//    expToolAccessibleDuringGame = option;
//  }
//  
  public boolean getAllowBranchingOption() {
  	return allowBranching;
  }
//  
//  public void setAllowBranchingOption(boolean option) {
//  	allowBranching = option;
//  }
  
  public File getIconDirectory() {
    return iconDirectory;
  }
  
  public void setIconDirectory(File f) {
    iconDirectory = f;
  }
  
  public File getCodeGenerationDestinationDirectory() {
    return codeGenerationDestinationDirectory;
  }
  
  public void setCodeGenerationDestinationDirectory(File f) {
    codeGenerationDestinationDirectory = f;
  }
  
  /*
   * Sets all options to default settings
   */
  public void clearAll() {
    includeEveryoneStopOption = false;
    iconDirectory = null;
    codeGenerationDestinationDirectory = null;
  }
}