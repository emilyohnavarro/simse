/*
 * This class represents one individual tile of the map
 */

package simse.modelbuilder.mapeditor;

import java.awt.Image;

public class TileData {
  int baseKey;
  int fringeKey;

  public TileData(int baseKey, int fringeKey) {
    this.baseKey = baseKey;
    this.fringeKey = fringeKey;
  }

  public void setBase(int b) {
    baseKey = b;
  }

  public void setFringe(int f) {
    fringeKey = f;
  }

  public Image getBase() {
    return MapData.getImage(baseKey);
  }

  public Image getFringe() {
    return MapData.getImage(fringeKey);
  }

  public int getBaseKey() {
    return baseKey;
  }

  public int getFringeKey() {
    return fringeKey;
  }

  public void setBaseKey(int b) {
    baseKey = b;
  }

  public void setFringeKey(int f) {
    fringeKey = f;
  }
}