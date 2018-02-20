/*
 * @(#)Representation.java 1.00 98/01/31
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
 * This interface defines the framework for representation classes
 * such as Range.
 */
public interface Representation
{
  /**
   * Specifies a key for the icon, that is displayed together with each
   * instance that implements this representation. The associated icon is
   * to be stored via UIManager.getDefaults().put(iconKey, icon).
   * @param iconKey The key of the icon to be displayed.
   */
  public void setIconKey(String iconKey);

  /**
   * Returns the key for the icon, that is displayed together with each
   * instance that implements this representation.
   * @return The key of the icon to be displayed.
   */
   public String getIconKey();

  /**
   * Returns the minimally required bit size for coding.
   * @return The minimally required bit size for coding.
   */
  public int getRequiredBitSize();

  /**
   * Checks, if the specified value is a valid member of this
   * representation.
   * @param x The value to be checked.
   * @return True, if the value is in range.
   */
  public boolean isInRange(int x);

  /**
   * Returns the lowermost value of this representation.
   * @return The lowermost value of this representation or null, if the
   *    range is empty.
   */
  public Integer lowermost();

  /**
   * Returns the uppermost value of this representation.
   * @return The uppermost value of this representation or null, if the
   *    range is empty.
   */
  public Integer uppermost();

  /**
   * Returns true, if this representation is enumeratable.
   * @return true, if this representation is enumeratable.
   */
  public boolean isEnumerable();

  /**
   * Given some value x that may be or may be not in range, returns the
   * next upper value that is in range, provided that this representation
   * is enumeratable.<BR>
   * @param x Some arbitrary value (which may be even out of range).
   * @return The next upper value that is in range or null, if there is
   *    no such value or this representation is not enumeratable.
   */
  public Integer succ(int x);

  /**
   * Given some value x that may be or may be not in range, returns the
   * next lower value that is in range, provided that this representation
   * is enumeratable.<BR>
   * @param x Some arbitrary value (which may be even out of range).
   * @return The next lower value that is in range or null, if there is
   *    no such value or this representation is not enumeratable.
   */
  public Integer pred(int x);

  /**
   * Returns a String that represents x according to this Representation
   * or null, if x is not in range.
   * @param x The value to be represented.
   * @return The String representation of x.
   */
  public String getDisplayValue(int x);
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
