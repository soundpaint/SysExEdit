/*
 * @(#)NodeMetaData.java 1.00 18/06/10
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

public class NodeMetaData
{
  private final String description;
  private final String label;
  private final long desiredAddress;

  public NodeMetaData()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a folder node's metadata, consisting of a description,
   * label and desired address.
   * @param description An optional informal description of this
   * FolderNode.  Useful e.g. as tooltip in the GUI.
   * @param label The label of this node.
   * @param desiredAddress If negative, automatically determine an
   *    absolute address for this node.  If non-negative, request that
   *    this node will appear at the specified absolute address in the
   *    address space.  Effectively, by setting an absolute address,
   *    an area of inaccessible memory bits will precede this node's
   *    data in order to make this node appear at the desired address.
   *    If specifying an absolute address, it must be chosen such that
   *    all previous nodes' memory mapped values (with respect to
   *    depth first search order) fit into the address space range
   *    preceding the desired address.  Note that validity check for
   *    this restriction will be made only upon completion of the tree
   *    and thus may result in throwing an exception some time later.
   */
  public NodeMetaData(final String description, final String label,
                      final long desiredAddress)
  {
    this.description = description;
    this.label = label;
    this.desiredAddress = desiredAddress;
  }

  public String getDescription()
  {
    return description;
  }

  public String getLabel()
  {
    return label;
  }

  public long getDesiredAddress()
  {
    return desiredAddress;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
