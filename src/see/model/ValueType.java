/*
 * @(#)ValueType.java 1.00 18/02/19
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
 * This interface defines the basic type for a single Contents object.
 */
public interface ValueType
{
  public final static String DISPLAY_VALUE_UNKNOWN = "???";

  int getMinValue();
  int getSize();
  String getDisplayValue(final int value);
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
