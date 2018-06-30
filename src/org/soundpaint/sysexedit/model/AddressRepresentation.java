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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  String getDisplayAddress(final long bitAddress);

  /**
   * Returns the address of the memory bit in the map that represents
   * the device's memory address with the specified string
   * representation.
   * @param displayAddress A string representation for the device's
   * memory address of the bit.
   * @return The index in the bit array of the memory map.
   * @exception NumberFormatException If parsing fails.
   */
  long parse(final String displayAddress);

  static AddressRepresentation TRIPLE_7_BITS = new Triple7Bits();

  static class Triple7Bits implements AddressRepresentation
  {
    public String getDisplayAddress(final long bitAddress)
    {
      final long address = bitAddress / 7;
      final byte addrHigh = (byte)((address >> 14) & 0x7f);
      final byte addrMiddle = (byte)((address >> 7) & 0x7f);
      final byte addrLow = (byte)(address & 0x7f);
      final byte offset = (byte)(bitAddress % 7);
      final String offsetStr = offset == 0 ? "" : " [" + offset + "]";
      return
        String.format("%02X", addrHigh) + " " +
        String.format("%02X", addrMiddle) + " " +
        String.format("%02X", addrLow) +
        offsetStr;
    }

    public long parse(final String displayAddress)
    {
      final String regex = "^([a-fA-F0-9]{1,2})\\s+([a-fA-F0-9]{1,2})\\s+([a-fA-F0-9]{1,2})(\\s+\\[[0-7]\\])?$";
      final Pattern pattern = Pattern.compile(regex);
      final Matcher matcher = pattern.matcher(displayAddress);

      final StringBuilder result = new StringBuilder();
      if (!matcher.find()) {
        throw new NumberFormatException();
      }
      final int addrHigh = Integer.parseInt(matcher.group(1), 16) & 0xff;
      final int addrMiddle = Integer.parseInt(matcher.group(2), 16) & 0xff;
      final int addrLow = Integer.parseInt(matcher.group(3), 16) & 0xff;
      final long address = (addrHigh << 14) | (addrMiddle << 7) | addrLow;
      final String offsGroup = matcher.group(4);
      final int offs =
        offsGroup != null ? Integer.parseInt(offsGroup, 16) & 0x7 : 0x0;
      final long bitAddress = address * 7 + offs;
      return bitAddress;
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
