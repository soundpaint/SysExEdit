/*
 * @(#)TreeSelectionDumpListener.java 1.00 18/03/20
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

import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Receiver;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import see.model.MapDef;
import see.model.MapNode;

public class TreeSelectionDumpListener extends KeyAdapter
{
  private static final String MSG_CONFIRM_OVERWRITE =
    "MIDI Dump file %s already exists.  Overwrite file?";
  private static final String MSG_NO_MIDI_OUTPUT_PORT =
    "No MIDI output port selected.  " +
    "Please select a MIDI output port under the " +
    "Options → MIDI Options… dialog.";
  private static final String MSG_NO_MIDI_OUTPUT_FILE =
    "No MIDI output file selected.  " +
    "Please select a MIDI output file under the " +
    "Options → MIDI Options… dialog.";

  private final MapDef mapDef;
  private final Map map;
  private final DocumentMetaData documentMetaData;
  private final Frame frame;

  /**
   * The first adress of the current contiguous bulk area.
   */
  private long bulkAreaStartAddress;

  /**
   * The address following the last address of the current contiguous
   * bulk area.
   */
  private long bulkAreaStopBeforeAddress;

  private TreeSelectionDumpListener()
  {
    throw new UnsupportedOperationException();
  }

  public TreeSelectionDumpListener(final MapDef mapDef, final Map map,
                                   final DocumentMetaData documentMetaData,
                                   final Frame frame)
  {
    this.mapDef = mapDef;
    this.map = map;
    this.documentMetaData = documentMetaData;
    this.frame = frame;
    reset();
  }

  /**
   * Initializes the save automaton.
   */
  private void reset()
  {
    bulkAreaStartAddress = -1;
    bulkAreaStopBeforeAddress = -1;
  }

  private MidiMessage createMidiMessage() throws IOException
  {
    System.out.println("dump " + bulkAreaStartAddress +
                       "-" + bulkAreaStopBeforeAddress);
    final InputStream bulkDump =
      mapDef.bulkDump(documentMetaData.getMidiDeviceId(),
                      bulkAreaStartAddress, bulkAreaStopBeforeAddress);
    final List<Byte> sysexData = new ArrayList<Byte>();
    int data;
    while ((data = bulkDump.read()) >= 0) {
      System.out.println("data=" + data);
      sysexData.add((byte)data);
    }
    final int msgSize = sysexData.size() + 2;
    final byte[] bytes = new byte[msgSize];
    bytes[0] = (byte)SysexMessage.SYSTEM_EXCLUSIVE;
    for (int i = 0; i < msgSize - 2; i++) {
      bytes[i + 1] = sysexData.get(i);
    }
    bytes[msgSize - 1] = (byte)SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
    try {
      return new SysexMessage(bytes, bytes.length);
    } catch (final InvalidMidiDataException e) {
      throw new IOException("failed creating SysEx message: " + e.getMessage(),
                            e);
    }
  }

  /**
   * Flushes the save automaton.
   */
  private void flushDump(final Receiver receiver) throws IOException
  {
    if (bulkAreaStartAddress < 0) {
      return; // nothing to flush
    }
    final MidiMessage bulkDump = createMidiMessage();
    receiver.send(bulkDump, -1);
    reset();
  }

  /**
   * Notes down that the specified MapNode is to be saved; eventually
   * saves a bulk of collected MapNode objects.
   * @param node The MapNode to be saved eventually.
   */
  private void addToDump(final Receiver receiver,
                         final MapNode node) throws IOException
  {
    if (node.getAllowsChildren()) {
      // non-leaf node => add children to dump
      for (int i = 0; i < node.getChildCount(); i++) {
        final TreeNode child = node.getChildAt(i);
        addToDump(receiver, (MapNode)child);
      }
      return;
    }
    if (node.getAddress() == bulkAreaStopBeforeAddress) {
      // append to contiguous block & quit
      bulkAreaStopBeforeAddress += node.getContents().getBitSize();
      return;
    }
    if (bulkAreaStopBeforeAddress >= 0) {
      // end of contiguous block; dump it
      flushDump(receiver);
    }
    // start a new contiguous block
    bulkAreaStartAddress = node.getAddress();
    bulkAreaStopBeforeAddress =
      bulkAreaStartAddress + node.getContents().getBitSize();
  }

  /**
   * @return A receiver for creating a dump MIDI file or null, if
   * the operation has been aborted by the user.
   */
  private Receiver getReceiverForDumpMidiFile() throws IOException
  {
    final File dumpMidiFile = documentMetaData.getDumpMidiFile();
    if ((dumpMidiFile == null) || (dumpMidiFile.toString().isEmpty())) {
      throw new IOException(MSG_NO_MIDI_OUTPUT_FILE);
    }
    final boolean exists = dumpMidiFile.exists();
    boolean canOverwrite = false;
    if (exists) {
      final String message =
        String.format(MSG_CONFIRM_OVERWRITE, dumpMidiFile.toString());
      canOverwrite =
        JOptionPane.showConfirmDialog(frame,
                                      message,
                                      "Confirm Overwrite",
                                      JOptionPane.YES_NO_OPTION) ==
        JOptionPane.YES_OPTION;
    }
    if (exists && !canOverwrite) {
      return null;
    }
    try {
      return new MidiFileReceiver(dumpMidiFile);
    } catch (final IOException e) {
      throw new IOException("failed creating MIDI output file: " +
                            e.getMessage(), e);
    }
  }

  private String getDeviceName(final MidiDevice device)
  {
    final MidiDevice.Info info = device.getDeviceInfo();
    final String name = info.getName();
    return name;
  }

  /**
   * @return A receiver for the specified MIDI device.
   */
  private Receiver getReceiverForDevice(final MidiDevice device)
    throws IOException
  {
    if (!device.isOpen()) {
      try {
        device.open();
      } catch (final MidiUnavailableException e) {
        throw new IOException("open MIDI device failed: " + e.getMessage(), e);
      }
    }
    Receiver receiver = null;
    try {
      receiver = device.getReceiver();
    } catch (final MidiUnavailableException e) {
      throw new IOException("get receiver for device " +
                            getDeviceName(device) + " failed: " +
                            e.getMessage(), e);
    }
    if (receiver == null) {
      throw new IOException("no receiver available for device " +
                            getDeviceName(device));
    }
    return receiver;
  }

  /**
   * @return A receiver according to the configured MIDI options,
   * or null, if the operation has been aborted by the user.
   */
  private Receiver getReceiver() throws IOException
  {
    final MidiDevice.Info deviceInfo = documentMetaData.getMidiOutput();
    if ((deviceInfo == null) ||
        (deviceInfo == MidiOptionsDialog.pleaseSelect)) {
      throw new IOException(MSG_NO_MIDI_OUTPUT_PORT);
    }
    System.out.println("deviceInfo=" + deviceInfo);
    if (deviceInfo == DocumentMetaData.dumpMidiFileDeviceInfo) {
      return getReceiverForDumpMidiFile();
    }
    MidiDevice device = null;
    try {
      device = MidiSystem.getMidiDevice(deviceInfo);
    } catch (final MidiUnavailableException e) {
      System.out.println("get MIDI device: " + e.getMessage());
    }
    System.out.println("device=" + device);
    return getReceiverForDevice(device);
  }

  /**
   * Walks through the whole Map object and schedules the contents of
   * each selected node for bulk dump.
   */
  private void dumpSelection()
  {
    try {
      final Receiver receiver = getReceiver();
      if (receiver == null) {
        return; // operation aborted by user
      }
      try {
        reset();
        int index = map.getMinSelectionRow();
        if (index >= 0) {
          while (index <= map.getMaxSelectionRow()) {
            if (map.isRowSelected(index)) {
              final MapNode node =
                (MapNode)map.getPathForRow(index).getLastPathComponent();
              addToDump(receiver, node);
            }
            index++;
          }
          // final flush
          flushDump(receiver);
        }
      } finally {
        receiver.close();
      }
    } catch (final IOException e) {
      JOptionPane.showMessageDialog(frame,
                                    e.getMessage(),
                                    "Bulk Dump Failed",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  public void keyTyped(final KeyEvent e)
  {
    final char key = e.getKeyChar();
    switch (key) {
    case '\u0013': // Ctrl-S
      dumpSelection();
      break;
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
