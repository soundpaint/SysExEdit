/*
 * @(#)SparseType.java 1.00 98/01/31
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

/**
 * All sparse type implementations must implement this interface.
 */
public interface SparseType
{
  /**
   * The key for an icon that is applicable as fallback for any kind
   * of data when no more specific icon is available.
   */
  public static final String GENERIC_ICON_KEY = "internal-data";

  /**
   * Returns optional informal description of this EnumRenderer.
   * Useful e.g. as tooltip in the GUI.  If no description is
   * available, returns <code>null</code>
   */
  String getDescription();

  /**
   * Returns the key for the icon that is displayed together with each
   * instance that implements this sparse type.
   * @return The key of the icon to be displayed.
   */
  String getIconKey();

  /**
   * Returns the minimally required bit size for coding.
   * @return The minimally required bit size for coding.
   */
  byte getRequiredBitSize();

  /**
   * Returns the lowermost value of this sparse type.
   * @return The lowermost value of this sparse type or null, if the
   *    sparse type is empty.
   */
  Integer lowermost();

  /**
   * Returns the uppermost value of this sparse type.
   * @return The uppermost value of this sparse type or null, if the
   *    sparse type is empty.
   */
  Integer uppermost();

  /**
   * Returns true, if this sparse type is enumeratable.
   * @return true, if this sparse type is enumeratable.
   */
  boolean isEnumerable();

  /**
   * Given some value x that may be or may be not in range, returns the
   * next upper value that is in range, provided that this sparse type
   * is enumeratable.<BR>
   * @param x Some arbitrary value (which may be even out of range).
   * @return The next upper value that is in range or null, if there is
   *    no such value or this sparse type is not enumeratable.
   */
  Integer succ(final int x);

  /**
   * Given some value x that may be or may be not in range, returns the
   * next lower value that is in range, provided that this sparse type
   * is enumeratable.<BR>
   * @param x Some arbitrary value (which may be even out of range).
   * @return The next lower value that is in range or null, if there is
   *    no such value or this sparse type is not enumeratable.
   */
  Integer pred(final int x);

  /**
   * Returns a String that represents x according to this sparse type
   * or null, if x is not a valid value for this sparse type.
   * @param x The value to be represented.
   * @return The String representation of x.
   */
  String getDisplayValue(final int x);
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
