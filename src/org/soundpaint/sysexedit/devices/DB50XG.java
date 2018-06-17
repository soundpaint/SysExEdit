/*
 * @(#)DB50XG.java 1.00 98/01/31
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

package org.soundpaint.sysexedit.devices;

import java.io.InputStream;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.soundpaint.sysexedit.model.AbstractDevice;
import org.soundpaint.sysexedit.model.AddressRepresentation;
import org.soundpaint.sysexedit.model.DataNode;
import org.soundpaint.sysexedit.model.EnumRenderer;
import org.soundpaint.sysexedit.model.FolderNode;
import org.soundpaint.sysexedit.model.IntegerRenderer;
import org.soundpaint.sysexedit.model.MapNode;
import org.soundpaint.sysexedit.model.SparseType;
import org.soundpaint.sysexedit.model.Value;
import org.soundpaint.sysexedit.model.ValueImpl;
import org.soundpaint.sysexedit.model.ValueRangeRenderer;

/**
 * This class customizes SysExEdit for a DB50XG synthesizer.
 */
public class DB50XG extends AbstractDevice
{
  private static final String DEVICE_NAME = "MU50 / DB50XG";
  private static final byte MANUFACTURER_ID = 0x43;
  private static final byte MODEL_ID = 0x4c;
  private static final String ENTERED_BY =
    "Jürgen Reuter, Copyright © 1998, 2018";

  /**
   * Pan Data Assign Table
   */
  private static final String[] PAN =
  {
    "L63", "L62", "L61", "L60",
    "L59", "L58", "L57", "L56",
    "L55", "L54", "L53", "L52",
    "L51", "L50", "L49", "L48",
    "L47", "L46", "L45", "L44",
    "L43", "L42", "L41", "L40",
    "L39", "L38", "L37", "L36",
    "L35", "L34", "L33", "L32",
    "L31", "L30", "L29", "L28",
    "L27", "L26", "L25", "L24",
    "L23", "L22", "L21", "L20",
    "L19", "L18", "L17", "L16",
    "L15", "L14", "L13", "L12",
    "L11", "L10", "L9", "L8",
    "L7", "L6", "L5", "L4",
    "L3", "L2", "L1", "C",
    "R1", "R2", "R3", "R4",
    "R5", "R6", "R7", "R8",
    "R9", "R10", "R11", "R12",
    "R13", "R14", "R15", "R16",
    "R17", "R18", "R19", "R20",
    "R21", "R22", "R23", "R24",
    "R25", "R26", "R27", "R28",
    "R29", "R30", "R31", "R32",
    "R33", "R34", "R35", "R36",
    "R37", "R38", "R39", "R40",
    "R41", "R42", "R43", "R44",
    "R45", "R46", "R47", "R48",
    "R49", "R50", "R51", "R52",
    "R53", "R54", "R55", "R56",
    "R57", "R58", "R59", "R60",
    "R61", "R62", "R63"
  };

  /**
   * Level Data Value Assign Table
   * LEVEL[i] = 6*log(i/64)/log(127/64); 0 &lt;= i &lt;= 127
   */
  private static final String[] LEVEL =
  {
    "-infinity", "-36.41dB", "-30.34dB", "-26.79dB",
    "-24.27dB", "-22.32dB", "-20.72dB", "-19.37dB",
    "-18.20dB", "-17.17dB", "-16.25dB", "-15.41dB",
    "-14.65dB", "-13.95dB", "-13.30dB", "-12.70dB",
    "-12.13dB", "-11.60dB", "-11.10dB", "-10.63dB",
    "-10.18dB", "-9.75dB", "-9.34dB", "-8.96dB",
    "-8.58dB", "-8.22dB", "-7.88dB", "-7.55dB",
    "-7.23dB", "-6.93dB", "-6.63dB", "-6.34dB",
    "-6.06dB", "-5.79dB", "-5.53dB", "-5.28dB",
    "-5.03dB", "-4.79dB", "-4.56dB", "-4.33dB",
    "-4.11dB", "-3.89dB", "-3.68dB", "-3.48dB",
    "-3.28dB", "-3.08dB", "-2.89dB", "-2.70dB",
    "-2.51dB", "-2.33dB", "-2.16dB", "-1.98dB",
    "-1.81dB", "-1.65dB", "-1.48dB", "-1.32dB",
    "-1.16dB", "-1.01dB", "-0.86dB", "-0.71dB",
    "-0.56dB", "-0.42dB", "-0.27dB", "-0.13dB",
    "0.00dB", "+0.13dB", "+0.26dB", "+0.40dB",
    "+0.53dB", "+0.65dB", "+0.78dB", "+0.90dB",
    "+1.03dB", "+1.15dB", "+1.27dB", "+1.38dB",
    "+1.50dB", "+1.61dB", "+1.73dB", "+1.84dB",
    "+1.95dB", "+2.06dB", "+2.16dB", "+2.27dB",
    "+2.38dB", "+2.48dB", "+2.58dB", "+2.68dB",
    "+2.78dB", "+2.88dB", "+2.98dB", "+3.08dB",
    "+3.17dB", "+3.27dB", "+3.36dB", "+3.45dB",
    "+3.54dB", "+3.64dB", "+3.73dB", "+3.81dB",
    "+3.90dB", "+3.99dB", "+4.08dB", "+4.16dB",
    "+4.25dB", "+4.33dB", "+4.41dB", "+4.49dB",
    "+4.58dB", "+4.66dB", "+4.74dB", "+4.82dB",
    "+4.89dB", "+4.97dB", "+5.05dB", "+5.13dB",
    "+5.20dB", "+5.28dB", "+5.35dB", "+5.43dB",
    "+5.50dB", "+5.57dB", "+5.64dB", "+5.71dB",
    "+5.79dB", "+5.86dB", "+5.93dB", "+6.00dB"
  };

  /**
   * Effect Data Value Assign Table #1
   */
  private static final String[] LFO_FREQUENCY =
  {
    "0.00Hz", "0.04Hz", "0.08Hz", "0.13Hz",
    "0.17Hz", "0.21Hz", "0.25Hz", "0.29Hz",
    "0.34Hz", "0.38Hz", "0.42Hz", "0.46Hz",
    "0.51Hz", "0.55Hz", "0.59Hz", "0.63Hz",
    "0.67Hz", "0.72Hz", "0.76Hz", "0.80Hz",
    "0.84Hz", "0.88Hz", "0.93Hz", "0.97Hz",
    "1.01Hz", "1.05Hz", "1.09Hz", "1.14Hz",
    "1.18Hz", "1.22Hz", "1.26Hz", "1.30Hz",
    "1.35Hz", "1.39Hz", "1.43Hz", "1.47Hz",
    "1.51Hz", "1.56Hz", "1.60Hz", "1.64Hz",
    "1.68Hz", "1.72Hz", "1.77Hz", "1.81Hz",
    "1.85Hz", "1.89Hz", "1.94Hz", "1.98Hz",
    "2.02Hz", "2.06Hz", "2.10Hz", "2.15Hz",
    "2.19Hz", "2.23Hz", "2.27Hz", "2.31Hz",
    "2.36Hz", "2.40Hz", "2.44Hz", "2.48Hz",
    "2.52Hz", "2.57Hz", "2.61Hz", "2.65Hz",
    "2.69Hz", "2.78Hz", "2.86Hz", "2.94Hz",
    "3.03Hz", "3.11Hz", "3.20Hz", "3.28Hz",
    "3.37Hz", "3.45Hz", "3.53Hz", "3.62Hz",
    "3.70Hz", "3.87Hz", "4.04Hz", "4.21Hz",
    "4.37Hz", "4.54Hz", "4.71Hz", "4.88Hz",
    "5.05Hz", "5.22Hz", "5.38Hz", "5.55Hz",
    "5.72Hz", "6.06Hz", "6.39Hz", "6.73Hz",
    "7.07Hz", "7.40Hz", "7.74Hz", "8.08Hz",
    "8.41Hz", "8.75Hz", "9.08Hz", "9.42Hz",
    "9.76Hz", "10.10Hz", "10.80Hz", "11.40Hz",
    "12.10Hz", "12.80Hz", "13.50Hz", "14.10Hz",
    "14.80Hz", "15.50Hz", "16.20Hz", "16.80Hz",
    "17.50Hz", "18.20Hz", "19.50Hz", "20.90Hz",
    "22.20Hz", "23.60Hz", "24.90Hz", "26.20Hz",
    "27.60Hz", "28.90Hz", "30.30Hz", "31.60Hz",
    "33.00Hz", "34.30Hz", "37.00Hz", "39.70Hz"
  };

  /**
   * Effect Data Value Assign Table #2
   */
  private static final String[] MODULATION_DELAY_OFFSET =
  {
    "0.0ms", "0.1ms", "0.2ms", "0.3ms",
    "0.4ms", "0.5ms", "0.6ms", "0.7ms",
    "0.8ms", "0.9ms", "1.0ms", "1.1ms",
    "1.2ms", "1.3ms", "1.4ms", "1.5ms",
    "1.6ms", "1.7ms", "1.8ms", "1.9ms",
    "2.0ms", "2.1ms", "2.2ms", "2.3ms",
    "2.4ms", "2.5ms", "2.6ms", "2.7ms",
    "2.8ms", "2.9ms", "3.0ms", "3.1ms",
    "3.2ms", "3.3ms", "3.4ms", "3.5ms",
    "3.6ms", "3.7ms", "3.8ms", "3.9ms",
    "4.0ms", "4.1ms", "4.2ms", "4.3ms",
    "4.4ms", "4.5ms", "4.6ms", "4.7ms",
    "4.8ms", "4.9ms", "5.0ms", "5.1ms",
    "5.2ms", "5.3ms", "5.4ms", "5.5ms",
    "5.6ms", "5.7ms", "5.8ms", "5.9ms",
    "6.0ms", "6.1ms", "6.2ms", "6.3ms",
    "6.4ms", "6.5ms", "6.6ms", "6.7ms",
    "6.8ms", "6.9ms", "7.0ms", "7.1ms",
    "7.2ms", "7.3ms", "7.4ms", "7.5ms",
    "7.6ms", "7.7ms", "7.8ms", "7.9ms",
    "8.0ms", "8.1ms", "8.2ms", "8.3ms",
    "8.4ms", "8.5ms", "8.6ms", "8.7ms",
    "8.8ms", "8.9ms", "9.0ms", "9.1ms",
    "9.2ms", "9.3ms", "9.4ms", "9.5ms",
    "9.6ms", "9.7ms", "9.8ms", "9.9ms",
    "10.0ms", "11.1ms", "12.2ms", "13.3ms",
    "14.4ms", "15.5ms", "17.1ms", "18.6ms",
    "20.2ms", "21.8ms", "23.3ms", "24.9ms",
    "26.5ms", "28.0ms", "29.6ms", "31.2ms",
    "32.8ms", "34.3ms", "35.9ms", "37.5ms",
    "39.0ms", "40.6ms", "42.2ms", "43.7ms",
    "45.3ms", "46.9ms", "48.4ms", "50.0ms"
  };

  /**
   * Effect Data Value Assign Table #3
   */
  private static final String[] EQ_FREQUENCY =
  {
    "20Hz", "22Hz", "25Hz", "28Hz",
    "32Hz", "36Hz", "40Hz", "45Hz",
    "50Hz", "56Hz", "63Hz", "70Hz",
    "80Hz", "90Hz", "100Hz", "110Hz",
    "125Hz", "140Hz", "160Hz", "180Hz",
    "200Hz", "225Hz", "250Hz", "280Hz",
    "315Hz", "355Hz", "400Hz", "450Hz",
    "500Hz", "560Hz", "630Hz", "700Hz",
    "800Hz", "900Hz", "1.0kHz", "1.1kHz",
    "1.2kHz", "1.4kHz", "1.6kHz", "1.8kHz",
    "2.0kHz", "2.2kHz", "2.5kHz", "2.8kHz",
    "3.2kHz", "3.6kHz", "4.0kHz", "4.5kHz",
    "5.0kHz", "5.6kHz", "6.3kHz", "7.0kHz",
    "8.0kHz", "9.0kHz", "10.0kHz", "11.0kHz",
    "12.0kHz", "14.0kHz", "16.0kHz", "18.0kHz",
    "20.0kHz"
  };

  /**
   * Effect Data Value Assign Table #4
   */
  private static final String[] REVERB_TIME =
  {
    "0.3ms", "0.4ms", "0.5ms", "0.6ms",
    "0.7ms", "0.8ms", "0.9ms", "1.0ms",
    "1.1ms", "1.2ms", "1.3ms", "1.4ms",
    "1.5ms", "1.6ms", "1.7ms", "1.8ms",
    "1.9ms", "2.0ms", "2.1ms", "2.2ms",
    "2.3ms", "2.4ms", "2.5ms", "2.6ms",
    "2.7ms", "2.8ms", "2.9ms", "3.0ms",
    "3.1ms", "3.2ms", "3.3ms", "3.4ms",
    "3.5ms", "3.6ms", "3.7ms", "3.8ms",
    "3.9ms", "4.0ms", "4.1ms", "4.2ms",
    "4.3ms", "4.4ms", "4.5ms", "4.6ms",
    "4.7ms", "4.8ms", "4.9ms", "5.0ms",
    "5.5ms", "6.0ms", "6.5ms", "7.0ms",
    "7.5ms", "8.0ms", "8.5ms", "9.0ms",
    "9.5ms", "10.0ms", "11.0ms", "12.0ms",
    "13.0ms", "14.0ms", "15.0ms", "16.0ms",
    "17.0ms", "18.0ms", "19.0ms", "20.0ms",
    "25.0ms", "30.0ms"
  };

  /**
   * Effect Data Value Assign Table #5
   */
  private static final String[] DELAY_TIME_1 =
  {
    "0.1ms", "1.7ms", "3.2ms", "4.8ms",
    "6.4ms", "8.0ms", "9.5ms", "11.1ms",
    "12.7ms", "14.3ms", "15.8ms", "17.4ms",
    "19.0ms", "20.6ms", "22.1ms", "23.7ms",
    "25.3ms", "26.9ms", "28.4ms", "30.0ms",
    "31.6ms", "33.2ms", "34.7ms", "36.3ms",
    "37.9ms", "39.5ms", "41.0ms", "42.6ms",
    "44.2ms", "45.7ms", "47.3ms", "48.9ms",
    "50.5ms", "52.0ms", "53.6ms", "55.2ms",
    "56.8ms", "58.3ms", "59.9ms", "61.5ms",
    "63.1ms", "64.6ms", "66.2ms", "67.8ms",
    "69.4ms", "70.9ms", "72.5ms", "74.1ms",
    "75.7ms", "77.2ms", "78.8ms", "80.4ms",
    "81.9ms", "83.5ms", "85.1ms", "86.7ms",
    "88.2ms", "89.8ms", "91.4ms", "93.0ms",
    "94.5ms", "96.1ms", "97.7ms", "99.3ms",
    "100.8ms", "102.4ms", "104.0ms", "105.6ms",
    "107.1ms", "108.7ms", "110.3ms", "111.9ms",
    "113.4ms", "115.0ms", "116.6ms", "118.2ms",
    "119.7ms", "121.3ms", "122.9ms", "124.4ms",
    "126.0ms", "127.6ms", "129.2ms", "130.7ms",
    "132.3ms", "133.9ms", "135.5ms", "137.0ms",
    "138.6ms", "140.2ms", "141.8ms", "143.3ms",
    "144.9ms", "146.5ms", "148.1ms", "149.6ms",
    "151.2ms", "152.8ms", "154.4ms", "155.9ms",
    "157.5ms", "159.1ms", "160.6ms", "162.2ms",
    "163.8ms", "165.4ms", "166.9ms", "168.5ms",
    "170.1ms", "171.7ms", "173.2ms", "174.8ms",
    "176.4ms", "178.0ms", "179.5ms", "181.1ms",
    "182.7ms", "184.3ms", "185.8ms", "187.4ms",
    "189.0ms", "190.6ms", "192.1ms", "193.7ms",
    "195.3ms", "196.9ms", "198.4ms", "200.0ms"
  };

  /**
   * Effect Data Value Assign Table #6
   */
  private static final String[] ROOM_SIZE =
  {
    "0.1m", "0.3m", "0.4m", "0.6m",
    "0.7m", "0.9m", "1.0m", "1.2m",
    "1.4m", "1.5m", "1.7m", "1.8m",
    "2.0m", "2.1m", "2.3m", "2.5m",
    "2.6m", "2.8m", "2.9m", "3.1m",
    "3.2m", "3.4m", "3.5m", "3.7m",
    "3.9m", "4.0m", "4.2m", "4.3m",
    "4.5m", "4.6m", "4.8m", "5.0m",
    "5.1m", "5.3m", "5.4m", "5.6m",
    "5.7m", "5.9m", "6.1m", "6.2m",
    "6.4m", "6.5m", "6.7m", "6.8m",
    "7.0m"
  };

  /**
   * Effect Data Value Assign Table #7
   */
  private static final String[] DELAY_TIME_2 =
  {
    "0.1ms", "3.2ms", "6.4ms", "9.5ms",
    "12.7ms", "15.8ms", "19.0ms", "22.1ms",
    "25.3ms", "28.4ms", "31.6ms", "34.7ms",
    "37.9ms", "41.0ms", "44.2ms", "47.3ms",
    "50.5ms", "53.6ms", "56.8ms", "59.9ms",
    "63.1ms", "66.2ms", "69.4ms", "72.5ms",
    "75.7ms", "78.8ms", "82.0ms", "85.1ms",
    "88.3ms", "91.4ms", "94.6ms", "97.7ms",
    "100.9ms", "104.0ms", "107.2ms", "110.3ms",
    "113.5ms", "116.6ms", "119.8ms", "122.9ms",
    "126.1ms", "129.2ms", "132.4ms", "135.5ms",
    "138.6ms", "141.8ms", "144.9ms", "148.1ms",
    "151.2ms", "154.4ms", "157.5ms", "160.7ms",
    "163.8ms", "167.0ms", "170.1ms", "173.3ms",
    "176.4ms", "179.6ms", "182.7ms", "185.9ms",
    "189.0ms", "192.2ms", "195.3ms", "198.5ms",
    "201.6ms", "204.8ms", "207.9ms", "211.1ms",
    "214.2ms", "217.4ms", "220.5ms", "223.7ms",
    "226.8ms", "230.0ms", "233.1ms", "236.3ms",
    "239.4ms", "242.6ms", "245.7ms", "248.9ms",
    "252.0ms", "255.2ms", "258.3ms", "261.5ms",
    "264.6ms", "267.7ms", "270.9ms", "274.0ms",
    "277.2ms", "280.3ms", "283.5ms", "286.6ms",
    "289.8ms", "292.9ms", "296.1ms", "299.2ms",
    "302.4ms", "305.5ms", "308.7ms", "311.8ms",
    "315.0ms", "318.1ms", "321.3ms", "324.4ms",
    "327.6ms", "330.7ms", "333.9ms", "337.0ms",
    "340.2ms", "343.3ms", "346.5ms", "349.6ms",
    "352.8ms", "355.9ms", "359.1ms", "362.2ms",
    "365.4ms", "368.5ms", "371.7ms", "374.8ms",
    "378.0ms", "381.1ms", "384.3ms", "387.4ms",
    "390.6ms", "393.7ms", "396.9ms", "400.0ms"
  };

  /**
   * Effect Data Value Assign Table #8
   */
  private static final String[] REVERB_DIM_LENGTH =
  {
    "0.5m", "0.8m", "1.0m", "1.3m",
    "1.5m", "1.8m", "2.0m", "2.3m",
    "2.6m", "2.8m", "3.1m", "3.3m",
    "3.6m", "3.9m", "4.1m", "4.4m",
    "4.6m", "4.9m", "5.2m", "5.4m",
    "5.7m", "5.9m", "6.2m", "6.5m",
    "6.7m", "7.0m", "7.2m", "7.5m",
    "7.8m", "8.0m", "8.3m", "8.6m",
    "8.8m", "9.1m", "9.4m", "9.6m",
    "9.9m", "10.2m", "10.4m", "10.7m",
    "11.0m", "11.2m", "11.5m", "11.8m",
    "12.1m", "12.3m", "12.6m", "12.9m",
    "13.1m", "13.4m", "13.7m", "14.0m",
    "14.2m", "14.5m", "14.8m", "15.1m",
    "15.4m", "15.6m", "15.9m", "16.2m",
    "16.5m", "16.8m", "17.1m", "17.3m",
    "17.6m", "17.9m", "18.2m", "18.5m",
    "18.8m", "19.1m", "19.4m", "19.7m",
    "20.0m", "20.2m", "20.5m", "20.8m",
    "21.1m", "21.4m", "21.7m", "22.0m",
    "22.4m", "22.7m", "23.0m", "23.3m",
    "23.6m", "23.9m", "24.2m", "24.5m",
    "24.9m", "25.2m", "25.5m", "25.8m",
    "26.1m", "26.5m", "26.8m", "27.1m",
    "27.5m", "27.8m", "28.1m", "28.5m",
    "28.8m", "29.2m", "29.5m", "29.9m",
    "30.2m"
  };

  /**
   * Effect Data Value Assign Dry / Wet
   */
  private static final String[] DRY_WET =
  {
    "D63>W", "D62>W", "D61>W", "D60>W",
    "D59>W", "D58>W", "D57>W", "D56>W",
    "D55>W", "D54>W", "D53>W", "D52>W",
    "D51>W", "D50>W", "D49>W", "D48>W",
    "D47>W", "D46>W", "D45>W", "D44>W",
    "D43>W", "D42>W", "D41>W", "D40>W",
    "D39>W", "D38>W", "D37>W", "D36>W",
    "D35>W", "D34>W", "D33>W", "D32>W",
    "D31>W", "D30>W", "D29>W", "D28>W",
    "D27>W", "D26>W", "D25>W", "D24>W",
    "D23>W", "D22>W", "D21>W", "D20>W",
    "D19>W", "D18>W", "D17>W", "D16>W",
    "D15>W", "D14>W", "D13>W", "D12>W",
    "D11>W", "D10>W", "D9>W", "D8>W",
    "D7>W", "D6>W", "D5>W", "D4>W",
    "D3>W", "D2>W", "D1>W", "D=W",
    "D<W1", "D<W2", "D<W3", "D<W4",
    "D<W5", "D<W6", "D<W7", "D<W8",
    "D<W9", "D<W10", "D<W11", "D<W12",
    "D<W13", "D<W14", "D<W15", "D<W16",
    "D<W17", "D<W18", "D<W19", "D<W20",
    "D<W21", "D<W22", "D<W23", "D<W24",
    "D<W25", "D<W26", "D<W27", "D<W28",
    "D<W29", "D<W30", "D<W31", "D<W32",
    "D<W33", "D<W34", "D<W35", "D<W36",
    "D<W37", "D<W38", "D<W39", "D<W40",
    "D<W41", "D<W42", "D<W43", "D<W44",
    "D<W45", "D<W46", "D<W47", "D<W48",
    "D<W49", "D<W50", "D<W51", "D<W52",
    "D<W53", "D<W54", "D<W55", "D<W56",
    "D<W57", "D<W58", "D<W59", "D<W60",
    "D<W61", "D<W62", "D<W63"
  };

  /**
   * Effect Data Value Assign Er / Rev Balance
   */
  private static final String[] ER_REV_BALANCE =
  {
    "E63>R", "E62>R", "E61>R", "E60>R",
    "E59>R", "E58>R", "E57>R", "E56>R",
    "E55>R", "E54>R", "E53>R", "E52>R",
    "E51>R", "E50>R", "E49>R", "E48>R",
    "E47>R", "E46>R", "E45>R", "E44>R",
    "E43>R", "E42>R", "E41>R", "E40>R",
    "E39>R", "E38>R", "E37>R", "E36>R",
    "E35>R", "E34>R", "E33>R", "E32>R",
    "E31>R", "E30>R", "E29>R", "E28>R",
    "E27>R", "E26>R", "E25>R", "E24>R",
    "E23>R", "E22>R", "E21>R", "E20>R",
    "E19>R", "E18>R", "E17>R", "E16>R",
    "E15>R", "E14>R", "E13>R", "E12>R",
    "E11>R", "E10>R", "E9>R", "E8>R",
    "E7>R", "E6>R", "E5>R", "E4>R",
    "E3>R", "E2>R", "E1>R", "E=R",
    "E<R1", "E<R2", "E<R3", "E<R4",
    "E<R5", "E<R6", "E<R7", "E<R8",
    "E<R9", "E<R10", "E<R11", "E<R12",
    "E<R13", "E<R14", "E<R15", "E<R16",
    "E<R17", "E<R18", "E<R19", "E<R20",
    "E<R21", "E<R22", "E<R23", "E<R24",
    "E<R25", "E<R26", "E<R27", "E<R28",
    "E<R29", "E<R30", "E<R31", "E<R32",
    "E<R33", "E<R34", "E<R35", "E<R36",
    "E<R37", "E<R38", "E<R39", "E<R40",
    "E<R41", "E<R42", "E<R43", "E<R44",
    "E<R45", "E<R46", "E<R47", "E<R48",
    "E<R49", "E<R50", "E<R51", "E<R52",
    "E<R53", "E<R54", "E<R55", "E<R56",
    "E<R57", "E<R58", "E<R59", "E<R60",
    "E<R61", "E<R62", "E<R63"
  };

  /**
   * EQ Gain
   */
  private static final String[] EQ_GAIN =
  {
    "-12db", "-11db", "-10db", "-9db", "-8db", "-7db", "-6db", "-5db",
    "-4db", "-3db", "-2db", "-1db", "0db", "+1db", "+2db", "+3db",
     "+4db", "+5db", "+6db", "+7db", "+8db", "+9db", "+10db", "+11db",
     "+12db"
  };


  /**
   * Phase Difference
   */
  private static final String[] PHASE_DIFFERENCE =
  {
    "-180°", "-177°", "-174°", "-171°", "-168°", "-165°", "-162°", "-159°",
    "-156°", "-153°", "-150°", "-147°", "-144°", "-141°", "-138°", "-135°",
    "-132°", "-129°", "-126°", "-123°", "-120°", "-117°", "-114°", "-111°",
    "-108°", "-105°", "-102°", "-99°", "-96°", "-93°", "-90°", "-87°",
    "-84°", "-81°", "-78°", "-75°", "-72°", "-69°", "-66°", "-63°",
    "-60°", "-57°", "-54°", "-51°", "-48°", "-45°", "-42°", "-39°",
    "-36°", "-33°", "-30°", "-27°", "-24°", "-21°", "-18°", "-15°",
    "-12°", "-9°", "-6°", "-3°", " 0°", "+3°", "+6°", "+9°",
    "+12°", "+15°", "+18°", "+21°", "+24°", "+27°", "+30°", "+33°",
    "+36°", "+39°", "+42°", "+45°", "+48°", "+51°", "+54°", "+57°",
    "+60°", "+63°", "+66°", "+69°", "+72°", "+75°", "+78°", "+81°",
    "+84°", "+87°", "+90°", "+93°", "+96°", "+99°", "+102°", "+105°",
    "+108°", "+111°", "+114°", "+117°", "+120°", "+123°", "+126°", "+129°",
    "+132°", "+135°", "+138°", "+141°", "+144°", "+147°", "+150°", "+153°",
    "+156°", "+159°", "+162°", "+165°", "+168°", "+171°", "+174°", "+177°",
    "+180°"
  };

  /**
   * Effect type hall
   */
  private static final String[] HALL =
  {
    "Hall 1", "Hall 2"
  };

  /**
   * Effect type room
   */
  private static final String[] ROOM =
  {
    "Room 1", "Room 2", "Room 3"
  };

  /**
   * Effect type stage
   */
  private static final String[] STAGE =
  {
    "Stage 1", "Stage 2"
  };

  /**
   * Effect type chorus
   */
  private static final String[] CHORUS =
  {
    "Chorus 1", "Chorus 2", "Chorus 3"
  };

  /**
   * Effect type celeste
   */
  private static final String[] CELESTE =
  {
    "Celeste 1", "Celeste 2", "Celeste 3"
  };

  /**
   * Effect type flanger
   */
  private static final String[] FLANGER =
  {
    "Flanger 1", "Flanger 2"
  };

  /**
   * Effect type early ref
   */
  private static final String[] EARLY_REF =
  {
    "Early Ref 1", "Early Ref 2"
  };

  /**
   * Effect type karaoke
   */
  private static final String[] KARAOKE =
  {
    "Karaoke 1", "Karaoke 2", "Karaoke 3"
  };

  /**
   * Variation connection type
   */
  private static final String[] CONNECTION =
  {
    "Insertion", "System"
  };

  /**
   * Variation part type
   */
  private static final String[] PART =
  {
    "Part 1", "Part 2", "Part 3", "Part 4",
    "Part 5", "Part 6", "Part 7", "Part 8",
    "Part 9", "Part 10", "Part 11", "Part 12",
    "Part 13", "Part 14", "Part 15", "Part 16"
  };

  /**
   * Mono / poly mode
   */
  private static final String[] MONO_POLY_MODE =
  {
    "Mono", "Poly"
  };

  /**
   * Key assign
   */
  private static final String[] KEY_ASSIGN =
  {
    "Single", "Multi"
  };

  /**
   * Same note number / key on assign
   */
  private static final String[] KEY_ON_ASSIGN =
  {
    "Single", "Multi", "Inst (for Drum)"
  };

  /**
   * Part mode
   */
  private static final String[] PART_MODE =
  {
    "Normal", "Drum", "Drums 1", "Drums 2"
  };

  /*
   * Detune is an 8 bits value split into to 2 nybbles distributed
   * over two memory locations with each having 7 bit, i.e. a sparse
   * 14 bits value.  We model this special case by composing the 14
   * bits value from value ranges enumerating over the upper nybble
   * value (DETUNE_0x0 ..  DETUNE_0xf).
   */

  /**
   * Detune_0x0
   */
  private static final String[] DETUNE_0X0 =
  {
    "-12.8Hz", "-12.7Hz", "-12.6Hz", "-12.5Hz",
    "-12.4Hz", "-12.3Hz", "-12.2Hz", "-12.1Hz",
    "-12.0Hz", "-11.9Hz", "-11.8Hz", "-11.7Hz",
    "-11.6Hz", "-11.5Hz", "-11.4Hz", "-11.3Hz"
  };

  /**
   * Detune_0x1
   */
  private static final String[] DETUNE_0X1 =
  {
    "-11.2Hz", "-11.1Hz", "-11.0Hz", "-10.9Hz",
    "-10.8Hz", "-10.7Hz", "-10.6Hz", "-10.5Hz",
    "-10.4Hz", "-10.3Hz", "-10.2Hz", "-10.1Hz",
    "-10.0Hz", "-9.9Hz", "-9.8Hz", "-9.7Hz"
  };

  /**
   * Detune_0x2
   */
  private static final String[] DETUNE_0X2 =
  {
    "-9.6Hz", "-9.5Hz", "-9.4Hz", "-9.3Hz",
    "-9.2Hz", "-9.1Hz", "-9.0Hz", "-8.9Hz",
    "-8.8Hz", "-8.7Hz", "-8.6Hz", "-8.5Hz",
    "-8.4Hz", "-8.3Hz", "-8.2Hz", "-8.1Hz"
  };

  /**
   * Detune_0x3
   */
  private static final String[] DETUNE_0X3 =
  {
    "-8.0Hz", "-7.9Hz", "-7.8Hz", "-7.7Hz",
    "-7.6Hz", "-7.5Hz", "-7.4Hz", "-7.3Hz",
    "-7.2Hz", "-7.1Hz", "-7.0Hz", "-6.9Hz",
    "-6.8Hz", "-6.7Hz", "-6.6Hz", "-6.5Hz"
  };

  /**
   * Detune_0x4
   */
  private static final String[] DETUNE_0X4 =
  {
    "-6.4Hz", "-6.3Hz", "-6.2Hz", "-6.1Hz",
    "-6.0Hz", "-5.9Hz", "-5.8Hz", "-5.7Hz",
    "-5.6Hz", "-5.5Hz", "-5.4Hz", "-5.3Hz",
    "-5.2Hz", "-5.1Hz", "-5.0Hz", "-4.9Hz"
  };

  /**
   * Detune_0x5
   */
  private static final String[] DETUNE_0X5 =
  {
    "-4.8Hz", "-4.7Hz", "-4.6Hz", "-4.5Hz",
    "-4.4Hz", "-4.3Hz", "-4.2Hz", "-4.1Hz",
    "-4.0Hz", "-3.9Hz", "-3.8Hz", "-3.7Hz",
    "-3.6Hz", "-3.5Hz", "-3.4Hz", "-3.3Hz"
  };

  /**
   * Detune_0x6
   */
  private static final String[] DETUNE_0X6 =
  {
    "-3.2Hz", "-3.1Hz", "-3.0Hz", "-2.9Hz",
    "-2.8Hz", "-2.7Hz", "-2.6Hz", "-2.5Hz",
    "-2.4Hz", "-2.3Hz", "-2.2Hz", "-2.1Hz",
    "-2.0Hz", "-1.9Hz", "-1.8Hz", "-1.7Hz"
  };

  /**
   * Detune_0x7
   */
  private static final String[] DETUNE_0X7 =
  {
    "-1.6Hz", "-1.5Hz", "-1.4Hz", "-1.3Hz",
    "-1.2Hz", "-1.1Hz", "-1.0Hz", "-0.9Hz",
    "-0.8Hz", "-0.7Hz", "-0.6Hz", "-0.5Hz",
    "-0.4Hz", "-0.3Hz", "-0.2Hz", "-0.1Hz"
  };

  /**
   * Detune_0x8
   */
  private static final String[] DETUNE_0X8 =
  {
    "0.0Hz", "0.1Hz", "0.2Hz", "0.3Hz",
    "0.4Hz", "0.5Hz", "0.6Hz", "0.7Hz",
    "0.8Hz", "0.9Hz", "1.0Hz", "1.1Hz",
    "1.2Hz", "1.3Hz", "1.4Hz", "1.5Hz"
  };

  /**
   * Detune_0x9
   */
  private static final String[] DETUNE_0X9 =
  {
    "1.6Hz", "1.7Hz", "1.8Hz", "1.9Hz",
    "2.0Hz", "2.1Hz", "2.2Hz", "2.3Hz",
    "2.4Hz", "2.5Hz", "2.6Hz", "2.7Hz",
    "2.8Hz", "2.9Hz", "3.0Hz", "3.1Hz"
  };

  /**
   * Detune_0xa
   */
  private static final String[] DETUNE_0XA =
  {
    "3.2Hz", "3.3Hz", "3.4Hz", "3.5Hz",
    "3.6Hz", "3.7Hz", "3.8Hz", "3.9Hz",
    "4.0Hz", "4.1Hz", "4.2Hz", "4.3Hz",
    "4.4Hz", "4.5Hz", "4.6Hz", "4.7Hz"
  };

  /**
   * Detune_0xb
   */
  private static final String[] DETUNE_0XB =
  {
    "4.8Hz", "4.9Hz", "5.0Hz", "5.1Hz",
    "5.2Hz", "5.3Hz", "5.4Hz", "5.5Hz",
    "5.6Hz", "5.7Hz", "5.8Hz", "5.9Hz",
    "6.0Hz", "6.1Hz", "6.2Hz", "6.3Hz"
  };

  /**
   * Detune_0xc
   */
  private static final String[] DETUNE_0XC =
  {
    "6.4Hz", "6.5Hz", "6.6Hz", "6.7Hz",
    "6.8Hz", "6.9Hz", "7.0Hz", "7.1Hz",
    "7.2Hz", "7.3Hz", "7.4Hz", "7.5Hz",
    "7.6Hz", "7.7Hz", "7.8Hz", "7.9Hz"
  };

  /**
   * Detune_0xd
   */
  private static final String[] DETUNE_0XD =
  {
    "8.0Hz", "8.1Hz", "8.2Hz", "8.3Hz",
    "8.4Hz", "8.5Hz", "8.6Hz", "8.7Hz",
    "8.8Hz", "8.9Hz", "9.0Hz", "9.1Hz",
    "9.2Hz", "9.3Hz", "9.4Hz", "9.5Hz"
  };

  /**
   * Detune_0xe
   */
  private static final String[] DETUNE_0XE =
  {
    "9.6Hz", "9.7Hz", "9.8Hz", "9.9Hz",
    "10.0Hz", "10.1Hz", "10.2Hz", "10.3Hz",
    "10.4Hz", "10.5Hz", "10.6Hz", "10.7Hz",
    "10.8Hz", "10.9Hz", "11.0Hz", "11.1Hz"
  };

  /**
   * Detune_0xf
   */
  private static final String[] DETUNE_0XF =
  {
    "11.2Hz", "11.3Hz", "11.4Hz", "11.5Hz",
    "11.6Hz", "11.7Hz", "11.8Hz", "11.9Hz",
    "12.0Hz", "12.1Hz", "12.2Hz", "12.3Hz",
    "12.4Hz", "12.5Hz", "12.6Hz", "12.7Hz"
  };

  /**
   * Note
   */
  private static final String[] NOTE =
  {
    "C-2", "C#-2", "D-2", "D#-2", "E-2", "F-2",
    "F#-2", "G-2", "G#-2", "A-2", "A#-2", "B-2",
    "C-1", "C#-1", "D-1", "D#-1", "E-1", "F-1",
    "F#-1", "G-1", "G#-1", "A-1", "A#-1", "B-1",
    "C0", "C#0", "D0", "D#0", "E0", "F0",
    "F#0", "G0", "G#0", "A0", "A#0", "B0",
    "C1", "C#1", "D1", "D#1", "E1", "F1",
    "F#1", "G1", "G#1", "A1", "A#1", "B1",
    "C2", "C#2", "D2", "D#2", "E2", "F2",
    "F#2", "G2", "G#2", "A2", "A#2", "B2",
    "C3", "C#3", "D3", "D#3", "E3", "F3",
    "F#3", "G3", "G#3", "A3", "A#3", "B3",
    "C4", "C#4", "D4", "D#4", "E4", "F4",
    "F#4", "G4", "G#4", "A4", "A#4", "B4",
    "C5", "C#5", "D5", "D#5", "E5", "F5",
    "F#5", "G5", "G#5", "A5", "A#5", "B5",
    "C6", "C#6", "D6", "D#6", "E6", "F6",
    "F#6", "G6", "G#6", "A6", "A#6", "B6",
    "C7", "C#7", "D7", "D#7", "E7", "F7",
    "F#7", "G7", "G#7", "A7", "A#7", "B7",
    "C8", "C#8", "D8", "D#8", "E8", "F8",
    "F#8", "G8"
  };

  /**
   * Filter Control
   */
  private static final String[] FILTER_CONTROL =
  {
    "-9600ct", "-9450ct", "-9300ct", "-9150ct",
    "-9000ct", "-8850ct", "-8700ct", "-8550ct",
    "-8400ct", "-8250ct", "-8100ct", "-7950ct",
    "-7800ct", "-7650ct", "-7500ct", "-7350ct",
    "-7200ct", "-7050ct", "-6900ct", "-6750ct",
    "-6600ct", "-6450ct", "-6300ct", "-6150ct",
    "-6000ct", "-5850ct", "-5700ct", "-5550ct",
    "-5400ct", "-5250ct", "-5100ct", "-4950ct",
    "-4800ct", "-4650ct", "-4500ct", "-4350ct",
    "-4200ct", "-4050ct", "-3900ct", "-3750ct",
    "-3600ct", "-3450ct", "-3300ct", "-3150ct",
    "-3000ct", "-2850ct", "-2700ct", "-2550ct",
    "-2400ct", "-2250ct", "-2100ct", "-1950ct",
    "-1800ct", "-1650ct", "-1500ct", "-1350ct",
    "-1200ct", "-1050ct", "-900ct",  "-750ct",
    "-600ct",  "-450ct",  "-300ct",  "-150ct",
    "0ct",     "+150ct",  "+300ct",  "+450ct",
    "+600ct ", "+750ct",  "+900ct",  "+1050ct",
    "+1200ct", "+1350ct", "+1500ct", "+1650ct",
    "+1800ct", "+1950ct", "+2100ct", "+2250ct",
    "+2400ct", "+2550ct", "+2700ct", "+2850ct",
    "+3000ct", "+3150ct", "+3300ct", "+3450ct",
    "+3600ct", "+3750ct", "+3900ct", "+4050ct",
    "+4200ct", "+4350ct", "+4500ct", "+4650ct",
    "+4800ct", "+4950ct", "+5100ct", "+5250ct",
    "+5400ct", "+5550ct", "+5700ct", "+5850ct",
    "+6000ct", "+6150ct", "+6300ct", "+6450ct",
    "+6600ct", "+6750ct", "+6900ct", "+7050ct",
    "+7200ct", "+7350ct", "+7500ct", "+7650ct",
    "+7800ct", "+7950ct", "+8100ct", "+8250ct",
    "+8400ct", "+8550ct", "+8700ct", "+8850ct",
    "+9000ct", "+9150ct", "+9300ct", "+9450ct"
  };

  /**
   * Modulation Depth
   */
  private static final String[] BEND_LFO_MOD_DEPTH =
  {
    "-100%", "-98%", "-97%", "-95%", "-94%", "-92%", "-91%", "-89%",
    "-87%", "-86%", "-84%", "-83%", "-81%", "-80%", "-78%", "-76%",
    "-75%", "-73%", "-72%", "-70%", "-69%", "-67%", "-65%", "-64%",
    "-62%", "-61%", "-59%", "-57%", "-56%", "-54%", "-53%", "-51%",
    "-50%", "-48%", "-46%", "-45%", "-43%", "-42%", "-40%", "-39%",
    "-37%", "-35%", "-34%", "-32%", "-31%", "-29%", "-28%", "-26%",
    "-24%", "-23%", "-21%", "-20%", "-18%", "-17%", "-15%", "-13%",
    "-12%", "-10%", "-9%", "-7%", "-6%", "-4%", "-2%", "-1%",
    " 1%", " 2%", " 4%", " 6%", " 7%", " 9%", "10%", "12%",
    "13%", "15%", "17%", "18%", "20%", "21%", "23%", "24%",
    "26%", "28%", "29%", "31%", "32%", "34%", "35%", "37%",
    "39%", "40%", "42%", "43%", "45%", "46%", "48%", "50%",
    "51%", "53%", "54%", "56%", "57%", "59%", "61%", "62%",
    "64%", "65%", "67%", "69%", "70%", "72%", "73%", "75%",
    "76%", "78%", "80%", "81%", "83%", "84%", "86%", "87%",
    "89%", "91%", "92%", "94%", "95%", "97%", "98%", "100%"
  };

  /**
   * Variation connection type
   */
  private static final String[] SWITCH =
  {
    "Off", "On"
  };

  /**
   * Mono / Stereo
   */
  private static final String[] MONO_STEREO =
  {
    "Mono", "Stereo"
  };

  /**
   * Scale Tuning
   */
  private static final String[] SCALE_TUNING =
  {
    "-64ct", "-63ct", "-62ct", "-61ct", "-60ct", "-59ct", "-58ct", "-57ct",
    "-56ct", "-55ct", "-54ct", "-53ct", "-52ct", "-51ct", "-50ct", "-49ct",
    "-48ct", "-47ct", "-46ct", "-45ct", "-44ct", "-43ct", "-42ct", "-41ct",
    "-40ct", "-39ct", "-38ct", "-37ct", "-36ct", "-35ct", "-34ct", "-33ct",
    "-32ct", "-31ct", "-30ct", "-29ct", "-28ct", "-27ct", "-26ct", "-25ct",
    "-24ct", "-23ct", "-22ct", "-21ct", "-20ct", "-19ct", "-18ct", "-17ct",
    "-16ct", "-15ct", "-14ct", "-13ct", "-12ct", "-11ct", "-10ct", "-9ct",
    "-8ct", "-7ct", "-6ct", "-5ct", "-4ct", "-3ct", "-2ct", "-1ct",
    "0ct", "1ct", "2ct", "3ct", "4ct", "5ct", "6ct", "7ct",
    "8ct", "9ct", "10ct", "11ct", "12ct", "13ct", "14ct", "15ct",
    "16ct", "17ct", "18ct", "19ct", "20ct", "21ct", "22ct", "23ct",
    "24ct", "25ct", "26ct", "27ct", "28ct", "29ct", "30ct", "31ct",
    "32ct", "33ct", "34ct", "35ct", "36ct", "37ct", "38ct", "39ct",
    "40ct", "41ct", "42ct", "43ct", "44ct", "45ct", "46ct", "47ct",
    "48ct", "49ct", "50ct", "51ct", "52ct", "53ct", "54ct", "55ct",
    "56ct", "57ct", "58ct", "59ct", "60ct", "61ct", "62ct", "63ct",
  };

  /**
   * Returns the manufacturer ID as defined in the MIDI specification.
   */
  public byte getManufacturerId()
  {
    return MANUFACTURER_ID;
  }

  /**
   * Returns the model ID.
   */
  public byte getModelId()
  {
    return MODEL_ID;
  }

  private static final SparseType rangeDeviceId =
    new SparseType("internal-control").
    addValueRange(0x0, 0xf,
                  new IntegerRenderer(16, true, "0x", "", (byte)4));

  /**
   * Returns a Value object that represents the MIDI device model ID
   * of this device.  As the DB50XG specs do not explicitly specify
   * this value, we assume default value 0.
   */
  public Value getDeviceId()
  {
    final Value deviceId = new ValueImpl(rangeDeviceId);
    deviceId.setBitSize(7);
    deviceId.setDefaultValue(0x00);
    return deviceId;
  }

  /**
   * Returns the name of the author; optionally, a copyright message.
   */
  public String getEnteredBy()
  {
    return ENTERED_BY;
  }

  /**
   * Returns an AddressRepresentation object that defines how addresses
   * are to be displayed to the user.
   */
  public AddressRepresentation getAddressRepresentation()
  {
    return new AddressRepresentation() {
      public String memoryBitAddress2DeviceAddress(final long bitAddress)
      {
        final long address = bitAddress / 7;
        final byte addrHigh = (byte)((address >> 14) & 0x7f);
        final byte addrMiddle = (byte)((address >> 7) & 0x7f);
        final byte addrLow = (byte)(address & 0x7f);
        final byte offset = (byte)(bitAddress % 7);
        final String offsetStr = offset == 0 ? "" : " [" + offset + "]";
        return
          String.format("%02x", addrHigh) + " " +
          String.format("%02x", addrMiddle) + " " +
          String.format("%02x", addrLow) +
          offsetStr;
      }
    };
  }

  /**
   * Converts a DB50XG memory address into a the corresponding index
   * of a bit vector.
   * @param hi The DB50XG address high component value.
   * @param mid The DB50XG address mid component value.
   * @param lo The DB50XG address low component value.
   * @return The corresponding index in a vector of bits.
   */
  private long addr2index(final int hi, final int mid, final int lo)
  {
    return
      7 * ((((long)(hi & 0x7f)) << 14) | ((mid & 0x7f) << 7) | (lo & 0x7f));
  }

  private static final ValueRangeRenderer rendererPan =
    new EnumRenderer(PAN);

  private static final ValueRangeRenderer rendererLevel =
    new EnumRenderer(LEVEL);

  private static final ValueRangeRenderer rendererModulationDelayOffset =
    new EnumRenderer(MODULATION_DELAY_OFFSET);
  private static final SparseType typeModulationDelayOffset =
    new SparseType("internal-time").
    addValueRange(0x00, 0x7f, rendererModulationDelayOffset);

  private static final ValueRangeRenderer rendererEqFrequency =
    new EnumRenderer(EQ_FREQUENCY);
  private static final SparseType typeEqFrequency =
    new SparseType("internal-tune").
    addValueRange(0x00, 0x3c, rendererEqFrequency);

  private static final ValueRangeRenderer rendererReverbTime =
    new EnumRenderer(REVERB_TIME);
  private static final SparseType typeReverbTime =
    new SparseType("internal-time").
    addValueRange(0x00, 0x45, rendererReverbTime);

  private static final ValueRangeRenderer rendererDelayTime1 =
    new EnumRenderer(DELAY_TIME_1);
  private static final SparseType typeDelayTime1 =
    new SparseType("internal-time").
    addValueRange(0x00, 0x7f, rendererDelayTime1);

  private static final ValueRangeRenderer rendererRoomSize =
    new EnumRenderer(ROOM_SIZE);

  private static final ValueRangeRenderer rendererDelayTime2 =
    new EnumRenderer(DELAY_TIME_2);
  private static final SparseType typeDelayTime2 =
    new SparseType("internal-time").
    addValueRange(0x00, 0x7f, rendererDelayTime2);

  private static final ValueRangeRenderer rendererReverbDimLength =
    new EnumRenderer(REVERB_DIM_LENGTH);
  private static final SparseType typeReverbWidth =
    new SparseType("internal-length").
    addValueRange(0x00, 0x25, rendererReverbDimLength);
  private static final SparseType typeReverbHeight =
    new SparseType("internal-length").
    addValueRange(0x00, 0x49, rendererReverbDimLength);
  private static final SparseType typeReverbDepth =
    new SparseType("internal-length").
    addValueRange(0x00, 0x68, rendererReverbDimLength);

  private static final ValueRangeRenderer rendererDryWet =
    new EnumRenderer(DRY_WET);
  private static final SparseType typeDryWet =
    new SparseType("internal-pan").
    addValueRange(0x01, 0x7f, rendererDryWet);

  private static final ValueRangeRenderer rendererErRevBalance =
    new EnumRenderer(ER_REV_BALANCE);
  private static final SparseType typeErRevBalance =
    new SparseType("internal-pan").
    addValueRange(0x01, 0x7f, rendererErRevBalance);

  private static final ValueRangeRenderer rendererEqGain =
    new EnumRenderer(EQ_GAIN);
  private static final SparseType typeEqGain =
    new SparseType("internal-volume").
    addValueRange(0x34, 0x4c, rendererEqGain);

  private static final ValueRangeRenderer rendererPhaseDifference =
    new EnumRenderer(PHASE_DIFFERENCE);
  private static final SparseType typePhaseDifference =
    new SparseType("internal-control").
    addValueRange(0x04, 0x7c, rendererPhaseDifference);

  private static final ValueRangeRenderer rendererHall = new EnumRenderer(HALL);

  private static final ValueRangeRenderer rendererRoom = new EnumRenderer(ROOM);

  private static final ValueRangeRenderer rendererStage =
    new EnumRenderer(STAGE);

  private static final ValueRangeRenderer rendererChorus =
    new EnumRenderer(CHORUS);

  private static final ValueRangeRenderer rendererCeleste =
    new EnumRenderer(CELESTE);

  private static final ValueRangeRenderer rendererFlanger =
    new EnumRenderer(FLANGER);

  private static final ValueRangeRenderer rendererEarlyRef =
    new EnumRenderer(EARLY_REF);

  private static final ValueRangeRenderer rendererKaraoke =
    new EnumRenderer(KARAOKE);

  private static final ValueRangeRenderer rendererConnection =
    new EnumRenderer(CONNECTION);

  private static final ValueRangeRenderer rendererMonoPolyMode =
    new EnumRenderer(MONO_POLY_MODE);
  private static final SparseType typeMonoPolyMode =
    new SparseType("internal-switch").
    addValueRange(0x0, 0x1, rendererMonoPolyMode);

  private static final ValueRangeRenderer rendererKeyAssign =
    new EnumRenderer(KEY_ASSIGN);

  private static final ValueRangeRenderer rendererKeyOnAssign =
    new EnumRenderer(KEY_ON_ASSIGN);

  private static final ValueRangeRenderer rendererPartMode =
    new EnumRenderer(PART_MODE);

  private static final ValueRangeRenderer rendererDetune0x0 =
    new EnumRenderer(DETUNE_0X0);
  private static final ValueRangeRenderer rendererDetune0x1 =
    new EnumRenderer(DETUNE_0X1);
  private static final ValueRangeRenderer rendererDetune0x2 =
    new EnumRenderer(DETUNE_0X2);
  private static final ValueRangeRenderer rendererDetune0x3 =
    new EnumRenderer(DETUNE_0X3);
  private static final ValueRangeRenderer rendererDetune0x4 =
    new EnumRenderer(DETUNE_0X4);
  private static final ValueRangeRenderer rendererDetune0x5 =
    new EnumRenderer(DETUNE_0X5);
  private static final ValueRangeRenderer rendererDetune0x6 =
    new EnumRenderer(DETUNE_0X6);
  private static final ValueRangeRenderer rendererDetune0x7 =
    new EnumRenderer(DETUNE_0X7);
  private static final ValueRangeRenderer rendererDetune0x8 =
    new EnumRenderer(DETUNE_0X8);
  private static final ValueRangeRenderer rendererDetune0x9 =
    new EnumRenderer(DETUNE_0X9);
  private static final ValueRangeRenderer rendererDetune0xa =
    new EnumRenderer(DETUNE_0XA);
  private static final ValueRangeRenderer rendererDetune0xb =
    new EnumRenderer(DETUNE_0XB);
  private static final ValueRangeRenderer rendererDetune0xc =
    new EnumRenderer(DETUNE_0XC);
  private static final ValueRangeRenderer rendererDetune0xd =
    new EnumRenderer(DETUNE_0XD);
  private static final ValueRangeRenderer rendererDetune0xe =
    new EnumRenderer(DETUNE_0XE);
  private static final ValueRangeRenderer rendererDetune0xf =
    new EnumRenderer(DETUNE_0XF);
  private static final SparseType typeDetune =
    new SparseType("internal-transpose").
    addValueRange(0x0000, 0x000f, rendererDetune0x0).
    addValueRange(0x0080, 0x008f, rendererDetune0x1).
    addValueRange(0x0100, 0x010f, rendererDetune0x2).
    addValueRange(0x0180, 0x018f, rendererDetune0x3).
    addValueRange(0x0200, 0x020f, rendererDetune0x4).
    addValueRange(0x0280, 0x028f, rendererDetune0x5).
    addValueRange(0x0300, 0x030f, rendererDetune0x6).
    addValueRange(0x0380, 0x038f, rendererDetune0x7).
    addValueRange(0x0400, 0x040f, rendererDetune0x8).
    addValueRange(0x0480, 0x048f, rendererDetune0x9).
    addValueRange(0x0500, 0x050f, rendererDetune0xa).
    addValueRange(0x0580, 0x058f, rendererDetune0xb).
    addValueRange(0x0600, 0x060f, rendererDetune0xc).
    addValueRange(0x0680, 0x068f, rendererDetune0xd).
    addValueRange(0x0700, 0x070f, rendererDetune0xe).
    addValueRange(0x0780, 0x078f, rendererDetune0xf);

  private static final ValueRangeRenderer rendererNote = new EnumRenderer(NOTE);
  private static final SparseType typeNote =
    new SparseType("internal-transpose").
    addValueRange(0x00, 0x7f, rendererNote);

  private static final ValueRangeRenderer rendererFilterControl =
    new EnumRenderer(FILTER_CONTROL);
  private static final SparseType typeFilterControl =
    new SparseType("internal-tune").
    addValueRange(0x00, 0x7f, rendererFilterControl);

  private static final ValueRangeRenderer rendererBendLfoModDepth =
    new EnumRenderer(BEND_LFO_MOD_DEPTH);
  private static final SparseType typeBendLfoModDepth =
    new SparseType("internal-control").
    addValueRange(0x00, 0x7f, rendererBendLfoModDepth);

  private static final ValueRangeRenderer rendererSwitch =
    new EnumRenderer(SWITCH);
  private static final SparseType typeSwitch =
    new SparseType("internal-switch").
    addValueRange(0x0, 0x1, rendererSwitch);

  private static final ValueRangeRenderer rendererMonoStereo =
    new EnumRenderer(MONO_STEREO);
  private static final SparseType typeMonoStereo =
    new SparseType("internal-switch").
    addValueRange(0x0, 0x1, rendererMonoStereo);

  private static final ValueRangeRenderer rendererScaleTuning =
    new EnumRenderer(SCALE_TUNING);
  private static final SparseType typeScaleTuning =
    new SparseType("internal-tune").
    addValueRange(0x00, 0x7f, rendererScaleTuning);

  private static final SparseType typeTranspose =
    new SparseType("internal-transpose").
    addValueRange(0x28, 0x58, -0x18, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeReverbType =
    new SparseType("internal-fx-reverb").
    addSingleValue(0x0000, "No Effect").
    addValueRange(0x0080, 0x0081, rendererHall).
    addValueRange(0x0100, 0x0102, rendererRoom).
    addValueRange(0x0180, 0x0181, rendererStage).
    addSingleValue(0x0200, "Plate").
    addSingleValue(0x0800, "White Room").
    addSingleValue(0x0880, "Tunnel").
    addSingleValue(0x0980, "Basement");

  private static final SparseType typeChorusType =
    new SparseType("internal-fx-chorus").
    addSingleValue(0x0000, "No Effect").
    addValueRange(0x2080, 0x2082, rendererChorus).
    addSingleValue(0x2088, "Chorus 4").
    addValueRange(0x2100, 0x2102, rendererCeleste).
    addSingleValue(0x2108, "Celeste 4").
    addValueRange(0x2180, 0x2181, rendererFlanger).
    addSingleValue(0x2188, "Flanger 3");

  private static final SparseType typeVariationType =
    new SparseType("internal-control").
    addSingleValue(0x0000, "No Effect").
    addValueRange(0x0080, 0x0081, rendererHall).
    addValueRange(0x0100, 0x0102, rendererRoom).
    addValueRange(0x0180, 0x0181, rendererStage).
    addSingleValue(0x0200, "Plate").
    addSingleValue(0x0280, "Delay L, C, R").
    addSingleValue(0x0300, "Delay L, R").
    addSingleValue(0x0380, "Echo").
    addSingleValue(0x0400, "Cross Delay").
    addValueRange(0x0480, 0x0481, rendererEarlyRef).
    addSingleValue(0x0500, "Gate Reverb").
    addSingleValue(0x0580, "Reverse Gate").
    addValueRange(0x0a00, 0x0a02, rendererKaraoke).
    addValueRange(0x2080, 0x2082, rendererChorus).
    addSingleValue(0x2088, "Chorus 4").
    addValueRange(0x2100, 0x2102, rendererCeleste).
    addSingleValue(0x2108, "Celeste 4").
    addValueRange(0x2180, 0x2181, rendererFlanger).
    addSingleValue(0x2188, "Flanger 3").
    addSingleValue(0x2200, "Symphonic").
    addSingleValue(0x2280, "Rotary Speaker").
    addSingleValue(0x2300, "Tremolo").
    addSingleValue(0x2380, "Auto Pan").
    addSingleValue(0x2401, "Phaser 1").
    addSingleValue(0x2408, "Phaser 2").
    addSingleValue(0x2480, "Distortion").
    addSingleValue(0x2500, "Over Drive").
    addSingleValue(0x2580, "Amp Simulator").
    addSingleValue(0x2600, "3-Band EQ (Mono)").
    addSingleValue(0x2680, "2-Band EQ (Stereo)").
    addSingleValue(0x2700, "Auto Wah (LFO)").
    addSingleValue(0x2000, "Thru");

  private static final SparseType typePan =
    new SparseType("internal-pan").
    addValueRange(0x01, 0x7f, rendererPan);

  private static final SparseType typePanExtended =
    new SparseType("internal-pan").
    addSingleValue(0x00, "Random").
    addValueRange(0x01, 0x7f, rendererPan);

  private static final SparseType typeLevel =
    new SparseType("internal-volume").
    addValueRange(0x00, 0x7f, rendererLevel);

  private static final SparseType typeVolume =
    new SparseType("internal-volume").
    addValueRange(0x00, 0x7f, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeConnection =
    new SparseType("internal-control").
    addValueRange(0x0, 0x1, rendererConnection);

  private static final EnumRenderer rendererPart = new EnumRenderer(PART);
  private static final SparseType typePart =
    new SparseType("internal-control").
    addValueRange(0x00, 0x0f, rendererPart).
    addSingleValue(0x7f, "Off");

  private static final SparseType typeNonNegative7Bit =
    new SparseType("internal-control").
    addValueRange(0x00, 0x7f, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typePositive7Bit =
    new SparseType("internal-control").
    addValueRange(0x01, 0x7f, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeSigned7Bit =
    new SparseType("internal-control").
    addValueRange(0x00, 0x7f, -0x40, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeControllerNumber =
    new SparseType("internal-control").
    addValueRange(0x00, 0x5f, IntegerRenderer.DEFAULT_RENDERER);

  private static final ValueRangeRenderer lfoFrequency =
    new EnumRenderer(LFO_FREQUENCY);
  private static final SparseType typeLfoFrequency =
    new SparseType("internal-tune").
    addValueRange(0x00, 0x7f, lfoFrequency);

  private static final SparseType typeTune3 =
    new SparseType("internal-tune").
    addValueRange(0x0, 0x0, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeTune2 =
    new SparseType("internal-tune").
    addValueRange(0x0, 0x7, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeTune1 =
    new SparseType("internal-tune").
    addValueRange(0x0, 0xf, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeTune0 =
    new SparseType("internal-tune").
    addValueRange(0x0, 0xf, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeDrumsSetupReset =
    new SparseType("internal-error").
    addValueRange(0x0, 0x1, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeXgOn =
    new SparseType("internal-error").
    addSingleValue(0x0, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeAllReset =
    new SparseType("internal-error").
    addSingleValue(0x0, IntegerRenderer.DEFAULT_RENDERER);

  private FolderNode buildFolderNodeSystem()
  {
    FolderNode nodeSystem = new FolderNode("System");

    final Value tune3 = new ValueImpl(typeTune3, "Master Tune[3]");
    tune3.setBitSize(7);
    tune3.setDefaultValue(0x0);
    nodeSystem.add(new DataNode(tune3));

    final Value tune2 = new ValueImpl(typeTune2, "Master Tune[2]");
    tune2.setBitSize(7);
    tune2.setDefaultValue(0x4);
    nodeSystem.add(new DataNode(tune2));

    final Value tune1 = new ValueImpl(typeTune1, "Master Tune[1]");
    tune1.setBitSize(7);
    tune1.setDefaultValue(0x0);
    nodeSystem.add(new DataNode(tune1));

    final Value tune0 = new ValueImpl(typeTune0, "Master Tune[0]");
    tune0.setBitSize(7);
    tune0.setDefaultValue(0x0);
    nodeSystem.add(new DataNode(tune0));

    final Value volume = new ValueImpl(typeVolume, "Master Volume");
    volume.setBitSize(7);
    volume.setDefaultValue(0x7f);
    nodeSystem.add(new DataNode(volume));

    nodeSystem.add(new DataNode(new ValueImpl(7, "Unused")));

    final Value transpose = new ValueImpl(typeTranspose, "Transpose");
    transpose.setBitSize(7);
    transpose.setDefaultValue(0x40);
    nodeSystem.add(new DataNode(transpose));

    final Value drumsSetupReset =
      new ValueImpl(typeDrumsSetupReset, "Drum Setup Reset",
                    addr2index(0x00, 0x00, 0x7d));
    drumsSetupReset.setBitSize(7);
    drumsSetupReset.setDefaultValue(0x0);
    nodeSystem.add(new DataNode(drumsSetupReset));

    final Value xgOn = new ValueImpl(typeXgOn, "XG System On");
    xgOn.setBitSize(7);
    xgOn.setDefaultValue(0x0);
    nodeSystem.add(new DataNode(xgOn));

    final Value allReset = new ValueImpl(typeAllReset, "All Parameter Reset");
    allReset.setBitSize(7);
    allReset.setDefaultValue(0x0);
    nodeSystem.add(new DataNode(allReset));

    return nodeSystem;
  }

  private static final SparseType typeDiffusion =
    new SparseType("internal-control").
    addValueRange(0x00, 0x0a, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeWallVary =
    new SparseType("internal-control").
    addValueRange(0x00, 0x1e, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeDensity =
    new SparseType("internal-control").
    addValueRange(0x0, 0x3, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeFeedbackLevel =
    new SparseType("internal-volume").
    addValueRange(0x01, 0x7f, -0x3f, IntegerRenderer.DEFAULT_RENDERER);

  private FolderNode buildFolderNodeReverb()
  {
    final FolderNode nodeReverb = new FolderNode("Reverb");

    final Value reverbType = new ValueImpl(typeReverbType, "Reverb Type");
    reverbType.setBitSize(14);
    reverbType.setDefaultValue(0x0080);
    nodeReverb.add(new DataNode(reverbType));

    final Value reverbTime = new ValueImpl(typeReverbTime, "Reverb Time");
    reverbTime.setBitSize(7);
    reverbTime.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(reverbTime));

    final Value diffusion = new ValueImpl(typeDiffusion, "Diffusion");
    diffusion.setBitSize(7);
    diffusion.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(diffusion));

    final Value initialDelay = new ValueImpl(typeDelayTime1, "Initial Delay");
    initialDelay.setBitSize(7);
    initialDelay.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(initialDelay));

    final Value hpfCutoff = new ValueImpl(typeEqFrequency, "HPF Cutoff");
    hpfCutoff.setBitSize(7);
    hpfCutoff.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(hpfCutoff));

    final Value lpfCutoff = new ValueImpl(typeEqFrequency, "LPF Cutoff");
    lpfCutoff.setBitSize(7);
    lpfCutoff.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(lpfCutoff));

    final Value width = new ValueImpl(typeReverbWidth, "Width");
    width.setBitSize(7);
    width.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(width));

    final Value height = new ValueImpl(typeReverbHeight, "Height");
    height.setBitSize(7);
    height.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(height));

    final Value depth = new ValueImpl(typeReverbDepth, "Depth");
    depth.setBitSize(7);
    depth.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(depth));

    final Value wallVary = new ValueImpl(typeWallVary, "Wall Vary");
    wallVary.setBitSize(7);
    wallVary.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(wallVary));

    final Value dryWet = new ValueImpl(typeDryWet, "Dry / Wet");
    dryWet.setBitSize(7);
    dryWet.setDefaultValue(0x40);
    nodeReverb.add(new DataNode(dryWet));

    final Value level = new ValueImpl(typeLevel, "Reverb Return");
    level.setBitSize(7);
    level.setDefaultValue(0x40);
    nodeReverb.add(new DataNode(level));

    final Value pan = new ValueImpl(typePan, "Reverb Pan",
                                    addr2index(0x02, 0x01, 0x10));
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    nodeReverb.add(new DataNode(pan));

    final Value revDelay = new ValueImpl(typeDelayTime1, "Rev Delay");
    revDelay.setBitSize(7);
    revDelay.setDefaultValue(0x00);
    nodeReverb.add(new DataNode(revDelay));

    final Value density = new ValueImpl(typeDensity, "Density");
    density.setBitSize(7);
    density.setDefaultValue(0x0);
    nodeReverb.add(new DataNode(density));

    final Value erRevBalance =
      new ValueImpl(typeErRevBalance, "Er / Rev Balance");
    erRevBalance.setBitSize(7);
    erRevBalance.setDefaultValue(0x40);
    nodeReverb.add(new DataNode(erRevBalance));

    nodeReverb.add(new DataNode(new ValueImpl(7, "Unused")));

    final Value feedbackLevel =
      new ValueImpl(typeFeedbackLevel, "Feedback Level");
    feedbackLevel.setBitSize(7);
    feedbackLevel.setDefaultValue(0x40);
    nodeReverb.add(new DataNode(feedbackLevel));

    nodeReverb.add(new DataNode(new ValueImpl(7, "Unused")));

    return nodeReverb;
  }

  private FolderNode buildFolderNodeChorus()
  {
    final FolderNode nodeChorus =
      new FolderNode("Chorus", addr2index(0x02, 0x01, 0x20));

    final Value chorusType = new ValueImpl(typeChorusType, "Chorus Type");
    chorusType.setBitSize(14);
    chorusType.setDefaultValue(0x2080);
    nodeChorus.add(new DataNode(chorusType));

    final Value lfoFrequency = new ValueImpl(typeLfoFrequency, "LFO Frequency");
    lfoFrequency.setBitSize(7);
    lfoFrequency.setDefaultValue(0x00);
    nodeChorus.add(new DataNode(lfoFrequency));

    final Value lfoPmDepth = new ValueImpl(typeNonNegative7Bit, "LFO PM Depth");
    lfoPmDepth.setBitSize(7);
    lfoPmDepth.setDefaultValue(0x40);
    nodeChorus.add(new DataNode(lfoPmDepth));

    final Value feedbackLevel =
      new ValueImpl(typeFeedbackLevel, "Feedback Level");
    feedbackLevel.setBitSize(7);
    feedbackLevel.setDefaultValue(0x40);
    nodeChorus.add(new DataNode(feedbackLevel));

    final Value modulationDelayOffset =
      new ValueImpl(typeModulationDelayOffset, "Modulation Delay Offset");
    modulationDelayOffset.setBitSize(7);
    modulationDelayOffset.setDefaultValue(0x00);
    nodeChorus.add(new DataNode(modulationDelayOffset));

    nodeChorus.add(new DataNode(new ValueImpl(7, "Unused")));

    final Value eqLowFrequency =
      new ValueImpl(typeEqFrequency, "EQ Low Frequency");
    eqLowFrequency.setBitSize(7);
    eqLowFrequency.setDefaultValue(0x08);
    nodeChorus.add(new DataNode(eqLowFrequency));

    final Value eqLowGain = new ValueImpl(typeEqGain, "EQ Low Gain");
    eqLowGain.setBitSize(7);
    eqLowGain.setDefaultValue(0x40);
    nodeChorus.add(new DataNode(eqLowGain));

    final Value eqHighFrequency =
      new ValueImpl(typeEqFrequency, "EQ High Frequency");
    eqHighFrequency.setBitSize(7);
    eqHighFrequency.setDefaultValue(0x3a);
    nodeChorus.add(new DataNode(eqHighFrequency));

    final Value eqHighGain = new ValueImpl(typeEqGain, "EQ High Gain");
    eqHighGain.setBitSize(7);
    eqHighGain.setDefaultValue(0x40);
    nodeChorus.add(new DataNode(eqHighGain));

    final Value dryWet = new ValueImpl(typeDryWet, "Dry / Wet");
    dryWet.setBitSize(7);
    dryWet.setDefaultValue(0x40);
    nodeChorus.add(new DataNode(dryWet));

    final Value level = new ValueImpl(typeLevel, "Chorus Return");
    level.setBitSize(7);
    level.setDefaultValue(0x40);
    nodeChorus.add(new DataNode(level));

    final Value pan = new ValueImpl(typePan, "Chorus Pan");
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    nodeChorus.add(new DataNode(pan));

    final Value levelReverb = new ValueImpl(typeLevel, "Send Chorus To Reverb");
    levelReverb.setBitSize(7);
    levelReverb.setDefaultValue(0x00);
    nodeChorus.add(new DataNode(levelReverb));

    nodeChorus.add(new DataNode(new ValueImpl(7, "Unused",
                                              addr2index(0x02, 0x01, 0x30))));
    nodeChorus.add(new DataNode(new ValueImpl(7, "Unused")));
    nodeChorus.add(new DataNode(new ValueImpl(7, "Unused")));

    final Value lfoPhaseDifference =
      new ValueImpl(typePhaseDifference, "LFO Phase Difference");
    lfoPhaseDifference.setBitSize(7);
    lfoPhaseDifference.setDefaultValue(0x40);
    nodeChorus.add(new DataNode(lfoPhaseDifference));

    final Value monoStereo = new ValueImpl(typeMonoStereo, "Mono / Stereo");
    monoStereo.setBitSize(7);
    monoStereo.setDefaultValue(0x1);
    nodeChorus.add(new DataNode(monoStereo));

    nodeChorus.add(new DataNode(new ValueImpl(7, "Unused")));

    return nodeChorus;
  }

  private FolderNode buildFolderNodeVariation()
  {
    final FolderNode nodeVariation =
      new FolderNode("Variation", addr2index(0x02, 0x01, 0x40));

    final Value variationType =
      new ValueImpl(typeVariationType, "Variation Type");
    variationType.setBitSize(14);
    variationType.setDefaultValue(0x0280);
    nodeVariation.add(new DataNode(variationType));

    for (int i = 0; i < 10; i++) {
      final Value value7BitMsb =
        new ValueImpl(typeNonNegative7Bit,
                      "Variation Parameter " + (i + 1) + " MSB");
      value7BitMsb.setBitSize(7);
      value7BitMsb.setDefaultValue(0x00);
      nodeVariation.add(new DataNode(value7BitMsb));
      final Value value7BitLsb =
        new ValueImpl(typeNonNegative7Bit,
                      "Variation Parameter " + (i + 1) + " LSB");
      value7BitLsb.setBitSize(7);
      value7BitLsb.setDefaultValue(0x00);
      nodeVariation.add(new DataNode(value7BitLsb));
    }

    final Value level = new ValueImpl(typeLevel, "Variation Return");
    level.setBitSize(7);
    level.setDefaultValue(0x40);
    nodeVariation.add(new DataNode(level));

    final Value pan = new ValueImpl(typePan, "Variation Pan");
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    nodeVariation.add(new DataNode(pan));

    final Value levelReverb =
      new ValueImpl(typeLevel, "Send Variation To Reverb");
    levelReverb.setBitSize(7);
    levelReverb.setDefaultValue(0x00);
    nodeVariation.add(new DataNode(levelReverb));

    final Value levelChorus =
      new ValueImpl(typeLevel, "Send Variation To Chorus");
    levelChorus.setBitSize(7);
    levelChorus.setDefaultValue(0x00);
    nodeVariation.add(new DataNode(levelChorus));

    final Value connection =
      new ValueImpl(typeConnection, "Variation Connection");
    connection.setBitSize(7);
    connection.setDefaultValue(0x0);
    nodeVariation.add(new DataNode(connection));

    final Value part = new ValueImpl(typePart, "Variation Part");
    part.setBitSize(7);
    part.setDefaultValue(0x7f);
    nodeVariation.add(new DataNode(part));

    final Value mw =
      new ValueImpl(typeSigned7Bit, "MW Variation Control Depth");
    mw.setBitSize(7);
    mw.setDefaultValue(0x40);
    nodeVariation.add(new DataNode(mw));

    final Value bend =
      new ValueImpl(typeSigned7Bit, "Bend Variation Control Depth");
    bend.setBitSize(7);
    bend.setDefaultValue(0x40);
    nodeVariation.add(new DataNode(bend));

    final Value cat =
      new ValueImpl(typeSigned7Bit, "CAT Variation Control Depth");
    cat.setBitSize(7);
    cat.setDefaultValue(0x40);
    nodeVariation.add(new DataNode(cat));

    final Value ac1 =
      new ValueImpl(typeSigned7Bit, "AC1 Variation Control Depth");
    ac1.setBitSize(7);
    ac1.setDefaultValue(0x40);
    nodeVariation.add(new DataNode(ac1));

    final Value ac2 =
      new ValueImpl(typeSigned7Bit, "AC2 Variation Control Depth");
    ac2.setBitSize(7);
    ac2.setDefaultValue(0x40);
    nodeVariation.add(new DataNode(ac2));

    for (int i = 10; i < 16; i++) {
      final long desiredAddress = i == 10 ? addr2index(0x02, 0x01, 0x70) : -1;
      final Value value7Bit =
        new ValueImpl(typeNonNegative7Bit, "Variation Parameter " + (i + 1),
                      desiredAddress);
      value7Bit.setBitSize(7);
      value7Bit.setDefaultValue(0x00);
      nodeVariation.add(new DataNode(value7Bit));
    }

    return nodeVariation;
  }

  private FolderNode buildFolderNodeEffect1()
  {
    final FolderNode nodeEffect =
      new FolderNode("Effect1", addr2index(0x02, 0x01, 0x00));
    nodeEffect.add(buildFolderNodeReverb());
    nodeEffect.add(buildFolderNodeChorus());
    nodeEffect.add(buildFolderNodeVariation());
    return nodeEffect;
  }

  private void buildPartControl(final FolderNode node,
                                final String displayPrefix,
                                final int defaultPModDepth)
  {
    final Value pitchControl =
      new ValueImpl(typeTranspose, displayPrefix + " Pitch Control");
    pitchControl.setBitSize(7);
    pitchControl.setDefaultValue(0x40);
    node.add(new DataNode(pitchControl));

    final Value filterControl =
      new ValueImpl(typeFilterControl, displayPrefix + " Filter Control");
    filterControl.setBitSize(7);
    filterControl.setDefaultValue(0x40);
    node.add(new DataNode(filterControl));

    final Value amplitudeControl =
      new ValueImpl(typeSigned7Bit, displayPrefix + " Amplitude Control");
    amplitudeControl.setBitSize(7);
    amplitudeControl.setDefaultValue(0x40);
    node.add(new DataNode(amplitudeControl));

    final Value lfoPModDepth =
      new ValueImpl(typeNonNegative7Bit, displayPrefix + " LFO PMod Depth");
    lfoPModDepth.setBitSize(7);
    lfoPModDepth.setDefaultValue(defaultPModDepth);
    node.add(new DataNode(lfoPModDepth));

    final Value lfoFModDepth =
      new ValueImpl("internal-mod-fm", typeNonNegative7Bit,
                    displayPrefix + " LFO FMod Depth");
    lfoFModDepth.setBitSize(7);
    lfoFModDepth.setDefaultValue(0x00);
    node.add(new DataNode(lfoFModDepth));

    final Value lfoAModDepth =
      new ValueImpl("internal-mod-am", typeNonNegative7Bit,
                    displayPrefix + " LFO AMod Depth");
    lfoAModDepth.setBitSize(7);
    lfoAModDepth.setDefaultValue(0x00);
    node.add(new DataNode(lfoAModDepth));
  }

  private static final SparseType typeElementReserve =
    new SparseType("internal-control").
    addValueRange(0x00, 0x1f, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeProgramNumber =
    new SparseType("internal-control").
    addValueRange(0x00, 0x7f, 0x01, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeRcvChannel =
    new SparseType("internal-control").
    addValueRange(0x00, 0x0f, 0x01, IntegerRenderer.DEFAULT_RENDERER).
    addSingleValue(0x10, "Off");

  private static final SparseType typeKeyOnAssign =
    new SparseType("internal-control").
    addValueRange(0x0, 0x2, rendererKeyOnAssign);

  private static final SparseType typePartMode =
    new SparseType("internal-control").
    addValueRange(0x0, 0x3, rendererPartMode);

  private FolderNode buildFolderNodeMultiPartN(final int n)
  {
    final FolderNode nodeMultiPartN =
      new FolderNode("Multi Part " + (n + 1), addr2index(0x08, n, 0x00));

    final Value elementReserve =
      new ValueImpl(typeElementReserve, "Element Reserve");
    elementReserve.setBitSize(7);
    elementReserve.setDefaultValue(n == 9 ? 0x00 : 0x02);
    nodeMultiPartN.add(new DataNode(elementReserve));

    final Value bankSelectMsb =
      new ValueImpl(typeNonNegative7Bit, "Bank Select MSB");
    bankSelectMsb.setBitSize(7);
    bankSelectMsb.setDefaultValue(n == 9 ? 0x7f : 0x00);
    nodeMultiPartN.add(new DataNode(bankSelectMsb));

    final Value bankSelectLsb =
      new ValueImpl(typeNonNegative7Bit, "Bank Select LSB");
    bankSelectLsb.setBitSize(7);
    bankSelectLsb.setDefaultValue(0x00);
    nodeMultiPartN.add(new DataNode(bankSelectLsb));

    final Value programNumber =
      new ValueImpl(typeProgramNumber, "Program Number");
    programNumber.setBitSize(7);
    programNumber.setDefaultValue(0x00);
    nodeMultiPartN.add(new DataNode(programNumber));

    final Value rcvChannel =
      new ValueImpl(typeRcvChannel, "Rcv Channel");
    rcvChannel.setBitSize(7);
    rcvChannel.setDefaultValue(n);
    nodeMultiPartN.add(new DataNode(rcvChannel));

    final Value monoPolyMode =
      new ValueImpl(typeMonoPolyMode, "Mono / Poly Mode");
    monoPolyMode.setBitSize(7);
    monoPolyMode.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(monoPolyMode));

    final Value keyOnAssign =
      new ValueImpl(typeKeyOnAssign, "Same Not Number Key on Assign");
    keyOnAssign.setBitSize(7);
    keyOnAssign.setDefaultValue(n == 9 ? 0x2 : 0x0);
    nodeMultiPartN.add(new DataNode(keyOnAssign));

    final Value partMode = new ValueImpl(typePartMode, "Part Mode");
    partMode.setBitSize(7);
    partMode.setDefaultValue(n == 9 ? 0x2 : 0x0);
    nodeMultiPartN.add(new DataNode(partMode));

    final Value noteShift = new ValueImpl(typeTranspose, "Note Shift");
    noteShift.setBitSize(7);
    noteShift.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(noteShift));

    final Value detune = new ValueImpl(typeDetune, "Detune");
    detune.setBitSize(14);
    detune.setDefaultValue(0x0400);
    nodeMultiPartN.add(new DataNode(detune));

    final Value volume = new ValueImpl(typeVolume, "Volume");
    volume.setBitSize(7);
    volume.setDefaultValue(0x64);
    nodeMultiPartN.add(new DataNode(volume));

    final Value velocitySenseDepth =
      new ValueImpl(typeNonNegative7Bit, "Velocity Sense Depth");
    velocitySenseDepth.setBitSize(7);
    velocitySenseDepth.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(velocitySenseDepth));

    final Value velocitySenseOffset =
      new ValueImpl(typeNonNegative7Bit, "Velocity Sense Offset");
    velocitySenseOffset.setBitSize(7);
    velocitySenseOffset.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(velocitySenseOffset));

    final Value pan = new ValueImpl(typePanExtended, "Pan");
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(pan));

    final Value noteLimitLow =
      new ValueImpl("internal-limit-bottom", typeNote, "Note Limit Low");
    noteLimitLow.setBitSize(7);
    noteLimitLow.setDefaultValue(0x00);
    nodeMultiPartN.add(new DataNode(noteLimitLow));

    final Value noteLimitHigh =
      new ValueImpl("internal-limit-top", typeNote, "Note Limit High");
    noteLimitHigh.setBitSize(7);
    noteLimitHigh.setDefaultValue(0x7f);
    nodeMultiPartN.add(new DataNode(noteLimitHigh));

    final Value dryLevel = new ValueImpl(typeVolume, "Dry Level");
    dryLevel.setBitSize(7);
    dryLevel.setDefaultValue(0x7f);
    nodeMultiPartN.add(new DataNode(dryLevel));

    final Value chorusSend = new ValueImpl(typeVolume, "Chorus Send");
    chorusSend.setBitSize(7);
    chorusSend.setDefaultValue(0x00);
    nodeMultiPartN.add(new DataNode(chorusSend));

    final Value reverbSend = new ValueImpl(typeVolume, "Reverb Send");
    reverbSend.setBitSize(7);
    reverbSend.setDefaultValue(0x28);
    nodeMultiPartN.add(new DataNode(reverbSend));

    final Value variationSend = new ValueImpl(typeVolume, "Variation Send");
    variationSend.setBitSize(7);
    variationSend.setDefaultValue(0x00);
    nodeMultiPartN.add(new DataNode(variationSend));

    final Value vibratoRate = new ValueImpl(typeSigned7Bit, "Vibrato Rate");
    vibratoRate.setBitSize(7);
    vibratoRate.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(vibratoRate));

    final Value vibratoDepth = new ValueImpl(typeSigned7Bit, "Vibrato Depth");
    vibratoDepth.setBitSize(7);
    vibratoDepth.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(vibratoDepth));

    final Value vibratoDelay = new ValueImpl(typeSigned7Bit, "Vibrato Delay");
    vibratoDelay.setBitSize(7);
    vibratoDelay.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(vibratoDelay));

    final Value filterCutoffFrequency =
      new ValueImpl("internal-tune", typeSigned7Bit, "Filter Cutoff Frequency");
    filterCutoffFrequency.setBitSize(7);
    filterCutoffFrequency.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(filterCutoffFrequency));

    final Value filterResonance =
      new ValueImpl(typeSigned7Bit, "Filter Resonance");
    filterResonance.setBitSize(7);
    filterResonance.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(filterResonance));

    final Value egAttackTime =
      new ValueImpl("internal-time", typeSigned7Bit, "EG Attack Time");
    egAttackTime.setBitSize(7);
    egAttackTime.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(egAttackTime));

    final Value egDecayTime =
      new ValueImpl("internal-time", typeSigned7Bit, "EG Decay Time");
    egDecayTime.setBitSize(7);
    egDecayTime.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(egDecayTime));

    final Value egReleaseTime =
      new ValueImpl("internal-time", typeSigned7Bit, "EG Release Time");
    egReleaseTime.setBitSize(7);
    egReleaseTime.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(egReleaseTime));

    buildPartControl(nodeMultiPartN, "MW", 0x0a);

    final Value bendPitchControl =
      new ValueImpl(typeTranspose, "Bend Pitch Control");
    bendPitchControl.setBitSize(7);
    bendPitchControl.setDefaultValue(0x42);
    nodeMultiPartN.add(new DataNode(bendPitchControl));

    final Value bendFilterControl =
      new ValueImpl(typeFilterControl, "Bend Filter Control");
    bendFilterControl.setBitSize(7);
    bendFilterControl.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(bendFilterControl));

    final Value bendAmplitudeControl =
      new ValueImpl(typeSigned7Bit, "Bend Amplitude Control");
    bendAmplitudeControl.setBitSize(7);
    bendAmplitudeControl.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(bendAmplitudeControl));

    final Value bendLfoPModDepth =
      new ValueImpl(typeBendLfoModDepth, "Bend LFO PMod Depth");
    bendLfoPModDepth.setBitSize(7);
    bendLfoPModDepth.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(bendLfoPModDepth));

    final Value bendLfoFModDepth =
      new ValueImpl("internal-mod-fm", typeBendLfoModDepth,
                    "Bend LFO FMod Depth");
    bendLfoFModDepth.setBitSize(7);
    bendLfoFModDepth.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(bendLfoFModDepth));

    final Value bendLfoAModDepth =
      new ValueImpl("internal-mod-am", typeBendLfoModDepth,
                    "Bend LFO AMod Depth");
    bendLfoAModDepth.setBitSize(7);
    bendLfoAModDepth.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(bendLfoAModDepth));

    final Value rcvPitchBend =
      new ValueImpl(typeSwitch, "Rcv Pitch Bend", addr2index(0x08, n, 0x30));
    rcvPitchBend.setBitSize(7);
    rcvPitchBend.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvPitchBend));

    final Value rcvChAfterTouch =
      new ValueImpl(typeSwitch, "Rcv Ch After Touch (CAT)");
    rcvChAfterTouch.setBitSize(7);
    rcvChAfterTouch.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvChAfterTouch));

    final Value rcvProgramChange =
      new ValueImpl(typeSwitch, "Rcv Program Change");
    rcvProgramChange.setBitSize(7);
    rcvProgramChange.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvProgramChange));

    final Value rcvControlChange =
      new ValueImpl(typeSwitch, "Rcv Control Change");
    rcvControlChange.setBitSize(7);
    rcvControlChange.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvControlChange));

    final Value rcvPolyAfterTouch =
      new ValueImpl(typeSwitch, "Rcv Poly After Touch (PAT)");
    rcvPolyAfterTouch.setBitSize(7);
    rcvPolyAfterTouch.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvChAfterTouch));

    final Value rcvNoteMessage = new ValueImpl(typeSwitch, "Rcv Note Message");
    rcvNoteMessage.setBitSize(7);
    rcvNoteMessage.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvNoteMessage));

    final Value rcvRpn = new ValueImpl(typeSwitch, "Rcv RPN");
    rcvRpn.setBitSize(7);
    rcvRpn.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvRpn));

    final Value rcvNrpn = new ValueImpl(typeSwitch, "Rcv NRPN");
    rcvNrpn.setBitSize(7);
    rcvNrpn.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvNrpn));

    final Value rcvModulation = new ValueImpl(typeSwitch, "Rcv Modulation");
    rcvModulation.setBitSize(7);
    rcvModulation.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvModulation));

    final Value rcvVolume = new ValueImpl(typeSwitch, "Rcv Volume");
    rcvVolume.setBitSize(7);
    rcvVolume.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvVolume));

    final Value rcvPan = new ValueImpl(typeSwitch, "Rcv Pan");
    rcvPan.setBitSize(7);
    rcvPan.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvPan));

    final Value rcvExpression = new ValueImpl(typeSwitch, "Rcv Expression");
    rcvExpression.setBitSize(7);
    rcvExpression.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvExpression));

    final Value rcvHold1 = new ValueImpl(typeSwitch, "Rcv Hold1");
    rcvHold1.setBitSize(7);
    rcvHold1.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvHold1));

    final Value rcvPortamento = new ValueImpl(typeSwitch, "Rcv Portamento");
    rcvPortamento.setBitSize(7);
    rcvPortamento.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvPortamento));

    final Value rcvSostenuto = new ValueImpl(typeSwitch, "Rcv Sostenuto");
    rcvSostenuto.setBitSize(7);
    rcvSostenuto.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvSostenuto));

    final Value rcvSoftPedal = new ValueImpl(typeSwitch, "Rcv Soft Pedal");
    rcvSoftPedal.setBitSize(7);
    rcvSoftPedal.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvSoftPedal));

    final Value rcvBankSelect = new ValueImpl(typeSwitch, "Rcv Bank Select");
    rcvBankSelect.setBitSize(7);
    rcvBankSelect.setDefaultValue(0x1);
    nodeMultiPartN.add(new DataNode(rcvBankSelect));

    final Value scaleTuningC =
      new ValueImpl("internal-tune-c", typeScaleTuning, "Scale Tuning C");
    scaleTuningC.setBitSize(7);
    scaleTuningC.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningC));

    final Value scaleTuningCSharp =
      new ValueImpl("internal-tune-c-sharp", typeScaleTuning,
                    "Scale Tuning C#");
    scaleTuningCSharp.setBitSize(7);
    scaleTuningCSharp.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningCSharp));

    final Value scaleTuningD =
      new ValueImpl("internal-tune-d", typeScaleTuning, "Scale Tuning D");
    scaleTuningD.setBitSize(7);
    scaleTuningD.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningD));

    final Value scaleTuningDSharp =
      new ValueImpl("internal-tune-d-sharp", typeScaleTuning,
                    "Scale Tuning D#");
    scaleTuningDSharp.setBitSize(7);
    scaleTuningDSharp.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningDSharp));

    final Value scaleTuningE =
      new ValueImpl("internal-tune-e", typeScaleTuning, "Scale Tuning E");
    scaleTuningE.setBitSize(7);
    scaleTuningE.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningE));

    final Value scaleTuningF =
      new ValueImpl("internal-tune-f", typeScaleTuning, "Scale Tuning F");
    scaleTuningF.setBitSize(7);
    scaleTuningF.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningF));

    final Value scaleTuningFSharp =
      new ValueImpl("internal-tune-f-sharp", typeScaleTuning,
                    "Scale Tuning F#");
    scaleTuningFSharp.setBitSize(7);
    scaleTuningFSharp.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningFSharp));

    final Value scaleTuningG =
      new ValueImpl("internal-tune-g", typeScaleTuning, "Scale Tuning G");
    scaleTuningG.setBitSize(7);
    scaleTuningG.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningG));

    final Value scaleTuningGSharp =
      new ValueImpl("internal-tune-g-sharp", typeScaleTuning,
                    "Scale Tuning G#");
    scaleTuningGSharp.setBitSize(7);
    scaleTuningGSharp.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningGSharp));

    final Value scaleTuningA =
      new ValueImpl("internal-tune-a", typeScaleTuning, "Scale Tuning A");
    scaleTuningA.setBitSize(7);
    scaleTuningA.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningA));

    final Value scaleTuningASharp =
      new ValueImpl("internal-tune-a-sharp",typeScaleTuning, "Scale Tuning A#");
    scaleTuningASharp.setBitSize(7);
    scaleTuningASharp.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningASharp));

    final Value scaleTuningB =
      new ValueImpl("internal-tune-b", typeScaleTuning, "Scale Tuning B");
    scaleTuningB.setBitSize(7);
    scaleTuningB.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(scaleTuningB));

    buildPartControl(nodeMultiPartN, "CAT", 0x00);

    buildPartControl(nodeMultiPartN, "PAT", 0x00);

    final Value ac1ControllerNumber =
      new ValueImpl(typeControllerNumber, "AC1 Controller Number");
    ac1ControllerNumber.setBitSize(7);
    ac1ControllerNumber.setDefaultValue(0x10);
    nodeMultiPartN.add(new DataNode(ac1ControllerNumber));
    buildPartControl(nodeMultiPartN, "AC1", 0x00);

    final Value ac2ControllerNumber =
      new ValueImpl(typeControllerNumber, "AC2 Controller Number");
    ac2ControllerNumber.setBitSize(7);
    ac2ControllerNumber.setDefaultValue(0x11);
    nodeMultiPartN.add(new DataNode(ac2ControllerNumber));
    buildPartControl(nodeMultiPartN, "AC2", 0x00);

    final Value portamentoSwitch =
      new ValueImpl(typeSwitch, "Portamento Switch");
    portamentoSwitch.setBitSize(7);
    portamentoSwitch.setDefaultValue(0x0);
    nodeMultiPartN.add(new DataNode(portamentoSwitch));

    final Value portamentoTime =
      new ValueImpl("internal-time", typeNonNegative7Bit, "Portamento Time");
    portamentoTime.setBitSize(7);
    portamentoTime.setDefaultValue(0x00);
    nodeMultiPartN.add(new DataNode(portamentoTime));

    final Value pitchEgInitialLevel =
      new ValueImpl("internal-volume", typeSigned7Bit,
                    "Pitch EG Initial Level");
    pitchEgInitialLevel.setBitSize(7);
    pitchEgInitialLevel.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(pitchEgInitialLevel));

    final Value pitchEgAttackTime =
      new ValueImpl("internal-time", typeSigned7Bit, "Pitch EG Attack Time");
    pitchEgAttackTime.setBitSize(7);
    pitchEgAttackTime.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(pitchEgAttackTime));

    final Value pitchEgReleaseLevel =
      new ValueImpl("internal-volume", typeSigned7Bit,
                    "Pitch EG Release Level");
    pitchEgReleaseLevel.setBitSize(7);
    pitchEgReleaseLevel.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(pitchEgReleaseLevel));

    final Value pitchEgReleaseTime =
      new ValueImpl("internal-time",typeSigned7Bit, "Pitch EG Release Time");
    pitchEgReleaseTime.setBitSize(7);
    pitchEgReleaseTime.setDefaultValue(0x40);
    nodeMultiPartN.add(new DataNode(pitchEgReleaseTime));

    final Value velocityLimitLow =
      new ValueImpl("internal-limit-bottom", typePositive7Bit,
                    "Velocity Limit Low");
    velocityLimitLow.setBitSize(7);
    velocityLimitLow.setDefaultValue(0x01);
    nodeMultiPartN.add(new DataNode(velocityLimitLow));

    final Value velocityLimitHigh =
      new ValueImpl("internal-limit-top", typePositive7Bit,
                    "Velocity Limit High");
    velocityLimitHigh.setBitSize(7);
    velocityLimitHigh.setDefaultValue(0x7f);
    nodeMultiPartN.add(new DataNode(velocityLimitHigh));

    return nodeMultiPartN;
  }

  private FolderNode buildFolderNodeMultiPart()
  {
    final FolderNode nodeMultiPart =
      new FolderNode("Multi Part", addr2index(0x08, 0x00, 0x00));
    for (int n = 0; n < 16; n++) {
      nodeMultiPart.add(buildFolderNodeMultiPartN(n));
    }
    return nodeMultiPart;
  }

  private static final SparseType typeAlternativeGroup =
    new SparseType("internal-control").
    addSingleValue(0x00, "Off").
    addValueRange(0x01, 0x7f, IntegerRenderer.DEFAULT_RENDERER);

  private static final SparseType typeKeyAssign =
    new SparseType("internal-control").
    addValueRange(0x00, 0x01, rendererKeyAssign);

  private FolderNode buildFolderNodeDrumSetupNoteNR(final int n, final int r)
  {
    final FolderNode nodeDrumSetupNoteR =
      new FolderNode("Drum Setup Note " + r, addr2index(0x30 + n, r, 0x00));

    final Value pitchCoarse =
      new ValueImpl("internal-transpose", typeSigned7Bit, "Pitch Coarse");
    pitchCoarse.setBitSize(7);
    pitchCoarse.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(pitchCoarse));

    final Value pitchFine = new ValueImpl(typeScaleTuning, "Pitch Fine");
    pitchFine.setBitSize(7);
    pitchFine.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(pitchFine));

    final Value level = new ValueImpl(typeVolume, "Level");
    level.setBitSize(7);
    level.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(level));

    final Value alternativeGroup =
      new ValueImpl(typeAlternativeGroup, "Alternative Group");
    alternativeGroup.setBitSize(7);
    alternativeGroup.setDefaultValue(n);
    nodeDrumSetupNoteR.add(new DataNode(alternativeGroup));

    final Value pan = new ValueImpl(typePanExtended, "Pan");
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(pan));

    final Value reverbSend = new ValueImpl(typeVolume, "Reverb Send");
    reverbSend.setBitSize(7);
    reverbSend.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(reverbSend));

    final Value chorusSend = new ValueImpl(typeVolume, "Chorus Send");
    chorusSend.setBitSize(7);
    chorusSend.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(chorusSend));

    final Value variationSend = new ValueImpl(typeVolume, "Variation Send");
    variationSend.setBitSize(7);
    variationSend.setDefaultValue(0x7f);
    nodeDrumSetupNoteR.add(new DataNode(variationSend));

    final Value keyAssign = new ValueImpl(typeKeyAssign, "Key Assign");
    keyAssign.setBitSize(7);
    keyAssign.setDefaultValue(0x0);
    nodeDrumSetupNoteR.add(new DataNode(keyAssign));

    final Value rcvNoteOff = new ValueImpl(typeSwitch, "Rcv Note Off");
    rcvNoteOff.setBitSize(7);
    rcvNoteOff.setDefaultValue(0x0);
    nodeDrumSetupNoteR.add(new DataNode(rcvNoteOff));

    final Value rcvNoteOn = new ValueImpl(typeSwitch, "Rcv Note On");
    rcvNoteOn.setBitSize(7);
    rcvNoteOn.setDefaultValue(0x0);
    nodeDrumSetupNoteR.add(new DataNode(rcvNoteOn));

    final Value filterCutoffFrequency =
      new ValueImpl("internal-tune", typeSigned7Bit, "Filter Cutoff Frequency");
    filterCutoffFrequency.setBitSize(7);
    filterCutoffFrequency.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(filterCutoffFrequency));

    final Value filterResonance =
      new ValueImpl(typeSigned7Bit, "Filter Resonance");
    filterResonance.setBitSize(7);
    filterResonance.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(filterResonance));

    final Value egAttackRate = new ValueImpl(typeSigned7Bit, "EG Attack Rate");
    egAttackRate.setBitSize(7);
    egAttackRate.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(egAttackRate));

    final Value egDecay1Rate = new ValueImpl(typeSigned7Bit, "EG Decay1 Rate");
    egDecay1Rate.setBitSize(7);
    egDecay1Rate.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(egDecay1Rate));

    final Value egDecay2Rate = new ValueImpl(typeSigned7Bit, "EG Decay2 Rate");
    egDecay2Rate.setBitSize(7);
    egDecay2Rate.setDefaultValue(0x40);
    nodeDrumSetupNoteR.add(new DataNode(egDecay2Rate));

    return nodeDrumSetupNoteR;
  }

  private FolderNode buildFolderNodeDrumSetupN(final int n)
  {
    final FolderNode nodeDrumSetupN =
      new FolderNode("Drum Setup " + n, addr2index(0x30 + n, 0x00, 0x00));
    for (int r = 0x0d; r < 0x5c; r++) {
      nodeDrumSetupN.add(buildFolderNodeDrumSetupNoteNR(n, r));
    }
    return nodeDrumSetupN;
  }

  private FolderNode buildFolderNodeDrumSetup()
  {
    final FolderNode nodeDrumSetup =
      new FolderNode("Drum Setup", addr2index(0x30, 0x00, 0x00));
    for (int n = 0x0; n < 0x2; n++) {
      nodeDrumSetup.add(buildFolderNodeDrumSetupN(n));
    }
    return nodeDrumSetup;
  }

  public void buildMap(final MapRoot root)
  {
    root.add(buildFolderNodeSystem());
    root.add(buildFolderNodeEffect1());
    root.add(buildFolderNodeMultiPart());
    root.add(buildFolderNodeDrumSetup());
  }

  private class BulkStream extends InputStream
  {
    private final byte deviceId;
    private final int byteStart;
    private final int byteCount;
    private final byte hi;
    private final byte mid;
    private final byte lo;
    private final long end;
    private long pos;
    private int extrapos;
    private int checkSum;
    private DataNode dataNode;

    private BulkStream()
    {
      throw new UnsupportedOperationException();
    }

    BulkStream(final byte deviceId, final MapNode root,
               final long start, final long end)
      throws IOException
    {
      if (root == null)
        throw new NullPointerException("root");
      if (start > end)
        throw new IOException("start > end");
      if (start < 0)
        throw new IOException("start < 0");
      if (end > 7 * 0x3fffff)
        throw new IOException("end > 7 * 0x3fffff");
      this.deviceId = deviceId;
      byteStart = (int)(start / 7);
      byteCount = (int)((end - start + 6) / 7);
      hi = (byte)((byteStart >> 14) & 0x7f);
      mid = (byte)((byteStart >> 7) & 0x7f);
      lo = (byte)(byteStart & 0x7f);
      pos = start;
      this.end = end;
      extrapos = -8;
      checkSum = 0x00;
      dataNode = root.locate(pos);
    }

    private int nextHeaderByte()
    {
      final int data;
      switch (extrapos) {
      case -8:
        data = MANUFACTURER_ID;
        break;
      case -7:
        data = deviceId;
        break;
      case -6:
        data = MODEL_ID;
        break;
      case -5:
        data = byteCount >> 7;
        break;
      case -4:
        data = byteCount & 0x7f;
        break;
      case -3:
        data = hi;
        break;
      case -2:
        data = mid;
        break;
      case -1:
        data = lo;
        break;
      default:
        throw new IllegalStateException("invalid extrapos");
      }
      if (extrapos >= -5) {
        checkSum = (checkSum + data) & 0x7f;
      }
      extrapos++;
      return data;
    }

    private int nextTailByte()
    {
      final int data;
      switch (extrapos) {
      case 0:
        checkSum = (0x100 - checkSum) & 0x7f;
        data = checkSum;
        break;
      default:
        throw new IllegalStateException("invalid extrapos");
      }
      extrapos++;
      return data;
    }

    private int nextBulkDumpByte()
    {
      dataNode = dataNode.locate(pos);
      // TODO: Fix performance: dataNode.getData() will call
      // dataNode.locate() once again, although we already know the
      // node.
      final Integer data[] = dataNode.getData(pos, 7);
      checkSum = (checkSum + data[0]) & 0x7f;
      pos += 7;
      return data[0];
    }

    public int read() throws IOException
    {
      if (pos < end)
        if (extrapos >= 0)
          return nextBulkDumpByte();
        else
          return nextHeaderByte();
      else
        if (extrapos < 1) // tail data
          return nextTailByte();
        else
          return -1; // EOF
    }
  }

  public InputStream bulkDump(final Value deviceId, final MapNode root,
                              final long start, final long end)
  {
    try {
      return new BulkStream((byte)deviceId.getNumericalValue(),
                            root, start, end);
    } catch (final IOException e) {
      throw new IllegalStateException("failed creating bulk stream: " +
                                      e.getMessage(), e);
    }
  }

  /**
   * Given an InputStream that represents a sequence of bulk dumped MIDI
   * bytes from the MIDI device, this method interprets the MIDI data and
   * updates the memory map accordingly.
   * @param in The InputStream of MIDI bytes to be interpreted.
   */
  public void bulkRead(final InputStream in)
  {
    // TODO
  }

  /**
   * Returns descriptive name of the device(s) (for headlines etc.)
   */
  public String getName()
  {
    return DEVICE_NAME;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
