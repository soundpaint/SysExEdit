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
import java.util.List;

public class Folder implements ParserNode
{
  private final List<ParserNode> contents;
  private String description;
  private StringExpression label;
  private int multiplicity;
  private IndexVariable variable;
  private long desiredAddress;
  private long desiredAddressIncrement;
  private Folder addressBase;
  private long incrementedDesiredAddress;

  public Folder()
  {
    contents = new ArrayList<ParserNode>();
  }

  public void setDescription(final String description)
  {
    this.description = description;
  }

  public String getDescription()
  {
    return description;
  }

  public void setLabel(final StringExpression label)
  {
    this.label = label;
  }

  public StringExpression getLabel()
  {
    return label;
  }

  public void setMultiplicity(final int multiplicity)
  {
    this.multiplicity = multiplicity;
  }

  public int getMultiplicity()
  {
    return multiplicity;
  }

  public void setIndexVariable(final IndexVariable variable)
  {
    this.variable = variable;
  }

  public IndexVariable getIndexVariable()
  {
    return variable;
  }

  public void setDesiredAddress(final long desiredAddress)
  {
    this.desiredAddress = desiredAddress;
  }

  public long getDesiredAddress()
  {
    return desiredAddress;
  }

  public void setDesiredAddressIncrement(final long desiredAddressIncrement)
  {
    this.desiredAddressIncrement = desiredAddressIncrement;
  }

  public long getDesiredAddressIncrement()
  {
    return desiredAddressIncrement;
  }

  public void resetLoopIndex()
  {
    incrementedDesiredAddress = desiredAddress;
    if (incrementedDesiredAddress > -1) {
      if (addressBase != null) {
        final long addressBaseIncrementedDesiredAddress =
          addressBase.getIncrementedDesiredAddress();
        if (addressBaseIncrementedDesiredAddress > -1) {
          incrementedDesiredAddress += addressBaseIncrementedDesiredAddress;
        }
      }
    }
  }

  public void incrementLoopIndex()
  {
    if (incrementedDesiredAddress > -1) {
      if (desiredAddressIncrement > -1) {
        this.incrementedDesiredAddress += desiredAddressIncrement;
      }
    }
  }

  public long getIncrementedDesiredAddress()
  {
    return incrementedDesiredAddress;
  }

  public void setAddressBase(final Folder addressBase)
  {
    this.addressBase = addressBase;
  }

  public Folder getAddressBase()
  {
    return addressBase;
  }

  public void addAll(final Collection<ParserNode> nodes)
  {
    contents.addAll(nodes);
  }

  public Collection<ParserNode> getContents()
  {
    return contents;
  }

  public String toString()
  {
    return
      "Folder[description=" + description +
      ", label=" + label +
      ", multiplicity=" + multiplicity +
      ", variable=" + variable +
      ", desiredAddress=" + desiredAddress +
      ", desiredAddressIncrement=" + desiredAddressIncrement +
      ", addressBase=" + addressBase +
      ", incrementedDesiredAddress=" + incrementedDesiredAddress;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
