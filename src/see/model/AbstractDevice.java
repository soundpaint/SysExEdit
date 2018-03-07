/*
 * @(#)AbstractDevice.java 1.00 18/03/04
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

import javax.swing.tree.DefaultMutableTreeNode;

import see.gui.Map;

/**
 * Abstract device implementation.  Handles root node.
 */
public abstract class AbstractDevice implements MapDef
{
  public class MapRoot extends MapNode
  {
    private static final long serialVersionUID = 3044259528722489945L;

    private final Map map;

    public MapRoot(final String deviceName)
    {
      super(deviceName);
      map = new Map();
    }

    public Map getMap()
    {
      return map;
    }
  }

  private final MapRoot root;

  public AbstractDevice(final String deviceName)
  {
    if (deviceName == null) {
      throw new NullPointerException("deviceName");
    }
    root = new MapRoot(deviceName);
    buildMap();
    root.evaluateAddresses();
  }

  public MapRoot getRoot()
  {
    return root;
  }

  public Map getMap()
  {
    return root.getMap();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
