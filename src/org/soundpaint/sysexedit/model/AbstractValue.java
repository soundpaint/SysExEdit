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

  /**
   * An optional informal description of this Value.  Useful e.g. as
   * tooltip in a GUI.
   */
  private final String description;

  /** An optional label for this value. */
  private final String label;

  /** The initial (default) value. */
  private final int defaultValue;

  /** The data type of this value. */
  protected final SparseType sparseType;

  private AbstractValue()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  /**
   * Creates a new Value object with the specified icon to use for
   * type display.
   * @param iconKey If non-null, overrides the associated
   * underlying value type's iconKey.
   * @param description An optional informal description of this
   * Value.  Useful e.g. as tooltip in the GUI.
   * @param label The label of this node.
   * @param defaultValue The default value.
   */
  protected AbstractValue(final String iconKey, final String description,
                          final String label, final int defaultValue,
                          final SparseType sparseType)
  {
    this.iconKey = iconKey;
    this.description = description;
    this.label = label;
    this.defaultValue = defaultValue;
    this.sparseType = sparseType;
  }

  /**
   * Returns the underlying SparseType object associated with this
   * Value object.
   * @return The underlying SparseType object associated with this
   * Value object.
   */
  public SparseType getSparseType()
  {
    return sparseType;
  }

  public String getDisplayValue(final int numericalValue)
  {
    final SparseType sparseType = getSparseType();
    if (sparseType != null) {
      return sparseType.getDisplayValue(numericalValue);
    } else {
      return null;
    }
  }

  protected String getIconKey()
  {
    return
      this.iconKey != null ?
      this.iconKey :
      sparseType.getIconKey();
  }

  public Icon getIcon()
  {
    final SparseType sparseType = getSparseType();
    if (sparseType == null)
      return null;
    final String iconKey = getIconKey();
    if (iconKey == null)
      return null;
    final Icon icon = UIManager.getIcon(iconKey);
    if (icon != null)
      return icon;
    System.err.println("[WARNING: icon not found: " + iconKey + "]");
    System.err.flush();
    return UIManager.getIcon(SparseType.GENERIC_ICON_KEY);
  }

  public String getDescription()
  {
    return description;
  }

  public String getLabel()
  {
    return label;
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

  public Integer succ(final int numericalValue)
  {
    final SparseType sparseType = getSparseType();
    if (sparseType.isEnumerable()) {
      return sparseType.succ(numericalValue);
    } else {
      return null;
    }
  }

  public Integer pred(final int numericalValue)
  {
    final SparseType sparseType = getSparseType();
    if (sparseType.isEnumerable()) {
      final Integer predValue = sparseType.pred(numericalValue);
      return predValue;
    } else {
      return null;
    }
  }

  public Integer uppermost()
  {
    return getSparseType().uppermost();
  }

  public Integer lowermost()
  {
    return getSparseType().lowermost();
  }

  public abstract void setBitSize(final int bitSize);

  public abstract byte getBitSize();
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
