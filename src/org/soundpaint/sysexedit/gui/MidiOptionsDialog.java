/*
 * @(#)MidiOptionsDialog.java 1.00 18/03/25
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MidiOptionsDialog extends Dialog
{
  private static final long serialVersionUID = 5116563378752233886L;

  public static final MidiDevice.Info pleaseSelect =
    new MidiDevice.Info("Please Select", "SysExEdit",
                        "Please select a MIDI device.", "1.0")
    {
    };

  private final DocumentMetaData documentMetaData;
  private final MidiConnectionsPane midiConnectionsPane;
  private final JLabel dumpMidiFileLabel;
  private final JTextField dumpMidiFileTF;
  private final JButton dumpMidiFileBT;

  private static class FixedHeightComboBox<T> extends JComboBox<T>
  {
    private static final long serialVersionUID = 8174406547096436409L;

    public Dimension getMaximumSize()
    {
      return new Dimension(Short.MAX_VALUE, getPreferredSize().height);
    }
  }

  private static class FixedHeightTextField extends JTextField
  {
    private static final long serialVersionUID = -1880365363403214006L;

    public Dimension getMaximumSize()
    {
      return new Dimension(Short.MAX_VALUE, getPreferredSize().height);
    }
  }

  private static final Comparator<MidiDevice.Info> midiDeviceInfoComparator =
    new Comparator<MidiDevice.Info>() {
      public int compare(final MidiDevice.Info info1,
                         final MidiDevice.Info info2) {
        return info1.getName().compareTo(info2.getName());
      }
    };

  /**
   * Detects if a MidiDevice represents a hardware MIDI port, as explained
   * in the JavaDoc documentation of the MidiDevice class.
   */
  private static boolean representsHardwareMidiPort(final MidiDevice device)
  {
    return !(device instanceof Sequencer) && !(device instanceof Synthesizer);
  }

  private static Collection<MidiDevice.Info> getMidiInputs()
  {
    final Collection<MidiDevice.Info> inputInfos =
      new TreeSet<MidiDevice.Info>(midiDeviceInfoComparator);
    final MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    for (final MidiDevice.Info info : infos) {
      try {
        final MidiDevice device = MidiSystem.getMidiDevice(info);
        if (representsHardwareMidiPort(device)) {
          if (device.getMaxTransmitters() != 0) {
            inputInfos.add(info);
          }
        }
      } catch (final MidiUnavailableException ex) {
        // skip unavailable device
      }
    }
    return inputInfos;
  }

  private static Collection<MidiDevice.Info> getMidiOutputs()
  {
    final Collection<MidiDevice.Info> outputInfos =
      new TreeSet<MidiDevice.Info>(midiDeviceInfoComparator);
    final MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    for (final MidiDevice.Info info : infos) {
      try {
        final MidiDevice device = MidiSystem.getMidiDevice(info);
        if (representsHardwareMidiPort(device)) {
          if (device.getMaxReceivers() != 0) {
            outputInfos.add(info);
          }
        }
      } catch (final MidiUnavailableException ex) {
        // skip unavailable device
      }
    }
    return outputInfos;
  }

  public MidiOptionsDialog(final Frame owner,
                           final DocumentMetaData documentMetaData)
  {
    super(owner, "MIDI Options", true);
    if (documentMetaData == null) {
      throw new NullPointerException("documentMetaData");
    }
    this.documentMetaData = documentMetaData;
    final Container contentPane = getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    dumpMidiFileTF = new FixedHeightTextField();
    dumpMidiFileTF.setToolTipText("select file path for MIDI dump");
    dumpMidiFileTF.setEnabled(false);
    dumpMidiFileBT = new JButton("…");
    dumpMidiFileBT.addActionListener(new FileChooseListener());
    dumpMidiFileBT.setEnabled(false);
    dumpMidiFileLabel = new JLabel("Dump to MIDI File");
    dumpMidiFileLabel.setLabelFor(dumpMidiFileTF);
    dumpMidiFileLabel.setEnabled(false);
    midiConnectionsPane = new MidiConnectionsPane();
    contentPane.add(midiConnectionsPane);
    contentPane.add(Box.createVerticalGlue());
    contentPane.add(new ButtonRow());
    loadInputFields();
    pack();
    setMinimumSize(getPreferredSize());
  }

  private class FileDumpSelectionListener implements ItemListener
  {
    public void itemStateChanged(final ItemEvent event)
    {
      if (event.getItem() == DocumentMetaData.dumpMidiFileDeviceInfo) {
        boolean dumpMidiFileEnabled =
          event.getStateChange() == ItemEvent.SELECTED;
        dumpMidiFileLabel.setEnabled(dumpMidiFileEnabled);
        dumpMidiFileTF.setEnabled(dumpMidiFileEnabled);
        dumpMidiFileBT.setEnabled(dumpMidiFileEnabled);
      }
    }
  }

  private class FileChooseListener implements ActionListener
  {
    private JFileChooser chooser = new JFileChooser();

    public void actionPerformed(final ActionEvent e)
    {
      final int returnVal = chooser.showSaveDialog(MidiOptionsDialog.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File dumpMidiFile =
          new File(chooser.getCurrentDirectory(),
                   chooser.getSelectedFile().getName());
        dumpMidiFileTF.setText(dumpMidiFile.toString());
      } else {
        // chooser cancelled
      }
    }
  }

  private class MidiConnectionsPane extends JPanel
  {
    private static final long serialVersionUID = -8621643906994357861L;

    private final JComboBox<MidiDevice.Info> inputConnectionCB;
    private final JComboBox<MidiDevice.Info> outputConnectionCB;

    private MidiConnectionsPane()
    {
      setBorder(BorderFactory.createTitledBorder("MIDI Connections"));

      final JLabel inputConnectionLabel = new JLabel("MIDI Input Connection");
      inputConnectionCB = new FixedHeightComboBox<MidiDevice.Info>();
      inputConnectionLabel.setLabelFor(inputConnectionCB);
      inputConnectionCB.setToolTipText("select MIDI input connection");

      final JLabel outputConnectionLabel = new JLabel("MIDI Output Connection");
      outputConnectionCB = new FixedHeightComboBox<MidiDevice.Info>();
      outputConnectionLabel.setLabelFor(outputConnectionCB);
      outputConnectionCB.setToolTipText("select MIDI output connection");
      outputConnectionCB.addItemListener(new FileDumpSelectionListener());

      final JPanel dumpMidiFilePanel = createDumpMidiFilePanel();

      final GroupLayout layout = new GroupLayout(this);
      setLayout(layout);
      layout.setAutoCreateGaps(true);
      layout.setAutoCreateContainerGaps(true);

      final GroupLayout.SequentialGroup columns =
        layout.createSequentialGroup();
      final GroupLayout.ParallelGroup labelColumn =
        layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
      labelColumn.addComponent(inputConnectionLabel);
      labelColumn.addComponent(outputConnectionLabel);
      labelColumn.addComponent(dumpMidiFileLabel);
      columns.addGroup(labelColumn);
      final GroupLayout.ParallelGroup valueColumn =
        layout.createParallelGroup(GroupLayout.Alignment.CENTER);
      valueColumn.addComponent(inputConnectionCB);
      valueColumn.addComponent(outputConnectionCB);
      valueColumn.addComponent(dumpMidiFilePanel);
      columns.addGroup(valueColumn);
      layout.setHorizontalGroup(columns);

      final GroupLayout.SequentialGroup rows =
        layout.createSequentialGroup();
      final GroupLayout.ParallelGroup inputRow =
        layout.createParallelGroup(GroupLayout.Alignment.CENTER);
      inputRow.addComponent(inputConnectionLabel);
      inputRow.addComponent(inputConnectionCB);
      rows.addGroup(inputRow);
      final GroupLayout.ParallelGroup outputRow =
        layout.createParallelGroup(GroupLayout.Alignment.CENTER);
      outputRow.addComponent(outputConnectionLabel);
      outputRow.addComponent(outputConnectionCB);
      rows.addGroup(outputRow);
      final GroupLayout.ParallelGroup dumpFileRow =
        layout.createParallelGroup(GroupLayout.Alignment.CENTER);
      dumpFileRow.addComponent(dumpMidiFileLabel);
      dumpFileRow.addComponent(dumpMidiFilePanel);
      rows.addGroup(dumpFileRow);
      layout.setVerticalGroup(rows);
    }

    private JPanel createDumpMidiFilePanel()
    {
      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      panel.add(dumpMidiFileTF);
      panel.add(Box.createHorizontalStrut(10));
      panel.add(dumpMidiFileBT);
      return panel;
    }

    private void updateConnections()
    {
      final Collection<MidiDevice.Info> midiInputs = getMidiInputs();
      inputConnectionCB.removeAllItems();
      inputConnectionCB.addItem(pleaseSelect);
      for (final MidiDevice.Info midiInput : midiInputs) {
        inputConnectionCB.addItem(midiInput);
      }
      final Collection<MidiDevice.Info> midiOutputs = getMidiOutputs();
      outputConnectionCB.removeAllItems();
      outputConnectionCB.addItem(pleaseSelect);
      for (final MidiDevice.Info midiOutput : midiOutputs) {
        outputConnectionCB.addItem(midiOutput);
      }
      outputConnectionCB.addItem(DocumentMetaData.dumpMidiFileDeviceInfo);
    }

    private MidiDevice.Info getSelectedMidiInput()
    {
      final MidiDevice.Info info =
        (MidiDevice.Info)inputConnectionCB.getSelectedItem();
      return info == pleaseSelect ? null : info;
    }

    private void setSelectedMidiInput(final MidiDevice.Info midiInput)
    {
      inputConnectionCB.setSelectedItem(midiInput == null ?
                                        pleaseSelect : midiInput);
    }

    private MidiDevice.Info getSelectedMidiOutput()
    {
      final MidiDevice.Info info =
       (MidiDevice.Info)outputConnectionCB.getSelectedItem();
      return info == pleaseSelect ? null : info;
    }

    private void setSelectedMidiOutput(final MidiDevice.Info midiOutput)
    {
      outputConnectionCB.setSelectedItem(midiOutput == null ?
                                         pleaseSelect : midiOutput);
    }
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
    public void actionPerformed(final ActionEvent e)
    {
      saveInputFields();
      setVisible(false);
    }
  }

  private void saveInputFields()
  {
    documentMetaData.setMidiInput(midiConnectionsPane.getSelectedMidiInput());
    documentMetaData.setMidiOutput(midiConnectionsPane.getSelectedMidiOutput());
    documentMetaData.setDumpMidiFile(new File(dumpMidiFileTF.getText()));
  }

  private void loadInputFields()
  {
    midiConnectionsPane.updateConnections();
    midiConnectionsPane.setSelectedMidiInput(documentMetaData.getMidiInput());
    midiConnectionsPane.setSelectedMidiOutput(documentMetaData.getMidiOutput());
    final File dumpMidiFile = documentMetaData.getDumpMidiFile();
    dumpMidiFileTF.setText(dumpMidiFile != null ? dumpMidiFile.toString() : "");
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
