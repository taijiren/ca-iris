/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.viconptz;

import java.io.InputStream;
import us.mn.state.dot.tms.server.comm.ControllerProperty;

/**
 * Vicon Property
 *
 * @author Douglas Lau
 */
abstract public class ViconPTZProperty extends ControllerProperty {

	/** Mask for command requests (second byte) */
	static protected final byte CMD = 0x10;

	/** Mask for extended command requests (second byte) */
	static protected final byte EXTENDED_CMD = 0x50;

	/** Decode a STORE response */
	public void decodeStore(InputStream is, int drop) {
		// do not expect any response
	}
}
