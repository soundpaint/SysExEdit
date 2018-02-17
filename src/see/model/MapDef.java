/*
 * @(#)MapDef.java 1.00 98/01/31
 *
 * Copyright (C) 1998 Juergen Reuter
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

// $Source:$
// $Revision:$
// $Aliases:$
// $Author:$
// $Date:$
// $State:$

package see.model;

import java.io.InputStream;

/**
 * By subclassing this abstract class, you can customize SysExEdit to a
 * variety of MIDI hardware.
 */
public interface MapDef
{
  /**
   * System Exclusive Status code
   */
  public final static int SYS_EX_STAT = 0xf0;

  /**
   * End Of Exclusive Code
   */
  public final static int SYS_EX_END = 0xf7;

  /**
   * Returns the manufacturer ID as defined in the MIDI specification.
   */
  abstract public byte getManufacturerID();

  /**
   * Returns the model ID.
   */
  abstract public byte getModelID();

  /**
   * Returns the default device model ID. If the synthesizer specs do not
   * explicitly specify such a value, the value 0 may be a good choice as
   * return value.
   */
  abstract public byte getDefaultDeviceID();

  /**
   * Returns the name of the author; optionally, a copyright message.
   */
  abstract public String getEnteredBy();

  /**
   * Returns an AddressRepresentation object that defines how addresses
   * are to be displayed to the user.
   */
  abstract public AddressRepresentation getAddressRepresentation();

  /**
   * Creates a new Map with a specific structure. Usually, this method
   * creates a <CODE>new MapNode(...)</CODE> as root node and then inserts
   * MapNode objects as children at will.
   * @return The root node of the structured map.
   */
  abstract public MapNode buildMap();

  /**
   * Given a contigous area of memory, returns a a stream of MIDI bytes
   * that may be used to send the memory contents to the MIDI device.
   * @param root The root node of the map to use.
   * @param start The bit address in the memory map where to start.
   * @param end The bit address in the memory map where to end before;
   *    thus the total bulk dump size is (end - start) bits of memory.
   * @return A stream that bulk dumps the sequence of bytes for the
   *    MIDI device.
   */
  abstract public InputStream bulkDump(MapNode root, long start, long end);

  /**
   * Given an InputStream that represents a sequence of bulk dumped MIDI
   * bytes from the MIDI device, this method interprets the MIDI data and
   * updates the memory map accordingly.
   * @param in The InputStream of MIDI bytes to be interpreted.
   */
  abstract public void bulkRead(InputStream in);

  /**
   * Returns descriptive name of the device(s) (for headlines etc.)
   */
  abstract public String toString();
}
