/*
 * This class is responsible for generating all of the code for the Branch
 * class in the explanatory tool
 */

package simse.codegenerator.explanatorytoolgenerator;

import simse.codegenerator.CodeGeneratorConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class BranchGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into

  public BranchGenerator(File directory) {
    this.directory = directory;
  }

  public void generate() {
    File branchFile = new File(directory,
        ("simse\\explanatorytool\\Branch.java"));
    if (branchFile.exists()) {
      branchFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(branchFile);
      writer
          .write("/* File generated by: simse.codegenerator.explanatorytoolgenerator.BranchGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.explanatorytool;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import simse.state.State;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public class Branch {");
      writer.write(NEWLINE);
      writer.write("private String name; // name of this branch");
      writer.write(NEWLINE);
      writer.write("private int startTick; // tick that this branch started");
      writer.write(NEWLINE);
      writer.write("private int endTick; // tick that this branch ended (or last tick of branch, if branch is still ongoing)");
      writer.write(NEWLINE);
      writer.write("private Branch root; // Branch that this branch stemmed from (null if root)");
      writer.write(NEWLINE);
      writer.write("private String score; // score (null if no score given)");
      writer.write(NEWLINE);
      writer.write("private boolean closed; // whether or not this branch has been closed");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public Branch(String name, int start, int end, Branch root, String score) {");
      writer.write(NEWLINE);
      writer.write("this.name = name;");
      writer.write(NEWLINE);
      writer.write("this.startTick = start;");
      writer.write(NEWLINE);
      writer.write("this.endTick = end;");
      writer.write(NEWLINE);
      writer.write("this.root = root;");
      writer.write(NEWLINE);
      writer.write("this.score = score;");
      writer.write(NEWLINE);
      writer.write("closed = false;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public String getName() {");
      writer.write(NEWLINE);
      writer.write("return name;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public int getStartTick() {");
      writer.write(NEWLINE);
      writer.write("return startTick;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public int getEndTick() {");
      writer.write(NEWLINE);
      writer.write("return endTick;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public void setEndTick(int end) {");
      writer.write(NEWLINE);
      writer.write("endTick = end;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public Branch getRoot() {");
      writer.write(NEWLINE);
      writer.write("return root;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public String getScore() {");
      writer.write(NEWLINE);
      writer.write("return score;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public void setScore(String score) {");
      writer.write(NEWLINE);
      writer.write("this.score = score;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public boolean isClosed() {");
      writer.write(NEWLINE);
      writer.write("return closed;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public void setClosed() {");
      writer.write(NEWLINE);
      writer.write("closed = true;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("public void update(State state) {");
      writer.write(NEWLINE);
      writer.write("endTick = state.getClock().getTime();");
      writer.write(NEWLINE);
      writer.write("score = String.valueOf(state.getScore());");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);

      writer.write(CLOSED_BRACK);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + branchFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}
