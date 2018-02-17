/*
 * @(#)DialogDeviceID.java 1.00 98/02/06
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

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class implements a dialog that prompts the user for a new
 * Device ID.
 */
class DialogDevID
{
  private static Hashtable labels;

  static
  {
    labels = new Hashtable();
    for (int i = 0; i < 128; i+=0x20)
      labels.put(new Integer(i), new JLabel(Utils.intTo0xnn(i)));
    labels.put(new Integer(0x7f), new JLabel(Utils.intTo0xnn(0x7f)));
  }

  private DialogDevID() {}

  /**
   * A listener that updates a label according to a slider's state.
   */
  private static class MyChangeListener implements ChangeListener
  {
    JLabel label_deviceID;
    JSlider slider;
    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e A ChangeEvent object
     */
    public void stateChanged(ChangeEvent e)
    {
      int deviceID = slider.getValue();
      label_deviceID.setText(Utils.intTo0xnn(deviceID));
      label_deviceID.updateUI();
    }
  }

  /**
   * A panel with all gui elements of the dialog.
   */
  private static class Panel extends JPanel
  {
    MyChangeListener myChangeListener;
    Panel(Object message, int deviceID)
    {
      myChangeListener = new MyChangeListener();
      Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

      BorderLayout layout = new BorderLayout();
      layout.setHgap(10);
      layout.setVgap(10);
      setLayout(layout);
      setBorder(emptyBorder);
      // BasicOptionPaneUI splits up Strings that contain '\n'. We must
      // do it here manually, i.e. create a sequence of labels
      JPanel panel_label = new JPanel(); // keep lines close together
      GridLayout layout_panel_label = new GridLayout(0, 1);
      layout_panel_label.setHgap(0);
      layout_panel_label.setVgap(0);
      panel_label.setLayout(layout_panel_label);
      JLabel label_text;
      label_text = new JLabel("Select a value (0x00..0x7f) to be used as");
      label_text.setForeground(Color.black);
      panel_label.add(label_text);
      label_text = new JLabel("Device ID for SysEx messages.");
      label_text.setForeground(Color.black);
      panel_label.add(label_text);
      add(panel_label, "North");

      JPanel panel_slider = new JPanel();
      panel_slider.setLayout(new BorderLayout());
      panel_slider.setBorder(emptyBorder);
      panel_slider.setToolTipText("Adjust a new value for the Device ID");
      //panel_slider.add("West", new JLabel("0x00"));
      //panel_slider.add("East", new JLabel("0x7f"));
      JLabel label_deviceID = new JLabel((String)null, SwingConstants.CENTER);
      panel_slider.add("North", label_deviceID);
      JSlider slider = new JSlider(0, 127, deviceID);
      slider.addChangeListener(myChangeListener);
      slider.setOrientation(SwingConstants.HORIZONTAL);
      slider.setLabelTable(labels);
      slider.setPaintLabels(true);
      slider.setPaintTicks(true);
      slider.setMajorTickSpacing(0x10);
      myChangeListener.label_deviceID = label_deviceID;
      myChangeListener.slider = slider;
      myChangeListener.stateChanged(null); // update label
      panel_slider.add("Center", slider);

      JPanel panel_panel_slider = new JPanel();
      panel_panel_slider.setLayout(new BorderLayout());
      panel_panel_slider.setBorder(BorderFactory.createEtchedBorder());
      panel_panel_slider.add(panel_slider, "Center");

      JPanel panel_panel_panel_slider = new JPanel();
      panel_panel_panel_slider.setLayout(new BorderLayout());
      panel_panel_panel_slider.add(panel_panel_slider, "North");

      add(panel_panel_panel_slider, "Center");
    }
  }

  /**
   * Prompts the user for device ID input in a blocking dialog where the
   * initial selection and other options can be specified.
   * <code>initialValue</code> is the initial value to prompt the user with.
   *
   * @param parentComponent The parent Component for the dialog
   * @param message The Object to display (currently ignored).
   * @param title The String to display in the dialog title bar
   * @param messageType The type of message to be displayed.
   * @param icon The Icon image to display.
   * @param initialValue The value used to initialize the input field.
   * @return device ID, or -1 meaning the user canceled the input
   */
  static int showDialog(Component parentComponent, Object message,
			String title, int messageType,
			Icon icon, int initialValue)
  {
    Panel panel = new Panel(message, initialValue);
    JOptionPane pane =
      new JOptionPane(panel, messageType,
		      JOptionPane.OK_CANCEL_OPTION, icon, null, null);
    pane.setWantsInput(false);
    JDialog dialog = pane.createDialog(parentComponent, title);
    dialog.show();
    Object value = pane.getValue();
    if (value instanceof Integer)
      if (((Integer)value).intValue() == JOptionPane.OK_OPTION)
	return panel.myChangeListener.slider.getValue();
    return -1;
  }
}
