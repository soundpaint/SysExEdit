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
  private final long lb; // lower bound
  private final long ub; // upper bound
  private final ValueRangeRenderer renderer;

  private ValueRange()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a new contiguous value range.
   * @param lb The lower bound of the contiguous value range.
   * @param ub The upper bound of the contiguous value range.
   * @param renderer The renderer for this contiguous value range.
   * @exception NullPointerException If renderer equals null.
   */
  public ValueRange(final long lb, final long ub,
                    final ValueRangeRenderer renderer)
  {
    this(null, lb, ub, renderer);
  }

  /**
   * Creates a new contiguous value range.
   * @param description An optional informal description of this
   * ValueRange.  Useful e.g. as tooltip in the GUI.
   * @param lb The lower bound of the contiguous value range.
   * @param ub The upper bound of the contiguous value range.
   * @param renderer The renderer for this contiguous value range.
   * @exception NullPointerException If renderer equals null.
   */
  public ValueRange(final String description,
                    final long lb, final long ub,
                    final ValueRangeRenderer renderer)
  {
    if (lb < 0) {
      throw new IllegalArgumentException("lower bound must be positive");
    }
    if (ub < lb) {
      throw new IllegalArgumentException("upper bound must be greater than lower bound");
    }
    if (renderer == null) {
      throw new NullPointerException("renderer");
    }
    this.description = description;
    this.lb = lb;
    this.ub = ub;
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
    return lb;
  }

  /**
   * Returns the upper bound of this value range.
   * @return The upper bound.
   */
  public long getUpperBound()
  {
    return ub;
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
    return renderer.getDisplayValue(numericalValue);
  }

  /**
   * Compares this value range to another one.
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
    if (ub < other.lb)
      return -1;
    if (lb > other.ub)
      return +1;
    if ((lb == other.lb) && (ub == other.ub))
      return 0;
    throw new ClassCastException("incomparable value ranges: " +
                                 "[" + lb + ", " + ub + "] vs. " +
                                 "[" + other.lb + ", " + other.ub + "]");
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return
      "ValueRange{renderer=" + renderer + ", lb=" + lb + ", ub=" + ub + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
