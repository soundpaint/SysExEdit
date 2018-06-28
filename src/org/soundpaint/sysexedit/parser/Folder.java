/*
 * @(#)Folder.java 1.00 18/06/20
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Folder implements ParserNode
{
  private final String description;
  private final StringExpression label;
  private final int multiplicity;
  private final IndexVariable variable;
  private final long desiredAddress;
  private final long desiredAddressIncrement;
  private final List<ParserNode> contents;

  private Folder()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public Folder(final String description, final StringExpression label,
                final int multiplicity, final IndexVariable variable,
                final long desiredAddress, final long desiredAddressIncrement)
  {
    this.description = description;
    this.label = label;
    this.multiplicity = multiplicity;
    this.variable = variable;
    this.desiredAddress = desiredAddress;
    this.desiredAddressIncrement = desiredAddressIncrement;
    contents = new ArrayList<ParserNode>();
  }

  public String getDescription()
  {
    return description;
  }

  public StringExpression getLabel()
  {
    return label;
  }

  public int getMultiplicity()
  {
    return multiplicity;
  }

  public IndexVariable getIndexVariable()
  {
    return variable;
  }

  public long getDesiredAddress()
  {
    return desiredAddress;
  }

  public long getDesiredAddressIncrement()
  {
    return desiredAddressIncrement;
  }

  public void addAll(final Collection<ParserNode> nodes)
  {
    contents.addAll(nodes);
  }

  public Collection<ParserNode> getContents()
  {
    return contents;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
