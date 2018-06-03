/*
 * @(#)Device.java 1.00 18/06/03
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

import java.io.InputStream;

import org.soundpaint.sysexedit.model.AbstractDevice;
import org.soundpaint.sysexedit.model.AddressRepresentation;
import org.soundpaint.sysexedit.model.MapNode;
import org.soundpaint.sysexedit.model.Value;

/**
 * Device implementation for use by the device parser.
 */
public class Device extends AbstractDevice
{
  private final String name;
  private final byte manufacturerId;
  private final byte modelId;
  private final String enteredBy;
  private final AddressRepresentation addressRepresentation;

  public Device(final String name,
                final byte manufacturerId,
                final byte modelId,
                final String enteredBy,
                final AddressRepresentation addressRepresentation)
  {
    this.name = name;
    this.manufacturerId = manufacturerId;
    this.modelId = modelId;
    this.enteredBy = enteredBy;
    this.addressRepresentation = addressRepresentation;
  }

  public String getName()
  {
    return name;
  }
  public byte getManufacturerId()
  {
    return manufacturerId;
  }

  public byte getModelId()
  {
    return modelId;
  }

  public Value createDeviceId()
  {
    // TODO
    return null;
  }

  public String getEnteredBy()
  {
    return enteredBy;
  }

  public AddressRepresentation getAddressRepresentation()
  {
    return addressRepresentation;
  }

  public void buildMap(final MapRoot root)
  {
    // TODO
  }

  public InputStream bulkDump(final Value deviceId,
                              final MapNode root,
                              final long start, final long end)
  {
    // TODO
    return null;
  }

  public void bulkRead(final InputStream in)
  {
    // TODO
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
