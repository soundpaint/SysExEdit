/*
 * @(#)EditorFrame.java 1.00 98/02/06
 *
 * Copyright (C) 1998, 2018 Jürgen Reuter
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package see.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import see.model.MapDef;
import see.model.MapNode;

/**
 * This class implements the main window of the application.
 */
public class EditorFrame extends JFrame implements Runnable
{
  private static final long serialVersionUID = -8230511863227744503L;

  /*
   * dialog titles
   */
  private static final String CONFIRM = "Confirm";

  private static final String ERROR = "Error";

  /*
   * dialog messages
   */
  private static final String CONFIRM_CLOSE = "Data modified.  Close anyway?";

  private static final String
  CONFIRM_LDM = "Data is modified and will be lost.  Load anyway?";

  /*
   * action commands
   */
  private static final String FILE = "File";
  private static final String NEW = "New";
  private static final String LOAD = "Load...";
  private static final String SAVE = "Save";
  private static final String SAVE_AS = "Save as...";
  private static final String REQUEST = "Request";
  private static final String DUMP = "Dump";
  private static final String CLOSE = "Close";
  private static final String EXIT = "Exit";
  private static final String EDIT = "Edit";
  private static final String SELECT_ALL = "Select All";
  private static final String SELECT_NONE = "Select None";
  private static final String INVERT_SELECTION = "Invert Selection";
  private static final String COPY = "Copy";
  private static final String PASTE = "Paste";
  private static final String INCREMENT = "Increment";
  private static final String DECREMENT = "Decrement";
  private static final String UPPERMOST = "Uppermost";
  private static final String LOWERMOST = "Lowermost";
  private static final String RESET = "Reset";
  private static final String EVENT = "Event";
  private static final String OPTIONS = "Options";
  private static final String LOAD_DEVICE_MODEL = "Load Device Model...";
  private static final String DEVICE_ID = "Device ID...";
  private static final String DISPLAY_ADDRESSES = "Display Addresses";
  private static final String TOOL_TIPS = "Tool Tips";
  private static final String LAF = "Look & Feel";
  private static final String HELP = "Help";
  private static final String TUTORIAL = "Tutorial...";
  private static final String API = "API Specs...";
  private static final String LICENSE = "License...";
  private static final String ABOUT_FRAME_APPLICATION = "About Application...";
  private static final String
  ABOUT_DEVICE_MODEL_APPLICATION = "About Device Model...";

  /*
   * GUI elements
   */
  private Map map;
  private JCheckBox checkbox_dispAddr;
  private JCheckBox checkbox_toolTips;
  private JCheckBox checkbox_bd;
  private JCheckBox checkbox_br;
  private JCheckBox checkbox_md;
  private JLabel label_deviceName;
  private JLabel label_manID;
  private JLabel label_modelID;
  private JLabel label_deviceID;

  /*
   * Other instance variables
   */
  private final String filepath; // path of map def class
  private MapDef mapDef;
  private final FramesManager manager; // manages this and all other editor frames
  private File defaultLoadFile = null; // default load dialog file
  private File defaultSaveFile = null; // default save dialog file
  private int deviceID; // the device ID used for bulk requests & dumps
  private DefaultTreeModel mapModel = null;

  private EditorFrame()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @param filepath The device model to use on startup.<BR>
   *    [PENDING: not implemented yet.]
   * @param mapDef If filepath equals null, this MapDef object is used
   *    as the device model. If filepath and mapDef are both null,
   *    the user is prompted a window to manually select a device model.
   * @param manager A frames manager that manages all other editor frames.
   */
  public EditorFrame(final String filepath,
                     final MapDef mapDef,
                     final FramesManager manager)
  {
    this.filepath = filepath;
    this.mapDef = mapDef;
    this.manager = manager;
  }

  /**
   * Each EditorFrame object runs its own thread. This method is called
   * when a new EditorFrame object is started.
   */
  public void run()
  {
    final String windowID = "Window #" + manager.addFrame(this);
    setTitle(manager.getVersion() + " " + windowID);
    if (filepath != null)
      {
	System.err.println("[WARNING: loading map def by cmd line arg " +
			   "not supported by now - sorry! (ignored)]");
	System.err.flush();
	//mapDef = loadMapFrom(filepath);
      }
    if (mapDef == null)
      {
	System.out.println("[" + windowID + ": prompting for map def...]");
	System.out.flush();
	mapDef = loadDeviceModel();
      }
    if (mapDef != null)
      {
	setMap(mapDef);
	System.out.println("[" + windowID + ": initializing GUI...]");
	System.out.flush();
	initGUI();
	System.out.println("[" + windowID + ": opening window...]");
	System.out.flush();
	pack();
	setVisible(true);
	awaitDelete();
	setVisible(false);
	System.out.println("[" + windowID + ": window closed]");
	System.out.flush();
      }
    else
      {
	System.out.println("[" + windowID + " aborted]");
	System.out.flush();
      }
    dispose();
    manager.removeFrame(this);
  }

  private void setMap(final MapDef mapDef)
  {
    this.mapDef = mapDef;
    final MapNode root = mapDef.buildMap();
    if (mapModel != null)
      mapModel.setRoot(root);
    else
      mapModel = new DefaultTreeModel(root);
    if (label_deviceName != null) // gui already initialized
      updateModelInfo();
  }

  private void updateModelInfo()
  {
    label_deviceName.setText("Dev Name: " + mapDef);
    label_deviceName.updateUI();
    label_manID.
      setText("Man ID: " + Utils.intTo0xnn(mapDef.getManufacturerID()));
    label_manID.updateUI();
    label_modelID.setText("Model ID: " + Utils.intTo0xnn(mapDef.getModelID()));
    label_modelID.updateUI();
    deviceID = mapDef.getDefaultDeviceID();
    label_deviceID.setText("Device ID: " + Utils.intTo0xnn(deviceID));
    label_deviceID.updateUI();
  }

  private static UIManager.LookAndFeelInfo[] lookAndFeelInfo;

  static
  {
    lookAndFeelInfo = UIManager.getInstalledLookAndFeels();
  }

  private void setLookAndFeel(final String name) throws Exception
  {
    String className = null;
    for (int i = 0; i < lookAndFeelInfo.length; i++)
      if (name.equals(lookAndFeelInfo[i].getName()))
	{
	  className = lookAndFeelInfo[i].getClassName();
	  break;
	}
    UIManager.setLookAndFeel(className);
    manager.updateUI();
  }

  private void initGUI()
  {
    JButton button;
    final EditorActionListener actionListener = new EditorActionListener();
    constructMenuBar(actionListener);

    // tool icons area
    final JPanel panel_toolIcons = new JPanel();
    panel_toolIcons.setLayout(new FlowLayout(FlowLayout.LEFT));
    getContentPane().add(panel_toolIcons, "North");
    final Insets insets = new Insets(0, 0, 0, 0);

    button = new JButton(UIManager.getIcon("internal-button-load"));
    button.addActionListener(actionListener);
    button.setActionCommand(LOAD);
    button.setToolTipText("Loads a File of Data from Disk");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    button = new JButton(UIManager.getIcon("internal-button-save"));
    button.addActionListener(actionListener);
    button.setActionCommand(SAVE);
    button.setToolTipText("Saves Selected Data to a Disk File");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    button = new JButton(UIManager.getIcon("internal-button-request"));
    button.setEnabled(false);
    button.addActionListener(actionListener);
    button.setActionCommand(REQUEST);
    button.setToolTipText("Requests Selected Data via MIDI");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    button = new JButton(UIManager.getIcon("internal-button-dump"));
    button.setEnabled(false);
    button.addActionListener(actionListener);
    button.setActionCommand(DUMP);
    button.setToolTipText("Dumps Selected Data via MIDI");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    button = new JButton(UIManager.getIcon("internal-button-exit"));
    button.addActionListener(actionListener);
    button.setActionCommand(CLOSE);
    button.setToolTipText("Closes this editor window");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    // map area
    final JPanel panel_map = new JPanel();
    getContentPane().add(panel_map, "Center");
    map = new Map();
    map.setAddressRepresentation(mapDef.getAddressRepresentation());
    map.setAddressInfoEnabled(false);
    map.setModel(mapModel);
    map.setShowsRootHandles(true);
    map.setRowHeight(-1);
    map.addKeyListener(new KeyListener());
    map.getModel().addTreeModelListener(new TreeModelListener());
    map.setToolTipText("The Input Area gets its data from disk or " +
		       "via MIDI.");
    final JScrollPane scrollpane_map = new JScrollPane();
    scrollpane_map.setPreferredSize(new Dimension(450, 450));
    scrollpane_map.getViewport().add(map);
    panel_map.setLayout(new BorderLayout());
    panel_map.add("Center", scrollpane_map);
    panel_map.
      setBorder(BorderFactory.
		createTitledBorder(BorderFactory.createEtchedBorder(),
				   "Map",
				   TitledBorder.CENTER,
				   TitledBorder.TOP));

    // Layout Definitions for panel button rows
    GridBagLayout gbl;
    GridBagConstraints c = new GridBagConstraints();
    gbl = new GridBagLayout();
    c.ipadx = 5; /* internal padding */
    c.ipady = 5; /* internal padding */
    c.insets = new Insets(5, 5, 5, 5); /* external padding */
    c.gridx = GridBagConstraints.RELATIVE; /* default setting */
    c.gridy = GridBagConstraints.RELATIVE; /* default setting */
    c.gridwidth = 1;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    c.weightx = 0.0;
    c.weighty = 0.0;

    // Layout Definitions for panel_button_row
    JPanel panel_pad;
    final JPanel panel_button_row = new JPanel();
    gbl = new GridBagLayout();
    panel_button_row.setLayout(gbl);

    button = new JButton(LOAD);
    button.addActionListener(actionListener);
    button.setMnemonic((int)'l');
    button.setToolTipText("Loads a File of Data from Disk");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    button = new JButton(SAVE);
    button.addActionListener(actionListener);
    button.setMnemonic((int)'s');
    button.setToolTipText("Saves Selected Data to a Disk File");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    button = new JButton(REQUEST);
    button.setEnabled(false);
    button.addActionListener(actionListener);
    button.setMnemonic((int)'r');
    button.setToolTipText("Requests Selected Data via MIDI");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    button = new JButton(DUMP);
    button.setEnabled(false);
    button.addActionListener(actionListener);
    button.setMnemonic((int)'d');
    button.setToolTipText("Dumps Selected Data via MIDI");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    panel_pad = new JPanel();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0; c.weighty = 0.0;
    gbl.setConstraints(panel_pad, c);
    panel_button_row.add(panel_pad);
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0; c.weighty = 0.0;
    checkbox_bd = new JCheckBox("Accept Bulk Dumps");
    checkbox_bd.setEnabled(false);
    checkbox_bd.setToolTipText("Allows Bulk Dumps to be Received via MIDI");
    gbl.setConstraints(checkbox_bd, c);
    panel_button_row.add(checkbox_bd);
    checkbox_br = new JCheckBox("Accept Bulk Requests");
    checkbox_br.setEnabled(false);
    checkbox_br.
      setToolTipText("Allows Bulk Requests via MIDI to be Processed");
    gbl.setConstraints(checkbox_br, c);
    panel_button_row.add(checkbox_br);
    panel_map.add("South", panel_button_row);

    // Layout Definitions for operations panel
    final JPanel panel_footer = new JPanel();
    gbl = new GridBagLayout();
    panel_footer.setLayout(gbl);
    c.gridwidth = 1;
    c.gridheight = GridBagConstraints.REMAINDER;

    // operations panel components
    label_deviceName = new JLabel("Device Name: [none]");
    label_deviceName.setToolTipText("The name of the device");
    gbl.setConstraints(label_deviceName, c);
    panel_footer.add(label_deviceName);
    label_manID = new JLabel("Manufacturer ID: [none]");
    label_manID.setToolTipText("The Manufacturer ID of the device");
    gbl.setConstraints(label_manID, c);
    panel_footer.add(label_manID);
    label_modelID = new JLabel("Model ID: [none]");
    label_modelID.setToolTipText("The Model ID of the device");
    gbl.setConstraints(label_modelID, c);
    panel_footer.add(label_modelID);
    label_deviceID = new JLabel("Device ID: [none]");
    label_deviceID.setToolTipText("The Device ID of the device");
    gbl.setConstraints(label_deviceID, c);
    panel_footer.add(label_deviceID);
    updateModelInfo();
    panel_pad = new JPanel();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0; c.weighty = 0.0;
    gbl.setConstraints(panel_pad, c);
    panel_footer.add(panel_pad);
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0; c.weighty = 0.0;
    checkbox_md = new JCheckBox("Modified");
    checkbox_md.setEnabled(false);
    checkbox_md.setToolTipText("True, if the output window has been " +
			       "modified " + "since the last save");
    gbl.setConstraints(checkbox_md, c);
    panel_footer.add(checkbox_md);
    button = new JButton(CLOSE);
    button.addActionListener(actionListener);
    button.setMnemonic((int)'x');
    button.setToolTipText("Closes this editor window");
    gbl.setConstraints(button, c);
    panel_footer.add(button);

    getContentPane().add("South", panel_footer);

    addWindowListener(new EditorWindowListener());
  }

  private class EditorWindowListener extends WindowAdapter
  {
    public void windowClosing(final WindowEvent e)
    {
      signalDelete();
    }
  }

  private void constructMenuBar(final ActionListener listener)
  {
    final JMenuBar menubar = new JMenuBar();
    JMenu menu, submenu;
    JMenuItem menuItem;

    menu = new JMenu(FILE);
    menu.setMnemonic((int)'f');
    menu.add(new JMenuItem(NEW, (int)'n')).addActionListener(listener);
    menu.add(new JMenuItem(LOAD, (int)'l')).addActionListener(listener);
    menu.add(new JMenuItem(SAVE, (int)'s')).addActionListener(listener);
    menu.add(new JMenuItem(SAVE_AS, (int)'a')).addActionListener(listener);
    menu.add(new JSeparator());
    menuItem = new JMenuItem(REQUEST, (int)'r');
    menuItem.setEnabled(false);
    menu.add(menuItem).addActionListener(listener);
    menuItem = new JMenuItem(DUMP, (int)'d');
    menuItem.setEnabled(false);
    menu.add(menuItem).addActionListener(listener);
    menu.add(new JSeparator());
    menu.add(new JMenuItem(CLOSE, (int)'c')).addActionListener(listener);
    menu.add(new JMenuItem(EXIT, (int)'x')).addActionListener(listener);
    menubar.add(menu);

    menu = new JMenu(EDIT);
    menu.setMnemonic((int)'e');
    menu.add(new JMenuItem(SELECT_ALL, (int)'a')).addActionListener(listener);
    menu.add(new JMenuItem(SELECT_NONE, (int)'n')).addActionListener(listener);
    menu.add(new JMenuItem(INVERT_SELECTION, (int)'v')).
      addActionListener(listener);
    menu.add(new JSeparator());
    menu.add(new JMenuItem(COPY, (int)'c')).addActionListener(listener);
    menu.add(new JMenuItem(PASTE, (int)'p')).addActionListener(listener);
    menu.add(new JSeparator());
    menu.add(new JMenuItem(INCREMENT, (int)'i')).addActionListener(listener);
    menu.add(new JMenuItem(DECREMENT, (int)'d')).addActionListener(listener);
    menu.add(new JMenuItem(UPPERMOST, (int)'u')).addActionListener(listener);
    menu.add(new JMenuItem(LOWERMOST, (int)'l')).addActionListener(listener);
    menu.add(new JSeparator());
    menu.add(new JMenuItem(RESET, (int)'r')).addActionListener(listener);
    menubar.add(menu);

    menu = new JMenu(EVENT);
    menu.setMnemonic((int)'v');
    menu.add(new JMenuItem("Reset MIDI Devices")).addActionListener(listener);
    menu.add(new JMenuItem("Prg Change...")).addActionListener(listener);
    menu.add(new JMenuItem("Bank Select...")).addActionListener(listener);
    menu.add(new JMenuItem("Ctrl Change...")).addActionListener(listener);
    menu.add(new JMenuItem("RPN/NRPN...")).addActionListener(listener);
    menu.add(new JSeparator());
    menu.add(new JMenuItem("Options...")).addActionListener(listener);
    menubar.add(menu);

    submenu = new JMenu(LAF);
    submenu.setMnemonic((int)'l');
    for (int i = 0; i < lookAndFeelInfo.length; i++)
      {
	final UIManager.LookAndFeelInfo info = lookAndFeelInfo[i];
	submenu.add(new JMenuItem(info.getName())).addActionListener(listener);
      }

    menu = new JMenu(OPTIONS);
    menu.setMnemonic((int)'o');
    menu.add(new JMenuItem(LOAD_DEVICE_MODEL)).addActionListener(listener);
    menu.add(new JMenuItem("Set Current Device Model as Default")).
      addActionListener(listener);
    menu.add(new JMenuItem(DEVICE_ID)).addActionListener(listener);
    menu.add(new JMenuItem("Manufacturer ID...")).addActionListener(listener);
    menu.add(new JMenuItem("Message Interval Time...")).
      addActionListener(listener);
    checkbox_dispAddr = new JCheckBox(DISPLAY_ADDRESSES);
    checkbox_dispAddr.setMnemonic((int)'a');
    checkbox_dispAddr.addActionListener(listener);
    checkbox_dispAddr.setSelected(false);
    menu.add(checkbox_dispAddr);
    checkbox_toolTips = new JCheckBox(TOOL_TIPS);
    checkbox_toolTips.setMnemonic((int)'t');
    checkbox_toolTips.addActionListener(listener);
    checkbox_toolTips.setSelected(true);
    ToolTipManager.sharedInstance().setEnabled(true);
    menu.add(checkbox_toolTips);
    menu.add(new JSeparator());
    menu.add(submenu);
    menubar.add(menu);

    menu = new JMenu(HELP);
    menu.setMnemonic((int)'h');
    menu.add(new JMenuItem(TUTORIAL, (int)'t')).
      addActionListener(listener);
    menu.add(new JMenuItem(API, (int)'s')).
      addActionListener(listener);
    menu.add(new JMenuItem(ABOUT_FRAME_APPLICATION, (int)'a')).
      addActionListener(listener);
    menu.add(new JMenuItem(ABOUT_DEVICE_MODEL_APPLICATION, (int)'d')).
      addActionListener(listener);
    menu.add(new JMenuItem(LICENSE, (int)'l')).addActionListener(listener);
    menubar.add(menu);

    setJMenuBar(menubar);
  }

  private class IncrementAction implements ActionListener
  {
    private final Map map;

    private IncrementAction()
    {
      throw new UnsupportedOperationException();
    }

    private IncrementAction(final Map map)
    {
      this.map = map;
    }

    public void actionPerformed(final ActionEvent e)
    {
    }
  }

  private void increment(final DefaultTreeModel mapModel, final TreePath path)
  {
    final MapNode node = (MapNode)path.getLastPathComponent();
    try
      {
	node.increment(mapModel);
      }
    catch (final Exception e) {} // ignore
    mapModel.nodeChanged(node);
  }

  private void decrement(final DefaultTreeModel mapModel, final TreePath path)
  {
    final MapNode node = (MapNode)path.getLastPathComponent();
    try
      {
	node.decrement(mapModel);
      }
    catch (final Exception e) {} // ignore
    mapModel.nodeChanged(node);
  }

  private void lowermost(final DefaultTreeModel mapModel, final TreePath path)
  {
    final MapNode node = (MapNode)path.getLastPathComponent();
    try
      {
	node.lowermost(mapModel);
      }
    catch (final Exception e) {} // ignore
    mapModel.nodeChanged(node);
  }

  private void uppermost(final DefaultTreeModel mapModel, final TreePath path)
  {
    final MapNode node = (MapNode)path.getLastPathComponent();
    try
      {
	node.uppermost(mapModel);
      }
    catch (final Exception e) {} // ignore
    mapModel.nodeChanged(node);
  }

  private void reset(final DefaultTreeModel mapModel, final TreePath path)
  {
    final MapNode node = (MapNode)path.getLastPathComponent();
    try
      {
	node.reset(mapModel);
      }
    catch (final Exception e) {} // ignore
    mapModel.nodeChanged(node);
  }

  /**
   * The first node te be saved in a bulk of nodes.
   */
  private MapNode first_save_node;

  /**
   * The last node te be saved in a bulk of nodes.
   */
  private MapNode last_save_node;

  /**
   * The destination stream for save operation.
   */
  private OutputStream save_stream;

  /**
   * Initializes the save automaton.
   */
  private void save_init()
  {
    first_save_node = null;
    last_save_node = null;
  }

  /**
   * Notes down that the specified MapNode is to be saved; eventually
   * saves a bulk of collected MapNode objects.
   * @param node The MapNode to be saved eventually.
   */
  private void save_add(final MapNode node) throws IOException
  {
    if (node.getAllowsChildren())
      {
	final Enumeration children = node.children();
	while (children.hasMoreElements())
	  save_add((MapNode)children.nextElement());
      }
    else
      {
	if (last_save_node != null)
	  if (last_save_node.getAddress() + last_save_node.getTotalSize() ==
	      node.getAddress())
	    {
	      // add to contigous block & quit
	      last_save_node = node;
	      return;
	    }
	  else
	    // end of contigous block; dump it
	    save_flush();
	// start a new contigous block
	first_save_node = node;
	last_save_node = node;
      }
  }

  /**
   * Flushes the save automaton.
   */
  private void save_flush() throws IOException
  {
    if (first_save_node == null)
      return; // nothing to flush
    else
      {
	System.out.println("dump " + first_save_node.getAddress() + "-" +
			   (last_save_node.getAddress() +
			    last_save_node.getTotalSize()));
	final InputStream bulkDump =
	  mapDef.bulkDump((MapNode)mapModel.getRoot(),
			  first_save_node.getAddress(),
			  last_save_node.getAddress() +
			  last_save_node.getTotalSize());
	int data;
	while ((data = bulkDump.read()) >= 0)
	  {
	    System.out.println("data=" + data);
	    save_stream.write(data);
	  }
	save_init();
      }
  }

  /**
   * Walks through the whole Map object and requests each selected
   * MapNode to be saved.
   */
  private void save_selected(final Map map)
  {
    try
      {
	save_stream = new FileOutputStream("bulkdump.mid");
	save_init();
	int index = map.getMinSelectionRow();
	if (index >= 0)
	  {
	    while (index <= map.getMaxSelectionRow())
	      {
		if (map.isRowSelected(index))
		  save_add((MapNode)map.getPathForRow(index).
			   getLastPathComponent());
		index++;
	      }
	    save_flush();
	  }
      }
    catch (final IOException e)
      {
	new MessageFrame(e.toString());
      }
  }

  private MapDef loadDeviceModel()
  {
    // [PENDING: Sometimes, the program hangs while calling
    // JOptionPane.showDialog() (after sucessfully calling
    // manager.getMapDefClasses).]
    final Class<MapDef> selection =
      (Class<MapDef>)JOptionPane.
      showInputDialog(this, "Select a device model:",
                      "Device Model Selection",
                      JOptionPane.QUESTION_MESSAGE, null,
                      manager.getMapDefClasses(), null);
    if (selection != null)
      try
      {
	return selection.newInstance();
      }
    catch (final Exception e)
      {
	JOptionPane.showMessageDialog(this, e.toString(), ERROR,
				      JOptionPane.INFORMATION_MESSAGE);
      }
    return null;
  }

  private void showAboutFrameApplicationDialog()
  {
    final String msg =
      manager.getVersion() + "\n" +
      manager.getCopyright() + "\n" +
      "\n" +
      "Original distribution site is\n" +
      "https://github.com/soundpaint/SysExEdit\n";
    JOptionPane.showMessageDialog(this, msg, ABOUT_FRAME_APPLICATION,
				  JOptionPane.INFORMATION_MESSAGE);
  }

  private void showAboutDeviceModelApplicationDialog()
  {
    final Box hbox = Box.createHorizontalBox();
    final Box vbox1 = Box.createVerticalBox();
    final Box vbox2 = Box.createVerticalBox();
    final Box vbox3 = Box.createVerticalBox();
    hbox.add(vbox1);
    hbox.add(vbox2);
    hbox.add(vbox3);
    JLabel label;
    label = new JLabel("Device Name");
    label.setForeground(Color.black);
    vbox1.add(label);
    label = new JLabel("Manufacturer ID");
    label.setForeground(Color.black);
    vbox1.add(label);
    label = new JLabel("Model ID");
    label.setForeground(Color.black);
    vbox1.add(label);
    label = new JLabel("Entered by");
    label.setForeground(Color.black);
    vbox1.add(label);
    label = new JLabel("  :  ");
    label.setForeground(Color.black);
    vbox2.add(label);
    label = new JLabel("  :  ");
    label.setForeground(Color.black);
    vbox2.add(label);
    label = new JLabel("  :  ");
    label.setForeground(Color.black);
    vbox2.add(label);
    label = new JLabel("  :  ");
    label.setForeground(Color.black);
    vbox2.add(label);
    label = new JLabel(mapDef.toString());
    label.setForeground(Color.black);
    vbox3.add(label);
    label = new JLabel(Utils.intTo0xnn(mapDef.getManufacturerID()));
    label.setForeground(Color.black);
    vbox3.add(label);
    label = new JLabel(Utils.intTo0xnn(mapDef.getModelID()));
    label.setForeground(Color.black);
    vbox3.add(label);
    label = new JLabel(mapDef.getEnteredBy());
    label.setForeground(Color.black);
    vbox3.add(label);
    JOptionPane.showMessageDialog(this, hbox, ABOUT_DEVICE_MODEL_APPLICATION,
				  JOptionPane.INFORMATION_MESSAGE);
  }

  public void exit()
  {
    if ((!checkbox_md.isSelected()) ||
      (JOptionPane.showConfirmDialog(EditorFrame.this,
				     "Window #" + manager.getID(this) +
				     ": " + CONFIRM_CLOSE, CONFIRM,
				     JOptionPane.YES_NO_OPTION)
       == JOptionPane.YES_OPTION))
      signalDelete();
    else {}
  }

  private class TreeModelListener
    implements javax.swing.event.TreeModelListener
  {
    public void treeNodesChanged(final TreeModelEvent e)
    {
      checkbox_md.setSelected(true);
    }

    public void treeNodesInserted(final TreeModelEvent e)
    {
      checkbox_md.setSelected(true);
    }

    public void treeNodesRemoved(final TreeModelEvent e)
    {
      checkbox_md.setSelected(true);
    }

    public void treeStructureChanged(final TreeModelEvent e)
    {
      checkbox_md.setSelected(true);
    }
  }

  private class KeyListener extends KeyAdapter
  {
    public void keyTyped(final KeyEvent e)
    {
      final TreePath path = map.getSelectionPath();
      final char key = e.getKeyChar();
      switch (key)
	{
	case '+':
	  if (path != null)
	    increment((DefaultTreeModel)map.getModel(), path);
	  break;
	case '-':
	  if (path != null)
	    decrement((DefaultTreeModel)map.getModel(), path);
	  break;
	case '<':
	  if (path != null)
	    lowermost((DefaultTreeModel)map.getModel(), path);
	  break;
	case '>':
	  if (path != null)
	    uppermost((DefaultTreeModel)map.getModel(), path);
	  break;
	case '!':
	  if (path != null)
	    reset((DefaultTreeModel)map.getModel(), path);
	  break;
	case '\u0013': // Ctrl-S
	  save_selected(map);
	  break;
	}
    }
  }

  private class EditorActionListener implements ActionListener
  {
    private JFileChooser newChooser(final File defaultFile)
    {
      final JFileChooser chooser = new JFileChooser();
      final ExtensionFileFilter filter =
        new ExtensionFileFilter(new String[] {"mid", "midi", "sysex"},
                                "MIDI SysEx files");
      chooser.addChoosableFileFilter(filter);
      chooser.setFileFilter(filter);
      if (defaultFile != null)
	chooser.setSelectedFile(defaultFile);
      return chooser;
    }

    public void actionPerformed(final ActionEvent e)
    {
      try
	{
	  unguardedActionPerformed(e);
	}
      catch (final Throwable t)
	{
	  ExceptionPanel.showException(EditorFrame.this, t, false);
	}
    }

    private void unguardedActionPerformed(final ActionEvent e) throws Throwable
    {
      final String command = e.getActionCommand();
      if (command.equals(NEW))
	new Thread(new EditorFrame(null, mapDef, manager)).start();
      else if (command.equals(LOAD))
	{
	  final JFileChooser chooser = newChooser(defaultLoadFile);
	  final int returnVal = chooser.showOpenDialog(EditorFrame.this);
	  if (returnVal == JFileChooser.APPROVE_OPTION)
	    {
	      defaultLoadFile = new File(chooser.getCurrentDirectory(),
					 chooser.getSelectedFile().getName());
	      //load(defaultLoadFile);
	      //checkbox_md.setSelected(false);
	    }
	}
      else if (command.equals(SAVE_AS) ||
	  (command.equals(SAVE) && defaultSaveFile == null))
	{
	  final JFileChooser chooser = newChooser(defaultSaveFile);
	  final int returnVal = chooser.showSaveDialog(EditorFrame.this);
	  if (returnVal == JFileChooser.APPROVE_OPTION)
	    {
	      defaultSaveFile = new File(chooser.getCurrentDirectory(),
					 chooser.getSelectedFile().getName());
	      //saveAs(defaultSaveFile);
	      //checkbox_md.setSelected(false);
	    }
	}
      else if (command.equals(CLOSE))
	exit();
      else if (command.equals(EXIT))
	manager.exitAll();
      else if (command.equals(LOAD_DEVICE_MODEL))
	if ((!checkbox_md.isSelected()) ||
	    (JOptionPane.showConfirmDialog(EditorFrame.this,
					   CONFIRM_LDM, CONFIRM,
					   JOptionPane.YES_NO_OPTION)
	     == JOptionPane.YES_OPTION))
	  {
	    final MapDef mapDef = EditorFrame.this.loadDeviceModel();
	    if (mapDef != null)
	      setMap(mapDef);
	    else {} // no selection - abort operaion!
	  }
	else {}
      else if (command.equals(DEVICE_ID))
	{
	  final int newDeviceID =
	    DialogDevID.showDialog(EditorFrame.this,
				   "Device ID Selection",
				   JOptionPane.QUESTION_MESSAGE,
				   UIManager.getDefaults().
				   getIcon("internal-control"),
				   deviceID);
	  if (newDeviceID >= 0)
	    {
	      deviceID = newDeviceID;
	      label_deviceID.setText("Device ID: " +
				     Utils.intTo0xnn(deviceID));
	      label_deviceID.updateUI();
	    }
	}
      else if (command.equals(DISPLAY_ADDRESSES))
	{
	  map.setAddressInfoEnabled(checkbox_dispAddr.isSelected());
	  map.updateUI();
	}
      else if (command.equals(TOOL_TIPS))
	{
	  ToolTipManager.sharedInstance().
	    setEnabled(checkbox_toolTips.isSelected());
	}
      else if (command.equals(TUTORIAL))
	{
	  final URL url =
            EditorFrame.class.getResource("/doc/tutorial/MissingPage.html");
	  if (url != null) {
            final HtmlPanel panel = new HtmlPanel(url);
            if (panel != null)
              JOptionPane.showMessageDialog(EditorFrame.this, panel, TUTORIAL,
                                            JOptionPane.INFORMATION_MESSAGE);
          }
	}
      else if (command.equals(API))
	{
	  final URL url = EditorFrame.class.getResource("/doc/api/index.html");
	  if (url != null) {
            final HtmlPanel panel = new HtmlPanel(url);
            if (panel != null)
              JOptionPane.showMessageDialog(EditorFrame.this, panel, API,
                                            JOptionPane.INFORMATION_MESSAGE);
          }
	}
      else if (command.equals(ABOUT_FRAME_APPLICATION))
	showAboutFrameApplicationDialog();
      else if (command.equals(ABOUT_DEVICE_MODEL_APPLICATION))
	showAboutDeviceModelApplicationDialog();
      else if (command.equals(LICENSE))
	{
	  final URL url = EditorFrame.class.getResource("/LICENSE.html");
	  if (url != null) {
            final HtmlPanel panel = new HtmlPanel(url);
            if (panel != null)
              JOptionPane.showMessageDialog(EditorFrame.this, panel, LICENSE,
                                            JOptionPane.INFORMATION_MESSAGE);
          }
	}
      else
	for (int i = 0; i < lookAndFeelInfo.length; i++)
	  if (command.equals(lookAndFeelInfo[i].getName()))
	    {
	      EditorFrame.this.setLookAndFeel(command);
	      break;
	    }
    }
  }

  private void signalDelete()
  {
    synchronized(this)
      {
	notify();
      }
  }

  private void awaitDelete()
  {
    synchronized(this)
      {
	try
	  {
	    wait();
	  }
	catch (final InterruptedException e) {}
      }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
