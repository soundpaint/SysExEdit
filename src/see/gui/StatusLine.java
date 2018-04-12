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

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import see.model.Contents;

public class StatusLine extends JPanel implements DocumentMetaDataChangeListener
{
  private static final long serialVersionUID = -8976328276884031182L;

  private final JLabel labelDeviceName;
  private final JLabel labelManId;
  private final JLabel labelModelId;
  private final JLabel labelDeviceId;
  private final JCheckBox checkBoxModified;

  private StatusLine()
  {
    throw new UnsupportedOperationException();
  }

  public StatusLine(final Controller controller)
  {
    final GridBagLayout gbl = new GridBagLayout();
    setLayout(gbl);

    final GridBagConstraints c = new GridBagConstraints();
    c.gridwidth = 1;
    c.gridheight = GridBagConstraints.REMAINDER;

    labelDeviceName = new JLabel();
    labelDeviceName.setToolTipText("The name of the device");
    gbl.setConstraints(labelDeviceName, c);
    add(labelDeviceName);

    labelManId = new JLabel();
    labelManId.setToolTipText("The Manufacturer ID of the device");
    gbl.setConstraints(labelManId, c);
    add(labelManId);

    labelModelId = new JLabel();
    labelModelId.setToolTipText("The Model ID of the device");
    gbl.setConstraints(labelModelId, c);
    add(labelModelId);

    labelDeviceId = new JLabel();
    labelDeviceId.setToolTipText("The Device ID of the device");
    gbl.setConstraints(labelDeviceId, c);
    add(labelDeviceId);

    final JPanel panelGlue = new JPanel();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0; c.weighty = 0.0;
    gbl.setConstraints(panelGlue, c);
    add(panelGlue);
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0; c.weighty = 0.0;
    checkBoxModified = new JCheckBox("Modified");
    checkBoxModified.setEnabled(false);
    checkBoxModified.setToolTipText("True, if the output window has been " +
                                    "modified " + "since the last save");
    gbl.setConstraints(checkBoxModified, c);
    add(checkBoxModified);
    final JButton buttonClose = new JButton("Close");
    buttonClose.addActionListener(controller.getCloseListener());
    buttonClose.setMnemonic((int)'x');
    buttonClose.setToolTipText("Closes this editor window");
    gbl.setConstraints(buttonClose, c);
    add(buttonClose);
  }

  public void hasUnsavedDataChanged(final boolean hasUnsavedData)
  {
    checkBoxModified.setSelected(hasUnsavedData);
  }

  public void setMidiDeviceId(final Contents deviceId)
  {
    labelDeviceId.setText("Device ID: " + deviceId.getDisplayValue());
    labelDeviceId.updateUI();
  }

  public void setModelInfo(final String deviceName,
                           final int manId,
                           final int modelId)
  {
    labelDeviceName.setText("Dev Name: " + deviceName);
    labelDeviceName.updateUI();
    labelManId.setText("Man ID: " + Utils.intTo0xnn(manId));
    labelManId.updateUI();
    labelModelId.setText("Model ID: " + Utils.intTo0xnn(modelId));
    labelModelId.updateUI();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
