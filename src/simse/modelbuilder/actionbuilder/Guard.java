/* This class defines the guards like at least, at most, exactly, etc. */

package simse.modelbuilder.actionbuilder;

public class Guard {
  public static final int AT_LEAST = 0;
  public static final int AT_MOST = 1;
  public static final int EXACTLY = 2;
  public static final int AT_LEAST_AND_AT_MOST = 3;

  public static String getText(int guard) {
    switch (guard) {
    case 0: {
      return "at least";
    }
    case 1: {
      return "at most";
    }
    case 2: {
      return "exactly";
    }
    case 3: {
      return "at least, at most";
    }
    }
    return "Error: invalid guard";
  }

  public static int getIntRepresentation(String text) {
    if (text.equals("at least")) {
      return AT_LEAST;
    } else if (text.equals("at most")) {
      return AT_MOST;
    } else if (text.equals("exactly")) {
      return EXACTLY;
    } else if (text.equals("at least, at most")) {
      return AT_LEAST_AND_AT_MOST;
    } else {
      return -1;
    }
  }

  public static int[] getGuards() {
    int[] guards = { AT_LEAST, AT_MOST, EXACTLY, AT_LEAST_AND_AT_MOST };
    return guards;
  }
}