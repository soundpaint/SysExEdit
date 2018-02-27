/*
 * @(#)AbstractContents.java 1.00 18/02/25
 *
 * Copyright (C) 2018 Jürgen Reuter
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
import javax.swing.UIManager;

/**
 * This class holds the structural information and the actual contents,
 * based on Range objects, of a single memory entry of the target device.
 * A memory entry may be part of a memory location or represent just one
 * memory location or cover a couple of memory locations (up to 32 bits).
 */
public abstract class AbstractContents implements Contents
{
  /** The current value. */
  private int value;

  /** The initial (default) value. */
  private int defaultValue;

  protected AbstractContents()
  {
    value = 0;
    defaultValue = 0;
  }

  /**
   * Adds a single representation to the pool of available representations.
   * Together, these representations define a union of single
   * representations.<BR>
   * The effective bit size is automatically changed, if necessary.
   * @param representation The representation to be added to the pool.
   * @exception NullPointerException If representation equals null.
   * @exception IllegalArgumentException If representation is not an
   *    instance of a compatible class.
   */
  public abstract void addRepresentation(final Representation representation);

  /**
   * Returns the Representation object that is currently selected for this
   * Contents object.
   * @return The Representation object that is currently selected for this
   *    Contents object or null, if there is no valid selection.
   */
  protected abstract Representation getSelectedRepresentation();

  /**
   * Returns a String that represents this contents' current value
   * according to the underlying Representation, or null, if the value
   * is not in range.
   * @return The String representation of this contents' current
   * value.
   */
  public String getDisplayValue()
  {
    final Representation representation = getSelectedRepresentation();
    if (representation != null) {
      return representation.getDisplayValue(getValue());
    } else {
      return null;
    }
  }

  /**
   * Returns an icon that represents this content's type.
   * @return The icon.
   */
  public Icon getIcon()
  {
    final Representation representation = getSelectedRepresentation();
    if (representation != null) {
      final String iconKey = representation.getIconKey();
      if (iconKey != null) {
        return UIManager.getIcon(iconKey);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /*
   * Sets the default value for this contents. This value is shared among
   * all ranges; thus it even may be out of the currently selected or
   * any other range.
   * @param defaultValue The default value.
   * @see #reset
   */
  public void setDefaultValue(final int defaultValue)
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

  /*
   * Sets the value for this contents. This value is shared among
   * all ranges; thus it even may be out of the currently selected or
   * any other range.
   * @param value The value.
   */
  public void setValue(final int value)
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
    setValue(defaultValue);
  }

  /**
   * Increments the contents of this node, if possible.
   * @param model The tree model of the tree that contains this node.
   */
  public void increment()
  {
    final Representation representation = getSelectedRepresentation();
    if (representation.isEnumerable())
      {
        final Integer succValue = representation.succ(getValue());
        if (succValue != null)
          setValue(succValue);
      }
  }

  /**
   * Decrements the contents of this node.
   * @param model The tree model of the tree that contains this node.
   */
  public void decrement()
  {
    final Representation representation = getSelectedRepresentation();
    if (representation.isEnumerable())
      {
        final Integer predValue = representation.pred(getValue());
        if (predValue != null)
          setValue(predValue);
      }
  }

  /**
   * Sets the contents of this node to the uppermost value that is in range.
   * @param model The tree model of the tree that contains this node.
   */
  public void uppermost()
  {
    setValue(getSelectedRepresentation().uppermost());
  }

  /**
   * Sets the contents of this node to the lowermost value that is in range.
   * @param model The tree model of the tree that contains this node.
   */
  public void lowermost()
  {
    setValue(getSelectedRepresentation().lowermost());
  }

  /**
   * Selects a representation from the pool of available representations.
   * @param selectionID The number of the representation with respect to
   *    the order the representations were added to this Contents object.
   *    The first representation has the number 0.
   * @exception IndexOutOfBoundsException If selectionID is below 0 or
   *    above or equal to the number of representations of the union.
   */
  public abstract void setSelectedRepresentation(final int selectionID);

  /**
   * Sets the effective bit size of this contents. The effective bit size
   * must be equal to or greater than the required bit size that is
   * implicitly or explicitly defined through the structure of the
   * underlying information. Those bits that remain unused are supposed to
   * be constantly zero.
   * @param bit_size The effective bit size of this contents.
   * @exception IllegalArgumentException If bit_size is below zero or
   *    below the required bit size.
   */
  public abstract void setBitSize(final int bit_size);

  /**
   * Returns the current effective bit size of this Contents object.
   * This can be set by method setBitSize.
   * Initially, the effective bit size is set to the required bit size of
   * the underlying structure.
   * @return The current effective bit size.
   */
  public abstract byte getBitSize();

  /**
   * Returns a representation of the contents value according to the
   * underlying bit layout.
   * @return The array of bits that represents the contents value. For
   *    performance reasons, the return value is actually not an array of
   *    bits, but rather an array of int values with each int value
   *    holding 32 bits. The least significant bit is stored in the least
   *    significant bit of field 0 of the return value.
   */
  public abstract int[] toBits();

  public String toString()
  {
    final String displayValue = getDisplayValue();
    return
      displayValue != null ? displayValue : ValueType.DISPLAY_VALUE_UNKNOWN;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
