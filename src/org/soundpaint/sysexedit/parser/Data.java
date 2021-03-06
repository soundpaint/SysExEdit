/*
 * @(#)Data.java 1.00 18/06/20
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

import org.soundpaint.sysexedit.model.Value;

public class Data implements ParserNode
{
  private final Value value;
  private final long desiredAddress;

  private Data()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public Data(final Value value, final long desiredAddress)
  {
    this.value = value;
    this.desiredAddress = desiredAddress;
  }

  public Value getValue()
  {
    return value;
  }

  public long getDesiredAddress()
  {
    // TODO / FIXME: desired address will typically depend on indexVar
    // of enclosing folder when multiplicity of that folder is greater
    // than 1.
    return desiredAddress;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
