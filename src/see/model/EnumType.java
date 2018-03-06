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
 * This class renders an integer value based on a given enumeration of
 * display values.
 */
public class EnumType implements ValueType
{
  private int lowerBound;
  private String[] displayValues;

  public int getLowerBound()
  {
    return lowerBound;
  }

  public int getSize()
  {
    return displayValues.length;
  }

  /**
   * Defines a new EnumerationType with the specified display values.
   * Each display value is associated with a specific numerical value.
   * The first of the specified display values is associated with the
   * numerical value <code>0</code>; the next display value is
   * associated with <code>1</code>, and so on.
   * @param displayValues An array of strings representing the display
   * values.
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
   * @param value The numerical representation of the specified
   * display value.
   * @param displayValue The display value for the internal value.
   */
  public EnumType(final int value, final String displayValue)
  {
    this(value, new String[] {displayValue});
  }

  /**
   * Defines a new EnumerationType with the specified display values.
   * Each display value is associated with a specific numerical value.
   * The first of the specified display values is associated with the
   * numerical value <code>lowerBound</code>; the next display value
   * is associated with <code>lowerBound + 1</code>, and so on.
   * @param lowerBound The numerical representation of the first of the
   * specified display values.
   * @param displayValues An array of strings representing the display
   * values.
   * @exception NullPointerException If displayValues equals null.
   */
  public EnumType(final int lowerBound, final String[] displayValues)
  {
    this.lowerBound = lowerBound;
    if (displayValues == null)
      throw new NullPointerException("displayValues equals null");
    this.displayValues = displayValues;
  }

  /**
   * Returns the display value that this enumeration associated with
   * the specified numerical representation value.
   * @param value The numerical value.
   * @return The associated display value.
   */
  public String getDisplayValue(final int value)
  {
    final int index = value - lowerBound;
    if ((index >= 0) && (index < displayValues.length))
      return displayValues[index];
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
    return "{" + s.toString() + "}";
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return "EnumType{lowerBound=" + lowerBound + ", displayValues=" +
      displayValuesToString() + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
