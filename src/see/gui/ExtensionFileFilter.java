/*
 * @(#)ExtensionFileFilter.java 1.00 98/02/06
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

import java.io.File;
import java.util.HashSet;
import java.util.Enumeration;
import javax.swing.filechooser.FileFilter;

/**
 * A convenience implementation of FileFilter that filters out all
 * files except for those type extensions that it knows about.
 *
 * Extensions are of the type <code>".extension"</code>, which is
 * typically found on Windows and Unix boxes, but not on Macinthosh.
 * Case is ignored.
 *
 * Example: Create a new filter that filters out all files but MIDI
 * SysEx files:
 *
 * <code>
 *   JFileChooser chooser = new JFileChooser();
 *   ExtensionFileFilter filter =
 *     new ExtensionFileFilter(new String[] {"mid", "midi", "sysex"},
 *                             "MIDI SysEx files");
 *   chooser.addChoosableFileFilter(filter);
 *   chooser.showOpenDialog(this);
 * </code>
 */
public class ExtensionFileFilter extends FileFilter
{
  private final HashSet<String> extensions;
  private final String description;
  private final String fullDescription;

  private ExtensionFileFilter()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a file filter that accepts the file type with the given
   * case-insensitive extension (without the leading
   * <code>"."</code>).  Example:
   * <code>
   *   new ExtensionFileFilter({"mid", "midi", "sys"}, "SysEx MIDI files");
   * </code>
   * @param extensions An array of strings that represents the
   * extensions.
   * @param description A description of the extensions for display.
   */
  public ExtensionFileFilter(final String[] extensions,
                             final String description)
  {
    if (extensions == null)
      {
        throw new NullPointerException("extensions");
      }
    this.extensions = new HashSet<String>(extensions.length);
    for (final String extension : extensions)
      {
        this.extensions.add(extension.toLowerCase());
      }
    this.description = description;
    this.fullDescription = buildFullDescription(extensions, description);
  }

  /**
   * Return <code>true</code> if this file should be shown in the
   * directory pane, or <code>false</code> if it should not.
   *
   * Files that begin with <code>"."</code> are ignored.
   *
   * @see #getExtension
   * @see FileFilter#accept
   */
  public boolean accept(final File file)
  {
    if (file == null)
      return false;
    if (file.isDirectory())
      return true;
    final String extension = getExtension(file);
    if (extension == null)
      return false;
    return extensions.contains(extension.toLowerCase());
  }

  public String getDescription()
  {
    return fullDescription;
  }

  /**
   * Return the extension portion of the file's name.
   *
   * @see FileFilter#accept
   */
  private String getExtension(final File file)
  {
    if (file == null)
      return null;
    final String filename = file.getName();
    final int dotPos = filename.lastIndexOf('.');
    if (dotPos <= 0 || dotPos >= filename.length() - 1)
      return null;
    return filename.substring(dotPos + 1);
  }

  /**
   * Builds the human readable description of this filter, for
   * example: <code>"MIDI Files (*.mid, *.midi)"</code>.
   *
   * @see FileFilter#getDescription
   */
  private String buildFullDescription(final String[] extensions,
                                      final String description)
  {
    final StringBuilder fullDescriptionBuilder = new StringBuilder();
    for (final String extension : extensions)
      {
        if (fullDescriptionBuilder.length() > 0)
          {
            fullDescriptionBuilder.append(", ");
          }
        fullDescriptionBuilder.append("*.");
        fullDescriptionBuilder.append(extension);
      }
    return
      description != null ?
      description + " (" + fullDescriptionBuilder + ")" :
      fullDescriptionBuilder.toString();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
