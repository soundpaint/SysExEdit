/*
 * @(#)Contents.java 1.00 98/01/31
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
 * This class holds the structural information and the actual contents
 * of a single memory entry of the target device. A memory entry may be
 * part of a memory location or represent just one memory location or
 * cover a couple of memory locations.
 */
public interface Contents
{
  /**
   * Returns a GUI component that enables the user to enter a value of
   * the type of contents that this object represents.
   * @return The GUI component.
   */
  Component getEditor();

  /**
   * Adds a ContentsChangeListener that is envoked whenever the
   * contents value changes.
   * @param listener The listener to add.
   * @exception NullPointerException If <code>listener</code> is
   * <code>null</code>.
   */
  void addContentsChangeListener(final ContentsChangeListener listener);

  /**
   * Returns a String that represents this contents' current value
   * according to the underlying sparse type, or null, if the value
   * is not in range.
   * @return The String representation of this contents' current
   * value.
   */
  String getDisplayValue();

  /**
   * Returns an icon that represents this content's type.
   * @return The icon.
   */
  Icon getIcon();

  /*
   * Sets the default value for this contents.
   * @param default_value The default value.
   * @exception IllegalArgumentException If value is not an instance of the
   *    class that holds the value represented by this class.
   * @see #reset
   */
  void setDefaultValue(final int default_value);

  /**
   * Returns the current default value for this contents.
   * @return The current default value.
   */
  int getDefaultValue();

  /*
   * Sets the contents value for this contents.
   * @param value The value.
   * @exception IllegalArgumentException If value is not an instance of the
   *    class that holds the value represented by this class.
   * @see #reset
   */
  void setValue(final int value);

  /**
   * Returns the current value for this contents.
   * @return The current value.
   */
  int getValue();

  /**
   * Resets the contents value to its default value.
   * @see #setDefaultValue
   */
  void reset();

  /**
   * Increments the contents of this node, if possible.
   */
  void increment();

  /**
   * Decrements the contents of this node.
   */
  void decrement();

  /**
   * Sets the contents of this node to the uppermost value that is in range.
   */
  void uppermost();

  /**
   * Sets the contents of this node to the lowermost value that is in range.
   */
  void lowermost();

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
  void setBitSize(final int bit_size);

  /**
   * Returns the current effective bit size of this Contents object.
   * This can be set by method setBitSize.
   * Initially, the effective bit size is set to the required bit size of
   * the underlying structure.
   * @return The current effective bit size.
   */
  byte getBitSize();

  /**
   * Returns a representation of the contents value according to the
   * underlying bit layout.
   * @return The array of bits that represents the contents value. For
   *    performance reasons, the return value is actually not an array of
   *    bits, but rather an array of int values with each int value
   *    holding 32 bits. The least significant bit is stored in the least
   *    significant bit of field 0 of the return value.
   */
  int[] toBits();
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
