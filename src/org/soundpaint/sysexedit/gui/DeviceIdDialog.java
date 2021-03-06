/*
 * @(#)DeviceIdDialog.java 1.00 98/02/06
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.soundpaint.sysexedit.model.Value;
import org.soundpaint.sysexedit.model.Editor;

/**
 * This class implements a dialog that prompts the user for a new
 * Device ID.
 */
public class DeviceIdDialog extends Dialog
{
  private static final long serialVersionUID = 2731367018460251157L;

  private static class FixedHeightPanel extends JPanel
  {
    private static final long serialVersionUID = 4644066253251498788L;

    public Dimension getMaximumSize()
    {
      return new Dimension(Short.MAX_VALUE, getPreferredSize().height);
    }
  }

  private final Controller controller;
  private final DocumentMetaData documentMetaData;
  private final JPanel deviceIdValuePanel;
  private final JPanel iconPanel;

  /**
   * Creates a blocking dialog for editing the device ID.
   * @param owner The frame that shall own this dialog.
   * @param controller The controller to use for event generation and
   * handling.
   * @param documentMetaData The document meta data that holds the
   * device ID.
   */
  public DeviceIdDialog(final Frame owner, final Controller controller,
                        final DocumentMetaData documentMetaData)
  {
    super(owner, "MIDI Device ID", true);
    if (controller == null) {
      throw new NullPointerException("controller");
    }
    this.controller = controller;
    if (documentMetaData == null) {
      throw new NullPointerException("documentMetaData");
    }
    this.documentMetaData = documentMetaData;

    final Container contentPane = getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

    final JPanel labelPanel = new JPanel();
    labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
    iconPanel = new JPanel();
    labelPanel.add(iconPanel);
    final JLabel deviceIdLabel =
      new JLabel("<html>Select a value to be used as<br></br>" +
                 "Device ID for SysEx messages.</html>");
    labelPanel.add(deviceIdLabel);
    contentPane.add(labelPanel);

    deviceIdValuePanel = new FixedHeightPanel();
    deviceIdValuePanel.setLayout(new BoxLayout(deviceIdValuePanel,
                                               BoxLayout.Y_AXIS));
    deviceIdValuePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    deviceIdValuePanel.setToolTipText("Adjust a new value for the Device ID");
    contentPane.add(deviceIdValuePanel);

    contentPane.add(Box.createVerticalGlue());
    contentPane.add(new ButtonRow());
  }

  private class ButtonRow extends JPanel
  {
    private static final long serialVersionUID = -2382996892216093540L;

    public ButtonRow()
    {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      final JButton buttonCancel = new JButton("Discard");
      buttonCancel.setMnemonic('d');
      buttonCancel.setToolTipText("discard any changed options");
      buttonCancel.addActionListener(new ButtonCancelListener());
      add(buttonCancel);
      add(Box.createHorizontalGlue());
      final JButton buttonOk = new JButton("Apply");
      buttonOk.setMnemonic('a');
      buttonOk.setToolTipText("apply any changed options");
      buttonOk.addActionListener(new ButtonOkListener());
      add(buttonOk);
    }
  }

  private class ButtonCancelListener implements ActionListener
  {
    public void actionPerformed(final ActionEvent e)
    {
      setVisible(false);
    }
  }

  private class ButtonOkListener implements ActionListener
  {
    public void actionPerformed(final ActionEvent event)
    {
      saveInputFields();
      setVisible(false);
      controller.getDeviceChangeListener().actionPerformed(event);
    }
  }

  private void saveInputFields()
  {
    final JValue deviceId = documentMetaData.getMidiDeviceId();
    final Editor editor = (Editor)deviceIdValuePanel.getComponent(0);
    final JValue selectedValue = editor.getSelectedValue();
    if (selectedValue != null) {
      deviceId.setSystemValue(selectedValue.getSystemValue());
    }
  }

  private void loadInputFields()
  {
    final JValue deviceId = documentMetaData.getMidiDeviceId();
    deviceIdValuePanel.removeAll();
    iconPanel.removeAll();
    final Value deviceIdType = deviceId.getValueType();
    iconPanel.add(new JLabel(deviceIdType.getIcon()));
    final Component deviceIdEditor = (Component)(deviceIdType.createEditor());
    deviceIdValuePanel.add(deviceIdEditor);
    deviceIdValuePanel.updateUI();
    pack();
    setMinimumSize(getPreferredSize());
  }

  public void showDialog()
  {
    loadInputFields();
    setVisible(true);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
