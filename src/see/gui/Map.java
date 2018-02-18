/*
 * @(#)Map.java 1.00 99/01/30
 *
 * Copyright (C) 1999, 2018 Jürgen Reuter
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

package see.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import see.model.AddressRepresentation;
import see.model.Contents;
import see.model.MapNode;
import see.model.Representation;

/**
 * This class extends JTree with some more attributes that specify how the
 * Map is to be displayed as a tree.
 */
class Map extends JTree
{
  private static final long serialVersionUID = 5701224992251396503L;

  /**
   * This String is used as an ID for a contents value that could not be
   * evaluated.
   */
  protected final static
  String STRING_CONTENTS_UNKNOWN = "???";

  /**
   * When displaying addresses, call addressToString() of this class.
   */
  protected AddressRepresentation addressRepresentation;

  /**
   * A flag that turns address information generation on or off.
   */
  protected boolean addressInfoEnabled = false;

  public void setForeground(Color value)
  {
    CellRenderer renderer = (CellRenderer)getCellRenderer();
    renderer.setForeground(value);
    super.setForeground(value);
  }

  public void setBackground(Color value)
  {
    CellRenderer renderer = (CellRenderer)getCellRenderer();
    renderer.setBackground(value);
    super.setBackground(value);
  }

  public void updateUI()
  {
    TreeCellRenderer renderer = getCellRenderer();
    if (renderer == null)
      setCellRenderer(renderer = new CellRenderer());
    ((CellRenderer)renderer).updateUI(); // updates cell widths
    super.updateUI();
  }

  /**
   * Sets the AddressRepresentation object that is used when address
   * information is to be displayed.
   * @param addressRepresentation The AddressRepresentation object to use.
   * @see #getAddressRepresentation
   * @see #setAddressInfoEnabled
   */
  void setAddressRepresentation(AddressRepresentation representation)
  {
    addressRepresentation = representation;
  }

  /**
   * Returns the AddressRepresentation object that is currently set.
   * @return The AddressRepresentation object.
   * @see #setAddressRepresentation
   */
  AddressRepresentation getAddressRepresentation()
  {
    return addressRepresentation;
  }

  /**
   * Enables or disables address information of method toString(). In
   * other words, only if address information is enabled, method toString()
   * will return a String that contains address information about this
   * node. If disabled, toString() will never return address information.
   * Note that this method is static, i.e. it affects all nodes in all
   * trees. The default value is false, i.e. disabled.
   * @param enabled If true, enables address information.
   * @see setAddressRepresentation
   * @see #getAddressInfoEnabled
   */
  void setAddressInfoEnabled(boolean enabled)
  {
    addressInfoEnabled = enabled;
  }

  /**
   * Returns true, if address information is enabled on toString() method.
   * @return True, if address information is enabled on toString() method.
   * @see #setAddressInfoEnabled
   */
  boolean getAddressInfoEnabled()
  {
    return addressInfoEnabled;
  }

  /**
   * Creates the text for a given tree cell node.
   * @param node The node.
   * @return A proper String object for the tree cell.
   */
  String getTreeCellText(MapNode node)
  {
    String text = null;
    long address = node.getAddress();
    String addressStr;
    if ((address >= 0) && addressInfoEnabled)
      addressStr = addressRepresentation.addressToString(address) + ": ";
    else // address not evaluated yet or not enabled
      addressStr = "";
    String nameStr = (node.toString() != null) ? node.toString() : "";
    String contentsStr = null;
    Contents contents = node.getContents();
    if (contents != null)
      {
	Representation representation = contents.getSelectedRepresentation();
	if (representation != null)
	  contentsStr = representation.toString(contents.getValue());
      }
    else
      contentsStr = "";
    if (contentsStr == null)
      contentsStr = STRING_CONTENTS_UNKNOWN;
    String sep = ((nameStr.length() > 0) && (contentsStr.length() > 0)) ?
      " : " : "";
    text = addressStr + nameStr + sep + contentsStr;
    return text;
  }

  /**
   * Creates the icon for a given tree cell node.
   * @param node The node.
   * @param leaf True, if the node is a leaf.
   * @param expanded True, if the node is expanded.
   * @return A proper Icon object for the tree cell.
   */
  Icon getTreeCellIcon(MapNode node, boolean leaf, boolean expanded)
  {
    Icon icon = null;
    if (leaf)
      {
	String iconKey =
	  node.getContents().getSelectedRepresentation().getIconKey();
	if (iconKey != null)
	  icon = UIManager.getIcon(iconKey);
      }
    else if (expanded)
      icon = UIManager.getIcon("Tree.openIcon");
    else
      icon = UIManager.getIcon("Tree.closedIcon");
    return icon;
  }

  /**
   * Displays an entry in a tree.
   * This is based on javax.swing.tree.DefaultTreeCellRenderer.
   */
  private class CellRenderer
    extends SelectableLabel implements TreeCellRenderer
  {
    private static final long serialVersionUID = 2131652642719584363L;

    public void updateUI()
    {
      super.updateUI();
      // super.updateUI() resets the background color to ui default,
      // but we want to use Map's background color:
      if (Map.this != null)
	this.setBackground(Map.this.getBackground());
    }

    /**
     * This is messaged from JTree whenever it needs to get the size
     * of the component or it wants to draw it.
     * This attempts to set the font based on value, which will be
     * a TreeNode.
     */
    public Component
      getTreeCellRendererComponent(JTree tree, Object value,
				   boolean selected, boolean expanded,
				   boolean leaf, int row, boolean hasFocus)
    {
      Map map = (Map)tree;
      String text = null;
      Icon icon = null;
      if ((value != null) && (value instanceof MapNode))
	{
	  MapNode node = (MapNode)value;
	  text = map.getTreeCellText(node);
	  icon = map.getTreeCellIcon(node, leaf, expanded);
	}
      if (text == null)
	text = tree.convertValueToText(value, selected, expanded,
				       leaf, row, hasFocus);
      if (icon == null)
	icon = UIManager.getDefaults().getIcon("internal-control");

      setIcon(icon);
      setText(text);
      // setFont(defaultFont);
      setSelected(selected);
      return this;
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
