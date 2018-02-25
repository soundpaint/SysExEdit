/*
 * @(#)RangeContents.java 1.00 98/01/31
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

import java.util.Vector;

/**
 * This class holds the structural information and the actual contents,
 * based on Range objects, of a single memory entry of the target device.
 * A memory entry may be part of a memory location or represent just one
 * memory location or cover a couple of memory locations (up to 32 bits).
 */
public class RangeContents extends AbstractContents
{
  /*
   * Memory locations are typically distributed sparse in the address space
   * of the device; thus we associate each memory cell with its address
   * rather than allocating a huge array with many empty memory cells.
   */
  private byte min_bit_size; // the minimally required size of this contents
  private byte bit_size; // the effective size of this contents in bits (0..32)
  private int value; // the current value
  private int defaultValue; // the initial (default) value
  private Vector<Range> ranges; // Range objects that allow implementing range union
  private int selectionID; // the currently valid range

  /**
   * Creates a new RangeContents object with initially no range (and thus no
   * range selection).
   */
  public RangeContents()
  {
    this(null);
  }

  /**
   * Creates a new RangeContents for the given Range object. The range is
   * selected as currently valid range.
   * @param range The Range object that holds range and representation
   *    information for this RangeContents.
   * @exception NullPointerException If range equals null.
   */
  public RangeContents(Range range)
  {
    min_bit_size = 0;
    bit_size = 0;
    value = 0;
    defaultValue = 0;
    ranges = new Vector<Range>();
    selectionID = -1;
    if (range != null)
      {
	addRepresentation(range);
	// if (bit_size == 0)
	//  throw new IllegalArgumentException("range empty");
	// [PENDING: needs somewhat like:
	// if (bit_size == 0) setEditable(false); ]
	setSelectedRepresentation(0);
      }
  }

  /**
   * Creates a Range object that represents a bit string.<BR>
   * [PENDING: Should this be moved into class Range?]
   */
  private static Range getBitStringRange(final int amount)
  {
    if ((amount < 0) || (amount > 15))
      throw new IllegalArgumentException("amount");
    final FlagsType unusedType = new FlagsType();
    final Range range = new Range(0, (1 << amount) - 1, unusedType);
    range.setIconKey("internal-unknown");
    return range;
  }

  /**
   * Creates a new RangeContents that represents a couple of unused bits.
   * @param amount The amount of unused bits.
   * @exception IllegalArgumentException If amount is below zero or above
   *    the upper limit of 15.
   */
  public RangeContents(int amount)
  {
    this(getBitStringRange(amount));
    setBitSize(amount);
    setDefaultValue(new Integer(0));
  }

  /**
   * Adds a single range to the pool of available ranges.
   * Together, these ranges define a union of single ranges.<BR>
   * The effective bit size is automatically changed, if necessary.
   * @param representation The range to be added to the pool.
   * @exception NullPointerException If range equals null.
   * @exception IllegalArgumentException If range is not an
   *    instance of class Range.
   */
  public void addRepresentation(Representation representation)
  {
    if (representation == null)
      throw new NullPointerException("representation");
    if (!(representation instanceof Range))
      throw new IllegalArgumentException("representation not a range");
    ranges.addElement((Range)representation);
    min_bit_size =
      (byte)Math.max(min_bit_size, representation.getRequiredBitSize());
    bit_size = (byte)Math.max(min_bit_size, bit_size);
  }

  /**
   * Returns the Range object that is currently selected for this
   * RangeContents object.
   * @return The Range object that is currently selected for this
   *    RangeContents object or null, if there is no valid selection.
   */
  public Representation getSelectedRepresentation()
  {
    if (selectionID == -1)
      return null; // no range selected
    else
      return ranges.elementAt(selectionID);
  }

  /**
   * Selects a range from the pool of available ranges.
   * @param selectionID The number of the range with respect to the order
   *    the ranges were added to this RangeContents object. The first range
   *    has the number 0.
   * @exception IndexOutOfBoundsException If selectionID is below 0 or above
   *    or equal to the number of ranges of the union.
   */
  public void setSelectedRepresentation(int selectionID)
  {
    if ((selectionID < 0) || (selectionID >= ranges.size()))
      throw new IndexOutOfBoundsException("selectionID");
    this.selectionID = selectionID;
  }

  /**
   * Sets the effective bit size of this contents. The effective bit size
   * is a value in the range 0..32 which must be equal to or greater than
   * the required bit size of the Range of this RangeContents. The unused
   * bits are supposed to be constantly zero.
   * @exception IllegalArgumentException If bit_size is out of range.
   */
  public void setBitSize(int bit_size)
  {
    if ((bit_size < 0) || (bit_size > 32) ||
	(min_bit_size > bit_size))
      throw new IllegalArgumentException("bit_size out of range");
    else
      this.bit_size = (byte)bit_size;
  }

  /**
   * Returns the current effective bit size of this RangeContents object.
   * This can be set by method setBitSize.<BR>
   * Initially, the effective bit size is set to the required bit size of
   * the underlying Range object.
   * @return The current effective bit size.
   */
  public byte getBitSize()
  {
    return bit_size;
  }

  /*
   * Sets the value for this contents. This value is shared among
   * all ranges; thus it even may be out of the currently selected or
   * any other range.
   * @param value The value.
   */
  public void setValue(int value)
  {
    this.value = value;
  }

  /**
   * Returns the current value. This value is shared among
   * all ranges; thus it even may be out of the currently selected or
   * any other range.
   * @return The current value.
   */
  public int getValue()
  {
    return value;
  }

  /**
   * Resets the contents value to its default value.
   * @see #setDefaultValue
   */
  public void reset()
  {
    value = defaultValue;
  }

  /*
   * Sets the default value for this contents. This value is shared among
   * all ranges; thus it even may be out of the currently selected or
   * any other range.
   * @param defaultValue The default value.
   * @see #reset
   */
  public void setDefaultValue(int defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  /**
   * Returns the current default value. This value is shared among
   * all ranges; thus it even may be out of the currently selected or
   * any other range.
   * @return The current default value.
   */
  public int getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * Returns a representation of the contents value according to the
   * underlying bit layout. 
   * @return The array of bits that represents the contents value. For
   *    performance reasons, the return value is actually not an array of
   *    bits, but rather an array of int values with each int value
   *    holding 32 bits. The least significant bit is stored in the least
   *    significant bit of field 0 of the return value.
   */
  public int[] toBits()
  {
    int[] bits = new int[1];
    bits[1] = value;
    return bits;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
