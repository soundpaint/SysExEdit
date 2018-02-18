/*
 * @(#)MapCellRenderer.java 1.00 98/02/06
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

package see.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class SelectableLabel extends JLabel
{
  private static final long serialVersionUID = -5044595891279570668L;

  /**
   * True, if the label that was last configured is selected.
   */
  private boolean _selected;

  public boolean isSelected() { return _selected; }
  
  public void setSelected(boolean value) { _selected = value; }

  /**
   * Method paint is subclassed to draw the background correctly.
   * JLabel currently does not allow backgrounds other than white, and
   * it will also fill behind the icon.
   */
  public void paint(Graphics g)
  {
    Color orig_foreground;

    orig_foreground = getForeground();
    if (_selected)
      {
	setForeground(UIManager.getColor("Tree.selectionForeground"));
	g.setColor(UIManager.getColor("Tree.selectionBackground"));
      }
    else
      g.setColor(getBackground());

    // clear background
    Icon defaultIcon = getIcon();
    if ((defaultIcon != null) && (getText() != null))
      {
	int offset = defaultIcon.getIconWidth() + getIconTextGap();
	g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
      }
    else
      g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

    super.paint(g);
    setForeground(orig_foreground);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
