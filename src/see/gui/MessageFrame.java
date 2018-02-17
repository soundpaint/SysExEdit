/*
 * @(#)MessageFrame.java 1.00 98/02/06
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 * This class implements a message window.
 */
class MessageFrame extends JFrame
{
  private MessageFrame() {}

  /**
   * Creates a new MessageFrame with the specified message.
   * @param msg The message to be displayed.
   */
  MessageFrame(String msg)
  {
    super("Message");
    
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    getContentPane().add("Center", panel);
    setBackground(Color.lightGray);

    JTextField textField = new JTextField(msg);
    textField.setEditable(false);
    panel.add("Center", textField);

    JButton button = new JButton("Ok");
    button.addActionListener(new Listener());
    panel.add("South", button);

    pack();
    show();
  }

  private class Listener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      setVisible(false);
    }
  }
}
