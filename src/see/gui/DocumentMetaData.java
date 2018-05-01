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
import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.MidiDevice;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import see.model.Contents;
import see.model.MapDef;

public class DocumentMetaData implements TreeSelectionListener
{
  public static final MidiDevice.Info dumpMidiFileDeviceInfo =
    new MidiDevice.Info("Dump to MIDI File", "SysExEdit",
                        "Dumps MIDI data to a configurable MIDI file", "1.0")
    {
    };

  private final List<DocumentMetaDataChangeListener> metaDataChangeListeners;
  private final List<MapSelectionChangeListener> selectionChangeListeners;
  private boolean hasUnsavedData;
  private int selectionCount;
  private MapDef mapDef;
  private Contents midiDeviceId;
  private MidiDevice.Info midiInput;
  private MidiDevice.Info midiOutput;
  private File dumpMidiFile;
  private SelectionMultiplicity lastSelectionMultiplicity;

  public DocumentMetaData(final MapDef mapDef)
  {
    setDevice(mapDef);
    metaDataChangeListeners = new ArrayList<DocumentMetaDataChangeListener>();
    selectionChangeListeners = new ArrayList<MapSelectionChangeListener>();
    hasUnsavedData = false;
    selectionCount = 0;
    lastSelectionMultiplicity = SelectionMultiplicity.NONE;
  }

  public void setDevice(final MapDef mapDef)
  {
    this.mapDef = mapDef;
    // FIXME: Currently, mapDef may be initially null,
    // since model selection will follow later.  This
    // should be changed such that model selection
    // occurs before instantiating the editor frame.
    if (mapDef != null) {
      setMidiDeviceId(mapDef.createDeviceIdContents());
    }
  }

  public MapDef getDevice()
  {
    return mapDef;
  }

  public void addMetaDataChangeListener(final DocumentMetaDataChangeListener listener)
  {
    metaDataChangeListeners.add(listener);
  }

  public void removeMetaDataChangeListener(final DocumentMetaDataChangeListener listener)
  {
    metaDataChangeListeners.remove(listener);
  }

  public void addSelectionChangeListener(final MapSelectionChangeListener listener)
  {
    selectionChangeListeners.add(listener);
  }

  public void removeSelectionChangeListener(final MapSelectionChangeListener listener)
  {
    selectionChangeListeners.remove(listener);
  }

  public void setHasUnsavedData(final boolean hasUnsavedData)
  {
    this.hasUnsavedData = hasUnsavedData;
    for (final DocumentMetaDataChangeListener listener :
           metaDataChangeListeners) {
      listener.hasUnsavedDataChanged(hasUnsavedData);
    }
  }

  public boolean getHasUnsavedData()
  {
    return hasUnsavedData;
  }

  public void setMidiDeviceId(final Contents midiDeviceId)
  {
    this.midiDeviceId = midiDeviceId;
    for (final DocumentMetaDataChangeListener listener :
           metaDataChangeListeners) {
      listener.midiDeviceIdChanged(midiDeviceId);
    }
  }

  public Contents getMidiDeviceId()
  {
    return midiDeviceId;
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

  private void selectionChanged(final SelectionMultiplicity multiplicity)
  {
    for (final MapSelectionChangeListener listener : selectionChangeListeners) {
      listener.selectionChanged(multiplicity);
    }
    switch (multiplicity) {
    case NONE:
      if (lastSelectionMultiplicity != SelectionMultiplicity.NONE) {
        anythingSelectedChanged(false);
      }
      if (lastSelectionMultiplicity == SelectionMultiplicity.SINGLE_LEAF) {
        singleLeafSelectedChanged(false);
      }
      break;
    case SINGLE_LEAF:
      if (lastSelectionMultiplicity == SelectionMultiplicity.NONE) {
        anythingSelectedChanged(true);
      }
      if (lastSelectionMultiplicity != SelectionMultiplicity.SINGLE_LEAF) {
        singleLeafSelectedChanged(true);
      }
      break;
    case SINGLE_PARENT:
    case MULTIPLE:
      if (lastSelectionMultiplicity == SelectionMultiplicity.NONE) {
        anythingSelectedChanged(true);
      }
      if (lastSelectionMultiplicity == SelectionMultiplicity.SINGLE_LEAF) {
        singleLeafSelectedChanged(false);
      }
      break;
    default:
      throw new IllegalArgumentException("unexpected multiplicity value: " +
                                         multiplicity);
    }
    lastSelectionMultiplicity = multiplicity;
  }

  private void singleLeafSelectedChanged(final boolean haveSingleLeafSelected)
  {
    for (final MapSelectionChangeListener listener : selectionChangeListeners) {
      listener.singleLeafSelectedChanged(haveSingleLeafSelected);
    }
  }

  private void anythingSelectedChanged(final boolean haveAnythingSelected)
  {
    for (final MapSelectionChangeListener listener : selectionChangeListeners) {
      listener.anythingSelectedChanged(haveAnythingSelected);
    }
  }

  public void valueChanged(final TreeSelectionEvent event)
  {
    for (final TreePath path : event.getPaths()) {
      if (event.isAddedPath(path)) {
        selectionCount++;
      } else {
        selectionCount--;
      }
    }
    if (selectionCount == 0) {
      selectionChanged(SelectionMultiplicity.NONE);
    } else if (selectionCount == 1) {
      final TreePath path = event.getPath();
      final TreeNode node = (TreeNode)path.getLastPathComponent();
      if (!node.getAllowsChildren()) {
        selectionChanged(SelectionMultiplicity.SINGLE_LEAF);
      } else {
        selectionChanged(SelectionMultiplicity.SINGLE_PARENT);
      }
    } else /* (selectionCount > 1) */ {
      selectionChanged(SelectionMultiplicity.MULTIPLE);
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
