/*
 * @(#)ToolBar.java 1.00 18/04/11
 *
 * Copyright (C) 2018 Jürgen Reuter
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

import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.soundpaint.sysexedit.model.Value;

public class ToolBar extends JToolBar
  implements DocumentMetaDataChangeListener, MapSelectionChangeListener
{
  private static final long serialVersionUID = 7539331823973840058L;

  private final JButton buttonDump;
  private final JButton buttonSave;

  private ToolBar()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public ToolBar(final Controller controller)
  {
    final Insets insets = new Insets(0, 0, 0, 0);

    final JButton buttonLoad =
      new JButton(UIManager.getIcon("internal-button-load"));
    buttonLoad.addActionListener(controller.getLoadListener());
    buttonLoad.setActionCommand("Load…");
    buttonLoad.setToolTipText("Loads a File of Data from Disk");
    buttonLoad.setMargin(insets);
    add(buttonLoad);

    buttonSave = new JButton(UIManager.getIcon("internal-button-save"));
    buttonSave.addActionListener(controller.getSaveListener());
    buttonSave.setActionCommand("Save");
    buttonSave.setToolTipText("Saves Selected Data to a Disk File");
    buttonSave.setMargin(insets);
    buttonSave.setEnabled(false);
    add(buttonSave);

    addSeparator();

    final JButton buttonRequest =
      new JButton(UIManager.getIcon("internal-button-request"));
    buttonRequest.setEnabled(false);
    buttonRequest.addActionListener(controller.getRequestListener());
    buttonRequest.setActionCommand("Request");
    buttonRequest.setToolTipText("Requests Selected Data via MIDI");
    buttonRequest.setMargin(insets);
    add(buttonRequest);

    buttonDump = new JButton(UIManager.getIcon("internal-button-dump"));
    buttonDump.setEnabled(false);
    buttonDump.addActionListener(controller.getBulkDumpListener());
    buttonDump.setActionCommand("Dump");
    buttonDump.setToolTipText("Dumps Selected Data via MIDI");
    buttonDump.setMargin(insets);
    buttonDump.setEnabled(false);
    add(buttonDump);

    addSeparator();

    final JButton buttonExit =
      new JButton(UIManager.getIcon("internal-button-exit"));
    buttonExit.addActionListener(controller.getExitListener());
    buttonExit.setActionCommand("Close");
    buttonExit.setToolTipText("Closes this editor window");
    buttonExit.setMargin(insets);
    add(buttonExit);
  }

  public void hasUnsavedDataChanged(final boolean hasUnsavedData)
  {
    buttonSave.setEnabled(hasUnsavedData);
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
    // ignore
  }

  public void anythingSelectedChanged(final boolean hasAnythingSelected)
  {
    buttonDump.setEnabled(hasAnythingSelected);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
