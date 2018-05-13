/*
 * @(#)Subrange.java 1.00 98/01/31
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

public class Subrange implements Comparable<Subrange>
{
  private final long lb; // lower bound
  private final long ub; // upper bound
  private final ValueType valueType;

  private Subrange()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a new contiguous subrange.
   * @param lb The lower bound of the contiguous subrange.
   * @param ub The upper bound of the contiguous subrange.
   * @param valueType The ValueType for the contiguous subrange.
   * @exception NullPointerException If valueType equals null.
   */
  public Subrange(final long lb, final long ub, final ValueType valueType)
  {
    if (lb < 0) {
      throw new IllegalArgumentException("lower bound must be positive");
    }
    if (ub < lb) {
      throw new IllegalArgumentException("upper bound must be greater than lower bound");
    }
    if (valueType == null)
      throw new NullPointerException("valueType");
    this.lb = lb;
    this.ub = ub;
    this.valueType = valueType;
  }

  /**
   * Returns the lower bound of this subrange.
   * @return The lower bound.
   */
  public long getLowerBound()
  {
    return lb;
  }

  /**
   * Returns the upper bound of this subrange.
   * @return The upper bound.
   */
  public long getUpperBound()
  {
    return ub;
  }

  /**
   * Returns the value type of this subrange.
   * @return The value type.
   */
  public ValueType getValueType()
  {
    return valueType;
  }

  /**
   * Returns a String that represents x (interpreted as unsigned
   * integer) according to the specification of the underlying
   * ValueType.
   * @param x The unsigned integer as underlying value.
   */
  public String getDisplayValue(final int x)
  {
    return valueType.getDisplayValue(x);
  }

  /**
   * Compares this subrange to another one.
   * @return 0, if this subrange and the other one are identical.
   * @return -1, if this subrange is totally ordered before the other
   * one.
   * @return +1, if this subrange is totally ordered after the other
   * one.
   * @exception ClassCastException If this subrange and the other one
   * are not identical, but yet overlap.
   */
   //* @exception ClassCastException If this and the other subrange are
   //* built upon different value types.
  public int compareTo(final Subrange other)
  {
    /*
    if (!valueType.equals(other.valueType))
      throw new ClassCastException("different value types: " +
                                   valueType + " vs. " + other.valueType);
    */
    if (ub < other.lb)
      return -1;
    if (lb > other.ub)
      return +1;
    if ((lb == other.lb) && (ub == other.ub))
      return 0;
    throw new ClassCastException("incomparable subranges: " +
                                 "[" + lb + ", " + ub + "] vs. " +
                                 "[" + other.lb + ", " + other.ub + "]");
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    return "Subrange{valueType=" + valueType +
      ", lb=" + lb + ", ub=" + ub + "}";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
