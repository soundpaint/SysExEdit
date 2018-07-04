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

package org.soundpaint.sysexedit.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.soundpaint.sysexedit.model.AddressRepresentation;
import org.soundpaint.sysexedit.model.DataNode;
import org.soundpaint.sysexedit.model.Editor;
import org.soundpaint.sysexedit.model.MapNode;
import org.soundpaint.sysexedit.model.SparseType;
import org.soundpaint.sysexedit.model.ValueRangeRenderer;

/**
 * This class extends JTree with some more attributes that specify how the
 * Map is to be displayed as a tree.
 */
public class Map extends JTree
{
  private static final long serialVersionUID = 5701224992251396503L;

  /**
   * When displaying addresses, call addressToString() of this class.
   */
  private final AddressRepresentation addressRepresentation;

  /**
   * A flag that turns address information generation on or off.
   */
  private boolean addressInfoEnabled = false;

  private Map()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public Map(final TreeSelectionListener selectionListener,
             final MapContextMenu mapContextMenu,
             final AddressRepresentation addressRepresentation)
  {
    super();
    this.addressRepresentation = addressRepresentation;
    getSelectionModel().
      setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    setEditable(true);
    setCellRenderer(new CellRenderer());
    setCellEditor(new MapCellEditor());
    setComponentPopupMenu(mapContextMenu);
    addTreeSelectionListener(selectionListener);
  }

  public void setForeground(final Color value)
  {
    final CellRenderer renderer = (CellRenderer)getCellRenderer();
    if (renderer != null) {
      renderer.setForeground(value);
    }
    super.setForeground(value);
  }

  public void setBackground(final Color value)
  {
    final CellRenderer renderer = (CellRenderer)getCellRenderer();
    if (renderer != null) {
      renderer.setBackground(value);
    }
    super.setBackground(value);
  }

  public void updateUI()
  {
    final CellRenderer renderer = (CellRenderer)getCellRenderer();
    if (renderer != null) {
      renderer.updateUI(); // updates cell widths
    }
    super.updateUI();
  }

  /**
   * Returns the AddressRepresentation object that is currently set.
   * @return The AddressRepresentation object.
   */
  public AddressRepresentation getAddressRepresentation()
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
  void setAddressInfoEnabled(final boolean enabled)
  {
    addressInfoEnabled = enabled;
  }

  /**
   * Returns true, if address information is enabled on toString() method.
   * @return True, if address information is enabled on toString() method.
   * @see #setAddressInfoEnabled
   */
  private boolean getAddressInfoEnabled()
  {
    return addressInfoEnabled;
  }

  /**
   * Creates the text for a given tree cell node.
   * @param node The node.
   * @return A proper String object for the tree cell.
   */
  private String getTreeCellText(final MapNode node)
  {
    final long address = node.getAddress();
    final String addressStr;
    if ((address >= 0) && addressInfoEnabled) {
      addressStr = addressRepresentation.getDisplayAddress(address) + ": ";
    } else {
      // address not yet evaluated or not enabled
      addressStr = "";
    }
    String label = node.getLabel();
    String displayValue;
    if (node instanceof DataNode) {
      displayValue = ((DataNode)node).getDisplayValue();
      if (displayValue == null) {
        displayValue = ValueRangeRenderer.DISPLAY_VALUE_UNKNOWN;
      }
    } else {
      displayValue = "";
    }
    if (label == null) {
      label = "";
    }
    final String separator =
      ((label.length() > 0) && (displayValue.length() > 0)) ? ": " : "";
    final String text = addressStr + label + separator + displayValue;
    return text;
  }

  /**
   * Creates the icon for a given tree cell node.
   * @param node The node.
   * @param expanded True, if the node is expanded.
   * @return A proper Icon object for the tree cell.
   */
  private Icon getTreeCellIcon(final MapNode node, final boolean expanded)
  {
    final Icon icon;
    if (node instanceof DataNode)
      icon = ((DataNode)node).getIcon();
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
  private class CellRenderer extends DefaultTreeCellRenderer
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

    /*
     * This method is called from JTree whenever it needs to get the
     * size of the component or wants to draw the tree.  It attempts
     * to set the font based on value, which will be a TreeNode.
     */
    public Component
      getTreeCellRendererComponent(final JTree tree, final Object value,
                                   final boolean selected,
                                   final boolean expanded,
                                   final boolean leaf, final int row,
                                   final boolean hasFocus)
    {
      final Map map = (Map)tree;
      String text = null;
      Icon icon = null;
      if ((value != null) && (value instanceof MapNode))
        {
          final MapNode node = (MapNode)value;
          text = map.getTreeCellText(node);
          icon = map.getTreeCellIcon(node, expanded);
        }
      if (text == null)
        text = tree.convertValueToText(value, selected, expanded,
                                       leaf, row, hasFocus);
      if (icon == null)
        icon = UIManager.getDefaults().getIcon(SparseType.GENERIC_ICON_KEY);

      setIcon(icon);
      setText(text);
      // setFont(defaultFont);
      this.selected = selected;
      return this;
    }
  }

  private class MapCellEditor extends AbstractCellEditor
    implements TreeCellEditor
  {
    private static final long serialVersionUID = -1479756067528395593L;

    /**
     * Unfortunetely, class AbstractCellEditor is not prepared to
     * handle different cell editors for different cells.  Therefore,
     * we have to remember the cell editor that is currently in use
     * (assuming that there is always at most one active cell editor
     * in a tree).
     */
    private Component lastRequestedEditor;

    public Object getCellEditorValue()
    {
      final Editor editor = (Editor)lastRequestedEditor;
      return editor.getSelectedValue();
    }

    public boolean isCellEditable(final EventObject event)
    {
      if (event.getSource() != Map.this) {
        throw new IllegalStateException("unexpected event source: " +
                                        event.getSource().getClass());
      }
      if (!(event instanceof MouseEvent)) {
        throw new UnsupportedOperationException("unexpected event type: " +
                                                event.getClass());
      }
      final MouseEvent mouseEvent = (MouseEvent)event;
      final int x = mouseEvent.getX();
      final int y = mouseEvent.getY();
      final TreePath path = Map.this.getClosestPathForLocation(x, y);
      final Object node = path.getLastPathComponent();
      return node instanceof DataNode;
    }

    public Component getTreeCellEditorComponent(final JTree tree,
                                                final Object value,
                                                final boolean isSelected,
                                                final boolean expanded,
                                                final boolean leaf,
                                                final int row)
    {
      if (!(value instanceof MapNode)) {
        throw new RuntimeException("tree value is not a map node");
      }
      final DataNode node = (DataNode)value;
      final Component editor = (Component)(node.getEditor());
      lastRequestedEditor = editor;
      return editor;
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
