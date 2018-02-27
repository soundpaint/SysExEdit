/*
 * @(#)HtmlFactory.java 1.00 99/25/02
 *
 * Copyright (C) 1999, 2018 Jürgen Reuter
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

package see.gui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * This class contains methods that utilize the creation of HTML documents
 * by either reading HTML from a file; this class further contains some
 * often used static HTML pages (such as the "Not Found" error page).
 */
public class HtmlFactory
{
  /**
   * Contains a HTML document with a "broken link" message.
   */
  public static final HTMLDocument Error404 =
    errorPage("Error 404", "Not found");

  /**
   * Creates a HTML document from the specified String.
   */
  private static HTMLDocument fromString(final String str)
    throws IOException, BadLocationException
  {
    final StringReader reader = new StringReader(str);
    final HTMLEditorKit kit = new HTMLEditorKit();
    final HTMLDocument document = new HTMLDocument(kit.getStyleSheet());
    kit.read(reader, document, 0);
    return document;
  }

  /**
   * Creates a HTML error page with the specified message.
   * The message is included in the body of the error page and may
   * contain any markup that is allowed within the body of a html
   * page.
   * @param customTitle The title of the html page. The title is put
   *    between the tags "&lt;TITLE&gt;" and "&lt;/TITLE&gt;" in the
   *    header, and at the beginning of the body between "&lt;H1&gt;"
   *    and "&lt;/H1&gt;" tags.  If the title equals null, the String
   *    "Error" is used.
   * @param customMessage The message to be put into the body of the
   *    html page.  If message equals null, the String "An error
   *    occurred" is used.
   * @return The HTML document or null, if no valid HTML document could
   *    be created with the given title and message.
   */
  public static HTMLDocument errorPage(final String customTitle,
                                       final String customMessage)
  {
    final String title = customTitle != null ? customTitle : "Error";
    final String message =
      customMessage != null ? customMessage : "An error occurred";
    final StringBuffer html = new StringBuffer();
    html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " +
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n");
    html.append("<!--NewPage-->\r\n");
    html.append("<html>\r\n");
    html.append("  <head>\r\n");
    html.append("    <title>" + title + "</title>\r\n");
    html.append("  </head>\r\n");
    html.append("  <body>\r\n");
    html.append("    <h1>" + title + "</h1>\r\n");
    html.append("    <p>" + message + "</p>\r\n");
    html.append("  </body>\r\n");
    html.append("</html>\r\n");
    final HTMLDocument document;
    try {
      return fromString(html.toString());
    } catch (final IOException e) {
      // StringReader should not throw an IOException
      return null;
    } catch (final BadLocationException e) {
      System.out.println("Warning: message: invalid html: " + e);
      System.out.flush();
      return null;
    }
  }

  /**
   * Reads a HTML document from the specified file.
   * @param filepath The path of the file to read the document from.
   * @return The HTML document as read from the specified file.
   * @exception IOException If an I/O error occurs while reading.
   */
  public static HTMLDocument readFrom(final String filepath)
    throws IOException
  {
    final FileReader reader;
    final HTMLEditorKit kit = new HTMLEditorKit();
    final HTMLDocument document = new HTMLDocument();
    try {
      reader = new FileReader(filepath);
    } catch (final FileNotFoundException e) {
      throw new IOException("could not load html page: " + e);
    }
    try {
      kit.read(reader, document, 0);
    } catch (final BadLocationException e) {
      throw new IOException("could not read html page: " + e);
    }
    return document;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
