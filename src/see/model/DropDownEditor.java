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

package see.model;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class DropDownEditor extends JComboBox<Contents> implements Editor
{
  private static final long serialVersionUID = 1620075446582652477L;

  private final DefaultComboBoxModel<Contents> model;

  public DropDownEditor()
  {
    model = new DefaultComboBoxModel<Contents>();
    setModel(model);
  }

  public void clear()
  {
    while (model.getSize() > 0) {
      model.removeElementAt(model.getSize() - 1);
    }
  }

  public void addContents(final Contents contents)
  {
    model.addElement(contents);
  }

  public Contents getSelectedContents()
  {
    return getItemAt(getSelectedIndex());
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
