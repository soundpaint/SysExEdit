/*
 * @(#)HtmlPanel.java 1.00 98/02/06
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

import java.util.Vector;

/**
 * This class implements a history that stores data in each of its entries
 * and allows to go back and forward among the entries, just as used in a
 * browser, for example. Each entry consists of a reference (e.g. a URL)
 * and the actual content that is referred to (e.g. a HTML document).
 */
public class History<ReferenceType, ContentsType>
{
  private final Vector<ReferenceType> references = new Vector<ReferenceType>();
  private final Vector<ContentsType> contents = new Vector<ContentsType>();
  private int index = 0; // index of current + 1

  private History() {}

  /**
   * Creates a new History with the given reference and content as the
   * first entry.
   * @param reference A reference (e.g. a URL) to the content.
   * @param content The actual content (e.g. a HTML document).
   */
  public History(final ReferenceType reference, final ContentsType content)
  {
    add(reference, content);
  }

  /**
   * Uses reference and content to create a new entry and inserts it after
   * the current. Any other entries after the current will be lost.
   * @param reference A reference (e.g. a URL) to the content.
   * @param content The actual content (e.g. a HTML document).
   */
  public void add(final ReferenceType reference, final ContentsType content)
  {
    contents.setSize(index + 1);
    contents.setElementAt(content, index);
    references.setSize(index + 1);
    references.setElementAt(reference, index);
    index++;
  }

  /**
   * Returns true, if there is another entry before the current.
   * @return true, if there is another entry before the current.
   */
  public boolean hasPrev()
  {
    return index > 1;
  }

  /**
   * Moves to the previous entry in the history, if there is a previous
   * entry. Otherwise, this method has no effect.
   */
  public void prev()
  {
    if (hasPrev()) index--;
  }

  /**
   * Returns true, if there is another entry after the current.
   * @return true, if there is another entry after the current.
   */
  public boolean hasNext()
  {
    return index < references.size();
  }

  /**
   * Moves to the next entry in the history, if there is a next
   * entry. Otherwise, this method has no effect.
   */
  public void next()
  {
    if (hasNext()) index++;
  }

  /**
   * Sets the reference for the current position in the history.
   * @param reference The reference for the current position in the
   * history.
   */
  public void setReference(final ReferenceType reference)
  {
    references.setElementAt(reference, index - 1);
  }

  /**
   * Returns the reference at the current position in the history.
   * @return The reference at the current position in the history.
   */
  public ReferenceType getReference()
  {
    return references.elementAt(index - 1);
  }

  /**
   * Sets the content for the current position in the history.
   * @param content The content for the current position in the
   * history.
   */
  public void setContent(final ContentsType content)
  {
    contents.setElementAt(content, index - 1);
  }

  /**
   * Returns the content at the current position in the history.
   * @return The content at the current position in the history.
   */
  public ContentsType getContent()
  {
    return contents.elementAt(index - 1);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
