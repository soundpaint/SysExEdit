/*
 * @(#)XsdResourceResolver.java 1.00 18/07/08
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

import java.io.IOException;
import java.net.URL;
import javax.xml.XMLConstants;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class XsdResourceResolver implements LSResourceResolver
{
  private final URL schemaUrl;

  private XsdResourceResolver()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  public XsdResourceResolver(final URL schemaUrl)
  {
    if (schemaUrl == null) {
      throw new NullPointerException("schemaUrl");
    }
    this.schemaUrl = schemaUrl;
  }

  @Override
  public LSInput resolveResource(final String type,
                                 final String namespaceURI,
                                 final String publicId,
                                 final String systemId,
                                 final String baseURI)
  {
    if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(type)) {
      return null; // mismatching resource type
    }
    // TODO: May also want to match public id, system id, etc.
    try {
      return new XsdResourceInput(publicId, systemId, baseURI, schemaUrl);
    } catch (final IOException e) {
      return null; // resource not available
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
