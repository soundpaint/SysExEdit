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

package see.model;

import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;

public class SpinnerEditor extends JSpinner implements Editor
{
  private static final long serialVersionUID = -3039669459111454715L;

  private final SpinnerStringModel model;

  public SpinnerEditor()
  {
    model = new SpinnerStringModel();
    setModel(model);
    final JFormattedTextField tf =
      ((JSpinner.DefaultEditor)getEditor()).getTextField();
    tf.setEditable(false);
  }

  public void clear()
  {
    model.clear();
  }

  public void addContents(final Contents contents)
  {
    model.addContents(contents);
  }

  public void setSelectedIndex(final int index)
  {
    model.setIndex(index);
  }

  public Contents getSelectedContents()
  {
    return model.getValue();
  }

  @Override
  public void addKeyListener(final KeyListener keyListener)
  {
    super.addKeyListener(keyListener);
    ((JSpinner.DefaultEditor)getEditor()).
      getTextField().addKeyListener(keyListener);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
