/*
 * @(#)Value.java 1.00 98/01/31
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
import javax.swing.Icon;

/**
 * This class holds all of the meta data of a single memory entry of
 * the target device. A memory entry may be part of a memory location
 * or represent just one memory location or cover a couple of memory
 * locations.
 *
 * TODO: Rename this interface into "ValueSet", class "AbstractValue"
 * into "AbstractValueSet" and class "ValueImpl" into "ValueSetImpl".
 */
public interface Value
{
  /**
   * Returns a GUI component that enables the user to enter a value
   * for this Value object in a proper manner.
   * @return The GUI component.
   */
  Editor createEditor();

  /**
   * Returns a String that represents the specified numerical, or
   * null, if the specified numerical value is out of range.
   * @return A String representation for the specified numerical
   * value.
   */
  String getDisplayValue(final int numericalValue);

  /**
   * Returns an icon that represents this Value object's type.
   * @return The icon.
   */
  Icon getIcon();

  /**
   * An optional informal description of this Value.  Useful e.g. as
   * tooltip in the GUI.
   */
  String getDescription();

  /**
   * @return A label for this value.
   */
  String getLabel();

  /**
   * Returns the underlying numerical value to apply when this Value
   * instance is reset.
   * @return The underlying numerical default value.
   */
  int getDefaultValue();

  /**
   * Given some numerical value that this set of values may contain or
   * not, returns the next upper value that this set of values
   * contains.
   * @param numericalValue Some arbitrary numerical value (which may
   *    be even from the omitted value ranges of this set of values).
   * @return The next upper value that this set of values contains or
   *    null, if there is no such value.
   */
  Integer succ(final int numericalValue);

  /**
   * Given some numerical value that this set of values may contain or
   * not, return the next lower value that this set of values
   * contains.
   * @param numericalValue Some arbitrary numerical value (which may
   *    be even from the omitted value ranges of this set of values).
   * @return The next lower value that this set of values contains or
   *    null, if there is no such value.
   */
  Integer pred(final int numericalValue);

  /**
   * Returns the lowermost value of this set of values.
   * @return The lowermost value of this set of values or null, if
   *    this set of values is an empty set.
   */
  Integer uppermost();

  /**
   * Returns the uppermost value of this set of values.
   * @return The uppermost value of this set of values or null, if
   *    this set of values is an empty set.
   */
  Integer lowermost();

  /**
   * Sets the bit size of this Value object's underlying numerical
   * value. The bit size must be equal to or greater than the required
   * bit size that is implicitly or explicitly defined through the
   * structure of the underlying information. Those bits that remain
   * unused are supposed to be constantly zero.
   * @param bitSize The bit size of this Value object's underlying
   * numerical value.
   * @exception IllegalArgumentException If bitSize is below zero or
   *    below the required bit size.
   */
  void setBitSize(final int bitSize);

  /**
   * Sets the bit size of this Value object's underlying numerical
   * value.  Initially, the bit size is set to the required bit size
   * of the underlying numerical value.
   * @return The bit size of this Value object's underlying numerical
   * value.
   */
  byte getBitSize();
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
