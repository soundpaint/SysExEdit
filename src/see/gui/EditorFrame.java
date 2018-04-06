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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import see.model.MapDef;
import see.model.MapNode;

/**
 * This class implements the main window of the application.
 */
public class EditorFrame extends JFrame implements Runnable, Editor
{
  private static final long serialVersionUID = -8230511863227744503L;

  /*
   * dialog title "confirm"
   */
  private static final String CONFIRM = "Confirm";

  /*
   * dialog title "error"
   */
  private static final String ERROR = "Error";

  /*
   * dialog message "confirm close"
   */
  private static final String CONFIRM_CLOSE = "Data modified.  Close anyway?";

  /*
   * action commands
   */

  private static final String LOAD = "Load…";
  private static final String SAVE = "Save";
  private static final String REQUEST = "Request";
  private static final String DUMP = "Dump";
  private static final String CLOSE = "Close";

  /*
   * GUI elements
   */

  private Map map;
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
  private final Controller controller;
  private final DocumentMetaData documentMetaData;
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
    documentMetaData = new DocumentMetaData();
    if (mapDef != null) {
      documentMetaData.setMidiDeviceId(mapDef.getDefaultDeviceID());
    }
    controller = new Controller(manager, this, documentMetaData, this);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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
        //mapDef = loadMapFrom(filepath);
        System.err.println("[WARNING: loading map def by cmd line arg " +
                           "not supported by now - sorry!]");
        System.err.flush();
      }
    if (mapDef == null)
      {
        System.out.println("[" + windowID + ": prompting for map def...]");
        System.out.flush();
        loadDeviceModel(this);
      }
    if (mapDef != null)
      {
        documentMetaData.setMidiDeviceId(mapDef.getDefaultDeviceID());
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

  public void setMidiDeviceId(final int midiDeviceId)
  {
    label_deviceID.setText("Device ID: " + Utils.intTo0xnn(midiDeviceId));
    label_deviceID.updateUI();
  }

  private void updateModelInfo()
  {
    label_deviceName.setText("Dev Name: " + mapDef.getName());
    label_deviceName.updateUI();
    label_manID.
      setText("Man ID: " + Utils.intTo0xnn(mapDef.getManufacturerID()));
    label_manID.updateUI();
    label_modelID.setText("Model ID: " + Utils.intTo0xnn(mapDef.getModelID()));
    label_modelID.updateUI();
    setMidiDeviceId(documentMetaData.getMidiDeviceId());
  }

  public void setAddressInfoEnabled(final boolean enabled)
  {
    map.setAddressInfoEnabled(enabled);
    map.updateUI();
  }

  private void initGUI()
  {
    JButton button;

    setJMenuBar(new MenuBar(controller));

    // tool icons area
    final JPanel panel_toolIcons = new JPanel();
    panel_toolIcons.setLayout(new FlowLayout(FlowLayout.LEFT));
    getContentPane().add(panel_toolIcons, "North");
    final Insets insets = new Insets(0, 0, 0, 0);

    button = new JButton(UIManager.getIcon("internal-button-load"));
    button.addActionListener(controller.getLoadListener());
    button.setActionCommand(LOAD);
    button.setToolTipText("Loads a File of Data from Disk");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    button = new JButton(UIManager.getIcon("internal-button-save"));
    button.addActionListener(controller.getSaveListener());
    button.setActionCommand(SAVE);
    button.setToolTipText("Saves Selected Data to a Disk File");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    button = new JButton(UIManager.getIcon("internal-button-request"));
    button.setEnabled(false);
    button.addActionListener(controller.getRequestListener());
    button.setActionCommand(REQUEST);
    button.setToolTipText("Requests Selected Data via MIDI");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    button = new JButton(UIManager.getIcon("internal-button-dump"));
    button.setEnabled(false);
    button.addActionListener(controller.getDumpListener());
    button.setActionCommand(DUMP);
    button.setToolTipText("Dumps Selected Data via MIDI");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    button = new JButton(UIManager.getIcon("internal-button-exit"));
    button.addActionListener(controller.getExitListener());
    button.setActionCommand(CLOSE);
    button.setToolTipText("Closes this editor window");
    button.setMargin(insets);
    panel_toolIcons.add(button);

    // map area
    final JPanel panel_map = new JPanel();
    getContentPane().add(panel_map, "Center");
    map = mapDef.getMap();
    map.setAddressRepresentation(mapDef.getAddressRepresentation());
    setAddressInfoEnabled(false);
    map.setModel(mapModel);
    map.setShowsRootHandles(true);
    map.setRowHeight(-1);
    map.addKeyListener(new KeyListener());
    map.addKeyListener(new TreeSelectionDumpListener(mapDef, map,
                                                     documentMetaData, this));
    map.getModel().addTreeModelListener(controller.getTreeModelListener());
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
    button.addActionListener(controller.getLoadListener());
    button.setMnemonic((int)'l');
    button.setToolTipText("Loads a File of Data from Disk");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    button = new JButton(SAVE);
    button.addActionListener(controller.getSaveListener());
    button.setMnemonic((int)'s');
    button.setToolTipText("Saves Selected Data to a Disk File");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    button = new JButton(REQUEST);
    button.setEnabled(false);
    button.addActionListener(controller.getRequestListener());
    button.setMnemonic((int)'r');
    button.setToolTipText("Requests Selected Data via MIDI");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    button = new JButton(DUMP);
    button.setEnabled(false);
    button.addActionListener(controller.getDumpListener());
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
    button.addActionListener(controller.getCloseListener());
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
      tryClose();
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
   * Returns an array of all available map def classes.
   */
  private static Class<MapDef>[] getMapDefClasses()
  {
    final Vector<Class<MapDef>> classes = new Vector<Class<MapDef>>();
    try {
      // TODO: The list of available device class names should be read
      // from a file.
      classes.addElement((Class<MapDef>)Class.forName("see.devices.DB50XG"));
    } catch (final Exception e) {
      System.err.println("[WARNING: Failed loading device: " + e + "]");
      System.err.flush();
    }
    return classes.toArray((Class<MapDef>[])new Class[0]);
  }

  /**
   * Wrap device classes in a model suitable for putting into a combo
   * box.
   */
  private static class ComboBoxDeviceEntry
  {
    private final MapDef device;

    private ComboBoxDeviceEntry(final MapDef device)
    {
      this.device = device;
    }

    public MapDef getDevice()
    {
      return device;
    }

    /**
     * Displays the device's name (rather than a class specifier) in
     * the combo box.
     */
    public String toString()
    {
      return device.getName();
    }
  }

  private static ComboBoxDeviceEntry[] createDeviceSelectionEntries(final Frame parent)
  {
    final Class<MapDef>[] deviceClasses = getMapDefClasses();
    final ArrayList<ComboBoxDeviceEntry> deviceEntries =
      new ArrayList<ComboBoxDeviceEntry>();
    for (final Class<MapDef> deviceClass : deviceClasses) {
      try {
        deviceEntries.add(new ComboBoxDeviceEntry(deviceClass.newInstance()));
      } catch (final Exception e) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.close();
        JOptionPane.showMessageDialog(parent, stringWriter, ERROR,
                                      JOptionPane.INFORMATION_MESSAGE);
      }
    }
    return deviceEntries.toArray(new ComboBoxDeviceEntry[0]);
  }

  public void loadDeviceModel(final Frame parent)
  {
    // [PENDING: Sometimes, the program hangs while calling
    // JOptionPane.showDialog() (after sucessfully calling
    // getMapDefClasses).]
    final MapDef currentDevice = mapDef;
    final ComboBoxDeviceEntry[] deviceSelectionEntries =
      createDeviceSelectionEntries(parent);
    if (deviceSelectionEntries == null) {
      JOptionPane.showMessageDialog(parent,
                                    "No device model available.",
                                    ERROR,
                                    JOptionPane.INFORMATION_MESSAGE);
      mapDef = currentDevice;
      return;
    }
    final ComboBoxDeviceEntry selection =
      (ComboBoxDeviceEntry)JOptionPane.
      showInputDialog(parent, "Select a device model:",
                      "Device Model Selection",
                      JOptionPane.QUESTION_MESSAGE, null,
                      deviceSelectionEntries, null);
    if (selection == null) {
      mapDef = currentDevice;
      return;
    }
    final MapDef newDevice = selection.getDevice();
    if (newDevice == null) {
      mapDef = currentDevice;
      return;
    }
    final TreeNode root = newDevice.buildMap();
    if (mapModel != null) // no need to re-create mapModel, if already
                          // existing
      mapModel.setRoot(root);
    else
      mapModel = new DefaultTreeModel(root);
    if (label_deviceName != null) // gui already initialized
      updateModelInfo();
    mapDef = newDevice;
  }

  public void showAboutDeviceModelDialog()
  {
    final AboutDeviceModelDialog aboutDeviceModelDialog =
      new AboutDeviceModelDialog();
    aboutDeviceModelDialog.showDialog(this, mapDef);
  }

  public void tryClose()
  {
    if ((!documentMetaData.getHaveUnsavedData()) ||
      (JOptionPane.showConfirmDialog(EditorFrame.this,
                                     "Window #" + manager.getID(this) +
                                     ": " + CONFIRM_CLOSE, CONFIRM,
                                     JOptionPane.YES_NO_OPTION)
       == JOptionPane.YES_OPTION))
      signalDelete();
    else {
      // close aborted by user => do nothing
    }
  }

  public void setHaveUnsavedData(final boolean haveUnsavedData)
  {
    checkbox_md.setSelected(haveUnsavedData);
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
