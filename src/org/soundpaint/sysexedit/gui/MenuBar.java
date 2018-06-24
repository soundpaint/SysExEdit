/*
 * @(#)MenuBar.java 1.00 18/03/26
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

package org.soundpaint.sysexedit.gui;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.soundpaint.sysexedit.model.Value;

/**
 * This class holds the menu bar of the application and related logic.
 */
public class MenuBar extends JMenuBar
  implements DocumentMetaDataChangeListener, MapSelectionChangeListener
{
  private static final long serialVersionUID = -7319508134978167717L;

  private final FileMenu fileMenu;
  private final EditMenu editMenu;

  private MenuBar()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public MenuBar(final Controller ctrl)
  {
    add(fileMenu = new FileMenu(ctrl));
    add(editMenu = new EditMenu(ctrl));
    add(new EventMenu(ctrl));
    add(new OptionsMenu(ctrl));
    add(new HelpMenu(ctrl));
  }

  public void hasUnsavedDataChanged(final boolean hasUnsavedData)
  {
    fileMenu.itemSave.setEnabled(hasUnsavedData);
  }

  public void modelInfoChanged(final String deviceName,
                               final int manId,
                               final int modelId)
  {
    // ignore
  }

  public void midiDeviceIdChanged(final JValue midiDeviceId)
  {
    // ignore
  }

  public void selectionChanged(final SelectionMultiplicity multiplicity)
  {
    // ignore
  }

  public void singleLeafSelectedChanged(final boolean hasSingleLeafSelected)
  {
    editMenu.itemIncrement.setEnabled(hasSingleLeafSelected);
    editMenu.itemDecrement.setEnabled(hasSingleLeafSelected);
    editMenu.itemUppermost.setEnabled(hasSingleLeafSelected);
    editMenu.itemLowermost.setEnabled(hasSingleLeafSelected);
    editMenu.itemReset.setEnabled(hasSingleLeafSelected);
  }

  public void anythingSelectedChanged(final boolean hasAnythingSelected)
  {
    fileMenu.itemDump.setEnabled(hasAnythingSelected);
  }

  private class FileMenu extends JMenu
  {
    private static final long serialVersionUID = -8667602152603261461L;

    private final JMenuItem itemSave;
    private final JMenuItem itemDump;

    private FileMenu(final Controller ctrl)
    {
      super("File");
      setMnemonic('f');

      final JMenuItem itemNew = new JMenuItem("New…");
      itemNew.setMnemonic('n');
      itemNew.addActionListener(ctrl.getNewListener());
      add(itemNew);

      final JMenuItem itemLoad = new JMenuItem("Load…");
      itemLoad.setMnemonic('l');
      itemLoad.addActionListener(ctrl.getLoadListener());
      add(itemLoad);

      itemSave = new JMenuItem("Save");
      itemSave.setMnemonic('s');
      itemSave.addActionListener(ctrl.getSaveListener());
      itemSave.setEnabled(false);
      add(itemSave);

      final JMenuItem itemSaveAs = new JMenuItem("Save as…");
      itemSaveAs.setMnemonic('a');
      itemSaveAs.addActionListener(ctrl.getSaveAsListener());
      add(itemSaveAs);

      final JMenuItem itemClose = new JMenuItem("Close");
      itemClose.setMnemonic('c');
      itemClose.addActionListener(ctrl.getCloseListener());
      add(itemClose);

      addSeparator();

      final JMenuItem itemRequest = new JMenuItem("Request");
      itemRequest.setMnemonic('r');
      // itemRequest.addActionListener(ctrl.getRequestListener()); // TODO
      add(itemRequest);
      itemRequest.setEnabled(false); // TODO

      itemDump = new JMenuItem("Dump Selected Nodes");
      itemDump.setMnemonic('p');
      itemDump.addActionListener(ctrl.getBulkDumpListener());
      add(itemDump);
      itemDump.setEnabled(false);

      addSeparator();

      final JMenuItem itemExit = new JMenuItem("Exit");
      itemExit.setMnemonic('x');
      itemExit.addActionListener(ctrl.getExitListener());
      add(itemExit);
    }
  }

  private class EditMenu extends JMenu
  {
    private static final long serialVersionUID = 4653424577586668740L;

    private final JMenuItem itemIncrement;
    private final JMenuItem itemDecrement;
    private final JMenuItem itemUppermost;
    private final JMenuItem itemLowermost;
    private final JMenuItem itemReset;

    private EditMenu(final Controller ctrl)
    {
      super("Edit");
      setMnemonic('e');

      final JMenuItem itemSelectAll = new JMenuItem("Select all");
      itemSelectAll.setMnemonic('a');
      itemSelectAll.addActionListener(ctrl.getSelectAllListener());
      add(itemSelectAll);

      final JMenuItem itemSelectNone = new JMenuItem("Select none");
      itemSelectNone.setMnemonic('n');
      itemSelectNone.addActionListener(ctrl.getSelectNoneListener());
      add(itemSelectNone);

      addSeparator();

      final JMenuItem itemCopy = new JMenuItem("Copy");
      itemCopy.setMnemonic('c');
      itemCopy.addActionListener(ctrl.getCopyListener());
      add(itemCopy);
      itemCopy.setEnabled(false); // TODO

      final JMenuItem itemPaste = new JMenuItem("Paste");
      itemPaste.setMnemonic('p');
      itemPaste.addActionListener(ctrl.getPasteListener());
      add(itemPaste);
      itemPaste.setEnabled(false); // TODO

      addSeparator();

      itemIncrement = new JMenuItem("Increment");
      itemIncrement.setMnemonic('i');
      itemIncrement.addActionListener(ctrl.getIncrementListener());
      add(itemIncrement);
      itemIncrement.setEnabled(false);

      itemDecrement = new JMenuItem("Decrement");
      itemDecrement.setMnemonic('d');
      itemDecrement.addActionListener(ctrl.getDecrementListener());
      add(itemDecrement);
      itemDecrement.setEnabled(false);

      itemUppermost = new JMenuItem("Uppermost");
      itemUppermost.setMnemonic('u');
      itemUppermost.addActionListener(ctrl.getUppermostListener());
      add(itemUppermost);
      itemUppermost.setEnabled(false);

      itemLowermost = new JMenuItem("Lowermost");
      itemLowermost.setMnemonic('l');
      itemLowermost.addActionListener(ctrl.getLowermostListener());
      add(itemLowermost);
      itemLowermost.setEnabled(false);

      addSeparator();

      itemReset = new JMenuItem("Reset");
      itemReset.setMnemonic('r');
      itemReset.addActionListener(ctrl.getResetListener());
      add(itemReset);
      itemReset.setEnabled(false);
    }
  }

  private class EventMenu extends JMenu
  {
    private static final long serialVersionUID = -1180494834886442270L;

    private EventMenu(final Controller ctrl)
    {
      super("Event");
      setMnemonic('v');

      final JMenuItem itemResetMidiDevices = new JMenuItem("Reset MIDI Devices");
      itemResetMidiDevices.setMnemonic('r');
      itemResetMidiDevices.addActionListener(ctrl.getResetMidiDevicesListener());
      add(itemResetMidiDevices);
      itemResetMidiDevices.setEnabled(false); // TODO

      final JMenuItem itemPrgChange = new JMenuItem("Prg Change…");
      itemPrgChange.setMnemonic('p');
      itemPrgChange.addActionListener(ctrl.getPrgChangeListener());
      add(itemPrgChange);
      itemPrgChange.setEnabled(false); // TODO

      final JMenuItem itemBankSelect = new JMenuItem("Bank Select…");
      itemBankSelect.setMnemonic('b');
      itemBankSelect.addActionListener(ctrl.getBankSelectListener());
      add(itemBankSelect);
      itemBankSelect.setEnabled(false); // TODO

      final JMenuItem itemCtrlChange = new JMenuItem("Ctrl Change…");
      itemCtrlChange.setMnemonic('c');
      itemCtrlChange.addActionListener(ctrl.getCtrlChangeListener());
      add(itemCtrlChange);
      itemCtrlChange.setEnabled(false); // TODO

      final JMenuItem itemRpnNrpn = new JMenuItem("RPN / NRPN…");
      itemRpnNrpn.setMnemonic('n');
      itemRpnNrpn.addActionListener(ctrl.getRpnNrpnListener());
      add(itemRpnNrpn);
      itemRpnNrpn.setEnabled(false); // TODO

      addSeparator();

      final JMenuItem itemOptions = new JMenuItem("Options…");
      itemOptions.setMnemonic('o');
      itemOptions.addActionListener(ctrl.getOptionsListener());
      add(itemOptions);
      itemOptions.setEnabled(false); // TODO
    }
  }

  private class OptionsMenu extends JMenu
  {
    private static final long serialVersionUID = 7190813773763884475L;

    private OptionsMenu(final Controller ctrl)
    {
      super("Options");
      setMnemonic('o');

      final JMenuItem itemMidiOptions = new JMenuItem("MIDI Options…");
      itemMidiOptions.setMnemonic('l');
      itemMidiOptions.addActionListener(ctrl.getMidiOptionsListener());
      add(itemMidiOptions);

      final JMenuItem itemLoadDeviceModel =
        new JMenuItem("Load Device Model…");
      itemLoadDeviceModel.setMnemonic('l');
      itemLoadDeviceModel.addActionListener(ctrl.getLoadDeviceModelListener());
      add(itemLoadDeviceModel);

      final JMenuItem itemSetDefaultDeviceModel =
        new JMenuItem("Set Current Device Model as Default");
      itemSetDefaultDeviceModel.setMnemonic('c');
      itemSetDefaultDeviceModel.addActionListener(ctrl.getSetDefaultDeviceModelListener());
      add(itemSetDefaultDeviceModel);
      itemSetDefaultDeviceModel.setEnabled(false); // TODO

      final JMenuItem itemDeviceId = new JMenuItem("Device ID…");
      itemDeviceId.setMnemonic('d');
      itemDeviceId.addActionListener(ctrl.getDeviceIdListener());
      add(itemDeviceId);

      final JMenuItem itemManufacturerId = new JMenuItem("Manufacturer ID…");
      itemManufacturerId.setMnemonic('m');
      itemManufacturerId.addActionListener(ctrl.getManufacturerIdListener());
      add(itemManufacturerId);
      itemManufacturerId.setEnabled(false); // TODO

      final JMenuItem itemMsgIntervalTime = new JMenuItem("Message Interval Time…");
      itemMsgIntervalTime.setMnemonic('i');
      itemMsgIntervalTime.addActionListener(ctrl.getMsgIntervalTimeListener());
      add(itemMsgIntervalTime);
      itemMsgIntervalTime.setEnabled(false); // TODO

      final JCheckBoxMenuItem itemDisplayAddresses =
        new JCheckBoxMenuItem("Display Addresses");
      itemDisplayAddresses.setMnemonic('a');
      itemDisplayAddresses.addItemListener(ctrl.getDisplayAddressesListener());
      itemDisplayAddresses.setSelected(false);
      add(itemDisplayAddresses);

      final JCheckBoxMenuItem itemToolTips =
        new JCheckBoxMenuItem("Tool Tips");
      itemToolTips.setMnemonic('t');
      itemToolTips.addItemListener(ctrl.getToolTipsListener());
      itemToolTips.setSelected(false);
      add(itemToolTips);
      ToolTipManager.sharedInstance().setEnabled(false);

      addSeparator();

      final JMenu menuLookAndFeel = new JMenu("Look & Feel");
      menuLookAndFeel.setMnemonic('o');
      for (final UIManager.LookAndFeelInfo info :
             UIManager.getInstalledLookAndFeels()) {
        final JMenuItem itemLookAndFeel = new JMenuItem(info.getName());
        itemLookAndFeel.addActionListener(ctrl.getLookAndFeelListener());
        menuLookAndFeel.add(itemLookAndFeel);
      }
      add(menuLookAndFeel);
    }
  }

  private class HelpMenu extends JMenu
  {
    private static final long serialVersionUID = -5411980681720910436L;

    private HelpMenu(final Controller ctrl)
    {
      super("Help");
      setMnemonic('h');

      final JMenuItem itemTutorial = new JMenuItem("Tutorial…");
      itemTutorial.setMnemonic('t');
      itemTutorial.addActionListener(ctrl.getTutorialListener());
      add(itemTutorial);

      final JMenuItem itemApiSpecs = new JMenuItem("API Specs…");
      itemApiSpecs.setMnemonic('s');
      itemApiSpecs.addActionListener(ctrl.getApiSpecsListener());
      add(itemApiSpecs);

      final JMenuItem itemAboutApplication = new JMenuItem("About Application…");
      itemAboutApplication.setMnemonic('a');
      itemAboutApplication.addActionListener(ctrl.getAboutApplicationListener());
      add(itemAboutApplication);

      final JMenuItem itemAboutDeviceModel = new JMenuItem("About Device Model…");
      itemAboutDeviceModel.setMnemonic('d');
      itemAboutDeviceModel.addActionListener(ctrl.getAboutDeviceModelListener());
      add(itemAboutDeviceModel);

      final JMenuItem itemLicense = new JMenuItem("License…");
      itemLicense.setMnemonic('l');
      itemLicense.addActionListener(ctrl.getLicenseListener());
      add(itemLicense);
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
