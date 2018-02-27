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

package see.devices;

import java.io.InputStream;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import see.model.Contents;
import see.model.RangeContents;
import see.model.MapDef;
import see.model.MapNode;
import see.model.Range;
import see.model.ValueType;
import see.model.EnumType;
import see.model.Int8Type;
import see.model.AddressRepresentation;

/**
 * This class customizes SysExEdit for a DB50XG synthesizer.
 */
public class DB50XG implements MapDef
{
  private final static String DEVICE_NAME = "Yamaha™ MU50/DB50XG";
  private final static byte MANUFACTURER_ID = 0x43;
  private final static byte DEVICE_NUMBER = 0x1f;
  private final static byte MODEL_ID = 0x4c;
  private final static String ENTERED_BY =
    "Jürgen Reuter, Copyright © 1998, 2018";

  /**
   * Pan Data Assign Table
   */
  final static String[] PAN =
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
   * LEVEL[i] = 6*log(i/64)/log(127/64); 0 <= i <= 127
   */
  final static String[] LEVEL =
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
  final static String[] LFO_FREQUENCY =
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
  final static String[] MODULATION_DELAY_OFFSET =
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
  final static String[] EQ_FREQUENCY =
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
  final static String[] REVERB_TIME =
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
  final static String[] DELAY_TIME_1 =
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
  final static String[] ROOM_SIZE =
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
  final static String[] DELAY_TIME_2 =
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
  final static String[] REVERB_CUBE_SIZE =
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
   * Effect type hall
   */
  final static String[] HALL =
  {
    "Hall 1", "Hall 2"
  };

  /**
   * Effect type room
   */
  final static String[] ROOM =
  {
    "Room 1", "Room 2", "Room 3"
  };

  /**
   * Effect type stage
   */
  final static String[] STAGE =
  {
    "Stage 1", "Stage 2"
  };

  /**
   * Effect type chorus
   */
  final static String[] CHORUS =
  {
    "Chorus 1", "Chorus 2", "Chorus 3"
  };

  /**
   * Effect type celeste
   */
  final static String[] CELESTE =
  {
    "Celeste 1", "Celeste 2", "Celeste 3"
  };

  /**
   * Effect type flanger
   */
  final static String[] FLANGER =
  {
    "Flanger 1", "Flanger 2"
  };

  /**
   * Effect type early ref
   */
  final static String[] EARLY_REF =
  {
    "Early Ref 1", "Early Ref 2"
  };

  /**
   * Effect type karaoke
   */
  final static String[] KARAOKE =
  {
    "Karaoke 1", "Karaoke 2", "Karaoke 3"
  };

  /**
   * Variation connection type
   */
  final static String[] CONNECTION =
  {
    "Insertion", "System"
  };

  /**
   * Variation part type
   */
  final static String[] PART =
  {
    "Part 1", "Part 2", "Part 3", "Part 4",
    "Part 5", "Part 6", "Part 7", "Part 8",
    "Part 9", "Part 10", "Part 11", "Part 12",
    "Part 13", "Part 14", "Part 15", "Part 16"
  };

  public DB50XG() {}

  /**
   * Returns the manufacturer ID as defined in the MIDI specification.
   */
  public byte getManufacturerID() { return MANUFACTURER_ID; }

  /**
   * Returns the model ID.
   */
  public byte getModelID() { return MODEL_ID; }

  /**
   * Returns the default device model ID. As the synthesizer specs do not
   * explicitly specify such a value, we return the value 0, which may be
   * a good choice.
   */
  public byte getDefaultDeviceID() { return 0; }

  /**
   * Returns the name of the author; optionally, a copyright message.
   */
  public String getEnteredBy() { return ENTERED_BY; }

  private class DB50XGAddressRepresentation implements AddressRepresentation
  {
    /**
     * Returns a string representation for the given address.
     * @param address The index in the bit array of the memory map.
     * @return A string representation for the given address.
     */
    public String addressToString(long address)
      {
        byte bitpos = (byte)(address % 7);
        address = address / 7;
        byte hi = (byte)((address >> 14) & 0x7f);
        byte mid = (byte)((address >> 7) & 0x7f);
        byte lo = (byte)(address & 0x7f);
        String bitpos_str = bitpos == 0 ? "" : " [" + bitpos + "]";
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
  private long addr2index(int hi, int mid, int lo)
  {
    return
      (7*((((long)(hi & 0x7f)) << 14) | ((mid & 0x7f) << 7) | (lo & 0x7f)));
  }

  /**
   * Fills into map so many inaccessible bit locations, that the next address
   * to be handled equals the given address.
   * @param node The last node that was added and thus contains the uppermost
   *    address.
   * @param contents The Contents object that will the inaccessible bits
   *    associated with.
   * @param index The address which to fill up to.
   * @exception IllegalArgumentException If the specified address already
   *    has been passed.
   */
  private void skipToAddress(MapNode node, long index)
  {
    long delta = index - node.getTotalSize();
    if (delta < 0)
      throw new IllegalArgumentException("address already passed");
    if (delta > 0)
      node.setOffset(delta);
  }

  /**
   * Creates a map that represents the DB50XG's internal memory.
   * @return The root node of the created map.
   */
  public MapNode buildMap()
  {
    RangeContents temp_contents;
    Range temp_range;
    ValueType temp_valueType;

    ValueType
      pan = new EnumType(-1, PAN),
      level = new EnumType(LEVEL),
      lfo_frequency = new EnumType(LFO_FREQUENCY),
      modulation_delay_offset = new EnumType(MODULATION_DELAY_OFFSET),
      eq_frequency = new EnumType(EQ_FREQUENCY),
      reverb_time = new EnumType(REVERB_TIME),
      delay_time_1 = new EnumType(DELAY_TIME_1),
      room_size = new EnumType(ROOM_SIZE),
      delay_time_2 = new EnumType(DELAY_TIME_2),
      reverb_cube_size = new EnumType(REVERB_CUBE_SIZE);

    Int8Type int_valueType = new Int8Type();

    Int8Type centered_7bit_valueType = new Int8Type(-0x40);
    Range centered_7bit_range = new Range();
    centered_7bit_range.addContigous(0x00, 0x7f,
                                     centered_7bit_valueType);

    MapNode root = new MapNode("DB50XG");
    MapNode temp_node = new MapNode("System");

    temp_range = new Range();
    temp_range.addContigous(0x0, 0x7, int_valueType);
    temp_range.setIconKey("internal-tune");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x4);
    temp_node.add(new MapNode("Master Tune[3]", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0x0, 0xf, int_valueType);
    temp_range.setIconKey("internal-tune");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x0);
    temp_node.add(new MapNode("Master Tune[2]", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0x0, 0xf, int_valueType);
    temp_range.setIconKey("internal-tune");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x0);
    temp_node.add(new MapNode("Master Tune[1]", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0x0, 0xf, int_valueType);
    temp_range.setIconKey("internal-tune");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x0);
    temp_node.add(new MapNode("Master Tune[0]", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0x0, 0x7f, int_valueType);
    temp_range.setIconKey("internal-volume");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x7f);
    temp_node.add(new MapNode("Master Volume", temp_contents));

    temp_node.add(new MapNode("Unused", new RangeContents(7)));

    temp_range = new Range();
    temp_range.addContigous(0x28, 0x58, centered_7bit_valueType);
    temp_range.setIconKey("internal-transpose");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node.add(new MapNode("Transpose", temp_contents));


    temp_range = new Range();
    temp_range.addContigous(0x7f, 0x7f, int_valueType);
    temp_range.setIconKey("internal-error");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x7f);
    temp_node.add(new MapNode("Drum Setup Reset", temp_contents,
                              addr2index(0x00, 0x00, 0x76)));

    temp_range = new Range();
    temp_range.addContigous(0x7f, 0x7f, int_valueType);
    temp_range.setIconKey("internal-error");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x7f);
    temp_node.add(new MapNode("XG System On", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0x7f, 0x7f, int_valueType);
    temp_range.setIconKey("internal-error");
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x7f);
    temp_node.add(new MapNode("All Parameter Reset", temp_contents));

    root.add(temp_node);

    ValueType
      hall_enumType = new EnumType(-0x0080, HALL),
      room_enumType = new EnumType(-0x0100, ROOM),
      stage_enumType = new EnumType(-0x0180, STAGE),
      chorus_enumType = new EnumType(-0x2080, CHORUS),
      celeste_enumType = new EnumType(-0x2100, CELESTE),
      flanger_enumType = new EnumType(-0x2180, FLANGER),
      early_ref_enumType = new EnumType(-0x0480, EARLY_REF),
      karaoke_enumType = new EnumType(-0x0a00, KARAOKE);

    Range reverb_range = new Range();
    reverb_range.addContigous(0x0000, "No Effect");
    reverb_range.addContigous(0x0080, 0x0081, hall_enumType);
    reverb_range.addContigous(0x0100, 0x0102, room_enumType);
    reverb_range.addContigous(0x0180, 0x0181, stage_enumType);
    reverb_range.addContigous(0x0200, "Plate");
    reverb_range.addContigous(0x0800, "White Room");
    reverb_range.addContigous(0x0880, "Tunnel");
    reverb_range.addContigous(0x0980, "Basement");
    reverb_range.setIconKey("internal-fx-reverb");

    Range chorus_range = new Range();
    chorus_range.addContigous(0x0000, "No Effect");
    chorus_range.addContigous(0x2080, 0x2082, chorus_enumType);
    chorus_range.addContigous(0x2088, "Chorus 4");
    chorus_range.addContigous(0x2100, 0x2102, celeste_enumType);
    chorus_range.addContigous(0x2108, "Celeste 4");
    chorus_range.addContigous(0x2180, 0x2181, flanger_enumType);
    chorus_range.addContigous(0x2188, "Flanger 3");
    chorus_range.setIconKey("internal-fx-chorus");

    Range variation_range = new Range();
    variation_range.addContigous(0x0000, "No Effect");
    variation_range.addContigous(0x0080, 0x0081, hall_enumType);
    variation_range.addContigous(0x0100, 0x0102, room_enumType);
    variation_range.addContigous(0x0180, 0x0181, stage_enumType);
    variation_range.addContigous(0x0200, "Plate");
    variation_range.addContigous(0x0280, "Delay L, C, R");
    variation_range.addContigous(0x0300, "Delay L, R");
    variation_range.addContigous(0x0380, "Echo");
    variation_range.addContigous(0x0400, "Cross Delay");
    variation_range.addContigous(0x0480, 0x0481, early_ref_enumType);
    variation_range.addContigous(0x0500, "Gate Reverb");
    variation_range.addContigous(0x0580, "Reverse Gate");
    variation_range.addContigous(0x0a00, 0x0a02, karaoke_enumType);
    variation_range.addContigous(0x2080, 0x2082, chorus_enumType);
    variation_range.addContigous(0x2088, "Chorus 4");
    variation_range.addContigous(0x2100, 0x2102, celeste_enumType);
    variation_range.addContigous(0x2108, "Celeste 4");
    variation_range.addContigous(0x2180, 0x2181, flanger_enumType);
    variation_range.addContigous(0x2188, "Flanger 3");
    variation_range.addContigous(0x2200, "Symphonic");
    variation_range.addContigous(0x2280, "Rotary Speaker");
    variation_range.addContigous(0x2300, "Tremolo");
    variation_range.addContigous(0x2380, "Auto Pan");
    variation_range.addContigous(0x2401, "Phaser 1");
    variation_range.addContigous(0x2408, "Phaser 2");
    variation_range.addContigous(0x2480, "Distortion");
    variation_range.addContigous(0x2500, "Over Drive");
    variation_range.addContigous(0x2580, "Amp Simulator");
    variation_range.addContigous(0x2600, "3-Band EQ (Mono)");
    variation_range.addContigous(0x2680, "2-Band EQ (Stereo)");
    variation_range.addContigous(0x2700, "Auto Wah (LFO)");
    variation_range.addContigous(0x2000, "Thru");

    Range pan_range = new Range();
    pan_range.addContigous(0x01, 0x7f, pan);
    pan_range.setIconKey("internal-pan");

    Range level_range = new Range();
    level_range.addContigous(0x00, 0x7f, level);
    level_range.setIconKey("internal-volume");

    EnumType connection_enumType = new EnumType(CONNECTION);
    Range connection_range = new Range();
    connection_range.addContigous(0x00, 0x01, connection_enumType);

    EnumType part_enumType = new EnumType(PART);
    Range part_range = new Range();
    part_range.addContigous(0x00, 0x01, part_enumType);
    part_range.addContigous(0x7f, "Off");

    temp_node = new MapNode("Effect1", addr2index(0x02, 0x00, 0x00));
    MapNode temp_node2 = new MapNode("Reverb");

    temp_contents = new RangeContents(reverb_range);
    temp_contents.setBitSize(14);
    temp_contents.setDefaultValue(0x0080);
    temp_node2.add(new MapNode("Reverb Type", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0, 127, int_valueType);
    for (int i = 0; i < 10; i++)
      {
        temp_contents = new RangeContents(temp_range);
        temp_contents.setBitSize(7);
        temp_contents.setDefaultValue(0x00);
        temp_node2.add(new MapNode("Reverb Parameter " + (i + 1),
                                   temp_contents));
      }

    temp_contents = new RangeContents(level_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("Reverb Return", temp_contents));

    temp_contents = new RangeContents(pan_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("Reverb Pan", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0, 127, int_valueType);
    long offset;
    for (int i = 10; i < 16; i++)
      {
        temp_contents = new RangeContents(temp_range);
        temp_contents.setBitSize(7);
        temp_contents.setDefaultValue(0x00);
        offset = (i == 10) ? addr2index(0x00, 0x00, 0x02) : 0;
        temp_node2.add(new MapNode("Reverb Parameter " + (i + 1),
                                   temp_contents, offset));
      }

    temp_node.add(temp_node2);
    temp_node2 = new MapNode("Chorus", addr2index(0x00, 0x00, 0x0a));

    temp_contents = new RangeContents(chorus_range);
    temp_contents.setBitSize(14);
    temp_contents.setDefaultValue(0x2080);
    temp_node2.add(new MapNode("Chorus Type", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0, 127, int_valueType);
    for (int i = 0; i < 10; i++)
      {
        temp_contents = new RangeContents(temp_range);
        temp_contents.setBitSize(7);
        temp_contents.setDefaultValue(0x00);
        temp_node2.add(new MapNode("Chorus Parameter " + (i + 1),
                                   temp_contents));
      }

    temp_contents = new RangeContents(level_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("Chorus Return", temp_contents));

    temp_contents = new RangeContents(pan_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("Chorus Pan", temp_contents));

    temp_contents = new RangeContents(level_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x00);
    temp_node2.add(new MapNode("Send Chorus To Reverb", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0, 127, int_valueType);
    for (int i = 10; i < 16; i++)
      {
        temp_contents = new RangeContents(temp_range);
        temp_contents.setBitSize(7);
        temp_contents.setDefaultValue(0x00);
        offset = (i == 10) ? addr2index(0x00, 0x00, 0x01) : 0;
        temp_node2.add(new MapNode("Chorus Parameter " + (i + 1),
                                   temp_contents, offset));
      }

    temp_node.add(temp_node2);
    temp_node2 = new MapNode("Variation", addr2index(0x00, 0x00, 0x0a));

    temp_contents = new RangeContents(variation_range);
    temp_contents.setBitSize(14);
    temp_contents.setDefaultValue(0x0280);
    temp_node2.add(new MapNode("Variation Type", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0, 127, int_valueType);
    for (int i = 0; i < 10; i++)
      {
        temp_contents = new RangeContents(temp_range);
        temp_contents.setBitSize(14);
        temp_contents.setDefaultValue(0x00);
        temp_node2.add(new MapNode("Variation Parameter " + (i + 1),
                                   temp_contents));
      }

    temp_contents = new RangeContents(level_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("Variation Return", temp_contents));

    temp_contents = new RangeContents(pan_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("Variation Pan", temp_contents));

    temp_contents = new RangeContents(level_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x00);
    temp_node2.add(new MapNode("Send Variation To Reverb", temp_contents));

    temp_contents = new RangeContents(level_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x00);
    temp_node2.add(new MapNode("Send Variation To Chorus", temp_contents));

    temp_contents = new RangeContents(connection_range);
    temp_contents.setBitSize(1);
    temp_contents.setDefaultValue(0x00);
    temp_node2.add(new MapNode("Variation Connection", temp_contents));

    temp_node2.add(new MapNode("Unused", new RangeContents(6)));

    temp_contents = new RangeContents(part_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x7f);
    temp_node2.add(new MapNode("Variation Part", temp_contents));

    temp_contents = new RangeContents(centered_7bit_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("MW Variation Control Depth", temp_contents));

    temp_contents = new RangeContents(centered_7bit_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("Bend Variation Control Depth", temp_contents));

    temp_contents = new RangeContents(centered_7bit_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("CAT Variation Control Depth", temp_contents));

    temp_contents = new RangeContents(centered_7bit_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("AC1 Variation Control Depth", temp_contents));

    temp_contents = new RangeContents(centered_7bit_range);
    temp_contents.setBitSize(7);
    temp_contents.setDefaultValue(0x40);
    temp_node2.add(new MapNode("AC2 Variation Control Depth", temp_contents));

    temp_range = new Range();
    temp_range.addContigous(0, 127, int_valueType);
    for (int i = 10; i < 16; i++)
      {
        temp_contents = new RangeContents(temp_range);
        temp_contents.setBitSize(7);
        temp_contents.setDefaultValue(0x00);
        offset = (i == 10) ? addr2index(0x00, 0x00, 0x0f) : 0;
        temp_node2.add(new MapNode("Variation Parameter " + (i + 1),
                                   temp_contents));
      }

    temp_node.add(temp_node2);
    root.add(temp_node);

    temp_node = new MapNode("Multi Part 1", addr2index(0x05, 0x7e, 0x0a));
    temp_range = new Range();
    temp_range.addContigous(0, 127, lfo_frequency);
    temp_contents = new RangeContents(temp_range);
    temp_contents.setBitSize(14);
    temp_node.add(new MapNode("LFO Frequency", temp_contents));
    temp_node.add(new MapNode("Unused", new RangeContents(2)));

    root.add(temp_node);
    root.evaluateAddresses();
    return root;
  }

  private String two_digits(String s)
  {
    switch (s.length())
      {
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
    private long pos;
    private long end;
    private int extrapos;
    private int byte_start;
    private int byte_count;
    private byte hi;
    private byte mid;
    private byte lo;
    private int check_sum;
    private MapNode node;

    private BulkStream() {}

    BulkStream(MapNode root, long start, long end) throws IOException
    {
      if (root == null)
        throw new NullPointerException("root");
      if (start > end)
        throw new IOException("start > end");
      if (start < 0)
        throw new IOException("start < 0");
      if (end > 7 * 0x3fffff)
        throw new IOException("end > 7 * 0x3fffff");
      byte_start = (int)(start / 7);
      byte_count = (int)((end - start + 6) / 7);
      hi = (byte)((byte_start >> 14) & 0x7f);
      mid = (byte)((byte_start >> 7) & 0x7f);
      lo = (byte)(byte_start & 0x7f);
      pos = start;
      this.end = end;
      extrapos = -9;
      check_sum = 0x00;
      node = root;
    }

    private int next_header_byte()
    {
      int data;
      switch (extrapos)
        {
        case -9:
          data = SYS_EX_STAT;
          break;
        case -8:
          data = MANUFACTURER_ID;
          break;
        case -7:
          data = DEVICE_NUMBER;
          break;
        case -6:
          data = MODEL_ID;
          break;
        case -5:
          data = byte_count >> 7;
          check_sum = (check_sum + data) & 0x7f;
          break;
        case -4:
          data = byte_count & 0x7f;
          check_sum = (check_sum + data) & 0x7f;
          break;
        case -3:
          data = hi;
          check_sum = (check_sum + data) & 0x7f;
          break;
        case -2:
          data = mid;
          check_sum = (check_sum + data) & 0x7f;
          break;
        case -1:
          data = lo;
          check_sum = (check_sum + data) & 0x7f;
          break;
        default:
          throw new IllegalStateException("invalid extrapos");
        }
      extrapos++;
      return data;
    }

    private int next_tail_byte()
    {
      int data;
      switch (extrapos)
        {
        case 0:
          check_sum = (~check_sum) & 0x7f;
          data = check_sum;
          break;
        case 1:
          data = SYS_EX_END;
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
      int data[] = node.getData(pos, 7);
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
        if (extrapos < 2) // tail data
          return next_tail_byte();
        else
          return -1; // EOF
    }
  }

  /**
   * Given a contigous area of memory, returns a a stream of MIDI bytes
   * that may be used to send the memory contents to the MIDI device.
   * @param root The root node of the map to use.
   * @param start The bit address in the memory map where to start.
   * @param end The bit address in the memory map where to end before;
   *    thus the total bulk dump size is (end - start) bits of memory.
   * @return A stream that bulk dumps the sequence of bytes for the
   *    MIDI device.
   */
  public InputStream bulkDump(MapNode root, long start, long end)
  {
    try
      {
        return new BulkStream(root, start, end);
      }
    catch (IOException e)
      {
        throw new IllegalStateException(e.toString());
      }
  }

  /**
   * Given an InputStream that represents a sequence of bulk dumped MIDI
   * bytes from the MIDI device, this method interprets the MIDI data and
   * updates the memory map accordingly.
   * @param in The InputStream of MIDI bytes to be interpreted.
   */
  public void bulkRead(InputStream in)
  {
  }

  /**
   * Returns descriptive name of the device(s) (for headlines etc.)
   */
  public String toString() { return DEVICE_NAME; }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
