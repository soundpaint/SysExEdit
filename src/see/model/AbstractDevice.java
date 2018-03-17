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

import java.io.InputStream;
import javax.swing.tree.TreeNode;

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

    private MapRoot(final String deviceName)
    {
      super(deviceName);
      map = new Map();
    }

    public Map getMap()
    {
      return map;
    }
  }

  private MapRoot root;

  public AbstractDevice()
  {
  }

  /**
   * Creates a map that represents the device's internal memory.
   */
  public abstract void buildMap(final MapRoot root);

  public TreeNode buildMap()
  {
    root = new MapRoot(getName());
    buildMap(root);
    root.resolveAddresses(0);
    return root;
  }

  public Map getMap()
  {
    return root.getMap();
  }

  public InputStream bulkDump(final long start, final long end)
  {
    return bulkDump(root, start, end);
  }

  /**
   * Given a contigous area of memory, returns a a stream of MIDI bytes
   * that may be used to send the memory contents to the MIDI device.
   * @param root The root node of the map to use.
   * @param start The bit address in the memory map where to start.
   * @param end The bit address in the memory map where to end before;
   *    thus the total bulk dump size is (end - start) bits of memory.
   * @return A stream that bulk dumps the sequence of bytes for the
   *    MIDI device.
   */
  public abstract InputStream bulkDump(final MapNode root,
                                       final long start, final long end);

  public abstract String getName();
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
