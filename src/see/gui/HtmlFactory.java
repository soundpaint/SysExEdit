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
  public static HTMLDocument Error404 = errorPage("Error 404", "Not found");

  /**
   * Creates a HTML document from the specified String.
   */
  private static HTMLDocument fromString(String s)
    throws IOException, BadLocationException
  {
    StringReader reader = new StringReader(s);
    HTMLEditorKit kit = new HTMLEditorKit();
    HTMLDocument document = new HTMLDocument(kit.getStyleSheet());
    kit.read(reader, document, 0);
    return document;
  }

  /**
   * Creates a HTML error page with the specified message.
   * The message is included in the body of the error page and may
   * contain any markup that is allowed within the body of a html
   * page.
   * @param title The title of the html page. The title is put between
   *    the tags "&lt;TITLE&gt;" and "&lt;/TITLE&gt;" in the header,
   *    and at the beginning of the body between "&lt;H1&gt;" and
   *    "&lt;/H1&gt;" tags. If title equals null, the String "Error"
   *    is used.
   * @param message The message to be put into the body of the html page.
   *    If message equals null, the String "An error occurred" is used.
   * @return The HTML document or null, if no valid HTML document could
   *    be created with the given title and message.
   */
  public static HTMLDocument errorPage(String title, String message)
  {
    if (title == null)
      title = "Error";
    if (message == null)
      message = "An error occurred";
    StringBuffer html = new StringBuffer();
    html.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">\r\n");
    html.append("<!--NewPage-->\r\n");
    html.append("<HTML>\r\n");
    html.append("<HEAD>\r\n");
    html.append("<TITLE>" + title + "</TITLE>\r\n");
    html.append("</HEAD>\r\n");
    html.append("<BODY BGCOLOR=\"#ffffff\">\r\n");
    html.append("<H1>" + title + "</H1>\r\n");
    html.append("<P>\r\n");
    html.append(message + "\r\n");
    html.append("</BODY>\r\n");
    html.append("</HTML>\r\n");
    HTMLDocument document = null;
    try
      {
	document = fromString(html.toString());
      }
    catch (IOException e) {} // StringReader should not throw an IOException
    catch (BadLocationException e)
      {
	System.out.println("Warning: message: invalid html: " + e);
	System.out.flush();
      }
    return document;
  }

  /**
   * Reads a HTML document from the specified file.
   * @param filepath The path of the file to read the document from.
   * @return The HTML document as read from the specified file.
   * @exception IOException If an I/O error occurs while reading.
   */
  public static HTMLDocument readFrom(String filepath)
    throws IOException
  {
    FileReader reader;
    HTMLEditorKit kit = new HTMLEditorKit();
    HTMLDocument document = new HTMLDocument();
    try
      {
	reader = new FileReader(filepath);
      }
    catch (FileNotFoundException e)
      {
	throw new IOException("could not load html page: " + e);
      }
    try
      {
	kit.read(reader, document, 0);
      }
    catch (BadLocationException e)
      {
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
