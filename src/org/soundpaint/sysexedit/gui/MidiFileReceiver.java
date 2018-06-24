/*
 * @(#)MidiFileReceiver.java 1.00 18/04/02
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
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public class MidiFileReceiver implements Receiver
{
  private static final int MIDI_FILE_TYPE_0 = 1;

  private final FileOutputStream out;
  private final Sequence sequence;
  private final Track track;
  private boolean open;

  private MidiFileReceiver()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public MidiFileReceiver(final File midiFile) throws IOException
  {
    if (midiFile == null) {
      throw new NullPointerException("midiFile");
    }
    out = new FileOutputStream(midiFile);
    try {
      sequence = new Sequence(Sequence.SMPTE_30, 1, 1);
    } catch (final InvalidMidiDataException e) {
      throw new IOException("failed creating MIDI sequence: " + e.getMessage(),
                            e);
    }
    track = sequence.createTrack();
    open = true;
  }

  // TODO: This method implementation does not meet the behaviour of
  // implicit / explicit close as described in the JavaDoc
  // specification of the MidiDevice class.  However, since we use
  // this class only internally, this flaw currently does not hurt.
  public void close()
  {
    if (!open) {
      throw new UnsupportedOperationException("transmitter already closed");
    }
    try {
      MidiSystem.write(sequence, MIDI_FILE_TYPE_0, out);
      out.close();
    } catch (final IOException e) {
      // TODO: Remember error somewhere for delayed display to the
      // user.  For now, we ignore the exception.
      throw new RuntimeException("write failed: " + e.getMessage(), e);
    }
    sequence.deleteTrack(track);
  }

  public void send(final MidiMessage message, final long timeStamp)
  {
    if (!open) {
      throw new UnsupportedOperationException("transmitter already closed");
    }
    final MidiEvent event = new MidiEvent(message, timeStamp);
    track.add(event);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
