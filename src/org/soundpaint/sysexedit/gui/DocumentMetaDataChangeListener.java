/*
 * @(#)DocumentMetaDataChangeListener.java 1.00 18/03/26
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

import org.soundpaint.sysexedit.gui.JValue;

/**
 * Interface for listening to changes in document meta data.
 */
public interface DocumentMetaDataChangeListener
{
  /**
   * This method is called whenever the state of having unsaved data
   * changes.
   */
  void hasUnsavedDataChanged(final boolean hasUnsavedData);

  /**
   * This method is called whenever the MIDI device changes.
   */
  void modelInfoChanged(final String deviceName,
                        final int manId,
                        final int modelId);
  /**
   * This method is called whenever the MIDI device ID changes.
   */
  void midiDeviceIdChanged(final JValue mididDeviceId);

}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
