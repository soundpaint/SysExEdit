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
  private static final String ATTR_NAME_MULTIPLICITY = "multiplicity";
  private static final String ATTR_NAME_INDEX_VAR = "index-var";
  private static final String ATTR_NAME_RELATIVE_TO = "relative-to";
  private static final String TAG_NAME_SYSEXEDIT = "sysexedit";
  private static final String TAG_NAME_ADDRESS_MODEL = "address-model";
  private static final String TAG_NAME_REPRESENTATION = "representation";
  private static final String TAG_NAME_DEFAULT_DATA_SIZE = "default-data-size";
  private static final String TAG_NAME_MULTIPLE_OF_BITS = "multiple-of-bits";
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
  private static final String TAG_NAME_DISPLAY_OFFSET = "display-offset";
  private static final String TAG_NAME_INTEGER = "integer";
  private static final String TAG_NAME_RADIX = "radix";
  private static final String TAG_NAME_FILL_WITH_LEADING_ZEROS = "fill-with-leading-zeros";
  private static final String TAG_NAME_DISPLAY_PREFIX = "display-prefix";
  private static final String TAG_NAME_DISPLAY_SUFFIX = "display-suffix";
  private static final String TAG_NAME_DISPLAY_MIN_WIDTH = "display-min-width";
  private static final String TAG_NAME_BIT_MASK = "bit-mask";
  private static final String TAG_NAME_BIT_STRING_SIZE = "bit-string-size";
  private static final String TAG_NAME_DATA = "data";
  private static final String TAG_NAME_BIT_SIZE = "bit-size";
  private static final String TAG_NAME_DEFAULT_VALUE = "default-value";
  private static final String TAG_NAME_BIT_ADDRESS = "bit-address";
  private static final String TAG_NAME_BIT_ADDRESS_INCREMENT =
    "bit-address-increment";
  private static final String TAG_NAME_ADDRESS = "address";
  private static final String TAG_NAME_ADDRESS_INCREMENT = "address-increment";

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

  private static boolean startsWithHexPrefix(final String value)
  {
    return value.startsWith("0x") || value.startsWith("-0x");
  }

  private static boolean startsWithBinPrefix(final String value)
  {
    return value.startsWith("0b") || value.startsWith("-0b");
  }

  private static String dropPrefix(final String value)
  {
    return
      value.startsWith("-") ?
      "-" + value.substring(3) :
      value.substring(2);
  }

  private static byte parseByte(final Element location, final String value)
    throws ParseException
  {
    final String trimmedValue = value.trim();
    final String lowerCaseValue = trimmedValue.toLowerCase();
    try {
      if (startsWithHexPrefix(lowerCaseValue)) {
        return Byte.parseByte(dropPrefix(trimmedValue), 16);
      } else if (startsWithBinPrefix(lowerCaseValue)) {
        return Byte.parseByte(dropPrefix(trimmedValue), 2);
      } else {
        return Byte.parseByte(trimmedValue);
      }
    } catch (final NumberFormatException e) {
      throw new ParseException(location, "invalid byte value: " + value);
    }
  }

  private static int parseInt(final Element location, final String value)
    throws ParseException
  {
    final String trimmedValue = value.trim();
    final String lowerCaseValue = trimmedValue.toLowerCase();
    try {
      if (startsWithHexPrefix(lowerCaseValue)) {
        return Integer.parseInt(dropPrefix(trimmedValue), 16);
      } else if (startsWithBinPrefix(lowerCaseValue)) {
        return Integer.parseInt(dropPrefix(trimmedValue), 2);
      } else {
        return Integer.parseInt(trimmedValue);
      }
    } catch (final NumberFormatException e) {
      throw new ParseException(location, "invalid integer value: " + value);
    }
  }

  private static long parseLong(final Element location, final String value)
    throws ParseException
  {
    final String trimmedValue = value.trim();
    final String lowerCaseValue = trimmedValue.toLowerCase();
    try {
      if (startsWithHexPrefix(lowerCaseValue)) {
        return Long.parseLong(dropPrefix(trimmedValue), 16);
      } else if (startsWithBinPrefix(lowerCaseValue)) {
        return Long.parseLong(dropPrefix(trimmedValue), 2);
      } else {
        return Long.parseLong(trimmedValue);
      }
    } catch (final NumberFormatException e) {
      throw new ParseException(location, "invalid long value: " + value);
    }
  }

  private Scope scope;
  private Symbol<AddressRepresentation> addressRepresentationSymbol;
  private Symbol<DefaultDataSize> defaultDataSizeSymbol;
  private Symbol<String> deviceClassSymbol;
  private Symbol<String> deviceNameSymbol;
  private Symbol<Byte> manufacturerIdSymbol;
  private Symbol<Byte> modelIdSymbol;
  private Symbol<? extends Data> deviceIdSymbol;
  private Symbol<String> enteredBySymbol;
  private SymbolTable<ValueRange> rangeSymbols;
  private SymbolTable<ValueRangeRenderer> rendererSymbols;
  private SymbolTable<Data> dataSymbols;

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
    scope = new Scope();
    rangeSymbols = new SymbolTable<ValueRange>();
    rendererSymbols = new SymbolTable<ValueRangeRenderer>();
    dataSymbols = new SymbolTable<Data>();
    parse(document);
  }

  public String getDeviceClass()
  {
    return deviceClassSymbol.getValue();
  }

  public String getDeviceName()
  {
    return deviceNameSymbol.getValue();
  }

  public byte getManufacturerId()
  {
    return manufacturerIdSymbol.getValue();
  }

  public byte getModelId()
  {
    return modelIdSymbol.getValue();
  }

  public Value getDeviceId()
  {
    return deviceIdSymbol.getValue().getValue();
  }

  public String getEnteredBy()
  {
    return enteredBySymbol.getValue();
  }

  public AddressRepresentation getAddressRepresentation()
  {
    return addressRepresentationSymbol.getValue();
  }

  private void checkRoot(final Element documentElement)
    throws ParseException
  {
    final Symbol<? extends Folder> rootSymbol =
      scope.lookupFolder(Identifier.ROOT_ID);
    if (rootSymbol == null) {
      throw new ParseException(documentElement,
                               "no global folder node found that is marked as '#root'");
    }
    final Element rootElement = (Element)(rootSymbol.getLocation());
    if (rootElement == null) {
      throw new ParseException(documentElement,
                               "root folder element without location info");
    }
    final Folder root = rootSymbol.getValue();
    if (root == null) {
      throw new ParseException(rootElement, "invalid root folder element");
    }
    if (root.getMultiplicity() != 1) {
      throw new ParseException(rootElement, "multiplicity of root folder must be 1");
    }
  }

  public Folder getRoot()
  {
    final Symbol<? extends Folder> rootSymbol =
      scope.lookupFolder(Identifier.ROOT_ID);
    return rootSymbol.getValue();
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

  private void checkAddressRepresentationExists(final Element documentElement)
    throws ParseException
  {
    final AddressRepresentation addressRepresentation =
      getAddressRepresentation();
    if (addressRepresentation == null) {
      throw new ParseException(documentElement,
                               "no global address representation definition found");
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
        if (childElementName.equals(TAG_NAME_ADDRESS_MODEL)) {
          parseAddressModel(childElement);
        } else if (childElementName.equals(TAG_NAME_DEVICE_CLASS)) {
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
    checkAddressRepresentationExists(documentElement);
    checkRoot(documentElement);
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

  private void throwDuplicateException(final Element element,
                                       final String tagName1,
                                       final String tagName2)
    throws ParseException
  {
    throw new ParseException(element, "can define only one of '" +
                             tagName1 + "' and '" + tagName2 + "', " +
                             "but not both");
  }

  private void parseAddressModel(final Element element) throws ParseException
  {
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_REPRESENTATION)) {
          parseRepresentation(childElement);
        } else if (childElementName.equals(TAG_NAME_DEFAULT_DATA_SIZE)) {
          parseDefaultDataSize(childElement);
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

  private void parseDefaultDataSize(final Element element)
    throws ParseException
  {
    if (defaultDataSizeSymbol != null) {
      final Throwable cause =
        new ParseException(defaultDataSizeSymbol.getLocation(),
                           "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_DEFAULT_DATA_SIZE, cause);
    }
    Byte multipleOfBits = null;
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_MULTIPLE_OF_BITS)) {
          if (multipleOfBits != null) {
            throwDuplicateException(childElement, TAG_NAME_MULTIPLE_OF_BITS);
          }
          multipleOfBits =
            parseByte(childElement, childElement.getTextContent());
          if (multipleOfBits < 1) {
            throw new ParseException(childElement,
                                     "'multiple-of-bits' must be a " +
                                     "positive value > 0");
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
    if (multipleOfBits == null) {
      throw new ParseException(element, "'multiple-of-bits' expected");
    }
    final DefaultDataSize defaultDataSize =
      DefaultDataSize.fromMultipleOfBits(multipleOfBits);
    defaultDataSizeSymbol =
      new Symbol<DefaultDataSize>(element, defaultDataSize);
  }

  private static final String ADDRESS_REPRESENTATION_CLASS_NAME =
    "org.soundpaint.sysexedit.model.AddressRepresentation";

  private void parseRepresentation(final Element element)
    throws ParseException
  {
    if (addressRepresentationSymbol != null) {
      final Throwable cause =
        new ParseException(addressRepresentationSymbol.getLocation(),
                           "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_REPRESENTATION, cause);
    }
    final String className =
      ADDRESS_REPRESENTATION_CLASS_NAME + "$" + element.getTextContent();
    final Class<? extends AddressRepresentation> clazz;
    try {
      clazz = (Class<? extends AddressRepresentation>)Class.forName(className);
    } catch (final ClassNotFoundException e) {
      throw
        new ParseException("no such address representation found: " +
                           className, e);
    }
    final AddressRepresentation value;
    try {
      value = clazz.newInstance();
    } catch (final InstantiationException e) {
      throw
        new ParseException("failed instantiating address representation: " +
                           className, e);
    } catch (final IllegalAccessException e) {
      throw
        new ParseException("failed instantiating address representation: " +
                           className, e);
    }
    addressRepresentationSymbol =
      new Symbol<AddressRepresentation>(element, value);
  }

  private void parseDeviceClass(final Element element) throws ParseException
  {
    if (deviceClassSymbol != null) {
      final Throwable cause =
        new ParseException(deviceClassSymbol.getLocation(),
                           "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_DEVICE_CLASS, cause);
    }
    final String value = element.getTextContent();
    deviceClassSymbol = new Symbol<String>(element, value);
  }

  private void parseDeviceName(final Element element) throws ParseException
  {
    if (deviceNameSymbol != null) {
      final Throwable cause =
        new ParseException(deviceNameSymbol.getLocation(),
                           "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_DEVICE_NAME, cause);
    }
    final String value = element.getTextContent();
    deviceNameSymbol = new Symbol<String>(element, value);
  }

  private void parseManufacturerId(final Element element) throws ParseException
  {
    if (manufacturerIdSymbol != null) {
      final Throwable cause =
        new ParseException(manufacturerIdSymbol.getLocation(),
                           "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_MAN_ID, cause);
    }
    final byte value = parseByte(element, element.getTextContent());
    manufacturerIdSymbol = new Symbol<Byte>(element, value);
  }

  private void parseModelId(final Element element) throws ParseException
  {
    if (modelIdSymbol != null) {
      final Throwable cause =
        new ParseException(modelIdSymbol.getLocation(),
                           "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_MODEL_ID, cause);
    }
    final byte value = parseByte(element, element.getTextContent());
    modelIdSymbol = new Symbol<Byte>(element, value);
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
          final Symbol<? extends Data> dataSymbol =
            dataSymbols.lookupSymbol(dataId);
          if (dataSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve data reference '" +
                                     dataId + "'");
          }
          deviceIdSymbol = dataSymbol;
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
    if (enteredBySymbol != null) {
      final Throwable cause =
        new ParseException(enteredBySymbol.getLocation(),
                           "first definition here");
      cause.fillInStackTrace();
      throwDuplicateException(element, TAG_NAME_ENTERED_BY, cause);
    }
    final String value = element.getTextContent();
    enteredBySymbol = new Symbol<String>(element, value);
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
                                   "element may not have child elements, but found tag '" +
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
    final EnumRenderer renderer = new EnumRenderer(description, strValues);
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
        if (childElementName.equals(TAG_NAME_RADIX)) {
          if (radix != null) {
            throwDuplicateException(childElement, TAG_NAME_RADIX);
          }
          radix = parseInt(childElement, childElement.getTextContent());
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
          displayMinWidth =
            parseByte(childElement, childElement.getTextContent());
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
      new IntegerRenderer(radix != null ? radix : 10,
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
    Integer bitStringSize = null;
    final NodeList childNodes = element.getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode instanceof Element) {
        final Element childElement = (Element)childNode;
        final String childElementName = childElement.getTagName();
        if (childElementName.equals(TAG_NAME_BIT_STRING_SIZE)) {
          if (bitStringSize != null) {
            throwDuplicateException(childElement, TAG_NAME_BIT_STRING_SIZE);
          }
          bitStringSize = parseInt(childElement, childElement.getTextContent());
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
      new BitMaskRenderer(bitStringSize != null ? bitStringSize : 8);
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
                                     "can not resolve range reference '" +
                                     rangeId + "'");
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
    scope.enterType(symbol, identifier);
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
    Long displayOffset = null;
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
          lowerBound = parseLong(childElement, childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_UPPER_BOUND)) {
          if (upperBound != null) {
            throwDuplicateException(childElement, TAG_NAME_UPPER_BOUND);
          }
          upperBound = parseLong(childElement, childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_DISPLAY_OFFSET)) {
          if (displayOffset != null) {
            throwDuplicateException(childElement, TAG_NAME_DISPLAY_OFFSET);
          }
          displayOffset =
            parseLong(childElement, childElement.getTextContent());
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
                                     "can not resolve renderer reference '" +
                                     rendererId + "'");
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
                                     "can not resolve renderer reference '" +
                                     rendererId + "'");
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
                                     "can not resolve renderer reference '" +
                                     rendererId + "'");
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
    final long lb = lowerBound != null ? lowerBound : 0;
    final long ub =
      upperBound != null ?
      upperBound :
      lb + renderer.getSize() - 1;
    final long dspOffs = displayOffset != null ? displayOffset : 0;
    final ValueRange range =
      new ValueRange(description, lb, ub, dspOffs, renderer);
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

  private long parseAddress(final Element location,
                            final String text) throws ParseException
  {
    try {
      return addressRepresentationSymbol.getValue().parse(text);
    } catch (final NumberFormatException e) {
      throw new ParseException(location, "failed parsing display address");
    }
  }

  private long parseAddress(final Element element) throws ParseException
  {
    if (addressRepresentationSymbol == null) {
      throw new ParseException(element, "need address representation be defined prior to parsing display address");
    }
    return parseAddress(element, element.getTextContent());
  }

  private Identifier parseFolder(final Element element, final boolean requireId)
    throws ParseException
  {
    final Identifier identifier = parseId(element, requireId);
    if (isTypeRef(element)) {
      return identifier;
    }

    final Folder folder = new Folder();
    final Symbol<Folder> symbol = new Symbol<Folder>(element, folder);
    scope.enterFolder(identifier, symbol);

    scope.enterScope();

    final String unparsedLabel;
    if (element.hasAttribute(ATTR_NAME_LABEL)) {
      unparsedLabel = element.getAttribute(ATTR_NAME_LABEL);
    } else {
      unparsedLabel = null;
    }

    final Integer multiplicity;
    if (element.hasAttribute(ATTR_NAME_MULTIPLICITY)) {
      multiplicity =
        parseInt(element, element.getAttribute(ATTR_NAME_MULTIPLICITY));
      if (multiplicity < 1) {
        throw new ParseException(element, "non-positive folder multiplicity");
      }
    } else {
      multiplicity = 1;
    }

    final Identifier indexVarId;
    if (element.hasAttribute(ATTR_NAME_INDEX_VAR)) {
      indexVarId =
        Identifier.fromString(element.getAttribute(ATTR_NAME_INDEX_VAR));
    } else {
      indexVarId = Identifier.createAnonymousIdentifier();
    }
    final IndexVariable indexVar = new IndexVariable(indexVarId);
    final Symbol<IndexVariable> indexVarSymbol =
      new Symbol<IndexVariable>(element, indexVar);
    scope.enterIndexVariable(indexVarSymbol);

    final StringExpression label =
      StringExpression.parse(element, unparsedLabel, scope);

    String description = null;
    Long bitAddress = null;
    Long address = null;
    Long bitAddressIncrement = null;
    Long addressIncrement = null;
    Identifier relativeToId = null;
    final List<ParserNode> contents = new ArrayList<ParserNode>();
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

        } else if (childElementName.equals(TAG_NAME_BIT_ADDRESS)) {
          if (address != null) {
            throwDuplicateException(childElement,
                                    TAG_NAME_ADDRESS, TAG_NAME_BIT_ADDRESS);
          }
          if (bitAddress != null) {
            throwDuplicateException(childElement, TAG_NAME_BIT_ADDRESS);
          }
          bitAddress = parseLong(childElement, childElement.getTextContent());
          if (bitAddress < 0) {
            throw new ParseException(childElement, "negative bit address");
          }
          if (childElement.hasAttribute(ATTR_NAME_RELATIVE_TO)) {
            relativeToId =
              Identifier.fromString(childElement.getAttribute(ATTR_NAME_RELATIVE_TO));
          }
        } else if (childElementName.equals(TAG_NAME_ADDRESS)) {
          if (bitAddress != null) {
            throwDuplicateException(childElement,
                                    TAG_NAME_BIT_ADDRESS, TAG_NAME_ADDRESS);
          }
          if (address != null) {
            throwDuplicateException(childElement, TAG_NAME_ADDRESS);
          }
          address = parseAddress(childElement);
          if (address < 0) {
            throw new ParseException(childElement, "negative address");
          }
          if (childElement.hasAttribute(ATTR_NAME_RELATIVE_TO)) {
            relativeToId =
              Identifier.fromString(childElement.getAttribute(ATTR_NAME_RELATIVE_TO));
          }
        } else if (childElementName.equals(TAG_NAME_BIT_ADDRESS_INCREMENT)) {
          if (addressIncrement != null) {
            throwDuplicateException(childElement,
                                    TAG_NAME_ADDRESS_INCREMENT,
                                    TAG_NAME_BIT_ADDRESS_INCREMENT);
          }
          if (bitAddressIncrement != null) {
            throwDuplicateException(childElement,
                                    TAG_NAME_BIT_ADDRESS_INCREMENT);
          }
          bitAddressIncrement =
            parseLong(childElement, childElement.getTextContent());
          if (bitAddressIncrement < 1) {
            throw new ParseException(childElement,
                                     "non-positive bit address increment");
          }
        } else if (childElementName.equals(TAG_NAME_ADDRESS_INCREMENT)) {
          if (bitAddressIncrement != null) {
            throwDuplicateException(childElement,
                                    TAG_NAME_BIT_ADDRESS_INCREMENT,
                                    TAG_NAME_ADDRESS_INCREMENT);
          }
          if (addressIncrement != null) {
            throwDuplicateException(childElement, TAG_NAME_ADDRESS_INCREMENT);
          }
          addressIncrement = parseAddress(childElement);
          if (addressIncrement < 1) {
            throw new ParseException(childElement,
                                     "non-positive address increment");
          }
        } else if (childElementName.equals(TAG_NAME_FOLDER)) {
          final Identifier folderId = parseFolder(childElement, false);
          final Symbol<? extends Folder> folderSymbol =
            scope.lookupFolder(folderId);
          if (folderSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve folder reference '" +
                                     folderId + "'");
          }
          if (folderId == Identifier.ROOT_ID) {
            throw new ParseException(element,
                                     "can not put root beneath other node");
          }
          contents.add(folderSymbol.getValue());
        } else if (childElementName.equals(TAG_NAME_DATA)) {
          final Identifier dataId = parseData(childElement, false);
          final Symbol<? extends Data> dataSymbol =
            dataSymbols.lookupSymbol(dataId);
          if (dataSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve data reference '" +
                                     dataId + "'");
          }
          contents.add(dataSymbol.getValue());
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

    final long desiredAddress =
      bitAddress != null ? bitAddress : (address != null ? address : -1);
    final long desiredAddressIncrement =
      bitAddressIncrement != null ?
      bitAddressIncrement :
      (addressIncrement != null ?
       addressIncrement : -1);
    if ((desiredAddressIncrement != -1) && (desiredAddress == -1)) {
      throw new ParseException(element, "can not declare address increment " +
                               "if no address is declared");
    }

    if ((multiplicity > 1) && (desiredAddress > -1)) {
      if (addressIncrement == null) {
        throw new ParseException(element,
                                 "since this folder's multiplicity is " +
                                 "greater than 1 and a desired address has " +
                                 "been explicitly specified, you also need " +
                                 "to explicitly specify an address increment");
      }
    }

    final Folder addressBase;
    if (relativeToId != null) {
      final Symbol<? extends Folder> folderSymbol =
        scope.lookupFolder(relativeToId);
      if (folderSymbol == null) {
        throw new ParseException(element,
                                 "can not resolve folder reference '" +
                                 relativeToId + "'");
      }
      addressBase = folderSymbol.getValue();
    } else {
      addressBase = null;
    }

    folder.setDescription(description);
    folder.setLabel(label);
    folder.setMultiplicity(multiplicity);
    folder.setIndexVariable(indexVar);
    folder.setDesiredAddress(desiredAddress);
    folder.setDesiredAddressIncrement(desiredAddressIncrement);
    folder.setAddressBase(addressBase);
    folder.addAll(contents);

    scope.leaveScope();
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
    String description = null;
    Identifier iconId = null;
    Long address = null;
    Long bitAddress = null;

    final String label;
    if (element.hasAttribute(ATTR_NAME_LABEL)) {
      label = element.getAttribute(ATTR_NAME_LABEL);
    } else {
      label = null;
    }

    SparseType type = null;
    Byte bitSize = null;
    Integer defaultValue = null;

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
            scope.lookupType(typeId);
          if (typeSymbol == null) {
            throw new ParseException(element,
                                     "can not resolve type reference '" +
                                     typeId + "'");
          }
          type = typeSymbol.getValue();
        } else if (childElementName.equals(TAG_NAME_ICON)) {
          if (iconId != null) {
            throwDuplicateException(childElement, TAG_NAME_ICON);
          }
          iconId = Identifier.fromString(childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_BIT_SIZE)) {
          if (bitSize != null) {
            throwDuplicateException(childElement, TAG_NAME_BIT_SIZE);
          }
          bitSize = parseByte(childElement, childElement.getTextContent());
          if ((bitSize < 0) || (bitSize > 32)) {
            throw new ParseException("invalid bit size; valid range: 0..32");
          }
        } else if (childElementName.equals(TAG_NAME_DEFAULT_VALUE)) {
          if (defaultValue != null) {
            throwDuplicateException(childElement, TAG_NAME_DEFAULT_VALUE);
          }
          defaultValue = parseInt(childElement, childElement.getTextContent());
        } else if (childElementName.equals(TAG_NAME_ADDRESS)) {
          if (bitAddress != null) {
            throwDuplicateException(childElement,
                                    TAG_NAME_BIT_ADDRESS, TAG_NAME_ADDRESS);
          }
          if (address != null) {
            throwDuplicateException(childElement, TAG_NAME_ADDRESS);
          }
          address = parseAddress(childElement);
        } else if (childElementName.equals(TAG_NAME_BIT_ADDRESS)) {
          if (address != null) {
            throwDuplicateException(childElement,
                                    TAG_NAME_ADDRESS, TAG_NAME_BIT_ADDRESS);
          }
          if (bitAddress != null) {
            throwDuplicateException(childElement, TAG_NAME_BIT_ADDRESS);
          }
          bitAddress = parseLong(childElement, childElement.getTextContent());
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
    final long desiredAddress =
      bitAddress != null ? bitAddress : (address != null ? address : -1);
    final Value value =
      new ValueImpl(iconId != null ? iconId.toString() : null,
                    type, description, label,
                    defaultValue != null ? defaultValue : 0);
    final byte requiredBitSize = type.getRequiredBitSize();
    if (bitSize == null) {
      if (defaultDataSizeSymbol != null) {
        final DefaultDataSize defaultDataSize =
          defaultDataSizeSymbol.getValue();
        bitSize = defaultDataSize.getBitSize(requiredBitSize);
      } else {
        bitSize = requiredBitSize;
      }
    } else if (bitSize < requiredBitSize) {
      throw new ParseException(element, "declared bit size (" + bitSize +
                               ") must be greater than or equal to " +
                               "required bit size (" + requiredBitSize + ")");
    }
    value.setBitSize(bitSize);
    final Data data = new Data(value, desiredAddress);
    final Symbol<Data> dataSymbol = new Symbol<Data>(element, data);
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
