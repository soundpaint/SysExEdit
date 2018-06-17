/*
 * @(#)ValueRange.java 1.00 98/01/31
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

public class ValueRange implements Comparable<ValueRange>
{
  private final String description;
  private final long lowerBound;
  private final long upperBound;
  private final long displayOffset;
  private final ValueRangeRenderer renderer;

  private ValueRange()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a new contiguous value range.
   * @param lowerBound The lower bound of the contiguous value range.
   * @param upperBound The upper bound of the contiguous value range.
   * @param renderer The renderer for this contiguous value range.
   * @exception NullPointerException If renderer equals null.
   */
  public ValueRange(final long lowerBound, final long upperBound,
                    final ValueRangeRenderer renderer)
  {
    this(null, lowerBound, upperBound, renderer);
  }

  /**
   * Creates a new contiguous value range, using the lower bound as
   * display offset, such that the first value of this value range
   * maps to the display value for the numerical value 0 of the
   * specified renderer.
   * @param description An optional informal description of this
   * ValueRange.  Useful e.g. as tooltip in the GUI.
   * @param lowerBound The lower bound of the contiguous value range.
   * @param upperBound The upper bound of the contiguous value range.
   * @param renderer The renderer for this contiguous value range.
   * @exception NullPointerException If renderer equals null.
   */
  public ValueRange(final String description,
                    final long lowerBound, final long upperBound,
                    final ValueRangeRenderer renderer)
  {
    this(description, lowerBound, upperBound, 0, renderer);
  }

  /**
   * Creates a new contiguous value range.
   * @param description An optional informal description of this
   * ValueRange.  Useful e.g. as tooltip in the GUI.
   * @param lowerBound The lower bound of the contiguous value range.
   * @param upperBound The upper bound of the contiguous value range.
   * @param displayOffset The offset to add from the numerical value
   * relative to the lower bound when getting the display value from
   * the specified renderer.
   * @param renderer The renderer for this contiguous value range.
   * @exception NullPointerException If renderer equals null.
   */
  public ValueRange(final String description,
                    final long lowerBound, final long upperBound,
                    final long displayOffset,
                    final ValueRangeRenderer renderer)
  {
    if (lowerBound < 0) {
      throw new IllegalArgumentException("lower bound must be positive");
    }
    if (upperBound < lowerBound) {
      throw new IllegalArgumentException("upper bound must be greater than lower bound");
    }
    if (renderer == null) {
      throw new NullPointerException("renderer");
    }
    // TODO: renderer.checkLowerBound(displayOffset);
    // TODO: renderer.checkUpperBound(upperBound - lowerBound + displayOffset);
    this.description = description;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.displayOffset = displayOffset;
    this.renderer = renderer;
  }

  /**
   * Returns optional informal description of this ValueRange.  Useful
   * e.g. as tooltip in the GUI.  If no description is available,
   * returns <code>null</code>
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Returns the lower bound of this value range.
   * @return The lower bound.
   */
  public long getLowerBound()
  {
    return lowerBound;
  }

  /**
   * Returns the upper bound of this value range.
   * @return The upper bound.
   */
  public long getUpperBound()
  {
    return upperBound;
  }

  /**
   * Returns the display offsez of this value range.
   * @return The display offset.
   */
  public long getDisplayOffset()
  {
    return displayOffset;
  }

  /**
   * Returns the renderer of this value range.
   * @return The renderer.
   */
  public ValueRangeRenderer getRenderer()
  {
    return renderer;
  }

  /**
   * Returns a String that represents the numerical value according to
   * the specification of the underlying renderer.
   * @param numericalValue The numerical value to render.
   */
  public String getDisplayValue(final int numericalValue)
  {
    return
      renderer.
      getDisplayValue((int)(numericalValue - lowerBound + displayOffset));
  }

  /**
   * Compares this value range topographically to another one,
   * considering only the upper and lower bound and ignoring the
   * renderer and display offset.
   * @return 0, if this value range and the other one are identical.
   * @return -1, if this value range is totally ordered before the
   * other one.
   * @return +1, if this value range is totally ordered after the
   * other one.
   * @exception ClassCastException If this value range and the other
   * one are not identical, but yet overlap.
   */
   //* @exception ClassCastException If this and the other value range
   //* are built upon different value types.
  public int compareTo(final ValueRange other)
  {
    /*
    if (!renderer.equals(other.renderer))
      throw new ClassCastException("different renderers: " +
                                   renderer + " vs. " + other.renderer);
    */
    if (upperBound < other.lowerBound)
      return -1;
    if (lowerBound > other.upperBound)
      return +1;
    if ((lowerBound == other.lowerBound) && (upperBound == other.upperBound))
      return 0;
    throw new ClassCastException("incomparable value ranges: [" +
                                 lowerBound + ", " + upperBound + "] vs. [" +
                                 other.lowerBound + ", " + other.upperBound +
                                 "]");
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return
      "ValueRange{renderer=" + renderer + ", lowerBound=" + lowerBound +
      ", upperBound=" + upperBound + ", displayOffset=" + displayOffset + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
