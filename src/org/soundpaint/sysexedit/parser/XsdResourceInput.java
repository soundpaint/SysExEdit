/*
 * @(#)XsdResourceInput.java 1.00 18/07/08
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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;

public class XsdResourceInput implements LSInput
{
  private final String publicId;
  private final String systemId;
  private final String baseURI;
  private final String stringData;

  public XsdResourceInput(final String publicId, final String sysId,
                          final String baseURI, final URL schemaURL)
    throws IOException
  {
    this.publicId = publicId;
    this.systemId = sysId;
    this.baseURI = baseURI;
    final InputSource inputSource = new InputSource(schemaURL.openStream());
    final InputStream inputStream =
      new BufferedInputStream(inputSource.getByteStream());
    final byte[] input = new byte[inputStream.available()];
    inputStream.read(input);
    stringData = new String(input);
    inputStream.close();
  }

  @Override
  public Reader getCharacterStream()
  {
    return null;
  }

  @Override
  public void setCharacterStream(final Reader characterStream)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InputStream getByteStream()
  {
    return null;
  }

  @Override
  public void setByteStream(final InputStream byteStream)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getStringData()
  {
    return stringData;
  }

  @Override
  public void setStringData(final String stringData)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getSystemId()
  {
    return systemId;
  }

  @Override
  public void setSystemId(final String systemId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPublicId()
  {
    return publicId;
  }

  @Override
  public void setPublicId(final String publicId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getBaseURI()
  {
    return baseURI;
  }

  @Override
  public void setBaseURI(final String baseURI)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getEncoding()
  {
    return null;
  }

  @Override
  public void setEncoding(final String encoding)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean getCertifiedText()
  {
    return false;
  }

  @Override
  public void setCertifiedText(final boolean certifiedText)
  {
    throw new UnsupportedOperationException();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
