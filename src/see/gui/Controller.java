/*
 * @(#)Controller.java 1.00 18/03/28
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

package see.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import see.SysExEdit;

public class Controller
{
  private static final String MSG_DISCARD_UNSAVED_DATA =
    "Data is modified and will be lost.  Continue anyway?";

  private final FramesManager manager;
  private final Editor editor;
  private final DocumentMetaData documentMetaData;
  private final Frame frame;
  private final MidiOptionsDialog midiOptionsDialog;

  private Controller()
  {
    throw new UnsupportedOperationException();
  }

  public Controller(final FramesManager manager,
                    final Editor editor,
                    final DocumentMetaData documentMetaData,
                    final Frame frame)
  {
    this.manager = manager;
    this.editor = editor;
    this.documentMetaData = documentMetaData;
    this.frame = frame;
    midiOptionsDialog =
      new MidiOptionsDialog(frame, documentMetaData);
  }

  private abstract class GuardedItemListener implements ItemListener
  {
    public void itemStateChanged(final ItemEvent event)
    {
      try {
        final boolean selected = event.getStateChange() == ItemEvent.SELECTED;
        unguardedItemStateChanged(event, selected);
      } catch (final Throwable t) {
        ExceptionPanel.showException(frame, t, true);
      }
    }

    abstract void unguardedItemStateChanged(final ItemEvent event,
                                            final boolean selected)
      throws Throwable;
  }

  private abstract class GuardedActionListener implements ActionListener
  {
    public void actionPerformed(final ActionEvent event)
    {
      try {
        unguardedActionPerformed(event);
      } catch (final Throwable t) {
        ExceptionPanel.showException(frame, t, true);
      }
    }

    abstract void unguardedActionPerformed(final ActionEvent event)
      throws Throwable;
  }

  private abstract class GuardedTreeModelListener implements TreeModelListener
  {
    public void treeNodesChanged(final TreeModelEvent event)
    {
      try {
        unguardedTreeNodesChanged(event);
      } catch (final Throwable t) {
        ExceptionPanel.showException(frame, t, true);
      }
    }

    abstract void unguardedTreeNodesChanged(final TreeModelEvent event)
      throws Throwable;

    public void treeNodesInserted(final TreeModelEvent event)
    {
      try {
        unguardedTreeNodesInserted(event);
      } catch (final Throwable t) {
        ExceptionPanel.showException(frame, t, true);
      }
    }

    abstract void unguardedTreeNodesInserted(final TreeModelEvent event)
      throws Throwable;

    public void treeNodesRemoved(final TreeModelEvent event)
    {
      try {
        unguardedTreeNodesRemoved(event);
      } catch (final Throwable t) {
        ExceptionPanel.showException(frame, t, true);
      }
    }

    abstract void unguardedTreeNodesRemoved(final TreeModelEvent event)
      throws Throwable;

    public void treeStructureChanged(final TreeModelEvent event)
    {
      try {
        unguardedTreeStructureChanged(event);
      } catch (final Throwable t) {
        ExceptionPanel.showException(frame, t, true);
      }
    }

    abstract void unguardedTreeStructureChanged(final TreeModelEvent event)
      throws Throwable;
  }

  private final ActionListener newListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        manager.createEditorFrame(null);
      }
    };

  private static JFileChooser newChooser(final File defaultFile)
  {
    final JFileChooser chooser = new JFileChooser();
    final ExtensionFileFilter filter =
      new ExtensionFileFilter(new String[] {"mid", "midi", "sysex"},
                              "MIDI SysEx files");
    chooser.addChoosableFileFilter(filter);
    chooser.setFileFilter(filter);
    if (defaultFile != null)
      chooser.setSelectedFile(defaultFile);
    return chooser;
  }

  private final ActionListener loadListener = new GuardedActionListener()
    {
      private File defaultLoadFile = null; // default load dialog file

      public void unguardedActionPerformed(final ActionEvent event)
      {
        final JFileChooser chooser = newChooser(defaultLoadFile);
        final int returnVal = chooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          defaultLoadFile = new File(chooser.getCurrentDirectory(),
                                     chooser.getSelectedFile().getName());
          // TODO:
          //load(defaultLoadFile);
          //documentMetaData.setHaveUnsavedData(false);
        }
      }
    };

  private final ActionListener saveListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener saveAsListener = new GuardedActionListener()
    {
      private File defaultSaveFile = null; // default save dialog file

      public void unguardedActionPerformed(final ActionEvent event)
      {
        final JFileChooser chooser = newChooser(defaultSaveFile);
        final int returnVal = chooser.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          defaultSaveFile = new File(chooser.getCurrentDirectory(),
                                     chooser.getSelectedFile().getName());
          // TODO
          //saveAs(defaultSaveFile);
          //documentMetaData.setHaveUnsavedData(false);
        }
      }
    };

  private final ActionListener closeListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        if ((!documentMetaData.getHaveUnsavedData()) ||
            (JOptionPane.showConfirmDialog(frame,
                                           MSG_DISCARD_UNSAVED_DATA,
                                           "Confirm Close",
                                           JOptionPane.YES_NO_OPTION) ==
             JOptionPane.YES_OPTION)) {
          synchronized(frame) {
            frame.notify();
          }
        } else {
          // action aborted
        }
      }
    };

  private final ActionListener requestListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener dumpListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener exitListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        manager.tryExit();
      }
    };

  private final ActionListener selectAllListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener selectNoneListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener invertSelectionListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener copyListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener pasteListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener incrementListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener decrementListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener uppermostListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener lowermostListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener resetListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener resetMidiDevicesListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener prgChangeListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener bankSelectListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener ctrlChangeListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener rpnNrpnListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener optionsListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener midiOptionsListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        midiOptionsDialog.showDialog();
      }
    };

  private final ActionListener loadDeviceModelListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        if ((!documentMetaData.getHaveUnsavedData()) ||
            (JOptionPane.showConfirmDialog(frame,
                                           MSG_DISCARD_UNSAVED_DATA,
                                           "Confirm Load",
                                           JOptionPane.YES_NO_OPTION) ==
             JOptionPane.YES_OPTION)) {
          editor.loadDeviceModel(frame);
        } else {
          // action aborted
        }
      }
    };

  private final ActionListener setDefaultDeviceModelListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener deviceIdListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        final byte newDeviceId =
          DialogDevID.showDialog(frame,
                                 "Device ID Selection",
                                 JOptionPane.QUESTION_MESSAGE,
                                 documentMetaData.getMidiDeviceId());
        if (newDeviceId >= 0) {
          documentMetaData.setMidiDeviceId(newDeviceId);
          editor.setMidiDeviceId(newDeviceId);
        }
      }
    };

  private final ActionListener manufacturerIdListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ActionListener msgIntervalTimeListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        // TODO
      }
    };

  private final ItemListener displayAddressesListener = new GuardedItemListener()
    {
      public void unguardedItemStateChanged(final ItemEvent event,
                                            final boolean selected)
      {
        editor.setAddressInfoEnabled(selected);
      }
    };

  private final ItemListener toolTipsListener = new GuardedItemListener()
    {
      public void unguardedItemStateChanged(final ItemEvent event,
                                            final boolean selected)
      {
        ToolTipManager.sharedInstance().setEnabled(selected);
      }
    };

  private final ActionListener lookAndFeelListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
        throws Exception
      {
        manager.setLookAndFeel(event.getActionCommand());
      }
    };

  private final ActionListener tutorialListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
        throws IOException
      {
        final URL url =
          Controller.class.getResource("/doc/tutorial/MissingPage.html");
        if (url != null) {
          final HtmlPanel panel = new HtmlPanel(url);
          if (panel != null)
            JOptionPane.showMessageDialog(frame, panel, "Tutorial",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
      }
    };

  private final ActionListener apiSpecsListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
        throws IOException
      {
        final URL url =
          Controller.class.getResource("/doc/api/index.html");
        if (url != null) {
          final HtmlPanel panel = new HtmlPanel(url);
          if (panel != null)
            JOptionPane.showMessageDialog(frame, panel, "API Specs",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
      }
    };

  private final ActionListener aboutApplicationListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        final String msg =
          manager.getVersion() + "\n" +
          manager.getCopyright() + "\n" +
          "\n" +
          "Original distribution site is\n" +
          "https://github.com/soundpaint/SysExEdit\n";
        JOptionPane.showMessageDialog(frame, msg, "About Application",
                                      JOptionPane.INFORMATION_MESSAGE);
      }
    };

  private final ActionListener aboutDeviceModelListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
      {
        editor.showAboutDeviceModelDialog();
      }
    };

  private final ActionListener licenseListener = new GuardedActionListener()
    {
      public void unguardedActionPerformed(final ActionEvent event)
        throws IOException
      {
        final URL url = EditorFrame.class.getResource("/LICENSE.html");
        if (url != null) {
          final HtmlPanel panel = new HtmlPanel(url);
          if (panel != null)
            JOptionPane.showMessageDialog(frame, panel, "License",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
      }
    };

  private final TreeModelListener treeModelListener =
    new GuardedTreeModelListener()
    {
      public void unguardedTreeNodesChanged(final TreeModelEvent event)
      {
        documentMetaData.setHaveUnsavedData(true);
        editor.setHaveUnsavedData(true);
      }

      public void unguardedTreeNodesInserted(final TreeModelEvent event)
      {
        documentMetaData.setHaveUnsavedData(true);
        editor.setHaveUnsavedData(true);
      }

      public void unguardedTreeNodesRemoved(final TreeModelEvent event)
      {
        documentMetaData.setHaveUnsavedData(true);
        editor.setHaveUnsavedData(true);
      }

      public void unguardedTreeStructureChanged(final TreeModelEvent event)
      {
        documentMetaData.setHaveUnsavedData(true);
        editor.setHaveUnsavedData(true);
      }
    };

  public ActionListener getNewListener()
  {
    return newListener;
  }

  public ActionListener getLoadListener()
  {
    return loadListener;
  }

  public ActionListener getSaveListener()
  {
    return saveListener;
  }

  public ActionListener getSaveAsListener()
  {
    return saveAsListener;
  }

  public ActionListener getCloseListener()
  {
    return closeListener;
  }

  public ActionListener getRequestListener()
  {
    return requestListener;
  }

  public ActionListener getDumpListener()
  {
    return dumpListener;
  }

  public ActionListener getExitListener()
  {
    return exitListener;
  }

  public ActionListener getSelectAllListener()
  {
    return selectAllListener;
  }

  public ActionListener getSelectNoneListener()
  {
    return selectNoneListener;
  }

  public ActionListener getInvertSelectionListener()
  {
    return invertSelectionListener;
  }

  public ActionListener getCopyListener()
  {
    return copyListener;
  }

  public ActionListener getPasteListener()
  {
    return pasteListener;
  }

  public ActionListener getIncrementListener()
  {
    return incrementListener;
  }

  public ActionListener getDecrementListener()
  {
    return decrementListener;
  }

  public ActionListener getUppermostListener()
  {
    return uppermostListener;
  }

  public ActionListener getLowermostListener()
  {
    return lowermostListener;
  }

  public ActionListener getResetListener()
  {
    return resetListener;
  }

  public ActionListener getResetMidiDevicesListener()
  {
    return resetMidiDevicesListener;
  }

  public ActionListener getPrgChangeListener()
  {
    return prgChangeListener;
  }

  public ActionListener getBankSelectListener()
  {
    return bankSelectListener;
  }

  public ActionListener getCtrlChangeListener()
  {
    return ctrlChangeListener;
  }

  public ActionListener getRpnNrpnListener()
  {
    return rpnNrpnListener;
  }

  public ActionListener getOptionsListener()
  {
    return optionsListener;
  }

  public ActionListener getMidiOptionsListener()
  {
    return midiOptionsListener;
  }

  public ActionListener getLoadDeviceModelListener()
  {
    return loadDeviceModelListener;
  }

  public ActionListener getSetDefaultDeviceModelListener()
  {
    return setDefaultDeviceModelListener;
  }

  public ActionListener getDeviceIdListener()
  {
    return deviceIdListener;
  }

  public ActionListener getManufacturerIdListener()
  {
    return manufacturerIdListener;
  }

  public ActionListener getMsgIntervalTimeListener()
  {
    return msgIntervalTimeListener;
  }

  public ItemListener getDisplayAddressesListener()
  {
    return displayAddressesListener;
  }

  public ItemListener getToolTipsListener()
  {
    return toolTipsListener;
  }

  public ActionListener getLookAndFeelListener()
  {
    return lookAndFeelListener;
  }

  public ActionListener getTutorialListener()
  {
    return tutorialListener;
  }

  public ActionListener getApiSpecsListener()
  {
    return apiSpecsListener;
  }

  public ActionListener getAboutApplicationListener()
  {
    return aboutApplicationListener;
  }

  public ActionListener getAboutDeviceModelListener()
  {
    return aboutDeviceModelListener;
  }

  public ActionListener getLicenseListener()
  {
    return licenseListener;
  }

  public TreeModelListener getTreeModelListener()
  {
    return treeModelListener;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
