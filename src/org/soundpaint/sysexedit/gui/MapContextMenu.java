/*
 * @(#)MapContextMenu.java 1.00 18/04/09
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

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import org.soundpaint.sysexedit.model.Contents;

public class MapContextMenu extends JPopupMenu
  implements MapSelectionChangeListener
{
  private static final long serialVersionUID = 2035908881059488806L;

  private final JMenuItem menuItemMinValue;
  private final JMenuItem menuItemMaxValue;
  private final JMenuItem menuItemIncValue;
  private final JMenuItem menuItemDecValue;
  private final JMenuItem menuItemResetValue;
  private final JMenuItem menuItemBulkDump;

  public MapContextMenu(final Controller ctrl)
  {
    super("Map Node Actions");
    menuItemMinValue = new JMenuItem("Set Value to Minimum");
    menuItemMinValue.addActionListener(ctrl.getLowermostListener());
    add(menuItemMinValue);
    menuItemMaxValue = new JMenuItem("Set Value to Maximum");
    menuItemMaxValue.addActionListener(ctrl.getUppermostListener());
    add(menuItemMaxValue);
    menuItemIncValue = new JMenuItem("Increment Value");
    menuItemIncValue.addActionListener(ctrl.getIncrementListener());
    add(menuItemIncValue);
    menuItemDecValue = new JMenuItem("Decrement Value");
    menuItemDecValue.addActionListener(ctrl.getDecrementListener());
    add(menuItemDecValue);
    menuItemResetValue = new JMenuItem("Reset Value");
    menuItemResetValue.addActionListener(ctrl.getResetListener());
    add(menuItemResetValue);
    addSeparator();
    menuItemBulkDump = new JMenuItem("Bulk Dump Selection");
    menuItemBulkDump.addActionListener(ctrl.getBulkDumpListener());
    add(menuItemBulkDump);
    singleLeafSelectedChanged(false);
    menuItemBulkDump.setEnabled(false);
  }

  public void selectionChanged(final SelectionMultiplicity multiplicity)
  {
    // ignore
  }

  public void singleLeafSelectedChanged(final boolean hasSingleLeafSelected)
  {
    menuItemMinValue.setEnabled(hasSingleLeafSelected);
    menuItemMaxValue.setEnabled(hasSingleLeafSelected);
    menuItemIncValue.setEnabled(hasSingleLeafSelected);
    menuItemDecValue.setEnabled(hasSingleLeafSelected);
    menuItemResetValue.setEnabled(hasSingleLeafSelected);
  }

  public void anythingSelectedChanged(final boolean hasAnythingSelected)
  {
    menuItemBulkDump.setEnabled(hasAnythingSelected);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
