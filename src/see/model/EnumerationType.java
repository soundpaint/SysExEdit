/*
 * @(#)EnumerationType.java 1.00 98/01/31
 *
 * Copyright (C) 1998 Juergen Reuter
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

// $Source:$
// $Revision:$
// $Aliases:$
// $Author:$
// $Date:$
// $State:$

package see.model;

/**
 * This class defines an enumeration type for a single Contents object.
 */
public class EnumerationType
{

  private final static byte TYPE_NUMBER = 0;
  private final static byte TYPE_ENUMERATION = 1;
  private final static String UNKNOWN = "???";

  private byte type;
  private int offs;
  private int radix;
  private String[] enum;

  /**
   * Defines a new EnumerationType for some value x for the range 0x00
   * through 0xff.
   * The EnumerationType is just the value itself.
   */
  public EnumerationType()
  {
    this(0);
  }

  /**
   * Defines a new EnumerationType for some integer value x.
   * The EnumerationType is just the value itself plus the specified offset,
   * which may be negative or positive or zero.
   * @param offs The offset to be added to the value that is to be
   *    represented.
   */
  public EnumerationType(int offs)
  {
    init(TYPE_NUMBER, offs, null);
  }

  /**
   * Defines a new EnumerationType for some integer value x.
   * The EnumerationType of x is enum[x]. If x is beyond the bounds of the
   * array enum, the EnumerationType of x is defined as the String
   * constant UNKNOWN.
   * @param enum An array of strings representing the value x for each x.
   * @exception NullPointerException If enum equals null.
   */
  public EnumerationType(String[] enum)
  {
    this(0, enum);
  }

  /**
   * Defines a new EnumerationType for some integer value x.
   * The EnumerationType of x is enum[x + offs]. If (x + offs) is beyond the
   * bounds of the array enum, the EnumerationType of x is defined as the
   * String constant UNKNOWN.
   * @param offs The offset to be added to the value that is to be
   *    represented.
   * @param enum An array of strings representing the value (x + offs) for
   *    each x.
   * @exception NullPointerException If enum equals null.
   */
  public EnumerationType(int offs, String[] enum)
  {
    init(TYPE_ENUMERATION, offs, enum);
  }

  /**
   * Defines a new EnumerationType for some integer value x.
   * The EnumerationType of x is s only if x equals x0, and UNKNOWN
   * in all other cases.
   * @param x0 The value for x to be represented by s.
   * @param s The EnumerationType for x0.
   */
  EnumerationType(int x0, String s)
  {
    String[] enum = new String[1];
    enum[0] = s;
    init(TYPE_ENUMERATION, -x0, enum);
  }

  /**
   * Defines a new EnumerationType for some integer value x.
   * If type equals TYPE_NUMBER, the EnumerationType is just the value itself
   * plus the specified offset, which may be negative or positive or zero.
   * Parameter enum is then irrelevant.<BR>
   * If type equals TYPE_ENUMERATION, the EnumerationType of x is
   * enum[x + offs].<BR>
   * If (x + offs) is beyond the bounds of the array enum, the
   * EnumerationType of x is defined as the String constant UNKWOWN.
   * @param type Either TYPE_NUMBER or TYPE_ENUMERATION.
   * @param offs The offset to be added to the value that is to be
   *    represented.
   * @param enum For type TYPE_ENUMERATION, an array of strings
   *    representing the value (x + offs) for each x.
   * @exception IllegalArgumentException If parameter type is invalid.
   * @exception NullPointerException If type equals TYPE_ENUMERATION and
   *    enum equals null.
   */
  private void init(byte type, int offs, String[] enum)
  {
    if ((type < TYPE_NUMBER) || (type > TYPE_ENUMERATION))
      throw new IllegalArgumentException("parameter type out of range");
    this.type = type;
    if (type == TYPE_ENUMERATION)
      if (enum == null)
	throw new NullPointerException("enum equals null");
      else
	this.enum = enum;
    else
      this.enum = null;
    this.offs = offs;
    this.radix = radix;
  }

  /**
   * Sets the radix for String representation of numbers.
   * The default is 10.
   * @param radix The radix.
   */
  public void setRadix(int radix)
  {
    this.radix = radix;
  }

  /**
   * Returns the radix for String representation of numbers.
   * The default is 10.
   * @param radix The radix.
   */
  public int getRadix()
  {
    return radix;
  }

  /**
   * Returns a bit string representation for a given value x.
   * @param x The value to be represented.
   * @param n The number of digits to display.
   * @return A bit string representation of the value x.
   */
  private static String formattedBitString(int x, int n)
  {
    StringBuffer s = new StringBuffer();
    s.append("%");
    for (int i = n - 1; i >= 0; i--)
      s.append((((x >> i) & 1) == 1) ? "1" : "0");
    return s.toString();
  }

  boolean useFormattedBitString = false;
  int bitStringSize = 8;

  /**
   * Returns a String that represents x according to the specification
   * of this EnumerationType.
   */
  String toString(int x)
  {
    int y = x + offs;
    switch (type)
      {
      case TYPE_NUMBER:
	if (useFormattedBitString)
	  return formattedBitString(y, bitStringSize);
	else
	  return "" + Integer.toString(y, radix);
      case TYPE_ENUMERATION:
	if ((y >= 0) && (y < enum.length))
	  return enum[y];
	else
	  return UNKNOWN;
      default:
	throw new Error("invalid type = " + type);
      }
  }

  /**
   * Returns a string representation of the enum array (e.g. for debugging).
   * @return A string representation of the enum array.
   */
  private String enumToString()
  {
    StringBuffer s = new StringBuffer();
    s.append("[");
    if (enum != null)
      {
	if (enum.length > 0)
	  s.append(enum[0]);
	for (int i = 1; i < enum.length; i++)
	  s.append(", " + enum[i]);
      }
    s.append("]");
    return s.toString();
  }

  /**
   * Returns a string representation of this object (e.g. for debugging).
   * @return A string representation of this object.
   */
  public String toString()
  {
    String type_str;
    switch (type)
      {
      case TYPE_NUMBER:
	type_str = "TYPE_NUMBER";
	break;
      case TYPE_ENUMERATION:
	type_str = "TYPE_ENUMERATION";
	break;
      default:
	type_str = "" + type;
      }
    return "EnumerationType{type=" + type_str + ", offs=" + offs +
      ", enum=" + enumToString() + "}";
  }
}
