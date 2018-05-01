/*
 * @(#)Dialog.java 1.00 18/04/13
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

package org.soundpaint.sysexedit.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class Dialog extends JDialog
{
  private static final long serialVersionUID = -5885226391722528867L;

  private Dialog()
  {
    throw new UnsupportedOperationException();
  }

  public Dialog(final Frame owner, final String title, final boolean modal)
  {
    super(owner, title, modal);
  }

  private class CloseAction extends AbstractAction
  {
    private static final long serialVersionUID = -8108757189043311813L;

    public void actionPerformed(final ActionEvent actionEvent)
    {
      setVisible(false);
    }
  }

  protected JRootPane createRootPane()
  {
    final JRootPane rootPane = new JRootPane();
    final KeyStroke escStroke = KeyStroke.getKeyStroke('\u001B');
    final Action closeAction = new CloseAction();
    final InputMap inputMap =
      rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(escStroke, this);
    rootPane.getActionMap().put(this, closeAction);
    return rootPane;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
