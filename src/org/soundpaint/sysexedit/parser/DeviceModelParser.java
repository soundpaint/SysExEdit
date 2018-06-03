/*
 * @(#)DeviceModelParser.java 1.00 18/06/03
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
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import org.soundpaint.sysexedit.SysExEdit;
import org.soundpaint.sysexedit.model.Value;
import org.soundpaint.sysexedit.model.ValueImpl;
import org.soundpaint.sysexedit.model.MapNode;
import org.soundpaint.sysexedit.model.SparseType;
import org.soundpaint.sysexedit.model.SparseTypeImpl;
import org.soundpaint.sysexedit.model.ValueRangeRenderer;
import org.soundpaint.sysexedit.model.EnumRenderer;
import org.soundpaint.sysexedit.model.IntegerRenderer;
import org.soundpaint.sysexedit.model.AddressRepresentation;

import org.soundpaint.sysexedit.model.AbstractDevice;

public class DeviceModelParser
{
  private static final String ATTR_NAME_ID = "id";
  private static final String TAG_NAME_DEVICE_CLASS = "device-class";
  private static final String TAG_NAME_DEVICE_NAME = "device-name";
  private static final String TAG_NAME_MAN_ID = "manufacturer-id";
  private static final String TAG_NAME_MODEL_ID = "model-id";
  private static final String TAG_NAME_ENTERED_BY = "entered-by";
  private static final String TAG_NAME_ENUM = "enum";
  private static final String TAG_NAME_TYPE = "type";
  private static final String TAG_NAME_RANGE = "range";
  private static final String TAG_NAME_RENDERER = "renderer";
  private static final String TAG_NAME_FOLDER = "folder";
  private static final String TAG_NAME_TREE = "tree";
  private static final String TAG_NAME_DESCRIPTION = "description";
  private static final String TAG_NAME_VALUES = "values";

  private static Document loadXml(final URL deviceUrl)
    throws ParseException
  {
    System.out.println("[parsing device model from URL " + deviceUrl + "]");
    final Document document;
    try {
      final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setValidating(false);
      final DocumentBuilder db = dbf.newDocumentBuilder();
      document = db.parse(deviceUrl.toString());
    } catch (final Exception ex) {
      throw new ParseException("parsing device xml failed: "
          + ex.getMessage() + "\r\n" + deviceUrl, ex);
    }
    return document;
  }

  private static Document loadXml(final String deviceName)
    throws ParseException
  {
    final URL deviceResource =
      SysExEdit.class.getResource("/devices/" + deviceName + ".xml");
    return loadXml(deviceResource);
  }

  private Symbol<String> deviceClass;
  private Symbol<String> deviceName;
  private Symbol<String> manufacturerId;
  private Symbol<String> modelId;
  private Symbol<String> enteredBy;
  private SymbolTable<EnumRenderer> enums;

  public DeviceModelParser()
  {
    enums = new SymbolTable<EnumRenderer>();
  }

  public AbstractDevice parse(final String deviceName)
    throws ParseException
  {
    final Document document = loadXml(deviceName);
    return parse(document);
  }

  private static boolean isIgnorableNodeType(final Node node)
  {
    return
      (node instanceof Comment) ||
      (node instanceof Entity) ||
      (node instanceof Notation) ||
      (node instanceof ProcessingInstruction);
  }

  private static boolean isWhiteSpace(final Node node)
  {
    if (!(node instanceof Text)) {
      return false;
    }
    final Text text = (Text)node;
    return text.getData().trim().isEmpty();
  }

  public AbstractDevice parse(final Document document)
    throws ParseException
  {
    final Element docElement = document.getDocumentElement();
    final String docName = docElement.getTagName();
    if (!docName.equals("sysexedit")) {
      throw new ParseException("expected document element 'sysexedit', but found " + docName);
    }
    final NodeList children = docElement.getChildNodes();
    for (int index = 0; index < children.getLength(); index++) {
      final Node child = children.item(index);
      if (child instanceof Element) {
        final Element element = (Element)child;
        final String elementName = element.getTagName();
        if (elementName.equals(TAG_NAME_DEVICE_CLASS)) {
          parseDeviceClass(element);
        } else if (elementName.equals(TAG_NAME_DEVICE_NAME)) {
          parseDeviceName(element);
        } else if (elementName.equals(TAG_NAME_MAN_ID)) {
          parseManufacturerId(element);
        } else if (elementName.equals(TAG_NAME_MODEL_ID)) {
          parseModelId(element);
        } else if (elementName.equals(TAG_NAME_ENTERED_BY)) {
          parseEnteredBy(element);
        } else if (elementName.equals(TAG_NAME_ENUM)) {
          parseEnum(element, true);
        } else if (elementName.equals(TAG_NAME_TYPE)) {
          parseType(element);
        } else if (elementName.equals(TAG_NAME_RANGE)) {
          parseRange(element);
        } else if (elementName.equals(TAG_NAME_RENDERER)) {
          parseRenderer(element);
        } else if (elementName.equals(TAG_NAME_FOLDER)) {
          parseFolder(element);
        } else if (elementName.equals(TAG_NAME_TREE)) {
          parseTree(element);
        } else {
          throw new ParseException(element, "unexpected element: " +
                                   elementName);
        }
      } else if (isWhiteSpace(child)) {
        // ignore white space
      } else if (isIgnorableNodeType(child)) {
        // ignore comments, entities, etc.
      } else {
        throw new ParseException(child, "unsupported node");
      }
    }
    // TODO
    return null;
  }

  private void parseDeviceClass(final Element element)
    throws ParseException
  {
    if (deviceClass != null) {
      final Throwable cause =
        new ParseException(deviceClass.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throw new ParseException(element, "duplicate device class definition",
                               cause);
    }
    final String value = element.getTextContent();
    deviceClass = new Symbol<String>(element, value);
  }

  private void parseDeviceName(final Element element)
    throws ParseException
  {
    if (deviceName != null) {
      final Throwable cause =
        new ParseException(deviceName.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throw new ParseException(element, "duplicate device name definition",
                               cause);
    }
    final String value = element.getTextContent();
    deviceName = new Symbol<String>(element, value);
  }

  private void parseManufacturerId(final Element element)
    throws ParseException
  {
    if (manufacturerId != null) {
      final Throwable cause =
        new ParseException(deviceName.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throw new ParseException(element, "duplicate manufacturer ID definition",
                               cause);
    }
    final String value = element.getTextContent();
    manufacturerId = new Symbol<String>(element, value);
  }

  private void parseModelId(final Element element)
    throws ParseException
  {
    if (modelId != null) {
      final Throwable cause =
        new ParseException(deviceName.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throw new ParseException(element, "duplicate model ID definition",
                               cause);
    }
    final String value = element.getTextContent();
    modelId = new Symbol<String>(element, value);
  }

  private void parseEnteredBy(final Element element)
    throws ParseException
  {
    if (enteredBy != null) {
      final Throwable cause =
        new ParseException(deviceName.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throw new ParseException(element, "duplicate 'entered by' definition",
                               cause);
    }
    final String value = element.getTextContent();
    enteredBy = new Symbol<String>(element, value);
  }

  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  private void parseEnum(final Element element, final boolean requireId)
    throws ParseException
  {
    final Identifier identifier;
    if (element.hasAttribute(ATTR_NAME_ID)) {
      identifier = Identifier.fromString(element.getAttribute(ATTR_NAME_ID));
    } else {
      if (requireId) {
        throw new ParseException(element, "missing attribute 'id'");
      }
      identifier = Identifier.createAnonymousIdentifier();
    }

    String description = null;
    List<String> values = null;
    final NodeList children = element.getChildNodes();
    for (int index = 0; index < children.getLength(); index++) {
      final Node childNode = children.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_DESCRIPTION)) {
          if (description != null) {
            throw new ParseException(childElement,
                                     "duplicate enum description definition");
          }
          description = childElement.getTextContent();
        } else if (childElementName.equals(TAG_NAME_VALUES)) {
          if (values != null) {
            throw new ParseException(childElement,
                                     "duplicate enum values definition");
          }
          values = new ArrayList<String>();
          parseValues(childElement, values);
        } else {
          throw new ParseException(childElement, "unexpected element: " +
                                   childElementName);
        }
      } else if (isWhiteSpace(childNode)) {
        // ignore white space
      } else if (isIgnorableNodeType(childNode)) {
        // ignore comments, entities, etc.
      } else {
        throw new ParseException(childNode, "unsupported node");
      }
    }
    final EnumRenderer enumRenderer =
      new EnumRenderer(0,
                       values != null ? values.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY,
                       description);
    final Symbol<EnumRenderer> symbol =
      new Symbol<EnumRenderer>(element, enumRenderer);
    enums.enterSymbol(identifier, symbol);
  }

  private void parseType(final Element element)
  {
    // TODO
  }

  private void parseRange(final Element element)
  {
    // TODO
  }

  private void parseRenderer(final Element element)
  {
    // TODO
  }

  private void parseFolder(final Element element)
  {
    // TODO
  }

  private void parseTree(final Element element)
  {
    // TODO
  }

  private void parseValues(final Element element, final List<String> values)
  {
    // TODO
  }

  /**
   * For testing only.
   */
  private void test() throws ParseException
  {
    parse("db50xg");
  }

  public static void main(final String argv[]) throws ParseException
  {
    new DeviceModelParser().test();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
