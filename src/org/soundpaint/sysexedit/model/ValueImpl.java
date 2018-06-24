/*
 * @(#)ValueImpl.java 1.00 98/01/31
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

import java.awt.Component;
import java.util.Vector;

import org.soundpaint.sysexedit.gui.JValue;

/**
 * This class holds the structural information and actual value, based
 * on a sparse type of a single entry in the target device's model.
 * An entry may be part of a memory location or represent just one
 * memory location or cover a couple of memory locations, up to 32
 * bits of size.
 */
public class ValueImpl extends AbstractValue
{
  /**
   * The minimally required size of this value.  Memory locations are
   * typically distributed sparse in the address space of the device;
   * thus we associate each memory cell with its address rather than
   * allocating a huge array with many empty memory cells.
   */
  private final byte minBitSize;

  /** The effective size of this value in bits (0..32). */
  private byte bitSize;

  public ValueImpl(final SparseType sparseType, final int defaultValue)
  {
    this(null, sparseType, defaultValue);
  }

  public ValueImpl(final String iconKey, final SparseType sparseType,
                   final int defaultValue)
  {
    this(iconKey, sparseType, null, defaultValue);
  }

  public ValueImpl(final SparseType sparseType, final String label,
                   final int defaultValue)
  {
    this(null, sparseType, label, defaultValue);
  }

  public ValueImpl(final String iconKey, final SparseType sparseType,
                   final String label, final int defaultValue)
  {
    this(iconKey, sparseType, null, label, defaultValue);
  }

  public ValueImpl(final SparseType sparseType, final String description,
                   final String label, final int defaultValue)
  {
    this(null, sparseType, description, label, defaultValue);
  }

  /**
   * Creates a new Value object of the specified sparse type and
   * description, label, and icon key.
   * @param description An optional informal description of this
   * Value.  Useful e.g. as tooltip in the GUI.
   * @param label The label of this node.
   * @param sparseType The SparseType object that specifies
   *    the sparse type of this Value object.
   * @param iconKey If non-null, overrides the associated
   * sparse type's iconKey.
   * @param defaultValue The underlying numerical default value.
   * @exception NullPointerException If sparseType equals null.
   */
  public ValueImpl(final String iconKey, final SparseType sparseType,
                   final String description, final String label,
                   final int defaultValue)
  {
    super(iconKey, description, label, defaultValue, sparseType);
    if (sparseType == null) {
      throw new NullPointerException("sparseType");
    }
    minBitSize = sparseType.getRequiredBitSize();
    bitSize = (byte)Math.max(minBitSize, bitSize);
  }

  /**
   * Creates a new Value object that represents unused bits.
   * @param amount The amount of unused bits.
   * @param label The label of this node.
   * @exception IllegalArgumentException If amount of unused bits is
   *    below zero or above the upper limit of 15.
   */
  public ValueImpl(final int amount, final String label)
  {
    this(amount, label, 0);
  }

  /**
   * Creates a new Value object that represents unused bits.
   * @param amount The amount of unused bits.
   * @param label The label of this node.
   * @param defaultValue The underlying numerical default value.
   * @exception IllegalArgumentException If amount of unused bits is
   *    below zero or above the upper limit of 15.
   */
  public ValueImpl(final int amount, final String label, final int defaultValue)
  {
    this(amount, null, label, defaultValue);
  }

  /**
   * Creates a new Value object that represents unused bits.
   * @param amount The amount of unused bits.
   * @param iconKey If non-null, overrides the associated
   * SparseType's iconKey.
   * @param label The label of this node.
   * @param defaultValue The underlying numerical default value.
   * @exception IllegalArgumentException If amount of unused bits is
   *    below zero or above the upper limit of 15.
   */
  public ValueImpl(final int amount, final String iconKey, final String label,
                   final int defaultValue)
  {
    this(iconKey, getBitStringType(amount), label, defaultValue);
    setBitSize(amount);
  }

  /**
   * Creates a SparseType that represents a bit string.<BR>
   * [PENDING: Should this be moved to SparseType?]
   */
  private static SparseType getBitStringType(final int amount)
  {
    if ((amount < 0) || (amount > 15))
      throw new IllegalArgumentException("amount");
    final BitMaskRenderer bitMaskRenderer = new BitMaskRenderer();
    final SparseType sparseType =
      new SparseType(SparseType.GENERIC_ICON_KEY, 0, (1 << amount) - 1,
                     bitMaskRenderer);
    return sparseType;
  }

  /**
   * Sets the effective bit size of this Value object.  The effective
   * bit size is a value in the range 0..32 which must be equal to or
   * greater than the required bit size of this Value object.  The
   * unused bits are assumed to always be zero.
   * @exception IllegalArgumentException If bitSize is out of range.
   */
  public void setBitSize(final int bitSize)
  {
    if ((bitSize < 0) || (bitSize > 32) ||
        (minBitSize > bitSize))
      throw new IllegalArgumentException("bitSize out of range");
    else
      this.bitSize = (byte)bitSize;
  }

  /**
   * Returns the current effective bit size of this Value object.
   * This can be set by method #setBitSize().<BR>
   * Initially, the effective bit size is set to the required bit size of
   * the underlying sparse type.
   * @return The current effective bit size.
   */
  public byte getBitSize()
  {
    return bitSize;
  }

  public Editor createEditor()
  {
    int index = -1;
    Integer numericalValue = sparseType.lowermost();
    final Vector<JValue> values = new Vector<JValue>();
    while (numericalValue != null) {
      final String displayValue = sparseType.getDisplayValue(numericalValue);
      final EnumRenderer enumRenderer =
        new EnumRenderer(new String[] { displayValue });
      final SparseType editorSparseType =
        new SparseType(sparseType.getIconKey(),
                       numericalValue, numericalValue, enumRenderer);
      final JValue value = new JValue(this, numericalValue);
      values.add(value);
      index++;
      numericalValue = sparseType.succ(numericalValue);
    }
    // TODO: Editor can also be a SpinnerEditor or anything else,
    // depending on the underlying SparseType.
    final Editor editor = new DropDownEditor();
    editor.setSelectableValues(values);
    editor.setSelectionByNumericalValue(getDefaultValue());
    return editor;
  }

  public String toString()
  {
    return "ValueImpl[minBitSize=" + minBitSize + ", bitSize=" + bitSize +
      ", iconKey=" + getIconKey() + ", description=" + getDescription() +
      ", label=" + getLabel() + ", defaultValue=" + getDefaultValue() +
      ", sparseType=" + getSparseType() + "]";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
