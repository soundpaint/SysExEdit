/*
 * @(#)FramesManager.java 1.00 98/02/06
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

package see.gui;

import java.awt.Frame;

/**
 * A class that implements this interface manages a set of bundled frames.
 */
public interface FramesManager
{
  /**
   * Returns the version id of this application.
   * @return The version id of this application.
   */
  String getVersion();

  /**
   * Returns the copyright mark of this application.
   * @return The copyright mark of this application.
   */
  String getCopyright();

  /**
   * Sets the Swing Look and Feel for all frames.
   * @param name The name of the Look and Feel flavour.
   */
  void setLookAndFeel(final String name) throws Exception;

  /**
   * Starts an EditorFrame thread with the specified device model.
   * @param filepath The device model filepath to use in this thread.
   *    If null, the user will be prompted to select a device model from
   *    a list.
   */
  void createEditorFrame(final String filepath);

  /**
   * Registers a new frame. Whenever a frame is registered, it is assigned
   * an ID that is unique among all frames that are currently registered.
   * This ID is always a non-negative number; ideally, it is the smallest
   * non-negative number that is not already in use.
   * @param frame The frame to be registered.
   * @return The unique ID.
   * @see #removeFrame
   * @see #getID
   */
  int addFrame(final Frame frame);

  /**
   * Removes a frame from the set of registered frames.
   * When the last frame has been removed, the application may assume
   * that it can safely do a System.exit().
   * @param frame The frame to be removed.
   * @see #addFrame
   */
  void removeFrame(final Frame frame);

  /**
   * Gets the unique ID of the specified frame.
   * @param frame The frame.
   * @return The unique ID or -1, if the frame is not registered.
   * @see #addFrame
   */
  int getID(final Frame frame);

  /**
   * Closes all frames and exits the application.
   */
  void exitAll();
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
