package simse.codegenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

public class CodeGeneratorUtils {
	public CodeGeneratorUtils() {
	}

	public static String getUpperCaseLeading(String s) {
		return (s.substring(0, 1).toUpperCase() + s.substring(1));
	}

	public static String getLowerCaseLeading(String s) {
		return (s.substring(0, 1).toLowerCase() + s.substring(1));
	}

	/*
	 * copies the directory whose path is dirToCopyPath, along with all its files
	 * (not directories), to the directory whose path is destinationDirPath
	 */
	public static void copyDir(String dirToCopyPath, String destinationDirPath) 
	{
		File dirToCopy = new File(dirToCopyPath);
		File destinationDir = new File(destinationDirPath);
		destinationDir.mkdir();

		String[] files = dirToCopy.list();
		try {
			// go through each file:
			for (String s : files) {
				File inFile = new File(dirToCopyPath, s);
				if (inFile.isFile()) { // not a directory
					File outFile = new File(destinationDir, s);
					if (outFile.exists()) {
						outFile.delete();
					}
					FileInputStream inStream = new FileInputStream(inFile);
					FileOutputStream outStream = new FileOutputStream(outFile);
					boolean eof = false;
					while (!eof) { // not end of file
						int b = inStream.read();
						if (b == -1) {
							eof = true;
						} else {
							outStream.write(b); // write it to the other file
						}
					}
					inStream.close();
					outStream.close();
				} else { // is a directory
					// recurse:
					copyDir(inFile.getPath(), new String(destinationDirPath + "\\" + 
							inFile.getName()));
				}
			}
		} catch (FileNotFoundException e) {
			JOptionPane
					.showMessageDialog(null, ("Cannot find directory to copy: *"
							+ dirToCopyPath + "*"), "File Not Found",
							JOptionPane.WARNING_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, ("Error reading file: " + e
					.toString()), "File IO Error", JOptionPane.WARNING_MESSAGE);
		}
	}
}