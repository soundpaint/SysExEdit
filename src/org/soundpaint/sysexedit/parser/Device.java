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
import org.soundpaint.sysexedit.model.DataNode;
import org.soundpaint.sysexedit.model.FolderNode;
import org.soundpaint.sysexedit.model.MapNode;
import org.soundpaint.sysexedit.model.Value;

/**
 * Device implementation for use by the device parser.
 */
public class Device extends AbstractDevice
{
  private final Folder parserRoot;
  private final String name;
  private final byte manufacturerId;
  private final byte modelId;
  private final Value deviceIdType;
  private final String enteredBy;
  private final AddressRepresentation addressRepresentation;

  private Device()
  {
    throw new UnsupportedOperationException("unsupported constructor");
  }

  private Device(final String name, final byte manufacturerId,
                 final byte modelId, final Value deviceIdType,
                 final String enteredBy,
                 final AddressRepresentation addressRepresentation,
                 final Folder parserRoot)
  {
    this.name = name;
    this.manufacturerId = manufacturerId;
    this.modelId = modelId;
    this.deviceIdType = deviceIdType;
    this.enteredBy = enteredBy;
    this.addressRepresentation = addressRepresentation;
    this.parserRoot = parserRoot;
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

  public Value getDeviceIdType()
  {
    return deviceIdType;
  }

  public String getEnteredBy()
  {
    return enteredBy;
  }

  public AddressRepresentation getAddressRepresentation()
  {
    return addressRepresentation;
  }

  private void addFolder(final FolderNode mapFolder, final Folder parseFolder)
  {
    final String description = parseFolder.getDescription();
    final String label = parseFolder.getLabel();
    final long desiredAddress = parseFolder.getDesiredAddress();
    for (int index = 0; index < parseFolder.getMultiplicity(); index++) {
      final FolderNode mapChildFolder =
        new FolderNode(description, label, desiredAddress);
      mapFolder.add(mapChildFolder);
      for (final ParserNode node : parseFolder.getContents()) {
        addNode(mapChildFolder, node);
      }
    }
  }

  private void addNode(final FolderNode mapFolder, final ParserNode node)
  {
    if (node instanceof Folder) {
      addFolder(mapFolder, (Folder)node);
    } else if (node instanceof Data) {
      final Data data = (Data)node;
      mapFolder.add(new DataNode(data.getValue(), data.getDesiredAddress()));
    } else {
      throw new IllegalStateException("unknown node type: " + node.getClass());
    }
  }

  public void buildMap(final MapRoot root)
  {
    for (final ParserNode node : parserRoot.getContents()) {
      addNode(root, node);
    }
  }

  public InputStream bulkDump(final byte deviceId,
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
