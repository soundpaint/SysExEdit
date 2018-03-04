/*
 * @(#)IntType.java 1.00 18/02/19
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
 * This class renders an 8 bits integer value as a plain integer
 * number.
 */
public class Int8Type implements ValueType
{
  private final int offs;
  private final int radix;

  /**
   * A predefined Int8Type for arbitrary values n in the range 0x00
   * through 0xff.  The display value is just the value's ordinary
   * numeric representation.
   */
  public final static Int8Type defaultInstance = new Int8Type();

  /**
   * Defines a new Int8Type for arbitrary values n in the range 0x00
   * through 0xff.  The display value is just the value's ordinary
   * numeric representation.
   */
  private Int8Type()
  {
    this(0);
  }

  /**
   * Defines a new Int8Type for arbitrary values n in the range
   * [offset, offset + 255].  That is, the display value is just the
   * value's internal 8 bits numeric representation plus the specified
   * offset, which may be negative or positive or zero.  This
   * convenience constructor assumes a radix of 10 for displaying the
   * value.
   * @param offs The offset to be added to the value that is to be
   *    represented.
   */
  public Int8Type(final int offs)
  {
    this(offs, 10);
  }

  /**
   * Defines a new Int8Type for arbitrary values n in the range
   * [offset, offset + 255].  That is, the display value is just the
   * value's internal 8 bits numeric representation plus the specified
   * offset, which may be negative or positive or zero.
   * @param offs The offset to be added to the value that is to be
   *    represented.
   * @param radix The radix to use when creating a numeric display
   * value from the integer value.
   */
  public Int8Type(final int offs, final int radix)
  {
    this.offs = offs;
    this.radix = radix;
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
   * Returns the radix for String representation of numbers.
   * The default is 10.
   * @return The radix.
   */
  public int getRadix()
  {
    return radix;
  }

  /**
   * Returns a String that represents the specified value according to
   * the specification of this Int8Type.
   */
  public String getDisplayValue(final int value)
  {
    final long internalValue = value + offs;
    if ((internalValue < 0) || (internalValue > 255)) {
      return DISPLAY_VALUE_UNKNOWN;
    }
    return Integer.toString(value + offs, radix);
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return "IntType{offs=" + offs + ", radix=" + radix + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
