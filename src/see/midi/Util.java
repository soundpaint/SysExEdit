package see.midi;

import java.util.Vector;
import javax.media.sound.AudioManager;
import javax.media.sound.midi.MidiOutControlDevice;
import javax.media.transport.ControlDevice;

public class Util
{
  public static void main(String argv[])
  {
    Vector devices = new Vector();
    ControlDevice[] allDevices = AudioManager.listControlDevices();
    for (int i = 0; i < allDevices.length; i++)
      if (allDevices[i] instanceof MidiOutControlDevice)
	devices.addElement(allDevices[i]);
    MidiOutControlDevice[] midiOut = new MidiOutControlDevice[devices.size()];
    devices.copyInto(midiOut);
    System.out.println("Midi Out Devices:");
    for (int i = 0; i < midiOut.length; i++)
      System.out.println(midiOut[i].getName());
    System.out.flush();
  }
}
