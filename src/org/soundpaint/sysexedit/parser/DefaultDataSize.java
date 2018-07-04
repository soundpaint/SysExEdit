/*
 * @(#)DefaultDataSize.java 1.00 18/06/26
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

package org.soundpaint.sysexedit.parser;

public class DefaultDataSize
{
  private final byte multipleOfBits;

  private DefaultDataSize()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  private DefaultDataSize(final byte multipleOfBits)
  {
    this.multipleOfBits = multipleOfBits;
  }

  public static DefaultDataSize fromMultipleOfBits(final byte multipleOfBits)
  {
    if (multipleOfBits < 1) {
      throw new IllegalArgumentException("multipleOfBits < 1: " +
                                         multipleOfBits);
    }
    return new DefaultDataSize(multipleOfBits);
  }

  public byte getMultipleOfBits()
  {
    return multipleOfBits;
  }

  public byte getBitSize(final byte requiredBitSize)
  {
    return
      (byte)(multipleOfBits * ((requiredBitSize - 1) / multipleOfBits + 1));
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
