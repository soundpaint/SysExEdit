/*
 * @(#)AddressRepresentation.java 1.00 98/01/31
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

/**
 * A class implements this interface to customize the literal
 * representation of addresses for a specific synthesizer.
 */
public interface AddressRepresentation
{
  /**
   * Returns a string representation of the device's memory address
   * specified by the address of a memory bit in the map.
   * @param bitAddress The index in the bit array of the memory map.
   * @return A string representation for the device's memory address
   * of the bit.
   */
  String memoryBitAddress2DeviceAddress(final long bitAddress);

  static class Triple7Bits implements AddressRepresentation
  {
      public String memoryBitAddress2DeviceAddress(final long bitAddress)
      {
        final long address = bitAddress / 7;
        final byte addrHigh = (byte)((address >> 14) & 0x7f);
        final byte addrMiddle = (byte)((address >> 7) & 0x7f);
        final byte addrLow = (byte)(address & 0x7f);
        final byte offset = (byte)(bitAddress % 7);
        final String offsetStr = offset == 0 ? "" : " [" + offset + "]";
        return
          String.format("%02x", addrHigh) + " " +
          String.format("%02x", addrMiddle) + " " +
          String.format("%02x", addrLow) +
          offsetStr;
      }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
