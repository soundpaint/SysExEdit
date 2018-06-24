/*
 * @(#)SpinnerEditor.java 1.00 18/03/25
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

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.soundpaint.sysexedit.gui.JValue;

public class SpinnerEditor extends JSpinner
  implements Editor, ChangeListener
{
  private static final long serialVersionUID = -3039669459111454715L;

  private SpinnerStringModel model;
  private final List<ValueChangeListener> listeners;

  public SpinnerEditor()
  {
    final JFormattedTextField tf =
      ((JSpinner.DefaultEditor)getEditor()).getTextField();
    tf.setEditable(false);
    listeners = new ArrayList<ValueChangeListener>();
    addChangeListener(this);
  }

  public void setSelectableValues(final Vector<JValue> values)
  {
    model = new SpinnerStringModel(values);
    setModel(model);
  }

  public void setSelectedIndex(final int index)
  {
    if (model == null) {
      throw new NullPointerException("no model set on spinner editor");
    }
    model.setIndex(index);
  }

  public JValue getSelectedValue()
  {
    if (model == null) {
      throw new NullPointerException("no model set on spinner editor");
    }
    return model.getValue();
  }

  public void setSelectionByNumericalValue(final Integer numericalValue)
  {
    if (model == null) {
      throw new NullPointerException("no model set on spinner editor");
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

  @Override
  public void addKeyListener(final KeyListener keyListener)
  {
    super.addKeyListener(keyListener);
    ((JSpinner.DefaultEditor)getEditor()).
      getTextField().addKeyListener(keyListener);
  }

  public void addValueChangeListener(final ValueChangeListener listener)
  {
    listeners.add(listener);
  }

  public void stateChanged(final ChangeEvent changeEvent)
  {
    final JValue value = getSelectedValue();
    if (value != null) {
      final int numericalValue = value.getSystemValue();
      for (final ValueChangeListener listener : listeners) {
        listener.editingPathValueChanged(numericalValue);
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
