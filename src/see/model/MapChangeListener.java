/*
 * @(#)MapChangeListener.java 1.00 98/01/31
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

package see.model;

import java.util.EventListener;

/**
 * The listener interface for receiving map change events. 
 */
public interface MapChangeListener extends EventListener
{
  /**
   * Invoked when a map change occurs.
   */
  public void mapChangePerformed(MapChangeEvent e);
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
