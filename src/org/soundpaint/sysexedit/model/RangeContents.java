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

package org.soundpaint.sysexedit.model;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.SwingUtilities;

/**
 * This class holds the structural information and the actual contents,
 * based on Range objects, of a single memory entry of the target device.
 * A memory entry may be part of a memory location or represent just one
 * memory location or cover a couple of memory locations (up to 32 bits).
 */
public class RangeContents extends AbstractContents
{
  /**
   * The minimally required size of this contents.
   *
   * Memory locations are typically distributed sparse in the address space
   * of the device; thus we associate each memory cell with its address
   * rather than allocating a huge array with many empty memory cells.
   */
  private final byte min_bit_size;

  /** The effective size of this contents in bits (0..32). */
  private byte bit_size;

  /** The sparse type of this contents. */
  private final SparseType sparseType;

  /** The editor for entering a value of this range contents. */
  private final Editor editor;

  private Vector<ContentsChangeListener> listeners;

  private RangeContents()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a new RangeContents of the specified sparse type.
   * @param sparseType The SparseType object that specifies
   *    the sparse type of this RangeContents.
   * @exception NullPointerException If sparseType equals null.
   */
  public RangeContents(final SparseType sparseType)
  {
    this(sparseType, null);
  }

  /**
   * Creates a new RangeContents of the specified sparse type.
   * @param sparseType The SparseType object that specifies
   *    the sparse type of this RangeContents.
   * @param iconKey If non-null, overrides the associated
   * sparse type's iconKey.
   * @exception NullPointerException If sparseType equals null.
   */
  public RangeContents(final SparseType sparseType,
                       final String iconKey)
  {
    super(iconKey);
    if (sparseType == null) {
      throw new NullPointerException("sparseType");
    }
    this.sparseType = sparseType;
    min_bit_size = sparseType.getRequiredBitSize();
    bit_size = (byte)Math.max(min_bit_size, bit_size);
    editor = new DropDownEditor();
    listeners = new Vector<ContentsChangeListener>();
    final KeyListener kl = new KeyAdapter()
      {
        public void keyTyped(KeyEvent e)
        {
          if (e.getKeyChar() == '\n') {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  final Contents newContents = editor.getSelectedContents();
                  editingPathValueChanged(newContents);
                }
              });
          }
        }
      };
    ((Component)editor).addKeyListener(kl);
  }

  public void addContentsChangeListener(final ContentsChangeListener listener)
  {
    if (listener == null) {
      throw new NullPointerException("listener");
    }
    listeners.add(listener);
  }

  private void editingPathValueChanged(final Contents newContents)
  {
    // Here, we actually update the map (rather than in
    // MapNode#setUserObject()).
    setValue(newContents.getValue());

    for (final ContentsChangeListener listener : listeners) {
      listener.editingPathValueChanged(this);
    }
  }

  /**
   * Creates a SparseType that represents a bit string.<BR>
   * [PENDING: Should this be moved to SparseType?]
   */
  private static SparseType getBitStringType(final int amount)
  {
    if ((amount < 0) || (amount > 15))
      throw new IllegalArgumentException("amount");
    final FlagsType unusedType = new FlagsType();
    final SparseType sparseType =
      new Range(SparseType.GENERIC_ICON_KEY, 0, (1 << amount) - 1,
                unusedType);
    return sparseType;
  }

  /**
   * Creates a new RangeContents that represents a couple of unused bits.
   * @param amount The amount of unused bits.
   * @exception IllegalArgumentException If amount is below zero or above
   *    the upper limit of 15.
   */
  public RangeContents(final int amount)
  {
    this(amount, null);
  }

  /**
   * Creates a new RangeContents that represents a couple of unused bits.
   * @param amount The amount of unused bits.
   * @param iconKey If non-null, overrides the associated
   * SparseType's iconKey.
   * @exception IllegalArgumentException If amount is below zero or above
   *    the upper limit of 15.
   */
  public RangeContents(final int amount, final String iconKey)
  {
    this(getBitStringType(amount));
    setBitSize(amount);
    setDefaultValue(new Integer(0));
  }

  /**
   * Returns the sparse type of this RangeContents object.
   * @return The SparseType object of this RangeContents object.
   */
  protected SparseType getSparseType()
  {
    return sparseType;
  }

  /**
   * Sets the effective bit size of this RangeContents. The effective bit
   * size is a value in the range 0..32 which must be equal to or
   * greater than the required bit size of this RangeContents. The
   * unused bits are supposed to be constantly zero.
   * @exception IllegalArgumentException If bit_size is out of range.
   */
  public void setBitSize(final int bit_size)
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
   * the underlying sparse type.
   * @return The current effective bit size.
   */
  public byte getBitSize()
  {
    return bit_size;
  }

  /**
   * Returns a representation of the RangeContents value according to the
   * underlying bit layout.
   * @return The array of bits that represents the contents value. For
   *    performance reasons, the return value is actually not an array of
   *    bits, but rather an array of int values with each int value
   *    holding 32 bits. The least significant bit is stored in the least
   *    significant bit of field 0 of the return value.
   */
  public int[] toBits()
  {
    final int[] bits = new int[1];
    bits[0] = getValue();
    return bits;
  }

  public Component getEditor()
  {
    editor.clear();
    int selectedIndex = -1;
    int index = -1;
    Integer value = sparseType.lowermost();
    while (value != null) {
      final Contents contents =
        new RangeContents(new Range(sparseType.getIconKey(),
                                    value, value,
                                    new EnumType(value, new String[] {
                                        sparseType.getDisplayValue(value)})));
      contents.setValue(value);
      editor.addContents(contents);
      index++;
      if (value == getValue()) {
        selectedIndex = index;
      }
      value = sparseType.succ(value);
    }
    if (selectedIndex > 0) {
      editor.setSelectedIndex(selectedIndex);
    }
    return (Component)editor;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
