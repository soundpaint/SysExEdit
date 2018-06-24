/*
 * @(#)MidiFileTransmitter.java 1.00 18/04/02
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class MidiFileTransmitter implements Transmitter
{
  private final Receiver receiver;

  private MidiFileTransmitter()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public MidiFileTransmitter(final File midiFile) throws IOException
  {
    if (midiFile == null) {
      throw new NullPointerException("midiFile");
    }
    this.receiver = new MidiFileReceiver(midiFile);
  }

  // TODO: This method implementation does not meet the behaviour of
  // implicit / explicit close as described in the JavaDoc
  // specification of the MidiDevice class.  However, since we use
  // this class only internally, this flaw currently does not hurt.
  public void close()
  {
    receiver.close();
  }

  public Receiver getReceiver()
  {
    return receiver;
  }

  public void setReceiver(final Receiver receiver)
  {
    throw new UnsupportedOperationException("method setReceiver() not implemented for MidiFileTransmitter");
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
