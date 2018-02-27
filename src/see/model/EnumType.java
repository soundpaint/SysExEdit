/*
 * @(#)EnumType.java 1.00 18/02/19
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

package see.model;

/**
 * This class implements an enumeration type for a single Contents object.
 */
public class EnumType implements ValueType
{
  private int offs;
  private String[] displayValues;

  public int getMinValue()
  {
    return offs;
  }

  public int getSize()
  {
    return displayValues.length;
  }

  /**
   * Defines a new EnumType for the specified display values, starting
   * with an internal value of 0 for the internal representation.  For
   * an internal value beyond the bounds of the display values string
   * array, the EnumType's display value is defined as the string
   * constant DISPLAY_VALUE_UNKNOWN.
   * @param displayValues An array of strings representing the value x
   * for each x.
   * @exception NullPointerException If enum equals null.
   */
  public EnumType(final String[] displayValues)
  {
    this(0, displayValues);
  }

  /**
   * Defines a new EnumType for a single display value.  The
   * EnumType's display value equals the specified display value if
   * and only if the internal value equals the specified internal
   * value, and DISPLAY_VALUE_UNKNOWN in all other cases.
   * @param internalValue The internal value to be represented by the
   * display value.
   * @param value The display value for the internal value.
   */
  public EnumType(final int internalValue, final String value)
  {
    this(-internalValue, new String[] {value});
  }

  /**
   * Defines a new EnumerationType for some integer value x.
   * The EnumerationType of x is enum[x + offs]. If (x + offs) is beyond the
   * bounds of the array enum, the EnumerationType of x is defined as the
   * String constant DISPLAY_VALUE_UNKNOWN.
   * @param offs The offset to be added to the value that is to be
   *    represented.
   * @param displayValues An array of strings representing the value
   *    (internalValue + offs) for each internal value.
   * @exception NullPointerException If displayValues equals null.
   */
  public EnumType(final int offs, final String[] displayValues)
  {
    this.offs = offs;
    if (displayValues == null)
      throw new NullPointerException("displayValues equals null");
    this.displayValues = displayValues;
  }

  /**
   * Returns a String that represents x according to the specification
   * of this EnumerationType.
   */
  public String getDisplayValue(final int value)
  {
    final int internalValue = value + offs;
    if ((internalValue >= 0) && (internalValue < displayValues.length))
      return displayValues[internalValue];
    else
      return DISPLAY_VALUE_UNKNOWN;
  }

  /**
   * Returns a string representation of the displayValues array
   * (e.g. for debugging).
   * @return A string representation of the displayValues array.
   */
  private String displayValuesToString()
  {
    final StringBuilder s = new StringBuilder();
    if (displayValues != null) {
      for (final String displayValue : displayValues) {
        if (s.length() > 0)
          s.append(", ");
        s.append(displayValue);
      }
    }
    return s.toString();
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return "EnumType{" + displayValuesToString() + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
