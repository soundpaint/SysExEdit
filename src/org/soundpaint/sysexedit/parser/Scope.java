/*
 * @(#)Scope.java 1.00 18/06/26
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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.soundpaint.sysexedit.model.SparseType;

public class Scope
{
  private class ScopeItem
  {
    private SymbolTable<IndexVariable> indexVariableSymbols;
    private SymbolTable<SparseType> typeSymbols;
    private SymbolTable<Folder> folderSymbols;

    public ScopeItem()
    {
      indexVariableSymbols = new SymbolTable<IndexVariable>();
      typeSymbols = new SymbolTable<SparseType>();
      folderSymbols = new SymbolTable<Folder>();
    }
  }

  private Stack<ScopeItem> scopeItems;

  public Scope()
  {
    scopeItems = new Stack<ScopeItem>();
    enterScope();
  }

  public boolean empty()
  {
    return scopeItems.empty();
  }

  public ScopeItem peek()
  {
    return scopeItems.peek();
  }

  public void enterScope()
  {
    scopeItems.push(new ScopeItem());
  }

  public void leaveScope()
  {
    scopeItems.pop();
  }

  public void enterIndexVariable(final Symbol<IndexVariable> indexVariable)
    throws ParseException
  {
    final Identifier id = indexVariable.getValue().getIdentifier();
    if (lookupIndexVariable(id) != null) {
      // TODO: Where to put warnings?  Probably not directly to
      // System.out.
      System.out.println("warning: index variable " + id +
                         " hides declaration from enclosing scope");
    }
    final ScopeItem scopeItem = scopeItems.peek();
    scopeItem.indexVariableSymbols.enterSymbol(id, indexVariable);
  }

  public Symbol<? extends IndexVariable>
    lookupIndexVariable(final Identifier id)
  {
    return lookupIndexVariable(id, scopeItems.size() - 1);
  }

  private Symbol<? extends IndexVariable>
    lookupIndexVariable(final Identifier id, final int scopeIndex)
  {
    final ScopeItem scopeItem = scopeItems.get(scopeIndex);
    final Symbol<? extends IndexVariable> indexVariable =
      scopeItem.indexVariableSymbols.lookupSymbol(id);
    if (indexVariable != null) {
      return indexVariable;
    } else {
      if (scopeIndex > 0) {
        return lookupIndexVariable(id, scopeIndex - 1);
      } else {
        return null;
      }
    }
  }

  public void enterType(final Symbol<SparseType> type, final Identifier id)
    throws ParseException
  {
    if (lookupType(id) != null) {
      // TODO: Where to put warnings?  Probably not directly to
      // System.out.
      System.out.println("warning: type " + id +
                         " hides declaration from enclosing scope");
    }
    final ScopeItem scopeItem = scopeItems.peek();
    scopeItem.typeSymbols.enterSymbol(id, type);
  }

  public Symbol<? extends SparseType>
    lookupType(final Identifier id)
  {
    return lookupType(id, scopeItems.size() - 1);
  }

  private Symbol<? extends SparseType>
    lookupType(final Identifier id, final int scopeIndex)
  {
    final ScopeItem scopeItem = scopeItems.get(scopeIndex);
    final Symbol<? extends SparseType> type =
      scopeItem.typeSymbols.lookupSymbol(id);
    if (type != null) {
      return type;
    } else {
      if (scopeIndex > 0) {
        return lookupType(id, scopeIndex - 1);
      } else {
        return null;
      }
    }
  }

  public void enterFolder(final Identifier id, final Symbol<Folder> folder)
    throws ParseException
  {
    if (lookupIndexVariable(id) != null) {
      // TODO: Where to put warnings?  Probably not directly to
      // System.out.
      System.out.println("warning: folder " + id +
                         " hides declaration from enclosing scope");
    }
    final ScopeItem scopeItem = scopeItems.peek();
    scopeItem.folderSymbols.enterSymbol(id, folder);
  }

  public Symbol<? extends Folder> lookupFolder(final Identifier id)
  {
    return lookupFolder(id, scopeItems.size() - 1);
  }

  private Symbol<? extends Folder>
    lookupFolder(final Identifier id, final int scopeIndex)
  {
    final ScopeItem scopeItem = scopeItems.get(scopeIndex);
    final Symbol<? extends Folder> folder =
      scopeItem.folderSymbols.lookupSymbol(id);
    if (folder != null) {
      return folder;
    } else {
      if (scopeIndex > 0) {
        return lookupFolder(id, scopeIndex - 1);
      } else {
        return null;
      }
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
