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
import java.net.URL;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import org.soundpaint.sysexedit.gui.Map;
import org.soundpaint.sysexedit.gui.MapContextMenu;
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
  private final Value deviceId;
  private final String enteredBy;
  private final AddressRepresentation addressRepresentation;

  private Device()
  {
    throw new UnsupportedOperationException();
  }

  private Device(final String name, final byte manufacturerId,
                 final byte modelId, final Value deviceId,
                 final String enteredBy,
                 final AddressRepresentation addressRepresentation,
                 final MapRoot root)
  {
    this.name = name;
    this.manufacturerId = manufacturerId;
    this.modelId = modelId;
    this.deviceId = deviceId;
    this.enteredBy = enteredBy;
    this.addressRepresentation = addressRepresentation;
    this.root = root;
  }

  public static Device create(final URL deviceDescriptionUrl)
    throws ParseException
  {
    final DeviceModelParser parser =
      new DeviceModelParser(deviceDescriptionUrl);
    return create(parser);
  }

  public static Device create(final String deviceName)
    throws ParseException
  {
    final DeviceModelParser parser =
      new DeviceModelParser(deviceName);
    return create(parser);
  }

  private static Device create(final DeviceModelParser parser)
    throws ParseException
  {
    return new Device(parser.getDeviceName(),
                      parser.getManufacturerId(),
                      parser.getModelId(),
                      parser.getDeviceId(),
                      parser.getEnteredBy(),
                      parser.getAddressRepresentation(),
                      parser.getRoot());
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

  public Value getDeviceId()
  {
    return deviceId;
  }

  public String getEnteredBy()
  {
    return enteredBy;
  }

  public AddressRepresentation getAddressRepresentation()
  {
    return addressRepresentation;
  }

  @Override
  public TreeNode buildMap(final TreeSelectionListener selectionListener,
                           final MapContextMenu mapContextMenu)
  {
    final Map map = new Map(selectionListener, mapContextMenu);
    root.setMap(map);
    resolve();
    return root;
  }

  public void buildMap(final MapRoot root)
  {
    // map already built => nothing to do
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

  public String toString()
  {
    return root.toString();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
