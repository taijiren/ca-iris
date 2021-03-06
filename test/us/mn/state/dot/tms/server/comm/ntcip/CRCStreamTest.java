/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ntcip;

import junit.framework.TestCase;

/**
 * CRC test cases.
 *
 * @author Doug Lau
 */
public class CRCStreamTest extends TestCase {

	static private final byte[] CHECK = "123456789".getBytes();

	public CRCStreamTest(String name) {
		super(name);
	}

	public void testStream() {
		CRCStream cs = new CRCStream();
		for (byte c: CHECK)
			cs.write(c);
		assertTrue(cs.getCrc() == 0x906E);
	}
}
