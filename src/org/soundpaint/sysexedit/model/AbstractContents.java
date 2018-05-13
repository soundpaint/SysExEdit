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

package org.soundpaint.sysexedit.model;

import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * This class holds the structural information and the actual
 * contents, based on a sparse type of a single entry in the target
 * device's model.  A memory entry may be part of a memory location or
 * represent just one memory location or cover a couple of memory
 * locations (up to 32 bits).
 */
public abstract class AbstractContents implements Contents
{
  /** If non-null, overrides associated sparse type's iconKey. */
  private final String iconKey;

  /** The current value. */
  private int value;

  /** The initial (default) value. */
  private int defaultValue;

  protected AbstractContents()
  {
    this(null);
  }

  /**
   * @param iconKey If non-null, overrides the associated
   * sparse type's iconKey.
   */
  protected AbstractContents(final String iconKey)
  {
    this.iconKey = iconKey;
    value = 0;
    defaultValue = 0;
  }

  /**
   * Returns the SparseType object for this Contents object.
   * @return The SparseType object for this Contents object.
   */
  protected abstract SparseType getSparseType();

  /**
   * Returns a String that represents this contents' current value
   * according to the underlying SparseType, or null, if the
   * associated sparse type does not contain this value.
   * @return The String representation of this contents' current
   * value.
   */
  public String getDisplayValue()
  {
    final SparseType sparseType = getSparseType();
    if (sparseType != null) {
      return sparseType.getDisplayValue(getValue());
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
    final SparseType sparseType = getSparseType();
    if (sparseType == null)
      return null;
    final String iconKey =
      this.iconKey != null ? this.iconKey : sparseType.getIconKey();
    if (iconKey == null)
      return null;
    final Icon icon = UIManager.getIcon(iconKey);
    if (icon != null)
      return icon;
    System.err.println("[WARNING: icon not found: " + iconKey + "]");
    System.err.flush();
    return UIManager.getIcon(SparseType.GENERIC_ICON_KEY);
  }

  /**
   * Sets the default value for this contents.  This value is not
   * bound to a specific subrange of the associated sparse type; thus
   * it even may be out of the currently selected or any other
   * subrange.
   * @param defaultValue The default value.
   * @see #reset
   */
  public void setDefaultValue(final int defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  /**
   * Returns the current default value.  This value is not bound to a
   * specific subrange of the associated sparse type; thus it even may
   * be out of the currently selected or any other subrange.
   * @return The current default value.
   */
  public int getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * Sets the value for this contents.  This value is not bound to a
   * specific subrange of the associated sparse type; thus it even may
   * be out of the currently selected or any other subrange.
   * @param value The value.
   */
  public void setValue(final int value)
  {
    this.value = value;
  }

  /**
   * Returns the current value.  This value is not bound to a specific
   * subrange of the associated sparse type; thus it even may be out
   * of the currently selected or any other subrange.
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
   */
  public void increment()
  {
    final SparseType sparseType = getSparseType();
    if (sparseType.isEnumerable())
      {
        final Integer succValue = sparseType.succ(getValue());
        if (succValue != null)
          setValue(succValue);
      }
  }

  /**
   * Decrements the contents of this node.
   */
  public void decrement()
  {
    final SparseType sparseType = getSparseType();
    if (sparseType.isEnumerable())
      {
        final Integer predValue = sparseType.pred(getValue());
        if (predValue != null)
          setValue(predValue);
      }
  }

  /**
   * Sets the contents of this node to the uppermost value that the
   * associated sparse type contains.
   */
  public void uppermost()
  {
    setValue(getSparseType().uppermost());
  }

  /**
   * Sets the contents of this node to the lowermost value that the
   * associated sparse type contains.
   */
  public void lowermost()
  {
    setValue(getSparseType().lowermost());
  }

  /**
   * Sets the effective bit size of this contents. The effective bit
   * size must be equal to or greater than the required bit size that
   * is implicitly or explicitly defined through the structure of the
   * associated sparse type.  Those bits that remain unused are
   * supposed to be constantly zero.
   * @param bit_size The effective bit size of this contents.
   * @exception IllegalArgumentException If bit_size is below zero or
   *    below the required bit size.
   */
  public abstract void setBitSize(final int bit_size);

  /**
   * Returns the current effective bit size of this Contents object.
   * The value is set with method #setBitSize().
   * Initially, the effective bit size is set to the required bit size of
   * the associated sparse type.
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
