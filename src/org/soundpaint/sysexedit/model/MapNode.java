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
public class MapNode extends DefaultMutableTreeNode
  implements MapChangeListener, ValueChangeListener
{
  private static final long serialVersionUID = -1726377369359671649L;

  private final String label;

  // map change listeners
  private final List<MapChangeListener> listeners;

  // the desired absolute bit address of this node
  private final long desiredAddress;

  // the confirmed absolute bit address of this node
  private long address;

  /**
   * The node preceding this node in depth first search order, or null
   * if there is no node preceding this one.
   */
  private MapNode dfsPreviousNode;

  /**
   * The node following this node in depth first search order, or null
   * if there is no node follwing this one.
   */
  private MapNode dfsNextNode;

  /**
   * The last node in depth first search order of all descandants of
   * this node, or <code>this</code>, if this node is a leaf node.
   */
  private MapNode dfsLastDescendant;

  private MapNode()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a new node with initially no children.
   */
  public MapNode(final String description, final String label,
                 final boolean allowsChildren,
                 final Value value, final long desiredAddress)
  {
    super(value, allowsChildren);
    if (!allowsChildren) {
      if (value == null) {
        throw new NullPointerException("value");
      }
    }
    this.label = label;
    this.desiredAddress = desiredAddress;
    address = -1; // resolve later
    listeners = new ArrayList<MapChangeListener>();
    if (value != null) {
      value.addValueChangeListener(this);
    }
  }

  private MapNode(final String label, final boolean allowsChildren,
                  final Value value, final long desiredAddress)
  {
    this(null, label, allowsChildren, value, desiredAddress);
  }

  /**
   * Creates a node that allows children but does not contain a Value
   * object.
   * @param description An optional informal description of this
   * MapNode.  Useful e.g. as tooltip in the GUI.
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
  public MapNode(final String description, final String label,
                 final long desiredAddress)
  {
    this(description, label, true, null, desiredAddress);
  }

  /**
   * Creates a node that allows children but does not contain a Value
   * object.
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
  public MapNode(final String label, final long desiredAddress)
  {
    this(label, true, null, desiredAddress);
  }

  /**
   * Creates a node that allows adding children but does not contain
   * any value.  Automatically determines the absolute address for
   * this node.
   * @param label The label of this node.
   */
  public MapNode(final String description, final String label)
  {
    this(description, label, -1);
  }

  /**
   * Creates a node that allows adding children but does not contain
   * any value.  Automatically determines the absolute address for
   * this node.
   * @param label The label of this node.
   */
  public MapNode(final String label)
  {
    this(label, -1);
  }

  /**
   * Creates a node that contains a Value object (and hence does not
   * allow children).
   * @param description An optional informal description of this
   * MapNode.  Useful e.g. as tooltip in the GUI.
   * @param label The label of this node.
   * @param value The underlying Value object.
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
   * @exception NullPointerException If value equals null.
   */
  public MapNode(final String description, final String label,
                 final Value value, final long desiredAddress)
  {
    this(description, label, false, value, desiredAddress);
  }

  /**
   * Creates a node that contains a Value object (and hence does not
   * allow children).
   * @param label The label of this node.
   * @param value The underlying Value object.
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
   * @exception NullPointerException If value equals null.
   */
  public MapNode(final String label, final Value value,
                 final long desiredAddress)
  {
    this(null, label, false, value, desiredAddress);
  }

  /**
   * Creates a node that contains a Value object (and hence does not
   * allow children) and automatically determines the absolute address
   * for this node.
   * @param description An optional informal description of this
   * MapNode.  Useful e.g. as tooltip in the GUI.
   * @param label The label of this node.
   * @param value The underlying Value object.
   * @exception NullPointerException If value equals null.
   */
  public MapNode(final String description, final String label,
                 final Value value)
  {
    this(description, label, value, -1);
  }

  /**
   * Creates a node that contains a Value object (and hence does not
   * allow children) and automatically determines the absolute address
   * for this node.
   * @param label The label of this node.
   * @param value The underlying Value object.
   * @exception NullPointerException If value equals null.
   */
  public MapNode(final String label, final Value value)
  {
    this(null, label, value);
  }

  public void editingPathValueChanged(final Value value)
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

  /**
   * If this node does not allow children, this method returns the
   * appropriate Value object associated with this node.
   * @return The Value object associated with this node.
   */
  public Value getValue()
  {
    final Object obj = getUserObject();
    if (obj == null) {
      return null;
    }
    if (!(obj instanceof Value)) {
      throw new IllegalStateException("user object is not a Value [obj=" +
                                      obj.getClass() + "]");
    }
    return (Value)getUserObject();
  }

  public Component getEditor()
  {
    final Value value = getValue();
    return value != null ? value.getEditor() : null;
  }

  /**
   * Returns the numerical representation of this node's value.
   * @return The numerical representation of this node's value.
   */
  public Integer getNumericalValue()
  {
    final Value value = getValue();
    if (value != null) {
      return value.getNumericalValue();
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
    final Value value = getValue();
    if (value != null) {
      return value.getDisplayValue();
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
    final Value value = getValue();
    if (value != null) {
      return value.getIcon();
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

  @Override
  public void setUserObject(final Object userObject)
  {
    /*
     * The user object is actually already updated in
     * ValueImpl#editingPathValueChanged().  Hence, this method does
     * not change anything in the map.
     */
  }

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
    MapNode dfsLastDescendant = this;
    for (int i = 0; i < getChildCount(); i++) {
      final MapNode child = (MapNode)getChildAt(i);
      dfsLastDescendant = child.resolveDfsLinkedNodes(dfsLastDescendant);
    }
    this.dfsLastDescendant = dfsLastDescendant;
    return dfsLastDescendant;
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
    final Value value = getValue();
    long result =
      address + (value != null ? value.getBitSize() : 0);
    for (int i = 0; i < getChildCount(); i++) {
      final MapNode child = (MapNode)getChildAt(i);
      result = child.resolveAddresses(result);
    }
    return result;
  }

  /**
   * Increments the value of this node, if possible.
   * @param model The tree model of the tree that contains this node.
   */
  public void increment(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Value value = getValue();
        value.increment();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Decrements the value of this node, if possible.
   * @param model The tree model of the tree that contains this node.
   */
  public void decrement(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Value value = getValue();
        value.decrement();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Sets the value of this node to the uppermost value that is in range.
   * @param model The tree model of the tree that contains this node.
   */
  public void uppermost(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Value value = getValue();
        value.uppermost();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Sets the value of this node to the lowermost value that is in range.
   * @param model The tree model of the tree that contains this node.
   */
  public void lowermost(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Value value = getValue();
        value.lowermost();
        fireMapChangeEvents(model);
      }
  }

  /**
   * Resets the value of this node to its default value.
   * @param model The tree model of the tree that contains this node.
   */
  public void reset(final DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
        final Value value = getValue();
        value.reset();
        fireMapChangeEvents(model);
      }
  }

  private int getBitSize()
  {
    final Value value = getValue();
    return value != null ? value.getBitSize() : 0;
  }

  /**
   * Locates the node that covers the specified address.
   * @param address The address to be located.
   * @return The node that covers the specified address, or null, if
   *    the address either can not be found in the map or is inaccessible.
   */
  public MapNode locate(final long address)
  {
    final long fromAddress = this.address;

    final MapNode toAddressNode = dfsLastDescendant.dfsNextNode;
    final long toAddress =
      toAddressNode != null ?
      toAddressNode.address :
      dfsLastDescendant.address + dfsLastDescendant.getBitSize();

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
              return dfsNextNode;
            }
          }
          if (address < dfsNextNode.address + dfsNextNode.getBitSize()) {
            return dfsNextNode;
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

    if (!getAllowsChildren()) {
      // have no children => wanted node must be myself
      return this;
    }

    MapNode child = null;
    long nextAddress = -1;
    for (int i = 0;
         i < getChildCount() && (address >= nextAddress);
         i++) {
      child = (MapNode)getChildAt(i);
      final MapNode childDfs = child.dfsLastDescendant.dfsNextNode;
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

  private static final Integer[] EMPTY_INTEGER_ARRAY = new Integer[0];

  /**
   * Returns the numerical value according to the given address and
   * amount.  The data, that is returned, may not go beyond the node
   * that contains the addressed data.
   * @param address The address of the data.
   * @param size The bit size of the data. The maximum size is limited
   *    through the effective bit size of the addressed node, as the
   *    requested data may not go beyond the node.
   * @return The numerical value according to the given address and
   * amount, split into int values.
   * @exception IllegalArgumentException If size is below 0.
   * @exception IllegalArgumentException If the specified address is not
   *    accessible.
   * @exception IllegalArgumentException If size goes beyond the addressed
   *    node.
   */
  public Integer[] getData(final long address, final int size)
  {
    if (size < 0) {
      throw new IllegalArgumentException("size < 0");
    }
    if (size == 0) {
      return new Integer[0];
    }
    final MapNode node = locate(address);
    if (node == null) {
      throw new IllegalArgumentException("address not accessible");
    }
    final List<Integer> resultList = new ArrayList<Integer>();
    node.appendDataToResult(address, size, resultList);
    return resultList.toArray(EMPTY_INTEGER_ARRAY);
  }

  /**
   * Returns the numerical value according to the given address and
   * amount.  Assumes, that this node completely contains the
   * requested data.<BR> [PENDING: This is not yet fully implemented]
   * @param address The address of the data.
   * @param size The bit size of the data. The maximum size is limited
   *    through the effective bit size of the addressed node, as the
   *    requested data may not go beyond the node.
   * @excveption IllegalArgumentException If size goes beyond the addressed
   *    node.
   */
  private void appendDataToResult(final long address, final int size,
                                  final List<Integer> resultList)
  {
    if (getAllowsChildren()) {
      throw new UnsupportedOperationException("only leaf nodes carry data");
    }
    final int addrOffs = (int)(address - this.address);
    final Value value = getValue();
    final int valueBitSize = value.getBitSize();
    final int shiftSize = valueBitSize - size - addrOffs;
    if (shiftSize < 0) {
      throw new IllegalStateException("Partial addresses nodes not yet fully supported");
    }
    final int[] localData = value.toBits();
    final int localDataIndex = shiftSize / 32;
    final int localDataBitOffs = shiftSize % 32;
    final int dataValue = localDataBitOffs >= 0 ?
      localData[localDataIndex] >> localDataBitOffs :
      localData[localDataIndex] << -localDataBitOffs;
    final int needClearUppermostBits =
      32 - size - Math.max(localDataBitOffs, 0);
    // TODO: Corner-case for (shiftSize < 0), e.g. single unused bits
    // in map not yet supported: If (localDataBitOffs < 0), fetch
    // remaining lower bits from (new_address := address + size +
    // localDataBitOffs, new_size := -localDataBitOffs).
    final int trimmedDataValue;
    if (needClearUppermostBits > 0) {
      final int mask = 0xffffffff >>> needClearUppermostBits;
      trimmedDataValue = dataValue & mask;
    } else {
      trimmedDataValue = dataValue;
    }
    resultList.add(trimmedDataValue);
    if (size > 32) {
      appendDataToResult(address + 32, size - 32, resultList);
    }
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
