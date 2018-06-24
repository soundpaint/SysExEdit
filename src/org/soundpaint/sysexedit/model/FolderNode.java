/*
 * @(#)FolderNode.java 1.00 18/06/09
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

import java.awt.Component;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.soundpaint.sysexedit.gui.Map;

/**
 * This class is used to represent a node in the hierarchical
 * structure of the whole memory of a device.  A node may or may not
 * carry a data value.  Address mapping is done in two steps: First of
 * all, the map is assumed to represent a linear addressed array of
 * bits of memory, starting with address 0x0.  By default, addresses
 * will be implicitly assigned to all nodes, such that the resulting
 * map has no address gap.  However, for devices that use a sparse
 * address layout, for every node, optionally, its desired address may
 * be explicitly specified when creating the node.  Note that it is
 * the responsibility of the device model to choose desired addresses
 * in such a way, that the nodes inbetween fit into the address range
 * resulting from the desired addresses.  After the memory map has
 * been created, actually addresses are resolved, considering any
 * desired address specified by the device model.  If it turns out
 * that the desired addresses do not fit, an exception will be thrown.
 */
public class FolderNode extends MapNode
{
  private static final long serialVersionUID = -2167951077552183614L;

  private final String description;

  /**
   * Creates a folder node with the specified label.
   * @param label The label of this node.
   */
  public FolderNode(final String label)
  {
    this(label, -1);
  }

  /**
   * Creates a folder node with the specified label and desired
   * address.
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
  public FolderNode(final String label, final long desiredAddress)
  {
    this(null, label, desiredAddress);
  }

  /**
   * Creates a folder node with the specified description and label.
   * @param description An optional informal description of this
   * FolderNode.  Useful e.g. as tooltip in the GUI.
   * @param label The label of this node.
   */
  public FolderNode(final String description, final String label)
  {
    this(description, label, -1);
  }

  /**
   * Creates a folder node with the specified description, label and
   * desired address.
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
  public FolderNode(final String description, final String label,
                    final long desiredAddress)
  {
    super(label, desiredAddress, true);
    this.description = description;
  }

  /**
   * The node preceding this node in depth first search order, or null
   * if there is no node preceding this one.
   */
  private MapNode dfsLastDescendant;

  protected MapNode getDfsLastDescendant()
  {
    return dfsLastDescendant;
  }

  /**
   * Returns this node's value as string for display.
   * @return Folder nodes do not have a value.  Hence, this method
   * returns @code{null}.
   */
  public String getDisplayValue()
  {
    return null;
  }

  protected MapNode resolveDfsLastDescendant()
  {
    MapNode dfsLastDescendant = this;
    for (int i = 0; i < getChildCount(); i++) {
      final MapNode child = (MapNode)getChildAt(i);
      dfsLastDescendant = child.resolveDfsLinkedNodes(dfsLastDescendant);
    }
    this.dfsLastDescendant = dfsLastDescendant;
    return dfsLastDescendant;
  }

  /**
   * Removes newChild from its present parent (if it has a parent), sets
   * the child's parent to this node, and then adds the child to this
   * node's child array at index childIndex. newChild must not be null
   * and must not be an ancestor of this node.
   * @param newChild The MutableTreeNode to insert under this node.
   * @param childIndex The index in this node's child array where this node
   *    is to be inserted.
   * @exception ArrayIndexOutOfBoundsException If childIndex is out of bounds.
   * @exception IllegalArgumentException If newChild is null or is an
   *    ancestor of this node.
   * @exception IllegalStateException If this node does not allow children.
   */
  @Override
  public void insert(final MutableTreeNode newChild, final int childIndex)
  {
    super.insert(newChild, childIndex);
    if (newChild instanceof DataNode) {
      final DataNode child = (DataNode)newChild;
    }
  }

  /**
   * Removes the child at the specified index from this node's children
   * and sets that node's parent to null. The child node to remove
   * must be a MutableTreeNode.
   * @param childIndex The index in this node's child array of the child
   *    to remove.
   * @exception ArrayIndexOutOfBoundsException If childIndex is out of bounds.
   */
  @Override
  public void remove(final int childIndex)
  {
    final MapNode child = (MapNode)getChildAt(childIndex);
    super.remove(childIndex);
  }

  public int getBitSize()
  {
    return 0;
  }

  @Override
  public String toString()
  {
    return "FolderNode[" + getTreePath() + "]";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
