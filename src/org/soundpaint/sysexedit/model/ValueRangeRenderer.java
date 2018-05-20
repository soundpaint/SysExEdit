/*
 * @(#)ValueRangeRenderer.java 1.00 18/02/19
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
 * A class that implements this interface represents the type of a
 * contiguous, finite range of integer values.  For each integer value
 * in this range, the implementing class must also define a string
 * representation for human display, called "display value".  The
 * display value should be unique among all values of the value range
 * type.
 */
public interface ValueRangeRenderer
{
  /**
   * Default display value for an integer value out of range.  This
   * string is a fallback display value for an integer value that is
   * not in the contiguous range of integer values for this value
   * range type.
   */
  String DISPLAY_VALUE_UNKNOWN = "???";

  /**
   * Returns the lower bound of the range of values that this value
   * range type represents.
   * @return The lower bound integer value of this value range type.
   */
  int getLowerBound();

  /**
   * Returns the size of this value range type, i.e. the number of
   * different values in this value range type.
   * @return The size of this value range type.
   */
  int getSize();

  /**
   * Returns the display value associated with the specified numerical
   * value of this value range type.
   * @param value An integer value in the range of this value range
   * type.
   * @return The display value that is associated with the specified
   * integer value.
   */
  String getDisplayValue(final int value);
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
