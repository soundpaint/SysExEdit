/*
 * @(#)SysExEdit.java 1.00 98/01/31
 *
 * Copyright (C) 1998 Juergen Reuter
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

// $Source:$
// $Revision:$
// $Aliases:$
// $Author:$
// $Date:$
// $State:$

package see;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Frame;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIDefaults;

import see.gui.EditorFrame;
import see.gui.FramesManager;

/**
 * This is the main entry of the SysExEdit utility.
 *
 * @version 1.00, 01/31/98
 * @author  Juergen Reuter
 * @since   Jsound1.0alpha1
 */
public class SysExEdit extends Applet implements FramesManager
{
  private final static
  String VERSION = "SysExEdit/1.0alpha1";

  private final static
  String COPYRIGHT = "Copyright (C) 1998 J\u00fcrgen Reuter";

  private final static String APPL_INFO =
  VERSION + "\n" +
  COPYRIGHT + "\n" +
  "\n" +
  "SysExEdit comes with ABSOLUTELY NO WARRANTY;\n" +
  "for details click on the menu item \"License...\" in\n" +
  "the \"Help\" menu of the main window to view the full\n" +
  "license.\n" +
  "\n" +
  "This is free software, and you are welcome to\n" +
  "redistribute it under certain conditions; view the\n" +
  "full license for details.\n";

  /*
   * command line arguments
   */
  private static int argNoGreeting = 0;	     /*
					      * 0=unread;
					      * 1=argName read;
					      * 2=argValue read
					      */
  private static int argHelp = 0;	     /* dto. */
  private static int argVersion = 0;	     /* dto. */
  private static int argCopyright = 0;	     /* dto. */

  private static URL resource = null; // a URL that references this class

  private Hashtable frames; // frames and their unique IDs
  private boolean inAnApplet = false; // true, if run as an applet
  private Class[] classes = null; // array of map def classes

  private static void print_version(PrintWriter out)
  {
    out.println(VERSION);
    out.println(COPYRIGHT);
    out.println();
    if (out.checkError()) System.exit(-2);
  }

  private static void print_copyright(PrintWriter out)
  {
    print_version(out);
    out.println("This program is free software; you can redistribute it");
    out.println("and/or modify it under the terms of the GNU General Public");
    out.println("License as published by the Free Software Foundation;");
    out.println("either version 2 of the License, or (at your option) any");
    out.println("later version.");
    out.println();
    out.println("This program is distributed in the hope that it will be");
    out.println("useful, but WITHOUT ANY WARRANTY; without even the implied");
    out.println("warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR");
    out.println("PURPOSE.  See the GNU General Public License for more");
    out.println("details.");
    out.println();
    out.println("You should have received a copy of the GNU General Public");
    out.println("License along with this program; if not, write to the Free");
    out.println("Software Foundation, Inc., 59 Temple Place - Suite 330,");
    out.println("Boston, MA  02111-1307, USA.");
    out.println();
    if (out.checkError()) System.exit(-2);
  }

  private static void print_greeting(PrintWriter out)
  {
    print_version(out);
    out.println("The SysExEdit utility comes with ABSOLUTELY NO WARRANTY;");
    out.println("for details run this program with option `-copyright'.");
    out.println("This is free software, and you are welcome to redistribute");
    out.println("it under certain conditions; run with option `-copyright'");
    out.println("for details.");
    out.println("Run with option `-nogreeting' to suppress this message.");
    out.println();
    if (out.checkError()) System.exit(-2);
  }

  private static void print_help(PrintWriter out)
  {
    print_version(out);
    out.println("Usage: see.SysExEdit [-nogreeting] [-help] [-version] ");
    out.println("  [-copyright] [file]");
    out.println();
    out.println("Exit status codes:");
    out.println("Exit  1 : ok");
    out.println("Exit -1 : ordinary exit through help");
    out.println("Exit -2 : error");
    if (out.checkError()) System.exit(-2);
  }

  private static void print_multiple_files(PrintWriter out)
  {
    out.println("There were multiple files specified.");
    print_help(out);
    if (out.checkError()) System.exit(-2);
  }

  /**
   * Returns the argument counter's last value that points to the first
   * file argument.
   */
  private static int parse_argv(String argv[])
  {
    int argc;
    for (argc = 0; argc < argv.length; argc++) {
      if ((argv[argc].equals("-nogreeting")) && (argNoGreeting == 0)) {
        argNoGreeting++;
      } else if ((argv[argc].equals("-help")) && (argHelp == 0)) {
        argHelp++;
      } else if ((argv[argc].equals("-?")) && (argHelp == 0)) {
        argHelp++;
      } else if ((argv[argc].equals("-version")) && (argVersion == 0)) {
        argVersion++;
      } else if ((argv[argc].equals("-copyright")) && (argCopyright == 0)) {
        argCopyright++;
      } else { /* here starts the list of file names => we are done */
        break;
      }
    }
    return argc;
  }

  /**
   * Envokes the SysExEdit utility using the specified arguments as
   * options as if they were typed on the command line.
   *
   * @param argv The argument list as passed from the command line.
   *    Use option `-help' for details on options available.
   */
  public static void main(String[] argv)
  {
    int argc = parse_argv(argv);
    PrintWriter out = new PrintWriter(System.out);
    if (argHelp != 0)
      {
	print_help(out);
	System.exit(-1);
      }
    else if (argVersion != 0)
      {
	print_version(out);
	System.exit(1);
      }
    else if (argCopyright != 0)
      {
	print_copyright(out);
	System.exit(1);
      }
    else
      {
	if (argNoGreeting == 0)
	  print_greeting(out);
	String filepath = null;
	switch (argv.length - argc)
	  {
	  case 0:
	    break;
	  case 1:
	    filepath = argv[argc];
	    break;
	  default:
	    print_multiple_files(out);
	    System.exit(-2);
	  }
	SysExEdit sysExEdit = new SysExEdit();
	sysExEdit.createEditorFrame(filepath);
      }
  }

  /**
   * Starts an EditorFrame thread with the specified device model.
   * @param filepath The device model filepath to use in this thread.
   *    If null, the user will be prompted to select a device model from
   *    a list.
   */
  public void createEditorFrame(String filepath)
  {
    frames = new Hashtable();
    System.out.println("[loading & initializing swing...]");
    System.out.flush();
    if (JOptionPane.showConfirmDialog(null, APPL_INFO, "Application Info",
				      JOptionPane.OK_CANCEL_OPTION) ==
	JOptionPane.OK_OPTION)
      {
	evaluateResource();
	new Thread(new EditorFrame(filepath, null, this)).start();
      }
    else
      removeFrame(new Frame());
  }

  private class ButtonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      createEditorFrame(null);
    }
  }

  private boolean isResourceDir(String path)
  {
    String myPath =
      path + File.separatorChar +
      getClass().getName().replace('.', File.separatorChar) + ".class";
    File file = new File(myPath);
    return (file.exists() && file.canRead());
    // [PENDING: we may also want to check is the file really is a class file]
  }

  /*
   * This method evaluates a static resource URL that references this
   * class, and loads all static icons needed for this application, if
   * not already done.
   */
  private void evaluateResource()
  {
    if (resource != null)
      return; // already done
    /*
    ClassLoader loader = getClass().getClassLoader();
    if (loader == null)
      resource = ClassLoader.getSystemResource("");
    else
      resource = loader.getResource("");
      */
    // The above lines do not work properly because of a bug in jdk 1.1.x
    // when using a "systemresource" protocol URL.
    // Hence, we determine resource manually, permitting only "file" and
    // "http" as valid protocols for the URL.
    if (inAnApplet)
      resource = getCodeBase(); // returns a file or http URL
    else // create a file URL manually
      {
	String classpath = System.getProperty("java.class.path");
	String path = null;
	int start = 0;
	int end = classpath.indexOf(File.pathSeparatorChar, start);
	while (end != -1)
	  {
	    path = classpath.substring(start, end);
	    if (isResourceDir(path))
	      break; // path found
	    start = end + 1;
	    end = classpath.indexOf(File.pathSeparatorChar, start);
	  }
	if (end == -1)
	  {
	    path = classpath.substring(start);
	    if (isResourceDir(path))
	      ; // path found
	    else
	      path = null;
	  }
	if (path != null)
	  {
	    try
	      {
		resource =
		  new URL("file:" + new File(path).getCanonicalPath().
			  replace(File.separatorChar, '/') + "/");
	      }
	    catch (Exception e)
	      {
		resource = null;
	      }
	  }
	else
	  resource = null;
      }
    System.out.println("[resource=" + resource + "]");
    System.out.flush();
    if (resource != null)
      loadIcons(resource);
  }

  /**
   * Called by the browser or applet viewer to inform
   * this applet that it should start its execution. It is called after
   * the init method and each time the applet is revisited 
   * in a Web page.
   */
  public void start()
  {
    Button button = new Button("Start SysExEdit");
    button.addActionListener(new ButtonListener());
    add(button);
  }

  /**
   * Called by the browser or applet viewer to inform
   * this applet that it has been loaded into the system. It is always
   * called before the first time that the <code>start</code> method is
   * called.
   * <P>
   * This method evaluates a static resource URL that references this
   * class, and loads all static icons needed for this application, if
   * not already done.
   * @see #getResource
   */
  public void init()
  {
    inAnApplet = true;
  }

  /**
   * Returns information about this applet.<BR>
   * Specifically, it returns a String containing information 
   * about the author, version, and copyright of the applet. 
   *
   * @return A String containing information about the author, version,
   *    and copyright of the applet.
   */
  public String getAppletInfo()
  {
    return APPL_INFO;
  }

  private int maxID = 0;

  /**
   * Returns the version id of this application.
   */
  public String getVersion() { return VERSION; }

  /**
   * Returns the copyright mark of this application.
   */
  public String getCopyright() { return COPYRIGHT; }

  /**
   * Registers a new frame.
   * @param frame The frame to be registered.
   * @return A unique ID that is assigned to the frame.
   */
  public int addFrame(Frame frame)
  {
    synchronized(frames)
      {
	int id = ++maxID;
	frames.put(frame, new Integer(id));
	return id;
      }
  }

  /**
   * Removes a frame from the set of registered frames. If not in an
   * applet, calls System.exit(1), if no more frames are registered.
   * @param frame The frame to be removed.
   */
  public void removeFrame(Frame frame)
  {
    frames.remove(frame);
    if (frames.isEmpty() && !inAnApplet)
      {
	System.out.println("[done]"); System.out.flush();
	System.exit(1);
      }
  }

  /**
   * Gets the unique ID of the specified frame.
   * @param frame The frame.
   * @return The unique ID or -1, if the frame is not registered.
   */
  public int getID(Frame frame)
  {
    Integer i = (Integer)frames.get(frame);
    return (i != null) ? i.intValue() : -1;
  }

  /**
   * Executes SwingUtilities.updateComponentTreeUI on all registered frames.
   */
  public void updateUI()
  {
    Enumeration enum = frames.keys();
    while (enum.hasMoreElements())
      {
	SwingUtilities.updateComponentTreeUI((Frame)enum.nextElement());
      }
  }

  /**
   * Closes all frames and exits the application.
   */
  public void exitAll()
  {
    Enumeration enum = frames.keys();
    while (enum.hasMoreElements())
      ((EditorFrame)enum.nextElement()).exit();
  }

  /**
   * Loads all of the internal icons. This should be called only once
   * at startup, e.g. when this class is instantiated for the first time.
   * @param resource A URL that is used to locate icons. Typically, the
   *    URL references the directory that contains all class files of this
   *    application.
   */
  private static void loadIcons(URL resource)
  {
    System.out.println("[loading icon properties...]");
    System.out.flush();
    Properties icons = new Properties();
    try
      {
	icons.load(new URL(resource, "icons.properties").
		   openConnection().getInputStream());
      }
    catch (IOException e)
      {
	System.out.println("WARNING: failed loading icons: " + e);
	System.out.flush();
	return;
      }
    UIDefaults uiDefaults = UIManager.getDefaults();
    Enumeration iconsEnumeration = icons.propertyNames();
    System.out.print("[loading icons");
    System.out.flush();
    while (iconsEnumeration.hasMoreElements())
      {
	URL icon_location = null;
	try
	  {
	    String key = (String)iconsEnumeration.nextElement();
	    String value = icons.getProperty(key);
	    System.out.print(".");
	    System.out.flush();
	    icon_location = new URL(resource, value);
	    ImageIcon imageIcon = new ImageIcon(icon_location);
	    int status = imageIcon.getImageLoadStatus();
	    if (status == MediaTracker.COMPLETE)
	      uiDefaults.put(key, imageIcon);
	    else
	      throw new IOException("bad image load status: " + status);
	  }
	catch (Exception e)
	  {
	    System.out.println();
	    System.out.println("WARNING: failed loading " +
			       icon_location + ":");
	    System.out.println("" + e);
	    System.out.flush();
	  }
      }
    System.out.println("]");
    System.out.flush();
  }

  /**
   * Returns the root directory of this application as a resource URL.
   */
  public URL getResource()
  {
    return resource;
  }

  /**
   * Returns an array of all available map def classes.
   */
  public Class[] getMapDefClasses()
  {
    if (classes == null)
      {
	Vector classesVector = new Vector();
	Class _class;
	try
	  {
	    classesVector.addElement(Class.forName("see.devices.DB50XG"));
	    //classesVector.addElement(Class.forName("see.devices.U20"));
	  }
	catch (Exception e)
	  {
	    System.out.println("WARNING: " + e);
	    e.printStackTrace(System.out);
	    System.out.flush();
	  }
	classes = new Class[classesVector.size()];
	classesVector.copyInto(classes);
      }
    return classes;
  }
}
