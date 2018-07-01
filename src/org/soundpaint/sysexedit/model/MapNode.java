/*
 * @(#)MapNode.java 1.00 98/01/31
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

import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
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
public abstract class MapNode extends DefaultMutableTreeNode
  implements MapChangeListener
{
  private static final long serialVersionUID = -1726377369359671649L;

  /** Map change listeners. */
  private final List<MapChangeListener> listeners;

  /** The desired absolute address for the associated node. */
  private final long desiredAddress;

  /** The label to display as node name in the tree view. */
  private final String label;

  /** The confirmed absolute bit address of this node. */
  protected long address;

  /**
   * The node preceding this node in depth first search order, or null
   * if there is no node preceding this one.
   */
  private MapNode dfsPreviousNode;

  abstract protected MapNode getDfsLastDescendant();

  /**
   * The node following this node in depth first search order, or null
   * if there is no node follwing this one.
   */
  private MapNode dfsNextNode;

  private MapNode()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  /**
   * Creates a folder node with the specified meta data.
   * @param label The label to display as node name in the tree view.
   * @param desiredAddress Desired absolute address for the associated
   * node.  If negative, automatically determine an absolute address
   * for this node.  If non-negative, request that this node will
   * appear at the specified absolute address in the address space.
   * Effectively, by setting an absolute address, an area of
   * inaccessible memory bits will precede this node's data in order
   * to make this node appear at the desired address.  If specifying
   * an absolute address, it must be chosen such that all previous
   * nodes' memory mapped values (with respect to depth first search
   * order) fit into the address space range preceding the desired
   * address.  Note that validity check for this restriction will be
   * made only upon completion of the tree and thus may result in
   * throwing an exception some time later.
   * @param allowsChildren If @code{true}, the node is allowed to have
   * child nodes.  Otherwise, it is always a leaf node.
   */
  public MapNode(final String label, final long desiredAddress,
                 final boolean allowsChildren)
  {
    super(allowsChildren);
    this.label = label;
    this.desiredAddress = desiredAddress;
    address = -1; // resolve later
    listeners = new ArrayList<MapChangeListener>();
  }

  /**
   * Invoked when a map change occurs.
   */
  public void mapChangePerformed(final MapChangeEvent e)
  {
    final DefaultTreeModel model = e.getModel();
    if (model != null)
      model.nodeChanged(this); // note: this only works *within* a tree
  }

  /**
   * Fires a map change event to all map change listeners.
   * @param model The tree model of the tree that contains this node.
   */
  protected void fireMapChangeEvents(final DefaultTreeModel model)
  {
    final MapChangeEvent event = new MapChangeEvent(this, model);
    for (final MapChangeListener listener : listeners) {
      listener.mapChangePerformed(event);
    }
  }

  /**
   * Adds a MapChangeListener to this tree node.
   * @param listener The MapChangeListener to add.
   */
  public void addMapChangeListener(final MapChangeListener listener)
  {
    listeners.add(listener);
  }

  /**
   * Removes a MapChangeListener from this tree node.
   * @param listener The MapChangeListener to remove.
   */
  public void removeMapChangeListener(final MapChangeListener listener)
  {
    listeners.remove(listener);
  }

  /**
   * Returns the address of this node.
   * @return The address of this node or -1, if it has not yet been
   *    resolved.
   * @see #resolveAddresses
   */
  public long getAddress()
  {
    return address;
  }

  /**
   * Returns a descriptive label for this tree node.
   * @return A descriptive label.
   */
  public String getLabel()
  {
    return label;
  }

  abstract protected MapNode resolveDfsLastDescendant();

  /**
   * Resolves the links to the preceding and following node in depth
   * first search order for this and all of its descendant nodes.
   * WARNING: For proper handling of these links, link resolution must
   * be performed not only at startup, but also whenever the map has
   * been modified.
   * @param dfsPreviousNode The node that has been visited previously
   * to this node in depth first search order or null, if this node is
   * the root node of the tree.
   * @return The last visited node while traversing the complete
   * subtree below this node in depth first search order.
   */
  protected MapNode resolveDfsLinkedNodes(final MapNode dfsPreviousNode)
  {
    this.dfsPreviousNode = dfsPreviousNode;
    if (dfsPreviousNode != null) {
      dfsPreviousNode.dfsNextNode = this;
    }
    return resolveDfsLastDescendant();
  }

  /**
   * @return If negative, automatically determine an absolute address
   * for this node.  If non-negative, request that this node will
   * appear at the specified absolute address in the address space.
   * Effectively, by setting an absolute address, an area of
   * inaccessible memory bits will precede this node's data in order
   * to make this node appear at the desired address.  If specifying
   * an absolute address, it must be chosen such that all previous
   * nodes' memory mapped values (with respect to depth first search
   * order) fit into the address space range preceding the desired
   * address.  Note that validity check for this restriction will be
   * made only upon completion of the tree and thus may result in
   * throwing an exception some time later.
   */
  public long getDesiredAddress()
  {
    return desiredAddress;
  }

  /**
   * Resolves the address information for this and all of its
   * descendant nodes.  WARNING: For proper handling of address
   * information, address resolution must be performed not only at
   * startup, but also whenever the map has been modified.
   * @param nextAvailableAddress The next absolute address that is no
   * yet assigned.
   * @return The next available address after the last descendant of
   * this node (in depth first search order).
   */
  protected long resolveAddresses(final long nextAvailableAddress)
  {
    final long desiredAddress = getDesiredAddress();
    if (desiredAddress == -1) {
      // no desired address specified => use default
      address = nextAvailableAddress;
    } else if (nextAvailableAddress > desiredAddress) {
      final Map map = ((AbstractDevice.MapRoot)getRoot()).getMap();
      final AddressRepresentation addressRepresentation =
        map.getAddressRepresentation();
      final String desiredDisplayAddress =
        addressRepresentation.getDisplayAddress(desiredAddress);
      final String nextAvailableDisplayAddress =
        addressRepresentation.getDisplayAddress(nextAvailableAddress);
      throw new RuntimeException("invalid desired address " +
                                 desiredDisplayAddress +
                                 " for node " + this + ": " +
                                 "desired address must be " +
                                 nextAvailableDisplayAddress + " or higher");
    } else {
      // use desired address
      address = desiredAddress;
    }
    long result = address + getBitSize();
    for (int i = 0; i < getChildCount(); i++) {
      final MapNode child = (MapNode)getChildAt(i);
      result = child.resolveAddresses(result);
    }
    return result;
  }

  abstract public int getBitSize();

  /**
   * Locates the data node that covers the specified address.
   * @param address The address to be located.
   * @return The node that covers the specified address, or null, if
   *    the address either can not be found in the map or is inaccessible.
   */
  public DataNode locate(final long address)
  {
    final long fromAddress = this.address;

    final MapNode toAddressNode = getDfsLastDescendant().dfsNextNode;
    final long toAddress =
      toAddressNode != null ?
      toAddressNode.address :
      getDfsLastDescendant().address + getDfsLastDescendant().getBitSize();

    if ((address < fromAddress) || (address >= toAddress)) {

      // performance boost: due to mostly linear reads, first try next
      // node in deapth first search order before searching anywhere
      // else, thus reducing search from O(n) to O(1) for linear
      // access patterns (TODO: rather than putting this performance
      // hack here, the caller should try calling method locate()
      // already on the best-known fitting node)
      if (dfsNextNode != null) {
        if (address >= dfsNextNode.address) {
          final MapNode dfsNextNextNode = dfsNextNode.dfsNextNode;
          if (dfsNextNextNode != null) {
            if (address < dfsNextNextNode.address) {
              return (DataNode)dfsNextNode;
            }
          }
          if (address < dfsNextNode.address + dfsNextNode.getBitSize()) {
            return (DataNode)dfsNextNode;
          }
        }
      }

      // address not among this node or its descendants
      if ((parent != null) && (dfsNextNode != null)) {
        // try looking at sister nodes
        return ((MapNode)parent).locate(address);
      }
      // no such address
      return null;
    }

    // wanted node must be among this node or its descendants

    if (this instanceof DataNode) {
      return (DataNode)this;
    }

    MapNode child = null;
    long nextAddress = -1;
    for (int i = 0;
         i < getChildCount() && (address >= nextAddress);
         i++) {
      child = (MapNode)getChildAt(i);
      final MapNode childDfs = child.getDfsLastDescendant().dfsNextNode;
      nextAddress =
        childDfs != null ?
        childDfs.address :
        child.address + child.getBitSize();
    }

    if (child == null) {
      throw new IllegalStateException("DFS structure corrupted");
    }

    if (address < child.address) {
      throw new IllegalStateException("no such address: " + address + " < " +
                                      child.address +
                                      "(child = " + child + ")");
    }

    return child.locate(address);
  }

  protected String getTreePath()
  {
    final StringBuffer s = new StringBuffer();
    final TreeNode[] path = getPath();
    for (final TreeNode node : path) {
      if (s.length() > 0) {
        s.append(" => ");
      }
      final MapNode mapNode = (MapNode)node;
      s.append(mapNode.getLabel());
    }
    return s.toString();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
