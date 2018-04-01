/*
 * @(#)DocumentMetaData.java 1.00 18/03/28
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

package see.gui;

import java.io.File;
import javax.sound.midi.MidiDevice;

public class DocumentMetaData
{
  private boolean haveUnsavedData;
  private MidiDevice.Info midiInput;
  private MidiDevice.Info midiOutput;
  private File dumpMidiFile;

  public DocumentMetaData()
  {
    haveUnsavedData = false;
  }

  public void setHaveUnsavedData(final boolean haveUnsavedData)
  {
    this.haveUnsavedData = haveUnsavedData;
  }

  public boolean getHaveUnsavedData()
  {
    return haveUnsavedData;
  }

  public MidiDevice.Info getMidiInput()
  {
    return midiInput;
  }

  public void setMidiInput(final MidiDevice.Info midiInput)
  {
    this.midiInput = midiInput;
  }

  public MidiDevice.Info getMidiOutput()
  {
    return midiOutput;
  }

  public void setMidiOutput(final MidiDevice.Info midiOutput)
  {
    this.midiOutput = midiOutput;
  }

  public File getDumpMidiFile()
  {
    return dumpMidiFile;
  }

  public void setDumpMidiFile(final File dumpMidiFile)
  {
    this.dumpMidiFile = dumpMidiFile;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
