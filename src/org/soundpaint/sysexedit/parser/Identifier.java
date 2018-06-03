/*
 * @(#)Identifier.java 1.00 18/06/03
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Identifier
{
  private final String id;

  private static final Pattern idSyntax;

  static
  {
    idSyntax = Pattern.compile("[A-Za-z][A-Za-z0-9-_]*");
  }

  private static void checkSyntax(final String id) throws ParseException
  {
    final Matcher matcher = idSyntax.matcher(id);
    if (!matcher.matches()) {
      throw new ParseException("invalid identifier: " + id);
    }
  }

  public static Identifier fromString(final String id) throws ParseException
  {
    return new Identifier(id);
  }

  private static long count;

  public static Identifier createAnonymousIdentifier() throws ParseException
  {
    return new Identifier(count++);
  }

  private Identifier()
  {
    throw new RuntimeException("unsupported constructor");
  }

  private Identifier(final String id) throws ParseException
  {
    checkSyntax(id);
    this.id = id;
  }

  private Identifier(final long id) throws ParseException
  {
    if (id < 0) {
      throw new ParseException("anonymous identifier overflow");
    }
    this.id = "#anonymous_" + id;
  }

  public String toString()
  {
    return id;
  }

  public boolean equals(final Object obj)
  {
    if (!(obj instanceof Identifier)) {
      return false;
    }
    final Identifier other = (Identifier)obj;
    if (id == null) {
      return other.id == null;
    }
    return id.equals(other.id);
  }

  public int hashCode()
  {
    return id.hashCode();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
