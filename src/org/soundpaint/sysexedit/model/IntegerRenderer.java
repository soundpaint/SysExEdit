/*
 * @(#)IntegerRenderer.java 1.00 18/02/19
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
 * This class renders an 8 bits integer value as a plain integer
 * number.
 */
public class IntegerRenderer implements ValueRangeRenderer
{
  private final int radix;
  private final boolean fillWithLeadingZeroes;
  private final String displayPrefix;
  private final String displaySuffix;
  private final byte minWidth;

  /**
   * A predefined IntegerRenderer for arbitrary values n in the range
   * 0x00 through 0xff.  The display value is just the value's
   * ordinary numeric representation.
   */
  public static final IntegerRenderer DEFAULT_RENDERER = new IntegerRenderer();

  /**
   * Defines a new IntegerRenderer for arbitrary integer values n.
   * This convenience constructor assumes a radix of 10 for displaying
   * the value.
   */
  public IntegerRenderer()
  {
    this(10, false, "", "", (byte)0);
  }

  /**
   * Defines a new IntegerRenderer for arbitrary integer values n.
   * @param radix The radix to use when creating a numeric display
   * value from the integer value.
   * @param fillWithLeadingZeroes If minWidth is larger than the
   * actual number of digits to display, then fill the remaining
   * characters with leading zeroes.
   * @param displayPrefix A string to add before the actual digits
   * of the number.
   * @param displaySuffix A string to add after the actual digits
   * of the number.
   * @param minWidth The minimum number of characters that the
   * generated String will cover (including display prefix and display
   * suffix).
   */
  public IntegerRenderer(final int radix,
                         final boolean fillWithLeadingZeroes,
                         final String displayPrefix,
                         final String displaySuffix,
                         final byte minWidth)
  {
    this.radix = radix;
    this.fillWithLeadingZeroes = fillWithLeadingZeroes;
    this.displayPrefix = displayPrefix;
    this.displaySuffix = displaySuffix;
    this.minWidth = minWidth;
  }

  public int getSize()
  {
    throw new UnsupportedOperationException("method getSize() not implemented for integer renderer");
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

  public boolean getFillWithLeadingZeroes()
  {
    return fillWithLeadingZeroes;
  }

  public String getDisplaySuffix()
  {
    return displaySuffix;
  }

  public int getMinWidth()
  {
    return minWidth;
  }

  private static String createFill(final char ch, final int size)
  {
    if (size <= 0) {
      // shortcut for better performance
      return "";
    }
    final StringBuffer sb = new StringBuffer();
    for (int i = 0; i < size; i++) sb.append(ch);
    return sb.toString();
  }

  /**
   * Returns a String that represents the specified value according to
   * the specification of this IntegerRenderer.
   */
  public String getDisplayValue(final int numericalValue)
  {
    final long nonNegativeNumericalValue =
      numericalValue >= 0 ? numericalValue : -numericalValue;
    final String digits =
      Integer.toString((int)nonNegativeNumericalValue, radix);
    final String sign = numericalValue >= 0 ? "" : "-";
    final int width =
      displayPrefix.length() +
      sign.length() +
      digits.length() +
      displaySuffix.length();
    final int charsToAdd = minWidth - width;
    final String fill =
      createFill(fillWithLeadingZeroes ? '0' : ' ', charsToAdd);
    return displayPrefix + fill + sign + digits + displaySuffix;
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return
      "IntegerRenderer{radix=" + radix + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
