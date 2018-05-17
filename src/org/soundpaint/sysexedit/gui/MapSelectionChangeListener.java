/*
 * @(#)MapSelectionChangeListener.java 1.00 18/04/13
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

/**
 * Interface for listening to changes in map nodes selection.
 */
public interface MapSelectionChangeListener
{
  /**
   * This method is called whenever the selection changes
   * from none to at least one node or the other way around.
   */
  void selectionChanged(final SelectionMultiplicity multiplicity);

  /**
   * This method is a convenience method if method #selectionChanged.
   * It is called whenever the selection changes such that either a
   * unique leaf becomes selected, or such that there is no more
   * unique leaf selected.
   */
  void singleLeafSelectedChanged(final boolean hasSingleLeafSelected);

  /**
   * This method is a convenience method if method #selectionChanged.
   * It is called whenever the selection changes such that either
   * anything becomes selected, or such that there is not anything
   * anymore selected.
   */
  void anythingSelectedChanged(final boolean hasAnythingSelected);
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
