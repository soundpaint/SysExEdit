/*
 * @(#)FlagsType.java 1.00 18/02/19
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
 * This class renders an 8 bits mask of flags.
 */
public class FlagsType implements ValueType
{
  private final int lowerBound;
  private final int bitStringSize;

  /**
   * Defines a new bits mask for arbitrary values n in the range 0x00
   * through 0xff.  The display value is just the value's ordinary
   * numeric representation as a set of binary digits.  This
   * convenience constructor assumes a bit string size of 8.
   */
  public FlagsType()
  {
    this(0);
  }

  /**
   * Defines a new bits mask for arbitrary values n in the range 0x00
   * through 0xff.  The display value is just the value's ordinary
   * numeric representation relative to the specified lower bound,
   * displayed as a set of binary digits.  This convenience
   * constructor assumes a bit string size of 8.
   * @param lowerBound The integer value that the bit mask consisting
   * of '0's only maps to.
   */
  public FlagsType(final int lowerBound)
  {
    this(lowerBound, 8);
  }

  /**
   * Defines a new bits mask for arbitrary values n in the range 0x00
   * through 0xff.  The display value is just the value's ordinary
   * numeric representation relative to the specified lower bound,
   * displayed as a set of binary digits.
   * @param lowerBound The integer value that the bit mask consisting
   * of '0's only maps to.
   * @param bitStringSize The number of flags to be shown in the
   * display value.
   */
  public FlagsType(final int lowerBound, final int bitStringSize)
  {
    this.lowerBound = lowerBound;
    this.bitStringSize = bitStringSize;
  }

  public int getLowerBound()
  {
    return lowerBound;
  }

  public int getSize()
  {
    return 256;
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
   * @param value The numerical value to be represented.
   * @return A bit mask as display value.
   */
  public String getDisplayValue(final int value)
  {
    final long index = value - lowerBound;
    if ((index < 0) || (index > 255)) {
      return DISPLAY_VALUE_UNKNOWN;
    }
    return formatBitMask((int)index);
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return "FlagsType{lowerBound=" + lowerBound +
      ", bitStringSize=" + bitStringSize + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
