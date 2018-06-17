/*
 * @(#)EnumRenderer.java 1.00 18/02/19
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
 * This class renders a numerical value based on a given enumeration
 * of display values.
 */
public class EnumRenderer implements ValueRangeRenderer
{
  /**
   * An optional informal description of this EnumRenderer.  Useful
   * e.g. as tooltip in the GUI.
   */
  private String description;

  /**
   * An array of strings representing the display values.
   */
  private String[] displayValues;

  /**
   * Returns optional informal description of this EnumRenderer.
   * Useful e.g. as tooltip in the GUI.  If no description is
   * available, returns <code>null</code>
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Returns the number of different values in this enumeration.
   */
  public int getSize()
  {
    return displayValues.length;
  }

  /**
   * Defines a new EnumRenderer for a single display value.  The
   * EnumRenderer's display value equals the numerical value 0.
   * @param displayValue The display value.
   */
  public EnumRenderer(final String displayValue)
  {
    this(new String[] {displayValue});
  }

  /**
   * Defines a new EnumRenderer with the specified display values.
   * Each display value is associated with a specific numerical value.
   * The first of the specified display values is associated with the
   * numerical value <code>0</code>; the next display value is
   * associated with <code>1</code>, and so on.
   * @param displayValues An array of strings representing the display
   * values.
   * @exception NullPointerException If <code>displayValues</code>
   * equals null.
   */
  public EnumRenderer(final String[] displayValues)
  {
    this(null, displayValues);
  }

  /**
   * Defines a new EnumRenderer with the specified display values.
   * Each display value is associated with a specific numerical value.
   * The first of the specified display values is associated with the
   * numerical value <code>0</code>; the next display value is
   * associated with <code>1</code>, and so on.
   * @param displayValues An array of strings representing the display
   * values.
   * @param description An optional informal description of this
   * EnumRenderer.  Useful e.g. as tooltip in the GUI.
   * @exception NullPointerException If <code>displayValues</code>
   * equals null.
   */
  public EnumRenderer(final String description, final String[] displayValues)
  {
    if (displayValues == null) {
      throw new NullPointerException("displayValues");
    }
    this.displayValues = displayValues;
    this.description = description;
  }

  /**
   * Returns the display value that this enumeration associated with
   * the specified numerical value.
   * @param numericalValue The numerical value.
   * @return The associated display value or
   * <code>ValueRangeRenderer.DISPLAY_VALUE_UNKNOWN</code>, if there
   * is no associated display value for the specified numerical value.
   */
  public String getDisplayValue(final int numericalValue)
  {
    if ((numericalValue >= 0) && (numericalValue < displayValues.length))
      return displayValues[numericalValue];
    else
      return DISPLAY_VALUE_UNKNOWN;
  }

  /**
   * Returns a string representation of the display values array
   * (e.g. for debugging).
   * @return A string representation of the display values array.
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
    return "EnumRenderer{description=" + description +
      ", displayValues=" + displayValuesToString() + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
