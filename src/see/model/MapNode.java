/*
 * @(#)MapNode.java 1.00 98/01/31
 *
 * Copyright (C) 1998 Juergen Reuter
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

// $Source:$
// $Revision:$
// $Aliases:$
// $Author:$
// $Date:$
// $State:$

package see.model;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * This class is used to represent a node in the hierarchical structure of
 * the whole memory of a device.<BR>
 * Addressing is done implicitly: the map is supposed to represent a linear
 * addressed array of bits of memory, starting with address 0x0.
 * If the device's accessible memory starts at some higher address, an
 * arbitrary amount of inaccessible memory bits may be specified as
 * address offset for first node in the map. An offset may be declared for
 * each node. This allows to define areas of inaccessible memory.
 */
public class MapNode extends DefaultMutableTreeNode
implements MapChangeListener
{
  private long total_size; // the total bit size including all sub-trees
  private long address; // the absolute bit address of this node
  private long offset; // the number of inaccessible bits preceding this node
  private Contents contents; // only significant, if not allowsChildren
  private Vector listeners = null; // map change listeners

  private MapNode() {}

  /**
   * Creates a new node with initially no children.
   */
  private MapNode(boolean allowsChildren, String name,
		  Contents contents, long contents_size, long offset)
  {
    super(name, allowsChildren);
    if ((!allowsChildren) && (contents == null))
      throw new NullPointerException("contents");
    this.offset = offset;
    this.contents = contents;
    this.total_size =  offset + contents_size;
    address = -1; // evaluate later
  }

  /**
   * Creates a node that allows children but does not contain a Contents
   * object.
   * @param name The name of this node.
   * @param offset The address offset for the child. This allows to
   *    define an area of inaccessible bits that precede the child's
   *    data.
   * @exception NullPointerException If contents equals null.
   */
  public MapNode(String name, long offset)
  {
    this(true, name, null, 0, offset);
  }

  /**
   * Creates a node that allows children but does not contain a Contents
   * object. Offset is supposed to be 0.
   * @param name The name of this node.
   * @exception NullPointerException If contents equals null.
   */
  public MapNode(String name)
  {
    this(name, 0);
  }

  /**
   * Creates a node that contains a Contents object (and hence does not
   * allow children).
   * @param name The name of this node.
   * @param contents The underlying Contents object.
   * @param offset The address offset for the child. This allows to
   *    define an area of inaccessible bits that precede the child's
   *    data.
   * @exception NullPointerException If contents equals null.
   */
  public MapNode(String name, Contents contents, long offset)
  {
    this(false, name, contents, contents.getBitSize(), offset);
  }

  /**
   * Creates a node that contains a Contents object (and hence does not
   * allow children). Offset is supposed to be 0.
   * @param name The name of this node.
   * @param contents The underlying Contents object.
   * @exception NullPointerException If contents equals null.
   */
  public MapNode(String name, Contents contents)
  {
    this(name, contents, 0);
  }

  /**
   * Returns an integer value that serves as an ID that may be posted to
   * a Contents object whose range selection depends on the contents value
   * of this MapNode object.<BR>
   * Basically, this is a function that bijectively maps the current value
   * of instance variable contents_value onto an integer value in the
   * range [0, x-1], where x is the size of the total range of this node's
   * contents_value range. This is only applicable, if contents_value
   * has an enumaratable representation.<BR>
   * [PENDING: This method has not been implemented yet. Currently, it
   * always returns the value 0.]
   * @return An integer value that serves as an ID for another node's
   *    range selection.
   */
  private int getSelector()
  {
    return 0;
  }

  /**
   * Invoked when a map change occurs.
   */
  public void mapChangePerformed(MapChangeEvent e)
  {
    if (contents != null)
      {
	DefaultTreeModel model = e.getModel();
	contents.setSelectedRepresentation(e.getSelector());
	if (model != null)
	  model.nodeChanged(this); // note: this only works *within* a tree
      }
  }

  /**
   * Fires a map change event to all map change listeners.
   * @param model The tree model of the tree that contains this node.
   */
  private void fireMapChangeEvents(DefaultTreeModel model)
  {
    if (listeners != null)
      {
	Enumeration listeners_enum = listeners.elements();
	MapChangeEvent e = new MapChangeEvent(this, model, getSelector());
	while (listeners_enum.hasMoreElements())
	  ((MapChangeListener)listeners_enum.nextElement()).
	    mapChangePerformed(e);
      }
  }

  /**
   * Adds a MapChangeListener to this Contents object.
   */
  public void addMapChangeListener(MapChangeListener l)
  {
    if (listeners == null)
      listeners = new Vector();
    listeners.addElement(l);
  }

  /**
   * Removes a MapChangeListener from this Contents object.
   */
  public void removeMapChangeListener(MapChangeListener l)
  {
    if (listeners != null)
      listeners.removeElement(l);
  }

  /**
   * Sets the address offset for this node.
   * @param offset The address offset for this node. This allows to
   *    define an area of inaccessible bits that precede this node's
   *    data.
   */
  public void setOffset(long offset)
  {
    this.offset = offset;
  }

  /**
   * Returns the address offset for this node.
   * @return The address offset for this node.
   */
  public long getOffset() { return offset; }

  /**
   * Returns the address of this node.
   * @return The address of this node or -1, if it has not been evaluated
   *    yet.
   * @see #evaluateAddresses
   */
  public long getAddress()
  {
    return address;
  }

  /**
   * If this node does not allow children, this method returns the
   * appropriate Contents object associated with this node.
   * @return The Contents object associated with this node.
   */
  public Contents getContents()
  {
    return contents;
  }

  /**
   * Sets a descriptive name for this contents.
   * @param name A descriptive name.
   */
  void setName(String name)
  {
    setUserObject(name);
  }

  /**
   * Returns a descriptive name for this contents.
   * @return A descriptive name.
   */
  String getName()
  {
    return (String)getUserObject();
  }

  /**
   * Removes newChild from its present parent (if it has a parent), sets
   * the child's parent to this node, and then adds the child to this
   * node's child array at index childIndex. newChild must not be null
   * and must not be an ancestor of this node.
   * @param newChild The MutableTreeNode to insert under this node.
   * @param childInden The index in this node's child array where this node
   *    is to be inserted.
   * @exception ArrayIndexOutOfBoundsException If childIndex is out of bounds.
   * @exception IllegalArgumentException If newChild is null or is an
   *    ancestor of this node.
   * @exception IllegalStateException If this node does not allow children.
   */
  public void insert(MutableTreeNode newChild, int childIndex)
  {
    super.insert(newChild, childIndex);
    MapNode child = (MapNode)newChild;
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
  public void remove(int childIndex)
  {
    MapNode child = (MapNode)getChildAt(childIndex);
    super.remove(childIndex);
    adjust_total_size(-child.total_size);
  }

  /**
   * Adjusts the total size of this map and all of its
   * parent maps.
   * @param delta_size The adjustment size.
   */
  private void adjust_total_size(long delta_size)
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
   * Evaluates the address information for the whole tree.<BR>
   * For proper handling of address information, this must be called before
   * using the address information whenever the map has been modified.
   */
  public void evaluateAddresses()
  {
    ((MapNode)getRoot()).evaluateAddresses(0);
  }

  /**
   * Evaluates the address information for this node and all of its
   * children.<BR>
   * For proper handling of address information, this must be called before
   * using the address information whenever the map has been modified.
   * @param externalOffset The offset address to start with.
   */
  private void evaluateAddresses(long externalOffset)
  {
    address = externalOffset + offset;
    for (int i = 0; i < getChildCount(); i++)
      {
	MapNode child = (MapNode)getChildAt(i);
	child.evaluateAddresses(externalOffset);
	externalOffset += child.getTotalSize();
      }
  }

  /**
   * Increments the contents of this node, if possible.
   * @param model The tree model of the tree that contains this node.
   */
  public void increment(DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
	Representation representation = contents.getSelectedRepresentation();
	if (representation.isEnumeratable())
	  {
	    Object succValue = representation.succ(contents.getValue());
	    if (succValue != null)
	      {
		contents.setValue(succValue);
		fireMapChangeEvents(model);
	      }
	  }
      }
  }

  /**
   * Decrements the contents of this node.
   * @param model The tree model of the tree that contains this node.
   */
  public void decrement(DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
	Representation representation = contents.getSelectedRepresentation();
	if (representation.isEnumeratable())
	  {
	    Object predValue = representation.pred(contents.getValue());
	    if (predValue != null)
	      {
		contents.setValue(predValue);
		fireMapChangeEvents(model);
	      }
	  }
      }
  }

  /**
   * Sets the contents of this node to the uppermost value that is in range.
   * @param model The tree model of the tree that contains this node.
   */
  public void uppermost(DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
	contents.setValue(contents.getSelectedRepresentation().uppermost());
	fireMapChangeEvents(model);
      }
  }

  /**
   * Sets the contents of this node to the lowermost value that is in range.
   * @param model The tree model of the tree that contains this node.
   */
  public void lowermost(DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
	contents.setValue(contents.getSelectedRepresentation().lowermost());
	fireMapChangeEvents(model);
      }
  }

  /**
   * Resets the contents of this node to its default value.
   * @param model The tree model of the tree that contains this node.
   */
  public void reset(DefaultTreeModel model)
  {
    if (!getAllowsChildren())
      {
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
  public MapNode locate(long address)
  {
    if ((this.address <= address) && (address < this.address + total_size))
      if (!getAllowsChildren())
	return this;
      else
	{
	  MapNode next_node = null;
	  MapNode node = null;
	  long cumulative_address = this.address;
	  Enumeration children_enum = children();
	  while ((cumulative_address < address) &&
		 children_enum.hasMoreElements())
	    {
	      node = next_node;
	      next_node = (MapNode)children_enum.nextElement();
	      cumulative_address += next_node.total_size;
	    }
	  // Assertion[1] (node != null):
	  //   The while loop must always be executed at least twice, as
	  //   the parent node has the same address as the first child node.
	  //   If not, the map structure is corrupt.
	  //
	  // Assertion[2] (cumulative_address < address):
	  //   The location must somewhere among the childs because of the
	  //   if clauses that guard this block. Otherwise, the map
	  //   structure is corrupt.
	  //
	  if ((node == null) || (cumulative_address < address))
	    throw new IllegalStateException("map structure corrupt");
	  else if (node.address > address)
	    return null; // address is located in inaccessible (padding) area
	  else if (node.getAllowsChildren())
	    return node.locate(address); // more specific location required
	  else
	    return node; // gotcha!
	}
    else
      if (parent != null)
	return ((MapNode)parent).locate(address);
      else
	return null; // address is located in inaccessible (padding) area
  }

  /**
   * Returns data contents according to the given address and amount.
   * The data, that is returned, may not go beyond the node that contains
   * the addressed data.
   * @param address The address of the data.
   * @param size The bit size of the data. The maximum size is limited
   *    through the effective bit size of the addressed node, as the
   *    requested data may not go beyond the node.
   * @exception IllegalArgumentException If size is below 0.
   * @exception IllegalArgumentException If the specified address is not
   *    accessible.
   * @excveption IllegalArgumentException If size goes beyond the addressed
   *    node.
   */
  public int[] getData(long address, int size)
  {
    if (size < 0)
      throw new IllegalArgumentException("size < 0");
    if (size == 0)
      return new int[0];
    MapNode node = locate(address);
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
  private int[] getLocalData(long address, int size)
  {
    if (getAllowsChildren())
      throw new IllegalArgumentException("internal error: locate failed [1]");
    else
      {
	int shift_size = (int)(address - this.address);
	int contents_size = contents.getBitSize();
	if (shift_size >= contents_size)
	  throw new IllegalStateException("locate failed [2]");
	int[] local_data = contents.toBits();
	/*
	 * [PENDING: incomplete implementation]
	 *
	int local_size = Math.min(size, contents_size - shift_size);
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
  private static int[] shiftRight(int[] data, int n)
  {
    int intShift = n / 32;
    int bitShift = n % 32;
    int[] shiftedData = new int[data.length - intShift];
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
  private static boolean[] cloneBooleanArray(boolean[] source)
  {
    boolean[] target = new boolean[source.length];
    for (int i = 0; i < source.length; i++)
      target[i] = source[i];
    return target;
  }

  /**
   * Returns a clone of this node and all of its children, if any.
   * @return A clone of this node and all of its children, if any.
   */
  public Object clone()
  {
    MapNode newNode = null;
    newNode = (MapNode)super.clone();
    if (getAllowsChildren())
      for (int i = 0; i < getChildCount(); i++)
	newNode.add((MapNode)((MapNode)getChildAt(i)).clone());
    return newNode;
  }
}
