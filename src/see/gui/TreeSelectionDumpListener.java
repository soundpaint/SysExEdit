/*
 * @(#)EditorFrame.java 1.00 98/02/06
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

package see.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import see.model.MapDef;
import see.model.MapNode;

public class TreeSelectionDumpListener extends KeyAdapter
{
  private final MapDef mapDef;

  private final Map map;

  /**
   * The first adress of the current contiguous bulk area.
   */
  private long bulkAreaStartAddress;

  /**
   * The address following the last address of the current contiguous
   * bulk area.
   */
  private long bulkAreaStopBeforeAddress;

  private TreeSelectionDumpListener()
  {
    throw new UnsupportedOperationException();
  }

  public TreeSelectionDumpListener(final MapDef mapDef, final Map map)
  {
    this.mapDef = mapDef;
    this.map = map;
    reset();
  }

  /**
   * Initializes the save automaton.
   */
  private void reset()
  {
    bulkAreaStartAddress = -1;
    bulkAreaStopBeforeAddress = -1;
  }

  /**
   * Flushes the save automaton.
   */
  private void flushDump(final OutputStream out) throws IOException
  {
    if (bulkAreaStartAddress < 0)
      return; // nothing to flush
    else
      {
        System.out.println("dump " +
                           bulkAreaStartAddress + "-" +
                           bulkAreaStopBeforeAddress);
        final InputStream bulkDump =
          mapDef.bulkDump(bulkAreaStartAddress,
                          bulkAreaStopBeforeAddress);
        int data;
        while ((data = bulkDump.read()) >= 0)
          {
            System.out.println("data=" + data);
            out.write(data);
          }
        reset();
      }
  }

  /**
   * Notes down that the specified MapNode is to be saved; eventually
   * saves a bulk of collected MapNode objects.
   * @param node The MapNode to be saved eventually.
   */
  private void addToDump(final OutputStream out,
                         final MapNode node) throws IOException
  {
    if (node.getAllowsChildren()) {
      // non-leaf node => add children to dump
      for (int i = 0; i < node.getChildCount(); i++) {
        final TreeNode child = node.getChildAt(i);
        addToDump(out, (MapNode)child);
      }
      return;
    }
    if (node.getAddress() == bulkAreaStopBeforeAddress) {
      // append to contiguous block & quit
      bulkAreaStopBeforeAddress += node.getContents().getBitSize();
      return;
    }
    if (bulkAreaStopBeforeAddress >= 0) {
      // end of contiguous block; dump it
      flushDump(out);
    }
    // start a new contiguous block
    bulkAreaStartAddress = node.getAddress();
    bulkAreaStopBeforeAddress =
      bulkAreaStartAddress + node.getContents().getBitSize();
  }

  /**
   * Walks through the whole Map object and schedules the contents of
   * each selected node for bulk dump.
   */
  private void dumpSelection()
  {
    try {
      final FileOutputStream out = new FileOutputStream("bulkdump.mid");
      reset();
      int index = map.getMinSelectionRow();
      if (index >= 0) {
        while (index <= map.getMaxSelectionRow()) {
          if (map.isRowSelected(index)) {
            final MapNode node =
              (MapNode)map.getPathForRow(index).getLastPathComponent();
            addToDump(out, node);
          }
          index++;
        }
        // final flush
        flushDump(out);
      }
    } catch (final IOException e) {
      new MessageFrame(e.toString());
    }
  }

  public void keyTyped(final KeyEvent e)
  {
    final char key = e.getKeyChar();
    switch (key) {
    case '\u0013': // Ctrl-S
      dumpSelection();
      break;
    }
  }
}
