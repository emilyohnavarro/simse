/*
 * SimSE - MapEditor Professor Andre van der Hoek Emily Oh Navarro developed by:
 * Kuan-Sung Lee (Ethan) uciethan@yahoo.com
 * 
 * co-developed by : Calvin Lee
 */

package simse.modelbuilder.mapeditor;

import simse.modelbuilder.ModelBuilderGUI;
import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.startstatebuilder.CreatedObjects;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class MapEditorMap extends SimSEMap {
  private ModelBuilderGUI mainGUI;

  private int clickedX; // x tile which was clicked
  private int clickedY; // y tile which was clicked

  private int screenX = MapData.X_MAPSIZE * MapData.TILE_SIZE + 5; //608; x
                                                                   // width of
                                                                   // the screen
  private int screenY = MapData.Y_MAPSIZE * MapData.TILE_SIZE + 54; //659; y
                                                                    // width of
                                                                    // the
                                                                    // screen

  private JPopupMenu popup;
  private PopupListener popupListener;

  JMenuItem drawComputer;
  JMenuItem drawChairT;
  JMenuItem drawChairB;
  JMenuItem drawChairL;
  JMenuItem drawChairR;
  JMenuItem drawFloor;
  JMenuItem drawTrashCanE;
  JMenuItem drawTrashCanF;
  JMenuItem drawPapers;
  JMenuItem delete;

  /* Doors submenu */
  JMenu doors;
  JMenuItem drawDoorTO;
  JMenuItem drawDoorTC;
  JMenuItem drawDoorLO;
  JMenuItem drawDoorLC;
  JMenuItem drawDoorRO;
  JMenuItem drawDoorRC;

  /* walls submenu */
  JMenu walls;
  JMenuItem drawWallsT;
  JMenuItem drawWallsB;
  JMenuItem drawWallsL;
  JMenuItem drawWallsR;
  JMenuItem drawWallsTL;
  JMenuItem drawWallsTR;
  JMenuItem drawWallsBL;
  JMenuItem drawWallsBR;

  /* tables submenu */
  JMenu tables;
  JMenuItem drawTableTL;
  JMenuItem drawTableTM;
  JMenuItem drawTableTR;
  JMenuItem drawTableBL;
  JMenuItem drawTableBM;
  JMenuItem drawTableBR;

  public MapEditorMap(ModelBuilderGUI owner, ModelOptions opts, 
      DefinedObjectTypes objTypes, CreatedObjects objs, DefinedActionTypes 
      acts, Hashtable startStateObjs, Hashtable ruleObjs) {
    super(opts, objTypes, objs, acts, startStateObjs, ruleObjs);
    mainGUI = owner;

    drawComputer = new JMenuItem("- Computer");
    drawComputer.setForeground(new Color(0, 150, 0, 255));
    drawChairT = new JMenuItem("- Chair Front");
    drawChairT.setForeground(new Color(0, 150, 0, 255));
    drawChairB = new JMenuItem("- Chair Back");
    drawChairB.setForeground(new Color(0, 150, 0, 255));
    drawChairL = new JMenuItem("- Chair Left");
    drawChairL.setForeground(new Color(0, 150, 0, 255));
    drawChairR = new JMenuItem("- Chair Right");
    drawChairR.setForeground(new Color(0, 150, 0, 255));
    drawFloor = new JMenuItem("- Floor");
    drawFloor.setForeground(new Color(0, 0, 200, 255));
    drawTrashCanE = new JMenuItem("- Trash Can Empty");
    drawTrashCanE.setForeground(new Color(0, 150, 0, 255));
    drawTrashCanF = new JMenuItem("- Trash Can Full");
    drawTrashCanF.setForeground(new Color(0, 150, 0, 255));
    drawPapers = new JMenuItem("- Papers");
    drawPapers.setForeground(new Color(0, 150, 0, 255));
    delete = new JMenuItem("- Delete");

    drawComputer.addActionListener(this);
    drawChairT.addActionListener(this);
    drawChairB.addActionListener(this);
    drawChairL.addActionListener(this);
    drawChairR.addActionListener(this);
    drawFloor.addActionListener(this);
    drawTrashCanE.addActionListener(this);
    drawTrashCanF.addActionListener(this);
    drawPapers.addActionListener(this);
    delete.addActionListener(this);

    /* Doors submenu */
    doors = new JMenu("- Doors");
    drawDoorTO = new JMenuItem("- Door Top/Bottom Open");
    drawDoorTC = new JMenuItem("- Door Top/Bottom Close");
    drawDoorLO = new JMenuItem("- Door Left Open");
    drawDoorLC = new JMenuItem("- Door Left Close");
    drawDoorRO = new JMenuItem("- Door Right Open");
    drawDoorRC = new JMenuItem("- Door Right Close");

    doors.add(drawDoorTO);
    doors.add(drawDoorTC);
    doors.add(drawDoorLO);
    doors.add(drawDoorLC);
    doors.add(drawDoorRO);
    doors.add(drawDoorRC);

    drawDoorTO.addActionListener(this);
    drawDoorTC.addActionListener(this);
    drawDoorLO.addActionListener(this);
    drawDoorLC.addActionListener(this);
    drawDoorRO.addActionListener(this);
    drawDoorRC.addActionListener(this);

    doors.setForeground(new Color(0, 0, 200, 255));
    Component[] dsub = doors.getMenuComponents();
    for (int i = 0; i < dsub.length; i++)
      dsub[i].setForeground(new Color(0, 0, 200, 255));

    /* walls submenu */
    walls = new JMenu("- Walls");

    drawWallsT = new JMenuItem("- Wall Top");
    drawWallsB = new JMenuItem("- Wall Bottom");
    drawWallsL = new JMenuItem("- Wall Left");
    drawWallsR = new JMenuItem("- Wall Right");
    drawWallsTL = new JMenuItem("- Wall Top Left");
    drawWallsTR = new JMenuItem("- Wall Top Right");
    drawWallsBL = new JMenuItem("- Wall Bottom Left");
    drawWallsBR = new JMenuItem("- Wall Bottom Right");

    walls.add(drawWallsTL);
    walls.add(drawWallsT);
    walls.add(drawWallsTR);
    walls.addSeparator();
    walls.add(drawWallsL);
    walls.add(drawWallsR);
    walls.addSeparator();
    walls.add(drawWallsBL);
    walls.add(drawWallsB);
    walls.add(drawWallsBR);

    drawWallsT.addActionListener(this);
    drawWallsB.addActionListener(this);
    drawWallsL.addActionListener(this);
    drawWallsR.addActionListener(this);
    drawWallsTL.addActionListener(this);
    drawWallsTR.addActionListener(this);
    drawWallsBL.addActionListener(this);
    drawWallsBR.addActionListener(this);

    walls.setForeground(new Color(0, 0, 200, 255));
    Component[] wsub = walls.getMenuComponents();
    for (int i = 0; i < wsub.length; i++)
      wsub[i].setForeground(new Color(0, 0, 200, 255));

    /* tables submenu */
    tables = new JMenu("- Tables");

    drawTableTL = new JMenuItem("- Table Top Left");
    drawTableTM = new JMenuItem("- Table Top Middle");
    drawTableTR = new JMenuItem("- Table Top Right");
    drawTableBL = new JMenuItem("- Table Bottom Left");
    drawTableBM = new JMenuItem("- Table Bottom Middle");
    drawTableBR = new JMenuItem("- Table Bottom Right");

    tables.add(drawTableTL);
    tables.add(drawTableTM);
    tables.add(drawTableTR);

    tables.addSeparator();
    tables.add(drawTableBL);
    tables.add(drawTableBM);
    tables.add(drawTableBR);

    drawTableTL.addActionListener(this);
    drawTableTM.addActionListener(this);
    drawTableTR.addActionListener(this);
    drawTableBL.addActionListener(this);
    drawTableBM.addActionListener(this);
    drawTableBR.addActionListener(this);

    tables.setForeground(new Color(0, 0, 200, 255));
    Component[] tsub = tables.getMenuComponents();
    for (int i = 0; i < tsub.length; i++)
      tsub[i].setForeground(new Color(0, 0, 200, 255));

    popup = new JPopupMenu();
    popupListener = new PopupListener(popup);
    addMouseListener(popupListener);

    addMouseListener(this);
    // create popup menu
    createPopupMenu();
    setNoOpenFile();
    setPreferredSize(new Dimension(screenX, screenY));
  }

  public ArrayList<UserData> getUserDatas() {
    return sopUsers;
  }

  public TileData[][] getMap() {
    return mapRep;
  }

  public void setNoOpenFile() {
    // reset the SOP Objects and remove it from the popup menu
    sopUsers.clear();
    createPopupMenu();
    popupListener.setEnabled(false);

    for (int i = 0; i < MapData.Y_MAPSIZE; i++) {
      for (int j = 0; j < MapData.X_MAPSIZE; j++) {
        mapRep[j][i].setBase(MapData.TILE_GRID);
        mapRep[j][i].setFringe(MapData.TRANSPARENT);
      }
    }
  }

  public Vector<String> loadFile(File inputFile) {
    Vector<String> v = super.loadFile(inputFile);
    createPopupMenu();
    return v;
  }

  public void setNewOpenFile() {
    // reset the SOP Objects and remove it from the popup menu
    sopUsers.clear();
    createPopupMenu();
    popupListener.setEnabled(true);

    for (int i = 0; i < MapData.Y_MAPSIZE; i++) {
      for (int j = 0; j < MapData.X_MAPSIZE; j++) {
        mapRep[j][i].setBase(MapData.TILE_GRID);
        mapRep[j][i].setFringe(MapData.TRANSPARENT);
      }
    }
  }

  private void createPopupMenu() {
    popup.removeAll();
    popup.add(walls);
    popup.add(doors);
    popup.add(tables);
    popup.add(drawFloor);
    popup.addSeparator();
    popup.add(drawComputer);
    popup.add(drawChairT);
    popup.add(drawChairB);
    popup.add(drawChairL);
    popup.add(drawChairR);
    popup.add(drawTrashCanE);
    popup.add(drawTrashCanF);
    popup.add(drawPapers);
    popup.addSeparator();

    // add any existing sop objects to menu
    for (int i = 0; i < sopUsers.size(); i++) {
      UserData tmp = sopUsers.get(i);

      if (i == ssObjCount) { //&& i < sopUsers.size()) // now on rule objects
        tmp.getUserMenu().setForeground(new Color(200, 0, 0, 255));
        popup.addSeparator();
      }
      popup.add(tmp.getUserMenu());
    }

    popup.addSeparator();
    popup.add(delete);
    popup.repaint();
    validate();
    repaint();
  }

  public void paintComponent(Graphics g) {
    for (int i = 0; i < MapData.Y_MAPSIZE; i++) {
      for (int j = 0; j < MapData.X_MAPSIZE; j++) {
        g.drawImage(mapRep[j][i].getBase(), j * MapData.TILE_SIZE, i
            * MapData.TILE_SIZE, this);
        g.drawImage(mapRep[j][i].getFringe(), j * MapData.TILE_SIZE, i
            * MapData.TILE_SIZE, this);
      }
    }

    // draw employees:
    for (int i = 0; i < sopUsers.size(); i++) {
      UserData tmp = sopUsers.get(i);
      if (tmp.isDisplayed()) {
        g.drawImage(tmp.getUserIcon(), tmp.getXLocation() * MapData.TILE_SIZE,
            tmp.getYLocation() * MapData.TILE_SIZE, this);

        // if is a rule object, shade it blue to differentiate it:
        if (!tmp.isActivated()) {
          g.setColor(new Color(0, 100, 200, 60));
          g.fillRect(tmp.getXLocation() * MapData.TILE_SIZE + 1, tmp
              .getYLocation()
              * MapData.TILE_SIZE + 1, MapData.TILE_SIZE - 1,
              MapData.TILE_SIZE - 1);
          g.setColor(new Color(250, 0, 250, 255));
          g.drawRect(tmp.getXLocation() * MapData.TILE_SIZE, tmp.getYLocation()
              * MapData.TILE_SIZE, MapData.TILE_SIZE, MapData.TILE_SIZE);
          g.setColor(Color.black);
        }
      }
    }
  }

  public void mouseReleased(MouseEvent me) {
    clickedX = (me.getX()) / MapData.TILE_SIZE; 
    clickedY = (me.getY()) / MapData.TILE_SIZE; 
  }

  public void mousePressed(MouseEvent me) {
    createPopupMenu(); // refresh menu
  }

  // all actions for the mapeditor tiles
  public void popupMenuActions(JMenuItem source) {
    boolean validOption = true;
    // clicked out of boundaries, ignore
    if (clickedX > (MapData.X_MAPSIZE - 1)
        || clickedY > (MapData.Y_MAPSIZE - 1))
      return;
    if (source == drawComputer)
      mapRep[clickedX][clickedY].setFringe(MapData.TILE_COMPUTER);
    else if (source == drawChairT)
      mapRep[clickedX][clickedY].setFringe(MapData.TILE_CHAIRT);
    else if (source == drawChairB)
      mapRep[clickedX][clickedY].setFringe(MapData.TILE_CHAIRB);
    else if (source == drawChairL)
      mapRep[clickedX][clickedY].setFringe(MapData.TILE_CHAIRL);
    else if (source == drawChairR)
      mapRep[clickedX][clickedY].setFringe(MapData.TILE_CHAIRR);
    else if (source == drawFloor)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_FLOOR);
    else if (source == drawTrashCanE)
      mapRep[clickedX][clickedY].setFringe(MapData.TILE_TRASHCANE);
    else if (source == drawTrashCanF)
      mapRep[clickedX][clickedY].setFringe(MapData.TILE_TRASHCANF);
    else if (source == drawPapers)
      mapRep[clickedX][clickedY].setFringe(MapData.TILE_PAPERS);
    else if (source == delete) {
      // deletes employee if on tile
      for (int i = 0; i < sopUsers.size(); i++) {
        UserData tmp = sopUsers.get(i);

        if (tmp.checkXYLocations(clickedX, clickedY))
          tmp.setDisplayed(false);
      }
      mapRep[clickedX][clickedY].setBase(MapData.TILE_GRID);
      mapRep[clickedX][clickedY].setFringe(MapData.TRANSPARENT);
    } else if (source == drawDoorTO)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_DOOR_TO);
    else if (source == drawDoorTC)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_DOOR_TC);
    else if (source == drawDoorLO)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_DOOR_LO);
    else if (source == drawDoorLC)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_DOOR_LC);
    else if (source == drawDoorRO)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_DOOR_RO);
    else if (source == drawDoorRC)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_DOOR_RC);

    else if (source == drawWallsT)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_WALL_T);
    else if (source == drawWallsB)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_WALL_B);
    else if (source == drawWallsL)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_WALL_L);
    else if (source == drawWallsR)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_WALL_R);

    else if (source == drawWallsTL)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_WALL_TL);
    else if (source == drawWallsTR)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_WALL_TR);
    else if (source == drawWallsBL)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_WALL_BL);
    else if (source == drawWallsBR)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_WALL_BR);

    else if (source == drawTableTL)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_STABLE_TL);
    else if (source == drawTableTM)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_STABLE_TM);
    else if (source == drawTableTR)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_STABLE_TR);

    else if (source == drawTableBL)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_STABLE_BL);
    else if (source == drawTableBM)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_STABLE_BM);
    else if (source == drawTableBR)
      mapRep[clickedX][clickedY].setBase(MapData.TILE_STABLE_BR);

    // enables the employee to be drawn on map
    else if (source.getText().startsWith("- Employee")) {
      boolean userMatched = false;
      boolean tileIsOccupied = false;
      UserData user = null;

      for (int i = 0; i < sopUsers.size(); i++) {
        UserData tmp = sopUsers.get(i);

        // a employee exists on the current tile so prevent it from being placed
        // there:
        if (tmp.isDisplayed() && tmp.checkXYLocations(clickedX, clickedY)) {
          JOptionPane.showMessageDialog(null, tmp.getName()
              + " currently resides on this tile, please choose another.");
          i = sopUsers.size();
          tileIsOccupied = true;
        } else if (source.getText().equals(tmp.getName())) {
          userMatched = true;
          user = tmp;
        }
      }

      // user to be placed exists and is not on an occupied tile, place him:
      if (userMatched && !tileIsOccupied) {
        user.setDisplayed(true);
        user.setXYLocations(clickedX, clickedY);
      }
    } else {
      validOption = false;
    }

    if (validOption)
      mainGUI.setFileModSinceLastSave();
  }

  // dealing with actions generated by menu bar or popup menu
  public void actionPerformed(ActionEvent e) {
    JMenuItem source = (JMenuItem) (e.getSource());

    // all popupmenu options start with a '-' to differentiate betw menubar and
    // popup actions:
    if (source.getText().startsWith("-")) {
      popupMenuActions(source);
    }
    repaint();
  }
}