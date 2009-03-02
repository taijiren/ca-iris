/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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
 */
package us.mn.state.dot.tms.client.toast;

import java.awt.Dimension;
import javax.swing.JTable;

/**
 * ZTable is a simple JTable extension which adds a setVisibleRowCount method
 *
 * @author Douglas Lau
 */
public class ZTable extends JTable {

	/** Set the visible row count */
	public void setVisibleRowCount(int c) {
		int h = getRowHeight();
		Dimension d = new Dimension(getPreferredSize().width,
			getRowHeight() * c);
		setPreferredScrollableViewportSize(d);
	}
}
