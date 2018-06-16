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
 * This class holds the structural information and the actual value of
 * a single memory entry of the target device. A memory entry may be
 * part of a memory location or represent just one memory location or
 * cover a couple of memory locations.
 */
public interface Value
{
  /**
   * Returns a GUI component that enables the user to enter a value
   * for this Value object in a proper manner.
   * @return The GUI component.
   */
  Component getEditor();

  /**
   * Adds a ValueChangeListener that is envoked whenever the value
   * changes.
   * @param listener The listener to add.
   * @exception NullPointerException If <code>listener</code> is
   * <code>null</code>.
   */
  void addValueChangeListener(final ValueChangeListener listener);

  /**
   * Returns a String that represents this Value object's underlying
   * numerical value, or null, if the value is out of range with
   * respect to the associated type.
   * @return A String representation for this Value object's
   * underlying numerical value.
   */
  String getDisplayValue();

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
   * @return If negative, automatically determine an absolute address
   * for this node.  If non-negative, request that this node will
   * appear at the specified absolute address in the address space.
   * Effectively, by setting an absolute address, an area of
   * inaccessible memory bits will precede this node's data in order
   * to make this node appear at the desired address.  If specifying
   * an absolute address, it must be chosen such that all previous
   * nodes' memory mapped values (with respect to depth first search
   * order) fit into the address space range preceding the desired
   * address.  Note that validity check for this restriction will be
   * made only upon completion of the tree and thus may result in
   * throwing an exception some time later.
   */
  long getDesiredAddress();

  /**
   * Sets the underlying numerical value to apply when this Value
   * instance is reset.
   * @param defaultValue The underlying numerical default value.
   * @see #reset
   */
  void setDefaultValue(final int defaultValue);

  /**
   * Returns the underlying numerical value to apply when this Value
   * instance is reset.
   * @return The underlying numerical default value.
   */
  int getDefaultValue();

  /**
   * Sets the underlying numerical value of this Value object.
   * @param value The underlying numerical value.
   * @exception IllegalArgumentException If value is not an instance of the
   *    class that holds the value represented by this class.
   * @see #reset
   */
  void setNumericalValue(final int value);

  /**
   * Returns the underlying numerical value of this Value object.
   * @return The underlying numerical value.
   */
  int getNumericalValue();

  /**
   * Resets the underlying mumerical value to the default value.
   * @see #setDefaultValue
   */
  void reset();

  /**
   * Increments the underlying numerical value, if possible.
   */
  void increment();

  /**
   * Decrements the underlying numerical value, if possible.
   */
  void decrement();

  /**
   * Sets the underlying numerical value to the uppermost value that
   * is valid for this Value object.
   */
  void uppermost();

  /**
   * Sets the underlying numerical value to the lowermost value that
   * is valid for this Value object.
   */
  void lowermost();

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

  /**
   * Returns a numerical representation of the value according to the
   * underlying bit layout.
   * @return The array of bits that represents the underlying
   *    numerical value.  For performance reasons, the return value is
   *    actually not an array of bits, but rather an array of int
   *    values with each int value holding 32 bits. The least
   *    significant bit is stored in the least significant bit of
   *    field 0 of the return value.
   */
  int[] toBits();
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
