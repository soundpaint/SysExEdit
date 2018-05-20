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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.SwingUtilities;

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

  /** The data type of this value. */
  private final SparseType sparseType;

  /** The editor for entering a value. */
  private final Editor editor;

  private Vector<ValueChangeListener> listeners;

  private ValueImpl()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a new Value object of the specified sparse type.
   * @param sparseType The SparseType object that specifies
   *    the sparse type of this Value object.
   * @exception NullPointerException If sparseType equals null.
   */
  public ValueImpl(final SparseType sparseType)
  {
    this(sparseType, null);
  }

  /**
   * Creates a new Value object of the specified sparse type.
   * @param sparseType The SparseType object that specifies
   *    the sparse type of this Value object.
   * @param iconKey If non-null, overrides the associated
   * sparse type's iconKey.
   * @exception NullPointerException If sparseType equals null.
   */
  public ValueImpl(final SparseType sparseType,
                   final String iconKey)
  {
    super(iconKey);
    if (sparseType == null) {
      throw new NullPointerException("sparseType");
    }
    this.sparseType = sparseType;
    minBitSize = sparseType.getRequiredBitSize();
    bitSize = (byte)Math.max(minBitSize, bitSize);

    // TODO: Editor can also be a SpinnerEditor or anything else,
    // depending on the underlying SparseType.
    editor = new DropDownEditor();

    listeners = new Vector<ValueChangeListener>();
    final KeyListener keyListener = new KeyAdapter()
      {
        public void keyTyped(KeyEvent e)
        {
          if (e.getKeyChar() == '\n') {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  final Value newValue = editor.getSelectedValue();
                  editingPathValueChanged(newValue);
                }
              });
          }
        }
      };
    ((Component)editor).addKeyListener(keyListener);
  }

  public void addValueChangeListener(final ValueChangeListener listener)
  {
    if (listener == null) {
      throw new NullPointerException("listener");
    }
    listeners.add(listener);
  }

  private void editingPathValueChanged(final Value newValue)
  {
    // Here, we actually update the map (rather than in
    // MapNode#setUserObject()).
    setNumericalValue(newValue.getNumericalValue());

    for (final ValueChangeListener listener : listeners) {
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
    final BitMaskRenderer bitMaskRenderer = new BitMaskRenderer();
    final SparseType sparseType =
      new SparseTypeImpl(SparseType.GENERIC_ICON_KEY, 0, (1 << amount) - 1,
                         bitMaskRenderer);
    return sparseType;
  }

  /**
   * Creates a new Value object that represents unused bits.
   * @param amount The amount of unused bits.
   * @exception IllegalArgumentException If amount of unused bits is
   *    below zero or above the upper limit of 15.
   */
  public ValueImpl(final int amount)
  {
    this(amount, null);
  }

  /**
   * Creates a new Value object that represents unused bits.
   * @param amount The amount of unused bits.
   * @param iconKey If non-null, overrides the associated
   * SparseType's iconKey.
   * @exception IllegalArgumentException If amount of unused bits is
   *    below zero or above the upper limit of 15.
   */
  public ValueImpl(final int amount, final String iconKey)
  {
    this(getBitStringType(amount));
    setBitSize(amount);
    setDefaultValue(new Integer(0));
  }

  /**
   * Returns the sparse type of this Value object.
   * @return The SparseType object of this Value object.
   */
  protected SparseType getSparseType()
  {
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

  /**
   * Returns a numerical representation of this Value object according
   * to the underlying bit layout.
   * @return The array of bits that represents the numerical value.
   *    For performance reasons, the return value is actually not an
   *    array of bits, but rather an array of integer values with each
   *    integer value holding 32 bits.  The least significant bit is
   *    stored in the least significant bit of field 0 of the return
   *    value.
   */
  public int[] toBits()
  {
    final int[] bits = new int[1];
    bits[0] = getNumericalValue();
    return bits;
  }

  public Component getEditor()
  {
    editor.clear();
    int selectedIndex = -1;
    int index = -1;
    Integer numericalValue = sparseType.lowermost();
    while (numericalValue != null) {
      final EnumRenderer enumRenderer =
        new EnumRenderer(numericalValue, new String[]
          {
            sparseType.getDisplayValue(numericalValue)
          });
      final SparseTypeImpl editorSparseType =
        new SparseTypeImpl(sparseType.getIconKey(),
                           numericalValue, numericalValue, enumRenderer);
      final Value value = new ValueImpl(editorSparseType);
      value.setNumericalValue(numericalValue);
      editor.addSelectableValue(value);
      index++;
      if (numericalValue == getNumericalValue()) {
        selectedIndex = index;
      }
      numericalValue = sparseType.succ(numericalValue);
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
