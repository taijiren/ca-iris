/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.snmp;

import java.io.InputStream;
import java.io.IOException;

/**
 * ASN1 Integer.  Base class for MIB integer objects.
 *
 * @author Douglas Lau
 */
public class ASN1Integer extends ASN1Object {

	/** Create a new ASN1 integer */
	public ASN1Integer(MIBNode n) {
		super(n);
	}

	/** Actual integer value */
	protected int value;

	/** Set the integer value */
	public void setInteger(int v) {
		value = v;
	}

	/** Get the integer value */
	public int getInteger() {
		return value;
	}

	/** Get the object value */
	@Override
	protected String getValue() {
		return String.valueOf(value);
	}

	/** Encode an integer */
	@Override
	public void encode(BER er) throws IOException {
		er.encodeInteger(value);
	}

	/** Decode an integer */
	@Override
	public void decode(InputStream is, BER er) throws IOException {
		value = er.decodeInteger(is);
	}
}
