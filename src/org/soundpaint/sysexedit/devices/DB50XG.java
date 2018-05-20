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
import org.soundpaint.sysexedit.model.Value;
import org.soundpaint.sysexedit.model.ValueImpl;
import org.soundpaint.sysexedit.model.MapNode;
import org.soundpaint.sysexedit.model.SparseType;
import org.soundpaint.sysexedit.model.SparseTypeImpl;
import org.soundpaint.sysexedit.model.ValueRangeRenderer;
import org.soundpaint.sysexedit.model.EnumType;
import org.soundpaint.sysexedit.model.Int8Type;
import org.soundpaint.sysexedit.model.AddressRepresentation;

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
  private static final String[] DETUNE_0x0 =
  {
    "-12.8Hz", "-12.7Hz", "-12.6Hz", "-12.5Hz",
    "-12.4Hz", "-12.3Hz", "-12.2Hz", "-12.1Hz",
    "-12.0Hz", "-11.9Hz", "-11.8Hz", "-11.7Hz",
    "-11.6Hz", "-11.5Hz", "-11.4Hz", "-11.3Hz"
  };

  /**
   * Detune_0x1
   */
  private static final String[] DETUNE_0x1 =
  {
    "-11.2Hz", "-11.1Hz", "-11.0Hz", "-10.9Hz",
    "-10.8Hz", "-10.7Hz", "-10.6Hz", "-10.5Hz",
    "-10.4Hz", "-10.3Hz", "-10.2Hz", "-10.1Hz",
    "-10.0Hz", "-9.9Hz", "-9.8Hz", "-9.7Hz"
  };

  /**
   * Detune_0x2
   */
  private static final String[] DETUNE_0x2 =
  {
    "-9.6Hz", "-9.5Hz", "-9.4Hz", "-9.3Hz",
    "-9.2Hz", "-9.1Hz", "-9.0Hz", "-8.9Hz",
    "-8.8Hz", "-8.7Hz", "-8.6Hz", "-8.5Hz",
    "-8.4Hz", "-8.3Hz", "-8.2Hz", "-8.1Hz"
  };

  /**
   * Detune_0x3
   */
  private static final String[] DETUNE_0x3 =
  {
    "-8.0Hz", "-7.9Hz", "-7.8Hz", "-7.7Hz",
    "-7.6Hz", "-7.5Hz", "-7.4Hz", "-7.3Hz",
    "-7.2Hz", "-7.1Hz", "-7.0Hz", "-6.9Hz",
    "-6.8Hz", "-6.7Hz", "-6.6Hz", "-6.5Hz"
  };

  /**
   * Detune_0x4
   */
  private static final String[] DETUNE_0x4 =
  {
    "-6.4Hz", "-6.3Hz", "-6.2Hz", "-6.1Hz",
    "-6.0Hz", "-5.9Hz", "-5.8Hz", "-5.7Hz",
    "-5.6Hz", "-5.5Hz", "-5.4Hz", "-5.3Hz",
    "-5.2Hz", "-5.1Hz", "-5.0Hz", "-4.9Hz"
  };

  /**
   * Detune_0x5
   */
  private static final String[] DETUNE_0x5 =
  {
    "-4.8Hz", "-4.7Hz", "-4.6Hz", "-4.5Hz",
    "-4.4Hz", "-4.3Hz", "-4.2Hz", "-4.1Hz",
    "-4.0Hz", "-3.9Hz", "-3.8Hz", "-3.7Hz",
    "-3.6Hz", "-3.5Hz", "-3.4Hz", "-3.3Hz"
  };

  /**
   * Detune_0x6
   */
  private static final String[] DETUNE_0x6 =
  {
    "-3.2Hz", "-3.1Hz", "-3.0Hz", "-2.9Hz",
    "-2.8Hz", "-2.7Hz", "-2.6Hz", "-2.5Hz",
    "-2.4Hz", "-2.3Hz", "-2.2Hz", "-2.1Hz",
    "-2.0Hz", "-1.9Hz", "-1.8Hz", "-1.7Hz"
  };

  /**
   * Detune_0x7
   */
  private static final String[] DETUNE_0x7 =
  {
    "-1.6Hz", "-1.5Hz", "-1.4Hz", "-1.3Hz",
    "-1.2Hz", "-1.1Hz", "-1.0Hz", "-0.9Hz",
    "-0.8Hz", "-0.7Hz", "-0.6Hz", "-0.5Hz",
    "-0.4Hz", "-0.3Hz", "-0.2Hz", "-0.1Hz"
  };

  /**
   * Detune_0x8
   */
  private static final String[] DETUNE_0x8 =
  {
    "0.0Hz", "0.1Hz", "0.2Hz", "0.3Hz",
    "0.4Hz", "0.5Hz", "0.6Hz", "0.7Hz",
    "0.8Hz", "0.9Hz", "1.0Hz", "1.1Hz",
    "1.2Hz", "1.3Hz", "1.4Hz", "1.5Hz"
  };

  /**
   * Detune_0x9
   */
  private static final String[] DETUNE_0x9 =
  {
    "1.6Hz", "1.7Hz", "1.8Hz", "1.9Hz",
    "2.0Hz", "2.1Hz", "2.2Hz", "2.3Hz",
    "2.4Hz", "2.5Hz", "2.6Hz", "2.7Hz",
    "2.8Hz", "2.9Hz", "3.0Hz", "3.1Hz"
  };

  /**
   * Detune_0xa
   */
  private static final String[] DETUNE_0xa =
  {
    "3.2Hz", "3.3Hz", "3.4Hz", "3.5Hz",
    "3.6Hz", "3.7Hz", "3.8Hz", "3.9Hz",
    "4.0Hz", "4.1Hz", "4.2Hz", "4.3Hz",
    "4.4Hz", "4.5Hz", "4.6Hz", "4.7Hz"
  };

  /**
   * Detune_0xb
   */
  private static final String[] DETUNE_0xb =
  {
    "4.8Hz", "4.9Hz", "5.0Hz", "5.1Hz",
    "5.2Hz", "5.3Hz", "5.4Hz", "5.5Hz",
    "5.6Hz", "5.7Hz", "5.8Hz", "5.9Hz",
    "6.0Hz", "6.1Hz", "6.2Hz", "6.3Hz"
  };

  /**
   * Detune_0xc
   */
  private static final String[] DETUNE_0xc =
  {
    "6.4Hz", "6.5Hz", "6.6Hz", "6.7Hz",
    "6.8Hz", "6.9Hz", "7.0Hz", "7.1Hz",
    "7.2Hz", "7.3Hz", "7.4Hz", "7.5Hz",
    "7.6Hz", "7.7Hz", "7.8Hz", "7.9Hz"
  };

  /**
   * Detune_0xd
   */
  private static final String[] DETUNE_0xd =
  {
    "8.0Hz", "8.1Hz", "8.2Hz", "8.3Hz",
    "8.4Hz", "8.5Hz", "8.6Hz", "8.7Hz",
    "8.8Hz", "8.9Hz", "9.0Hz", "9.1Hz",
    "9.2Hz", "9.3Hz", "9.4Hz", "9.5Hz"
  };

  /**
   * Detune_0xe
   */
  private static final String[] DETUNE_0xe =
  {
    "9.6Hz", "9.7Hz", "9.8Hz", "9.9Hz",
    "10.0Hz", "10.1Hz", "10.2Hz", "10.3Hz",
    "10.4Hz", "10.5Hz", "10.6Hz", "10.7Hz",
    "10.8Hz", "10.9Hz", "11.0Hz", "11.1Hz"
  };

  /**
   * Detune_0xf
   */
  private static final String[] DETUNE_0xf =
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
    new SparseTypeImpl("internal-control").
    addValueRange(0x0, 0xf, new Int8Type(0, 16, true, "0x", "", (byte)4));

  /**
   * Returns a Value object that represents the MIDI device model ID
   * of this device.  As the DB50XG specs do not explicitly specify
   * this value, we assume default value 0.
   */
  public Value createDeviceId()
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

  private class DB50XGAddressRepresentation implements AddressRepresentation
  {
    /**
     * Returns a string representation for the given address.
     * @param address The index in the bit array of the memory map.
     * @return A string representation for the given address.
     */
    public String addressToString(long address)
    {
      final byte bitpos = (byte)(address % 7);
      address /= 7;
      final byte hi = (byte)((address >> 14) & 0x7f);
      final byte mid = (byte)((address >> 7) & 0x7f);
      final byte lo = (byte)(address & 0x7f);
      final String bitpos_str = bitpos == 0 ? "" : " [" + bitpos + "]";
      return
        two_digits(Integer.toString(hi, 16)) + " " +
        two_digits(Integer.toString(mid,16)) + " " +
        two_digits(Integer.toString(lo,16)) +
        bitpos_str;
    }
  }

  /**
   * Returns an AddressRepresentation object that defines how addresses
   * are to be displayed to the user.
   */
  public AddressRepresentation getAddressRepresentation()
  {
    return new DB50XGAddressRepresentation();
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

  private static final ValueRangeRenderer
    enumType_pan = new EnumType(1, PAN);

  private static final ValueRangeRenderer
    enumType_level = new EnumType(LEVEL);

  private static final ValueRangeRenderer
    enumType_modulation_delay_offset = new EnumType(MODULATION_DELAY_OFFSET);
  private static final SparseType sparseType_modulation_delay_offset =
    new SparseTypeImpl("internal-time").
    addValueRange(0x00, 0x7f, enumType_modulation_delay_offset);

  private static final ValueRangeRenderer
    enumType_eq_frequency = new EnumType(EQ_FREQUENCY);
  private static final SparseType sparseType_eq_frequency =
    new SparseTypeImpl("internal-tune").
    addValueRange(0x00, 0x3c, enumType_eq_frequency);

  private static final ValueRangeRenderer
    enumType_reverb_time = new EnumType(REVERB_TIME);
  private static final SparseType sparseType_reverb_time =
    new SparseTypeImpl("internal-time").
    addValueRange(0x00, 0x45, enumType_reverb_time);

  private static final ValueRangeRenderer
    enumType_delay_time_1 = new EnumType(DELAY_TIME_1);
  private static final SparseType sparseType_delay_time_1 =
    new SparseTypeImpl("internal-time").
    addValueRange(0x00, 0x7f, enumType_delay_time_1);

  private static final ValueRangeRenderer
    enumType_room_size = new EnumType(ROOM_SIZE);

  private static final ValueRangeRenderer
    enumType_delay_time_2 = new EnumType(DELAY_TIME_2);
  private static final SparseType sparseType_delay_time_2 =
    new SparseTypeImpl("internal-time").
    addValueRange(0x00, 0x7f, enumType_delay_time_2);

  private static final ValueRangeRenderer
    enumType_reverb_dim_length = new EnumType(REVERB_DIM_LENGTH);
  private static final SparseType sparseType_reverb_width =
    new SparseTypeImpl("internal-length").
    addValueRange(0x00, 0x25, enumType_reverb_dim_length);
  private static final SparseType sparseType_reverb_height =
    new SparseTypeImpl("internal-length").
    addValueRange(0x00, 0x49, enumType_reverb_dim_length);
  private static final SparseType sparseType_reverb_depth =
    new SparseTypeImpl("internal-length").
    addValueRange(0x00, 0x68, enumType_reverb_dim_length);

  private static final ValueRangeRenderer
    enumType_dry_wet = new EnumType(0x01, DRY_WET);
  private static final SparseType sparseType_dry_wet =
    new SparseTypeImpl("internal-pan").
    addValueRange(0x01, 0x7f, enumType_dry_wet);

  private static final ValueRangeRenderer
    enumType_er_rev_balance = new EnumType(0x01, ER_REV_BALANCE);
  private static final SparseType sparseType_er_rev_balance =
    new SparseTypeImpl("internal-pan").
    addValueRange(0x01, 0x7f, enumType_er_rev_balance);

  private static final ValueRangeRenderer
    enumType_eq_gain = new EnumType(0x34, EQ_GAIN);
  private static final SparseType sparseType_eq_gain =
    new SparseTypeImpl("internal-volume").
    addValueRange(0x34, 0x4c, enumType_eq_gain);

  private static final ValueRangeRenderer
    enumType_phase_difference = new EnumType(0x04, PHASE_DIFFERENCE);
  private static final SparseType sparseType_phase_difference =
    new SparseTypeImpl("internal-control").
    addValueRange(0x4, 0x7c, enumType_phase_difference);

  private static final ValueRangeRenderer
    enumType_hall = new EnumType(0x0080, HALL);

  private static final ValueRangeRenderer
    enumType_room = new EnumType(0x0100, ROOM);

  private static final ValueRangeRenderer
    enumType_stage = new EnumType(0x0180, STAGE);

  private static final ValueRangeRenderer
    enumType_chorus = new EnumType(0x2080, CHORUS);

  private static final ValueRangeRenderer
    enumType_celeste = new EnumType(0x2100, CELESTE);

  private static final ValueRangeRenderer
    enumType_flanger = new EnumType(0x2180, FLANGER);

  private static final ValueRangeRenderer
    enumType_early_ref = new EnumType(0x0480, EARLY_REF);

  private static final ValueRangeRenderer
    enumType_karaoke = new EnumType(0x0a00, KARAOKE);

  private static final ValueRangeRenderer
    enumType_connection = new EnumType(CONNECTION);

  private static final ValueRangeRenderer
    enumType_mono_poly_mode = new EnumType(MONO_POLY_MODE);
  private static final SparseType sparseType_mono_poly_mode =
    new SparseTypeImpl("internal-switch").
    addValueRange(0x0, 0x1, enumType_mono_poly_mode);

  private static final ValueRangeRenderer
    enumType_key_assign = new EnumType(KEY_ASSIGN);

  private static final ValueRangeRenderer
    enumType_key_on_assign = new EnumType(KEY_ON_ASSIGN);

  private static final ValueRangeRenderer
    enumType_part_mode = new EnumType(PART_MODE);

  private static final ValueRangeRenderer
    enumType_detune_0x0 = new EnumType(0x0000, DETUNE_0x0);
  private static final ValueRangeRenderer
    enumType_detune_0x1 = new EnumType(0x0080, DETUNE_0x1);
  private static final ValueRangeRenderer
    enumType_detune_0x2 = new EnumType(0x0100, DETUNE_0x2);
  private static final ValueRangeRenderer
    enumType_detune_0x3 = new EnumType(0x0180, DETUNE_0x3);
  private static final ValueRangeRenderer
    enumType_detune_0x4 = new EnumType(0x0200, DETUNE_0x4);
  private static final ValueRangeRenderer
    enumType_detune_0x5 = new EnumType(0x0280, DETUNE_0x5);
  private static final ValueRangeRenderer
    enumType_detune_0x6 = new EnumType(0x0300, DETUNE_0x6);
  private static final ValueRangeRenderer
    enumType_detune_0x7 = new EnumType(0x0380, DETUNE_0x7);
  private static final ValueRangeRenderer
    enumType_detune_0x8 = new EnumType(0x0400, DETUNE_0x8);
  private static final ValueRangeRenderer
    enumType_detune_0x9 = new EnumType(0x0480, DETUNE_0x9);
  private static final ValueRangeRenderer
    enumType_detune_0xa = new EnumType(0x0500, DETUNE_0xa);
  private static final ValueRangeRenderer
    enumType_detune_0xb = new EnumType(0x0580, DETUNE_0xb);
  private static final ValueRangeRenderer
    enumType_detune_0xc = new EnumType(0x0600, DETUNE_0xc);
  private static final ValueRangeRenderer
    enumType_detune_0xd = new EnumType(0x0680, DETUNE_0xd);
  private static final ValueRangeRenderer
    enumType_detune_0xe = new EnumType(0x0700, DETUNE_0xe);
  private static final ValueRangeRenderer
    enumType_detune_0xf = new EnumType(0x0780, DETUNE_0xf);
  private static final SparseType sparseType_detune =
    new SparseTypeImpl("internal-transpose").
    addValueRange(0x0000, 0x000f, enumType_detune_0x0).
    addValueRange(0x0080, 0x008f, enumType_detune_0x1).
    addValueRange(0x0100, 0x010f, enumType_detune_0x2).
    addValueRange(0x0180, 0x018f, enumType_detune_0x3).
    addValueRange(0x0200, 0x020f, enumType_detune_0x4).
    addValueRange(0x0280, 0x028f, enumType_detune_0x5).
    addValueRange(0x0300, 0x030f, enumType_detune_0x6).
    addValueRange(0x0380, 0x038f, enumType_detune_0x7).
    addValueRange(0x0400, 0x040f, enumType_detune_0x8).
    addValueRange(0x0480, 0x048f, enumType_detune_0x9).
    addValueRange(0x0500, 0x050f, enumType_detune_0xa).
    addValueRange(0x0580, 0x058f, enumType_detune_0xb).
    addValueRange(0x0600, 0x060f, enumType_detune_0xc).
    addValueRange(0x0680, 0x068f, enumType_detune_0xd).
    addValueRange(0x0700, 0x070f, enumType_detune_0xe).
    addValueRange(0x0780, 0x078f, enumType_detune_0xf);

  private static final ValueRangeRenderer
    enumType_note = new EnumType(NOTE);
  private static final SparseType sparseType_note =
    new SparseTypeImpl("internal-transpose").
    addValueRange(0x00, 0x7f, enumType_note);

  private static final ValueRangeRenderer
    enumType_filter_control = new EnumType(FILTER_CONTROL);
  private static final SparseType sparseType_filter_control =
    new SparseTypeImpl("internal-tune").
    addValueRange(0x00, 0x7f, enumType_filter_control);

  private static final ValueRangeRenderer
    enumType_bend_lfo_mod_depth = new EnumType(BEND_LFO_MOD_DEPTH);
  private static final SparseType sparseType_bend_lfo_mod_depth =
    new SparseTypeImpl("internal-control").
    addValueRange(0x00, 0x7f, enumType_bend_lfo_mod_depth);

  private static final ValueRangeRenderer
    enumType_switch = new EnumType(SWITCH);
  private static final SparseType sparseType_switch =
    new SparseTypeImpl("internal-switch").
    addValueRange(0x0, 0x1, enumType_switch);

  private static final ValueRangeRenderer
    enumType_mono_stereo = new EnumType(MONO_STEREO);
  private static final SparseType sparseType_mono_stereo =
    new SparseTypeImpl("internal-switch").
    addValueRange(0x0, 0x1, enumType_mono_stereo);

  private static final ValueRangeRenderer
    enumType_scale_tuning = new EnumType(SCALE_TUNING);
  private static final SparseType sparseType_scale_tuning =
    new SparseTypeImpl("internal-tune").
    addValueRange(0x00, 0x7f, enumType_scale_tuning);

  private static final SparseType sparseType_transpose =
    new SparseTypeImpl("internal-transpose").
    addValueRange(0x28, 0x58, new Int8Type(0x40));

  private static final SparseType sparseType_reverb_type =
    new SparseTypeImpl("internal-fx-reverb").
    addSingleValue(0x0000, "No Effect").
    addValueRange(0x0080, 0x0081, enumType_hall).
    addValueRange(0x0100, 0x0102, enumType_room).
    addValueRange(0x0180, 0x0181, enumType_stage).
    addSingleValue(0x0200, "Plate").
    addSingleValue(0x0800, "White Room").
    addSingleValue(0x0880, "Tunnel").
    addSingleValue(0x0980, "Basement");

  private static final SparseType sparseType_chorus_type =
    new SparseTypeImpl("internal-fx-chorus").
    addSingleValue(0x0000, "No Effect").
    addValueRange(0x2080, 0x2082, enumType_chorus).
    addSingleValue(0x2088, "Chorus 4").
    addValueRange(0x2100, 0x2102, enumType_celeste).
    addSingleValue(0x2108, "Celeste 4").
    addValueRange(0x2180, 0x2181, enumType_flanger).
    addSingleValue(0x2188, "Flanger 3");

  private static final SparseType sparseType_variation_type =
    new SparseTypeImpl("internal-control").
    addSingleValue(0x0000, "No Effect").
    addValueRange(0x0080, 0x0081, enumType_hall).
    addValueRange(0x0100, 0x0102, enumType_room).
    addValueRange(0x0180, 0x0181, enumType_stage).
    addSingleValue(0x0200, "Plate").
    addSingleValue(0x0280, "Delay L, C, R").
    addSingleValue(0x0300, "Delay L, R").
    addSingleValue(0x0380, "Echo").
    addSingleValue(0x0400, "Cross Delay").
    addValueRange(0x0480, 0x0481, enumType_early_ref).
    addSingleValue(0x0500, "Gate Reverb").
    addSingleValue(0x0580, "Reverse Gate").
    addValueRange(0x0a00, 0x0a02, enumType_karaoke).
    addValueRange(0x2080, 0x2082, enumType_chorus).
    addSingleValue(0x2088, "Chorus 4").
    addValueRange(0x2100, 0x2102, enumType_celeste).
    addSingleValue(0x2108, "Celeste 4").
    addValueRange(0x2180, 0x2181, enumType_flanger).
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

  private static final SparseType sparseType_pan =
    new SparseTypeImpl("internal-pan").
    addValueRange(0x01, 0x7f, enumType_pan);

  private static final SparseType sparseType_pan_extended =
    new SparseTypeImpl("internal-pan").
    addSingleValue(0x00, "Random").
    addValueRange(0x01, 0x7f, enumType_pan);

  private static final SparseType sparseType_level =
    new SparseTypeImpl("internal-volume").
    addValueRange(0x00, 0x7f, enumType_level);

  private static final SparseType sparseType_volume =
    new SparseTypeImpl("internal-volume").
    addValueRange(0x00, 0x7f, Int8Type.defaultInstance);

  private static final SparseType sparseType_connection =
    new SparseTypeImpl("internal-control").
    addValueRange(0x0, 0x1, enumType_connection);

  private static final EnumType enumType_part = new EnumType(PART);
  private static final SparseType sparseType_part =
    new SparseTypeImpl("internal-control").
    addValueRange(0x00, 0x0f, enumType_part).
    addSingleValue(0x7f, "Off");

  private static final SparseType sparseType_non_negative_7bit =
    new SparseTypeImpl("internal-control").
    addValueRange(0x00, 0x7f, Int8Type.defaultInstance);

  private static final SparseType sparseType_positive_7bit =
    new SparseTypeImpl("internal-control").
    addValueRange(0x01, 0x7f, Int8Type.defaultInstance);

  private static final SparseType sparseType_signed_7bit =
    new SparseTypeImpl("internal-control").
    addValueRange(0x00, 0x7f, new Int8Type(0x40));

  private static final SparseType sparseType_controller_number =
    new SparseTypeImpl("internal-control").
    addValueRange(0x00, 0x5f, Int8Type.defaultInstance);

  private static final ValueRangeRenderer lfo_frequency =
    new EnumType(LFO_FREQUENCY);
  private static final SparseType sparseType_lfo_frequency =
    new SparseTypeImpl("internal-tune").
    addValueRange(0x00, 0x7f, lfo_frequency);

  private MapNode buildMapNodeSystem()
  {
    MapNode node_system = new MapNode("System");

    final SparseType sparseType_tune_3 =
      new SparseTypeImpl("internal-tune").
      addValueRange(0x0, 0x0, Int8Type.defaultInstance);
    final Value tune_3 = new ValueImpl(sparseType_tune_3);
    tune_3.setBitSize(7);
    tune_3.setDefaultValue(0x0);
    node_system.add(new MapNode("Master Tune[3]", tune_3));

    final SparseType sparseType_tune_2 =
      new SparseTypeImpl("internal-tune").
      addValueRange(0x0, 0x7, Int8Type.defaultInstance);
    final Value tune_2 = new ValueImpl(sparseType_tune_2);
    tune_2.setBitSize(7);
    tune_2.setDefaultValue(0x4);
    node_system.add(new MapNode("Master Tune[2]", tune_2));

    final SparseType sparseType_tune_1 =
      new SparseTypeImpl("internal-tune").
      addValueRange(0x0, 0xf, Int8Type.defaultInstance);
    final Value tune_1 = new ValueImpl(sparseType_tune_1);
    tune_1.setBitSize(7);
    tune_1.setDefaultValue(0x0);
    node_system.add(new MapNode("Master Tune[1]", tune_1));

    final SparseType sparseType_tune_0 =
      new SparseTypeImpl("internal-tune").
      addValueRange(0x0, 0xf, Int8Type.defaultInstance);
    final Value tune_0 = new ValueImpl(sparseType_tune_0);
    tune_0.setBitSize(7);
    tune_0.setDefaultValue(0x0);
    node_system.add(new MapNode("Master Tune[0]", tune_0));

    final Value volume = new ValueImpl(sparseType_volume);
    volume.setBitSize(7);
    volume.setDefaultValue(0x7f);
    node_system.add(new MapNode("Master Volume", volume));

    node_system.add(new MapNode("Unused", new ValueImpl(7)));

    final Value transpose = new ValueImpl(sparseType_transpose);
    transpose.setBitSize(7);
    transpose.setDefaultValue(0x40);
    node_system.add(new MapNode("Transpose", transpose));

    final SparseType sparseType_drums_setup_reset =
      new SparseTypeImpl("internal-error").
      addValueRange(0x0, 0x1, Int8Type.defaultInstance);
    final Value drums_setup_reset = new ValueImpl(sparseType_drums_setup_reset);
    drums_setup_reset.setBitSize(7);
    drums_setup_reset.setDefaultValue(0x0);
    node_system.add(new MapNode("Drum Setup Reset", drums_setup_reset,
                                addr2index(0x00, 0x00, 0x7d)));

    final SparseType sparseType_xg_on =
      new SparseTypeImpl("internal-error").
      addSingleValue(0x0, Int8Type.defaultInstance);
    final Value xg_on = new ValueImpl(sparseType_xg_on);
    xg_on.setBitSize(7);
    xg_on.setDefaultValue(0x0);
    node_system.add(new MapNode("XG System On", xg_on));

    final SparseType sparseType_all_reset =
      new SparseTypeImpl("internal-error").
      addSingleValue(0x0, Int8Type.defaultInstance);
    final Value all_reset = new ValueImpl(sparseType_all_reset);
    all_reset.setBitSize(7);
    all_reset.setDefaultValue(0x0);
    node_system.add(new MapNode("All Parameter Reset", all_reset));

    return node_system;
  }

  private MapNode buildMapNodeReverb()
  {
    final MapNode node_reverb = new MapNode("Reverb");

    final Value reverb_type = new ValueImpl(sparseType_reverb_type);
    reverb_type.setBitSize(14);
    reverb_type.setDefaultValue(0x0080);
    node_reverb.add(new MapNode("Reverb Type", reverb_type));

    final Value reverb_time = new ValueImpl(sparseType_reverb_time);
    reverb_time.setBitSize(7);
    reverb_time.setDefaultValue(0x00);
    node_reverb.add(new MapNode("Reverb Time", reverb_time));

    final SparseType sparseType_diffusion =
      new SparseTypeImpl("internal-control").
      addValueRange(0x00, 0x0a, Int8Type.defaultInstance);
    final Value diffusion = new ValueImpl(sparseType_diffusion);
    diffusion.setBitSize(7);
    diffusion.setDefaultValue(0x00);
    node_reverb.add(new MapNode("Diffusion", diffusion));

    final Value initial_delay = new ValueImpl(sparseType_delay_time_1);
    initial_delay.setBitSize(7);
    initial_delay.setDefaultValue(0x00);
    node_reverb.add(new MapNode("Initial Delay", initial_delay));

    final Value hpf_cutoff = new ValueImpl(sparseType_eq_frequency);
    hpf_cutoff.setBitSize(7);
    hpf_cutoff.setDefaultValue(0x00);
    node_reverb.add(new MapNode("HPF Cutoff", hpf_cutoff));

    final Value lpf_cutoff = new ValueImpl(sparseType_eq_frequency);
    lpf_cutoff.setBitSize(7);
    lpf_cutoff.setDefaultValue(0x00);
    node_reverb.add(new MapNode("LPF Cutoff", lpf_cutoff));

    final Value width = new ValueImpl(sparseType_reverb_width);
    width.setBitSize(7);
    width.setDefaultValue(0x00);
    node_reverb.add(new MapNode("Width", width));

    final Value height = new ValueImpl(sparseType_reverb_height);
    height.setBitSize(7);
    height.setDefaultValue(0x00);
    node_reverb.add(new MapNode("Height", height));

    final Value depth = new ValueImpl(sparseType_reverb_depth);
    depth.setBitSize(7);
    depth.setDefaultValue(0x00);
    node_reverb.add(new MapNode("Depth", depth));

    final SparseType sparseType_wall_vary =
      new SparseTypeImpl("internal-control").
      addValueRange(0x00, 0x1e, Int8Type.defaultInstance);
    final Value wall_vary = new ValueImpl(sparseType_wall_vary);
    wall_vary.setBitSize(7);
    wall_vary.setDefaultValue(0x00);
    node_reverb.add(new MapNode("Wall Vary", wall_vary));

    final Value dry_wet = new ValueImpl(sparseType_dry_wet);
    dry_wet.setBitSize(7);
    dry_wet.setDefaultValue(0x40);
    node_reverb.add(new MapNode("Dry / Wet", dry_wet));

    final Value level = new ValueImpl(sparseType_level);
    level.setBitSize(7);
    level.setDefaultValue(0x40);
    node_reverb.add(new MapNode("Reverb Return", level));

    final Value pan = new ValueImpl(sparseType_pan);
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    node_reverb.add(new MapNode("Reverb Pan", pan));

    final Value rev_delay = new ValueImpl(sparseType_delay_time_1);
    rev_delay.setBitSize(7);
    rev_delay.setDefaultValue(0x00);
    node_reverb.add(new MapNode("Rev Delay", rev_delay,
                                addr2index(0x02, 0x01, 0x10)));

    final SparseType sparseType_density =
      new SparseTypeImpl("internal-control").
      addValueRange(0x0, 0x3, Int8Type.defaultInstance);
    final Value density = new ValueImpl(sparseType_density);
    density.setBitSize(7);
    density.setDefaultValue(0x0);
    node_reverb.add(new MapNode("Density", density));

    final Value er_rev_balance = new ValueImpl(sparseType_er_rev_balance);
    er_rev_balance.setBitSize(7);
    er_rev_balance.setDefaultValue(0x40);
    node_reverb.add(new MapNode("Er / Rev Balance", er_rev_balance));

    node_reverb.add(new MapNode("Unused", new ValueImpl(7)));

    final SparseType sparseType_feedback_level =
      new SparseTypeImpl("internal-volume").
      addValueRange(0x01, 0x7f, new Int8Type(0x40));
    final Value feedback_level = new ValueImpl(sparseType_feedback_level);
    feedback_level.setBitSize(7);
    feedback_level.setDefaultValue(0x40);
    node_reverb.add(new MapNode("Feedback Level", feedback_level));

    node_reverb.add(new MapNode("Unused", new ValueImpl(7)));

    return node_reverb;
  }

  private MapNode buildMapNodeChorus()
  {
    final MapNode node_chorus =
      new MapNode("Chorus", addr2index(0x02, 0x01, 0x20));

    final Value chorus_type = new ValueImpl(sparseType_chorus_type);
    chorus_type.setBitSize(14);
    chorus_type.setDefaultValue(0x2080);
    node_chorus.add(new MapNode("Chorus Type", chorus_type));

    final Value lfo_frequency = new ValueImpl(sparseType_lfo_frequency);
    lfo_frequency.setBitSize(7);
    lfo_frequency.setDefaultValue(0x00);
    node_chorus.add(new MapNode("LFO Frequency", lfo_frequency));

    final Value lfo_pm_depth = new ValueImpl(sparseType_non_negative_7bit);
    lfo_pm_depth.setBitSize(7);
    lfo_pm_depth.setDefaultValue(0x40);
    node_chorus.add(new MapNode("LFO PM Depth", lfo_pm_depth));

    final SparseType sparseType_feedback_level =
      new SparseTypeImpl("internal-volume").
      addValueRange(0x01, 0x7f, new Int8Type(0x40));
    final Value feedback_level = new ValueImpl(sparseType_feedback_level);
    feedback_level.setBitSize(7);
    feedback_level.setDefaultValue(0x40);
    node_chorus.add(new MapNode("Feedback Level", feedback_level));

    final Value modulation_delay_offset =
      new ValueImpl(sparseType_modulation_delay_offset);
    modulation_delay_offset.setBitSize(7);
    modulation_delay_offset.setDefaultValue(0x00);
    node_chorus.add(new MapNode("Modulation Delay Offset",
                                modulation_delay_offset));

    node_chorus.add(new MapNode("Unused", new ValueImpl(7)));

    final Value eq_low_frequency = new ValueImpl(sparseType_eq_frequency);
    eq_low_frequency.setBitSize(7);
    eq_low_frequency.setDefaultValue(0x08);
    node_chorus.add(new MapNode("EQ Low Frequency", eq_low_frequency));

    final Value eq_low_gain = new ValueImpl(sparseType_eq_gain);
    eq_low_gain.setBitSize(7);
    eq_low_gain.setDefaultValue(0x40);
    node_chorus.add(new MapNode("EQ Low Gain", eq_low_gain));

    final Value eq_high_frequency = new ValueImpl(sparseType_eq_frequency);
    eq_high_frequency.setBitSize(7);
    eq_high_frequency.setDefaultValue(0x3a);
    node_chorus.add(new MapNode("EQ High Frequency", eq_high_frequency));

    final Value eq_high_gain = new ValueImpl(sparseType_eq_gain);
    eq_high_gain.setBitSize(7);
    eq_high_gain.setDefaultValue(0x40);
    node_chorus.add(new MapNode("EQ High Gain", eq_high_gain));

    final Value dry_wet = new ValueImpl(sparseType_dry_wet);
    dry_wet.setBitSize(7);
    dry_wet.setDefaultValue(0x40);
    node_chorus.add(new MapNode("Dry / Wet", dry_wet));

    final Value level = new ValueImpl(sparseType_level);
    level.setBitSize(7);
    level.setDefaultValue(0x40);
    node_chorus.add(new MapNode("Chorus Return", level));

    final Value pan = new ValueImpl(sparseType_pan);
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    node_chorus.add(new MapNode("Chorus Pan", pan));

    final Value level_reverb = new ValueImpl(sparseType_level);
    level_reverb.setBitSize(7);
    level_reverb.setDefaultValue(0x00);
    node_chorus.add(new MapNode("Send Chorus To Reverb", level_reverb));

    node_chorus.add(new MapNode("Unused", new ValueImpl(7),
                                addr2index(0x02, 0x01, 0x30)));
    node_chorus.add(new MapNode("Unused", new ValueImpl(7)));
    node_chorus.add(new MapNode("Unused", new ValueImpl(7)));

    final Value lfo_phase_difference =
      new ValueImpl(sparseType_phase_difference);
    lfo_phase_difference.setBitSize(7);
    lfo_phase_difference.setDefaultValue(0x40);
    node_chorus.add(new MapNode("LFO Phase Difference", lfo_phase_difference));

    final Value mono_stereo = new ValueImpl(sparseType_mono_stereo);
    mono_stereo.setBitSize(7);
    mono_stereo.setDefaultValue(0x1);
    node_chorus.add(new MapNode("Mono / Stereo", mono_stereo));

    node_chorus.add(new MapNode("Unused", new ValueImpl(7)));

    return node_chorus;
  }

  private MapNode buildMapNodeVariation()
  {
    final MapNode node_variation =
      new MapNode("Variation", addr2index(0x02, 0x01, 0x40));

    final Value variation_type = new ValueImpl(sparseType_variation_type);
    variation_type.setBitSize(14);
    variation_type.setDefaultValue(0x0280);
    node_variation.add(new MapNode("Variation Type", variation_type));

    for (int i = 0; i < 10; i++) {
      final Value value_7bit_msb = new ValueImpl(sparseType_non_negative_7bit);
      value_7bit_msb.setBitSize(7);
      value_7bit_msb.setDefaultValue(0x00);
      node_variation.add(new MapNode("Variation Parameter " + (i + 1) + " MSB",
                                     value_7bit_msb));
      final Value value_7bit_lsb = new ValueImpl(sparseType_non_negative_7bit);
      value_7bit_lsb.setBitSize(7);
      value_7bit_lsb.setDefaultValue(0x00);
      node_variation.add(new MapNode("Variation Parameter " + (i + 1) + " LSB",
                                     value_7bit_lsb));
    }

    final Value level = new ValueImpl(sparseType_level);
    level.setBitSize(7);
    level.setDefaultValue(0x40);
    node_variation.add(new MapNode("Variation Return", level));

    final Value pan = new ValueImpl(sparseType_pan);
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    node_variation.add(new MapNode("Variation Pan", pan));

    final Value level_reverb = new ValueImpl(sparseType_level);
    level_reverb.setBitSize(7);
    level_reverb.setDefaultValue(0x00);
    node_variation.add(new MapNode("Send Variation To Reverb", level_reverb));

    final Value level_chorus = new ValueImpl(sparseType_level);
    level_chorus.setBitSize(7);
    level_chorus.setDefaultValue(0x00);
    node_variation.add(new MapNode("Send Variation To Chorus", level_chorus));

    final Value connection = new ValueImpl(sparseType_connection);
    connection.setBitSize(7);
    connection.setDefaultValue(0x0);
    node_variation.add(new MapNode("Variation Connection", connection));

    final Value part = new ValueImpl(sparseType_part);
    part.setBitSize(7);
    part.setDefaultValue(0x7f);
    node_variation.add(new MapNode("Variation Part", part));

    final Value mw = new ValueImpl(sparseType_signed_7bit);
    mw.setBitSize(7);
    mw.setDefaultValue(0x40);
    node_variation.add(new MapNode("MW Variation Control Depth", mw));

    final Value bend = new ValueImpl(sparseType_signed_7bit);
    bend.setBitSize(7);
    bend.setDefaultValue(0x40);
    node_variation.add(new MapNode("Bend Variation Control Depth", bend));

    final Value cat = new ValueImpl(sparseType_signed_7bit);
    cat.setBitSize(7);
    cat.setDefaultValue(0x40);
    node_variation.add(new MapNode("CAT Variation Control Depth", cat));

    final Value ac1 = new ValueImpl(sparseType_signed_7bit);
    ac1.setBitSize(7);
    ac1.setDefaultValue(0x40);
    node_variation.add(new MapNode("AC1 Variation Control Depth", ac1));

    final Value ac2 = new ValueImpl(sparseType_signed_7bit);
    ac2.setBitSize(7);
    ac2.setDefaultValue(0x40);
    node_variation.add(new MapNode("AC2 Variation Control Depth", ac2));

    for (int i = 10; i < 16; i++) {
      final Value value_7bit = new ValueImpl(sparseType_non_negative_7bit);
      final long desiredAddress = i == 10 ? addr2index(0x02, 0x01, 0x70) : -1;
      value_7bit.setBitSize(7);
      value_7bit.setDefaultValue(0x00);
      node_variation.add(new MapNode("Variation Parameter " + (i + 1),
                                     value_7bit, desiredAddress));
    }

    return node_variation;
  }

  private MapNode buildMapNodeEffect1()
  {
    final MapNode node_effect =
      new MapNode("Effect1", addr2index(0x02, 0x01, 0x00));
    node_effect.add(buildMapNodeReverb());
    node_effect.add(buildMapNodeChorus());
    node_effect.add(buildMapNodeVariation());
    return node_effect;
  }

  private void buildPartControl(final MapNode node,
                                final String displayPrefix,
                                final int defaultPModeDepth)
  {
    final Value pitch_control = new ValueImpl(sparseType_transpose);
    pitch_control.setBitSize(7);
    pitch_control.setDefaultValue(0x40);
    node.add(new MapNode(displayPrefix + " Pitch Control", pitch_control));

    final Value filter_control = new ValueImpl(sparseType_filter_control);
    filter_control.setBitSize(7);
    filter_control.setDefaultValue(0x40);
    node.add(new MapNode(displayPrefix + " Filter Control", filter_control));

    final Value amplitude_control = new ValueImpl(sparseType_signed_7bit);
    amplitude_control.setBitSize(7);
    amplitude_control.setDefaultValue(0x40);
    node.add(new MapNode(displayPrefix + " Amplitude Control",
                         amplitude_control));

    final Value lfo_pmod_depth = new ValueImpl(sparseType_non_negative_7bit);
    lfo_pmod_depth.setBitSize(7);
    lfo_pmod_depth.setDefaultValue(defaultPModeDepth);
    node.add(new MapNode(displayPrefix + " LFO PMod Depth", lfo_pmod_depth));

    final Value lfo_fmod_depth =
      new ValueImpl(sparseType_non_negative_7bit, "internal-mod-fm");
    lfo_fmod_depth.setBitSize(7);
    lfo_fmod_depth.setDefaultValue(0x00);
    node.add(new MapNode(displayPrefix + " LFO FMod Depth", lfo_fmod_depth));

    final Value lfo_amod_depth =
      new ValueImpl(sparseType_non_negative_7bit, "internal-mod-am");
    lfo_amod_depth.setBitSize(7);
    lfo_amod_depth.setDefaultValue(0x00);
    node.add(new MapNode(displayPrefix + " LFO AMod Depth", lfo_amod_depth));
  }

  private MapNode buildMapNodeMultiPartN(final int n)
  {
    final MapNode node_multi_part_n =
      new MapNode("Multi Part " + (n + 1), addr2index(0x08, n, 0x00));

    final SparseType sparseType_element_reserve =
      new SparseTypeImpl("internal-control").
      addValueRange(0x00, 0x1f, Int8Type.defaultInstance);
    final Value element_reserve = new ValueImpl(sparseType_element_reserve);
    element_reserve.setBitSize(7);
    element_reserve.setDefaultValue(n == 9 ? 0x00 : 0x02);
    node_multi_part_n.add(new MapNode("Element Reserve", element_reserve));

    final Value bank_select_msb = new ValueImpl(sparseType_non_negative_7bit);
    bank_select_msb.setBitSize(7);
    bank_select_msb.setDefaultValue(n == 9 ? 0x7f : 0x00);
    node_multi_part_n.add(new MapNode("Bank Select MSB", bank_select_msb));

    final Value bank_select_lsb = new ValueImpl(sparseType_non_negative_7bit);
    bank_select_lsb.setBitSize(7);
    bank_select_lsb.setDefaultValue(0x00);
    node_multi_part_n.add(new MapNode("Bank Select LSB", bank_select_lsb));

    final ValueRangeRenderer VALUE_TYPE_1_TO_N = new Int8Type(-1);

    final SparseType sparseType_program_number =
      new SparseTypeImpl("internal-control").
      addValueRange(0x00, 0x7f, VALUE_TYPE_1_TO_N);
    final Value program_number = new ValueImpl(sparseType_program_number);
    program_number.setBitSize(7);
    program_number.setDefaultValue(0x00);
    node_multi_part_n.add(new MapNode("Program Number", program_number));

    final SparseType sparseType_rcv_channel =
      new SparseTypeImpl("internal-control").
      addValueRange(0x00, 0x0f, VALUE_TYPE_1_TO_N).
      addSingleValue(0x10, "Off");
    final Value rcv_channel = new ValueImpl(sparseType_rcv_channel);
    rcv_channel.setBitSize(7);
    rcv_channel.setDefaultValue(n);
    node_multi_part_n.add(new MapNode("Rcv Channel", rcv_channel));

    final Value mono_poly_mode = new ValueImpl(sparseType_mono_poly_mode);
    mono_poly_mode.setBitSize(7);
    mono_poly_mode.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Mono / Poly Mode", mono_poly_mode));

    final SparseType sparseType_key_on_assign =
      new SparseTypeImpl("internal-control").
      addValueRange(0x0, 0x2, enumType_key_on_assign);
    final Value key_on_assign = new ValueImpl(sparseType_key_on_assign);
    key_on_assign.setBitSize(7);
    key_on_assign.setDefaultValue(n == 9 ? 0x2 : 0x0);
    node_multi_part_n.add(new MapNode("Same Not Number Key on Assign",
                                      key_on_assign));

    final SparseType sparseType_part_mode =
      new SparseTypeImpl("internal-control").
      addValueRange(0x0, 0x3, enumType_part_mode);
    final Value part_mode = new ValueImpl(sparseType_part_mode);
    part_mode.setBitSize(7);
    part_mode.setDefaultValue(n == 9 ? 0x2 : 0x0);
    node_multi_part_n.add(new MapNode("Part Mode", part_mode));

    final Value note_shift = new ValueImpl(sparseType_transpose);
    note_shift.setBitSize(7);
    note_shift.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Note Shift", note_shift));

    final Value detune = new ValueImpl(sparseType_detune);
    detune.setBitSize(14);
    detune.setDefaultValue(0x0400);
    node_multi_part_n.add(new MapNode("Detune", detune));

    final Value volume = new ValueImpl(sparseType_volume);
    volume.setBitSize(7);
    volume.setDefaultValue(0x64);
    node_multi_part_n.add(new MapNode("Volume", volume));

    final Value velocity_sense_depth =
      new ValueImpl(sparseType_non_negative_7bit);
    velocity_sense_depth.setBitSize(7);
    velocity_sense_depth.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Velocity Sense Depth",
                                      velocity_sense_depth));

    final Value velocity_sense_offset =
      new ValueImpl(sparseType_non_negative_7bit);
    velocity_sense_offset.setBitSize(7);
    velocity_sense_offset.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Velocity Sense Offset",
                                      velocity_sense_offset));

    final Value pan = new ValueImpl(sparseType_pan_extended);
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Pan", pan));

    final Value note_limit_low =
      new ValueImpl(sparseType_note, "internal-limit-bottom");
    note_limit_low.setBitSize(7);
    note_limit_low.setDefaultValue(0x00);
    node_multi_part_n.add(new MapNode("Note Limit Low", note_limit_low));

    final Value note_limit_high =
      new ValueImpl(sparseType_note, "internal-limit-top");
    note_limit_high.setBitSize(7);
    note_limit_high.setDefaultValue(0x7f);
    node_multi_part_n.add(new MapNode("Note Limit High", note_limit_high));

    final Value dry_level = new ValueImpl(sparseType_volume);
    dry_level.setBitSize(7);
    dry_level.setDefaultValue(0x7f);
    node_multi_part_n.add(new MapNode("Dry Level", dry_level));

    final Value chorus_send = new ValueImpl(sparseType_volume);
    chorus_send.setBitSize(7);
    chorus_send.setDefaultValue(0x00);
    node_multi_part_n.add(new MapNode("Chorus Send", chorus_send));

    final Value reverb_send = new ValueImpl(sparseType_volume);
    reverb_send.setBitSize(7);
    reverb_send.setDefaultValue(0x28);
    node_multi_part_n.add(new MapNode("Reverb Send", reverb_send));

    final Value variation_send = new ValueImpl(sparseType_volume);
    variation_send.setBitSize(7);
    variation_send.setDefaultValue(0x00);
    node_multi_part_n.add(new MapNode("Variation Send", variation_send));

    final Value vibrato_rate = new ValueImpl(sparseType_signed_7bit);
    vibrato_rate.setBitSize(7);
    vibrato_rate.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Vibrato Rate", vibrato_rate));

    final Value vibrato_depth = new ValueImpl(sparseType_signed_7bit);
    vibrato_depth.setBitSize(7);
    vibrato_depth.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Vibrato Depth", vibrato_depth));

    final Value vibrato_delay = new ValueImpl(sparseType_signed_7bit);
    vibrato_delay.setBitSize(7);
    vibrato_delay.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Vibrato Delay", vibrato_delay));

    final Value filter_cutoff_frequency =
      new ValueImpl(sparseType_signed_7bit, "internal-tune");
    filter_cutoff_frequency.setBitSize(7);
    filter_cutoff_frequency.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Filter Cutoff Frequency",
                                      filter_cutoff_frequency));

    final Value filter_resonance = new ValueImpl(sparseType_signed_7bit);
    filter_resonance.setBitSize(7);
    filter_resonance.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Filter Resonance", filter_resonance));

    final Value eg_attack_time =
      new ValueImpl(sparseType_signed_7bit, "internal-time");
    eg_attack_time.setBitSize(7);
    eg_attack_time.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("EG Attack Time", eg_attack_time));

    final Value eg_decay_time =
      new ValueImpl(sparseType_signed_7bit, "internal-time");
    eg_decay_time.setBitSize(7);
    eg_decay_time.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("EG Decay Time", eg_decay_time));

    final Value eg_release_time =
      new ValueImpl(sparseType_signed_7bit, "internal-time");
    eg_release_time.setBitSize(7);
    eg_release_time.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("EG Release Time", eg_release_time));

    buildPartControl(node_multi_part_n, "MW", 0x0a);

    final Value bend_pitch_control = new ValueImpl(sparseType_transpose);
    bend_pitch_control.setBitSize(7);
    bend_pitch_control.setDefaultValue(0x42);
    node_multi_part_n.add(new MapNode("Bend Pitch Control",
                                      bend_pitch_control));

    final Value bend_filter_control = new ValueImpl(sparseType_filter_control);
    bend_filter_control.setBitSize(7);
    bend_filter_control.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Bend Filter Control",
                                      bend_filter_control));

    final Value bend_amplitude_control = new ValueImpl(sparseType_signed_7bit);
    bend_amplitude_control.setBitSize(7);
    bend_amplitude_control.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Bend Amplitude Control",
                                      bend_amplitude_control));

    final Value bend_lfo_pmod_depth =
      new ValueImpl(sparseType_bend_lfo_mod_depth);
    bend_lfo_pmod_depth.setBitSize(7);
    bend_lfo_pmod_depth.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Bend LFO PMod Depth",
                                      bend_lfo_pmod_depth));

    final Value bend_lfo_fmod_depth =
      new ValueImpl(sparseType_bend_lfo_mod_depth, "internal-mod-fm");
    bend_lfo_fmod_depth.setBitSize(7);
    bend_lfo_fmod_depth.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Bend LFO FMod Depth",
                                      bend_lfo_fmod_depth));

    final Value bend_lfo_amod_depth =
      new ValueImpl(sparseType_bend_lfo_mod_depth, "internal-mod-am");
    bend_lfo_amod_depth.setBitSize(7);
    bend_lfo_amod_depth.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Bend LFO AMod Depth",
                                      bend_lfo_amod_depth));

    final Value rcv_pitch_bend = new ValueImpl(sparseType_switch);
    rcv_pitch_bend.setBitSize(7);
    rcv_pitch_bend.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Pitch Bend",
                                      rcv_pitch_bend,
                                      addr2index(0x08, n, 0x30)));

    final Value rcv_ch_after_touch = new ValueImpl(sparseType_switch);
    rcv_ch_after_touch.setBitSize(7);
    rcv_ch_after_touch.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Ch After Touch (CAT)",
                                      rcv_ch_after_touch));

    final Value rcv_program_change = new ValueImpl(sparseType_switch);
    rcv_program_change.setBitSize(7);
    rcv_program_change.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Program Change",
                                      rcv_program_change));

    final Value rcv_control_change = new ValueImpl(sparseType_switch);
    rcv_control_change.setBitSize(7);
    rcv_control_change.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Control Change",
                                      rcv_control_change));

    final Value rcv_poly_after_touch =
      new ValueImpl(sparseType_switch);
    rcv_poly_after_touch.setBitSize(7);
    rcv_poly_after_touch.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Poly After Touch (PAT)",
                                      rcv_ch_after_touch));

    final Value rcv_note_message = new ValueImpl(sparseType_switch);
    rcv_note_message.setBitSize(7);
    rcv_note_message.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Note Message", rcv_note_message));

    final Value rcv_rpn = new ValueImpl(sparseType_switch);
    rcv_rpn.setBitSize(7);
    rcv_rpn.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv RPN", rcv_rpn));

    final Value rcv_nrpn = new ValueImpl(sparseType_switch);
    rcv_nrpn.setBitSize(7);
    rcv_nrpn.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv NRPN", rcv_nrpn));

    final Value rcv_modulation = new ValueImpl(sparseType_switch);
    rcv_modulation.setBitSize(7);
    rcv_modulation.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Modulation", rcv_modulation));

    final Value rcv_volume = new ValueImpl(sparseType_switch);
    rcv_volume.setBitSize(7);
    rcv_volume.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Volume", rcv_volume));

    final Value rcv_pan = new ValueImpl(sparseType_switch);
    rcv_pan.setBitSize(7);
    rcv_pan.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Pan", rcv_pan));

    final Value rcv_expression = new ValueImpl(sparseType_switch);
    rcv_expression.setBitSize(7);
    rcv_expression.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Expression", rcv_expression));

    final Value rcv_hold1 = new ValueImpl(sparseType_switch);
    rcv_hold1.setBitSize(7);
    rcv_hold1.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Hold1", rcv_hold1));

    final Value rcv_portamento = new ValueImpl(sparseType_switch);
    rcv_portamento.setBitSize(7);
    rcv_portamento.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Portamento", rcv_portamento));

    final Value rcv_sostenuto = new ValueImpl(sparseType_switch);
    rcv_sostenuto.setBitSize(7);
    rcv_sostenuto.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Sostenuto", rcv_sostenuto));

    final Value rcv_soft_pedal = new ValueImpl(sparseType_switch);
    rcv_soft_pedal.setBitSize(7);
    rcv_soft_pedal.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Soft Pedal", rcv_soft_pedal));

    final Value rcv_bank_select = new ValueImpl(sparseType_switch);
    rcv_bank_select.setBitSize(7);
    rcv_bank_select.setDefaultValue(0x1);
    node_multi_part_n.add(new MapNode("Rcv Bank Select", rcv_bank_select));

    final Value scale_tuning_c =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-c");
    scale_tuning_c.setBitSize(7);
    scale_tuning_c.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning C", scale_tuning_c));

    final Value scale_tuning_c_sharp =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-c-sharp");
    scale_tuning_c_sharp.setBitSize(7);
    scale_tuning_c_sharp.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning C#", scale_tuning_c_sharp));

    final Value scale_tuning_d =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-d");
    scale_tuning_d.setBitSize(7);
    scale_tuning_d.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning D", scale_tuning_d));

    final Value scale_tuning_d_sharp =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-d-sharp");
    scale_tuning_d_sharp.setBitSize(7);
    scale_tuning_d_sharp.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning D#", scale_tuning_d_sharp));

    final Value scale_tuning_e =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-e");
    scale_tuning_e.setBitSize(7);
    scale_tuning_e.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning E", scale_tuning_e));

    final Value scale_tuning_f =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-f");
    scale_tuning_f.setBitSize(7);
    scale_tuning_f.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning F", scale_tuning_f));

    final Value scale_tuning_f_sharp =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-f-sharp");
    scale_tuning_f_sharp.setBitSize(7);
    scale_tuning_f_sharp.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning F#", scale_tuning_f_sharp));

    final Value scale_tuning_g =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-g");
    scale_tuning_g.setBitSize(7);
    scale_tuning_g.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning G", scale_tuning_g));

    final Value scale_tuning_g_sharp =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-g-sharp");
    scale_tuning_g_sharp.setBitSize(7);
    scale_tuning_g_sharp.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning G#", scale_tuning_g_sharp));

    final Value scale_tuning_a =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-a");
    scale_tuning_a.setBitSize(7);
    scale_tuning_a.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning A", scale_tuning_a));

    final Value scale_tuning_a_sharp =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-a-sharp");
    scale_tuning_a_sharp.setBitSize(7);
    scale_tuning_a_sharp.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning A#", scale_tuning_a_sharp));

    final Value scale_tuning_b =
      new ValueImpl(sparseType_scale_tuning, "internal-tune-b");
    scale_tuning_b.setBitSize(7);
    scale_tuning_b.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Scale Tuning B", scale_tuning_b));

    buildPartControl(node_multi_part_n, "CAT", 0x00);

    buildPartControl(node_multi_part_n, "PAT", 0x00);

    final Value ac1_controller_number =
      new ValueImpl(sparseType_controller_number);
    ac1_controller_number.setBitSize(7);
    ac1_controller_number.setDefaultValue(0x10);
    node_multi_part_n.add(new MapNode("AC1 Controller Number",
                                      ac1_controller_number));
    buildPartControl(node_multi_part_n, "AC1", 0x00);

    final Value ac2_controller_number =
      new ValueImpl(sparseType_controller_number);
    ac2_controller_number.setBitSize(7);
    ac2_controller_number.setDefaultValue(0x11);
    node_multi_part_n.add(new MapNode("AC2 Controller Number",
                                      ac2_controller_number));
    buildPartControl(node_multi_part_n, "AC2", 0x00);

    final Value portamento_switch = new ValueImpl(sparseType_switch);
    portamento_switch.setBitSize(7);
    portamento_switch.setDefaultValue(0x0);
    node_multi_part_n.add(new MapNode("Portamento Switch", portamento_switch));

    final Value portamento_time =
      new ValueImpl(sparseType_non_negative_7bit, "internal-time");
    portamento_time.setBitSize(7);
    portamento_time.setDefaultValue(0x00);
    node_multi_part_n.add(new MapNode("Portamento Time", portamento_time));

    final Value pitch_eg_initial_level =
      new ValueImpl(sparseType_signed_7bit, "internal-volume");
    pitch_eg_initial_level.setBitSize(7);
    pitch_eg_initial_level.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Pitch EG Initial Level",
                                      pitch_eg_initial_level));

    final Value pitch_eg_attack_time =
      new ValueImpl(sparseType_signed_7bit, "internal-time");
    pitch_eg_attack_time.setBitSize(7);
    pitch_eg_attack_time.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Pitch EG Attack Time",
                                      pitch_eg_attack_time));

    final Value pitch_eg_release_level =
      new ValueImpl(sparseType_signed_7bit, "internal-volume");
    pitch_eg_release_level.setBitSize(7);
    pitch_eg_release_level.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Pitch EG Release Level",
                                      pitch_eg_release_level));

    final Value pitch_eg_release_time =
      new ValueImpl(sparseType_signed_7bit, "internal-time");
    pitch_eg_release_time.setBitSize(7);
    pitch_eg_release_time.setDefaultValue(0x40);
    node_multi_part_n.add(new MapNode("Pitch EG Release Time",
                                      pitch_eg_release_time));

    final Value velocity_limit_low =
      new ValueImpl(sparseType_positive_7bit, "internal-limit-bottom");
    velocity_limit_low.setBitSize(7);
    velocity_limit_low.setDefaultValue(0x01);
    node_multi_part_n.add(new MapNode("Velocity Limit Low",
                                      velocity_limit_low));

    final Value velocity_limit_high =
      new ValueImpl(sparseType_positive_7bit, "internal-limit-top");
    velocity_limit_high.setBitSize(7);
    velocity_limit_high.setDefaultValue(0x7f);
    node_multi_part_n.add(new MapNode("Velocity Limit High",
                                      velocity_limit_high));

    return node_multi_part_n;
  }

  private MapNode buildMapNodeMultiPart()
  {
    final MapNode node_multi_part =
      new MapNode("Multi Part", addr2index(0x08, 0x00, 0x00));
    for (int n = 0; n < 16; n++) {
      node_multi_part.add(buildMapNodeMultiPartN(n));
    }
    return node_multi_part;
  }

  private MapNode buildMapNodeDrumSetupNoteNR(final int n, final int r)
  {
    final MapNode node_drum_setup_note_r =
      new MapNode("Drum Setup Note " + r, addr2index(0x30 + n, r, 0x00));

    final Value pitch_coarse =
      new ValueImpl(sparseType_signed_7bit, "internal-transpose");
    pitch_coarse.setBitSize(7);
    pitch_coarse.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("Pitch Coarse", pitch_coarse));

    final Value pitch_fine = new ValueImpl(sparseType_scale_tuning);
    pitch_fine.setBitSize(7);
    pitch_fine.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("Pitch Fine", pitch_fine));

    final Value level = new ValueImpl(sparseType_volume);
    level.setBitSize(7);
    level.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("Level", level));

    final SparseType sparseType_alternative_group =
      new SparseTypeImpl("internal-control").
      addSingleValue(0x00, "Off").
      addValueRange(0x01, 0x7f, Int8Type.defaultInstance);
    final Value alternative_group = new ValueImpl(sparseType_alternative_group);
    alternative_group.setBitSize(7);
    alternative_group.setDefaultValue(n);
    node_drum_setup_note_r.add(new MapNode("Alternative Group",
                                           alternative_group));

    final Value pan = new ValueImpl(sparseType_pan_extended);
    pan.setBitSize(7);
    pan.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("Pan", pan));

    final Value reverb_send = new ValueImpl(sparseType_volume);
    reverb_send.setBitSize(7);
    reverb_send.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("Reverb Send", reverb_send));

    final Value chorus_send = new ValueImpl(sparseType_volume);
    chorus_send.setBitSize(7);
    chorus_send.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("Chorus Send", chorus_send));

    final Value variation_send = new ValueImpl(sparseType_volume);
    variation_send.setBitSize(7);
    variation_send.setDefaultValue(0x7f);
    node_drum_setup_note_r.add(new MapNode("Variation Send", variation_send));

    final SparseType sparseType_key_assign =
      new SparseTypeImpl("internal-control").
      addValueRange(0x00, 0x01, enumType_key_assign);
    final Value key_assign = new ValueImpl(sparseType_key_assign);
    key_assign.setBitSize(7);
    key_assign.setDefaultValue(0x0);
    node_drum_setup_note_r.add(new MapNode("Key Assign", key_assign));

    final Value rcv_note_off = new ValueImpl(sparseType_switch);
    rcv_note_off.setBitSize(7);
    rcv_note_off.setDefaultValue(0x0);
    node_drum_setup_note_r.add(new MapNode("Rcv Note Off", rcv_note_off));

    final Value rcv_note_on = new ValueImpl(sparseType_switch);
    rcv_note_on.setBitSize(7);
    rcv_note_on.setDefaultValue(0x0);
    node_drum_setup_note_r.add(new MapNode("Rcv Note On", rcv_note_on));

    final Value filter_cutoff_frequency =
      new ValueImpl(sparseType_signed_7bit, "internal-tune");
    filter_cutoff_frequency.setBitSize(7);
    filter_cutoff_frequency.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("Filter Cutoff Frequency",
                                           filter_cutoff_frequency));

    final Value filter_resonance = new ValueImpl(sparseType_signed_7bit);
    filter_resonance.setBitSize(7);
    filter_resonance.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("Filter Resonance",
                                           filter_resonance));

    final Value eg_attack_rate = new ValueImpl(sparseType_signed_7bit);
    eg_attack_rate.setBitSize(7);
    eg_attack_rate.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("EG Attack Rate", eg_attack_rate));

    final Value eg_decay1_rate = new ValueImpl(sparseType_signed_7bit);
    eg_decay1_rate.setBitSize(7);
    eg_decay1_rate.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("EG Decay1 Rate", eg_decay1_rate));

    final Value eg_decay2_rate = new ValueImpl(sparseType_signed_7bit);
    eg_decay2_rate.setBitSize(7);
    eg_decay2_rate.setDefaultValue(0x40);
    node_drum_setup_note_r.add(new MapNode("EG Decay2 Rate", eg_decay2_rate));

    return node_drum_setup_note_r;
  }

  private MapNode buildMapNodeDrumSetupN(final int n)
  {
    final MapNode node_drum_setup_n =
      new MapNode("Drum Setup " + n, addr2index(0x30 + n, 0x00, 0x00));
    for (int r = 0x0d; r < 0x5c; r++) {
      node_drum_setup_n.add(buildMapNodeDrumSetupNoteNR(n, r));
    }
    return node_drum_setup_n;
  }

  private MapNode buildMapNodeDrumSetup()
  {
    final MapNode node_drum_setup =
      new MapNode("Drum Setup", addr2index(0x30, 0x00, 0x00));
    for (int n = 0x0; n < 0x2; n++) {
      node_drum_setup.add(buildMapNodeDrumSetupN(n));
    }
    return node_drum_setup;
  }

  public void buildMap(final MapRoot root)
  {
    root.add(buildMapNodeSystem());
    root.add(buildMapNodeEffect1());
    root.add(buildMapNodeMultiPart());
    root.add(buildMapNodeDrumSetup());
  }

  private String two_digits(final String s)
  {
    switch (s.length()) {
    case 0:
      return "00";
    case 1:
      return "0" + s;
    case 2:
      return s;
    default:
      return s.substring(s.length() - 2);
    }
  }

  private class BulkStream extends InputStream
  {
    private final byte deviceId;
    private final int byte_start;
    private final int byte_count;
    private final byte hi;
    private final byte mid;
    private final byte lo;
    private final long end;
    private long pos;
    private int extrapos;
    private int check_sum;
    private MapNode node;

    private BulkStream()
    {
      throw new UnsupportedOperationException();
    }

    BulkStream(final byte deviceId,
               final MapNode root, final long start, final long end)
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
      byte_start = (int)(start / 7);
      byte_count = (int)((end - start + 6) / 7);
      hi = (byte)((byte_start >> 14) & 0x7f);
      mid = (byte)((byte_start >> 7) & 0x7f);
      lo = (byte)(byte_start & 0x7f);
      pos = start;
      this.end = end;
      extrapos = -8;
      check_sum = 0x00;
      node = root;
    }

    private int next_header_byte()
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
        data = byte_count >> 7;
        break;
      case -4:
        data = byte_count & 0x7f;
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
        check_sum = (check_sum + data) & 0x7f;
      }
      extrapos++;
      return data;
    }

    private int next_tail_byte()
    {
      final int data;
      switch (extrapos) {
      case 0:
        check_sum = (0x100 - check_sum) & 0x7f;
        data = check_sum;
        break;
      default:
        throw new IllegalStateException("invalid extrapos");
      }
      extrapos++;
      return data;
    }

    private int next_bulk_dump_byte()
    {
      node = node.locate(pos);
      // TODO: Fix performance: node.getData() will call node.locate()
      // once again, although we already know the node.
      final Integer data[] = node.getData(pos, 7);
      check_sum = (check_sum + data[0]) & 0x7f;
      pos += 7;
      return data[0];
    }

    public int read() throws IOException
    {
      if (pos < end)
        if (extrapos >= 0)
          return next_bulk_dump_byte();
        else
          return next_header_byte();
      else
        if (extrapos < 1) // tail data
          return next_tail_byte();
        else
          return -1; // EOF
    }
  }

  public InputStream bulkDump(final Value deviceId,
                              final MapNode root,
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
