/*
 * @(#)ParseException.java 1.00 18/06/03
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

package org.soundpaint.sysexedit.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ParseException extends Exception
{
  private static final long serialVersionUID = 8748895015695309559L;

  private final Node location;

  public ParseException()
  {
    this((Node)null);
  }

  public ParseException(final Node location)
  {
    this.location = location;
  }

  public ParseException(final String message)
  {
    this(null, message);
  }

  public ParseException(final Node location, final String message)
  {
    super(message);
    this.location = location;
  }

  public ParseException(final String message, final Throwable cause)
  {
    this(null, message, cause);
  }

  public ParseException(final Node location,
                        final String message, final Throwable cause)
  {
    super(message, cause);
    this.location = location;
  }

  public ParseException(final Throwable cause)
  {
    this((Node)null, cause);
  }

  public ParseException(final Node location, final Throwable cause)
  {
    super(cause);
    this.location = location;
  }

  public Node getLocation()
  {
    return location;
  }

  private static String formatLocation(final Node location)
  {
    if (location == null) {
      return "<null>";
    }
    final Document document = location.getOwnerDocument();
    // TODO: Print line number, xpath etc.
    return location.toString() + " in " + document + "[" +
      document.compareDocumentPosition(location) + "]";
  }

  public String getMessage()
  {
    return super.getMessage() + ", location: " +
      formatLocation(location);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
