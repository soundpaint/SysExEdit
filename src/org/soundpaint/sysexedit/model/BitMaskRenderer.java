/*
 * @(#)BitMaskRenderer.java 1.00 18/02/19
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

package org.soundpaint.sysexedit.model;

/**
 * This class renders an bits mask of flags with up to 8 bits.
 */
public class BitMaskRenderer implements ValueRangeRenderer
{
  private final int bitStringSize;

  /**
   * Defines a new bits mask for arbitrary values n in the range 0x00
   * through 0xff.  The display value is just the value's ordinary
   * numerical representation, displayed as a set of binary digits.
   * This convenience constructor assumes a bit string size of 8.
   */
  public BitMaskRenderer()
  {
    this(8);
  }

  /**
   * Defines a new bits mask for arbitrary values n in the range 0x00
   * through 0xff.  The display value is just the value's ordinary
   * numerical representation, displayed as a set of binary digits.
   * @param bitStringSize The number of flags to be shown in the
   * display value.
   */
  public BitMaskRenderer(final int bitStringSize)
  {
    this.bitStringSize = bitStringSize;
  }

  public int getSize()
  {
    throw new UnsupportedOperationException("method getSize() not implemented for bit mask renderer");
  }

  private String formatBitMask(final int value)
  {
    final StringBuffer s = new StringBuffer();
    s.append("%");
    for (int i = bitStringSize - 1; i >= 0; i--)
      s.append((((value >> i) & 1) == 1) ? "1" : "0");
    return s.toString();
  }

  /**
   * Returns a bit mask representation as display value for a given
   * numerical value.
   * @param numericalValue The numerical value to be represented.
   * @return A bit mask as display value.
   */
  public String getDisplayValue(final int numericalValue)
  {
    if ((numericalValue < 0) || (numericalValue > 255)) {
      return DISPLAY_VALUE_UNKNOWN;
    }
    return formatBitMask(numericalValue);
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return "BitMaskRenderer{bitStringSize=" + bitStringSize + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
