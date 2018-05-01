/*
 * @(#)AboutDeviceModelDialog.java 1.00 18/03/29
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

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.soundpaint.sysexedit.model.Device;

/**
 * This class provides a dialog that informs the user about the device
 * model.
 */
public class AboutDeviceModelDialog
{
  private final Panel panel = new Panel();

  /**
   * A panel with all gui elements of the dialog.
   */
  private class Panel extends JPanel
  {
    private static final long serialVersionUID = -6386549930560676410L;

    private final JLabel valueDeviceName;
    private final JLabel valueDeviceClass;
    private final JLabel valueManId;
    private final JLabel valueModelId;
    private final JLabel valueEnteredBy;

    private Panel()
    {
      final Box rows = Box.createHorizontalBox();

      final Box labelColumn = Box.createVerticalBox();
      rows.add(labelColumn);

      final JLabel labelDeviceName = new JLabel("Device Name:");
      labelColumn.add(labelDeviceName);
      final JLabel labelDeviceClass = new JLabel("Device Class:");
      labelColumn.add(labelDeviceClass);
      final JLabel labelManId = new JLabel("Manufacturer ID: ");
      labelColumn.add(labelManId);
      final JLabel labelModelId = new JLabel("Model ID:");
      labelColumn.add(labelModelId);
      final JLabel labelEnteredBy = new JLabel("Entered by:");
      labelColumn.add(labelEnteredBy);

      final Box valuesColumn = Box.createVerticalBox();
      rows.add(valuesColumn);

      valueDeviceName = new JLabel();
      valuesColumn.add(valueDeviceName);
      valueDeviceClass = new JLabel();
      valuesColumn.add(valueDeviceClass);
      valueManId = new JLabel();
      valuesColumn.add(valueManId);
      valueModelId = new JLabel();
      valuesColumn.add(valueModelId);
      valueEnteredBy = new JLabel();
      valuesColumn.add(valueEnteredBy);

      add(rows);
    }

    public void updateValues(final Device device)
    {
      valueDeviceName.setText(device.getName());
      valueDeviceClass.setText(device.getClass().getName());
      valueManId.setText(Utils.intTo0xnn(device.getManufacturerId()));
      valueModelId.setText(Utils.intTo0xnn(device.getModelId()));
      valueEnteredBy.setText(device.getEnteredBy());
    }
  }

  /**
   * Shows a dialog that informs the user about the device model.
   *
   * @param parentComponent The parent Component for the dialog.
   */
  public void showDialog(final Component parentComponent,
                         final Device device)
  {
    panel.updateValues(device);
    JOptionPane.showMessageDialog(parentComponent, panel,
                                  "About Device Model",
                                  JOptionPane.INFORMATION_MESSAGE);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
