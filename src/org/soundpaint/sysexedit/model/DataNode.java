/*
 * @(#)DataNode.java 1.00 18/06/09
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

public class DataNode extends MapNode implements ValueChangeListener
{
  private static final long serialVersionUID = -2825523179205933317L;

  /**
   * Creates a data node with the specified value and no children.
   * @param value The underlying Value object.
   * @exception NullPointerException If value equals null.
   */
  public DataNode(final Value value)
  {
    super(value);
    if (value == null) {
      throw new NullPointerException("value");
    }
    value.addValueChangeListener(this);
  }

  protected MapNode getDfsLastDescendant()
  {
    return this;
  }

  protected boolean allowsChildren()
  {
    return true;
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
    return value.getDisplayValue();
  }

  protected MapNode resolveDfsLastDescendant()
  {
    return this;
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
    throw new RuntimeException("can not insert node beneath data leaf node");
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
    throw new RuntimeException("can not remove node from beneath of data leaf node");
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
   * Increments the value of this node, if possible.
   * @param model The tree model of the tree that contains this node.
   */
  public void increment(final DefaultTreeModel model)
  {
    if (!getAllowsChildren()) {
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
    if (!getAllowsChildren()) {
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
    if (!getAllowsChildren()) {
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
    if (!getAllowsChildren()) {
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
    if (!getAllowsChildren()) {
      final Value value = getValue();
      value.reset();
      fireMapChangeEvents(model);
    }
  }

  public int getBitSize()
  {
    final Value value = getValue();
    return value != null ? value.getBitSize() : 0;
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
    final DataNode node = locate(address);
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

  @Override
  public String toString()
  {
    return "DataNode[" + getTreePath() + "]";
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
