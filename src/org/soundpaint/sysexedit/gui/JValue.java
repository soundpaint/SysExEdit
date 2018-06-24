/*
 * @(#)DropDownEditor.java 1.00 18/03/07
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

package org.soundpaint.sysexedit.gui;

import org.soundpaint.sysexedit.model.Value;

/**
 * A JValue is a value for use in the Java GUI, e.g. in a JComboBox.
 * It contains an integer value as system value for internal use and a
 * value type with type information, including a renderer for
 * displaying the value to the user.  The display value is not
 * considered to be constant, e.g. when switching between languages;
 * it is just for display to the user.  The system value, however, is
 * assumed to have constant semantics.  For example, when representing
 * a day of week, the integer system value "0" may constantly
 * represent a Sunday, "1" = Monday, "2" = Tuesday, etc.  The display
 * value for Sunday may be "Sunday" in English language, or "Sonntag"
 * in German language.  When changing the language, the system value
 * "0" keeps unmodified, but the display value may change e.g. from
 * "Sunday" to "Sonntag".
 */
public class JValue
{
  private final Value valueType;
  private int systemValue;

  private JValue()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public JValue(final Value valueType)
  {
    this(valueType, valueType.getDefaultValue());
  }

  public JValue(final Value valueType, final int systemValue)
  {
    this.systemValue = systemValue;
    this.valueType = valueType;
  }

  public Value getValueType()
  {
    return valueType;
  }

  public int getSystemValue()
  {
    return systemValue;
  }

  public void setSystemValue(final int systemValue)
  {
    this.systemValue = systemValue;
  }

  public String getDisplayValue()
  {
    return valueType.getDisplayValue(systemValue);
  }

  public String toString()
  {
    return getDisplayValue();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
