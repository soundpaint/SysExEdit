/*
 * @(#)Device.java 1.00 98/01/31
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

package org.soundpaint.sysexedit.model;

import java.io.InputStream;
import javax.swing.tree.TreeNode;
import javax.swing.event.TreeSelectionListener;

import org.soundpaint.sysexedit.gui.Map;
import org.soundpaint.sysexedit.gui.MapContextMenu;
import org.soundpaint.sysexedit.model.Value;

/**
 * By implementing this interface, you can customize SysExEdit to a
 * variety of MIDI hardware.
 */
public interface Device
{
  /**
   * Returns the manufacturer ID as defined in the MIDI specification.
   * @return The manufacturer ID as defined in the MIDI specification.
   */
  byte getManufacturerId();

  /**
   * Returns the model ID.
   * @return The model ID.
   */
  byte getModelId();

  /**
   * Returns the range of possible values and default value for this
   * device's device ID.
   */
  Value getDeviceIdType();

  /**
   * Returns the name of the author; optionally, a copyright message.
   * @return The name of the author; optionally, a copyright message.
   */
  String getEnteredBy();

  /**
   * Returns an AddressRepresentation object that defines how addresses
   * are to be displayed to the user.
   * @return An AddressRepresentation object that defines how
   * addresses are to be displayed to the user.
   */
  AddressRepresentation getAddressRepresentation();

  /**
   * Creates a new Map with a specific structure. Usually, this method
   * creates a <CODE>new MapNode(...)</CODE> as root node and then inserts
   * MapNode objects as children at will.
   */
  TreeNode buildMap(final TreeSelectionListener selectionListener,
                    final MapContextMenu mapContextMenu);

  /**
   * Returns the associated tree component.
   * @return The associated tree component.
   */
  Map getMap();

  /**
   * Given a contiguous area of memory, returns a a stream of MIDI
   * bytes that may be used to send the memory contents to the MIDI
   * device.
   * @param deviceId The MIDI device ID.  Valid range is 0x00..0x7f.
   * @param start The bit address in the memory map where to start.
   * @param end The bit address in the memory map where to end before;
   *    thus the total bulk dump size is (end - start) bits of memory.
   * @return A stream that bulk dumps the sequence of bytes for the
   *    MIDI device.
   */
  InputStream bulkDump(final byte deviceId,
                       final long start, final long end);

  /**
   * Given an InputStream that represents a sequence of bulk dumped MIDI
   * bytes from the MIDI device, this method interprets the MIDI data and
   * updates the memory map accordingly.
   * @param in The InputStream of MIDI bytes to be interpreted.
   */
  void bulkRead(final InputStream in);

  /**
   * Returns descriptive name of the device(s) (for headlines etc.)
   */
  String getName();
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
