/*
 * @(#)SpinnerStringModel.java 1.00 18/03/25
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.soundpaint.sysexedit.gui.JValue;

public class SpinnerStringModel implements SpinnerModel
{
  private final List<JValue> selectableValues;
  private final List<ChangeListener> listeners;
  private final ChangeEvent changeEvent;
  private int index;

  public SpinnerStringModel(final Vector<JValue> selectableValues)
  {
    this.selectableValues = new ArrayList<JValue>();
    this.selectableValues.addAll(selectableValues);
    listeners = new ArrayList<ChangeListener>();
    changeEvent = new ChangeEvent(this);
    index = -1;
  }

  public int getSize()
  {
    return selectableValues.size();
  }

  public JValue getElementAt(final int index)
  {
    return selectableValues.get(index);
  }

  public void clear()
  {
    while (selectableValues.size() > 0) {
      selectableValues.remove(selectableValues.size() - 1);
    }
    index = -1;
    fireStateChanged();
  }

  public void addSelectableValue(final JValue value)
  {
    index = 0;
    selectableValues.add(value);
    fireStateChanged();
  }

  public void setIndex(final int index)
  {
    if ((index < 0) || (index >= selectableValues.size())) {
      throw new IndexOutOfBoundsException("!(" + -1 + "<=" + index + "<" +
                                          selectableValues.size());
    }
    this.index = index;
    fireStateChanged();
  }

  public JValue getValue()
  {
    if (index < 0) {
      return null;
    }
    return selectableValues.get(index);
  }

  /**
   * Unfortunately, the Swing JSpinner component internally uses a
   * JFormattedTextField component for value display, and passes this
   * text field's string representation to the setValue() method of
   * this model (rather than the original JValue object).  To
   * compensate, we have to look up the index by string representation
   * comparison rather than by object comparison.
   */
  private int indexOfString(final String displayValue)
  {
    for (int index = 0; index < selectableValues.size(); index++) {
      if (displayValue.equals(selectableValues.get(index).toString())) {
        return index;
      }
    }
    return -1;
  }

  public void setValue(final Object value)
  {
    if (value != null) {
      if (value instanceof String) {
        index = indexOfString((String)value);
      } else {
        index = selectableValues.indexOf(value);
      }
    }
    //fireStateChanged(); // already handled internally by JSpinner
  }

  public JValue getNextValue()
  {
    if (index < selectableValues.size() - 1) {
      index++;
      fireStateChanged();
      return selectableValues.get(index);
      //return index;
    }
    return null;
  }

  public JValue getPreviousValue()
  {
    if (index > 0) {
      index--;
      fireStateChanged();
      return selectableValues.get(index);
      //return index;
    }
    return null;
  }

  public void addChangeListener(final ChangeListener listener)
  {
    listeners.add(listener);
  }

  public void removeChangeListener(final ChangeListener listener)
  {
    listeners.remove(listener);
  }

  protected void fireStateChanged()
  {
    for (final ChangeListener listener : listeners) {
      listener.stateChanged(changeEvent);
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
