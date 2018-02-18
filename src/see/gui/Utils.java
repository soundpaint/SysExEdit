/*
 * @(#)Utils.java 1.00 98/02/06
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

package see.gui;

/**
 * This class contains a collection of small, general-purpose methods
 * for general use. They are usually static.
 */
class Utils
{
  /**
   * Given an integer value, returns a String that starts with "0x",
   * followed by two hex digits that represent the lowermost byte of
   * the integer value.
   * @param n An integer value.
   * @return A textual representation of the LSB as 2 hex digits String.
   */
  static String intTo0xnn(int n)
  {
    byte b = (byte)n;
    if (b < 0)
      return "0x" + Integer.toString(b + 0x100, 0x10);
    else if (b <= 0xf)
      return "0x0" + Integer.toString(b, 0x10);
    else
      return "0x" + Integer.toString(b, 0x10);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
