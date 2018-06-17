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
import org.soundpaint.sysexedit.model.AbstractDevice;
import org.soundpaint.sysexedit.model.AddressRepresentation;
import org.soundpaint.sysexedit.model.BitMaskRenderer;
import org.soundpaint.sysexedit.model.DataNode;
import org.soundpaint.sysexedit.model.EnumRenderer;
import org.soundpaint.sysexedit.model.FolderNode;
import org.soundpaint.sysexedit.model.IntegerRenderer;
import org.soundpaint.sysexedit.model.MapNode;
import org.soundpaint.sysexedit.model.SparseType;
import org.soundpaint.sysexedit.model.Value;
import org.soundpaint.sysexedit.model.ValueImpl;
import org.soundpaint.sysexedit.model.ValueRange;
import org.soundpaint.sysexedit.model.ValueRangeRenderer;

public class DeviceModelParser
{
  private static final String ATTR_NAME_ID = "id";
  private static final String ATTR_NAME_REF = "ref";
  private static final String ATTR_NAME_LABEL = "label";
  private static final String TAG_NAME_SYSEXEDIT = "sysexedit";
  private static final String TAG_NAME_DEVICE_CLASS = "device-class";
  private static final String TAG_NAME_DEVICE_NAME = "device-name";
  private static final String TAG_NAME_MAN_ID = "manufacturer-id";
  private static final String TAG_NAME_MODEL_ID = "model-id";
  private static final String TAG_NAME_DEVICE_ID = "device-id";
  private static final String TAG_NAME_ENTERED_BY = "entered-by";
  private static final String TAG_NAME_ENUM = "enum";
  private static final String TAG_NAME_TYPE = "type";
  private static final String TAG_NAME_RANGE = "range";
  private static final String TAG_NAME_RENDERER = "renderer";
  private static final String TAG_NAME_FOLDER = "folder";
  private static final String TAG_NAME_DESCRIPTION = "description";
  private static final String TAG_NAME_VALUES = "values";
  private static final String TAG_NAME_VALUE = "value";
  private static final String TAG_NAME_ICON = "icon";
  private static final String TAG_NAME_LOWER_BOUND = "lower-bound";
  private static final String TAG_NAME_UPPER_BOUND = "upper-bound";
  private static final String TAG_NAME_INTEGER = "integer";
  private static final String TAG_NAME_RADIX = "radix";
  private static final String TAG_NAME_FILL_WITH_LEADING_ZEROS = "fill-with-leading-zeros";
  private static final String TAG_NAME_DISPLAY_PREFIX = "display-prefix";
  private static final String TAG_NAME_DISPLAY_SUFFIX = "display-suffix";
  private static final String TAG_NAME_DISPLAY_MIN_WIDTH = "display-min-width";
  private static final String TAG_NAME_BIT_MASK = "bit-mask";
  private static final String TAG_NAME_BIT_STRING_SIZE = "bit-string-size";
  private static final String TAG_NAME_DATA = "data";
  private static final String TAG_NAME_LOOP = "loop";
  private static final String TAG_NAME_DESIRED_ADDRESS = "desired-address";

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
  private Symbol<Byte> manufacturerId;
  private Symbol<Byte> modelId;
  private Symbol<? extends Value> deviceId;
  private Symbol<String> enteredBy;
  private SymbolTable<ValueRange> rangeSymbols;
  private SymbolTable<SparseType> typeSymbols;
  private SymbolTable<ValueRangeRenderer> rendererSymbols;
  private SymbolTable<FolderNode> folderSymbols;
  private SymbolTable<Value> dataSymbols;

  public DeviceModelParser(final URL deviceUrl)
    throws ParseException
  {
    this(loadXml(deviceUrl));
  }

  public DeviceModelParser(final String deviceName)
    throws ParseException
  {
    this(loadXml(deviceName));
  }

  private DeviceModelParser(final Document document)
    throws ParseException
  {
    rangeSymbols = new SymbolTable<ValueRange>();
    typeSymbols = new SymbolTable<SparseType>();
    rendererSymbols = new SymbolTable<ValueRangeRenderer>();
    folderSymbols = new SymbolTable<FolderNode>();
    dataSymbols = new SymbolTable<Value>();
    parse(document);
  }

  public String getDeviceClass()
  {
    return deviceClass.getValue();
  }

  public String getDeviceName()
  {
    return deviceName.getValue();
  }

  public byte getManufacturerId()
  {
    return manufacturerId.getValue();
  }

  public byte getModelId()
  {
    return modelId.getValue();
  }

  public Value getDeviceId()
  {
    return deviceId.getValue();
  }

  public String getEnteredBy()
  {
    return enteredBy.getValue();
  }

  public AbstractDevice.MapRoot getRoot()
  {
    final Symbol<? extends FolderNode> rootSymbol =
      folderSymbols.lookupSymbol(Identifier.ROOT_ID);
    return
      rootSymbol != null ?
      (AbstractDevice.MapRoot)rootSymbol.getValue() :
      null;
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

  private void checkRootExists(final Element documentElement)
    throws ParseException
  {
    final FolderNode root = getRoot();
    if (root == null) {
      throw new ParseException(documentElement,
                               "no global folder node found that is marked as '#root'");
    }
  }

  private void parse(final Document document) throws ParseException
  {
    final Element documentElement = document.getDocumentElement();
    final String documentName = documentElement.getTagName();
    if (!documentName.equals(TAG_NAME_SYSEXEDIT)) {
      throw new ParseException("expected document element '" +
                               TAG_NAME_SYSEXEDIT + "', but found " +
                               documentName);
    }
    final NodeList childNodes = documentElement.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_DEVICE_CLASS)) {
          parseDeviceClass(childElement);
        } else if (childElementName.equals(TAG_NAME_DEVICE_NAME)) {
          parseDeviceName(childElement);
        } else if (childElementName.equals(TAG_NAME_MAN_ID)) {
          parseManufacturerId(childElement);
        } else if (childElementName.equals(TAG_NAME_MODEL_ID)) {
          parseModelId(childElement);
        } else if (childElementName.equals(TAG_NAME_DEVICE_ID)) {
          parseDeviceId(childElement);
        } else if (childElementName.equals(TAG_NAME_ENTERED_BY)) {
          parseEnteredBy(childElement);
        } else if (childElementName.equals(TAG_NAME_ENUM)) {
          parseEnumRenderer(childElement, true);
        } else if (childElementName.equals(TAG_NAME_INTEGER)) {
          parseIntegerRenderer(childElement, true);
        } else if (childElementName.equals(TAG_NAME_BIT_MASK)) {
          parseBitMaskRenderer(childElement, true);
        } else if (childElementName.equals(TAG_NAME_TYPE)) {
          parseType(childElement, true);
        } else if (childElementName.equals(TAG_NAME_RANGE)) {
          parseRange(childElement, true);
        } else if (childElementName.equals(TAG_NAME_FOLDER)) {
          parseFolder(childElement, true);
        } else if (childElementName.equals(TAG_NAME_DATA)) {
          parseData(childElement, true);
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
    checkRootExists(documentElement);
  }

  private void throwDuplicateException(final Element element,
                                       final String tagName,
                                       final Throwable cause)
    throws ParseException
  {
    throw new ParseException(element, "duplicate '" + tagName + "' definition",
                             cause);
  }

  private void throwDuplicateException(final Element element,
                                       final String tagName)
    throws ParseException
  {
    throw new ParseException(element, "duplicate '" + tagName + "' definition");
  }

  private void parseDeviceClass(final Element element) throws ParseException
  {
    if (deviceClass != null) {
      final Throwable cause =
        new ParseException(deviceClass.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_DEVICE_CLASS, cause);
    }
    final String value = element.getTextContent();
    deviceClass = new Symbol<String>(element, value);
  }

  private void parseDeviceName(final Element element) throws ParseException
  {
    if (deviceName != null) {
      final Throwable cause =
        new ParseException(deviceName.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_DEVICE_NAME, cause);
    }
    final String value = element.getTextContent();
    deviceName = new Symbol<String>(element, value);
  }

  private static byte parseByte(final Element element) throws ParseException
  {
    final byte value;
    final String text = element.getTextContent();
    try {
      if (text.toLowerCase().startsWith("0x")) {
        value = Byte.parseByte(text.substring(2), 16);
      } else {
        value = Byte.parseByte(text);
      }
    } catch (final NumberFormatException e) {
      throw new ParseException(element, "invalid byte value: " + text);
    }
    return value;
  }

  private void parseManufacturerId(final Element element) throws ParseException
  {
    if (manufacturerId != null) {
      final Throwable cause =
        new ParseException(manufacturerId.getLocation(),
                           "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_MAN_ID, cause);
    }
    final byte value = parseByte(element);
    manufacturerId = new Symbol<Byte>(element, value);
  }

  private void parseModelId(final Element element) throws ParseException
  {
    if (modelId != null) {
      final Throwable cause =
        new ParseException(modelId.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_MODEL_ID, cause);
    }
    final byte value = parseByte(element);
    modelId = new Symbol<Byte>(element, value);
  }

  private void parseDeviceId(final Element element) throws ParseException
  {
    Identifier dataId = null;
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_DATA)) {
          if (dataId != null) {
            throwDuplicateException(childElement, TAG_NAME_DATA);
          }
          dataId = parseData(childElement, false);
          final Symbol<? extends Value> dataSymbol =
            dataSymbols.lookupSymbol(dataId);
          if (dataSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve data reference " +
                                     dataId);
          }
          deviceId = dataSymbol;
        } else if (isWhiteSpace(childNode)) {
          // ignore white space
        } else if (isIgnorableNodeType(childNode)) {
          // ignore comments, entities, etc.
        } else {
          throw new ParseException(childNode, "unsupported node");
        }
      }
    }
  }

  private void parseEnteredBy(final Element element) throws ParseException
  {
    if (enteredBy != null) {
      final Throwable cause =
        new ParseException(enteredBy.getLocation(), "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_ENTERED_BY, cause);
    }
    final String value = element.getTextContent();
    enteredBy = new Symbol<String>(element, value);
  }

  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  private Identifier parseId(final Element element, final boolean requireId)
    throws ParseException
  {
    final Identifier identifier;
    if (element.hasAttribute(ATTR_NAME_REF)) {
      identifier = Identifier.fromString(element.getAttribute(ATTR_NAME_REF));
      if (element.hasAttribute(ATTR_NAME_ID)) {
        throw new ParseException(element, "element may declare at most one of the attributes 'id' and 'ref'");
      }
    } else if (element.hasAttribute(ATTR_NAME_ID)) {
      identifier = Identifier.fromString(element.getAttribute(ATTR_NAME_ID));
    } else {
      if (requireId) {
        throw new ParseException(element,
                                 "missing attribute '" + ATTR_NAME_ID + "'");
      }
      identifier = Identifier.createAnonymousIdentifier();
    }
    return identifier;
  }

  private boolean isTypeRef(final Element element) throws ParseException
  {
    final boolean isTypeRef = element.hasAttribute(ATTR_NAME_REF);
    if (isTypeRef) {
      final NodeList childNodes = element.getChildNodes();
      for (int index = 0; index < childNodes.getLength(); index++) {
        final Node childNode = childNodes.item(index);
        if (childNode instanceof Element) {
          final Element childElement = (Element)childNode;
          final String childElementName = childElement.getTagName();
          throw new ParseException(childElement,
                                   "element may not have children elements, but found tag '" +
                                   childElementName + "'");
        } else if (isWhiteSpace(childNode)) {
          // ignore white space
        } else if (isIgnorableNodeType(childNode)) {
          // ignore comments, entities, etc.
        } else {
          throw new ParseException(childNode, "unsupported node");
        }
      }
    }
    return isTypeRef;
  }

  private Identifier parseEnumRenderer(final Element element,
                                       final boolean requireId)
    throws ParseException
  {
    final Identifier identifier = parseId(element, requireId);
    if (isTypeRef(element)) {
      return identifier;
    }
    String description = null;
    List<String> values = null;
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_DESCRIPTION)) {
          if (description != null) {
            throwDuplicateException(childElement, TAG_NAME_DESCRIPTION);
          }
          description = childElement.getTextContent();
        } else if (childElementName.equals(TAG_NAME_VALUES)) {
          if (values != null) {
            throwDuplicateException(childElement, TAG_NAME_VALUES);
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
    if (values == null) {
      throw new ParseException(element,
                               "missing '" + TAG_NAME_VALUES + "' declaration");
    }
    final String[] strValues = values.toArray(EMPTY_STRING_ARRAY);
    if (strValues.length == 0) {
      throw new ParseException(element,
                               "empty '" + TAG_NAME_VALUES + "' declaration");
    }
    final EnumRenderer renderer = new EnumRenderer(description, 0, strValues);
    final Symbol<EnumRenderer> symbol =
      new Symbol<EnumRenderer>(element, renderer);
    rendererSymbols.enterSymbol(identifier, symbol);
    return identifier;
  }

  private Identifier parseIntegerRenderer(final Element element,
                                          final boolean requireId)
    throws ParseException
  {
    final Identifier identifier = parseId(element, requireId);
    if (isTypeRef(element)) {
      return identifier;
    }
    Integer lowerBound = null;
    Integer radix = null;
    Boolean fillWithLeadingZeros = null;
    String displayPrefix = null;
    String displaySuffix = null;
    Byte displayMinWidth = null;
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_LOWER_BOUND)) {
          if (lowerBound != null) {
            throwDuplicateException(childElement, TAG_NAME_LOWER_BOUND);
          }
          lowerBound = Integer.parseInt(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_RADIX)) {
          if (radix != null) {
            throwDuplicateException(childElement, TAG_NAME_RADIX);
          }
          radix = Integer.parseInt(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_FILL_WITH_LEADING_ZEROS)) {
          if (fillWithLeadingZeros != null) {
            throwDuplicateException(childElement, TAG_NAME_FILL_WITH_LEADING_ZEROS);
          }
          fillWithLeadingZeros = Boolean.parseBoolean(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_DISPLAY_PREFIX)) {
          if (displayPrefix != null) {
            throwDuplicateException(childElement, TAG_NAME_DISPLAY_PREFIX);
          }
          displayPrefix = childElement.getTextContent().trim();
        } else if (childElementName.equals(TAG_NAME_DISPLAY_SUFFIX)) {
          if (displaySuffix != null) {
            throwDuplicateException(childElement, TAG_NAME_DISPLAY_SUFFIX);
          }
          displaySuffix = childElement.getTextContent().trim();
        } else if (childElementName.equals(TAG_NAME_DISPLAY_MIN_WIDTH)) {
          if (displayMinWidth != null) {
            throwDuplicateException(childElement, TAG_NAME_DISPLAY_MIN_WIDTH);
          }
          displayMinWidth = Byte.parseByte(childElement.getTextContent());
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
    final IntegerRenderer renderer =
      new IntegerRenderer(lowerBound != null ? lowerBound : 0,
                          radix != null ? radix : 10,
                          fillWithLeadingZeros != null ? fillWithLeadingZeros : false,
                          displayPrefix != null ? displayPrefix : "",
                          displaySuffix != null ? displaySuffix : "",
                          displayMinWidth != null ? displayMinWidth : 0);
    final Symbol<IntegerRenderer> symbol =
      new Symbol<IntegerRenderer>(element, renderer);
    rendererSymbols.enterSymbol(identifier, symbol);
    return identifier;
  }

  private Identifier parseBitMaskRenderer(final Element element,
                                          final boolean requireId)
    throws ParseException
  {
    final Identifier identifier = parseId(element, requireId);
    if (isTypeRef(element)) {
      return identifier;
    }
    Integer lowerBound = null;
    Integer bitStringSize = null;
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_LOWER_BOUND)) {
          if (lowerBound != null) {
            throwDuplicateException(childElement, TAG_NAME_LOWER_BOUND);
          }
          lowerBound = Integer.parseInt(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_BIT_STRING_SIZE)) {
          if (bitStringSize != null) {
            throwDuplicateException(childElement, TAG_NAME_BIT_STRING_SIZE);
          }
          bitStringSize = Integer.parseInt(childElement.getTextContent());
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
    final BitMaskRenderer renderer =
      new BitMaskRenderer(lowerBound != null ? lowerBound : 0,
                          bitStringSize != null ? bitStringSize : 8);
    final Symbol<BitMaskRenderer> symbol =
      new Symbol<BitMaskRenderer>(element, renderer);
    rendererSymbols.enterSymbol(identifier, symbol);
    return identifier;
  }

  private Identifier parseType(final Element element, final boolean requireId)
    throws ParseException
  {
    final Identifier identifier = parseId(element, requireId);
    if (isTypeRef(element)) {
      return identifier;
    }
    String description = null;
    Identifier iconId = null;
    final List<ValueRange> ranges = new ArrayList<ValueRange>();
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_DESCRIPTION)) {
          if (description != null) {
            throwDuplicateException(childElement, TAG_NAME_DESCRIPTION);
          }
          description = childElement.getTextContent();
        } else if (childElementName.equals(TAG_NAME_ICON)) {
          if (iconId != null) {
            throwDuplicateException(childElement, TAG_NAME_ICON);
          }
          iconId = Identifier.fromString(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_RANGE)) {
          final Identifier rangeId = parseRange(childElement, false);
          final Symbol<? extends ValueRange> rangeSymbol =
            rangeSymbols.lookupSymbol(rangeId);
          if (rangeSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve range reference " +
                                     rangeId);
          }
          ranges.add(rangeSymbol.getValue());
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
    final SparseType type =
      new SparseType(description,
                     iconId != null ? iconId.toString() : null,
                     ranges);
    final Symbol<SparseType> symbol = new Symbol<SparseType>(element, type);
    typeSymbols.enterSymbol(identifier, symbol);
    return identifier;
  }

  private void throwDuplicateRendererException(final Element element,
                                               final String tagName)
    throws ParseException
  {
    throw new ParseException(element, "duplicate '" + tagName +
                             "' renderer definition");
  }

  private Identifier parseRange(final Element element, final boolean requireId)
    throws ParseException
  {
    final Identifier identifier = parseId(element, requireId);
    if (isTypeRef(element)) {
      return identifier;
    }
    String description = null;
    Long lowerBound = null;
    Long upperBound = null;
    Symbol<? extends ValueRangeRenderer> rendererSymbol = null;
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_DESCRIPTION)) {
          if (description != null) {
            throwDuplicateException(childElement, TAG_NAME_DESCRIPTION);
          }
          description = childElement.getTextContent();
        } else if (childElementName.equals(TAG_NAME_LOWER_BOUND)) {
          if (lowerBound != null) {
            throwDuplicateException(childElement, TAG_NAME_LOWER_BOUND);
          }
          lowerBound = Long.parseLong(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_UPPER_BOUND)) {
          if (upperBound != null) {
            throwDuplicateException(childElement, TAG_NAME_UPPER_BOUND);
          }
          upperBound = Long.parseLong(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_ENUM)) {
          if (rendererSymbol != null) {
            final Throwable cause =
              new ParseException(rendererSymbol.getLocation(),
                                 "first definition here");
            cause.fillInStackTrace();
            throwDuplicateRendererException(childElement, TAG_NAME_ENUM);
          }
          final Identifier rendererId = parseEnumRenderer(childElement, false);
          rendererSymbol = rendererSymbols.lookupSymbol(rendererId);
          if (rendererSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve renderer reference " +
                                     rendererId);
          }
        } else if (childElementName.equals(TAG_NAME_INTEGER)) {
          if (rendererSymbol != null) {
            final Throwable cause =
              new ParseException(rendererSymbol.getLocation(),
                                 "first definition here");
            cause.fillInStackTrace();
            throwDuplicateRendererException(childElement, TAG_NAME_INTEGER);
          }
          final Identifier rendererId =
            parseIntegerRenderer(childElement, false);
          rendererSymbol = rendererSymbols.lookupSymbol(rendererId);
          if (rendererSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve renderer reference " +
                                     rendererId);
          }
        } else if (childElementName.equals(TAG_NAME_BIT_MASK)) {
          if (rendererSymbol != null) {
            final Throwable cause =
              new ParseException(rendererSymbol.getLocation(),
                                 "first definition here");
            cause.fillInStackTrace();
            throwDuplicateRendererException(childElement, TAG_NAME_BIT_MASK);
          }
          final Identifier rendererId =
            parseBitMaskRenderer(childElement, true);
          rendererSymbol = rendererSymbols.lookupSymbol(rendererId);
          if (rendererSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve renderer reference " +
                                     rendererId);
          }
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
    if (rendererSymbol == null) {
      throw new ParseException(element, "one of '" + TAG_NAME_ENUM +
                               "', '" + TAG_NAME_INTEGER +
                               "', '" + TAG_NAME_BIT_MASK +
                               "' must be specified");
    }
    final ValueRangeRenderer renderer = rendererSymbol.getValue();
    final long lb =
      lowerBound != null ?
      lowerBound :
      renderer.getLowerBound();
    final long ub =
      upperBound != null ?
      upperBound :
      lb + renderer.getSize() - 1;
    final ValueRange range =
      new ValueRange(description, lb, ub, renderer);
      /*
      new ValueRange(description,
                     lowerBound != null ? lowerBound : 0,
                     upperBound != null ? upperBound : 0,
                     renderer);
      */
    final Symbol<ValueRange> symbol = new Symbol<ValueRange>(element, range);
    rangeSymbols.enterSymbol(identifier, symbol);
    return identifier;
  }

  private Identifier parseFolder(final Element element, final boolean requireId)
    throws ParseException
  {
    final Identifier identifier = parseId(element, requireId);
    if (isTypeRef(element)) {
      return identifier;
    }
    String description = null;
    final String label;
    if (element.hasAttribute(ATTR_NAME_LABEL)) {
      label = element.getAttribute(ATTR_NAME_LABEL);
    } else {
      label = null;
    }
    final Long desiredAddress = null; // TODO
    final List<MapNode> folderContents = new ArrayList<MapNode>();
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_DESCRIPTION)) {
          if (description != null) {
            throwDuplicateException(childElement, TAG_NAME_DESCRIPTION);
          }
          description = childElement.getTextContent();
        } else if (childElementName.equals(TAG_NAME_FOLDER)) {
          final Identifier folderId = parseFolder(childElement, false);
          final Symbol<? extends FolderNode> folderSymbol =
            folderSymbols.lookupSymbol(folderId);
          if (folderSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve folder reference " +
                                     folderId);
          }
          if (folderId == Identifier.ROOT_ID) {
            throw new ParseException(element,
                                     "can not put root beneath other node");
          }
          folderContents.add(folderSymbol.getValue());
        } else if (childElementName.equals(TAG_NAME_DATA)) {
          final Identifier dataId = parseData(childElement, false);
          final Symbol<? extends Value> dataSymbol =
            dataSymbols.lookupSymbol(dataId);
          if (dataSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve data reference " +
                                     dataId);
          }
          folderContents.add(new DataNode(dataSymbol.getValue()));
        } else if (childElementName.equals(TAG_NAME_LOOP)) {
          // TODO
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
    final FolderNode folderNode;
    if (identifier == Identifier.ROOT_ID) {
      folderNode =
        new AbstractDevice.MapRoot(description != null ? description : null,
                                   label != null ? label : null,
                                   desiredAddress != null ? desiredAddress : -1);
    } else {
      folderNode =
        new FolderNode(description != null ? description : null,
                       label != null ? label : null,
                       desiredAddress != null ? desiredAddress : -1);
    }
    for (MapNode node : folderContents) {
      folderNode.add(node);
    }
    final Symbol<FolderNode> symbol =
      new Symbol<FolderNode>(element, folderNode);
    folderSymbols.enterSymbol(identifier, symbol);
    return identifier;
  }

  private void parseValues(final Element element, final List<String> values)
    throws ParseException
  {
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_VALUE)) {
          final String value = childElement.getTextContent();
          values.add(value);
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
  }

  private Identifier parseData(final Element element, final boolean requireId)
    throws ParseException
  {
    final Identifier identifier = parseId(element, requireId);
    if (isTypeRef(element)) {
      return identifier;
    }
    String description = null; // TODO
    Identifier iconId = null;
    Long desiredAddress = null;

    final String label;
    if (element.hasAttribute(ATTR_NAME_LABEL)) {
      label = element.getAttribute(ATTR_NAME_LABEL);
    } else {
      label = null;
    }

    SparseType type = null;
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_DESCRIPTION)) {
          if (description != null) {
            throwDuplicateException(childElement, TAG_NAME_DESCRIPTION);
          }
          description = childElement.getTextContent();
        } else if (childElementName.equals(TAG_NAME_TYPE)) {
          if (type != null) {
            throwDuplicateException(childElement, TAG_NAME_TYPE);
          }
          final Identifier typeId = parseType(childElement, false);
          final Symbol<? extends SparseType> typeSymbol =
            typeSymbols.lookupSymbol(typeId);
          if (typeSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve type reference " +
                                     typeId);
          }
          type = typeSymbol.getValue();
        } else if (childElementName.equals(TAG_NAME_ICON)) {
          if (iconId != null) {
            throwDuplicateException(childElement, TAG_NAME_ICON);
          }
          iconId = Identifier.fromString(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_DESIRED_ADDRESS)) {
          if (desiredAddress != null) {
            throwDuplicateException(childElement, TAG_NAME_DESIRED_ADDRESS);
          }
          desiredAddress = Long.parseLong(childElement.getTextContent());
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
    if (type == null) {
      throw new ParseException(element,
                               "missing '" + TAG_NAME_TYPE + "' declaration");
    }
    final Value value =
      new ValueImpl(iconId != null ? iconId.toString() : null,
                    type, description, label,
                    desiredAddress != null ? desiredAddress : -1);
    final Symbol<Value> dataSymbol = new Symbol<Value>(element, value);
    dataSymbols.enterSymbol(identifier, dataSymbol);
    return identifier;
  }

  /**
   * For testing only.
   */
  public static void main(final String argv[]) throws ParseException
  {
    final Device device = Device.create("db50xg");
    System.out.println("[created device=" + device + "]");
    device.buildMap(null, null);
    System.out.println("[map=" + device.getMap() + "]");
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
