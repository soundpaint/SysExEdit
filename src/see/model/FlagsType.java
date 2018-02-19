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

package see.model;

/**
 * This class defines an enumeration type for a single Contents object.
 */
public class FlagsType implements ValueType
{
  private final int offs;
  private final int bitStringSize;

  /**
   * Defines a new FlagsType for some value x for the range 0x00 through
   * 0xff.  The display value is just the value's ordinary numeric
   * representation.
   */
  public FlagsType()
  {
    this(0);
  }

  /**
   * Defines a new FlagsType for some integer offset.  The display
   * value is just the value's ordinary numeric representation plus
   * the specified offset, which may be negative or positive or zero.
   * This convenience constructor assumes a bit string size of 8.
   * @param offs The offset to be added to the value that is to be
   *    represented.
   */
  public FlagsType(final int offs)
  {
    this(offs, 8);
  }

  /**
   * Defines a new FlagsType for some integer value x.  The display
   * value is just the value's ordinary numeric representation plus
   * the specified offset, which may be negative or positive or zero.
   * @param offs The offset to be added to the value that is to be
   *    represented.
   * @param bitStringSize The number of flags that this type
   *    represents.
   */
  public FlagsType(final int offs, final int bitStringSize)
  {
    this.offs = offs;
    this.bitStringSize = bitStringSize;
  }

  public int getMinValue()
  {
    return offs;
  }

  public int getSize()
  {
    return 256;
  }

  /**
   * Returns a bit string representation for a given value.
   * @param value The value to be represented.
   * @return A bit string representation of the value.
   */
  private String formatBitString(final int value)
  {
    final StringBuffer s = new StringBuffer();
    s.append("%");
    for (int i = bitStringSize - 1; i >= 0; i--)
      s.append((((value >> i) & 1) == 1) ? "1" : "0");
    return s.toString();
  }

  /**
   * Returns a String that represents x according to the specification
   * of this EnumerationType.
   */
  public String getDisplayValue(final int value)
  {
    final long internalValue = value + offs;
    if ((internalValue < 0) || (internalValue > 255)) {
      return DISPLAY_VALUE_UNKNOWN;
    }
    return formatBitString(value + offs);
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return "FlagsType{offs=" + offs + ", bitStringSize=" + bitStringSize + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
