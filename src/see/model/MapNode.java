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

package see.model;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import see.gui.Map;

/**
 * This class is used to represent a node in the hierarchical
 * structure of the whole memory of a device.  A node may or may not
 * carry data contents.  Address mapping is done in two steps: First
 * of all, the map is assumed to represent a linear addressed array of
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
public class MapNode extends DefaultMutableTreeNode
  implements MapChangeListener, ContentsChangeListener
{
  private static final long serialVersionUID = -1726377369359671649L;

  private final String label;

  // map change listeners
  private final Vector<MapChangeListener> listeners;

  // the desired absolute bit address of this node
  private final long desiredAddress;

  // the confirmed absolute bit address of this node
  private long address;

  // the total bit size including all sub-trees
  private long total_size;

  private MapNode()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a new node with initially no children.
   */
  private MapNode(final boolean allowsChildren, final String label,
                  final Contents contents, final long desiredAddress)
  {
    super(contents, allowsChildren);
    if (!allowsChildren) {
      if (contents == null) {
        throw new NullPointerException("contents");
      }
    }
    this.label = label;
    this.desiredAddress = desiredAddress;
    address = -1; // resolve later
    listeners = new Vector<MapChangeListener>();
    if (contents != null) {
      total_size = contents.getBitSize();
      contents.addContentsChangeListener(this);
    } else {
      total_size = 0;
    }
  }

  /**
   * Creates a node that allows children but does not contain a Contents
   * object.
   * @param label The label of this node.
   * @param desiredAddress If negative, automatically determine an
   *    absolute address for this node.  If non-negative, request that
   *    this node will appear at the specified absolute address in the
   *    address space.  Effectively, by setting an absolute address,
   *    an area of inaccessible memory bits will precede this node's
   *    data in order to make this node appear at the desired address.
   *    If specifying an absolute address, it must be chosen such that
   *    all previous nodes' memory mapped contents (with respect to
   *    depth first search order) fit into the address space range
   *    preceding the desired address.  Note that validity check for
   *    this restriction will be made only upon completion of the tree
   *    and thus may result in throwing an exception some time later.
   * @exception NullPointerException If contents equals null.
   */
  public MapNode(final String label, final long desiredAddress)
  {
    this(true, label, null, desiredAddress);
  }

  /**
   * Creates a node that allows adding children but does not contain
   * any content.  Automatically determines the absolute address for
   * this node.
   * @param label The label of this node.
   * @exception NullPointerException If contents equals null.
   */
  public MapNode(final String label)
  {
    this(label, -1);
  }

  /**
   * Creates a node that contains a Contents object (and hence does not
   * allow children).
   * @param label The label of this node.
   * @param contents The underlying Contents object.
   * @param desiredAddress If negative, automatically determine an
   *    absolute address for this node.  If non-negative, request that
   *    this node will appear at the specified absolute address in the
   *    address space.  Effectively, by setting an absolute address,
   *    an area of inaccessible memory bits will precede this node's
   *    data in order to make this node appear at the desired address.
   *    If specifying an absolute address, it must be chosen such that
   *    all previous nodes' memory mapped contents (with respect to
   *    depth first search order) fit into the address space range
   *    preceding the desired address.  Note that validity check for
   *    this restriction will be made only upon completion of the tree
   *    and thus may result in throwing an exception some time later.
   * @exception NullPointerException If contents equals null.
   */
  public MapNode(final String label, final Contents contents,
                 final long desiredAddress)
  {
    this(false, label, contents, desiredAddress);
  }

  /**
   * Creates a node that contains a Contents object (and hence does
   * not allow children) and automatically determines the absolute
   * address for this node.
   * @param label The label of this node.
   * @param contents The underlying Contents object.
   * @exception NullPointerException If contents equals null.
   */
  public MapNode(final String label, final Contents contents)
  {
    this(label, contents, -1);
  }

  public void editingPathValueChanged(final Contents contents)
  {
    final TreeNode root = getRoot();
    final Map map = ((AbstractDevice.MapRoot)root).getMap();
    map.stopEditing();
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
  private void fireMapChangeEvents(final DefaultTreeModel model)
  {
    final Enumeration<MapChangeListener> listeners_enum = listeners.elements();
    final MapChangeEvent e = new MapChangeEvent(this, model);
    while (listeners_enum.hasMoreElements())
      (listeners_enum.nextElement()).mapChangePerformed(e);
  }

  /**
   * Adds a MapChangeListener to this Contents node.
   * @param l The MapChangeListener to add.
   */
  public void addMapChangeListener(final MapChangeListener l)
  {
    listeners.addElement(l);
  }

  /**
   * Removes a MapChangeListener from this Contents node.
   * @param l The MapChangeListener to remove.
   */
  public void removeMapChangeListener(final MapChangeListener l)
  {
    listeners.removeElement(l);
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
   * Returns a descriptive label for this contents.
   * @return A descriptive label.
   */
  public String getLabel()
  {
    return label;
  }

  /**
   * If this node does not allow children, this method returns the
   * appropriate Contents object associated with this node.
   * @return The Contents object associated with this node.
   */
  public Contents getContents()
  {
    final Object obj = getUserObject();
    if (obj == null) {
      return null;
    }
    if (!(obj instanceof Contents)) {
      throw new IllegalStateException("user object is not contents [obj=" +
                                      obj.getClass() + "]");
    }
    return (Contents)getUserObject();
  }

  public Component getEditor()
  {
    final Contents contents = getContents();
    return contents != null ? contents.getEditor() : null;
  }

  /**
   * Returns the numerical representation of this node's value.
   * @return The numerical representation of this node's value.
   */
  public Integer getValue()
  {
    final Contents contents = getContents();
    if (contents != null) {
      return contents.getValue();
    } else {
      return null;
    }
  }

  /**
   * Returns this node's value as string for display.
   * @return This node's value as string for display.
   */
  public String getDisplayValue()
  {
    final Contents contents = getContents();
    if (contents != null) {
      return contents.getDisplayValue();
    } else {
      return null;
    }
  }

  /**
   * Returns the icon associated with this node.
   * @return The icon associated with this node.
   */
  public Icon getIcon()
  {
    final Contents contents = getContents();
    if (contents != null) {
      return contents.getIcon();
    } else {
      return null;
    }
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
    final MapNode child = (MapNode)newChild;
    child.reset(null);
    adjust_total_size(((MapNode)newChild).total_size);
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
    adjust_total_size(-child.total_size);
  }

  @Override
  public void setUserObject(Object userObject)
  {
    // The user object is actually already updated in
    // RangeContents#editingPathValueChanged().
    // Hence, this method does not change anything in the map.
  }

  /**
   * Adjusts the total size of this map and all of its
   * parent maps.
   * @param delta_size The adjustment size.
   */
  private void adjust_total_size(final long delta_size)
  {
    total_size += delta_size;
    if (parent != null)
      ((MapNode)parent).adjust_total_size(delta_size);
  }

  /**
   * Returns the total size of this node's memory in bits.
   * @return The total size of this node's memory in bits.
   */
  public long getTotalSize()
  {
    return total_size;
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
    if (desiredAddress == -1) {
      // no desired address specified => use default
      address = nextAvailableAddress;
    } else if (nextAvailableAddress > desiredAddress) {
      throw new RuntimeException("invalid desired address " + desiredAddress +
                                 " for node " + this + ": " +
                                 "desired address must be " +
                                 nextAvailableAddress + " or higher");
    } else {
      // use desired address
      address = desiredAddress;
    }
    final Contents contents = getContents();
    long result =
      address + (contents != null ? contents.getBitSize() : 0);
    for (int i = 0; i < getChildCount(); i++) {
      final MapNode child = (MapNode)getChildAt(i);
      result = child.resolveAddresses(result);
    }
    return result;
  }

  /**
   * Increments the contents of this node, if possible.
   * @param model The tree model of the tree that contains this node.
   */
  public void increment(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Contents contents = getContents();
        contents.increment();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Decrements the contents of this node.
   * @param model The tree model of the tree that contains this node.
   */
  public void decrement(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Contents contents = getContents();
        contents.decrement();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Sets the contents of this node to the uppermost value that is in range.
   * @param model The tree model of the tree that contains this node.
   */
  public void uppermost(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Contents contents = getContents();
        contents.uppermost();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Sets the contents of this node to the lowermost value that is in range.
   * @param model The tree model of the tree that contains this node.
   */
  public void lowermost(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Contents contents = getContents();
        contents.lowermost();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Resets the contents of this node to its default value.
   * @param model The tree model of the tree that contains this node.
   */
  public void reset(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Contents contents = getContents();
        contents.reset();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Locates the node that covers the specified address.
   * @param address The address to be located.
   * @return The node that covers the specified address, or null, if
   *    the address either can not be found in the map or is inaccessible.
   */
  public MapNode locate(final long address)
  {
    if ((address < this.address) || (address >= this.address + total_size)) {
      // address not among this node or its descendants
      if (parent != null) {
        // try looking at sister nodes
        return ((MapNode)parent).locate(address);
      }
      // no such address
      return null;
    }

    // wanted node must be among this node or its descendants

    if (!getAllowsChildren()) {
      // have no children => wanted node must be myself
      return this;
    }

    MapNode previous_node = null;
    MapNode node = null;
    long next_address = -1;
    for (int i = 0;
         i < getChildCount() && (address > next_address);
         i++) {
      previous_node = node;
      node = (MapNode)getChildAt(i);
      next_address = node.address + node.total_size;
    }

    // Assertion[1] (previous_node != null):
    //   The while loop must always be executed at least twice, as
    //   the parent node has the same address as the first child node.
    //   If not, the map structure is corrupted.
    //

    if (previous_node == null) {
      throw new IllegalStateException("map structure corrupt");
    }

    // Assertion[2] (address <= next_address):
    //   The location must somewhere among the childs because of the
    //   if clauses that guard this block. Otherwise, the map
    //   structure is corrupt.
    //

    if (address > next_address) {
      throw new IllegalStateException("map structure corrupt");
    }

    if (address < previous_node.address) {
      // address is located in inaccessible (padding) area
      return null;
    }

    if (previous_node.getAllowsChildren()) {
      // more specific location required
      return previous_node.locate(address);
    }

    // gotcha!
    return previous_node;
  }

  /**
   * Returns data contents according to the given address and amount.
   * The data, that is returned, may not go beyond the node that contains
   * the addressed data.
   * @param address The address of the data.
   * @param size The bit size of the data. The maximum size is limited
   *    through the effective bit size of the addressed node, as the
   *    requested data may not go beyond the node.
   * @return Data contents according to the given address and amount.
   * @exception IllegalArgumentException If size is below 0.
   * @exception IllegalArgumentException If the specified address is not
   *    accessible.
   * @exception IllegalArgumentException If size goes beyond the addressed
   *    node.
   */
  public int[] getData(final long address, final int size)
  {
    if (size < 0)
      throw new IllegalArgumentException("size < 0");
    if (size == 0)
      return new int[0];
    final MapNode node = locate(address);
    if (node == null)
      throw new IllegalArgumentException("address not accessible");
    else
      return node.getLocalData(address, size);
  }

  /**
   * Returns data contents according to the given address and amount.
   * Assumes, that this node completely contains the requested data.<BR>
   * [PENDING: This is not yet fully implemented]
   * @param address The address of the data.
   * @param size The bit size of the data. The maximum size is limited
   *    through the effective bit size of the addressed node, as the
   *    requested data may not go beyond the node.
   * @excveption IllegalArgumentException If size goes beyond the addressed
   *    node.
   */
  private int[] getLocalData(final long address, final int size)
  {
    if (getAllowsChildren())
      throw new IllegalArgumentException("internal error: locate failed [1]");
    else
      {
        final int shift_size = (int)(address - this.address);
        final Contents contents = getContents();
        final int contents_size = contents.getBitSize();
        if (shift_size >= contents_size)
          throw new IllegalStateException("locate failed [2]");
        final int[] local_data = contents.toBits();
        /*
         * [PENDING: incomplete implementation]
         *
        final int local_size = Math.min(size, contents_size - shift_size);
        local_data = shiftLeft(local_data, shift_size);
        local_data = trim(local_data, local_size);
        if (local_size < size)
          local_data |=
            (locate(address + local_size).
             getLocalData(address + local_size, size - local_size)
             << local_size);
             */
        return local_data;
      }
  }

  /**
   * Given an array of bits (which is, for performance reasons, organized as
   * an array of int values), shifts the data n bit positions to the right,
   * thereby loosing the n lowermost bits.
   * @param data The data to be shifted.
   * @param n The number of bit positions to shift.
   * @return The shifted data.
   */
  private static int[] shiftRight(final int[] data, final int n)
  {
    final int intShift = n / 32;
    final int bitShift = n % 32;
    final int[] shiftedData = new int[data.length - intShift];
    for (int i = 0; i < shiftedData.length; i++)
      {
        shiftedData[i] = data[i + intShift] >> bitShift;
        if ((bitShift > 0) && ((i + intShift + 1) < data.length))
          shiftedData[i] |= data[i + intShift + 1] << (32 - bitShift);
      }
    return shiftedData;
  }

  /**
   * Returns a clone of the specified boolean array.
   * @param source The boolean array to be cloned.
   * @return The clone of the specified boolean array.
   */
  private static boolean[] cloneBooleanArray(final boolean[] source)
  {
    final boolean[] target = new boolean[source.length];
    for (int i = 0; i < source.length; i++)
      target[i] = source[i];
    return target;
  }

  /**
   * Returns a clone of this node and all of its children, if any.
   * @return A clone of this node and all of its children, if any.
   */
  @Override
  public MapNode clone()
  {
    final MapNode newNode = (MapNode)super.clone();
    if (getAllowsChildren())
      for (int i = 0; i < getChildCount(); i++)
        newNode.add(((MapNode)getChildAt(i)).clone());
    return newNode;
  }

  @Override
  public String toString()
  {
    final StringBuffer s = new StringBuffer();
    final TreeNode[] path = getPath();
    for (final TreeNode node : path) {
      if (s.length() > 0) {
        s.append(" => ");
      }
      final MapNode mapNode = (MapNode)node;
      s.append(mapNode.label);
    }
    return "TreePath[" + s + "]";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
