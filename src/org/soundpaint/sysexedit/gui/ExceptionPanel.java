/*
 * @(#)ExceptionPanel.java 1.00 98/02/06
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

package org.soundpaint.sysexedit.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;

/**
 * A Panel that is used to display a java.lang.Exception object, optionally
 * including its full stack trace.
 */
public class ExceptionPanel extends JPanel
{
  private static final long serialVersionUID = -8568553156220565622L;

  private static final String EXCEPTION = "Exception Info Message";

  private static class StringOutputStream extends OutputStream
  {
    private final StringBuffer buffer;

    public StringOutputStream()
    {
      buffer = new StringBuffer();
    }

    public void write(final int b)
    {
      buffer.append((char)b);
    }

    public String toString()
    {
      return buffer.toString();
    }
  }

  private ExceptionPanel() {}

  private ExceptionPanel(final Throwable t, final boolean printStackTrace)
  {
    final StringOutputStream out = new StringOutputStream();
    final PrintWriter printer = new PrintWriter(out);
    final String message;
    if (printStackTrace) {
      t.printStackTrace(printer);
      printer.close();
      message = out.toString();
    } else {
      message = t.getMessage();
    }
    setBorder(new BevelBorder(BevelBorder.LOWERED, Color.white, Color.gray));
    setLayout(new BorderLayout());
    getAccessibleContext().setAccessibleName("Exception panel");
    getAccessibleContext().setAccessibleDescription
      ("A panel for viewing exception messages");
    final JScrollPane scroller = new JScrollPane();
    scroller.setPreferredSize(new Dimension(700, 420));
    scroller.setBorder(new BevelBorder(BevelBorder.LOWERED,
                                       Color.white, Color.gray));
    final JTextArea textArea = new JTextArea(message);
    final JViewport vp = scroller.getViewport();
    vp.add(textArea);
    vp.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
    add(scroller, BorderLayout.CENTER);
    //add(new JEditorPane("text/html", "<html><body><pre>" + out +
    //                    "</pre></body></html>"));
  }

  /**
   * Shows an Exception with (optionally) its full stack trace in a
   * modal dialog.
   * @param parent The parent frame.
   * @param t The exception to show.
   * @param printStackTrace If true, also show the stack trace.
   */
  public static void showException(final Frame parent, final Throwable t,
                                   final boolean printStackTrace)
  {
    JOptionPane.showMessageDialog(parent,
                                  new ExceptionPanel(t, printStackTrace),
                                  EXCEPTION,
                                  JOptionPane.ERROR_MESSAGE);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
