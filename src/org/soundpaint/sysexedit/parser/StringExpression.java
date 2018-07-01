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
import org.w3c.dom.Node;

import org.soundpaint.sysexedit.model.SparseType;

public class StringExpression
{
  private List<StringFactor> factors;

  private interface StringFactor
  {
    String evaluate();
  }

  private class StringConstant implements StringFactor
  {
    private final String stringLiteral;

    private StringConstant()
    {
      throw new UnsupportedOperationException("unsupported constructor");
    }

    public StringConstant(final String stringLiteral)
    {
      if (stringLiteral == null) {
        throw new NullPointerException("stringLiteral");
      }
      this.stringLiteral = stringLiteral;
    }

    public String evaluate()
    {
      return stringLiteral;
    }
  }

  private class StringVariable implements StringFactor
  {
    private IndexVariable variable;
    private SparseType type;

    private StringVariable()
    {
      throw new UnsupportedOperationException("unsupported constructor");
    }

    public StringVariable(final IndexVariable variable,
                          final SparseType type)
    {
      this.variable = variable;
      this.type = type;
    }

    public String evaluate()
    {
      final int systemValue = variable.getValue();
      return type.getDisplayValue(systemValue);
    }
  }

  private StringExpression()
  {
    factors = new ArrayList<StringFactor>();
  }

  private void add(final String stringLiteral)
  {
    factors.add(new StringConstant(stringLiteral));
  }

  private void add(final IndexVariable variable,
                   final SparseType type)
  {
    factors.add(new StringVariable(variable, type));
  }

  private enum ParseState {
    START,
    CONST,
    ESCAPE,
    FUNC_START,
    FUNC,
    VAR_START,
    VAR
  }

  private static boolean isIdentifierStartChar(final char ch)
  {
    return
      (ch >= 'A' && ch <= 'Z') ||
      (ch >= 'a' && ch <= 'z');
  }

  private static boolean isIdentifierChar(final char ch)
  {
    return
      isIdentifierStartChar(ch) ||
      (ch >= '0' && ch <= '9') ||
      (ch == '-') ||
      (ch == '_');
  }

  private static char escape(final char ch)
  {
    switch (ch) {
    case 'r': return '\r';
    case 'n': return '\n';
    case 't': return '\t';
    default: return ch;
    }
  }

  private static class SEParseException extends ParseException
  {
    private static final long serialVersionUID = -299586991505951473L;

    private SEParseException()
    {
      throw new UnsupportedOperationException("unsupported constructor");
    }

    public SEParseException(final Node location,
                            final String parseInput,
                            final int charPos,
                            final String message)
    {
      super(location, createMessage(parseInput, charPos, message), null);
    }

    private static String createMessage(final String parseInput,
                                        final int charPos,
                                        final String message)
    {
      return
        "error at character position " + charPos +
        "while parsing string expression: " + message + "\r\n" +
        parseInput;
    }
  }

  private void addVariable(final Node location, final String parseInput,
                           final int charPos, final String variableId,
                           final String typeId, final Scope scope)
    throws ParseException
  {
    final Identifier variableIdentifier = Identifier.fromString(variableId);
    final Symbol<? extends IndexVariable> variableSymbol =
      scope.lookupIndexVariable(variableIdentifier);
    if (variableSymbol == null) {
      throw new SEParseException(location, parseInput, charPos,
                                 "could not resolve variable '" +
                                 variableId + "'");
    }
    final Identifier typeIdentifier = Identifier.fromString(typeId);
    final Symbol<? extends SparseType> typeSymbol =
      scope.lookupType(typeIdentifier);
    if (typeSymbol == null) {
      throw new SEParseException(location, parseInput, charPos,
                                 "could not resolve type '" + typeId + "'");
    }
    add(variableSymbol.getValue(), typeSymbol.getValue());
  }

  public static StringExpression parse(final Node location,
                                       final String parseInput,
                                       final Scope scope)
    throws ParseException
  {
    final StringExpression stringExpression = new StringExpression();
    ParseState state = ParseState.START;
    String typeId = null;
    final StringBuilder token = new StringBuilder();
    for (int pos = 0; pos < parseInput.length(); pos++) {
      final char ch = parseInput.charAt(pos);
      switch (state) {
      case START:
        if (ch == '\\') {
          state = ParseState.ESCAPE;
        } else if (ch == '$') {
          token.setLength(0);
          state = ParseState.FUNC_START;
        } else {
          token.setLength(0);
          token.append(ch);
          state = ParseState.CONST;
        }
        break;
      case CONST:
        if (ch == '\\') {
          state = ParseState.ESCAPE;
        } else if (ch == '$') {
          final String stringConst = token.toString();
          stringExpression.add(stringConst);
          token.setLength(0);
          state = ParseState.FUNC;
        } else {
          token.append(ch);
          // keep state
        }
        break;
      case ESCAPE:
        token.append(escape(ch));
        state = ParseState.CONST;
        break;
      case FUNC_START:
        if (isIdentifierStartChar(ch)) {
          token.append(ch);
          state = ParseState.FUNC;
        } else {
          throw new SEParseException(location, parseInput, pos,
                                     "identifier start character expected");
        }
        break;
      case FUNC:
        if (isIdentifierChar(ch)) {
          token.append(ch);
          // keep state
        } else if (ch == '(') {
          typeId = token.toString();
          token.setLength(0);
          state = ParseState.VAR_START;
        } else {
          throw new SEParseException(location, parseInput, pos,
                                     "identifier character expected");
        }
        break;
      case VAR_START:
        if (isIdentifierStartChar(ch)) {
          token.append(ch);
          state = ParseState.VAR;
        } else {
          throw new SEParseException(location, parseInput, pos,
                                     "identifier start character expected");
        }
        break;
      case VAR:
        if (isIdentifierChar(ch)) {
          token.append(ch);
          // keep state
        } else if (ch == ')') {
          final String variableId = token.toString();
          stringExpression.
            addVariable(location, parseInput, pos, variableId, typeId, scope);
          token.setLength(0);
          state = ParseState.START;
        } else {
          throw new SEParseException(location, parseInput, pos,
                                     "identifier character expected");
        }
        break;
      default:
        throw new IllegalStateException("unexpected state: " + state);
      }
    }

    switch (state) {
    case START:
      // ok
      break;
    case CONST:
      final String stringConst = token.toString();
      stringExpression.add(stringConst);
      break;
    case ESCAPE:
      throw new SEParseException(location, parseInput,
                                 parseInput.length(),
                                 "escape completion character expected");
    case FUNC_START:
      throw new SEParseException(location, parseInput,
                                 parseInput.length(),
                                 "identifier start character expected");
    case FUNC:
      throw new SEParseException(location, parseInput,
                                 parseInput.length(),
                                 "'(' expected");
    case VAR_START:
      throw new SEParseException(location, parseInput,
                                 parseInput.length(),
                                 "identifier start charracter expected");
    case VAR:
      throw new SEParseException(location, parseInput,
                                 parseInput.length(),
                                 "')' expected");
    default:
      throw new IllegalStateException("unexpected state: " + state);
    }

    return stringExpression;
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
