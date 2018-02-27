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

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

public class Util
{
  public static void main(final String argv[]) throws MidiUnavailableException
  {
    final Transmitter transmitter = MidiSystem.getTransmitter();
    if (transmitter instanceof MidiDeviceTransmitter) {
      final MidiDeviceTransmitter deviceTransmitter =
        (MidiDeviceTransmitter)transmitter;
      final MidiDevice device = deviceTransmitter.getMidiDevice();
      final MidiDevice.Info info = device.getDeviceInfo();
      System.out.println("Midi Device:");
      System.out.println("Name: " + info.getName());
      System.out.println("Description: " + info.getDescription());
      System.out.println("Vendor: " + info.getVendor());
      System.out.println("Version: " + info.getVersion());
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
