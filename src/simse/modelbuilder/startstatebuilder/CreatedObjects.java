/*
 * This class is the data structure for holding the objects that are currently
 * instantiated
 */

package simse.modelbuilder.startstatebuilder;

import simse.modelbuilder.objectbuilder.SimSEObjectType;

import java.util.Vector;

public class CreatedObjects {
  private Vector<SimSEObject> objs; // Vector of SimSEObjects
  private String startingNarrative; // starting narrative

  public CreatedObjects(Vector<SimSEObject> v, String s) {
    objs = v;
    startingNarrative = s;
  }

  public CreatedObjects() {
    objs = new Vector<SimSEObject>();
    startingNarrative = new String();
  }

  public Vector<SimSEObject> getAllObjects() {
    return objs;
  }

  // returns a Vector of SimSEObjects, all of them having the type specified
  public Vector<SimSEObject> getAllObjectsOfType(SimSEObjectType type) { 
    Vector<SimSEObject> v = new Vector<SimSEObject>();
    for (int i = 0; i < objs.size(); i++) {
      SimSEObject obj = objs.elementAt(i);
      if (obj.getSimSEObjectType() == type) {
        v.add(obj);
      }
    }
    return v;
  }
  
  /*
   * adds an object to the end of the Vector
   */
  public void addObject(SimSEObject newObject) {
//    boolean otherObjsOfSameType;
//    int numObjectsOfSameType = 
//      getAllObjectsOfType(newObject.getSimSEObjectType()).size();
//    if (numObjectsOfSameType > 0) {
//      otherObjsOfSameType = true;
//    }
//    else {
//      otherObjsOfSameType = false;
//    }
//    
//	  // insert at correct alpha order:
//    int numObjsSameTypeEncountered = 0;
//    for (int i = 0; i < objs.size(); i++) {
//      SimSEObject tempObj = (SimSEObject) objs.elementAt(i);
//      
//      if (otherObjsOfSameType) {
//      // Check SimSEObjectType name:
//	      if (newObject.getName().equals(tempObj.getName())) {
//	        numObjsSameTypeEncountered++;
//	        // same SimSEObjectType, so check key att val:
//	        if (newObject.getKey().getValue().toString().compareToIgnoreCase(
//	            tempObj.getKey().getValue().toString()) < 0) {
//	          // should be inserted before tempObj
//	          objs.insertElementAt(newObject, i);
//	          return;
//	        }
//	        if (numObjsSameTypeEncountered == numObjectsOfSameType) { // last 
//	          																												// object 
//	          																												// of same 
//	          																												// type
//	          // insert after last object of same type
//	          objs.insertElementAt(newObject, (i + 1));
//	          return;
//	        }
//	      }
//      }
//      else { // no other objects of same type
//        // just need to compare SimSEObjectType name:
//  	    if (newObject.getName().compareToIgnoreCase(tempObj.getName()) < 0) {
//  	      // should be inserted before tempObj
//  	      objs.insertElementAt(newObject, i);
//  	      return;
//  	    }
//      }
//    }
//    
//	  // only reaches here if objs is empty or "newObject" should be placed at 
//	  // the end
	  objs.add(newObject);
  }
  
  /*
   * adds an object at the specified position
   */
  public void addObject(SimSEObject obj, int position) {
    objs.insertElementAt(obj, position);
  }
  
  /*
   * returns the object in the data structure with the specified type, name, and
   * key attribute value
   */
  public SimSEObject getObject(int type, String name, Object keyAttValue) { 
    for (int i = 0; i < objs.size(); i++) {
      SimSEObject tempObj = objs.elementAt(i);
      if ((tempObj.getSimSEObjectType().getType() == type)
          && (tempObj.getName().equals(name))
          && (tempObj.getKey().isInstantiated())
          && (tempObj.getKey().getValue().equals(keyAttValue))) {
        return tempObj;
      }
    }
    return null;
  }

  /*
   * removes the object with the specified SimSEObjectTypeType ("type"),
   * SimSEObjectType name ("name"), and key attribute value ("keyAttValue"),
   * and returns the position from which it was removed, or -1 if not found
   */
  public int removeObject(int type, String name, Object keyAttValue) {
    for (int i = 0; i < objs.size(); i++) {
      SimSEObject tempObj = objs.elementAt(i);
      if ((tempObj.getSimSEObjectType().getType() == type)
          && (tempObj.getName().equals(name))
          && (tempObj.getKey().isInstantiated())
          && (tempObj.getKey().getValue().equals(keyAttValue))) {
        objs.remove(i);
        return i;
      }
    }
    return -1;
  }

  public void removeObject(SimSEObject obj) {
    objs.remove(obj);
  }
  
  public int getIndexOf(SimSEObject obj) {
    for (int i = 0; i < objs.size(); i++) {
      SimSEObject tempObj = objs.elementAt(i);
      if (tempObj.getName().equals(obj.getName())) {
        return objs.indexOf(obj);
      }
    }
    return -1;
  }

  // removes all objects
  public void clearAll() { 
    objs.removeAllElements();
  }

  public String getStartingNarrative() {
    return startingNarrative;
  }

  public void setStartingNarrative(String s) {
    startingNarrative = s;
  }

  // calls updateAllInstantiatedAttributes() for all created objects
  public void updateAllInstantiatedAttributes() { 
    for (int i = 0; i < objs.size(); i++) {
      SimSEObject tempObj = objs.elementAt(i);
      tempObj.updateInstantiatedAttributes();
    }
  }
}