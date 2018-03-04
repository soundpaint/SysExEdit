/*
 * @(#)Range.java 1.00 98/01/31
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

import java.util.Comparator;
import java.util.TreeSet;
import javax.swing.Icon;

/**
 * A (possibly non-contigous) range represents a (possibly sparse) set
 * of integer values that map to objects with string representation.
 * It is composed of a finite list of disjunctive contigous subranges.
 * A contigous subrange is a (non-sparse) set of integer values in the
 * range 0x00000000 through 0xffffffff.
 *
 * IMPORTANT NOTE: Values are handled as signed integer values,
 * i.e. note that 0x7fffffff &gt; 0x0, but 0xffffffff &lt; 0x0. This
 * is important when specifying upper/lower bounds beyond 0x7fffffff.
 *
 * A contigous range is specified by two integer values that define
 * the lower and upper bound of the range (the range includes the
 * bounds).  Here, the integer range is regarded to be cyclic; i.e. by
 * swapping the lower and upper bound, you get the complementary range
 * within the range of integers (except for the bounds, that are
 * included in both cases). E.g. the range [0x8000, 0x8fff] holds 4096
 * values, while [0x8fff, 0x8000] equals the union of the two ranges
 * [0x8fff, 0xffffffff] and [0x0, 0x8000], that holds 32769 +
 * 4294930433 = 4294963202 values.
 */
public class Range implements Representation
{
  /**
   * A key that represents an icon that illustrates an instance of
   * this Range.
   */
  private final String iconKey;

  /**
   * The list of all contigous ranges, sorted by ascending
   * representation values.
   */
  private final TreeSet<Subrange> subranges;

  /**
   * Same as subranges, but with a different comparator for looking up
   * a subrange by a given value, rather than for comparing different
   * subranges.
   */
  private TreeSet<Subrange> subrangesByValue;

  /**
   * The total number of valid values in this range.
   */
  private long size = 0;

  /**
   * Creates a range that is initially empty.  Uses the default
   * "internal-data" icon for this range.
   */
  public Range()
  {
    this("internal-data");
  }

  /**
   * Creates a range that is initially empty.
   * @param iconKey The key of the icon to be used when rendering this
   * range in the GUI.
   */
  public Range(final String iconKey)
  {
    this.iconKey = iconKey;
    subranges = new TreeSet<Subrange>();
    subrangesByValue = null;
  }

  /**
   * Creates a range with initially a single contigous range.
   * @param lb The lower bound of the contigous range.
   * @param ub The upper bound of the contigous range.
   * @param valueType The ValueType for the contigous range.
   * @exception NullPointerException If valueType equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already exisiting range.
   */
  public Range(final String iconKey,
               final int lb, final int ub, final ValueType valueType)
  {
    this(iconKey);
    addSubrange(lb, ub, valueType);
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

  private static final long min_unsigned = 0x0000000000000000L;
  private static final long max_unsigned = 0x00000000ffffffffL;

  /**
   * Returns the key for the icon, that is displayed together with each
   * instance of this range.
   * @return The key of the icon to be displayed.
   */
  public String getIconKey()
  {
    return iconKey;
  }

  /**
   * Adds a single contigous range to the total range.
   * @param lb The lower bound of the contigous range.
   * @param ub The upper bound of the contigous range.
   * @param valueType The ValueType for the contigous range.
   * @exception NullPointerException If valueType equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already exisiting range.
   */
  public void addSubrange(final int lb, final int ub,
                          final ValueType valueType)
  {
    final long unsigned_lb = signed_int_to_long(lb);
    final long unsigned_ub = signed_int_to_long(ub);
    if ((lb < 0) && (ub >= 0)) {
      // overlapping contigous; so split it up
      subranges.add(new Subrange(unsigned_lb, max_unsigned, valueType));
      subranges.add(new Subrange(min_unsigned, unsigned_ub, valueType));
    } else {
      subranges.add(new Subrange(unsigned_lb, unsigned_ub, valueType));
    }
    subrangesByValue = null;
  }

  /**
   * Adds a single enumeration value to the total range.
   * @param value The enumeration value to be added.
   * @param valueType The ValueType for the value.
   * @exception NullPointerException If valueType equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already existing range.
   */
  public void addSingleValue(final int value, final ValueType valueType)
  {
    addSubrange(value, value, valueType);
  }

  /**
   * Adds a single value to the total range.
   * @param valueType The ValueType for the value.
   * @exception NullPointerException If valueType equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already existing range.
   */
  public void addSingleValue(final ValueType valueType)
  {
    if (valueType.getSize() != 1) {
      throw new IllegalArgumentException("valueType does not represent a single value");
    }
    addSingleValue(valueType.getMinValue(), valueType);
  }

  /**
   * Adds a single enumeration value to the total range.
   * @param value The enumeration value to be added.
   * @param enumValue The enumeration value as string.
   * @exception NullPointerException If enumValue equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already exisiting range.
   */
  public void addSingleValue(final int value, final String enumValue)
  {
    addSingleValue(value, new EnumType(value, enumValue));
  }

  private final static Comparator<Subrange>
    subrangeContainmentComparator = new Comparator<Subrange>() {
        public int compare(final Subrange r1, final Subrange r2)
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
          return obj == subrangeContainmentComparator;
        }
      };

  private Subrange getSubrangeByValue(int value)
  {
    if (subrangesByValue == null) {
      subrangesByValue =
        new TreeSet<Subrange>(subrangeContainmentComparator);
      subrangesByValue.addAll(subranges);
    }
    final Subrange valueSubrange =
      new Subrange(value, value, Int8Type.defaultInstance);
    final Subrange floor = subrangesByValue.floor(valueSubrange);
    if (floor != null) {
      return floor;
    }
    final Subrange ceiling = subrangesByValue.ceiling(valueSubrange);
    if (ceiling != null) {
      return ceiling;
    }
    return null;
  }

  /**
   * Returns the minimally required bit size to code the total range.
   * @return The minimally required bit size in the range 0..32.
   */
  public int getRequiredBitSize()
  {
    return (byte)Math.ceil(Math.log(size) / Math.log(2));
  }

  /**
   * Checks, if the specified value is a member of one of the
   * contigous ranges of this Range object.
   * @param x The Integer value to be checked.
   * @return True, if the value is in range.
   */
  public synchronized boolean isInRange(final int x)
  {
    final Subrange subrange = getSubrangeByValue(x);
    return subrange != null;
  }

  /**
   * Returns the lowermost value that is in range.
   * @return The lowermost value that is in range or null, if the range
   *    is empty.
   */
  public Integer lowermost()
  {
    if (subranges.isEmpty())
      return null;
    final Subrange subrange = subranges.first();
    return new Integer(long_to_signed_int(subrange.getLowerBound()));
  }

  /**
   * Returns the uppermost value that is in range.
   * @return The uppermost value that is in range or null, if the range
   *    is empty.
   */
  public Integer uppermost()
  {
    if (subranges.isEmpty())
      return null;
    final Subrange subrange = subranges.last();
    return new Integer(long_to_signed_int(subrange.getUpperBound()));
  }

  /**
   * Returns <code>true</code>, if this representation is enumerable.
   */
  public boolean isEnumerable()
  {
    return true;
  }

  /**
   * Given some Integer value x that may be or not in range, returns the
   * next upper value that is in range.
   * @param x Some arbitrary contents value (which may be even out of
   *    range).
   * @return The next upper value that is in range or null, if there is
   *    no such value.
   */
  public Integer succ(final int x)
  {
    final Subrange subrange = getSubrangeByValue(x);
    if (subrange == null)
      return null;
    if (x < subrange.getUpperBound())
      return x + 1;
    final Subrange nextSubrange = subranges.higher(subrange);
    if (nextSubrange == null)
      return null;
    return (int)nextSubrange.getLowerBound();
  }

  /**
   * Given some Integer value x that may be or not in range, returns the
   * next lower value that is in range.
   * @param x Some arbitrary contents value (which may be even out of
   *    range).
   * @return The next lower value that is in range or null, if there is
   *    no such value.
   */
  public Integer pred(final int x)
  {
    final Subrange subrange = getSubrangeByValue(x);
    if (subrange == null)
      return null;
    if (x > subrange.getLowerBound())
      return x - 1;
    final Subrange previousSubrange = subranges.lower(subrange);
    if (previousSubrange == null)
      return null;
    return (int)previousSubrange.getUpperBound();
  }

  /**
   * Returns a String that represents x according to the ValueType
   * specifications of each contigous range.  If x is null or its
   * value is beyond each contigous range, this method returns null.
   * @param x The Integer value to be represented.
   * @return The String representation of x.
   */
  public String getDisplayValue(final int x)
  {
    final Subrange subrange = getSubrangeByValue(x);
    if (subrange == null)
      return null;
    return subrange.getDisplayValue(x);
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    final StringBuffer s = new StringBuffer();
    for (final Subrange subrange : subranges) {
      if (s.length() > 0) {
        s.append(", ");
      }
      s.append(subrange.toString());
    }
    return "Range[subRanges={" + s + "}]";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
