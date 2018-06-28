/*
 * @(#)SparseType.java 1.00 98/01/31
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import javax.swing.Icon;

/**
 * A sparse type represents a (possibly sparse) set of integer values
 * that map to objects with string representation.  It is composed of
 * a finite list of disjunctive contiguous value ranges.  A contiguous
 * value range is a (non-sparse) set of integer values in the range
 * 0x00000000 through 0xffffffff.
 *
 * IMPORTANT NOTE: Values are handled as signed integer values,
 * i.e. note that 0x7fffffff &gt; 0x0, but 0xffffffff &lt; 0x0. This
 * is important when specifying upper/lower bounds beyond 0x7fffffff.
 *
 * A contiguous range is specified by two integer values that define
 * the lower and upper bound of the range (the range includes the
 * bounds).  Here, the integer range is regarded to be cyclic; i.e. by
 * swapping the lower and upper bound, you get the complementary range
 * within the range of integers (except for the bounds, that are
 * included in both cases). E.g. the range [0x8000, 0x8fff] holds 4096
 * values, while [0x8fff, 0x8000] equals the union of the two ranges
 * [0x8fff, 0xffffffff] and [0x0, 0x8000], that holds 32769 +
 * 4294930433 = 4294963202 values.
 */
public class SparseType
{
  /**
   * The key for an icon that is applicable as fallback for any kind
   * of data when no more specific icon is available.
   */
  public static final String GENERIC_ICON_KEY = "internal-data";

  /**
   * An optional informal description of this SparseType.  Useful
   * e.g. as tooltip in the GUI.
   */
  private String description;

  /**
   * A key that represents an icon that illustrates an instance of
   * this sparse type.
   */
  private final String iconKey;

  /**
   * The set of all contiguous ranges that make up this sparse type,
   * sorted by ascending representation values.
   */
  private final TreeSet<ValueRange> valueRanges;

  /**
   * Same as valueRanges, but with a different comparator for looking
   * up a value range by a given numerical value, rather than for
   * comparing different value ranges.
   */
  private TreeSet<ValueRange> valueRangesByNumericalValue;

  /**
   * The total number of valid values in this sparse type.
   */
  private long size = 0;

  /**
   * Creates a sparse type that is initially empty.  Uses the generic
   * fall-back icon for this sparse type.
   */
  public SparseType()
  {
    this(GENERIC_ICON_KEY);
  }

  /**
   * Creates a sparse type that is initially empty.
   * @param iconKey The key of the icon to be used when rendering this
   * sparse type in the GUI.
   */
  public SparseType(final String iconKey)
  {
    this(null, iconKey);
  }

  /**
   * Creates a sparse type that is initially empty.
   * @param iconKey The key of the icon to be used when rendering this
   * sparse type in the GUI.
   */
  public SparseType(final String description, final String iconKey)
  {
    this.description = description;
    this.iconKey = iconKey;
    valueRanges = new TreeSet<ValueRange>();
    valueRangesByNumericalValue = null;
  }

  /**
   * Creates a sparse type with initially a single contiguous range.
   * @param iconKey The key of the icon to be used when rendering this
   * @param lowerBound The lower bound of the contiguous range.
   * @param upperBound The upper bound of the contiguous range.
   * @param renderer The renderer for the contiguous range.
   * @exception NullPointerException If renderer equals null.
   */
  public SparseType(final String iconKey,
                    final int lowerBound, final int upperBound,
                    final ValueRangeRenderer renderer)
  {
    this(iconKey);
    addValueRange(lowerBound, upperBound, renderer);
  }

  /**
   * Creates a sparse type with initially a single contiguous range.
   * @param description An optional informal description of this
   * sparse type.
   * @param iconKey The key of the icon to be used when rendering this
   * @param ranges A list of contiguous ranges.
   * SparseType.  Useful e.g. as tooltip in the GUI.
   * @exception NullPointerException If renderer equals null.
   */
  public SparseType(final String description,
                    final String iconKey,
                    final List<ValueRange> ranges)
  {
    this(description, iconKey);
    for (final ValueRange range : ranges) {
      valueRanges.add(range);
      size += range.getUpperBound() - range.getLowerBound() + 1;
    }
  }

  /**
   * Returns a long value that equals the unsigned interpretation of
   * the given signed int value.
   * @param n A signed int value.
   * @return A long value that equals the unsigned value of parameter n.
   */
  private static long signed_int_to_long(final int n)
  {
    final long long_n = (long)n;
    if (long_n >= 0)
      return long_n;
    return long_n + 0x0000000100000000L;
  }

  /**
   * Returns a signed int value whose unsigned interpretation equals the
   * value of the given long value.
   * @param n A long value that equals the unsigned value of some int.
   * @return The signed int value.
   * @exception IllegalArgumentException If n does not fit into the int range.
   */
  private static int long_to_signed_int(final long n)
  {
    if (n < 0)
      throw new IllegalArgumentException("n out of bounds");
    if (n >= 0x0000000100000000L)
      throw new IllegalArgumentException("n out of bounds");
    return (int)n;
  }

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
   * Returns the key for the icon, that is displayed together with each
   * instance of this sparse type.
   * @return The key of the icon to be displayed.
   */
  public String getIconKey()
  {
    return iconKey;
  }

  private static final long min_unsigned = 0x0000000000000000L;
  private static final long max_unsigned = 0x00000000ffffffffL;

  /**
   * Adds a single contiguous range to this sparse type.
   * @param lowerBound The lower bound of the contiguous range.
   * @param upperBound The upper bound of the contiguous range.
   * @param renderer The renderer for the contiguous range.
   * @return This object for convenience of chained expressions.
   * @exception NullPointerException If renderer equals null.
   * @exception IllegalArgumentException If the value range overlaps
   *    some already exisiting value range.
   */
  public SparseType addValueRange(final int lowerBound, final int upperBound,
                                  final ValueRangeRenderer renderer)
  {
    return addValueRange(lowerBound, upperBound, 0, renderer);
  }

  /**
   * Adds a single contiguous range to this sparse type.
   * @param lowerBound The lower bound of the contiguous range.
   * @param upperBound The upper bound of the contiguous range.
   * @param displayOffset The offset to add from the numerical value
   * relative to the lower bound when getting the display value from
   * the specified renderer.
   * @param renderer The renderer for the contiguous range.
   * @return This object for convenience of chained expressions.
   * @exception NullPointerException If renderer equals null.
   * @exception IllegalArgumentException If the value range overlaps
   *    some already exisiting value range.
   */
  public SparseType addValueRange(final int lowerBound, final int upperBound,
                                  final int displayOffset,
                                  final ValueRangeRenderer renderer)
  {
    return addValueRange(null, lowerBound, upperBound, displayOffset, renderer);
  }

  /**
   * Adds a single contiguous range to this sparse type.
   * @param description An optional informal description of the
   * range to add.
   * @param lowerBound The lower bound of the contiguous range.
   * @param upperBound The upper bound of the contiguous range.
   * @param displayOffset The offset to add from the numerical value
   * relative to the lower bound when getting the display value from
   * the specified renderer.
   * @param renderer The renderer for the contiguous range.
   * @return This object for convenience of chained expressions.
   * @exception NullPointerException If renderer equals null.
   * @exception IllegalArgumentException If the value range overlaps
   *    some already exisiting value range.
   */
  public SparseType addValueRange(final String description,
                                  final int lowerBound, final int upperBound,
                                  final int displayOffset,
                                  final ValueRangeRenderer renderer)
  {
    final long unsigned_lb = signed_int_to_long(lowerBound);
    final long unsigned_ub = signed_int_to_long(upperBound);
    if ((lowerBound < 0) && (upperBound >= 0)) {
      // contiguous wrap-over ; split it up
      valueRanges.add(new ValueRange(description, unsigned_lb, max_unsigned,
                                     displayOffset, renderer));
      valueRanges.add(new ValueRange(description, min_unsigned, unsigned_ub,
                                     displayOffset, renderer));
    } else {
      valueRanges.add(new ValueRange(description, unsigned_lb, unsigned_ub,
                                     displayOffset, renderer));
    }
    valueRangesByNumericalValue = null;
    size += upperBound - lowerBound + 1;
    return this;
  }

  /**
   * Adds a single enumeration value to this sparse type.
   * @param value The enumeration value to be added.
   * @param renderer The ValueRangeRenderer for the value.
   * @return This object for convenience of chained expressions.
   * @exception NullPointerException If renderer equals null.
   * @exception IllegalArgumentException If the single value is
   *    already contained in some existing value range.
   */
  public SparseType addSingleValue(final int value,
                                   final ValueRangeRenderer renderer)
  {
    return addValueRange(value, value, renderer);
  }

  /**
   * Adds a single enumeration value to the sparse type.
   * @param value The enumeration value to be added.
   * @param enumValue The enumeration value as string.
   * @return This object for convenience of chained expressions.
   * @exception NullPointerException If enumValue equals null.
   * @exception IllegalArgumentException If the single value is
   *    already contained in some exisiting value range.
   */
  public SparseType addSingleValue(final int value, final String enumValue)
  {
    return addSingleValue(value, new EnumRenderer(enumValue));
  }

  private final static Comparator<ValueRange>
    valueRangeContainmentComparator = new Comparator<ValueRange>() {
        public int compare(final ValueRange r1, final ValueRange r2)
        {
          if ((r1.getLowerBound() <= r2.getLowerBound()) &&
              (r1.getUpperBound() >= r2.getUpperBound())) {
            // r1 encompasses r2
            return 0;
          }
          if ((r2.getLowerBound() <= r1.getLowerBound()) &&
              (r2.getUpperBound() >= r1.getUpperBound())) {
            // r2 encompasses r1
            return 0;
          }
          if (r1.getUpperBound() <= r2.getLowerBound()) {
            // r1 comes before r2
            return -1;
          }
          if (r2.getUpperBound() <= r1.getLowerBound()) {
            // r2 comes before r1
            return +1;
          }
          throw new ClassCastException("r1 / r2 overlap");
        }

        public boolean equals(final Object obj)
        {
          return obj == valueRangeContainmentComparator;
        }
      };

  private ValueRange getValueRangeByNumericalValue(final int numericalValue)
  {
    if (valueRangesByNumericalValue == null) {
      valueRangesByNumericalValue =
        new TreeSet<ValueRange>(valueRangeContainmentComparator);
      valueRangesByNumericalValue.addAll(valueRanges);
    }
    final ValueRange valueRange =
      new ValueRange(numericalValue, numericalValue,
                     IntegerRenderer.DEFAULT_RENDERER);
    final ValueRange floor = valueRangesByNumericalValue.floor(valueRange);
    if (floor != null) {
      return floor;
    }
    final ValueRange ceiling = valueRangesByNumericalValue.ceiling(valueRange);
    if (ceiling != null) {
      return ceiling;
    }
    return null;
  }

  /**
   * Returns the minimally required bit size to code the sparse type.
   * @return The minimally required bit size in the range 0..32.
   */
  public byte getRequiredBitSize()
  {
    return (byte)Math.ceil(Math.log(size) / Math.log(2));
  }

  /**
   * Checks, if the specified value is a member of one of the
   * contiguous value ranges of this sparse type.
   * @param numericalValue The numerical value to be checked.
   * @return True, if the value is in range.
   */
  private synchronized boolean containsValue(final int numericalValue)
  {
    final ValueRange valueRange = getValueRangeByNumericalValue(numericalValue);
    return valueRange != null;
  }

  /**
   * Returns the lowermost value of this sparse type.
   * @return The lowermost value of this sparse type or null, if this
   *    sparse type is empty.
   */
  public Integer lowermost()
  {
    if (valueRanges.isEmpty()) {
      return null;
    }
    final ValueRange valueRange = valueRanges.first();
    return new Integer(long_to_signed_int(valueRange.getLowerBound()));
  }

  /**
   * Returns the uppermost value of this sparse type.
   * @return The uppermost value of this sparse type or null, if this
   *    sparse type is empty.
   */
  public Integer uppermost()
  {
    if (valueRanges.isEmpty()) {
      return null;
    }
    final ValueRange valueRange = valueRanges.last();
    return new Integer(long_to_signed_int(valueRange.getUpperBound()));
  }

  /**
   * Returns <code>true</code>, if this sparse type is enumeratable.
   */
  public boolean isEnumerable()
  {
    return true;
  }

  /**
   * Given some numerical value that this sparse type may contain or
   * not, returns the next upper value that this sparse type contains.
   * @param numericalValue Some arbitrary numerical value (which may
   *    be even from the omitted value ranges of this sparse type).
   * @return The next upper value that this sparse type contains or
   *    null, if there is no such value.
   */
  public Integer succ(final int numericalValue)
  {
    final ValueRange valueRange = getValueRangeByNumericalValue(numericalValue);
    if (valueRange == null)
      return null;
    if (numericalValue < valueRange.getUpperBound())
      return numericalValue + 1;
    final ValueRange nextValueRange = valueRanges.higher(valueRange);
    if (nextValueRange == null)
      return null;
    return (int)nextValueRange.getLowerBound();
  }

  /**
   * Given some numerical value that this sparse type may contain or
   * not, returns the next lower value that this sparse type contains.
   * @param numericalValue Some arbitrary numerical value (which may
   *    be even from the omitted value ranges of this sparse type).
   * @return The next lower value that this sparse type contains or
   *    null, if there is no such value.
   */
  public Integer pred(final int numericalValue)
  {
    final ValueRange valueRange = getValueRangeByNumericalValue(numericalValue);
    if (valueRange == null)
      return null;
    if (numericalValue > valueRange.getLowerBound())
      return numericalValue - 1;
    final ValueRange previousValueRange = valueRanges.lower(valueRange);
    if (previousValueRange == null)
      return null;
    return (int)previousValueRange.getUpperBound();
  }

  /**
   * Returns a String that represents the specified numerical value
   * according to the renderers' specifications of each contiguous
   * range.  If the sparse type does not contain the specified value,
   * this method returns null.
   * @param numericalValue The numerical value to be represented.
   * @return The String representation of the numerical value or
   * <code>null</code>, if there is no representation.
   */
  public String getDisplayValue(final int numericalValue)
  {
    final ValueRange valueRange = getValueRangeByNumericalValue(numericalValue);
    if (valueRange == null)
      return null;
    return valueRange.getDisplayValue(numericalValue);
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    final StringBuffer s = new StringBuffer();
    for (final ValueRange valueRange : valueRanges) {
      if (s.length() > 0) {
        s.append(", ");
      }
      s.append(valueRange.toString());
    }
    return "SparseType[description=" + description + ", iconKey=" + iconKey +
      ", valueRanges={" + s + "}, size=" + size + "]";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
