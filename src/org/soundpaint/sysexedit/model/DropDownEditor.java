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

package org.soundpaint.sysexedit.model;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.soundpaint.sysexedit.gui.JValue;

public class DropDownEditor extends JComboBox<JValue>
  implements Editor, ItemListener
{
  private static final long serialVersionUID = 1620075446582652477L;

  private DefaultComboBoxModel<JValue> model;
  private final List<ValueChangeListener> listeners;

  public DropDownEditor()
  {
    listeners = new ArrayList<ValueChangeListener>();
    addItemListener(this);
  }

  public void setSelectableValues(final Vector<JValue> values)
  {
    model = new DefaultComboBoxModel<JValue>(values);
    setModel(model);
  }

  public JValue getSelectedValue()
  {
    return getItemAt(getSelectedIndex());
  }

  public void setSelectionByNumericalValue(final Integer numericalValue)
  {
    if (model == null) {
      throw new NullPointerException("no model set on drop-down editor");
    }
    int selectedIndex = -1;
    if (numericalValue != null) {
      for (int index = 0; index < model.getSize(); index++) {
        final JValue value = model.getElementAt(index);
        if (value.getSystemValue() == numericalValue) {
          selectedIndex = index;
          break;
        }
      }
    }
    setSelectedIndex(selectedIndex);
  }

  public void addValueChangeListener(final ValueChangeListener listener)
  {
    listeners.add(listener);
  }

  public void itemStateChanged(final ItemEvent itemEvent)
  {
    if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
      final JValue value = getSelectedValue();
      if (value != null) {
        final int numericalValue = value.getSystemValue();
        for (final ValueChangeListener listener : listeners) {
          listener.editingPathValueChanged(numericalValue);
        }
      }
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
