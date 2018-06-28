/*
 * @(#)StringExpression.java 1.00 18/06/27
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

public class StringExpression
{
  private List<StringFactor> factors;

  private interface StringFactor
  {
    String evaluate();
  }

  private class StringConstant implements StringFactor
  {
    private final String value;

    private StringConstant()
    {
      throw new UnsupportedOperationException("unsupported constructor");
    }

    public StringConstant(final String value)
    {
      if (value == null) {
        throw new NullPointerException("value");
      }
      this.value = value;
    }

    public String evaluate()
    {
      return value;
    }
  }

  private class StringVariable implements StringFactor
  {
    private IndexVariable variable;

    private StringVariable()
    {
      throw new UnsupportedOperationException("unsupported constructor");
    }

    public StringVariable(final IndexVariable variable)
    {
      this.variable = variable;
    }

    public String evaluate()
    {
      final int value = variable.getValue();
      return "" + value; // TODO: support enums, etc.
    }
  }

  public StringExpression()
  {
    factors = new ArrayList<StringFactor>();
  }

  public void add(final String constValue)
  {
    factors.add(new StringConstant(constValue));
  }

  public void add(final IndexVariable variable)
  {
    factors.add(new StringVariable(variable));
  }

  public String evaluate()
  {
    final StringBuilder value = new StringBuilder();
    for (final StringFactor factor : factors) {
      value.append(factor.evaluate());
    }
    return value.toString();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
