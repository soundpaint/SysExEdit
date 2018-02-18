/*
 * @(#)Util.java 1.00 98/02/06
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

package see.midi;

import java.util.Vector;
import javax.media.sound.AudioManager;
import javax.media.sound.midi.MidiOutControlDevice;
import javax.media.transport.ControlDevice;

public class Util
{
  public static void main(String argv[])
  {
    Vector devices = new Vector();
    ControlDevice[] allDevices = AudioManager.listControlDevices();
    for (int i = 0; i < allDevices.length; i++)
      if (allDevices[i] instanceof MidiOutControlDevice)
	devices.addElement(allDevices[i]);
    MidiOutControlDevice[] midiOut = new MidiOutControlDevice[devices.size()];
    devices.copyInto(midiOut);
    System.out.println("Midi Out Devices:");
    for (int i = 0; i < midiOut.length; i++)
      System.out.println(midiOut[i].getName());
    System.out.flush();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
