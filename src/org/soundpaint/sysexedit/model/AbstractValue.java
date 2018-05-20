/*
 * @(#)AbstractValue.java 1.00 18/02/25
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
 * This class holds the structural information and the actual value,
 * based on a sparse type of a single entry in the target device's
 * model.  A memory entry may be part of a memory location or
 * represent just one memory location or cover a couple of memory
 * locations (up to 32 bits).
 */
public abstract class AbstractValue implements Value
{
  /** If non-null, overrides associated sparse type's iconKey. */
  private final String iconKey;

  /** The current value. */
  private int value;

  /** The initial (default) value. */
  private int defaultValue;

  /**
   * Creates a new Value object, using the associated sparse type's
   * icon for type display.
   */
  protected AbstractValue()
  {
    this(null);
  }

  /**
   * Creates a new Value object with the specified icon to use for
   * type display.
   * @param iconKey If non-null, overrides the associated
   * sparse type's iconKey.
   */
  protected AbstractValue(final String iconKey)
  {
    this.iconKey = iconKey;
    value = 0;
    defaultValue = 0;
  }

  /**
   * Returns the underlying SparseType object associated with this
   * Value object.
   * @return The underlying SparseType object associated with this
   * Value object.
   */
  protected abstract SparseType getSparseType();

  /**
   * Returns a String that represents this Value object's underlying
   * numerical value according to the underlying SparseType, or null,
   * if the value is out of range with respect to the sparse type.
   * @return A String representation for this Value object's
   * underlying numerical value.
   */
  public String getDisplayValue()
  {
    final SparseType sparseType = getSparseType();
    if (sparseType != null) {
      return sparseType.getDisplayValue(getNumericalValue());
    } else {
      return null;
    }
  }

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
   * Sets the underlying numerical value to apply when this Value
   * instance is reset.  This value is not bound to a specific value
   * range of the associated sparse type; thus it even may be out of
   * the currently selected or any other value range.
   * @param defaultValue The default value.
   * @see #reset
   */
  public void setDefaultValue(final int defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  /**
   * Returns the underlying numerical value to apply when this Value
   * instance is reset.  This value is not bound to a specific value
   * range of the associated sparse type; thus it even may be out of
   * the currently selected or any other value range.
   * @return The current default value.
   */
  public int getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * Sets the underlying numerical value of this Value object.  The
   * value is not bound to a specific value range of the associated
   * sparse type; thus it even may be out of any value range.
   * @param value The underlying numerical value.
   */
  public void setNumericalValue(final int value)
  {
    this.value = value;
  }

  /**
   * Returns the underlying numerical value of this Value object.  The
   * value is not bound to a specific value range of the associated
   * sparse type; thus it even may be out of any value range.
   * @return The underlying numerical value.
   */
  public int getNumericalValue()
  {
    return value;
  }

  public void reset()
  {
    setNumericalValue(defaultValue);
  }

  public void increment()
  {
    final SparseType sparseType = getSparseType();
    if (sparseType.isEnumerable())
      {
        final Integer succValue = sparseType.succ(getNumericalValue());
        if (succValue != null)
          setNumericalValue(succValue);
      }
  }

  public void decrement()
  {
    final SparseType sparseType = getSparseType();
    if (sparseType.isEnumerable())
      {
        final Integer predValue = sparseType.pred(getNumericalValue());
        if (predValue != null)
          setNumericalValue(predValue);
      }
  }

  /**
   * Sets the underlying numerical value to the uppermost value that
   * is valid for the associated sparse type.
   */
  public void uppermost()
  {
    setNumericalValue(getSparseType().uppermost());
  }

  /**
   * Sets the underlying numerical value to the lowermost value that
   * is valid for the associated sparse type.
   */
  public void lowermost()
  {
    setNumericalValue(getSparseType().lowermost());
  }

  public abstract void setBitSize(final int bitSize);

  public abstract byte getBitSize();

  public abstract int[] toBits();

  public String toString()
  {
    final String displayValue = getDisplayValue();
    return
      displayValue != null ?
      displayValue : ValueRangeRenderer.DISPLAY_VALUE_UNKNOWN;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
