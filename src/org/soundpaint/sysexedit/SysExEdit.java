/*
 * @(#)SysExEdit.java 1.00 98/01/31
 *
 * Copyright (C) 1998, 2018 Jürgen Reuter
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

package org.soundpaint.sysexedit;

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

import org.soundpaint.sysexedit.gui.EditorFrame;
import org.soundpaint.sysexedit.gui.FramesManager;

/**
 * This is the main entry of the SysExEdit utility.
 *
 * @version 1.01, 18/02/18
 */
public class SysExEdit extends Applet implements FramesManager
{
  private static final long serialVersionUID = -4898787358405623599L;

  private final static
  String VERSION = "SysExEdit/1.0alpha1";

  private final static
  String COPYRIGHT = "Copyright © 1998, 2018 Jürgen Reuter";

  private final static String APPL_INFO =
  VERSION + "\n" +
  COPYRIGHT + "\n" +
  "\n" +
  "SysExEdit comes with ABSOLUTELY NO WARRANTY;\n" +
  "for details click on the menu item “License…” in\n" +
  "the “Help” menu of the main window to view the full\n" +
  "license.\n" +
  "\n" +
  "This is free software, and you are welcome to\n" +
  "redistribute it under certain conditions; view the\n" +
  "full license for details.\n";

  /*
   * command line arguments
   */
  private static int argNoGreeting = 0; /*
                                         * 0=unread;
                                         * 1=argName read;
                                         * 2=argValue read
                                         */
  private static int argHelp = 0;       /* dto. */
  private static int argVersion = 0;    /* dto. */
  private static int argCopyright = 0;  /* dto. */

  private final Preferences preferences;
  private final Hashtable<Frame, Integer> frames; // frames and their unique IDs
  private boolean inAnApplet = false; // true, if run as an applet

  private static void print_version(final PrintWriter out)
  {
    out.println(VERSION);
    out.println(COPYRIGHT);
    out.println();
    if (out.checkError()) System.exit(-2);
  }

  private static void print_copyright(final PrintWriter out)
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

  private static void print_greeting(final PrintWriter out)
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

  private static void print_help(final PrintWriter out)
  {
    print_version(out);
    out.println("Usage: org.soundpaint.sysexedit.SysExEdit [-nogreeting] [-help] [-version] ");
    out.println("  [-copyright] [file]");
    out.println();
    out.println("Exit status codes:");
    out.println("Exit  1 : ok");
    out.println("Exit -1 : ordinary exit through help");
    out.println("Exit -2 : error");
    if (out.checkError()) System.exit(-2);
  }

  private static void print_multiple_files(final PrintWriter out)
  {
    out.println("There were multiple files specified.");
    print_help(out);
    if (out.checkError()) System.exit(-2);
  }

  /**
   * Returns the argument counter's last value that points to the first
   * file argument.
   */
  private static int parse_argv(final String argv[])
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
  public static void main(final String[] argv)
  {
    final int argc = parse_argv(argv);
    final PrintWriter out = new PrintWriter(System.out);
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
        final String filepath;
        switch (argv.length - argc)
          {
          case 0:
            filepath  = null;
            break;
          case 1:
            filepath = argv[argc];
            break;
          default:
            filepath  = null;
            print_multiple_files(out);
            System.exit(-2);
          }
        final SysExEdit sysExEdit = new SysExEdit();
        sysExEdit.createEditorFrame(filepath);
      }
  }

  public SysExEdit()
  {
    preferences = Preferences.getDefault();
    frames = new Hashtable<Frame, Integer>();
  }

  private static UIManager.LookAndFeelInfo[] lookAndFeelInfo;

  static
  {
    lookAndFeelInfo = UIManager.getInstalledLookAndFeels();
  }

  /**
   * Executes SwingUtilities.updateComponentTreeUI on all registered frames.
   */
  private void updateUI()
  {
    final Enumeration<Frame> enumeration = frames.keys();
    while (enumeration.hasMoreElements()) {
      final Frame frame = enumeration.nextElement();
      SwingUtilities.updateComponentTreeUI(frame);
      frame.pack();
    }
  }

  /**
   * Sets the Swing Look and Feel for all registered frames.
   */
  public void setLookAndFeel(final String name) throws Exception
  {
    String className = null;
    for (int i = 0; i < lookAndFeelInfo.length; i++)
      if (name.equals(lookAndFeelInfo[i].getName()))
        {
          className = lookAndFeelInfo[i].getClassName();
          break;
        }
    UIManager.setLookAndFeel(className);
    updateUI();
  }

  private boolean confirmTermsOfUse()
  {
    System.out.println("[loading & initializing swing...]");
    System.out.flush();
    return
      (JOptionPane.showConfirmDialog(null, APPL_INFO, "Terms of Use",
                                     JOptionPane.OK_CANCEL_OPTION) ==
       JOptionPane.OK_OPTION);
  }

  /**
   * Starts an EditorFrame thread with the specified device model.
   * @param filepath The device model filepath to use in this thread.
   *    If null, the user will be prompted to select a device model from
   *    a list.
   */
  public void createEditorFrame(final String filepath)
  {
    final boolean termsOfUseConfirmed =
      !frames.isEmpty() || confirmTermsOfUse();
    if (termsOfUseConfirmed) {
      loadIcons();
      new Thread(new EditorFrame(filepath, null, this)).start();
    } else {
      exitOnNoMoreFrame();
    }
  }

  private class ButtonListener implements ActionListener
  {
    public void actionPerformed(final ActionEvent e)
    {
      createEditorFrame(null);
    }
  }

  private boolean isResourceDir(final String path)
  {
    final String myPath =
      path + File.separatorChar +
      getClass().getName().replace('.', File.separatorChar) + ".class";
    final File file = new File(myPath);
    return (file.exists() && file.canRead());
    // [PENDING: we may also want to check is the file really is a class file]
  }

  /**
   * Called by the browser or applet viewer to inform
   * this applet that it should start its execution. It is called after
   * the init method and each time the applet is revisited 
   * in a Web page.
   */
  public void start()
  {
    final Button button = new Button("Start SysExEdit");
    button.addActionListener(new ButtonListener());
    add(button);
  }

  /**
   * Called by the browser or applet viewer to inform
   * this applet that it has been loaded into the system. It is always
   * called before the first time that the <code>start</code> method is
   * called.
   */
  public void init()
  {
    inAnApplet = true;
  }

  /**
   * Returns information about this applet.
   *
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

  private int maxId = 0;

  /**
   * Returns the version ID of this application.
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
  public int addFrame(final Frame frame)
  {
    synchronized(frames)
      {
        final int id = ++maxId;
        frames.put(frame, id);
        return id;
      }
  }

  private void exitOnNoMoreFrame()
  {
    if (frames.isEmpty() && !inAnApplet) {
      System.out.println("[done]"); System.out.flush();
      System.exit(1);
    }
  }

  /**
   * Removes a frame from the set of registered frames. If not in an
   * applet, calls System.exit(1), if no more frames are registered.
   * @param frame The frame to be removed.
   */
  public void removeFrame(final Frame frame)
  {
    frames.remove(frame);
    exitOnNoMoreFrame();
  }

  /**
   * Gets the unique ID of the specified frame.
   * @param frame The frame.
   * @return The unique ID or -1, if the frame is not registered.
   */
  public int getId(final Frame frame)
  {
    final Integer id = frames.get(frame);
    return (id != null) ? id : -1;
  }

  /**
   * Closes all frames and exits the application.
   */
  public void tryExit()
  {
    final Enumeration<Frame> enumeration = frames.keys();
    while (enumeration.hasMoreElements())
      ((EditorFrame)enumeration.nextElement()).tryClose();
  }

  /**
   * Loads all of the internal icons. This should be called only once
   * at startup, e.g. when this class is instantiated for the first time.
   */
  private static void loadIcons()
  {
    System.out.print("[loading icon properties");
    System.out.flush();
    final Properties icons = new Properties();
    try
      {
        final URL resource = SysExEdit.class.getResource("/icons.properties");
        if (resource == null)
          {
            throw new NullPointerException("icons.properties resource not found");
          }
        icons.load(resource.openConnection().getInputStream());
      }
    catch (final Exception e)
      {
        System.err.println("[WARNING: failed loading icons list: " + e + "]");
        System.err.flush();
        return;
      }
    final UIDefaults uiDefaults = UIManager.getDefaults();
    for (final String key : icons.stringPropertyNames())
      {
        final String value = icons.getProperty(key);
        System.out.print(".");
        System.out.flush();
        final URL iconLocation = SysExEdit.class.getResource(value);
        try
          {
            final ImageIcon imageIcon = new ImageIcon(iconLocation);
            final int status = imageIcon.getImageLoadStatus();
            if (status == MediaTracker.COMPLETE)
              uiDefaults.put(key, imageIcon);
            else
              throw new IOException("bad image load status: " + status);
          }
        catch (final Exception e)
          {
            System.err.println("[WARNING: failed loading " +
                               iconLocation + ": " + e + "]");
            System.err.flush();
          }
      }
    System.out.println("]");
    System.out.flush();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
