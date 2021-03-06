/*
 * @(#)MapChangeEvent.java 1.00 98/01/31
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

package org.soundpaint.sysexedit.model;

import java.util.EventObject;
import javax.swing.tree.DefaultTreeModel;

/**
 * An object instance of this class represents an event that occurs
 * whenever the value of a MapNode changes.
 */
public class MapChangeEvent extends EventObject
{
  private static final long serialVersionUID = 2435483410608666577L;

  private final DefaultTreeModel model;

  /**
   * Creates a new MapChangeEvent object with the specified source.
   * @param source The source node that triggers this event.
   * @param model The tree model of the source node.
   * @exception IllegalArgumentException If source equals null.
   */
  public MapChangeEvent(final MapNode source,
                        final DefaultTreeModel model)
  {
    super(source);
    this.model = model;
  }

  /**
   * Returns the tree model of the source node.
   * @return The tree model of the source node.
   */
  public DefaultTreeModel getModel()
  {
    return model;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
