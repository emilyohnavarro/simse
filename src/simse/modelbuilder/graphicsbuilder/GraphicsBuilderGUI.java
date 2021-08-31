package simse.modelbuilder.graphicsbuilder;

import simse.modelbuilder.ModelBuilderGUI;
import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.WarningListPane;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.rulebuilder.CreateObjectsRule;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.SimSEObject;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GraphicsBuilderGUI extends JPanel implements ActionListener {
	private ModelBuilderGUI mainGUI;

	private ModelOptions options;

	private CreatedObjects objects; // data structure for holding all of the

	// created SimSE objects
	private DefinedObjectTypes objTypes;

	private DefinedActionTypes actTypes;

	private SimSEObject objInFocus; // object currently selected

	private SopFileManipulator sopFileManip; // for generating/loading .sop files

	/* 
	 * maps SimSEObjects (keys) from the start state to the filename of the
	 * associated image file (String):
	 */
	private Hashtable<SimSEObject, String> startStateObjsToImgFilenames; 

	/* 
	 * maps SimSEObjects (keys) from the create objects rules to the filename of 
	 * the associated image file (String):
	 */
	private Hashtable<SimSEObject, String> ruleObjsToImgFilenames; 

	/* 
	 * maps ImageIcons (keys) to the filename of that image's file (String) 
	 * (values):
	 */
	private Hashtable<ImageIcon, String> imagesToFilenames; 
	
	private JList objectList; // list of objects to match pictures to

	private Vector<String> objectListData; // data for objectList

	private JButton matchButton; // button to match a picture to an object

	private JLabel selectedImage; // shows the image selected or matched

	private JPanel imagesPanel; // holds the image buttons

	private JLabel topPanelLabel;

	private JLabel bottomPanelLabel;

	private WarningListPane warningPane;

	public GraphicsBuilderGUI(ModelBuilderGUI mainGUI, ModelOptions options,
			DefinedObjectTypes objTypes, CreatedObjects objects, 
			DefinedActionTypes actTypes) {
		this.mainGUI = mainGUI;
		imagesToFilenames = new Hashtable<ImageIcon, String>();
		startStateObjsToImgFilenames = new Hashtable<SimSEObject, String>();
		ruleObjsToImgFilenames = new Hashtable<SimSEObject, String>();
		this.options = options;
		this.objects = objects;
		this.objTypes = objTypes;
		this.actTypes = actTypes;

		sopFileManip = new SopFileManipulator(options, objTypes, objects, actTypes,
				startStateObjsToImgFilenames, ruleObjsToImgFilenames);

		// Create main panel (box):
		Box mainPane = Box.createVerticalBox();
		mainPane.setPreferredSize(new Dimension(1024, 650));

		// Create top pane:
		Box topPane = Box.createVerticalBox();
		JPanel topLabelPane = new JPanel();
		topPanelLabel = new JLabel();
		topLabelPane.add(topPanelLabel);
		topPane.add(topLabelPane);
		// objectList:
		objectList = new JList();
		objectList.setVisibleRowCount(10); // make 10 items visible at a time
		objectList.setFixedCellWidth(650);
		objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		JScrollPane objectListPane = new JScrollPane(objectList);
		topPane.add(objectListPane);
		refreshObjectList();
		setupObjectListSelectionListenerStuff();

		// Create middle pane:
		JPanel middlePane = new JPanel();
		matchButton = new JButton("Match");
		matchButton.addActionListener(this);
		middlePane.add(matchButton);
		selectedImage = new JLabel();
		selectedImage.setText("No image selected");
		middlePane.add(selectedImage);

		// Create bottom pane:
		Box bottomPane = Box.createVerticalBox();
		JPanel bottomLabelPane = new JPanel();
		bottomPanelLabel = new JLabel();
		bottomLabelPane.add(bottomPanelLabel);
		bottomPane.add(bottomLabelPane);
		// Images panel:
		imagesPanel = new JPanel(new GridLayout(0, 5));
		JScrollPane imagesScrollPane = new JScrollPane(imagesPanel);
		imagesScrollPane.setPreferredSize(new Dimension(650, 300));
		bottomPane.add(imagesScrollPane);

		// Warning pane:
		warningPane = new WarningListPane();

		// Add panes and separators to main pane:
		mainPane.add(topPane);
		mainPane.add(middlePane);
		JSeparator separator2 = new JSeparator();
		separator2.setMaximumSize(new Dimension(2700, 1));
		mainPane.add(separator2);
		mainPane.add(bottomPane);
		JSeparator separator3 = new JSeparator();
		separator3.setMaximumSize(new Dimension(2900, 1));
		mainPane.add(separator3);
		mainPane.add(warningPane);
		add(mainPane);

		// make it so no file is open to begin with:
		setNoOpenFile();
		validate();
		repaint();
	}

	/*
	 * reloads the graphics from a temporary file; if resetUI is true, clears all
	 * current selections in the UI.
	 */
	public void reload(File tempFile, boolean resetUI) {
		// reload:
		generateWarnings(sopFileManip.loadFile(tempFile));
		if (resetUI) {
			// reset UI stuff:
			refreshImagePane();
			topPanelLabel.setText("Choose an object:");
			bottomPanelLabel.setText("Choose an image:");
			selectedImage.setEnabled(true);
			objectList.setEnabled(true);
			selectedImage.setIcon(null);
			refreshObjectList();
			clearObjectInFocus();
			matchButton.setEnabled(false);
		}
	}

	/*
	 * displays warnings of errors found during checking for inconsistencies
	 */
	private void generateWarnings(Vector<String> warnings) { 		
		if (warnings.size() > 0) { // there is at least 1 warning
			warningPane.setWarnings(warnings);
		}
	}

	public Hashtable<SimSEObject, String> getStartStateObjsToImages() {
		return startStateObjsToImgFilenames;
	}

	public Hashtable<SimSEObject, String> getRuleObjsToImages() {
		return ruleObjsToImgFilenames;
	}

	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source == matchButton) {
			if (objInFocus != null) { // there is an object selected
				if (selectedImage.getIcon() != null) { // there is an image selected
					// match the image and the object in the hashtable:
					String imgFilename = imagesToFilenames
							.get((ImageIcon) selectedImage.getIcon());
					if (objects.getAllObjects().contains(objInFocus)) { // a start state
																															// object
						startStateObjsToImgFilenames.put(objInFocus, imgFilename);
					}  else { // a rule-generated object
						ruleObjsToImgFilenames.put(objInFocus, imgFilename);
					}
					mainGUI.setFileModSinceLastSave();
				}  else {
					JOptionPane.showMessageDialog(null,
							"Please choose an image to match", "Match Unsuccessful",
							JOptionPane.WARNING_MESSAGE);
				}
			}  else {
				JOptionPane.showMessageDialog(null, "Please choose an object to match",
						"Match Unsuccessful", JOptionPane.WARNING_MESSAGE);
			}
		}  else if (source instanceof JButton) { // one of the image buttons
			JButton butt = (JButton) source;
			selectedImage.setText(null);
			selectedImage.setIcon((ImageIcon) butt.getIcon());
			if (objectList.isSelectionEmpty() == false) { // an object is selected
				matchButton.setEnabled(true);
			}  else { // no object selected
				matchButton.setEnabled(false);
			}
		}
	}

	private boolean isImageFile(String filename) {
		if (filename.indexOf('.') >= 0) {
			String extension = filename.substring(filename.lastIndexOf('.') + 1);
			if ((extension.equalsIgnoreCase("png"))
					|| (extension.equalsIgnoreCase("gif"))
					|| (extension.equalsIgnoreCase("jpg"))
					|| (extension.equalsIgnoreCase("jpeg"))) {
				return true;
			}  else {
				return false;
			}
		}  else {
			return false;
		}
	}

	private void refreshObjectList() {
		objectListData = new Vector<String>();
		Vector<SimSEObject> currentObjs = new Vector<SimSEObject>();
		currentObjs.addAll(objects.getAllObjects());

		// append all objects created by CreateObjectsRules to the currentObjs
		// Vector:
		Vector<ActionType> actions = actTypes.getAllActionTypes();
		for (int i = 0; i < actions.size(); i++) {
			ActionType act = actions.elementAt(i);
			Vector<CreateObjectsRule> rules = act.getAllCreateObjectsRules();
			for (int j = 0; j < rules.size(); j++) {
				CreateObjectsRule rule = rules.elementAt(j);
				Vector<SimSEObject> objs = rule.getAllSimSEObjects();
				for (int k = 0; k < objs.size(); k++) {
					currentObjs.add(objs.elementAt(k));
				}
			}
		}

		// go through all objects and add their info to the list
		for (int i = 0; i < currentObjs.size(); i++) {
			StringBuffer data = new StringBuffer();
			SimSEObject tempObj = currentObjs.elementAt(i);
			data.append(tempObj.getSimSEObjectType().getName()
					+ " "
					+ SimSEObjectTypeTypes
							.getText(tempObj.getSimSEObjectType().getType()) + " ");
			if (tempObj.getSimSEObjectType().hasKey()
					&& tempObj.getKey().isInstantiated()) { // has key and it is
																									// instantiated
				data.append(tempObj.getKey().getValue().toString());
				objectListData.add(data.toString());
			}
		}
		objectList.setListData(objectListData);
	}

	private void refreshImagePane() {
		imagesPanel.removeAll();
		imagesToFilenames.clear();
		File imageDir = options.getIconDirectory();
		if ((imageDir != null)
				&& ((!imageDir.exists()) || (!imageDir.isDirectory()))) {
			String warning = new String("Cannot find icon directory "
					+ imageDir.getAbsolutePath());
			Vector<String> warningVector = new Vector<String>();
			warningVector.add(warning);
			generateWarnings(warningVector);
		} else if ((imageDir != null) && (imageDir.exists())) { // image dir exists
			// list out all picture filenames and store in pictureFiles[]:
			String pictureFiles[] = imageDir.list(); 
			for (int i = 0; i < pictureFiles.length; i++) {
				if (isImageFile(pictureFiles[i])) { // to prevent non-image files from
																						// being loaded
					ImageIcon img = new ImageIcon(imageDir.getPath().concat(
							"\\" + pictureFiles[i]));
					imagesToFilenames.put(img, pictureFiles[i]);
					JButton button = new JButton(img);
					button.addActionListener(this);
					imagesPanel.add(button);
				}
			}
			validate();
			repaint();
		}
	}

	private void clearObjectInFocus() {
		objInFocus = null;
		matchButton.setEnabled(false);
	}

	public void setNewOpenFile(File f) {
		refreshImagePane();
		topPanelLabel.setText("Choose an object:");
		bottomPanelLabel.setText("Choose an image:");
		selectedImage.setEnabled(true);
		objectList.setEnabled(true);
		warningPane.clearWarnings();
		if (f.exists()) { // file has been saved before
			reload(f, true);
		}
	}

	public void setNoOpenFile() { // makes it so there's no open file in the GUI
		selectedImage.setIcon(null);
		selectedImage.setText("No image selected");
		selectedImage.setEnabled(false);
		imagesToFilenames.clear();
		startStateObjsToImgFilenames.clear();
		ruleObjsToImgFilenames.clear();
		refreshObjectList();
		clearObjectInFocus();
		refreshImagePane();
		topPanelLabel.setText("No File Opened");
		bottomPanelLabel.setText("");
		objectList.setEnabled(false);
		warningPane.clearWarnings();
	}

	// enables match button whenever both an object and an image are selected
	private void setupObjectListSelectionListenerStuff() { 
		// Copied from a Java tutorial:
		ListSelectionModel rowSM = objectList.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				// Ignore extra messages.
				if (lse.getValueIsAdjusting())
					return;
				ListSelectionModel lsm = (ListSelectionModel) lse.getSource();
				if (lsm.isSelectionEmpty() == false) {
					// get the selected object information from the selected item in
					// objectList:
					String selectedItem = (String) objectList.getSelectedValue();
					String ssObjType = selectedItem.substring(0, selectedItem
							.indexOf(' '));
					// take off SimSEObjectTypeType:
					String temp = selectedItem.substring(selectedItem.indexOf(' ') + 1); 
					String ssObjTypeType = temp.substring(0, temp.indexOf(' '));
					String keyAttVal = temp.substring(temp.indexOf(' ') + 1);
					int metaType = SimSEObjectTypeTypes
							.getIntRepresentation(ssObjTypeType);
					// set the object in focus:
					SimSEObjectType objType = objTypes.getObjectType(metaType, ssObjType); 
					if (objType.hasKey()) { // objType has a key attribute specified
						if (objType.getKey().getType() == AttributeTypes.INTEGER) {
							try {
								Integer val = new Integer(keyAttVal);
								// try to get the object from the CreatedObjects:
								objInFocus = objects.getObject(metaType, ssObjType, val);
								if (objInFocus == null) { // not a start state object
									// get it from the rules:
									objInFocus = getObjectFromRules(metaType, ssObjType, val);
								}
							} catch (NumberFormatException e) {
								System.out.println(e);
							}
						} else if (objType.getKey().getType() == AttributeTypes.DOUBLE) {
							try {
								Double val = new Double(keyAttVal);
								// try to get the object from the CreatedObjects:
								objInFocus = objects.getObject(metaType, ssObjType, val);
								if (objInFocus == null) { // not a start state object
									// get it from the rules:
									objInFocus = getObjectFromRules(metaType, ssObjType, val);
								}
							} catch (NumberFormatException e) {
								System.out.println(e);
							}
						} else if (objType.getKey().getType() == AttributeTypes.BOOLEAN) {
							// try to get the object from the CreatedObjects:
							objInFocus = objects.getObject(metaType, ssObjType, new Boolean(
									keyAttVal));
							if (objInFocus == null) { // not a start state object
								// get it from the rules:
								objInFocus = getObjectFromRules(metaType, ssObjType,
										new Boolean(keyAttVal));
							}
						} else { // string
							// try to get the object from the CreatedObjects:
							objInFocus = objects.getObject(metaType, ssObjType, keyAttVal);
							if (objInFocus == null) { // not a start state object
								// get it from the rules:
								objInFocus = getObjectFromRules(metaType, ssObjType, keyAttVal);
							}
						}
					}

					if (startStateObjsToImgFilenames.containsKey(objInFocus)) { 
						// object in focus is a start state object and has an image matched
						// to it
						// display the associated image:
						selectedImage.setText(null);
						ImageIcon img = getImage((String) startStateObjsToImgFilenames
								.get(objInFocus)); // get the image from the filename
						selectedImage.setIcon(img);
					} else if (ruleObjsToImgFilenames.containsKey(objInFocus)) { 
						// object in focus is a rule-generated object and has an image
						// matched to it
						// display the associated image:
						selectedImage.setText(null);
						ImageIcon img = getImage((String) ruleObjsToImgFilenames
								.get(objInFocus)); // get the image from the filename
						selectedImage.setIcon(img);
					} else { // object in focus is not matched to an image
						selectedImage.setIcon(null);
						selectedImage.setText("No image selected");
					}
					if ((selectedImage.getText() == null)
							|| (selectedImage.getText().length() == 0)) { // image selected
						// enable match button:
						matchButton.setEnabled(true);
					} else { // no image selected
						// disable match button:
						matchButton.setEnabled(false);
					}
				}
			}
		});
	}

	// returns the image associated with the specified filename
	private ImageIcon getImage(String filename) { 
		for (Enumeration<ImageIcon> imgs = 
			imagesToFilenames.keys(); imgs.hasMoreElements();) {
			ImageIcon icon = imgs.nextElement();
			if (imagesToFilenames.get(icon).equals(filename)) {
				return icon;
			}
		}
		return null;
	}

	/*
	 * returns the specified object if it is generated by one of the 
	 * CreateObjectsRules. Otherwise, returns null.
	 */
	private SimSEObject getObjectFromRules(int type, String simSEObjectTypeName,
			Object keyAttValue) { 
		Vector<ActionType> actions = actTypes.getAllActionTypes();
		for (int i = 0; i < actions.size(); i++) {
			ActionType act = actions.elementAt(i);
			Vector<CreateObjectsRule> rules = act.getAllCreateObjectsRules();
			for (int j = 0; j < rules.size(); j++) {
				CreateObjectsRule rule = rules.elementAt(j);
				Vector<SimSEObject> objs = rule.getAllSimSEObjects();
				for (int k = 0; k < objs.size(); k++) {
					SimSEObject tempObj = objs.elementAt(k);
					if ((tempObj.getSimSEObjectType().getType() == type)
							&& (tempObj.getName().equals(simSEObjectTypeName))
							&& (tempObj.getKey().isInstantiated())
							&& (tempObj.getKey().getValue().equals(keyAttValue))) {
						return tempObj;
					}
				}
			}
		}
		return null;
	}
}