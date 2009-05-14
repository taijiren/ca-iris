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
package us.mn.state.dot.tms.comm.mndot;

import us.mn.state.dot.tms.LCSArrayImpl;
import us.mn.state.dot.tms.comm.Device2Operation;

/**
 * An LCS array operation.
 *
 * @author Douglas Lau
 */
abstract public class LCSOperation extends Device2Operation {

	/** LCS array to query */
	protected final LCSArrayImpl lcs_array;

	/** Create a new LCS operation */
	protected LCSOperation(int p, LCSArrayImpl l) {
		super(p, l);
		lcs_array = l;
	}
}
