/*
 * @(#)Editor.java 1.00 98/01/31
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

package org.soundpaint.sysexedit.model;

import java.util.Vector;

import org.soundpaint.sysexedit.gui.JValue;

public interface Editor
{
  void setSelectableValues(final Vector<JValue> values);

  void setSelectedIndex(final int selectedIndex);

  JValue getSelectedValue();

  /*
   * @param numericalValue The preselection value to use in the
   * editor, or null, if there is no preferred preselection.  In that
   * case, the editor may decide by itself which value to preselect.
   */
  void setSelectionByNumericalValue(final Integer numericalValue);

  void addValueChangeListener(ValueChangeListener listener);
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
