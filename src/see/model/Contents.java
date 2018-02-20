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

package see.model;

/**
 * This class holds the structural information and the actual contents
 * of a single memory entry of the target device. A memory entry may be
 * part of a memory location or represent just one memory location or
 * cover a couple of memory locations.
 */
public interface Contents
{
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
  public void addRepresentation(Representation representation);

  /**
   * Returns the Representation object that is currently selected for this
   * Contents object.
   * @return The Representation object that is currently selected for this
   *    Contents object or null, if there is no valid selection.
   */
  public Representation getSelectedRepresentation();

  /**
   * Selects a representation from the pool of available representations.
   * @param selectionID The number of the representation with respect to
   *    the order the representations were added to this Contents object.
   *    The first representation has the number 0.
   * @exception IndexOutOfBoundsException If selectionID is below 0 or
   *    above or equal to the number of representations of the union.
   */
  public void setSelectedRepresentation(int selectionID);

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
  public void setBitSize(int bit_size);

  /**
   * Returns the current effective bit size of this Contents object.
   * This can be set by method setBitSize.
   * Initially, the effective bit size is set to the required bit size of
   * the underlying structure.
   * @return The current effective bit size.
   */
  public byte getBitSize();

  /*
   * Sets the contents value for this contents. This value is shared among
   * selectable representations of the contents value; thus it even may be
   * out of the currently selected or any other representation.
   * @param value The value.
   * @exception IllegalArgumentException If value is not an instance of the
   *    class that holds the value represented by this class.
   * @see #reset
   */
  public void setValue(int value);

  /**
   * Returns the current contents value. This value is shared among
   * selectable representations of the contents value; thus it even may be
   * out of the currently selected or any other representation.
   * @return The current value.
   */
  public int getValue();

  /**
   * Resets the contents value to its default value.
   * @see #setDefaultValue
   */
  public void reset();

  /*
   * Sets the default value for this contents. This value is shared among
   * selectable representations of the contents value; thus it even may be
   * out of the currently selected or any other representation.
   * @param default_value The default value.
   * @exception IllegalArgumentException If value is not an instance of the
   *    class that holds the value represented by this class.
   * @see #reset
   */
  public void setDefaultValue(int default_value);

  /**
   * Returns the current default value. This value is shared among
   * selectable representations of the contents value; thus it even may be
   * out of the currently selected or any other representation.
   * @return The current default value.
   */
  public int getDefaultValue();

  /**
   * Returns a representation of the contents value according to the
   * underlying bit layout. 
   * @return The array of bits that represents the contents value. For
   *    performance reasons, the return value is actually not an array of
   *    bits, but rather an array of int values with each int value
   *    holding 32 bits. The least significant bit is stored in the least
   *    significant bit of field 0 of the return value.
   */
  public int[] toBits();
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
