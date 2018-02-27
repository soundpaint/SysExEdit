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
   * Returns a long value that equals the unsigned interpretation of
   * the given signed int value.
   * @param n A signed int value.
   * @return A long value that equals the unsigned value of parameter n.
   */
  private static long signed_int_to_long(final int n)
  {
    long long_n = (long)n;
    if (long_n < 0)
      {
	long_n += 0x080000000;
	long_n += 0x080000000;
      }
    return long_n;
  }

  /**
   * Returns a signed int value whose unsigned interpretation equals the
   * value of the given long value.
   * @param n A long value that equals the unsigned value of some int.
   * @return The signed int value.
   * @exception IllegalArgumentException If n does not fit into the int range.
   */
  private static int long_to_signed_int(long n)
  {
    if (n < 0)
      throw new IllegalArgumentException("n out of bounds");
    if (n > 0x07fffffff)
      {
	n = n - 0x080000000;
	n = n - 0x080000000;
      }
    if (n > 0x07fffffff)
      throw new IllegalArgumentException("n out of bounds");
    return (int)n;
  }

  private class Contigous
  {
    final long lb; // lower bound
    final long ub; // upper bound
    final ValueType valueType;
    Contigous prev; // previous contigous range
    Contigous next; // next contigous range

    private Contigous()
    {
      throw new UnsupportedOperationException();
    }

    /**
     * Creates a new contigous range object.
     * @param lb The lower bound of the contigous range.
     * @param ub The upper bound of the contigous range.
     * @param valueType The ValueType for the contigous range.
     * @exception NullPointerException If valueType equals null.
     */
    Contigous(final long lb, final long ub, final ValueType valueType)
    {
      if (valueType == null)
	throw new NullPointerException("valueType");
      this.lb = lb;
      this.ub = ub;
      this.valueType = valueType;
      this.prev = null;
      this.next = null;
    }

    /**
     * Returns a String that represents x according to the specification
     * of the underlying ValueType.
     */
    private String getDisplayValue(final int x)
    {
      return valueType.getDisplayValue(x);
    }

    /**
     * Returns a string valueType of this object (e.g. for debugging).
     * @return A string valueType of this object.
     */
    public String toString()
    {
      return "Contigous{valueType=" + valueType + ", lb=" + lb +
	", ub=" + ub + "}";
    }
  }

  /**
   * The head element of the sorted list of all contigous ranges; if
   * the range is not empty, this variable points to the contigous
   * range that contains the lowermost value that is in range.
   */
  private Contigous contigous_first;

  /**
   * The tail element of the sorted list of all contigous ranges; if
   * the range is not empty, this variable points to the contigous
   * range that contains the uppermost value that is in range.
   */
  private Contigous contigous_last;

  /**
   * the total size of this range
   */
  private long size = 0;

  /**
   * A key that represents an icon that is illustrates this Range instance.
   */
  private String iconKey = null;

  /**
   * Creates a range that is initially empty.
   */
  public Range() {}

  /**
   * Creates a range with initially a single contigous range.
   * @param lb The lower bound of the contigous range.
   * @param ub The upper bound of the contigous range.
   * @param valueType The ValueType for the contigous range.
   * @exception NullPointerException If valueType equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already exisiting range.
   */
  public Range(final int lb, final int ub, final ValueType valueType)
  {
    addContigous(lb, ub, valueType);
  }

  /**
   * Specifies a key for the icon, that is displayed together with each
   * instance of this range. The associated icon is to be stored via
   * UIManager.getDefaults().put(iconKey, icon).
   * @param iconKey The key of the icon to be displayed.
   */
  public void setIconKey(final String iconKey)
  {
    this.iconKey = iconKey;
  }

  /**
   * Returns the key for the icon, that is displayed together with each
   * instance of this range.
   * @return The key of the icon to be displayed.
   */
  public String getIconKey()
  {
    return iconKey;
  }

  private Contigous hint = null; // cached reference for better performance

  private static final long min_unsigned = signed_int_to_long(0x00000000);
  private static final long max_unsigned = signed_int_to_long(0xffffffff);

  /**
   * Adds a single contigous range to the total range.
   * @param lb The lower bound of the contigous range.
   * @param ub The upper bound of the contigous range.
   * @param valueType The ValueType for the contigous range.
   * @exception NullPointerException If valueType equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already exisiting range.
   */
  public void addContigous(final int lb, final int ub,
                           final ValueType valueType)
  {
    final long unsigned_lb = signed_int_to_long(lb);
    final long unsigned_ub = signed_int_to_long(ub);
    if ((lb < 0) && (ub >= 0)) // overlapping contigous; so split it up
      {
	addContigous(new Contigous(unsigned_lb, max_unsigned, valueType));
	addContigous(new Contigous(min_unsigned, unsigned_ub, valueType));
      }
    else
      addContigous(new Contigous(unsigned_lb, unsigned_ub, valueType));
  }

  /**
   * Adds a single value to the total range.
   * @param valueType The ValueType for the value.
   * @exception NullPointerException If valueType equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already exisiting range.
   */
  public void addContigous(final ValueType valueType)
  {
    if (valueType.getSize() != 1) {
      throw new IllegalArgumentException("valueType does not represent a single value");
    }
    final int value = valueType.getMinValue();
    addContigous(value, value, valueType);
  }

  /**
   * Adds a single enumeration value to the total range.
   * @param value The enumeration value to be added.
   * @param enumValue The enumeration value as string.
   * @exception NullPointerException If enumValue equals null.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    some already exisiting range.
   */
  public void addContigous(final int value, final String enumValue)
  {
    addContigous(value, value, new EnumType(value, enumValue));
  }

  /**
   * Inserts the contigous range into the sorted list of ranges.
   * @param insertion The contigous range to be inserted.
   * @exception IllegalArgumentException If the insertion range overlaps
   *    with another range.
   */
  private synchronized void addContigous(final Contigous insertion)
  {
    if (hint == null) // no hint given; start with contigous first
      hint = contigous_first;
    if (hint == null) // empty range; just start with a new list
      {
	insertion.prev = null;
	insertion.next = null;
	contigous_first = insertion;
	contigous_last = insertion;
      }
    else // search & insert
      {
	Contigous past = null; // previously visited contigous
	Contigous current = hint; // presently visited contigous
	while ((current != null) && (current.ub < insertion.lb))
	  {
	    past = current;
	    current = current.next;
	  }
	if (current == null) // must insert as new tail
	  if (past.ub < insertion.lb)
	    {
	      insertion.prev = past;
	      insertion.next = null;
	      past.next = insertion;
	      contigous_last = insertion;
	      hint = insertion;
	      return;
	    }
	  else
	    throw new IllegalStateException("overlapping range [1]" +
					    "(insertion=" + insertion +
					    "; past=" + past);
	while ((current != null) && (current.lb > insertion.ub))
	  {
	    past = current;
	    current = current.prev;
	  }
	if (current == null) // must insert as new head
	  if (past.lb > insertion.ub)
	    {
	      insertion.prev = null;
	      insertion.next = past;
	      past.prev = insertion;
	      contigous_first = insertion;
	      hint = insertion;
	      return;
	    }
	  else
	    throw new IllegalStateException("overlapping range [2]" +
					    "(insertion=" + insertion +
					    "; past=" + past);
	else // must insert between past and current
	  {
	    insertion.prev = current;
	    insertion.next = past;
	    past.prev = insertion;
	    current.next = insertion;
	    hint = insertion;
	    return;
	  }
      }
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
    final long unsigned_x = signed_int_to_long(x);
    if (hint == null) // no hint given; start with contigous first
      hint = contigous_first;
    if (hint == null) // empty range; we are done!
      return false;
    else
      {
	Contigous past = null; // previously visited contigous
	Contigous current = hint; // presently visited contigous
	while ((current != null) && (current.ub < unsigned_x))
	  {
	    past = current;
	    current = current.next;
	  }
	if (current == null)
	  {
	    hint = contigous_last;
	    return false; // above uppermost value
	  }
	while ((current != null) && (current.lb > unsigned_x))
	  {
	    past = current;
	    current = current.prev;
	  }
	if (current == null)
	  {
	    hint = contigous_first;
	    return false; // below lowermost value
	  }
	hint = current;
	return unsigned_x <= current.ub; // in or between ranges
      }
  }

  /**
   * Returns the lowermost value that is in range.
   * @return The lowermost value that is in range or null, if the range
   *    is empty.
   */
  public Integer lowermost()
  {
    if (contigous_first == null)
      return null;
    else
      {
	hint = contigous_first;
	return new Integer(long_to_signed_int(contigous_first.lb));
      }
  }

  /**
   * Returns the uppermost value that is in range.
   * @return The uppermost value that is in range or null, if the range
   *    is empty.
   */
  public Integer uppermost()
  {
    if (contigous_last == null)
      return null;
    else
      {
	hint = contigous_last;
	return new Integer(long_to_signed_int(contigous_last.ub));
      }
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
    final long unsigned_x = signed_int_to_long(x);
    if (hint == null) // no hint given; start with contigous first
      hint = contigous_first;
    if (hint == null) // empty range; we are done!
      return null;
    else
      {
	Contigous past = null; // previously visited contigous
	Contigous current = hint; // presently visited contigous
	while ((current != null) && (current.ub < unsigned_x))
	  {
	    past = current;
	    current = current.next;
	  }
	if (current == null)
	  {
	    hint = contigous_last;
	    return null; // x above uppermost value
	  }
	while ((current != null) && (current.lb > unsigned_x))
	  {
	    past = current;
	    current = current.prev;
	  }
	if (current == null) // x below lowermost value; return lowermost
	  {
	    hint = past;
	    return new Integer(long_to_signed_int(past.lb));
	  }
	else if (unsigned_x >= current.ub) // succ(x) above current
	  {
	    hint = current.next;
	    if (current.next == null)
	      return null; // x is already uppermost value
	    else
	      return new Integer(long_to_signed_int(current.next.lb));
	  }
	else
	  {
	    hint = current;
	    return new Integer(long_to_signed_int(unsigned_x + 1));
	  }
      }
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
    final long unsigned_x = signed_int_to_long(x);
    if (hint == null) // no hint given; start with contigous first
      hint = contigous_first;
    if (hint == null) // empty range; we are done!
      return null;
    else
      {
	Contigous past = null; // previously visited contigous
	Contigous current = hint; // presently visited contigous
	while ((current != null) && (current.lb > unsigned_x))
	  {
	    past = current;
	    current = current.prev;
	  }
	if (current == null)
	  {
	    hint = contigous_first;
	    return null; // x below lowermost value
	  }
	while ((current != null) && (current.ub < unsigned_x))
	  {
	    past = current;
	    current = current.next;
	  }
	if (current == null) // x above uppermost value; return uppermost
	  {
	    hint = past;
	    return new Integer(long_to_signed_int(past.ub));
	  }
	else if (unsigned_x <= current.lb) // pred(x) below current
	  {
	    hint = current.prev;
	    if (current.prev == null)
	      return null; // x is already lowermost value
	    else
	      return new Integer(long_to_signed_int(current.prev.ub));
	  }
	else
	  {
	    hint = current;
	    return new Integer(long_to_signed_int(unsigned_x - 1));
	  }
      }
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
    final long unsigned_x = signed_int_to_long(x);
    if (hint == null) // no hint given; start with contigous first
      hint = contigous_first;
    if (hint == null) // empty range; we are done!
      return null;
    else
      {
	Contigous past = null; // previously visited contigous
	Contigous current = hint; // presently visited contigous
	while ((current != null) && (current.ub < unsigned_x))
	  {
	    past = current;
	    current = current.next;
	  }
	if (current == null)
	  return null; // above uppermost value
	while ((current != null) && (current.lb > unsigned_x))
	  {
	    past = current;
	    current = current.prev;
	  }
	if (current == null)
	  {
	    hint = contigous_first;
	    return null; // below lowermost value
	  }
	if (unsigned_x <= current.ub)
	  {
	    hint = current;
	    return
              current.valueType.getDisplayValue(x);
	  }
	else
	  {
	    hint = current;
	    return null; // between two ranges
	  }
      }
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    final StringBuffer s = new StringBuffer();
    s.append("Range[Contigous-Ranges{");
    Contigous contigous = contigous_first;
    while (contigous != null)
      {
	s.append(contigous.toString());
	contigous = contigous.next;
      }
    s.append("}]");
    return s.toString();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
