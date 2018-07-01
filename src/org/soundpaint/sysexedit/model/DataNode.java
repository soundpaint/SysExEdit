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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.soundpaint.sysexedit.gui.JValue;
import org.soundpaint.sysexedit.gui.Map;

public class DataNode extends MapNode implements ValueChangeListener
{
  private static final long serialVersionUID = -2825523179205933317L;

  /** The associated value type. */
  private final Value value;

  /** The current value. */
  private int numericalValue;

  /**
   * The editor instance of the associated value type for this data
   * node for entering a value.
   */
  private Editor editor;

  /**
   * Creates a data node with the specified value and no children
   * and no explicitly specified desired address.
   * @param value The underlying Value object.
   * @exception NullPointerException If value equals null.
   */
  public DataNode(final Value value)
  {
    this(value, -1);
  }

  /**
   * Creates a data node with the specified value and no children.
   * @param value The underlying Value object.
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
   * @exception NullPointerException If value equals null.
   */
  public DataNode(final Value value, final long desiredAddress)
  {
    super(getLabel(value), desiredAddress, false);
    this.value = value;
    this.numericalValue = value.getDefaultValue();
  }

  private static String getLabel(final Value value) {
    if (value == null) {
      throw new NullPointerException("value must not be null");
    }
    return value.getLabel();
  }

  protected MapNode getDfsLastDescendant()
  {
    return this;
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

  public Editor getEditor()
  {
    if (editor == null) {
      editor = value.createEditor();
      ((Component)editor).addKeyListener(createKeyListener());
    }
    return editor;
  }

  /**
   * Returns the numerical representation of this node's value.  The
   * value is not bound to a specific value range; thus it even may be
   * out of any value range.
   * @return The numerical representation of this node's value.
   */
  public int getNumericalValue()
  {
    return numericalValue;
  }

  /**
   * Sets the underlying numerical value of this Value object.  The
   * value is not bound to a specific value range of the associated
   * sparse type; thus it even may be out of any value range.
   * @param numericalValue The underlying numerical value.
   * @exception IllegalArgumentException If value is not an instance
   *    of the class that holds the value represented by this class.
   * @see #reset
   */
  public void setNumericalValue(final int numericalValue)
  {
    // TODO: Check numerical value against ranges of valid values?
    this.numericalValue = numericalValue;
    getEditor().setSelectionByNumericalValue(numericalValue);
  }

  /**
   * Returns a String that represents this Value object's underlying
   * numerical value, or null, if the value is out of range with
   * respect to the associated type.
   * @return A String representation for this Value object's
   * underlying numerical value.
   */
  public String getDisplayValue()
  {
    return value.getDisplayValue(getNumericalValue());
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
    return value.getIcon();
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

  /**
   * Returns the underlying numerical value to apply when this Value
   * instance is reset.  This value is not bound to a specific value
   * range of the associated sparse type; thus it even may be out of
   * the currently selected or any other value range.
   * @return The current default value.
   */
  public int getDefaultValue()
  {
    return value.getDefaultValue();
  }

  /**
   * Increments the underlying numerical value, if possible.
   * @param model The tree model of the tree that contains this node.
   */
  public void increment(final DefaultTreeModel model)
  {
    final Integer succ = value.succ(getNumericalValue());
    if (succ != null) {
      setNumericalValue(succ);
    }
    fireMapChangeEvents(model);
  }

  /**
   * Decrements the underlying numerical value, if possible.
   * @param model The tree model of the tree that contains this node.
   */
  public void decrement(final DefaultTreeModel model)
  {
    final Integer pred = value.pred(getNumericalValue());
    if (pred != null) {
      setNumericalValue(pred);
    }
    fireMapChangeEvents(model);
  }

  /**
   * Sets the underlying numerical value to the uppermost value that
   * is valid.
   * @param model The tree model of the tree that contains this node.
   */
  public void uppermost(final DefaultTreeModel model)
  {
    final Integer numericalValue = value.uppermost();
    if (numericalValue != null) {
      setNumericalValue(numericalValue);
      fireMapChangeEvents(model);
    } else {
      // empty set of values => nothing that could be changed
    }
  }

  /**
   * Sets the underlying numerical value to the lowermost value that
   * is valid.
   * @param model The tree model of the tree that contains this node.
   */
  public void lowermost(final DefaultTreeModel model)
  {
    final Integer numericalValue = value.lowermost();
    if (numericalValue != null) {
      setNumericalValue(numericalValue);
      fireMapChangeEvents(model);
    } else {
      // empty set of values => nothing that could be changed
    }
  }

  /**
   * Resets the underlying numerical value to the default value.
   * @param model The tree model of the tree that contains this node.
   * @see Value#getDefaultValue
   */
  public void reset(final DefaultTreeModel model)
  {
    setNumericalValue(value.getDefaultValue());
    fireMapChangeEvents(model);
  }

  public int getBitSize()
  {
    return value.getBitSize();
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
   * Returns a numerical representation of the value according to the
   * underlying bit layout.
   * @return The array of bits that represents the underlying
   *    numerical value.  For performance reasons, the return value is
   *    actually not an array of bits, but rather an array of int
   *    values with each int value holding 32 bits. The least
   *    significant bit is stored in the least significant bit of
   *    field 0 of the return value.
   */
  public int[] toBits()
  {
    final int[] bits = new int[1];
    bits[0] = getNumericalValue();
    return bits;
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
    final int valueBitSize = value.getBitSize();
    final int shiftSize = valueBitSize - size - addrOffs;
    if (shiftSize < 0) {
      throw new IllegalStateException("Partial addresses nodes not yet fully supported");
    }
    final int[] localData = toBits();
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

  private KeyListener createKeyListener()
  {
    final KeyListener keyListener = new KeyAdapter()
      {
        public void keyTyped(final KeyEvent e)
        {
          if (e.getKeyChar() == '\n') {
            SwingUtilities.invokeLater(new Runnable()
              {
                public void run()
                {
                  final JValue newValue = getEditor().getSelectedValue();
                  if (newValue != null) {
                    editingPathValueChanged(newValue.getSystemValue());
                  }
                }
              });
          }
        }
      };
    return keyListener;
  }

  public void editingPathValueChanged(final int numericalValue)
  {
    // Here, we actually update the map.
    setNumericalValue(numericalValue);
    final TreeNode root = getRoot();
    final Map map = ((AbstractDevice.MapRoot)root).getMap();
    map.stopEditing();
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
