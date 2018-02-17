/*
 * @(#)HtmlPanel.java 1.00 98/02/06
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

package see.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

/**
 * This class implements a simple browser-like JPanel that displays HTML.
 */
public class HtmlPanel
  extends JPanel implements HyperlinkListener, ActionListener
{
  private final static String ACTION_PREV = "<";
  private final static String ACTION_NEXT = ">";
  private final static String ACTION_STOP = "Stop";

  private JEditorPane html_pane;
  private JLabel title;
  private JButton bt_prev, bt_next, bt_stop;
  private History history;

  /**
   * Creates a panel that displays the content referenced by the specified
   * URL.
   * @exception IOException If the URL can not be resolved.
   */
  public HtmlPanel(URL url) throws IOException
  {
    setBorder(new BevelBorder(BevelBorder.LOWERED, Color.white, Color.gray));
    setLayout(new BorderLayout());
    getAccessibleContext().setAccessibleName("HTML panel");
    getAccessibleContext().setAccessibleDescription
      ("A panel for viewing HTML documents, and following their links");
    JScrollPane scroller = new JScrollPane();
    scroller.setPreferredSize(new Dimension(700, 420));
    scroller.setBorder(new BevelBorder(BevelBorder.LOWERED,
				       Color.white, Color.gray));
    html_pane = new JEditorPane(url);
    html_pane.setEditable(false);
    html_pane.addHyperlinkListener(this);
    history = new History(url, html_pane.getDocument());
    JViewport vp = scroller.getViewport();
    vp.add(html_pane);
    vp.setBackingStoreEnabled(true);
    add(scroller, BorderLayout.CENTER);
    JPanel panel_header = new JPanel();
    panel_header.setLayout(new FlowLayout());
    add(panel_header, BorderLayout.NORTH);
    title = new JLabel();
    title.setToolTipText("Document Title");
    updateTitle();
    panel_header.add(title);
    JPanel panel_buttons = new JPanel();
    panel_buttons.setLayout(new FlowLayout());
    add(panel_buttons, BorderLayout.SOUTH);
    bt_prev = new JButton(ACTION_PREV);
    bt_prev.setToolTipText("Go to previous page");
    bt_prev.setEnabled(false);
    bt_prev.addActionListener(this);
    panel_buttons.add(bt_prev);
    bt_next = new JButton(ACTION_NEXT);
    bt_next.setToolTipText("Go to next page");
    bt_next.setEnabled(false);
    bt_next.addActionListener(this);
    panel_buttons.add(bt_next);
    bt_stop = new JButton(ACTION_STOP);
    bt_stop.setToolTipText("Stop loading");
    bt_stop.setEnabled(false);
    bt_stop.addActionListener(this);
    panel_buttons.add(bt_stop);
  }

  /**
   * Returns the title of the given document.
   */
  private void updateTitle()
  {
    String titlestr =
      (String)html_pane.getDocument().getProperty(Document.TitleProperty);
    title.setText((titlestr != null) ? titlestr : "<untitled>");
    title.updateUI();
  }

  /**
   * Initiates loading a new page.
   */
  private void updatePage()
  {
    bt_next.setEnabled(history.hasNext());
    bt_prev.setEnabled(history.hasPrev());
    SwingUtilities.invokeLater(new PageLoader());
  }

  /**
   * Handles buttons "<", ">", "Stop"
   */
  public void actionPerformed(ActionEvent e)
  {
    if ((e.getActionCommand().equals(ACTION_PREV)) && history.hasPrev())
      {
	history.prev();
	updatePage();
      }
    else if ((e.getActionCommand().equals(ACTION_NEXT)) &&
	     history.hasNext())
      {
	history.next();
	updatePage();
      }
    else if (e.getActionCommand().equals(ACTION_STOP))
      {
	// not implemented yet
      }
  }

  /**
   * Notification of a change relative to a hyperlink.
   */
  public void hyperlinkUpdate(HyperlinkEvent e)
  {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
      {
	history.add(e.getURL(), null);
	updatePage();
      }
  }

  /**
   * A temporary class that loads synchronously (although later than
   * the request so that a cursor change can be done).
   */
  private class PageLoader implements Runnable
  {
    private Cursor original_cursor;
    private boolean restore_cursor_pending;

    PageLoader()
    {
      original_cursor = html_pane.getCursor();
      html_pane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      restore_cursor_pending = false;
    }

    public void run()
    {
      if (restore_cursor_pending)
	{
	  updateTitle();

	  // restore the original cursor
	  html_pane.setCursor(original_cursor);

	  // [PENDING: remove this when automatic validation is activated.]
	  Container parent = html_pane.getParent();
	  parent.repaint();

	  restore_cursor_pending = false;
	  bt_stop.setEnabled(false);
	}
      else
	{
	  try
	    {
	      Document document = (Document)history.getContent();
	      if (document != null)
		html_pane.setDocument(document);
	      else
		{
		  html_pane.setPage((URL)history.getReference());
		  document = html_pane.getDocument();
		  history.setContent(document);
		}
	    }
	  catch (IOException e)
	    {
	      html_pane.setDocument(HtmlFactory.errorPage("I/O Error",
							  e.toString()));
	    }
	  finally
	    {
	      // schedule the cursor to revert after
	      // the paint has happened.
	      restore_cursor_pending = true;
	      bt_stop.setEnabled(true);
	      SwingUtilities.invokeLater(this);
	    }
	}
    }
  }
}
