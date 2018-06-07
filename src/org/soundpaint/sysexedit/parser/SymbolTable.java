/*
 * @(#)SymbolTable.java 1.00 18/06/03
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

import java.util.HashMap;

public class SymbolTable<Type>
{
  private HashMap<Identifier, Symbol<? extends Type>> table;

  public SymbolTable()
  {
    table = new HashMap<Identifier, Symbol<? extends Type>>();
  }

  public void enterSymbol(final Identifier id,
                          final Symbol<? extends Type> symbol)
    throws ParseException
  {
    if (table.containsKey(id)) {
      final Symbol<? extends Type> other = table.get(id);
      final Throwable cause =
        new ParseException(other.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throw new ParseException(symbol.getLocation(), "duplicate symbol " + id,
                               cause);
    }
    table.put(id, symbol);
  }

  public Symbol<? extends Type> lookupSymbol(final Identifier id)
  {
    return table.get(id);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
