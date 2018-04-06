/*
 * @(#)Preferences.java 1.00 18/03/26
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

package see;

import java.util.prefs.BackingStoreException;

public class Preferences
{
  private final static String PATH_MIDI_INPUT_CONNECTION =
    "midi/inputConnection";
  private final static String PATH_MIDI_OUTPUT_CONNECTION =
    "midi/outputConnection";
  private final static String PATH_APPEARANCE_SHOW_TOOL_TIPS =
    "appearance/showToolTips";

  private static Preferences defaultPreferences;

  private java.util.prefs.Preferences prefs;

  public static synchronized Preferences getDefault()
  {
    if (defaultPreferences == null) {
      defaultPreferences = new Preferences();
    }
    return defaultPreferences;
  }

  private Preferences()
  {
    prefs = java.util.prefs.Preferences.userNodeForPackage(getClass());
  }

  public String getMidiInputConnection()
  {
    return prefs.get(PATH_MIDI_INPUT_CONNECTION, "");
  }

  public void setMidiInputConnection(final String midiInputConnection)
  {
    prefs.put(PATH_MIDI_INPUT_CONNECTION, midiInputConnection);
  }

  public String getMidiOutputConnection()
  {
    return prefs.get(PATH_MIDI_OUTPUT_CONNECTION, "");
  }

  public void setMidiOutputConnection(final String midiOutputConnection)
  {
    prefs.put(PATH_MIDI_OUTPUT_CONNECTION, midiOutputConnection);
  }

  public boolean getAppearanceShowToolTips()
  {
    return prefs.getBoolean(PATH_APPEARANCE_SHOW_TOOL_TIPS, true);
  }

  public void setAppearanceShowToolTips(final boolean showToolTips)
  {
    prefs.putBoolean(PATH_APPEARANCE_SHOW_TOOL_TIPS, showToolTips);
  }

  public void flush() throws BackingStoreException
  {
    prefs.flush();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
