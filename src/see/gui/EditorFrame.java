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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import see.model.Contents;
import see.model.MapDef;
import see.model.MapNode;

/**
 * This class implements the main window of the application.
 */
public class EditorFrame extends JFrame
  implements Runnable, Editor,
             DocumentMetaDataChangeListener, MapSelectionChangeListener
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
   * GUI elements
   */

  private Map map;
  private StatusLine statusLine;
  private JButton btDump;
  private JButton btSave;
  private JCheckBox checkbox_bd;
  private JCheckBox checkbox_br;

  /*
   * Other instance variables
   */
  private final String filepath; // path of map def class
  private MapDef mapDef;
  private final FramesManager manager; // manages this and all other editor frames
  private final Controller controller;
  private final MapContextMenu mapContextMenu;
  private final DocumentMetaData documentMetaData;
  private DefaultTreeModel mapModel = null;
  private TreeSelectionDumpListener treeSelectionDumpListener = null;

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
    documentMetaData.addMetaDataChangeListener(this);
    documentMetaData.addSelectionChangeListener(this);
    if (mapDef != null) {
      documentMetaData.setMidiDeviceId(mapDef.createDeviceIdContents());
    }
    controller = new Controller(manager, this, documentMetaData, this);
    mapContextMenu = new MapContextMenu(controller);
    documentMetaData.addSelectionChangeListener(mapContextMenu);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
  }

  /**
   * Each EditorFrame object runs its own thread. This method is called
   * when a new EditorFrame object is started.
   */
  public void run()
  {
    final String windowId = "Window #" + manager.addFrame(this);
    setTitle(manager.getVersion() + " " + windowId);
    if (filepath != null)
      {
        //mapDef = loadMapFrom(filepath);
        System.err.println("[WARNING: loading map def by cmd line arg " +
                           "not supported by now - sorry!]");
        System.err.flush();
      }
    if (mapDef == null)
      {
        System.out.println("[" + windowId + ": prompting for map def...]");
        System.out.flush();
        loadDeviceModel(this);
      }
    if (mapDef != null)
      {
        documentMetaData.setMidiDeviceId(mapDef.createDeviceIdContents());
        System.out.println("[" + windowId + ": initializing GUI...]");
        System.out.flush();
        initGUI();
        updateStatusLine();
        System.out.println("[" + windowId + ": opening window...]");
        System.out.flush();
        pack();
        setVisible(true);
        awaitDelete();
        setVisible(false);
        System.out.println("[" + windowId + ": window closed]");
        System.out.flush();
      }
    else
      {
        System.out.println("[" + windowId + " aborted]");
        System.out.flush();
      }
    dispose();
    manager.removeFrame(this);
  }

  private void updateStatusLine()
  {
    statusLine.setMidiDeviceId(documentMetaData.getMidiDeviceId());
    statusLine.setModelInfo(mapDef.getName(),
                            mapDef.getManufacturerId(),
                            mapDef.getModelId());
  }

  private final ActionListener deviceChangeListener =
    new ActionListener() {
      public void actionPerformed(final ActionEvent event)
      {
        statusLine.setMidiDeviceId(documentMetaData.getMidiDeviceId());
      }
    };

  public ActionListener getDeviceChangeListener()
  {
    return deviceChangeListener;
  }

  public void setAddressInfoEnabled(final boolean enabled)
  {
    map.setAddressInfoEnabled(enabled);
    map.updateUI();
  }

  private void initGUI()
  {
    JButton button;

    final MenuBar menuBar = new MenuBar(controller);
    documentMetaData.addMetaDataChangeListener(menuBar);
    documentMetaData.addSelectionChangeListener(menuBar);
    setJMenuBar(menuBar);

    final ToolBar toolBar = new ToolBar(controller);
    documentMetaData.addMetaDataChangeListener(toolBar);
    documentMetaData.addSelectionChangeListener(toolBar);
    getContentPane().add(toolBar, "North");

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
    treeSelectionDumpListener =
      new TreeSelectionDumpListener(mapDef, map, documentMetaData, this);
    map.addKeyListener(treeSelectionDumpListener);
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

    button = new JButton("Load…");
    button.addActionListener(controller.getLoadListener());
    button.setMnemonic((int)'l');
    button.setToolTipText("Loads a File of Data from Disk");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    btSave = new JButton("Save");
    btSave.setEnabled(false);
    btSave.addActionListener(controller.getSaveListener());
    btSave.setMnemonic((int)'s');
    btSave.setToolTipText("Saves Selected Data to a Disk File");
    gbl.setConstraints(btSave, c);
    panel_button_row.add(btSave);
    button = new JButton("Request");
    button.setEnabled(false);
    button.addActionListener(controller.getRequestListener());
    button.setMnemonic((int)'r');
    button.setToolTipText("Requests Selected Data via MIDI");
    gbl.setConstraints(button, c);
    panel_button_row.add(button);
    btDump = new JButton("Dump");
    btDump.setEnabled(false);
    btDump.addActionListener(controller.getBulkDumpListener());
    btDump.setMnemonic((int)'d');
    btDump.setToolTipText("Dumps Selected Data via MIDI");
    gbl.setConstraints(btDump, c);
    panel_button_row.add(btDump);
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

    statusLine = new StatusLine(controller);
    documentMetaData.addMetaDataChangeListener(statusLine);
    getContentPane().add("South", statusLine);

    addWindowListener(new EditorWindowListener());
  }

  private class EditorWindowListener extends WindowAdapter
  {
    public void windowClosing(final WindowEvent e)
    {
      tryClose();
    }
  }

  public void selectAll()
  {
    map.setSelectionInterval(0, 0);
  }

  public void selectNone()
  {
    map.setSelectionInterval(-1, -1);
  }

  public void incrementSelected()
  {
    final TreePath[] paths = map.getSelectionPaths();
    if (paths.length != 1) {
      // no unique node to apply
      return;
    }
    final TreePath path = map.getSelectionPath();
    final MapNode node = (MapNode)path.getLastPathComponent();
    final DefaultTreeModel mapModel = (DefaultTreeModel)map.getModel();
    try {
      node.increment(mapModel);
    } catch (final Exception e) {
      // ignore
    }
    mapModel.nodeChanged(node);
  }

  public void decrementSelected()
  {
    final TreePath[] paths = map.getSelectionPaths();
    if (paths.length != 1) {
      // no unique node to apply
      return;
    }
    final TreePath path = map.getSelectionPath();
    final MapNode node = (MapNode)path.getLastPathComponent();
    final DefaultTreeModel mapModel = (DefaultTreeModel)map.getModel();
    try {
      node.decrement(mapModel);
    } catch (final Exception e) {
      // ignore
    }
    mapModel.nodeChanged(node);
  }

  public void minimizeSelected()
  {
    final TreePath[] paths = map.getSelectionPaths();
    if (paths.length != 1) {
      // no unique node to apply
      return;
    }
    final TreePath path = map.getSelectionPath();
    final MapNode node = (MapNode)path.getLastPathComponent();
    final DefaultTreeModel mapModel = (DefaultTreeModel)map.getModel();
    try {
      node.lowermost(mapModel);
    } catch (final Exception e) {
      // ignore
    }
    mapModel.nodeChanged(node);
  }

  public void maximizeSelected()
  {
    final TreePath[] paths = map.getSelectionPaths();
    if (paths.length != 1) {
      // no unique node to apply
      return;
    }
    final TreePath path = map.getSelectionPath();
    final MapNode node = (MapNode)path.getLastPathComponent();
    final DefaultTreeModel mapModel = (DefaultTreeModel)map.getModel();
    try {
      node.uppermost(mapModel);
    } catch (final Exception e) {
      // ignore
    }
    mapModel.nodeChanged(node);
  }

  public void resetSelected()
  {
    final TreePath[] paths = map.getSelectionPaths();
    if (paths.length != 1) {
      // no unique node to apply
      return;
    }
    final TreePath path = map.getSelectionPath();
    final MapNode node = (MapNode)path.getLastPathComponent();
    final DefaultTreeModel mapModel = (DefaultTreeModel)map.getModel();
    try {
      node.reset(mapModel);
    } catch (final Exception e) {
      // ignore
    }
    mapModel.nodeChanged(node);
  }

  public void bulkDumpSelected()
  {
    treeSelectionDumpListener.dumpSelection();
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
    final TreeNode root = newDevice.buildMap(documentMetaData, mapContextMenu);
    if (mapModel != null) // no need to re-create mapModel, if already
                          // existing
      mapModel.setRoot(root);
    else
      mapModel = new DefaultTreeModel(root);
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
    if ((!documentMetaData.getHasUnsavedData()) ||
      (JOptionPane.showConfirmDialog(EditorFrame.this,
                                     "Window #" + manager.getId(this) +
                                     ": " + CONFIRM_CLOSE, CONFIRM,
                                     JOptionPane.YES_NO_OPTION)
       == JOptionPane.YES_OPTION))
      signalDelete();
    else {
      // close aborted by user => do nothing
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
            incrementSelected();
          break;
        case '-':
          if (path != null)
            decrementSelected();
          break;
        case '<':
          if (path != null)
            minimizeSelected();
          break;
        case '>':
          if (path != null)
            maximizeSelected();
          break;
        case '!':
          if (path != null)
            resetSelected();
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

  public void hasUnsavedDataChanged(final boolean hasUnsavedData)
  {
    btSave.setEnabled(hasUnsavedData);
  }

  public void setMidiDeviceId(final Contents midiDeviceId)
  {
    // ignore
  }

  public void selectionChanged(final SelectionMultiplicity multiplicity)
  {
    // ignore
  }

  public void singleLeafSelectedChanged(final boolean hasSingleLeafSelected)
  {
    // ignore
  }

  public void anythingSelectedChanged(final boolean hasAnythingSelected)
  {
    btDump.setEnabled(hasAnythingSelected);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
