/*
 * This class implements a filer for a JFileChooser so that only *.mdl files are
 * shown (Partially copied from a Java tutorial)
 */

package simse.modelbuilder;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MDLFileFilter extends FileFilter {
	
  //Accept all directories and all *.mdl files.
  public boolean accept(File f) {
    String fileName = f.getName();
    if (f.isDirectory()) {
      return true;
    } else {
      if (fileName.length() >= 5) {
        String extension = fileName.substring(fileName.length() - 3);
        if (extension != null) {
          return extension.equals("mdl");
        }
        return false;
      }
      return false;
    }
  }

  //The description of this filter
  public String getDescription() {
    return "SimSE Model Files (*.mdl)";
  }
}